/*
 *  Player.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// the player's character
public class Player extends Sprite3D {

  // story event: the player has been destroyed
  public class EventKilled extends StoryEvent {
    public boolean mSavePoint;
    public EventKilled() { mSavePoint = true; }
  } // class Player.EventKilled

  // time to take various actions
  private static final int kTurnTime      = 3,
                           kStepTime1     = 3,
                           kStepTime2     = 3,
                           kUpStepTime1   = 1,
                           kUpStepTime2   = 1,
                           kHalfStepTime1 = (kStepTime1 + kUpStepTime1)/2,
                           kFireTime      = 7,
                           kReloadTime    = 20;

  // player image in a particular colour scheme
  private static final char kColourScheme[] = { '9', '1' };
  private static final PlayerImage kPlayerImage = 
                                          new PlayerImage(kColourScheme);
  
  // size of the player when navigating blocks
  private static final int kPlayerHeight = 5;

  // how far the player falls before dying
  private static final int kFatalFallDistance = 20;
  
  // colour when killed
  private static final byte kSplatterColour = 9;
  
  // colour of bullets
  private static final byte kBulletColour = 9;
  
  // current position (x, y in block units, z in pixels)
  private int mXPos,
              mYPos,
              mZPos;

  // current direction (see enumeration in Env)
  private int mDirec;
  
  // time remaining to complete the current action
  private int mActionTimer;

  // whether current action is to take a step forward
  private boolean mStepping;
  
  // expected position at the end of a step (only defined if mStepping)
  private int mStepXPos,
              mStepYPos,
              mStepZPos;

  // whether currently airborne
  private boolean mFalling;
  
  // height fallen so far
  private int mFallDistance;
  
  // how far the player can fall
  private int mFatalFallDistance;
  
  // whether currently in the firing pose
  private boolean mFiring;
  
  // time remaining before the player can fire again
  private int mReloadDelay;

  // fire at the next opportunity
  private boolean mFireAway;

  // player has been destroyed
  private boolean mKilled;

  // how far under water the player currently is (or zero)
  private int mLiquidSubmersion;

  // play a sound for the player walking, depending on the terrain
  private boolean mPlaySteppingSound;

  // controller for the player
  private Brain mBrain = new Brain();
  
  // list of objects to navigate around
  private LinkedList<Obstacle> mObstacles = new LinkedList<Obstacle>();
  
  // constructor
  public Player(int x, int y, int z, int direc) {

    mXPos = x;
    mYPos = y;
    mZPos = z;

    assert( direc >= 0 && direc < 4 );
    mDirec = direc;
    
    mActionTimer = 0;

    mStepping = false;
    mStepXPos = mStepYPos = mStepZPos = 0;

    mFalling = false;
    mFallDistance = 0;
    mFatalFallDistance = kFatalFallDistance;
    
    mFiring = false;
    mReloadDelay = 0;
    mFireAway = false;

    mKilled = false;

    mLiquidSubmersion = 0;
    mPlaySteppingSound = false;
    
  } // constructor
  
  // accessors
  public int getXPos() { return mXPos; }
  public int getYPos() { return mYPos; }
  public int getZPos() { return mZPos; }
  public int getDirec() { return mDirec; }
  
  // modify position (ignores obstacles)
  public void shiftPos(int dx, int dy, int dz) {
    
    mXPos += dx;
    mYPos += dy;
    mZPos += dz;
    mStepXPos += dx;
    mStepYPos += dy;
    mStepZPos += dz;
    
  } // shiftPos()
  
  // modify position (stop at obstacles)
  public void slidePos(int direc, int steps) { 
    
    assert( direc >= 0 && direc < 4 );
    assert( steps > 0 );

    final int dx = Env.STEP_X[direc],
              dy = Env.STEP_Y[direc];
    for ( int k = 0 ; k < steps ; k++ ) {
      if ( !standingPosition(mXPos+dx, mYPos+dy, mZPos) ||
           ( mStepping && 
             !standingPosition(mStepXPos+dx, mStepYPos+dy, mStepZPos) ) ) {
        return;
      }
      mXPos += dx;
      mYPos += dy;
      mStepXPos += dx;
      mStepYPos += dy;
    }
    
  } // shiftPos()
  
  // whether the player is performing an action
  public boolean isActing() { return ( mActionTimer > 0 || mStepping ); }
  
  // whether the player is falling
  public boolean isFalling() { return mFalling; }
  
  // how far the player can fall without dying
  public void setFatalFallDistance(int distance) {
    
    assert( distance > 0 );
    mFatalFallDistance = distance;
    
  } // setFatalFallDistance()
  
  // register fatal collision
  public void destroy(int direc) {
    
    assert( direc >= -1 && direc < 4 );
    mKilled = true;

  } // destroy()
  
  // give the player some instructions
  public void addBrain(Brain.Module newBrain) { mBrain.add(newBrain); }
  
  // cancel the player's last instructions
  public void removeBrain() { mBrain.remove(); }
  
  // check for Sprites we want to keep track of
  @Override
  public void observeArrival(Sprite newSprite) { 

    super.observeArrival(newSprite);
    
    if ( newSprite instanceof Obstacle ) {
      mObstacles.add((Obstacle)newSprite);
    }
    
    if ( newSprite instanceof Critter ||
         newSprite instanceof Flame ||
         newSprite instanceof FlameArea ||
         newSprite instanceof FlameBeam ||
         newSprite instanceof Liquid ||
         newSprite instanceof Snake ||
         newSprite instanceof Spook ) {
      mSpritesToWatch.add(newSprite);
    }
    
  } // Sprite.observeArrival()
  
  // keep track of Sprites that have been destroyed
  @Override
  public void observeDeparture(Sprite deadSprite) {

    if ( deadSprite instanceof Obstacle ) {
      mObstacles.remove(deadSprite);
    }

    mSpritesToWatch.remove(deadSprite);
    
    super.observeDeparture(deadSprite);
    
  } // Sprite.observeDeparture()
  
  // move the player
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {

    mBrain.advance();
    final boolean keyRight = mBrain.right(),
                  keyUp    = mBrain.up(),
                  keyLeft  = mBrain.left(),
                  keyDown  = mBrain.down(), 
                  keyFire  = mBrain.fire();

    final int keyDirec = keyRight ? Env.RIGHT
                       : keyUp    ? Env.UP
                       : keyLeft  ? Env.LEFT
                       : keyDown  ? Env.DOWN
                       : -1;
    
    mPlaySteppingSound = false;
    
    if ( mActionTimer > 0 ) {

      // action is in progress
      mActionTimer -= 1;
      if ( mActionTimer == 0 ) {
        mFiring = false;
      }

    } else if ( mStepping ) {
      
      // finish taking a step
      assert( mFalling == false );
      assert( mFiring == false );
      mStepping = false;
      mActionTimer = kStepTime2;
      if ( mStepZPos > mZPos ) mActionTimer += kUpStepTime2;
      mXPos = mStepXPos;
      mYPos = mStepYPos;
      mZPos = mStepZPos;
      
    } else if ( mFalling ) {
      
      // falling
      assert( mStepping == false );
      assert( mFiring == false );
      mFallDistance += 1;
      mZPos -= 1;
      if ( isPlatform(mXPos, mYPos, mZPos) ) {
        mFalling = false;
        if ( mFallDistance >= mFatalFallDistance ) mKilled = true;
        mFallDistance = 0;
        mActionTimer = kStepTime2; 
      }
      
    } else if ( mFireAway ) {
    
      // fire a bullet
      addTheseSprites.add(new Bullet(mXPos, mYPos, mZPos+2, mDirec,
                                     kBulletColour, this));
      mReloadDelay = kReloadTime;
      mActionTimer = kFireTime;
      mFireAway = false;
      mFiring = true;
      Env.sounds().play(Sounds.ARROW);
    
    } else if ( keyDirec >= 0 && keyDirec != mDirec ) {

      // turn in direction of key press
      mDirec = keyDirec;
      mActionTimer = kTurnTime;
      
    } else if ( keyDirec >= 0 ) {
    
      // try stepping forward
      final int xDest = mXPos + ((keyDirec==Env.RIGHT) ? +1 : 0)
                              + ((keyDirec==Env.LEFT)  ? -1 : 0),
                yDest = mYPos + ((keyDirec==Env.UP)    ? +1 : 0)
                              + ((keyDirec==Env.DOWN)  ? -1 : 0);

      for ( int k = 0 ; k < 5 ; k++ ) {
        final int zDest = mZPos + ((k<=2) ? k : (2-k));
        if ( standingPosition(xDest, yDest, zDest) ) {
          mStepXPos = xDest;
          mStepYPos = yDest;
          mStepZPos = zDest;
          mStepping = true;
          break;
        }
      }
      
      if ( mStepping ) {
        mActionTimer = kStepTime1;
        if ( mStepZPos > mZPos ) mActionTimer += kUpStepTime1;
        mPlaySteppingSound = true;
      }

    } else {

      // nothing happening
      
    }

    // check for moving/disappearing platforms: adjust position if possible
    if ( !mFalling ) {
      boolean falling = false,
              invalid = false;
      
      final int dz = checkPosition(mXPos, mYPos, mZPos);
      if ( dz == +1 || dz == -1 ) mZPos += dz;
      if ( dz == +2 ) invalid = true;
      
      if ( mStepping ) {      
        final int dz2 = checkPosition(mStepXPos, mStepYPos, mStepZPos);
        if ( dz2 == +1 || dz2 == -1 ) mStepZPos += dz2;
        if ( dz2 == +2 ) invalid = true;
        if ( dz2 == -2 && mStepZPos > mZPos ) mStepZPos -= 1;
        if ( Math.abs(mStepZPos-mZPos) > 3 ) {
          Env.debug("Warning: player step is too large");
        }
      } else {
        if ( dz == -2 ) falling = true;        
      }
      
      if ( falling ) {
        mFalling = true;
        mFallDistance = 0;
        mActionTimer = 0;
        mFiring = false;
        mFireAway = false;
      }
      if ( invalid ) Env.debug("Warning: player intersects obstacles");
    }

    // check firing
    if ( mReloadDelay > 0 ) {
      mReloadDelay -= 1;
    } else if ( mLiquidSubmersion > 2 ) {
      mFireAway = false;
    } else {
      if ( keyFire && !mFireAway ) {
        mFireAway = true;
      }
    }
    
  } // Sprite.advance()

  // check isPlatform() on all obstacles
  private boolean isPlatform(int x, int y, int z) {
    
    for ( Obstacle ob : mObstacles ) {
      if ( ob.isPlatform(x, y, z) ) return true;
    }
    return false;
    
  } // isPlatform()
  
  // check isEmpty() on all obstacles
  private boolean isEmpty(int x, int y, int z) {
    
    for ( Obstacle ob : mObstacles ) {
      if ( !ob.isEmpty(x, y, z) ) return false;
    }
    return true;
    
  } // isEmpty()
  
  // whether it's possible for the player to be at the specified position
  private boolean standingPosition(int x, int y, int z) {
    
    if ( !isPlatform(x, y, z) ) return false;
    
    for ( int k = 1 ; k <= kPlayerHeight ; k++ ) {
      if ( !isEmpty(x, y, z+k) ) return false;
    }

    return true;
    
  } // standingPosition()
  
  // check for any changes to a previously valid 'standing position'
  // 0 => no change, +1 => platform moved up, +2 => too far up  
  // -1 => platform moved down, -2 => too far down
  private int checkPosition(int x, int y, int z) {

    for ( int dz = +1 ; dz >= -1 ; dz-- ) {
      if ( isPlatform(x, y, z+dz) ) {
        for ( int k = 1 ; k <= kPlayerHeight ; k++ ) {
          if ( !isEmpty(x, y, z+dz+k) ) return +2;
        }
        return dz;
      }
    }
    
    for ( int k = 0 ; k <= kPlayerHeight ; k++ ) {
      if ( !isEmpty(x, y, z+k) ) return +2;
    }
    return -2;
    
  } // checkPosition()
  
  // whether the player intersects a particular position
  public boolean hits(int x, int y, int z) {

    if ( z >= mZPos && z < mZPos + kPlayerHeight ) {
      if ( x == mXPos && y == mYPos ) return true;
      if ( mStepping && x == mStepXPos && y == mStepYPos ) return true;
    }
    return false;
    
  } // hits()
  
  // whether the player intersects a position range
  public boolean hits(int x0, int x1, int y0, int y1, int z0, int z1) {

    assert( x1 >= x0 && y1 >= y0 && z1 >= z0 );
    
    if ( z1 < mZPos || z0 >= mZPos+kPlayerHeight ) return false;
    if ( x0 <= mXPos && x1 >= mXPos && 
         y0 <= mYPos && y1 >= mYPos ) return true;
    if ( mStepping &&
         x0 <= mStepXPos && x1 >= mStepXPos && 
         y0 <= mStepYPos && y1 >= mStepYPos ) return true;
    return false;
    
  } // hits()
  
  // check for collisions, etc.
  @Override
  public void interact() {
    
    mLiquidSubmersion = 0;
    boolean stepInWater = false;
    
    for ( Sprite sp : mSpritesToWatch ) {
      
      if ( sp instanceof Critter ) {
        Critter cr = (Critter)sp;
        for ( int dz = 0 ; dz < kPlayerHeight ; dz++ ) {
          if ( cr.hits(mXPos, mYPos, mZPos+dz) ||
               (mStepping && cr.hits(mStepXPos, mStepYPos, mStepZPos+dz)) ) {
            mKilled = true;
            break;
          }
        }
      }

      else if ( sp instanceof Flame ) {
        Flame f = (Flame)sp;
        if ( f.hits(mXPos, mYPos, mZPos+0.5f*kPlayerHeight,
                    1.0f, 0.5f*kPlayerHeight) ) {
          mKilled = true;
          break;
        }
      }
      
      else if ( sp instanceof FlameArea ) {
        FlameArea fa = (FlameArea)sp;
        if ( fa.hits(mXPos+0.5f, mYPos+0.5f, mZPos+0.5f*kPlayerHeight,
                     0.499f, 0.5f*kPlayerHeight) ) {
          mKilled = true;
          break;
        }
      }
      
      else if ( sp instanceof FlameBeam ) {
        FlameBeam fb = (FlameBeam)sp;
        if ( fb.hits(mXPos, mYPos, mZPos+0.5f*kPlayerHeight,
                     1.0f, 0.5f*kPlayerHeight) ) {
          mKilled = true;
          break;
        }
      }
      
      else if ( sp instanceof Liquid ) {
        Liquid liquid = (Liquid)sp;
        int submersion = liquid.depth(mXPos, mYPos, mZPos);
        if ( mStepping && liquid.isWater() &&
             liquid.depth(mStepXPos, mStepYPos, mStepZPos) > 0 ) {
          stepInWater = true;
        }
        if ( submersion >= liquid.lethalDepth() ) {
          mKilled = true;
        }
        mLiquidSubmersion = Math.max(mLiquidSubmersion, submersion);
      }
      
      else if ( sp instanceof Snake ) {
        Snake s = (Snake)sp;
        for ( int dz = 0 ; dz < kPlayerHeight ; dz++ ) {
          if ( s.hits(mXPos, mYPos, mZPos+dz) ||
               (mStepping && s.hits(mStepXPos, mStepYPos, mStepZPos+dz)) ) {
            mKilled = true;
            break;
          }
        }
      }
      
      else if ( sp instanceof Spook ) {
        Spook s = (Spook)sp;
        for ( int dz = 0 ; dz < kPlayerHeight ; dz++ ) {
          if ( s.hits(mXPos, mYPos, mZPos+dz) ||
               (mStepping && s.hits(mStepXPos, mStepYPos, mStepZPos+dz)) ) {
            mKilled = true;
            break;
          }
        }
      }

    }

    // play sound effect, now we know what the player is walking on
    if ( mPlaySteppingSound ) {
      if ( stepInWater ) Env.sounds().play(Sounds.SPLASH);
      else               Env.sounds().play(Sounds.STEP);
    }
    
  } // Sprite.interact()
  
  // handle consequences of collisions
  @Override
  public void aftermath(LinkedList<Sprite>     addTheseSprites, 
                        LinkedList<Sprite>     killTheseSprites,
                        LinkedList<StoryEvent> newStoryEvents) { 
    
    if ( mKilled ) {
      killTheseSprites.add(this);
      addTheseSprites.add(new Splatter(mXPos, mYPos, mZPos,
                                       (mStepping ? mDirec : -1),
                                       kPlayerHeight, kSplatterColour, 
                                       -1));
      newStoryEvents.add(new EventKilled());
    }
    
  } // Sprite.aftermath()
  
  // display the player
  @Override
  public void draw(EgaCanvas canvas) {

    final int x0 = mCamera.xPos(),
              y0 = mCamera.yPos(),
              z0 = mCamera.zPos();
    
    if ( mStepping ) {
      int z;
      if ( mStepZPos <= mZPos ) {
        z = mZPos;
      } else {
        z = (mActionTimer > kHalfStepTime1) ? (mStepZPos+mZPos)/2 : mStepZPos;
      }
      kPlayerImage.drawStep(canvas, mXPos-x0, mYPos-y0, z-z0, mDirec);
    } else if ( mFiring ) {
      kPlayerImage.drawFiring(canvas, mXPos-x0, mYPos-y0, mZPos-z0, mDirec);
    } else {
      kPlayerImage.drawBasic(canvas, mXPos-x0, mYPos-y0, mZPos-z0, mDirec);
    }
    
  } // Sprite.draw()
  
} // class Player
