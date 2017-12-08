/*
 *  Picture.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// a simple object for displaying a static, two-dimensional image
public class Picture extends Sprite {

  // the image to display
  private EgaImage mImage;
  
  // two-dimensional position of the image
  private int mXPos,
              mYPos;
  
  // depth for the image
  private float mDepth;
  
  // constructor
  public Picture(EgaImage image) {
  
    assert( image != null );
    mImage = image;
  
    mXPos = mYPos = 0;
    mDepth = -1.0f;
    
  } // constructor
  
  // constructor (with depth)
  public Picture(EgaImage image, float depth) {
  
    assert( image != null );
    mImage = image;
  
    mXPos = mYPos = 0;
    mDepth = depth;
    
  } // constructor
  
  // constructor (with position and depth)
  public Picture(EgaImage image, int xPos, int yPos, float depth) {
  
    assert( image != null );
    mImage = image;
  
    mXPos  = xPos;
    mYPos  = yPos;
    mDepth = depth;
    
  } // constructor

  // access to the image data
  public EgaImage image() { return mImage; }

  // access the position
  public int getXPos() { return mXPos; }
  public int getYPos() { return mYPos; }
  public void setXPos(int xPos) { mXPos = xPos; }
  public void setYPos(int yPos) { mYPos = yPos; }
  
  // nothing to do here
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {
  } // Sprite.advance()

  // display the image
  @Override
  public void draw(EgaCanvas canvas) {

    mImage.draw(canvas, mXPos, mYPos, mDepth);
    
  } // Sprite.draw()

} // Picture
