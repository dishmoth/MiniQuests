/*
 *  RoomC14.java
 *  Copyright Simon Hern 2013
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BitBuffer;
import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Bullet;
import com.dishmoth.miniquests.game.Critter;
import com.dishmoth.miniquests.game.CritterTrack;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.Sprite;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.WallSwitch;

// the room "C14"
public class RoomC14 extends Room {

  // unique identifier for this room
  public static final String NAME = "C14";
  
  // blocks by the door
  private static final String kDoorBlocks[][] = { { "00" } };

  // blocks for the lift
  private static final String kLiftTop[] = { "1111111",
                                             "1  1  1", 
                                             "1  1  1",
                                             "1111111",
                                             "1  1  1",
                                             "1  1  1",
                                             "1111111" },
                              kLiftSide[] = { "1      ", "1      ", "1      ",
                                              "1      ", "1      ", "1      ", 
                                              "1111111" };
  private static final String kLiftBlocks[][] = { kLiftSide, kLiftSide, 
                                                  kLiftSide, kLiftSide, 
                                                  kLiftSide, kLiftSide, 
                                                  kLiftSide, kLiftSide, 
                                                  kLiftSide, kLiftSide, 
                                                  kLiftSide, 
                                                  kLiftTop, kLiftTop };

  // blocks below the door (artistic license: 11x11, not 10x10)
  private static final String kBelowBlocks[][] = { { "22222222222",
                                                     "2         2",
                                                     "2         2",
                                                     "2         2",
                                                     "2         2",
                                                     "2         2",
                                                     "2         2",
                                                     "2         2",
                                                     "2         2",
                                                     "2         2",
                                                     "22222222222" } };
  
  // blocks right at the top (artistic license: 11x11, not 10x10)
  private static final String kAboveBlocks[][] = { { "3333       ",
                                                     "3          ",
                                                     "3          ",
                                                     "3          ",
                                                     "           ",
                                                     "           ",
                                                     "           ",
                                                     "          3",
                                                     "          3",
                                                     "          3",
                                                     "       3333" } };
  
  // blocks that the monsters appear on
  private static final String kCritterBlocksUp[][]    = { { "3","3" } },
                              kCritterBlocksRight[][] = { { "33" } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "6L",   // purple 
                                                  "eL",   // dark purple
                                                  "6e",   // purple with yellow
                                                  "6K" }; // dark orange 
  
  // details of exit/entry points for the room 
  // (plus special case: 1 => ride down on lift)
  private static final Exit kExits[] 
          = { new Exit(Env.RIGHT, 4,10, "#L",1, 0, RoomC15.NAME, 0) };

  // details of different camera height levels
  private static final CameraLevel kCameraLevels[]
                                     = { new CameraLevel(8, -100, +1000) };

  // parameters controlling the lift
  private static final int kLiftHeight = 24,
                           kLiftZMin   = -14,
                           kLiftZMax   = 2,
                           kLiftZTop   = 126;
  private static final int kLiftDelay  = 4;

  // positions of high-up blocks
  private static final int kAboveBlocksZ1   = kLiftZTop + 8,
                           kAboveBlocksZ2   = kAboveBlocksZ1 - 20,
                           kAboveBlocksZ3   = kAboveBlocksZ2 - 20,
                           kCritterBlocksZ1 = kAboveBlocksZ3 - 20,
                           kCritterBlocksDZ = 10;
  
  // path followed by enemies in this room
  private static final CritterTrack kCritterTrack = 
                     new CritterTrack(new String[]{ " +        ",
                                                    " +        ",
                                                    " +++++++  ",
                                                    " +  +  +  ",
                                                    " +  +  +  ",
                                                    " +++++++  ",
                                                    " +  +  +  ",
                                                    " +  +  +  ",
                                                    " +++++++++",
                                                    "          " }, 0, 0);
  
  // time delay before the door unlocks
  private static final int kUnlockDelay = 25;
  
  // time delay before the player respawns - in the room above
  // (note: we're overriding the default behaviour in QuestStory)
  private static final int kPlayerDeathDelay = 30;
  
  // reference to the lift blocks
  private BlockArray mLift;
  
  // timer for lift movement
  private int mLiftTimer;
  
  // where the lift is heading
  private boolean mLiftAtTop;

  // lift switch
  private WallSwitch mSwitch;

  // monsters, prepared in advance
  private Critter mCritters[];

  // time until the door unlocks
  private int mUnlockTimer;

  // time until the player respawns (overriding the behaviour in QuestStory)
  private int mPlayerDeathTimer;
  
  // status of the room
  private boolean mComplete;
  
  // constructor
  public RoomC14() {

    super(NAME);

    mComplete = false;
    
  } // constructor

  // serialize the room state by writing bits to the specified buffer
  @Override
  public void save(BitBuffer buffer) {
    
    buffer.writeBit(mComplete);
    
  } // Room.save()

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  @Override
  public boolean restore(int version, BitBuffer buffer) { 
    
    if ( buffer.numBitsToRead() < 1 ) return false;
    mComplete = buffer.readBit();
    return true; 
    
  } // Room.restore() 
  
  // remove the player sprite from the room
  // (special behaviour: the player may be null already, since the respawn
  // in this case can also require a change of room)
  @Override
  public void removePlayer() { mPlayer = null; }
  
  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < kExits.length+1 );
    if ( entryPoint >= 0 && entryPoint < kExits.length ) {
      assert( mComplete );
      setPlayerAtExit(kExits[entryPoint], kCameraLevels);
    } else {
      // special case: exit 1 => at top of lift
      mLiftAtTop = false;
      mLift.setPos(mLift.getXPos(), mLift.getYPos(), 
                   (mComplete ? kLiftZMax : kLiftZTop));
      RoomC04 roomAbove = (RoomC04)findRoom(RoomC04.NAME);
      assert( roomAbove != null );
      int x = roomAbove.liftChangeXPos()+mLift.getXPos(),
          y = roomAbove.liftChangeYPos()+mLift.getYPos(),
          z = mLift.getZPos()+kLiftHeight;
      int direc = roomAbove.liftChangeDirec();
      mPlayer = new Player(x, y, z, direc);
      if ( mComplete ) mCamera.set(0, 0, kCameraLevels[0].mCameraZPos);
      else             mCamera.set(0, 0, mLift.getZPos()+kLiftHeight-2);
      mSwitch.setState(1);
    }
    return mPlayer;
    
  } // createPlayer()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {
    
    addBasicWalls(kExits, spriteManager);

    spriteManager.addSprite( new BlockArray(kBelowBlocks, 
                                            kBlockColours, -1,-1,2) );    

    spriteManager.addSprite( new BlockArray(kDoorBlocks, 
                                            kBlockColours, 8,4,10) );

    if ( !mComplete ) kExits[0].mDoor.setClosed(true);

    mLift = new BlockArray(kLiftBlocks, kBlockColours, 1,1,kLiftZMin);
    spriteManager.addSprite(mLift);
    
    if ( !mComplete ) {
      spriteManager.addSprite( new BlockArray(kAboveBlocks, 
                                              kBlockColours, 
                                              -1,-1,kAboveBlocksZ1) );
      spriteManager.addSprite( new BlockArray(kAboveBlocks, 
                                              kBlockColours, 
                                              -1,-1,kAboveBlocksZ2) );
      spriteManager.addSprite( new BlockArray(kAboveBlocks, 
                                              kBlockColours, 
                                              -1,-1,kAboveBlocksZ3) );
      
      int z[] = new int[4];
      for ( int n = 0 ; n < 4 ; n++ ) {
        z[n] = kCritterBlocksZ1 - n*kCritterBlocksDZ;
      }
   
      spriteManager.addSprite( new BlockArray(kCritterBlocksUp, 
                                              kBlockColours, 1, 8, z[0]) );
      spriteManager.addSprite( new BlockArray(kCritterBlocksRight, 
                                              kBlockColours, 8, 1, z[1]) );
      spriteManager.addSprite( new BlockArray(kCritterBlocksUp, 
                                              kBlockColours, 1, 8, z[2]) );
      spriteManager.addSprite( new BlockArray(kCritterBlocksRight,
                                              kBlockColours, 8, 1, z[3]) );
      
      mCritters = new Critter[]{ new Critter(1, 9, z[0], 
                                             Env.DOWN, kCritterTrack),
                                 new Critter(9, 1, z[1], 
                                             Env.LEFT, kCritterTrack),
                                 new Critter(1, 9, z[2], 
                                             Env.DOWN, kCritterTrack),
                                 new Critter(9, 1, z[3], 
                                             Env.LEFT, kCritterTrack) };
      for ( Critter critter : mCritters ) {
        critter.easilyKilled(true);
        critter.setColour(3);
      }
    }
    
    mSwitch = new WallSwitch(Env.RIGHT, 6, 12, 
                             new String[]{"Ru","7u"}, false);
    spriteManager.addSprite(mSwitch);

    mLiftTimer = 0;
    mLiftAtTop = false;
    mUnlockTimer = 0;
    mPlayerDeathTimer = 0;
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mLift = null;
    mSwitch = null;
    mCritters = null;
    
  } // Room.discardResources()
  
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
    if ( mLiftAtTop && mLift.getZPos() == kLiftZMax ) {
      storyEvents.add(new EventRoomChange(RoomC04.NAME, 4));
      RoomC04 roomAbove = (RoomC04)findRoom(RoomC04.NAME);
      assert( roomAbove != null );
      roomAbove.recordLiftChangeInfo(mPlayer.getXPos()-mLift.getXPos(), 
                                     mPlayer.getYPos()-mLift.getYPos(),
                                     mPlayer.getDirec());
      return;
    }

    // process the story event list
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();
      
      // switch has been hit
      if ( event instanceof WallSwitch.EventStateChange ) {
        mLiftAtTop = true;
        it.remove();
      }

      // player has died - respawn in the room above
      else if ( event instanceof Player.EventKilled ) {
        mPlayerDeathTimer = kPlayerDeathDelay;
      }
    }

    // animate the lift
    if ( mLiftAtTop || (!mLiftAtTop && mLift.getZPos() > kLiftZMin) ) {
      if ( --mLiftTimer <= 0 ) {
        mLiftTimer = kLiftDelay;
        mLift.shiftPos(0, 0, (mLiftAtTop?+1:-1));
        if ( !mLiftAtTop && mLift.getZPos() == kLiftZMin ) {
          Env.sounds().play(Sounds.SWITCH_DEEP);
          if ( mComplete ) mSwitch.setState(0);
        }
        if ( !mComplete ) {
          // move the camera to follow the lift
          mCamera.set(0, 0, mLift.getZPos()+kLiftHeight-2);
          // move any bullets to follow the lift
          for ( Sprite s : spriteManager.list() ) {
            if ( s instanceof Bullet ) ((Bullet)s).shiftPos(0, 0, -1);
          }
          // don't let the player go off screen
          if ( mPlayer != null && 
               mPlayer.getZPos() >= mLift.getZPos() + kLiftHeight + 14 ) {
            mPlayer.destroy(-1);
          }
        }
      }
    }

    // spawn monsters at the right moment 
    if ( !mComplete ) {
      int z0 = mLift.getZPos() + kLiftHeight - 18;
      if ( z0 <= kCritterBlocksZ1 && 
           z0 >= kCritterBlocksZ1-3*kCritterBlocksDZ &&
           (kCritterBlocksZ1-z0)%kCritterBlocksDZ == 0 &&
           mLiftTimer == 4 ) {
        int n = (kCritterBlocksZ1-z0)/kCritterBlocksDZ;
        Critter critter = mCritters[n];
        mCritters[n] = null;
        spriteManager.addSprite(critter);
      }
    }

    // unlock the door
    if ( mUnlockTimer > 0 ) {
      if ( --mUnlockTimer == 0 ) {
        kExits[0].mDoor.setClosed(false);
        mSwitch.setState(0);
        Env.sounds().play( Sounds.SUCCESS );
        mComplete = true;
      }
    }
    if ( !mComplete && mLift.getZPos() == kLiftZMin && 
         mUnlockTimer == 0 && mPlayer != null &&
         spriteManager.findSpriteOfType(Critter.class) == null ) {
      mUnlockTimer = kUnlockDelay;
    }

    // respawn in previous room
    if ( mPlayerDeathTimer > 0 ) {
      if ( --mPlayerDeathTimer == 0 ) {
        assert( mPlayer == null );
        storyEvents.add(new EventRoomChange(RoomC04.NAME, 3));
      }
    }
    
  } // Room.advance()

} // class RoomC14
