/*
 *  RoomD12.java
 *  Copyright Simon Hern 2014
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.CritterTrack;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.Spikes;
import com.dishmoth.miniquests.game.Spook;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.Statue;
import com.dishmoth.miniquests.game.StoryEvent;

// the room "D12"
public class RoomD12 extends Room {

  // unique identifier for this room
  public static final String NAME = "D12";
  
  // main blocks for the room
  private static final String kBlocks[][] = { { "0000000000",
                                                "0000000000",
                                                "0011111000",
                                                "0000000000",
                                                "0011111000",
                                                "0000000000",
                                                "0011111000",
                                                "0000000000",
                                                "0011111000",
                                                "0000000000" },
                                                
                                              { "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "         2",
                                                "         2",
                                                "         2",
                                                "         2",
                                                "         2" },
                                                
                                              { "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "         2",
                                                "         2",
                                                "         2",
                                                "         2",
                                                "         2" },
                                                
                                              { "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "         2",
                                                "         2",
                                                "         2",
                                                "         2",
                                                "         2" } };

  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "Ke",
                                                  "Ee",
                                                  "Ke" }; 
  
  // details of exit/entry points for the room 
  private static final Exit kExits[][] 
          = { { new Exit(Env.LEFT,  8,0, "Ke",0, -1, RoomD11.NAME, 1),
                new Exit(Env.RIGHT, 8,0, "#e",0, -1, "",0) },
  
              { new Exit(Env.LEFT,  8,0, "Ke",0, -1, RoomD11.NAME, 1),
                new Exit(Env.RIGHT, 8,0, "#e",0, -1, RoomD04.NAME, 2) },
              
              { new Exit(Env.LEFT,  8,0, "Ke",0, -1, RoomD11.NAME, 1),
                new Exit(Env.RIGHT, 8,0, "#e",0, -1, "",0) },
              
              { new Exit(Env.LEFT,  8,0, "Ke",0, -1, RoomD11.NAME, 1),
                new Exit(Env.RIGHT, 8,0, "#e",0, -1, "",0) } };
  
  // the current exits, based on room D02's twist
  private Exit mExits[];

  // path the monsters follow
  private static final String kPath[] = { "#####" };
  
  // interval between monsters
  private static final int kSpookDelay   = 60,
                           kSpookStagger = 6;

  // maximum gap between killing spooks
  private static final int kKillDelay = 8;

  // time intervals once the room is completed
  private static final int kEndDelay          = 35,
                           kStatueFlashStart  = 15,
                           kStatueFlashDelay  = 7,
                           kSpikeRetreatDelay = 5;
  
  // whether the room is complete
  private boolean mRoomDone;
  
  // counter controlling monster respawning
  private int mSpookTimer;

  // which spook was just killed (-1 or not recent or in order)
  private int mSpookKilled;
  
  // countdown for killing the spooks in sequence
  private int mKillTimer;

  // countdown until the room is fully finished
  private int mEndTimer;
  
  // references to the statues
  private Statue mStatues[];
  
  // reference to the spikes
  private Spikes mSpikes;
  
  // constructor
  public RoomD12() {

    super(NAME);

    mRoomDone = false;
    
  } // constructor

  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < mExits.length );
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

    if ( mRoomDone ) {
      mSpikes = null;
    } else {
      mSpikes = new Spikes(8,6,0, 2,4, true, "u0");
      spriteManager.addSprite(mSpikes);
    }
    
    mStatues = new Statue[]{ new Statue(9,0,6, Env.LEFT, 3),
                             new Statue(9,4,6, Env.LEFT, 3) };
    for ( Statue s : mStatues ) spriteManager.addSprite(s);
    
    mSpookTimer = 0;
    mSpookKilled = -1;
    mKillTimer = 0;

    mEndTimer = 0;

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

    // check exits
    final int exitIndex = checkExits(mExits);
    if ( exitIndex != -1 ) {
      storyEvents.add(new EventRoomChange(mExits[exitIndex].mDestination,
                                          mExits[exitIndex].mEntryPoint));
      return;
    }

    // check spook events
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();
      if ( event instanceof Spook.EventKilled ) {
        int y = (((Spook.EventKilled)event).mSpook).getYPos();
        int n = (y - 1)/2;
        if ( n == mSpookKilled+1 ) {
          mSpookKilled += 1;
          mKillTimer = kKillDelay;
          if ( mSpookKilled == 3 ) {
            mRoomDone = true;
            mEndTimer = kEndDelay;
          }
        } else {
          mSpookKilled = -1;
        }
        it.remove();
      }
    }    
    
    // the spooks must be killed quickly
    if ( mKillTimer > 0 ) {
      if ( --mKillTimer == 0 ) mSpookKilled = -1;
    }
    
    // spawn monsters
    if ( !mRoomDone ) {
      if ( mSpookTimer%kSpookStagger == 0 ) {
        int n = mSpookTimer/kSpookStagger;
        if ( n < 4 ) {
          int y = 1 + 2*n;
          Spook spook = new Spook(6,y,0, Env.LEFT, 
                                  new CritterTrack(kPath, 2,y));
          spook.vanishAfterSteps(3);
          spriteManager.addSprite(spook);
        }
      }
      mSpookTimer = (mSpookTimer+1) % kSpookDelay;
    }
    
    // check spikes
    if ( !mRoomDone ) {
      assert( mSpikes != null );
      if ( mPlayer != null && 
           mPlayer.getXPos() >= 8 && mPlayer.getYPos() >= 6 ) {
        mSpikes.trigger();
      }
    }

    // tidy the room once complete
    if ( mRoomDone && mEndTimer > 0 ) {
      mEndTimer -= 1;
      if ( mEndTimer == kEndDelay-kStatueFlashStart ) {
        for ( Statue s : mStatues ) s.setColour(0);
        Env.sounds().play(Sounds.SUCCESS);
      } 
      if ( mEndTimer == kEndDelay-kStatueFlashStart-kStatueFlashDelay ) {
        for ( Statue s : mStatues ) s.setColour(3);
      }
      if ( mEndTimer%kSpikeRetreatDelay == 0 ) {
        int n = mEndTimer/kSpikeRetreatDelay;
        if ( n < 4 ) {
          spriteManager.removeSprite(mSpikes);
          if ( n == 0 ) {
            mSpikes = null;
          } else {
            mSpikes = new Spikes(8,10-n,0, 2,n, true, "u0");
            spriteManager.addSprite(mSpikes);
          }
        }
      }
    }

  } // Room.advance()

} // class RoomD12
