/*
 *  RoomD01.java
 *  Copyright Simon Hern 2010
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Brain;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.Tree;
import com.dishmoth.miniquests.game.TreesRight;
import com.dishmoth.miniquests.game.TreesUp;
import com.dishmoth.miniquests.game.Wall;
import com.dishmoth.miniquests.game.WallDown;

// the room "D01"
public class RoomD01 extends Room {

  // unique identifier for this room
  public static final String NAME = "D01";
  
  // all visible blocks for the room
  private static final String kBlocks[][] = { { "            1  ",
                                                "000000000000100",
                                                "000000000001100",
                                                "000000000001110",
                                                "000000000011110",
                                                "000000001111100",
                                                "000000011111100",
                                                "000000111111110",
                                                "000000111111111",
                                                "000001111111111",
                                                "000011111111111" },
                                                
                                              { "               ",
                                                "           * * ",
                                                "          *  * ",
                                                "          *   *",
                                                "        **    *",
                                                "       *     * ",
                                                "      *      * ",
                                                "     *        *",
                                                "     *         ",
                                                "    *   222    ",
                                                "   *   22222   " }};
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "22",   // grass
                                                  "Y2",   // path
                                                  "#S" }; // entrance
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { // note: dummy exit at index 0
              new Exit(Env.DOWN, 4,2, "#S",0, -1, RoomD03.NAME, 0) };

  // constructor
  public RoomD01() {

    super(NAME);
    
  } // constructor

  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < kExits.length+1 );
    
    if ( entryPoint == 0 ) {
      // special case: start of game
      mPlayer = new Player(7, 10, 0, Env.DOWN);
      mPlayer.addBrain(new Brain.ZombieModule(new int[]{ Env.NONE,1, 
                                                         Env.DOWN,25 }));
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

    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, -5,0,0) );

    spriteManager.addSprite(new TreesRight(0, 0, 0, 0));
    spriteManager.addSprite(new TreesUp(0, 0, 0, 0));

    Wall wall = new WallDown(0, 0, 0);
    for ( Exit exit : kExits ) {
      exit.mDoor = wall.addDoor(exit.mDoorXYPos, exit.mDoorZPos, 
                                exit.mFloorColour, exit.mFloorDrop);
    }
    spriteManager.addSprite(wall);

    Wall extraWall = new WallDown(-Room.kSize, 0, 0);
    spriteManager.addSprite(extraWall);
    
    spriteManager.addSprite(new Tree(9,9,0, 1, 0));
    spriteManager.addSprite(new Tree(8,4,0, 1, 0));

    spriteManager.addSprite(new Tree( 4,8,0, 1, 0));
    spriteManager.addSprite(new Tree( 2,8,0, 0, 0));
    spriteManager.addSprite(new Tree(-1,8,0, 0, 0));
    spriteManager.addSprite(new Tree(-1,6,0, 0, 0));
    spriteManager.addSprite(new Tree(-3,6,0, 0, 0));
    spriteManager.addSprite(new Tree(-4,4,0, 1, 0));
    spriteManager.addSprite(new Tree(-1,4,0, 1, 0));
    spriteManager.addSprite(new Tree(-6,3,0, 1, 0));
    spriteManager.addSprite(new Tree(-5,1,0, 1, 0));
    spriteManager.addSprite(new Tree(-3,0,0, 0, 0));

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

} // class RoomD01
