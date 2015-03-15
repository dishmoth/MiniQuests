/*
 *  RoomZ03.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Brain;
import com.dishmoth.miniquests.game.Critter;
import com.dishmoth.miniquests.game.CritterTrack;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;

// training room "Z03"
public class RoomZ03 extends Room {

  // unique identifier for this room
  public static final String NAME = "Z03";
  
  // main blocks for the room
  private static final String kBlocks[][] = { { "          ",
                                                "          ",
                                                "0000  0000",
                                                "0  0  0  0",
                                                "0  0  0  0",
                                                "0000  0000",
                                                "0  0  0  0",
                                                "0  0  0  0",
                                                "0000  0000",
                                                "          " },
                                                
                                              { "          ",
                                                "          ",
                                                "0000  0000",
                                                "0  0  0  0",
                                                "0  0  0  0",
                                                "0000  0000",
                                                "0  0  0  0",
                                                "0  0  0  0",
                                                "0000  0000",
                                                "          " },
                                                
                                              { "          ",
                                                "          ",
                                                "0000  0000",
                                                "0  0  0  0",
                                                "0  0  0  0",
                                                "0000  0000",
                                                "0  0  0  0",
                                                "0  0  0  0",
                                                "0000  0000",
                                                "          " },
                                                
                                              { "  00      ",
                                                "  0       ",
                                                "0000  0000",
                                                "0  0  0  0",
                                                "0  0  0  0",
                                                "0000  0000",
                                                "0  0  0  0",
                                                "0  0  0  0",
                                                "0000  0000",
                                                "          " },
  
                                              { "   00     ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
  
                                              { "    00    ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " } };
  
  // a few extra blocks continuing the exit
  private static final String kExitBlocks[][] = {{ "-", "-", "-" }};
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "Vh" };
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.LEFT, 4,2, "Vh",1, -1, RoomZ02.NAME, 1),
              new Exit(Env.UP,   5,6, "#h",0, -1, RoomZ03.NAME, 99) };

  // where the critters can walk
  private static final CritterTrack kCritterTrack =
                        new CritterTrack( new String[]{ "0000",
                                                        "0  0",
                                                        "0  0",
                                                        "0000",
                                                        "0  0",
                                                        "0  0",
                                                        "0000" }, 6, 1);
  
  // how long before the door shuts
  private static final int kEntranceDelay = 20;
  
  // time until the entrance locks
  private int mEntranceTimer;
  
  // constructor
  public RoomZ03() {

    super(NAME);

  } // constructor

  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < kExits.length );
    
    setPlayerAtExit(kExits[entryPoint]);
    if ( entryPoint == 0 ) {
      mPlayer.addBrain(new Brain.ZombieModule(
                                 new int[]{ -1,5, Env.RIGHT,10, -1,15 }));
    }
    
    return mPlayer;
    
  } // createPlayer()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {

    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,-4) );

    addBasicWalls(kExits, spriteManager);
    kExits[1].mDoor.setClosed(true);

    spriteManager.addSprite( new BlockArray(kExitBlocks, kBlockColours, 
                                            5,10,4) );
    
    Critter critters[] = { new Critter(6, 2, 2, Env.UP,    kCritterTrack),
                           new Critter(8, 1, 2, Env.RIGHT, kCritterTrack),
                           new Critter(7, 7, 2, Env.LEFT,  kCritterTrack),
                           new Critter(9, 5, 2, Env.DOWN,  kCritterTrack) };
    for ( Critter critter : critters ) {
      critter.easilyKilled(true);
      critter.setColour(1);
      spriteManager.addSprite(critter);
    }
    
    mEntranceTimer = kEntranceDelay;
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

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

    // open the final door
    if ( kExits[1].mDoor.closed() && mPlayer.getYPos() < 10 &&
         spriteManager.findSpriteOfType(Critter.class) == null ) {
      kExits[1].mDoor.setClosed(false);
      Env.sounds().play(Sounds.SUCCESS, 3);
    }
    
    // shut the entrance door
    if ( mEntranceTimer > 0 ) {
      if ( --mEntranceTimer == 0 ) {
        kExits[0].mDoor.setClosed(true);
        Env.sounds().play(Sounds.GATE);
      }
    }

    // shut the exit door
    if ( mPlayer.getYPos() > 11 && !kExits[1].mDoor.closed() ) {
        kExits[1].mDoor.setClosed(true);
        Env.sounds().play(Sounds.GATE);
    }
    
  } // Room.advance()

} // class RoomZ03
