/*
 *  RoomE09.java
 *  Copyright (c) 2018 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.BlockStairs;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Liquid;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;

// the room "E06"
public class RoomE06 extends Room {

  // unique identifier for this room
  public static final String NAME = "E06";
  
  // main blocks for the room
  private static final String kBlocks[][] = { { "    0     ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " } };
                                              
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "NY" }; //
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.UP, 4,0, "#Y",0, -1, RoomE06.NAME, 0) };

  // constructor
  public RoomE06() {

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
  
  private static int f() { return 4*Env.randomInt(2); }
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {
    
    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,0) );
    
    addBasicWalls(kExits, spriteManager);

    spriteManager.addSprite(new Liquid(0,0,0, 2));

    final int h = 4;
    spriteManager.addSprite(new BlockStairs(0, 0, f()-h,
                                            4, 0, f()-h,
                                            "NY", 3));
    spriteManager.addSprite(new BlockStairs(4, 0, f()-h,
                                            8, 0, f()-h,
                                            "NY", 3));

    spriteManager.addSprite(new BlockStairs(0, 4, f()-h,
                                            4, 4, f()-h,
                                            "NY", 3));
    spriteManager.addSprite(new BlockStairs(4, 4, f()-h,
                                            8, 4, f()-h,
                                            "NY", 3));

    spriteManager.addSprite(new BlockStairs(0, 8, f()-h,
                                            4, 8, f()-h,
                                            "NY", 3));
    spriteManager.addSprite(new BlockStairs(4, 8, f()-h,
                                            8, 8, f()-h,
                                            "NY", 3));
    
    spriteManager.addSprite(new BlockStairs(0, 0, f()-h,
                                            0, 4, f()-h,
                                            "NY", 3));
    spriteManager.addSprite(new BlockStairs(0, 4, f()-h,
                                            0, 8, f()-h,
                                            "NY", 3));

    spriteManager.addSprite(new BlockStairs(4, 0, f()-h,
                                            4, 4, f()-h,
                                            "NY", 3));
    spriteManager.addSprite(new BlockStairs(4, 4, f()-h,
                                            4, 8, f()-h,
                                            "NY", 3));

    spriteManager.addSprite(new BlockStairs(8, 0, 0-h,
                                            8, 4, 0-h,
                                            "NY", 3));
    spriteManager.addSprite(new BlockStairs(8, 4, 4-h,
                                            8, 8, 0-h,
                                            "NY", 3));

  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

  } // Room.discardResources()
  
  // update the room (events may be added or processed)
  @Override
  public void advance(LinkedList<StoryEvent> storyEvents,
                      SpriteManager          spriteManager) {

    final int exitIndex = checkExits(kExits);
    if ( exitIndex != -1 ) {
      storyEvents.add(new EventRoomChange(kExits[exitIndex].mDestination,
                                          kExits[exitIndex].mEntryPoint));
      return;
    }

  } // Room.advance()

} // class RoomE06
