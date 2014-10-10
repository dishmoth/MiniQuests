/*
 *  Track.java
 *  Copyright Simon Hern 2014
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

// logic determining where a monster can walk next
public interface Track {

  // whether the monster can move in the specified direction
  public boolean canMove(int xPos, int yPos, int zPos, int direc);
  
} // class Track
