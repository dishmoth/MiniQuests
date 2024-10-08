/*
 *  Critter.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// a monster
public class Critter extends Sprite3D implements Obstacle {

  // time to take various actions
  private static final int kTurnTime      = 3,
                           kStepTime1     = 5,
                           kStepTime2     = 5,
                           kUpStepTime1   = 1,
                           kUpStepTime2   = 1,
                           kHalfStepTime1 = (kStepTime1 + kUpStepTime1)/2;
  
  // time for various effects when hit
  private static final int kFreezeTimeDefault = 50,
                           kFlashTime         = 4;

  // different colour schemes (0,1 => basic colours, 2 => flash, 3 => splatter)
  private static final char kColourSchemes[][] = { { '2', 'G', 'I', '2' },
                                                   { 'z', 'D', 'l', 'z' },
                                                   { 'q', 'a', 'c', 'q' },
                                                   { 'h', 'P', 'x', 'h' },
                                                   { 'q', '4', 's', 'q' } };

  // critter images
  private static CritterImage kCritterImages[] = null;
  private static CritterImage kCritterFlashImages[] = null;
  
  // size of the critter when navigating blocks
  private static final int kHeight = 4;

  // current position (x, y in block units, z in pixels)
  protected int mXPos,
                mYPos,
                mZPos;

  // current direction (see enumeration in Env)
  protected int mDirec;
  
  // direction critter most recently came from
  private int mDirecFrom;
  
  // time remaining to complete the current action
  private int mActionTimer;

  // critter has just turned, ready to step forward
  private boolean mTurning;
  
  // whether current action is to take a step forward
  protected boolean mStepping;
  
  // expected position at the end of a step (only defined if mStepping)
  private int mStepXPos,
              mStepYPos,
              mStepZPos;

  // whether currently airborne
  private boolean mFalling;
  
  // height fallen so far
  private int mFallDistance;

  // how long the critter stays frozen for (may be zero)
  private int mFreezeTime;

  // time remaining until the critter goes back to normal
  private int mFreezeTimer;

  // which colour scheme to use
  private int mColour;
  
  // true for a critter that is easily killed
  private boolean mInstantKill;
  
  // whether the critter has been hit, and the hit direction
  private boolean mDestroyed;
  private int mDestroyDirec;

  // true for a critter than makes no sound
  private boolean mSilent;
  
  // possible positions where the Critter can walk (or null)
  private Track mTrack;
  
  // list of objects to navigate around
  private LinkedList<Obstacle> mObstacles = new LinkedList<Obstacle>();

  // prepare the images
  static public void initialize() {

    if ( kCritterImages != null ) return;
    
    final int numColours = kColourSchemes.length;
    kCritterImages = new CritterImage[numColours];
    kCritterFlashImages = new CritterImage[numColours];
    
    for ( int k = 0 ; k < numColours ; k++ ) {
      char cols[] = kColourSchemes[k];
      assert( cols != null && cols.length == 4 );
      
      kCritterImages[k] = new CritterImage(new char[]{cols[0], cols[1]});
      kCritterFlashImages[k] = new CritterImage(new char[]{cols[2], cols[2]});
    }
    
  } // initialize()
  
  // constructor
  public Critter(int x, int y, int z, int direc, Track track) {

    initialize();
    
    mXPos = x;
    mYPos = y;
    mZPos = z;

    assert( direc >= 0 && direc < 4 );
    mDirec = direc;

    mDirecFrom = ((direc+2) % 4);
    
    mTrack = track;
    
    mActionTimer = 0;

    mTurning = false;
    mStepping = false;
    mStepXPos = mStepYPos = mStepZPos = 0;

    mFalling = false;
    mFallDistance = 0;

    mFreezeTime = kFreezeTimeDefault;
    mFreezeTimer = 0;

    mColour = 0;
    mSilent = false;
    
    mInstantKill = false;
    
    mDestroyed = false;
    mDestroyDirec = -1;
    
  } // constructor
  
  // accessors
  public int getXPos() { return mXPos; }
  public int getYPos() { return mYPos; }
  public int getZPos() { return mZPos; }
  public int getDirec() { return mDirec; }

  // current position taking stepping into account
  protected int getZPosStepping() {

    if ( mStepping ) {
      if (mStepZPos <= mZPos) {
        return mZPos;
      } else {
        return (mActionTimer > kHalfStepTime1)
                ? (mStepZPos + mZPos) / 2
                : mStepZPos;
      }
    }
    return mZPos;

  } // getZPosStepping()

  // modify position (ignores obstacles)
  public void shiftPos(int dx, int dy, int dz) {
    
    mXPos += dx;
    mYPos += dy;
    mZPos += dz;
    mStepXPos += dx;
    mStepYPos += dy;
    mStepZPos += dz;
    
  } // shiftPos()
  
  // whether the critter is performing an action
  public boolean isActing() { return ( mActionTimer > 0 || mStepping ); }

  // register a collision
  public void stun(int direc) { 
    
    assert( direc >= -1 && direc < 4 );
    if ( mInstantKill ) {
      destroy(direc);
      return;
    }
    mFreezeTimer = mFreezeTime; 
    if ( !mSilent ) Env.sounds().play(Sounds.CRITTER_STUN);
    
  } // stun()

  // choose the colour scheme
  public void setColour(int scheme) {
    
    assert( scheme >= 0 && scheme < kColourSchemes.length );
    mColour = scheme;
    
  } // setColour()
  
  // returns the colour scheme number
  public int getColour() { return mColour; }
  
  // access the critter's track
  public void setTrack(Track track) { mTrack = track; }
  public Track getTrack() { return mTrack; }
  
  // set whether the critter is killed by bullets
  public void easilyKilled(boolean v) { mInstantKill = v; }
  
  // set how long a shot stuns the critter for (may be zero)
  public void setStunTime(int t) { mFreezeTime = Math.max(0, t); }
  
  // query whether critter is frozen
  public boolean isStunned() { return (mFreezeTimer > 0); }
  
  // register fatal collision
  public void destroy(int direc) {
    
    assert( direc >= -1 && direc < 4 );
    mDestroyed = true;
    mDestroyDirec = direc;

  } // destroy()
  
  // set whether the critter makes any sound
  public void setSilent(boolean v) { mSilent = v; }
  
  // check for Sprites we want to keep track of
  @Override
  public void observeArrival(Sprite newSprite) { 

    super.observeArrival(newSprite);
    
    if ( newSprite instanceof Obstacle ) {
      if ( newSprite instanceof Barrier ) {
        if ( !((Barrier)newSprite).blocks(this) ) return;
      }
      mObstacles.add((Obstacle)newSprite);
    }
    
  } // Sprite.observeArrival()
  
  // keep track of Sprites that have been destroyed
  @Override
  public void observeDeparture(Sprite deadSprite) {

    if ( deadSprite instanceof Obstacle ) {
      mObstacles.remove(deadSprite);
    }

    super.observeDeparture(deadSprite);
    
  } // Sprite.observeDeparture()

  // methods required for the Obstacle interface
  public boolean isEmpty(int x, int y, int z) { return !hits(x,y,z); }
  public boolean isPlatform(int x, int y, int z) { return false; }
  public boolean isVoid(int x, int y, int z) { return false; }
  
  // move the critter
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {

    if ( mFreezeTimer > 0 && !mFalling ) {
      
      // critter is stunned
      mFreezeTimer -= 1;
      
    } else if ( mActionTimer > 0 ) {

      // action is in progress
      mActionTimer -= 1;

    } else if ( mStepping ) {
      
      // finish taking a step
      assert( mFalling == false );
      mStepping = false;
      mActionTimer = kStepTime2;
      if ( mStepZPos > mZPos ) mActionTimer += kUpStepTime2;
      mXPos = mStepXPos;
      mYPos = mStepYPos;
      mZPos = mStepZPos;
      
    } else if ( mFalling ) {
      
      // falling
      assert( mStepping == false );
      assert( mFallDistance >= 0 );
      mFallDistance += 1;
      mZPos -= 1;
      if ( checkIsPlatform(mXPos, mYPos, mZPos) ) {
        mFalling = false;
        mFallDistance = 0;
        mActionTimer = kStepTime2; 
      }
      
    } else {

      // try stepping
      Integer zDests[] = checkDirections(mXPos, mYPos, mZPos);
      
      int direc = mDirec;
      if ( !mTurning || zDests[mDirec] == null ) {
        LinkedList<Integer> randomDirec = new LinkedList<Integer>();
        for ( int dir = 0 ; dir < 4 ; dir++ ) {
          if ( dir != mDirecFrom && zDests[dir] != null ) randomDirec.add(dir);
        }
        Env.shuffle(randomDirec);
        if ( zDests[mDirecFrom] != null ) randomDirec.add(mDirecFrom);
        if ( randomDirec.size() > 0 ) {
          direc = randomDirec.getFirst();          
        } else {
          direc = (mDirec + Env.randomInt(3)) % 4;
        }
      }

      if ( direc == mDirec && zDests[direc] != null ) {
        mStepXPos = mXPos + Env.STEP_X[mDirec]; 
        mStepYPos = mYPos + Env.STEP_Y[mDirec];
        mStepZPos = zDests[direc];
        mDirecFrom = ((mDirec+2) % 4);
        mStepping = true;
        mTurning = false;
        mActionTimer = kStepTime1;
        if ( mStepZPos > mZPos ) mActionTimer += kUpStepTime1;
      } else if ( direc >= 0 ) {
        mDirec = direc;
        mActionTimer = kTurnTime;
        mStepping = false;
        mTurning = true;
      }

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
      }
      if ( invalid ) Env.debug("Warning: critter intersects obstacles");
    }

  } // Sprite.advance()

  // check isPlatform() on all obstacles
  private boolean checkIsPlatform(int x, int y, int z) {
    
    for ( Obstacle ob : mObstacles ) {
      if ( ob.isPlatform(x, y, z) ) return true;
    }
    return false;
    
  } // isPlatform()
  
  // check isEmpty() on all obstacles
  private boolean checkIsEmpty(int x, int y, int z) {
    
    for ( Obstacle ob : mObstacles ) {
      if ( !ob.isEmpty(x, y, z) ) return false;
    }
    return true;
    
  } // checkIsEmpty()
  
  // whether it's possible for the critter to be at the specified position
  private boolean standingPosition(int x, int y, int z) {
    
    if ( !checkIsPlatform(x, y, z) ) return false;
    
    for ( int k = 1 ; k <= kHeight ; k++ ) {
      if ( !checkIsEmpty(x, y, z+k) ) return false;
    }

    return true;
    
  } // standingPosition()

  // which directions are possible to step in from here 
  // (returns array of step end z-positions, or null if direction invalid)
  private Integer[] checkDirections(int x, int y, int z) {

    Integer zResults[] = new Integer[4];
    for ( int direc = 0 ; direc < 4 ; direc++ ) {
      if ( mTrack != null && !mTrack.canMove(x, y, z, direc) ) continue;
      
      final int xDest = x + Env.STEP_X[direc],
                yDest = y + Env.STEP_Y[direc];
      
      for ( int k = 0 ; k < 5 ; k++ ) {
        final int zDest = z + ((k<=2) ? k : (2-k));
        if ( standingPosition(xDest, yDest, zDest) ) {
          zResults[direc] = zDest;
          break;
        }
      }
    }
    return zResults;
        
  } // checkDirections()
  
  // check for any changes to a previously valid 'standing position'
  // 0 => no change, +1 => platform moved up, +2 => too far up  
  // -1 => platform moved down, -2 => too far down
  private int checkPosition(int x, int y, int z) {

    for ( int dz = +1 ; dz >= -1 ; dz-- ) {
      if ( checkIsPlatform(x, y, z+dz) ) {
        for ( int k = 1 ; k <= kHeight ; k++ ) {
          if ( !checkIsEmpty(x, y, z+dz+k) ) return +2;
        }
        return dz;
      }
    }
    
    for ( int k = 0 ; k <= kHeight ; k++ ) {
      if ( !checkIsEmpty(x, y, z+k) ) return +2;
    }
    return -2;
    
  } // checkPosition()

  // whether the critter intersects a particular position
  public boolean hits(int x, int y, int z) {

    if ( z >= mZPos && z < mZPos + kHeight ) {
      if ( x == mXPos && y == mYPos ) return true;
      if ( mStepping && x == mStepXPos && y == mStepYPos ) return true;
    }
    return false;
    
  } // hits()
  
  // whether the critter intersects a position range
  public boolean hits(int x0, int x1, int y0, int y1, int z0, int z1) {

    assert( x1 >= x0 && y1 >= y0 && z1 >= z0 );
    
    if ( z1 < mZPos || z0 >= mZPos+kHeight ) return false;
    if ( x0 <= mXPos && x1 >= mXPos && 
         y0 <= mYPos && y1 >= mYPos ) return true;
    if ( mStepping &&
         x0 <= mStepXPos && x1 >= mStepXPos && 
         y0 <= mStepYPos && y1 >= mStepYPos ) return true;
    return false;
    
  } // hits()
  
  // handle consequences of collisions
  @Override
  public void aftermath(LinkedList<Sprite>     addTheseSprites, 
                        LinkedList<Sprite>     killTheseSprites,
                        LinkedList<StoryEvent> newStoryEvents) { 
    
    if ( mDestroyed ) {
      killTheseSprites.add(this);
      final byte colour = EgaTools.decodePixel(kColourSchemes[mColour][3]);
      addTheseSprites.add(new Splatter(mXPos, mYPos, mZPos,
                                       (mStepping ? mDirec : -1),
                                       kHeight, colour, mDestroyDirec));
      if ( !mSilent ) Env.sounds().play(Sounds.CRITTER_DEATH);
    }
    
  } // Sprite.aftermath()

  // the current display object for the critter (including colour and flash)
  protected CritterImage getCritterImage() {

    boolean flash = (mFreezeTimer > Math.max(0, mFreezeTime-kFlashTime));
    return (flash ? kCritterFlashImages[mColour] : kCritterImages[mColour]);

  } // getCritterImage()

  // display the critter
  @Override
  public void draw(EgaCanvas canvas) {

    final int x = mXPos - mCamera.xPos(),
              y = mYPos - mCamera.yPos(),
              z = getZPosStepping() - mCamera.zPos();

    CritterImage image = getCritterImage();
    
    if ( mStepping ) {
      image.drawStep(canvas, x, y, z, mDirec);
    } else {
      image.drawBasic(canvas, x, y, z, mDirec);
    }
    
  } // Sprite.draw()

} // class Critter
