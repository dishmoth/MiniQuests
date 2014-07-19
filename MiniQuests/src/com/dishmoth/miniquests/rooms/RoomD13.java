/*
 *  RoomD13.java
 *  Copyright Simon Hern 2014
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.BlockPattern;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Spikes;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.WallSwitch;

// the room "D13"
public class RoomD13 extends Room {

  // unique identifier for this room
  public static final String NAME = "D13";
  
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
                                                
                                              { "1        1",
                                                "         1",
                                                "         1",
                                                "         1",
                                                "         1",
                                                "         1",
                                                "         1",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "1        1",
                                                "         1",
                                                "         1",
                                                "         1",
                                                "         1",
                                                "         1",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "1        1",
                                                "         1",
                                                "         1",
                                                "         1",
                                                "         1",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "1        1",
                                                "         1",
                                                "         1",
                                                "         1",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "1        1",
                                                "         1",
                                                "         1",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "1        1",
                                                "1        1",
                                                "1         ",
                                                "1         ",
                                                "1         ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " } };

  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "kK",
                                                  "#k" }; 
  
  // details of exit/entry points for the room 
  private static final Exit kExits[][] 
          = { { new Exit(Env.LEFT,  5, 0, "kK",0, 0, RoomD05.NAME, 0),
                new Exit(Env.RIGHT, 1, 0, "#K",0, 0, RoomD14.NAME, 0),
                new Exit(Env.LEFT,  5,12, "#k",1, 1, "",0) },
  
              { new Exit(Env.LEFT,  5, 0, "kK",0, 0, RoomD04.NAME, 0),
                new Exit(Env.RIGHT, 1, 0, "#K",0, 0, RoomD14.NAME, 0),
                new Exit(Env.LEFT,  5,12, "#k",1, 1, RoomD04.NAME, 5) },
              
              { new Exit(Env.LEFT,  5, 0, "kK",0, 0, RoomD03.NAME, 3),
                new Exit(Env.RIGHT, 1, 0, "#K",0, 0, RoomD14.NAME, 0),
                new Exit(Env.LEFT,  5,12, "#k",1, 1, "",0) },
              
              { new Exit(Env.LEFT,  5, 0, "kK",0, 0, "",0),
                new Exit(Env.RIGHT, 1, 0, "#K",0, 0, RoomD14.NAME, 0),
                new Exit(Env.LEFT,  5,12, "#k",1, 1, "",0) } };
  
  // details of different camera height levels
  private static final CameraLevel kCameraLevels[]
                                     = { new CameraLevel( 0, -100,    8),
                                         new CameraLevel(10,    2, +100) };

  // moving paths
  private static final String kPath1[] = { "          ",
                                           "          ",
                                           "          ",
                                           "          ",
                                           "          ",
                                           "0FEDC     ",
                                           "1   B     ",
                                           "2   A     ",
                                           "3   9     ",
                                           "45678     " };
                                           
  private static final String kPath2[] = { "    43210 ",
                                           "    5   F ",
                                           "    6   E ",
                                           "    7   D ",
                                           "    89ABC ",
                                           "          ",
                                           "          ",
                                           "          ",
                                           "          ",
                                           "          " };
  
  // parameters for the moving paths
  private static final int kPathDelay  = 10,
                           kPathLength = 9,
                           kPathPeriod = 16;
        
  // the current exits, based on room D02's twist
  private Exit mExits[];
  
  // spikes across the floor
  private Spikes mSpikes;

  // delay until the next path, or -1 if not active
  private int mPathTimer;
  
  // constructor
  public RoomD13() {

    super(NAME);

  } // constructor

  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < mExits.length );
    setPlayerAtExit(mExits[entryPoint], kCameraLevels);
    
    mPlayer.setFatalFallDistance(10);
    
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
    
    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,0) );
    
    addBasicWalls(mExits, spriteManager);

    mSpikes = new Spikes(4,0,0, 3,10, false,"W0");
    spriteManager.addSprite(mSpikes);
  
    WallSwitch ws = new WallSwitch(Env.RIGHT, 8,14, 
                                   new String[]{"ou","7u"}, false);
    spriteManager.addSprite(ws);
    
    mPathTimer = -1;
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mSpikes = null;
    
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
      return;
    }

    // check camera level
    if ( mPlayer != null && !mPlayer.isFalling() ) {
      EventRoomScroll scroll = checkVerticalScroll(kCameraLevels);
      if ( scroll != null ) storyEvents.add(scroll);
    }

    // check the switch
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();
      if ( event instanceof WallSwitch.EventStateChange ) {
        assert( mPathTimer == -1 );
        mPathTimer = 0;
        it.remove();
      }
    }

    // set the paths moving
    if ( mPathTimer >= 0 ) {
      if ( --mPathTimer < 0 ) {
        BlockPattern paths[] = { new BlockPattern(kPath1, "Zk", 0, 0, 12),
                                 new BlockPattern(kPath2, "Lk", 0, 0, 12) };
        for ( BlockPattern path : paths ) {
          path.setRange( path.minValue()-kPathLength, path.minValue()-1 );
          path.setStartRate(+kPathDelay);
          path.setEndRate(+kPathDelay);
          spriteManager.addSprite(path);
        }
        mPathTimer = kPathDelay*kPathPeriod - 1;
      }
    }
    
    // trigger the spikes
    if ( mPlayer != null && mPlayer.getZPos() == 0 &&
         mPlayer.getXPos() >= mSpikes.getXPos() &&
         mPlayer.getXPos() < mSpikes.getXPos()+mSpikes.getXSize() ) {
      mSpikes.trigger();
    }
    
  } // Room.advance()

} // class RoomD13
