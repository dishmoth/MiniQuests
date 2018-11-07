/*
 *  SnakeC.java
 *  Copyright (c) 2018 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

// a specific snake
public class SnakeC extends Snake {
  
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
  
  // true if the snake is headless and un-moving
  private boolean mDead;
  
  // position where the snake has been hit (or -1)
  private int mHitXPos,
              mHitYPos;

  // temporary workspace: 0 => empty, 1 => wall, 2 => dead snake, 3 => visited
  private int mWaypoints[][] = { { 1, 1, 1, 1, 1, 1, 1, 1, 1 },
                                 { 1, 0, 0, 0, 0, 0, 0, 0, 1 },
                                 { 1, 0, 1, 0, 1, 0, 1, 0, 1 },
                                 { 1, 0, 0, 0, 0, 0, 0, 0, 1 },
                                 { 1, 0, 1, 0, 1, 0, 1, 0, 1 },
                                 { 1, 0, 0, 0, 0, 0, 0, 0, 1 },
                                 { 1, 0, 1, 0, 1, 0, 1, 0, 1 },
                                 { 1, 0, 0, 0, 0, 0, 0, 0, 1 },
                                 { 1, 1, 1, 1, 1, 1, 1, 1, 1 } };
  
  // constructor (alive)
  public SnakeC(int x, int y, int z, int direc) {

    super(x, y, z, direc, kTrack);

    mDead = false;
    mDieWhenStuck = false;
    setLength(6);
    setSpeed(5);
    setColour(2);
    
    mHitXPos = mHitYPos = -1;
   
  } // constructor
  
  // constructor (dead)
  public SnakeC(int x, int y, int z, ArrayList<Integer> body) {
    
    super(x, y, z, body.get(body.size()-1), kTrack);
    
    mBody = body;
    
    mDead = true;
    setColour(2);

    mHitXPos = mHitYPos = -1;

  } // constructor
  
  // map x or y position to its index in the waypoints array  
  private int waypointIndex(int x) { 
    
    if ( x < 0 ) return 0;
    if ( x > 9 ) return 8;
    return (1 + 2*(x/3) + (x%3>0 ? 1 : 0));
    
  } // waypointIndex()
  
  // set the array of how vacant the track way-points are
  private void prepareWaypoints() {

    // clear the waypoints
    assert( !mDead );
    for ( int ix = 0 ; ix < mWaypoints.length ; ix++ ) {
      for ( int iy = 0 ; iy < mWaypoints[ix].length ; iy++ ) {
        if ( mWaypoints[ix][iy] != 1 ) mWaypoints[ix][iy] = 0;
      }
    }
    
    // mark the dead snakes
    for ( Obstacle ob : mObstacles ) {
      if ( !(ob instanceof SnakeC) ) continue;
      SnakeC s = (SnakeC)ob;
      assert( s.mDead );
      int x = s.mXPos,
          y = s.mYPos;
      for ( int k = s.mBody.size()-1 ; k >= 0 ; k-- ) {
        final int direc = s.mBody.get(k);
        x -= Env.STEP_X[direc];
        y -= Env.STEP_Y[direc];
        final int ix = waypointIndex(x),
                  iy = waypointIndex(y);
        assert( mWaypoints[ix][iy] != 1 );
        mWaypoints[ix][iy] = 2;
      }
    }
    
  } // prepareWaypoints()
  
  // check whether the snake can loop back on itself for a particular direction
  private int longestPath(int ix, int iy, int direc) {
    
    assert( ix % 2 == 1 && iy % 2 == 1 );

    // special case: path to the gate
    if ( ix == 5 && iy == 7 && direc == Env.UP ) return 2;
    
    final int infiniteLoop = 999;
    for ( int steps = 0 ; steps < 2 ; steps ++ ) {
      ix += Env.STEP_X[direc];
      iy += Env.STEP_Y[direc];

      if ( mWaypoints[ix][iy] == 1 || mWaypoints[ix][iy] == 2 ) return steps;
      if ( mWaypoints[ix][iy] == 3 ) return infiniteLoop;
    
      mWaypoints[ix][iy] = 3;      
    }

    final int lenForward = 2 + longestPath(ix, iy, direc),
              lenRight   = 2 + longestPath(ix, iy, Env.fold(direc-1, 4)),
              lenLeft    = 2 + longestPath(ix, iy, Env.fold(direc+1, 4));
    final int len        = Math.max(lenForward, Math.max(lenRight, lenLeft));
    return Math.min(len, infiniteLoop);
    
  } // findLoop()
  
  // decide which direction to move in next
  @Override
  protected int chooseDirection() {

    // special case: path to the gate
    if ( mYPos > 9 ) {
      if ( mYPos >= 14 ) return -1;
      return mDirec;
    }

    // continue to a junction
    if ( (mXPos % 3) != 0 || (mYPos % 3) != 0 ) {
      return ( canMove(mDirec) ? mDirec : -1 );
    }

    prepareWaypoints();
    
    final int ix = waypointIndex(mXPos),
              iy = waypointIndex(mYPos);
    mWaypoints[ix][iy] = 3;
    
    final int direcRight = Env.fold(mDirec-1, 4),
              direcLeft  = Env.fold(mDirec+1, 4);
    final int forward    = longestPath(ix, iy, mDirec),
              turnLeft   = longestPath(ix, iy, direcLeft),
              turnRight  = longestPath(ix, iy, direcRight);
    
    int max = Math.max(Math.max(forward, turnLeft), turnRight);
    if ( max > 0 ) {
      return randomTurn(mDirec, (forward==max),
                                (turnLeft==max),
                                (turnRight==max));
    } else {
      return randomTurn(mDirec, canMove(mDirec),
                                canMove(direcLeft),
                                canMove(direcRight));
    }    

  } // Snake.chooseDirection()

  // move the snake
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {

    if ( mDead ) return;
    
    if ( mYPos > 9 ) mDieWhenStuck = true;
    
    // zap an obstacle
    if ( !mDying && mStuck && mActionTimer == 1 ) {
      zapTail(mXPos, mYPos, mDirec);
      mStuck = false;
      mActionTimer = kDeathTime1;
    }
    
    super.advance(addTheseSprites, killTheseSprites, newStoryEvents);

    // get ready to zap an obstacle
    if ( !mDying && mStuck ) {
      mStepping = false;
      if ( mFlashTimer == 0 ) {
        mFlashTimer = kDeathTime1;
        mFlashColour = 4;
      }
    }
    
    // get ready for the dying zap
    if ( mDying && mFlashColour == 0 ) {
      mFlashColour = 4;
      mFlashTimer *= 2;
    }
    
  } // Snake.advance()

  // destroy the snake tail at a target position 
  private void zapTail(int x0, int y0, int direc) {
    
    assert( x0 >= 0 && x0 < 10 && y0 >= 0 && y0 < 10 );
    
    int x = x0 + Env.STEP_X[direc],
        y = y0 + Env.STEP_Y[direc];
    if ( x < 0 || x > 9 || y < 0 || y > 9 ) {
      final int flip = ( Env.randomBoolean() ? +1 : -1 );
      for ( int d = -1 ; d <= +1 ; d += 2 ) {
        int dir = Env.fold(direc + flip*d, 4);
        x = x0 + Env.STEP_X[dir];
        y = y0 + Env.STEP_Y[dir];
        if ( x >= 0 && x < 10 && y >= 0 && y < 10 ) break;
      }
      assert( x >= 0 && x < 10 && y >= 0 && y < 10 );
    }
    
    for ( Obstacle ob : mObstacles ) {
      if ( !(ob instanceof SnakeC) ) continue;
      SnakeC s = (SnakeC)ob;
      assert( s.mDead );
      if ( s.hitsBody(x, y, mZPos)) {
        s.shotInBody(x, y);
        return;
      }
    }
    assert( false );
    
  } // zapTail()
  
  // whether the snake's head intersects a particular position
  public boolean hitsHead(int x, int y, int z) {
  
    if ( mDead ) return false;
    else         return super.hitsHead(x, y, z);
    
  } // Snake.hitsHead()
  
  //
  public void shotInBody(int x, int y) {
    
    if ( !mDying ) {
      mHitXPos = x;
      mHitYPos = y;
    }

  } // Snake.shotInBody()

  // break when hit
  @Override
  public void aftermath(LinkedList<Sprite>     addTheseSprites, 
                        LinkedList<Sprite>     killTheseSprites,
                        LinkedList<StoryEvent> newStoryEvents) { 
    
    if ( mHitXPos >= 0 && mHitYPos >= 0 ) {
      int index;
      int xBody = mXPos,
          yBody = mYPos;
      for ( index = mBody.size()-1 ; index >= 0 ; index-- ) {
        final int direc = mBody.get(index);
        xBody -= Env.STEP_X[direc];
        yBody -= Env.STEP_Y[direc];
        if ( mHitXPos == xBody && mHitYPos == yBody ) break;
      }
      assert( index >= 0 );
    
      final byte colour = EgaTools.decodePixel(kColourSchemes[mColour][0]);
      addTheseSprites.add(new Splatter(mHitXPos, mHitYPos, mZPos,
                                       -1, 2, colour, -1));
    
      final boolean tailStep = tailStepping();
      ArrayList<Integer> tailBody = new ArrayList<Integer>();
      for ( int k = 0 ; k <= index ; k++ ) {
        int piece = mBody.remove(0);
        if ( k < index && (k > 0 || !tailStep)) {
          tailBody.add(piece);
        }
      }
      if ( mDead && mBody.isEmpty() ) {
        killTheseSprites.add(this);
      }
      if ( tailBody.size() > 0 ) {
        addTheseSprites.add(new SnakeC(mHitXPos, mHitYPos, mZPos, tailBody));
      }
    }
    mHitXPos = mHitYPos = -1;
    
  } // Sprite.aftermath()
  
  // special case to display the dead (headless) snake
  @Override
  public void draw(EgaCanvas canvas) {

    if ( !mDead ) {
      super.draw(canvas);
      return;
    }
    
    SnakeImage image = kSnakeImages[mColour];

    int x = mXPos - mCamera.xPos(),
        y = mYPos - mCamera.yPos(),
        z = mZPos - mCamera.zPos();
    
    for ( int k = mBody.size()-1 ; k >= 0 ; k-- ) {
      final int direc = mBody.get(k);
      final int direcPrev = (k > 0) ? mBody.get(k-1) : Env.NONE;
      x -= Env.STEP_X[direc];
      y -= Env.STEP_Y[direc];
      image.drawBody(canvas, x, y, z, direc, direcPrev, false);
    }
    
  } // Sprite.draw()

} // class SnakeC
