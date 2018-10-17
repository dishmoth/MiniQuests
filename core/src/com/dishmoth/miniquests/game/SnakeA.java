/*
 *  SnakeA.java
 *  Copyright (c) 2018 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.Arrays;
import java.util.LinkedList;

// a specific snake
public class SnakeA extends Snake {
  
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
  
  // temporary workspace: 0 for head, increasing for body, 999 for empty
  private int mWaypoints[][] = { { 999, 999, 999, 999 },
                                 { 999, 999, 999, 999 },
                                 { 999, 999, 999, 999 },
                                 { 999, 999, 999, 999 } };

  // constructor
  public SnakeA(int x, int y, int z, int direc) {

    super(x, y, z, direc, kTrack);
    
    setColour(1);
   
  } // constructor
  
  // set the array of how vacant the track way-points are
  private void prepareWaypoints() {
    
    for ( int k = 0 ; k < mWaypoints.length ; k++ ) {
      Arrays.fill(mWaypoints[k], 999);
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
    if ( (mXPos % 3) != 0 || (mYPos % 3) != 0 ) {
      return ( canMove(mDirec) ? mDirec : -1 );
    }
    
    prepareWaypoints();
    
    final int x = mXPos / 3,
              y = mYPos / 3;
    final int direcRight = Env.fold(mDirec-1, 4),
              direcLeft  = Env.fold(mDirec+1, 4);
    final int depth = 2;
    final int scoreForward = rateDirection(x, y, mDirec, depth),
              scoreRight   = rateDirection(x, y, direcRight, depth),
              scoreLeft    = rateDirection(x, y, direcLeft, depth);

    final int score = Math.max(scoreForward, Math.max(scoreRight, scoreLeft));
    assert( score >= 0 );
    
    final boolean forward   = (scoreForward == score),
                  turnRight = (scoreRight == score),
                  turnLeft  = (scoreLeft == score);

    if ( forward ) {
      if ( !turnRight && !turnLeft ) return mDirec;
      if ( Env.randomBoolean() ) return mDirec;
    }
    if ( turnRight && turnLeft ) {
      return ( Env.randomBoolean() ? direcLeft : direcRight );
    }
    if ( turnLeft && !turnRight ) return direcLeft;
    if ( turnRight && !turnLeft ) return direcRight;
    return -1;

  } // Snake.chooseDirection()

  // move the snake
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {
  
    if ( mPlayer == null ) {
      setLength(2);
      mDieWhenStuck = false;
    } else {
      mDieWhenStuck = true;
    }
    
    super.advance(addTheseSprites, killTheseSprites, newStoryEvents);

  } // Snake.advance()

  //
  public void shotInBody() { shotInHead(); }
  public void shotInHead() {
    if (!mDying) {
      grow(1); //2);
      flash(0);
    }
  } // Snake.shotInHead()

} // class SnakeA
