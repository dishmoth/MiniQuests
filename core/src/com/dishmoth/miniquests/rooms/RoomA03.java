/*
 *  RoomA03.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BitBuffer;
import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Bullet;
import com.dishmoth.miniquests.game.EgaImage;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.FlameBeam;
import com.dishmoth.miniquests.game.FlameBeamSpin;
import com.dishmoth.miniquests.game.FloorSwitch;
import com.dishmoth.miniquests.game.Mural;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Shrapnel;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.Sprite;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.QuestStory;
import com.dishmoth.miniquests.game.WallSwitch;

// the room "A03"
public class RoomA03 extends Room {

  // unique identifier for this room
  public static final String NAME = "A03";
  
  // main blocks for the floor
  private static final String kFloorBlocks[] = { "00000000000000000000",
                                                 "00000000000000000000",
                                                 "00000000000000000000",
                                                 "00000000000000000000",
                                                 "00000000000000000000",
                                                 "000000000  000000000",
                                                 "000000000  000000000",
                                                 "000000000  000000000",
                                                 "00000000000000000000",
                                                 "   00   0000   00   ",
                                                 "   00   0000   00   ",
                                                 "   00000000000000000",
                                                 "   000000  000000000",
                                                 "   000000  000000000",
                                                 "000000000  000000000",
                                                 "000000000000000000  ",
                                                 "000000000000000000  ",
                                                 "000000     0000000  ",
                                                 "000000     0000000  ",
                                                 "000000     0000000  " };
  private static final String kBlocks[][] = { kFloorBlocks, kFloorBlocks,
                                              kFloorBlocks, kFloorBlocks };
  
  // blocks for the (0,1) zone
  private static final String kBlocks01a[][] = 
                                { { "1", "1", "1", "1" } },
                              kBlocks01b[][] = 
                                { { "1" } };
  
  // blocks for the (1,0) zone
  private static final String kBlocks10a[][] =
                                { { "111", "1  ", "1  ", "1  " },
                                  { "111", "1  ", "1  ", "1  " },
                                  { "111", "1  ", "1  ", "1  " } },
                              kBlocks10b[][] = { { "1" }, { "1" }, { "1" } };
    
  // blocks for the (1,1) zone
  private static final String kBlocks11a[][] = 
                                { { "1", "1", "1", "1" } },
                              kBlocks11b[][] = 
                                { { "1" } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#k",   // orange
                                                  "BV" }; // blue 
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(0,0, Env.DOWN,  1,0, "#k",0, -1, RoomA02.NAME, 1), 
              new Exit(0,0, Env.LEFT,  3,0, "#k",0, -1, RoomA04.NAME, 0), 
              new Exit(1,0, Env.RIGHT, 5,6, "#V",2, -1, RoomA08.NAME, 1), 
              new Exit(0,1, Env.LEFT,  8,8, "BV",1, -1, RoomA05.NAME, 1), 
              new Exit(1,1, Env.RIGHT, 3,8, "#V",1, -1, RoomA06.NAME, 0),
              new Exit(1,1, Env.UP,    1,0, "#k",0, -1, RoomA09.NAME, 0) };

  // angular speed of flames (radians per frame)
  private static final float kFlameAngSpeed = 0.010f;
  
  // times for various cut scenes
  private static final int kTimeScene1   = 20,
                           kTimeOpenDoor = 15,
                           kTimeScene2   = 35;
  
  // time delay for the final switch (if scrolling)
  private static final int kTimeFinalSwitch = 5;
  
  // time delay for a moving stair blocks
  private static final int kTimeStairDrop = 3;

  // picture on the 'up' wall of the room
  private static EgaImage kMuralImage;
  
  // whether the floor switches are completed
  private boolean mSwitchesDone;
  
  // set of floor switches
  private FloorSwitch mSwitches[];

  // switches on walls
  private WallSwitch mButton10,
                     mButton11;
  
  // first set of stairs has been dropped
  private boolean mFirstStairsDone;
  
  // second set of stairs has been dropped
  private boolean mSecondStairsDone;
  
  // third set of stairs has been dropped (and flames turned off)
  private boolean mThirdStairsDone;
  
  // blocks that move to form stairs
  private BlockArray mStairBlocks10[],
                     mStairBlocks01[],
                     mStairBlocks11[];

  // whether the final switch has been activated
  private boolean mFinalSwitchDone;
  
  // the switch at the centre of the flames 
  private FloorSwitch mFinalSwitch;
  
  // allow a slight delay to the final switch to allow for scrolling
  private int mFinalSwitchTimer;
  
  // control the animation of stair blocks
  private int mStairTimer;

  // time during which all sprites are frozen
  private int mCutSceneTimer;
  
  // constructor
  public RoomA03() {
    
    super(NAME);

    if ( kMuralImage == null ) {
      kMuralImage = Env.resources().loadEgaImage("MuralX03.png");
    }
    
    mSwitchesDone = false;
    mFirstStairsDone = false;
    mSecondStairsDone = false;
    mThirdStairsDone = false;
    mFinalSwitchDone = false;
    
  } // constructor

  // serialize the room state by writing bits to the specified buffer
  @Override
  public void save(BitBuffer buffer) {
    
    buffer.writeBit(mSwitchesDone);
    buffer.writeBit(mFirstStairsDone);
    buffer.writeBit(mSecondStairsDone);
    buffer.writeBit(mThirdStairsDone);
    buffer.writeBit(mFinalSwitchDone);
    
  } // Room.save()

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  @Override
  public boolean restore(int version, BitBuffer buffer) { 
    
    if ( buffer.numBitsToRead() < 5 ) return false;
    mSwitchesDone     = buffer.readBit();
    mFirstStairsDone  = buffer.readBit();
    mSecondStairsDone = buffer.readBit();
    mThirdStairsDone  = buffer.readBit();
    mFinalSwitchDone  = buffer.readBit();
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

    mSwitches = null;
    mButton10 = mButton11 = null;
    mStairBlocks10 = mStairBlocks01 = mStairBlocks11 = null;
    mFinalSwitch = null;
    
  } // Room.discardResources()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {

    int zoneX, zoneY;
    
    // zone (0,0)
    
    zoneX = 0;
    zoneY = 0;
    spriteManager.addSprite( 
                 new BlockArray(kBlocks, kBlockColours, 
                                zoneX*Room.kSize, zoneY*Room.kSize, 
                                -2*(kBlocks.length-1)) );
    addBasicZone(zoneX, zoneY, 
                 false, false, true, true, 
                 kExits, spriteManager);
    
    // zone (1,0)
    
    zoneX = 1;
    zoneY = 0;
    spriteManager.addSprite( 
                 new BlockArray(kBlocks10a, kBlockColours, 
                                zoneX*Room.kSize+7, zoneY*Room.kSize+2, 2 ));
    addBasicZone(zoneX, zoneY, 
                 true, false, false, true, 
                 kExits, spriteManager);

    mStairBlocks10 = new BlockArray[2];
    for ( int k = 0 ; k < 2 ; k++ ) {
      final int x = zoneX*Room.kSize + 7,
                y = zoneY*Room.kSize + k,
                z = ( mThirdStairsDone ? (-2 + 2*k) : 2 );
      mStairBlocks10[k] = new BlockArray(kBlocks10b, kBlockColours, x, y, z);
      spriteManager.addSprite( mStairBlocks10[k] );
    }
    
    mButton10 = new WallSwitch(zoneX, zoneY, Env.RIGHT, 3, 8, 
                               new String[]{"au","7u"}, false);
    if ( mThirdStairsDone ) mButton10.setState(1);
    spriteManager.addSprite( mButton10 );
    
    // zone (0,1)
    
    zoneX = 0;
    zoneY = 1;
    spriteManager.addSprite( 
                 new BlockArray(kBlocks01a, kBlockColours, 
                                zoneX*Room.kSize, zoneY*Room.kSize+5, 8));
    addBasicZone(zoneX, zoneY, 
                 false, true, true, false, 
                 kExits, spriteManager);

    mStairBlocks01 = new BlockArray[3];
    for ( int k = 0 ; k < 3 ; k++ ) {
      final int x = zoneX*Room.kSize,
                y = zoneY*Room.kSize + 2 + k,
                z = ( mFirstStairsDone ? (2 + 2*k) : 8 );
      mStairBlocks01[k] = new BlockArray(kBlocks01b, kBlockColours, x, y, z);
      spriteManager.addSprite( mStairBlocks01[k] );
    }
    
    spriteManager.addSprite(new Mural(0, 1, Env.UP, 6,0, kMuralImage));
    
    // zone (1,1)
    
    zoneX = 1;
    zoneY = 1;
    spriteManager.addSprite( 
                 new BlockArray(kBlocks11a, kBlockColours, 
                                zoneX*Room.kSize+9, zoneY*Room.kSize+2, 8));
    addBasicZone(zoneX, zoneY, 
                 true, true, false, false, 
                 kExits, spriteManager);

    mStairBlocks11 = new BlockArray[3];
    for ( int k = 0 ; k < 3 ; k++ ) {
      final int x = zoneX*Room.kSize + 9,
                y = zoneY*Room.kSize + 8 - k,
                z = ( mSecondStairsDone ? (2 + 2*k) : 8 );
      mStairBlocks11[k] = new BlockArray(kBlocks11b, kBlockColours, x, y, z);
      spriteManager.addSprite( mStairBlocks11[k] );
    }

    mButton11 = new WallSwitch(zoneX, zoneY, Env.RIGHT, 6, 10, 
                               new String[]{"au","7u"}, false);
    if ( mSecondStairsDone ) mButton11.setState(1);
    spriteManager.addSprite( mButton11 );
    
    // spinning flame beams

    if ( !mThirdStairsDone ) {
      final float centre    = 0.5f*(2*Room.kSize-1),
                  height    = 1,
                  radiusMax = Room.kSize - 0.5f,
                  radiusMin = 0,
                  deltaAng  = (float)(2*Math.PI/3);
      FlameBeamSpin f1 = new FlameBeamSpin(centre, centre, height,
                                           radiusMin, radiusMax, 
                                           kFlameAngSpeed, 0.0f, 2);
      FlameBeamSpin f2 = new FlameBeamSpin(centre, centre, height,
                                           radiusMin, radiusMax, 
                                           kFlameAngSpeed, deltaAng, 2);
      FlameBeamSpin f3 = new FlameBeamSpin(centre, centre, height,
                                           radiusMin, radiusMax,
                                           kFlameAngSpeed, 2*deltaAng, 2);
      spriteManager.addSprite(f1);
      spriteManager.addSprite(f2);
      spriteManager.addSprite(f3);
      f1.setSilent(false);
    }
    
    // floor switches
    
    if ( !mSwitchesDone ) {
      mSwitches = new FloorSwitch[]
                       { new FloorSwitch( 4, 4, 0, "#a", "#2"),
                         new FloorSwitch(15, 4, 0, "#a", "#2"),
                         new FloorSwitch( 4,15, 0, "#a", "#2"),
                         new FloorSwitch(15,15, 0, "#a", "#2") };
      for ( FloorSwitch s : mSwitches ) spriteManager.addSprite(s);

      kExits[1].mDoor.setClosed(true);
    }

    // final switch
    
    mFinalSwitch = new FloorSwitch(10, 10, 0, "#a", "#2");
    spriteManager.addSprite( mFinalSwitch );
    if ( mFinalSwitchDone ) {
      mFinalSwitch.freezeState(true);
    } else {
      kExits[5].mDoor.setClosed(true);
    }
        
    mStairTimer = 0;
    mFinalSwitchTimer = 0;
    mCutSceneTimer = 0;
    
  } // Room.createSprites()
  
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
      Env.sounds().stop(Sounds.FLAME);
      return;
    }
    
    // check for scrolling
    
    EventRoomScroll scroll = checkHorizontalScroll();
    if ( scroll != null ) {
      //Env.sounds().stopFlameSound();
      storyEvents.add(scroll);
    }
    
    // process the story event list
    
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();
      
      if ( event instanceof FloorSwitch.EventStateChange ) {
        FloorSwitch s = (FloorSwitch)
                        ((FloorSwitch.EventStateChange)event).mSwitch;
        s.freezeState(true);
        Env.sounds().play(Sounds.SWITCH_ON);
        it.remove();
      }
      
      if ( event instanceof WallSwitch.EventStateChange ) {
        it.remove();
      }
      
      if ( event instanceof Player.EventKilled ) {
        if ( !mSwitchesDone ) {
          for ( FloorSwitch s : mSwitches ) s.unfreezeState();
        }
      }
      
    } // for (event)

    // check floor switches
    
    if ( !mSwitchesDone ) {
      assert( mSwitches != null );
      boolean done = true;
      for ( FloorSwitch s : mSwitches ) {
        if ( !s.isOn() ) done = false;
      }
      if ( done ) {
        mSwitchesDone = true;
        for ( FloorSwitch s : mSwitches ) spriteManager.removeSprite(s);
        storyEvents.add(new QuestStory.EventSaveGame());
        mSwitches = null;
        scroll = scrollToZone(0, 0);
        if ( scroll == null ) {
          kExits[1].mDoor.setClosed(false);
          Env.sounds().play(Sounds.SUCCESS, 0);
        } else {
          storyEvents.add(scroll);
          mCutSceneTimer = kTimeScene1;
          Env.sounds().stop(Sounds.FLAME);
        }
      }
    }
    
    // check first stairs
    
    if ( !mFirstStairsDone ) {
      if ( mPlayer != null &&
           mPlayer.getXPos() == 0 && 
           mPlayer.getYPos() == 17 && 
           mPlayer.getZPos() == 8) {
        mFirstStairsDone = true;
        mStairTimer = kTimeStairDrop;
        Env.sounds().play(Sounds.SWITCH_ON);
      }
    }
    
    // check second stairs
    
    if ( !mSecondStairsDone ) {
      // cheat: move the bullet to make it easier to hit the switch
      for ( Sprite sp : spriteManager.list() ) {
        if ( sp instanceof Bullet ) {
          Bullet b = (Bullet)sp;
          if ( b.getXPos() == mButton11.getXPos()-2 && 
               b.getYPos() >= 15 && b.getYPos() <= 17 &&
               b.getZPos() == mButton11.getZPos() && 
               b.getDirec() == Env.RIGHT ) {
            final int dy = mButton11.getYPos() - b.getYPos();
            if ( dy != 0 ) b.shiftPos(0, dy, 0);
          }
        }
      }
      
      // check the switch
      if ( mButton11.getState() > 0 ) {
        mSecondStairsDone = true;
        mStairTimer = kTimeStairDrop;
        scroll = scrollToZone(1, 1);
        assert( scroll != null );
        storyEvents.add(scroll);
        storyEvents.add(new QuestStory.EventSaveGame());
        mCutSceneTimer = kTimeScene2;
        Env.sounds().stop(Sounds.FLAME);
      }
    }
    
    // check third stairs
    
    if ( !mThirdStairsDone ) {
      if ( mButton10.getState() > 0) {
        mThirdStairsDone = true;
        mStairTimer = kTimeStairDrop;
        for ( Sprite s : spriteManager.list() ) {
          if ( s instanceof FlameBeam ) ((FlameBeam)s).setFlame(false);
        }
        storyEvents.add(new QuestStory.EventSaveGame());
        Env.sounds().stop(Sounds.FLAME);
        Env.sounds().play(Sounds.SUCCESS, 3);
      }
    }

    // check final switch
    
    if ( !mFinalSwitchDone ) {
      if ( mFinalSwitch.isOn() && mPlayer != null ) {
        mFinalSwitch.freezeState(true);
        mFinalSwitchDone = true;
        mFinalSwitchTimer = ( mPlayer.getDirec() == Env.RIGHT || 
                              mPlayer.getDirec() == Env.UP ) 
                            ? kTimeFinalSwitch : 1;
      }
    }
    if ( mFinalSwitchDone && mFinalSwitchTimer > 0 ) {
      if ( --mFinalSwitchTimer == 0 ) {
        kExits[5].mDoor.setClosed(false);
        int delay = ( mPlayer.getDirec() == Env.RIGHT || 
                      mPlayer.getDirec() == Env.UP ) ? 0 : 5;
        Env.sounds().play(Sounds.SUCCESS, delay);
        storyEvents.add(new QuestStory.EventSaveGame());
      }
    }
    
    // animate stair blocks
    
    if ( mStairTimer > 0 ) {
      if ( --mStairTimer == 0 ) {
        BlockArray stairs[] = ( mThirdStairsDone  ? mStairBlocks10
                              : mSecondStairsDone ? mStairBlocks11 
                                                  : mStairBlocks01 );
        final int zBase = ( mThirdStairsDone ? -2 : 2 );
        int z = stairs[0].getZPos();
        if ( z > zBase ) {
          z -= 1;
          for ( int k = 0 ; k < stairs.length ; k++ ) {
            int dz = (z + 2*k) - stairs[k].getZPos();
            if ( dz < 0 ) stairs[k].shiftPos(0, 0, dz);
          }
          mStairTimer = kTimeStairDrop;
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

    // second cut scene (stairs descend)
    if ( mSecondStairsDone ) {
      for ( Sprite s : spriteManager.list() ) {
        if ( s instanceof Shrapnel ) s.mAdvanceDisabled = false;
      }
      if ( mCutSceneTimer == kTimeScene2 - 1 ) { 
        Env.sounds().play(Sounds.SUCCESS, 0);
      }
    }
    
    // first cut scene (door opens)
    else if ( mSwitchesDone ) {
      if ( mCutSceneTimer == kTimeOpenDoor ) {
        kExits[1].mDoor.setClosed(false);
        Env.sounds().play(Sounds.SUCCESS, 0);
      }
    }
    
    // tidy up at the end
    if ( mCutSceneTimer == 0 ) {
      spriteManager.enableAdvanceForAll();
      EventRoomScroll scroll = scrollToPlayer();
      if ( mSecondStairsDone && scroll != null ) {
        // hack: for the 'second stair' cut-scene, limit the scroll 
        // just in case the player has walked into the doorway  
        if ( scroll.mShiftX < -kSize ) scroll.mShiftX = -kSize;
      }
      if ( scroll != null ) storyEvents.add(scroll);
    }

    return advanceFinished;
    
  } // advanceCutScene()
  
} // class RoomA03
