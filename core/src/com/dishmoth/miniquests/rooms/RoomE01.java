/*
 *  RoomE01.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Liquid;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.SnakeA;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;

// the room "E01"
public class RoomE01 extends Room {

  // unique identifier for this room
  public static final String NAME = "E01";
  
  // all visible blocks for the room
  private static final String kBlocks[][] = { { "0000000000",
                                                "0  0  0  0",
                                                "0  0  0  0",
                                                "0000000000",
                                                "0  0  0  0",
                                                "0  0  0  0",
                                                "0000000000",
                                                "0  0  0  0",
                                                "0  0  0  0",
                                                "0000000000" },
                                              
                                              { "0000000000",
                                                "0  0  0  0",
                                                "0  0  0  0",
                                                "0000000000",
                                                "0  0  0  0",
                                                "0  0  0  0",
                                                "0000000000",
                                                "0  0  0  0",
                                                "0  0  0  0",
                                                "0000000000" } };
  
  //
  private static final String kBlocksSide[][] = { { "0000      ",
                                                    "0000      ",
                                                    "0000      ",
                                                    "0000      ",
                                                    "0000      ",
                                                    "0000      ",
                                                    "0000      ",
                                                    "0000      ",
                                                    "0000      ",
                                                    "0000      " } };  
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#k" }; // 
  
  // details of exit/entry points for the room 
  private static final Exit kExits[]
          = { // note: dummy exit at index 0
              new Exit(0,0, Env.LEFT, 3,0, "#k",0, -1, RoomE02.NAME, 1), 
              new Exit(0,1, Env.LEFT, 3,0, "#k",0, -1, RoomE02.NAME, 0),
              new Exit(0,0, Env.DOWN, 2,0, "#k",0, -1, RoomE09.NAME, 0) };
          
  // constructor
  public RoomE01() {

    super(NAME);

  } // constructor

  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < kExits.length+1 );
    
    // special behaviour
    if ( entryPoint == 0 ) {
      // special case: start of game
      mPlayer = new Player(2, 2*Room.kSize+5, 0, Env.DOWN);
      mCameraLevel = -1;
      mCamera.set(0, 2*Room.kSize, 0);
    } else {
      setPlayerAtExit(kExits[entryPoint-1]);
    }
    
    return mPlayer;
    
  } // createPlayer()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {

    int zoneX, zoneY;

    for ( zoneY = 0 ; zoneY <= 2 ; zoneY++ ) {
      for ( zoneX = 0 ; zoneX <= 2 ; zoneX++ ) {
        addBasicZone(zoneX, zoneY, 
                     (zoneX==2), (zoneY==2), (zoneX==0), (zoneY==0),
                     kExits, spriteManager);
        if ( zoneX != 1 || zoneY != 1 ) {
          spriteManager.addSprite(new Liquid(zoneX*Room.kSize,
                                             zoneY*Room.kSize,
                                             -2, 2));
        }
      }
    }
    
    // zone (0,0)

    zoneX = 0;
    zoneY = 0;

    spriteManager.addSprite(
                new BlockArray(kBlocksSide, kBlockColours,
                               zoneX*Room.kSize, zoneY*Room.kSize, 0) );

    // zone (0,1)

    zoneX = 0;
    zoneY = 1;

    spriteManager.addSprite(
                new BlockArray(kBlocksSide, kBlockColours,
                               zoneX*Room.kSize, zoneY*Room.kSize, 0) );

    // zone (1,1)

    zoneX = 1;
    zoneY = 1;

    spriteManager.addSprite(
                new BlockArray(kBlocks, kBlockColours,
                               zoneX*Room.kSize, zoneY*Room.kSize, -2) );

    //spriteManager.addSprite(
    //            new SnakeA(zoneX*Room.kSize+3, zoneY*Room.kSize+3, 0,
    //                       Env.DOWN) );
    
    // zone (0,2)

    zoneX = 0;
    zoneY = 2;

    spriteManager.addSprite(
                new BlockArray(kBlocksSide, kBlockColours,
                               zoneX*Room.kSize, zoneY*Room.kSize, 0) );

  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

  } // Room.discardResources()
  
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

    // check for scrolling
    
    EventRoomScroll scroll = checkHorizontalScroll();
    if ( scroll != null ) {
      storyEvents.add(scroll);
    }

  } // Room.advance()

} // class RoomE01
