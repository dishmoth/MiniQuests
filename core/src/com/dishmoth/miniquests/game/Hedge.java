/*
 *  Hedge.java
 *  Copyright (c) 2024 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// a low wall
public class Hedge extends Sprite3D implements Obstacle {

  // colour schemes for the wall
  private static final byte kColours[][] = { { 16, 2 },   // hedge
                                             { 56, 7 } }; // stone

  // colour patterns for different block styles
  private static final int kPixels[][] = { { 1,0, 0,0 },
                                           { 1,0, 1,1 },
                                           { 0,1, 0,1 },
                                           { 1,0, 1,0 } };

  // position of base point (bottom-left) of wall
  final private int mXPos,
                    mYPos,
                    mZPos;

  // array of block styles (-1 for empty)
  final private int mPattern[][];

  // index of the colours to use
  final private int mColourScheme;

  // constructor for a straight wall section (all blocks the same style)
  public Hedge(int xPos, int yPos, int zPos, 
               int length, int direc, int colourScheme) {
    
    mXPos = xPos;
    mYPos = yPos;
    mZPos = zPos;
  
    assert( length > 0 );
    assert( direc == Env.UP || direc == Env.RIGHT );
    if ( direc == Env.UP ) {
      mPattern = new int[length][1];
    } else {
      mPattern = new int[1][length];
    }

    for ( int iy = 0 ; iy < mPattern.length ; iy++ ) {
      for ( int ix = 0 ; ix < mPattern[iy].length ; ix++ ) {
        mPattern[iy][ix] = 0;
      }
    }

    assert ( colourScheme >= 0 && colourScheme < kColours.length );
    mColourScheme = colourScheme;

  } // constructor

  // constructor for a pattern of walls (block style based on neighbours)
  public Hedge(int xPos, int yPos, int zPos,
               String pattern[], int colourScheme) {

    mXPos = xPos;
    mYPos = yPos;
    mZPos = zPos;

    assert( pattern != null && pattern.length > 0 && pattern[0].length() > 0 );
    final int numY = pattern.length,
              numX = pattern[0].length();

    boolean solid[][] = new boolean[numY][numX];
    for ( int iy = 0 ; iy < numY ; iy++ ) {
      for ( int ix = 0 ; ix < numX ; ix++ ) {
        solid[iy][ix] = (pattern[numY-1-iy].charAt(ix) != ' ');
      }
    }

    mPattern = new int[numY][numX];
    for ( int iy = 0 ; iy < numY ; iy++ ) {
      for ( int ix = 0 ; ix < numX ; ix++ ) {
        if (solid[iy][ix]) {
          boolean sL = (ix > 0      && solid[iy][ix-1]),
                  sR = (ix < numX-1 && solid[iy][ix+1]),
                  sD = (iy > 0      && solid[iy-1][ix]),
                  sU = (iy < numY-1 && solid[iy+1][ix]);
          if      ( sL && sD ) mPattern[iy][ix] = 2;
          else if ( sL )       mPattern[iy][ix] = 0;
          else if ( sD )       mPattern[iy][ix] = 1;
          else if ( sR && sU ) mPattern[iy][ix] = 3;
          else if ( sR )       mPattern[iy][ix] = 0;
          else if ( sU )       mPattern[iy][ix] = 1;
          else                 mPattern[iy][ix] = 0;
        } else {
          mPattern[iy][ix] = -1;
        }
      }
    }

    assert ( colourScheme >= 0 && colourScheme < kColours.length );
    mColourScheme = colourScheme;

  } // constructor

  // whether the player can stand at the specified position
  public boolean isPlatform(int x, int y, int z) {

    return false;
    
  } // Obstacle.isPlatform()

  // whether there is space at the specified position
  public boolean isEmpty(int x, int y, int z) {

    if ( z != mZPos+1 ) return true;

    int ix = x - mXPos,
        iy = y - mYPos;
    if ( ix < 0 || ix >= mPattern[0].length ) return true;
    if ( iy < 0 || iy >= mPattern.length )    return true;

    return (mPattern[iy][ix] < 0);

  } // Obstacle.isEmpty()

  // whether the position is outside of the game world
  public boolean isVoid(int x, int y, int z) { 

    return false;
    
  } // Obstacle.isVoid()

  // nothing to do here
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {

  } // Sprite.advance()

  // display the object
  @Override
  public void draw(EgaCanvas canvas) {

    final int xPos = mXPos - mCamera.xPos(),
              yPos = mYPos - mCamera.yPos(),
              zPos = mZPos - mCamera.zPos();

    final int x0 = Env.originXPixel() + 2*xPos - 2*yPos,
              y0 = Env.originYPixel() - xPos - yPos - zPos;
    final float depth0 = xPos + yPos - 0.01f;
    
    byte colours[] = kColours[mColourScheme];

    for ( int iy = 0 ; iy < mPattern.length ; iy++ ) {
      for ( int ix = 0 ; ix < mPattern[iy].length ; ix++ ) {
        int block = mPattern[iy][ix];
        if ( block >= 0 ) {
          int x = x0 + 2*ix - 2*iy,
              y = y0 - ix - iy;
          float depth = depth0 + ix + iy;
          int pixels[] = kPixels[block];
          canvas.plot(x,   y,   depth, colours[pixels[2]]);
          canvas.plot(x+1, y,   depth, colours[pixels[3]]);
          canvas.plot(x,   y-1, depth, colours[pixels[0]]);
          canvas.plot(x+1, y-1, depth, colours[pixels[1]]);
        }
      }
    }

  } // Sprite.draw()

} // class Hedge
