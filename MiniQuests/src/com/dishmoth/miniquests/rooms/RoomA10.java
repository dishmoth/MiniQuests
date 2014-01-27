/*
 *  RoomA10.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BitBuffer;
import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.BlockPattern;
import com.dishmoth.miniquests.game.Critter;
import com.dishmoth.miniquests.game.CritterTrack;
import com.dishmoth.miniquests.game.Dragon;
import com.dishmoth.miniquests.game.DragonBubbles;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.FloorSwitch;
import com.dishmoth.miniquests.game.Liquid;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.Splatter;
import com.dishmoth.miniquests.game.Sprite;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.TinyStory;
import com.dishmoth.miniquests.game.WallSwitch;

// the room "A10"
public class RoomA10 extends Room {

  // main blocks for the room
  private static final String kBlocks[][] = { { "0        0",
                                                "0        0",
                                                "0        0",
                                                "0        0",
                                                "0        0",
                                                "0        0",
                                                "          ",
                                                "          ",
                                                " 00000000 ",
                                                " 0      0 " } };

  // blocks for a simple bridge
  private static final String kBridgeBlocks[][] = { { "0", "0", "0" } };

  // blocks for critters to walk on
  private static final String kCritterBlocks[][] = { { "000000000" } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#c" }; // orange
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit( 0,0, Env.DOWN,  1,0, "#c",0, -1, RoomA09.class, 1),
              new Exit( 0,0, Env.LEFT,  4,0, "#c",0, -1, RoomA12.class, 1),
              new Exit( 0,0, Env.RIGHT, 4,0, "#c",1, -1, RoomA11.class, 0),
              new Exit(-1,1, Env.DOWN,  4,0, "#c",0, -1, RoomA12.class, 0),
              new Exit( 0,1, Env.UP,    5,0, "#c",0, -1, RoomA13.class, 0),
              new Exit( 1,1, Env.DOWN,  5,0, "#c",0, -1, RoomA11.class, 1) };

  // moving path after first switch
  private static final String kPath1[] = { "             U  ",
                                           "      P      T  ",
                                           "      O   PQRST ",
                                           "      NLMNO   U ",
                                           "       K      V ",
                                           "       J      W ",
                                           "       I      X ",
                                           "ABCDEFGHI  baZYZ",
                                           "9       J  c   a",
                                           "8       K  d    ",
                                           "7               ",
                                           "6               ",
                                           "5               ",
                                           "4               ",
                                           "3               ",
                                           "2               ",
                                           "1               ",
                                           "0               " };
  
  // moving path after second switch
  private static final String kPath2[] = { "               ",
                                           "               ",
                                           "               ",
                                           "SRQPOPQRS      ",
                                           "T   N   T      ",
                                           "U   M   U      ",
                                           "V   L   V      ",
                                           "WXYZKJIHGFEDCBA",
                                           "    L         9",
                                           "    M         8",
                                           "              7",
                                           "              6",
                                           "              5",
                                           "              4",
                                           "              3",
                                           "              2",
                                           "              1",
                                           "              0" };
  
  // moving path after third switch
  private static final String kPath3[] = { "    H   ", 
                                           "    G   ", 
                                           "    F   ",
                                           "    E   ",
                                           "    D   ",
                                           "    C   ", 
                                           "    B   ",
                                           "    A   ",
                                           "    9   ",
                                           "    8   ",
                                           "    7   ",
                                           "    6   ", 
                                           "    5   ",
                                           "    4   ",
                                           "    3   ",
                                           "    2   ",
                                           "    1   ",
                                           "    0   " };
  
  // region where the critters are allowed to walk
  private static final CritterTrack kCritterTrack = 
                   new CritterTrack( new String[]{ "+++++++++" }, -10, 19 );
    
  // location of the boss fight
  private static final int kBossXPos  = 5,
                           kBossYPos  = 14,
                           kBossRange = 4;
  
  // how many switches have been triggered
  private int mNumSwitchesDone;

  // whether the wall switches have been triggered
  private boolean mWallSwitchesDone;
  
  // whether the boss has been killed
  private boolean mDragonKilled;
  
  // current behaviour
  private int mMode;
  
  // count the frames
  private int mTimer;

  // reference to the bridge blocks
  private BlockArray mRightBridge,
                     mLeftBridge;

  // reference to the switch for the first moving path's door
  private FloorSwitch mFloatingSwitch;

  // references to the switches for the second moving path's door
  private WallSwitch mWallSwitches[];
  
  // reference to the path object that leads to the final fight
  private BlockPattern mFinalPath;
  
  // reference to the switch object that controls to the final path
  private FloorSwitch mFinalSwitch;

  // direction from which the dragon (boss form) next attacks 
  private int mBossDragonDirec;
  
  // constructor
  public RoomA10() {
  
    mNumSwitchesDone = 0;
    mWallSwitchesDone = false;
    mDragonKilled = false;
    
  } // constructor

  // serialize the room state by writing bits to the specified buffer
  @Override
  public void save(BitBuffer buffer) {
    
    buffer.write(mNumSwitchesDone, 2);
    buffer.writeBit(mWallSwitchesDone);
    buffer.writeBit(mDragonKilled);
    
  } // Room.save()

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  @Override
  public boolean restore(int version, BitBuffer buffer) { 
    
    mNumSwitchesDone = buffer.read(2);
    if ( mNumSwitchesDone < 0 || mNumSwitchesDone > 3 ) return false;
    
    if ( buffer.numBitsToRead() < 2 ) return false;
    mWallSwitchesDone = buffer.readBit();
    mDragonKilled     = buffer.readBit();
    
    return true; 
    
  } // Room.restore() 
  
  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < kExits.length );
    
    // special behaviour
    if ( mMode == 2 ) {
      if ( entryPoint == 0 ) {
        kExits[5].mDoor.setClosed(true);
      } else {
        assert( entryPoint == 2 || entryPoint == 5 );
        kExits[5].mDoor.setClosed(false);
        mMode = 3;
        mTimer = 0;
      }
    } else if ( mMode == 5 ) {
      if ( entryPoint == 1 || entryPoint == 3 ) {
        assert( mWallSwitchesDone );
        kExits[1].mDoor.setClosed(false);
        kExits[3].mDoor.setClosed(false);
        mMode = 6;
        mTimer = 0;
      } else {
        assert( entryPoint == 0 || entryPoint == 2 || entryPoint == 5 );
        //if ( !mWallSwitchesDone ) {
        //  for ( WallSwitch ws : mWallSwitches ) ws.setState(0);
        //}
      }
    }
    
    setPlayerAtExit(kExits[entryPoint]);
    return mPlayer;
    
  } // createPlayer()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mRightBridge = mLeftBridge = null;
    mFloatingSwitch = null;
    mWallSwitches = null;
    mFinalPath = null;
    mFinalSwitch = null;
    
  } // Room.discardResources()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {

    int zoneX, zoneY;
    
    // zone (0,0)
    
    zoneX = 0;
    zoneY = 0;
    spriteManager.addSprite( 
                 new BlockArray(kBlocks, kBlockColours, 
                                zoneX*Room.kSize, zoneY*Room.kSize, 0) );
    spriteManager.addSprite( new Liquid(zoneX*Room.kSize, zoneY*Room.kSize,
                                        -1, 1) );
    addBasicZone(zoneX, zoneY, 
                 true, false, true, true, 
                 kExits, spriteManager);

    if ( mNumSwitchesDone < 3 ) kExits[1].mDoor.setClosed(true);
    
    mRightBridge = new BlockArray(kBridgeBlocks, kBlockColours,
                                  zoneX*Room.kSize+9, zoneY*Room.kSize+1, 0);
    if ( mNumSwitchesDone < 2 ) {
      mRightBridge.shiftPos(0, 3, 0);
    }
    spriteManager.addSprite(mRightBridge);
    
    mLeftBridge = new BlockArray(kBridgeBlocks, kBlockColours,
                                 zoneX*Room.kSize+0, zoneY*Room.kSize+1, 0);
    if ( mNumSwitchesDone < 3 ) {
      mLeftBridge.shiftPos(0, 3, 0);
    }
    spriteManager.addSprite(mLeftBridge);

    if ( mNumSwitchesDone >= 3 ) {
      makeFinalPath(spriteManager);
      if ( mDragonKilled ) {
        mFinalPath.setRange(mFinalPath.minValue(), mFinalPath.maxValue());
        mFinalSwitch.freezeState(true);
      } else {
        mFinalPath.setRange(mFinalPath.minValue(), mFinalPath.end());
      }
    }
    
    // zone (-1,1)
    
    zoneX = -1;
    zoneY = 1;
    spriteManager.addSprite( new Liquid(zoneX*Room.kSize, zoneY*Room.kSize,
                                        -1, 1) );
    addBasicZone(zoneX, zoneY, 
                 false, true, true, true, 
                 kExits, spriteManager);

    spriteManager.addSprite( 
                 new BlockArray(kCritterBlocks, kBlockColours, 
                                zoneX*Room.kSize, zoneY*Room.kSize+9, 0) );

    Critter critters[] = { new Critter( -2, 19, 0, Env.RIGHT, kCritterTrack),
                           new Critter(-10, 19, 0, Env.RIGHT, kCritterTrack) };
    for ( Critter critter : critters ) {
      critter.setColour(2);
      spriteManager.addSprite(critter);
    }
    
    String switchCols[] = new String[]{ "A7", "u7" };
    mWallSwitches = new WallSwitch[] { 
                      new WallSwitch(-1,1, Env.UP, 0, 2, switchCols, false),
                      new WallSwitch(-1,1, Env.UP, 4, 2, switchCols, false),
                      new WallSwitch(-1,1, Env.UP, 8, 2, switchCols, false) };
    for ( WallSwitch ws : mWallSwitches ) {
      if ( mWallSwitchesDone ) ws.setState(1);
      spriteManager.addSprite(ws);
    }
    
    if ( !mWallSwitchesDone ) kExits[3].mDoor.setClosed(true);
    
    // zone (0,1)
    
    zoneX = 0;
    zoneY = 1;
    spriteManager.addSprite( new Liquid(zoneX*Room.kSize, zoneY*Room.kSize,
                                        -1, 1) );
    addBasicZone(zoneX, zoneY, 
                 false, true, false, false, 
                 kExits, spriteManager);

    if ( !mDragonKilled ) kExits[4].mDoor.setClosed(true);
    
    // zone (1,1)
    
    zoneX = 1;
    zoneY = 1;
    spriteManager.addSprite( new Liquid(zoneX*Room.kSize, zoneY*Room.kSize,
                                        -1, 1) );
    addBasicZone(zoneX, zoneY, 
                 true, true, false, true, 
                 kExits, spriteManager);

    if ( mNumSwitchesDone <= 1 ) kExits[5].mDoor.setClosed(true);
    
    // switches
    
    if ( mNumSwitchesDone < 1 ) {
      spriteManager.addSprite( new FloorSwitch(8, 0, 0, "#v", "#c") );
    }
    if ( mNumSwitchesDone < 2 ) {
      spriteManager.addSprite( new FloorSwitch(9, 9, 0, "#v", "#c") );
    }
    if ( mNumSwitchesDone < 3 ) {
      spriteManager.addSprite( new FloorSwitch(0, 9, 0, "#v", "#c") );
    }

    // set the behaviour mode
    
    assert( mNumSwitchesDone >= 0 && mNumSwitchesDone <= 3 );

    if      ( mNumSwitchesDone == 0 ) mMode = 0;
    else if ( mNumSwitchesDone == 1 ) mMode = 2;
    else if ( mNumSwitchesDone == 2 ) mMode = 5;
    else if ( !mDragonKilled )        mMode = 8;
    else                              mMode = 10;
    
    mTimer = 0;

    mBossDragonDirec = Env.randomInt(4);
    
  } // Room.createSprites()

  // construct the final moving path (and its switch)
  private void makeFinalPath(SpriteManager spriteManager) {
    
    assert( mFinalPath == null );
    mFinalPath = new BlockPattern(kPath3, "#c", 1, 2, 0);
    mFinalPath.setRange(12, 12);
    spriteManager.addSprite(mFinalPath);
    
    assert( mFinalSwitch == null );
    mFinalSwitch = new FloorSwitch(kBossXPos, kBossYPos, 0, "#4", "#c");
    spriteManager.addSprite(mFinalSwitch);
    
  } // makeFinalPath()
  
  // construct a dragon for the boss fight
  private Dragon makeBossDragon() {
    
    int x = kBossXPos + kBossRange*Env.STEP_X[mBossDragonDirec],
        y = kBossYPos + kBossRange*Env.STEP_Y[mBossDragonDirec];
    Dragon dragon = new Dragon(x,y,-2, (mBossDragonDirec+2)%4, 
                               true, kBossRange, 0);
    
    mBossDragonDirec = (mBossDragonDirec + 1 + Env.randomInt(3))%4;
    
    return dragon;
    
  } // makeBossDragon()
  
  // construct a fake dragon for the boss fight
  private DragonBubbles makeBossDecoy(int offset, int lifetime) {
    
    assert( offset > 0 && offset < 4 );
    assert( lifetime > 0 );

    int direc = (mBossDragonDirec+offset)%4;
    int x = kBossXPos + kBossRange*Env.STEP_X[direc],
        y = kBossYPos + kBossRange*Env.STEP_Y[direc];
    return new DragonBubbles(x,y,-2, lifetime);
    
  } // makeBossDecoy()
  
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
    
    // process the story event list
    boolean saveGameEvent = false;
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();
      
      if ( event instanceof FloorSwitch.EventStateChange ) {
        FloorSwitch sw = ((FloorSwitch.EventStateChange)event).mSwitch;
        sw.freezeState(true);
        Env.sounds().play(Sounds.SWITCH_ON);
        if ( sw == mFloatingSwitch ) {
          assert( mMode == 2 );
          kExits[5].mDoor.setClosed(false);
        } else if ( sw == mFinalSwitch ) {
          assert( mMode == 8 );
          sw.freezeState(true);
        } else {
          assert( mFloatingSwitch == null && mFinalSwitch == null );
          mNumSwitchesDone += 1;
          assert( mNumSwitchesDone <= 3 );
        }
        it.remove();
      }
      
      if ( event instanceof WallSwitch.EventStateChange ) {
        assert( mMode == 5 );
        assert( !mWallSwitchesDone );
        assert( kExits[3].mDoor.closed() );
        boolean done = true;
        for ( WallSwitch ws : mWallSwitches ) {
          if ( ws.getState() == 0 ) done = false;
        }
        if ( done ) {
          mWallSwitchesDone = true;
          saveGameEvent = true;
          kExits[3].mDoor.setClosed(false);
          Env.sounds().play(Sounds.SUCCESS, 3);
        }
        it.remove();
      }
      
    }
    if ( saveGameEvent ) storyEvents.add(new TinyStory.EventSaveGame());

    // check for scrolling
    EventRoomScroll scroll = checkHorizontalScroll();
    if ( scroll != null ) {
      storyEvents.add(scroll);
      return;
    }

    // complete the path
    if ( mMode == 10 ) {
      assert( mFinalPath != null );
      assert( mDragonKilled );
      if ( mFinalPath.start() > mFinalPath.minValue() ) {
        mFinalPath.setStartRate(-6);
      } else {
        mFinalPath.setStartRate(0);
      }
      if ( mFinalPath.end() < mFinalPath.maxValue() ) {
        mFinalPath.setEndRate(+12);
      } else {
        mFinalPath.setEndRate(0);
        if ( kExits[4].mDoor.closed() ) {
          kExits[4].mDoor.setClosed(false);
          Env.sounds().play(Sounds.SUCCESS);
        }
      }
    }
    
    // boss fight
    if ( mMode == 9 ) {
      final int attackTime1  = 30,
                attackEarly2 = attackTime1 + 120,
                attackTime2  = attackEarly2 + 10,
                attackTime3  = attackTime2 + 120,
                finishTime   = attackTime3 + 90;
      if ( mTimer == attackTime1 ) {
        Dragon dragon = makeBossDragon();
        spriteManager.addSprite(dragon);
      }
      if ( mTimer == attackEarly2 ) {
        int decoy = Env.randomInt(3)+1;
        spriteManager.addSprite( makeBossDecoy(decoy, 15) );
      }
      if ( mTimer == attackTime2 ) {
        Dragon dragon = makeBossDragon();
        spriteManager.addSprite(dragon);
      }
      if ( mTimer == attackTime3 ) {
        for ( int decoy = 1 ; decoy <= 3 ; decoy++ ) {
          spriteManager.addSprite( makeBossDecoy(decoy, 10) );
        }
        Dragon dragon = makeBossDragon();
        dragon.setKillable(true);
        spriteManager.addSprite(dragon);
      }
      if ( mTimer == finishTime ) {
        assert( mPlayer != null );
        mDragonKilled = true;
        storyEvents.add(new TinyStory.EventSaveGame());
        mMode = 10;
        mTimer = -1;
      }
      if ( mPlayer == null ) {
        mMode = 8;
        mTimer = 0;
      }
    }
    
    // animate the path to the boss fight
    if ( mMode == 8 ) {
      final int pathDefault = 12,
                pathRate    = 6;
      if ( mFinalPath == null ) {
        makeFinalPath(spriteManager);
        spriteManager.addSprite(new Splatter(kBossXPos,kBossYPos,0, 
                                             -1, 2, (byte)4, -1));
      }
      if ( mPlayer == null ) {
        mFinalSwitch.unfreezeState();
        if ( mFinalPath.startRate() >= 0 && 
             spriteManager.findSpriteOfType(Dragon.class) == null ) {
          mFinalPath.setStartRate(-pathRate);
        }
      }
      if ( mFinalSwitch.isOn() ) {
        if ( mFinalPath.start() == pathDefault ) {
          mFinalPath.setStartRate(0);
          mMode = 9;
          mTimer = -1;
        } else {
          mFinalPath.setStartRate(+pathRate);
        }
      } else {
        final int startDelay = 30;
        if ( mTimer > startDelay && mFinalPath.start() == pathDefault) {
          mFinalPath.setStartRate(-pathRate);
        }
        if ( mFinalPath.start() <= mFinalPath.minValue() ) {
          mFinalPath.setStartRate(0);
        }
      }
    }
    
    // left bridge moves into place
    if ( mMode == 7 ) {
      final int stepDelay = 10;
      if ( mTimer == stepDelay ) {
        mLeftBridge.shiftPos(0, -1, 0);
        mTimer = -1;
        if ( mLeftBridge.getYPos() == 1 ) {
          mMode = 8;
          storyEvents.add(new TinyStory.EventSaveGame());
          Env.sounds().play(Sounds.SUCCESS);
        }
      }
    }
    
    // dragon defends the third switch
    if ( mMode == 6 ) {
      /*
      final int period = 90;
      mTimer = mTimer % (2*period);
      if ( mPlayer == null || mPlayer.getXPos() >= -1 ) {
        if ( mTimer == 0 ) {
          spriteManager.addSprite( new Dragon(4,6,-2, Env.LEFT, false, 4,0) );
        } else if ( mTimer == period ) {
          spriteManager.addSprite( new Dragon(4,9,-2, Env.LEFT, false, 4,0) );
        }
      }
      */
      if ( mNumSwitchesDone > 2 ) {
        mMode = 7;
        mTimer = -1;
      }
    }
    
    // the second moving path
    if ( mMode == 5 ) {
      final int pathDelay  = 16, //12,
                pathLength = 10,
                pathGap    = 6;
      final int pathPeriod = (pathLength+pathGap)*pathDelay;
      if ( mTimer % pathPeriod == 0 ) {
        BlockPattern path = new BlockPattern(kPath2, "#c", -10, 2, 0);
        path.setRange( path.minValue()-pathLength, path.minValue()-1 );
        path.setStartRate(+pathDelay);
        path.setEndRate(+pathDelay);
        spriteManager.addSprite(path);
      }
      final int dragonTime1 = 145, //105,
                dragonTime2 = dragonTime1 + 3*(pathPeriod/2);
      if ( mTimer >= dragonTime1 && (mTimer-dragonTime1)%pathPeriod == 0 ) {
        spriteManager.addSprite( new Dragon(8,12,-2, Env.LEFT, false, 4,0) );
      }
      if ( mTimer >= dragonTime2 && (mTimer-dragonTime2)%pathPeriod == 0 ) {
        spriteManager.addSprite( new Dragon(2,16,-2, Env.LEFT, false, 4,0) );
      }
      if ( mTimer == 100*pathPeriod ) mTimer -= pathPeriod;
    }

    // right bridge moves into place
    if ( mMode == 4 ) {
      final int stepDelay = 10;
      if ( mTimer == stepDelay ) {
        mRightBridge.shiftPos(0, -1, 0);
        mTimer = -1;
        if ( mRightBridge.getYPos() == 1 ) {
          mMode = 5;
          storyEvents.add(new TinyStory.EventSaveGame());
          Env.sounds().play(Sounds.SUCCESS);
        }
      }
    }
    
    // dragon defends the second switch
    if ( mMode == 3 ) {
      final int period = 140;
      mTimer = mTimer % period;
      if ( mTimer == 0 && (mPlayer == null || mPlayer.getXPos() <= 11) ) {
        spriteManager.addSprite( new Dragon(5,7,-2, Env.RIGHT, false, 4,0) );
      }
      if ( mNumSwitchesDone > 1 ) {
        mMode = 4;
        mTimer = -1;
      }
    }
    
    // the first moving path
    if ( mMode == 2 ) {
      final int pathDelay  = 16, //12,
                pathLength = 10,
                pathGap    = 6;
      final int pathPeriod = (pathLength+pathGap)*pathDelay;
      if ( mTimer % pathPeriod == 0 ) {
        BlockPattern path = new BlockPattern(kPath1, "#c", 4, 2, 0);
        path.setRange( path.minValue()-pathLength, path.minValue()-1 );
        path.setStartRate(+pathDelay);
        path.setEndRate(+pathDelay);
        spriteManager.addSprite(path);
      }
      final int dragonTime1 = 145, //96,
                dragonTime2 = dragonTime1 + pathPeriod/2;
      if ( mTimer >= dragonTime1 && (mTimer-dragonTime1)%pathPeriod == 0 ) {
        spriteManager.addSprite( new Dragon(6,16,-2, Env.DOWN, false, 4,0) );
      }
      if ( mTimer >= dragonTime2 && (mTimer-dragonTime2)%pathPeriod == 0 ) {
        spriteManager.addSprite( new Dragon(15,14,-2, Env.LEFT, false, 4,0) );
      }
      final int switchXPos    = 19,
                switchYPos    = 11,
                switchOnTime  = 37*pathDelay-1,
                switchOffTime = 47*pathDelay-1;
      if ( mTimer >= switchOnTime ) {
        if ( (mTimer-switchOnTime)%pathPeriod == 0 ) {
          assert( mFloatingSwitch == null );
          mFloatingSwitch = new FloorSwitch(switchXPos, switchYPos, 0, 
                                            "#o", "#c");
          spriteManager.addSprite(mFloatingSwitch);
        } else if ( (mTimer-switchOffTime)%pathPeriod == 0 ) {
          assert( mFloatingSwitch != null );
          spriteManager.removeSprite(mFloatingSwitch);
          mFloatingSwitch = null;
        }
      }
      if ( mTimer == 100*pathPeriod ) mTimer -= pathPeriod;
    }
    
    // dragon defends the first switch
    if ( mMode == 1 ) {
      final int period    = 90,
                periodMin = period - 15;
      mTimer = mTimer % (2*period);
      if ( mTimer == 0 ) {
        spriteManager.addSprite( new Dragon(6, 5, -2, Env.DOWN, false, 4, 0) );
      } else if ( mTimer == period ) {
        spriteManager.addSprite( new Dragon(3, 5, -2, Env.DOWN, false, 4, 0) );
      }
      if ( mNumSwitchesDone > 0 && (mTimer%period) > periodMin ) {
        mMode = 2;
        mTimer = -1;
        storyEvents.add(new TinyStory.EventSaveGame());
        Env.sounds().play(Sounds.SUCCESS, 3);
      }
    }

    // dragon not started yet
    if ( mMode == 0 ) {
      if ( mPlayer != null && mPlayer.getYPos() >= 0 ) {
        mMode = 1;
        mTimer = -1;
      }
    }

    // advance the timer
    mTimer++;
    
    // tidy up the sprite list
    removeDeadPaths(spriteManager);
    
  } // Room.advance()

  // kill any moving paths that have completed their journey
  private void removeDeadPaths(SpriteManager spriteManager) {
    
    LinkedList<Sprite> deadSprites = new LinkedList<Sprite>();
    for ( Sprite sp : spriteManager.list() ) {
      if ( sp instanceof BlockPattern ) {
        BlockPattern path = (BlockPattern)sp;
        if ( ( path.start() > path.maxValue() && path.startRate() > 0 ) ||
             ( path.end() < path.minValue() && path.endRate() < 0 ) ) {
          deadSprites.add(sp);
        }
      }
    }
    if (deadSprites.size() > 0) spriteManager.removeSprites(deadSprites);

  } // removeDeadPaths()
  
} // class RoomA10
