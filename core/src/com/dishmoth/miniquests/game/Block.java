/*
 *  Block.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

// functions for drawing a three-dimensional obstruction
public class Block {

  // colours for the sides of the block
  private static final byte kColourLeftSide  = 7,
                            kColourRightSide = 56;

  // depth tweaks
  private static final float kDepthFront = -0.95f,
                             kDepthBack  = +0.95f,
                             kDepthSide  = -0.1f,
                             kDepthBase  = -0.01f;
  
  // bounds for the image (pixels relative to the reference position)
  public static int boundXMin() { return -1; }
  public static int boundXMax() { return +2; }
  public static int boundYMin() { return -1; }
  public static int boundYMax() { return +1; }
  
  // draw a block (reference point at x, y in pixels)
  public static void draw(EgaImage image, int x, int y, int depth,
                          byte colourLeft, byte colourRight, 
                          byte colourTop, byte colourBottom, 
                          byte colourMiddle, int sideHeight) {
    
    assert( sideHeight >= 0 );

    assert( colourLeft   >= 0 && colourLeft   < 64 );
    assert( colourRight  >= 0 && colourRight  < 64 );
    assert( colourTop    >= 0 && colourTop    < 64 );
    assert( colourBottom >= 0 && colourBottom < 64 );
    assert( colourMiddle >= 0 && colourMiddle < 64 );
    
    image.plot(x, y-1, depth+kDepthBack, colourTop);
    image.plot(x+1, y-1, depth+kDepthBack, colourTop);
    
    //image.plot(x, y, depth, colourLeft);
    image.plot(x-1, y, depth, colourLeft);
    image.plot(x, y, depth, colourMiddle);
    image.plot(x+1, y, depth, colourMiddle);
    image.plot(x+2, y, depth, colourRight);
    //image.plot(x+3, y, depth, colourRight);
    
    image.plot(x, y+1, depth+kDepthFront, colourBottom);
    image.plot(x+1, y+1, depth+kDepthFront, colourBottom);
    
    for ( int k = 0 ; k < sideHeight ; k++ ) {
      //image.plot(x-2, y+1+k, depth+kDepthBase, kColourLeftSide);
      image.plot(x-1, y+1+k, depth+kDepthBase+kDepthSide, kColourLeftSide);
      image.plot(x, y+2+k, depth+kDepthBase+kDepthFront, kColourLeftSide);
      image.plot(x+1, y+2+k, depth+kDepthBase+kDepthFront, kColourRightSide);
      image.plot(x+2, y+1+k, depth+kDepthBase+kDepthSide, kColourRightSide);
      //image.plot(x+3, y+1+k, depth+kDepthBase, kColourRightSide);
    }
        
  } // draw() 
  
  // draw a block (x, y in half-blocks, z in pixels)
  public static void draw3D(EgaCanvas canvas, int x, int y, int z,
                            byte colourLeft, byte colourRight, 
                            byte colourTop, byte colourBottom, 
                            byte colourMiddle, int sideHeight) {
    
    final int depth = (int)Math.floor(0.5f*x) + (int)Math.ceil(0.5f*y);
    final int xPixel = Env.originXPixel() + x - y,
              yPixel = Env.originYPixel() - depth - z;
    Block.draw(canvas, xPixel, yPixel, depth, 
               colourLeft, colourRight, colourTop, colourBottom, colourMiddle, 
               sideHeight);
    
  } // draw3D()
  
} // class Block
