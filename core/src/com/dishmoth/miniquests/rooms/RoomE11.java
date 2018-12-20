/*
 *  RoomE11.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;

// the room "E11"
public class RoomE11 extends Room {

  // unique identifier for this room
  public static final String NAME = "E11";
  
  // the basic blocks for the room
  private static final String kBlocks[][] = { { "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000111000",
                                                "0000001000",
                                                "0000001000",
                                                "0000111000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000" } };

  // start and end of the maze
  private static final int kStartXPos = 4,
                           kStartYPos = 3,
                           kEndXPos   = 4,
                           kEndYPos   = 6;
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#l",   // green
                                                  "#T",   // brown
                                                  "lT" }; // brown with centre
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.LEFT,  5,0, "#l",0, -1, RoomE11.NAME, 2), 
              new Exit(Env.DOWN,  5,0, "#l",0, -1, RoomE11.NAME, 3), 
              new Exit(Env.RIGHT, 5,0, "#l",0, -1, RoomE11.NAME, 0), 
              new Exit(Env.UP,    5,0, "#l",0, -1, RoomE11.NAME, 1) };
              
  // dummy exit objects for the true maze exits 
  private static final Exit kMazeExits[] 
          = { new Exit(0,0,0,"  ",0,0, RoomE10.NAME, 1),
              new Exit(0,0,0,"  ",0,0, RoomE12.NAME, 0) };

  // track whether the player is currently in the maze
  private boolean mInMaze;
  
  // position in the overall maze
  private int mMazeXPos,
              mMazeYPos;
  
  // constructor
  public RoomE11() {

    super(NAME);

    mInMaze = false;
    
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

    if ( !mInMaze ) {
      // player has just entered the maze
      mMazeXPos = kStartXPos;
      mMazeYPos = kStartYPos;
      mInMaze = true;
    }

    // adjust the blocks to show the current position
    String blocks[][] = new String[1][ kBlocks[0].length ];
    for ( int k = 0 ; k < kBlocks[0].length ; k++ ) {
      if ( k == kBlocks[0].length-1-mMazeYPos ) {
        assert( kBlocks[0][k].charAt(mMazeXPos) == '1' );
        blocks[0][k] = kBlocks[0][k].substring(0, mMazeXPos)
                     + "2"
                     + kBlocks[0][k].substring(mMazeXPos+1);
      } else {
        blocks[0][k] = kBlocks[0][k];
      }
    }
    
    spriteManager.addSprite( new BlockArray(blocks, kBlockColours, 0, 0, 0) );
    
    addBasicWalls(kExits, spriteManager);

  } // Room.createSprites()
  
  // update the room (events may be added or processed)
  @Override
  public void advance(LinkedList<StoryEvent> storyEvents,
                      SpriteManager          spriteManager) {

    final int exitIndex = checkExits(kExits);
    if ( exitIndex != -1 ) {
      Exit exit = kExits[exitIndex];

      final int direc = exit.mWallSide;
      mMazeXPos += Env.STEP_X[direc];
      mMazeYPos += Env.STEP_Y[direc];
      
      if ( mMazeXPos == kStartXPos-1 && mMazeYPos == kStartYPos ) {
        // retreat from the maze
        mInMaze = false;
        exit = kMazeExits[0];
      }
      
      else if ( kBlocks[0][Room.kSize-1-mMazeYPos].charAt(mMazeXPos) != '1' ) {
        // back to start of maze
        mMazeXPos = kStartXPos;
        mMazeYPos = kStartYPos;
      }
      
      else if ( mMazeXPos == kEndXPos && mMazeYPos == kEndYPos ) {
        // leave by the true exit
        mInMaze = false;
        exit = kMazeExits[1];
      }
      
      storyEvents.add(new EventRoomChange(exit.mDestination,
                                          exit.mEntryPoint));
      return;
    }

  } // Room.advance()

} // class RoomE11
