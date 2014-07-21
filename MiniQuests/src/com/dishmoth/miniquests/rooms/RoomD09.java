/*
 *  RoomD09.java
 *  Copyright Simon Hern 2014
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.CritterTrack;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Spook;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;

// the room "D09"
public class RoomD09 extends Room {

  // unique identifier for this room
  public static final String NAME = "D09";
  
  // main blocks for the room
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
                                                "0001001000",
                                                "0  0  0  0",
                                                "0  0  0  0",
                                                "0001001000",
                                                "0  0  0  0",
                                                "0  0  0  0",
                                                "0000000000" }};

  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "WS",
                                                  "0S" }; 
  
  // details of exit/entry points for the room 
  private static final Exit kExits[][] 
          = { { new Exit(Env.UP,   2,0, "#S",0, -1, RoomD08.NAME, 1),
                new Exit(Env.DOWN, 8,0, "WS",0, -1, "",0) },
  
              { new Exit(Env.UP,   2,0, "#S",0, -1, RoomD08.NAME, 1),
                new Exit(Env.DOWN, 8,0, "WS",0, -1, "",0) },
              
              { new Exit(Env.UP,   2,0, "#S",0, -1, RoomD08.NAME, 1),
                new Exit(Env.DOWN, 8,0, "WS",0, -1, RoomD06.NAME, 0) },
              
              { new Exit(Env.UP,   2,0, "#S",0, -1, RoomD08.NAME, 1),
                new Exit(Env.DOWN, 8,0, "WS",0, -1, RoomD05.NAME, 1) } };
  
  // path the spooks follow
  private static final String kTrack[] = { "+++++++",
                                           "+  +  +",
                                           "+  +  +",
                                           "+++++++",
                                           "+  +  +",
                                           "+  +  +",
                                           "+++++++"};
  
  // time until spooks emerge
  private static final int kDelayStart = 30,
                           kDelayNext  = 15;
  
  // the current exits, based on room D02's twist
  private Exit mExits[];
  
  // countdown as the spooks emerge 
  private int mTimer;
  
  // constructor
  public RoomD09() {

    super(NAME);

  } // constructor

  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < mExits.length );
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
    
    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,-2) );
    
    addBasicWalls(mExits, spriteManager);

    mTimer = kDelayStart + 3*kDelayNext;
    
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

    if ( mTimer > 0 ) {
      mTimer -= 1;
      int num = mTimer/kDelayNext;
      if ( num <= 3 && mTimer == num*kDelayNext ) {
        switch ( num ) {
          case 0: {
            spriteManager.addSprite( new Spook(6,3,0, Env.UP,
                                               new CritterTrack(kTrack,3,0)) );
          } break;
          case 1: {
            spriteManager.addSprite( new Spook(3,6,0, Env.DOWN,
                                               new CritterTrack(kTrack,0,3)) );
          } break;
          case 2: {
            spriteManager.addSprite( new Spook(6,6,0, Env.RIGHT,
                                               new CritterTrack(kTrack,3,3)) );
          } break;
          case 3: {
            spriteManager.addSprite( new Spook(3,3,0, Env.LEFT,
                                               new CritterTrack(kTrack,0,0)) );
          } break;
        }
      }
    }
    
  } // Room.advance()

} // class RoomD09
