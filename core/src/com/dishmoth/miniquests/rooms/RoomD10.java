/*
 *  RoomD10.java
 *  Copyright Simon Hern 2014
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BitBuffer;
import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.CritterTrack;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.FloorSwitch;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.Spook;
import com.dishmoth.miniquests.game.Sprite;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;

// the room "D10"
public class RoomD10 extends Room {

  // unique identifier for this room
  public static final String NAME = "D10";
  
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
  
                                              { "1111111111",
                                                "1        1",
                                                "1        1",
                                                "111111   1",
                                                "     1    ",
                                                "     1    ",
                                                "1    11111",
                                                "1        1",
                                                "1        1",
                                                "1111111111" },
  
                                              { "2111111111",
                                                "1        1",
                                                "1        1",
                                                "111111   2",
                                                "     1    ",
                                                "     1    ",
                                                "2    11111",
                                                "1        1",
                                                "1        1",
                                                "1111111112" } };

  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "mG",
                                                  "62",
                                                  "K2" }; 
  
  // details of exit/entry points for the room 
  private static final Exit kExits[][] 
          = { { new Exit(Env.UP,   5,2, "#2",0, -1, RoomD04.NAME, 4),
                new Exit(Env.DOWN, 1,2, "62",0, -1, RoomD11.NAME, 0) },
  
              { new Exit(Env.UP,   5,2, "#2",0, -1, RoomD03.NAME, 1),
                new Exit(Env.DOWN, 1,2, "62",0, -1, RoomD11.NAME, 0) },
              
              { new Exit(Env.UP,   5,2, "#2",0, -1, "",0),
                new Exit(Env.DOWN, 1,2, "62",0, -1, RoomD11.NAME, 0) },
              
              { new Exit(Env.UP,   5,2, "#2",0, -1, "",0),
                new Exit(Env.DOWN, 1,2, "62",0, -1, RoomD11.NAME, 0) } };
  
  // path the spooks follow
  private static final CritterTrack kTrack = new CritterTrack( 
                                                  new String[]{"++++++++++",
                                                               "+        +",
                                                               "+        +",
                                                               "++++++   +",
                                                               "     +    ",
                                                               "     +    ",
                                                               "+    +++++",
                                                               "+        +",
                                                               "+        +",
                                                               "++++++++++"} );
  
  // time until switch reappears when player dies
  private static final int kSwitchDelay = 20;
  
  // the current exits, based on room D02's twist
  private Exit mExits[];
  
  // reference to the switch
  private FloorSwitch mSwitch;

  // time until switch reappears (or zero)
  private int mSwitchTimer;
  
  // whether room has been completed
  private boolean mDone;
  
  // constructor
  public RoomD10() {

    super(NAME);
    
    mDone = false;

  } // constructor

  // serialize the room state by writing bits to the specified buffer
  @Override
  public void save(BitBuffer buffer) {
    
    buffer.writeBit(mDone);
    
  } // Room.save()

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  @Override
  public boolean restore(int version, BitBuffer buffer) { 
    
    if ( buffer.numBitsToRead() < 1 ) return false;
    mDone = buffer.readBit();
    return true;
    
  } // Room.restore() 
  
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
    
    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,-2) );
    
    addBasicWalls(mExits, spriteManager);

    if ( mDone ) {
      mSwitch = null;
    } else {
      mSwitch = new FloorSwitch(5,5,2, "6K","62");
      spriteManager.addSprite(mSwitch);
    }
    mSwitchTimer = 0;
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mSwitch = null;
    
  } // Room.discardResources()
  
  // update the room (events may be added or processed)
  @Override
  public void advance(LinkedList<StoryEvent> storyEvents,
                      SpriteManager          spriteManager) {

    // check the exits
    final int exitIndex = checkExits(mExits);
    if ( exitIndex != -1 ) {
      storyEvents.add(new EventRoomChange(mExits[exitIndex].mDestination,
                                          mExits[exitIndex].mEntryPoint));
      if ( exitIndex == 1 ) {
        mDone = true;
      }
      return;
    }

    // check the switch
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();
      
      if ( event instanceof FloorSwitch.EventStateChange ) {
        assert( !mDone );
        assert( ((FloorSwitch.EventStateChange)event).mSwitch == mSwitch );
        mSwitch.freezeState(true);
        Env.sounds().play(Sounds.SWITCH_ON);
        
        Spook s1 = new Spook(0,9,2, Env.LEFT, kTrack);
        s1.vanishAfterSteps(29);
        spriteManager.addSprite(s1);
        Spook s2 = new Spook(9,0,2, Env.RIGHT, kTrack);
        s2.vanishAfterSteps(29);
        spriteManager.addSprite(s2);
        Spook s3 = new Spook(0,3,2, Env.DOWN, kTrack);
        s3.vanishAfterSteps(41);
        spriteManager.addSprite(s3);
        Spook s4 = new Spook(9,6,2, Env.UP, kTrack);
        s4.vanishAfterSteps(41);
        spriteManager.addSprite(s4);
        Env.sounds().play(Sounds.SPOOK_EMERGE);
        
        it.remove();
      }  
      
      if ( event instanceof Player.EventKilled ) {
        mSwitchTimer = kSwitchDelay;
      }
      
      if ( event instanceof Spook.EventKilled ) {
        it.remove();
      }
    }

    // make switch reappear, and spooks vanish
    if ( mSwitchTimer > 0 && !mDone ) {
      if ( --mSwitchTimer == 0 ) {
        mSwitch.unfreezeState();
        for ( Sprite s : spriteManager.list() ) {
          if ( s instanceof Spook ) ((Spook)s).vanish();
        }
      }
    }
    
  } // Room.advance()

} // class RoomD10
