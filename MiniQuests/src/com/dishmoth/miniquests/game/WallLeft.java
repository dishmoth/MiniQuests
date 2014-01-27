/*
 *  WallLeft.java
 *  Copyright Simon Hern 2010
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

// boundary on the left-side (negative x) of a room
public class WallLeft extends Wall {

  // constructor
  public WallLeft(int x, int y, int z) {

    super(x, y, z);
    
  } // constructor

  // put a door in the wall
  @Override
  public Door addDoor(int yPos, int zPos, byte floorColour[], int floorDrop) {
    
    assert( yPos >= 0 && yPos < Room.kSize );
    assert( zPos >= 0 );
    assert( floorColour != null && floorColour.length == 2 );
    
    Door door = new Door(yPos, zPos, Env.LEFT, floorColour, floorDrop);
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
    if ( x != -1 ) return false;
    for ( Door door : mDoors ) {
      if ( y == door.xyPos() && z == door.zPos() ) return true;
    }
    return false;
    
  } // Obstacle.isPlatform()

  // whether there is space at the specified position
  @Override
  public boolean isEmpty(int x, int y, int z) {

    x -= mXPos;
    y -= mYPos;
    z -= mZPos;
    if ( x != -1 || (y < 0 || y >= Room.kSize) ) return true;
    for ( Door door : mDoors ) {
      if ( door.closed() ) continue;
      final int dz = z - door.zPos();
      if ( y==door.xyPos() && (dz>0 && dz<=Door.gapHeight()) ) return true;
    }
    return false;
  
  } // Obstacle.isEmpty()

  // whether the position is outside of the game world
  public boolean isVoid(int x, int y, int z) { 

    x -= mXPos;
    y -= mYPos;
    z -= mZPos;
    if ( x != -2 || (y < 0 || y >= Room.kSize) ) return false;
    for ( Door door : mDoors ) {
      final int dz = z - door.zPos();
      if ( y==door.xyPos() && (dz>0 && dz<=Door.gapHeight()) ) return true;
    }
    return false;
    
  } // Obstacle.isVoid()

  // display the wall
  @Override
  public void draw(EgaCanvas canvas) {

    final int xOrigin = mXPos - mCamera.xPos(),
              yOrigin = mYPos - mCamera.yPos(),
              zOrigin = mZPos - mCamera.zPos();

    final byte colour = Env.backgroundColour();
    
    for ( int iy = -1 ; iy < Room.kSize ; iy++ ) {
      final int xPos = 2*(xOrigin - 1),
                yPos = 2*(yOrigin + iy),
                zPos = zOrigin - 1;
      
      final int depth = (int)Math.floor(0.5f*xPos) + (int)Math.ceil(0.5f*yPos);
      final int x = Env.originXPixel() + xPos - yPos,
                y = Env.originYPixel() - depth - zPos;
      
      final int x0 = x + ( (iy == Room.kSize-1) ? 1 : 0 ),
                x1 = x + ( (iy == -1) ? 0 : 1 );
      
      canvas.fill(x0, x1, y, Env.screenHeight()-1, depth, colour);
    }

    for ( Door door : mDoors ) {
      door.draw(canvas, xOrigin, yOrigin, zOrigin);
    }
    
  } // Sprite.draw()

} // class WallLeft
