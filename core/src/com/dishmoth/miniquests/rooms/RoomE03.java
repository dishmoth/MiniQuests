/*
 *  RoomE03.java
 *  Copyright (c) 2018 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.BlockStairs;
import com.dishmoth.miniquests.game.Critter;
import com.dishmoth.miniquests.game.CritterTrack;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Fence;
import com.dishmoth.miniquests.game.FenceGate;
import com.dishmoth.miniquests.game.FloorSwitch;
import com.dishmoth.miniquests.game.Liquid;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Snake;
import com.dishmoth.miniquests.game.SnakeBoss1;
import com.dishmoth.miniquests.game.SnakeEgg;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.Sprite;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.WallSwitch;
import com.dishmoth.miniquests.game.ZoneSwitch;

// the room "E03"
public class RoomE03 extends Room {

  // unique identifier for this room
  public static final String NAME = "E03";
  
  // rate at which the raft tiles move
  private static final int kRaftMoveTime  = 8;
  
  // rate at which critters spawn (zone (2,0))
  private static final int kCritterSpawnTime = 80;

  // blocks for zone (2,0)
  private static final String kBlocks20[][] = { { "           ",
                                                  "           ",
                                                  "           ",
                                                  "           ",
                                                  "           ",
                                                  "           ",
                                                  "           ",
                                                  "0          ",
                                                  "     0     ",
                                                  "           " },
                                                
                                                { "           ",
                                                  "           ",
                                                  "           ",
                                                  "           ",
                                                  "           ",
                                                  "           ",
                                                  "           ",
                                                  " 0         ",
                                                  "      0    ",
                                                  "           " },
                                                
                                                { "           ",
                                                  "           ",
                                                  "           ",
                                                  "           ",
                                                  "           ",
                                                  "           ",
                                                  "           ",
                                                  "  0        ",
                                                  "       0   ",
                                                  "           " },
                                                
                                                { "           ",
                                                  "           ",
                                                  "           ",
                                                  "           ",
                                                  "           ",
                                                  "           ",
                                                  "   000000  ",
                                                  "   0    000",
                                                  "   0    0  ",
                                                  "   0       " },
                                                
                                                { "           ",
                                                  "           ",
                                                  "           ",
                                                  "           ",
                                                  "           ",
                                                  "           ",
                                                  "          0",
                                                  "           ",
                                                  "           ",
                                                  "           " },
                                                                                                
                                                { "           ",
                                                  "           ",
                                                  "           ",
                                                  "           ",
                                                  "          0",
                                                  "          0",
                                                  "           ",
                                                  "           ",
                                                  "           ",
                                                  "           " },
                                                                                                
                                                { "           ",
                                                  "           ",
                                                  "           ",
                                                  "           ",
                                                  "          0",
                                                  "           ",
                                                  "           ",
                                                  "           ",
                                                  "           ",
                                                  "           " } };
  
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
  
  // blocks for zone (2,1)
  private static final String kBlocks21[][] = { { "          ",
                                                  "          ",
                                                  "000   0000",
                                                  "000   0000",
                                                  "000   0000",
                                                  "000   0000",
                                                  "000   0000",
                                                  "000   0000",
                                                  "000   0000",
                                                  "          " } };  
  
  // blocks for zone (1,2)
  private static final String kBlocks12[][] = { { " 0   0  00",
                                                  "        0 ",
                                                  "        0 ",
                                                  "        0 ",
                                                  "        0 ",
                                                  "        0 ",
                                                  "    00000 ",
                                                  "          ",
                                                  "          ",
                                                  "          " } };  
  
  // blocks for zone (2,2)
  private static final String kBlocks22[][] = { { "00        ",
                                                  "          ",
                                                  "          ",
                                                  "         0",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          " },
  
                                                { "          ",
                                                  "          ",
                                                  "         0",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "   1111   ",
                                                  "   1  1   ",
                                                  "   1  1   ",
                                                  "   1111   " },
  
                                                { "          ",
                                                  "         0",
                                                  "          ",
                                                  " 1111     ",
                                                  " 1  1     ",
                                                  " 1  1     ",
                                                  " 1111     ",
                                                  "          ",
                                                  "       0  ",
                                                  "          " },
  
                                                { "  11110000",
                                                  "  1  1    ",
                                                  "  1  1    ",
                                                  "  1111    ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "        0 ",
                                                  "          " },
  
                                                { "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "         0",
                                                  "          " } };
  
  // block pattern for the raft
  private static final String kBlocksRaft[][] = { { "222", "222", "222" } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#h",
                                                  "Nh",
                                                  "Hh"}; // 
  
  // details of exit/entry points for the room 
  private static final Exit kExits[]
          = { new Exit(2,2, Env.UP,    1,0, "#h",0, -1, RoomE05.NAME, 1),
              new Exit(2,2, Env.RIGHT, 6,0, "#h",0, -1, RoomE02.NAME, 2),
              new Exit(2,2, Env.RIGHT, 1,8, "#h",0, -1, RoomE02.NAME, 3),
              new Exit(2,1, Env.RIGHT, 4,0, "#h",0, -1, RoomE02.NAME, 4),
              new Exit(2,0, Env.DOWN,  2,0, "#h",0, -1, RoomE04.NAME, 0) };

  // details of the paths followed by enemies
  private static final CritterTrack kCritterTrack20
                    = new CritterTrack(new String[]{ "   ++++++",
                                                     "++++    +",
                                                     "     ++++" }, 19, 1); 
  private static final CritterTrack kCritterTrack22
                    = new CritterTrack(new String[]{ "  ++++    ",
                                                     "  +  +    ",
                                                     "  +  +    ",
                                                     " +++++    ",
                                                     " +  +     ",
                                                     " +  +     ",
                                                     " ++++++   ",
                                                     "   +  +   ",
                                                     "   +  +   ",
                                                     "   ++++   " }, 20, 20); 
  
  // flags for zone (1,1)
  private boolean mSnakeDone;
  
  // references to objects in zone (2,0)
  private BlockStairs mStairs20;
  private ZoneSwitch  mStairSwitch20;
  private int         mCritterTimer20;
  
  // references to objects in zone (1,2)
  private BlockStairs mStairs12;
  private ZoneSwitch  mStairSwitch12;
  private WallSwitch  mSwitches12[];

  // flags for zone (1,2)
  private boolean mRaftDone;
  
  // reference to objects in zone (2,1)
  private FenceGate   mGate21a,
                      mGate21b;
  private FloorSwitch mGateSwitch21;
  
  // flags for zone (2,1)
  private boolean mGatesDone;
  
  // references to objects in zone (2,2)
  private FloorSwitch mRaftSwitch22;
  private BlockArray  mRaft;
  private int         mRaftTimer;
  private boolean     mRaftForward;
  
  // constructor
  public RoomE03() {

    super(NAME);

    mRaftDone = false;
    mGatesDone = false;
    mSnakeDone = false;
    
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

    // zone (2,0)

    zoneX = 2;
    zoneY = 0;

    spriteManager.addSprite(
                new BlockArray(kBlocks20, kBlockColours,
                               zoneX*Room.kSize-1, zoneY*Room.kSize, -6) );

    mStairs20 = new BlockStairs(zoneX*Room.kSize+6, zoneY*Room.kSize+6, 6,
                                zoneX*Room.kSize+9, zoneY*Room.kSize+6, 6,
                                "Nh", 1);
    spriteManager.addSprite(mStairs20);

    mStairSwitch20 = new ZoneSwitch(zoneX*Room.kSize+6, zoneY*Room.kSize+6);
    spriteManager.addSprite(mStairSwitch20);
    
    mCritterTimer20 = kCritterSpawnTime/2;
    
    // zone (1,1)

    zoneX = 1;
    zoneY = 1;

    spriteManager.addSprite(
                new BlockArray(kBlocks11, kBlockColours,
                               zoneX*Room.kSize, zoneY*Room.kSize, -2) );
    
    spriteManager.addSprite(new SnakeEgg(13, 13, 0, 1));
    
    // zone (2,1)

    zoneX = 2;
    zoneY = 1;

    spriteManager.addSprite(
                new BlockArray(kBlocks21, kBlockColours,
                               zoneX*Room.kSize, zoneY*Room.kSize, 0) );
    
    spriteManager.addSprite(new Fence(zoneX*Room.kSize+0,
                                      zoneY*Room.kSize+1,
                                      0, 3, Env.UP, 1));
    spriteManager.addSprite(new Fence(zoneX*Room.kSize+0,
                                      zoneY*Room.kSize+5,
                                      0, 3, Env.UP, 1));
    mGate21a = new FenceGate(zoneX*Room.kSize+0, 
                             zoneY*Room.kSize+3, 
                             0, Env.UP, 1);
    spriteManager.addSprite(mGate21a);

    spriteManager.addSprite(new Fence(zoneX*Room.kSize+6,
                                      zoneY*Room.kSize+1,
                                      0, 3, Env.UP, 1));
    spriteManager.addSprite(new Fence(zoneX*Room.kSize+6,
                                      zoneY*Room.kSize+5,
                                      0, 3, Env.UP, 1));
    mGate21b = new FenceGate(zoneX*Room.kSize+6, 
                             zoneY*Room.kSize+3, 
                             0, Env.UP, 1);
    spriteManager.addSprite(mGate21b);

    if ( !mGatesDone ) {
      mGateSwitch21 = new FloorSwitch(zoneX*Room.kSize+8,
                                      zoneY*Room.kSize+2,
                                      0, "#c", "#h");
      spriteManager.addSprite(mGateSwitch21);
      mGate21a.setClosed(true);
      mGate21b.setClosed(true);
    } else {
      mGateSwitch21 = null;
    }
    
    RoomE02 adjacentRoom = (RoomE02)findRoom(RoomE02.NAME);
    assert( adjacentRoom != null );
    if ( !adjacentRoom.door4Open() ) {
      kExits[3].mDoor.setClosed(true);
    }
    
    // zone (1,2)

    zoneX = 1;
    zoneY = 2;

    spriteManager.addSprite(
                new BlockArray(kBlocks12, kBlockColours,
                               zoneX*Room.kSize, zoneY*Room.kSize, 0) );

    mStairs12 = new BlockStairs(zoneX*Room.kSize+3, zoneY*Room.kSize+3, 0,
                                zoneX*Room.kSize+3, zoneY*Room.kSize+7, 8,
                                "Hh", 1);
    spriteManager.addSprite(mStairs12);
    
    mStairSwitch12 = new ZoneSwitch(zoneX*Room.kSize+3, zoneY*Room.kSize+7);
    spriteManager.addSprite(mStairSwitch12);

    mSwitches12 = new WallSwitch[4];
    for ( int k = 0 ; k < mSwitches12.length ; k++ ) {
      final int zPos = 2 + 2*k;
      WallSwitch ws = new WallSwitch(zoneX, zoneY, Env.UP, 3, zPos, 
                                      new String[]{"c7","u7"}, false);
      if ( mRaftDone ) ws.setState(1); 
      mSwitches12[k] = ws;
      spriteManager.addSprite(ws);
    }
    
    // zone (2,2)

    zoneX = 2;
    zoneY = 2;

    spriteManager.addSprite(
                new BlockArray(kBlocks22, kBlockColours,
                               zoneX*Room.kSize, zoneY*Room.kSize, 0) );

    Critter critters[] = new Critter[] 
             { new Critter(23,20,2, Env.RIGHT, kCritterTrack22),
               new Critter(21,25,4, Env.UP,    kCritterTrack22),
               new Critter(24,24,4, Env.DOWN,  kCritterTrack22),
               new Critter(25,29,6, Env.LEFT,  kCritterTrack22) };
    for ( int k = 0 ; k < critters.length ; k++ ) {
      critters[k].easilyKilled(true);
      critters[k].setColour(1);
      spriteManager.addSprite(critters[k]);
    }    
    
    mRaft = new BlockArray(kBlocksRaft, kBlockColours,
                           zoneX*Room.kSize+3, zoneY*Room.kSize-2,
                           (mRaftDone ? 0 : -4));
    spriteManager.addSprite(mRaft);
    
    mRaftTimer = kRaftMoveTime;
    mRaftForward = true;
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

  } // Room.discardResources()
  
  // move the raft, return true if the player is on it
  private boolean updateRaft() {

    assert(mRaftDone);
    
    if ( mRaftTimer > 0 ) {
      mRaftTimer--;
      return false;
    }

    final int raftSize = 3;

    boolean playerOnboard = false;
    if ( mPlayer != null ) {
      final int dx = mPlayer.getXPos() - mRaft.getXPos(),
                dy = mPlayer.getYPos() - mRaft.getYPos();
      playerOnboard = ( dx >= 0 && dx < raftSize &&
                        dy >= 0 && dy < raftSize );
    }
    
    final int yStart = 18,
              yEnd   = 5;
    if ( mRaft.getZPos() < 0 ) {
      mRaft.shiftPos(0, 0, 1);
      mRaftTimer = kRaftMoveTime;
      if ( mRaft.getZPos() == 0 ) {
        Env.sounds().play(Sounds.SUCCESS);
        mRaftTimer *= 2;
      }
    } else if ( mRaftForward ) {
      mRaft.shiftPos(0, -1, 0);
      if ( playerOnboard ) mPlayer.slidePos(Env.DOWN, 1);
      if ( mRaft.getYPos() == yEnd ) mRaftForward = false;
      mRaftTimer = kRaftMoveTime;
    } else {
      mRaft.shiftPos(0, +1, 0);
      if ( playerOnboard ) mPlayer.slidePos(Env.UP, 1);
      if ( mRaft.getYPos() == yStart ) mRaftForward = true;
      mRaftTimer = kRaftMoveTime;
    }
  
    return playerOnboard;
    
  } // updateRaft()
  
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

    // check for scrolling (might be overridden by raft movement later)
    
    EventRoomScroll scroll = checkHorizontalScroll();

    // process the story event list
    
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();
      
      if ( event instanceof FloorSwitch.EventStateChange ) {
        FloorSwitch s = (FloorSwitch)
                        ((FloorSwitch.EventStateChange)event).mSwitch;
        s.freezeState(true);
        Env.sounds().play(Sounds.SWITCH_ON);
        if ( s == mRaftSwitch22 ) {
          mRaftDone = true;
        } else if ( s == mGateSwitch21 ) {
          mGatesDone = true;
          mGate21a.setClosed(false);
          mGate21b.setClosed(false);
        } else {
          assert(false);
        }
        it.remove();
      }
      
      if ( event instanceof WallSwitch.EventStateChange ) {
        if ( !mRaftDone ) {
          boolean allDone = true;
          for ( WallSwitch ws : mSwitches12 ) {
            if ( ws.getState() == 0 ) allDone = false;
          }
          if ( allDone ) {
            mRaftSwitch22 = new FloorSwitch(21, 29, 0, "#c", "#h");
            spriteManager.addSprite(mRaftSwitch22);
            Env.sounds().play(Sounds.SUCCESS);
          }
        }
        it.remove();
      }
      
      if ( event instanceof ZoneSwitch.EventStateChange ) {
        ZoneSwitch s = (ZoneSwitch)
                       ((ZoneSwitch.EventStateChange)event).mSwitch;
        if ( s == mStairSwitch20 ) {
          if ( s.isOn() ) {
            mStairs20.setZStart(0);
            Env.sounds().play(Sounds.SWITCH_ON);
          } else {
            mStairs20.setZStart(6);
            Env.sounds().play(Sounds.SWITCH_OFF);        
          }
        } else if ( s == mStairSwitch12 ) {
          if ( s.isOn() ) {
            mStairs12.setZEnd(0);
            Env.sounds().play(Sounds.SWITCH_ON);
          } else {
            mStairs12.setZEnd(8);
            Env.sounds().play(Sounds.SWITCH_OFF);        
          }
        } else {
          assert(false);
        }
        it.remove();
      }
      
      if ( event instanceof Snake.EventKilled ) {
        mSnakeDone = true;
        mGate21a.setClosed(false);
        mGate21b.setClosed(false);
        it.remove();
      }
      
    } // for (event)

    // kill and spawn critters in zone (2,0)
    
    if ( --mCritterTimer20 <= 0 ) {
      mCritterTimer20 = kCritterSpawnTime;
      Critter crit = new Critter(24,1,-4, Env.RIGHT, kCritterTrack20);
      crit.easilyKilled(true);
      crit.setColour(1);
      spriteManager.addSprite(crit);
    }
    for ( Sprite s : spriteManager.list() ) {
      if ( s instanceof Critter ) {
        Critter c = (Critter)s;
        if ( c.getXPos() == 19 && c.getYPos() == 2 && !c.isActing() ) {
          spriteManager.removeSprite(c);
          break;
        }
      }
    }
    
    // move the raft (and check for scrolling)

    if ( mRaftDone ) {
      final boolean playerOnRaft = updateRaft();
      if ( playerOnRaft && !mPlayer.isActing() ) {
        final int x = mPlayer.getXPos() - mCamera.xPos(),
                  y = mPlayer.getYPos() - mCamera.yPos();
        if      ( x >= kSize ) scroll = new EventRoomScroll(+kSize, 0, 0);
        else if ( y >= kSize ) scroll = new EventRoomScroll(0, +kSize, 0);
        else if ( x < 0 )      scroll = new EventRoomScroll(-kSize, 0, 0);
        else if ( y < 0 )      scroll = new EventRoomScroll(0, -kSize, 0);
        else                   scroll = null;
      }
    }

    // update the snake boss
    
    if ( !mSnakeDone && mGatesDone ) {
      SnakeEgg egg = (SnakeEgg)spriteManager.findSpriteOfType(SnakeEgg.class);
      if ( egg != null && mPlayer != null && !mPlayer.isActing() &&
           mPlayer.getXPos() == 19 && mPlayer.getYPos() == 14 ) {
        if ( !mGate21a.isClosed() || !mGate21b.isClosed() ) {
          mGate21a.setClosed(true);
          mGate21b.setClosed(true);
          Env.sounds().play(Sounds.GATE);
        }
      } else if ( egg != null && mPlayer != null &&
                  (mPlayer.getXPos() >= 20 || mPlayer.getXPos() < 10 ||
                   mPlayer.getYPos() >= 20 || mPlayer.getYPos() < 10) ){
        if ( mGate21a.isClosed() || mGate21b.isClosed() ) {
          mGate21a.setClosed(false);
          mGate21b.setClosed(false);
        }
      }
    }
    
    // finalize scrolling
    
    if ( scroll != null ) {
      storyEvents.add(scroll);
    }
    
  } // Room.advance()

} // class RoomE03
