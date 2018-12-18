/*
 *  RoomE02.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
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
import com.dishmoth.miniquests.game.Room.EventRoomScroll;

// the room "E02"
public class RoomE02 extends Room {

  // unique identifier for this room
  public static final String NAME = "E02";
  
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
  
  // blocks for zone (2,2)
  private static final String kBlocks22[][] = { { "      0000",
                                                  "      0000",
                                                  "      0000",
                                                  "      0000",
                                                  "      0000",
                                                  "      0000",
                                                  "      0000",
                                                  "      0000",
                                                  "      0000",
                                                  "      0000" },
  
                                                { "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "      0000",
                                                  "          " },
  
                                                { "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "       000",
                                                  "          " },
  
                                                { "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "        00",
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
  private static final String kBlockColours[] = { "#h" }; // 
  
  // details of exit/entry points for the room 
  private static final Exit kExits[]
          = { new Exit(2,2, Env.RIGHT, 6,0, "#h",0, -1, RoomE01.NAME, 2),
              new Exit(2,2, Env.RIGHT, 1,8, "#h",0, -1, RoomE01.NAME, 3),
              new Exit(2,1, Env.RIGHT, 4,0, "#h",0, -1, RoomE01.NAME, 4), 
              new Exit(2,0, Env.DOWN,  7,0, "#h",0, -1, RoomE03.NAME, 0),
              new Exit(2,0, Env.RIGHT, 5,0, "#h",0, -1, RoomE09.NAME, 1)};

  // whether the floor switches are completed
  private boolean mSwitchesDone;
  
  // set of floor switches
  private FloorSwitch mSwitches[];

  // constructor
  public RoomE02() {

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
                                           0, "#i", "#h");
        }
      }
      for ( FloorSwitch s : mSwitches ) spriteManager.addSprite(s);
    }
  
    // zone (2,1)

    zoneX = 2;
    zoneY = 1;

    spriteManager.addSprite(
                new BlockArray(kBlocksSide, kBlockColours,
                               zoneX*Room.kSize, zoneY*Room.kSize, 0) );
    
    // zone (2,2)

    zoneX = 2;
    zoneY = 2;

    spriteManager.addSprite(
                new BlockArray(kBlocks22, kBlockColours,
                               zoneX*Room.kSize, zoneY*Room.kSize, 0) );
    
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

} // class RoomE02
