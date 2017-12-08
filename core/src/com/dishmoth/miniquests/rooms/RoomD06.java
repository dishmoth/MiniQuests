/*
 *  RoomD06.java
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
import com.dishmoth.miniquests.game.Spikes;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;

// the room "D06"
public class RoomD06 extends Room {

  // unique identifier for this room
  public static final String NAME = "D06";
  
  // main blocks for the room
  private static final String kBlocks[][] = { { "0000000000",
                                                "0  0  0  0",
                                                "0  0  0  0",
                                                "0000000000",
                                                "0  0  0  0",
                                                "0  0  0  0",
                                                "0000000000",
                                                "0  0  0   ",
                                                "0  0  0   ",
                                                "0000000000" },
                                                
                                              { "0000000111",
                                                "0  0  0  1",
                                                "0  0  0  1",
                                                "0000000000",
                                                "1  1  0  0",
                                                "1  1  0  0",
                                                "0000000000",
                                                "0  0  0   ",
                                                "0  0  0   ",
                                                "0000000111" },
                                                
                                              { "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "         1" },
                                                
                                              { "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "         1",
                                                "          " },
                                                
                                              { "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "         1",
                                                "          ",
                                                "          " },
                                                
                                              { "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "         1",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "         1",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "         1",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "        11",
                                                "         1",
                                                "         1",
                                                "         1",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " } };
                                              
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "kq",   // orange
                                                  "kq" }; //
  
  // details of exit/entry points for the room 
  private static final Exit kExits[][]
        = { { new Exit(Env.UP,   8,0,  "#q",0, 0, RoomD16.NAME, 0),
              new Exit(Env.UP,   8,14, "#q",1, 1, RoomD16.NAME, 1),
              new Exit(Env.LEFT, 5,0,  "kq",0, 0, RoomD02.NAME, 3) },
              
            { new Exit(Env.UP,   8,0,  "#q",0, 0, "",0),
              new Exit(Env.UP,   8,14, "#q",1, 1, "",0),
              new Exit(Env.LEFT, 5,0,  "kq",0, 0, RoomD07.NAME, 0) },
              
            { new Exit(Env.UP,   8,0,  "#q",0, 0, RoomD09.NAME, 1),
              new Exit(Env.UP,   8,14, "#q",1, 1, "",0),
              new Exit(Env.LEFT, 5,0,  "kq",0, 0, "",0) },
              
            { new Exit(Env.UP,   8,0,  "#q",0, 0, "",0),
              new Exit(Env.UP,   8,14, "#q",1, 1, "",0),
              new Exit(Env.LEFT, 5,0,  "kq",0, 0, "",0) } };
              
  // details of different camera height levels
  private static final CameraLevel kCameraLevels[]
                                     = { new CameraLevel( 0,-100, 10),
                                         new CameraLevel(12,  4,+100) };
  
  // positions of spikes
  private static final String kSpikePattern[] = { "++++      ",
                                                  "+  +      ",
                                                  "+  +      ",
                                                  "++++  ++++",
                                                  "      +  +",
                                                  "      +  +",
                                                  "++++  ++++",
                                                  "+  +      ",
                                                  "+  +      ",
                                                  "++++      " };
  
  // path followed by spikes, {x,y, x,y, ...}
  private static final int kSpikePaths[][] = { { 6,3, 9,3, 9,6, 6,6 },
                                               //{ 6,3, 9,3, 9,6, 6,6,
                                               //  6,9, 3,9, 3,6, 6,6,
                                               //  6,3, 6,0, 3,0, 3,3 },
                                               { 3,9, 3,6, 0,6, 0,9 },
                                               { 0,0, 3,0, 3,3, 0,3 } };
  private static final int kStartIndices[] = { 1, 1, 0, 0 };
  
  // time between spikes
  private static final int kSpikeTime = 3;
  
  // number of ticks to run spikes before the room starts
  private static final int kSpikeWarmUp = 10;
  
  // the current exits, based on room D02's twist
  private Exit mExits[];

  // whether the spikes are active
  private boolean mSpikesOn;
  
  // array of spikes ([y][x], may be null)
  private Spikes mSpikes[][];
  
  // time until the next spike is triggered
  private int mSpikeTimer;

  // current spike position
  private int mXPos[],
              mYPos[];

  // current destination on the path
  private int mIndex[];

  // cheat: the player can't win, but it's important to make sure
  private boolean mCertainDeath;
  private int mDeathTimer;
  
  // constructor
  public RoomD06() {

    super(NAME);
    
    mSpikesOn = true;
    
  } // constructor

  // serialize the room state by writing bits to the specified buffer
  @Override
  public void save(BitBuffer buffer) {
    
    buffer.writeBit(mSpikesOn);
    
  } // Room.save()

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  @Override
  public boolean restore(int version, BitBuffer buffer) { 
    
    if ( buffer.numBitsToRead() < 1 ) return false;
    mSpikesOn = buffer.readBit();
    return true;
    
  } // Room.restore() 
  
  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < mExits.length );
    setPlayerAtExit(mExits[entryPoint], kCameraLevels);

    mCertainDeath = ( mSpikesOn && entryPoint == 2 );
    mDeathTimer = 0;

    return mPlayer;
    
  } // createPlayer()
  
  // configure exits based on the room D02's twist
  private void prepareExits() {
    
    RoomD02 twistRoom = (RoomD02)findRoom(RoomD02.NAME);
    assert( twistRoom != null );
    mExits = kExits[ twistRoom.twist() ];    
    
  } // prepareExist()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {
    
    prepareExits();
    
    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,-2) );
    
    addBasicWalls(mExits, spriteManager);

    mSpikes = new Spikes[Room.kSize][Room.kSize];
    for ( int iy = 0 ; iy < Room.kSize ; iy++ ) {
      for ( int ix = 0 ; ix < Room.kSize ; ix++ ) {
        if ( kSpikePattern[Room.kSize-1-iy].charAt(ix) != ' ' ) {
          mSpikes[iy][ix] = new Spikes(ix,iy,0, 1,1, true, "K0");
          mSpikes[iy][ix].setSilent(true);
          spriteManager.addSprite( mSpikes[iy][ix] );
        }
      }
    }
    
    mSpikeTimer = kSpikeTime;

    mIndex = new int[kSpikePaths.length];
    mXPos = new int[kSpikePaths.length];
    mYPos = new int[kSpikePaths.length];
    for ( int k = 0 ; k < kSpikePaths.length ; k++ ) {
      mIndex[k] = kStartIndices[k];
      mXPos[k] = kSpikePaths[k][ 2*mIndex[k] ];
      mYPos[k] = kSpikePaths[k][ 2*mIndex[k]+1 ];
    }

    if ( mSpikesOn ) {
      for ( int k = 0 ; k < kSpikeWarmUp ; k++ ) {
        updateSpikes();
        advanceSpikes();
      }
    }
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {
    
    mSpikes = null;
    
  } // Room.discardResources()

  // trigger spikes along paths
  private void updateSpikes() {
    
    if ( --mSpikeTimer <= 0 ) {
      mSpikeTimer = kSpikeTime;
      for ( int path = 0 ; path < kSpikePaths.length ; path++ ) {
        mSpikes[ mYPos[path] ][mXPos[path] ].trigger();
        int dx = kSpikePaths[path][2*mIndex[path]] - mXPos[path],
            dy = kSpikePaths[path][2*mIndex[path]+1] - mYPos[path];
        if ( dx == 0 && dy == 0 ) {
          mIndex[path] += 1;
          if ( mIndex[path] >= kSpikePaths[path].length/2 ) mIndex[path] = 0;
          dx = kSpikePaths[path][2*mIndex[path]] - mXPos[path];
          dy = kSpikePaths[path][2*mIndex[path]+1] - mYPos[path];
        }
        assert ( (dx == 0 && dy != 0) || (dx != 0 && dy == 0) );
        if ( dx > 0 ) mXPos[path] += 1;
        if ( dx < 0 ) mXPos[path] -= 1;
        if ( dy > 0 ) mYPos[path] += 1;
        if ( dy < 0 ) mYPos[path] -= 1;
      }
    }
    
  } // updateSpikes()
  
  // force an advance for all spikes 
  private void advanceSpikes() {
    
    for ( int iy = 0 ; iy < mSpikes.length ; iy++ ) {
      Spikes spikeRow[] = mSpikes[iy]; 
      for ( int ix = 0 ; ix < spikeRow.length ; ix++ ) {
        if ( spikeRow[ix] != null ) spikeRow[ix].advance(null,null,null);
      }
    }
    
  } // advanceSpikes()
  
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

    // trigger spikes along path
    if ( mSpikesOn ) {
      updateSpikes();
      if ( mSpikeTimer == 1 ) Env.sounds().play(Sounds.SPIKES_QUIET);
    }
    
    // turn off spikes
    if ( mSpikesOn && mPlayer != null &&
         mPlayer.getXPos() == 6 && mPlayer.getYPos() == 0 ) {
      Env.sounds().play(Sounds.SWITCH_ON);
      Env.sounds().play(Sounds.SUCCESS, 10);
      mSpikesOn = false;
    }

    // don't let the player win if they came in the left door
    if ( mSpikesOn && mCertainDeath && mPlayer != null ) {
      if ( mDeathTimer > 0 ) {
        if ( --mDeathTimer == 0 ) {
          mPlayer.destroy(-1);
          Env.debug("Warning: unfair death (sorry!)");
        }
      } else if ( mPlayer.getXPos() == 3 ) {
        mDeathTimer = 8;
      }
    }
    
  } // Room.advance()

} // class RoomD06
