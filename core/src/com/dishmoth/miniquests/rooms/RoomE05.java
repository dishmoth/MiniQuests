/*
 *  RoomE05.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.GlowPath;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;

// the room "E05"
public class RoomE05 extends Room {

  // unique identifier for this room
  public static final String NAME = "E05";
  
  // the basic blocks for the room
  private static final String kBlocks[][] = { { "1111111111",
                                                "1  1111  1",
                                                "1  1  1  1",
                                                "1  1  2111",
                                                "111111  11",
                                                "    11  11",
                                                "    111111",
                                                "    1    1",
                                                "00001    1",
                                                "    111111" } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#k",   //
                                                  "#V",   // 
                                                  "hV" }; // 
  
  // details of exit/entry points for the room
  private static final Exit kExits[] 
          = { new Exit(Env.LEFT,  1,0, "#k",0, -1, RoomE04.NAME, 0) }; 
  
  // colour of the glowing path
  private static final char kPathColour = 's';
  
  // glowing path
  private static final String kGlowPath[] = { "           ",
                                              "           ",
                                              "           ",
                                              "           ",
                                              "      X    ",
                                              "      +    ",
                                              "      +    ",
                                              "      +    ",
                                              "+++++++    ",
                                              "           " };
  
  // whether the path has been walked yet
  private boolean mPathDone;
  
  // the glowing path
  private GlowPath mPath;

  // constructor
  public RoomE05() {

    super(NAME);

    mPathDone = false;
    
  } // constructor

  // whether the path is complete
  // (note: this function may be called by RoomC07)
  public boolean pathComplete() { return mPathDone; }
  
  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < kExits.length );
    setPlayerAtExit(kExits[entryPoint]);
    return mPlayer;
    
  } // createPlayer()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mPath = null;
    
  } // Room.discardResources()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {

    addBasicWalls(kExits, spriteManager);

    spriteManager.addSprite(new BlockArray(kBlocks, kBlockColours, 0,0,0));

    mPath = new GlowPath(kGlowPath, -1, 0, 0, kPathColour);
    if ( mPathDone ) {
      mPath.setComplete();
    }
    //spriteManager.addSprite(mPath);
  
  } // Room.createSprites()
  
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

    // check the path
    if ( !mPathDone && mPath.complete() ) {
      mPathDone = true;
    }
        
  } // Room.advance()

} // class RoomE05
