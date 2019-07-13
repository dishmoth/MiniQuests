/*
 *  Snake.java
 *  Copyright (c) 2018 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.ArrayList;
import java.util.LinkedList;

// base class for a monster
abstract public class Snake extends Sprite3D implements Obstacle {

  // story event: the monster has been destroyed
  public class EventKilled extends StoryEvent {
    public Snake mSnake;
    public EventKilled(Snake s) { mSnake=s; }
  } // class Snake.EventKilled

  // time to take various actions
  protected static final int kFlashTime  = 4,
                             kDeathTime1 = 8,
                             kDeathTime2 = 0;
  
  // different colour schemes
  protected static final char kColourSchemes[][] = { { 'a', 'a' },   // red (death)
                                                     { 'G', 'm' },   // green
                                                     { 'D', 'z' },   // pink
                                                     { 'q', '4' },   // orange 
                                                     { 's', 's' } }; // yellow (flash)

  // snake images
  protected static SnakeImage kSnakeImages[] = null;
  
  // size of the snake parts
  protected static final int kHeightHead = 4,
                             kHeightBody = 3;

  // current position (x, y in block units, z in pixels)
  protected int mXPos,
                mYPos,
                mZPos;

  // current direction (see enumeration in Env)
  protected int mDirec;

  // previous directions, oldest first
  protected ArrayList<Integer> mBody = new ArrayList<Integer>();
  
  // time between movement steps
  protected int mStepTime1,
                mStepTime2;
  
  // time remaining to complete the current action
  protected int mActionTimer;
  
  // whether current action is to take a step forward
  protected boolean mStepping;
  
  // whether the head can move
  protected boolean mStuck;

  // what happens when the snake can't move
  protected boolean mDieWhenStuck;
  
  // target number of body pieces
  protected int mFullLength;
  
  // snake death animation 
  protected boolean mDying;
  
  // snake is going back to egg form
  protected boolean mHibernating;
  
  // possible positions where the snake can move
  protected Track mTrack;
  
  // which colour scheme to use
  protected int mColour;

  // temporary change of colour
  protected int mFlashColour;
  
  // time remaining for colour change 
  protected int mFlashTimer;

  // list of objects to navigate around
  protected LinkedList<Obstacle> mObstacles = new LinkedList<Obstacle>();

  // reference to the player (or null)
  protected Player mPlayer;
  
  // prepare the images
  static public void initialize() {

    if ( kSnakeImages != null ) return;
    
    final int numColours = kColourSchemes.length;
    kSnakeImages = new SnakeImage[numColours];
    
    for ( int k = 0 ; k < numColours ; k++ ) {
      char cols[] = kColourSchemes[k];
      assert( cols != null && cols.length == 2 );
      
      kSnakeImages[k] = new SnakeImage(new char[]{cols[0], cols[1]});
    }
    
  } // initialize()
  
  // constructor
  public Snake(int x, int y, int z, int direc, Track track) {

    initialize();
    
    mXPos = x;
    mYPos = y;
    mZPos = z;

    mFullLength = 2;
    
    assert( direc >= 0 && direc < 4 );
    mDirec = direc;
    mStepTime1 = mStepTime2 = 3;
    mActionTimer = mStepTime2;
    mStepping = false;
    mStuck = false;
    mDieWhenStuck = true;
    mDying = false;
    mHibernating = false;

    mTrack = track;
    
    mColour = 1;
    mFlashColour = 0;
    mFlashTimer = 0;
   
  } // constructor
  
  // snake identity (1, 2 or 3)
  abstract public int snakeType();

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
    
  } // shiftPos()

  // change speed (delay time during steps)
  public void setSpeed(int stepTime) {
    
    assert( stepTime > 0 );
    mStepTime1 = mStepTime2 = stepTime;
    
  } // setSpeed(step)
  
  // change speed (delay time during steps)
  public void setSpeed(int stepTime1, int stepTime2) {
    
    assert( stepTime1 > 0 && stepTime2 > 0 );
    mStepTime1 = stepTime1;
    mStepTime2 = stepTime2;
    
  } // setSpeed(step1,step2)
  
  // choose the colour scheme
  public void setColour(int scheme) {
    
    assert( scheme >= 0 && scheme < kColourSchemes.length );
    mColour = scheme;
    
  } // setColour()
  
  // returns the colour scheme number
  public int getColour() { return mColour; }

  // check for Sprites we want to keep track of
  @Override
  public void observeArrival(Sprite newSprite) { 

    super.observeArrival(newSprite);
    
    if ( newSprite instanceof Obstacle ) {
      if ( newSprite instanceof Barrier ) {
        if ( !((Barrier)newSprite).blocks(this) ) return;
      }
      mObstacles.add((Obstacle)newSprite);
    } else if ( newSprite instanceof Player ) {
      assert( mPlayer == null );
      mPlayer = (Player)newSprite;
    }
    
  } // Sprite.observeArrival()
  
  // keep track of Sprites that have been destroyed
  @Override
  public void observeDeparture(Sprite deadSprite) {

    if ( deadSprite instanceof Obstacle ) {
      mObstacles.remove(deadSprite);
    } else if ( deadSprite instanceof Player ) {
      assert( mPlayer == deadSprite );
      mPlayer = null;
    }

    super.observeDeparture(deadSprite);
    
  } // Sprite.observeDeparture()
  
  // methods required for the Obstacle interface
  @Override public boolean isEmpty(int x, int y, int z) { return !hits(x,y,z); }
  @Override public boolean isPlatform(int x, int y, int z) { return false; }
  @Override public boolean isVoid(int x, int y, int z) { return false; }

  // temporary change of colour
  public void flash(int colour) {

    assert( colour >= 0 && colour < kColourSchemes.length );    
    mFlashColour = colour;
    mFlashTimer = kFlashTime;
    
  } // flash()
  
  // change the snake's target length
  public void grow(int len) { mFullLength += len; }
  public void shrink(int len) { mFullLength = Math.max(0, mFullLength-len); }
  public void setLength(int len) { mFullLength = len; }
  public int length() { return mFullLength; }
  
  // whether the monster can move in a particular direction
  protected boolean canMove(int x, int y, int direc) {

    if ( mTrack != null && !mTrack.canMove(x, y, mZPos, direc) ) {
      return false;
    }
      
    final int xDest = x + Env.STEP_X[direc],
              yDest = y + Env.STEP_Y[direc];
    if ( hitsBody(xDest, yDest, mZPos) ) return false;

    boolean platform = false;
    for ( Obstacle ob : mObstacles ) {
      if ( ob.isPlatform(xDest, yDest, mZPos) ) platform = true;
      for ( int dz = 1 ; dz < kHeightHead ; dz++ ) {
        if ( !ob.isEmpty(xDest, yDest, mZPos+dz) ) return false;
      }
    }
    if ( !platform ) return false;

    return true;
        
  } // canMove()

  // whether the monster can move in a particular direction
  protected boolean canMove(int direc) {

    return canMove(mXPos, mYPos, direc);
        
  } // canMove()

  // returns a new direction from the possibilities (-1 if stuck) 
  static protected int randomTurn(int direction,
                                  boolean forward,
                                  boolean turnLeft,
                                  boolean turnRight) {
    
    if ( forward ) {
      if ( !turnRight && !turnLeft ) return direction;
      if ( Env.randomBoolean() ) return direction;
    }
    if ( turnRight && turnLeft ) {
      return Env.fold(direction+(Env.randomBoolean() ? +1 : -1), 4);
    }
    if ( turnLeft && !turnRight ) return Env.fold(direction+1, 4);
    if ( turnRight && !turnLeft ) return Env.fold(direction-1, 4);
    return -1;

  } // randomTurn()
  
  // decide which direction to move in next (-1 if none possible)
  protected int chooseDirection() {

    final int direcRight = Env.fold(mDirec-1, 4),
              direcLeft = Env.fold(mDirec+1, 4);
    final boolean forward = canMove(mDirec),
                  turnRight = canMove(direcRight),
                  turnLeft = canMove(direcLeft);

    /*
    if ( mPlayer != null ) {
      int dx = mPlayer.getXPos() - mXPos,
          dy = mPlayer.getYPos() - mYPos;
      int scoreForward = forward
                         ? (dx * Env.STEP_X[mDirec] + dy * Env.STEP_Y[mDirec])
                         : -100,
          scoreRight   = turnRight
                         ? (dx * Env.STEP_X[direcRight] + dy * Env.STEP_Y[direcRight])
                         : -100,
          scoreLeft    = turnLeft
                         ? (dx * Env.STEP_X[direcLeft] + dy * Env.STEP_Y[direcLeft])
                         : -100;
      if ( scoreForward < Math.max(scoreRight, scoreLeft) ) {
        forward = false;
      }
      if ( scoreRight < Math.max(scoreForward, scoreLeft) ) {
        turnRight = false;
      }
      if ( scoreLeft < Math.max(scoreRight, scoreForward) ) {
        turnLeft = false;
      }
    }
    */
    
    return randomTurn(mDirec, forward, turnLeft, turnRight);

  } // chooseDirection()
  
  // move the snake
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {

    if ( mPlayer == null && !mHibernating ) {
      mFullLength = 2;
      mDieWhenStuck = false;
      mHibernating = true;
    }
        
    if ( mActionTimer > 0 ) {

      // action is in progress
      mActionTimer -= 1;
      
    } else if ( mDying ) {

      // explode another body segment
      int x = mXPos,
          y = mYPos;
      for ( int k = mBody.size()-1 ; k >= 0 ; k-- ) {
        final int direc = mBody.get(k);
        x -= Env.STEP_X[direc];
        y -= Env.STEP_Y[direc];
      }
      final byte colour = EgaTools.decodePixel(kColourSchemes[mColour][0]);
      addTheseSprites.add(new Splatter(x, y, mZPos, -1, 1, colour, -1));
      mActionTimer = kDeathTime2;
      if (mBody.size() > 0) {
        mBody.remove(0);
      } else {
        killTheseSprites.add(this);
        newStoryEvents.add(new EventKilled(this));
      }
      
    } else if ( mStepping ) {
      
      // at the end of a step
      mActionTimer = mStepTime2;
      mStepping = false;      
      
      if ( mBody.size() > mFullLength || mStuck ) mBody.remove(0);
      if ( mHibernating && mBody.size() > 1 ) mBody.remove(0);
      
    } else {

      // at the start of a step
      if ( mHibernating && mBody.size() == 0 ) {
        killTheseSprites.add(this);
        addTheseSprites.add(new SnakeEgg(mXPos, mYPos, mZPos, snakeType()));
        return;
      }

      if ( mBody.size() > mFullLength ) mBody.remove(0);
 
      int direc = chooseDirection();
      
      final boolean atJunction = ( (mXPos-10)%3 == 0 && (mYPos-10)%3 == 0 );
      if ( mHibernating && atJunction ) {
        direc = -1;
        mFullLength = 0;
      }
      
      if ( direc < 0 ) {
        mStuck = true;
      } else {
        mDirec = direc;
        mStuck = false;
        
        mBody.add(mDirec);
        mXPos += Env.STEP_X[mDirec];
        mYPos += Env.STEP_Y[mDirec];
      }
      
      if ( mStuck && mDieWhenStuck ) {
        mDying = true;
        mActionTimer = kDeathTime1;
        mFlashTimer = kDeathTime1;
        mFlashColour = 0;
        mStepping = false;
      } else if ( mStuck && mHibernating ){
        mActionTimer = (mBody.size() > 0 ? 0 : mStepTime1);
        mStepping = false;
      } else {
        mActionTimer = mStepTime1;
        mStepping = true;
      }

    }

    if ( mFlashTimer > 0 ) mFlashTimer -= 1;
    
  } // Sprite.advance()

  // true if the end of the tail is stepping at the same speed as the head
  protected boolean tailStepping() {
    
    return (mStepping && (mBody.size() == mFullLength+1 || mStuck));

  } // tailStepping()
  
  // whether the snake's head intersects a particular position
  public boolean hitsHead(int x, int y, int z) {
    
    if ( z >= mZPos && z < mZPos + kHeightHead ) {
      if ( x == mXPos && y == mYPos ) return true;
      if ( mStepping && x == mXPos - Env.STEP_X[mDirec]
                     && y == mYPos - Env.STEP_Y[mDirec] ) return true;
    }
    return false;
    
  } // hitsHead()
  
  // whether the snake's body intersects a particular position
  public boolean hitsBody(int x, int y, int z) {
    
    if ( z < mZPos || z >= mZPos + kHeightBody ) return false;
    int xBody = mXPos,
        yBody = mYPos;
    for ( int k = mBody.size()-1 ; k >= 0 ; k-- ) {
      final int direc = mBody.get(k);
      xBody -= Env.STEP_X[direc];
      yBody -= Env.STEP_Y[direc];
      if ( x == xBody && y == yBody ) return true;
    }
    return false;
    
  } // hitsBody()
  
  // whether the snake intersects a particular position
  public boolean hits(int x, int y, int z) {

    return ( hitsHead(x, y, z) || hitsBody(x, y, z) );
    
  } // hits()
  
  // register a hit on the snake's body
  public void shotInBody(int x, int y) {}
  
  // register a hit on the snake's head
  public void shotInHead() {}
  
  // display the snake
  @Override
  public void draw(EgaCanvas canvas) {

    final int x0 = mCamera.xPos(),
              y0 = mCamera.yPos(),
              z0 = mCamera.zPos();

    final int colour = (mFlashTimer > 0 ? mFlashColour : mColour);
    SnakeImage image = kSnakeImages[colour];

    int x = mXPos - x0,
        y = mYPos - y0,
        z = mZPos - z0;
    
    if (mStepping && !mStuck) image.drawHeadStep(canvas, x, y, z, mDirec);
    else                      image.drawHead(canvas, x, y, z, mDirec);

    for ( int k = mBody.size()-1 ; k >= 0 ; k-- ) {
      final int direc = mBody.get(k);
      final int direcPrev = (k > 0) ? mBody.get(k-1) : Env.NONE;
      final boolean tailStep = (k == 0 && tailStepping());
      x -= Env.STEP_X[direc];
      y -= Env.STEP_Y[direc];
      image.drawBody(canvas, x, y, z, direc, direcPrev, tailStep);
    }
    
  } // Sprite.draw()

} // class Snake
