/*
 *  RoomD14.java
 *  Copyright Simon Hern 2014
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

// the room "D14"
public class RoomD14 extends Room {

  // unique identifier for this room
  public static final String NAME = "D14";
  
  // main blocks for the room
  private static final String kBlocks[][] = { { "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000" } };

  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#U",
                                                  "#U" }; 
  
  // details of exit/entry points for the room 
  private static final Exit kExits[]
            = { new Exit(Env.LEFT, 1,0, "#U",0, -1, RoomD13.NAME, 1),
                new Exit(Env.UP,   5,0, "#U",0, -1, RoomD15.NAME, 0) };
  
  // constructor
  public RoomD14() {

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
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {
    
    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,0) );
    
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

    final int exitIndex = checkExits(kExits);
    if ( exitIndex != -1 ) {
      storyEvents.add(new EventRoomChange(kExits[exitIndex].mDestination,
                                          kExits[exitIndex].mEntryPoint));
      return;
    }

  } // Room.advance()

} // class RoomD14
