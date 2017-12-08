/*
 *  RoomC11.java
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
import com.dishmoth.miniquests.game.FloorSwitch;
import com.dishmoth.miniquests.game.GlowPath;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.Sprite;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.QuestStory;

// the room "C11"
public class RoomC11 extends Room {

  // unique identifier for this room
  public static final String NAME = "C11";
  
  // the blocks for the room, in several layers
  private static final String kBlocks[][][] = { { { "  111     ",
                                                    "          ",
                                                    "          ",
                                                    "          ",
                                                    "          ",
                                                    "          ",
                                                    "          ",
                                                    "          ",
                                                    "          ",
                                                    "          " } },

                                                { { "          ",
                                                    "  1       ",
                                                    "111       ",
                                                    "1         ",
                                                    "1         ",
                                                    "          ",
                                                    "          ",
                                                    "          ",
                                                    "          ",
                                                    "          " } },

                                                { { "          ",
                                                    "          ",
                                                    "          ",
                                                    "    11    ",
                                                    "    1     ",
                                                    "10001     ",
                                                    "1   1     ",
                                                    "10001     ",
                                                    "1   1     ",
                                                    "11111     " } },

                                                { { "          ",
                                                    "      000 ",
                                                    "      0 0 ",
                                                    "      1 1 ",
                                                    "      1 1 ",
                                                    "      1 1 ",
                                                    "      1 1 ",
                                                    "      1 1 ",
                                                    "      111 ",
                                                    "          " } },

                                                { { "          ",
                                                    "          ",
                                                    "          ",
                                                    "         1",
                                                    "          ",
                                                    "          ",
                                                    "          ",
                                                    "          ",
                                                    "          ",
                                                    "          " } } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#6",   // green
                                                  "#2" }; // yellow
  
  // details of exit/entry points for the room
  private static final Exit kExits[] 
          = { new Exit(Env.UP,   4,0, "#2",0, -1, RoomC10.NAME, 1),
              new Exit(Env.RIGHT,6,0, "I2",0, -1, RoomC07.NAME, 3) }; 
  
  // colour of the glowing path
  private static final char kPathColour = 'I';
  
  // glowing path
  private static final String kGlowPath[] = { "    X     ",
                                              "  +++     ",
                                              "  +       ",
                                              "+++       ",
                                              "+   +++ ++",
                                              "+   + + + ",
                                              "+   + + + ",
                                              "+   + + + ",
                                              "+   + + + ",
                                              "+   + +++ ",
                                              "+++++     " };

  // paths followed by enemies
  private static final CritterTrack kCritterTrack 
                    = new CritterTrack(new String[]{ "          ",
                                                     "          ",
                                                     "          ",
                                                     "          ",
                                                     "          ",
                                                     "+++++     ",
                                                     "+   +     ",
                                                     "+++++     ",
                                                     "+   +     ",
                                                     "+++++     " });
  
  // switch positions (x,y)
  private static final int kSwitchPos[][] = { {4,9}, {0,5}, {5,6}, {7,8} };

  // how long the main switch stays on for
  private static final int kSwitchDelay[] = { 180, 270, 270, 240 };

  // ticking sounds counting down
  private static final int kTickDelayMax   = 16,
                           kTickDelayMin   = 8,
                           kTickChangeTime = 60;
  
  // how far sprites fall before dying
  private static final int kDropDeadHeight = -26;
  
  // references to the separate blocks (whether or not they are visible)
  private BlockArray mBlocks[];
  
  // how many of the blocks are currently visible
  private int mNumBlocksVisible;
  
  // whether the path has been walked yet
  private boolean mPathDone;
  
  // the glowing path
  private GlowPath mPath;

  // number of switches hit
  private int mNumSwitchesDone;
  
  // reference to the current switch (or null)
  private FloorSwitch mSwitch;
  
  // time until the blocks vanish
  private int mSwitchTimer;
  
  // time until the next tick
  private int mTickTimer,
              mTickDelay;

  // constructor
  public RoomC11() {

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
  
  // whether the path is complete
  // (note: this function may be called by RoomC07)
  public boolean pathComplete() { return mPathDone; }
  
  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < kExits.length );
    setPlayerAtExit(kExits[entryPoint]);
    
    if ( mSwitchTimer > 0 ) {
      mSwitchTimer = 1;
      mTickTimer = 0;
    }
    
    return mPlayer;
    
  } // createPlayer()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mBlocks = null;
    mPath = null;
    mSwitch = null;
    
  } // Room.discardResources()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {

    addBasicWalls(kExits, spriteManager);

    mNumBlocksVisible = 0;
    mNumSwitchesDone = ( mPathDone ? kSwitchPos.length : 0 );

    updateBlocks(spriteManager);
    updateSwitch(spriteManager);
    
    mPath = new GlowPath(kGlowPath, 0, 0, 0, kPathColour);
    if ( mPathDone ) {
      mPath.setComplete();
    } else {
      kExits[1].mDoor.setClosed(true);
    }
    spriteManager.addSprite(mPath);

    mSwitchTimer = 0;
    mTickTimer = mTickDelay = 0;
    
  } // Room.createSprites()

  // create or remove blocks
  private void updateBlocks(SpriteManager spriteManager) {
    
    if ( mBlocks == null ) {
      mBlocks = new BlockArray[ kBlocks.length ];
      for ( int k = 0 ; k < mBlocks.length ; k++ ) {
        mBlocks[k] = new BlockArray(kBlocks[k], kBlockColours, 0,0,0);
      }
    }
    
    int numBlocksOld = mNumBlocksVisible;
    mNumBlocksVisible = mNumSwitchesDone+1;
    
    for ( int k = 0 ; k < mBlocks.length ; k++ ) {
      if ( k >= numBlocksOld && k < mNumBlocksVisible ) {
        spriteManager.addSprite( mBlocks[k] );
      } else if ( k >= mNumBlocksVisible && k < numBlocksOld ) {
        spriteManager.removeSprite( mBlocks[k] );
      }
    }

  } // updateBlocks()
  
  // create the next switch (and remove the old one)
  private void updateSwitch(SpriteManager spriteManager) {

    if ( mSwitch != null ) {
      spriteManager.removeSprite(mSwitch);
      mSwitch = null;
    }
    
    if ( mNumSwitchesDone < kSwitchPos.length ) {
      int xy[] = kSwitchPos[mNumSwitchesDone];
      mSwitch = new FloorSwitch(xy[0], xy[1], 0, "#q", "#2");
      spriteManager.addSprite(mSwitch);
    }
    
  } // updateSwitch()
  
  // create critter sprites
  private void makeCritters(SpriteManager spriteManager) {

    Critter critters[] = { new Critter(3,0,0, Env.LEFT, kCritterTrack),
                           new Critter(4,0,0, Env.RIGHT, kCritterTrack),
                           new Critter(4,4,0, Env.RIGHT, kCritterTrack) };
    for ( Critter critter : critters ) {
      critter.setColour(1);
      critter.easilyKilled(true);
      spriteManager.addSprite(critter);
    }
    
  } // makeCritters()
  
  // show or hide the blocks (and switch)
  private void vanishBlocks(boolean vanish) {
    
    for ( int k = 1 ; k < mBlocks.length ; k++ ) {
      BlockArray blocks = mBlocks[k];
      blocks.mDrawDisabled = vanish;
    }
    if ( mSwitch != null ) mSwitch.mDrawDisabled = vanish;
    
  } // vanishBlocks()
  
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

    // check events (switches)
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();

      if ( event instanceof FloorSwitch.EventStateChange ) {
        mNumSwitchesDone += 1;
        updateSwitch(spriteManager);
        updateBlocks(spriteManager);
        if ( mNumSwitchesDone == 2 ) {
          makeCritters(spriteManager);
        }
        vanishBlocks(false);
        mSwitchTimer = kSwitchDelay[mNumSwitchesDone-1];
        mTickDelay = kTickDelayMax;
        mTickTimer = (3*kTickDelayMax)/2;
        Env.sounds().play(Sounds.SWITCH_ON);
        it.remove();
      }
    }

    // count down the timer
    if ( mSwitchTimer > 0 ) {
      assert( !mPathDone );
      if ( mSwitchTimer > mTickTimer ) mSwitchTimer--;
      if ( mSwitchTimer == 0 ) {
        mNumSwitchesDone = 0;
        updateSwitch(spriteManager);
        updateBlocks(spriteManager);
        vanishBlocks(false);
        mPath.setIndex(Math.min(3, mPath.index()));
        mTickTimer = mTickDelay = 0;
        Env.sounds().play(Sounds.SWITCH_OFF);
      } else {
        if ( mTickTimer == 0 ) {
          mTickDelay = ( mSwitchTimer <= kTickChangeTime 
                         ? kTickDelayMin : kTickDelayMax );
          mTickTimer = mTickDelay;
        }
        assert( mTickDelay > 0 );
        mTickTimer--;
        if ( mTickTimer == mTickDelay/2 ) {
          if ( mTickDelay == kTickDelayMin ) vanishBlocks(true);
          Env.sounds().play(Sounds.TICK);
        } else if ( mTickTimer == 0 ) {
          if ( mTickDelay == kTickDelayMin ) vanishBlocks(false);
          Env.sounds().play(Sounds.TOCK);
        }
      }
    }

    // check the path
    if ( !mPathDone && mPath.complete() ) {
      mPathDone = true;
      mSwitchTimer = 0;
      mTickTimer = mTickDelay = 0;
      vanishBlocks(false);
      kExits[1].mDoor.setClosed(false);
      Env.sounds().play(Sounds.SUCCESS);
      storyEvents.add(new QuestStory.EventSaveGame());
    }

    // dispose of any falling sprites
    if ( mPlayer != null && mPlayer.getZPos() <= kDropDeadHeight ) {
      mPlayer.destroy(-1);
    }
    LinkedList<Sprite> killList = new LinkedList<Sprite>();
    for ( Sprite sp : spriteManager.list() ) {
      if ( sp instanceof Critter ) {
        Critter critter = (Critter)sp;
        if ( critter.getZPos() <= kDropDeadHeight ) {
          killList.add(critter);
        }
      }
    }
    spriteManager.removeSprites(killList);

  } // Room.advance()

} // class RoomC11
