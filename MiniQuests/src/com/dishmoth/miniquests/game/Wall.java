/*
 *  Wall.java
 *  Copyright Simon Hern 2010
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.Iterator;
import java.util.LinkedList;

// boundary of a room, possibly including doors
abstract public class Wall extends Sprite3D implements Obstacle {

  // position of the room reference point
  protected int mXPos,
                mYPos,
                mZPos;
  
  // details of any doors
  protected LinkedList<Door> mDoors = new LinkedList<Door>();

  // the door z-positions in increasing order organized by x/y-positions
  // (just as a convenience, not needed for all wall types)
  protected int mDoorZPos[][] = null;
  
  // constructor
  public Wall(int x, int y, int z) {

    mXPos = x;
    mYPos = y;
    mZPos = z;
    
  } // constructor

  // put a door in the wall (and return a reference to it)
  // (specified in room coordinates, x or y depending on the wall's side)
  abstract public Door addDoor(int xyPos, int zPos, 
                               byte floorColour[], int floorDrop);

  // remove a door
  public void removeDoor(Door deadDoor) {
    
    for ( Iterator<Door> it = mDoors.iterator() ; it.hasNext() ; ) {
      Door d = it.next();
      if ( d == deadDoor ) {
        it.remove();
        mDoorZPos = null;
        return;
      }
    }
    assert( false );
    
  } // removeDoor()
  
  // build the door position array (if needed)
  protected void processDoors() {
    
    assert( mDoorZPos == null );
    
    mDoorZPos = new int[Room.kSize][];

    for ( Door door : mDoors ) {
      final int index = door.xyPos();
      assert( index >= 0 && index < Room.kSize );
      if ( mDoorZPos[index] == null ) {
        mDoorZPos[index] = new int[]{ door.zPos() };
      } else { 
        int zOld[] = mDoorZPos[index],
            zNew[] = new int[zOld.length + 1];
        int iOld = 0,
            iNew = 0;
        while ( iOld < zOld.length && 
                zOld[iOld] < door.zPos() ) zNew[iNew++] = zOld[iOld++];
        zNew[iNew++] = door.zPos();
        while ( iNew < zNew.length ) zNew[iNew++] = zOld[iOld++];
        mDoorZPos[index] = zNew;
      }
    }
    
  } // processDoors()
  
  // whether the player can stand at the specified position
  abstract public boolean isPlatform(int x, int y, int z);

  // whether there is space at the specified position
  abstract public boolean isEmpty(int x, int y, int z);

  // nothing to do here
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {
  } // Sprite.advance()

} // class Wall
