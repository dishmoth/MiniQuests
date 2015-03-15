/*
 *  RoomA13.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Chest;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Flame;
import com.dishmoth.miniquests.game.Liquid;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.TinyStory;

// the room "A13"
public class RoomA13 extends Room {

  // unique identifier for this room
  public static final String NAME = "A13";
  
  // main blocks for the room
  private static final String kBlocks[][] = { { "          ",
                                                "  0     0 ",
                                                "   00000  ",
                                                "   00000  ",
                                                "   00000  ",
                                                "   00000  ",
                                                "   00000  ",
                                                "          ",
                                                "          ",
                                                "          " },
                                              
                                              { "          ",
                                                "  0     0 ",
                                                "   00000  ",
                                                "   00000  ",
                                                "   00000  ",
                                                "   00000  ",
                                                "   00000  ",
                                                "          ",
                                                "          ",
                                                "          " },
                                              
                                              { "          ",
                                                "  0     0 ",
                                                "   00000  ",
                                                "   00000  ",
                                                "   00000  ",
                                                "   00000  ",
                                                "   00000  ",
                                                "     0    ",
                                                "     0    ",
                                                "     0    " },
                                              
                                              { "          ",
                                                "  0     0 ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "          ",
                                                "  0     0 ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " } };

  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#x",  
                                                  "#D" }; 
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.DOWN, 5,0, "#x",0, -1, RoomA10.NAME, 4) };

  // times at which things happen
  private static final int kGameEndsDelay   = 50,
                           kChestSoundDelay = kGameEndsDelay - 5,
                           kChestOpenDelay  = kChestSoundDelay - 10;  
  
  // reference to the chest object
  private Chest mChest;

  // countdown once the chest is opened
  private int mEndTimer;
  
  // constructor
  public RoomA13() {

    super(NAME);

  } // constructor

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
    
    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,-4) );
    
    addBasicWalls(kExits, spriteManager);

    Liquid lava = new Liquid(0,0,-3, 1);
    spriteManager.addSprite(lava);

    mChest = new Chest(4, 4, 0, Env.DOWN);
    spriteManager.addSprite(mChest);
    
    Flame flame1 = new Flame(2,8,4);
    flame1.warmUp();
    spriteManager.addSprite(flame1);
    
    Flame flame2 = new Flame(8,8,4);
    flame2.warmUp();
    spriteManager.addSprite(flame2);

    mEndTimer = 0;
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mChest = null;
    
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
        storyEvents.add(new TinyStory.EventPlayerWins());
      }
    }

    // check for opening the chest
    if ( mEndTimer == 0 && !mChest.isOpen() && mPlayer != null &&
         mPlayer.getXPos() == 5 && mPlayer.getYPos() == 3 ) {
      mPlayer.mAdvanceDisabled = true;
      mEndTimer = kGameEndsDelay;
    }
        
  } // Room.advance()

} // class RoomA13
