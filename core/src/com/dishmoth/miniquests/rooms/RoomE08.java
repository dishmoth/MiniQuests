/*
 *  RoomE08.java
 *  Copyright (c) 2018 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Fence;
import com.dishmoth.miniquests.game.FlameArea;
import com.dishmoth.miniquests.game.Liquid;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;

// the room "E08"
public class RoomE08 extends Room {

  // unique identifier for this room
  public static final String NAME = "E08";
  
  // main blocks for the room
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
                                                "0000000000" } };
                                              
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "NY" }; //
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.RIGHT, 8,0, "#Y",0, -1, RoomE04.NAME, 1),
              new Exit(Env.DOWN,  5,0, "NY",0, -1, RoomE09.NAME, 0) };

  // reference to some objects
  private Liquid    mLiquid;
  private FlameArea mFlames[][];
  
  //
  private int mTimer;
  
  // constructor
  public RoomE08() {

    super(NAME);

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
    
    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,-2) );
    
    addBasicWalls(kExits, spriteManager);

    mLiquid = new Liquid(0,0,0, 2);
    spriteManager.addSprite(mLiquid);

    mFlames = new FlameArea[3][3];
    for ( int i = 0 ; i < 3 ; i++ ) {
      for ( int j = 0 ; j < 3 ; j++ ) {
        float x = 2.0f + 3.0f*j,
              y = 2.0f + 3.0f*i;
        mFlames[i][j] = new FlameArea(x-1.05f, x+1.05f, y-1.05f, y+1.05f, 0.0f);
        mFlames[i][j].setFlame(false);
        spriteManager.addSprite(mFlames[i][j]);
      }
    }
  
    mTimer = 0;
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mLiquid = null;
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

    mTimer++;
    if ( mTimer == 30 ) {
      mFlames[1][1].setFlame(true);
    } else if ( mTimer == 120 ) {
      mFlames[1][0].setFlame(true);
      mFlames[0][1].setFlame(true);
      mFlames[1][2].setFlame(true);
      mFlames[2][1].setFlame(true);
    } else if ( mTimer == 210 ) {
      mFlames[0][0].setFlame(true);
      mFlames[2][0].setFlame(true);
      mFlames[0][2].setFlame(true);
      mFlames[2][2].setFlame(true);
    }
    
  } // Room.advance()

} // class RoomE08
