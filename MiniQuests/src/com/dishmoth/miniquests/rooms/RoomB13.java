/*
 *  RoomB13.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BitBuffer;
import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Chest;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.FlameBolt;
import com.dishmoth.miniquests.game.FloorSwitch;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.Splatter;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.TinyStory;
import com.dishmoth.miniquests.game.Triffid;
import com.dishmoth.miniquests.game.TriffidBoss;

// the room "B13"
public class RoomB13 extends Room {

  // blocks for beneath the room
  private static final String kFloorBlocks[][] = { { "0000000000",
                                                     "0000000000",
                                                     "0000000000",
                                                     "0000000000",
                                                     "0000000000",
                                                     "0000000000",
                                                     "0000000000",
                                                     "0000000000",
                                                     "0000000000",
                                                     "0000000000" } };
  
  // blocks building up to the centre of the room
  private static final String kTowerBlocks1[][] = { { "1111",
                                                      "1111",
                                                      "1111",
                                                      "1111" },
                                                     
                                                    { "1111",
                                                      "1111",
                                                      "1111",
                                                      "1111" },
                                                     
                                                    { "1111",
                                                      "1111",
                                                      "1111",
                                                      "1111" },
                                                      
                                                    { "1111",
                                                      "1111",
                                                      "1111",
                                                      "1111" } };
  
  // blocks building up to the start of the room
  private static final String kTowerBlocks2[][] = { { "111",
                                                      "111",
                                                      "111" },
                                                     
                                                    { "111",
                                                      "111",
                                                      "111" },
                                                     
                                                    { "111",
                                                      "111",
                                                      "111" },
                                                      
                                                    { "111",
                                                      "111",
                                                      "111" } };
  
  // main blocks for the room
  private static final String kBlocks[][] = { { "          ",
                                                "          ",
                                                "          ",
                                                "   1111   ",
                                                "   1111   ",
                                                "   1111   ",
                                                "   1111   ",
                                                "          ",
                                                "          ",
                                                "    1     ",
                                                "    1     ",
                                                "    1     ",
                                                "    1     ",
                                                "111 1     ",
                                                "12111 111 ",
                                                "111 11121 ",
                                                "    1 111 ",
                                                "    1     ",
                                                "    1     ",
                                                "    1     " },
                                                
                                              { "2222222222",
                                                "2        2",
                                                "2        2",
                                                "2        2",
                                                "2        2",
                                                "2        2",
                                                "2        2",
                                                "2        2",
                                                "2        2",
                                                "2222222222",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " } };

  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "ue",   // dark red 
                                                  "NB",   // 
                                                  "#B" }; // 
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(0,0, Env.DOWN, 4,10, "NB",1, 0, RoomB12.class, 1) };

  // details of different camera height levels
  private static final CameraLevel kCameraLevels[]
                                     = { new CameraLevel(10, -100, +100) };

  // how long before the boss emerges
  private static final int kTimeBossAppear = 10;

  // how long until a new helper appears
  private static final int kTimeHelperTriffid = 40;
  
  // how fast the helper turns
  private static final int kHelperRotateRate = 25;
  
  // times for game end events
  private static final int kGameEndsDelay   = 50,
                           kChestSoundDelay = kGameEndsDelay - 5,
                           kChestOpenDelay  = kChestSoundDelay - 10;
  
  // whether the boss has been defeated yet
  private boolean mBossDone;
  
  // whether the chest has appeared yet
  private boolean mChestAppeared;
  
  // references to triffids near the entrance
  private Triffid mSentryTriffid1,
                  mSentryTriffid2;

  // reference to the triffid near the boss
  private Triffid mHelperTriffid;

  // reference to the boss
  private TriffidBoss mBoss;
  
  // ticks remaining until boss appears
  private int mBossAppearTimer;

  // ticks remaining until new help triffid
  private int mHelperTriffidTimer;

  // reference to the chest object
  private Chest mChest;
  
  // countdown once the chest is opened
  private int mEndTimer;
  
  // constructor
  public RoomB13() {
    
    mBossDone = false;
    mChestAppeared = false;
    
  } // constructor

  // serialize the room state by writing bits to the specified buffer
  @Override
  public void save(BitBuffer buffer) {
    
    buffer.writeBit(mBossDone);
    buffer.writeBit(mChestAppeared);
    
  } // Room.save()

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  @Override
  public boolean restore(int version, BitBuffer buffer) { 
    
    if ( buffer.numBitsToRead() < 2 ) return false;
    mBossDone      = buffer.readBit();
    mChestAppeared = buffer.readBit();
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

    mSentryTriffid1 = mSentryTriffid2 = null;
    mHelperTriffid = null;
    mBoss = null;
    mChest = null;
    
  } // Room.discardResources()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {

    int zoneX, zoneY;
    
    // zone (0,0)
    
    zoneX = 0;
    zoneY = 0;
    spriteManager.addSprite( 
                 new BlockArray(kFloorBlocks, kBlockColours, 
                                zoneX*Room.kSize, zoneY*Room.kSize, 0) );
    spriteManager.addSprite( 
                 new BlockArray(kBlocks, kBlockColours, 
                                zoneX*Room.kSize, zoneY*Room.kSize, 10) );
    spriteManager.addSprite( 
                 new BlockArray(kTowerBlocks2, kBlockColours, 
                                zoneX*Room.kSize+0, zoneY*Room.kSize+4, 2) );
    spriteManager.addSprite( 
                 new BlockArray(kTowerBlocks2, kBlockColours, 
                                zoneX*Room.kSize+6, zoneY*Room.kSize+3, 2) );
    addBasicZone(zoneX, zoneY, 
                 true, false, true, true, 
                 kExits, spriteManager);

    // zone (0,1)
    
    zoneX = 0;
    zoneY = 1;
    spriteManager.addSprite( 
                 new BlockArray(kFloorBlocks, kBlockColours, 
                                zoneX*Room.kSize, zoneY*Room.kSize, 0) );
    spriteManager.addSprite( 
                 new BlockArray(kTowerBlocks1, kBlockColours, 
                                zoneX*Room.kSize+3, zoneY*Room.kSize+3, 2) );
    addBasicZone(zoneX, zoneY, 
                 true, true, true, false, 
                 kExits, spriteManager);

    // assorted stuff
    
    if ( mBossDone && !mChestAppeared ) {
      makeChestSwitch(spriteManager);
    }
    
    if ( mBossDone && mChestAppeared ) {
      makeChest(spriteManager, false);
    }
    
    mBossAppearTimer = 0;
    mHelperTriffidTimer = 0;
    mEndTimer = 0;
    
  } // Room.createSprites()

  // create a helper triffid
  private void makeHelperTriffid(SpriteManager spriteManager) {
    
    assert( mHelperTriffid == null );
    
    int playerSide = Env.DOWN;
    if ( mPlayer != null ) {
      if      ( mPlayer.getXPos() ==  9 ) playerSide = Env.RIGHT;
      else if ( mPlayer.getXPos() ==  0 ) playerSide = Env.LEFT;
      else if ( mPlayer.getYPos() == 10 ) playerSide = Env.DOWN;
      else if ( mPlayer.getYPos() == 19 ) playerSide = Env.UP;
    }
    int side = ((playerSide+2) % 4);
    
    final int xyPos[] = { 9, 14,   // right
                          4, 19,   // up
                          0, 14,   // left
                          4, 10 }; // down
    mHelperTriffid = new Triffid(xyPos[2*side], xyPos[2*side+1], 12, 
                                 (side+2)%4);
    mHelperTriffid.setRotateRate( kHelperRotateRate
                                  *(Env.randomBoolean()?+1:-1) );
    mHelperTriffid.setFireRange(7);
    spriteManager.addSprite(mHelperTriffid);
    
  } // makeHelperTriffid()

  // create the chest switch
  private void makeChestSwitch(SpriteManager spriteManager) {
    
    assert( !mChestAppeared );
    spriteManager.addSprite( new FloorSwitch(1, 5, 10, "#W", "#B") );

  } // makeChestSwitch()
  
  // create the chest
  private void makeChest(SpriteManager spriteManager, boolean withSplatter) {
    
    final int x = 7,
              y = 3,
              z = 10;
    
    assert( mChestAppeared && mChest == null );
    mChest = new Chest(x, y, z, Env.LEFT);
    spriteManager.addSprite( mChest );

    if ( withSplatter ) {
      final int h = 5;
      final byte col = 0;
      spriteManager.addSprite(new Splatter(x,   y,   z, -1, h, col, -1));
      spriteManager.addSprite(new Splatter(x,   y+1, z, -1, h, col, -1));
      spriteManager.addSprite(new Splatter(x,   y+2, z, -1, h, col, -1));
      spriteManager.addSprite(new Splatter(x+1, y,   z, -1, h, col, -1));
      spriteManager.addSprite(new Splatter(x+1, y+2, z, -1, h, col, -1));
    }
    
  } // makeChest()
  
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
    
    // check for scrolling
    
    EventRoomScroll scroll = checkHorizontalScroll();
    if ( scroll != null ) {
      storyEvents.add(scroll);
    }

    // process the story event list
    boolean saveGameEvent = false;
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();

      // the player has been killed
      if ( event instanceof Player.EventKilled ) {
        if ( mSentryTriffid1 != null ) mSentryTriffid1.kill();
        if ( mSentryTriffid2 != null ) mSentryTriffid2.kill();
        if ( mHelperTriffid != null )  mHelperTriffid.kill();
        mBossAppearTimer = 0;
        mHelperTriffidTimer = 0;
      }
      
      // a triffid has been killed
      if ( event instanceof Triffid.EventKilled ) {
        Triffid tr = ((Triffid.EventKilled)event).mSource;
        if ( tr == mSentryTriffid1 ) mSentryTriffid1 = null;
        if ( tr == mSentryTriffid2 ) mSentryTriffid2 = null;
        if ( tr == mHelperTriffid )  mHelperTriffid  = null;
        it.remove();
      }
      
      // the triffid boss has been killed
      if ( event instanceof TriffidBoss.EventKilled ) {
        if ( mSentryTriffid1 != null ) mSentryTriffid1.kill();
        if ( mSentryTriffid2 != null ) mSentryTriffid2.kill();
        if ( mHelperTriffid != null )  mHelperTriffid.kill();
        mBoss = null;
        mBossAppearTimer = 0;
        mHelperTriffidTimer = 0;
        mBossDone = true;
        makeChestSwitch(spriteManager);
        saveGameEvent = true;
        it.remove();
      }
      
      // the chest switch has been triggered
      if ( event instanceof FloorSwitch.EventStateChange ) {
        FloorSwitch s = ((FloorSwitch.EventStateChange)event).mSwitch;
        s.freezeState(true);
        Env.sounds().play(Sounds.SWITCH_ON);
        mChestAppeared = true;
        makeChest(spriteManager, true);
        saveGameEvent = true;
        Env.sounds().play(Sounds.SUCCESS, 10);
        it.remove();
      }
      
    }
    if ( saveGameEvent ) storyEvents.add(new TinyStory.EventSaveGame());
    
    // create sentry triffids
    
    if ( mSentryTriffid1 == null && !mBossDone && 
         mPlayer != null && mPlayer.getYPos() == 6 ) {
      mSentryTriffid1 = new Triffid(7, 4, 10, Env.LEFT);
      spriteManager.addSprite(mSentryTriffid1);
      Env.sounds().play(Sounds.TRIFFID_EMERGE);
    }
    if ( mSentryTriffid2 == null && !mBossDone && 
         mPlayer != null && mPlayer.getYPos() == 7 ) {
      mSentryTriffid2 = new Triffid(1, 5, 10, Env.RIGHT);
      spriteManager.addSprite(mSentryTriffid2);
      Env.sounds().play(Sounds.TRIFFID_EMERGE);
    }

    // create boss triffid
    
    if ( !mBossDone && mBoss == null && mBossAppearTimer == 0 && 
         mPlayer != null && mPlayer.getYPos() == 10 ) {
      mBossAppearTimer = kTimeBossAppear;
    }
    if ( mBossAppearTimer > 0 ) {
      if ( --mBossAppearTimer == 0 ) {
        mBoss = new TriffidBoss(4, 14, 10); 
        spriteManager.addSprite(mBoss);
        Env.sounds().play(Sounds.TBOSS_EMERGE);
      }
    }

    // create helper triffid
    
    if ( mHelperTriffid == null ) {
      if ( mHelperTriffidTimer == 0 ) {
        if ( !mBossDone && mBoss != null && 
             mPlayer != null && mPlayer.getYPos() >= 10 && 
             spriteManager.findSpriteOfType(FlameBolt.class) != null ) {
          mHelperTriffidTimer = kTimeHelperTriffid;
        }
      } else if ( mBossDone || mPlayer == null || mPlayer.getYPos() < 10 ) {
        mHelperTriffidTimer = 0;
      } else if ( --mHelperTriffidTimer == 0 ) {
        makeHelperTriffid(spriteManager);
        Env.sounds().play(Sounds.TRIFFID_EMERGE);
      }
    }

    // once the chest is open
    
    if ( mEndTimer > 0 ) {
      mEndTimer--;
      if ( mEndTimer == kChestSoundDelay ) {
        Env.sounds().play(Sounds.CHEST);        
      } else if ( mEndTimer == kChestOpenDelay ) {
        mChest.setOpen(true);
      } else if ( mEndTimer == 0 ) {
        storyEvents.add(new TinyStory.EventPlayerWins());
      }
    }

    // check for opening the chest

    if ( mEndTimer == 0 && mBossDone && mChestAppeared && 
         mChest != null && !mChest.isOpen() && mPlayer != null &&
         mPlayer.getXPos() == 6 && mPlayer.getYPos() == 4 ) {
      mPlayer.mAdvanceDisabled = true;
      mEndTimer = kGameEndsDelay;
    }
        
  } // Room.advance()

} // class RoomB13
