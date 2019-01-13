/*
 *  RoomE04.java
 *  Copyright (c) 2018 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Fence;
import com.dishmoth.miniquests.game.FenceGate;
import com.dishmoth.miniquests.game.Liquid;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.SnakeC;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;

// the room "E04"
public class RoomE04 extends Room {

  // unique identifier for this room
  public static final String NAME = "E04";
  
  private static final String kBlocks[][] = { { "0000000000",
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

  //
  private static final String kBlocks21[][] = { { "     00000",
                                                  "     00000",
                                                  "     00000",
                                                  "0000000000",
                                                  "     00000",
                                                  "     00000",
                                                  "     00000",
                                                  "          ",
                                                  "          ",
                                                  "          " } };
  
  //
  private static final String kBlocks12[][] = { { " 0000000000000000000",
                                                  " 0000000000000000000",
                                                  " 0000000000000000000",
                                                  "                    ",
                                                  "                    ",
                                                  "                    ",
                                                  "                    ",
                                                  "                    ",
                                                  "                    ",
                                                  "                    " } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#g" }; // 
  
  // details of exit/entry points for the room 
  private static final Exit kExits[]
          = { new Exit(1,2, Env.UP,    2,0, "#g",0, -1, RoomE03.NAME, 5),
              new Exit(2,1, Env.RIGHT, 6,0, "#g",0, -1, RoomE03.NAME, 3),
              new Exit(2,2, Env.UP,    5,0, "#g",0, -1, RoomE09.NAME, 2) };

  // constructor
  public RoomE04() {

    super(NAME);

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
                new BlockArray(kBlocks, kBlockColours,
                               zoneX*Room.kSize, zoneY*Room.kSize, -2) );

    //spriteManager.addSprite( new SnakeC(3,3,0, Env.DOWN) );
    

    // zone (2,1)

    zoneX = 2;
    zoneY = 1;

    spriteManager.addSprite(
                new BlockArray(kBlocks21, kBlockColours,
                               zoneX*Room.kSize, zoneY*Room.kSize, 0) );

    spriteManager.addSprite(new Fence(zoneX*Room.kSize+5, 
                                      zoneY*Room.kSize+3,
                                      0, 3, Env.UP, 1));
    spriteManager.addSprite(new Fence(zoneX*Room.kSize+5, 
                                      zoneY*Room.kSize+7,
                                      0, 3, Env.UP, 1));

    FenceGate gate = new FenceGate(zoneX*Room.kSize+5, 
                                   zoneY*Room.kSize+5, 
                                   0, Env.UP, 1);
    //gate.setClosed(true);
    spriteManager.addSprite(gate);
    
    // zone (1,2)
    
    zoneX = 1;
    zoneY = 2;

    spriteManager.addSprite(
                new BlockArray(kBlocks12, kBlockColours,
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
    
  } // Room.advance()

} // class RoomE04
