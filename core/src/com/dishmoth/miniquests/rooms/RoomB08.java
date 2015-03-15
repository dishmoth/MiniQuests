/*
 *  RoomB08.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.Arrays;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;

// the room "B08"
public class RoomB08 extends Room {

  // unique identifier for this room
  public static final String NAME = "B08";
  
  // blocks for the room
  private static final String kBlocks[][] = { { "3    00   ",
                                                "0         ",
                                                "0         ",
                                                "0         ",
                                                "0         ",
                                                "0         ",
                                                "0        1",
                                                "0        0",
                                                "0        0",
                                                "3    00000" } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "VT",   // plum with blue
                                                  "lT",   // plum with pink
                                                  ":T",   // plum with yellow
                                                  "#T" }; // plum with white
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.RIGHT, 3,10, "#T",1, 0, RoomB07.NAME, 1),
              new Exit(Env.RIGHT, 7,10, "#T",1, 0, RoomB07.NAME, 2),
              new Exit(Env.DOWN,  5,10, "VT",1, 0, RoomB09.NAME, 0),
              new Exit(Env.LEFT,  5,10, "VT",1, 0, RoomB10.NAME, 0),
              new Exit(Env.UP,    5,10, "#T",1, 0, RoomB11.NAME, 0) };

  // details of different camera height levels
  private static final CameraLevel kCameraLevels[]
                                     = { new CameraLevel(10, -100, +100) };
  
  // blocks making up the bridge that extends from the room to the right
  private BlockArray mBridgeBlocksRight;
  
  // constructor
  public RoomB08() {

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

    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,10) );
    addBasicWalls(kExits, spriteManager);

    // check the blocks from the room to the right
    updateBridgeRight(spriteManager);
    
    // check the blocks from the room to the left
    RoomB10 roomLeft = (RoomB10)findRoom(RoomB10.NAME);
    assert( roomLeft != null );
    if ( roomLeft.upperBlocksActived() ) {
      final String blocks[][] = {{"3333"}};
      spriteManager.addSprite( new BlockArray(blocks, kBlockColours, 1,9,10));
    }
    if ( roomLeft.lowerBlocksActived() ) {
      final String blocks[][] = {{"3333"}};
      spriteManager.addSprite( new BlockArray(blocks, kBlockColours, 1,0,10));
    }
    
    // check the blocks from the room above
    RoomB11 roomUp = (RoomB11)findRoom(RoomB11.NAME);
    assert( roomUp != null );
    int extent = roomUp.bridgeExtent();
    if ( extent > 0 ) {
      String pattern[] = roomUp.getBridgeBlocks();
      String blocks[] = new String[extent];
      for ( int k = 0 ; k < blocks.length ; k++ ) {
        blocks[k] = pattern[pattern.length - extent + k];
      }
      spriteManager.addSprite( new BlockArray(new String[][]{blocks}, 
                                              kBlockColours, 
                                              7, Room.kSize-extent, 10) );
    }
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mBridgeBlocksRight = null;
    
  } // Room.discardResources()

  // create or move the bridge extending from the room to the right
  private void updateBridgeRight(SpriteManager spriteManager) {

    RoomB07 roomRight = (RoomB07)findRoom(RoomB07.NAME);
    assert( roomRight != null );
    int extent = 8 - roomRight.bridgeExtent();
    assert( extent >= 0 );
    
    if ( mBridgeBlocksRight != null ) {
      spriteManager.removeSprite(mBridgeBlocksRight);
    }
    
    if ( extent == 0 ) {
      mBridgeBlocksRight = null;
    } else {
      char blocks[] = new char[extent];
      Arrays.fill(blocks, '1');
      mBridgeBlocksRight = new BlockArray(new String[][]{{new String(blocks)}},
                                          kBlockColours, 9-extent, 3, 10);
      spriteManager.addSprite(mBridgeBlocksRight);
    }
    
  } // updateBridgeRight()
  
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

    // update the bridge extending from the room to the right
    RoomB07 roomRight = (RoomB07)findRoom(RoomB07.NAME);
    assert( roomRight != null );
    if ( roomRight.updateBridge() ) {
      updateBridgeRight(spriteManager);
    }
    
    // check whether the player is falling
    if ( mPlayer != null && mPlayer.getZPos() < -25 ) {
      mPlayer.destroy(-1);
    }
    
  } // Room.advance()
  
} // class RoomB08
