/*
 *  RoomE03.java
 *  Copyright (c) 2018 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
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
import com.dishmoth.miniquests.game.SnakeB;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.Room.EventRoomScroll;

// the room "E03"
public class RoomE03 extends Room {

  // unique identifier for this room
  public static final String NAME = "E03";
  
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
                                                  "00    0000",
                                                  "00    0000",
                                                  "00    0000",
                                                  "00    0000",
                                                  "00    0000",
                                                  "00    0000",
                                                  "00    0000",
                                                  "          " } };  
  
  // blocks for zone (1,2)
  private static final String kBlocks12[][] = { { "        00",
                                                  "        0 ",
                                                  "        0 ",
                                                  "        0 ",
                                                  "        0 ",
                                                  "  0  0  0 ",
                                                  "  0000000 ",
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
  
  //
  private static final String kBlocksSide[][] = { { "      0000",
                                                    "      0000",
                                                    "      0000",
                                                    "      0000",
                                                    "      0000",
                                                    "      0000",
                                                    "      0000",
                                                    "      0000",
                                                    "      0000",
                                                    "      0000" } };  
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#h",
                                                  "Nh" }; // 
  
  // details of exit/entry points for the room 
  private static final Exit kExits[]
          = { new Exit(2,2, Env.UP,    1,0, "#h",0, -1, RoomE02.NAME, 1),
              new Exit(2,2, Env.RIGHT, 6,0, "#h",0, -1, RoomE02.NAME, 2),
              new Exit(2,2, Env.RIGHT, 1,8, "#h",0, -1, RoomE02.NAME, 3),
              new Exit(2,1, Env.RIGHT, 4,0, "#h",0, -1, RoomE02.NAME, 4), 
              new Exit(2,0, Env.DOWN,  7,0, "#h",0, -1, RoomE04.NAME, 0),
              new Exit(2,0, Env.RIGHT, 5,0, "#h",0, -1, RoomE09.NAME, 1) };

  // details of the paths followed by enemies
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
  
  // whether the floor switches are completed
  private boolean mSwitchesDone;
  
  // set of floor switches
  private FloorSwitch mSwitches[];

  // constructor
  public RoomE03() {

    super(NAME);

    mSwitchesDone = false;
    
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
          spriteManager.addSprite(new Liquid(zoneX*Room.kSize,
                                             zoneY*Room.kSize,
                                             -2, 2));
        }
      }
    }

    // zone (2,0)

    zoneX = 2;
    zoneY = 0;

    spriteManager.addSprite(
                new BlockArray(kBlocksSide, kBlockColours,
                               zoneX*Room.kSize, zoneY*Room.kSize, 0) );
    
    // zone (1,1)

    zoneX = 1;
    zoneY = 1;

    spriteManager.addSprite(
                new BlockArray(kBlocks11, kBlockColours,
                               zoneX*Room.kSize, zoneY*Room.kSize, -2) );
    
    //spriteManager.addSprite( new SnakeB(3,3,0, Env.DOWN) );
    
    if ( !mSwitchesDone ) {
      mSwitches = new FloorSwitch[16];
      int k = 0;
      for ( int i = 0 ; i <= 9 ; i += 3 ) {
        for ( int j = 0 ; j <= 9 ; j += 3 ) {
          mSwitches[k++] = new FloorSwitch(zoneX*Room.kSize+i,
                                           zoneY*Room.kSize+j,
                                           0, "#H", "#h");
        }
      }
      for ( FloorSwitch s : mSwitches ) spriteManager.addSprite(s);
    }
  
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
    FenceGate gate1 = new FenceGate(zoneX*Room.kSize+0, 
                                    zoneY*Room.kSize+3, 
                                    0, Env.UP, 1);
    gate1.setClosed(true);
    spriteManager.addSprite(gate1);

    spriteManager.addSprite(new Fence(zoneX*Room.kSize+6,
                                      zoneY*Room.kSize+1,
                                      0, 3, Env.UP, 1));
    spriteManager.addSprite(new Fence(zoneX*Room.kSize+6,
                                      zoneY*Room.kSize+5,
                                      0, 3, Env.UP, 1));
    FenceGate gate2 = new FenceGate(zoneX*Room.kSize+6, 
                                    zoneY*Room.kSize+3, 
                                    0, Env.UP, 1);
    gate2.setClosed(true);
    spriteManager.addSprite(gate2);

    spriteManager.addSprite(new FloorSwitch(zoneX*Room.kSize+8,
                                            zoneY*Room.kSize+2,
                                            0, "#D", "#2"));
    
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
        s.freezeState(true);
        Env.sounds().play(Sounds.SWITCH_ON);
        it.remove();
      }
      
      if ( event instanceof Player.EventKilled ) {
        if ( !mSwitchesDone ) {
          for ( FloorSwitch s : mSwitches ) s.unfreezeState();
        }
      }
      
    } // for (event)

    // check the switches
    
    if ( !mSwitchesDone ) {
      boolean done = true;
      for ( FloorSwitch s : mSwitches ) {
        if ( !s.isOn() ) {
          done = false;
          break;
        }
      }
      if ( done ) {
        mSwitchesDone = true;
        SnakeB snake = (SnakeB)spriteManager.findSpriteOfType(SnakeB.class);
        snake.kill();
      }
    }
    
  } // Room.advance()

} // class RoomE03
