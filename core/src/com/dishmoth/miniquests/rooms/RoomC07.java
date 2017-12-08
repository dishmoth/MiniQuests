/*
 *  RoomC07.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
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
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.QuestStory;

// the room "C07"
public class RoomC07 extends Room {

  // unique identifier for this room
  public static final String NAME = "C07";
  
  // the basic blocks for the room
  private static final String kBlocks[][] = { { "0000000000",
                                                "0        0",
                                                "0        0",
                                                "0 000000 0",
                                                "0 0    0 0",
                                                "0 0    0 0",
                                                "0 000000 0",
                                                "0        0",
                                                "0        0",
                                                "0000000000" },
                                                
                                              { "0010000000",
                                                "0010000000",
                                                "0011111111",
                                                "0000000000",
                                                "000    000",
                                                "110    000",
                                                "0100000002",
                                                "0111100002",
                                                "0000100002",
                                                "0000100000" } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#M",   // green
                                                  "#T",   // purple
                                                  "#T" }; // ??
  
  // details of exit/entry points for the room
  private static final Exit kExits[] 
          = { new Exit(Env.RIGHT, 2,0, "#T",0, -1, RoomC06.NAME, 1),
              new Exit(Env.RIGHT, 7,0, "#T",0, -1, RoomC08.NAME, 0),
              new Exit(Env.UP,    2,0, "#T",0, -1, RoomC09.NAME, 0),
              new Exit(Env.LEFT,  4,0, "#T",0, -1, RoomC11.NAME, 1),
              new Exit(Env.DOWN,  4,0, "zT",0, -1, RoomC12.NAME, 0) }; 
  
  // colour of the glowing path
  private static final char kPathColour = 'z';
  
  // glowing paths
  private static final String kGlowPath1[] = { "  +        ",
                                               "  +        ",
                                               "  +        ",
                                               "  ++++++++X" },
                              kGlowPath2[] = { "X++        ",
                                               "  +        ",
                                               "  ++++     ",
                                               "     +     ",
                                               "     +     " };

  // time before first door opens
  private static final int kFirstDoorDelay = 10;
  
  // the glowing paths
  private GlowPath mFirstPath,
                   mSecondPath;

  // first door has been unlocked
  private boolean mFirstDoorOpen;

  // second door has been dealt with (first path has been walked)
  private boolean mSecondDoorOpen;
  
  // third door has been unlocked (second path has been walked)
  private boolean mThirdDoorOpen;
  
  // general timer
  private int mTimer;
  
  // constructor
  public RoomC07() {

    super(NAME);

    mFirstDoorOpen = false;
    mSecondDoorOpen = false;
    mThirdDoorOpen = false;
    
  } // constructor

  // serialize the room state by writing bits to the specified buffer
  @Override
  public void save(BitBuffer buffer) {
    
    buffer.writeBit(mFirstDoorOpen);
    buffer.writeBit(mSecondDoorOpen);
    buffer.writeBit(mThirdDoorOpen);
    
  } // Room.save()

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  @Override
  public boolean restore(int version, BitBuffer buffer) { 
    
    if ( buffer.numBitsToRead() < 3 ) return false;
    mFirstDoorOpen  = buffer.readBit();
    mSecondDoorOpen = buffer.readBit();
    mThirdDoorOpen  = buffer.readBit();
    return true; 
    
  } // Room.restore() 
  
  // whether the path is complete
  // (note: this function may be called by RoomC09)
  public boolean firstPathComplete() { return mSecondDoorOpen; }
  
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

    mFirstPath = mSecondPath = null;
    
  } // Room.discardResources()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {

    addBasicWalls(kExits, spriteManager);

    spriteManager.addSprite(new BlockArray(kBlocks, kBlockColours, 0,0,-2));

    if ( !mFirstDoorOpen ) {
      kExits[1].mDoor.setClosed(true);
      kExits[2].mDoor.setClosed(true);
      Critter critters[] = { new Critter(5, 7, 0, Env.RIGHT, 
                                         new CritterTrack(kGlowPath1, 0,7)),
                             new Critter(2, 2, 0, Env.LEFT, 
                                         new CritterTrack(kGlowPath2, -1,0)) };
      for ( Critter c : critters ) {
        c.easilyKilled(true);
        c.setColour(3);
        spriteManager.addSprite(c);
      }
    }
    
    RoomC08 roomRight = (RoomC08)findRoom(RoomC08.NAME);
    assert( roomRight != null );
    boolean firstPathAvailable = roomRight.pathComplete();
    if ( firstPathAvailable ) {
      mFirstPath = new GlowPath(kGlowPath1, 0, 7, 0, kPathColour);
      if ( mSecondDoorOpen ) {
        mFirstPath.setComplete();
      }
      spriteManager.addSprite(mFirstPath);
    }
    
    RoomC11 roomLeft = (RoomC11)findRoom(RoomC11.NAME);
    assert( roomLeft != null );
    boolean secondPathAvailable = roomLeft.pathComplete();
    if ( secondPathAvailable ) {
      mSecondPath = new GlowPath(kGlowPath2, -1, 0, 0, kPathColour);
      if ( mThirdDoorOpen ) {
        mSecondPath.setComplete();
      }
      spriteManager.addSprite(mSecondPath);
    } else {
      kExits[3].mDoor.setClosed(true);      
    }

    if ( !mThirdDoorOpen ) {
      kExits[4].mDoor.setClosed(true);
    }
    
    mTimer = 0;
    
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

    // open the first two doors when the critters are dead
    if ( !mFirstDoorOpen ) {
      if ( spriteManager.findSpriteOfType(Critter.class) == null ) {
        if ( mTimer == 0 ) {
          mTimer = kFirstDoorDelay;
        } else if ( --mTimer == 0 ) {
          mFirstDoorOpen = true;
          kExits[1].mDoor.setClosed(false);
          kExits[2].mDoor.setClosed(false);
          Env.sounds().play(Sounds.SUCCESS);
          storyEvents.add(new QuestStory.EventSaveGame());
        }
      }
    }
    
    // check the first path
    if ( mFirstPath != null && !mSecondDoorOpen && mFirstPath.complete() ) {
      mSecondDoorOpen = true;
      //kExits[2].mDoor.setClosed(false);
      //Env.sounds().play(Sounds.SUCCESS);
    }
    
    // check the second path
    if ( mSecondPath != null && !mThirdDoorOpen && mSecondPath.complete() ) {
      mThirdDoorOpen = true;
      kExits[4].mDoor.setClosed(false);
      Env.sounds().play(Sounds.SUCCESS);
      storyEvents.add(new QuestStory.EventSaveGame());
    }
        
  } // Room.advance()

} // class RoomC07
