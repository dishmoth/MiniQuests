/*
 *  Triffid.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// a shooting turret
public class Triffid extends Sprite3D implements Obstacle {

  // story event: the triffid has been killed
  public class EventKilled extends StoryEvent {
    public Triffid mSource;
    public EventKilled(Triffid tr) { mSource = tr; }
  } // class Triffid.EventKilled

  // images in various colour schemes 
  private static final char kBasicColours[] = {'a','W'},
                            kSleepColours[] = {'2','G'},
                            kHitColours[]   = {'b','b'},
                            kFlareColours[] = {'b','e'};
  private static final TriffidImage kTriffidImages = 
                                        new TriffidImage(kBasicColours),
                                    kSleepImages = 
                                        new TriffidImage(kSleepColours),
                                    kHitImages = 
                                        new TriffidImage(kHitColours),
                                    kFlareImages = 
                                        new TriffidImage(kFlareColours);

  // colour of bullets
  private static final byte kBulletColour = 32;
  
  // different states
  enum TriffidState { kGrowing, kNormal, kSleeping, kDying };
  
  // time (ticks) for recovery after being hit
  private static final int kHitDelay = 6;

  // time (ticks) for the creature to grow a stage
  private static final int kGrowthDelay = 4,
                           kDeathDelay  = 4;
  
  // time (ticks) for the creature to wake after sleeping
  private static final int kWakeDelay = 10;
  
  // time (ticks) until another bullet can be fired
  private static final int kReloadDelay = 25;

  // time (ticks) for the normal colour scheme to return after firing
  private static final int kFlareDelay = 5;

  // how far the turret looks for the player
  private static final int kDefaultFireRange = 10;
  
  // position (x, y in block units, z in pixels)
  private int mXPos,
              mYPos,
              mZPos;

  // direction (see enumeration in Env)
  private int mDirec;

  // time (ticks) for one quarter rotation (+ve, -ve, or zero for not turning)
  private int mRotateDelay;
  
  // how long until the triffid turns
  private int mRotateTimer;

  // how long until triffid recovers from from a hit (0 if not hit)
  private int mHitTimer;

  // how long until the triffid grows a stage
  private int mGrowthTimer;
  
  // which stage of growth the triffid is in
  private int mGrowth;
  
  // how long until the triffid can fire again
  private int mReloadTimer;

  // how long the triffid changes colour for when firing
  private int mFlareTimer;
  
  // there is a target, fire at it if possible
  private boolean mFireAway;
  
  // current state
  private TriffidState mState;

  // whether the triffid is initially asleep
  private boolean mSleepy;

  // how far the turret looks for the player
  private int mFireRange;
  
  // whether the triffid automatically shoots when shot at
  private boolean mRetaliates;

  // triffid is about to retaliate
  private boolean mReturnFire;
  
  // constructor
  public Triffid(int x, int y, int z, int direc) {

    mXPos = x;
    mYPos = y;
    mZPos = z;

    assert( direc >= 0 && direc < 4 );
    mDirec = direc;

    mRotateDelay = 0;
    
    mRotateTimer = 0;
    mHitTimer = 0;
    
    mGrowthTimer = kGrowthDelay;
    mGrowth = 0;

    mReloadTimer = kReloadDelay;
    mFireAway = false;
    mFlareTimer = 0;

    mState = TriffidState.kGrowing;
    
    mSleepy = false;

    mFireRange = kDefaultFireRange;
    
    mRetaliates = true;
    mReturnFire = false;
    
  } // constructor
  
  // accessors
  public int getXPos() { return mXPos; }
  public int getYPos() { return mYPos; }
  public int getZPos() { return mZPos; }
  public int getDirec() { return mDirec; }
  
  // current height of the triffid
  public int height() {
    
    int h = 3;
    if ( (mState == TriffidState.kGrowing || mState == TriffidState.kDying) &&
         mGrowth < TriffidImage.growthStages() ) {
      h = Math.max(0, h-(TriffidImage.growthStages()-mGrowth-1));
    }
    return h;
    
  } // height();

  // whether the triffid is alert and dangerous
  public boolean isActive() { return (mState == TriffidState.kNormal); }
  
  // whether the triffid is still sleeping
  public boolean isAsleep() { return mSleepy; } 
  
  // set the triffid to sleep until it is shot
  public void setSleepMode() { 

    if ( mState == TriffidState.kNormal ) mState = TriffidState.kGrowing;
    assert( mState == TriffidState.kGrowing );
    mSleepy = true;
    
  } // setSleepMode()
  
  // fast-forward the growth of the triffid 
  public void setFullyGrown() {
    
    assert( mState == TriffidState.kGrowing );
    mGrowth = TriffidImage.growthStages()-1;
    if ( !mSleepy ) mState = TriffidState.kNormal;
    
  } // setFullyGrown()

  // set the current direction
  public void setDirec(int direc) {
    
    assert( direc >= 0 && direc < 4 );
    mDirec = direc;
    
  } // setDirec()
  
  // set the turning speed (+ve, -ve, or zero for not turning)
  public void setRotateRate(int delay) { 
    
    mRotateDelay = delay;
    mRotateTimer = Math.abs(mRotateDelay);
    
  } // setRotateRate()
  
  // set the triffid to fire back when shot
  public void setRetaliates(boolean v) { mRetaliates = v; }

  // set how far the triffid looks for targets
  public void setFireRange(int d) { assert(d >= 0); mFireRange = d; }
  
  // check for Sprites we want to keep track of
  @Override
  public void observeArrival(Sprite newSprite) { 

    super.observeArrival(newSprite);
    
    if ( newSprite instanceof Player ||
         newSprite instanceof FlameBeam ) {
      mSpritesToWatch.add(newSprite);
    }
    
  } // Sprite.observeArrival()
  
  // keep track of Sprites that have been destroyed
  @Override
  public void observeDeparture(Sprite deadSprite) {

    mSpritesToWatch.remove(deadSprite);

    super.observeDeparture(deadSprite);
    
  } // Sprite.observeDeparture()

  // methods required for the Obstacle interface
  public boolean isPlatform(int x, int y, int z) { return false; }
  public boolean isVoid(int x, int y, int z) { return false; }

  // whether there is space at the specified position
  public boolean isEmpty(int x, int y, int z) {
    
    if ( x == mXPos && y == mYPos && z >= mZPos && z <= mZPos+height() ) {
      return false;
    }
    return true;
 
  } // Obstacle.isEmpty()

  // whether the triffid intersects a particular position
  public boolean hits(int x, int y, int z) {
    
    if ( x == mXPos && y == mYPos && z >= mZPos && z <= mZPos+height() ) {
      return true;
    }
    return false;
    
  } // hits()
  
  // report that the triffid has been shot
  public void stun(int direc, boolean lethal) {

    assert( direc >= -1 && direc < 4 );
    
    if ( mState == TriffidState.kDying ) {
      return;
    }
    
    mHitTimer = kHitDelay;
    mFlareTimer = 0;
    
    if ( mState == TriffidState.kSleeping ) {
      if ( direc != -1 ) mDirec = (direc+3)%4;
    }

    if ( mRetaliates && mState == TriffidState.kNormal ) {
      mReturnFire = true;
    }
    
    if ( lethal && mState == TriffidState.kNormal ) {
      mState = TriffidState.kDying;
      mGrowth = TriffidImage.growthStages()-1;
      mGrowthTimer = kDeathDelay;
      Env.sounds().play(Sounds.TRIFFID_DEATH);
    } else {
      Env.sounds().play(Sounds.TRIFFID_HIT);
    }
    
  } // stun()
  
  // make the triffid quietly disappear
  public void kill() {
    
    if ( mState == TriffidState.kDying ) {
      return;
    }
    
    mState = TriffidState.kDying;
    mGrowth = Math.min(mGrowth, TriffidImage.growthStages()-1);
    mGrowthTimer = kDeathDelay;
      
  } // kill()
  
  // update the turret
  @Override
  public void advance(LinkedList<Sprite>     addTheseSprites,
                      LinkedList<Sprite>     killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {

    switch ( mState ) {
      
      case kGrowing: {
        if ( --mGrowthTimer <= 0 ) {
          mGrowthTimer = kGrowthDelay;
          mGrowth += 1;
          if ( mGrowth == TriffidImage.growthStages() ) {
            mState = ( mSleepy ? TriffidState.kSleeping 
                               : TriffidState.kNormal );
          }
        }
      } break;

      case kNormal: {
        if ( mRotateDelay != 0 ) {
          if ( mHitTimer == 0 && --mRotateTimer <= 0 ) {
            mRotateTimer = Math.abs(mRotateDelay);
            mDirec = (mDirec + ((mRotateDelay>0) ? +1 : +3))%4;
          }
        }
      } break;
      
      case kSleeping: {
      } break;
      
      case kDying: {
        if ( mHitTimer == 0 ) {
          if ( --mGrowthTimer <= 0 ) {
            mGrowthTimer = kDeathDelay;
            mGrowth -= 1;
            if ( mGrowth < 0 ) {
              killTheseSprites.add(this);
              newStoryEvents.add(new EventKilled(this));
            }
          }
        }
      } break;

      default: assert(false);
    }
    
    if ( mHitTimer > 0 ) {
      if ( --mHitTimer == 0 ) {
        if ( mState == TriffidState.kSleeping ) {
          assert( mSleepy );
          mSleepy = false;
          mState = TriffidState.kGrowing;
          mGrowth = TriffidImage.growthStages()-1;
          mGrowthTimer = kWakeDelay;
        }
      }
    }

    if ( mReloadTimer > 0 ) mReloadTimer--;
    if ( mFlareTimer > 0 ) mFlareTimer--;
    
  } // Sprite.advance()

  // check for things to shoot at
  @Override
  public void interact() {
    
    int x0 = mXPos - ( mDirec == Env.LEFT  ? mFireRange : 0 ),
        x1 = mXPos + ( mDirec == Env.RIGHT ? mFireRange : 0 ),
        y0 = mYPos - ( mDirec == Env.DOWN  ? mFireRange : 0 ),
        y1 = mYPos + ( mDirec == Env.UP    ? mFireRange : 0 ),
        z  = mZPos + 3;

    mFireAway = false;
    
    for ( Sprite sp : mSpritesToWatch ) {
      
      if ( sp instanceof Player ) {
        Player target = (Player)sp;
        if ( target.hits(x0, x1, y0, y1, z, z) ) {
          mFireAway = true;
        }
      }
      
      else if ( sp instanceof FlameBeam ) {
        FlameBeam fb = (FlameBeam)sp;
        if ( fb.hits(mXPos, mYPos, mZPos+1.0f, 0.1f, 1.0f) ) {
          kill();
          break;
        }
          
      }
    }
    
  } // Sprite.interact()
  
  // fire bullets, etc.
  @Override
  public void aftermath(LinkedList<Sprite>     addTheseSprites, 
                        LinkedList<Sprite>     killTheseSprites,
                        LinkedList<StoryEvent> newStoryEvents) {

    if ( mReturnFire ) mFireAway = true;
    
    if ( mFireAway && 
         mState == TriffidState.kNormal && 
         mReloadTimer == 0 && 
         mHitTimer == 0 ) {
      addTheseSprites.add(new Bullet(mXPos + Env.STEP_X[mDirec],
                                     mYPos + Env.STEP_Y[mDirec], 
                                     mZPos+3, mDirec, 
                                     kBulletColour, this));
      mReloadTimer = kReloadDelay;
      mFireAway = false;
      mFlareTimer = ( mReturnFire ? 0 : kFlareDelay );
      if ( !mReturnFire ) Env.sounds().play(Sounds.TRIFFID_FIRE);
      mReturnFire = false;
    }
    
  } // Sprite.aftermath()
  
  // display the creature
  @Override
  public void draw(EgaCanvas canvas) {

    final int x = mXPos - mCamera.xPos(),
              y = mYPos - mCamera.yPos(),
              z = mZPos - mCamera.zPos();

    switch (mState) {
    
      case kGrowing: {
        TriffidImage images = (mSleepy ? kSleepImages : kTriffidImages);
        if ( mHitTimer > 0 ) images = kHitImages;
        images.drawGrowing(canvas, x, y, z, mGrowth);
      } break;
    
      case kNormal: {
        TriffidImage images = ( (mHitTimer > 0) ? kHitImages 
                              : (mFlareTimer > 0) ? kFlareImages
                                                  : kTriffidImages);
        images.drawBasic(canvas, x, y, z, mDirec);
      } break;
      
      case kSleeping: {
        TriffidImage images = ((mHitTimer > 0) ? kHitImages : kSleepImages);
        images.drawGrowing(canvas, x, y, z, TriffidImage.growthStages()-1);
      } break;
      
      case kDying: {
        if ( mHitTimer > 0 ) {
          kHitImages.drawBasic(canvas, x, y, z, mDirec);
        } else {
          kTriffidImages.drawGrowing(canvas, x, y, z, mGrowth);
        }
      } break;
    
      default: assert(false);
    }
    
  } // Sprite.draw()

} // class Triffid
