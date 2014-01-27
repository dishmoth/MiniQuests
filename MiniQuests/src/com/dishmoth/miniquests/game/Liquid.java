/*
 *  Liquid.java
 *  Copyright Simon Hern 2010
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// lava or water or something
public class Liquid extends Sprite3D {

  // colours for different liquids
  private static final byte kColourSchemes[][] = { { 11, 59 },   // water   
                                                   { 52, 36 },   // lava
                                                   { 13, 21 } }; // purple gunk
  
  // how fast the liquid colours change (chance per pixel, per tick)
  private static final float kColourChange = 0.02f;
  
  // how deep before the different types of liquid are dangerous
  private static final int kLethalDepths[] = { 6, 1, 6 };

  // default layout (fills a room)
  private static final String kRow = "@@@@@@@@@@";
  private static final String kDefaultPattern[] = { kRow, kRow, kRow, kRow,
                                                    kRow, kRow, kRow, kRow,
                                                    kRow, kRow };

  // which liquid type
  private int mType;

  // how deep before the liquid becomes dangerous
  private int mLethalDepth;
  
  // current reference position (x, y in block units, z in pixels)
  private int mXPos,
              mYPos,
              mZPos;
  
  // size of the pattern (block units)
  private int mXSize,
              mYSize;

  // which blocks have the liquid (' ' for none)
  private String mPattern[];
  
  // image (with depth) of the liquid
  private EgaImage mImage;
  
  // constructor (with pattern)
  public Liquid(int x, int y, int z, int type, String pattern[]) {
    
    mXPos = x;
    mYPos = y;
    mZPos = z;
  
    assert( type >= 0 && type < kColourSchemes.length );
    mType = type;
    mLethalDepth = kLethalDepths[mType];
    
    assert( pattern != null );
    mYSize = pattern.length;
    assert( pattern[0] != null );
    mXSize = pattern[0].length();
    assert( mXSize > 0 );
    for ( int k = 2 ; k < mYSize ; k++ ) {
      assert( pattern[k] != null && pattern[k].length() == mXSize );
    }

    mPattern = pattern;

    buildImage();
    
  } // constructor
  
  // constructor (default pattern)
  public Liquid(int x, int y, int z, int type) {
    
    mXPos = x;
    mYPos = y;
    mZPos = z;
  
    assert( type >= 0 && type < kColourSchemes.length );
    mType = type;
    mLethalDepth = kLethalDepths[mType];
    
    mPattern = kDefaultPattern;
    mYSize = mPattern.length;
    mXSize = mPattern[0].length();

    buildImage();
    
  } // constructor
  
  // set position (in pixels)
  public void setXPos(int x) { mXPos = x; }
  public void setYPos(int y) { mYPos = y; }
  public void setZPos(int z) { mZPos = z; }

  // set the depth at which liquid becomes deadly
  public void setLethalDepth(int depth) { 
    
    assert( depth >= 1 ); 
    mLethalDepth = depth; 
    
  } // setLethalDepth()
  
  // get the depth at which liquid becomes deadly
  public int lethalDepth() { return mLethalDepth; }

  // liquid type
  public boolean isWater() { return (mType == 0); }
  
  // returns how far under the liquid the position is (or zero)
  public int depth(int x, int y, int z) {

    x -= mXPos;
    y -= mYPos;
    z -= mZPos;
    
    if ( y < 0 || y >= mPattern.length ) return 0;
    String row = mPattern[mPattern.length-1-y];
    if ( x < 0 || x >= row.length() ) return 0;
    if ( row.charAt(x) == ' ' ) return 0;

    return Math.max(0, -z);
    
  } // depth()
  
  // make an image object for the lava
  private void buildImage() {
    
    final int left  = 2*(mYSize-1) + 1,
              right = 2*(mXSize-1) + 2,
              above = (mXSize-1) + (mYSize-1) + 1,
              below = 1;
    
    final int refXPos = left,
              refYPos = above,
              width   = left + right + 1,
              height  = above + below + 1;
    mImage = new EgaImage(refXPos, refYPos, width, height);
    
    byte pixels[] = mImage.pixels();
    float depths[] = mImage.depths();
    
    for ( int iy = 0 ; iy < mYSize ; iy++ ) {
      String yRow = mPattern[mYSize-1-iy];
      for ( int ix = 0 ; ix < mXSize ; ix++ ) {
        if ( yRow.charAt(ix) == ' ' ) continue;
        final int   xPixel = refXPos + 2*ix - 2*iy,
                    yPixel = refYPos - ix - iy;
        final float depth  = ix + iy + 0.5f;
        final int   index  = yPixel*width + xPixel;
        for ( int k = -1 ; k <= 2 ; k++ ) {
          pixels[index+k] = 0;
          depths[index+k] = depth;
        }
        for ( int k = 0 ; k <= 1 ; k++ ) {
          pixels[index+k-width] = 0;
          depths[index+k-width] = depth + 1;
          pixels[index+k+width] = 0;
          depths[index+k+width] = depth - 1;
        }
      }
    }
    
    byte colours[] = kColourSchemes[mType];
    for ( int k = 0 ; k < pixels.length ; k++ ) {
      if ( pixels[k] == 0 ) {
        pixels[k] = colours[ Env.randomInt(colours.length) ]; 
      }
    }
    
  } // buildImage()
  
  // make the liquid appear to move (shift in block units)
  public void scrollImage(int xScroll, int yScroll) {

    if ( xScroll == 0 && yScroll == 0 ) return;
    
    byte pixels[] = mImage.pixels();
    byte oldPixels[] = pixels.clone();
    
    int dx = 2*yScroll - 2*xScroll,
        dy = xScroll + yScroll;
    
    byte colours[] = kColourSchemes[mType];
    
    final int width = mImage.width(),
              height = mImage.height();
    
    final int dIndex = dy*width + dx;
    int index = 0;
    
    for ( int iy = 0 ; iy < height ; iy++ ) {
      for ( int ix = 0 ; ix < width ; ix++ ) {
        if ( pixels[index] >= 0 ) {
          if ( ix+dx >= 0 && ix+dx < width &&
               iy+dy >= 0 && iy+dy < height &&
               pixels[index+dIndex] >= 0 ) {
            pixels[index] = oldPixels[index+dIndex];
          } else {
            pixels[index] = colours[ Env.randomInt(colours.length) ];
          }
        }
        index++;
      }
    }
    
  } // scrollImage()
  
  // change some of the pixel colours
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {
    
    byte pixels[] = mImage.pixels();
    byte colours[] = kColourSchemes[mType];
    
    final int num = (int)Math.round( kColourChange * pixels.length );
    for ( int k = 0 ; k < num ; k++ ) {
      final int index = Env.randomInt(pixels.length);
      if ( pixels[index] >= 0 ) {
        if ( pixels[index] == colours[0] ) pixels[index] = colours[1];
        else                               pixels[index] = colours[0];
      }
    }
    
  } // Sprite.advance()

  // display the liquid
  @Override
  public void draw(EgaCanvas canvas) {

    final int x0 = mXPos - mCamera.xPos(),
              y0 = mYPos - mCamera.yPos(),
              z0 = mZPos - mCamera.zPos();
    mImage.draw3D(canvas, 2*x0, 2*y0, z0);

  } // Sprite.draw()

} // class Liquid
