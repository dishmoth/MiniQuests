/*
 *  RoomE12.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Critter;
import com.dishmoth.miniquests.game.CritterTrack;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;

// the room "E12"
public class RoomE12 extends Room {

  // unique identifier for this room
  public static final String NAME = "E12";
  
  // main blocks for the room
  private static final String kBlocks1[][] = { { "0000000000",
                                                 "0000000000",
                                                 "0011111100",
                                                 "0010000100",
                                                 "0010000100",
                                                 "0010000100",
                                                 "0010000100",
                                                 "0011111100",
                                                 "0000000000",
                                                 "0000000000" } };
  
  // high-up blocks for the room
  private static final String kBlocks2[][] = { { "    3     ",
                                                 "    3     ",
                                                 "    3     ",
                                                 "33333     ",
                                                 "          ",
                                                 "          ",
                                                 "          ",
                                                 "          ",
                                                 "111       ",
                                                 "          " } };

  // stair details
  private static final int kStairStart     = -42,
                           kStairEnd       = +2,
                           kStairTime      = 1,
                           kStairStartTime = 20;
  
  // blocks making a staircase
  private static final String kStairBlocks[][] = { { "      ",
                                                     "      ",
                                                     "      ",
                                                     "      ",
                                                     "      ",
                                                     "2     " },
                                                     
                                                   { "      ",
                                                     "      ",
                                                     "      ",
                                                     "      ",
                                                     "2     ",
                                                     "      " },  
                                                     
                                                   { "      ",
                                                     "      ",
                                                     "      ",
                                                     "2     ",
                                                     "      ",
                                                     "      " },  
                                                     
                                                   { "      ",
                                                     "      ",
                                                     "2     ",
                                                     "      ",
                                                     "      ",
                                                     "      " },  
                                                     
                                                   { "      ",
                                                     "2     ",
                                                     "      ",
                                                     "      ",
                                                     "      ",
                                                     "      " },
  
                                                   { "2     ",
                                                     "      ",
                                                     "      ",
                                                     "      ",
                                                     "      ",
                                                     "      " },
  
                                                   { " 2    ",
                                                     "      ",
                                                     "      ",
                                                     "      ",
                                                     "      ",
                                                     "      " },
  
                                                   { "  2   ",
                                                     "      ",
                                                     "      ",
                                                     "      ",
                                                     "      ",
                                                     "      " },
  
                                                   { "   2  ",
                                                     "      ",
                                                     "      ",
                                                     "      ",
                                                     "      ",
                                                     "      " },
  
                                                   { "    2 ",
                                                     "      ",
                                                     "      ",
                                                     "      ",
                                                     "      ",
                                                     "      " },
  
                                                   { "     2",
                                                     "      ",
                                                     "      ",
                                                     "      ",
                                                     "      ",
                                                     "      " },
  
                                                   { "      ",
                                                     "     2",
                                                     "      ",
                                                     "      ",
                                                     "      ",
                                                     "      " },
  
                                                   { "      ",
                                                     "      ",
                                                     "     2",
                                                     "      ",
                                                     "      ",
                                                     "      " },
  
                                                   { "      ",
                                                     "      ",
                                                     "      ",
                                                     "     2",
                                                     "      ",
                                                     "      " },
  
                                                   { "      ",
                                                     "      ",
                                                     "      ",
                                                     "      ",
                                                     "     2",
                                                     "      " },
  
                                                   { "      ",
                                                     "      ",
                                                     "      ",
                                                     "      ",
                                                     "      ",
                                                     "     2" }, 

                                                   { "      ",
                                                     "      ",
                                                     "      ",
                                                     "      ",
                                                     "      ",
                                                     "    2 " }, 

                                                   { "      ",
                                                     "      ",
                                                     "      ",
                                                     "      ",
                                                     "      ",
                                                     "   2  " }, 

                                                   { "      ",
                                                     "      ",
                                                     "      ",
                                                     "      ",
                                                     "      ",
                                                     "  2   " },

                                                   { "      ",
                                                     "      ",
                                                     "      ",
                                                     "      ",
                                                     "      ",
                                                     " 2    " },

                                                   { "      ",
                                                     "      ",
                                                     "      ",
                                                     "      ",
                                                     "      ",
                                                     "2     " } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#V",   // blue 
                                                  "#c",   // orange
                                                  "Kc",   // orange
                                                  "#A" }; // green
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.RIGHT, 4,0,  "#V",0, 0, RoomE11.NAME, 0),
              new Exit(Env.LEFT,  1,42, "#c",1, 4, RoomE07.NAME, 3),
              new Exit(Env.LEFT,  6,42, "#A",1, 4, RoomE07.NAME, 1),
              new Exit(Env.UP,    4,42, "#A",0, 4, RoomE12.NAME, 3) };

  // details of different camera height levels
  private static final CameraLevel kCameraLevels[]
                                     = { new CameraLevel(0, -100, 10),
                                         new CameraLevel(8, 2, 20),
                                         new CameraLevel(18, 14, 30),
                                         new CameraLevel(28, 24, 40),
                                         new CameraLevel(38, 34, +100) };
  
  // path followed by enemies in this room
  private static final CritterTrack kCritterTrack 
                      = new CritterTrack(new String[]{ "          ",
                                                       "          ",
                                                       "  ++++++  ",
                                                       "  +    +  ",
                                                       "  +    +  ",
                                                       "  +    +  ",
                                                       "  +    +  ",
                                                       "  ++++++  ",
                                                       "          ",
                                                       "          " });

  // references to critters
  private Critter mCritters[];

  // stairs that appear when the room is complete
  private BlockArray mStairBlocks;

  // time until next movement of stairs
  private int mStairTimer;
  
  // whether the room has been done yet
  private boolean mCompleted;
  
  // constructor
  public RoomE12() {
    
    super(NAME);

    mCompleted = false;
    
  } // constructor

  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < kExits.length );
    setPlayerAtExit(kExits[entryPoint], kCameraLevels);
    return mPlayer;
    
  } // createPlayer()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {
    
    spriteManager.addSprite( new BlockArray(kBlocks1, kBlockColours, 0,0,0) );
    spriteManager.addSprite( new BlockArray(kBlocks2, kBlockColours, 0,0,42) );
    
    addBasicWalls(kExits, spriteManager);

    mStairBlocks = null;

    if ( mCompleted ) {
      spriteManager.addSprite( new BlockArray(kStairBlocks, kBlockColours, 
                                              2, 2, kStairEnd) );
    } else {
      mCritters = new Critter[]{ new Critter(2,5,0, Env.DOWN, kCritterTrack),
                                 new Critter(6,2,0, Env.RIGHT, kCritterTrack),
                                 new Critter(7,7,0, Env.UP, kCritterTrack) };    
      for ( Critter c : mCritters ) {
        c.setStunTime(70);
        spriteManager.addSprite(c);
      }
    }
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {
    
    mCritters = null;
    mStairBlocks = null;
    
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

    // check enemies
    if ( mCritters != null ) {
      boolean allStunned = true;
      for ( Critter c : mCritters ) { 
        if ( !c.isStunned() ) allStunned = false;
      }
      if ( allStunned ) {
        for ( Critter c : mCritters ) c.destroy(-1);
        mCritters = null;
        mCompleted = true;
        mStairBlocks = new BlockArray(kStairBlocks, kBlockColours, 
                                      2, 2, kStairStart);
        spriteManager.addSprite( mStairBlocks );
        mStairTimer = kStairStartTime;
      }
    }

    // check camera level
    EventRoomScroll scroll = checkVerticalScroll(kCameraLevels);
    if ( scroll != null ) storyEvents.add(scroll);

    // move stairs
    if ( mStairBlocks != null ) {
      if ( mStairTimer > 0 ) {
        mStairTimer--;
      } else {
        mStairBlocks.shiftPos(0, 0, +1);
        if ( mStairBlocks.getZPos() == kStairEnd ) {
          mStairBlocks = null;
        } else {
          mStairTimer = kStairTime;
        }
      }
    }
    
  } // Room.advance()

} // class RoomE12
