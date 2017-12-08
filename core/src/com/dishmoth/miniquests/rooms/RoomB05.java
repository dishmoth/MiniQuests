/*
 *  RoomB05.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BitBuffer;
import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.QuestStory;
import com.dishmoth.miniquests.game.Triffid;

// the room "B05"
public class RoomB05 extends Room {

  // unique identifier for this room
  public static final String NAME = "B05";
  
  // blocks for the room
  private static final String kBlocks[][] = { { "          ",
                                                "000000000 ",
                                                "0   0   0 ",
                                                "0   0   0 ",
                                                "0  000  0 ",
                                                "0  000  0 ",
                                                "0  000  00",
                                                "0   0   0 ",
                                                "0   0   0 ",
                                                "000000000 " } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#d" }; // 
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.RIGHT, 3,13, "#d",1, 0, RoomB04.NAME, 1),
              new Exit(Env.DOWN,  1,13, "#d",1, 0, RoomB06.NAME, 0) };

  // details of different camera height levels
  private static final CameraLevel kCameraLevels[]
                                     = { new CameraLevel(10, -100, +100) };
  
  // which triffids are still alive
  private boolean mDoneTop,
                  mDoneBottom;

  // references to the triffids
  private Triffid mTriffids[];
  
  // constructor
  public RoomB05() {

    super(NAME);

    mDoneTop = false;
    mDoneBottom = false;

  } // constructor

  // serialize the room state by writing bits to the specified buffer
  @Override
  public void save(BitBuffer buffer) {
    
    buffer.writeBit(mDoneTop);
    buffer.writeBit(mDoneBottom);
    
  } // Room.save()

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  @Override
  public boolean restore(int version, BitBuffer buffer) { 
    
    if ( buffer.numBitsToRead() < 2 ) return false;
    mDoneTop    = buffer.readBit();
    mDoneBottom = buffer.readBit();
    return true;
    
  } // Room.restore() 
  
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

    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,13) );
    addBasicWalls(kExits, spriteManager);

    mTriffids = new Triffid[3];
    
    mTriffids[0] = new Triffid(4, 4, 13, Env.RIGHT);
    mTriffids[0].setRotateRate(-35);
    
    if ( !mDoneBottom ) mTriffids[1] = new Triffid(4, 0, 13, Env.LEFT);
    if ( !mDoneTop )    mTriffids[2] = new Triffid(4, 8, 13, Env.RIGHT);
    
    for ( Triffid tr : mTriffids ) {
      if ( tr != null ) {
        tr.setFullyGrown();
        spriteManager.addSprite(tr);
      }
    }
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mTriffids = null;
    
  } // Room.discardResources()
  
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
    boolean saveGameEvent = false;
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();
      
      if ( event instanceof Triffid.EventKilled ) {
        Triffid tr = ((Triffid.EventKilled)event).mSource;
        if ( tr == mTriffids[1] ) mDoneBottom = true;
        if ( tr == mTriffids[2] ) mDoneTop = true;
        saveGameEvent = true;
        it.remove();
      }
    }
    if ( saveGameEvent ) storyEvents.add(new QuestStory.EventSaveGame());
    
  } // Room.advance()
  
} // class RoomB05
