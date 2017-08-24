/*
 *  RoomC09.java
 *  Copyright Simon Hern 2013
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BitBuffer;
import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Critter;
import com.dishmoth.miniquests.game.CritterTrack;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.GlowPath;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.Spinner;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.QuestStory;

// the room "C09"
public class RoomC09 extends Room {

  // unique identifier for this room
  public static final String NAME = "C09";
  
  // the basic blocks for the room
  private static final String kBlocks[][] = { { "2222212222",
                                                "2000010002",
                                                "2000010002",
                                                "2000010002",
                                                "1111111111",
                                                "2000010002",
                                                "2000010002",
                                                "0000010002",
                                                "0000010002",
                                                "0022212222" } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#V",   // blue 
                                                  "FV",   // grey
                                                  "#c" }; // orange
  
  // details of exit/entry points for the room
  private static final Exit kExits[] 
          = { new Exit(Env.DOWN,  2,0, "#c",0, -1, RoomC07.NAME, 2),
              new Exit(Env.LEFT,  3,0, "sc",0, -1, RoomC10.NAME, 0),
              new Exit(Env.RIGHT, 3,0, "#V",0, -1, RoomC08.NAME, 1) }; 
  
  // colour of the glowing path
  private static final char kPathColour = 's';
  
  // glowing path
  private static final String kGlowPath[] = { "++++++++++",
                                              "+        +",
                                              "+        +",
                                              "+        +",
                                              "+        +",
                                              "+        +",
                                              "+        +",
                                              "         +",
                                              "         +",
                                              "  ++++++++",
                                              "  X       " };
  
  // paths taken by spinning baddies
  private static final int kWait = 17;
  private static final int kSpinnerTracks[][][] 
                                    = { { {5,9,kWait}, {5,0,kWait} },
                                        { {9,5,kWait}, {0,5,kWait} } };
  
  // delay before the second spinner starts moving
  private static final int kSpinnerOffsetDelay = 17;

  // path followed by enemies
  private static final CritterTrack kCritterTrack 
                     = new CritterTrack(new String[]{ "+++++ ++++",
                                                      "+   + +  +",
                                                      "+   + +  +",
                                                      "+++++ ++++",
                                                      "          ",
                                                      "      ++++",
                                                      "      +  +",
                                                      "      +  +",
                                                      "      +  +",
                                                      "      ++++" });
  
  // whether the path has been walked yet
  private boolean mPathDone;
  
  // the glowing path
  private GlowPath mPath;

  // references to spinners
  private Spinner[] mSpinners;

  // delay on the second spinner
  private int mSpinnerTimer;
  
  // constructor
  public RoomC09() {

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
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mPath = null;
    mSpinners = null;
    
  } // Room.discardResources()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {

    addBasicWalls(kExits, spriteManager);

    spriteManager.addSprite(new BlockArray(kBlocks, kBlockColours, 0,0,0));

    RoomC08 roomRight = (RoomC08)findRoom(RoomC08.NAME);
    assert( roomRight != null );
    boolean doorLocked = roomRight.pathComplete();
    if ( doorLocked ) kExits[2].mDoor.setClosed(true);
    
    RoomC07 roomDown = (RoomC07)findRoom(RoomC07.NAME);
    assert( roomDown != null );
    boolean pathAvailable = roomDown.firstPathComplete();
    if ( pathAvailable || mPathDone ) {
      mPath = new GlowPath(kGlowPath, 0, -1, 0, kPathColour);
      if ( mPathDone ) {
        mPath.setComplete();
      }
      spriteManager.addSprite(mPath);
    }
  
    if ( !mPathDone ) {
      kExits[1].mDoor.setClosed(true);
    }
    
    mSpinners = new Spinner[]{ new Spinner(5, 0, 0, false),
                               new Spinner(0, 5, 0, true) };
    for ( Spinner sp : mSpinners ) {
      spriteManager.addSprite(sp);
    }
    mSpinners[0].setTargets(kSpinnerTracks[0], true);
    mSpinnerTimer = kSpinnerOffsetDelay;
    
    Critter critters[] = { new Critter(6, 0, 0, Env.DOWN, kCritterTrack),
                           new Critter(7, 9, 0, Env.LEFT, kCritterTrack),
                           new Critter(2, 9, 0, Env.LEFT, kCritterTrack) };
    for ( Critter critter : critters ) {
      spriteManager.addSprite(critter);
    }
    
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

    // start the second spinner after a delay
    if ( mSpinnerTimer > 0 ) {
      if ( --mSpinnerTimer == 0 ) {
        mSpinners[1].setTargets(kSpinnerTracks[1], true);
      }
    }

    // check the path
    if ( !mPathDone && mPath != null && mPath.complete() ) {
      mPathDone = true;
      kExits[1].mDoor.setClosed(false);
      Env.sounds().play(Sounds.SUCCESS);
      storyEvents.add(new QuestStory.EventSaveGame());
    }
        
  } // Room.advance()

} // class RoomC09
