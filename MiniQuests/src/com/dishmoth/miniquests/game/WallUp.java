/*
 *  WallUp.java
 *  Copyright Simon Hern 2010
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

// boundary on the up-side (positive y) of a room
public class WallUp extends Wall {

  // wall colour
  private static final byte kColour = 56;

  // constructor
  public WallUp(int x, int y, int z) {

    super(x, y, z);
    
  } // constructor

  // put a door in the wall
  @Override
  public Door addDoor(int xPos, int zPos, byte floorColour[], int floorDrop) {
    
    assert( xPos >= 0 && xPos < Room.kSize );
    //assert( zPos >= 0 );    
    assert( floorColour != null && floorColour.length == 2 );
    
    Door door = new Door(xPos, zPos, Env.UP, floorColour, floorDrop);
    mDoors.add(door);
    mDoorZPos = null;
    
    return door;
    
  } // addDoor()
  
  // whether the player can stand at the specified position
  @Override
  public boolean isPlatform(int x, int y, int z) {

    x -= mXPos;
    y -= mYPos;
    z -= mZPos;
    if ( y != Room.kSize ) return false;
    for ( Door door : mDoors ) {
      if ( x == door.xyPos() && z == door.zPos() ) return true;
    }
    return false;
    
  } // Obstacle.isPlatform()

  // whether there is space at the specified position
  @Override
  public boolean isEmpty(int x, int y, int z) {

    x -= mXPos;
    y -= mYPos;
    z -= mZPos;
    if ( y != Room.kSize || (x < 0 || x >= Room.kSize) ) return true;
    for ( Door door : mDoors ) {
      if ( door.closed() ) continue;
      final int dz = z - door.zPos();
      if ( x==door.xyPos() && (dz>0 && dz<=Door.gapHeight()) ) return true;
    }
    return false;
  
  } // Obstacle.isEmpty()

  // whether the position is outside of the game world
  public boolean isVoid(int x, int y, int z) { 

    x -= mXPos;
    y -= mYPos;
    z -= mZPos;
    if ( y != Room.kSize+1 || (x < 0 || x >= Room.kSize) ) return false;
    for ( Door door : mDoors ) {
      final int dz = z - door.zPos();
      if ( x==door.xyPos() && (dz>0 && dz<=Door.gapHeight()) ) return true;
    }
    return false;
    
  } // Obstacle.isVoid()

  // display the wall
  @Override
  public void draw(EgaCanvas canvas) {

    final int xOrigin = mXPos - mCamera.xPos(),
              yOrigin = mYPos - mCamera.yPos(),
              zOrigin = mZPos - mCamera.zPos();

    if ( xOrigin + yOrigin < -10 ) return;
    
    if ( mDoorZPos == null ) processDoors();
    
    for ( int ix = 0 ; ix <= Room.kSize ; ix++ ) {
      final int xPos = 2*(xOrigin + ix),
                yPos = 2*(yOrigin + Room.kSize),
                zPos = zOrigin;
      
      final int depth = (int)Math.floor(0.5f*xPos) + (int)Math.ceil(0.5f*yPos);
      final int x = Env.originXPixel() + xPos - yPos,
                y = Env.originYPixel() - depth - zPos;
      
      final int x0 = x + ( (ix == 0) ? 1 : 0 ),
                x1 = x + ( (ix == Room.kSize) ? 0 : 1 );
      
      //canvas.fill(x0, x1, y+1, Env.screenHeight()-1, depth, kColour);
      //int h = 0;
      
      int h = y - (Env.screenHeight()-1);
      if ( ix < Room.kSize && mDoorZPos[ix] != null ) {
        for ( int k = 0 ; k < mDoorZPos[ix].length ; k++ ) {
          int h2 = mDoorZPos[ix][k];
          canvas.fill(x0, x1, y-h2+1, y-h, depth, kColour);
          h = h2 + Door.gapHeight();
        }
      }
      canvas.fill(x0, x1, 0, y-h, depth, kColour);
    }

    for ( Door door : mDoors ) {
      door.draw(canvas, xOrigin, yOrigin, zOrigin);
    }
    
  } // Sprite.draw()

} // class WallUp
