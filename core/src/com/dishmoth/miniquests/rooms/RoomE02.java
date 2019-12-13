/*
 *  RoomE02.java
 *  Copyright (c) 2018 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.Barrier;
import com.dishmoth.miniquests.game.BitBuffer;
import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.BlockStairs;
import com.dishmoth.miniquests.game.Chest;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.FlameArea;
import com.dishmoth.miniquests.game.FloorSwitch;
import com.dishmoth.miniquests.game.Liquid;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.QuestStory;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Snake;
import com.dishmoth.miniquests.game.SnakeBoss3;
import com.dishmoth.miniquests.game.SnakeEgg;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.WallSwitch;
import com.dishmoth.miniquests.game.ZoneSwitch;

// the room "E02"
public class RoomE02 extends Room {

  // unique identifier for this room
  public static final String NAME = "E02";
  
  // blocks for zone (0,0)
  private static final String kBlocks00[][] = { { "0         ",
                                                  "0         ",
                                                  "0         ",
                                                  "0         ",
                                                  "0         ",
                                                  "01     0  ",
                                                  "0      0  ",
                                                  "0    0000 ",
                                                  "0    0  0 ",
                                                  "000000  0 " },
                                                
                                                { "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  " 1        ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          " },
                                                
                                                { "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  " 1        ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          " },
                                                
                                                { "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  " 1        ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          " },
                                                
                                                { "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "11        ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          " } };  
    
  // blocks for zone (0,1)
  private static final String kBlocks01[][] = { { "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "0000      ",
                                                  "          ",
                                                  "0         ",
                                                  "0         ",
                                                  "0         " },
                                                
                                                { "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "0000      ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          " },
                                                
                                                { "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "0000      ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          " },
                                                
                                                { "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "0000      ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          " },
                                                
                                                { "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "0000      ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          " } };  
    
  // blocks for zone (0,2)
  private static final String kBlocks02[][] = { { "    1     ",
                                                  "0         ",
                                                  "0    000  ",
                                                  "0         ",
                                                  "0         ",
                                                  "00000     ",
                                                  "0         ",
                                                  "0         ",
                                                  "0         ",
                                                  "          " },
                                                
                                                { "    1     ",
                                                  "          ",
                                                  "     000  ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          " },
                                                
                                                { "    1     ",
                                                  "          ",
                                                  "     000  ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          " },  
                                                
                                                { "    1     ",
                                                  "          ",
                                                  "     000  ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          " } };
  
  // blocks for zone (1,0)
  private static final String kBlocks10[][] = { { "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "  0   0   ",
                                                  "  0   0   ",
                                                  "  0   0   " } };

  // blocks for zone (1,1)
  private static final String kBlocks11[][] = { { "0000000000",
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
                                                  "0000000000",
                                                  "0  0  0  0",
                                                  "0  0  0  0",
                                                  "0000000000",
                                                  "0  0  0  0",
                                                  "0  0  0  0",
                                                  "0000000000" } };
  
  // blocks for zone (1,2)
  private static final String kBlocks21[][] = { { "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "    00000 ",
                                                  "    00000 ",
                                                  "    00000 ",
                                                  "    00000 ",
                                                  "    00000 ",
                                                  "          " },
                                                
                                                { "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "    00000 ",
                                                  "    00000 ",
                                                  "    00000 ",
                                                  "    00000 ",
                                                  "    00000 ",
                                                  "          " },
                                                
                                                { "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "    00000 ",
                                                  "    00000 ",
                                                  "    00000 ",
                                                  "    00000 ",
                                                  "    00000 ",
                                                  "          " } };

  // blocks for zone (2,0)
  private static final String kBlocks20[][] = { { "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "0   0     ",
                                                  "0   0     ",
                                                  "0   0     " } };

  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#k",   //
                                                  "Sk" }; // 
  
  // details of exit/entry points for the room
  private static final Exit kExits[]
          = { new Exit(0,2, Env.UP,   4,6, "#k",0, -1, RoomE01.NAME, 1),
              new Exit(0,2, Env.LEFT, 7,0, "#k",0, -1, RoomE05.NAME, 0),
              new Exit(0,1, Env.LEFT, 4,8, "#k",4, -1, RoomE03.NAME, 1),
              new Exit(0,1, Env.LEFT, 2,0, "#k",0, -1, RoomE03.NAME, 2),
              new Exit(0,0, Env.LEFT, 4,0, "#k",0, -1, RoomE03.NAME, 3), 
              new Exit(0,0, Env.DOWN, 8,0, "#k",0, -1, RoomE06.NAME, 0), 
              new Exit(2,0, Env.DOWN, 4,0, "#k",0, -1, RoomE12.NAME, 3) };
  
  // times at which things happen
  private static final int kGameEndsDelay   = 50,
                           kChestSoundDelay = kGameEndsDelay - 5,
                           kChestOpenDelay  = kChestSoundDelay - 10;  

  // pattern of flames around the chest
  private static final String kFlamePattern[] = { "ooooooo",
                                                  "o     o",
                                                  "o     o",
                                                  "      o",
                                                  "o     o",
                                                  "o     o",
                                                  "ooooooo" };

  // references to objects in zone (0,0)
  private BlockStairs mStairs00a,
                      mStairs00b;
  private ZoneSwitch  mStairSwitch00a,
                      mStairSwitch00b;
  
  // flags for zone (0,0)
  private boolean mStairs00Done;
  
  // references to objects in zone (0,2) 
  private BlockStairs mStairs02a,
                      mStairs02b,
                      mStairs02c;
  private ZoneSwitch  mStairSwitch02a,
                      mStairSwitch02b;
  private WallSwitch  mSwitch02a,
                      mSwitch02b;
  
  // flags for zone (0,2)
  private boolean mSwitch02aDone,
                  mSwitch02bDone,
                  mStairs02Done;
  
  // references to objects in zones (0,0) and (1,0)
  private FloorSwitch mSnakeBridgeSwitch;
  private BlockStairs mSnakeBridge;
  private Barrier     mSnakeBridgeBlock;

  // flags for zones (1,0) and (2,0)
  private int mSwitches10Done;
  
  // references to objects in zones (1,0) and (2,0)
  private FloorSwitch mSwitch10;
  private BlockStairs mBridges10[];
  
  // flags for zone (1,1)
  private boolean mSnakeDone;
  
  // references to objects in zone (1,1)
  private int         mSnakeSwitchesDone;
  private FloorSwitch mSnakeSwitches[];
  
  // references to objects in zone (1,2)
  private Chest       mChest;
  private BlockStairs mChestStairs;
  
  // countdown once the chest is opened
  private int mEndTimer;
  
  // constructor
  public RoomE02() {

    super(NAME);
    
    mStairs00Done = false;
    mSwitch02aDone = mSwitch02bDone = mStairs02Done = false;
    mSwitches10Done = 0;
    mSnakeDone = false;
    
  } // constructor

  // serialize the room state by writing bits to the specified buffer
  @Override
  public void save(BitBuffer buffer) {
    
    buffer.writeBit(mStairs00Done);
    buffer.writeBit(mSwitch02aDone);
    buffer.writeBit(mSwitch02bDone);
    buffer.writeBit(mStairs02Done);
    buffer.write(mSwitches10Done, 3);
    buffer.writeBit(mSnakeDone);
    
  } // Room.save()

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  @Override
  public boolean restore(int version, BitBuffer buffer) { 
    
    if ( buffer.numBitsToRead() < 8 ) return false;
    mStairs00Done = buffer.readBit();
    mSwitch02aDone = buffer.readBit();
    mSwitch02bDone = buffer.readBit();
    mStairs02Done = buffer.readBit();
    mSwitches10Done = buffer.read(3);
    mSnakeDone = buffer.readBit();
    if ( mSwitches10Done > 4 ) return false;
    return true;
    
  } // Room.restore() 
  
  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < kExits.length );
    
    // special behaviour
    if ( entryPoint == 0 ) {
      // special case: start of game
      mPlayer = new Player(4, 2*Room.kSize+9, 6, Env.DOWN);
      mCameraLevel = -1;
      mCamera.set(0, 2*Room.kSize, 0);
    } else {
      setPlayerAtExit(kExits[entryPoint]);
    }
    
    return mPlayer;
    
  } // createPlayer()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {

    int zoneX, zoneY;

    for ( zoneY = 0 ; zoneY <= 2 ; zoneY++ ) {
      for ( zoneX = 0 ; zoneX <= 2 ; zoneX++ ) {
        addBasicZone(zoneX, zoneY, 
                     (zoneX==2), (zoneY==2), (zoneX==0), (zoneY==0),
                     kExits, spriteManager);
        if ( zoneX != 1 || zoneY != 1 ) {
          spriteManager.addSprite(new Liquid(zoneX*Room.kSize,
                                             zoneY*Room.kSize,
                                             -2, 2));
        }
      }
    }
    
    // zone (0,0)

    zoneX = 0;
    zoneY = 0;

    spriteManager.addSprite(
                new BlockArray(kBlocks00, kBlockColours,
                               zoneX*Room.kSize, zoneY*Room.kSize, 0) );

    final int z00a = (mStairs00Done ? 8 : 2);
    mStairs00a = new BlockStairs(zoneX*Room.kSize+0, zoneY*Room.kSize+8, z00a,
                                 zoneX*Room.kSize+0, zoneY*Room.kSize+5, 8,
                                 "Sk", 1);
    spriteManager.addSprite(mStairs00a);
    
    mStairSwitch00a = new ZoneSwitch(zoneX*Room.kSize+0, zoneY*Room.kSize+8, 8);
    spriteManager.addSprite(mStairSwitch00a);    
    
    final int z00b = (mStairs00Done ? 2 : 8);
    mStairs00b = new BlockStairs(zoneX*Room.kSize+0, zoneY*Room.kSize+0, z00b,
                                 zoneX*Room.kSize+0, zoneY*Room.kSize+3, 8,
                                 "Sk", 1);
    spriteManager.addSprite(mStairs00b);
    
    mStairSwitch00b = new ZoneSwitch(zoneX*Room.kSize+0, zoneY*Room.kSize+0, 8);
    spriteManager.addSprite(mStairSwitch00b);
    
    if ( mSnakeDone ) {
      mSnakeBridgeSwitch = null;
    } else {
      mSnakeBridgeSwitch = new FloorSwitch(7, 4, 0, "Pk", "#k");
      spriteManager.addSprite(mSnakeBridgeSwitch);
    }
    
    // zone (0,1)

    zoneX = 0;
    zoneY = 1;

    spriteManager.addSprite(
                new BlockArray(kBlocks01, kBlockColours,
                               zoneX*Room.kSize, zoneY*Room.kSize, 0) );

    // zone (0,2)

    zoneX = 0;
    zoneY = 2;

    spriteManager.addSprite(
                new BlockArray(kBlocks02, kBlockColours,
                               zoneX*Room.kSize, zoneY*Room.kSize, 0) );

    final int z02a = (mStairs02Done ? 6 : 0);
    mStairs02a = new BlockStairs(zoneX*Room.kSize+0, zoneY*Room.kSize+9, z02a,
                                 zoneX*Room.kSize+3, zoneY*Room.kSize+9, 6,
                                 "Sk", 4);
    spriteManager.addSprite(mStairs02a);

    mStairSwitch02a = new ZoneSwitch(zoneX*Room.kSize+0, zoneY*Room.kSize+9);
    spriteManager.addSprite(mStairSwitch02a);
    
    final int z02b = (mStairs02Done ? 0 : 6);
    mStairs02b = new BlockStairs(zoneX*Room.kSize+4, zoneY*Room.kSize+5, z02b,
                                 zoneX*Room.kSize+4, zoneY*Room.kSize+8, 6,
                                 "Sk", 4);
    spriteManager.addSprite(mStairs02b);

    mStairSwitch02b = new ZoneSwitch(zoneX*Room.kSize+4, zoneY*Room.kSize+5);
    spriteManager.addSprite(mStairSwitch02b);
    
    mSwitch02a = new WallSwitch(0, 2, Env.UP, 1, 8, 
                                new String[]{"P7","u7"}, false);
    spriteManager.addSprite(mSwitch02a);

    if ( mSwitch02aDone ) {
      mSwitch02a.setState(1);
    } else {
      kExits[1].mDoor.setClosed(true);
    }
    
    mSwitch02b = new WallSwitch(0, 2, Env.UP, 7, 8, 
                                new String[]{"P7","u7"}, false);
    spriteManager.addSprite(mSwitch02b);

    if ( mSwitch02bDone ) {
      mSwitch02b.setState(1);
    }

    final int z02c = ( mSwitch02bDone ? 6 : 0 );
    mStairs02c = new BlockStairs(zoneX*Room.kSize+0, zoneY*Room.kSize+0, 0,
                                 zoneX*Room.kSize+3, zoneY*Room.kSize+0, z02c,
                                 "#k", 3);
    mStairs02c.setSlopeType(-1);
    spriteManager.addSprite(mStairs02c);
    
    // zone (1,0)

    zoneX = 1;
    zoneY = 0;

    spriteManager.addSprite(
                new BlockArray(kBlocks10, kBlockColours,
                               zoneX*Room.kSize, zoneY*Room.kSize, 0) );
    
    if ( mSwitches10Done < 4 ) {
      final int x = 14 - 4*mSwitches10Done;
      mSwitch10 = new FloorSwitch(zoneX*Room.kSize+x, zoneY*Room.kSize+2,
                                  0, "Pk", "#k");
      spriteManager.addSprite(mSwitch10);
    } else {
      mSwitch10 = null;
    }
    
    mBridges10 = new BlockStairs[4];
    for ( int k = 0 ; k < mBridges10.length ; k++ ) {
      final int x0 = 14 - 4*k - 1,
                x1 = 14 - 4*k - 3,
                z  = ( k < mSwitches10Done ? 0 : -4 );
      mBridges10[k] = new BlockStairs(
                              zoneX*Room.kSize+x0, zoneY*Room.kSize+2, z,
                              zoneX*Room.kSize+x1, zoneY*Room.kSize+2, z,
                              "#k", 1);
      spriteManager.addSprite(mBridges10[k]);
    }

    final int z0 = ( mSnakeDone ? 0 : -4 ),
              z1 = ( mSnakeDone ? 0 : -15 );
    mSnakeBridge = new BlockStairs(zoneX*Room.kSize+2, zoneY*Room.kSize+3, z0,
                                   zoneX*Room.kSize+2, zoneY*Room.kSize+9, z1,
                                   "#k", 1);
    mSnakeBridge.setSlopeType(+1);
    spriteManager.addSprite(mSnakeBridge);
    
    mSnakeBridgeBlock = new Barrier(zoneX*Room.kSize+2, zoneY*Room.kSize+9, 2,
                                    Player.class);
    if ( !mSnakeDone ) spriteManager.addSprite(mSnakeBridgeBlock);
    
    // zone (1,1)

    zoneX = 1;
    zoneY = 1;

    spriteManager.addSprite(
                new BlockArray(kBlocks11, kBlockColours,
                               zoneX*Room.kSize, zoneY*Room.kSize, -2) );

    if ( mSnakeDone ) {
      mSnakeSwitches = null;
      mSnakeSwitchesDone = 16;
    } else {
      spriteManager.addSprite(new SnakeEgg(16, 16, 0, 3));

      mSnakeSwitches = new FloorSwitch[16];
      int k = 0;
      for ( int i = 0 ; i <= 9 ; i += 3 ) {
        for ( int j = 0 ; j <= 9 ; j += 3 ) {
          mSnakeSwitches[k] = new FloorSwitch(zoneX*Room.kSize+i,
                                              zoneY*Room.kSize+j,
                                              0, "#P", "#k");
          mSnakeSwitches[k].freezeState(true);
          spriteManager.addSprite(mSnakeSwitches[k]);
          k++;
        }
      }
      mSnakeSwitchesDone = -1;
    }
  
    // zone (2,0)

    zoneX = 2;
    zoneY = 0;

    spriteManager.addSprite(
                new BlockArray(kBlocks20, kBlockColours,
                               zoneX*Room.kSize, zoneY*Room.kSize, 0) );

    // zone (2,1)

    zoneX = 2;
    zoneY = 1;

    spriteManager.addSprite(
                new BlockArray(kBlocks21, kBlockColours,
                               zoneX*Room.kSize, zoneY*Room.kSize, 0) );

    int z = ( mSnakeDone ? 0 : 4 );
    mChestStairs = new BlockStairs(zoneX*Room.kSize+0, zoneY*Room.kSize+3, z,
                                   zoneX*Room.kSize+3, zoneY*Room.kSize+3, 4,
                                   "#k", 4);
    mChestStairs.setSlopeType(+1);
    spriteManager.addSprite(mChestStairs);
    
    mChest = new Chest(zoneX*Room.kSize+5, zoneY*Room.kSize+2, 4, Env.LEFT);
    spriteManager.addSprite(mChest);    

    FlameArea flames = new FlameArea(zoneX*Room.kSize+3, zoneY*Room.kSize+0,
                                     -2, kFlamePattern);
    flames.warmUp();
    spriteManager.addSprite(flames);

    mEndTimer = 0;
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mStairs00a = mStairs00b = null;
    mStairSwitch00a = mStairSwitch00b = null;
    mStairs02a = mStairs02b = mStairs02c = null;
    mStairSwitch02a = mStairSwitch02b = null;
    mSwitch02a = mSwitch02b = null;
    mSnakeBridgeSwitch = null;
    mSnakeBridge = null;
    mSnakeBridgeBlock = null;
    mSwitch10 = null;
    mBridges10 = null;
    mSnakeSwitches = null;
    mChest = null;
    mChestStairs = null;

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

    // process the story event list
    
    boolean save = false;
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();
      
      if ( event instanceof WallSwitch.EventStateChange ) {
        WallSwitch ws = ((WallSwitch.EventStateChange)event).mSwitch;
        if ( ws == mSwitch02a ) {
          assert( !mSwitch02aDone );
          kExits[1].mDoor.setClosed(false);      
          Env.sounds().play(Sounds.SUCCESS, 3);
          mSwitch02aDone = true;
        } else if ( ws == mSwitch02b ) {
          assert( !mSwitch02bDone );
          mStairs02c.setZEnd(6);
          Env.sounds().play(Sounds.SUCCESS, 6);
          mSwitch02bDone = true;
        } else {
          assert(false);
        }
        it.remove();
      }
      
      if ( event instanceof FloorSwitch.EventStateChange ) {
        FloorSwitch fs = ((FloorSwitch.EventStateChange)event).mSwitch;
        if ( fs == mSnakeBridgeSwitch ) {
          assert( !mSnakeDone );
          mSnakeBridgeSwitch.freezeState(true);
          mSnakeBridge.setZStart(0);
          mSnakeBridge.setZEnd(0);
          spriteManager.removeSprite(mSnakeBridgeBlock);
          Env.sounds().play(Sounds.SWITCH_ON);
        } else if ( fs == mSwitch10 ){
          assert( !mSnakeDone && mSwitches10Done < 4 );
          BlockStairs bridge = mBridges10[mSwitches10Done];
          bridge.setZStart(0);
          bridge.setZEnd(0);
          Env.sounds().play(Sounds.SWITCH_ON);
          mSwitches10Done += 1;
          spriteManager.removeSprite(mSwitch10);
          mSwitch10 = null;
        } else {
          boolean snakeSwitch = false;
          for ( FloorSwitch s : mSnakeSwitches ) {
            if ( fs == s ) snakeSwitch = true;
          }
          if ( snakeSwitch ) {
            assert( !mSnakeDone );
            fs.freezeState(true);
            mSnakeSwitchesDone += 1;
            if ( mSnakeSwitchesDone == 16 ) {
              SnakeBoss3 snake =
                  (SnakeBoss3)spriteManager.findSpriteOfType(SnakeBoss3.class);
              snake.transform();
            } else {
              Env.sounds().play(Sounds.SWITCH_ON);
            }
          } else {
            assert(false);
          }
        }
        it.remove();
      }

      if ( event instanceof ZoneSwitch.EventStateChange ) {
        ZoneSwitch s = ((ZoneSwitch.EventStateChange)event).mSwitch;
        if ( s == mStairSwitch00a ) {
          if ( s.isOn() && mStairs00a.getZStart() == 8 ) {
            Env.sounds().play(Sounds.SWITCH_ON);
            mStairs00a.setZStart(2);
            mStairs00b.setZStart(8);
            mStairs00Done = false;
          }
        } else if ( s == mStairSwitch00b ) {
          if ( s.isOn() && mStairs00b.getZStart() == 8 ) {
            Env.sounds().play(Sounds.SWITCH_ON);
            mStairs00a.setZStart(8);
            mStairs00b.setZStart(2);
            mStairs00Done = true;
          }
        } else if ( s == mStairSwitch02a ) {
          if ( s.isOn() && mStairs02a.getZStart() == 6 ) {
            Env.sounds().play(Sounds.SWITCH_ON);
            mStairs02a.setZStart(0);
            mStairs02b.setZStart(6);
            mStairs02Done = false;
          }
        } else if ( s == mStairSwitch02b ) {
          if ( s.isOn() && mStairs02b.getZStart() == 6 ) {
            Env.sounds().play(Sounds.SWITCH_ON);
            mStairs02a.setZStart(6);
            mStairs02b.setZStart(0);
            mStairs02Done = true;
          }
        } else {
          assert(false);
        }
        it.remove();
      }
      
      if ( event instanceof Snake.EventKilled ) {
        assert( !mSnakeDone );
        mSnakeDone = true;
        mSnakeBridge.setZStart(0);
        mSnakeBridge.setZEnd(0);
        mSnakeBridgeSwitch.freezeState(true);
        save = true;
        spriteManager.removeSprite(mSnakeBridgeBlock);
        it.remove();
      }
      
    } // for (event)

    if ( save ) storyEvents.add(new QuestStory.EventSaveGame());
    
    // enable the bridge switches in zone (1,0)
    if ( mSwitches10Done < 4 && mSwitch10 == null ) {
      assert( !mSnakeDone && mSwitches10Done > 0 );
      BlockStairs bridge = mBridges10[mSwitches10Done-1];
      if ( bridge.getZEnd() == 0 ) {
        final int x = 14 - 4*mSwitches10Done;
        mSwitch10 = new FloorSwitch(Room.kSize+x, 2, 0, "Pk", "#k");
        spriteManager.addSprite(mSwitch10);
      }
    }
    
    // hide the bridge to the snake zone
    if ( !mSnakeDone && mSwitches10Done == 4 && mSnakeBridgeSwitch.isOn() &&
         mPlayer != null && !mPlayer.isActing() ) {
      int x = mPlayer.getXPos(),
          y = mPlayer.getYPos();
      if ( x >= 10 && x < 20 && y >= 10 && y < 20 ) {
        mSnakeBridge.setZStart(-4);
        mSnakeBridge.setZEnd(-15);
        mSnakeBridgeSwitch.unfreezeState();
        spriteManager.addSprite(mSnakeBridgeBlock);
      }
    }
    
    // check the snake switches
    if ( !mSnakeDone ) {
      assert( mSnakeSwitches != null );
      if ( mSnakeSwitchesDone < 0 && mPlayer != null &&
           mPlayer.getXPos() >= 10 && mPlayer.getXPos() < 20 &&
           mPlayer.getYPos() >= 10 && mPlayer.getYPos() < 20 &&
           spriteManager.findSpriteOfType(SnakeBoss3.class) != null ) {
        for ( FloorSwitch s : mSnakeSwitches ) s.unfreezeState();
        mSnakeSwitchesDone = 0;
      }
      if ( mSnakeSwitchesDone >= 0 && mPlayer == null ) {
        for ( FloorSwitch s : mSnakeSwitches ) s.freezeState(true);
        mSnakeSwitchesDone = -1;
      }
    }

    // lower the stairs to the chest
    if ( mSnakeDone && mChestStairs.getZStart() == 4 &&
         mSnakeBridge.getZEnd() == 0 ) {
      mChestStairs.setZStart(0);
    }

    // once the chest is open
    if ( mEndTimer > 0 ) {
      mEndTimer--;
      if ( mEndTimer == kChestSoundDelay ) {
        Env.sounds().play(Sounds.CHEST);        
      } else if ( mEndTimer == kChestOpenDelay ) {
        mChest.setOpen(true);
      } else if ( mEndTimer == 0 ) {
        storyEvents.add(new QuestStory.EventPlayerWins());
      }
    }

    // check for opening the chest
    if ( mEndTimer == 0 && !mChest.isOpen() && mPlayer != null &&
         mPlayer.getXPos() == 23 && mPlayer.getYPos() == 13 ) {
      mPlayer.mAdvanceDisabled = true;
      mEndTimer = kGameEndsDelay;
    }
     
  } // Room.advance()

} // class RoomE02
