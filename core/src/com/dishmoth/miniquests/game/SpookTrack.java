/*
 *  SpookTrack.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

// monster path between a set of way-points
public class SpookTrack implements Track {

  // set of way-points
  final private int mPoints[][]; 
  
  // index of last way-point
  private int mIndex;
  
  // change direction at the end of a non-periodic track
  private boolean mReversed;
  
  // constructor
  public SpookTrack(int points[][]) {
    
    assert( points != null && points.length > 0 && points[0].length == 2 );
    mPoints = points;
    
    mIndex = 0;
    mReversed = false;
    
  } // constructor

  // access to points (read only)
  public int[][] points() { return mPoints; }
  
  // whether the monster can move in the specified direction
  // (note: the z-position is never used)
  @Override
  public boolean canMove(int xPos, int yPos, int zPos, int direc) {
    
    assert( direc >= 0 && direc < 4 );

    int nextIndex = mIndex + (mReversed?-1:+1);
    
    assert( mIndex >= 0 && mIndex < mPoints.length );
    assert( nextIndex >= 0 && nextIndex < mPoints.length );
    
    if ( xPos == mPoints[nextIndex][0] && yPos == mPoints[nextIndex][1] ) {
      mIndex = nextIndex;
      if ( mIndex == 0 ) {
        assert( mReversed );
        mReversed = false;
      } else if ( mIndex == mPoints.length-1 ) {
        assert( !mReversed );
        if ( mPoints[mIndex][0] == mPoints[0][0] && 
             mPoints[mIndex][1] == mPoints[0][1] ) {
          mIndex = 0;
        } else {
          mReversed = true;
        }
      }
      nextIndex = mIndex + (mReversed?-1:+1);
    }
    
    int dx = mPoints[nextIndex][0] - mPoints[mIndex][0],
        dy = mPoints[nextIndex][1] - mPoints[mIndex][1];
    assert( (dx == 0 && dy != 0) || (dx != 0 && dy == 0) );

    if ( dx > 0 ) {
      assert( xPos >= mPoints[mIndex][0] && xPos <= mPoints[nextIndex][0] );
      assert( yPos == mPoints[mIndex][1] );
      return (direc == Env.RIGHT);
    } else if ( dx < 0 ) {
      assert( xPos <= mPoints[mIndex][0] && xPos >= mPoints[nextIndex][0] );
      assert( yPos == mPoints[mIndex][1] );
      return (direc == Env.LEFT);
    } else if ( dy > 0 ) {
      assert( xPos == mPoints[mIndex][0] );
      assert( yPos >= mPoints[mIndex][1] && yPos <= mPoints[nextIndex][1] );
      return (direc == Env.UP);
    } else {
      assert( xPos == mPoints[mIndex][0] );
      assert( yPos <= mPoints[mIndex][1] && yPos >= mPoints[nextIndex][1] );
      return (direc == Env.DOWN);
    }
    
  } // canMove()
  
} // class SpookTrack
