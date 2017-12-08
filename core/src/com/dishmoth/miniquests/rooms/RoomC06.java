/*
 *  RoomC06.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BitBuffer;
import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Critter;
import com.dishmoth.miniquests.game.CritterTrack;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.ScrewTower;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.Splatter;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.QuestStory;
import com.dishmoth.miniquests.game.WallSwitch;

// the room "C06"
public class RoomC06 extends Room {

  // unique identifier for this room
  public static final String NAME = "C06";
  
  // the basic blocks for the room
  private static final String kBlocks[][] = { { "          ",
                                                "          ",
                                                "    00    ",
                                                "          ",
                                                "  0    0  ",
                                                "  0    0  ",
                                                "  1       ",
                                                "          ",
                                                "          ",
                                                "       0  " } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "Kk",   // dark brown
                                                  "tk" }; // light brown
  
  // details of exit/entry points for the room
  private static final Exit kExits[] 
          = { new Exit(Env.DOWN, 7,0, "Kk",0, 0, RoomC05.NAME, 1),
              new Exit(Env.LEFT, 7,8, "Kk",4, 0, RoomC07.NAME, 0) }; 
  
  // details of different camera height levels
  private static final CameraLevel kCameraLevels[]
                                     = { new CameraLevel(2, -100, +100) };
  
  // colours of the tower blocks
  private static final String kTowerColours[] = { "Vk", ":k", "Mk" };
                              
  
  // paths followed by critters
  private static final CritterTrack 
      kCritterTrack1  = new CritterTrack(new String[]{ "+++",
                                                       "+ +",
                                                       "+++" }, 6, 6),
      kCritterTrack2a = new CritterTrack(new String[]{ "+++",
                                                       "  +",
                                                       " ++",
                                                       " + ",
                                                       " + ",
                                                       " + " }, 1, 3),
      kCritterTrack2b = new CritterTrack(new String[]{ "+++",
                                                       "+ +",
                                                       "+++" }, 1, 6);
  
  // whether there is a critter on Tower 2
  private boolean mCritterKilled;
  
  // constructor
  public RoomC06() {

    super(NAME);

    mCritterKilled = false;
    
  } // constructor

  // serialize the room state by writing bits to the specified buffer
  @Override
  public void save(BitBuffer buffer) {
    
    buffer.writeBit(mCritterKilled);
    
  } // Room.save()

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  @Override
  public boolean restore(int version, BitBuffer buffer) { 
    
    if ( buffer.numBitsToRead() < 1 ) return false;
    mCritterKilled = buffer.readBit();
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
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

  } // Room.discardResources()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {

    addBasicWalls(kExits, spriteManager);

    spriteManager.addSprite(new BlockArray(kBlocks, kBlockColours, 0,0,0));
    spriteManager.addSprite(new BlockArray(new String[][]{{"0"}}, kBlockColours,
                                           0,7,8));

    spriteManager.addSprite(new ScrewTower(6,1, 0,8, false, 
                                           kTowerColours[0]));
    spriteManager.addSprite(new ScrewTower(6,6, 0,8, !mCritterKilled, 
                                           kTowerColours[1]));
    spriteManager.addSprite(new ScrewTower(1,6, 0,8, false, 
                                           kTowerColours[2]));

    spriteManager.addSprite(new WallSwitch(Env.UP, 0, 10, 
                                           new String[]{"P7","u7"}, false));

    if ( !mCritterKilled ) {
      Critter critter = new Critter(7, 6, 8, Env.RIGHT, kCritterTrack1);
      critter.easilyKilled(true);
      critter.setColour(3);
      spriteManager.addSprite(critter);
    }
    
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

    // process the story event list
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();
      if ( event instanceof WallSwitch.EventStateChange ) {
        Critter c = new Critter(2, 3, 0, Env.UP, kCritterTrack2a);
        c.easilyKilled(true);
        c.setColour(3);
        spriteManager.addSprite(c);
        spriteManager.addSprite(
               new Splatter(c.getXPos(), c.getYPos(), c.getZPos(),
                            -1, 4, (byte)25, -1));
        Env.sounds().play(Sounds.MATERIALIZE, 2);
        it.remove();
      }
    }

    // check whether the first critter has been killed yet
    if ( !mCritterKilled ) {
      if ( spriteManager.findSpriteOfType(Critter.class) == null ) {
        mCritterKilled = true;
        storyEvents.add(new QuestStory.EventSaveGame());
      }
    }

    // check the second critter
    if ( mCritterKilled ) {
      Critter critter = (Critter)spriteManager.findSpriteOfType(Critter.class);
      if ( critter == null ) {
        // reset the switch if the critter is killed
        WallSwitch ws = (WallSwitch)spriteManager.findSpriteOfType(
                                                             WallSwitch.class);
        if ( ws.getState() == 1 ) ws.setState(0);
      } else {
        // update the track when the critter is on the tower
        if ( critter.getTrack() == kCritterTrack2a &&
             critter.getYPos() == 8 ) {
          critter.setTrack( kCritterTrack2b );
        }
      }
    }
    
  } // Room.advance()

} // class RoomC06
