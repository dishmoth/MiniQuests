/*
 *  Door.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.Arrays;

// simple class and image details for doors in walls 
public class Door {

  // details of the door images
  private static final int kWidth  = 4,
                           kHeight = 7,
                           kRefX   = 1,
                           kRefY   = 5;
  
  // details of the door images (stored for left/right walls)
  private static final String kPixelsX =  "000 "
                                        + "0  0"
                                        + "0  0"
                                        + "0  0"
                                        + "0  0"
                                        + "   0"
                                        + "   0";
  private static final float kDepthsX[] = { +1, 0, -0.05f, -1 },
                             kDepthsY[] = { -1, -0.05f, 0, +1 };
  
  // images for doors (x for left/right walls, y for up/down walls)
  private static final EgaImage kImageX,
                                kImageY;
  static {

    float depthsX[] = new float[kWidth*kHeight],
          depthsY[] = new float[kWidth*kHeight];
    char  pixelsY[] = new char[kWidth*kHeight];

    int index = 0;
    for ( int iy = 0 ; iy < kHeight ; iy++ ) {
      for ( int ix = 0 ; ix < kWidth ; ix++ ) {
        depthsX[index] = kDepthsX[ix];
        depthsY[index] = kDepthsY[ix];
        pixelsY[index] = kPixelsX.charAt(iy*kWidth + (kWidth-1-ix));
        index++;
      }
    }
    kImageX = new EgaImage(kRefX, kRefY, kWidth, kHeight,
                           kPixelsX, depthsX);
    kImageY = new EgaImage(kRefX, kRefY, kWidth, kHeight,
                           new String(pixelsY), depthsY);
    
  } // static

  // dimensions of the door space (pixels)
  private static final int kGapHeight = 5,
                           kGapWidth = 2;
  
  // image for inside an open door
  private static final byte kEmptyColour = 63;
  private static final float kEmptyDepth = +0.9f;
  private static final EgaImage kImageEmpty;
  static {

    float depths[] = new float[kGapWidth*(kGapHeight-1)];
    byte  pixels[] = new byte[kGapWidth*(kGapHeight-1)];

    Arrays.fill(depths, kEmptyDepth);
    Arrays.fill(pixels, kEmptyColour);
    
    kImageEmpty = new EgaImage(0, kGapHeight-1, kGapWidth, kGapHeight-1, 
                               pixels, depths);
    
  } // static

  // image for inside a closed door
  // details of the door images (different for left/right walls)
  private static final String kPixelsClosedX =  "000 "
                                              + "0700"
                                              + "0070"
                                              + "0700"
                                              + "0070"
                                              + " 700"
                                              + "   0",
                              kPixelsClosedY =  " 000"
                                              + "00u0"
                                              + "0u00"
                                              + "00u0"
                                              + "0u00"
                                              + "00u "
                                              + "0   ";
  
  // images for closed doors (x for left/right walls, y for up/down walls)
  private static final EgaImage kImageClosedX,
                                kImageClosedY;
  static {

    float depthsX[] = new float[kWidth*kHeight],
          depthsY[] = new float[kWidth*kHeight];

    int index = 0;
    for ( int iy = 0 ; iy < kHeight ; iy++ ) {
      for ( int ix = 0 ; ix < kWidth ; ix++ ) {
        depthsX[index] = kDepthsX[ix];
        depthsY[index] = kDepthsY[ix];
        if ( iy == kRefY && (ix == 1 || ix == 2) ) {
          depthsX[index] -= 0.01f;
          depthsY[index] -= 0.01f;
        }
        index++;
      }
    }
    
    kImageClosedX = new EgaImage(kRefX, kRefY, kWidth, kHeight,
                                 kPixelsClosedX, depthsX);
    kImageClosedY = new EgaImage(kRefX, kRefY, kWidth, kHeight,
                                 kPixelsClosedY, depthsY);
    
  } // static


  // x or y position of door (in block coordinates)
  private int mXYPos;
  
  // z position of bottom of door (in pixels)
  private int mZPos;

  // which side of the room the door is on (see enumeration in Env)
  private int mWallSide;
  
  // block colour (middle and edge) for floor under the door
  private byte mFloorColour[];
  
  // block height (in block units) for floor under the door
  private int  mFloorDrop;

  // whether the door is open as an exit
  private boolean mClosed;
  
  // constructor
  public Door(int xyPos, int zPos, int wallSide, 
              byte floorColour[], int floorDrop) { 

    //assert( zPos >= 0 );
    assert( wallSide >= 0 && wallSide < 4 );
    assert( floorColour != null && floorColour.length == 2 );
    assert( floorDrop >= 0 );
    
    mXYPos = xyPos; 
    mZPos = zPos; 
    
    mWallSide = wallSide;

    mFloorColour = floorColour;
    mFloorDrop = floorDrop;

    mClosed = false;
    
  } // constructor

  // accessors
  public int xyPos() { return mXYPos; }
  public int zPos()  { return mZPos; }

  // height of the door space (pixels)
  static public int gapHeight() { return kGapHeight; }

  // query the door state
  public boolean closed() { return mClosed; }
  
  // change the door state
  public void setClosed(boolean v) { mClosed = v; }

  // draw the door (relative to the room)
  public void draw(EgaCanvas canvas, int mRoomX, int mRoomY, int mRoomZ) {

    int x=0, y=0;
    final int z = mRoomZ + mZPos;
    
    switch ( mWallSide ) {
      
      case Env.RIGHT: {
        x = 2*(mRoomX + Room.kSize); 
        y = 2*(mRoomY + mXYPos);
        if ( mClosed ) {
          kImageClosedX.draw3D(canvas, x, y, z);
        } else {
          kImageX.draw3D(canvas, x, y, z);
          kImageEmpty.draw3D(canvas, x, y, z);
        }
      } break;
      
      case Env.UP: {
        x = 2*(mRoomX + mXYPos);
        y = 2*(mRoomY + Room.kSize);
        if ( mClosed ) {
          kImageClosedY.draw3D(canvas, x, y, z);
        } else {
          kImageY.draw3D(canvas, x, y, z); 
          kImageEmpty.draw3D(canvas, x, y, z);
        }
      } break;
      
      case Env.LEFT: {
        x = 2*(mRoomX - 1);
        y = 2*(mRoomY + mXYPos);
        if ( mClosed ) {
          kImageClosedX.draw3D(canvas, x, y, z);
        } else {
          kImageX.draw3D(canvas, x, y, z);
        }
      } break;

      case Env.DOWN: {
        x = 2*(mRoomX + mXYPos);
        y = 2*(mRoomY - 1);
        if ( mClosed ) {
          kImageClosedY.draw3D(canvas, x, y, z);
        } else {
          kImageY.draw3D(canvas, x, y, z);
        }
      } break;
      
      default: {
        assert(false);
      }
      
    } // switch

    final byte midCol  = mFloorColour[0],
               edgeCol = mFloorColour[1];
    
    Block.draw3D(canvas, x, y, z, 
                 edgeCol, edgeCol, edgeCol, edgeCol, midCol, 
                 2*mFloorDrop);
    
  } // draw()
  
} // class Door
