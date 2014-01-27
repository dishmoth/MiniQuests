/*
 *  Camera.java
 *  Copyright Simon Hern 2010
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// dummy sprite for keeping track of the 'camera' when scrolling a 3D room
public class Camera extends Sprite {

  // position (point that appears at the bottom-middle of the canvas)
  private int mXPos,
              mYPos,
              mZPos;
  
  // constructor
  public Camera() {
    
    mXPos = mYPos = mZPos = 0;
    
  } // constructor

  // accessors
  public int xPos() { return mXPos; }
  public int yPos() { return mYPos; }
  public int zPos() { return mZPos; }
  public void set(int x, int y, int z) {mXPos=x; mYPos=y; mZPos=z;}
  public void shift(int dx, int dy, int dz) {mXPos+=dx; mYPos+=dy; mZPos+=dz;}
  
  // update (does nothing)
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {}

  // draw (does nothing)
  @Override
  public void draw(EgaCanvas canvas) {}
  
} // class Camera
