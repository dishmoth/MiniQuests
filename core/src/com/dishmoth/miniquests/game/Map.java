/*
 *  Map.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// a window on the overworld map
public class Map extends Sprite {

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

  // delay when reaching a new location
  private static final int kTimePaused = 10;
  
  // basic map data (images, exits, etc)
  private final MapData mMapData;
  
  // index of the current room
  private int mPos;

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
  
  // constructor (start at quest location)
  public Map(MapData mapData, int startPoint) {

    reset();
    
    mMapData = mapData;
    mPos = mMapData.startPos(startPoint);
    
  } // constructor
  
  // constructor (restart after pause)
  public Map(MapData mapData, int restartData[]) {

    reset();
    
    mMapData = mapData;
    assert( restartData != null && restartData.length == 1 );
    mPos = restartData[0];
    
  } // constructor
  
  // reset state
  private void reset() {
    
    mScrollDirec = -1;
    mScrollDist = 0;

    mPauseTimer = kTimePaused;
    mDungeonEntrance = false;
    mDungeonTrigger = false;
    
  } // reset()
  
  // freeze the map for a time
  public void pause(int delay) { assert(delay > 0); mPauseTimer = delay; }
  
  // get information for restarting the map here
  public int[] getRestartData() { return new int[]{ mPos }; }
  
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
      
      if ( mArrows == null ) {
        mArrows = new MapArrow[4];
        for ( int direc = 0 ; direc < 4 ; direc++ ) {
          if ( mMapData.exit(mPos, direc) ) {
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
      
      if ( keyUp && mMapData.exit(mPos,Env.UP) ) {
        mScrollDirec = Env.UP;
      } else if ( keyLeft && mMapData.exit(mPos,Env.LEFT) ) {
        mScrollDirec = Env.LEFT;
      } else if ( keyDown && mMapData.exit(mPos,Env.DOWN) ) {
        mScrollDirec = Env.DOWN;
      } else if ( keyRight && mMapData.exit(mPos,Env.RIGHT) ) {
        mScrollDirec = Env.RIGHT;
      }

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
        mPos = mMapData.destination(mPos, mScrollDirec);
        mScrollDirec = -1;
        mScrollDist = 0;
        mPauseTimer = kTimePaused;
        StoryEvent event = mMapData.eventLocation(mPos);
        if ( event != null ) newStoryEvents.add(event);
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

    int x = ( (mScrollDirec >= 0) ? -mScrollDist*STEP_X[mScrollDirec] : 0 ),
        y = ( (mScrollDirec >= 0) ? -mScrollDist*STEP_Y[mScrollDirec] : 0 );
    mMapData.image(mPos).draw(canvas, x, y, kDepth);
    
    if ( mScrollDirec >= 0 ) {
      x += Env.screenWidth()*STEP_X[mScrollDirec];
      y += Env.screenHeight()*STEP_Y[mScrollDirec];
      int dest = mMapData.destination(mPos, mScrollDirec);
      mMapData.image(dest).draw(canvas, x, y, kDepth);
    }
    
  } // Sprite.draw()

} // class Map
