/*
 *  RoomC08.java
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
import com.dishmoth.miniquests.game.Flame;
import com.dishmoth.miniquests.game.FloorSwitch;
import com.dishmoth.miniquests.game.GlowPath;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.Splatter;
import com.dishmoth.miniquests.game.Sprite;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.QuestStory;

// the room "C08"
public class RoomC08 extends Room {

  // unique identifier for this room
  public static final String NAME = "C08";
  
  // the basic blocks for the room
  private static final String kBlocks[][] = { { " 11111    ",
                                                " 1   1    ",
                                                "01 0 1000 ",
                                                " 1 1 1    ",
                                                " 111 1    ",
                                                "     11   ",
                                                "          ",
                                                "        3 ",
                                                "333333333 ",
                                                "          " } };
  
  // extra blocks building up the flame tower
  private static final String kTowerLayer[]    = { "1  ", "1  ", "111" };
  private static final String kTowerBlocks[][] = { kTowerLayer, kTowerLayer,
                                                   kTowerLayer, kTowerLayer,
                                                   kTowerLayer };

  // animated blocks under the flame
  private static final String kFlameBlocks[][][] = { { { "111", "141", "131" } },
                                                     { { "212", "141", "232" } },
                                                     { { "121", "242", "131" } } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "Sk",   // dark
                                                  "#k",   // light
                                                  "yk",   // flicker
                                                  "#S",   // flame path
                                                  "yS" }; // flame centre
  
  // details of exit/entry points for the room
  private static final Exit kExits[] 
          = { new Exit(Env.LEFT, 1,4, "#S",1, -1, RoomC07.NAME, 1),
              new Exit(Env.LEFT, 7,4, "Sk",1, -1, RoomC09.NAME, 2) }; 
  
  // path followed by enemies in this room
  private static final CritterTrack kCritterTrack 
                    =  new CritterTrack(new String[]{ " +++++    ",
                                                      " +   +    ",
                                                      " + + +    ",
                                                      " + + +    ",
                                                      " +++ +    ",
                                                      "     ++++ ",
                                                      "          ",
                                                      "          ",
                                                      "          ",
                                                      "          " });
  
  // enemy colour details [scheme index, splatter colour]
  private static final int kEnemyColours[][] = { {1,13},   // pink
                                                 {3,25},   // blue 
                                                 {4, 4} }; // orange

  // delay until next enemy appears
  private static final int kEnemySpawnTime  = 90,
                           kEnemySwitchTime = 15,
                           kEnemyStartTime  = 40;

  // different flame colour schemes
  private static final byte kFlameColours[][] = { {40,13,61},   // pink
                                                  { 1,25,43},   // blue
                                                  { 4,36,52} }; // orange
  
  // a slight wait before the flame turns back on
  private static final int kFlameRestartDelay = 25,
                           kFlameEndDelay     = 25;

  // animation under the flame
  private static final int kFlameBlocksDelay       = 40,
                           kFlameBlocksChangeDelay = 4;
  
  // colour of the glowing path
  private static final char kPathColour = 'y';
  
  // glowing path
  private static final String kGlowPath[] = { "           ",
                                              "           ",
                                              "           ",
                                              "           ",
                                              "           ",
                                              "         X ",
                                              "         + ",
                                              "         + ",
                                              "++++++++++ ",
                                              "           " };

  // whether the initial switch has been hit
  private boolean mSwitchDone;
  
  // whether the flame has been extinguished yet
  private boolean mFlameDone;
  
  // whether the path has been walked yet
  private boolean mPathDone;
  
  // time until next enemy appears
  private int mEnemyTimer;
  
  // colour of the next enemy (index into array above)
  private int mEnemyColour;

  // reference to the flame
  private Flame mFlame;
  
  // current flame colour scheme (index into array above)
  private int mFlameColour;
  
  // how long before the flame restarts
  private int mFlameRestartTimer;

  // animated platform under the flame
  private BlockArray mFlameBlocks[];

  // animation of the blocks under the flame
  private int mFlameBlocksTimer;
  
  // reference to the glowing path
  private GlowPath mPath;

  // number of uninterrupted flame changes
  private int mProgress;
  
  // constructor
  public RoomC08() {

    super(NAME);

    mSwitchDone = false;
    mFlameDone = false;
    mPathDone = false;
    
  } // constructor

  // serialize the room state by writing bits to the specified buffer
  @Override
  public void save(BitBuffer buffer) {
    
    buffer.writeBit(mSwitchDone);
    buffer.writeBit(mFlameDone);
    buffer.writeBit(mPathDone);
    
  } // Room.save()

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  @Override
  public boolean restore(int version, BitBuffer buffer) { 
    
    if ( buffer.numBitsToRead() < 3 ) return false;
    mSwitchDone = buffer.readBit();
    mFlameDone = buffer.readBit();
    mPathDone = buffer.readBit();
    return true; 
    
  } // Room.restore() 
  
  // whether the path is complete
  // (note: this function may be called by RoomC07 and RoomC09)
  public boolean pathComplete() { return mPathDone; }
  
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

    mFlame = null;
    mFlameBlocks = null;
    mPath = null;
    
  } // Room.discardResources()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {

    addBasicWalls(kExits, spriteManager);

    spriteManager.addSprite(new BlockArray(kBlocks, kBlockColours, 0,0,4));

    spriteManager.addSprite(new BlockArray(kTowerBlocks, kBlockColours, 
                                           7,3,-6));

    mFlameBlocks = new BlockArray[3];
    for ( int k = 0 ; k < kFlameBlocks.length ; k++ ) {
      mFlameBlocks[k] = new BlockArray(kFlameBlocks[k], kBlockColours, 7,3,4); 
    }
    spriteManager.addSprite(mFlameBlocks[0]);
    mFlameBlocksTimer = kFlameBlocksDelay;

    if ( !mSwitchDone ) {
      FloorSwitch fs = new FloorSwitch(8,7,4, "#z", "Sk");
      spriteManager.addSprite(fs);
    }
    
    if ( !mFlameDone ) {
      mFlameColour = 1;
      mFlameRestartTimer = 0;
      mFlame = new Flame(8, 4, 4);
      mFlame.setColours( kFlameColours[mFlameColour] );
      mFlame.setPhysicsMode(1);
      mFlame.warmUp(20);
      spriteManager.addSprite(mFlame);
    }
    
    if ( mFlameDone && mPathDone ) {
      mPath = new GlowPath(kGlowPath, -1,0,4, kPathColour);
      mPath.setComplete();
      spriteManager.addSprite(mPath);
      kExits[1].mDoor.setClosed(true);
    } else {
      mPath = null;
    }
   
    mEnemyColour = 2;
    mEnemyTimer = kEnemyStartTime;
    
    mFlameRestartTimer = 0;
    mProgress = 0;
    
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

    // check events (switches)
    boolean saveGameEvent = false;
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();

      if ( event instanceof FloorSwitch.EventStateChange ) {
        FloorSwitch fs = ((FloorSwitch.EventStateChange)event).mSwitch;
        fs.freezeState(true);
        mSwitchDone = true;
        mEnemyTimer = kEnemySwitchTime;
        Env.sounds().play(Sounds.SWITCH_ON);
        saveGameEvent = true;
        it.remove();
      }
    }
    if ( saveGameEvent ) storyEvents.add(new QuestStory.EventSaveGame());

    // add enemies
    if ( mSwitchDone && !mFlameDone ) {
      if ( --mEnemyTimer == 0 ) {
        mEnemyTimer = kEnemySpawnTime;
        Critter c = new Critter(3,7,4, Env.RIGHT, kCritterTrack);
        c.easilyKilled(true);
        c.setColour(kEnemyColours[mEnemyColour][0]);
        byte splatColour = (byte)kEnemyColours[mEnemyColour][1];
        mEnemyColour = (mEnemyColour+1) % 3;
        spriteManager.addSprite(c);
        spriteManager.addSprite(
               new Splatter(c.getXPos(), c.getYPos(), c.getZPos(),
                            -1, 4, splatColour, -1));
        Env.sounds().play(Sounds.MATERIALIZE);
      }
    }

    // check enemies that step into the flame
    if ( mSwitchDone && !mFlameDone ) {
      for ( Sprite sp : spriteManager.list() ) {
        if ( !(sp instanceof Critter) ) continue;
        Critter c = (Critter)sp;
        if ( c.getXPos() == 8 && c.getYPos() == 4 ) {
          int type;
          for ( type = 0 ; type < kEnemyColours.length ; type++ ) {
            if ( kEnemyColours[type][0] == c.getColour() ) break;
          }
          if ( type == mFlameColour ) {
            mProgress += 1;
            if ( mProgress == 3 ) {
              mFlameDone = true;
              mFlameRestartTimer = kFlameEndDelay;
            } else {
              mFlameColour = (mFlameColour+2) % 3;
              mFlameRestartTimer = kFlameRestartDelay;
            }
            mFlame.setFlame(false);
            spriteManager.removeSprite(c); // kill quietly
            Env.sounds().play(Sounds.FLAME_WHOOSH);
          } else {
            mProgress = 0;
            c.destroy(-1);
          }
          break;
        }
      }
    }

    // restart the flame
    if ( mFlameRestartTimer > 0 ) {
      assert( !mFlame.isOn() );
      if ( --mFlameRestartTimer == 0 ) {
        if ( mFlameDone ) {
          for ( Sprite sp : spriteManager.list() ) {
            if ( sp instanceof Critter ) ((Critter)sp).destroy(-1);
          }        
          Env.sounds().play(Sounds.SUCCESS);
          storyEvents.add(new QuestStory.EventSaveGame());
        } else {
          mFlame.setColours( kFlameColours[mFlameColour] );
          mFlame.setFlame(true);
        }
      }
    }
    
    // add the glow path when player stands on the old flame
    if ( mFlameDone && !mPathDone && mPath == null && 
         mFlameRestartTimer == 0 && mPlayer != null && 
         mPlayer.getXPos() == 8 && mPlayer.getYPos() == 4 ) {
      kExits[1].mDoor.setClosed(true);
      Env.sounds().play(Sounds.GATE);
      mPath = new GlowPath(kGlowPath, -1,0,4, kPathColour);
      spriteManager.addSprite(mPath);
    }

    // animate the blocks under the flame
    if ( mFlameDone && mFlameRestartTimer == 0 ) {
      int index0 = mFlameBlocksTimer/kFlameBlocksChangeDelay;
      index0 = (index0 < 3) ? (index0%2)+1 : 0;
      mFlameBlocksTimer -= 1;
      if ( mFlameBlocksTimer < 0 ) mFlameBlocksTimer = kFlameBlocksDelay;
      int index1 = mFlameBlocksTimer/kFlameBlocksChangeDelay;
      index1 = (index1 < 3) ? (index1%2)+1 : 0;
      if ( index0 != index1 ) {
        spriteManager.removeSprite( mFlameBlocks[index0] );
        spriteManager.addSprite( mFlameBlocks[index1] );
      }
    }
    
    // check the path
    if ( mPath != null && !mPathDone && mPath.complete() ) {
      mPathDone = true;
    }
        
  } // Room.advance()

} // class RoomC08
