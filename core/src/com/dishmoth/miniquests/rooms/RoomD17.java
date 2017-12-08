/*
 *  RoomD17.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BitBuffer;
import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Liquid;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.Statue;
import com.dishmoth.miniquests.game.StoryEvent;

// the room "D17"
public class RoomD17 extends Room {

  // unique identifier for this room
  public static final String NAME = "D17";
  
  // main blocks for the room
  private static final String kBlocks[][] = { { "0000000000",
                                                "0000000000",
                                                "00      00",
                                                "00      00",
                                                "00      00",
                                                "00      00",
                                                "00      00",
                                                "00      00",
                                                "0000000000",
                                                "0000000000" },
                                                
                                              { "0000000000",
                                                "0000000000",
                                                "00      00",
                                                "00      00",
                                                "00      00",
                                                "00      00",
                                                "00      00",
                                                "00      00",
                                                "0000000000",
                                                "0000000000" },
                                                
                                              { "0000000000",
                                                "0000000000",
                                                "00      00",
                                                "00      00",
                                                "00      00",
                                                "00      00",
                                                "00      00",
                                                "00      00",
                                                "0000000000",
                                                "0000000000" },
                                                
                                              { "0000000000",
                                                "0000000000",
                                                "00      00",
                                                "00      00",
                                                "00      00",
                                                "00      00",
                                                "00      00",
                                                "00      00",
                                                "0000000000",
                                                "0000000000" },
                                                
                                              { "0000010000",
                                                "0000000000",
                                                "00      00",
                                                "00      00",
                                                "10   ---11",
                                                "00      00",
                                                "00      00",
                                                "00      00",
                                                "0000000000",
                                                "0000010000" },
                                                
                                              { "     1    ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "1        1",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "     1    " } };

  // blocks for the bridge thing
  private static final String[][] kBridgeBlocks = { { "1" } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "V#",
                                                  "lL" }; 
  
  // area covered by water
  private static final String[] kWaterPattern = { "######",
                                                  "######",
                                                  "######",
                                                  "######",
                                                  "######",
                                                  "######" };
  
  // details of exit/entry points for the room 
  private static final Exit kExits[]
            = { new Exit(Env.DOWN, 8,8, "V#",0, 0, RoomD16.NAME, 2) };
  
  // details of different camera height levels
  private static final CameraLevel kCameraLevels[]
                                     = { new CameraLevel(6,-100,+100) };
  
  // how long things last for
  private static final int kStatueDelay    = 30,
                           kStatueEndDelay = 18;

  // how quickly the water drops
  private static final int kWaterDelay = 7;
  
  // whether the room is complete
  private boolean mRoomDone;
  
  // references to statue objects
  private Statue mStatues[];
  
  // how long until the statues return to normal
  private int mStatueTimers[];
  
  // reference to 'invisible' blocks
  private BlockArray mBridgeBlocks[];
  
  // how many of the bridge blocks are visible
  private int mBridgeLength;
  
  // reference to water object
  private Liquid mWater;
  
  // how long until the water level drops (or zero)
  private int mWaterTimer;
  
  // constructor
  public RoomD17() {

    super(NAME);

    mRoomDone = false;
    
  } // constructor

  // serialize the room state by writing bits to the specified buffer
  @Override
  public void save(BitBuffer buffer) {
    
    buffer.writeBit(mRoomDone);
    
  } // Room.save()

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  @Override
  public boolean restore(int version, BitBuffer buffer) { 
    
    if ( buffer.numBitsToRead() < 1 ) return false;
    mRoomDone = buffer.readBit();
    return true;
    
  } // Room.restore() 
  
  // whether this room is complete yet
  // (note: this function may be called by room D02)
  public boolean done() { return mRoomDone; }
  
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

    mStatues = new Statue[]{ new Statue(5,0,10, Env.UP, 0),
                             new Statue(9,5,10, Env.LEFT, 0),
                             new Statue(5,9,10, Env.DOWN, 0),
                             new Statue(0,5,10, Env.RIGHT, 0) };
    for ( Statue s : mStatues ) spriteManager.addSprite(s);
    
    mStatueTimers = new int[4];

    RoomD02 fountainRoom = (RoomD02)findRoom(RoomD02.NAME);
    assert( fountainRoom != null );
    if ( mRoomDone && !fountainRoom.completed() ) {
      mWater = null;
    } else {
      mWater = new Liquid(2,2,6, 0, kWaterPattern);
      spriteManager.addSprite(mWater);
    }
    
    mWaterTimer = 0;

    mBridgeLength = 0;
    mBridgeBlocks = new BlockArray[] {
                         new BlockArray(kBridgeBlocks, kBlockColours, 7,5,8),
                         new BlockArray(kBridgeBlocks, kBlockColours, 6,5,8),
                         new BlockArray(kBridgeBlocks, kBlockColours, 5,5,8) };
    if ( mRoomDone ) {
      for ( BlockArray b : mBridgeBlocks ) spriteManager.addSprite(b);
      mBridgeLength = mBridgeBlocks.length;
    }
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mStatues = null;
    mWater = null;
    mBridgeBlocks = null;
    
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

    if ( mWaterTimer > 0 ) {
      assert( mRoomDone );
      assert( mWater != null );
      if ( --mWaterTimer == 0 ) {
        mWater.setZPos( mWater.getZPos() - 1 );
        if ( mWater.getZPos() > -4 ) mWaterTimer = kWaterDelay;
      }
    }
    
    if ( mBridgeLength < mBridgeBlocks.length ) {
      if ( mPlayer != null && mPlayer.getYPos() == 5 ) {
        int xBridge = 8 - mBridgeLength;
        if ( mPlayer.getXPos() == xBridge-1 ) {
          spriteManager.addSprite( mBridgeBlocks[mBridgeLength] );
          mBridgeLength += 1;
          if ( mBridgeLength == mBridgeBlocks.length ) {
            Env.sounds().play( Sounds.SWITCH_ON );
          }
        } else if ( mPlayer.getXPos() == xBridge+1 ) {
          mBridgeLength -= 1;
          spriteManager.removeSprite( mBridgeBlocks[mBridgeLength] );
        }
      }
    }
    
    if ( !mRoomDone ) {
      boolean hit = false;
      for ( int k = 0 ; k < mStatues.length ; k++ ) {
        if ( mStatues[k].isHit() ) {
          hit = true;
          mStatues[k].setHit(false);
          mStatueTimers[k] = kStatueDelay;
          mStatues[k].setColour(3);
        }
      }
      if ( hit ) {
        for ( int k = 0 ; k < mStatues.length ; k++ ) {
          if ( mStatueTimers[k] > 0 ) mStatueTimers[k] = kStatueDelay;
        }
      }
    }
    
    for ( int k = 0 ; k < mStatues.length ; k++ ) {
      if ( mStatueTimers[k] > 0 ) {
        if ( --mStatueTimers[k] == 0 ) mStatues[k].setColour(0);
      }
    }
    
    if ( !mRoomDone ) {
      boolean done = true;
      for ( int k = 0 ; k < mStatues.length ; k++ ) {
        if ( mStatueTimers[k] == 0 ) done = false;
      }
      if ( done ) {
        for ( int k = 0 ; k < mStatues.length ; k++ ) {
          mStatueTimers[k] = kStatueEndDelay;
        }
        Env.sounds().play(Sounds.SUCCESS, 5);
        mRoomDone = true;
        mWaterTimer = 2*kWaterDelay;
      }
    }

  } // Room.advance()

} // class RoomD17
