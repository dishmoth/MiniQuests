/*
 *  SnakeBoss3.java
 *  Copyright (c) 2018 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// the third snake boss
public class SnakeBoss3 extends Snake {
  
  // temporary workspace: 0 for empty, 1 for snake/wall, 2 for player reachable
  private int mWaypoints[][] = { { 1, 1, 1, 1, 1, 1 },
                                 { 1, 0, 0, 0, 0, 1 },
                                 { 1, 0, 0, 0, 0, 1 },
                                 { 1, 0, 0, 0, 0, 1 },
                                 { 1, 0, 0, 0, 0, 1 },
                                 { 1, 1, 1, 1, 1, 1 } };

  // true if the snake can't move because its tail is shrinking
  private boolean mFrozen = false;
  
  // true if the snake has assumed its final form
  private boolean mTransformed = false;
  
  // constructor
  public SnakeBoss3(int x, int y, int z, int direc) {

    super(x, y, z, direc);
    
    mDieWhenStuck = false;
    mFullLength = 1000;
    setSpeed(3, 4);
    setColour(2);
   
  } // constructor
  
  // snake identity (1, 2 or 3)
  public int snakeType() { return 3; }

  // set the array of how vacant the track way-points are
  private void prepareWaypoints() {
    
    for ( int ix = 1 ; ix <= 4 ; ix++ ) {
      for ( int iy = 1 ; iy <= 4 ; iy++ ) {
        mWaypoints[ix][iy] = 0;
      }
    }
    
    int x = mXPos - 10,
        y = mYPos - 10;
    for ( int k = mBody.size() ; k >= 0 ; k-- ) {
      if ( k < mBody.size() ) {
        final int dir = mBody.get(k);
        x -= Env.STEP_X[dir];
        y -= Env.STEP_Y[dir];
      }
      if ( (x % 3) == 0 && (y % 3) == 0 ) {
        mWaypoints[x/3+1][y/3+1] = 1;
      }
    }

    if ( mPlayer != null ) {
      if ( mPlayer.getXPos() >= 10 && mPlayer.getXPos() < 20 &&
           mPlayer.getYPos() >= 10 && mPlayer.getYPos() < 20 ) {
        final int x0 = (mPlayer.getXPos()-10)/3 + 1,
                  x1 = (mPlayer.getXPos()-8)/3 + 1,
                  y0 = (mPlayer.getYPos()-10)/3 + 1,
                  y1 = (mPlayer.getYPos()-8)/3 + 1;
        if ( mWaypoints[x0][y0] == 0 ) mWaypoints[x0][y0] = 3;
        if ( mWaypoints[x1][y1] == 0 ) mWaypoints[x1][y1] = 3;
      }
    }

    boolean done;
    do {
      done = true;
      for ( int ix = 1 ; ix <= 4 ; ix++ ) {
        for ( int iy = 1 ; iy <= 4 ; iy++ ) {
          if ( mWaypoints[ix][iy] == 3 ) {
            mWaypoints[ix][iy] = 2;
            done = false;
            if ( mWaypoints[ix-1][iy] == 0 ) mWaypoints[ix-1][iy] = 3;
            if ( mWaypoints[ix+1][iy] == 0 ) mWaypoints[ix+1][iy] = 3;
            if ( mWaypoints[ix][iy-1] == 0 ) mWaypoints[ix][iy-1] = 3;
            if ( mWaypoints[ix][iy+1] == 0 ) mWaypoints[ix][iy+1] = 3;
          }
        }
      }
    } while (!done);
    
  } // prepareWaypoints()
  
  // decide which direction to move in next
  @Override
  protected int chooseDirection() {

    // continue to a junction
    if ( ((mXPos-10) % 3) != 0 || ((mYPos-10) % 3) != 0 ) {
      return ( canMove(mDirec) ? mDirec : -1 );
    }
    
    prepareWaypoints();
    
    final int direcRight = Env.fold(mDirec-1, 4),
              direcLeft  = Env.fold(mDirec+1, 4);
    final int x = (mXPos-10)/3 + 1,
              y = (mYPos-10)/3 + 1;
    final int xf = x + Env.STEP_X[mDirec],
              yf = y + Env.STEP_Y[mDirec];
    final int xr = x + Env.STEP_X[direcRight],
              yr = y + Env.STEP_Y[direcRight];
    final int xl = x + Env.STEP_X[direcLeft],
              yl = y + Env.STEP_Y[direcLeft];
    
    final boolean forward   = (mWaypoints[xf][yf] == 2),
                  turnLeft  = (mWaypoints[xl][yl] == 2),
                  turnRight = (mWaypoints[xr][yr] == 2);
    int direc = randomTurn(mDirec, forward, turnLeft, turnRight);

    if ( mTransformed && mPlayer != null ) {
      final int dx = mPlayer.getXPos() - mXPos,
                dy = mPlayer.getYPos() - mYPos;
      final int dir  = (Math.abs(dx) > Math.abs(dy))
                     ? (dx > 0 ? Env.RIGHT : Env.LEFT)
                     : (dy > 0 ? Env.UP    : Env.DOWN);
      if ( (dir == mDirec && forward) ||
           (dir == direcLeft && turnLeft) ||
           (dir == direcRight && turnRight) ) {
        direc = dir;
      }
    }
    
    if ( direc == -1 && mPlayer != null &&
         mPlayer.getXPos() >= 10 && mPlayer.getXPos() < 20 &&
         mPlayer.getYPos() >= 10 && mPlayer.getYPos() < 20 ) {
      assert( mPlayer.getXPos() == mXPos || mPlayer.getYPos() == mYPos );
      if      ( mPlayer.getXPos() > mXPos ) direc = Env.RIGHT;
      else if ( mPlayer.getXPos() < mXPos ) direc = Env.LEFT;
      else if ( mPlayer.getYPos() > mYPos ) direc = Env.UP;
      else if ( mPlayer.getYPos() < mYPos ) direc = Env.DOWN;
    }
    
    return direc;

  } // Snake.chooseDirection()

  // move the snake
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {
  
    // the snake stops moving while its body shrinks
    if ( mFrozen ) {
      if ( mBody.size() > 0 ) {
        mBody.remove(0);
        return;
      } else {
        mFrozen = false;
        if ( mTransformed ) {
          setSpeed(1, 2);
          setColour(5);
          mActionTimer = 12;
          mFlashTimer = mActionTimer;
        }
      }
    }
    
    super.advance(addTheseSprites, killTheseSprites, newStoryEvents);

  } // Snake.advance()

  // register a hit on the snake's head
  @Override
  public void shotInHead() {

    if ( mTransformed ) {
      if ( !mFrozen && !mDying && mPlayer != null &&
           !mPlayer.hits(mXPos, mYPos, mZPos) ) {
        mDying = true;
        mActionTimer = 12;
        mFlashTimer = mActionTimer;
        mFlashColour = 0;
        mStepping = false;
      }
    } else {
      if ( !mDying ) {
        flash(4);
        mFrozen = true;
      }
    }
    
  } // Snake.shotInHead()

  // change the snake into its final form
  public void transform() {

    assert( !mTransformed && !mDying );
    mTransformed = true;
    mFrozen = true;
    setColour(4);
    
  } // transform()

} // class SnakeBoss3
