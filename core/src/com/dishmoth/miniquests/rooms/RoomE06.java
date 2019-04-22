/*
 *  RoomE06.java
 *  Copyright (c) 2019 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Bullet;
import com.dishmoth.miniquests.game.EgaImage;
import com.dishmoth.miniquests.game.EgaTools;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Fence;
import com.dishmoth.miniquests.game.FenceGate;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.Sprite;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.WallSwitch;

// the room "E06"
public class RoomE06 extends Room {

  // unique identifier for this room
  public static final String NAME = "E06";
  
  // time for the cursor to change 
  private static final int kCursorMoveTime   = 20,
                           kCursorActiveTime = 30;
  
  // main blocks for the room
  private static final String kBlocks[][] = { { "0000001111",
                                                "0000001111",
                                                "0000001111",
                                                "0000001111",
                                                "0000001111",
                                                "0000001111",
                                                "0000001111",
                                                "0000001111",
                                                "0000001111",
                                                "0000001111" } };
                                              
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "Nn",   // purple-blue
                                                  "NZ" }; // blue-blue
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.UP,    8,0, "#Z",0, -1, RoomE02.NAME, 5),
              new Exit(Env.RIGHT, 6,0, "#Z",0, -1, RoomE07.NAME, 0),
              new Exit(Env.DOWN,  1,0, "Nn",0, -1, RoomE04.NAME, 4) };

  // pattern painted on part of the floor
  private EgaImage mPuzzleImage;
  
  // current puzzle configuration (3x3, tile colour indices)
  private int mPuzzle[][];
  
  // puzzle cursor position (0 to 3, clockwise from top-left) 
  private int mCursorPos;
  
  // true while the puzzle is being changed
  private boolean mCursorActive;
  
  // time until the next cursor behaviour change
  private int mCursorTimer;
  
  // keep track of the bullets so we know when one is fired
  private LinkedList<Bullet> mBullets,
                             mBulletsPrev;
  
  // whether the puzzle is complete yet
  private boolean mDone;
  
  // references to objects
  private BlockArray mFloor;
  private FenceGate  mGate;
  private WallSwitch mSwitches[];
  
  // constructor
  public RoomE06() {

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
    
    mFloor = new BlockArray(kBlocks, kBlockColours, 0,0,0);
    spriteManager.addSprite(mFloor);
    
    addBasicWalls(kExits, spriteManager);

    mGate = new FenceGate(5,1,0, Env.UP, 1);
    spriteManager.addSprite(mGate);
    if ( !mDone ) mGate.setClosed(true);
    
    spriteManager.addSprite(new Fence(5,0,0, 2, Env.UP, 1));
    spriteManager.addSprite(new Fence(5,3,0, 7, Env.UP, 1));

    mSwitches = new WallSwitch[] { 
              new WallSwitch(Env.UP, 1, 2, new String[]{"37","u7"}, false),
              new WallSwitch(Env.UP, 2, 2, new String[]{"b7","u7"}, false),
              new WallSwitch(Env.UP, 3, 2, new String[]{"c7","u7"}, false) };
    for ( WallSwitch s : mSwitches ) {
      if ( mDone ) s.setState(1);
      spriteManager.addSprite(s);
    }
    
    if ( mDone ) {
      mPuzzle = new int[][]{ {0,1,2}, {0,1,2}, {0,1,2} };
    } else {
      mPuzzle = new int[][]{ {0,1,0}, {1,2,2}, {0,2,1} };
    }
    
    mCursorPos = 0;
    mCursorActive = false;
    mCursorTimer = kCursorMoveTime;
    
    paintPuzzle();
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mPuzzleImage = null;
    mFloor = null;
    mGate = null;
    mSwitches = null;
    mBullets = mBulletsPrev = null;
    
  } // Room.discardResources()

  // repaint the puzzle image on the floor
  private void paintPuzzle() {

    if ( mPuzzleImage == null ) {
      final String pattern = "        77        "
                           + "      777777      "
                           + "    7777777777    "
                           + "  77777777777777  "
                           + "777777777777777777"
                           + "  77777777777777  "
                           + "    7777777777    "
                           + "      777777      "
                           + "        77        ";
      mPuzzleImage = new EgaImage(8, 8, 18, 9, pattern);
    }

    for ( int iy = 0 ; iy < 4 ; iy++ ) {
      for ( int ix = 0 ; ix < 4 ; ix++ ) {
        int x = 10 + 2*(iy + ix),
            y = 12 + (iy - ix);
        mPuzzleImage.plot(x, y, (byte)7);
        mPuzzleImage.plot(x+1, y, (byte)7);
      }
    }
    
    final byte colours[] = { EgaTools.decodePixel('3'),
                             EgaTools.decodePixel('b'),
                             EgaTools.decodePixel('c') };
    for ( int iy = 0 ; iy < mPuzzle.length ; iy++ ) {
      for ( int ix = 0 ; ix < mPuzzle[iy].length ; ix++ ) {
        byte col = colours[ mPuzzle[iy][ix] ];
        int x = 12 + 2*(iy + ix),
            y = 12 + (iy - ix);
        mPuzzleImage.plot(x, y, col);
        mPuzzleImage.plot(x+1, y, col);
      }
    }
    
    if ( !mDone ) {
      int ix = (mCursorPos == 1 || mCursorPos == 2) ? 1 : 0,
          iy = (mCursorPos == 2 || mCursorPos == 3) ? 1 : 0;
      int x = 14 + 2*(iy + ix),
          y = 12 + (iy - ix);
      if ( mCursorActive ) {
        for ( int ky = -1 ; ky <= +1 ; ky++ ) {
          for ( int kx = -1 ; kx <= +1 ; kx++ ) {
            if ( kx != 0 || ky != 0 ) {
              int dx = 2*(ky + kx),
                  dy = (ky - kx);
              mPuzzleImage.plot(x+dx, y+dy, (byte)63);
              mPuzzleImage.plot(x+dx+1, y+dy, (byte)63);
            }
          }
        }
      } else {
        mPuzzleImage.plot(x, y, (byte)63);
        mPuzzleImage.plot(x+1, y, (byte)63);
      }
    }
    
    mFloor.paint(mPuzzleImage, 0, 4, 0);
    
  } // paintPuzzle()
  
  // rotate the puzzle pieces around the cursor
  private void rotatePuzzle() {
    
    assert( mCursorActive );
    assert( mCursorPos >= 0 && mCursorPos <= 3 );
    
    final int ix = (mCursorPos == 1 || mCursorPos == 2) ? 1 : 0,
              iy = (mCursorPos == 2 || mCursorPos == 3) ? 1 : 0;
    
    int swap = mPuzzle[iy][ix];
    mPuzzle[iy  ][ix  ] = mPuzzle[iy+1][ix  ];
    mPuzzle[iy+1][ix  ] = mPuzzle[iy+1][ix+1];
    mPuzzle[iy+1][ix+1] = mPuzzle[iy  ][ix+1];
    mPuzzle[iy  ][ix+1] = swap;
    
  } // rotatePuzzle()
  
  // return whether the puzzle is solved, also update wall switch states
  private boolean puzzleCheck() {
    
    boolean complete = true;
    for ( int ix = 0 ; ix < mPuzzle[0].length ; ix++ ) {
      boolean rowComplete = true;
      for ( int iy = 0 ; iy < mPuzzle.length ; iy++ ) {
        if ( mPuzzle[iy][ix] != ix ) {
          rowComplete = false;
          break;
        }
      }
      if ( rowComplete ) {
        mSwitches[ix].setState(1);
      } else {
        mSwitches[ix].setState(0);
        complete = false;
      }
    }
    return complete;
    
  } // puzzleComplete()
  
  // track the bullets in the scene, return true if a new one is fired
  private boolean trackBullets(SpriteManager spriteManager) {
    
    if ( mBullets == null ) {
      mBullets     = new LinkedList<Bullet>();
      mBulletsPrev = new LinkedList<Bullet>();
    } else {
      LinkedList<Bullet> swap = mBulletsPrev;
      mBulletsPrev = mBullets;
      mBullets = swap;
      mBullets.clear();
    }
    
    boolean newBullet = false;
    for ( Sprite s : spriteManager.list() ) {
      if ( !(s instanceof Bullet) ) continue;
      Bullet bullet = (Bullet)s;
      if ( !mBulletsPrev.contains(bullet) ) newBullet = true;
      mBullets.add(bullet);
    }
    return newBullet;
    
  } // trackBullets()
  
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

    if ( !mDone ) {
      boolean bulletFired = trackBullets(spriteManager);
      if ( bulletFired ) {
        if ( mCursorActive ) {
          assert( mCursorTimer < kCursorActiveTime/2 );
          mCursorTimer = kCursorActiveTime;
        } else {
          mCursorActive = true;
          mCursorTimer = kCursorActiveTime;
          paintPuzzle();
        }
      } else {
        mCursorTimer -= 1;
        if ( mCursorActive && mCursorTimer == kCursorActiveTime/2 ) {
          rotatePuzzle();
          paintPuzzle();
          puzzleCheck();
          Env.sounds().play(Sounds.SWITCH_ON);
        } else if ( mCursorTimer == 0 ) {
          if ( mCursorActive ) {
            mCursorActive = false;
            mCursorTimer = kCursorMoveTime;
            if ( puzzleCheck() ) {
              mGate.setClosed(false);
              mDone = true;
              Env.sounds().play(Sounds.SUCCESS);
            }
          } else {
            mCursorPos = (mCursorPos + 1) % 4;
            mCursorTimer = kCursorMoveTime;
          }
          paintPuzzle();
        }
      }        
    }
    
  } // Room.advance()

} // class RoomE06
