/*
 *  RoomE08.java
 *  Copyright (c) 2018 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Critter;
import com.dishmoth.miniquests.game.CritterTrack;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.FlameArea;
import com.dishmoth.miniquests.game.Liquid;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.WallSwitch;

// the room "E08"
public class RoomE08 extends Room {

  // unique identifier for this room
  public static final String NAME = "E08";
  
  // times for things to happen
  private static final int kFlameOffTime = 150,
                           kTickTime     = 8;
  
  // main blocks for the room
  private static final String kBlocks[][] = { { "00000000  ",
                                                "0      0  ",
                                                "0      0  ",
                                                "11111  0 0",
                                                "1   1  0 0",
                                                "1   1  000",
                                                "1   1     ",
                                                "11111    0",
                                                "  0      0",
                                                "  0000   0" },
                                              
                                              { "          ",
                                                "          ",
                                                "          ",
                                                "         0",
                                                "         0",
                                                "        00",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "    00    " },
                                              
                                              { "          ",
                                                "          ",
                                                "          ",
                                                "         0",
                                                "         0",
                                                "         0",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "     0    " } };
                                              
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "NY",   // blue-green
                                                  "#Y" }; // white-green
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.RIGHT, 6,4, "#Y",0, -1, RoomE04.NAME, 2),
              new Exit(Env.DOWN,  5,4, "NY",0, -1, RoomE09.NAME, 0) };

  // regions covered by flames
  private static final String kFlamePattern[] = { "........OO",
                                                  ".OOOOOO.OO",
                                                  ".OOOOOO.OO",
                                                  ".....OO.O ",
                                                  ".OOO.OO.O ",
                                                  ".OOO.OO.  ",
                                                  ".OOO.OOOOO",
                                                  ".....OOOO ",
                                                  "OO.OOOOOO ",
                                                  "OO..  OOO " };
  
  // details of the paths followed by enemies
  private static final CritterTrack kCritterTrack
                          = new CritterTrack(new String[]{ "+++++",
                                                           "+   +",
                                                           "+   +",
                                                           "+   +",
                                                           "+++++"}, 0, 2);

  // references to some objects
  private Liquid     mLiquid;
  private FlameArea  mFlames;
  private WallSwitch mSwitches[];
  
  // true when the room is complete (no more flames)
  private boolean mDone;
  
  // countdown until the flames come back on
  private int mTimer;
  
  // constructor
  public RoomE08() {

    super(NAME);

    mDone = false;
    
  } // constructor

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
    
    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,0) );
    
    addBasicWalls(kExits, spriteManager);

    mLiquid = new Liquid(0,0,-2, 2);
    spriteManager.addSprite(mLiquid);

    if ( !mDone ) {
      mFlames = new FlameArea(0, 0, -2, kFlamePattern);
      mFlames.setFlame(true);
      mFlames.warmUp();
      spriteManager.addSprite(mFlames);
    }
  
    mSwitches = new WallSwitch[] {
                        new WallSwitch(Env.RIGHT, 4, 6,
                                       new String[]{"Du","qu","7u"}, false),
                        new WallSwitch(Env.UP, 4, 2,
                                       new String[]{"D7","q7","u7"}, false) };
    for ( WallSwitch s : mSwitches ) {
      s.setState(mDone ? 2 : 0);
      spriteManager.addSprite(s);
    }
    
    Critter critters[] = new Critter[]{
                           new Critter(0, 4, 0, Env.UP, kCritterTrack),
                           new Critter(1, 6, 0, Env.RIGHT, kCritterTrack),
                           new Critter(4, 6, 0, Env.RIGHT, kCritterTrack),
                           new Critter(4, 3, 0, Env.DOWN, kCritterTrack) };
    for ( Critter c : critters ) {
      c.setColour(2);
      c.setStunTime(0);
      spriteManager.addSprite(c);
    }
    
    mTimer = 0;
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mLiquid = null;
    mFlames = null;
    mSwitches = null;
    
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

    // process the story event list
    
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();
      
      if ( event instanceof WallSwitch.EventStateChange ) {
        if ( !mDone ) {
          for ( WallSwitch s : mSwitches ) s.setState(1);
          mFlames.setFlame(false);
          mTimer = kFlameOffTime;
        }
        it.remove();
      }
      
    } // for (event)
    
    // count down the flame timer
    if ( !mDone && mTimer > 0 ) {
      mTimer--;
      if ( mTimer == 0 ) {
        for ( WallSwitch s : mSwitches ) s.setState(0);
        mFlames.setFlame(true);
        Env.sounds().play(Sounds.SWITCH_OFF);
      } else if ( mTimer % kTickTime == 0 ) {
        int i = mTimer / kTickTime;
        if ( i <= 6 ) {
          if ( i % 2 == 0 ) {
            for ( WallSwitch s : mSwitches ) s.setState(1);
            Env.sounds().play(Sounds.TICK);
          } else {
            for ( WallSwitch s : mSwitches ) s.setState(0);
            Env.sounds().play(Sounds.TOCK);
          }
        } else if ( i <= 14 ){
          int j = i - 6;
          if ( j % 4 == 0 ) {
            Env.sounds().play(Sounds.TICK);
          } else if ( j % 4 == 2 ) {
            Env.sounds().play(Sounds.TOCK);
          }
        }
      }
    }
    
    // check for complete
    if ( !mDone && mPlayer != null &&
         mPlayer.getXPos() == 4 && mPlayer.getYPos() == 0 ) {
      mDone = true;
      for ( WallSwitch s : mSwitches ) s.setState(2);
      mFlames.setFlame(false);
      Env.sounds().play(Sounds.SUCCESS);
    }
    
  } // Room.advance()

} // class RoomE08
