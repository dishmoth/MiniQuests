/*
 *  RoomE01.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Splatter;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.Wall;
import com.dishmoth.miniquests.game.WallUp;

// the room "E01"
public class RoomE01 extends Room {

  // unique identifier for this room
  public static final String NAME = "E01";
  
  // all visible blocks for the room
  private static final String kBlocks[][] = { { "0010100000",
                                                "0011100000",
                                                "0000000011",
                                                "0000000010",
                                                "0000000011",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000" } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#h",   // 
                                                  "Th" }; // 
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.RIGHT, 6,0, "#h",1, -1, RoomE01.NAME, 0),
              new Exit(Env.UP,    3,0, "#h",1, -1, RoomE02.NAME, 0) };

  // how long the invisible barrier stays in place for
  private static final int kBarrierTime = 15;
  
  // whether this room has been completed already
  private boolean mCompleted;

  // invisible block preventing exit
  private BlockArray mBarrier;
  
  // how much longer the barrier stays in place for
  private int mBarrierTimer;
  
  // constructor
  public RoomE01() {

    super(NAME);

    mCompleted = false;
    
    mBarrierTimer = 0;
    
  } // constructor

  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < kExits.length );
    setPlayerAtExit(kExits[entryPoint]);
    
    // special behaviour
    if ( entryPoint == 0 ) mPlayer.shiftPos(-1,0,0); 
    
    return mPlayer;
    
  } // createPlayer()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {

    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,0) );

    Exit exits[] = ( mCompleted 
                     ? kExits 
                     : new Exit[]{ kExits[0] } );
    addBasicWalls(exits, spriteManager);

    kExits[0].mDoor.setClosed(true);

  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mBarrier = null;
    
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

    // check player position
    final int x0 = kExits[1].mDoorXYPos,
              y0 = Room.kSize-1,
              z0 = kExits[1].mDoorZPos;
    if ( !mCompleted &&
         mPlayer.getXPos() == x0 &&
         mPlayer.getYPos() == y0 &&
         mPlayer.getZPos() == z0 &&
         mPlayer.getDirec() == Env.UP ) {
      Wall wall = (Wall)spriteManager.findSpriteOfType( WallUp.class );
      wall.addDoor(x0, z0, kExits[1].mFloorColour, kExits[1].mFloorDrop);
      spriteManager.addSprite( new Splatter(x0, y0+1, z0, -1, 5,
                                            (byte)0, Env.DOWN) );
      
      String barrierBlock[][] = {{"-"}};
      mBarrier = new BlockArray(barrierBlock, kBlockColours, x0, y0+1, z0+1);
      spriteManager.addSprite(mBarrier);
      mBarrierTimer = kBarrierTime;
      
      mCompleted = true;
    }

    // remove barrier
    if ( mBarrier != null ) {
      assert( mBarrierTimer > 0 );
      if ( --mBarrierTimer == 0 ) {
        spriteManager.removeSprite(mBarrier);
        mBarrier = null;
      }
    }
    
  } // Room.advance()

} // class RoomE01
