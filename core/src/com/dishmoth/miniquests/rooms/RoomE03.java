/*
 *  RoomE03.java
 *  Copyright (c) 2018 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.ArrayList;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Fence;
import com.dishmoth.miniquests.game.FenceGate;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.SnakeC;
import com.dishmoth.miniquests.game.SnakeImage;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.Room.EventRoomScroll;

// the room "E03"
public class RoomE03 extends Room {

  // unique identifier for this room
  public static final String NAME = "E03";
  
  // all visible blocks for the room
  private static final String kBlocks[][] = { { "   0000000",
                                                "   0000000",
                                                "   0000000",
                                                "   0000000",
                                                "   0000000",
                                                "      0   ",
                                                "      0   ",
                                                "      0   ",
                                                "      0   ",
                                                "      0   ",
                                                "0000000000",
                                                "0  0  0  0",
                                                "0  0  0  0",
                                                "0000000000",
                                                "0  0  0  0",
                                                "0  0  0  0",
                                                "0000000000",
                                                "0  0  0  0",
                                                "0  0  0  0",
                                                "0000000000" } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#g" }; // 
  
  // details of exit/entry points for the room 
  private static final Exit kExits[]
          = { // note: dummy exit at index 0
              new Exit(0,1, Env.UP,  6,0, "#g",0, -1, RoomE01.NAME, 1) };

  // constructor
  public RoomE03() {

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
      mPlayer = new Player(9, 6, 0, Env.LEFT);
      mCameraLevel = -1;
      mCamera.set(0, 0, 0);
    } else {
      setPlayerAtExit(kExits[entryPoint-1]);
    }
    
    return mPlayer;
    
  } // createPlayer()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {

    int zoneX, zoneY;
    
    // zone (0,0)

    zoneX = 0;
    zoneY = 0;

    addBasicZone(zoneX, zoneY, 
                 false, false, true, true,
                 kExits, spriteManager);

    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,0) );

    spriteManager.addSprite( new SnakeC(3,3,0, Env.DOWN) );
    
    // zone (1,0)

    zoneX = 1;
    zoneY = 0;

    addBasicZone(zoneX, zoneY, 
                 true, false, false, true,
                 kExits, spriteManager);

    // zone (0,1)

    zoneX = 0;
    zoneY = 1;

    addBasicZone(zoneX, zoneY, 
                 false, true, true, false,
                 kExits, spriteManager);

    spriteManager.addSprite(new Fence(zoneX*Room.kSize+3, 
                                      zoneY*Room.kSize+5,
                                      0, 3, Env.RIGHT, 1));
    spriteManager.addSprite(new Fence(zoneX*Room.kSize+7, 
                                      zoneY*Room.kSize+5,
                                      0, 3, Env.RIGHT, 1));

    FenceGate gate = new FenceGate(zoneX*Room.kSize+5, 
                                   zoneY*Room.kSize+5, 
                                   0, Env.RIGHT, 1);
    gate.setClosed(true);
    spriteManager.addSprite(gate);
    
    // zone (1,1)

    zoneX = 1;
    zoneY = 1;

    addBasicZone(zoneX, zoneY, 
                 true, true, false, false,
                 kExits, spriteManager);

  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

  } // Room.discardResources()
  
  // update the room (events may be added or processed)
  @Override
  public void advance(LinkedList<StoryEvent> storyEvents,
                      SpriteManager          spriteManager) {

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

} // class RoomE03
