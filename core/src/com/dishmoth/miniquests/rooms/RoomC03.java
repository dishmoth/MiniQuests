/*
 *  RoomC03.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Critter;
import com.dishmoth.miniquests.game.CritterTrack;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.Splatter;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;

// the room "C03"
public class RoomC03 extends Room {

  // unique identifier for this room
  public static final String NAME = "C03";
  
  // main blocks for the room
  private static final String kBlocks[][] = { { "    0     ",
                                                " 21111    ",
                                                "    0     ",
                                                "    0     ",
                                                "   11112  ",
                                                "    0     ",
                                                "    0     ",
                                                " 21111    ",
                                                "    0    0",
                                                "    000000" },
                                              
                                              { "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "         0",
                                                "         0",
                                                "          " },
                                              
                                              { "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "         0",
                                                "         0",
                                                "         0",
                                                "          ",
                                                "          " } };

  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#Y",   // green and white
                                                  "NY",   // green and blue
                                                  "OY" }; // green and dark
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.RIGHT, 4,8, "#Y",1, 0, RoomC02.NAME, 1), 
              new Exit(Env.UP,    4,4, "#Y",1, 0, RoomC04.NAME, 0) };

  // details of different camera height levels
  private static final CameraLevel kCameraLevels[]
                                     = { new CameraLevel(2, -100, +100) };
  
  // path followed by enemies in this room
  private static final String[] kCritterTrackBase = { "+++++" };
  private static final CritterTrack kCritterTracks[] 
                    =  { new CritterTrack(kCritterTrackBase, 1, 2),
                         new CritterTrack(kCritterTrackBase, 3, 5),
                         new CritterTrack(kCritterTrackBase, 1, 8) };

  // respawn positions (x,y) for critters
  private static final int kCritterXY[][] = { {1,2}, {7,5}, {1,8} };

  // how long till a critter respawns
  private static final int kRespawnDelay = 60;
  
  // references to active critters
  private Critter mCritters[];

  // time until each critter respawns
  private int mRespawnTimers[];
  
  // constructor
  public RoomC03() {

    super(NAME);

  } // constructor

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
    
    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,4) );    
    addBasicWalls(kExits, spriteManager);
    
    mCritters = new Critter[3];
    mRespawnTimers = new int[]{ 30, 45, 60 };
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mCritters = null;
    mRespawnTimers = null;
    
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

    for ( int k = 0 ; k < mCritters.length ; k++ ) {
      if ( mRespawnTimers[k] > 0 ) {
        // count down to respawn
        if ( --mRespawnTimers[k] == 0 ) {
          assert( mCritters[k] == null );
          Critter c = new Critter(kCritterXY[k][0], kCritterXY[k][1], 4,
                                  Env.RIGHT, kCritterTracks[k]);
          c.easilyKilled(true);
          c.setColour(1);
          mCritters[k] = c;
          spriteManager.addSprite(c);
          spriteManager.addSprite(
                 new Splatter(c.getXPos(), c.getYPos(), c.getZPos(),
                              -1, 4, (byte)5, -1));
          Env.sounds().play(Sounds.MATERIALIZE);
        }
      } else {
        // check if still alive
        if ( mCritters[k] != null &&
             !spriteManager.list().contains(mCritters[k]) ) {
          mCritters[k] = null;
          mRespawnTimers[k] = kRespawnDelay;
        }
      }
    }
    
  } // Room.advance()

} // class RoomC03
