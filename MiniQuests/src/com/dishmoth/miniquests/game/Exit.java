/*
 *  Exit.java
 *  Copyright Simon Hern 2010
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

// basic details an exit/entry point for a room
public class Exit {

  // for a multi-zone room, which zone the exit is in (zero otherwise)
  final public int mZoneX,
                   mZoneY;
  
  // which side of the room the exit is on (see enumeration in Env)
  final public int mWallSide;
  
  // position of the door in the wall
  final public int mDoorXYPos,
                   mDoorZPos;
  
  // details of the floor tile under the door (see Door class)
  final public byte mFloorColour[];
  final public int  mFloorDrop;
  
  // which room the exit leads to, and the entry point in that room
  // (destination may be "" to indicate a dummy, locked door)
  final public String mDestination;
  final public int mEntryPoint;
  
  // which level the camera should be on if the player enters the room here
  final public int mCameraLevel;
  
  // reference to the associated Door object, if the room is current
  public Door mDoor;
  
  // constructor (no zone)
  public Exit(int wallSide, 
              int doorXYPos, int doorZPos, String floorColour, int floorDrop,
              int cameraLevel,
              String destination, int entryPoint) {
    
    mZoneX = 0;
    mZoneY = 0;
    
    assert( wallSide >= 0 && wallSide < 4 );
    mWallSide = wallSide; 
    
    mDoorXYPos = doorXYPos;
    mDoorZPos = doorZPos;
    
    assert( floorColour != null && floorColour.length() == 2 );
    mFloorColour = EgaTools.decodePixels(floorColour);
    mFloorDrop = floorDrop;
    
    assert( cameraLevel >= -1 );
    mCameraLevel = cameraLevel;
    
    assert( destination != null );
    mDestination = destination;
    
    assert( entryPoint >= 0 );
    mEntryPoint = entryPoint;

    mDoor = null;
    
  } // constructor
  
  // constructor (with zone)
  public Exit(int zoneX, int zoneY, int wallSide, 
              int doorXYPos, int doorZPos, String floorColour, int floorDrop,
              int cameraLevel,
              String destination, int entryPoint) {
    
    mZoneX = zoneX;
    mZoneY = zoneY;
    
    assert( wallSide >= 0 && wallSide < 4 );
    mWallSide = wallSide; 
    
    mDoorXYPos = doorXYPos;
    mDoorZPos = doorZPos;
    
    assert( floorColour != null && floorColour.length() == 2 );
    mFloorColour = EgaTools.decodePixels(floorColour);
    mFloorDrop = floorDrop;

    assert( cameraLevel >= -1 );
    mCameraLevel = cameraLevel;
    
    assert( destination != null );
    mDestination = destination;
    
    assert( entryPoint >= 0 );
    mEntryPoint = entryPoint;
    
    mDoor = null;
    
  } // constructor
  
} // class Exit
