/*
 *  Track.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

// logic determining where a monster can walk next
public interface Track {

  // whether the monster can move in the specified direction
  public boolean canMove(int xPos, int yPos, int zPos, int direc);
  
} // class Track
