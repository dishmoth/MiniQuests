/*
 *  RoomB04.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BitBuffer;
import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Bullet;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.Splatter;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.Statue;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.TinyStory;
import com.dishmoth.miniquests.game.Wall;
import com.dishmoth.miniquests.game.WallLeft;

// the room "B04"
public class RoomB04 extends Room {

  // blocks for the room
  private static final String kBlocks[][] = { { "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "000    000",
                                                "000    000",
                                                "000    000",
                                                "000    000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000" },
                                              
                                              { "          ",
                                                "          ",
                                                "  1    1  ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "  1    1  ",
                                                "          ",
                                                "          " } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "Bx",   // blue 
                                                  "x#" }; // blue-white
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.DOWN, 8,0, "Bx",0, -1, RoomB03.class, 1),
              new Exit(Env.LEFT, 3,0, "Bx",0, -1, RoomB05.class, 0) };

  // time delay until statues react
  private static final int kTimeStatues = 25;

  // time delay while statues change colour
  private static final int kTimeHighlight = 15;
  
  // references to the statues
  private Statue mStatues[];
  
  // whether this room has been completed already
  private boolean mCompleted;

  // delay until statues react
  private int mStatuesTimer;
  
  // delay until statues change back
  private int mHighlightTimer;
  
  // constructor
  public RoomB04() {

    mCompleted = false;

  } // constructor

  // serialize the room state by writing bits to the specified buffer
  @Override
  public void save(BitBuffer buffer) {
    
    buffer.writeBit(mCompleted);
    
  } // Room.save()

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  @Override
  public boolean restore(int version, BitBuffer buffer) { 
    
    if ( buffer.numBitsToRead() < 1 ) return false;
    mCompleted = buffer.readBit();
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

    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,0) );

    Exit exits[] = ( mCompleted 
                     ? kExits 
                     : new Exit[]{ kExits[0] } );
    addBasicWalls(exits, spriteManager);

    mStatues = new Statue[]{ new Statue(7, 2, 2, Env.LEFT, 0),
                             new Statue(2, 7, 2, Env.LEFT, 0),
                             new Statue(7, 7, 2, Env.LEFT, 0) };
    for ( Statue s : mStatues ) {
      spriteManager.addSprite(s);
    }
    
    mStatuesTimer = 0;
    mHighlightTimer = 0;
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mStatues = null;
    
  } // Room.discardResources()
  
  // update the room (events may be added or processed)
  @Override
  public void advance(LinkedList<StoryEvent> storyEvents,
                      SpriteManager          spriteManager) {

    // check exits
    final int exitIndex = checkExits(kExits);
    if ( exitIndex != -1 ) {
      storyEvents.add(new EventRoomChange(kExits[exitIndex].mDestination,
                                          kExits[exitIndex].mEntryPoint));
      return;
    }

    // return statues to normal colour and open door
    if ( mHighlightTimer > 0 ) {
      if ( --mHighlightTimer == 0 ) {
        for ( Statue s : mStatues ) s.setColour(0);

        final int x0 = 0,
                  y0 = kExits[1].mDoorXYPos,
                  z0 = kExits[1].mDoorZPos;
        Wall wall = (Wall)spriteManager.findSpriteOfType( WallLeft.class );
        wall.addDoor(y0, z0, kExits[1].mFloorColour, kExits[1].mFloorDrop);
        spriteManager.addSprite( new Splatter(x0-1, y0, z0, -1, 5,
                                              (byte)0, -1) );
        
        Env.sounds().play(Sounds.SUCCESS);
      }
    }
    
    // check the statues
    if ( !mCompleted && mPlayer != null && !mPlayer.isActing() &&
         mPlayer.getXPos() == 2 && mPlayer.getYPos() == 2 &&
         mPlayer.getZPos() == 2 && mPlayer.getDirec() == Env.LEFT &&
         spriteManager.findSpriteOfType(Bullet.class) == null ) {
      if ( mStatuesTimer > 0 ) {
        mStatuesTimer -= 1;
      } else {
        mHighlightTimer = kTimeHighlight;
        for ( Statue s : mStatues ) s.setColour(1);
        Env.sounds().play(Sounds.SWITCH_ON);
        mCompleted = true;
        storyEvents.add(new TinyStory.EventSaveGame());
      }
    } else {
      mStatuesTimer = kTimeStatues;
    }

  } // Room.advance()
  
} // class RoomB04
