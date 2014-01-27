/*
 *  RoomC02.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.WallSwitch;

// the room "C02"
public class RoomC02 extends Room {

  // the basic blocks for the room
  private static final String kBlocks[][] = { { "0         ",
                                                "0         ",
                                                "0         ",
                                                "0         ",
                                                "0000      ",
                                                "0000    00",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000" },
                                                
                                              { "0         ",
                                                "0         ",
                                                "0         ",
                                                "0         ",
                                                "0000      ",
                                                "0000    00",
                                                "  00000000",
                                                "  00000000",
                                                "  00000000",
                                                "    000000" },
                                                
                                              { "0         ",
                                                "0         ",
                                                "0  000000 ",
                                                "0  0    0 ",
                                                "0000    0 ",
                                                "        00",
                                                "         0",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "0         ",
                                                "0         ",
                                                "0  000000 ",
                                                "0  0    0 ",
                                                "0000    0 ",
                                                "        00",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "0         ",
                                                "0         ",
                                                "0  000000 ",
                                                "0  0    0 ",
                                                "0000    0 ",
                                                "        00",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "0         ",
                                                "0         ",
                                                "0  000000 ",
                                                "0  0    0 ",
                                                "0000    0 ",
                                                "        00",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "0         ",
                                                "0         ",
                                                "0  000000 ",
                                                "0  0    0 ",
                                                "0000    0 ",
                                                "        00",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "0         ",
                                                "0         ",
                                                "0  000000 ",
                                                "0  0    0 ",
                                                "0000    0 ",
                                                "        00",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "0         ",
                                                "0         ",
                                                "00 000000 ",
                                                "00 0    0 ",
                                                "0000    0 ",
                                                "        00",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                " 000    00",
                                                " 000    00",
                                                "        00",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "   0000000",
                                                "    000000",
                                                "         0",
                                                "         0",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "        00",
                                                "         0",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#k" }; // orange
  
  // the blocks for the room's lift
  private static final String kLiftBlocks[][] 
                                  = { { "0   ", "0   ", "0   ", "0000" },
                                      { "0   ", "0   ", "0   ", "0000" },
                                      { "0   ", "0   ", "0   ", "0000" },
                                      { "0   ", "0   ", "0   ", "0000" },
                                      { "0   ", "0   ", "0   ", "0000" },
                                      { "0   ", "0   ", "0   ", "0000" },
                                      { "0000", "0000", "0000", "0000" } };
  
  // different lift block colours (corresponding to '0', '1', '2', etc)
  private static final String kLiftColours1[] = { "5L" }, // dark purple
                              kLiftColours2[] = { "lL" }; // light purple
  
  // details of exit/entry points for the room
  private static final Exit kExits[] 
          = { new Exit(Env.UP,   5,20, "#k",0, 1, RoomC01.class, 1),
              new Exit(Env.LEFT, 2, 0, "#k",0, 0, RoomC03.class, 0) }; 
  
  // details of different camera height levels
  private static final CameraLevel kCameraLevels[]
                                     = { new CameraLevel( 0,-100,  15),
                                         new CameraLevel(18,   5,+100) };
  
  // parameters controlling for lift
  private static final int kLiftZMin  = -8,
                           kLiftZMax  = 4;
  private static final int kLiftDelay = 4;
  
  // reference to the lift blocks (going down and coming up)
  private BlockArray mLift1,
                     mLift2;
  
  // which direction the lift is moving in (+1 up, -1 down, 0 stopped)
  private int mLiftDirec;
  
  // how long until the lift moves again
  private int mLiftTimer;

  // switches for lift
  private WallSwitch mSwitchHigh,
                     mSwitchLow;
  
  // constructor
  public RoomC02() {

  } // constructor

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

    mLift1 = mLift2 = null;
    
  } // Room.discardResources()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {

    addBasicWalls(kExits, spriteManager);

    spriteManager.addSprite(new BlockArray(kBlocks, kBlockColours, 0,0,0));

    mLift1 = new BlockArray(kLiftBlocks, kLiftColours1, 4,3,kLiftZMin);
    spriteManager.addSprite(mLift1);

    mLift2 = new BlockArray(kLiftBlocks, kLiftColours2, 4,3,kLiftZMin);
    spriteManager.addSprite(mLift2);
    mLift2.mDrawDisabled = true;

    mLiftDirec = 0;
    mLiftTimer = 0;

    mSwitchHigh = new WallSwitch(Env.UP, 1, 20, 
                                 new String[]{"57","l7"}, true);
    mSwitchLow = new WallSwitch(Env.RIGHT, 1, 4, 
                                new String[]{"5u","lu"}, true);
    spriteManager.addSprite(mSwitchHigh);
    spriteManager.addSprite(mSwitchLow);
    
  } // Room.createSprites()
  
  // update the room (events may be added or processed)
  @Override
  public void advance(LinkedList<StoryEvent> storyEvents,
                      SpriteManager          spriteManager) {

    // check the wall switches
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();
      if ( event instanceof WallSwitch.EventStateChange ) {
        WallSwitch switchA = ((WallSwitch.EventStateChange)event).mSwitch;
        WallSwitch switchB = (switchA==mSwitchLow) ? mSwitchHigh : mSwitchLow;
        switchB.setState( switchA.getState() );
        it.remove();
      }
    }

    // check exits
    final int exitIndex = checkExits(kExits);
    if ( exitIndex != -1 ) {
      storyEvents.add(new EventRoomChange(kExits[exitIndex].mDestination,
                                          kExits[exitIndex].mEntryPoint));
      return;
    }

    // check camera level
    EventRoomScroll scroll = checkVerticalScroll(kCameraLevels);
    if ( scroll != null ) storyEvents.add(scroll);

    // animate the lift
    boolean stopAtTop = (mSwitchHigh.getState() == 1);
    assert( mLiftDirec >= -1 && mLiftDirec <= +1 );
    if ( ( stopAtTop && mLift1.getZPos() < kLiftZMax) ||
         (!stopAtTop && mLift1.getZPos() > kLiftZMin) ) {
      if ( --mLiftTimer <= 0 ) {
        mLiftTimer = kLiftDelay;
        mLift1.shiftPos(0, 0, mLiftDirec);
        mLift2.shiftPos(0, 0, mLiftDirec);
        if ( mLift1.getZPos() == kLiftZMax ) {
          if ( mLiftDirec > 0 ) Env.sounds().play(Sounds.SWITCH_OFF);
          mLiftDirec = (stopAtTop ? 0 : mLiftDirec-1);
        }
        if ( mLift1.getZPos() == kLiftZMin ) {
          if ( mLiftDirec < 0 ) Env.sounds().play(Sounds.SWITCH_OFF);
          mLiftDirec = (stopAtTop ? mLiftDirec+1 : 0);
        }
        if ( mLiftDirec == 0 ) mLiftTimer *= 2;
      }
    } else {
      mLiftDirec = 0;
      mLiftTimer = 1;
    }
    if ( stopAtTop ) {
      mLift1.mDrawDisabled = true;
      mLift2.mDrawDisabled = false;
    } else {
      mLift1.mDrawDisabled = false;
      mLift2.mDrawDisabled = true;
    }
    
  } // Room.advance()

} // class RoomC02
