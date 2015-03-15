/*
 *  RoomC10.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BitBuffer;
import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Critter;
import com.dishmoth.miniquests.game.CritterTrack;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.GlowPath;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.TinyStory;

// the room "C10"
public class RoomC10 extends Room {

  // unique identifier for this room
  public static final String NAME = "C10";
  
  // the basic blocks for the room
  private static final String kBlocks[][] = { { "0000000000",
                                                "0111111000",
                                                "0100001000",
                                                "0100001000",
                                                "0100001111",
                                                "0100000000",
                                                "0111100000",
                                                "0000100   ",
                                                "0000100   ",
                                                "0000100   " } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#d",   // pink
                                                  "#F" }; // blue
  
  // details of exit/entry points for the room
  private static final Exit kExits[] 
          = { new Exit(Env.RIGHT, 5,0, "BF",0, -1, RoomC09.NAME, 1), 
              new Exit(Env.DOWN,  4,0, "BF",0, -1, RoomC11.NAME, 0) };
              
  // glowing path
  private static final String kGlowPath[] = { "           ",
                                              " ++++++    ",
                                              " +    +    ",
                                              " +    +    ",
                                              " +    ++++X",
                                              " +         ",
                                              " ++++      ",
                                              "    +      ",
                                              "    +      ",
                                              "    +      " };
  
  // paths followed by enemies
  private static final CritterTrack kCritterTrack 
                    = new CritterTrack(new String[]{ "          ",
                                                     " ++++++   ",
                                                     " +    +   ",
                                                     " +    +   ",
                                                     " +    +   ",
                                                     " +    +   ",
                                                     " ++++++   ",
                                                     "          ",
                                                     "          ",
                                                     "          " });

  // whether the path has been walked yet
  private boolean mPathDone;
  
  // the glowing path
  private GlowPath mPath;
  
  // constructor
  public RoomC10() {

    super(NAME);

    mPathDone = false;
    
  } // constructor

  // serialize the room state by writing bits to the specified buffer
  @Override
  public void save(BitBuffer buffer) {
    
    buffer.writeBit(mPathDone);
    
  } // Room.save()

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  @Override
  public boolean restore(int version, BitBuffer buffer) { 
    
    if ( buffer.numBitsToRead() < 1 ) return false;
    mPathDone = buffer.readBit();
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

    mPath = new GlowPath(kGlowPath, 0, 0, 0, 'B');
    if ( mPathDone ) {
      mPath.setComplete();
    } else {
      kExits[1].mDoor.setClosed(true);
    }
    spriteManager.addSprite(mPath);
    
    spriteManager.addSprite(new Critter(1,7,0, Env.UP,   kCritterTrack));
    spriteManager.addSprite(new Critter(6,7,0, Env.DOWN, kCritterTrack));
    spriteManager.addSprite(new Critter(4,3,0, Env.LEFT, kCritterTrack));

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
      kExits[1].mDoor.setClosed(false);
      Env.sounds().play(Sounds.SUCCESS);
      storyEvents.add(new TinyStory.EventSaveGame());
    }
        
  } // Room.advance()

} // class RoomC10
