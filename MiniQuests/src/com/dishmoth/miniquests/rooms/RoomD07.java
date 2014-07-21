/*
 *  RoomD07.java
 *  Copyright Simon Hern 2014
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.CritterTrack;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.Spikes;
import com.dishmoth.miniquests.game.Spook;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.WallSwitch;

// the room "D07"
public class RoomD07 extends Room {

  // unique identifier for this room
  public static final String NAME = "D07";
  
  // main blocks for the room
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
                                                
                                              { " 2       2",
                                                " 2       2",
                                                " 222222222",
                                                "         1",
                                                "         1",
                                                "111      1",
                                                "  1      1",
                                                "  1112   1",
                                                "     2   1",
                                                "     2    " },
                                                
                                              { " 3       3",
                                                " 2       2",
                                                " 222232222",
                                                "         1",
                                                "         1",
                                                "111      1",
                                                "  1      1",
                                                "  1112   1",
                                                "     2   1",
                                                "     3    " } };

  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "SE",
                                                  "6U",
                                                  "gU",
                                                  "OU" }; 
  
  // details of exit/entry points for the room 
  private static final Exit kExits[][] 
          = { { new Exit(Env.RIGHT, 5,4, "#U",1, 0, RoomD03.NAME, 2),
                new Exit(Env.LEFT,  4,4, "6U",2, 0, RoomD08.NAME, 0),
                new Exit(Env.RIGHT, 1,4, "#U",2, 0, "",0) },
  
              { new Exit(Env.RIGHT, 5,4, "#U",1, 0, RoomD06.NAME, 2),
                new Exit(Env.LEFT,  4,4, "6U",2, 0, RoomD08.NAME, 0),
                new Exit(Env.RIGHT, 1,4, "#U",2, 0, "",0) },
              
              { new Exit(Env.RIGHT, 5,4, "#U",1, 0, "",0),
                new Exit(Env.LEFT,  4,4, "6U",2, 0, RoomD08.NAME, 0),
                new Exit(Env.RIGHT, 1,4, "#U",2, 0, "",0) },
              
              { new Exit(Env.RIGHT, 5,4, "#U",1, 0, "",0),
                new Exit(Env.LEFT,  4,4, "6U",2, 0, RoomD08.NAME, 0),
                new Exit(Env.RIGHT, 1,4, "#U",2, 0, RoomD04.NAME, 3) } };

  // details of different camera height levels
  private static final CameraLevel kCameraLevels[]
                                     = { new CameraLevel(3, -100, +100) };

  // path the spooks follow
  private static final CritterTrack kTrack = new CritterTrack(
                                                new String[]{" +       +",
                                                             " +       +",
                                                             " +++++++++",
                                                             "     +    ",
                                                             "     +    ",
                                                             "     +    ",
                                                             "     +    ",
                                                             "     +    ",
                                                             "     +    ",
                                                             "     +    "} );
  
  // how long the completed bridge is
  private static final int kBridgeMaxLength = 4;
  
  // how much time until the bridge shrinks
  private static final int kBridgeShrinkDelay      = 33,
                           kBridgeShrinkDelayShort = 3;
  
  // how long until the next monster appears
  private static final int kSpookDelay       = 75, //65,
                           kSpookDelayStart  = 10,
                           kSpook3Delay      = 155,
                           kSpook3DelayStart = 30;
  
  // the current exits, based on room D02's twist
  private Exit mExits[];

  // whether the bridge has been completed yet
  private boolean mBridgeDone;
  
  // reference to the bridge blocks
  private BlockArray mBridgeBlocks;
  
  // how many bridge blocks have been added
  private int mBridgeLength;
  
  // time until the bridge shrinks
  private int mBridgeShrinkTimer;
  
  // time until the next monster
  private int mSpook1Timer,
              mSpook2Timer,
              mSpook3Timer;
  
  // constructor
  public RoomD07() {

    super(NAME);

    mBridgeDone = false;
    
  } // constructor

  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < mExits.length );
    setPlayerAtExit(mExits[entryPoint], kCameraLevels);
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
    
    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,0) );
    
    addBasicWalls(mExits, spriteManager);

    WallSwitch ws = new WallSwitch(Env.UP, 5, 6, 
                                   new String[]{"a7","u7"}, false);
    if ( mBridgeDone ) ws.setState(1);
    spriteManager.addSprite(ws);
    
    mBridgeLength = ( mBridgeDone ? kBridgeMaxLength : 0 );
    mBridgeBlocks = null;
    makeBridgeBlocks(spriteManager);
    mBridgeShrinkTimer = 0;
    
    mSpook1Timer = kSpookDelayStart;
    mSpook2Timer = kSpookDelayStart + kSpookDelay/2;
    mSpook3Timer = kSpook3DelayStart;
    
    Spikes spikes = new Spikes(0,0,0, 10,10, false, "S0");
    spikes.setSilent(true);
    spriteManager.addSprite(spikes);
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mBridgeBlocks = null;
    
  } // Room.discardResources()
  
  // update the bridge length
  private void makeBridgeBlocks(SpriteManager spriteManager) {
    
    if ( mBridgeBlocks != null ) {
      spriteManager.removeSprite(mBridgeBlocks);
      mBridgeBlocks = null;
    }
    
    if ( mBridgeLength == 0 ) return;
    
    String blocks[][] = new String[1][mBridgeLength];
    for ( int k = 0 ; k < blocks[0].length ; k++ ) blocks[0][k] = "2";
    mBridgeBlocks = new BlockArray(blocks, kBlockColours, 5, 3, 4);
    spriteManager.addSprite(mBridgeBlocks);
    
  } // makeBridgeBlocks()
  
  // update the room (events may be added or processed)
  @Override
  public void advance(LinkedList<StoryEvent> storyEvents,
                      SpriteManager          spriteManager) {

    // check the exits
    final int exitIndex = checkExits(mExits);
    if ( exitIndex != -1 ) {
      storyEvents.add(new EventRoomChange(mExits[exitIndex].mDestination,
                                          mExits[exitIndex].mEntryPoint));
      return;
    }

    // check the switch
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();
      if ( event instanceof WallSwitch.EventStateChange ) {
        assert( mBridgeLength < kBridgeMaxLength );
        boolean impossible = ( mPlayer != null && 
                               mPlayer.getYPos() >= 7 &&
                               mBridgeLength == kBridgeMaxLength-1 );
        if ( impossible ) {
          Env.debug("Warning: unfair death (sorry!)");
          WallSwitch ws = ((WallSwitch.EventStateChange)event).mSwitch;
          ws.setState(0);
        } else {
          mBridgeLength += 1;
          makeBridgeBlocks(spriteManager);
          if ( mBridgeLength == kBridgeMaxLength ) {
            mBridgeDone = true;
            mBridgeShrinkTimer = 0;
            Env.sounds().play(Sounds.SUCCESS, 3);
          } else {
            WallSwitch ws = ((WallSwitch.EventStateChange)event).mSwitch;
            ws.setState(0);
            mBridgeShrinkTimer = kBridgeShrinkDelay;
          }
        }
        it.remove();
      }  
    }

    // shrink the bridge
    if ( !mBridgeDone && mBridgeLength > 0 ) {
      assert( mBridgeShrinkTimer > 0 );
      if ( --mBridgeShrinkTimer == 0 ) {
        mBridgeLength -= 1;
        makeBridgeBlocks(spriteManager);
        mBridgeShrinkTimer = kBridgeShrinkDelayShort;
      }
    }

    // unleash the spooks
    if ( --mSpook1Timer == 0 ) {
      mSpook1Timer = kSpookDelay;
      Spook spook1 = new Spook(9,9,4, Env.DOWN, kTrack);
      spook1.vanishAfterSteps(5);
      spriteManager.addSprite(spook1);
    }
    if ( --mSpook2Timer == 0 ) {
      mSpook2Timer = kSpookDelay;
      Spook spook = new Spook(1,9,4, Env.DOWN, kTrack);
      spook.vanishAfterSteps(5);
      spriteManager.addSprite(spook);
    }
    if ( mBridgeDone ) {
      if ( --mSpook3Timer == 0 ) {
        mSpook3Timer = kSpook3Delay;
        Spook spook = new Spook(5,0,4, Env.UP, kTrack);
        spook.vanishAfterSteps(6);
        spriteManager.addSprite(spook);
      }
    }

    // trigger spikes on floor
    if ( mPlayer != null && mPlayer.getZPos() == 0 && !mPlayer.isActing() ) {
      Spikes sp = (Spikes)spriteManager.findSpriteOfType(Spikes.class);
      if ( !sp.active() ) {
        sp.trigger();
        Env.sounds().play(Sounds.SPIKES);
      }
    }
    
  } // Room.advance()

} // class RoomD07
