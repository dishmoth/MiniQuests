/*
 *  SnakeB.java
 *  Copyright (c) 2018 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// a specific snake
public class SnakeB extends Snake {
  
  // the snake is specialized to a particular path
  private static final Track kTrack = new CritterTrack
                                                (new String[]{"##########",
                                                              "#  #  #  #",
                                                              "#  #  #  #",
                                                              "##########",
                                                              "#  #  #  #",
                                                              "#  #  #  #",
                                                              "##########",
                                                              "#  #  #  #",
                                                              "#  #  #  #",
                                                              "##########"});
  
  // temporary workspace: 0 for empty, 1 for snake/wall, 2 for player reachable
  private int mWaypoints[][] = { { 1, 1, 1, 1, 1, 1 },
                                 { 1, 0, 0, 0, 0, 1 },
                                 { 1, 0, 0, 0, 0, 1 },
                                 { 1, 0, 0, 0, 0, 1 },
                                 { 1, 0, 0, 0, 0, 1 },
                                 { 1, 1, 1, 1, 1, 1 } };

  //
  private boolean mFrozen = false;
  
  // constructor
  public SnakeB(int x, int y, int z, int direc) {

    super(x, y, z, direc, kTrack);
    
    mDieWhenStuck = false;
    mFullLength = 1000;
    setSpeed(3, 4);
    setColour(3);
   
  } // constructor
  
  // set the array of how vacant the track way-points are
  private void prepareWaypoints() {
    
    for ( int ix = 1 ; ix <= 4 ; ix++ ) {
      for ( int iy = 1 ; iy <= 4 ; iy++ ) {
        mWaypoints[ix][iy] = 0;
      }
    }
    
    int x = mXPos,
        y = mYPos;
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
      final int x0 = mPlayer.getXPos()/3 + 1,
                x1 = (mPlayer.getXPos()+2)/3 + 1,
                y0 = mPlayer.getYPos()/3 + 1,
                y1 = (mPlayer.getYPos()+2)/3 + 1;
      if ( mWaypoints[x0][y0] == 0 ) mWaypoints[x0][y0] = 3;
      if ( mWaypoints[x1][y1] == 0 ) mWaypoints[x1][y1] = 3;
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
  protected int chooseDirection() {

    // continue to a junction
    if ( (mXPos % 3) != 0 || (mYPos % 3) != 0 ) {
      return ( canMove(mDirec) ? mDirec : -1 );
    }
    
    prepareWaypoints();
    
    final int direcRight = Env.fold(mDirec-1, 4),
              direcLeft  = Env.fold(mDirec+1, 4);
    final int x = mXPos/3 + 1,
              y = mYPos/3 + 1;
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
    
    if ( direc == -1 && mPlayer != null ) {
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
  
    //
    if ( mFrozen ) {
      if ( mBody.size() > 0 ) {
        mBody.remove(0);
        return;
      } else {
        mFrozen = false;
      }
    }
    
    super.advance(addTheseSprites, killTheseSprites, newStoryEvents);

  } // Snake.advance()

  //
  public void shotInHead() {

    if (!mDying) {
      flash(4);
      mFrozen = true;
    }
    
  } // Snake.shotInHead()

  //
  public void kill() {

    if ( !mDying ) {
      mDying = true;
      mActionTimer = 12;
      mFlashTimer = mActionTimer;
      mFlashColour = 0;
      mStepping = false;
    }

  } // kill()
  
} // class SnakeB
