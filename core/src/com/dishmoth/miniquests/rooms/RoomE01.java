/*
 *  RoomE01.java
 *  Copyright (c) 2018 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Brain;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Fence;
import com.dishmoth.miniquests.game.Liquid;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;

// the room "E01"
public class RoomE01 extends Room {

  // unique identifier for this room
  public static final String NAME = "E01";
  
  // rate at which the floor tiles move
  private static final int kRaftMoveTime  = 8;

  // main blocks for the room
  private static final String kBlocks[][] = { { "0000000000",
                                                "000000    ",
                                                "000000    ",
                                                "000000    ",
                                                "0000000000",
                                                "0         ",
                                                "0         ",
                                                "0         ",
                                                "0         ",
                                                "111111    " },
                                              
                                              { "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                " 11111    " },
                                              
                                              { "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "  1111    " },
                                              
                                              { "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "   111    " } };
                           
  // block pattern for the raft
  private static final String kBlocksRaft[][] = { { "222", "222", "222" } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "LX",
                                                  "lX",
                                                  "FX" };
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { // note: dummy exit at index 0
              new Exit(0, 0, Env.DOWN, 4,6, "lX",0, -1, RoomE02.NAME, 0) };

  // reference to the raft object
  private BlockArray mRaft;
  
  // time until the next raft step
  private int mTimer;
  
  // constructor
  public RoomE01() {

    super(NAME);

  } // constructor

  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < kExits.length+1 );
    
    if ( entryPoint == 0 ) {
      // special case: start of game
      mPlayer = new Player(14, 7, 0, Env.LEFT);
      mPlayer.addBrain(new Brain.ZombieModule(new int[]{ Env.NONE,70,
                                                         Env.LEFT,18 }));
      mCameraLevel = -1;
      mCamera.set(0, 0, 0);
    } else {
      setPlayerAtExit(kExits[entryPoint-1]);
      mRaft.setPos(6, 6, 0);
    }
    
    return mPlayer;
    
  } // createPlayer()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {
    
    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,0) );
    
    addBasicZone(0, 0, false, true, true, true,
                 kExits, spriteManager);
    addBasicZone(1, 0, false, true, false, true,
                 kExits, spriteManager);

    spriteManager.addSprite(new Fence(0, 9, 0, 10, Env.RIGHT, 1));
    spriteManager.addSprite(new Fence(1, 5, 0, 9, Env.RIGHT, 1));
    
    spriteManager.addSprite(new Liquid(0, 0, -2, 2));
    spriteManager.addSprite(new Liquid(10, 0, -2, 2));
    
    mRaft = new BlockArray(kBlocksRaft, kBlockColours, 13, 6, 0);
    spriteManager.addSprite(mRaft);
    
    mTimer = kRaftMoveTime;

  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mRaft = null;
    
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

    // move the raft
    if ( mTimer > 0 ) {
      mTimer--;
    } else if ( mRaft.getXPos() > 6 ) {
      if ( mPlayer != null &&
           mPlayer.getXPos() >= mRaft.getXPos() &&
           mPlayer.getXPos() <= mRaft.getXPos() + 2 &&
           mPlayer.getYPos() >= mRaft.getYPos() &&
           mPlayer.getYPos() <= mRaft.getYPos() + 2 ) {
        mPlayer.slidePos(Env.LEFT, 1);
      }
      mRaft.shiftPos(-1, 0, 0);
      if ( mRaft.getXPos() == 6 ) Env.sounds().play(Sounds.SWITCH_OFF);
      mTimer = kRaftMoveTime;
    }

  } // Room.advance()

} // class RoomE01
