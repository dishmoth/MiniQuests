/*
 *  Spinner.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// a dangerous obstacle
public class Spinner extends Sprite3D {

  // details of the image
  private static final int   kImageWidth   = 3,
                             kImageHeight  = 4;
  private static final int   kImageRefXPos = 1,
                             kImageRefYPos = 3;
  private static final float kImageDepth   = -0.04f;

  // data for the basic standing image
  private static final String kImagePixels[] = { " 0 "
                                               + "101"
                                               + "101"
                                               + " 0 ",
                                               
                                                 " 0 "
                                               + "001"
                                               + "110"
                                               + " 0 ",

                                                 " 0 "
                                               + "100"
                                               + "011"
                                               + " 0 ",

                                                 " 2 "
                                               + "222"
                                               + "222"
                                               + " 2 " };

  // colour scheme for the image (two basic colours, flash colour)
  private static final char kImageColours[] = { 'q', '4', 's' };
  //private static final char kImageColours[] = { 'H', 'Z' };
  
  // images
  private static final EgaImage kImages[];
  static {

    kImages = new EgaImage[ kImagePixels.length ];
    for ( int k = 0 ; k < kImagePixels.length ; k++ ) {
      kImages[k] = new EgaImage(kImageRefXPos, kImageRefYPos,
                                kImageWidth, kImageHeight,
                                EgaTools.convertColours(kImagePixels[k],
                                                        kImageColours),
                                kImageDepth);
    }
    
  } // static

  // animation rate (ticks)
  private static final int kAnimationDelay = 3;

  // flash duration (ticks)
  private static final int kFlashDelay = 3;
  
  // movement rate (ticks)
  private static final int kStepDelay = 1;
  
  // current position
  private int mXPos,
              mYPos,
              mZPos;

  // left or right pixel at the current position 
  private boolean mPixelRight;
  
  // where the object is currently heading
  private int mXTarget,
              mYTarget;
  
  // time until next movement
  private int mTimer;

  // time until next animation frame (when not moving)
  private int mAnimationTimer;

  // current image
  private int mAnimationFrame;

  // how long the flash stays on for
  private int mFlashTimer;
  
  // target list (triplets [x,y,wait])
  private int mTargets[][];
  
  // current entry in the target list
  private int mTargetsIndex;
  
  // whether to restart the list when finished 
  private boolean mTargetsRepeat;

  // whether to trigger sound effects when moving (false by default)
  private boolean mSilent;

  // whether the object is drawn
  private boolean mVisible;
  
  // constructor
  public Spinner(int xPos, int yPos, int zPos, boolean pixelRight) {
  
    mXPos = xPos;
    mYPos = yPos;
    mZPos = zPos;
    
    mPixelRight = pixelRight;
    
    mXTarget = mXPos;
    mYTarget = mYPos;

    mAnimationTimer = kAnimationDelay;
    mAnimationFrame = 0;
    mFlashTimer = 0;

    mTimer = 0;

    mTargets = null;
    mTargetsIndex = 0;
    mTargetsRepeat = false;
    
    mSilent = false;
    mVisible = true;
    
  } // constructor
  
  // accessors
  public int getXPos() { return mXPos; }
  public int getYPos() { return mYPos; }
  public int getZPos() { return mZPos; }

  // the object's current behaviour
  public boolean moving() { return (mXPos!=mXTarget || mYPos!=mYTarget); }

  // direction the object is currently moving in (or -1)
  public int direction() {
    
    if ( mYPos < mYTarget ) return Env.UP;
    if ( mYPos > mYTarget ) return Env.DOWN;
    if ( mXPos < mXTarget ) return Env.RIGHT;
    if ( mXPos > mXTarget ) return Env.LEFT;
    return -1;
    
  } // direction()
  
  // whether the object intersects a particular position
  public boolean hits(int x, int y, int z) {

    final int height = 3;
    return ( x == mXPos && y == mYPos &&
             z >= mZPos && z < mZPos + height );
    
  } // hits()
  
  // specify a list of points to visit (triplets [x,y,wait])
  public void setTargets(int targets[][], boolean repeat) {
    
    assert( !moving() );
    
    mTargets = targets;
    mTargetsIndex = 0;
    mTargetsRepeat = repeat;
    
  } // setTargets()

  // whether the object should trigger sound effects as it moves
  public void setSilent(boolean val) { mSilent = val; }
  
  // whether the object is drawn
  public void setVisible(boolean val) { mVisible = val; }
  
  // briefly flash the image
  public void flash() { mFlashTimer = kFlashDelay; }
  
  // check for Sprites we want to keep track of
  @Override
  public void observeArrival(Sprite newSprite) { 

    super.observeArrival(newSprite);
    
    if ( newSprite instanceof Critter ||
         newSprite instanceof Player ) {
      mSpritesToWatch.add(newSprite);
    }
    
  } // Sprite.observeArrival()
  
  // keep track of Sprites that have been destroyed
  @Override
  public void observeDeparture(Sprite deadSprite) {

    super.observeDeparture(deadSprite);
    
    if ( deadSprite instanceof Critter ||
         deadSprite instanceof Player ) {
      mSpritesToWatch.remove(deadSprite);
    }
    
  } // Sprite.observeDeparture()

  // set to the next target on the list
  private void nextTargetPosition() {
  
    assert( mTargets != null && mTargetsIndex < mTargets.length );
    int target[] = mTargets[mTargetsIndex];
    assert( target != null && target.length == 3 );
    
    assert( target[0] == mXPos || target[1] == mYPos );
    mXTarget = target[0];
    mYTarget = target[1];
  
  } // nextTargetPosition()
  
  // returns the wait at the current target, and advances the target list
  private int nextTargetDelay() {
    
    assert( mTargets != null && mTargetsIndex < mTargets.length );
    int target[] = mTargets[mTargetsIndex];
    assert( target != null && target.length == 3 );

    final int delay = target[2];
    assert( delay >= 0 );
    
    mTargetsIndex += 1;
    if ( mTargetsIndex >= mTargets.length ) {
      if ( !mTargetsRepeat ) mTargets = null;
      mTargetsIndex = 0;
    }
    
    return delay;
    
  } // nextTargetDelay()
  
  // move and animate
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {

    if ( moving() ) {

      // head towards the current target
      assert( mTimer > 0 );
      if ( --mTimer == 0 ) {
        int direc = direction();
        if ( mPixelRight == (direc==Env.RIGHT || direc==Env.DOWN) ) {
          mXPos += Env.STEP_X[direc];
          mYPos += Env.STEP_Y[direc];
        }
        mPixelRight = !mPixelRight;
  
        if ( mXPos == mXTarget && mYPos == mYTarget ) {
          mTimer = nextTargetDelay();
          mAnimationTimer = 1;
          if ( !mSilent ) Env.sounds().play(Sounds.SPINNER_STOP);
        } else {
          mTimer = kStepDelay;
        }
      }
      
    } else {
      
      // wait to move
      if ( --mAnimationTimer == 0 ) {
        mAnimationFrame = (mAnimationFrame+1) % (kImages.length-1);
        mAnimationTimer = kAnimationDelay;
      }
      if ( mTimer > 0 ) mTimer -= 1;
      if ( mTimer == 0 && mTargets != null ) {
        nextTargetPosition();
        mTimer = 1;
        if ( mXPos == mXTarget && mYPos == mYTarget ) {
          mTimer = nextTargetDelay();
        } else {
          mAnimationFrame = 0;
          mAnimationTimer = kAnimationDelay;
        }
      }
      
    }

    if ( mFlashTimer > 0 ) mFlashTimer -= 1;
    
  } // Sprite.advance()
  
  // check for collisions
  @Override
  public void interact() { 
    
    for ( Sprite sp : mSpritesToWatch ) {
      
      if ( sp instanceof Critter ) {
        Critter target = (Critter)sp;
        if ( target.hits(mXPos, mYPos, mZPos) ) {
          target.stun( direction() );
        }
      }
      
      else if ( sp instanceof Player ) {
        Player target = (Player)sp;
        if ( target.hits(mXPos, mYPos, mZPos) ) {
          target.destroy( direction() );
        }
      }
      
    }
    
  } // Sprite.interact()

  // display the object
  @Override
  public void draw(EgaCanvas canvas) {

    if ( !mVisible && mFlashTimer == 0 ) return;
    
    final int x = mXPos - mCamera.xPos(),
              y = mYPos - mCamera.yPos(),
              z = mZPos - mCamera.zPos();
    final int dx = ( mPixelRight ? 1 : 0 );

    int frame = mAnimationFrame;
    if ( mFlashTimer > 0 ) frame = kImages.length-1;
    
    kImages[frame].draw3D(canvas, 2*x+dx, 2*y, z);

  } // Sprite.draw()

} // class Spinner
