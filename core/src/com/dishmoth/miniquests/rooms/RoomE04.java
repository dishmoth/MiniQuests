/*
 *  RoomE04.java
 *  Copyright (c) 2018 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.Barrier;
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
import com.dishmoth.miniquests.game.SnakeC;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.Sprite;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.ZoneSwitch;

// the room "E04"
public class RoomE04 extends Room {

  // unique identifier for this room
  public static final String NAME = "E04";
  
  // blocks for zone (0,0)
  private static final String kBlocks00[][] = { { "  0       ",
                                                  "  0       ",
                                                  "  0       ",
                                                  "  0       ",
                                                  "000       ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          " } };
  // blocks for zone (0,1)
  private static final String kBlocks01[][] = { { "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "000       ",
                                                  "  0       ",
                                                  "  0       ",
                                                  "  0       ",
                                                  "  0       ",
                                                  "  0       " } };
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
  private static final String kBlocks02[][] = { { "    1     ",
                                                  "          ",
                                                  "  1       ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          " },
                                                
                                                { "   1      ",
                                                  "          ",
                                                  "   1      ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          " },
                                                
                                                { "  1       ",
                                                  "          ",
                                                  "    1     ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          " },
                                                
                                                { " 1   0    ",
                                                  " 1   0    ",
                                                  " 1   1    ",
                                                  "01   1    ",
                                                  " 1   1    ",
                                                  " 1   1    ",
                                                  " 1   1    ",
                                                  " 11111    ",
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
          = { new Exit(0,0, Env.LEFT,  5,0, "#g",0, -1, RoomE10.NAME, 0),
              new Exit(0,1, Env.LEFT,  5,0, "#g",0, -1, RoomE09.NAME, 1),
              new Exit(0,2, Env.LEFT,  6,0, "#g",0, -1, RoomE08.NAME, 0),
              new Exit(0,2, Env.UP,    5,0, "#g",1, -1, RoomE03.NAME, 5),
              new Exit(1,2, Env.UP,    4,8, "#g",0, -1, RoomE03.NAME, 4),
              new Exit(2,2, Env.UP,    1,0, "#g",1, -1, RoomE06.NAME, 3),
              new Exit(2,2, Env.UP,    8,0, "#g",1, -1, RoomE06.NAME, 2),
              new Exit(2,2, Env.RIGHT, 6,0, "#g",1, -1, RoomE13.NAME, 1)};

  // details of the paths followed by enemies
  private static final CritterTrack kCritterTrack02
                    = new CritterTrack(new String[]{ " ++++     ",
                                                     " +        ",
                                                     " +++++    ",
                                                     " +   +    ",
                                                     " +   +    ",
                                                     " +   +    ",
                                                     " +   +    ",
                                                     " +++++    ",
                                                     "          ",
                                                     "          " }, 0, 20);
  
  // flags for zone (0,2)
  private boolean mSwitch02Done;
  
  // references to objects in zone (0,2)
  private BlockStairs mPath02;
  private FloorSwitch mSwitch02;
  private int         mCritterTimer02;
  
  // flags for zone (1,2)
  private boolean mStairs12Done;

  // references to objects in zone (1,2)
  private BlockStairs mStairs12;
  private ZoneSwitch  mStairSwitch12;

  // constructor
  public RoomE04() {

    super(NAME);

    mSwitch02Done = false;
    mStairs12Done = false;
    
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
    
    // zone (0,0)

    zoneX = 0;
    zoneY = 0;
    
    spriteManager.addSprite(
                new BlockArray(kBlocks00, kBlockColours,
                               zoneX*Room.kSize, zoneY*Room.kSize, 0) );
    
    // zone (0,1)

    zoneX = 0;
    zoneY = 1;
    
    spriteManager.addSprite(
                new BlockArray(kBlocks01, kBlockColours,
                               zoneX*Room.kSize, zoneY*Room.kSize, 0) );
    
    // zone (1,1)

    zoneX = 1;
    zoneY = 1;
    
    spriteManager.addSprite(
                new BlockArray(kBlocks11, kBlockColours,
                               zoneX*Room.kSize, zoneY*Room.kSize, -2) );

    //spriteManager.addSprite( new SnakeC(3,3,0, Env.DOWN) );
    

    // zone (0,2)
    
    zoneX = 0;
    zoneY = 2;

    spriteManager.addSprite(
                new BlockArray(kBlocks02, kBlockColours,
                               zoneX*Room.kSize, zoneY*Room.kSize, -6) );

    int z02;
    if ( mSwitch02Done ) {
      z02 = 0;
      mSwitch02 = null;
    } else {
      z02 = -4;
      mSwitch02 = new FloorSwitch(zoneX*Room.kSize+5, zoneY*Room.kSize+7, 0,
                                  "gM", "Og");
      spriteManager.addSprite(mSwitch02);
    }

    mPath02 = new BlockStairs(zoneX*Room.kSize+6, zoneY*Room.kSize+7, z02,
                              zoneX*Room.kSize+9, zoneY*Room.kSize+7, z02,
                              kBlockColours[0], 1);
    spriteManager.addSprite(mPath02);
    
    spriteManager.addSprite(new Barrier(zoneX*Room.kSize+3, zoneY*Room.kSize+7,
                                        0, Player.class));
    spriteManager.addSprite(new Barrier(zoneX*Room.kSize+3, zoneY*Room.kSize+9,
                                        0, Player.class));
    
    mCritterTimer02 = 15;
    
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
    gate.setClosed(true);
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
      
      if ( event instanceof FloorSwitch.EventStateChange ) {
        FloorSwitch s = (FloorSwitch)
                        ((FloorSwitch.EventStateChange)event).mSwitch;
        if ( s == mSwitch02 ) {
          assert( !mSwitch02Done );
          mPath02.setZStart(0);
          mPath02.setZEnd(0);
          mSwitch02Done = true;
          s.freezeState(true);
          Env.sounds().play(Sounds.SWITCH_ON);
        } else {
          assert(false);
        }
        it.remove();
      }
      
      if ( event instanceof ZoneSwitch.EventStateChange ) {
        ZoneSwitch s = (ZoneSwitch)
                       ((ZoneSwitch.EventStateChange)event).mSwitch;
        if ( s == mStairSwitch12 ) {
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

    } // for (event)

    // critters in zone (0,2)
    if ( mSwitch02Done ) {
      mCritterTimer02--;
      if ( mCritterTimer02 <= 0 ) {
        Critter c = new Critter(4,29,-6, Env.DOWN, kCritterTrack02);
        c.setColour(3);
        c.easilyKilled(true);
        spriteManager.addSprite(c);
        mCritterTimer02 = 100;
      }
      for ( Sprite s : spriteManager.list() ) {
        if ( s instanceof Critter ) {
          Critter c = (Critter)s;
          if ( c.getXPos() == 2 && c.getYPos() == 27 && !c.isActing() ) {
            spriteManager.removeSprite(c);
            break;
          }
        }
      }
    }
    
  } // Room.advance()

} // class RoomE04
