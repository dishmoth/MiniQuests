/*
 *  RoomC04.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BitBuffer;
import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Fence;
import com.dishmoth.miniquests.game.GlowPath;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.QuestStory;
import com.dishmoth.miniquests.game.WallSwitch;

// the room "C04"
public class RoomC04 extends Room {

  // unique identifier for this room
  public static final String NAME = "C04";
  
  // main blocks for the room
  private static final String kBlocks[][] = { { "000000000           ",
                                                "001111200           ",
                                                "001000000           ",
                                                "001000000           ",
                                                "111004444444        ",
                                                "001000000           ",
                                                "001000000           ",
                                                "001111300           ",
                                                "000000000           ",
                                                "000000000           " } };

  // blocks for the lift
  private static final String kLiftSlice[] = { "4444444",
                                               "4  4  4", 
                                               "4  4  4",
                                               "4444444",
                                               "4  4  4",
                                               "4  4  4",
                                               "4444444" };
  private static final String kLiftBlocks[][] = { kLiftSlice, kLiftSlice,
                                                  kLiftSlice };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "lL",   // mauve
                                                  "#f",   // purple with white
                                                  "Jf",   // purple with blue
                                                  "Jf",   // purple with blue
                                                  "eL" }; // dark purple

  // details of exit/entry points for the room 
  // (plus special cases: 3 => respawn at top of lift, 4 => ride up on lift)
  private static final Exit kExits[] 
          = { new Exit(0,0, Env.DOWN, 2,0, "lL",0, -1, RoomC03.NAME, 1),
              new Exit(0,0, Env.LEFT, 5,0, "#f",0, -1, RoomC13.NAME, 1),
              new Exit(0,0, Env.UP,   2,0, "#L",0, -1, RoomC05.NAME, 0) };

  // glowing pathways
  private static final String kGlowPath1[] = { "   +++++",
                                               "   +    ",
                                               "   +    ",
                                               "X+++    " },
                              kGlowPath2[] = { "X+++    ",
                                               "   +    ",
                                               "   +    ",
                                               "   +++++" };

  // timings for pan-away to wall switch
  private static final int kTimeCutScene = 40,
                           kTimeScroll   = 30,
                           kTimeSwitchOn = 20;
  
  // parameters controlling the lift
  private static final int kLiftZMin  = -24,
                           kLiftZMax  = -4;
  private static final int kLiftDelay = 4;
  
  // temporary version of the floor blocks to allow colour changes
  private BlockArray mBlocks;
  private String mBlockColours[];
  
  // references to two bits of glowing path
  private GlowPath mPath1,
                   mPath2;

  // status of the paths
  private boolean mPathDone1,
                  mPathDone2;

  // lift switch
  private WallSwitch mSwitch;
  
  // time during which all sprites are frozen
  private int mCutSceneTimer;
  
  // reference to the lift blocks
  private BlockArray mLift;
  
  // timer for lift movement
  private int mLiftTimer;
  
  // where the lift should be
  private boolean mLiftAtTop;
  
  // last position of player when lift changes room (relative to lift)
  // (note: these variables are shared with RoomC11)
  private int mLiftChangeXPos,
              mLiftChangeYPos,
              mLiftChangeDirec;
  
  // constructor
  public RoomC04() {
    
    super(NAME);

    mPathDone1 = mPathDone2 = false;

    mLiftAtTop = false;
    mLiftChangeXPos = 3;
    mLiftChangeYPos = 3;
    mLiftChangeDirec = Env.RIGHT;
    
  } // constructor

  // serialize the room state by writing bits to the specified buffer
  @Override
  public void save(BitBuffer buffer) {
    
    buffer.writeBit(mPathDone1);
    buffer.writeBit(mPathDone2);
    buffer.writeBit(mLiftAtTop);
    
  } // Room.save()

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  @Override
  public boolean restore(int version, BitBuffer buffer) { 
    
    if ( buffer.numBitsToRead() < 3 ) return false;
    mPathDone1 = buffer.readBit();
    mPathDone2 = buffer.readBit();
    mLiftAtTop = buffer.readBit();
    return true; 
    
  } // Room.restore() 
  
  // record of where the player was on the lift when the room changed 
  // (note: these function may be called by RoomC11)
  public int liftChangeXPos()  { return mLiftChangeXPos; }
  public int liftChangeYPos()  { return mLiftChangeYPos; }
  public int liftChangeDirec() { return mLiftChangeDirec; }
  public void recordLiftChangeInfo(int x, int y, int direc) {
    mLiftChangeXPos=x; mLiftChangeYPos=y; mLiftChangeDirec=direc;
  }
  
  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < kExits.length+2 );
    if ( entryPoint < kExits.length ) {
      setPlayerAtExit(kExits[entryPoint]);
      mLift.setPos(mLift.getXPos(), mLift.getYPos(), 
                   (mLiftAtTop ? kLiftZMax : kLiftZMin));
    } else {
      RoomC14 roomBelow = (RoomC14)findRoom(RoomC14.NAME);
      assert( roomBelow != null );
      if ( entryPoint == kExits.length ) {
        // special case: 3 => respawn at top of lift
        mPlayer = new Player(11, 5, 0, Env.RIGHT);
        mLiftAtTop = false;
        Env.sounds().play(Sounds.HERO_GRUNT, 5);
      } else {
        // special case: 4 => ride up on lift
        int x = liftChangeXPos() + mLift.getXPos(),
            y = liftChangeYPos() + mLift.getYPos(),
            z = kLiftZMin + 4;
        int direc = liftChangeDirec();
        mPlayer = new Player(x, y, z, direc);
        mLiftAtTop = true;
      }
      mCamera.set(kSize, 0, 0);
    }
    return mPlayer;
    
  } // createPlayer()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mBlocks = null;
    mBlockColours = null;

    mPath1 = mPath2 = null;
    
    mLift = null;
    
  } // Room.discardResources()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {

    int zoneX, zoneY;
    
    // zone (0,0)
    
    zoneX = 0;
    zoneY = 0;
    addBasicZone(zoneX, zoneY, 
                 false, true, true, true, 
                 kExits, spriteManager);

    // zone (1,0)
    
    zoneX = 1;
    zoneY = 0;
    addBasicZone(zoneX, zoneY, 
                 true, true, false, true, 
                 kExits, spriteManager);

    makeNewBlocks(spriteManager);
    
    // assorted stuff
    
    spriteManager.addSprite(new Fence(9,0,0, 5, Env.UP, 1));
    spriteManager.addSprite(new Fence(9,6,0, 4, Env.UP, 1));

    RoomC13 roomLeft = (RoomC13)findRoom(RoomC13.NAME);
    assert( roomLeft != null );
    boolean pathsAvailable = roomLeft.pathComplete();
    
    if ( pathsAvailable ) {
      mPath1 = new GlowPath(kGlowPath1, -1,5,0,'J');
      if ( mPathDone1 ) mPath1.setComplete();
      spriteManager.addSprite(mPath1);
      
      mPath2 = new GlowPath(kGlowPath2, -1,2,0,'J');
      if ( mPathDone2 ) mPath2.setComplete();
      spriteManager.addSprite(mPath2);
    }
    
    mSwitch = new WallSwitch(1,0, Env.RIGHT, 5, 2, 
                             new String[]{"Ru","su","7u"}, false);
    if ( !(mPathDone1 && mPathDone2) ) {
      mSwitch.setState(2);
    } else if ( mLiftAtTop ) {
      mSwitch.setState(1);
    }
    spriteManager.addSprite(mSwitch);
    
    mLift = new BlockArray(kLiftBlocks, kBlockColours, 12, 2, kLiftZMin);
    spriteManager.addSprite(mLift);
    
    mCutSceneTimer = 0;
    mLiftTimer = 0;
    
  } // Room.createSprites()

  // update the floor pattern
  private void makeNewBlocks(SpriteManager spriteManager) {
    
    if ( mBlocks != null ) spriteManager.removeSprite(mBlocks);

    mBlockColours = Env.copyOf(kBlockColours);
    if ( mPathDone1 ) mBlockColours[2] = "J#";
    if ( mPathDone2 ) mBlockColours[3] = "J#";
    
    mBlocks = new BlockArray(kBlocks, mBlockColours, 0, 0, 0);
    spriteManager.addSprite(mBlocks);
    
  } // makeNewBlocks()
  
  // returns true if the room is frozen (e.g., during a cut-scene)
  @Override
  public boolean paused() { return (mCutSceneTimer > 0); }
  
  // update the room (events may be added or processed)
  @Override
  public void advance(LinkedList<StoryEvent> storyEvents,
                      SpriteManager          spriteManager) {

    // advance the cut-scene    
    if ( mCutSceneTimer > 0 ) {
      final boolean done = advanceCutScene(storyEvents, spriteManager);
      if ( done ) return;
    }
    
    // check exits
    final int exitIndex = checkExits(kExits);
    if ( exitIndex != -1 ) {
      storyEvents.add(new EventRoomChange(kExits[exitIndex].mDestination,
                                          kExits[exitIndex].mEntryPoint));
      return;
    }
    if ( !mLiftAtTop && mPlayer != null && mPlayer.getZPos() == kLiftZMin+4 ) {
      storyEvents.add(new EventRoomChange(RoomC14.NAME, 1));
      recordLiftChangeInfo(mPlayer.getXPos()-mLift.getXPos(), 
                           mPlayer.getYPos()-mLift.getYPos(),
                           mPlayer.getDirec());
      return;
    }
    
    // check for scrolling
    EventRoomScroll scroll = checkHorizontalScroll();
    if ( scroll != null ) {
      storyEvents.add(scroll);
    }

    // process the story event list
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();
      
      // switch has been hit - turn off temporarily
      if ( event instanceof WallSwitch.EventStateChange ) {
        assert( mSwitch.getState() == 1 || mSwitch.getState() == 2 );
        mLiftAtTop = (mSwitch.getState() == 1);
        mSwitch.setState(2);
        it.remove();
      }
    }
    
    // check the paths
    if ( mPath1 != null && mPath2 != null && !(mPathDone1 && mPathDone2) ) {
      if ( mPath1.index() > 3 && mPath2.index() < 3 ) mPath2.setIndex(3);
      if ( mPath1.index() < 3 && mPath2.index() > 3 ) mPath1.setIndex(3);

      if ( !mPathDone1 && mPath1.complete() ) {
        mPathDone1 = true;
        makeNewBlocks( spriteManager );
        Env.sounds().play( Sounds.SWITCH_ON );
      }
      
      if ( !mPathDone2 && mPath2.complete() ) {
        mPathDone2 = true;
        makeNewBlocks( spriteManager );
        Env.sounds().play( Sounds.SWITCH_ON );
      }
      
      if ( mPathDone1 && mPathDone2 ) {
        mCutSceneTimer = kTimeCutScene;
      }
    }
    
    // animate the lift
    if ( ( mLiftAtTop && mLift.getZPos() < kLiftZMax) ||
         (!mLiftAtTop && mLift.getZPos() > kLiftZMin) ) {
      if ( --mLiftTimer <= 0 ) {
        mLiftTimer = kLiftDelay;
        mLift.shiftPos(0, 0, (mLiftAtTop?+1:-1));
        if ( mLiftAtTop && mLift.getZPos() == kLiftZMax ) {
          Env.sounds().play(Sounds.SWITCH_DEEP);
          mSwitch.setState(1);
        } else if ( !mLiftAtTop && mLift.getZPos() == kLiftZMax-10 ) {
          mSwitch.setState(0);
        }
      }
    }
    
  } // Room.advance()

  // stop the game and show something happening
  private boolean advanceCutScene(LinkedList<StoryEvent> storyEvents,
                                  SpriteManager          spriteManager) {

    assert( mCutSceneTimer > 0 );
    mCutSceneTimer -= 1;

    boolean advanceFinished = false;
    
    spriteManager.disableAdvanceForAll();

    // scroll to switch
    if ( mCutSceneTimer == kTimeScroll ) {
      EventRoomScroll scroll = scrollToZone(1, 0);
      assert( scroll != null );
      storyEvents.add(scroll);
    }
    
    // switch on
    if ( mCutSceneTimer == kTimeSwitchOn ) {
      mSwitch.setState(0);
      Env.sounds().play(Sounds.GATE);
    }
    
    // scroll to player
    if ( mCutSceneTimer == 1 ) {
      EventRoomScroll scroll = scrollToPlayer();
      assert( scroll != null );
      storyEvents.add(scroll);
    }

    // tidy up at the end
    if ( mCutSceneTimer == 0 ) {
      spriteManager.enableAdvanceForAll();
      Env.sounds().play(Sounds.SUCCESS);
      storyEvents.add(new QuestStory.EventSaveGame());
    }

    return advanceFinished;
    
  } // advanceCutScene()
  
} // class RoomC04
