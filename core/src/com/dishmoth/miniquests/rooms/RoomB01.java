/*
 *  RoomB01.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BitBuffer;
import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Brain;
import com.dishmoth.miniquests.game.Door;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Fence;
import com.dishmoth.miniquests.game.FenceGate;
import com.dishmoth.miniquests.game.FloorSwitch;
import com.dishmoth.miniquests.game.Liquid;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.Tree;
import com.dishmoth.miniquests.game.TreesRight;
import com.dishmoth.miniquests.game.TreesUp;
import com.dishmoth.miniquests.game.Wall;
import com.dishmoth.miniquests.game.WallRight;

// the room "B01"
public class RoomB01 extends Room {

  // unique identifier for this room
  public static final String NAME = "B01";
  
  // main blocks for the floor
  private static final String kBlocks[][] = { { "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000" } };
  
  // blocks for floor with stream
  private static final String kStreamBlocks[][] = { { "0000000000",
                                                      " 000000000",
                                                      " 000000000",
                                                      " 000000000",
                                                      "0000000000",
                                                      "          ",
                                                      "          ",
                                                      "          ",
                                                      "          ",
                                                      "          " },
                                                      
                                                    { "0000000000",
                                                      " 000000000",
                                                      "          ",
                                                      " 000000000",
                                                      "0000000000",
                                                      "          ",
                                                      "          ",
                                                      "          ",
                                                      "          ",
                                                      "          " },
                                                      
                                                    { "0000000000",
                                                      "          ",
                                                      "          ",
                                                      "          ",
                                                      "0000000000",
                                                      "0000000000",
                                                      "0000000000",
                                                      "0000000000",
                                                      "0000000000",
                                                      "0000000000" },
  
                                                    { "    2     ",
                                                      "    2     ",
                                                      "          ",
                                                      "    2     ",
                                                      "    2     ",
                                                      "          ",
                                                      "          ",
                                                      "          ",
                                                      "          ",
                                                      "          " },
  
                                                    { "          ",
                                                      "    2     ",
                                                      "    2     ",
                                                      "    2     ",
                                                      "          ",
                                                      "          ",
                                                      "          ",
                                                      "          ",
                                                      "          ",
                                                      "          " } };
  
  // more blocks for floor with stream
  private static final String kMoreStreamBlocks[][] = { { "000000",
                                                          "      ",
                                                          "      ",
                                                          "      ",
                                                          "      ",
                                                          "   000",
                                                          "  0000",
                                                          "000000",
                                                          "000000",
                                                          "000000" } };
  
  // pattern for the stream tiles
  private static final String kStreamPattern[] = { "###############",
                                                   "###############",
                                                   "###############",
                                                   "#######        ",
                                                   "####           ",
                                                   "##             " };
  
  // blocks for the front door
  private static final String kEntranceBlocks[][] = { { "11111",
                                                        "11111",
                                                        "11111" },
                                                      { "11111",
                                                        "11111",
                                                        "     " },
                                                      { "11111",
                                                        "     ",
                                                        "     " } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "Y2",   // grass green
                                                  "#6",   // grey
                                                  "#S" }; // red
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { // note: dummy exits at index 0 (start) and index 3 (switch)
              new Exit(1,1, Env.UP,    4,6,  "#k",1, -1, RoomB02.NAME, 2),
              new Exit(0,2, Env.RIGHT, 7,-2, "SS",0, -1, RoomB02.NAME, 0) };

  // time delay before the entrance gate closes
  private static final int kTimeEntranceGate = 40;
  
  // step time to slow the door as it rises
  private static final int kTimeHiddenDoor = 4;

  // whether the bridge switch has been triggered yet
  private boolean mSwitchDone;
  
  // the gate the player arrived through
  private FenceGate mEntranceGate = null;
  
  // a slight delay before the entrance gate closes
  private int mEntranceGateTimer;

  // the hidden door, once it has been activated
  private Door mHiddenDoor     = null;
  private Wall mHiddenDoorWall = null;

  // step timer as the door raises
  private int mHiddenDoorTimer;
  
  // constructor
  public RoomB01() {

    super(NAME);

    mSwitchDone = false;
    
  } // constructor

  // serialize the room state by writing bits to the specified buffer
  @Override
  public void save(BitBuffer buffer) {
    
    buffer.writeBit(mSwitchDone);
    
  } // Room.save()

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  @Override
  public boolean restore(int version, BitBuffer buffer) { 
    
    if ( buffer.numBitsToRead() < 1 ) return false;
    mSwitchDone = buffer.readBit();
    return true; 
    
  } // Room.restore() 
  
  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint <= 3 );
    
    if ( entryPoint == 0 ) {
      // special case: start of game
      final int zoneX = 1,
                zoneY = 0;
      mPlayer = new Player(zoneX*Room.kSize + 5, 
                           zoneY*Room.kSize, 
                           0, Env.UP);
      mPlayer.addBrain(new Brain.ZombieModule(new int[]{ Env.NONE,1,
                                                         Env.UP,25 }));
      mCameraLevel = -1;
      mCamera.set(zoneX*Room.kSize, zoneY*Room.kSize, 0);
    } else if ( entryPoint == 3 ) {
      // special case: switch triggered
      final int zoneX = 0,
                zoneY = 2;
      mPlayer = new Player(zoneX*Room.kSize + 4, 
                           zoneY*Room.kSize + 7, 
                           -4, Env.LEFT);
      mCameraLevel = -1;
      mCamera.set(zoneX*Room.kSize, zoneY*Room.kSize, 0);
    } else {
      setPlayerAtExit(kExits[entryPoint-1]);
    }

    return mPlayer;
    
  } // createPlayer()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mEntranceGate = null;
    mHiddenDoor = null;
    mHiddenDoorWall = null;

  } // Room.discardResources()

  // add tree walls (0 = none, 1 = normal, 2 = with wall)
  private void addTrees(int zoneX, int zoneY, 
                        int treesRight, int treesUp,
                        SpriteManager spriteManager) {

    final int x = zoneX*Room.kSize,
              y = zoneY*Room.kSize;
    if ( treesRight > 0 ) {
      int index = treesRight - 1;
      spriteManager.addSprite(new TreesRight(x, y, 0, index));
    }
    if ( treesUp > 0 ) {
      int index = treesUp - 1;
      spriteManager.addSprite(new TreesUp(x, y, 0, index));
    }
    
  } // addTrees()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {

    int zoneX, zoneY;

    Exit exits[] = ( mSwitchDone 
                     ? kExits 
                     : new Exit[]{ kExits[0] } );

    // zone (1,-1)
    
    zoneX = 1;
    zoneY = -1;
    spriteManager.addSprite( 
                 new BlockArray(kBlocks, kBlockColours, 
                                zoneX*Room.kSize, zoneY*Room.kSize, 0) ); 

    spriteManager.addSprite(new Fence(zoneX*Room.kSize-1, 
                                      zoneY*Room.kSize+9,
                                      0, 6, Env.RIGHT, 0));
    spriteManager.addSprite(new Fence(zoneX*Room.kSize+6, 
                                      zoneY*Room.kSize+9, 
                                      0, 4, Env.RIGHT, 0));
    
    mEntranceGate = new FenceGate(zoneX*Room.kSize+4, 
                                  zoneY*Room.kSize+9, 
                                  0, Env.RIGHT, 0);
    spriteManager.addSprite(mEntranceGate);

    if ( mSwitchDone ) {
      mEntranceGate.setClosed(true);
      mEntranceGateTimer = 0;
    } else {
      mEntranceGateTimer = kTimeEntranceGate;
    }
    
    // zone (0,0)
    
    zoneX = 0;
    zoneY = 0;
    spriteManager.addSprite( 
                 new BlockArray(kBlocks, kBlockColours, 
                                zoneX*Room.kSize, zoneY*Room.kSize, 0) ); 

    spriteManager.addSprite(new Fence(zoneX*Room.kSize+9, 
                                      zoneY*Room.kSize-1,
                                      0, 12, Env.UP, 0));

    spriteManager.addSprite(new Tree(zoneX*Room.kSize+4, 
                                     zoneY*Room.kSize+2, 0, 1, 0,0));
    spriteManager.addSprite(new Tree(zoneX*Room.kSize+3, 
                                     zoneY*Room.kSize+6, 0, 0, 0,0));
    spriteManager.addSprite(new Tree(zoneX*Room.kSize-1, 
                                     zoneY*Room.kSize+6, 0, 1, 0,0));

    // zone (1,0)
    
    zoneX = 1;
    zoneY = 0;
    spriteManager.addSprite( 
                 new BlockArray(kBlocks, kBlockColours, 
                                zoneX*Room.kSize, zoneY*Room.kSize, 0) ); 
    addTrees(zoneX, zoneY, 1, 0, spriteManager);

    // zone (-1,1)
    
    zoneX = -1;
    zoneY = 1;
    spriteManager.addSprite( 
                 new BlockArray(kBlocks, kBlockColours, 
                                zoneX*Room.kSize, zoneY*Room.kSize, 0) ); 

    spriteManager.addSprite(new Tree(zoneX*Room.kSize+4, 
                                     zoneY*Room.kSize+2, 0, 1, 0,0));
    
    // zone (0,1)
    
    zoneX = 0;
    zoneY = 1;
    spriteManager.addSprite( 
                 new BlockArray(kBlocks, kBlockColours, 
                                zoneX*Room.kSize, zoneY*Room.kSize, 0) ); 

    spriteManager.addSprite(new Fence(zoneX*Room.kSize, 
                                      zoneY*Room.kSize,
                                      0, 10, Env.RIGHT, 0));
    spriteManager.addSprite(new Fence(zoneX*Room.kSize, 
                                      zoneY*Room.kSize,
                                      0, 11, Env.UP, 0));
    
    // zone (1,1)
    
    zoneX = 1;
    zoneY = 1;
    spriteManager.addSprite( 
                 new BlockArray(kBlocks, kBlockColours, 
                                zoneX*Room.kSize, zoneY*Room.kSize, 0) );
    spriteManager.addSprite(
                 new BlockArray(kEntranceBlocks, kBlockColours,
                                zoneX*Room.kSize+2, zoneY*Room.kSize+7, 2) );
    addBasicZone(zoneX, zoneY, 
                 false, true, false, false, 
                 exits, spriteManager);
    addTrees(zoneX, zoneY, 2, 0, spriteManager);

    // zone (-1,2)
    
    zoneX = -1;
    zoneY = 2;
    spriteManager.addSprite( 
                 new BlockArray(kMoreStreamBlocks, kBlockColours, 
                                zoneX*Room.kSize+4, zoneY*Room.kSize, 0) ); 

    // zone (0,2)
    
    zoneX = 0;
    zoneY = 2;
    spriteManager.addSprite( 
                 new BlockArray(kStreamBlocks, kBlockColours, 
                                zoneX*Room.kSize, zoneY*Room.kSize, -4) ); 
    addBasicZone(zoneX, zoneY, 
                 true, false, false, false, 
                 exits, spriteManager);

    spriteManager.addSprite(new Fence(zoneX*Room.kSize, 
                                      zoneY*Room.kSize,
                                      0, 6, Env.UP, 0));
    
    spriteManager.addSprite(new Liquid(zoneX*Room.kSize-5, 
                                       zoneY*Room.kSize+3, 
                                       -1, 0, kStreamPattern));
    
    spriteManager.addSprite(new Fence(zoneX*Room.kSize, 
                                      zoneY*Room.kSize+6,
                                      -4, 3, Env.UP, 1));

    if ( !mSwitchDone ) {
      spriteManager.addSprite(new FloorSwitch(zoneX*Room.kSize+4, 
                                              zoneY*Room.kSize+7,
                                              -4, "#0", "##"));
      mHiddenDoorWall = (Wall)spriteManager.findSpriteOfType(WallRight.class);
    }
    
    // zone (-1,3)
    
    zoneX = -1;
    zoneY = 3;
    spriteManager.addSprite( 
                 new BlockArray(kBlocks, kBlockColours, 
                                zoneX*Room.kSize, zoneY*Room.kSize, 0) ); 

    // zone (0,3)
    
    zoneX = 0;
    zoneY = 3;
    spriteManager.addSprite( 
                 new BlockArray(kBlocks, kBlockColours, 
                                zoneX*Room.kSize, zoneY*Room.kSize, 0) ); 
    addBasicZone(zoneX, zoneY, 
                 true, false, false, false, 
                 kExits, spriteManager);
    addTrees(zoneX, zoneY, 0, 1, spriteManager);

    spriteManager.addSprite(new Fence(zoneX*Room.kSize, 
                                      zoneY*Room.kSize-1,
                                      0, 5, Env.UP, 0));    
    spriteManager.addSprite(new Fence(zoneX*Room.kSize, 
                                      zoneY*Room.kSize+5,
                                      0, 5, Env.UP, 0));
    
    FenceGate gate = new FenceGate(zoneX*Room.kSize, 
                                   zoneY*Room.kSize+3, 
                                   0, Env.UP, 0);
    spriteManager.addSprite(gate);
    gate.setClosed(true);
    
    // other stuff
    
    RoomB02 roomInside = (RoomB02)findRoom(RoomB02.NAME);
    assert( roomInside != null );
    if ( !roomInside.doorsUnlocked() ) {
      kExits[0].mDoor.setClosed(true);
    }

  } // Room.createSprites()
  
  // returns true if the room is frozen (e.g., during a cut-scene)
  @Override
  public boolean paused() { return false; }

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
    
    // raise the hidden door
    
    if ( mHiddenDoor != null ) {
      raiseHiddenDoor();
    }
    
    // process the story event list

    int newEntryPoint = -1;
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();

      if ( event instanceof FloorSwitch.EventStateChange ) {
        assert( !mSwitchDone );
        FloorSwitch s = ((FloorSwitch.EventStateChange)event).mSwitch;
        s.freezeState(true);
        Env.sounds().play(Sounds.SWITCH_ON);
        mSwitchDone = true;
        newEntryPoint = 3;
        assert( mHiddenDoor == null );
        raiseHiddenDoor();
        it.remove();
      }

    } // for (event)
    if ( newEntryPoint >= 0 ) {
      storyEvents.add(new EventNewEntryPoint(newEntryPoint));
    }

    // close the entrance gate behind the player
    
    if ( mEntranceGateTimer > 0 ) {
      if ( --mEntranceGateTimer == 0 ) {
        mEntranceGate.setClosed(true);
        Env.sounds().play(Sounds.GATE);
      }
    }

  } // Room.advance()

  // create the hidden door or shift it into position 
  private void raiseHiddenDoor() {

    assert( mSwitchDone );
    assert( mHiddenDoorWall != null );
    
    int zPos;
    if ( mHiddenDoor == null ) {
      zPos = -8;
    } else {
      if ( --mHiddenDoorTimer > 0 ) return;
      zPos = mHiddenDoor.zPos() + 1;
      mHiddenDoorWall.removeDoor(mHiddenDoor);
    }
    
    mHiddenDoor = mHiddenDoorWall.addDoor(kExits[1].mDoorXYPos, zPos,
                                          kExits[1].mFloorColour, 
                                          kExits[1].mFloorDrop);

    if ( zPos == kExits[1].mDoorZPos ) {
      mHiddenDoor = null;
      Env.sounds().play(Sounds.SUCCESS);
    }
    
    mHiddenDoorTimer = kTimeHiddenDoor;
    
  } // raiseHiddenDoor()
  
} // class RoomB01
