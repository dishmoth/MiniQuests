/*
 *  RoomD18.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Chest;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.Spikes;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.TinyStory;

// the room "D18"
public class RoomD18 extends Room {

  // unique identifier for this room
  public static final String NAME = "D18";
  
  // main blocks for the room
  private static final String kBlocks[][] = { { "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000" },
                                              
                                              { "          ",
                                                "          ",
                                                "   11111  ",
                                                "   11111  ",
                                                "   11111  ",
                                                "   11111  ",
                                                "   11111  ",
                                                "     1    ",
                                                "     1    ",
                                                "          " },
                                              
                                              { "          ",
                                                "          ",
                                                "   11111  ",
                                                "   11111  ",
                                                "   11111  ",
                                                "   11111  ",
                                                "   11111  ",
                                                "     1    ",
                                                "          ",
                                                "          " },
                                              
                                              { "          ",
                                                "          ",
                                                "   11111  ",
                                                "   11111  ",
                                                "   11111  ",
                                                "   11111  ",
                                                "   11111  ",
                                                "          ",
                                                "          ",
                                                "          " } };

  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#U",
                                                  "#U" }; 
  
  // details of exit/entry points for the room 
  private static final Exit kExits[][] 
          = { { new Exit(Env.LEFT, 5,0, "#U",0, -1, RoomD03.NAME, 3)
                /*new Exit(Env.DOWN, 5,0, "#U",0, -1, RoomD02.NAME, 6)*/},
  
              { new Exit(Env.LEFT, 5,0, "#U",0, -1, "",0) },
              
              { new Exit(Env.LEFT, 5,0, "#U",0, -1, "",0) },
              
              { new Exit(Env.LEFT, 5,0, "#U",0, -1, "",0) } };
  
  // times at which things happen
  private static final int kGameEndsDelay   = 50,
                           kChestSoundDelay = kGameEndsDelay - 5,
                           kChestOpenDelay  = kChestSoundDelay - 10;  
  
  // the current exits, based on room D02's twist
  private Exit mExits[];
  
  // spikes around the chest
  private Spikes mSpikes[];
  
  // reference to the chest object
  private Chest mChest;

  // countdown once the chest is opened
  private int mEndTimer;
  
  // constructor
  public RoomD18() {

    super(NAME);

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

    mSpikes = new Spikes[]{ new Spikes(0,0,0, 3,3, false, "u0"),
                            new Spikes(0,8,0, 3,2, false, "u0"),
                            new Spikes(8,0,0, 2,3, false, "u0") };
    for ( Spikes spikes : mSpikes ) spriteManager.addSprite(spikes);
    
    mChest = new Chest(4, 4, 6, Env.DOWN);
    spriteManager.addSprite(mChest);

    mEndTimer = 0;
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mSpikes = null;
    mChest = null;
    
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

    // check the spikes
    if ( mPlayer != null ) {
      boolean triggerSpikes = false;
      for ( Spikes spikes : mSpikes ) {
        int dx = mPlayer.getXPos() - spikes.getXPos(),
            dy = mPlayer.getYPos() - spikes.getYPos();
        if ( dx >= 0 && dy >= 0 && 
             dx < spikes.getXSize() && dy < spikes.getYSize() ) {
          triggerSpikes = true;
          break;
        }
      }
      if ( triggerSpikes ) {
        for ( Spikes spikes : mSpikes ) {
          spikes.trigger();
        }
      }
    }
    
    // once the chest is open
    if ( mEndTimer > 0 ) {
      mEndTimer--;
      if ( mEndTimer == kChestSoundDelay ) {
        Env.sounds().play(Sounds.CHEST);        
      } else if ( mEndTimer == kChestOpenDelay ) {
        mChest.setOpen(true);
      } else if ( mEndTimer == 0 ) {
        storyEvents.add(new TinyStory.EventPlayerWins());
      }
    }

    // check for opening the chest
//    if ( mEndTimer == 0 && !mChest.isOpen() && mPlayer != null &&
//         mPlayer.getXPos() == 5 && mPlayer.getYPos() == 3 ) {
//      mPlayer.mAdvanceDisabled = true;
//      mEndTimer = kGameEndsDelay;
//    }
        
  } // Room.advance()

} // class RoomD18
