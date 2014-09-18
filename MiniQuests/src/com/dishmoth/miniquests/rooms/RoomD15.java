/*
 *  RoomD15.java
 *  Copyright Simon Hern 2014
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Bullet;
import com.dishmoth.miniquests.game.Chest;
import com.dishmoth.miniquests.game.CritterTrack;
import com.dishmoth.miniquests.game.EgaTools;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Splatter;
import com.dishmoth.miniquests.game.Spook;
import com.dishmoth.miniquests.game.Sprite;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;

// the room "D15"
public class RoomD15 extends Room {

  // unique identifier for this room
  public static final String NAME = "D15";
  
  // main blocks for the room
  private static final String kBlocks[][] = { { "0000000000",
                                                "0        0",
                                                "0  00000 0",
                                                "0  00000 0",
                                                "0  00000 0",
                                                "0   000  0",
                                                "0   000  0",
                                                "0   000  0",
                                                "0   000  0",
                                                "0000000000" },
                                                
                                              { "0000000000",
                                                "0        0",
                                                "0  00000 0",
                                                "0  00000 0",
                                                "0  00000 0",
                                                "0   000  0",
                                                "0   000  0",
                                                "0   000  0",
                                                "0   000  0",
                                                "0000000000" },
                                                
                                              { "0000000000",
                                                "0        0",
                                                "0  11111 0",
                                                "0  11111 0",
                                                "0  11111 0",
                                                "0   111  0",
                                                "0   111  0",
                                                "0   111  0",
                                                "0   111  0",
                                                "0000111000" },
                                                
                                              { "          ",
                                                "          ",
                                                "   11111  ",
                                                "   11111  ",
                                                "   11111  ",
                                                "    111   ",
                                                "    111   ",
                                                "          ",
                                                "          ",
                                                "          " },
                                              
                                              { "          ",
                                                "          ",
                                                "   11111  ",
                                                "   11111  ",
                                                "   11111  ",
                                                "    111   ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                              
                                              { "          ",
                                                "          ",
                                                "   11111  ",
                                                "   11111  ",
                                                "   11111  ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " } };

  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "YU",
                                                  "tU" }; 
  
  // path followed by monsters
  private static final CritterTrack kPath = new CritterTrack(
                                              new String[]{ "++++++++++",
                                                            "+        +",
                                                            "+        +",
                                                            "+        +",
                                                            "+        +",
                                                            "+        +",
                                                            "+        +",
                                                            "+        +",
                                                            "+        +",
                                                            "++++++++++" });
  
  // details of exit/entry points for the room 
  private static final Exit kExits[][] 
          = { { new Exit(Env.DOWN, 2,0, "YU",0, -1, RoomD14.NAME, 1),
                new Exit(Env.UP,   8,0, "#U",0, -1, "",0) },
  
              { new Exit(Env.DOWN, 2,0, "YU",0, -1, RoomD14.NAME, 1),
                new Exit(Env.UP,   8,0, "#U",0, -1, RoomD05.NAME, 2) },
              
              { new Exit(Env.DOWN, 2,0, "YU",0, -1, RoomD14.NAME, 1),
                new Exit(Env.UP,   8,0, "#U",0, -1, "",0) },
              
              { new Exit(Env.DOWN, 2,0, "YU",0, -1, RoomD14.NAME, 1),
                new Exit(Env.UP,   8,0, "#U",0, -1, "",0) } };
  
  // the current exits, based on room D02's twist
  private Exit mExits[];

  // whether the room has been completed
  private boolean mRoomDone;
  
  // reference to the chest object
  private Chest mChest;

  // constructor
  public RoomD15() {

    super(NAME);

    mRoomDone = false;
    
  } // constructor

  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < mExits.length );
    setPlayerAtExit(mExits[entryPoint]);
    return mPlayer;
    
  } // createPlayer()
  
  // configure exits based on the room D02's twist
  private void prepareExits() {
    
    RoomD02 twistRoom = (RoomD02)findRoom(RoomD02.NAME);
    assert( twistRoom != null );
    mExits = kExits[ twistRoom.twist() ];    
    
  } // prepareExist()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {
    
    prepareExits();
    
    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,-4) );
    
    addBasicWalls(mExits, spriteManager);

    if ( mRoomDone ) {
      mChest = null;
    } else {
      mChest = new Chest(4, 6, 6, Env.DOWN);
      spriteManager.addSprite(mChest);
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

    // check exits
    final int exitIndex = checkExits(mExits);
    if ( exitIndex != -1 ) {
      storyEvents.add(new EventRoomChange(mExits[exitIndex].mDestination,
                                          mExits[exitIndex].mEntryPoint));
      return;
    }

    // check events
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();
      if ( event instanceof Spook.EventKilled ) {
        it.remove();
      }
    }    
    
    // check the chest
    if ( !mRoomDone ) {
      assert( mChest != null );
      boolean hit = false;
      for ( Sprite sp : spriteManager.list() ) {
        if ( sp instanceof Bullet ) {
          Bullet bullet = (Bullet)sp;
          int x = bullet.getXPos() + Env.STEP_X[bullet.getDirec()],
              y = bullet.getYPos() + Env.STEP_Y[bullet.getDirec()],
              z = bullet.getZPos();
          if ( x >= 4 && x <= 6 && y >= 6 && y <= 7 && z >= 6 ) {
            hit = true;
          }
        }
      }
      if ( hit ) {
        spriteManager.removeSprite(mChest);
        mChest = null;
        final byte col = EgaTools.decodePixel('K');
        final int y = 6,
                  z = 7,
                  h = 2;
        for ( int x = 4 ; x <= 6 ; x++ ) {
          spriteManager.addSprite(new Splatter(x,y,z, -1, h, col, -1));
        }
        spriteManager.addSprite(new Spook(0,0,0, Env.DOWN, kPath));
        spriteManager.addSprite(new Spook(9,0,0, Env.RIGHT, kPath));
        spriteManager.addSprite(new Spook(9,9,0, Env.UP, kPath));
        spriteManager.addSprite(new Spook(0,9,0, Env.LEFT, kPath));
        mRoomDone = true;
      }
    }
    
  } // Room.advance()

} // class RoomD15
