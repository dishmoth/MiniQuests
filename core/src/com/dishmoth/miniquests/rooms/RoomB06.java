/*
 *  RoomB06.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;

// the room "B06"
public class RoomB06 extends Room {

  // unique identifier for this room
  public static final String NAME = "B06";
  
  // the basic blocks for the room
  private static final String kBlocks[][] = { { "0000     0",
                                                "         0",
                                                "         0",
                                                "         0",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " } };

  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "Es",   // yellow
                                                  "#w",   // green
                                                  "#z",   // pink
                                                  "#x",   // blue
                                                  "#c" }; // orange
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.UP,    2,2, "#s",1, -1, RoomB05.NAME, 1), 
              new Exit(Env.RIGHT, 6,2, "#s",1, -1, RoomB03.NAME, 3) };

  // width of a tile square
  private static final int kSquareSize = 4,
                           kScrollSize = 12;

  // different patterns of movement for the squares
  enum MoveMode { HORIZONTAL, VERTICAL, REVERSE_VERTICAL }

  // repeated pattern for square movement
  private static final MoveMode kMovePattern[] = { MoveMode.HORIZONTAL, 
                                                   MoveMode.VERTICAL,
                                                   MoveMode.HORIZONTAL, 
                                                   MoveMode.REVERSE_VERTICAL,
                                                   MoveMode.VERTICAL,
                                                   MoveMode.HORIZONTAL, 
                                                   MoveMode.VERTICAL,
                                                   MoveMode.HORIZONTAL };
  
  // rate at which the floor tiles move
  private static final int kUpdateTime = 3;
  
  // delay between square movements
  private static final int kPauseTime = 30;
  
  // reference to the floor blocks
  private BlockArray mFloorBlocks;

  // current positions of the main block squares
  private int mSquarePos[][];

  // ticks until the next change
  private int mTimer;

  // which type of floor animation is playing
  private MoveMode mMoveMode;

  // current stage in the movement pattern
  private int mMoveIndex;
  
  // constructor
  public RoomB06() {

    super(NAME);

  } // constructor

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

    mFloorBlocks = null;
    
  } // Room.discardResources()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {

    spriteManager.addSprite(new BlockArray(kBlocks, kBlockColours, 0, 0, 2));
    addBasicWalls(kExits, spriteManager);

    mSquarePos = new int[][]{ {0, 0}, {6, 0}, {0, 6}, {6, 6} };
    buildFloor(spriteManager);

    mMoveIndex = 0;
    mMoveMode = kMovePattern[mMoveIndex];
    mTimer = kPauseTime;
    
  } // Room.createSprites()

  // construct blocks for the current floor state
  private void buildFloor(SpriteManager spriteManager) {

    StringBuilder tiles[] = new StringBuilder[Room.kSize];
    for ( int k = 0 ; k < tiles.length ; k++ ) {
      tiles[k] = new StringBuilder("          ");
    }

    for ( int sq = 0 ; sq < mSquarePos.length ; sq++ ) {
      int x = mSquarePos[sq][0],
          y = mSquarePos[sq][1];
      char col = (char)('1' + sq);
      for ( int iy = y ; iy < y+kSquareSize ; iy++ ) {
        int ia = Env.fold(iy, kScrollSize);
        if ( ia >= Room.kSize ) continue;
        for ( int ix = x ; ix < x+kSquareSize ; ix++ ) {
          int ib = Env.fold(ix, kScrollSize);
          if ( ib >= Room.kSize ) continue;
          tiles[Room.kSize-1-ia].setCharAt(ib, col);
        }
      }
    }
    
    String colours[] = new String[tiles.length];
    for ( int k = 0 ; k < tiles.length ; k++ ) {
      colours[k] = tiles[k].toString();
    }
    
    if ( mFloorBlocks != null ) spriteManager.removeSprite(mFloorBlocks);
    mFloorBlocks = new BlockArray(new String[][]{colours}, kBlockColours, 
                                  0, 0, 0);
    spriteManager.addSprite(mFloorBlocks);
    
  } // buildFloor()

  // move the floor tiles
  private void updateFloor(SpriteManager spriteManager) {

    if ( mTimer > 0 ) {
      mTimer--;
      return;
    } else {
      mTimer = kUpdateTime;
    }

    int playerSquare = -1;
    int playerDirec = -1;
    if ( mPlayer != null && mPlayer.getZPos() == 0 ) {
      int px = mPlayer.getXPos(),
          py = mPlayer.getYPos();
      for ( int sqIndex = 0 ; sqIndex < mSquarePos.length ; sqIndex++ ) {
        int sx = mSquarePos[sqIndex][0],
            sy = mSquarePos[sqIndex][1];
        if ( px >= sx && px < sx+kSquareSize && 
             py >= sy && py < sy+kSquareSize ) {
          playerSquare = sqIndex;
          break;
        }
      }      
    }
    
    boolean done = true;
    boolean moveStart = true;
    switch ( mMoveMode ) {
    
      case HORIZONTAL: {
        for ( int sqIndex = 0 ; sqIndex < mSquarePos.length ; sqIndex++ ) {
          int x = mSquarePos[sqIndex][0],
              y = mSquarePos[sqIndex][1];
          assert( y == 0 || y == 6 );
          if ( x != 0 && x != 6 ) moveStart = false;
          x += ( (y==0) ? +1 : -1 );
          x = Env.fold(x, kScrollSize);
          mSquarePos[sqIndex][0] = x;
          if ( x != 0 && x != 6 ) done = false;
          if ( playerSquare == sqIndex ) {
            playerDirec = ( (y==0) ? Env.RIGHT : Env.LEFT );
          }
        }
      } break;
    
      case VERTICAL: {
        for ( int sqIndex = 0 ; sqIndex < mSquarePos.length ; sqIndex++ ) {
          int x = mSquarePos[sqIndex][0],
              y = mSquarePos[sqIndex][1];
          assert( x == 0 || x == 6 );
          if ( y != 0 && y != 6 ) moveStart = false;
          y += ( (x==0) ? -1 : +1 );
          y = Env.fold(y, kScrollSize);
          mSquarePos[sqIndex][1] = y;
          if ( y != 0 && y != 6 ) done = false;
          if ( playerSquare == sqIndex ) {
            playerDirec = ( (x==0) ? Env.DOWN : Env.UP );
          }
        }
      } break;
    
      case REVERSE_VERTICAL: {
        for ( int sqIndex = 0 ; sqIndex < mSquarePos.length ; sqIndex++ ) {
          int x = mSquarePos[sqIndex][0],
              y = mSquarePos[sqIndex][1];
          assert( x == 0 || x == 6 );
          if ( y != 0 && y != 6 ) moveStart = false;
          y += ( (x==0) ? +1 : -1 );
          y = Env.fold(y, kScrollSize);
          mSquarePos[sqIndex][1] = y;
          if ( y != 0 && y != 6 ) done = false;
          if ( playerSquare == sqIndex ) {
            playerDirec = ( (x==0) ? Env.UP : Env.DOWN );
          }
        }
      } break;
    
    }

    if ( moveStart ) Env.sounds().play(Sounds.GRIND);
    
    if ( done ) {
      mMoveIndex = (mMoveIndex+1) % kMovePattern.length;
      mMoveMode = kMovePattern[mMoveIndex];
      mTimer = kPauseTime;
    }
    
    buildFloor(spriteManager);

    if ( playerDirec != -1 ) mPlayer.slidePos(playerDirec, 1);
    
  } // updateFloor()
  
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

    // animate the floor
    updateFloor(spriteManager);
    
    // check whether the player is falling
    if ( mPlayer != null && mPlayer.getZPos() < -25 ) {
      mPlayer.destroy(-1);
    }
    
  } // Room.advance()

} // class RoomB06
