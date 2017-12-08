/*
 *  RoomD05.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.FloorSwitch;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.Spikes;
import com.dishmoth.miniquests.game.Splatter;
import com.dishmoth.miniquests.game.Sprite;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.Statue;
import com.dishmoth.miniquests.game.StoryEvent;

// the room "D05"
public class RoomD05 extends Room {

  // unique identifier for this room
  public static final String NAME = "D05";
  
  // blocks for the floor
  private static final String kBlocks[][] = { { "2222222222",
                                                "2222222222",
                                                "2222222222",
                                                "2222222222",
                                                "2222222222",
                                                "2222222222",
                                                "2222222222",
                                                "2222222222",
                                                "2222222222",
                                                "2222222222" },
  
                                              { "0     0000",
                                                "          ",
                                                "          ",
                                                "         0",
                                                "0        0",
                                                "         0",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "0     0000" },
                                                
                                              { "0     0000",
                                                "          ",
                                                "          ",
                                                "         0",
                                                "0        0",
                                                "         0",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "0     0000" },
                                                
                                              { "0     0000",
                                                "          ",
                                                "          ",
                                                "         0",
                                                "0        0",
                                                "         0",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "0     0000" } };
                                              
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "lD",    // purple
                                                  "#D",    // purple
                                                  "ue", }; // dark
  
  // details of exit/entry points for the room 
  private static final Exit kExits[][]
        = { { new Exit(Env.RIGHT, 5,6, "#D",0, 0, RoomD13.NAME, 0),
              new Exit(Env.UP,    8,6, "#D",1, 0, "",0),
              new Exit(Env.DOWN,  8,6, "lD",0, 0, "",0) },
              
            { new Exit(Env.RIGHT, 5,6, "#D",0, 0, "",0),
              new Exit(Env.UP,    8,6, "#D",1, 0, RoomD16.NAME, 0),
              new Exit(Env.DOWN,  8,6, "lD",0, 0, RoomD15.NAME, 1) },
              
            { new Exit(Env.RIGHT, 5,6, "#D",0, 0, RoomD18.NAME, 0),
              new Exit(Env.UP,    8,6, "#D",1, 0, "",0),
              new Exit(Env.DOWN,  8,6, "lD",0, 0, "",0) },
              
            { new Exit(Env.RIGHT, 5,6, "#D",0, 0, RoomD02.NAME, 1),
              new Exit(Env.UP,    8,6, "#D",1, 0, RoomD09.NAME, 1),
              new Exit(Env.DOWN,  8,6, "lD",0, 0, "",0) } };
              
  // details of different camera height levels
  private static final CameraLevel kCameraLevels[]
                                     = { new CameraLevel(4, -100, +100) };

  // {floor start x, y, direc, statue direc 1, 2, 3}
  private static final int kModes[][] = { { 10, 1, Env.LEFT, 
                                            Env.RIGHT, Env.UP, Env.RIGHT }, 
                                          { -3, 4, Env.RIGHT, 
                                            Env.RIGHT, Env.UP, Env.RIGHT },
                                          { 7, 10, Env.DOWN, 
                                            Env.UP, Env.RIGHT, Env.DOWN } };
  
  // rate at which the floor tiles move
  private static final int kFloorMoveTime  = 8,
                           kFloorStartTime = 20;

  // size of the floor tile
  private static final int kTileSize = 3;

  // colour change time for statues
  private static final int kStatueFlashDelay = 5;
  
  // the current exits, based on room D02's twist
  private Exit mExits[];

  // ticks until the next change
  private int mTimer;

  // current position of bottom-left corner of floor tile
  private int mFloorXPos,
              mFloorYPos;

  // current floor movement direction
  private int mFloorDirec;

  // start position and direction for the floor tile (or -1)
  private int mFloorStartXPos,
              mFloorStartYPos,
              mFloorStartDirec;
  
  // floating floor tile
  private BlockArray mFloorBlocks;

  // references to statues
  private Statue mStatues[];

  // times until the statue colours turn back
  private int mStatueTimers[];

  // constructor
  public RoomD05() {

    super(NAME);
    
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
    
    spriteManager.addSprite( new BlockArray(kBlocks,
                                            kBlockColours, 
                                            0,0,0) );
    
    addBasicWalls(mExits, spriteManager);
    
    mStatues = new Statue[3];
    mStatueTimers = new int[ mStatues.length ];
   
    mFloorStartXPos = mFloorStartYPos = -3;
    mFloorStartDirec = -1;
    
    mFloorXPos = mFloorYPos = -3;
    mFloorDirec = -1;    
    mTimer = kFloorStartTime;
    
    spriteManager.addSprite( new FloorSwitch(6,0,6, "le", "lD") );
    spriteManager.addSprite( new FloorSwitch(9,6,6, "le", "lD") );
    spriteManager.addSprite( new FloorSwitch(6,9,6, "le", "lD") );
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mFloorBlocks = null;
    mStatues = null;
    
  } // Room.discardResources()
  
  // construct blocks for the current floor state
  private void buildFloor(SpriteManager spriteManager) {
    
    int x0 = Math.max(0, mFloorXPos),
        x1 = Math.min(Room.kSize-1, mFloorXPos+kTileSize-1),
        y0 = Math.max(0, mFloorYPos),
        y1 = Math.min(Room.kSize-1, mFloorYPos+kTileSize-1);
    
    int xSize = x1 - x0 + 1,
        ySize = y1 - y0 + 1;
    
    if ( mFloorBlocks != null ) {
      spriteManager.removeSprite(mFloorBlocks);
      mFloorBlocks = null;
    }
    if ( xSize == 0 || ySize == 0 ) {
      return;
    }
    
    final String blockRows[] = { "1", "11", "111" };
    String row = blockRows[xSize-1];
    
    String blocks[] = new String[ySize];
    for ( int k = 0 ; k < blocks.length ; k++ ) blocks[k] = row;
    
    mFloorBlocks = new BlockArray(new String[][]{blocks}, kBlockColours, 
                                  x0, y0, 4);
    spriteManager.addSprite(mFloorBlocks);
    
  } // buildFloor()

  // move the floor tiles
  private void updateFloor(SpriteManager spriteManager) {

    if ( mFloorStartDirec == -1 ) return;
    
    if ( mTimer > 0 ) {
      mTimer--;
      return;
    } else {
      mTimer = kFloorMoveTime;
    }

    int playerDirec = -1;
    if ( mPlayer != null && mPlayer.getZPos() == 4 ) {
      int dx = mPlayer.getXPos() - mFloorXPos,
          dy = mPlayer.getYPos() - mFloorYPos;
      if ( dx >= 0 && dx < kTileSize && dy >= 0 && dy < kTileSize ) {
        playerDirec = mFloorDirec;
      }
    }

    if ( mFloorDirec >= 0 ) {
      mFloorXPos += Env.STEP_X[ mFloorDirec ];
      mFloorYPos += Env.STEP_Y[ mFloorDirec ];
    }
    
    if ( mFloorXPos < -2 || mFloorXPos >= Room.kSize ||
         mFloorYPos < -2 || mFloorYPos >= Room.kSize ) {
      mFloorXPos  = mFloorStartXPos;
      mFloorYPos  = mFloorStartYPos;
      mFloorDirec = mFloorStartDirec;
    }
    
    buildFloor(spriteManager);
    
    if ( playerDirec != -1 ) mPlayer.slidePos(playerDirec, 1);
    
  } // updateFloor()
  
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
    
    // check the switches
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();
      if ( event instanceof FloorSwitch.EventStateChange ) {
        FloorSwitch sw = ((FloorSwitch.EventStateChange)event).mSwitch;
        int mode = sw.getYPos()/4; 
        while ( true ) {
          Sprite s = spriteManager.findSpriteOfType(FloorSwitch.class);
          if ( s == null ) break;
          spriteManager.removeSprite(s);
        }
        Env.sounds().play(Sounds.SWITCH_ON);
        mFloorStartXPos  = kModes[mode][0];
        mFloorStartYPos  = kModes[mode][1];
        mFloorStartDirec = kModes[mode][2];
        for ( int k = 0 ; k < mStatues.length ; k++ ) {
          int direc = kModes[mode][k+3];
          if ( direc == -1 ) continue;
          int y = (k==0) ? 0 
                : (k==1) ? 5 
                         : 9;
          mStatues[k] = new Statue(0,y,6, direc, 3);
        }
        for ( Statue s : mStatues ) {
          if ( s == null ) continue;
          spriteManager.addSprite(s);
          spriteManager.addSprite(
             new Splatter(s.getXPos(), s.getYPos(), s.getZPos(),
                          -1, 4, (byte)55, -1));
        }
        it.remove();
      }
    }
    
    // animate the floor
    updateFloor(spriteManager);

    // check the statues
    for ( int k = 0 ; k < mStatues.length ; k++ ) {
      if ( mStatues[k] != null ) {
        if ( mStatues[k].isHit() ) {
          mStatues[k].setColour(2);
          mStatues[k].setHit(false);
          mStatueTimers[k] = kStatueFlashDelay;
          mFloorDirec = mStatues[k].getDirec();
        } else {
          if ( mStatueTimers[k] > 0 ) {
            if ( --mStatueTimers[k] == 0 ) mStatues[k].setColour(3);
          }
        }
      }
    }
    
    // trigger spikes on floor
    if ( mPlayer != null && mPlayer.getZPos() == 0 && !mPlayer.isActing() ) {
      Spikes sp = (Spikes)spriteManager.findSpriteOfType(Spikes.class);
      if ( sp == null || !sp.active() ) {
        if ( sp != null ) spriteManager.removeSprite(sp);
        int x0 = Math.max(0, mPlayer.getXPos()-2),
            x1 = Math.min(Room.kSize-1, mPlayer.getXPos()+2),
            y0 = Math.max(0, mPlayer.getYPos()-2),
            y1 = Math.min(Room.kSize-1, mPlayer.getYPos()+2);
        sp = new Spikes(x0,y0,0, x1-x0+1,y1-y0+1, false, "u0");
        sp.setSilent(true);
        spriteManager.addSprite(sp);
        sp.trigger();
        Env.sounds().play(Sounds.SPIKES);
      }
    }
    
  } // Room.advance()

} // class RoomD05
