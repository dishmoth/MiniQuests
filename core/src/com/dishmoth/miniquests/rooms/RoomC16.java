/*
 *  RoomC16.java
 *  Copyright Simon Hern 2013
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BitBuffer;
import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Chest;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.GlowPath;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.QuestStory;

// the room "C16"
public class RoomC16 extends Room {

  // unique identifier for this room
  public static final String NAME = "C16";
  
  // main blocks for the room
  private static final String kLowerBlocks[] = { "0000000000",
                                                 "0000000000",
                                                 "0000     0",
                                                 "00        ",
                                                 "00        ",
                                                 "00        ",
                                                 "00        ",
                                                 "00        ",
                                                 "00        ",
                                                 "0         " };
  private static final String kBlocks[][] = { kLowerBlocks, kLowerBlocks,
                                              kLowerBlocks, kLowerBlocks,
                                              kLowerBlocks, kLowerBlocks,
                                                
                                              { "0000000000",
                                                "000000  00",
                                                "0000      ",
                                                "00        ",
                                                "00        ",
                                                "00        ",
                                                "00        ",
                                                "00        ",
                                                "00        ",
                                                "0         " },
                                                
                                              { "0000000000",
                                                "112000  00",
                                                "0000      ",
                                                "00        ",
                                                "00        ",
                                                "00        ",
                                                "0333      ",
                                                "00        ",
                                                "00        ",
                                                "0         " },
                                                
                                              { "     00000",
                                                "         0",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "        00",
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
  private static final String kBlockColours[] = { "lL",   // purple
                                                  "#A",   // green
                                                  "LA",   // green with centre
                                                  "tB" }; // blue
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.LEFT, 8,0, "#A",0, -1, RoomC15.NAME, 1) };

  // the blocks for the room's lift
  private static final String kLiftLayer[] = { "33333", "33333", "33333",
                                               "33333", "33333" }; 
  private static final String kLiftBlocks[][] = { kLiftLayer, kLiftLayer,
                                                  kLiftLayer };
  
  // glowing pathway
  private static final String kGlowPath[] = { "X+++" };
  
  // parameters controlling the lift
  private static final int kLiftZMin  = -24,
                           kLiftZMax  = -4;
  private static final int kLiftDelay = 3;
  
  // times at which things happen
  private static final int kGameEndsDelay   = 50,
                           kChestSoundDelay = kGameEndsDelay - 5,
                           kChestOpenDelay  = kChestSoundDelay - 10;  
  
  // reference to the lift blocks
  private BlockArray mLift;
  
  // how long until the lift moves again
  private int mLiftTimer;

  // reference to the chest object
  private Chest mChest;

  // reference to the glowing path
  private GlowPath mPath;
  
  // whether the path has been completed yet
  private boolean mPathDone;
  
  // countdown once the chest is opened
  private int mEndTimer;
  
  // constructor
  public RoomC16() {

    super(NAME);

    mPathDone = false;
    
  } // constructor

  // serialize the room state by writing bits to the specified buffer
  @Override
  public void save(BitBuffer buffer) {
    
    buffer.writeBit(mPathDone);
    
  } // Room.save()

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  @Override
  public boolean restore(int version, BitBuffer buffer) { 
    
    if ( buffer.numBitsToRead() < 1 ) return false;
    mPathDone = buffer.readBit();
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
    
    spriteManager.addSprite( new BlockArray(kBlocks,kBlockColours, 0,0,-14) );
    
    addBasicWalls(kExits, spriteManager);

    int liftZ = (mPathDone ? kLiftZMax : kLiftZMin);
    mLift = new BlockArray(kLiftBlocks,kBlockColours, 4,1,liftZ);
    spriteManager.addSprite(mLift);
    
    mChest = new Chest(5, 2, liftZ+4, Env.LEFT);
    spriteManager.addSprite(mChest);
    
    RoomC15 roomLeft = (RoomC15)findRoom(RoomC15.NAME);
    assert( roomLeft != null );
    boolean pathAvailable = roomLeft.pathComplete();
    if ( pathAvailable ) {
      mPath = new GlowPath(kGlowPath, -1,8,0, 'U');
      if ( mPathDone ) mPath.setComplete();
      spriteManager.addSprite(mPath);
    } else {
      mPath = null;
    }
    
    mLiftTimer = 0;
    mEndTimer = 0;
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mLift = null;
    mChest = null;
    mPath = null;
    
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

    // once the chest is open
    if ( mEndTimer > 0 ) {
      mEndTimer--;
      if ( mEndTimer == kChestSoundDelay ) {
        Env.sounds().play(Sounds.CHEST);        
      } else if ( mEndTimer == kChestOpenDelay ) {
        mChest.setOpen(true);
      } else if ( mEndTimer == 0 ) {
        storyEvents.add(new QuestStory.EventPlayerWins());
      }
    }

    // check for opening the chest
    if ( mEndTimer == 0 && !mChest.isOpen() && mPlayer != null &&
         mPlayer.getXPos() == 4 && mPlayer.getYPos() == 3 ) {
      mPlayer.mAdvanceDisabled = true;
      mEndTimer = kGameEndsDelay;
    }
    
    // animate the lift
    if ( mPathDone && mLift.getZPos() < kLiftZMax ) {
      if ( --mLiftTimer <= 0 ) {
        mLiftTimer = kLiftDelay;
        mLift.shiftPos(0, 0, +1);
        mChest.shiftPos(0, 0, +1);
        if ( mLift.getZPos() == kLiftZMax ) {
          Env.sounds().play(Sounds.SWITCH_OFF);
        }
      }
    }
    
    // check the path
    if ( !mPathDone && mPath != null && mPath.complete() ) {
      mPathDone = true;
      mLiftTimer = 0;
      Env.sounds().play(Sounds.SUCCESS);
      storyEvents.add(new QuestStory.EventSaveGame());
    }
    
  } // Room.advance()

} // class RoomC16
