/*
 *  RoomZ01.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Brain;
import com.dishmoth.miniquests.game.EgaImage;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Mural;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;

// training room "Z01"
public class RoomZ01 extends Room {

  // unique identifier for this room
  public static final String NAME = "Z01";

  // main blocks for the room
  private static final String kBlocks[][] = { { "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "00000  000",
                                                "01110  000",
                                                "01010  000",
                                                "01110  000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000" },
                                                
                                              { "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "       000",
                                                "       000",
                                                "       000",
                                                "       000",
                                                "      0000",
                                                "      0000",
                                                "      0000" },
                                                
                                              { "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "       000",
                                                "       000",
                                                "       000",
                                                "       000",
                                                "       000",
                                                "       000",
                                                "       000" },
                                                
                                              { "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "       000",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "gN",
                                                  "EN" };
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { // note: dummy exit at index 0
              new Exit(Env.UP, 2,8, "#N",0, -1, RoomZ02.NAME, 0) };

  // time until the door unlocks
  private static final int kDoorDelay = 10;
  
  // how fast the door flashes
  private static final int kFlickerDelay      = 120,
                           kFlickerDelayStart = 360;
  
  // time until the door unlocks
  private int mDoorTimer;
  
  // highlight the door (or -1)
  private int mFlickerTimer;
  
  // reference to the image overlaying the door
  private Mural mFlickerImage;
  
  // constructor
  public RoomZ01() {

    super(NAME);

  } // constructor

  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < kExits.length+1 );
    
    if ( entryPoint == 0 ) {
      // special case: start of game
      mPlayer = new Player(2, 4, 0, Env.DOWN);
      mPlayer.addBrain( new Brain.NoFireModule() );
      mCameraLevel = -1;
      mCamera.set(0, 0, 0);
    } else {
      setPlayerAtExit(kExits[entryPoint-1]);
    }
    
    return mPlayer;
    
  } // createPlayer()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {

    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,0) );

    addBasicWalls(kExits, spriteManager);

    kExits[0].mDoor.setClosed(true);
    mDoorTimer = kDoorDelay;
    
    mFlickerImage = new Mural(Env.UP, 1, 8, 
                              new EgaImage(0,0, 6,7, "  sss "
                                                   + " s  s "
                                                   + " s  s "
                                                   + " s  s "
                                                   + " s  s "
                                                   + " s    "
                                                   + " s    "));
    mFlickerTimer = kFlickerDelayStart;
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mFlickerImage = null;
    
  } // Room.discardResources()
  
  // update the room (events may be added or processed)
  @Override
  public void advance(LinkedList<StoryEvent> storyEvents,
                      SpriteManager          spriteManager) {

    // check the exits
    final int exitIndex = checkExits(kExits);
    if ( exitIndex != -1 ) {
      storyEvents.add(new EventRoomChange(kExits[exitIndex].mDestination,
                                          kExits[exitIndex].mEntryPoint));
      return;
    }

    // unlock the door
    if ( mDoorTimer > 0 ) {
      if ( --mDoorTimer == 0 ) {
        kExits[0].mDoor.setClosed(false);
        Env.sounds().play(Sounds.GATE);
      }
    }
    
    // make the exit flash
    if ( mPlayer.getYPos() < 7 ) {
      if ( --mFlickerTimer < 0 ) mFlickerTimer = kFlickerDelay;
      final double t1 = 0,
                   t2 = 6;
      if ( mFlickerTimer == t1 || mFlickerTimer == t2 ) {
        spriteManager.removeSprite(mFlickerImage); 
      }
      if ( mFlickerTimer == t1+1 || mFlickerTimer == t2+1 ) {
        spriteManager.addSprite(mFlickerImage); 
      }
    } else {
      mFlickerTimer = Math.max(mFlickerTimer, kFlickerDelay/2);
    }
    
  } // Room.advance()

} // class RoomZ01
