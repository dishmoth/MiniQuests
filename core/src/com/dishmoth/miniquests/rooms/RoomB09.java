/*
 *  RoomB09.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
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

// the room "B09"
public class RoomB09 extends Room {

  // unique identifier for this room
  public static final String NAME = "B09";
  
  // blocks for the room
  private static final String kBlocks[][] = { { " 000000000",
                                                " 0  000  0",
                                                " 0  000  0",
                                                " 000000000",
                                                " 000000000",
                                                " 000000000",
                                                " 0  000  0",
                                                " 0  000  0",
                                                " 000000000",
                                                "          " },
                                                
                                              { " 000000000",
                                                " 0  000  0",
                                                " 0  000  0",
                                                " 000000000",
                                                " 000000000",
                                                " 000000000",
                                                " 0  000  0",
                                                " 0  000  0",
                                                " 000000000",
                                                "          " },
                                                
                                              { "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "  1  2  3 ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "Vh",   // blue
                                                  "j#",   // button 1
                                                  "I#",   // button 2
                                                  "j#" }; // button 3
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.UP, 5,10, "#h",1, 0, RoomB08.NAME, 2) };

  // details of different camera height levels
  private static final CameraLevel kCameraLevels[]
                                     = { new CameraLevel(12, -100, +100) };
  
  // constructor
  public RoomB09() {

    super(NAME);

  } // constructor

  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < kExits.length );
    setPlayerAtExit(kExits[entryPoint], kCameraLevels);
    return mPlayer;
    
  } // createPlayer()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {

    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,8) );
    addBasicWalls(kExits, spriteManager);

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

  } // Room.advance()
  
} // class RoomB09
