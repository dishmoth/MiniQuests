/*
 *  Statue.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// a stationary figure 
public class Statue extends Sprite3D implements Obstacle {

  // images in different colour schemes
  private static final PlayerImage kImages[] = 
                              { new PlayerImage(new char[]{ 'c', 'q' }),
                                new PlayerImage(new char[]{ '9', '1' }),
                                new PlayerImage(new char[]{ 'j', '5' }),
                                new PlayerImage(new char[]{ 't', 'k' }) };
  
  // position of the figure
  private int mXPos,
              mYPos,
              mZPos;
  
  // which direction the figure is facing
  private int mDirec;

  // which colour scheme to use
  private int mColour;
  
  // true if the statue has been shot 
  private boolean mHit;
  
  // constructor
  public Statue(int xPos, int yPos, int zPos, int direc, int colour) {
    
    mXPos = xPos;
    mYPos = yPos;
    mZPos = zPos;

    assert( direc >= 0 || direc < 4 );
    mDirec = direc;
    
    assert( colour >= 0 && colour < kImages.length );
    mColour = colour;
    
  } // constructor

  // change the current colour scheme
  public void setColour(int colour) {
    
    assert( colour >= 0 && colour < kImages.length );
    mColour = colour;
    
  } // setColour()
  
  // access to position
  public int getXPos() { return mXPos; }
  public int getYPos() { return mYPos; }
  public int getZPos() { return mZPos; }
  
  // access to direction
  public int getDirec() { return mDirec; }
  
  // shift position
  public void shiftPos(int dx, int dy, int dz) {
    
    mXPos += dx;
    mYPos += dy;
    mZPos += dz;
    
  } // shiftPos()
  
  // whether the player can stand at the specified position
  public boolean isPlatform(int x, int y, int z) {

    return false;
    
  } // Obstacle.isPlatform()

  // whether there is space at the specified position
  public boolean isEmpty(int x, int y, int z) {

    if ( x == mXPos && y == mYPos && z >= mZPos && z <= mZPos+3 ) return false;
    return true;
  
  } // Obstacle.isEmpty()

  // whether the position is outside of the game world
  public boolean isVoid(int x, int y, int z) { 

    return false;
    
  } // Obstacle.isVoid()

  // mark the statue as having been hit
  public void setHit(boolean on) { mHit = on; }
  
  // whether the statue has been hit
  public boolean isHit() { return mHit; } 
  
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

    kImages[mColour].drawBasic(canvas, xPos, yPos, zPos, mDirec);
      
  } // Sprite.draw()

} // class Statue
