/*
 *  RoomC15.java
 *  Copyright Simon Hern 2013
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BitBuffer;
import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Fence;
import com.dishmoth.miniquests.game.FloorBoss;
import com.dishmoth.miniquests.game.FloorSwitch;
import com.dishmoth.miniquests.game.GlowPath;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.QuestStory;
import com.dishmoth.miniquests.game.WallSwitch;

// the room "C15"
public class RoomC15 extends Room {

  // unique identifier for this room
  public static final String NAME = "C15";
  
  // the basic blocks for the room
  private static final String kBlocks[][] = { { "0       00",
                                                "0 0 0 0 00",
                                                "0000000000",
                                                "0000000000",
                                                "0000111111",
                                                "0000100000",
                                                "0000100000",
                                                "0000100000",
                                                "0000100000",
                                                "    1     " } };
  
  // blocks filling in the wall below main level
  private static final String kWallBelow[]     = { "0000000000" };
  private static final String kBlocksBelow[][] = { kWallBelow, kWallBelow,
                                                   kWallBelow, kWallBelow,
                                                   kWallBelow, kWallBelow,
                                                   kWallBelow, kWallBelow,
                                                   kWallBelow, kWallBelow };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "VZ",   // blue
                                                  "V:" }; // blue/yellow
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(0,1, Env.LEFT,  5,0, "VZ",0, -1, RoomC14.NAME, 0),
              new Exit(0,1, Env.RIGHT, 5,0, "#:",0, -1, RoomC16.NAME, 0) };

  // glowing path
  private static final String kGlowPath[] = { "+++++++",
                                              "+      ",
                                              "+      ",
                                              "+      ",
                                              "+      ",
                                              "+      ",
                                              "X      " };
  
  // different orders in which the switches can light up
  private static final int kSwitchOrders[][] = { {0,1,2}, {0,2,1}, {1,0,2}, 
                                                 {1,2,0}, {2,0,1}, {2,1,0} };
  
  // how long until the switches light up
  private static final int kSwitchsStartDelay   = 50,
                           kSwitchIntervalDelay = 10;

  // how long until the gate opens
  private static final int kGateDelay = 25;
  
  // references to the wall switches
  private WallSwitch mSwitches[];

  // how long until all the switches are lit
  private int mSwitchTimer;
  
  // which order the switches lit up in (or -1)
  private int mSwitchOrder;
  
  // number of switches hit (negative if order is wrong)
  private int mSwitchesHit;
  
  // whether the gate has been unlocked yet
  private boolean mSwitchesDone;
  
  // reference to the gate object (actually a bit of fence)
  private Fence mGate;
  
  // whether the boss has been killed yet
  private boolean mBossDone;

  // reference to the path object
  private GlowPath mPath;
  
  // whether the path has been completed
  private boolean mPathDone;
  
  // constructor
  public RoomC15() {

    super(NAME);

    mSwitchesDone = false;
    mBossDone = false;
    mPathDone = false;
    
  } // constructor

  // serialize the room state by writing bits to the specified buffer
  @Override
  public void save(BitBuffer buffer) {
    
    buffer.writeBit(mSwitchesDone);
    buffer.writeBit(mBossDone);
    buffer.writeBit(mPathDone);
    
  } // Room.save()

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  @Override
  public boolean restore(int version, BitBuffer buffer) { 
    
    if ( buffer.numBitsToRead() < 3 ) return false;
    mSwitchesDone = buffer.readBit();
    mBossDone = buffer.readBit();
    mPathDone = buffer.readBit();
    return true; 
    
  } // Room.restore() 
  
  // whether the path is complete
  // (note: this function may be called by RoomC16)
  public boolean pathComplete() { return mPathDone; }
  
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
    
    int zoneX, zoneY;
    
    // zone (0,0)
    
    zoneX = 0;
    zoneY = 0;
    addBasicZone(zoneX, zoneY, 
                 true, false, true, true, 
                 kExits, spriteManager);

    FloorBoss boss = new FloorBoss();
    spriteManager.addSprite( boss );
    if ( mBossDone ) boss.setDead();
    
    // zone (0,1)
    
    zoneX = 0;
    zoneY = 1;
    addBasicZone(zoneX, zoneY, 
                 true, true, true, false, 
                 kExits, spriteManager);

    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 
                                      Room.kSize*zoneX, Room.kSize*zoneY, 0) );
    spriteManager.addSprite( new BlockArray(kBlocksBelow, kBlockColours, 
                                      Room.kSize*zoneX, Room.kSize*zoneY+1, -20) );
    
    spriteManager.addSprite( new Fence(Room.kSize*zoneX, Room.kSize*zoneY, 0, 
                                       4,Env.RIGHT, 1) );
    spriteManager.addSprite( new Fence(Room.kSize*zoneX+5, Room.kSize*zoneY, 0, 
                                       5,Env.RIGHT, 1) );
    
    if ( !mSwitchesDone ) {
      mGate = new Fence(Room.kSize*zoneX+3, Room.kSize*zoneY, 0, 
                        3,Env.RIGHT, 1);
      spriteManager.addSprite(mGate);
    }

    String switchColours[] = { "j7", "u7" };
    mSwitches = new WallSwitch[3];
    mSwitches[0] = new WallSwitch(0,1, Env.UP, 2,2, switchColours, false);
    mSwitches[1] = new WallSwitch(0,1, Env.UP, 4,2, switchColours, false);
    mSwitches[2] = new WallSwitch(0,1, Env.UP, 6,2, switchColours, false);
    for ( WallSwitch ws : mSwitches ) {
      ws.setState(1);
      spriteManager.addSprite(ws);
    }
    
    if ( mSwitchesDone ) {
      mSwitchTimer = 0;
      mSwitchOrder = -1;
    } else {
      mSwitchTimer = kSwitchsStartDelay;
      mSwitchOrder = Env.randomInt(kSwitchOrders.length-2) + 1;
    }
    mSwitchesHit = 0;
 
    mPath = new GlowPath(kGlowPath, 4,9,0, 'D');
    if ( mBossDone ) {
      if ( mPathDone ) {
        mPath.setComplete();
      }
      spriteManager.addSprite(mPath);
    }

  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mGate = null;
    mSwitches = null;
    mPath = null;
    
  } // Room.discardResources()
  
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
    
    // check for scrolling
    EventRoomScroll scroll = checkHorizontalScroll();
    if ( scroll != null ) {
      storyEvents.add(scroll);
    }
    
    // check the switches
    boolean saveGameEvent = false;
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();
      if ( event instanceof FloorSwitch.EventStateChange ) {
        it.remove();
      }
      if ( event instanceof WallSwitch.EventStateChange ) {
        WallSwitch ws = ((WallSwitch.EventStateChange)event).mSwitch;
        assert( !mSwitchesDone );
        assert( ws.getState() == 1 );
        if ( mSwitchesHit < 0 ) {
          mSwitchesHit -= 1;
        } else {
          int order[] = kSwitchOrders[mSwitchOrder];
          int switchIndex = order[mSwitchesHit];
          mSwitchesHit += 1;
          if ( mSwitches[switchIndex] != ws ) mSwitchesHit = -mSwitchesHit;
        }
        if ( mSwitchesHit == mSwitches.length ) {
          mSwitchesDone = true;
          mSwitchTimer = kGateDelay;
          saveGameEvent = true;
        } else if ( mSwitchesHit == -mSwitches.length ) {
          mSwitchTimer = kSwitchsStartDelay;
          int next = Env.randomInt(kSwitchOrders.length-1) + 1;
          mSwitchOrder = (mSwitchOrder + next) % kSwitchOrders.length;
          mSwitchesHit = 0;
        }
        it.remove();
      }
    }
    if ( saveGameEvent ) storyEvents.add(new QuestStory.EventSaveGame());
    
    // light up the switches or open the gate
    if ( mSwitchesDone ) {
      if ( mSwitchTimer > 0 ) {
        if ( --mSwitchTimer == 0 ) {
          assert( mGate != null );
          spriteManager.removeSprite(mGate);
          mGate = null;
          Env.sounds().play(Sounds.GATE);
        }
      }
    } else {
      if ( mSwitchTimer > 0 ) {
        mSwitchTimer -= 1;
        int n = mSwitchTimer/kSwitchIntervalDelay;
        if ( n < mSwitches.length && mSwitchTimer == n*kSwitchIntervalDelay ) {
          int order[] = kSwitchOrders[mSwitchOrder];
          int switchIndex = order[ order.length - 1 - n ];
          assert( mSwitches[switchIndex].getState() == 1 );
          mSwitches[switchIndex].setState(0);
          Env.sounds().play(Sounds.SWITCH_OFF);
        }
      }
    }

    // shut the gate when the player goes in the boss area
    if ( !mBossDone && mGate == null && mPlayer != null && 
         !mPlayer.isActing() && scroll == null &&
         mPlayer.getXPos() == 4 && mPlayer.getYPos() == 9 ) {
      mGate = new Fence(3, 10, 0, 3,Env.RIGHT, 1);
      spriteManager.addSprite(mGate);
      Env.sounds().play(Sounds.GATE);
    }

    // open the gate after the player has died in the boss area
    if ( mSwitchesDone && !mBossDone && 
         mPlayer != null && mPlayer.getYPos() >= Room.kSize && 
         mGate != null && mSwitchTimer == 0 ) {
      spriteManager.removeSprite(mGate);
      mGate = null;
    }
    
    // check if the player falls to death (should be unnecessary?)
    if ( mPlayer != null && mPlayer.getZPos() < -20 ) {
      mPlayer.destroy(-1);
    }

    // check if the boss is dead
    if ( !mBossDone ) {
      FloorBoss b = (FloorBoss)spriteManager.findSpriteOfType(FloorBoss.class);
      if ( b.isDead() ) {
        mBossDone = true;
        spriteManager.removeSprite(mGate);
        mGate = null;
        Env.sounds().play(Sounds.GATE);
        spriteManager.addSprite(mPath);
        storyEvents.add(new QuestStory.EventSaveGame());
      }
    }
    
    // check the path
    if ( mPath != null && mPath.complete() ) {
      mPathDone = true;
    }
    
  } // Room.advance()

} // class RoomC15
