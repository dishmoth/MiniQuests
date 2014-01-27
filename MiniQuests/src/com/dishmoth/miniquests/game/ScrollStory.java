/*
 *  ScrollStory.java
 *  Copyright Simon Hern 2010
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.Iterator;
import java.util.LinkedList;

// freezes the game while the camera moves
public class ScrollStory extends Story {

  // how slowly the scrolling takes place (different for horizontal, vertical)
  private static final int kScrollDelayXY = 2,
                           kScrollDelayZ  = 1;

  // a few ticks delay at the end of the scroll
  private static final int kEndDelay = 5;
  
  // reference back to the original story
  private Story mOriginalStory;
  
  // reference to the camera object
  private Camera mCamera;

  // total amount to scroll
  private int mTargetX,
              mTargetY,
              mTargetZ;
  
  // current amount scrolled
  private int mCurrentX,
              mCurrentY,
              mCurrentZ;

  // whether this is the first call to advance
  private boolean mFirstStep;

  // count number of ticks while the scroll takes place
  private int mTotalTime,
              mScrollTime;
  
  // a short count-down at the end of the scroll
  private int mEndTimer;
  
  // keep track of whether the escape key is held down
  private boolean mEscPressed;
  
  // constructor
  public ScrollStory(Story originalStory, int dx, int dy, int dz) {

    assert( originalStory != null );
    mOriginalStory = originalStory;
    
    mCamera = null;

    assert( dx != 0 || dy != 0 || dz != 0 );
    mTargetX = dx;
    mTargetY = dy;
    mTargetZ = dz;

    mCurrentX = mCurrentY = mCurrentZ = 0;
    
    mFirstStep = true;

    mTotalTime = Math.max( kScrollDelayXY*Math.max(Math.abs(dx), Math.abs(dy)),
                           kScrollDelayZ*Math.abs(dz) );
    mScrollTime = 0;

    mEndTimer = (dx == 0 && dy == 0) ? 0 : kEndDelay;
    
  } // constructor
  
  // process events and advance 
  @Override
  public Story advance(LinkedList<StoryEvent> storyEvents,
                       SpriteManager          spriteManager) {

    // check for a story restart (skip this advance in such cases) 
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      if ( it.next() instanceof Story.EventStoryContinue ) {
        it.remove();
        return null;
      }
    }
    assert( storyEvents.size() == 0 );

    Story newStory = null;
    
    // freeze all movement until the end of the scroll
    if ( mFirstStep ) {
      mCamera = (Camera)spriteManager.findSpriteOfType(Camera.class);
      assert( mCamera != null );
      spriteManager.disableAdvanceForAll();
      mFirstStep = false;
    }

    // shift the room
    final float f = mScrollTime/(float)mTotalTime;
    final int x = Math.round(f*mTargetX),
              y = Math.round(f*mTargetY),
              z = Math.round(f*mTargetZ);
    final int dx = x - mCurrentX,
              dy = y - mCurrentY,
              dz = z - mCurrentZ;
    if ( dx != 0 || dy != 0 || dz != 0 ) {
      mCurrentX = x;
      mCurrentY = y;
      mCurrentZ = z;
      mCamera.shift(dx, dy, dz);
    }

    // unfreeze the sprites at the end
    if ( mScrollTime == mTotalTime ) {
      if ( mEndTimer == 0 ) {
        assert( mCurrentX == mTargetX && 
                mCurrentY == mTargetY &&
                mCurrentZ == mTargetZ );
        spriteManager.enableAdvanceForAll();
        Env.keys().setMode(KeyMonitor.MODE_GAME);
        newStory = mOriginalStory;
      } else {
        mEndTimer--;
      }
    }
    
    if ( mScrollTime < mTotalTime ) mScrollTime++;
    
    // quest aborted
    if ( Env.keys().escape() ) {
      if ( !mEscPressed && newStory == null ) {
        newStory = new QuitStory(this);
        storyEvents.add(new Story.EventGameBegins());
      }
      mEscPressed = true;
    } else {
      mEscPressed = false;
    }    
    
    return newStory;
    
  } // Story.advance()

} // class ScrollStory
