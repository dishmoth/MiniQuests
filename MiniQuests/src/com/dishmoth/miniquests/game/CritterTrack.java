/*
 *  CritterTrack.java
 *  Copyright Simon Hern 2010
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

// possible positions where a Critter can walk
public class CritterTrack {

  // (x,y) block position of the track's bottom-left corner
  final private int mXRef,
                    mYRef;
  
  // non-space in positions where the Critter can go
  final private String mTrackData[];
  
  // constructor (default ref position)
  public CritterTrack(String trackData[]) {
    
    assert( trackData != null );
    mTrackData = trackData;
    
    mXRef = mYRef = 0;
    
  } // constructor
  
  // constructor (default ref position)
  public CritterTrack(String trackData[], int xRef, int yRef) {
    
    assert( trackData != null );
    mTrackData = trackData;
    
    mXRef = xRef;
    mYRef = yRef;
    
  } // constructor

  // whether the Critter can move in the specified direction
  // (note: the z-position is never used)
  public boolean canMove(int xPos, int yPos, int zPos, int direc) {
    
    assert( direc >= 0 && direc < 4 );
    assert( trackAt(xPos, yPos) );
    return trackAt(xPos + Env.STEP_X[direc], yPos + Env.STEP_Y[direc]);
    
  } // canMove()
  
  // whether the track is defined at the specified position 
  private boolean trackAt(int x, int y) {
    
    final int dx = x - mXRef,
              dy = y - mYRef;
    if ( dx < 0 || dy < 0 ) return false;
    if ( dy >= mTrackData.length ) return false;
    
    String trackLine = mTrackData[mTrackData.length-1-dy];
    assert( trackLine != null );
    if ( dx >= trackLine.length() ) return false;
    
    final char ch = trackLine.charAt(dx); 
    return ( ch != ' ' );
    
  } // trackAt()
  
} // class CritterTrack
