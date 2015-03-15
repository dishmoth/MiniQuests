/*
 *  Fence.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// a simple barrier
public class Fence extends Sprite3D implements Obstacle {

  // colour schemes for the fence
  private static final byte kColours[] = { 48, 0 };

  // position of base point of fence
  final private int mXPos,
                    mYPos,
                    mZPos;
  
  // number of fence posts
  final private int mLength;
  
  // whether the fence runs horizontally or vertically
  final private int mDirec;

  // index of the colour to use
  final private int mColourScheme;
  
  // constructor
  public Fence(int xPos, int yPos, int zPos, 
               int length, int direc, int colourScheme) {
    
    mXPos = xPos;
    mYPos = yPos;
    mZPos = zPos;
  
    assert( length > 0 );
    mLength = length;
    
    assert( direc == Env.UP || direc == Env.RIGHT );
    mDirec = direc;

    assert ( colourScheme >= 0 && colourScheme < kColours.length );
    mColourScheme = colourScheme;
    
  } // constructor
  
  // whether the player can stand at the specified position
  public boolean isPlatform(int x, int y, int z) {

    return false;
    
  } // Obstacle.isPlatform()

  // whether there is space at the specified position
  public boolean isEmpty(int x, int y, int z) {

    if ( z != mZPos+1 ) return true;
    if ( mDirec == Env.RIGHT ) {
      if ( x >= mXPos && x < mXPos+mLength && y == mYPos ) return false;
    } else {
      if ( x == mXPos && y >= mYPos && y < mYPos+mLength ) return false;      
    }
    return true;
  
  } // Obstacle.isEmpty()

  // whether the position is outside of the game world
  public boolean isVoid(int x, int y, int z) { 

    return false;
    
  } // Obstacle.isVoid()

  // nothing to do here
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {

  } // Sprite.advance()

  // display the object
  @Override
  public void draw(EgaCanvas canvas) {

    final int xPos = mXPos - mCamera.xPos(),
              yPos = mYPos - mCamera.yPos(),
              zPos = mZPos - mCamera.zPos();

    final int x0 = Env.originXPixel() + 2*xPos - 2*yPos,
              y0 = Env.originYPixel() - xPos - yPos - zPos;
    final float depth0 = xPos + yPos;
    
    int x = x0,
        y = y0;
    float depth = depth0 - 0.01f;
    
    byte colour = kColours[mColourScheme];
    
    for ( int k = 0 ; k < mLength ; k++ ) {
      canvas.fill(x, x, y-4, y, depth, colour);
      if ( mDirec == Env.RIGHT ) {
        if ( k < mLength-1 ) {
          canvas.plot(x+1, y-1, depth, colour);
          canvas.plot(x+1, y-3, depth, colour);
        }
        x += 2;
        y -= 1;
        depth += 1.0f;
      } else {
        if ( k > 0 ) {
          canvas.plot(x+1, y, depth, colour);
          canvas.plot(x+1, y-2, depth, colour);
        }
        x -= 2;
        y -= 1;
        depth += 1.0f;
      }
    }
    
  } // Sprite.draw()

} // class Fence
