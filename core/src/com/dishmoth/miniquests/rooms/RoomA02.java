/*
 *  RoomA02.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BitBuffer;
import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Liquid;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.QuestStory;
import com.dishmoth.miniquests.game.WallSwitch;

// the room "A02"
public class RoomA02 extends Room {

  // unique identifier for this room
  public static final String NAME = "A02";
  
  // all visible blocks for the room
  private static final String kBlocks[][] = { { "          ",
                                                "          ",
                                                "    0    1",
                                                "    0    1",
                                                "    0    1",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "          ",
                                                "    0    1",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "    0    1",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "   000    ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "    0     ",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "    0     ",
                                                "    0     ",
                                                "    000000" },
                                                
                                              { "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "         0",
                                                "          " },
                                                
                                              { "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "         0",
                                                "         0",
                                                "          ",
                                                "          " } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#c",   // orange
                                                  "#S" }; // red
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.RIGHT, 3,6, "#c",1, -1, RoomA01.NAME, 1),
              new Exit(Env.UP,    4,0, "#c",1, -1, RoomA03.NAME, 0) };

  // height range for the lava
  private static final int kLavaMinHeight = -12,
                           kLavaMaxHeight = -1;
  
  // speed of the lava
  private static final int kLavaPeriod = 150;
  
  // update the height of the lava
  private int mLavaCounter;
  
  // current height of the lava
  private int mLavaZPos;

  // reference to the lava object
  private Liquid mLava;
  
  // whether the switch has been triggered yet
  private boolean mSwitchDone;
  
  // constructor
  public RoomA02() {

    super(NAME);

    mSwitchDone = false;
    
  } // constructor

  // serialize the room state by writing bits to the specified buffer
  @Override
  public void save(BitBuffer buffer) {
    
    buffer.writeBit(mSwitchDone);
    
  } // Room.save()

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  @Override
  public boolean restore(int version, BitBuffer buffer) { 
    
    if ( buffer.numBitsToRead() < 1 ) return false;
    mSwitchDone = buffer.readBit();
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

    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,-4) );

    addBasicWalls(kExits, spriteManager);
    if ( !mSwitchDone ) kExits[1].mDoor.setClosed(true);
    
    WallSwitch ws = new WallSwitch(Env.RIGHT, 6, -2, 
                                   new String[]{"Iu","7u"}, false);
    spriteManager.addSprite(ws);
    if ( mSwitchDone ) ws.setState(1);

    mLavaCounter = 0;
    mLavaZPos = kLavaMaxHeight;
    mLava = new Liquid(0,0,mLavaZPos, 1);
    spriteManager.addSprite( mLava );

  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mLava = null;
    
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
    boolean saveGameEvent = false;    
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();
      
      if ( event instanceof WallSwitch.EventStateChange ) {
        assert( !mSwitchDone );
        kExits[1].mDoor.setClosed(false);
        mSwitchDone = true;
        saveGameEvent = true;
        Env.sounds().play(Sounds.SUCCESS, 3);
        it.remove();
      }
      
    }
    if ( saveGameEvent ) storyEvents.add(new QuestStory.EventSaveGame());
    
    // animate the lava
    mLavaCounter = (mLavaCounter+1) % kLavaPeriod;
    final float t = mLavaCounter/(float)kLavaPeriod,
                y = 0.5f*((float)Math.cos(2.0*Math.PI*t) + 1);
    mLavaZPos = Math.round((1-y)*kLavaMinHeight + y*kLavaMaxHeight);
    mLava.setZPos(mLavaZPos);
    
  } // Room.advance()

} // class RoomA02
