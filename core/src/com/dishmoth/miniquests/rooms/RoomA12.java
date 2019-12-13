/*
 *  RoomA12.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BitBuffer;
import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Flame;
import com.dishmoth.miniquests.game.Liquid;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.QuestStory;

// the room "A12"
public class RoomA12 extends Room {

  // unique identifier for this room
  public static final String NAME = "A12";
  
  // main blocks for the room
  private static final String kBlocks[][] = { { " 6  0  5  ",
                                                "    0     ",
                                                " 0000000 7",
                                                " 0     0  ",
                                                " 0     0  ",
                                                " 0     000",
                                                " 0     0  ",
                                                " 0     0  ",
                                                " 0000000 8",
                                                "          " },
                                                
                                              { " 6  0  5  ",
                                                "    0     ",
                                                " 2222222 7",
                                                " 2111112  ",
                                                " 2133312  ",
                                                " 213431200",
                                                " 2133312  ",
                                                " 2111112  ",
                                                " 2222222 8",
                                                "          " },
                                                
                                              { " 6     5  ",
                                                "          ",
                                                "         7",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "         8",
                                                "          " },
                                              
                                              { " 6     5  ",
                                                "          ",
                                                "         7",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "         8",
                                                "          " } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#0",   // black
                                                  "D0",   // pink
                                                  "20",   // green
                                                  "B0",   // blue
                                                  "c0",   // yellow 
                                                  "#D",   // other pink
                                                  "#2",   // other green
                                                  "#B",   // other blue
                                                  "#c" }; // other yellow
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.UP,    4,0, "#0",0, -1, RoomA10.NAME, 3),
              new Exit(Env.RIGHT, 4,0, "#0",1, -1, RoomA10.NAME, 1) };

  // whether the room is complete yet
  private boolean mDone;

  // references to the flame objects
  private Flame mFlames[];
  
  // current position of the player
  private int mXPos,
              mYPos;
  
  // constructor
  public RoomA12() {

    super(NAME);

    mDone = false;
    
  } // constructor

  // serialize the room state by writing bits to the specified buffer
  @Override
  public void save(BitBuffer buffer) {
    
    buffer.writeBit(mDone);
    
  } // Room.save()

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  @Override
  public boolean restore(int version, BitBuffer buffer) { 
    
    if ( buffer.numBitsToRead() < 1 ) return false;
    mDone = buffer.readBit();
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
    
    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,-2) );
    
    addBasicWalls(kExits, spriteManager);

    if ( !mDone ) kExits[1].mDoor.setClosed(true);
    
    mFlames = new Flame[]{ new Flame(7, 9, 4),
                           new Flame(1, 9, 4),
                           new Flame(9, 7, 4),
                           new Flame(9, 1, 4) };
    for ( Flame f : mFlames ) {
      if ( mDone ) {
        f.setFlame(true);
        f.warmUp();
      } else {
        f.setFlame(false);
      }
      spriteManager.addSprite(f);
    }

    spriteManager.addSprite(new Liquid(0,0,-2, 1));
    
    mXPos = mYPos = -1;
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mFlames = null;
    
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

    if ( !mDone ) {
      if ( mPlayer == null ) {
        mXPos = mYPos = -1;
      } else {
        if ( mXPos != mPlayer.getXPos() || mYPos != mPlayer.getYPos() ) {
          mXPos = mPlayer.getXPos();
          mYPos = mPlayer.getYPos();
          if ( mXPos >= 1 && mXPos <= 7 && mYPos >= 1 && mYPos <= 7 ) {
            char ch = kBlocks[1][Room.kSize-1-mYPos].charAt(mXPos);
            assert( ch >= '1' && ch <= '4' );
            int index = (ch - '1');
            boolean isOn = mFlames[index].isOn();
            if ( isOn ) Env.sounds().play(Sounds.SWITCH_OFF);
            else        Env.sounds().play(Sounds.SWITCH_ON);
            mFlames[index].setFlame(!isOn);
          }
          if ( mXPos == 8 && mYPos == 4 && allFlamesOn() ) {
            mDone = true;
            storyEvents.add(new QuestStory.EventSaveGame());
            Env.sounds().play(Sounds.SUCCESS);
          }
          kExits[1].mDoor.setClosed( !allFlamesOn() ); 
        }
      }
    }
    
  } // Room.advance()

  // check if all flames are lit
  private boolean allFlamesOn() {
    
    for ( Flame f : mFlames ) {
      if ( !f.isOn() ) return false;
    }
    return true;
    
  } // allFlamesOn()
  
} // class RoomA12
