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
                                                "0000000000" } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#h",   // 
                                                  "Th" }; // 
  
  // details of exit/entry points for the room 
  private static final Exit kExits[]  = {}; // note: dummy exit at index 0

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

    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,0) );

    addBasicWalls(kExits, spriteManager);

    spriteManager.addSprite( new SnakeA(3,3,0, Env.DOWN) );
    
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

  } // Room.advance()

} // class RoomE01
