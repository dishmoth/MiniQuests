/*
 *  RoomD04.java
 *  Copyright Simon Hern 2014
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.Statue;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.WallSwitch;

// the room "D04"
public class RoomD04 extends Room {

  // unique identifier for this room
  public static final String NAME = "D04";
  
  // blocks for the floor
  private static final String kBlocks[][] = { { "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000" },
                                              
                                              { "1    1  11",
                                                "1       1 ",
                                                "1       11",
                                                "          ",
                                                "         1",
                                                "          ",
                                                "          ",
                                                "1       11",
                                                "1       1 ",
                                                "1    1  11" },
                                              
                                              { "1    1  11",
                                                "1       1 ",
                                                "1       11",
                                                "          ",
                                                "         1",
                                                "          ",
                                                "          ",
                                                "1       11",
                                                "1       1 ",
                                                "1    1  11" },
                                              
                                              { "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                              
                                              { "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                              
                                              { "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                              
                                              { "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                              
                                              { "          ",
                                                "         1",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "         1",
                                                "          " },
                                              
                                              { "          ",
                                                "          ",
                                                "         1",
                                                "         1",
                                                "         1",
                                                "         1",
                                                "         1",
                                                "         1",
                                                "          ",
                                                "          " } };
                                              
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "m6",   // dark
                                                  "#s" }; // yellow

  // path connecting doors
  private static final String kPathBlocks[][] = { { "1111" },
                                                  { "1111" } };
  
  // stairs
  private static final String kStairBlocks[][] = { { "1111" },
                                                   { "1111" },
                                                   { "1111" },
                                                   { " 111" },
                                                   { "  11" },
                                                   { "   1" } };
  
  // details of exit/entry points for the room 
  private static final Exit kExits[][]
        = { { new Exit(Env.RIGHT, 5,4,  "#s",1, 0, RoomD02.NAME, 1),
              new Exit(Env.UP,    5,4,  "#s",1, 0, "",0),
              new Exit(Env.LEFT,  8,4,  "#s",0, 0, "",0),
              new Exit(Env.LEFT,  1,4,  "#s",0, 0, "",0),
              new Exit(Env.DOWN,  5,4,  "#s",0, 0, RoomD10.NAME, 0),
              new Exit(Env.RIGHT, 5,16, "#s",0, 1, "",0) },
              
            { new Exit(Env.RIGHT, 5,4,  "#s",1, 0, RoomD13.NAME, 0),
              new Exit(Env.UP,    5,4,  "#s",1, 0, RoomD02.NAME, 2),
              new Exit(Env.LEFT,  8,4,  "#s",0, 0, RoomD12.NAME, 1),
              new Exit(Env.LEFT,  1,4,  "#s",0, 0, "",0),
              new Exit(Env.DOWN,  5,4,  "#s",0, 0, "",0),
              new Exit(Env.RIGHT, 5,16, "#s",0, 1, RoomD13.NAME, 2) },
              
            { new Exit(Env.RIGHT, 5,4,  "#s",1, 0, "",0),
              new Exit(Env.UP,    5,4,  "#s",1, 0, "",0),
              new Exit(Env.LEFT,  8,4,  "#s",0, 0, "",0),
              new Exit(Env.LEFT,  1,4,  "#s",0, 0, "",0),
              new Exit(Env.DOWN,  5,4,  "#s",0, 0, "",0),
              new Exit(Env.RIGHT, 5,16, "#s",0, 1, "",0) },
              
            { new Exit(Env.RIGHT, 5,4,  "#s",1, 0, RoomD18.NAME, 0),
              new Exit(Env.UP,    5,4,  "#s",1, 0, RoomD01.NAME, 1),
              new Exit(Env.LEFT,  8,4,  "#s",0, 0, "",0),
              new Exit(Env.LEFT,  1,4,  "#s",0, 0, RoomD07.NAME, 2),
              new Exit(Env.DOWN,  5,4,  "#s",0, 0, RoomD02.NAME, 0),
              new Exit(Env.RIGHT, 5,16, "#s",0, 1, "",0) } };

  // details of different camera height levels
  private static final CameraLevel kCameraLevels[]
                                     = { new CameraLevel( 4, -100,   10),
                                         new CameraLevel(12,    6, +100) };

  // z-range of the path blocks
  private static final int kPathZStart = -3,
                           kPathZEnd   = 2;

  // z-range of the statue
  private static final int kStatueZStart = -2,
                           kStatueZEnd   = 4;

  // z-range of the statue
  private static final int kStairZStart = -8,
                           kStairZEnd   = 2;

  // rate at which objects move upwards
  private static final int kZDelay = 4;
  
  // how long statue effects last for
  private static final int kStatueDelay    = 30,
                           kStatueEndDelay = 18;
  
  // the current exits, based on room D02's twist
  private Exit mExits[];
  
  // whether the switches are completed
  private boolean mSwitchesDone[];
  
  // whether the room is complete
  private boolean mRoomDone;
  
  // references to the two switches (may be null)
  private WallSwitch mSwitches[];
  
  // references to the two path objects
  private BlockArray mPaths[];
  
  // counters for the two paths moving up a step (or zero)
  private int mPathTimers[];
  
  // references to the two statue objects
  private Statue mStatues[];
  
  // counters for the two statues moving up a step (or zero)
  private int mStatueTimers[];

  // references to the stair blocks
  private BlockArray mStairs[];
  
  // counters for the stairs moving up a step (or zero)
  private int mStairTimer;
  
  // how long until the statues return to normal
  private int mHitTimers[];
  
  // constructor
  public RoomD04() {

    super(NAME);

    mSwitchesDone = new boolean[]{ false, false };
    mRoomDone = false;
    
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
    
    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,0) );
    
    addBasicWalls(mExits, spriteManager);
    
    mSwitches = new WallSwitch[2];
    for ( int k = 0 ; k < 2 ; k++ ) {
      if ( !mSwitchesDone[k] ) {
        mSwitches[k] = new WallSwitch(Env.RIGHT, (k==0 ? 1 : 8), 6, 
                                      new String[]{"qu","77"}, false);
        spriteManager.addSprite( mSwitches[k] );
      }
    }
    
    mPaths = new BlockArray[2];
    mPathTimers = new int[2];
    for ( int k = 0 ; k < 2 ; k++ ) {
      mPaths[k] = new BlockArray( kPathBlocks, kBlockColours, 
                                  1, (k==0 ? 0 : 9), 
                                  (mSwitchesDone[k]?kPathZEnd:kPathZStart) );
      spriteManager.addSprite( mPaths[k] );
      mPathTimers[k] = 0;
    }

    mStatues = new Statue[2];
    mStatueTimers = new int[2];
    mHitTimers = new int[2];
    for ( int k = 0 ; k < 2 ; k++ ) {
      mStatues[k] = new Statue( 9, (k==0 ? 1 : 8), 
                                (mSwitchesDone[k]?kStatueZEnd:kStatueZStart),
                                Env.LEFT, 0 );
      spriteManager.addSprite( mStatues[k] );
      mStatueTimers[k] = 0;
      mHitTimers[k] = 0;
    }
    
    mStairs = new BlockArray[2];
    mStairTimer = 0;
    for ( int k = 0 ; k < 2 ; k++ ) {
      mStairs[k] = new BlockArray( kStairBlocks, kBlockColours, 
                                   6, (k==0 ? 0 : 9), 
                                   (mRoomDone?kStairZEnd:kStairZStart) );
      spriteManager.addSprite( mStairs[k] );
    }

  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mSwitches = null;
    mPaths = null;
    mStatues = null;
    mStairs = null;
    
  } // Room.discardResources()
  
  // update the room (events may be added or processed)
  @Override
  public void advance(LinkedList<StoryEvent> storyEvents,
                      SpriteManager          spriteManager) {

    // check the exits
    final int exitIndex = checkExits(mExits);
    if ( exitIndex != -1 ) {
      storyEvents.add(new EventRoomChange(mExits[exitIndex].mDestination,
                                          mExits[exitIndex].mEntryPoint));
      return;
    }

    // check camera level
    EventRoomScroll scroll = checkVerticalScroll(kCameraLevels);
    if ( scroll != null ) storyEvents.add(scroll);

    // check switches
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();
      if ( event instanceof WallSwitch.EventStateChange ) {
        WallSwitch ws = ((WallSwitch.EventStateChange)event).mSwitch;
        assert( ws == mSwitches[0] || ws == mSwitches[1] );
        int index = ( (ws == mSwitches[0]) ? 0 : 1 );
        assert( mSwitchesDone[index] == false );
        mSwitchesDone[index] = true;
        assert( mPathTimers[index] == 0 );
        mPathTimers[index] = kZDelay;
        it.remove();
      }
    }
    
    // move the paths upwards
    for ( int k = 0 ; k < mPathTimers.length ; k++ ) {
      if ( mPathTimers[k] > 0 ) {
        if ( --mPathTimers[k] == 0 ) {
          mPaths[k].shiftPos(0, 0, 1);
          if ( mPaths[k].getZPos() < kPathZEnd ) {
            mPathTimers[k] = kZDelay;
          } else {
            Env.sounds().play( Sounds.SWITCH_ON );
            mStatueTimers[k] = kZDelay;
          }
        }
      }
    }
    
    // move the statues upwards
    for ( int k = 0 ; k < mStatueTimers.length ; k++ ) {
      if ( mStatueTimers[k] > 0 ) {
        if ( --mStatueTimers[k] == 0 ) {
          mStatues[k].shiftPos(0, 0, 1);
          if ( mStatues[k].getZPos() < kStatueZEnd ) {
            mStatueTimers[k] = kZDelay;
          } else {
            Env.sounds().play( Sounds.SUCCESS );
          }
        }
      }
    }

    // check for hits on the statues
    if ( !mRoomDone ) {
      for ( int k = 0 ; k < mStatues.length ; k++ ) {
        if ( mStatues[k].isHit() ) {
          mStatues[k].setHit(false);
          mHitTimers[k] = kStatueDelay;
          mStatues[k].setColour(3);
        }
      }
    }
    for ( int k = 0 ; k < mStatues.length ; k++ ) {
      if ( mHitTimers[k] > 0 ) {
        if ( --mHitTimers[k] == 0 ) mStatues[k].setColour(0);
      }
    }

    // check if the statues are both hit
    if ( !mRoomDone ) {
      if ( mHitTimers[0] > 0 && mHitTimers[1] > 0 ) {
        for ( int k = 0 ; k < mStatues.length ; k++ ) {
          mHitTimers[k] = kStatueEndDelay;
        }
        Env.sounds().play(Sounds.SUCCESS, 5);
        mRoomDone = true;
        mStairTimer = kZDelay;
      }
    }
    
    // move the stairs upwards
    if ( mStairTimer > 0 ) {
      if ( --mStairTimer == 0 ) {
        mStairs[0].shiftPos(0, 0, 1);
        mStairs[1].shiftPos(0, 0, 1);
        if ( mStairs[0].getZPos() < kStairZEnd ) {
          mStairTimer = kZDelay;
        } else {
          Env.sounds().play( Sounds.SWITCH_ON );
        }
      }
    }
        
  } // Room.advance()

} // class RoomD04
