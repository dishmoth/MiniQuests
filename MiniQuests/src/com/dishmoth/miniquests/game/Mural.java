/*
 *  Mural.java
 *  Copyright Simon Hern 2010
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// a picture on a wall
public class Mural extends Sprite3D {

  // small tweak to depth values to make the picture stand out from the wall
  private static final float kDepthEpsilon = 0.006f;
  
  // which wall the picture is on (enumerated in Env)
  final private int mWallSide;

  // position of the image's reference point (x, y block units, z pixel units)
  // (reference is bottom-left for UP walls, bottom-right for RIGHT walls)
  final private int mXPos,
                    mYPos,
                    mZPos;

  // the picture
  final private EgaImage mImage;
  
  // constructor
  public Mural(int wallSide, int xy, int z, EgaImage sourcePic) {
    
    assert( wallSide == Env.UP || wallSide == Env.RIGHT );
    mWallSide = wallSide;

    switch ( mWallSide ) {
      case Env.RIGHT: { mXPos = Room.kSize; mYPos = xy;         } break;
      case Env.UP:    { mXPos = xy;         mYPos = Room.kSize; } break;
      default:        { mXPos = mYPos = 0; assert(false); }
    }
    
    mZPos = z;
    
    mImage = deriveImage(sourcePic);
    
  } // constructor
  
  // constructor (with zone)
  public Mural(int zoneX, int zoneY, 
               int wallSide, int xy, int z, EgaImage sourcePic) {
    
    assert( wallSide == Env.UP || wallSide == Env.RIGHT );
    mWallSide = wallSide;

    switch ( mWallSide ) {
      case Env.RIGHT: { 
        mXPos = Room.kSize*zoneX + Room.kSize;
        mYPos = Room.kSize*zoneY + xy;
      } break;
      case Env.UP: { 
        mXPos = Room.kSize*zoneX + xy;
        mYPos = Room.kSize*zoneY + Room.kSize;
      } break;
      default: { 
        mXPos = mYPos = 0; 
        assert(false); 
      }
    }

    mZPos = z;
    
    mImage = deriveImage(sourcePic);
    
  } // constructor
  
  // construct a depth-mapped version of the source image
  // the depth values and reference point of the source are ignored
  private EgaImage deriveImage(EgaImage sourcePic) {

    final int width  = sourcePic.width(),
              height = sourcePic.height();
    
    final int refXPos = (mWallSide == Env.UP) ? 0 : (width-1),
              refYPos = height - 1;
    
    byte pixels[] = sourcePic.pixels().clone();

    float depths[] = new float[width*height];
    if ( mWallSide == Env.UP ) {
      for ( int ix = 0 ; ix < width ; ix++ ) {
        depths[ix] = ix/2 - kDepthEpsilon;
      }
    } else {
      for ( int ix = 0 ; ix < width ; ix++ ) {
        depths[width-1-ix] = ix/2 - kDepthEpsilon;
      }
    }
    
    int index = width;
    for ( int iy = 1 ; iy < height ; iy++ ) {
      for ( int ix = 0 ; ix < width ; ix++ ) {
        depths[index++] = depths[ix];
      }
    }
    
    return new EgaImage(refXPos, refYPos, width, height, pixels, depths);
    
  } // deriveImage()
  
  // update (nothing to do here)
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {
  } // Sprite.advance()

  // display the picture
  @Override
  public void draw(EgaCanvas canvas) {

    final int x0 = mXPos - mCamera.xPos(),
              y0 = mYPos - mCamera.yPos(),
              z0 = mZPos - mCamera.zPos();
    
    mImage.draw3D(canvas, 2*x0, 2*y0, z0);
    
  } // Sprite.draw()

} // class Mural
