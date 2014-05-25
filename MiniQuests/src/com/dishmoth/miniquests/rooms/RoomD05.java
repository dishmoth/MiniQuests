/*
 *  RoomD05.java
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

// the room "D05"
public class RoomD05 extends Room {

  // unique identifier for this room
  public static final String NAME = "D05";
  
  // main blocks for the room
  private static final String kBlocks[][] = { { "0000010000",
                                                "0000010000",
                                                "0000010000",
                                                "0000010000",
                                                "1111111111",
                                                "0000010000",
                                                "0000010000",
                                                "0000010000",
                                                "0000010000",
                                                "0000010000" } };
                                              
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#D",   //
                                                  "#D" }; //
  
  // details of exit/entry points for the room 
  private static final Exit kExits[][]
        = { { new Exit(Env.UP,    5,0, "#D",0, -1, RoomD02.NAME, 2),
              new Exit(Env.DOWN,  5,0, "#D",0, -1, "",0),
              new Exit(Env.LEFT,  5,0, "#D",0, -1, "",0),
              new Exit(Env.RIGHT, 5,0, "#D",0, -1, "",0) },
              
            { new Exit(Env.UP,    5,0, "#D",0, -1, "",0),
              new Exit(Env.DOWN,  5,0, "#D",0, -1, "",0),
              new Exit(Env.LEFT,  5,0, "#D",0, -1, RoomD02.NAME, 3),
              new Exit(Env.RIGHT, 5,0, "#D",0, -1, "",0) },
              
            { new Exit(Env.UP,    5,0, "#D",0, -1, "",0),
              new Exit(Env.DOWN,  5,0, "#D",0, -1, RoomD02.NAME, 0),
              new Exit(Env.LEFT,  5,0, "#D",0, -1, "",0),
              new Exit(Env.RIGHT, 5,0, "#D",0, -1, "",0) },
              
            { new Exit(Env.UP,    5,0, "#D",0, -1, "",0),
              new Exit(Env.DOWN,  5,0, "#D",0, -1, "",0),
              new Exit(Env.LEFT,  5,0, "#D",0, -1, "",0),
              new Exit(Env.RIGHT, 5,0, "#D",0, -1, RoomD02.NAME, 1) } };
              
  // the current exits, based on room D02's twist
  private Exit mExits[];
  
  // constructor
  public RoomD05() {

    super(NAME);
    
  } // constructor

  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < kExits.length );
    setPlayerAtExit(mExits[entryPoint]);
    return mPlayer;
    
  } // createPlayer()
  
  // configure exits based on the room D02's twist
  private void prepareExits() {
    
    RoomD02 twistRoom = (RoomD02)findRoom(RoomD02.NAME);
    assert( twistRoom != null );
    mExits = kExits[ twistRoom.twist() ];    
    
  } // prepareExist()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {
    
    prepareExits();
    
    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,0) );
    
    addBasicWalls(mExits, spriteManager);

  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {
    
  } // Room.discardResources()
  
  // update the room (events may be added or processed)
  @Override
  public void advance(LinkedList<StoryEvent> storyEvents,
                      SpriteManager          spriteManager) {

    final int exitIndex = checkExits(mExits);
    if ( exitIndex != -1 ) {
      storyEvents.add(new EventRoomChange(mExits[exitIndex].mDestination,
                                          mExits[exitIndex].mEntryPoint));
      return;
    }
    
  } // Room.advance()

} // class RoomD05
