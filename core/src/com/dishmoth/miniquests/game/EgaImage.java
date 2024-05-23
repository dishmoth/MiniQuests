/*
 *  EgaImage.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.Arrays;

// image in EGA format to display on canvas
// 2D coords: x right, y down
// 3D coords: x right-up, y left-up, z up
public class EgaImage {

  // position of reference pixel relative to top-left of pixel data
  protected int mRefXPos,
                mRefYPos;
  
  // dimensions of the pixel data
  protected int mWidth, 
                mHeight;
  
  // pixel colours (0 to 63, or -1 for transparent)
  protected byte mPixels[];

  // relative depths of pixels
  protected float mDepths[];
  
  // constructor (blank data)
  public EgaImage(int refXPos, int refYPos, 
                  int width, int height) {

    set(refXPos, refYPos, width, height, null, null);
    
  } // constructor

  // constructor (pixel array)
  public EgaImage(int refXPos, int refYPos, 
                  int width, int height, 
                  byte pixels[]) {

    set(refXPos, refYPos, width, height, pixels, null);
    
  } // constructor

  // constructor (pixel and depth arrays)
  public EgaImage(int refXPos, int refYPos, 
                  int width, int height, 
                  byte pixels[], float depth) {

    float depths[] = { depth };
    set(refXPos, refYPos, width, height, pixels, depths);
    
  } // constructor

  // constructor (pixel and depth arrays)
  public EgaImage(int refXPos, int refYPos, 
                  int width, int height, 
                  byte pixels[], float depths[]) {

    set(refXPos, refYPos, width, height, pixels, depths);
    
  } // constructor

  // constructor (ascii pixels)
  public EgaImage(int refXPos, int refYPos, 
                  int width, int height, 
                  String pixels) {

    set(refXPos,refYPos, width,height, EgaTools.decodePixels(pixels), null);
    
  } // constructor

  // constructor (ascii pixels, depth array)
  public EgaImage(int refXPos, int refYPos, 
                  int width, int height, 
                  String pixels, float depth) {

    float depths[] = { depth };
    set(refXPos,refYPos, width,height, EgaTools.decodePixels(pixels), depths);
    
  } // constructor

  // constructor (ascii pixels, depth array)
  public EgaImage(int refXPos, int refYPos, 
                  int width, int height, 
                  String pixels, float depths[]) {

    set(refXPos,refYPos, width,height, EgaTools.decodePixels(pixels), depths);
    
  } // constructor

  // initialize
  private void set(int refXPos, int refYPos, 
                   int width, int height,
                   byte pixels[], float depths[]) {
    
    assert( width > 0 && height > 0 );
    
    mRefXPos = refXPos;
    mRefYPos = refYPos;
    
    mWidth = width;
    mHeight = height;

    if ( pixels == null ) {
      mPixels = new byte[mWidth*mHeight];
      Arrays.fill(mPixels, (byte)-1);
    } else {
      assert( pixels.length == mWidth*mHeight );
      mPixels = pixels;
    }
    
    if ( depths == null ) {
      mDepths = new float[mWidth*mHeight];
      Arrays.fill(mDepths, 1.0e6f);
    } else if ( depths.length == 1 ) {
      mDepths = new float[mWidth*mHeight];
      Arrays.fill(mDepths, depths[0]);
    } else {
      assert( depths.length == mWidth*mHeight );
      mDepths = depths;
    }
    
  } // set()

  // copy the image
  public EgaImage clone() {
    
    return new EgaImage(mRefXPos, mRefYPos, mWidth, mHeight, 
                        pixelsCopy(), depthsCopy()); 
    
  } // clone()
  
  // size and reference position of image
  final public int refXPos() { return mRefXPos; }
  final public int refYPos() { return mRefYPos; }
  final public int width() { return mWidth; }
  final public int height() { return mHeight; }
  
  // access to the pixel array (values 0 to 63)
  final public byte[] pixels() { return mPixels; }
  
  // clone of the pixel array
  final public byte[] pixelsCopy() { return Env.copyOf(mPixels); }
  
  // access to the pixel depths
  final public float[] depths() { return mDepths; }
  
  // clone of the pixel depths
  final public float[] depthsCopy() { return Env.copyOf(mDepths); }
  
  // colour an individual pixel (ignoring depth)
  public void plot(int x, int y, byte colour) {
    
    assert( colour >= 0 && colour < 64 );

    x += mRefXPos;
    y += mRefYPos;
    
    if ( x < 0 || x >= mWidth || y < 0 || y >= mHeight ) return;
    final int index = x + y*mWidth;
    mPixels[index] = colour;
    
  } // plot()
  
  // colour an individual pixel (dependent on depth)
  public void plot(int x, int y, float depth, byte colour) {
    
    assert( colour >= 0 && colour < 64 );
    
    x += mRefXPos;
    y += mRefYPos;
    
    if ( x < 0 || x >= mWidth || y < 0 || y >= mHeight ) return;
    final int index = x + y*mWidth;
    if ( depth > mDepths[index] ) return;
    mPixels[index] = colour;
    mDepths[index] = depth;
    
  } // plot()
  
  // colour a rectangle of pixels (ignoring depth)
  public void fill(int x0, int x1, int y0, int y1, byte colour) {
    
    assert( colour >= 0 && colour < 64 );

    x0 += mRefXPos;
    y0 += mRefYPos;
    x1 += mRefXPos;
    y1 += mRefYPos;
    
    x0 = Math.max(x0, 0);
    x1 = Math.min(x1, mWidth-1);
    y0 = Math.max(y0, 0);
    y1 = Math.min(y1, mHeight-1);
    if ( x1 < x0 || y1 < y0 ) return;
    
    final int xSize = x1 - x0 + 1,
              ySize = y1 - y0 + 1;
    
    int index = x0 + y0*mWidth;
    for ( int iy = 0 ; iy < ySize ; iy++ ) {
      for ( int ix = 0 ; ix < xSize ; ix++ ) {
        mPixels[index++] = colour;
      }
      index += mWidth - xSize;
    }
    
  } // fill()
  
  // colour a rectangle of pixels (dependent on depth)
  public void fill(int x0, int x1, int y0, int y1, float depth, byte colour) {
    
    assert( colour >= 0 && colour < 64 );

    x0 += mRefXPos;
    y0 += mRefYPos;
    x1 += mRefXPos;
    y1 += mRefYPos;

    x0 = Math.max(x0, 0);
    x1 = Math.min(x1, mWidth-1);
    y0 = Math.max(y0, 0);
    y1 = Math.min(y1, mHeight-1);
    if ( x1 < x0 || y1 < y0 ) return;
    
    final int xSize = x1 - x0 + 1,
              ySize = y1 - y0 + 1;
    
    int index = x0 + y0*mWidth;
    for ( int iy = 0 ; iy < ySize ; iy++ ) {
      for ( int ix = 0 ; ix < xSize ; ix++ ) {
        if ( depth <= mDepths[index] ) {
          mPixels[index] = colour;
          mDepths[index] = depth;
        }
        index += 1;
      }
      index += mWidth - xSize;
    }
    
  } // fill()
  
  // display the image with its reference point at the specified position
  // (xy in pixels, origin top-left) 
  public void draw(EgaImage canvas, int xPos, int yPos, float depth) {

    draw(canvas, xPos, yPos, true, depth);
    
  } // draw()
  
  // display (as above) ignoring depth values
  public void draw(EgaImage canvas, int xPos, int yPos) {
    
    draw(canvas, xPos, yPos, false, 0);
    
  } // draw()
  
  // display the image relative to our isometric coordinate system
  // (x and y in half-blocks, z in pixels)
  public void draw3D(EgaCanvas canvas, int xPos, int yPos, int zPos) {

    final int depth = (int)Math.floor(0.5f*xPos) + (int)Math.ceil(0.5f*yPos);
    final int x = Env.originXPixel() + xPos - yPos,
              y = Env.originYPixel() - depth - zPos;  
    draw(canvas, x, y, true, depth);
    
  } // draw3D()
  
  // internal implementation of draw functions
  private void draw(EgaImage canvas, int xPos, int yPos, 
                    boolean useDepth, float depth) {

    final int x0 = xPos - mRefXPos + canvas.mRefXPos,
              y0 = yPos - mRefYPos + canvas.mRefYPos;
    
    final int sx = Math.max(0, -x0),
              sy = Math.max(0, -y0),
              nx = Math.min(mWidth, canvas.width()-x0) - sx,
              ny = Math.min(mHeight, canvas.height()-y0) - sy,
              dx = Math.max(0, x0),
              dy = Math.max(0, y0);
    if ( nx <= 0 || ny <= 0 ) return;

    final int sGap = mWidth - nx,
              dGap = canvas.width() - nx;
    assert( sGap >= 0 && dGap >= 0 );
    
    byte destPixels[] = canvas.pixels();
    float destDepths[] = canvas.depths();
    
    int sInd = sy*mWidth + sx,
        dInd = dy*canvas.width() + dx;
    
    if ( useDepth ) {
    
      for ( int ky = 0 ; ky < ny ; ky++, sInd+=sGap, dInd+=dGap ) {
        for ( int kx = 0 ; kx < nx ; kx++, sInd++, dInd++ ) {
          final byte pixel = mPixels[sInd];
          if ( pixel < 0 ) continue;
          final float pixDepth = mDepths[sInd] + depth;
          if ( pixDepth > destDepths[dInd] ) continue;
          destPixels[dInd] = pixel;
          destDepths[dInd] = pixDepth;
        }
      }
      
    } else {
    
      for ( int ky = 0 ; ky < ny ; ky++, sInd+=sGap, dInd+=dGap ) {
        for ( int kx = 0 ; kx < nx ; kx++, sInd++, dInd++ ) {
          final byte pixel = mPixels[sInd];
          if ( pixel >= 0 ) destPixels[dInd] = pixel;
        }
      }
      
    }
    
  } // draw()
  
} // class EgaImage
