/*
 *  RoomD03.java
 *  Copyright Simon Hern 2014
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.Spikes;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.Statue;
import com.dishmoth.miniquests.game.StoryEvent;

// the room "D03"
public class RoomD03 extends Room {

  // unique identifier for this room
  public static final String NAME = "D03";
  
  // main blocks for the room
  private static final String kBlocks[][] = { { "0000010000",
                                                "0000010000",
                                                "0000010000",
                                                "0000010000",
                                                "1111111111",
                                                "0000010000",
                                                "0000010000",
                                                "0000010000",
                                                "0000010000",
                                                "0000010000" },
                                                
                                              { "  2    2  ",
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
  private static final String kBlockColours[] = { "Vh",   // blue and blue
                                                  "#P",   // blue and white
                                                  "#V" }; //
  
  // details of exit/entry points for the room 
  private static final Exit kExits[][]
        = { { new Exit(Env.UP,    5,0, "#P",0, -1, RoomD01.NAME, 1),
              new Exit(Env.DOWN,  5,0, "#P",0, -1, RoomD02.NAME, 0),
              new Exit(Env.LEFT,  5,0, "#P",0, -1, RoomD07.NAME, 0),
              new Exit(Env.RIGHT, 5,0, "#P",0, -1, RoomD18.NAME, 0) },
              
            { new Exit(Env.UP,    5,0, "#P",0, -1, "",0),
              new Exit(Env.DOWN,  5,0, "#P",0, -1, "",0),
              new Exit(Env.LEFT,  5,0, "#P",0, -1, "",0),
              new Exit(Env.RIGHT, 5,0, "#P",0, -1, RoomD02.NAME, 1) },
              
            { new Exit(Env.UP,    5,0, "#P",0, -1, RoomD02.NAME, 2),
              new Exit(Env.DOWN,  5,0, "#P",0, -1, "",0),
              new Exit(Env.LEFT,  5,0, "#P",0, -1, RoomD12.NAME, 1),
              new Exit(Env.RIGHT, 5,0, "#P",0, -1, RoomD13.NAME, 0) },
              
            { new Exit(Env.UP,    5,0, "#P",0, -1, "",0),
              new Exit(Env.DOWN,  5,0, "#P",0, -1, "",0),
              new Exit(Env.LEFT,  5,0, "#P",0, -1, RoomD02.NAME, 3),
              new Exit(Env.RIGHT, 5,0, "#P",0, -1, "",0) } };
              
  // how long things last for
  private static final int kStatueDelay    = 30,
                           kStatueEndDelay = 18;
  private static final int kSpikeDelay     = 22;

  // the current exits, based on room D02's twist
  private Exit mExits[];
  
  // whether the spikes have been disabled yet
  private boolean mSpikesDone;
  
  // references to the statue objects
  private Statue mStatues[];

  // how long until the statues return to normal
  private int mStatueTimers[];
  
  // references to spike objects
  private Spikes mSpikes[];

  // how long until the spikes are triggered
  private int mSpikeTimer;
  
  // constructor
  public RoomD03() {

    super(NAME);

    mSpikesDone = false;
    
  } // constructor

  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < kExits.length );
    setPlayerAtExit(mExits[entryPoint]);
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
    
    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,0) );
    
    addBasicWalls(mExits, spriteManager);

    mStatues = new Statue[]{ new Statue(2,9,2, Env.DOWN, 0),
                             new Statue(7,9,2, Env.DOWN, 0) };
    for ( int k = 0 ; k < mStatues.length ; k++ ) {
      spriteManager.addSprite( mStatues[k] );
    }
    mStatueTimers = new int[ mStatues.length ];
    
    mSpikes = new Spikes[] { new Spikes(2,0,0, 2,3, true, "u0"),
                             new Spikes(4,1,0, 3,2, true, "u0"),
                             new Spikes(7,0,0, 2,3, true, "u0") };
    for ( int k = 0 ; k < mSpikes.length ; k++ ) {
      if ( k > 0 ) mSpikes[k].setSilent(true);
      spriteManager.addSprite( mSpikes[k] );
    }
    mSpikeTimer = kSpikeDelay/3;

    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mStatues = null;
    mSpikes = null;
    
  } // Room.discardResources()
  
  // update the room (events may be added or processed)
  @Override
  public void advance(LinkedList<StoryEvent> storyEvents,
                      SpriteManager          spriteManager) {

    final int exitIndex = checkExits(mExits);
    if ( exitIndex != -1 ) {
      storyEvents.add(new EventRoomChange(mExits[exitIndex].mDestination,
                                          mExits[exitIndex].mEntryPoint));
      return;
    }

    if ( !mSpikesDone ) {
      for ( int k = 0 ; k < mStatues.length ; k++ ) {
        if ( mStatues[k].isHit() ) {
          mStatues[k].setHit(false);
          mStatueTimers[k] = kStatueDelay;
          mStatues[k].setColour(3);
        }
      }
    }

    for ( int k = 0 ; k < mStatues.length ; k++ ) {
      if ( mStatueTimers[k] > 0 ) {
        if ( --mStatueTimers[k] == 0 ) mStatues[k].setColour(0);
      }
    }

    if ( !mSpikesDone ) {
      if ( mStatueTimers[0] > 0 && mStatueTimers[1] > 0 ) {
        for ( int k = 0 ; k < mStatues.length ; k++ ) {
          mStatueTimers[k] = kStatueEndDelay;
        }
        Env.sounds().play(Sounds.SUCCESS, 5);
        mSpikesDone = true;
      }
    }
    
    if ( !mSpikesDone ) {
      if ( --mSpikeTimer <= 0 ) {
        for ( Spikes spikes : mSpikes ) {
          spikes.trigger();
        }
        mSpikeTimer = kSpikeDelay;
      }
    }
    
  } // Room.advance()

} // class RoomD03
