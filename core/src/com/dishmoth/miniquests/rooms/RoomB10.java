/*
 *  RoomB10.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BitBuffer;
import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Critter;
import com.dishmoth.miniquests.game.CritterTrack;
import com.dishmoth.miniquests.game.EgaImage;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Mural;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.QuestStory;
import com.dishmoth.miniquests.game.WallSwitch;

// the room "B10"
public class RoomB10 extends Room {

  // unique identifier for this room
  public static final String NAME = "B10";
  
  // blocks for beneath the room
  private static final String kLowerBlocks[][] = { { "0000000000",
                                                     "0000000000",
                                                     "0000000000",
                                                     "000    000",
                                                     "000    000",
                                                     "000    000",
                                                     "000    000",
                                                     "0000000000",
                                                     "0000000000",
                                                     "0000000000" } };
  
  // blocks for the room
  private static final String kBlocks[][] = { { "          ",
                                                "          ",
                                                "          ",
                                                " 1111111  ",
                                                " 1  1  111",
                                                " 1  1  1  ",
                                                " 1111111  ",
                                                "          ",
                                                "          ",
                                                "          " } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "7u",   // grey 
                                                  "#k",   // 
                                                  "#T" }; // plum 
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.RIGHT, 5,10, "#k",1, 0, RoomB08.NAME, 3) };

  // details of different camera height levels
  private static final CameraLevel kCameraLevels[]
                                     = { new CameraLevel(10, -100, +100) };
  
  // details of the path followed by enemies
  private static final CritterTrack kCritterTrack 
                    = new CritterTrack(new String[]{ "+++++++",
                                                     "+  +  +",
                                                     "+  +  +",
                                                     "+++++++" }, 1, 3);

  // range of colours of the switches
  private static final String kSwitchColours[] = { "07", "47", "j7", 
                                                   "I7", "v7", "k7" }; 

  // different switch combinations
  private static final int kSwitchStatesStart[]   = { 4, 1, 2 },
                           kSwitchStatesUpper[]   = { 0, 4, 5 }, 
                           kSwitchStatesLower[]   = { 2, 3, 2 }, 
                           kSwitchStatesCritter[] = { 3, 3, 3 };
  
  // rate at which the upper and lower blocks move
  private static final int kTimeBlockShift = 3;
  
  // how far the blocks move
  private static final int kMaxBlockShift = 4;
  
  // references to the three switches
  private WallSwitch mSwitches[];
  
  // whether the two set of blocks have started moving yet
  private boolean mUpperActivated,
                  mLowerActivated;
  
  // ticks until the next block movement
  private int mUpperTimer,
              mLowerTimer;
  
  // how far the blocks have moved
  private int mUpperShift,
              mLowerShift;
  
  // references to the sets of blocks
  private BlockArray mUpperBlocks,
                     mLowerBlocks;

  // current states of the switches
  private int mSwitchStates[];

  // whethe the critter has been destroyed
  private boolean mCritterDead;
  
  // reference to the critter
  private Critter mCritter;
  
  // constructor
  public RoomB10() {

    super(NAME);

    mUpperActivated = false;
    mLowerActivated = false;

    mSwitchStates = Env.copyOf(kSwitchStatesStart);

    mCritterDead = false;
    
  } // constructor

  // serialize the room state by writing bits to the specified buffer
  @Override
  public void save(BitBuffer buffer) {
    
    buffer.writeBit(mUpperActivated);
    buffer.writeBit(mLowerActivated);
    
  } // Room.save()

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  @Override
  public boolean restore(int version, BitBuffer buffer) { 
    
    if ( buffer.numBitsToRead() < 2 ) return false;
    mUpperActivated = buffer.readBit();
    mLowerActivated = buffer.readBit();
    return true; 
    
  } // Room.restore() 
  
  // whether the upper blocks have been moved yet
  // (note: this function may be called by RoomB08)
  public boolean upperBlocksActived() { return mUpperActivated; }
  
  // whether the lower blocks have been moved yet
  // (note: this function may be called by RoomB08)
  public boolean lowerBlocksActived() { return mLowerActivated; }
  
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

    spriteManager.addSprite( new BlockArray(kLowerBlocks, kBlockColours, 
                                            0,0,0) );
    
    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,10) );
    addBasicWalls(kExits, spriteManager);

    if ( !mCritterDead ) {
      mCritter = new Critter(1,4,10, Env.UP, kCritterTrack);
      mCritter.easilyStunned(true);
      spriteManager.addSprite(mCritter);
    }

    mSwitches = new WallSwitch[3];
    final int switchX[] = { 1, 4, 7 };
    for ( int k = 0 ; k < mSwitches.length ; k++ ) {
      mSwitches[k] = new WallSwitch(Env.UP, switchX[k], 12, 
                                    kSwitchColours, true);
      mSwitches[k].setState( mSwitchStates[k] );
      spriteManager.addSprite(mSwitches[k]);
      
      char col = kSwitchColours[kSwitchStatesUpper[k]].charAt(0);
      String pixels = new String(new char[]{ ' ',col, ' ',' ', ' ',col });
      EgaImage image = new EgaImage(0,2, 2,3, pixels);
      Mural mural = new Mural(Env.UP, switchX[k], 15, image);
      spriteManager.addSprite(mural);
    }

    mUpperShift = ( mUpperActivated ? kMaxBlockShift : 0 );
    mLowerShift = ( mLowerActivated ? kMaxBlockShift : 0 );
    mUpperTimer = 0;
    mLowerTimer = 0;
    makeUpperBlocks(spriteManager);
    makeLowerBlocks(spriteManager);
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mSwitches = null;
    mUpperBlocks = mLowerBlocks = null;
    mCritter = null;
    
  } // Room.discardResources()
  
  // create or recreate the upper strip of blocks 
  private void makeUpperBlocks(SpriteManager spriteManager) {
    
    if ( mUpperBlocks != null ) spriteManager.removeSprite(mUpperBlocks);

    char blocks[] = new char[Room.kSize - mUpperShift];
    Arrays.fill(blocks, '2');
    mUpperBlocks = new BlockArray(new String[][]{{new String(blocks)}},
                                  kBlockColours, mUpperShift, 9, 10);
    spriteManager.addSprite(mUpperBlocks);
    
  } // makeUpperBlocks()
  
  // create or recreate the lower strip of blocks 
  private void makeLowerBlocks(SpriteManager spriteManager) {
    
    if ( mLowerBlocks != null ) spriteManager.removeSprite(mLowerBlocks);

    char blocks[] = new char[Room.kSize - mLowerShift];
    Arrays.fill(blocks, '2');
    mLowerBlocks = new BlockArray(new String[][]{{new String(blocks)}},
                                  kBlockColours, mLowerShift, 0, 10);
    spriteManager.addSprite(mLowerBlocks);
    
  } // makeLowerBlocks()
  
  // check whether the switches have a particular configuration
  private boolean checkSwitchStates(int states[]) {
    
    assert( states != null && states.length == mSwitchStates.length );
    for ( int k = 0 ; k < states.length ; k++ ) {
      if ( mSwitchStates[k] != states[k] ) return false;
    }
    return true;
    
  } // checkSwitchStates()
  
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

    // process the story event list
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();

      // one of the switches has been shot
      if ( event instanceof WallSwitch.EventStateChange ) {
        for ( int k = 0 ; k < mSwitches.length ; k++ ) {
          mSwitchStates[k] = mSwitches[k].getState();
        }
        it.remove();
      }

    } // for (event)
    
    // move the upper block strip
    if ( mUpperActivated && mUpperShift < kMaxBlockShift ) {
      if ( mUpperTimer > 0 ) {
        mUpperTimer--;
      } else {
        mUpperTimer = kTimeBlockShift;
        mUpperShift += 1;
        makeUpperBlocks(spriteManager);
      }
    }
    
    // move the lower block strip
    if ( mLowerActivated && mLowerShift < kMaxBlockShift ) {
      if ( mLowerTimer > 0 ) {
        mLowerTimer--;
      } else {
        mLowerTimer = kTimeBlockShift;
        mLowerShift += 1;
        makeLowerBlocks(spriteManager);
      }
    }
    
    // check whether switches have been triggered
    if ( !mUpperActivated && checkSwitchStates(kSwitchStatesUpper) ) {
      Env.sounds().play(Sounds.SUCCESS, 2);
      mUpperActivated = true;
      mUpperTimer = 5*kTimeBlockShift;
      storyEvents.add(new QuestStory.EventSaveGame());
    }
    if ( !mLowerActivated && checkSwitchStates(kSwitchStatesLower) ) {
      Env.sounds().play(Sounds.SUCCESS, 2);
      mLowerActivated = true;
      mLowerTimer = 5*kTimeBlockShift;
      storyEvents.add(new QuestStory.EventSaveGame());
    }

    // just for fun, blow up the critter
    if ( mCritter != null && checkSwitchStates(kSwitchStatesCritter) ) {
      mCritter.destroy(-1);
      mCritter = null;
      mCritterDead = true;
    }
    
  } // Room.advance()
  
} // class RoomB10
