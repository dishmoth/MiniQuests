/*
 *  RoomZ02.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Brain;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.FloorSwitch;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.WallSwitch;

// training room "Z02"
public class RoomZ02 extends Room {

  // unique identifier for this room
  public static final String NAME = "Z02";
  
  // main blocks for the room
  private static final String kBlocks[][] = { { "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "  000000  ",
                                                "  0    0  ",
                                                "  0    0  ",
                                                "  0    0  ",
                                                "  000000  ",
                                                "          " },
                                                
                                              { "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "  000000  ",
                                                "  0    0  ",
                                                "  0    0  ",
                                                "  0    0  ",
                                                "  000000  ",
                                                "          " },
                                                
                                              { "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "000    000",
                                                "000    000",
                                                "000    000",
                                                "0000000000",
                                                "0000000000" },
                                                
                                              { "   0      ",
                                                "   0      ",
                                                "   0000000",
                                                "         0",
                                                "         0",
                                                "         0",
                                                "         0",
                                                "         0",
                                                "         0",
                                                "         0" },
                                                
                                              { "   0      ",
                                                "   0      ",
                                                "   0000000",
                                                "         0",
                                                "         0",
                                                "         0",
                                                "         0",
                                                "         0",
                                                "         0",
                                                "         0" },
                                                
                                              { "   0      ",
                                                "   0      ",
                                                "   0000000",
                                                "         0",
                                                "         0",
                                                "         0",
                                                "         0",
                                                "         0",
                                                "         0",
                                                "         0" },
                                                
                                              { "   0      ",
                                                "   0      ",
                                                "   0010000",
                                                "         0",
                                                "         0",
                                                "         0",
                                                "         0",
                                                "         0",
                                                "         0",
                                                "         0" } };
  
  // stair blocks
  private static final String kStairBlocks[][] = { {"222"}, {" 22"}, {"  2"} };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "tk",
                                                  "7k",
                                                  "tk" };
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.DOWN,  2,0, "#k",0, -1, RoomZ01.NAME, 1),
              new Exit(Env.RIGHT, 3,8, "#k",0, -1, RoomZ03.NAME, 0) };

  // how fast the stairs move
  private static final int kStairsDelay = 4;

  // how long before the door shuts
  private static final int kEntranceDelay = 20;
  
  // how fast the switches flash
  private static final int kFlickerDelay = 80;
  
  // how many switches have been triggered
  private int mNumSwitchesDone;
  
  // reference to the stairs
  private BlockArray mStairBlocks;
  
  // time until the stairs move again
  private int mStairsTimer;

  // whether the stairs are in position
  private boolean mStairsDone;
  
  // references to the switches
  private FloorSwitch mFloorSwitchA,
                      mFloorSwitchB;
  private WallSwitch  mWallSwitch;
  
  // time until the entrance locks
  private int mEntranceTimer;
  
  // highlight the switches (or -1)
  private int mFlickerTimer;
  
  // constructor
  public RoomZ02() {

    super(NAME);

    mNumSwitchesDone = 0;
    
  } // constructor

  // whether the player is ready for more instructions
  public boolean firstBitDone() { 
    
    return ( mStairsDone && !mPlayer.isActing() ); 
    
  } // firstBitDone()
  
  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < kExits.length );
    
    setPlayerAtExit(kExits[entryPoint]);
    if ( entryPoint == 0 ) {
      mPlayer.addBrain(new Brain.NoFireModule());
      mPlayer.addBrain(new Brain.ZombieModule(
                                 new int[]{ -1,5, Env.UP,10, -1,15 }));
    }
    
    return mPlayer;
    
  } // createPlayer()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {

    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,-4) );

    addBasicWalls(kExits, spriteManager);
    kExits[1].mDoor.setClosed(true);
  
    mStairBlocks = new BlockArray(kStairBlocks, kBlockColours, 6,0,-4);
    spriteManager.addSprite(mStairBlocks);
    mStairsTimer = 0;
    mStairsDone = false;

    mFloorSwitchA = new FloorSwitch(1, 8, 0, "#v", "tk");
    mFloorSwitchB = new FloorSwitch(1, 8, 0, "bb", "tk");
    spriteManager.addSprite(mFloorSwitchA);
    mFloorSwitchA.interact(); // hack: make sure the switch knows about the floor

    mWallSwitch = new WallSwitch(Env.UP, 5, 10, 
                                 new String[]{"b#", "b7", "u7"}, false);
    mWallSwitch.setState(1);
    spriteManager.addSprite(mWallSwitch);

    mEntranceTimer = kEntranceDelay;
    
    mFlickerTimer = kFlickerDelay;
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mStairBlocks = null;
    mFloorSwitchA = mFloorSwitchB = null;
    mWallSwitch  = null;
    
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

    // process the story event list
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();
      
      if ( event instanceof FloorSwitch.EventStateChange ) {
        FloorSwitch fs = ((FloorSwitch.EventStateChange)event).mSwitch;
        assert( mNumSwitchesDone == 0 );
        fs.freezeState(true);
        mNumSwitchesDone = 1;
        mStairsTimer = kStairsDelay;
        mFlickerTimer = -1;
        Env.sounds().play(Sounds.SWITCH_ON);
        it.remove();
      }
      
      if ( event instanceof WallSwitch.EventStateChange ) {
        assert( mNumSwitchesDone == 1 );
        mWallSwitch.setState(2);
        mNumSwitchesDone = 2;
        kExits[1].mDoor.setClosed(false);
        Env.sounds().play(Sounds.SUCCESS, 7);
        it.remove();
      }
      
    }
    
    // move the stairs
    if ( mNumSwitchesDone > 0 && mStairsTimer > 0 ) {
      if ( --mStairsTimer == 0 ) {
        mStairBlocks.shiftPos(0, 0, 1);
        if ( mStairBlocks.getZPos() < 2 ) {
          mStairsTimer = kStairsDelay;
        } else {
          mStairsDone = true;
          Env.sounds().play(Sounds.SUCCESS);
        }
      }
    }

    // highlight the floor switch
    if ( mNumSwitchesDone == 0 ) {
      if ( --mFlickerTimer < 0 ) mFlickerTimer = kFlickerDelay;
      final double t1 = 0,
                   t2 = 6;
      if ( mFlickerTimer == t1 || mFlickerTimer == t2 ) {
        spriteManager.removeSprite(mFloorSwitchB);
        spriteManager.addSprite(mFloorSwitchA);
      }
      if ( mFlickerTimer == t1+1 || mFlickerTimer == t2+1 ) {
        spriteManager.removeSprite(mFloorSwitchA);
        spriteManager.addSprite(mFloorSwitchB);
      }
    }
    
    // highlight the wall switch
    if ( mNumSwitchesDone == 1 ) {
      if ( mFlickerTimer < 0 ) {
        if ( mStairsDone ) mFlickerTimer = kFlickerDelay;
      } else {
        if ( --mFlickerTimer < 0 ) mFlickerTimer = kFlickerDelay;
        final double t1 = 0,
                     t2 = 6;
        if ( mFlickerTimer == t1 || mFlickerTimer == t2 ) {
          mWallSwitch.setState(1); 
        }
        if ( mFlickerTimer == t1+1 || mFlickerTimer == t2+1 ) {
          mWallSwitch.setState(0); 
        }
      }
    }
    
    // shut the entrance door
    if ( mEntranceTimer > 0 ) {
      if ( --mEntranceTimer == 0 ) {
        kExits[0].mDoor.setClosed(true);
        Env.sounds().play(Sounds.GATE);
      }
    }
    
  } // Room.advance()

} // class RoomZ02
