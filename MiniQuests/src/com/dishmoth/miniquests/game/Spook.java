/*
 *  Spook.java
 *  Copyright Simon Hern 2014
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// a monster
public class Spook extends Sprite3D {

  // story event: the monster has been destroyed
  public class EventKilled extends StoryEvent {
    public Spook mSpook;
    public EventKilled(Spook s) { mSpook=s; }
  } // class Spook.EventKilled

  // time to take various actions
  private static final int kTurnTime  = 0,
                           kStepTime1 = 3,
                           kStepTime2 = 3;
  
  // different colour schemes (0,1 => basic colours, 2 => splatter)
  private static final char kColourSchemes[][] = { { 't', 'k', 't' },
                                                   { 'l', 'z', 'l' },
                                                   { 'V', 'x', 'V' } };

  // spook images
  private static SpookImage kSpookImages[] = null;
  
  // size of the spook when navigating blocks
  private static final int kHeight = 3;

  // current position (x, y in block units, z in pixels)
  private int mXPos,
              mYPos,
              mZPos;

  // current direction (see enumeration in Env)
  private int mDirec;

  // previous direction (old value of mDirec, for animation)
  private int mPrevDirec;
  
  // direction spook most recently came from (for navigation)
  private int mDirecFrom;
  
  // time remaining to complete the current action
  private int mActionTimer;

  // spook has just turned, ready to step forward
  private boolean mTurning;
  
  // whether current action is to take a step forward
  private boolean mStepping;
  
  // expected position at the end of a step (only defined if mStepping)
  private int mStepXPos,
              mStepYPos;

  // 0 => vanished, 1 => about to appear, 2 => small (appearing), 
  // 3 => normal, 4 => about to vanish, 5 => small (vanishing), 6 => very small
  private int mState;
  
  // which colour scheme to use
  private int mColour;
  
  // whether the spook has been hit, and the hit direction
  private boolean mDestroyed;
  private int mDestroyDirec;

  // number of steps until the spook vanishes
  private int mStepsUntilVanish;

  // whether the spook vanishes permanently
  private boolean mDestroyOnVanish;
  
  // possible positions where the spook can walk (or null)
  private Track mTrack;
  
  // list of objects to navigate around
  private LinkedList<Obstacle> mObstacles = new LinkedList<Obstacle>();

  // internal array used by checkDirections() to avoid memory allocation
  private boolean mValidDirections[] = new boolean[4];
  
  // prepare the images
  static public void initialize() {

    if ( kSpookImages != null ) return;
    
    final int numColours = kColourSchemes.length;
    kSpookImages = new SpookImage[numColours];
    
    for ( int k = 0 ; k < numColours ; k++ ) {
      char cols[] = kColourSchemes[k];
      assert( cols != null && cols.length == 3 );
      
      kSpookImages[k] = new SpookImage(new char[]{cols[0], cols[1]});
    }
    
  } // initialize()
  
  // constructor
  public Spook(int x, int y, int z, int direc, Track track) {

    initialize();
    
    mXPos = x;
    mYPos = y;
    mZPos = z;

    assert( direc >= 0 && direc < 4 );
    mDirec = direc;
    mPrevDirec = Env.NONE;
    
    mDirecFrom = ((direc+2) % 4);

    mState = 2;
    
    mStepsUntilVanish = 0;
    mDestroyOnVanish = true;
    
    mTrack = track;
    
    mActionTimer = kStepTime2;

    mTurning = false;
    mStepping = false;
    mStepXPos = mStepYPos = 0;

    mColour = 0;
    
    mDestroyed = false;
    mDestroyDirec = -1;
    
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
    
  } // shiftPos()
  
  // whether the spook is performing an action
  public boolean isActing() { return ( mActionTimer > 0 || mStepping ); }

  // choose the colour scheme
  public void setColour(int scheme) {
    
    assert( scheme >= 0 && scheme < kColourSchemes.length );
    mColour = scheme;
    
  } // setColour()
  
  // returns the colour scheme number
  public int getColour() { return mColour; }

  // make the spook disappear as soon as possible
  public void vanish() {
    
    if      ( mState == 1 ) mState = 0;
    else if ( mState == 2 ) mState = 5;
    else if ( mState == 3 ) mState = 4; 
    
  } // vanish()

  //  make the spook disappear after a number of steps
  public void vanishAfterSteps(int steps) {
    
    assert( steps > 0 );
    mStepsUntilVanish = steps;
    
  } // vanishAfterSteps()

  // by default the spook leaves the game permanently when it vanishes
  public void destroyOnVanish(boolean v) { mDestroyOnVanish = v; }
  
  // make the spook appear
  public void appear() { if ( mState == 0 ) mState = 1; }
  
  // access the spook's track
  public void setTrack(Track track) { mTrack = track; }
  public Track getTrack() { return mTrack; }
  
  // register fatal collision
  public void destroy(int direc) {
    
    assert( direc >= -1 && direc < 4 );
    mDestroyed = true;
    mDestroyDirec = direc;

  } // destroy()
  
  // check for Sprites we want to keep track of
  @Override
  public void observeArrival(Sprite newSprite) { 

    super.observeArrival(newSprite);
    
    if ( newSprite instanceof Obstacle ) {
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

  // move the spook
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {

    if ( mActionTimer > 0 ) {

      // action is in progress
      mActionTimer -= 1;

    } else if ( mStepping ) {
      
      // finish taking a step
      mStepping = false;
      mActionTimer = kStepTime2;
      mXPos = mStepXPos;
      mYPos = mStepYPos;
      mPrevDirec = mDirec;
      
      // appear or disappear
      if ( mState == 1 || mState == 2 ) mState += 1;
      if ( mState == 6 ) {
        if ( mDestroyOnVanish ) killTheseSprites.add(this);
        mState = 0;
      }
      if ( mStepsUntilVanish > 0 ) {
        if ( --mStepsUntilVanish == 0 ) vanish();
      }
      
    } else {

      // try stepping
      boolean validDirecs[] = checkDirections(mXPos, mYPos, mZPos);
      
      int direc = mDirec;
      if ( !mTurning || !validDirecs[mDirec] ) {
        LinkedList<Integer> randomDirec = new LinkedList<Integer>();
        for ( int dir = 0 ; dir < 4 ; dir++ ) {
          if ( dir != mDirecFrom && validDirecs[dir] ) randomDirec.add(dir);
        }
        shuffle(randomDirec);
        if ( validDirecs[mDirecFrom] ) randomDirec.add(mDirecFrom);
        if ( randomDirec.size() > 0 ) {
          direc = randomDirec.getFirst();          
        } else {
          direc = (mDirec + Env.randomInt(3)) % 4;
        }
      }

      if ( direc == mDirec && validDirecs[direc] ) {
        mStepXPos = mXPos + Env.STEP_X[mDirec]; 
        mStepYPos = mYPos + Env.STEP_Y[mDirec];
        mDirecFrom = ((mDirec+2) % 4);
        mStepping = true;
        mTurning = false;
        if      ( mState == 4 ) mState = 5;
        else if ( mState == 5 ) mState = 6;
        mActionTimer = kStepTime1;
      } else if ( direc >= 0 ) {
        mDirec = direc;
        mActionTimer = kTurnTime;
        mStepping = false;
        mTurning = true;
      }

    }

  } // Sprite.advance()

  // utility to rearrange the list (can't use Collections.shuffle() due to GWT)
  static private void shuffle(LinkedList<Integer> list) {

    for ( int k = list.size() - 1 ; k >= 0 ; k-- ) {
      int i = Env.randomInt(k+1);
      int temp = list.get(k);
      list.set(k, list.get(i));
      list.set(i, temp);
    }
    
  } // shuffle()
  
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
  
  // whether it's possible for the spook to be at the specified position
  private boolean standingPosition(int x, int y, int z) {
    
    if ( !checkIsPlatform(x, y, z) ) return false;
    
    for ( int k = 1 ; k <= kHeight ; k++ ) {
      if ( !checkIsEmpty(x, y, z+k) ) return false;
    }

    return true;
    
  } // standingPosition()

  // which directions are possible to step in from here 
  private boolean[] checkDirections(int x, int y, int z) {

    for ( int direc = 0 ; direc < 4 ; direc++ ) {
      mValidDirections[direc] = false;
      
      if ( mTrack != null && !mTrack.canMove(x, y, z, direc) ) continue;
      
      //final int xDest = x + Env.STEP_X[direc],
      //          yDest = y + Env.STEP_Y[direc];      
      //if ( standingPosition(xDest, yDest, z) ) mValidDirections[direc] = true;
      
      mValidDirections[direc] = true;
    }
    return mValidDirections;
        
  } // checkDirections()
  
  // current height of the spook
  private int height() {
    
    if ( mState == 0 || mState == 1 || mState == 6 ) return 0;
    if ( mState == 3 || mState == 4 )                return kHeight;
    
    return ( mStepping ? kHeight : 0 );
    
  } // height()
  
  // whether the spook intersects a particular position
  public boolean hits(int x, int y, int z) {

    if ( z >= mZPos && z < mZPos + height() ) {
      if ( x == mXPos && y == mYPos ) return true;
      if ( mStepping && x == mStepXPos && y == mStepYPos ) return true;
    }
    return false;
    
  } // hits()
  
  // whether the player intersects a position range
  public boolean hits(int x0, int x1, int y0, int y1, int z0, int z1) {

    assert( x1 >= x0 && y1 >= y0 && z1 >= z0 );
    
    if ( z1 < mZPos || z0 >= mZPos+height() ) return false;
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
      newStoryEvents.add(new EventKilled(this));
      final byte colour = EgaTools.decodePixel(kColourSchemes[mColour][2]);
      addTheseSprites.add(new Splatter(mXPos, mYPos, mZPos,
                                       (mStepping ? mDirec : -1),
                                       kHeight, colour, mDestroyDirec));
      Env.sounds().play(Sounds.CRITTER_DEATH);
    }
    
  } // Sprite.aftermath()
  
  // display the spook
  @Override
  public void draw(EgaCanvas canvas) {

    if ( mState == 0 || mState == 1 ) return;
    
    final int x0 = mCamera.xPos(),
              y0 = mCamera.yPos(),
              z0 = mCamera.zPos();

    SpookImage image = kSpookImages[mColour];

    boolean small = ( mState == 2 || mState == 5 );
    int prevDirec = (mState==2) ? Env.NONE : mPrevDirec;

    if ( mState == 6 ) {
      assert( mStepping = true );
      image.drawBasic(canvas, mXPos-x0, mYPos-y0, mZPos-z0, 
                      mDirec, Env.NONE, true);
    } else if ( mStepping ) {
      image.drawStep(canvas, mXPos-x0, mYPos-y0, mZPos-z0, 
                     mDirec, prevDirec, small);
    } else {
      image.drawBasic(canvas, mXPos-x0, mYPos-y0, mZPos-z0, 
                      mDirec, prevDirec, small);
    }
    
  } // Sprite.draw()

} // class Spook
