/*
 *  Bullet.java
 *  Copyright Simon Hern 2010
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// a projectile fired by the player
public class Bullet extends Sprite3D {

  // movement rate (ticks per step)
  private static final int kStepDelay = 2;
  
  // automatically destroy the bullet after it has travelled far enough
  private static final int kMaxRange = 3*Room.kSize;
  
  // image details for the bullet
  private static final float kDepthOffset = -0.001f;
  //private static final EgaImage kImage = new EgaImage(0, 0, 2, 1, 
  //                                                    "99", -0.001f);
  
  // bullet's colour
  private final byte mColour;

  // the sprite that fired the bullet
  private final Sprite mSource;
  
  // current position
  private int mXPos,
              mYPos,
              mZPos;
  
  // direction (see enumeration in Env)
  private int mDirec;
  
  // time until next movement
  private int mStepTimer;

  // true when the bullet has hit an enemy
  private boolean mHitTarget;

  // how far the bullet has travelled
  private int mRange;
  
  // list of objects to navigate around (excluding enemies)
  private LinkedList<Obstacle> mObstacles = new LinkedList<Obstacle>();
  
  // constructor
  public Bullet(int xPos, int yPos, int zPos, 
                int direc, byte colour, Sprite source) {
  
    mXPos = xPos;
    mYPos = yPos;
    mZPos = zPos;
    
    assert( direc >= 0 && direc < 4 );
    mDirec = direc;

    mColour = colour;
    mSource = source;
    
    mStepTimer = kStepDelay;
    
    mHitTarget = false;

    mRange = 0;
    
  } // constructor
  
  // accessors
  public int getXPos() { return mXPos; }
  public int getYPos() { return mYPos; }
  public int getZPos() { return mZPos; }
  public int getDirec() { return mDirec; }
  
  // modify position
  public void shiftPos(int dx, int dy, int dz) { 
    
    mXPos += dx; 
    mYPos += dy; 
    mZPos += dz;
    
  } // shiftPos();
  
  // check for Sprites we want to keep track of
  @Override
  public void observeArrival(Sprite newSprite) { 

    super.observeArrival(newSprite);
    
    if ( newSprite == mSource ) return;
    
    if ( newSprite instanceof Critter ||
         newSprite instanceof Dragon ||
         newSprite instanceof FloorBossHead ||
         newSprite instanceof Player ||
         newSprite instanceof Spinner ||
         newSprite instanceof Triffid ||
         newSprite instanceof TriffidBoss ||
         newSprite instanceof WallSwitch ) {
      mSpritesToWatch.add(newSprite);
    }
    
    else if ( newSprite instanceof Obstacle ) {
      mObstacles.add((Obstacle)newSprite);
    }

  } // Sprite.observeArrival()
  
  // keep track of Sprites that have been destroyed
  @Override
  public void observeDeparture(Sprite deadSprite) {

    super.observeDeparture(deadSprite); // remove from mSpritesToWatch
    
    if ( deadSprite instanceof Obstacle ) {
      mObstacles.remove(deadSprite);
    }

  } // Sprite.observeDeparture()
  
  // move the bullet
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {
    
    if ( --mStepTimer == 0 ) {
      mStepTimer = kStepDelay;
      switch ( mDirec ) {
        case Env.RIGHT: { mXPos += 1; } break;
        case Env.UP:    { mYPos += 1; } break;
        case Env.LEFT:  { mXPos -= 1; } break;
        case Env.DOWN:  { mYPos -= 1; } break;
        default: assert(false);
      }
      mRange += 1;
    }

    if ( outsideGameArea() || mRange > kMaxRange ) {
      killTheseSprites.add(this);
      return;
    }
    
    Obstacle collider = checkObstacles();
    if ( collider != null ) {
      killTheseSprites.add(this);
      byte colour = mColour;
      final boolean isWall = ( collider instanceof Wall );
      if ( isWall ) {
        WallSwitch ws = checkSwitches();
        if ( ws != null ) {
          assert( ws.isActive() );
          colour = ws.colour();
          ws.hit();
        }
      }
      addTheseSprites.add(new Shrapnel(mXPos, mYPos, mZPos, mDirec, 
                                       isWall, colour));
      Env.sounds().play(Sounds.ARROW_HIT);
    }
    
  } // Sprite.advance()

  // see whether the bullet has collided with any obstacles
  private Obstacle checkObstacles() {
    
    for ( Obstacle ob : mObstacles ) {
      if ( !ob.isEmpty(mXPos, mYPos, mZPos+1) ) return ob;
    }
    return null;
    
  } // checkObstacles()

  // see whether the bullet has left the game area
  private boolean outsideGameArea() {
    
    for ( Obstacle ob : mObstacles ) {
      if ( ob.isVoid(mXPos, mYPos, mZPos+1) ) return true;
    }
    return false;
    
  } // outsideGameArea()

  // check whether the bullet has hit a switch on the wall
  private WallSwitch checkSwitches() {
  
    for ( Sprite sp : mSpritesToWatch ) {
      if ( sp instanceof WallSwitch ) {
        WallSwitch ws = (WallSwitch)sp;
        if ( ws.isActive() &&
             mXPos == ws.getXPos() && 
             mYPos == ws.getYPos() && 
             mZPos == ws.getZPos() ) return ws;
      }
    }
    return null;

  } // checkSwitches()
  
  // check for collisions
  @Override
  public void interact() { 
    
    for ( Sprite sp : mSpritesToWatch ) {
      
      if ( sp instanceof Critter ) {
        Critter target = (Critter)sp;
        if ( target.hits(mXPos, mYPos, mZPos) ) {
          mHitTarget = true;
          target.stun(mDirec);
        }
      }
      
      else if ( sp instanceof Dragon ) {
        Dragon target = (Dragon)sp;
        if ( target.hits(mXPos, mYPos, mZPos) ) {
          mHitTarget = true;
          target.stun();
        }
      }
      
      else if ( sp instanceof FloorBossHead ) {
        FloorBossHead target = (FloorBossHead)sp;
        if ( target.hits(mXPos, mYPos, mZPos) ) {
          mHitTarget = true;
          target.stun();
        }
      }
      
      else if ( sp instanceof Player ) {
        Player target = (Player)sp;
        if ( target.hits(mXPos, mYPos, mZPos) ) {
          mHitTarget = true;
          target.destroy(mDirec);
        }
      }
      
      else if ( sp instanceof Spinner ) {
        Spinner target = (Spinner)sp;
        if ( target.hits(mXPos, mYPos, mZPos) ) {
          mHitTarget = true;
          Env.sounds().play(Sounds.ARROW_HIT);
        }
      }
      
      else if ( sp instanceof Triffid ) {
        Triffid target = (Triffid)sp;
        if ( target.hits(mXPos, mYPos, mZPos) ) {
          mHitTarget = true;
          boolean lethal = ( mSource instanceof Triffid );
          target.stun(mDirec, lethal);
        }
      }
      
      else if ( sp instanceof TriffidBoss ) {
        TriffidBoss target = (TriffidBoss)sp;
        if ( target.hits(mXPos, mYPos, mZPos) ) {
          mHitTarget = true;
          boolean lethal = ( mSource instanceof Triffid );
          target.stun(mDirec, lethal);
        }
      }
      
    }
    
  } // Sprite.interact()

  // handle consequences of collisions
  @Override
  public void aftermath(LinkedList<Sprite>     addTheseSprites, 
                        LinkedList<Sprite>     killTheseSprites,
                        LinkedList<StoryEvent> newStoryEvents) { 
    
    if ( mHitTarget ) {
      killTheseSprites.add(this);
      addTheseSprites.add(new Shrapnel(mXPos, mYPos, mZPos, mDirec, 
                                       true, mColour));
    }
    
  } // Sprite.aftermath()
  
  // display the bullet
  @Override
  public void draw(EgaCanvas canvas) {

    final int x = mXPos - mCamera.xPos(),
              y = mYPos - mCamera.yPos(),
              z = mZPos - mCamera.zPos();

    //kImage.draw3D(canvas, 2*x, 2*y, z);
    
    final int depth = x + y;
    final int xx = Env.originXPixel() + 2*x - 2*y,
              yy = Env.originYPixel() - depth - z;  
    canvas.fill(xx, xx+1, yy, yy, depth+kDepthOffset, mColour);

  } // Sprite.draw()

} // class Bullet
