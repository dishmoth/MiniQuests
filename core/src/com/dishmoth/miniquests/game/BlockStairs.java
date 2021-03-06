/*
 *  BlockStairs.java
 *  Copyright (c) 2018 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// a line of blocks between movable start and end points
public class BlockStairs extends Sprite3D implements Obstacle {

  // time delay when blocks move
  private static final int kBlockMoveTime = 2;
  
  // target z-positions of start and end blocks 
  private int mZStart,
              mZEnd;

  // number of blocks deep (>= 1)
  final private int mDepth;

  // 0 => even slope (default), -1 => sloped at start, +1 => sloped at end
  private int mSlopeType;
  
  // individual stair blocks
  // (note: these are owned by the BlockStairs object not the SpriteManager)
  private BlockArray mBlocks[];

  // count down until the block positions change again (or zero)
  private int mTimer;
  
  // constructor
  public BlockStairs(int xStart, int yStart, int zStart,
                     int xEnd, int yEnd, int zEnd,
                     String blockColour, int depth) {
  
    assert( xStart == xEnd || yStart == yEnd );
    assert( blockColour != null && blockColour.length() == 2 );
    assert( depth >= 1 );

    mDepth = depth;
    mSlopeType = 0;
    
    mZStart = zStart;
    mZEnd = zEnd;
    
    buildBlocks(xStart, yStart, zStart,
                xEnd, yEnd, zEnd,
                blockColour);
    
    mTimer = 0;
    
  } // constructor

  // build the individual stair blocks
  private void buildBlocks(int xStart, int yStart, int zStart,
                           int xEnd, int yEnd, int zEnd,
                           String colour) {

    final String blockPattern[][] = new String[mDepth][1];
    for ( int k = 0 ; k < mDepth ; k++ ) blockPattern[k][0] = "0";
    final String blockColour[] = new String[]{colour};
    
    final int len = Math.max(Math.abs(xEnd - xStart),
                             Math.abs(yEnd - yStart)) + 1;
    mBlocks = new BlockArray[len];

    final int dx = (int)Math.signum(xEnd - xStart),
              dy = (int)Math.signum(yEnd - yStart);

    for ( int k = 0 ; k < len ; k++ ) {
      final int x = xStart+k*dx,
                y = yStart+k*dy,
                z = (k<len/2 ? zStart : zEnd) - 2*(mDepth - 1);
      mBlocks[k] = new BlockArray(blockPattern, blockColour, x, y, z);
    }
    
    updateBlocks();
    
  } // buildBlocks()
  
  // update block positions between start and end
  private void updateBlocks() {

    final int z0   = mBlocks[0].getZPos(),
              z1   = mBlocks[mBlocks.length-1].getZPos(),
              dz   = Math.abs(z1 - z0),
              sign = (int)Math.signum(z1 - z0);
    assert( mSlopeType == 0 || dz <= 2*(mBlocks.length-1) );
    
    for ( int k = 1 ; k < mBlocks.length-1 ; k++ ) {
      int z;
      if ( mSlopeType < 0 ) {
        final int step = Math.min(2*k, dz);
        z = z0 + sign * step;
      } else if ( mSlopeType > 0 ) {
        final int step = Math.max(2*(k-mBlocks.length+1)+dz, 0);
        z = z0 + sign * step;
      } else {
        final float h = k / (mBlocks.length - 1.0f);
        z = Math.round(z0 + h*(z1 - z0));
      }
      BlockArray b = mBlocks[k];
      b.setPos(b.getXPos(), b.getYPos(), z);
    }
  
  } // updateBlocks()

  // set the target z-position for the start block
  public void setZStart(int z) {
    
    if ( z != mZStart ) {
      mZStart = z;
      if ( mTimer == 0 ) mTimer = kBlockMoveTime;
    }
    
  } // setZStart()
  
  // set the target z-position for the end block
  public void setZEnd(int z) {
    
    if ( z != mZEnd ) {
      mZEnd = z;
      if ( mTimer == 0 ) mTimer = kBlockMoveTime;
    }
    
  } // setZEnd()
  
  // return the current (not target) z-positions of the start and end blocks
  public int getZStart() {
    return mBlocks[0].getZPos() + 2*(mDepth-1);
  }
  public int getZEnd() {
    return mBlocks[mBlocks.length-1].getZPos() + 2*(mDepth-1);
  }

  // change how evenly the steps are spread out (-1 => start, +1 => end)
  public void setSlopeType(int type) {
    
    assert( type >= -1 && type <= +1 );
    mSlopeType = type;
    updateBlocks();
    
  } // setSlopeType()
  
  // returns true if the stair blocks are moving to target position
  public boolean moving() { return (mTimer > 0); }
  
  // change the stair start/end z-positions without any transition
  public void reset(int zStart, int zEnd) {
    
    mZStart = zStart;
    mZEnd = zEnd;
    mTimer = 0;

    BlockArray b0 = mBlocks[0];
    b0.setPos(b0.getXPos(), b0.getYPos(), mZStart - 2*(mDepth - 1));

    BlockArray b1 = mBlocks[mBlocks.length-1];
    b1.setPos(b1.getXPos(), b1.getYPos(), mZEnd - 2*(mDepth - 1));

    updateBlocks();
    
  } // reset()
  
  // whether the player can stand at the specified position
  @Override
  public boolean isPlatform(int x, int y, int z) {

    for ( BlockArray b : mBlocks ) {
      if ( b.isPlatform(x, y, z) ) return true;
    }
    return false;
    
  } // Obstacle.isPlatform()
  
  // whether there is space at the specified position
  @Override
  public boolean isEmpty(int x, int y, int z) {

    for ( BlockArray b : mBlocks ) {
      if ( !b.isEmpty(x, y, z) ) return false;
    }
    return true;
    
  } // Obstacle.isEmpty()

  // whether the position is outside of the game world
  @Override
  public boolean isVoid(int x, int y, int z) { return false; }

  // move the stair blocks up and down
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {
    
    if ( mTimer > 0 ) {
      if ( --mTimer == 0 ) {
        boolean update = false;
        BlockArray blockStart = mBlocks[0],
                   blockEnd   = mBlocks[mBlocks.length-1];
        if ( blockStart.getZPos() + 2*(mDepth-1) < mZStart ) {
          blockStart.shiftPos(0, 0, 1);
          update = true;
        } else if ( blockStart.getZPos() + 2*(mDepth-1) > mZStart ) {
          blockStart.shiftPos(0, 0, -1);
          update = true;
        }
        if ( blockEnd.getZPos() + 2*(mDepth-1) < mZEnd ) {
          blockEnd.shiftPos(0, 0, 1);
          update = true;
        } else if ( blockEnd.getZPos() + 2*(mDepth-1) > mZEnd ) {
          blockEnd.shiftPos(0, 0, -1);
          update = true;
        }
        if ( update ) {
          updateBlocks();
          mTimer = kBlockMoveTime;
        }
      }
    }
    
  } // Sprite.advance()

  // display the blocks
  @Override
  public void draw(EgaCanvas canvas) {
    
    for ( BlockArray b : mBlocks ) b.draw(canvas, mCamera);
    
  } // Sprite.draw()
  
} // class BlockStairs
