/*
 *  RoomC01.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Brain;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;

// the room "C01"
public class RoomC01 extends Room {

  // unique identifier for this room
  public static final String NAME = "C01";
  
  // the basic blocks for the room
  private static final String kBlocks[][] = { { "       000",
                                                "        00",
                                                "        00",
                                                "         0",
                                                "        00",
                                                "        00",
                                                "      0000",
                                                "      0000",
                                                "      0000",
                                                "      0000" },
                                                
                                              { "       000",
                                                "        00",
                                                "        00",
                                                "         0",
                                                "        00",
                                                "        00",
                                                "      0000",
                                                "      0000",
                                                "      0000",
                                                "      0000" },
                                                
                                              { "       000",
                                                "        00",
                                                "        00",
                                                "         0",
                                                "        00",
                                                "        00",
                                                "      0000",
                                                "      0000",
                                                "      0000",
                                                "      0000" },
                                                
                                              { "000    000",
                                                "0       00",
                                                "         0",
                                                "         0",
                                                "        00",
                                                "        00",
                                                "      0000",
                                                "      0000",
                                                "0000000000",
                                                "0000000000" },
                                                
                                              { "000    000",
                                                "0       00",
                                                "         0",
                                                "         0",
                                                "        00",
                                                "   2    00",
                                                "   2  0000",
                                                "   2  0000",
                                                "0002000000",
                                                "0002000000" },
                                                
                                              { "0        0",
                                                "         0",
                                                "         0",
                                                "          ",
                                                "          ",
                                                "         0",
                                                "         0",
                                                "        00",
                                                "        00",
                                                "        00" },
                                                
                                              { "         0",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "         0",
                                                "         0" } };
  
  // the blocks for the room's lift
  private static final String kLiftLayer[] = { "111", "121", "121" }; 
  private static final String kLiftBlocks[][] = { kLiftLayer, kLiftLayer, 
                                                  kLiftLayer, kLiftLayer, 
                                                  kLiftLayer, kLiftLayer, 
                                                  kLiftLayer, kLiftLayer, 
                                                  kLiftLayer, kLiftLayer };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "lL",   // purple
                                                  "tB",   // blue edge
                                                  "tB" }; // blue
  
  // details of exit/entry points for the room
  private static final Exit kExits[] 
          = { // note: dummy exit at index 0
              new Exit(Env.DOWN, 3,0, "tB",0, -1, RoomC02.NAME, 0) }; 
  
  // parameters controlling the lift
  private static final int kLiftZMin  = -18,
                           kLiftZMax  = -4;
  private static final int kLiftDelay = 4;
  
  // reference to the lift blocks
  private BlockArray mLift;
  
  // how long until the lift moves again
  private int mLiftTimer;

  // constructor
  public RoomC01() {

    super(NAME);

  } // constructor

  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < kExits.length+1 );
    
    if ( entryPoint == 0 ) {
      // special case: start of game
      mPlayer = new Player(3, 6, 14, Env.DOWN);
      mPlayer.addBrain(new Brain.ZombieModule(new int[]{ Env.NONE,70, 
                                                         Env.DOWN,25 }));
      mCameraLevel = -1;
      mCamera.set(0, 0, 0);
    } else {
      setPlayerAtExit(kExits[entryPoint-1]);
      mLift.setPos( mLift.getXPos(), mLift.getYPos(), kLiftZMin );
    }
    
    return mPlayer;
    
  } // createPlayer()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mLift = null;
    
  } // Room.discardResources()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {

    addBasicWalls(kExits, spriteManager);

    spriteManager.addSprite(new BlockArray(kBlocks, kBlockColours, 0,0,-8));

    mLift = new BlockArray(kLiftBlocks, kBlockColours, 2,5,kLiftZMax);
    spriteManager.addSprite(mLift);

    mLiftTimer = 0;

  } // Room.createSprites()
  
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

    // animate the lift
    if ( mLift.getZPos() > kLiftZMin ) {
      if ( --mLiftTimer <= 0 ) {
        mLiftTimer = kLiftDelay;
        mLift.shiftPos(0, 0, -1);
        if ( mLift.getZPos() == kLiftZMin ) {
          Env.sounds().play(Sounds.SWITCH_OFF);
        }
      }
    }
    
  } // Room.advance()

} // class RoomC01
