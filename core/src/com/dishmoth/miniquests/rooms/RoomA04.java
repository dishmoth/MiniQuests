/*
 *  RoomA04.java
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
import com.dishmoth.miniquests.game.FloorSwitch;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.Splatter;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;

// the room "A04"
public class RoomA04 extends Room {

  // unique identifier for this room
  public static final String NAME = "A04";
  
  // all visible blocks for the room
  private static final String kBlocks[][] = { { "0000000000",
                                                "0  0  0  0",
                                                "0  0  0  0",
                                                "0000000000",
                                                "0  0  0  0",
                                                "0  0  0  0",
                                                "0000000000",
                                                "0  0  0  0",
                                                "0  0  0  0",
                                                "0000000000" },
                                                
                                              { "0000000000",
                                                "0  0  0  0",
                                                "0  0  0  0",
                                                "0000000000",
                                                "0  0  0  0",
                                                "0  0  0  0",
                                                "0000000000",
                                                "0  0  0  0",
                                                "0  0  0  0",
                                                "0000000000" },
                                                
                                              { "0000000000",
                                                "0  0  0  0",
                                                "0  0  0  0",
                                                "0000000000",
                                                "   0  0  0",
                                                "   0  0  0",
                                                "   0000000",
                                                "      0  0",
                                                "      0  0",
                                                "      0000" },
                                                
                                              { "   0000000",
                                                "      0  0",
                                                "      0  0",
                                                "      0000",
                                                "         0",
                                                "         0",
                                                "         0",
                                                "          ",
                                                "          ",
                                                "          " } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#w",   // green
                                                  "#k" }; // brown
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.RIGHT, 3,4, "#w",2, -1, RoomA03.NAME, 1), 
              new Exit(Env.UP,    6,4, "#w",2, -1, RoomA05.NAME, 0) };

  // paths followed by enemies in this room
  private static final CritterTrack kCritterTrack[] 
                    = { new CritterTrack(new String[]{ "          ",
                                                       "          ",
                                                       "          ",
                                                       "+++++++   ",
                                                       "+  +  +   ",
                                                       "+  +  +   ",
                                                       "+++++++   ",
                                                       "+  +  +   ",
                                                       "+  +  +   ",
                                                       "+++++++   " }),
                        new CritterTrack(new String[]{ "          ",
                                                       "          ",
                                                       "          ",
                                                       "   +++++++",
                                                       "   +  +  +",
                                                       "   +  +  +",
                                                       "   +++++++",
                                                       "   +  +  +",
                                                       "   +  +  +",
                                                       "   +++++++" }),
                        new CritterTrack(new String[]{ "+++++++   ",
                                                       "+  +  +   ",
                                                       "+  +  +   ",
                                                       "+++++++   ",
                                                       "+  +  +   ",
                                                       "+  +  +   ",
                                                       "+++++++   ",
                                                       "          ",
                                                       "          ",
                                                       "          " }),
                        new CritterTrack(new String[]{ "   +++++++",
                                                       "   +  +  +",
                                                       "   +  +  +",
                                                       "   +++++++",
                                                       "   +  +  +",
                                                       "   +  +  +",
                                                       "   +++++++",
                                                       "          ",
                                                       "          ",
                                                       "          " }) };
  
  // time until critters appear
  private static final int kCritterDelay = 10;
  
  // the room's enemies
  private Critter mCritters[];
  
  // count-down until critters appear
  private int mCritterTimer;
  
  // whether this room has been completed already
  private boolean mDoorOpen;

  // constructor
  public RoomA04() {

    super(NAME);

    mDoorOpen = false;

  } // constructor

  // serialize the room state by writing bits to the specified buffer
  @Override
  public void save(BitBuffer buffer) {
    
    buffer.writeBit(mDoorOpen);
    
  } // Room.save()

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  @Override
  public boolean restore(int version, BitBuffer buffer) { 
    
    if ( buffer.numBitsToRead() < 1 ) return false;
    mDoorOpen = buffer.readBit();
    return true; 
    
  } // Room.restore() 
  
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

    spriteManager.addSprite( new BlockArray(kBlocks,kBlockColours, 0,0,-2) );
    
    addBasicWalls(kExits, spriteManager);
    
    if ( mDoorOpen ) {
      mCritters = new Critter[] 
             { new Critter(0,0,0, Env.randomInt(4), kCritterTrack[0]),
               new Critter(9,0,2, Env.DOWN,         kCritterTrack[1]),
               new Critter(0,9,2, Env.randomInt(4), kCritterTrack[2]),
               new Critter(9,9,4, Env.RIGHT,        kCritterTrack[3]) };
      for ( int k = 0 ; k < mCritters.length ; k++ ) {
        mCritters[k].easilyKilled(true);
        mCritters[k].setColour(1);
        spriteManager.addSprite(mCritters[k]);
      }
    } else {
      kExits[1].mDoor.setClosed(true);
      spriteManager.addSprite( new FloorSwitch(0,0,0, "#z", "#w") );
    }

    mCritterTimer = 0;
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mCritters = null;
    
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

    // process the story event list
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();
      
      if ( event instanceof FloorSwitch.EventStateChange ) {
        FloorSwitch s = ((FloorSwitch.EventStateChange)event).mSwitch;
        if ( s.isOn() ) {
          s.freezeState(true);
          assert( !mDoorOpen );
          mDoorOpen = true;
          kExits[1].mDoor.setClosed(false);
          mCritterTimer = kCritterDelay;
          Env.sounds().play(Sounds.SWITCH_ON);
        }
        it.remove();
      }
      
    }

    // critters materialize
    if ( mCritterTimer > 0 ) {
      if ( --mCritterTimer == 0 ) {
        mCritters = new Critter[] 
               { new Critter(6,6,4, Env.LEFT, kCritterTrack[0]),
                 new Critter(9,0,2, Env.DOWN, kCritterTrack[1]),
                 new Critter(0,9,2, Env.LEFT, kCritterTrack[2]),
                 new Critter(9,9,4, Env.DOWN, kCritterTrack[3]) };
        for ( int k = 0 ; k < mCritters.length ; k++ ) {
          Critter c = mCritters[k]; 
          c.easilyKilled(true);
          c.setColour(1);
          spriteManager.addSprite(c);
          spriteManager.addSprite(
                 new Splatter(c.getXPos(), c.getYPos(), c.getZPos(),
                              -1, 4, (byte)5, -1));
        }
        Env.sounds().play(Sounds.MATERIALIZE, 2);
      }
    }
    
  } // Room.advance()

} // class RoomA04
