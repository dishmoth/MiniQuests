/*
 *  Barrier.java
 *  Copyright (c) 2019 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

// an invisible obstacle that selectively blocks sprites
public class Barrier extends Sprite3D implements Obstacle {

  // position of origin
  private int mXPos,
              mYPos,
              mZPos;

  // size of the barrier
  private int mXSize,
              mYSize,
              mZSize;

  // sprite types that this barrier blocks
  private ArrayList<Class<?>> mBlockList = new ArrayList<Class<?>>();
  
  // constructor
  public Barrier(int x, int y, int z, Class<?> type) {

    mXPos = x;
    mYPos = y;
    mZPos = z;
    
    mXSize = mYSize = mZSize = 1;
    
    mBlockList.add(type);
    
  } // constructor

  // constructor
  public Barrier(int x, int y, int z, Collection<Class<?>> types) {

    mXPos = x;
    mYPos = y;
    mZPos = z;
    
    mXSize = mYSize = mZSize = 1;
    
    mBlockList.addAll(types);
    
  } // constructor

  // constructor
  public Barrier(int x, int y, int z,
                 int xSize, int ySize, int zSize,
                 Class<?> type) {

    assert( xSize >= 1 && ySize >= 1 && zSize >= 1);
    
    mXPos = x;
    mYPos = y;
    mZPos = z;
    
    mXSize = xSize;
    mYSize = ySize;
    mZSize = zSize;
    
    mBlockList.add(type);
    
  } // constructor

  // constructor
  public Barrier(int x, int y, int z,
                 int xSize, int ySize, int zSize,
                 Collection<Class<?>> types) {

    assert( xSize >= 1 && ySize >= 1 && zSize >= 1);
    
    mXPos = x;
    mYPos = y;
    mZPos = z;
    
    mXSize = xSize;
    mYSize = ySize;
    mZSize = zSize;
    
    mBlockList.addAll(types);
    
  } // constructor

  // whether this is an obstacle for a particular sprite
  public boolean blocks(Sprite3D s) {
    
    return mBlockList.contains(s.getClass());
    
  } // blocks()
  
  // access to position
  public int getXPos() { return mXPos; }
  public int getYPos() { return mYPos; }
  public int getZPos() { return mZPos; }
  
  // shift position
  public void shiftPos(int dx, int dy, int dz) {
    
    mXPos += dx;
    mYPos += dy;
    mZPos += dz;
    
  } // shiftPos()
  
  // set position
  public void setPos(int x, int y, int z) {
    
    mXPos = x;
    mYPos = y;
    mZPos = z;
    
  } // setPos()
  
  // whether there is space at the specified position
  public boolean isEmpty(int x, int y, int z) {

    return ( x < mXPos || x >= mXPos + mXSize ||
             y < mYPos || y >= mYPos + mYSize ||
             z < mZPos || z >= mZPos + mZSize );
    
  } // Obstacle.isEmpty()
  
  // whether the player can stand at the specified position
  public boolean isPlatform(int x, int y, int z) { return false; }
  
  // whether the position is outside of the game world
  public boolean isVoid(int x, int y, int z) { return false; }

  // nothing to do here
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {
  } // Sprite.advance()

  // nothing to do here
  @Override
  public void draw(EgaCanvas canvas) {
  } // Sprite.draw()

} // class Barrier
