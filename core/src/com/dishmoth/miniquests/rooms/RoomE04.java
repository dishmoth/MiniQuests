/*
 *  RoomE04.java
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
import com.dishmoth.miniquests.game.Fence;
import com.dishmoth.miniquests.game.FenceGate;
import com.dishmoth.miniquests.game.Liquid;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.SnakeC;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.ZoneSwitch;

// the room "E04"
public class RoomE04 extends Room {

  // unique identifier for this room
  public static final String NAME = "E04";
  
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
  private static final String kBlocks02[][] = { { "     0    ",
                                                  "     0    ",
                                                  "     00000",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
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
  private static final String kBlocks22[][] = { { "000       ",
                                                  "000       ",
                                                  "000       ",
                                                  "000       ",
                                                  "000       ",
                                                  "000       ",
                                                  "          ",
                                                  "          ",
                                                  "          ",
                                                  "          " } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#g" }; // 
  
  // details of exit/entry points for the room 
  private static final Exit kExits[]
          = { new Exit(0,2, Env.UP,    5,0, "#g",1, -1, RoomE03.NAME, 5),
              new Exit(1,2, Env.UP,    4,8, "#g",0, -1, RoomE03.NAME, 4),
              new Exit(2,2, Env.UP,    1,0, "#g",1, -1, RoomE06.NAME, 2) };

  // flags for zone (1,2)
  private boolean mStairs12Done;

  // references to objects in zone (1,2)
  private BlockStairs mStairs12;
  private ZoneSwitch  mStairSwitch12;

  // constructor
  public RoomE04() {

    super(NAME);

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
          spriteManager.addSprite(new Liquid(zoneX*Room.kSize,
                                             zoneY*Room.kSize,
                                             -2, 2));
        }
      }
    }
    
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
                               zoneX*Room.kSize, zoneY*Room.kSize, 0) );

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

  } // Room.advance()

} // class RoomE04
