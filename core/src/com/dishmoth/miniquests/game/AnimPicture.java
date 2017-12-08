/*
 *  AnimPicture.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// animated image (or sequence of images)
public class AnimPicture extends Sprite {

  // hardwired z-depth at which image is displayed
  private static final float kDepth = -2.0f;
  
  // sequence of images to display
  final private EgaImage mImages[];
  
  // time delay for each stage, alternating on then off (-1 => infinite)
  final private int mDelays[];
  
  // which image or delay between images is active (-1 for initial stage)
  private int mStage;
  
  // time remaining until next stage
  private int mTimer;
  
  // constructor (one image)
  public AnimPicture(int initialDelay, 
                     EgaImage image, int delayOn, int delayOff) {
    
    assert( image != null );
    mImages = new EgaImage[]{ image };
    
    assert( delayOn == -1 || delayOn > 0 );
    assert( delayOff >= -1 );
    mDelays = new int[]{ delayOn, delayOff };

    assert( initialDelay >= 0 );
    if ( initialDelay > 0 ) {
      mStage = -1;
      mTimer = initialDelay;
    } else {
      mStage = 0;
      mTimer = mDelays[0];
    }
    
  } // constructor
  
  // constructor (two images)
  public AnimPicture(int initialDelay, 
                     EgaImage image1, int delayOn1, int delayOff1,
                     EgaImage image2, int delayOn2, int delayOff2) {
    
    assert( image1 != null && image2 != null);
    mImages = new EgaImage[]{ image1, image2 };
    
    assert( delayOn1 > 0 );
    assert( delayOff1 > 0 );
    
    assert( delayOn2 == -1 || delayOn2 > 0 );
    assert( delayOff2 >= -1 );
    mDelays = new int[]{ delayOn1, delayOff1, delayOn2, delayOff2 };

    assert( initialDelay >= 0 );
    if ( initialDelay > 0 ) {
      mStage = -1;
      mTimer = initialDelay;
    } else {
      mStage = 0;
      mTimer = mDelays[0];
    }
    
  } // constructor
  
  // update the timers
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {

    if ( mTimer == -1 ) return;
    
    assert( mTimer > 0 );
    if ( --mTimer == 0 ) {
      mStage = (mStage+1) % mDelays.length;
      mTimer = mDelays[mStage];
      if ( mTimer == -1 && mStage%2 == 1 ) {
        killTheseSprites.add(this);
      }
    }
    
  } // Sprite.advance()

  // display the text
  @Override
  public void draw(EgaCanvas canvas) {

    if ( mStage < 0 || mStage%2 == 1 ) return;
    mImages[mStage/2].draw(canvas, 0, 0, kDepth);
    
  } // Sprite.draw()

} // class AnimPicture
