/*
 *  RoomA06.java
 *  Copyright Simon Hern 2010
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BitBuffer;
import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Critter;
import com.dishmoth.miniquests.game.CritterTrack;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.FloorSwitch;
import com.dishmoth.miniquests.game.Liquid;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.TinyStory;
import com.dishmoth.miniquests.game.WallSwitch;

// the room "A06"
public class RoomA06 extends Room {

  // unique identifier for this room
  public static final String NAME = "A06";
  
  // main blocks for the room
  private static final String kBlocks[][] = { { " 00000000 ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "0000000000",
                                                "          ",
                                                "          ",
                                                "          ",
                                                " 00000000 ",
                                                "          " },
                                                
                                              { "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "         0",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " }, };
  
  // extra blocks that appear later
  private static final String kExtraBlocks[][][] = { { { "          ",
                                                         " 0   0  0 ",
                                                         "          ",
                                                         "          ",
                                                         "          ",
                                                         "          ",
                                                         "          ",
                                                         " 0  0   0 ",
                                                         "          ",
                                                         "          " } },
                                                     { { "          ",
                                                         " 0   0  0 ",
                                                         " 0   0  0 ",
                                                         "          ",
                                                         "          ",
                                                         "          ",
                                                         " 0  0   0 ",
                                                         " 0  0   0 ",
                                                         "          ",
                                                         "          " } },
                                                     { { "          ",
                                                         " 0   0  0 ",
                                                         " 0   0  0 ",
                                                         " 0   0  0 ",
                                                         "          ",
                                                         " 0  0   0 ",
                                                         " 0  0   0 ",
                                                         " 0  0   0 ",
                                                         "          ",
                                                         "          " } } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#h" };   // blue 
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.LEFT,  5,0, "#h",0, -1, RoomA03.NAME, 4), 
              new Exit(Env.RIGHT, 5,2, "#h",2, -1, RoomA07.NAME, 0) };

  // paths followed by enemies
  private static final CritterTrack 
       kCritterTrack1 = new CritterTrack(new String[]{ "          ",
                                                       "          ",
                                                       "          ",
                                                       "          ",
                                                       " ++++++++ ",
                                                       " +  +   + ",
                                                       " +  +   + ",
                                                       " +  +   + ",
                                                       " ++++++++ ",
                                                       "          " }),
       kCritterTrack2 = new CritterTrack(new String[]{ " ++++++++ ",
                                                       " +   +  + ",
                                                       " +   +  + ",
                                                       " +   +  + ",
                                                       " ++++++++ ",
                                                       "          ",
                                                       "          ",
                                                       "          ",
                                                       "          ",
                                                       "          " });

  // speed at which the extra blocks appear
  private static final int kExtraBlocksDelay = 1;
  
  // no switch sounds initially
  private static final int kNoSoundDelay = 10;
  
  // references to the room's floor switch objects
  private FloorSwitch mSwitch1,
                      mSwitch2;
  private boolean mSwitchesDone;

  // references to the room's wall switch objects
  private WallSwitch mButton1,
                     mButton2;
  private boolean mButtonsDone;

  // extra blocks appear when the switches are triggered
  private BlockArray mExtraBlocks;
  private int mExtraBlocksIndex;
  private int mExtraBlocksTimer;
  
  // silence for the first few frames (no switch sounds)
  private int mNoSoundTimer;
  
  // constructor
  public RoomA06() {

    super(NAME);

    mSwitchesDone = false;
    mButtonsDone = false;
    
  } // constructor

  // serialize the room state by writing bits to the specified buffer
  @Override
  public void save(BitBuffer buffer) {
    
    buffer.writeBit(mSwitchesDone);
    buffer.writeBit(mButtonsDone);
    
  } // Room.save()

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  @Override
  public boolean restore(int version, BitBuffer buffer) { 
    
    if ( buffer.numBitsToRead() < 2 ) return false;
    mSwitchesDone = buffer.readBit();
    mButtonsDone  = buffer.readBit();
    return true; 
    
  } // Room.restore() 
  
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
    
    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0, 0, 0) );
    
    addBasicWalls(kExits, spriteManager);
    if ( !mButtonsDone ) kExits[1].mDoor.setClosed(true);

    int x1 = 1,
        x2 = 8;
    if ( mSwitchesDone ) {
      x1 = Env.randomInt(2, 5);
      x2 = Env.randomInt(4, 7);
    }
    spriteManager.addSprite( new Critter(x1, 1, 0, Env.RIGHT, kCritterTrack1) );
    spriteManager.addSprite( new Critter(x2, 9, 0, Env.LEFT, kCritterTrack2) );

    mSwitch1 = new FloorSwitch(8, 1, 0, "hD", "#h");
    spriteManager.addSprite( mSwitch1 );

    mSwitch2 = new FloorSwitch(8, 9, 0, "hD", "#h");
    spriteManager.addSprite( mSwitch2 );

    mExtraBlocks = null;
    mExtraBlocksIndex = -1;
    mExtraBlocksTimer = 0;
    
    if ( mSwitchesDone ) switchesTriggered(spriteManager, true);

    Liquid lava = new Liquid(0,0,-9, 1);
    spriteManager.addSprite(lava);
    
    mNoSoundTimer = kNoSoundDelay;
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {
    
    mSwitch1 = mSwitch2 = null;
    mButton1 = mButton2 = null;
    mExtraBlocks = null;
    
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

    // process the story event list
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();
      
      if ( event instanceof FloorSwitch.EventStateChange ) {
        FloorSwitch.EventStateChange e = (FloorSwitch.EventStateChange)event;
        if ( mNoSoundTimer == 0 ) {
          if ( e.mSwitch.isOn() ) Env.sounds().play(Sounds.SWITCH_ON);
          else                    Env.sounds().play(Sounds.SWITCH_OFF);
        }
        it.remove();
      }
      
      if ( event instanceof WallSwitch.EventStateChange ) {
        it.remove();
      }
      
    }

    // animate appearing blocks
    advanceExtraBlocks(spriteManager);
    
    // check floor switches
    if ( !mSwitchesDone && mSwitch1.isOn() && mSwitch2.isOn() ) {
      mSwitchesDone = true;
      switchesTriggered(spriteManager, false);
      storyEvents.add(new TinyStory.EventSaveGame());
      Env.sounds().play(Sounds.SUCCESS, 3);
    }

    // check wall switches
    if ( mSwitchesDone && !mButtonsDone && 
         mButton1.getState() > 0 && mButton2.getState() > 0 ) {
      mButtonsDone = true;
      kExits[1].mDoor.setClosed(false);
      storyEvents.add(new TinyStory.EventSaveGame());
      Env.sounds().play(Sounds.SUCCESS, 3);
    }
    
    // timer
    if ( mNoSoundTimer > 0 ) mNoSoundTimer--;
    
  } // Room.advance()

  // change the room after switches have been triggered
  private void switchesTriggered(SpriteManager spriteManager, 
                                 boolean immediate) {

    mSwitch1.freezeState(true);
    mSwitch2.freezeState(true);
    
    mExtraBlocksIndex = ( immediate ? 2 : 0 );
    mExtraBlocksTimer = kExtraBlocksDelay;
    
    assert( mExtraBlocks == null );
    mExtraBlocks = new BlockArray(kExtraBlocks[mExtraBlocksIndex],
                                  kBlockColours, 0, 0, 0);
    spriteManager.addSprite( mExtraBlocks );

    String buttonColours[] = { "ju", "7u" };
    mButton1 = new WallSwitch(Env.RIGHT, 1, 2, buttonColours, false);
    mButton2 = new WallSwitch(Env.RIGHT, 9, 2, buttonColours, false);
    if ( mButtonsDone ) {
      assert( immediate );
      mButton1.setState(1);
      mButton2.setState(1);
    }
    spriteManager.addSprite( mButton1 );
    spriteManager.addSprite( mButton2 );
    
  } // switchesTriggered()

  // animate the extra blocks as they appear
  private void advanceExtraBlocks(SpriteManager spriteManager) {
    
    if ( mExtraBlocksIndex < 0 || mExtraBlocksIndex >= 2 ) return;

    if ( --mExtraBlocksTimer > 0 ) return;
    mExtraBlocksTimer = kExtraBlocksDelay;
    
    assert( mExtraBlocks != null );
    mExtraBlocksIndex += 1;
    
    spriteManager.removeSprite(mExtraBlocks);
    mExtraBlocks = new BlockArray(kExtraBlocks[mExtraBlocksIndex],
                                  kBlockColours, 0, 0, 0);
    spriteManager.addSprite( mExtraBlocks );
    
  } // advanceExtraBlocks()
  
} // class RoomA06
