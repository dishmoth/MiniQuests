/*
 *  RoomA01.java
 *  Copyright Simon Hern 2010
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Brain;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Flame;
import com.dishmoth.miniquests.game.Ladder;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;

// the room "A01"
public class RoomA01 extends Room {

  // all visible blocks for the room
  private static final String kBlocks[][] = { { "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000" },
                                                
                                              { " 000000000",
                                                "   0000000",
                                                "    000000",
                                                "     00000",
                                                "     00000",
                                                "     00000",
                                                "     00000",
                                                "    000000",
                                                "   0000000",
                                                " 000000000" },
                                                
                                              { "       0 0",
                                                "         0",
                                                "         0",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "         0",
                                                "         0",
                                                "       0 0" },
                                                
                                              { "       0 0",
                                                "         0",
                                                "         0",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "         0",
                                                "         0",
                                                "       0 0" },
                                                
                                              { "       0 0",
                                                "         0",
                                                "         0",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "         0",
                                                "         0",
                                                "       0 0" },
                                                
                                              { "         0",
                                                "         0",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "         0",
                                                "         0" },
                                                
                                              { "         0",
                                                "         0",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "         0",
                                                "         0" },
                                                
                                              { "         0",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "         0" },
                                                
                                              { "         0",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "         0" },
                                                
                                              { "         0",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "         0" },
                                                
                                              { "         0",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "         0" } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "td" }; // pink 
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { // note: dummy exit at index 0
              new Exit(Env.LEFT, 4,0, "td",0, -1, RoomA02.class, 0) };

  // constructor
  public RoomA01() {

  } // constructor

  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < kExits.length+1 );
    
    if ( entryPoint == 0 ) {
      // special case: start of game
      mPlayer = new Player(8, 4, 2, Env.LEFT);
      mPlayer.addBrain(new Brain.ZombieModule(new int[]{ Env.NONE,1, 
                                                         Env.LEFT,25 }));
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
    
    spriteManager.addSprite( new Ladder() );
    
    Flame flame1 = new Flame(7,0,8);
    flame1.warmUp();
    spriteManager.addSprite(flame1);
    
    Flame flame2 = new Flame(7,9,8);
    flame2.warmUp();
    spriteManager.addSprite(flame2);

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

  } // Room.advance()

} // class RoomA01
