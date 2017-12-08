/*
 *  WallDown.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

// boundary on the down-side (negative y) of a room
public class WallDown extends Wall {

  // constructor
  public WallDown(int x, int y, int z) {

    super(x, y, z);
    
  } // constructor

  // put a door in the wall
  @Override
  public Door addDoor(int xPos, int zPos, byte floorColour[], int floorDrop) {
    
    assert( xPos >= 0 && xPos < Room.kSize );
    assert( zPos >= 0 );
    assert( floorColour != null && floorColour.length == 2 );

    Door door = new Door(xPos, zPos, Env.DOWN, floorColour, floorDrop);
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
    if ( y != -1 ) return false;
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
    if ( y != -1 || (x < 0 || x >= Room.kSize) ) return true;
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
    if ( y != -2 || (x < 0 || x >= Room.kSize) ) return false;
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
    
    final byte colour = Env.whiteBackground() ? (byte)63 : (byte)0;
    
    for ( int ix = -1 ; ix < Room.kSize ; ix++ ) {
      final int xPos = 2*(xOrigin + ix),
                yPos = 2*(yOrigin - 1),
                zPos = zOrigin - 1;
      
      final int depth = (int)Math.floor(0.5f*xPos) + (int)Math.ceil(0.5f*yPos);
      final int x = Env.originXPixel() + xPos - yPos,
                y = Env.originYPixel() - depth - zPos;
      
      final int x0 = x + ( (ix == -1) ? 1 : 0 ),
                x1 = x + ( (ix == Room.kSize-1) ? 0 : 1 );
      
      canvas.fill(x0, x1, y, Env.screenHeight()-1, depth, colour);
    }

    for ( Door door : mDoors ) {
      door.draw(canvas, xOrigin, yOrigin, zOrigin);
    }

  } // Sprite.draw()

} // class WallDown
