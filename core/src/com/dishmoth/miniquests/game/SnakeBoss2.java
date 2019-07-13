/*
 *  SnakeBoss2.java
 *  Copyright (c) 2018 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.ArrayList;
import java.util.LinkedList;

// the second snake boss
public class SnakeBoss2 extends Snake {
  
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
  
  // true if the snake is a headless and un-moving body segment
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
  public SnakeBoss2(int x, int y, int z, int direc) {

    super(x, y, z, direc, kTrack);

    mDead = false;
    mDieWhenStuck = false;
    setLength(6);
    setSpeed(5);
    setColour(3);
    
    mHitXPos = mHitYPos = -1;
   
  } // constructor
  
  // constructor (dead)
  public SnakeBoss2(int x, int y, int z, ArrayList<Integer> body) {
    
    super(x, y, z, body.get(body.size()-1), kTrack);
    
    mBody = body;
    
    mDead = true;
    setColour(3);

    mHitXPos = mHitYPos = -1;

  } // constructor
  
  // snake identity (1, 2 or 3)
  public int snakeType() { return 2; }

  // map x or y position to its index in the waypoints array  
  private int waypointIndex(int x) { 
    
    x -= 10;
    if ( x < 0 ) return 0;
    if ( x > 9 ) return 8;
    return (1 + 2*(x/3) + (x%3>0 ? 1 : 0));
    
  } // waypointIndex()
  
  // set the array of how vacant the track way-points are
  private void prepareWaypoints() {

    assert( !mDead );

    // clear the waypoints
    for ( int ix = 0 ; ix < mWaypoints.length ; ix++ ) {
      for ( int iy = 0 ; iy < mWaypoints[ix].length ; iy++ ) {
        if ( mWaypoints[ix][iy] != 1 ) mWaypoints[ix][iy] = 0;
      }
    }
    
    // mark the dead snakes
    for ( Obstacle ob : mObstacles ) {
      if ( !(ob instanceof SnakeBoss2) ) continue;
      SnakeBoss2 s = (SnakeBoss2)ob;
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
    if ( ix == 5 && iy == 7 && direc == Env.UP &&
         mPlayer != null &&
         mPlayer.getXPos() >= 10 && mPlayer.getXPos() < 20 && 
         mPlayer.getYPos() >= 10 && mPlayer.getYPos() < 20 ) {
      return 2;
    }
    
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
    
  } // longestPath()
  
  // decide which direction to move in next
  @Override
  protected int chooseDirection() {

    // special case: path to the gate
    if ( mYPos > 19 ) {
      if ( mYPos >= 24 ) return -1;
      return mDirec;
    }

    // continue to a junction
    if ( ((mXPos-10) % 3) != 0 || ((mYPos-10) % 3) != 0 ) {
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

    if ( mDead ) {
      if ( mDying ) destroyDeadBody();
      else          checkHead();
      return;
    }
    
    // special case: approaching the gate
    if ( mYPos > 19 ) mDieWhenStuck = true;
        
    // zap a nearby body part when stuck
    if ( mStuck && !mDying && (mActionTimer == 1 || mHibernating) ) {
      if ( zapTail(mXPos, mYPos, mDirec) ) {
        mStuck = false;
        mActionTimer = kDeathTime1;
      } else {
        assert(mHibernating);
      }
    }
    
    super.advance(addTheseSprites, killTheseSprites, newStoryEvents);

    // get ready to zap an obstacle
    if ( mStuck && !mDying ) {
      mStepping = false;
      if ( mFlashTimer == 0 && !mHibernating ) {
        mFlashTimer = kDeathTime1;
        mFlashColour = 4;
      }
    }
    
    // get ready for the dying zap
    if ( mDying && mFlashColour == 0 ) {
      mFlashColour = 4;
      mFlashTimer *= 4;
    }
    
    // break open the gate before dying
    if ( mDying && mActionTimer == 0 ) {
      final int x = mXPos,
                y = mYPos,
                z = mZPos + 1;
      for ( Obstacle ob : mObstacles ) {
        if ( ob instanceof FenceGate && !ob.isEmpty(x, y, z) ) {
          FenceGate gate = (FenceGate)ob;
          if ( gate.isClosed() ) {
            gate.setClosed(false);
            Env.sounds().play(Sounds.GATE);
          }
        }
      }
    }
    
  } // Snake.advance()

  // destroy a bit of snake tail neighbouring the target position 
  private boolean zapTail(int x0, int y0, int direc) {
    
    assert( !mDead );
    assert( x0 >= 10 && x0 < 20 && y0 >= 10 && y0 < 20 );
    
    int x = x0 + Env.STEP_X[direc],
        y = y0 + Env.STEP_Y[direc];
    if ( x < 10 || x >= 20 || y < 10 || y >= 20 ) {
      final int flip = ( Env.randomBoolean() ? +1 : -1 );
      for ( int d = -1 ; d <= +1 ; d += 2 ) {
        int dir = Env.fold(direc + flip*d, 4);
        x = x0 + Env.STEP_X[dir];
        y = y0 + Env.STEP_Y[dir];
        if ( x >= 10 && x < 20 && y >= 10 && y < 20 ) break;
      }
      assert( x >= 10 && x < 20 && y >= 10 && y < 20 );
    }
    
    for ( Obstacle ob : mObstacles ) {
      if ( !(ob instanceof SnakeBoss2) ) continue;
      SnakeBoss2 s = (SnakeBoss2)ob;
      assert( s.mDead );
      if ( s.hitsBody(x, y, mZPos)) {
        s.shotInBody(x, y);
        return true;
      }
    }
    return false;
    
  } // zapTail()
  
  // whether the snake's head intersects a particular position
  public boolean hitsHead(int x, int y, int z) {
  
    if ( mDead ) return false;
    else         return super.hitsHead(x, y, z);
    
  } // Snake.hitsHead()
  
  // destroy a body segment
  public void shotInBody(int x, int y) {
    
    if ( mDead || !mDying ) {
      mHitXPos = x;
      mHitYPos = y;
    }

  } // Snake.shotInBody()

  // if the head is dead then the body parts die
  private void checkHead() {
    
    assert( mDead && !mDying );
    for ( Obstacle ob : mObstacles ) {
      if ( !(ob instanceof SnakeBoss2) ) continue;
      SnakeBoss2 s = (SnakeBoss2)ob;
      if ( !s.mDead ) return;
    }
    mDying = true;
    mActionTimer = Env.randomInt(10, 20);
    
  } // checkHead()
  
  // destroy random body sections
  private void destroyDeadBody() {

    assert( mDead && mDying );
    if ( mActionTimer > 0 ) {
      mActionTimer--;
    } else {
      final int seg = Env.randomInt(mBody.size());
      int xBody = mXPos,
          yBody = mYPos;
      for ( int index = mBody.size()-1 ; index >= seg ; index-- ) {
        final int direc = mBody.get(index);
        xBody -= Env.STEP_X[direc];
        yBody -= Env.STEP_Y[direc];
      }
      shotInBody(xBody, yBody);
      mActionTimer = Env.randomInt(1, 5);
    }
    
  } // destroyDeadBody()
  
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
        addTheseSprites.add(
                  new SnakeBoss2(mHitXPos, mHitYPos, mZPos, tailBody));
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

} // class SnakeBoss2
