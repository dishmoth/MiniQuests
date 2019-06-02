/*
 *  RoomE07.java
 *  Copyright (c) 2018 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Fence;
import com.dishmoth.miniquests.game.FlameArea;
import com.dishmoth.miniquests.game.Liquid;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.WallSwitch;

// the room "E07"
public class RoomE07 extends Room {

  // unique identifier for this room
  public static final String NAME = "E07";
  
  // times for various actions
  private static final int kBridgeDelay  = 5,
                           kSwitchDelay  = 10,
                           kLiquidDelay1 = 20,
                           kLiquidDelay2 = 10;
  
  // main blocks for the room
  private static final String kBlocks[][] = { { "11      11",
                                                "11      11",
                                                "11      11",
                                                "11      11",
                                                "11      11",
                                                "11      11",
                                                "11      11",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000" },
                                              
                                              { "11      11",
                                                "11      11",
                                                "12      21",
                                                "12      21",
                                                "12      21",
                                                "11      11",
                                                "11      11",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000" } };
                                              
  // blocks for the bridge thing
  private static final String[][] kBridgeBlocks = { { "22" } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "t6",   //
                                                  "#c",   //
                                                  "yc" }; //
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.LEFT,  6,0, "#c",0, -1, RoomE06.NAME, 1),
              new Exit(Env.RIGHT, 6,0, "#c",0, -1, RoomE12.NAME, 2),
              new Exit(Env.DOWN,  2,0, "t6",0, -1, RoomE13.NAME, 0),
              new Exit(Env.RIGHT, 1,0, "#6",0, -1, RoomE12.NAME, 1) };

  // area covered by the flames for different numbers of switches
  private static final String kFlamePattern[][] = { { "OOOOOO",
                                                      "OOOOOO",
                                                      "OOOOOO",
                                                      "OOOOOO",
                                                      "OOOOOO",
                                                      "OOOOOO",
                                                      "OOOOOO" },
                                                    
                                                    { "OOOOOO",
                                                      "OOOOOO",
                                                      "OOOOOO",
                                                      "..OOOO",
                                                      "OOOOOO",
                                                      "OOOOOO",
                                                      "OOOOOO" },
                                                    
                                                    { "OOOOOO",
                                                      "OOOOOO",
                                                      "OOOOOO",
                                                      "....OO",
                                                      "OOOOOO",
                                                      "OOOOOO",
                                                      "OOOOOO" },
                                                    
                                                    { "OOOOOO",
                                                      "OOOOOO",
                                                      "OOOOOO",
                                                      "......",
                                                      "OOOOOO",
                                                      "OOOOOO",
                                                      "OOOOOO" } };
                                                    
  
  // references to the wall switches
  private WallSwitch mSwitches[];

  // number of switches that have been shot
  private int mSwitchesHit;
  
  // reference to blocks for the bridge
  private BlockArray mBridgeBlocks[];
  
  // reference to some objects
  private Liquid    mLiquid;
  private FlameArea mFlames;
  
  // delay while something moves
  private int mTimer;

  // constructor
  public RoomE07() {

    super(NAME);
    
    mSwitchesHit = 0;

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
    
    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,-2) );
    
    addBasicWalls(kExits, spriteManager);

    spriteManager.addSprite(new Fence(0, 2, 0, 10, Env.RIGHT, 1));

    mLiquid = new Liquid(0,0,-1, 2);
    spriteManager.addSprite(mLiquid);
    if ( mSwitchesHit == 4 ) mLiquid.setZPos(-3);

    if ( mSwitchesHit < 4 ) {
      mFlames = new FlameArea(2, 3, -1, kFlamePattern[mSwitchesHit]);
      mFlames.setTimeCycle(20, 75);
      spriteManager.addSprite(mFlames);
    }
  
    String switchColours1[] = { "c7", "u7" },
           switchColours2[] = { "L7", "u7" };
    mSwitches = new WallSwitch[4];
    mSwitches[0] = new WallSwitch(Env.UP, 1,2, switchColours1, false);
    mSwitches[1] = new WallSwitch(Env.UP, 3,2, switchColours1, false);
    mSwitches[2] = new WallSwitch(Env.UP, 7,2, switchColours1, false);
    mSwitches[3] = new WallSwitch(Env.UP, 5,2, switchColours2, false);
    for ( WallSwitch ws : mSwitches ) {
      ws.setState(1);
      spriteManager.addSprite(ws);
    }
    if ( mSwitchesHit < 4 ) mSwitches[mSwitchesHit].setState(0);

    mBridgeBlocks = new BlockArray[3];
    for ( int k = 0 ; k < mBridgeBlocks.length ; k++ ) {
      int x = 2*k + 2;
      mBridgeBlocks[k] = new BlockArray(kBridgeBlocks, kBlockColours, x,6,-3);
      spriteManager.addSprite(mBridgeBlocks[k]);
      if ( k < mSwitchesHit ) mBridgeBlocks[k].shiftPos(0, 0, 3);
    }

    mTimer = 0;
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mSwitches = null;
    mBridgeBlocks = null;
    mLiquid = null;
    mFlames = null;
    
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

    // check the switches
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();
      if ( event instanceof WallSwitch.EventStateChange ) {
        assert( mSwitchesHit < 4 );
        mSwitchesHit += 1;
        if ( mSwitchesHit < 4 ) {
          mFlames.setPattern(kFlamePattern[mSwitchesHit]);
          mTimer = kBridgeDelay;
        } else {
          mFlames.setFlame(false);
          mTimer = kLiquidDelay1;
        }
        it.remove();
      }
    }

    if ( mTimer > 0 ) {
      mTimer -= 1;
      if ( mTimer == 0 ) {
        assert( mSwitchesHit > 0 );
        if ( mSwitchesHit < 4 ) {
          BlockArray bridge = mBridgeBlocks[mSwitchesHit-1];
          if ( bridge.getZPos() < 0 ) {
            bridge.shiftPos(0, 0, 1);
            if ( bridge.getZPos() == 0 ) {
              Env.sounds().play(Sounds.SWITCH_OFF);
              mTimer = kSwitchDelay;
            } else {
              mTimer = kBridgeDelay;
            }
          } else {
            mSwitches[mSwitchesHit].setState(0);
          }
        } else {
          int z = mLiquid.getZPos() - 1;
          mLiquid.setZPos(z);
          if ( z > -3 ) mTimer = kLiquidDelay2;
        }
      }
    }
    
  } // Room.advance()

} // class RoomE07
