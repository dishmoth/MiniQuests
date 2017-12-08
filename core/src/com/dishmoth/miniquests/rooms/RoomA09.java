/*
 *  RoomA09.java
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
import com.dishmoth.miniquests.game.Liquid;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.QuestStory;
import com.dishmoth.miniquests.game.WallSwitch;

// the room "A09"
public class RoomA09 extends Room {

  // unique identifier for this room
  public static final String NAME = "A09";
  
  // main blocks for the room
  private static final String kBlocks[][] = { { "     0000 ",
                                                "     0    ",
                                                "     0    ",
                                                "     0    ",
                                                "     0    ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                              
                                              { "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "     00   ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                              
                                              { "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "      00  ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "        0 ",
                                                "        0 ",
                                                "        0 ",
                                                "      000 ",
                                                "      0   ",
                                                "      0   " } };
                                              
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "VB" }; 
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.DOWN, 6, 0, "VB",0, -1, RoomA03.NAME, 5),
              new Exit(Env.UP,   8,-8, "#B",1, -1, RoomA10.NAME, 0) };

  // different levels for the lava
  private static final int kLavaLevelHigh =  -1,
                           kLavaLevelLow  = -10;
  
  // how fast the lava level changes
  private static final float kLavaSpeedMin = 0.09f,
                             kLavaSpeedMax = 0.15f;

  // how long the main switch stays on for
  private static final int kSwitchDelay = 240;

  // ticking sounds counting down
  private static final int kTickDelay  = 8,
                           kTickNumber = 8;
  
  // whether the lava has been switched off yet
  private boolean mLavaDone;
  
  // current height of the lava
  private float mLavaLevel;
  
  // reference to the lava object
  private Liquid mLava;
  
  // reference to the main switch
  private WallSwitch mFirstSwitch;

  // references to the three other switches
  private WallSwitch mSecondSwitches[];
  
  // time until the main switch goes off
  private int mSwitchTimer;
  
  // extra block that only appears when necessary
  private BlockArray mExtraBlock;
  
  // constructor
  public RoomA09() {

    super(NAME);

    mLavaDone = false;
    
  } // constructor

  // serialize the room state by writing bits to the specified buffer
  @Override
  public void save(BitBuffer buffer) {
    
    buffer.writeBit(mLavaDone);
    
  } // Room.save()

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  @Override
  public boolean restore(int version, BitBuffer buffer) { 
    
    if ( buffer.numBitsToRead() < 1 ) return false;
    mLavaDone = buffer.readBit();
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
    
    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,-8) );
    
    addBasicWalls(kExits, spriteManager);
    if ( !mLavaDone ) kExits[1].mDoor.setClosed(true);

    mLavaLevel = ( mLavaDone ? kLavaLevelLow : kLavaLevelHigh );
    mLava = new Liquid(0,0,Math.round(mLavaLevel), 1);
    spriteManager.addSprite(mLava);

    addFirstSwitch(spriteManager);
    mSwitchTimer = 0;
    
    if ( mLavaDone ) addExtraBlock(spriteManager);
    
  } // Room.createSprites()
  
  // create (or reset) the first switch
  private void addFirstSwitch(SpriteManager spriteManager) {

    if ( mFirstSwitch != null ) spriteManager.removeSprite(mFirstSwitch);
    
    if ( mLavaDone ) {
      mFirstSwitch = new WallSwitch(Env.UP, 8, 2, 
                                    new String[]{"W7"}, false);
    } else {
      mFirstSwitch = new WallSwitch(Env.UP, 8, 2, 
                                    new String[]{"a7","W7"}, false);
    }
    spriteManager.addSprite(mFirstSwitch);
    
  } // addFirstSwitch()
  
  // create the secondary switches
  private void addSecondSwitches(SpriteManager spriteManager) {

    assert( mSecondSwitches == null );
    mSecondSwitches = new WallSwitch[]
        { new WallSwitch(Env.UP, 5, -6, new String[]{"s7","u7"}, false),
          new WallSwitch(Env.RIGHT, 8, -6, new String[]{"su","7u"}, false),
          new WallSwitch(Env.RIGHT, 2, 2, new String[]{"su","7u"}, false) };
    for ( WallSwitch ws : mSecondSwitches ) spriteManager.addSprite(ws);
    
  } // addSecondSwitches()
  
  // remove the secondary switches
  private void removeSecondSwitches(SpriteManager spriteManager) {
    
    assert( mSecondSwitches != null );
    for ( WallSwitch ws : mSecondSwitches ) {
      if ( ws != null ) spriteManager.removeSprite(ws);
    }
    mSecondSwitches = null;

  } // removeSecondSwitches()

  // additional block, prevent the player stepping into lava
  private void addExtraBlock(SpriteManager spriteManager) {

    if ( mExtraBlock != null ) return;
    
    String blocks[][] = { { "00" } };
    mExtraBlock = new BlockArray(blocks, kBlockColours, 7,5,-2);
    spriteManager.addSprite(mExtraBlock);
    
  } // addExtraBlock()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mLava = null;

    mFirstSwitch = null;
    mSecondSwitches = null;

    mExtraBlock = null;
    
  } // Room.discardResources()
  
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

    // check for switch events
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();
      
      if ( event instanceof WallSwitch.EventStateChange ) {
        assert( !mLavaDone );
        if ( ((WallSwitch.EventStateChange)event).mSwitch == mFirstSwitch ) {
          addSecondSwitches(spriteManager);
          mSwitchTimer = kSwitchDelay;
          addExtraBlock(spriteManager);
        }
        it.remove();
      }
      
    } // for (event)

    // count down the timer
    if ( !mLavaDone && mSwitchTimer > 0 ) {
      mSwitchTimer--;
      if ( mSwitchTimer == 0 ) {
        addFirstSwitch(spriteManager);
        removeSecondSwitches(spriteManager);
        Env.sounds().play(Sounds.SWITCH_OFF);
      } else if ( mSwitchTimer % kTickDelay == 0 ) {
        int i = mSwitchTimer/kTickDelay;
        if ( i <= kTickNumber ) {
          if ( i%2 == 0 ) Env.sounds().play(Sounds.TICK);
          else            Env.sounds().play(Sounds.TOCK);
        }
      }
    }
    
    // animate the lava
    boolean lavaLow = ( mLavaDone || mSwitchTimer > 0 );
    float targetLevel = ( lavaLow ? kLavaLevelLow : kLavaLevelHigh );
    if ( mLavaLevel != targetLevel ) {
      final float g = (mLavaLevel - kLavaLevelLow)
                     /(kLavaLevelHigh - kLavaLevelLow);
      final float h = 1.0f - Math.abs(2.0f*g - 1.0f);
      final float speed = (1-h)*kLavaSpeedMin + h*kLavaSpeedMax;  
      if ( mLavaLevel > targetLevel ) {
        mLavaLevel = Math.max(targetLevel, mLavaLevel-speed);
      } else {
        mLavaLevel = Math.min(targetLevel, mLavaLevel+speed);
      }
      mLava.setZPos( Math.round(mLavaLevel) );
    } else {
      if ( mLavaLevel == kLavaLevelHigh && mExtraBlock != null ) {
        spriteManager.removeSprite(mExtraBlock);
        mExtraBlock = null;
      }
    }

    // check whether all the switches have been hit
    if ( !mLavaDone && mSecondSwitches != null ) {
      boolean done = true;
      for ( WallSwitch ws : mSecondSwitches ) {
        if ( ws.isActive() ) {
          done = false;
          break;
        }
      }
      if ( done ) {
        mLavaDone = true;
        Env.sounds().play(Sounds.SUCCESS, 3);
        kExits[1].mDoor.setClosed(false);
        storyEvents.add(new QuestStory.EventSaveGame());
      }
    }
    
  } // Room.advance()

} // class RoomA09
