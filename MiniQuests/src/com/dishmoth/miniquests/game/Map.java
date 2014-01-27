/*
 *  Map.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// a window on the overworld map
public class Map extends Sprite {

  // story event: the player has entered a new location on the map
  public static class EventNewLocation extends StoryEvent {
    public int mXPos, mYPos;
    public EventNewLocation(int x, int y) { mXPos=x; mYPos=y; }
  } // class Map.EventNewLocation
  
  // story event: the player has left a location
  public static class EventLeftLocation extends StoryEvent {
    public EventLeftLocation() { }
  } // class Map.EventLeftLocation
  
  // story event: the player has entered a dungeon
  public static class EventEnterDungeon extends StoryEvent {
    public EventEnterDungeon() { }
  } // class Map.EventEnterDungeon
  
  // unit steps corresponding to each of the standard directions
  // (the y-axis is reversed compared to similar values in Env)
  public static final int STEP_X[] = { +1, 0, -1, 0 },
                          STEP_Y[] = { 0, -1, 0, +1 };
  
  // depth for the image
  private static final float kDepth = 0;
  
  // the map, broken up into sub-images, [y][x]
  private static EgaImage kImages[][];

  // which directions are possible in different rooms, [y][x][direc]
  private static boolean kExits[][][];
  
  // number of map pieces
  private static int kNumX,
                     kNumY;

  // delay when reaching a new location
  private static final int kTimePaused = 10; //5;
  
  // home position on the map
  private static final int kXOrigin = 2,
                           kYOrigin = 2;
  
  // index of the current room
  private int mXPos,
              mYPos;

  // direction being scrolled (or -1)
  private int mScrollDirec;
  
  // how far the room has scrolled
  private int mScrollDist;

  // how long to pause for (-1 to pause indefinitely)
  private int mPauseTimer;

  // whether there is a dungeon in this location
  private boolean mDungeonEntrance;
  
  // whether the fire key is ready
  private boolean mDungeonTrigger;
  
  // references to direction arrows if present
  private MapArrow mArrows[];
  
  // load and prepare the map images
  public static void initialize() {

    if ( kImages != null ) return;
    
    EgaImage image = Env.resources().loadEgaImage("Map.png");
    
    final int sizeX = Env.screenWidth(),
              sizeY = Env.screenHeight();
    kNumX = (image.width() + 1)/(sizeX + 1);
    kNumY = (image.height() + 1)/(sizeY + 1);
    assert( image.width() == sizeX*kNumX + (kNumX-1) );
    assert( image.height() == sizeY*kNumY + (kNumY-1) );
    
    kImages = new EgaImage[kNumY][kNumX];

    for ( int iy = 0 ; iy < kNumY ; iy++ ) {
      for ( int ix = 0 ; ix < kNumX ; ix++ ) {
        kImages[iy][ix] = new EgaImage(0, 0, sizeX, sizeY); 
        image.draw(kImages[iy][ix], -ix*(sizeX+1), -iy*(sizeY+1));
      }
    }

    kExits = new boolean[kNumY][kNumX][4];
    
    byte pixels[] = image.pixels();
    for ( int iy = 0 ; iy < kNumY ; iy++ ) {
      for ( int ix = 0 ; ix < kNumX ; ix++ ) {
        int index = iy*(sizeY+1)*image.width() + ix*(sizeX+1);
        byte hKey = ( (ix<kNumX-1) ? pixels[index+sizeX] : 63 ),
             vKey = ( (iy<kNumY-1) ? pixels[index+sizeY*image.width()] : 63 );
        assert( hKey == 0 || hKey == 63 );
        assert( vKey == 0 || vKey == 63 );
        if ( hKey == 0 ) {
          kExits[iy][ix][Env.RIGHT] = kExits[iy][ix+1][Env.LEFT] = true;
        }
        if ( vKey == 0 ) {
          kExits[iy][ix][Env.DOWN] = kExits[iy+1][ix][Env.UP] = true;
        }
      }
    }
    
  } // initialize()
  
  // constructor
  public Map(int x, int y) {
  
    initialize();
    
    mXPos = kXOrigin + x;
    mYPos = kYOrigin + y;
    
    mScrollDirec = -1;
    mScrollDist = 0;

    mPauseTimer = kTimePaused;
    mDungeonEntrance = false;
    mDungeonTrigger = false;
    
  } // constructor
  
  // freeze the map for a time
  public void pause(int delay) { assert(delay > 0); mPauseTimer = delay; }
  
  // notification that there is a dungeon here
  public void dungeonEntrance() { 
    
    assert( mScrollDirec == -1 );
    mDungeonEntrance = true; 
    
  } // dungeonEntrance()
  
  // scroll the map
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {
    
    final boolean keyUp    = Env.keys().up(),
                  keyDown  = Env.keys().down(),
                  keyLeft  = Env.keys().left(),
                  keyRight = Env.keys().right(),
                  keyFire  = Env.keys().fire();
    
    if ( mPauseTimer != 0 ) {
      
      // pause at the start of a new location, etc.
      
      if ( mPauseTimer > 0 ) mPauseTimer--;
      
    } else if ( mScrollDirec == -1 ) {
      
      // wait for a key to be pressed
      
      boolean exits[] = kExits[mYPos][mXPos];
      
      if ( mArrows == null ) {
        mArrows = new MapArrow[4];
        for ( int direc = 0 ; direc < 4 ; direc++ ) {
          if ( exits[direc] ) {
            mArrows[direc] = new MapArrow(direc);
            addTheseSprites.add(mArrows[direc]);
          }
        }
      }

      if ( mDungeonEntrance ) {
        if ( keyFire && mDungeonTrigger ) {
          newStoryEvents.add(new EventEnterDungeon());
          clearArrows(killTheseSprites);
          mPauseTimer = -1;
          Env.sounds().play(Sounds.MENU_2);
          return;
        } else {
          mDungeonTrigger = !keyFire;
        }
      }
      
      if      ( keyUp    && exits[Env.UP]    ) mScrollDirec = Env.UP;
      else if ( keyLeft  && exits[Env.LEFT]  ) mScrollDirec = Env.LEFT;
      else if ( keyDown  && exits[Env.DOWN]  ) mScrollDirec = Env.DOWN;
      else if ( keyRight && exits[Env.RIGHT] ) mScrollDirec = Env.RIGHT;

      if ( mScrollDirec != -1 ) {
        clearArrows(killTheseSprites);
        mDungeonEntrance = false;
        newStoryEvents.add(new EventLeftLocation());
        Env.sounds().play(Sounds.MAP);
      }
      
    } else {

      // scroll to a new location
      
      boolean horiz = ( mScrollDirec == Env.LEFT 
                     || mScrollDirec == Env.RIGHT );
      mScrollDist += (horiz ? 4 : 3);
      
      if ( mScrollDist >= (horiz ? Env.screenWidth() : Env.screenHeight()) ) {
        mXPos += STEP_X[mScrollDirec];
        mYPos += STEP_Y[mScrollDirec];
        mScrollDirec = -1;
        mScrollDist = 0;
        mPauseTimer = kTimePaused;
        newStoryEvents.add(new EventNewLocation(mXPos-kXOrigin, 
                                                mYPos-kYOrigin));
      }

      mDungeonTrigger = false;
      
    }
    
  } // Sprite.advance()

  // remove any direction arrows
  private void clearArrows(LinkedList<Sprite> killTheseSprites) {
    
    for ( MapArrow arrow : mArrows ) {
      if ( arrow != null ) killTheseSprites.add(arrow);
    }

    mArrows = null;
    
  } // clearArrows()
  
  // display the image
  @Override
  public void draw(EgaCanvas canvas) {

    int ix = mXPos,
        iy = mYPos;
    int x = ( (mScrollDirec >= 0) ? -mScrollDist*STEP_X[mScrollDirec] : 0 ),
        y = ( (mScrollDirec >= 0) ? -mScrollDist*STEP_Y[mScrollDirec] : 0 );
    kImages[iy][ix].draw(canvas, x, y, kDepth);
    
    if ( mScrollDirec >= 0 ) {
      ix += STEP_X[mScrollDirec];
      iy += STEP_Y[mScrollDirec];
      x += Env.screenWidth()*STEP_X[mScrollDirec];
      y += Env.screenHeight()*STEP_Y[mScrollDirec];
      kImages[iy][ix].draw(canvas, x, y, kDepth);
    }
    
  } // Sprite.draw()

} // class Map
