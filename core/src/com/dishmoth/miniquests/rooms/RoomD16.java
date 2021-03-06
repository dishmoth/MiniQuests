/*
 *  RoomD16.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BitBuffer;
import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.Splatter;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.Wall;
import com.dishmoth.miniquests.game.WallLeft;

// the room "D16"
public class RoomD16 extends Room {

  // unique identifier for this room
  public static final String NAME = "D16";
  
  // main blocks for the room
  private static final String kBlocks[][] = { { "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "1100000000",
                                                "0100000000",
                                                "1100000000",
                                                "0000000000",
                                                "0000000111",
                                                "0000000101" } };
  
  // upper blocks for the room
  private static final String kUpperBlocks[][] = { { "        2 ",
                                                     "        2 ",
                                                     "        2 ",
                                                     "        2 ",
                                                     "        2 ",
                                                     "        2 ",
                                                     "        2 ",
                                                     "        2 ",
                                                     "        2 ",
                                                     "        2 " } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "Et",   // beige 
                                                  "mt",   // dark
                                                  "tE" }; // reverse beige
  
  // details of exit/entry points for the room 
  private static final Exit kExits[][] 
          = { { new Exit(Env.DOWN, 8, 0, "Et",0, 0, RoomD06.NAME, 0),
                new Exit(Env.DOWN, 8,14, "tE",1, 1, RoomD06.NAME, 1),
                new Exit(Env.UP,   8,14, "tE",1, 1, RoomD17.NAME, 0),
                new Exit(Env.LEFT, 4, 0, "Et",0, 0, RoomD02.NAME, 4) },

              { new Exit(Env.DOWN, 8, 0, "Et",0, 0, RoomD05.NAME, 1),
                new Exit(Env.DOWN, 8,14, "tE",1, 1, "",0),
                new Exit(Env.UP,   8,14, "tE",1, 1, RoomD17.NAME, 0),
                new Exit(Env.LEFT, 4, 0, "Et",0, 0, RoomD02.NAME, 4) },

              { new Exit(Env.DOWN, 8, 0, "Et",0, 0, "",0),
                new Exit(Env.DOWN, 8,14, "tE",1, 1, "",0),
                new Exit(Env.UP,   8,14, "tE",1, 1, RoomD17.NAME, 0),
                new Exit(Env.LEFT, 4, 0, "Et",0, 0, RoomD02.NAME, 4) },

              { new Exit(Env.DOWN, 8, 0, "Et",0, 0, "",0),
                new Exit(Env.DOWN, 8,14, "tE",1, 1, "",0),
                new Exit(Env.UP,   8,14, "tE",1, 1, RoomD17.NAME, 0),
                new Exit(Env.LEFT, 4, 0, "Et",0, 0, RoomD02.NAME, 4) } };

  // details of different camera height levels
  private static final CameraLevel kCameraLevels[]
                                     = { new CameraLevel( 0,-100, 8),
                                         new CameraLevel(12, 6,+100) };
  
  // how long till things happen
  private static final int kDoorTimer   = 5,
                           kBarrierTime = 15;
  
  // whether this room has been completed already
  private boolean mCompleted;

  // the current exits, based on room D02's twist
  private Exit mExits[];
  
  // invisible block preventing exit
  private BlockArray mBarrier;
  
  // countdown after the trigger is hit
  private int mTimer;
  
  // constructor
  public RoomD16() {

    super(NAME);

    mCompleted = false;
    
  } // constructor

  // serialize the room state by writing bits to the specified buffer
  @Override
  public void save(BitBuffer buffer) {
    
    buffer.writeBit(mCompleted);
    
  } // Room.save()

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  @Override
  public boolean restore(int version, BitBuffer buffer) { 
    
    if ( buffer.numBitsToRead() < 1 ) return false;
    mCompleted = buffer.readBit();
    return true;
    
  } // Room.restore() 
  
  // access to the room's status
  // (note: this function may be called by room D02)
  public boolean completed() { return mCompleted; }
  
  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < mExits.length );
    setPlayerAtExit(mExits[entryPoint], kCameraLevels);
    return mPlayer;
    
  } // createPlayer()
  
  // configure exits based on the room D02's twist
  private void prepareExits() {
    
    RoomD02 twistRoom = (RoomD02)findRoom(RoomD02.NAME);
    assert( twistRoom != null );
    mExits = kExits[ twistRoom.twist() ];    

    if ( !mCompleted ) {
      mExits = new Exit[]{ mExits[0], mExits[1], mExits[2] };
    }
    
  } // prepareExist()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {

    prepareExits();
    
    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 
                                            0,0,0) );
    spriteManager.addSprite( new BlockArray(kUpperBlocks, kBlockColours, 
                                            0,0,14) );

    addBasicWalls(mExits, spriteManager);

    mBarrier = null;
    mTimer = 0;
    
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

    // check exits
    final int exitIndex = checkExits(mExits);
    if ( exitIndex != -1 ) {
      storyEvents.add(new EventRoomChange(mExits[exitIndex].mDestination,
                                          mExits[exitIndex].mEntryPoint));
      return;
    }

    // check camera level
    EventRoomScroll scroll = checkVerticalScroll(kCameraLevels);
    if ( scroll != null ) storyEvents.add(scroll);

    // check player position
    final int x0 = 0,
              y0 = kExits[0][3].mDoorXYPos,
              z0 = kExits[0][3].mDoorZPos;
    if ( !mCompleted && mTimer == 0 &&
         mPlayer.getXPos() == x0 &&
         mPlayer.getYPos() == y0 &&
         mPlayer.getZPos() == z0 &&
         mPlayer.getDirec() == Env.LEFT ) {
      mTimer = kDoorTimer;
    }

    // create the door and barrier
    if ( !mCompleted && mTimer > 0 ) {
      if ( --mTimer == 0 ) {
        mCompleted = true;

        prepareExits();
        Wall wall = (Wall)spriteManager.findSpriteOfType( WallLeft.class );
        wall.addDoor(y0, z0, 
                     mExits[3].mFloorColour, 
                     mExits[3].mFloorDrop);
        spriteManager.addSprite( new Splatter(x0-1, y0, z0, -1, 5,
                                              (byte)0, Env.RIGHT) );
        
        String barrierBlock[][] = {{"-"}};
        mBarrier = new BlockArray(barrierBlock, kBlockColours, x0-1, y0, z0+1);
        spriteManager.addSprite(mBarrier);
        mTimer = kBarrierTime;
        
        Env.sounds().play(Sounds.MATERIALIZE);
      }
    }
    
    // remove barrier
    if ( mCompleted && mBarrier != null ) {
      assert( mTimer > 0 );
      if ( --mTimer == 0 ) {
        spriteManager.removeSprite(mBarrier);
        mBarrier = null;
      }
    }
    
  } // Room.advance()

} // class RoomD16
