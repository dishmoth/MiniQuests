/*
 *  ScrewTower.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// odd bit of interactive scenery used in RoomC05
public class ScrewTower extends BlockArray {

  // blocks making up the tower
  private static final String kLayer[]    = { "000", "0 0", "000" },
                              kBlocks[][] = { kLayer, kLayer, kLayer, kLayer,
                                              kLayer, kLayer, kLayer, kLayer,
                                              kLayer, kLayer };
  
  // size of the tower
  private static final int kBlockHeight = 2*(kBlocks.length-1),
                           kBlockWidth = kLayer.length;
  
  // times controlling rate of tower dropping
  private static final int kDropStartDelay = 55,
                           kDropNextDelay = 5;
  
  // how many anti-clockwise steps are needed to raise the tower
  private static final int kRaiseSteps = 3;
  
  // how high the tower is raised relative to its z-range
  private int mHeight,
              mMaxHeight;

  // reference to the sprite that's raising the tower (Player, Critter or null)
  private Sprite mDriver;
  
  // last position of driver relative to tower (or -1)
  private int mDriverX,
              mDriverY;
  
  // how many anti-clockwise steps the driver has taken
  private int mDriverSteps;
  
  // time until tower drops a notch
  private int mDropTimer;
  
  // constructor
  public ScrewTower(int x, int y, int zMin, int zMax, 
                    boolean atTop, String colours) {
    
    super(kBlocks, new String[]{colours}, 
          x, y, (atTop?zMax:zMin)-kBlockHeight);

    mMaxHeight = zMax - zMin;
    mHeight = (atTop ? mMaxHeight : 0);

    mDriver = null;
    mDriverX = mDriverY = -1;
    mDriverSteps = -1;
    
    mDropTimer = kDropStartDelay;
    
  } // constructor
  
  // check for Sprites we want to keep track of
  @Override
  public void observeArrival(Sprite newSprite) { 

    super.observeArrival(newSprite);
    
    if ( newSprite instanceof Critter ||
         newSprite instanceof Player ) {
      mSpritesToWatch.add(newSprite);
    }
    
  } // Sprite.observeArrival()
  
  // keep track of Sprites that have been destroyed
  @Override
  public void observeDeparture(Sprite deadSprite) {

    if ( deadSprite instanceof Critter ||
         deadSprite instanceof Player ) {
      mSpritesToWatch.remove(deadSprite);
      if ( mDriver == deadSprite ) mDriver = null;
    }
    
    super.observeDeparture(deadSprite);
    
  } // Sprite.observeDeparture()

  // raise or lower the tower
  public void shiftHeight(int delta) {
    
    assert( delta == +1 || delta == -1 );
    mHeight += delta;
    assert( mHeight >= 0 && mHeight <= mMaxHeight );
    shiftPos(0, 0, delta);
    
  } // shiftHeight()
  
  // check which sprite is currently raising the tower
  private void findDriver() {
    
    assert( mDriver == null );
    for ( Sprite sprite : mSpritesToWatch ) {
      int x = -1, y = -1;
      if ( sprite instanceof Player ) {
        x = ((Player)sprite).getXPos() - getXPos();
        y = ((Player)sprite).getYPos() - getYPos();
      } else if ( sprite instanceof Critter ) {
        x = ((Critter)sprite).getXPos() - getXPos();
        y = ((Critter)sprite).getYPos() - getYPos();
      }
      if ( x >= 0 && x < kBlockWidth && y >= 0 && y < kBlockWidth ) {
        mDriver = sprite;
        mDriverX = x;
        mDriverY = y;
        mDriverSteps = 0;
        return;
      }
    }
      
  } // findDriver()
      
  // check the sprite that's controlling the tower height
  private void updateDriver() {
    
    assert( mDriver != null );
    
    int x = -1, y = -1;
    if ( mDriver instanceof Player ) {
      x = ((Player)mDriver).getXPos() - getXPos();
      y = ((Player)mDriver).getYPos() - getYPos();
    } else if ( mDriver instanceof Critter ) {
      x = ((Critter)mDriver).getXPos() - getXPos();
      y = ((Critter)mDriver).getYPos() - getYPos();
    }
    
    if ( x < 0 || x >= kBlockWidth || y < 0 || y >= kBlockWidth ) {
      mDriver = null;
      mDriverX = mDriverY = -1;
      mDriverSteps = -1;
      return;
    }

    if ( x == mDriverX && y == mDriverY ) {
      return;
    }
    
    int nextX = mDriverX,
        nextY = mDriverY;
    if ( mDriverY == 0 && mDriverX < kBlockWidth-1 ) {
      nextX += 1;
    } else if ( mDriverX == kBlockWidth-1 && mDriverY < kBlockWidth-1 ) {
      nextY += 1;
    } else if ( mDriverY == kBlockWidth-1 && mDriverX > 0 ) {
      nextX -= 1;
    } else if ( mDriverX == 0 && mDriverY > 0 ) {
      nextY -= 1;
    } else {
      assert( false );
    }

    if ( x == nextX && y == nextY ) {
      mDriverSteps += 1;
    } else {
      mDriverSteps = 0;
    }
    mDriverX = x;
    mDriverY = y;
    
  } // updateDriver()
  
  // raise or lower the tower
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {
    
    // raise the tower
    if ( mDriver == null ) {
      findDriver();
    } else {
      updateDriver();
      if ( mDriverSteps == kRaiseSteps ) {
        mDriverSteps = 0;
        if ( mHeight < mMaxHeight ) {
          shiftHeight(+1);
          Env.sounds().play(Sounds.WRENCH);
        }
        mDropTimer = kDropStartDelay;
      }
    }

    // lower the tower
    if ( mDropTimer > 0 ) {
      if ( --mDropTimer == 0 ) {
        if ( mHeight > 0 ) {
          shiftHeight(-1);
          if ( mHeight == 0 ) Env.sounds().play(Sounds.SWITCH_OFF);
        }
        mDropTimer = kDropNextDelay;
      }
    }
    
  } // Sprite.advance()

} // class ScrewTower
