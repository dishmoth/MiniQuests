/*
 *  Picture.java
 *  Copyright Simon Hern 2010
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
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
