/*
 *  TerrainPath.java
 *  Copyright (c) 2024 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.ArrayList;
import java.util.LinkedList;

// a coloured path over a terrain of blocks
public class TerrainPath extends Sprite3D {

  // reference position
  final private int mXPos,
                    mYPos;

  // whether an (x,y) point is on the path (relative to reference position)
  final private boolean mOnPath[][];

  // list of positions (x,y in block coords; z in pixels) on the path
  final private ArrayList<int[]> mPath;

  // colour of the path tiles
  final private byte mColour;

  // constructor (heights '0' to '9', or ' ')
  public TerrainPath(int xPos, int yPos, int zPos,
                     String heightData[], char colour) {

    mXPos = xPos;
    mYPos = yPos;

    mColour = EgaTools.decodePixel(colour);

    assert( heightData.length > 0 && heightData[0].length() > 0 );
    final int numX = heightData[0].length(),
              numY = heightData.length;

    mOnPath = new boolean[numY][numX];
    mPath = new ArrayList<int[]>();

    for ( int iy = 0 ; iy < numY ; iy++ ) {
      for ( int ix = 0 ; ix < numX ; ix++ ) {
        char ch = heightData[numY - 1 - iy].charAt(ix);
        if ( ch == ' ' ) {
          mOnPath[iy][ix] = false;
        } else {
          assert( ch >= '0' && ch <= '9' );
          mOnPath[iy][ix] = true;
          int iz = 2*(ch - '0');
          mPath.add(new int[]{xPos+ix, yPos+iy, zPos+iz});
        }
      }
    }

  } // constructor

  // nothing to do here
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {}

  // whether a path position has the expected value
  public boolean onPath(int x, int y) {

    int ix = x - mXPos,
        iy = y - mYPos;
    if ( ix < 0 || ix >= mOnPath[0].length ) return false;
    if ( iy < 0 || iy >= mOnPath.length )    return false;
    return mOnPath[iy][ix];

  } // onPath()
  
  // display the path tiles
  @Override
  public void draw(EgaCanvas canvas) {

    final int xPixel0 = Env.originXPixel()
                        - 2*mCamera.xPos() + 2*mCamera.yPos(),
              yPixel0 = Env.originYPixel()
                        + mCamera.xPos() + mCamera.yPos() + mCamera.zPos(),
              depth0  = -(mCamera.xPos() + mCamera.yPos());

    final float depthOffset = -0.003f;

    for ( int[] xyz : mPath ) {

      final int x = xyz[0],
                y = xyz[1],
                z = xyz[2];
      final int   xPixel = xPixel0 + 2*x - 2*y,
                  yPixel = yPixel0 - x - y - z;
      final float depth  = (depth0 + x + y) + depthOffset;

      canvas.plot(xPixel, yPixel, depth, mColour);
      canvas.plot(xPixel+1, yPixel, depth, mColour);

    }
    
  } // Sprite.draw()

} // TerrainPath
