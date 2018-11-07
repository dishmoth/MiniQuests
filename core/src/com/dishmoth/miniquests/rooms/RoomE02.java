/*
 *  RoomE02.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.FloorSwitch;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.SnakeB;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.WallSwitch;

// the room "E02"
public class RoomE02 extends Room {

  // unique identifier for this room
  public static final String NAME = "E02";
  
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
                                                "0000000000" } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#h" }; // 
  
  // details of exit/entry points for the room 
  private static final Exit kExits[]  = {}; // note: dummy exit at index 0

  // whether the floor switches are completed
  private boolean mSwitchesDone;
  
  // set of floor switches
  private FloorSwitch mSwitches[];

  // constructor
  public RoomE02() {

    super(NAME);

    mSwitchesDone = false;
    
  } // constructor

  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < kExits.length+1 );
    
    // special behaviour
    if ( entryPoint == 0 ) {
      // special case: start of game
      mPlayer = new Player(9, 6, 0, Env.LEFT);
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

    spriteManager.addSprite( new SnakeB(3,3,0, Env.DOWN) );
    
    if ( !mSwitchesDone ) {
      mSwitches = new FloorSwitch[16];
      int k = 0;
      for ( int i = 0 ; i <= 9 ; i += 3 ) {
        for ( int j = 0 ; j <= 9 ; j += 3 ) {
          mSwitches[k++] = new FloorSwitch(i, j, 0, "#i", "#h");
        }
      }
      for ( FloorSwitch s : mSwitches ) spriteManager.addSprite(s);
    }
  
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

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
        FloorSwitch s = (FloorSwitch)
                        ((FloorSwitch.EventStateChange)event).mSwitch;
        s.freezeState(true);
        Env.sounds().play(Sounds.SWITCH_ON);
        it.remove();
      }
      
      if ( event instanceof Player.EventKilled ) {
        if ( !mSwitchesDone ) {
          for ( FloorSwitch s : mSwitches ) s.unfreezeState();
        }
      }
      
    } // for (event)

    // check the switches
    
    if ( !mSwitchesDone ) {
      boolean done = true;
      for ( FloorSwitch s : mSwitches ) {
        if ( !s.isOn() ) {
          done = false;
          break;
        }
      }
      if ( done ) {
        mSwitchesDone = true;
        SnakeB snake = (SnakeB)spriteManager.findSpriteOfType(SnakeB.class);
        snake.kill();
      }
    }
    
  } // Room.advance()

} // class RoomE02
