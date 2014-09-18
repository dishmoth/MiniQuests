/*
 *  RoomD14.java
 *  Copyright Simon Hern 2014
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.CritterTrack;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.Spook;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;

// the room "D14"
public class RoomD14 extends Room {

  // unique identifier for this room
  public static final String NAME = "D14";
  
  // main blocks for the room
  private static final String kBlocks[][] = { { "1111111111",
                                                "1111111111",
                                                "1111111111",
                                                "1111111111",
                                                "1111111111",
                                                "1111111111",
                                                "1111111111",
                                                "1111111111",
                                                "1111111111",
                                                "1111111111" },
                                                
                                              { " 00000    ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                " 000000000",
                                                " 0 0 0 0 0",
                                                " 0 0 0 0 0",
                                                " 0 0 0 0 0",
                                                " 0 0 0 0 0",
                                                " 000000000" },
                                                
                                              { " 00000    ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                " 000000000",
                                                " 0 0 0 0 0",
                                                " 0 0 0 0 0",
                                                " 0 0 0 0 0",
                                                " 0 0 0 0 0",
                                                " 000000000" },
                                                
                                              { " 00000    ",
                                                "     -    ",
                                                "     -    ",
                                                "     -    ",
                                                " 000000000",
                                                " 0 0 0 0 0",
                                                " 0 0 0 0 0",
                                                " 0 0 0 0 0",
                                                "00 0 0 0 0",
                                                " 000000000" } };

  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "OA",
                                                  "SW" }; 
  
  // details of exit/entry points for the room 
  private static final Exit kExits[]
            = { new Exit(Env.LEFT, 1,6, "OA",1, 0, RoomD13.NAME, 1),
                new Exit(Env.UP,   2,6, "#A",0, 0, RoomD15.NAME, 0) };

  // details of different camera height levels
  private static final CameraLevel kCameraLevels[]
                                     = { new CameraLevel(4, -100, +100) };

  // basic path for the monsters
  private static final String kPath[] = { "#","#","#","#","#",
                                          "#","#","#","#","#" };

  // blocks for the bridge thing
  private static final String[][] kBridgeBlocks = { { "0" } };
  
  // time until next monster
  private static final int kSpookDelay = 28;

  // time until bridge shrinks
  private static final int kBridgeDelay = 5;
  
  // count-down until next monster
  private int mSpookTimer;
  
  // which monster appears next
  private int mSpookNum;
  
  // reference to 'invisible' blocks
  private BlockArray mBridgeBlocks[];
  
  // how many of the bridge blocks are visible
  private int mBridgeLength;
  
  // count-down until bridge shrinks
  private int mBridgeTimer;
  
  // whether the bridge is complete
  private boolean mRoomDone;
  
  // constructor
  public RoomD14() {

    super(NAME);
    
    mRoomDone = false;

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
    
    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,0) );
    
    addBasicWalls(kExits, spriteManager);

    mSpookTimer = kSpookDelay;
    mSpookNum = 0;
    
    mBridgeLength = 0;
    mBridgeTimer = kBridgeDelay;
    mBridgeBlocks = new BlockArray[] {
                         new BlockArray(kBridgeBlocks, kBlockColours, 5,6,6),
                         new BlockArray(kBridgeBlocks, kBlockColours, 5,7,6),
                         new BlockArray(kBridgeBlocks, kBlockColours, 5,8,6) };
    if ( mRoomDone ) {
      for ( BlockArray b : mBridgeBlocks ) spriteManager.addSprite(b);
      mBridgeLength = mBridgeBlocks.length;
    }
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mBridgeBlocks = null;
    
  } // Room.discardResources()
  
  // update the room (events may be added or processed)
  @Override
  public void advance(LinkedList<StoryEvent> storyEvents,
                      SpriteManager          spriteManager) {

    // check the exits
    final int exitIndex = checkExits(kExits);
    if ( exitIndex != -1 ) {
      storyEvents.add(new EventRoomChange(kExits[exitIndex].mDestination,
                                          kExits[exitIndex].mEntryPoint));
      return;
    }

    // check events
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();
      if ( event instanceof Spook.EventKilled ) {
        it.remove();
      }
    }    
    
    // make monsters appear
    if ( --mSpookTimer == 0 ) {
      mSpookTimer = kSpookDelay;
      int x = 9 - 2*mSpookNum;
      Spook sp = new Spook(x,0,6, Env.UP, new CritterTrack(kPath,x,0));
      sp.vanishAfterSteps( mSpookNum==2 ? 8 : 4 );
      mSpookNum = (mSpookNum+1) % 5;
      spriteManager.addSprite(sp);
    }

    // extend the bridge
    if ( !mRoomDone ) {
      int yBridge = ( mPlayer != null && mPlayer.getXPos() == 5 ) 
                    ? Math.max(0, mPlayer.getYPos()-5) 
                    : 0;
      if ( yBridge == mBridgeLength + 1 ) {
        if ( yBridge == mBridgeBlocks.length+1 ) {
          mRoomDone = true;
          Env.sounds().play( Sounds.SWITCH_ON );
        } else {
          spriteManager.addSprite( mBridgeBlocks[mBridgeLength] );
          mBridgeLength += 1;
          mBridgeTimer = 1;
        }
      } else if ( yBridge == mBridgeLength ) {
        mBridgeTimer = 1;
      } else if ( yBridge < mBridgeLength ) {
        if ( --mBridgeTimer <= 0 ) {
          mBridgeTimer = kBridgeDelay;
          mBridgeLength -= 1;
          spriteManager.removeSprite( mBridgeBlocks[mBridgeLength] );
        }
      }
    }
    
  } // Room.advance()

} // class RoomD14
