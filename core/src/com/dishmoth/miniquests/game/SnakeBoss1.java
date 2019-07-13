/*
 *  SnakeBoss1.java
 *  Copyright (c) 2018 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.Arrays;

// the first snake boss
public class SnakeBoss1 extends Snake {
  
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
                                                              "##########"},
                                                 10, 10);
  
  // temporary workspace: 0 for head, increasing for body, 999 for empty
  private int mWaypoints[][] = { { 999, 999, 999, 999 },
                                 { 999, 999, 999, 999 },
                                 { 999, 999, 999, 999 },
                                 { 999, 999, 999, 999 } };

  // constructor
  public SnakeBoss1(int x, int y, int z, int direc) {

    super(x, y, z, direc, kTrack);
    
    mDieWhenStuck = true;
    setSpeed(4);
    setColour(1);
   
  } // constructor
  
  // snake identity (1, 2 or 3)
  public int snakeType() { return 1; }

  // set the array of how vacant the track way-points are
  private void prepareWaypoints() {
    
    for ( int k = 0 ; k < mWaypoints.length ; k++ ) {
      Arrays.fill(mWaypoints[k], 999);
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
        mWaypoints[x/3][y/3] = (mBody.size() - k);
      }
    }

  } // prepareWaypoints()
  
  //??
  private int rateDirection(int x, int y, int direc, int depth) {

    x += Env.STEP_X[direc];
    y += Env.STEP_Y[direc];
    
    if ( x < 0 || x > 3 || y < 0 || y > 3 ) return -999;
    
    if ( mWaypoints[x][y] < 999 ) return mWaypoints[x][y];
    
    if ( depth == 0 ) return 999;

    final int direcRight = Env.fold(direc-1, 4),
              direcLeft  = Env.fold(direc+1, 4);
    final int scoreForward = rateDirection(x, y, direc, depth-1),
              scoreRight   = rateDirection(x, y, direcRight, depth-1),
              scoreLeft    = rateDirection(x, y, direcLeft, depth-1);
    
    int score = Math.max(scoreForward, Math.max(scoreRight, scoreLeft));
    if ( score < 999 && score > -999 ) score += 3;

    return score;
    
  } // rateDirection()
  
  // decide which direction to move in next
  protected int chooseDirection() {

    // continue to a junction
    if ( ((mXPos-10) % 3) != 0 || ((mYPos-10) % 3) != 0 ) {
      return ( canMove(mDirec) ? mDirec : -1 );
    }
    
    prepareWaypoints();
    
    final int x = (mXPos-10) / 3,
              y = (mYPos-10) / 3;
    final int direcRight = Env.fold(mDirec-1, 4),
              direcLeft  = Env.fold(mDirec+1, 4);
    final int depth = 2;
    final int scoreForward = rateDirection(x, y, mDirec, depth),
              scoreRight   = rateDirection(x, y, direcRight, depth),
              scoreLeft    = rateDirection(x, y, direcLeft, depth);

    final int score = Math.max(scoreForward, Math.max(scoreRight, scoreLeft));
    assert( score >= 0 );
    
    return randomTurn(mDirec,
                      (scoreForward == score),
                      (scoreLeft == score),
                      (scoreRight == score));

  } // Snake.chooseDirection()

  //
  public void shotInBody(int x, int y) { shotInHead(); }
  public void shotInHead() {
    if (!mDying && !mHibernating) {
      grow(1);
      flash(0);
    }
  } // Snake.shotInHead()

} // class SnakeBoss1
