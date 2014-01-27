/*
 *  RoomA05.java
 *  Copyright Simon Hern 2010
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BitBuffer;
import com.dishmoth.miniquests.game.BlockArray;
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

// the room "A05"
public class RoomA05 extends Room {

  // all visible blocks for the room
  private static final String kBlocksAll[][] = { { "1001001001",
                                                   "0000000000",
                                                   "0000000000",
                                                   "1001001001",
                                                   "0000000000",
                                                   "0000000000",
                                                   "1001001001",
                                                   "0000000000",
                                                   "0000000000",
                                                   "1111111111" } };
  
  // blocks for the room that are safe to walk on
  private static final String kBlocksTrue[][] = { { "1001001001",
                                                    "0        0",
                                                    "0        0",
                                                    "1001  1001",
                                                    "   0  0   ",
                                                    "   0  0   ",
                                                    "1001  1001",
                                                    "0         ",
                                                    "0         ",
                                                    "1111111111" } };
   
  // blocks for the room that become visible if the player falls
  private static final String kBlocksDeath[][] = { { "1  1  1  1",
                                                     "          ",
                                                     "          ",
                                                     "1  1  1  1",
                                                     "          ",
                                                     "          ",
                                                     "1  1  1  1",
                                                     "          ",
                                                     "          ",
                                                     "1111111111" } };
   
  // extra blocks for stairs
  private static final String kBlocksStairs[][] = { { " "," "," ","1" }, 
                                                    { " "," ","0"," " }, 
                                                    { " ","0"," "," " },
                                                    { "1"," "," "," " } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#c",   // orange
                                                  "#h" }; // blue
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.DOWN,  6,0, "#h",0, -1, RoomA04.class, 1), 
              new Exit(Env.RIGHT, 6,8, "#h",1, -1, RoomA03.class, 3) };

  // time for which the death blocks appear for
  private static final int kDeathTime = 20;
  
  // two different sets of blocks
  private BlockArray mAllBlocks,
                     mTrueBlocks,
                     mDeathBlocks;

  // two switches
  private FloorSwitch mSwitchVisible,
                      mSwitchComplete;

  // number of frames for which the death blocks are shown
  private int mDeathTimer;
  
  // whether this room has been completed already
  private boolean mCompleted;
  
  // constructor
  public RoomA05() {

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

    mAllBlocks = new BlockArray(kBlocksAll, kBlockColours, 0, 0, 0);
    mTrueBlocks = new BlockArray(kBlocksTrue, kBlockColours, 0, 0, 0);
    mDeathBlocks = new BlockArray(kBlocksDeath, kBlockColours, 0, 0, 0);
    
    spriteManager.addSprite(mCompleted ? mTrueBlocks : mAllBlocks);
    
    addBasicWalls(kExits, spriteManager);

    spriteManager.addSprite( new BlockArray(kBlocksStairs, kBlockColours,
                                            9, 3, 2) );
    
    if ( !mCompleted ) {
      mSwitchVisible = new FloorSwitch(3, 0, 0, "#a", "#h");
      mSwitchComplete = new FloorSwitch(9, 3, 2, "#h", "#h");
    
      spriteManager.addSprite(mSwitchVisible);
      spriteManager.addSprite(mSwitchComplete);
    }

    Liquid lava = new Liquid(0,0,-1, 1);
    lava.setLethalDepth(4);
    spriteManager.addSprite(lava);

    mDeathTimer = 0;
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mAllBlocks = mTrueBlocks = mDeathBlocks = null;
    mSwitchVisible = mSwitchComplete = null;
    
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
    boolean saveGameEvent = false;
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();
      
      if ( event instanceof FloorSwitch.EventStateChange ) {
        FloorSwitch.EventStateChange e = (FloorSwitch.EventStateChange)event;
        if ( e.mSwitch == mSwitchVisible ) {
          assert( !mCompleted );
          if ( mSwitchVisible.isOn() ) {
            spriteManager.removeSprite(mAllBlocks);
            spriteManager.addSprite(mTrueBlocks);
            Env.sounds().play(Sounds.SWITCH_ON);
          } else {
            spriteManager.removeSprite(mTrueBlocks);
            spriteManager.addSprite(mAllBlocks);
            Env.sounds().play(Sounds.SWITCH_OFF);
          }
        } else {
          assert( mSwitchComplete.isOn() );
          mSwitchComplete.freezeState(true);
          mSwitchVisible.freezeState(true);
          spriteManager.removeSprite(mAllBlocks);
          spriteManager.addSprite(mTrueBlocks);
          mCompleted = true;
          saveGameEvent = true;
          Env.sounds().play(Sounds.SWITCH_ON);
        }
        it.remove();
      }
      
    }
    if ( saveGameEvent ) storyEvents.add(new TinyStory.EventSaveGame());

    // replace the death blocks
    if ( mDeathTimer > 0 ) {
      assert( !mCompleted );
      if ( --mDeathTimer == 0 ) {
        spriteManager.removeSprite(mDeathBlocks);
        spriteManager.addSprite(mAllBlocks);
      }
    }
    
    // check player position
    if ( !mCompleted && mDeathTimer == 0 && mPlayer != null &&
         mPlayer.getYPos() >= 0 && 
         mPlayer.getYPos() < Room.kSize &&
         mPlayer.getZPos() == 0 &&
         !mTrueBlocks.isPlatform(mPlayer.getXPos(), 
                                 mPlayer.getYPos(), 
                                 mPlayer.getZPos()) ) {
      spriteManager.removeSprite(mAllBlocks);
      spriteManager.addSprite(mDeathBlocks);
      mDeathTimer = kDeathTime;
    }
    
  } // Room.advance()

} // class RoomA05
