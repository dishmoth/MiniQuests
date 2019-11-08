/*
 *  RoomE10.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.BlockStairs;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.FlameArea;
import com.dishmoth.miniquests.game.Liquid;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.ZoneSwitch;

// the room "E10"
public class RoomE10 extends Room {

  // unique identifier for this room
  public static final String NAME = "E10";
  
  // low-down blocks for the room
  private static final String kBlocks1[][] = { { "          ",
                                                 "          ",
                                                 "        11",
                                                 "        11",
                                                 "        11",
                                                 "        11",
                                                 "        11",
                                                 "        11",
                                                 "          ",
                                                 "          " } };

  // high-up blocks for the room
  private static final String kBlocks2[][] = { { "          ",
                                                 "          ",
                                                 "         0",
                                                 "         0",
                                                 "         0",
                                                 "         0",
                                                 "         0",
                                                 "          ",
                                                 "          ",
                                                 "          " } };

  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#X",   // purple
                                                  "FX" }; // purple
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.RIGHT, 4,42, "#X",0, 3, RoomE04.NAME, 0), 
              new Exit(Env.RIGHT, 5,0,  "#X",0, 0, RoomE11.NAME, 0) };

  // details of different camera height levels
  private static final CameraLevel kCameraLevels[]
                                     = { new CameraLevel(0, -100, 12),
                                         new CameraLevel(14,  12, 22),
                                         new CameraLevel(27,  22, 32),
                                         new CameraLevel(40,  32, +100) };
  
  // area covered by the flames for different numbers of switches
  private static final String kFlamePattern[] = { "OOOOOOOOOO",
                                                  "OOOOOOOOOO",
                                                  "OOO       ",
                                                  "OOOOOOOO  ",
                                                  "OOOOOOOO  ",
                                                  "OOOOOOOO  ",
                                                  "OOOOOOOO  ",
                                                  "OOOOOOOO  ",
                                                  "OOOOOOOOOO",
                                                  "OOOOOOOOOO" };

  // range covered by the stairs
  private static final int kStairsTop    = 42,
                           kStairsBottom = 2;
  
  // the shifting stairs
  private BlockStairs mStairs;
  
  // switches at both ends of the stairs
  private ZoneSwitch mSwitchStart,
                     mSwitchEnd;
  
  // constructor
  public RoomE10() {

    super(NAME);

  } // constructor

  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < kExits.length );
    setPlayerAtExit(kExits[entryPoint], kCameraLevels);
    
    if ( entryPoint == 1 ) {
      mStairs.reset(kStairsBottom+10, kStairsBottom);
    }
    
    return mPlayer;
    
  } // createPlayer()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

  } // Room.discardResources()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {

    spriteManager.addSprite(new BlockArray(kBlocks1, kBlockColours, 0,0,0));
    spriteManager.addSprite(new BlockArray(kBlocks2, kBlockColours, 0,0,42));
    addBasicWalls(kExits, spriteManager);

    mStairs = new BlockStairs(3, 7, kStairsTop,
                              8, 7, kStairsTop,
                              "LX", 16);
    spriteManager.addSprite(mStairs);
    
    mSwitchStart = new ZoneSwitch(3, 7);
    mSwitchEnd = new ZoneSwitch(8, 7);
    spriteManager.addSprite(mSwitchStart);
    spriteManager.addSprite(mSwitchEnd);
    
    spriteManager.addSprite(new Liquid(0,0,-2, 2));
    FlameArea flames = new FlameArea(0, 0, -4, kFlamePattern);
    flames.setFlame(true);
    flames.warmUp();
    spriteManager.addSprite(flames);
    
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

    // check camera level
    EventRoomScroll scroll = checkVerticalScroll(kCameraLevels);
    if ( scroll != null ) storyEvents.add(scroll);
    
    // process the story event list
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();

      if ( event instanceof ZoneSwitch.EventStateChange ) {
        ZoneSwitch s = (ZoneSwitch)
                       ((ZoneSwitch.EventStateChange)event).mSwitch;
        if ( s.isOn() ) {
          int zStart = mStairs.getZStart(),
              zEnd   = mStairs.getZEnd();
          if ( s == mSwitchStart ) {
            if ( zEnd >= zStart && zStart-10 >= kStairsBottom ) {
              mStairs.setZEnd(zStart-10);
              Env.sounds().play(Sounds.SWITCH_ON);
            } else if ( zEnd <= zStart && zStart+10 <= kStairsTop ) {
              mStairs.setZEnd(zStart+10);
              Env.sounds().play(Sounds.SWITCH_ON);
            }
          } else {
            if ( zStart >= zEnd && zEnd-10 >= kStairsBottom ) {
              mStairs.setZStart(zEnd-10);
              Env.sounds().play(Sounds.SWITCH_ON);
            } else if ( zStart <= zEnd && zEnd+10 <= kStairsTop ) {
              mStairs.setZStart(zEnd+10);
              Env.sounds().play(Sounds.SWITCH_ON);
            } else if ( zEnd == kStairsTop ) {
              mStairs.setZStart(kStairsTop);
              Env.sounds().play(Sounds.SWITCH_ON);
            }
          }
        }
        it.remove();
      }
      
    } // for (event)
    
  } // Room.advance()

} // class RoomE10
