/*
 *  RoomE02.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;

// the room "E02"
public class RoomE02 extends Room {

  // unique identifier for this room
  public static final String NAME = "E02";
  
  // the basic blocks for the room
  private static final String kBlocks[][] = { { "   0000   ",
                                                "          ",
                                                "          ",
                                                "         0",
                                                "         0",
                                                "         0",
                                                "         0",
                                                "          ",
                                                "          ",
                                                "          " } };

  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "tc",   // orange
                                                  "Ff" }; // purple
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.LEFT,  2,0, "tc",0, -1, RoomE01.NAME, 1), 
              new Exit(Env.RIGHT, 5,2, "tc",0, -1, RoomE03.NAME, 0) };

  // rate at which the floor tiles update
  private static final int kUpdateTime = 3;
  
  // rate at which the floor modes change
  private static final int kChangeTime = 20;
  
  // which blocks are currently part of the floor
  private boolean mFloor[][];
  
  // reference to the floor blocks
  private BlockArray mFloorBlocks;

  // which type of floor animation is playing
  private int mFloorMode;
  
  // general counter for floor animation
  private int mFloorCounter;

  // ticks until the next change of floor tiles
  private int mUpdateCounter;

  // constructor
  public RoomE02() {

    super(NAME);

  } // constructor

  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < kExits.length );
    setPlayerAtExit(kExits[entryPoint]);
    return mPlayer;
    
  } // createPlayer()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mFloorBlocks = null;
    
  } // Room.discardResources()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {

    spriteManager.addSprite(new BlockArray(kBlocks, kBlockColours, 0, 0, 2));
    addBasicWalls(kExits, spriteManager);

    mFloor = new boolean[Room.kSize][Room.kSize];
    setRect(0, 0, Room.kSize, Room.kSize, false);

    mFloorMode = 0;
    mFloorCounter = 0;
    mUpdateCounter = kChangeTime;
    updateFloor(spriteManager);
    
  } // Room.createSprites()

  // construct blocks for the current floor state
  private BlockArray buildFloor() {

    String tiles[] = new String[Room.kSize];
    
    int numTiles = 0;
    for ( int y = 0 ; y < mFloor.length ; y++ ) {
      StringBuilder sb = new StringBuilder();
      for ( int x = 0 ; x < mFloor[y].length ; x++ ) {
        char ch = ( mFloor[y][x] ? '1' : ' ' );
        if ( ch != ' ' ) numTiles += 1;
        sb.append(ch);
      }
      tiles[Room.kSize-1-y] = sb.toString();
    }

    if ( numTiles == 0 ) return null;    
    return new BlockArray(new String[][]{tiles}, kBlockColours, 0, 0, 0);
    
  } // buildFloor()

  // fill in a rectangular region of the floor
  private void setRect(int x, int y, int w, int h, boolean val) {
    
    assert( w >= 0 && h >= 0 );
    if ( w == 0 || h == 0 ) return;
    
    int x0 = Math.max(0, x),
        x1 = Math.min(Room.kSize, x+w) - 1,
        y0 = Math.max(0, y),
        y1 = Math.min(Room.kSize, y+h) - 1;
    if ( x1 < x0 || y1 < y0 ) return;
    
    for ( int iy = y0 ; iy <= y1 ; iy++ ) {
      for ( int ix = x0 ; ix <= x1 ; ix++ ) {
        mFloor[iy][ix] = val;
      }
    }
    
  } // setRect()
  
  // move the floor tiles
  private void updateFloor(SpriteManager spriteManager) {

    if ( mUpdateCounter > 0 ) {
      mUpdateCounter--;
      return;
    } else {
      mUpdateCounter = kUpdateTime;
    }
    
    int nextModeDelay = 0;
    switch ( mFloorMode ) {
    
      case 0: {
        // growing from corners
        int d = mFloorCounter;
        setRect(0, 0, Room.kSize, Room.kSize, false);
        setRect(           0,            0, d, d, true);
        setRect(           0, Room.kSize-d, d, d, true);
        setRect(Room.kSize-d,            0, d, d, true);
        setRect(Room.kSize-d, Room.kSize-d, d, d, true);
        if ( d == 4 ) nextModeDelay = kChangeTime;
      } break;

      case 1: {
        // shifting horizontally
        int d = mFloorCounter + 1;
        setRect(0, 0, Room.kSize, Room.kSize, false);
        setRect(  d,            0, 4, 4, true);
        setRect(d+6,            0, 4, 4, true);
        setRect( -d, Room.kSize-4, 4, 4, true);
        setRect(6-d, Room.kSize-4, 4, 4, true);
        if ( d == 6 ) nextModeDelay = kChangeTime;
      } break;

      case 2: {
        // shifting vertically
        int d = mFloorCounter + 1;
        setRect(0, 0, Room.kSize, Room.kSize, false);
        setRect(0, 6-d, 4, 4, true);
        setRect(6,   d, 4, 4, true);
        if ( d == 6 ) nextModeDelay = kChangeTime;
      } break;

      case 3: {
        // merge to centre
        int d = mFloorCounter + 1;
        setRect(0, 0, Room.kSize, Room.kSize, false);
        setRect(  d,   d, 4, 4, true);
        setRect(6-d, 6-d, 4, 4, true);
        if ( d == 3 ) nextModeDelay = kChangeTime;
      } break;
      
      case 4: {
        // split to edges
        int d = mFloorCounter + 1;
        setRect(0, 0, Room.kSize, Room.kSize, false);
        setRect(3-d,   3, 4, 2, true);
        setRect(3+d,   5, 4, 2, true);
        setRect(  3, 3+d, 2, 4, true);
        setRect(  5, 3-d, 2, 4, true);
        if ( d == 3 ) nextModeDelay = kChangeTime;
      } break;
      
      case 5: {
        mFloorMode = 0;
      } break;

      default: assert(false);
      
    } // switch (mFloorMode)
    
    if ( mFloorBlocks != null ) spriteManager.removeSprite(mFloorBlocks);
    mFloorBlocks = buildFloor();
    if ( mFloorBlocks != null ) spriteManager.addSprite(mFloorBlocks);

    if ( nextModeDelay > 0 ) {
      mFloorCounter = 0;
      mFloorMode++;
      mUpdateCounter = nextModeDelay;
    } else {
      mFloorCounter++;
    }
    
  } // updateFloor()
  
  // update the room (events may be added or processed)
  @Override
  public void advance(LinkedList<StoryEvent> storyEvents,
                      SpriteManager          spriteManager) {

    // check exits
    final int exitIndex = checkExits(kExits);
    if ( exitIndex != -1 ) {
      storyEvents.add(new EventRoomChange(kExits[exitIndex].mDestination,
                                          kExits[exitIndex].mEntryPoint));
      return;
    }

    // animate the floor
    updateFloor(spriteManager);
    
    // check whether the player is falling
    if ( mPlayer != null && mPlayer.getZPos() < -25 ) {
      mPlayer.destroy(-1);
    }
    
  } // Room.advance()

} // class RoomE02
