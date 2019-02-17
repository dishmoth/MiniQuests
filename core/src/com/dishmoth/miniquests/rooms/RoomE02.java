/*
 *  RoomE02.java
 *  Copyright (c) 2018 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.BlockStairs;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.FloorSwitch;
import com.dishmoth.miniquests.game.Liquid;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.SnakeB;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.WallSwitch;

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
                                                  "0 11      ",
                                                  "0         ",
                                                  "0    0000 ",
                                                  "0    0  0 ",
                                                  "000000  0 " },
                                                
                                                { "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "  11      ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          " },
                                                
                                                { "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "  11      ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          " },
                                                
                                                { "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "  11      ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          " },
                                                
                                                { "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "1111      ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          " } };  
    
  // blocks for zone (0,1)
  private static final String kBlocks01[][] = { { "0000      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "0  0      ",
                                                  "0  0      ",
                                                  "0  0      ",
                                                  "0  0      ",
                                                  "0  0      ",
                                                  "0  0      ",
                                                  "0         " },
                                                
                                                { " 000      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "          " },
                                                
                                                { "  00      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "          " },
                                                
                                                { "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "  00      ",
                                                  "          " },
                                                
                                                { "          ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "   0      ",
                                                  "0000      ",
                                                  "          " } };  
    
  // blocks for zone (0,2)
  private static final String kBlocks02[][] = { { "    1     ",
                                                  "0         ",
                                                  "0    000  ",
                                                  "0         ",
                                                  "0         ",
                                                  "00000     ",
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
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#k",   //
                                                  "Sk" }; // 
  
  // details of exit/entry points for the room
  private static final Exit kExits[]
          = { new Exit(0,2, Env.UP,   4,6, "#k",0, -1, RoomE01.NAME, 1),
              new Exit(0,2, Env.LEFT, 6,0, "#k",0, -1, RoomE05.NAME, 0),
              new Exit(0,1, Env.LEFT, 6,0, "#k",0, -1, RoomE03.NAME, 1),
              new Exit(0,1, Env.LEFT, 1,8, "#k",1, -1, RoomE03.NAME, 2),
              new Exit(0,0, Env.LEFT, 4,0, "#k",0, -1, RoomE03.NAME, 3), 
              new Exit(0,0, Env.DOWN, 8,0, "#k",0, -1, RoomE09.NAME, 0) };
  
  // references to objects in zone (0,0)
  private BlockStairs mStairs00a,
                      mStairs00b;
  private FloorSwitch mSwitch00;
  
  // flags for zone (0,0)
  private boolean mSwitch00Done;
  private boolean mStairs00Done;
  
  // references to objects in zone (0,2) 
  private BlockStairs mStairs02a,
                      mStairs02b,
                      mBridge02;
  private WallSwitch  mSwitch02a,
                      mSwitch02b;
  
  // flags for zone (0,2)
  private boolean mSwitch02aDone,
                  mSwitch02bDone,
                  mStairs02Done;
  
  // set of snake floor switches
  private FloorSwitch mSnakeSwitches[];

  // whether the snake floor switches are completed
  private boolean mSnakeSwitchesDone;
  
  // constructor
  public RoomE02() {

    super(NAME);
    
    mSwitch00Done = mStairs00Done = false;
    mSwitch02aDone = mSwitch02bDone = mStairs02Done = false;

    mSnakeSwitchesDone = false;
    
  } // constructor

  // whether the door to room E02 is open yet
  // (note: this function may be called by room E02)
  public boolean door4Open() { return mSwitch00Done; }
  
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
    
    final int z00b = (mStairs00Done ? 2 : 8);
    mStairs00b = new BlockStairs(zoneX*Room.kSize+0, zoneY*Room.kSize+0, z00b,
                                 zoneX*Room.kSize+0, zoneY*Room.kSize+3, 8,
                                 "Sk", 1);
    spriteManager.addSprite(mStairs00b);
    
    if ( mSwitch00Done ) {
      mSwitch00 = null;
    } else {
      mSwitch00 = new FloorSwitch(3, 4, 8, "Pk", "Sk");
      spriteManager.addSprite(mSwitch00);
      kExits[4].mDoor.setClosed(true);
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

    final int z02a = (mStairs02Done ? 0 : -6);
    mStairs02a = new BlockStairs(zoneX*Room.kSize+0, zoneY*Room.kSize+9, z02a,
                                 zoneX*Room.kSize+3, zoneY*Room.kSize+9, 0,
                                 "Sk", 4);
    spriteManager.addSprite(mStairs02a);

    final int z02b = (mStairs02Done ? -6 : 0);
    mStairs02b = new BlockStairs(zoneX*Room.kSize+4, zoneY*Room.kSize+5, z02b,
                                 zoneX*Room.kSize+4, zoneY*Room.kSize+8, 0,
                                 "Sk", 4);
    spriteManager.addSprite(mStairs02b);

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

    final int z02c = ( mSwitch02bDone ? 0 : -3 );
    mBridge02 = new BlockStairs(zoneX*Room.kSize+0, zoneY*Room.kSize+0, z02c,
                                zoneX*Room.kSize+0, zoneY*Room.kSize+3, z02c,
                                "#k", 1);
    spriteManager.addSprite(mBridge02);
    
    // zone (1,1)

    zoneX = 1;
    zoneY = 1;

    spriteManager.addSprite(
                new BlockArray(kBlocks11, kBlockColours,
                               zoneX*Room.kSize, zoneY*Room.kSize, -2) );

    //spriteManager.addSprite(
    //            new SnakeB(zoneX*Room.kSize+3, zoneY*Room.kSize+3, 0,
    //                       Env.DOWN) );
    
    if ( !mSnakeSwitchesDone ) {
      mSnakeSwitches = new FloorSwitch[16];
      int k = 0;
      for ( int i = 0 ; i <= 9 ; i += 3 ) {
        for ( int j = 0 ; j <= 9 ; j += 3 ) {
          mSnakeSwitches[k++] = new FloorSwitch(zoneX*Room.kSize+i,
                                                  zoneY*Room.kSize+j,
                                                  0, "#S", "#k");
        }
      }
      for ( FloorSwitch s : mSnakeSwitches ) spriteManager.addSprite(s);
    }
  
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

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
          mBridge02.setZStart(0);
          mBridge02.setZEnd(0);
          Env.sounds().play(Sounds.SUCCESS, 6);
          mSwitch02bDone = true;
        } else {
          assert(false);
        }
        it.remove();
      }
      
      if ( event instanceof FloorSwitch.EventStateChange ) {
        FloorSwitch fs = ((FloorSwitch.EventStateChange)event).mSwitch;
        if ( fs == mSwitch00 ) {
          assert( !mSwitch00Done );
          mSwitch00.freezeState(true);
          kExits[4].mDoor.setClosed(false);      
          Env.sounds().play(Sounds.SUCCESS, 3);
          mSwitch00Done = true;
        } else {
          assert(false);
        }
        it.remove();
      }

      if ( event instanceof Player.EventKilled ) {
        if ( !mSnakeSwitchesDone ) {
          for ( FloorSwitch s : mSnakeSwitches ) s.unfreezeState();
        }
      }
      
    } // for (event)

    // check the stairs
    
    if ( mPlayer != null ) {
      if ( mPlayer.getXPos() == 0 && mPlayer.getYPos() == 8 &&
           mPlayer.getZPos() == 8 && !mStairs00a.moving() && 
           mStairs00a.getZStart() == 8 ) {
        Env.sounds().play(Sounds.SWITCH_ON);
        mStairs00a.setZStart(2);
        mStairs00b.setZStart(8);
        mStairs00Done = false;
      }
      if ( mPlayer.getXPos() == 0 && mPlayer.getYPos() == 0 &&
           mPlayer.getZPos() == 8 && !mStairs00b.moving() &&
           mStairs00b.getZStart() == 8 ) {
        Env.sounds().play(Sounds.SWITCH_ON);
        mStairs00a.setZStart(8);
        mStairs00b.setZStart(2);
        mStairs00Done = true;
      }

      if ( mPlayer.getXPos() == 0 && mPlayer.getYPos() == 29 &&
           mPlayer.getZPos() == 6 && !mStairs02a.moving() &&
           mStairs02a.getZStart() == 0 ) {
        Env.sounds().play(Sounds.SWITCH_ON);
        mStairs02a.setZStart(-6);
        mStairs02b.setZStart(0);
        mStairs02Done = false;
      }
      if ( mPlayer.getXPos() == 4 && mPlayer.getYPos() == 25 &&
           mPlayer.getZPos() == 6 && !mStairs02b.moving() &&
           mStairs02b.getZStart() == 0 ) {
        Env.sounds().play(Sounds.SWITCH_ON);
        mStairs02a.setZStart(0);
        mStairs02b.setZStart(-6);
        mStairs02Done = true;
      }
    }
    
    // check the snake switches
    
    if ( !mSnakeSwitchesDone ) {
      boolean done = true;
      for ( FloorSwitch s : mSnakeSwitches ) {
        if ( !s.isOn() ) {
          done = false;
          break;
        }
      }
      if ( done ) {
        mSnakeSwitchesDone = true;
        SnakeB snake = (SnakeB)spriteManager.findSpriteOfType(SnakeB.class);
        snake.kill();
      }
    }

  } // Room.advance()

} // class RoomE02
