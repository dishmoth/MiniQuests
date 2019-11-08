/*
 *  RoomE09.java
 *  Copyright (c) 2018 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockStairs;
import com.dishmoth.miniquests.game.Brain;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Liquid;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;

// the room "E09"
public class RoomE09 extends Room {

  // unique identifier for this room
  public static final String NAME = "E09";
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "M6" }; // yellow
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.UP,    5,0, "#6",0, -1, RoomE08.NAME, 1),
              new Exit(Env.RIGHT, 5,4, "#6",0, -1, RoomE04.NAME, 1) };

  // stair heights for the different room states
  // for each position, hex value shows which of the incoming stairs are raised
  // (sum of: right=1, up=2, left=4, down=8)
  private static final String kStairStates[][] = { { "09C", // 0 (down-left)
                                                     "96A",
                                                     "002" },
                                                  
                                                   { "9DC", // 1 (down-mid)
                                                     "BF6",
                                                     "000" },
                                                  
                                                   { "000", // 2 (down-right)
                                                     "000",
                                                     "000" },
                                                  
                                                   { "000", // 3 (mid-left)
                                                     "AAA",
                                                     "222" },
                                                  
                                                   { "00C", // 4 (mid)
                                                     "00A",
                                                     "156" },
                                                  
                                                   { "000", // 5 (mid-right)
                                                     "004",
                                                     "000" },
                                                  
                                                   { "00C", // 6 (up-left)
                                                     "9C0",
                                                     "060" },
                                                  
                                                   { "000", // 7 (up-mid)
                                                     "004",
                                                     "000" },
                                                  
                                                   { "804", // 8 (up-right)
                                                     "000",
                                                     "102" } };
  
  // references to the stair objects
  private BlockStairs mStairsLR[][],
                      mStairsUD[][];
  
  // current stair state (0 to 8)
  private int mState;
  
  // constructor
  public RoomE09() {

    super(NAME);

  } // constructor

  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < kExits.length );
    setPlayerAtExit(kExits[entryPoint]);
    
    mState = ( entryPoint == 0 ? 7 : 5 ); 
    
    return mPlayer;
    
  } // createPlayer()
  
  // stair height for a particular state
  private int height(int x, int y, int dir, int state) {
    
    assert( x >= 0 && x <= 2 );
    assert( y >= 0 && y <= 2 );
    assert( state >= 0 && state < 9 );
    
    int mask = 0;
    switch (dir) {
      case Env.RIGHT: {
        assert( x < 2 );
        mask = 1;
      } break;
      case Env.UP: {
        assert( y < 2 );
        mask = 2;
      } break;
      case Env.LEFT: {
        assert( x > 0 );
        mask = 4;
      } break;
      case Env.DOWN: {
        assert( y > 0 );
        mask = 8;
      } break;
      default: {
        assert(false);
      }
    }

    final char ch = kStairStates[state][2-y].charAt(x);
    final int val = (ch >= 'a' && ch <= 'f') ? ((ch - 'a') + 10)
                  : (ch >= 'A' && ch <= 'F') ? ((ch - 'A') + 10)
                  : (ch >= '0' && ch <= '9') ? (ch - '0')
                                             : -1;
    assert( val >= 0 && val <= 15 );
    final boolean high = ((val & mask) != 0);
    return (high ? 4 : 0);
    
  } // height(x,y,dir,state)

  // current stair height
  private int height(int x, int y, int dir) {
    
    return height(x, y, dir, mState); 
    
  } // height(x,y,dir)
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {
    
    addBasicWalls(kExits, spriteManager);

    spriteManager.addSprite(new Liquid(0,0,-1, 2));

    mState = 7;
    
    mStairsLR = new BlockStairs[2][3];
    mStairsUD = new BlockStairs[3][2];

    for ( int ix = 0 ; ix < 3 ; ix++ ) {
      for ( int iy = 0 ; iy < 3 ; iy++ ) {
        int x = 4*ix + 1,
            y = 4*iy + 1;
        if ( ix < 2 ) {
          mStairsLR[ix][iy]
                  = new BlockStairs(x, y, height(ix, iy, Env.RIGHT),
                                    x+4, y, height(ix+1, iy, Env.LEFT),
                                    kBlockColours[0], 3);
          spriteManager.addSprite(mStairsLR[ix][iy]);
        }
        if ( iy < 2 ) {
          mStairsUD[ix][iy]
                  = new BlockStairs(x, y, height(ix, iy, Env.UP),
                                    x, y+4, height(ix, iy+1, Env.DOWN),
                                    kBlockColours[0], 3);
          spriteManager.addSprite(mStairsUD[ix][iy]);
        }
      }
    }
    
  } // Room.createSprites()
  
  // set the stair heights according to the current state
  private void updateStairs() {

    for ( int ix = 0 ; ix < 3 ; ix++ ) {
      for ( int iy = 0 ; iy < 3 ; iy++ ) {
        if ( ix < 2 ) {
          mStairsLR[ix][iy].setZStart(height(ix, iy, Env.RIGHT));
          mStairsLR[ix][iy].setZEnd(height(ix+1, iy, Env.LEFT));
        }
        if ( iy < 2 ) {
          mStairsUD[ix][iy].setZStart(height(ix, iy, Env.UP));
          mStairsUD[ix][iy].setZEnd(height(ix, iy+1, Env.DOWN));
        }
      }
    }
    
  } // updateStairs()
  
  // true if the player is about to take a step that shouldn't be possible
  private boolean badStep(int oldState, int newState, int x, int y, int dir) {

    if ( (dir == Env.RIGHT && x == 2) ||
         (dir == Env.UP    && y == 2) ||
         (dir == Env.LEFT  && x == 0) ||
         (dir == Env.DOWN  && y == 0) ) return false;
    
    final int oldHeight = height(x, y, dir, oldState),
              newHeight = height(x, y, dir, newState);
    
    int oldMax = oldHeight,
        newMax = newHeight;
    for ( int k = 1 ; k <= 3 ; k++ ) {
      final int d = (dir + k) % 4;
      if ( (d == Env.RIGHT && x == 2) ||
           (d == Env.UP    && y == 2) ||
           (d == Env.LEFT  && x == 0) ||
           (d == Env.DOWN  && y == 0) ) continue;
      oldMax = Math.max(oldMax, height(x, y, d, oldState));
      newMax = Math.max(newMax, height(x, y, d, newState));
    }
    
    final boolean oldPossible = (oldHeight == oldMax),
                  newPossible = (newHeight == newMax);
    return ( oldPossible && !newPossible );
    
  } // badStep()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mStairsLR = mStairsUD = null; 
    
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

    if ( mPlayer != null ) {
      int x = mPlayer.getXPos(),
          y = mPlayer.getYPos();
      if ( x%4 == 1 && y%4 == 1 ) {
        int ix = (x-1)/4,
            iy = (y-1)/4;
        int newState = 3*iy + ix;
        if ( newState != mState ) {
          Env.sounds().play(Sounds.SWITCH_ON);
          if ( badStep(mState, newState, ix, iy, mPlayer.getDirec()) ) {
            mPlayer.addBrain(new Brain.ZombieModule(new int[]{ Env.NONE,7 }));
          }
          mState = newState;
          updateStairs();
        }
      }
    }
    
  } // Room.advance()

} // class RoomE09
