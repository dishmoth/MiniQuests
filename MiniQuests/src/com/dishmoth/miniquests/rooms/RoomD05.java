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
import com.dishmoth.miniquests.game.Spikes;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;

// the room "D05"
public class RoomD05 extends Room {

  // unique identifier for this room
  public static final String NAME = "D05";
  
  // blocks for tower
  private static final String kBaseBlocks[] = { "          ",
                                                "          ",
                                                "          ",
                                                "    000   ",
                                                "    000   ",
                                                "    000   ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " };

  // main blocks for the room
  private static final String kBlocks[][] = { { "2222222222",
                                                "2222222222",
                                                "2222222222",
                                                "2222222222",
                                                "2222222222",
                                                "2222222222",
                                                "2222222222",
                                                "2222222222",
                                                "2222222222",
                                                "2222222222" },
                                                
                                                kBaseBlocks,
                                                kBaseBlocks,
                                                kBaseBlocks,
                                                kBaseBlocks,
                                                kBaseBlocks,
                                              
                                              { "     1111 ",
                                                "     1    ",
                                                "     1    ",
                                                "    000   ",
                                                "1111000111",
                                                "    000   ",
                                                "     1    ",
                                                "     1    ",
                                                "     1111 ",
                                                "        1 " } };
                                              
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "lD",    // purple
                                                  "#D",    // purple
                                                  "De", }; // dark
  
  // details of exit/entry points for the room 
  private static final Exit kExits[][]
        = { { new Exit(Env.RIGHT, 5,8, "#D",0, 0, RoomD13.NAME, 0),
              new Exit(Env.UP,    8,8, "#D",1, 0, "",0),
              new Exit(Env.DOWN,  8,8, "#D",0, 0, "",0) },
              
            { new Exit(Env.RIGHT, 5,8, "#D",0, 0, "",0),
              new Exit(Env.UP,    8,8, "#D",1, 0, RoomD16.NAME, 0),
              new Exit(Env.DOWN,  8,8, "#D",0, 0, RoomD15.NAME, 1) },
              
            { new Exit(Env.RIGHT, 5,8, "#D",0, 0, RoomD18.NAME, 0),
              new Exit(Env.UP,    8,8, "#D",1, 0, "",0),
              new Exit(Env.DOWN,  8,8, "#D",0, 0, "",0) },
              
            { new Exit(Env.RIGHT, 5,8, "#D",0, 0, RoomD02.NAME, 1),
              new Exit(Env.UP,    8,8, "#D",1, 0, RoomD09.NAME, 1),
              new Exit(Env.DOWN,  8,8, "#D",0, 0, "",0) } };
              
  // details of different camera height levels
  private static final CameraLevel kCameraLevels[]
                                     = { new CameraLevel(6, -100, +100) };
  
  // the current exits, based on room D02's twist
  private Exit mExits[];

  // reference to spikes object
  private Spikes mSpikes;
  
  // constructor
  public RoomD05() {

    super(NAME);
    
  } // constructor

  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < mExits.length );
    setPlayerAtExit(mExits[entryPoint], kCameraLevels);
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
    
    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,-4) );
    
    addBasicWalls(mExits, spriteManager);

    mSpikes = new Spikes(4,4,8, 3,3, true, "e0");
    spriteManager.addSprite(mSpikes);
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mSpikes = null;
    
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
