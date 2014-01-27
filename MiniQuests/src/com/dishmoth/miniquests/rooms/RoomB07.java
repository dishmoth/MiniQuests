/*
 *  RoomB07.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BitBuffer;
import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Critter;
import com.dishmoth.miniquests.game.CritterTrack;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.TinyStory;
import com.dishmoth.miniquests.game.WallSwitch;

// the room "B07"
public class RoomB07 extends Room {

  // the basic blocks for the room
  private static final String kBlocks[][] = { { "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "   0000000",
                                                "0  0  0  0",
                                                "0  0  0  0",
                                                "0000000000" },
                                                
                                              { "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "   0000000",
                                                "   0  0  0",
                                                "   0  0  0",
                                                "   0000000" },
                                                
                                              { "   0000000",
                                                "   0  0  0",
                                                "0  0  0  0",
                                                "0000000000",
                                                "      0   ",
                                                "      0   ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "   0000000",
                                                "0  0  0  0",
                                                "0  0  0  0",
                                                "   0000000",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "0         ",
                                                "0         ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "00        ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { " 00       ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "  00      ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "   00     ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "    00    ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "     00   ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "      00  ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "       00 ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "        00",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "   1     0",
                                                "   1     0",
                                                "1111     0",
                                                "         0",
                                                "         0",
                                                "         0",
                                                "1        0",
                                                "          ",
                                                "          ",
                                                "          " },
                                                };

  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "NY",   // green
                                                  "lT" }; // plum with pink
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.LEFT,  2,10, "NY",1, 0, RoomB03.class, 2),
              new Exit(Env.LEFT,  3,38, "lT",1, 2, RoomB08.class, 0),
              new Exit(Env.LEFT,  7,38, "lT",1, 2, RoomB08.class, 1),
              new Exit(Env.UP,    3,38, "#T",1, 2, RoomB12.class, 0) };

  // details of different camera height levels
  private static final CameraLevel kCameraLevels[]
                                     = { new CameraLevel(10, -100,   20),
                                         new CameraLevel(23,   20,   32),
                                         new CameraLevel(36,   34, +100) };
  
  // details of the paths followed by enemies
  private static final CritterTrack kCritterTrack
                    = new CritterTrack(new String[]{ "+++++++",
                                                     "+  +  +",
                                                     "+  +  +",
                                                     "+++++++",
                                                     "   +   ",
                                                     "   +   ",
                                                     "+++++++",
                                                     "+  +  +",
                                                     "+  +  +",
                                                     "+++++++" }, 3, 0); 
  
  // rate at which the bridge blocks move
  private static final int kTimeBridgeShift = 2;
  
  // whether the bridge blocks are in this room (0) or next door (1)
  private int mBridgeState;

  // reference to the blocks making a bridge
  private BlockArray mBridgeBlocks;

  // ticks until the next bridge movement
  private int mBridgeTimer;

  // current number of bridge blocks in this room
  private int mBridgeExtent;
  
  // constructor
  public RoomB07() {

    mBridgeState = 1;
    mBridgeExtent = 0;
    mBridgeTimer = 0;
    
  } // constructor

  // serialize the room state by writing bits to the specified buffer
  @Override
  public void save(BitBuffer buffer) {
    
    buffer.write(mBridgeState, 1);
    
  } // Room.save()

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  @Override
  public boolean restore(int version, BitBuffer buffer) { 
    
    mBridgeState = buffer.read(1);
    if ( mBridgeState < 0 || mBridgeState > 1 ) return false;
    
    mBridgeExtent = ( (mBridgeState==0) ? 8 : 0 );
    
    return true; 
    
  } // Room.restore() 
  
  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < kExits.length );
    setPlayerAtExit(kExits[entryPoint], kCameraLevels);
    return mPlayer;
    
  } // createPlayer()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mBridgeBlocks = null;
    
  } // Room.discardResources()

  // returns the current length of the bridge inside this room
  // (note: this function may be called by RoomB08 or RoomB11)
  public int bridgeExtent() { return mBridgeExtent; }
  
  // update the bridge, returns true if it has changed
  // (note: this function may be called by RoomB08)
  public boolean updateBridge() {
    
    if ( mBridgeTimer > 0 ) {
      mBridgeTimer--;
      return false;
    }
    mBridgeTimer = kTimeBridgeShift;
    
    if ( mBridgeState == 0 && mBridgeExtent < 8 ) {
      mBridgeExtent += 1;
      return true;
    } else if ( mBridgeState == 1 && mBridgeExtent > 0 ) {
      mBridgeExtent -= 1;
      return true;
    }
    
    return false;
    
  } // updateBridge
  
  // update the blocks for the bridge 
  private void makeBridgeBlocks(SpriteManager spriteManager) {
    
    if ( mBridgeBlocks != null ) spriteManager.removeSprite(mBridgeBlocks);
    
    if ( mBridgeExtent == 0 ) {
      mBridgeBlocks = null;
    } else {
      assert( mBridgeExtent > 0 && mBridgeExtent <= 8 );
      char blocks[] = new char[mBridgeExtent];
      Arrays.fill(blocks, '1');
      mBridgeBlocks = new BlockArray(new String[][]{{ new String(blocks) }},
                                     kBlockColours, 1, 3, 38);
      spriteManager.addSprite(mBridgeBlocks);
    }
    
  } // makeBridgeBlocks() 
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {

    spriteManager.addSprite(new BlockArray(kBlocks, kBlockColours, 0, 0, 10));
    addBasicWalls(kExits, spriteManager);

    Critter critters[] = new Critter[] 
             { new Critter(4,3,12, Env.LEFT, kCritterTrack),
               new Critter(9,2,12, Env.DOWN, kCritterTrack),
               new Critter(6,5,14, Env.DOWN, kCritterTrack),
               new Critter(3,7,16, Env.DOWN, kCritterTrack),
               new Critter(8,9,16, Env.LEFT, kCritterTrack) };
    for ( int k = 0 ; k < critters.length ; k++ ) {
      critters[k].easilyKilled(true);
      critters[k].setColour(1);
      spriteManager.addSprite(critters[k]);
    }    
    
    WallSwitch ws;
    RoomB11 roomDiag = (RoomB11)findRoom(RoomB11.class);
    assert( roomDiag != null );
    if ( roomDiag.bridgeExtent() < 9 ) {
      ws = new WallSwitch(Env.RIGHT, 3, 40, new String[]{"ou","zu"}, true);
      ws.setState(mBridgeState);
    } else {
      ws = new WallSwitch(Env.RIGHT, 3, 40, new String[]{"7u"}, false);
    }
    spriteManager.addSprite(ws);

    makeBridgeBlocks(spriteManager);
    mBridgeTimer = 0;
    
  } // Room.createSprites()

  // update the room (events may be added or processed)
  @Override
  public void advance(LinkedList<StoryEvent> storyEvents,
                      SpriteManager          spriteManager) {

    // check camera level (unless the player is falling)
    if ( mPlayer != null && !mPlayer.isFalling() ) {
      EventRoomScroll scroll = checkVerticalScroll(kCameraLevels);
      if ( scroll != null ) storyEvents.add(scroll);
    }

    // process the story event list
    boolean saveGameEvent = false;
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();

      // wall switch is shot
      if ( event instanceof WallSwitch.EventStateChange ) {
        WallSwitch.EventStateChange e = (WallSwitch.EventStateChange)event;
        mBridgeState = e.mNewState;
        saveGameEvent = true;
        it.remove();
      }

    }
    if ( saveGameEvent ) storyEvents.add(new TinyStory.EventSaveGame());
    
    // check exits
    final int exitIndex = checkExits(kExits);
    if ( exitIndex != -1 ) {
      storyEvents.add(new EventRoomChange(kExits[exitIndex].mDestination,
                                          kExits[exitIndex].mEntryPoint));
      return;
    }

    // update the bridge
    if ( updateBridge() ) makeBridgeBlocks(spriteManager);
    
    // check whether the player has fallen too far
    if ( mPlayer != null && mPlayer.getZPos() < 0 ) {
      mPlayer.destroy(-1);
    }
    
  } // Room.advance()

} // class RoomB07
