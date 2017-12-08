/*
 *  RoomB11.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

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
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.QuestStory;
import com.dishmoth.miniquests.game.Triffid;
import com.dishmoth.miniquests.game.WallSwitch;

// the room "B11"
public class RoomB11 extends Room {

  // unique identifier for this room
  public static final String NAME = "B11";
  
  // blocks for beneath the room
  private static final String kLowerBlocks[][] = { { "0000000000",
                                                     "0000000000",
                                                     "0000000000",
                                                     "0000000000",
                                                     "0000000000",
                                                     "0000000000",
                                                     "0000000000",
                                                     "0000000000",
                                                     "0000000000",
                                                     "0000000000" } };
  
  // blocks for the room
  private static final String kBlocks[][] = { { " 11111    ",
                                                " 11111    ",
                                                " 11111    ",
                                                " 11111    ",
                                                " 11111    ",
                                                "          ",
                                                "          ",
                                                " 11111    ",
                                                " 1   1    ",
                                                " 1   1    " } };

  // movable blocks
  private static final String kBridgeBlocks[] = { "222",
                                                  "2  ",
                                                  "2  ",
                                                  "222",
                                                  "2  ",
                                                  "2  ",
                                                  "2  ",
                                                  "2  ",
                                                  "2  ",
                                                  "2  " };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "7u",   // grey 
                                                  "#d",   // pink
                                                  ":T" }; // plum 
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.DOWN, 5,10, "#d",1, 0, RoomB08.NAME, 4) };

  // details of different camera height levels
  private static final CameraLevel kCameraLevels[]
                                     = { new CameraLevel(10, -100, +100) };
  
  // details of the path followed by enemies
  private static final CritterTrack kCritterTrack 
                    = new CritterTrack(new String[]{ "+++++",
                                                     "+   +",
                                                     "+   +",
                                                     "+   +",
                                                     "+++++" }, 1, 5);

  // rate at which the upper and lower blocks move
  private static final int kTimeBlockShift = 3;
  
  // how far the blocks move
  private static final int kHalfBridgeShift = 6,
                           kFullBridgeShift = 9;
  
  // references to the switch
  private WallSwitch mSwitch;
  
  // how far the bridge is extended (0 => none, 1 => half, 2 => full) 
  private int mBridgeState;
  
  // ticks until the next block movement
  private int mBridgeTimer;
  
  // how far the blocks have moved
  private int mBridgeShift;
  
  // references to the movable of blocks
  private BlockArray mBridgeBlocks;

  // constructor
  public RoomB11() {

    super(NAME);

    mBridgeState = 0;

  } // constructor

  // serialize the room state by writing bits to the specified buffer
  @Override
  public void save(BitBuffer buffer) {
    
    buffer.write(mBridgeState, 2);
    
  } // Room.save()

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  @Override
  public boolean restore(int version, BitBuffer buffer) { 
    
    mBridgeState = buffer.read(2);
    if ( mBridgeState < 0 || mBridgeState > 2 ) return false;
    return true; 
    
  } // Room.restore() 
  
  // how far the blocks should have been moved
  // (note: this function may be called by RoomB07 and RoomB08)
  public int bridgeExtent() { 
    
    return ( (mBridgeState == 0) ? 0
           : (mBridgeState == 1) ? kHalfBridgeShift
                                 : kFullBridgeShift );
    
  } // bridgeExtent()

  // retrieve the pattern of bridge blocks
  // (note: this function may be called by RoomB08)
  public String[] getBridgeBlocks() { return kBridgeBlocks; }
  
  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < kExits.length );
    setPlayerAtExit(kExits[entryPoint], kCameraLevels);
    return mPlayer;
    
  } // createPlayer()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {

    spriteManager.addSprite( new BlockArray(kLowerBlocks, kBlockColours, 
                                            0,0,0) );
    
    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,10) );
    addBasicWalls(kExits, spriteManager);

    Critter critter = new Critter(2,9,10, Env.RIGHT, kCritterTrack);
    critter.easilyStunned(true);
    spriteManager.addSprite(critter);

    Triffid triffid = new Triffid(3,7,10, Env.DOWN);
    triffid.setFullyGrown();
    spriteManager.addSprite(triffid);

    mBridgeShift = bridgeExtent();
    mBridgeTimer = 0;
    makeBridgeBlocks(spriteManager);
    
    makeWallSwitch(spriteManager);

  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mSwitch = null;
    mBridgeBlocks = null;
    
  } // Room.discardResources()
  
  // create or recreate the movable blocks 
  private void makeBridgeBlocks(SpriteManager spriteManager) {
    
    if ( mBridgeBlocks != null ) spriteManager.removeSprite(mBridgeBlocks);

    String blocks[] = new String[ kBridgeBlocks.length - mBridgeShift ];
    for ( int k = 0 ; k < blocks.length ; k++ ) blocks[k] = kBridgeBlocks[k];
    
    mBridgeBlocks = new BlockArray(new String[][]{blocks}, 
                                   kBlockColours, 7, 0, 10);
    spriteManager.addSprite(mBridgeBlocks);
    
  } // makeBridgeBlocks()

  // create or recreate the bridge switch
  private void makeWallSwitch(SpriteManager spriteManager) {
    
    if ( mSwitch != null ) spriteManager.removeSprite(mSwitch);

    if ( mBridgeState == 2 && mBridgeShift == bridgeExtent() ) {
      mSwitch = new WallSwitch(Env.UP, 1, 12, new String[]{"u7"}, false);
    } else {
      mSwitch = new WallSwitch(Env.UP, 1, 12, new String[]{"T7",":7"}, true);
      mSwitch.setState( (mBridgeState==0) ? 0 : 1 );
    }
    
    spriteManager.addSprite(mSwitch);
    
  } // makeWallSwitch()
  
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

    // process the story event list
    boolean saveGameEvent = false;
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();

      // the switch has been shot
      if ( event instanceof WallSwitch.EventStateChange ) {
        if ( mBridgeState == 0 ) {
          RoomB07 roomDiag = (RoomB07)findRoom(RoomB07.NAME);
          assert( roomDiag != null );
          boolean barrier = ( roomDiag.bridgeExtent() == 0 );
          mBridgeState = ( barrier ? 1 : 2 ); 
        } else {
          mBridgeState = 0;
        }
        saveGameEvent = true;
        mBridgeTimer = 2*kTimeBlockShift;
        it.remove();
      }

    }
    if ( saveGameEvent ) storyEvents.add(new QuestStory.EventSaveGame());
    
    // move the bridge blocks
    if ( mBridgeTimer > 0 ) {
      mBridgeTimer--;
    } else {
      mBridgeTimer = kTimeBlockShift;
      if ( mBridgeState == 0 && mBridgeShift > 0 ) {
        mBridgeShift -= 1;
        makeBridgeBlocks(spriteManager);
      } else if ( mBridgeState == 1 && mBridgeShift < kHalfBridgeShift ) {
        mBridgeShift += 1;
        makeBridgeBlocks(spriteManager);
        if ( mBridgeShift == kHalfBridgeShift ) {
          Env.sounds().play(Sounds.SWITCH_OFF);
        }
      } else if ( mBridgeState == 2 && mBridgeShift < kFullBridgeShift ) {
        mBridgeShift += 1;
        makeBridgeBlocks(spriteManager);
        if ( mBridgeShift == kFullBridgeShift ) {
          Env.sounds().play(Sounds.SUCCESS);
          makeWallSwitch(spriteManager);
        }
      }
    }
    
  } // Room.advance()
  
} // class RoomB11
