/*
 *  RoomE04.java
 *  Copyright (c) 2018 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BitBuffer;
import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.BlockStairs;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Fence;
import com.dishmoth.miniquests.game.FenceGate;
import com.dishmoth.miniquests.game.FloorSwitch;
import com.dishmoth.miniquests.game.Liquid;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.QuestStory;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Snake;
import com.dishmoth.miniquests.game.SnakeEgg;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.WallSwitch;
import com.dishmoth.miniquests.game.ZoneSwitch;

// the room "E04"
public class RoomE04 extends Room {

  // unique identifier for this room
  public static final String NAME = "E04";
  
  // main blocks for zones (0,0) and (0,1)
  private static final String kBlocks01[][] = { { "0    0    ",
                                                  "          ",
                                                  "          ",
                                                  "     0    ",
                                                  "          ",
                                                  "   0   0  ",
                                                  "          ",
                                                  "     0    ",
                                                  "          ",
                                                  "          ",
                                                  "0    0    ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "     0000 ",
                                                  "     0  0 ",
                                                  "     0  0 ",
                                                  "     0000 ",
                                                  "          " } };

  // changing blocks for zones (0,0) and(0,1)
  private static final String kBlockStates01[][][] = { { { "          ",
                                                           "          ",
                                                           " 0000     ",
                                                           "          ",
                                                           "          ",
                                                           "          ",
                                                           "     0    ",
                                                           "000 000 00",
                                                           "     0    ",
                                                           "          ",
                                                           "          ",
                                                           "          ",
                                                           " 0000     ",
                                                           "          ",
                                                           "          ",
                                                           "          ",
                                                           "          ",
                                                           "          ",
                                                           "      00  ",
                                                           "      00  ",
                                                           "          ",
                                                           "          " },
    
                                                         { "          ",
                                                           "          ",
                                                           "          ",
                                                           "          ",
                                                           "          ",
                                                           "          ",
                                                           "          ",
                                                           "0         ",
                                                           "          ",
                                                           "          ",
                                                           "          ",
                                                           "          ",
                                                           "          ",
                                                           "          ",
                                                           "          ",
                                                           "          ",
                                                           "          ",
                                                           "          ",
                                                           "          ",
                                                           "          ",
                                                           "          ",
                                                           "          " }},
  
                                                       { { "     0    ",
                                                           "     0    ",
                                                           "     0    ",
                                                           "          ",
                                                           "0         ",
                                                           "0         ",
                                                           "0  00 00  ",
                                                           "0  0   0  ",
                                                           "0         ",
                                                           "0  0   0  ",
                                                           "0  00 00  ",
                                                           "0         ",
                                                           "0         ",
                                                           "          ",
                                                           "     0    ",
                                                           "     0    ",
                                                           "     0    ",
                                                           "     0    ",
                                                           "00000     ",
                                                           "          ",
                                                           "          ",
                                                           "          ",
                                                           "          " } } };

  // invisible (temporary) blocks for zones (0,0) and (0,1)
  private static final String kBlockBarriers01[][] = { { "          ",
                                                         "          ",
                                                         "          ",
                                                         "    * *   ",
                                                         "   *   *  ",
                                                         "  * * * * ",
                                                         "   *   *  ",
                                                         "    * *   ",
                                                         "          ",
                                                         "          ",
                                                         "          ",
                                                         "          ",
                                                         "          ",
                                                         "          ",
                                                         "     *    ",
                                                         "    *     ",
                                                         "          ",
                                                         "          ",
                                                         "          ",
                                                         "          " } };

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

  // blocks for zone (0,2)
  private static final String kBlocks02[][] = { { "          ",
                                                  "  0   0   ",
                                                  "  0   0   ",
                                                  "000   0000",
                                                  "  0   0   ",
                                                  "  0   0   ",
                                                  "  00000   ",
                                                  "          ",
                                                  "          ",
                                                  "          " } };
  
  // blocks for zone (1,2)
  private static final String kBlocks12[][] = { { "0        0",
                                                  "00000    0",
                                                  "0000000000",
                                                  "0000000000",
                                                  "0000000000",
                                                  "0000000000",
                                                  "      0   ",
                                                  "      0   ",
                                                  "      0   ",
                                                  "      0   " } };
  
  // blocks for zone (2,2)
  private static final String kBlocks22[][] = { { "000    000",
                                                  "000    000",
                                                  "000    000",
                                                  "000    000",
                                                  "000    000",
                                                  "000    000",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          " } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#g",   // green
                                                  "Og" }; // 
  
  // details of exit/entry points for the room 
  private static final Exit kExits[]
          = { new Exit(0,0, Env.LEFT,  4,0, "#g",0, -1, RoomE10.NAME, 0),
              new Exit(0,1, Env.LEFT,  4,4, "#g",2, -1, RoomE09.NAME, 1),
              new Exit(0,2, Env.LEFT,  6,0, "#g",0, -1, RoomE08.NAME, 0),
              new Exit(0,2, Env.UP,    6,8, "#g",0, -1, RoomE03.NAME, 5),
              new Exit(1,2, Env.UP,    4,8, "#g",0, -1, RoomE03.NAME, 4),
              new Exit(2,2, Env.UP,    1,0, "#g",1, -1, RoomE06.NAME, 3),
              new Exit(2,2, Env.UP,    8,0, "#g",1, -1, RoomE06.NAME, 2),
              new Exit(2,2, Env.RIGHT, 5,0, "#g",1, -1, RoomE13.NAME, 1)};

  // which blocks in zone (0,1) are raised (0 or 1)
  private int mState01;
  
  // references to objects in zone (0,1)
  private BlockArray  mBlocks01[];
  private BlockArray  mBarriers01;
  private FloorSwitch mSwitches01[];
  private int         mTimer01;
  
  // flags for zone (0,2)
  private int     mStairs02State;
  private boolean mDoor02Done;
  
  // references to objects in zone (0,2)
  private BlockStairs mStairs02;
  private ZoneSwitch  mSwitch02a,
                      mSwitch02b,
                      mSwitch02c;
  private WallSwitch  mDoorSwitch02;
  
  // flags for zone (1,2)
  private boolean mStairs12Done;

  // flags for zone (1,1)
  private boolean mSnakeDone;
  
  // references to objects in zone (1,2)
  private BlockStairs mStairs12;
  private ZoneSwitch  mStairSwitch12;

  // constructor
  public RoomE04() {

    super(NAME);

    mState01 = 0;
    mStairs02State = 0;
    mDoor02Done = false;
    mSnakeDone = false;
    mStairs12Done = false;
    
  } // constructor

  // serialize the room state by writing bits to the specified buffer
  @Override
  public void save(BitBuffer buffer) {
    
    buffer.write(mState01, 1);
    buffer.write(mStairs02State, 2);
    buffer.writeBit(mDoor02Done);
    buffer.writeBit(mSnakeDone);
    buffer.writeBit(mStairs12Done);
    
  } // Room.save()

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  @Override
  public boolean restore(int version, BitBuffer buffer) { 
    
    if ( buffer.numBitsToRead() < 6 ) return false;
    mState01 = buffer.read(1);
    mStairs02State = buffer.read(2);
    mDoor02Done = buffer.readBit();
    mSnakeDone = buffer.readBit();
    mStairs12Done = buffer.readBit();
    if ( mStairs02State > 2 ) return false;
    return true;
    
  } // Room.restore() 
  
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

    for ( zoneY = 0 ; zoneY <= 2 ; zoneY++ ) {
      for ( zoneX = 0 ; zoneX <= 2 ; zoneX++ ) {
        addBasicZone(zoneX, zoneY, 
                     (zoneX==2), (zoneY==2), (zoneX==0), (zoneY==0),
                     kExits, spriteManager);
        if ( zoneX != 1 || zoneY != 1 ) {
          Liquid gunk = new Liquid(zoneX*Room.kSize, zoneY*Room.kSize, -2, 2);
          gunk.setLethalDepth(2);
          spriteManager.addSprite(gunk);
        }
      }
    }
    
    // zone (0,0) / (0,1)

    zoneX = 0;
    zoneY = 0;
    
    spriteManager.addSprite(
                new BlockArray(kBlocks01, kBlockColours,
                               zoneX*Room.kSize, zoneY*Room.kSize, 0) );
    
    mBlocks01 = new BlockArray[kBlockStates01.length];
    for ( int k = 0 ; k < mBlocks01.length ; k++ ) {
      int z = (k == mState01 ? 0 : -4);
      mBlocks01[k] = new BlockArray(kBlockStates01[k], kBlockColours,
                                    zoneX*Room.kSize, zoneY*Room.kSize, z); 
      spriteManager.addSprite(mBlocks01[k]);
    }

    mBarriers01 = new BlockArray(kBlockBarriers01, kBlockColours,
                                 zoneX*Room.kSize, zoneY*Room.kSize, 2);
    
    final int xy[][] = { {3,14}, {7,14}, {5,12}, {5,16}, {10,14},
                         {5,19}, {5,9}, {0,19}, {0,9},
                         {5,4}, {8,4}, {5,1}, {8,1},
                         {-1,14,4}, {-1,4} };
    mSwitches01 = new FloorSwitch[xy.length];
    for ( int k = 0 ; k < mSwitches01.length ; k++ ) {
      int z = (xy[k].length > 2 ? xy[k][2] : 0);
      mSwitches01[k] = new FloorSwitch(zoneX*Room.kSize + xy[k][0],
                                       zoneY*Room.kSize + xy[k][1],
                                       z,
                                       (k<xy.length-2 ? "#q" : "#g" ), "#g");
      spriteManager.addSprite(mSwitches01[k]);
    }
    if ( mState01 == 0 ) mSwitches01[xy.length-2].freezeState(true);
    if ( mState01 == 1 ) mSwitches01[xy.length-1].freezeState(true);

    mTimer01 = 0;
    
    // zone (1,1)

    zoneX = 1;
    zoneY = 1;
    
    spriteManager.addSprite(
                new BlockArray(kBlocks11, kBlockColours,
                               zoneX*Room.kSize, zoneY*Room.kSize, -2) );

    if ( !mSnakeDone ) {
      spriteManager.addSprite(new SnakeEgg(16, 13, 0, 2));
    }

    // zone (0,2)
    
    zoneX = 0;
    zoneY = 2;

    spriteManager.addSprite(
                new BlockArray(kBlocks02, kBlockColours,
                               zoneX*Room.kSize, zoneY*Room.kSize, 0) );

    int z02a = ( mStairs02State > 1 ? 0 : 8 ),
        z02b = ( mStairs02State > 0 ? 0 : 8 );
    mStairs02 = new BlockStairs(zoneX*Room.kSize+6, zoneY*Room.kSize+9, z02a,
                                zoneX*Room.kSize+2, zoneY*Room.kSize+9, z02b,
                                kBlockColours[1], 5);
    spriteManager.addSprite(mStairs02);
    
    mSwitch02a = new ZoneSwitch(zoneX*Room.kSize+2, zoneY*Room.kSize+9);
    mSwitch02b = new ZoneSwitch(zoneX*Room.kSize+6, zoneY*Room.kSize+9);
    mSwitch02c = new ZoneSwitch(zoneX*Room.kSize+6, zoneY*Room.kSize+10);
    spriteManager.addSprite(mSwitch02a);
    spriteManager.addSprite(mSwitch02b);
    spriteManager.addSprite(mSwitch02c);

    mDoorSwitch02 = new WallSwitch(zoneX, zoneY, Env.UP, 6, 2, 
                                   new String[]{"q7","u7"}, false);
    spriteManager.addSprite(mDoorSwitch02);
    
    if ( mDoor02Done ) {
      mDoorSwitch02.setState(1);
    } else {
      kExits[2].mDoor.setClosed(true);
    }
    
    // zone (1,2)
    
    zoneX = 1;
    zoneY = 2;

    spriteManager.addSprite(
                new BlockArray(kBlocks12, kBlockColours,
                               zoneX*Room.kSize, zoneY*Room.kSize, 0) );

    int z1 = 0,
        z2 = 0;
    if ( mStairs12Done ) {
      z1 = 2;
      z2 = 8;
    } else {
      mStairSwitch12 = new ZoneSwitch(zoneX*Room.kSize+4, zoneY*Room.kSize+9);
      spriteManager.addSprite(mStairSwitch12);
    }
    
    mStairs12 = new BlockStairs(zoneX*Room.kSize+1, zoneY*Room.kSize+9, z1,
                                zoneX*Room.kSize+4, zoneY*Room.kSize+9, z2,
                                "Og", 4);
    mStairs12.setSlopeType(-1);
    spriteManager.addSprite(mStairs12);

    spriteManager.addSprite(new Fence(zoneX*Room.kSize+0, 
                                      zoneY*Room.kSize+4,
                                      0, 6, Env.RIGHT, 1));
    spriteManager.addSprite(new Fence(zoneX*Room.kSize+7, 
                                      zoneY*Room.kSize+4,
                                      0, 6, Env.RIGHT, 1));

    FenceGate gate = new FenceGate(zoneX*Room.kSize+5, 
                                   zoneY*Room.kSize+4, 
                                   0, Env.RIGHT, 1);
    gate.setClosed(!mSnakeDone);
    spriteManager.addSprite(gate);
    
    // zone (2,2)
    
    zoneX = 2;
    zoneY = 2;

    spriteManager.addSprite(
                new BlockArray(kBlocks22, kBlockColours,
                               zoneX*Room.kSize, zoneY*Room.kSize, 0) );

    RoomE13 otherRoom = (RoomE13)findRoom(RoomE13.NAME);
    assert( otherRoom != null );
    if ( !otherRoom.completed() ) {
      kExits[7].mDoor.setClosed(true);
    }
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mBlocks01 = null;
    mBarriers01 = null;
    mSwitches01 = null;
    mStairs02 = null;
    mSwitch02a = mSwitch02b = mSwitch02c = null;
    mDoorSwitch02 = null;
    mStairs12 = null;
    mStairSwitch12 = null;

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
      if ( mTimer01 > 0 ) {
        // wait until the blocks have finished moving
        scroll = null;
      } else if ( !mSnakeDone && mPlayer != null &&
                  mPlayer.getXPos() == 16 && mPlayer.getYPos() < 24 ) {
        // don't scroll away from the snake until it's beaten
        scroll = null;
      } else {
        storyEvents.add(scroll);
      }
    }
    
    // process the story event list
    
    FloorSwitch switch01 = null;
    boolean save = false;
    
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();
      
      if ( event instanceof FloorSwitch.EventStateChange ) {
        FloorSwitch s = ((FloorSwitch.EventStateChange)event).mSwitch;
        for ( FloorSwitch fs : mSwitches01 ) {
          if ( s == fs ) switch01 = s;
        }
        assert(switch01 != null);
        it.remove();
      }
      
      if ( event instanceof ZoneSwitch.EventStateChange ) {
        ZoneSwitch s = ((ZoneSwitch.EventStateChange)event).mSwitch;
        if ( s == mSwitch02a ) {
          if ( s.isOn() && mStairs02State == 0 ) {
            mStairs02.setSlopeType(+1);
            mStairs02.setZEnd(0);
            mStairs02State = 1;
            Env.sounds().play(Sounds.SWITCH_ON);          
          }
        } else if ( s == mSwitch02b ) {
          if ( s.isOn() && mStairs02State == 1 ) {
            mStairs02.setSlopeType(-1);
            mStairs02.setZStart(0);
            mStairs02State = 2;
            Env.sounds().play(Sounds.SWITCH_ON);          
          }
        } else if ( s == mSwitch02c ) {
          if ( s.isOn() && mStairs02State > 0 ) {
            mStairs02.setSlopeType(+1);
            mStairs02.setZStart(8);
            mStairs02.setZEnd(8);
            mStairs02State = 0;
            save = true;
            Env.sounds().play(Sounds.SWITCH_ON);          
          }
        } else if ( s == mStairSwitch12 ) {
          if ( s.isOn() ) {
            mStairs12Done = true;
            mStairs12.setZStart(2);
            mStairs12.setZEnd(8);
            spriteManager.removeSprite(mStairSwitch12);
            mStairSwitch12 = null;
            Env.sounds().play(Sounds.SWITCH_ON);
          }
        } else {
          assert(false);
        }
        it.remove();
      }

      if ( event instanceof WallSwitch.EventStateChange ) {
        assert( !mDoor02Done );
        mDoor02Done = true;
        kExits[2].mDoor.setClosed(false);
        Env.sounds().play(Sounds.SUCCESS, 3);
        it.remove();
      }
      
      if ( event instanceof Snake.EventKilled ) {
        mSnakeDone = true;
        save = true;
        Env.sounds().play(Sounds.SUCCESS);
        it.remove();
      }

    } // for (event)

    // switches and blocks in zone (0,1)
    if ( switch01 != null ) {
      Env.sounds().play(Sounds.SWITCH_ON);
      for ( FloorSwitch fs : mSwitches01 ) {
        if ( fs == switch01 ) fs.freezeState(true);
        else                  fs.unfreezeState();
      }
      mState01 = 1 - mState01;
      final int num = mSwitches01.length;
      if ( mState01 == 0 ) mSwitches01[num-2].freezeState(true);
      if ( mState01 == 1 ) mSwitches01[num-1].freezeState(true);
      if ( mPlayer != null && mPlayer.getXPos() < 0 ) save = true;
      spriteManager.addSprite(mBarriers01);
      mTimer01 = 8;
    }
    if ( mTimer01 > 0 ) {
      mTimer01 -= 1;
      if ( mTimer01 % 2 == 0 ) {
        for ( int k = 0 ; k < mBlocks01.length ; k++ ) {
          int z = mBlocks01[k].getZPos();
          int dz = (k == mState01 ? +1 : -1);
          if ( z + dz <= 0 && z + dz >= -4 ) {
            mBlocks01[k].shiftPos(0, 0, dz);
          }
        }
      }
      if ( mTimer01 == 2 ) {
        spriteManager.removeSprite(mBarriers01);
      }
    }
    
    if ( save ) storyEvents.add(new QuestStory.EventSaveGame());
    
  } // Room.advance()

} // class RoomE04
