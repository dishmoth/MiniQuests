/*
 *  EgaTools.java
 *  Copyright Simon Hern 2010
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.Arrays;

// utilities for dealing with EGA images 
public class EgaTools {

  // EGA palette look-up table
  public static final int NUM_EGA_COLOURS = 64;
  public static final int EGA_PALETTE[] = new int[NUM_EGA_COLOURS];
  static {
    for ( int k = 0 ; k < NUM_EGA_COLOURS ; k++ ) {
      int b = ((k >> 0) & 1) * 0xAA + ((k >> 3) & 1) * 0x55,
          g = ((k >> 1) & 1) * 0xAA + ((k >> 4) & 1) * 0x55,
          r = ((k >> 2) & 1) * 0xAA + ((k >> 5) & 1) * 0x55;
      EGA_PALETTE[k] = ( (r << 16) | (g << 8) | b );
    }
  }

  // EGA-to-RGB (24-bit) palette conversion
  public static int egaToRgb(int egaIndex) {
    
    assert( egaIndex >= 0 && egaIndex < NUM_EGA_COLOURS );
    return EGA_PALETTE[egaIndex];
    
  } // egaToRgb()
  
  // ARGB-to-EGA palette conversion (returns -1 for transparent) 
  public static int argbToEga(int argb) {

    final boolean randomDither = false; // for experimenting 
    
    final int alpha = ((argb >> 24) & 0xFF);
    if ( alpha < 0xFF ) return -1;

    float rr = ((argb >> 16) & 0xFF) / 85.0f,
          gg = ((argb >> 8) & 0xFF) / 85.0f,
          bb = (argb & 0xFF) / 85.0f;
    if ( randomDither ) {
      rr += Env.randomFloat() - 0.5f;
      gg += Env.randomFloat() - 0.5f;
      bb += Env.randomFloat() - 0.5f;
    }
    final int r = (int)Math.round( rr ),
              g = (int)Math.round( gg ),
              b = (int)Math.round( bb );
    final int lo = ((r & 0x2) << 1) + (g & 0x2) + ((b & 0x2) >> 1),
              hi = ((r & 0x1) << 2) + ((g & 0x1) << 1) + (b & 0x1);
    final int ega = ((hi << 3) + lo);
    assert( ega >= 0 && ega < NUM_EGA_COLOURS );
    return ega;
    
  } // argbToEga()
  
  // RGBA-to-EGA palette conversion (returns -1 for transparent) 
  public static int rgbaToEga(int rgba) {

    final int alpha = (rgba & 0xFF);
    if ( alpha < 0xFF ) return -1;

    final int rgb = (rgba >> 8);
    return argbToEga( (alpha<<24) | rgb );
    
  } // rgbaToEga
  
  // convert a string of characters into an array of EGA pixel colours
  public static byte[] decodePixels(String string) {
    
    byte pixels[] = new byte[string.length()];
    for ( int k = 0 ; k < pixels.length ; k++ ) {
      char ch = string.charAt(k);
      pixels[k] = decodePixel(ch);
    }
    return pixels;
    
  } // decodePixels()

  // convert a character into an EGA pixel colour
  public static byte decodePixel(char ch) {
  
    if      ( ch == ' ' )              return -1;
    else if ( ch >= '0' && ch <= '9' ) return (byte)(ch - '0');
    else if ( ch >= 'A' && ch <= 'Z' ) return (byte)((ch - 'A') + 10);
    else if ( ch >= 'a' && ch <= 'z' ) return (byte)((ch - 'a') + 36);
    else if ( ch == ':' )              return 62;
    else if ( ch == '#' )              return 63;
    
    //assert(false);
    return -1;
    
  } // decodePixel()
  
  // convert an EGA pixel colour into a character
  public static char recodePixel(byte b) {
    
    if      ( b >=  0 && b <=  9 ) return (char)((byte)'0' + b);
    else if ( b >= 10 && b <= 35 ) return (char)((byte)'A' + (b-10));
    else if ( b >= 36 && b <= 61 ) return (char)((byte)'a' + (b-36));
    else if ( b == 62 )            return ':';
    else if ( b == 63 )            return '#';
    else                           return ' ';
    
  } // recodePixel()
  
  // convert from local colour scheme to EGA colours
  public static String convertColours(String source, char colourMap[]) {
    
    char colours[] = new char[source.length()];
    for ( int k = 0 ; k < colours.length ; k++ ) {
      final char ch = source.charAt(k);
      if ( ch == ' ' ) {
        colours[k] = ' ';
      } else {
        final int index = (int)(ch - '0');
        assert( index >= 0 && index < colourMap.length );
        colours[k] = colourMap[index];
      }
    }
    return new String(colours);
    
  } // convertColours()

  // reflect an image horizontally, assigning a new reference x position 
  public static EgaImage reflectX(EgaImage image, int newRefXPos) {
    
    final int height = image.height(),
              width  = image.width();
    
    byte pixels[] = image.pixelsCopy();
    float depths[] = image.depthsCopy();

    for ( int iy = 0 ; iy < height ; iy++ ) {
      for ( int ix = 0 ; ix < width/2 ; ix++ ) {
        int index1 = iy*width + ix,
            index2 = iy*width + width - 1 - ix;
        byte p = pixels[index1];
        pixels[index1] = pixels[index2];
        pixels[index2] = p;
        float d = depths[index1];
        depths[index1] = depths[index2];
        depths[index2] = d;
      }
    }

    return new EgaImage(newRefXPos, image.refYPos(), 
                        width, height, 
                        pixels, depths);
    
  } // reflectX()
  
  // reflect an image vertically, assigning a new reference y position 
  public static EgaImage reflectY(EgaImage image, int newRefYPos) {
    
    final int height = image.height(),
              width  = image.width();
    
    byte pixels[] = image.pixelsCopy();
    float depths[] = image.depthsCopy();

    for ( int iy = 0 ; iy < height/2 ; iy++ ) {
      for ( int ix = 0 ; ix < width ; ix++ ) {
        int index1 = iy*width + ix,
            index2 = (height - 1 - iy)*width + ix;
        byte p = pixels[index1];
        pixels[index1] = pixels[index2];
        pixels[index2] = p;
        float d = depths[index1];
        depths[index1] = depths[index2];
        depths[index2] = d;
      }
    }

    return new EgaImage(image.refXPos(), newRefYPos,
                        width, height, 
                        pixels, depths);
    
  } // reflectY()
  
  // counts the number of distinct colours in an image
  public static int numDistinctColours(EgaImage image) {
    
    int count[] = colourHistogram(image); 
    
    int num = 0;
    for ( int k = 0 ; k < count.length ; k++ ) {
      if ( count[k] > 0 ) num++;
    }
    
    return num;
    
  } // numDistinctColours()

  // count the pixels using each colour
  public static int[] colourHistogram(EgaImage image) {
    
    int count[] = new int[NUM_EGA_COLOURS];
    Arrays.fill(count, 0);

    for ( byte pixel : image.pixels() ) {
      if ( pixel != -1 ) {
        assert( pixel >= 0 && pixel < 64 );
        count[pixel]++;
      }
    }

    return count;
    
  } // colourHistogram()

  // restrict the number of distinct colours in the image
  public static void limitColours(EgaImage image, int maxColours,
                                  int requiredColours[]) {
    
    assert( maxColours > 0 && maxColours <= NUM_EGA_COLOURS );
    assert( requiredColours == null || requiredColours.length <= maxColours );
    
    int count[] = colourHistogram(image);

    if ( requiredColours != null ) {
      final int bigCount = image.pixels().length + 1;
      for ( int k = 0 ; k < requiredColours.length ; k++ ) {
        count[requiredColours[k]] = bigCount; 
      }
    }
    
    int numColours = 0;
    for ( int k = 0 ; k < count.length ; k++ ) {
      if ( count[k] > 0 ) numColours += 1;
    }
    if ( numColours <= maxColours ) return;
    
    while ( numColours > maxColours ) {
      
      int minColour = -1;
      for ( int k = 0 ; k < NUM_EGA_COLOURS ; k++ ) {
        if ( count[k] > 0 ) {
          if ( minColour == -1 || count[k] < count[minColour] ) minColour = k;
        }
      }
      assert( minColour != -1 );
      
      int newColour = -1;
      int minDiff = 1000;
      int minRgb = egaToRgb(minColour);
      for ( int k = 0 ; k < NUM_EGA_COLOURS ; k++ ) {
        if ( k == minColour || count[k] == 0 ) continue;
        int rgb = egaToRgb(k);
        int diff = Math.abs( ((rgb>>16) & 0xFF) - ((minRgb>>16) & 0xFF) )
                 + Math.abs( ((rgb>>8) & 0xFF) - ((minRgb>>8) & 0xFF) )
                 + Math.abs( (rgb & 0xFF) - (minRgb & 0xFF) );
        if ( newColour == -1 || diff < minDiff ) {
          newColour = k;
          minDiff = diff;
        }
      }
      assert( newColour != -1 && newColour != minColour );
      
      byte pixels[] = image.pixels();
      for ( int k = 0 ; k < pixels.length ; k++ ) {
        if ( pixels[k] == minColour ) pixels[k] = (byte)newColour;
      }
    
      count[newColour] += count[minColour];
      count[minColour] = 0;       
      numColours -= 1;
      
    }

  } // limitColours()
  
  // restrict the number of distinct colours in the image
  public static void limitColours(EgaImage image, int maxColours) {
  
    limitColours(image, maxColours, null);
    
  } // limitColours()
  
  // make an image darker
  public static void fadeImage(EgaImage image) {
    
    byte pixels[] = image.pixels();
    for ( int k = 0 ; k < pixels.length ; k++ ) {
      if ( pixels[k] == -1 ) continue;
      int r = 2*((pixels[k] >> 2) & 0x01) + ((pixels[k] >> 5) & 0x01),
          g = 2*((pixels[k] >> 1) & 0x01) + ((pixels[k] >> 4) & 0x01),
          b = 2*(pixels[k] & 0x01)        + ((pixels[k] >> 3) & 0x01);
      if ( r > 0 ) r -= 1;
      if ( g > 0 ) g -= 1;
      if ( b > 0 ) b -= 1;
      final int lo = ((r & 0x2) << 1) + (g & 0x2) + ((b & 0x2) >> 1),
                hi = ((r & 0x1) << 2) + ((g & 0x1) << 1) + (b & 0x1);
      final int ega = ((hi << 3) + lo);
      pixels[k] = (byte)ega;
    }
    
  } // fadeImage()
  
} // EgaTools
