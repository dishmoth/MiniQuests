/*
 *  Room.java
 *  Copyright Simon Hern 2010
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// base class for different rooms
abstract public class Room {

  // story event: the player changes room
  public class EventRoomChange extends StoryEvent {
    public String mNewRoom;
    public int mEntryPoint;
    public EventRoomChange(String r, int entryPoint) {
      mNewRoom = r; mEntryPoint = entryPoint;
    }
  } // class Room.EventRoomChange

  // story event: the player's respawn point changes
  public class EventNewEntryPoint extends StoryEvent {
    public int mEntryPoint;
    public EventNewEntryPoint(int entryPoint) {
      mEntryPoint = entryPoint;
    }
  } // class Room.EventNewEntryPoint

  // story event: the room scrolls
  public class EventRoomScroll extends StoryEvent {
    public int mShiftX, mShiftY, mShiftZ;
    public EventRoomScroll(int dx, int dy, int dz) {
      mShiftX = dx; mShiftY = dy; mShiftZ = dz; 
    }
  } // class Room.EventRoomScroll

  // camera can be at different heights to best view the player
  public static class CameraLevel {
    public int mCameraZPos;
    public int mPlayerZMin, mPlayerZMax;
    public CameraLevel(int z, int min, int max) {
      assert( min < max );
      mCameraZPos = z; mPlayerZMin = min ; mPlayerZMax = max;
    }
  } // class CameraLevel
  
  // number of blocks along the edge of a room (or zone in a multi-zone room)
  public static final int kSize = 10;

  // tag identifying the room
  final protected String mName;
  
  // reference to the camera object
  protected Camera mCamera;

  // which level the camera is on (or -1 if camera levels aren't used)
  protected int mCameraLevel;
  
  // reference to the player (null if dead or not in room)
  protected Player mPlayer;
  
  // references to the other rooms in the game
  protected Room mRoomList[];
  
  // constructor
  public Room(String name) {

    mName = name;
    
    mPlayer = null;
    mCamera = null;
    mCameraLevel = -1;
    mRoomList = null;
    
  } // constructor

  // serialize the room state by writing bits to the buffer
  public void save(BitBuffer buffer) {}

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  public boolean restore(int version, BitBuffer buffer) { return true; } 

  // unique identifier for the room
  public String name() { return mName; }
  
  // identify the game's camera object
  public void setCamera(Camera camera) { mCamera = camera; }
  
  // remove the player sprite from the room
  public void removePlayer() { assert(mPlayer != null); mPlayer = null; }
  
  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  abstract public Player createPlayer(int entryPoint);
  
  // helper function:
  // create the player as if just come through the exit
  // (also set the camera position; no cameraLevels are used)
  protected void setPlayerAtExit(Exit exit) { setPlayerAtExit(exit, null); }

  // helper function:
  // create the player as if just come through the exit
  // (also set the camera position; use the provided cameraLevels)
  protected void setPlayerAtExit(Exit exit, CameraLevel cameraLevels[]) {
    
    assert( mPlayer == null );
    
    int x = exit.mZoneX*kSize,
        y = exit.mZoneY*kSize,
        z = exit.mDoorZPos;
    
    switch ( exit.mWallSide ) {
      case Env.RIGHT: {
        x += kSize;
        y += exit.mDoorXYPos;
      } break;
      case Env.UP: {
        x += exit.mDoorXYPos;
        y += kSize;
      } break;
      case Env.LEFT: {
        x -= 1;
        y += exit.mDoorXYPos;
      } break;
      case Env.DOWN: {
        x += exit.mDoorXYPos;
        y -= 1;
      } break;
      default: {
        assert(false);
      } break;
    }

    final int direc = (exit.mWallSide + 2) % 4;
    mPlayer = new Player(x, y, z, direc);

    int camZPos;
    if ( cameraLevels == null ) {
      assert( exit.mCameraLevel == -1 );
      mCameraLevel = -1;
      camZPos = 0;
    } else {
      mCameraLevel = exit.mCameraLevel;
      assert( mCameraLevel >= 0 && mCameraLevel < cameraLevels.length );
      camZPos = cameraLevels[mCameraLevel].mCameraZPos; 
    }
    
    mCamera.set(exit.mZoneX*kSize, exit.mZoneY*kSize, camZPos);
    
  } // setPlayerAtExit()
  
  // create the sprites for a room (excluding the player)
  abstract public void createSprites(SpriteManager spriteManager);

  // room is no longer current, delete any unnecessary references 
  public void discardResources() {}
  
  // update the room (events may be added or processed)
  abstract public void advance(LinkedList<StoryEvent> storyEvents,
                               SpriteManager          spriteManager);
  
  // returns true if the room is frozen (e.g., during a cut-scene)
  public boolean paused() { return false; }
  
  // specify the other rooms in the game
  public void setRoomList(Room list[]) { mRoomList = list; }

  // retrieve a reference to one of the game's other rooms
  public Room findRoom(String name) {
    
    for ( Room room : mRoomList ) {
      if ( room.mName == name ) return room;
    }
    assert( false );
    return null;
    
  } // findRoom()
  
  // helper function:
  // create four basic walls with doors
  protected void addBasicWalls(Exit exits[], SpriteManager spriteManager) {
    
    Wall walls[] = new Wall[4];
    walls[Env.RIGHT] = new WallRight(0, 0, 0);
    walls[Env.UP]    = new WallUp(0, 0, 0);
    walls[Env.LEFT]  = new WallLeft(0, 0, 0);
    walls[Env.DOWN]  = new WallDown(0, 0, 0);

    for ( Exit exit : exits ) {
      final Wall wall = walls[ exit.mWallSide ];
      exit.mDoor = wall.addDoor(exit.mDoorXYPos, exit.mDoorZPos, 
                                exit.mFloorColour, exit.mFloorDrop);
      if ( exit.mDestination.isEmpty() ) {
        exit.mDoor.setClosed(true);
      }
    }
    
    for ( Wall w : walls ) spriteManager.addSprite(w);

  } // addBasicWalls()
  
  // helper function:
  // create walls and doors for a single zone of a multi-zone room
  protected void addBasicZone(int zoneX, int zoneY,
                              boolean wallRight, boolean wallUp,
                              boolean wallLeft, boolean wallDown,
                              Exit exits[], SpriteManager spriteManager) {
    
    final int x = Room.kSize*zoneX,
              y = Room.kSize*zoneY;
    
    Wall walls[] = new Wall[4];
    if ( wallRight ) walls[Env.RIGHT] = new WallRight(x, y, 0);
    if ( wallUp )    walls[Env.UP]    = new WallUp(x, y, 0);
    if ( wallLeft )  walls[Env.LEFT]  = new WallLeft(x, y, 0);
    if ( wallDown  ) walls[Env.DOWN]  = new WallDown(x, y, 0);

    for ( Exit exit : exits ) {
      if ( exit.mZoneX == zoneX && exit.mZoneY == zoneY ) {
        final Wall wall = walls[ exit.mWallSide ];
        assert ( wall != null );
        exit.mDoor = wall.addDoor(exit.mDoorXYPos, exit.mDoorZPos, 
                                  exit.mFloorColour, exit.mFloorDrop);
        if ( exit.mDestination.isEmpty() ) {
          exit.mDoor.setClosed(true);
        }
      }
    }
    
    for ( Wall wall : walls ) {
      if ( wall != null ) spriteManager.addSprite(wall);
    }

  } // addBasicZone()
  
  // helper function:
  // check which exit the player is at (-1 if none)
  protected int checkExits(Exit exits[]) {

    assert( exits != null );
    
    if ( mPlayer == null || mPlayer.isActing() ) return -1;
    
    for ( int index = 0 ; index < exits.length ; index++ ) {
      Exit exit = exits[index];
      
      if ( exit.mWallSide != mPlayer.getDirec() ) continue;
      
      int doorX = exit.mZoneX*kSize,
          doorY = exit.mZoneY*kSize,
          doorZ = exit.mDoorZPos;
      switch ( exit.mWallSide ) {
        case Env.RIGHT: {
          doorX += kSize;
          doorY += exit.mDoorXYPos;
        } break;
        case Env.UP: {
          doorX += exit.mDoorXYPos;
          doorY += kSize;
        } break;
        case Env.LEFT: {
          doorX -= 1;
          doorY += exit.mDoorXYPos;
        } break;
        case Env.DOWN: {
          doorX += exit.mDoorXYPos;
          doorY -= 1;
        } break;
        default: {
          assert(false);
        } break;
      }
      
      if ( mPlayer.getXPos() == doorX && 
           mPlayer.getYPos() == doorY && 
           mPlayer.getZPos() == doorZ ) return index;
    }
    
    return -1;
    
  } // checkExits()

  // helper function:
  // check whether the camera needs to move horizontally
  protected EventRoomScroll checkHorizontalScroll() {

    if ( mPlayer == null || mPlayer.isActing() ) return null;
        
    final int x = mPlayer.getXPos() - mCamera.xPos(),
              y = mPlayer.getYPos() - mCamera.yPos();
    
    int dx = 0,
        dy = 0;
    switch ( mPlayer.getDirec() ) {
      case Env.RIGHT: { if ( x >= kSize ) dx = +kSize; } break;
      case Env.UP:    { if ( y >= kSize ) dy = +kSize; } break;
      case Env.LEFT:  { if ( x < 0 )      dx = -kSize; } break;
      case Env.DOWN:  { if ( y < 0 )      dy = -kSize; } break;
    }
    if ( dx != 0 || dy != 0 ) return new EventRoomScroll(dx, dy, 0);
    
    return null;
    
  } // checkHorizontalScroll()

  // helper function:
  // check whether the camera needs to move vertically
  protected EventRoomScroll checkVerticalScroll(CameraLevel cameraLevels[]) {

    assert( cameraLevels != null );
    assert( mCameraLevel != -1 );
    assert( mCameraLevel >= 0 && mCameraLevel < cameraLevels.length );
    
    if ( mPlayer == null || mPlayer.isActing() ) return null;
        
    CameraLevel camLevel = cameraLevels[mCameraLevel];
    final int zOld = camLevel.mCameraZPos;
    if ( mPlayer.getZPos() < camLevel.mPlayerZMin ) {
      mCameraLevel -= 1;
      assert( mCameraLevel >= 0 );
    } else if ( mPlayer.getZPos() > camLevel.mPlayerZMax ) {
      mCameraLevel += 1;
      assert( mCameraLevel < cameraLevels.length );
    }
    final int dz = cameraLevels[mCameraLevel].mCameraZPos - zOld;
    
    if ( dz != 0 ) return new EventRoomScroll(0, 0, dz);
    
    return null;
    
  } // checkVerticalScroll()

  // helper function:
  // move the camera to a particular room zone
  protected EventRoomScroll scrollToZone(int zoneX, int zoneY) {
    
    final int dx = zoneX*Room.kSize - mCamera.xPos(),
              dy = zoneY*Room.kSize - mCamera.yPos();
    
    if ( dx != 0 || dy != 0 ) return new EventRoomScroll(dx, dy, 0);

    return null;
    
  } // scrollToZone()
  
  // helper function:
  // move the camera to the player's zone
  protected EventRoomScroll scrollToPlayer() {
    
    if ( mPlayer == null ) return null;
        
    final int x = mPlayer.getXPos() - mCamera.xPos(),
              y = mPlayer.getYPos() - mCamera.yPos();
    
    int dx = 0,
        dy = 0;
    while ( x-dx < 0 )      dx -= kSize;
    while ( x-dx >= kSize ) dx += kSize;
    while ( y-dy < 0 )      dy -= kSize;
    while ( y-dy >= kSize ) dy += kSize;

    if ( dx != 0 || dy != 0 ) return new EventRoomScroll(dx, dy, 0);
    
    return null;
    
  } // scrollToPlayer()

} // class Room
