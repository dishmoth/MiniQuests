/*
 *  BlockPattern.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// a changing arrangement of blocks
public class BlockPattern extends BlockArray {

  // pattern showing when blocks appear and disappear
  // character order: '0', ..., '9', 'A', ..., 'Z', 'a', ..., 'z'
  private String[] mPattern;

  // position for the pattern
  private int mXPosPattern,
              mYPosPattern,
              mZPosPattern;
  
  // range of values in the pattern (inclusive)
  private int mMaxVal,
              mMinVal;
  
  // range of blocks to be shown (inclusive)
  private int mStart,
              mEnd;
  
  // how range changes over time (+delay, -delay, or zero)
  private int mStartRate,
              mEndRate;
  
  // count frames until the range changes
  private int mStartTimer,
              mEndTimer;
  
  // constructor (none of the pattern is visible initially)
  public BlockPattern(String[] pattern, String blockColour,
                      int x, int y, int z) {
  
    super(null, new String[]{blockColour}, x, y, z);
    prepare(pattern, x, y, z);
    
    mStart = mEnd = -1;
    mStartRate = mEndRate = 0;
    mStartTimer = mEndTimer = 0;
    
    updateBlocks();

  } // constructor

  // record the pattern and determine its minimum and maximum values
  private void prepare(String[] pattern, int x, int y, int z) {
    
    assert( pattern != null );
    mPattern = pattern;

    mMinVal = mMaxVal = -1;
    for ( int iy = 0 ; iy < mPattern.length ; iy++ ) {
      String row = mPattern[iy];
      assert( row != null && row.length() == mPattern[iy].length() );
      for ( int ix = 0 ; ix < row.length() ; ix++ ) {
        int val = charValue( row.charAt(ix) );
        if ( val >= 0 ) {
          if ( mMinVal == -1 || val < mMinVal ) mMinVal = val;
          if ( mMaxVal == -1 || val > mMaxVal ) mMaxVal = val;
        }
      }
    }
    assert( mMinVal >= 0 && mMaxVal >= 0 );
    
    mXPosPattern = x;
    mYPosPattern = y;
    mZPosPattern = z;
    
  } // prepare()
  
  // construct blocks from the pattern
  public void updateBlocks() {
    
    if ( mStart > mEnd || mStart > mMaxVal || mEnd < mMinVal ) {
      setBlocks(null, mXPosPattern, mYPosPattern, mZPosPattern);
      return;
    }
    
    final int xSize = mPattern[0].length(), 
              ySize = mPattern.length;
    int xMin = xSize,
        xMax = 0,
        yMin = ySize,
        yMax = 0;
    for ( int iy = 0 ; iy < ySize ; iy++ ) {
      StringBuilder row = new StringBuilder( mPattern[iy] );
      for ( int ix = 0 ; ix < xSize ; ix++ ) {
        int val = charValue( row.charAt(ix) );
        if ( val >= 0 && val >= mStart && val <= mEnd ) {
          xMin = Math.min(xMin, ix);
          xMax = Math.max(xMax, ix);
          yMin = Math.min(yMin, iy);
          yMax = Math.max(yMax, iy);
        }
      }
    }
    assert( xMin <= xMax && yMin <= yMax );
    
    String blocks[][] = new String[1][yMax-yMin+1];
    for ( int iy = yMin ; iy <= yMax ; iy++ ) {
      String str = mPattern[iy].substring(xMin,xMax+1);
      StringBuilder row = new StringBuilder(str);
      for ( int ix = xMin ; ix <= xMax ; ix++ ) {
        int index = ix - xMin;
        int val = charValue( row.charAt(index) );
        if ( val >= 0 ) {
          char ch = ( val >= mStart && val <= mEnd ) ? '0' : ' ';
          row.setCharAt(index, ch);
        }
      }
      blocks[0][iy-yMin] = row.toString();
    }
    
    setBlocks(blocks, 
              mXPosPattern + xMin,
              mYPosPattern + (ySize-1-yMax),
              mZPosPattern);
    
  } // makeBlocks()

  // access to the extremes of the pattern
  public int minValue() { return mMinVal; }
  public int maxValue() { return mMaxVal; }

  // access to the current range
  public int start() { return mStart; }
  public int end() { return mEnd; }

  // access to the current rates
  public int startRate() { return mStartRate; }
  public int endRate() { return mEndRate; }

  // specify the current range
  public void setRange(int s, int e) { 
    
    mStart = s; 
    mEnd = e; 
    updateBlocks(); 
    
  } // setRange()

  // specify how the start value changes over time (+delay, -delay, or zero)
  public void setStartRate(int v) {
    
    if ( mStartRate != v ) {
      mStartRate = v;
      mStartTimer = 0;
    }
    
  } // setStartRate()
  
  // specify how the end value changes over time (+delay, -delay, or zero)
  public void setEndRate(int v) {
    
    if ( mEndRate != v ) {
      mEndRate = v;
      mEndTimer = 0;
    }
    
  } // setStartRate()
  
  // access to position
  @Override public int getXPos() { return mXPosPattern; }
  @Override public int getYPos() { return mYPosPattern; }
  @Override public int getZPos() { return mZPosPattern; }
  
  // shift position
  @Override
  public void shiftPos(int dx, int dy, int dz) {
    
    super.shiftPos(dx, dy, dz);
    mXPosPattern += dx;
    mYPosPattern += dy;
    mZPosPattern += dz;
    
  } // shiftPos()
  
  // set position
  @Override
  public void setPos(int x, int y, int z) {

    int dx = mXPosPattern - x,
        dy = mYPosPattern - y,
        dz = mZPosPattern - z;
    shiftPos(dx, dy, dz);
    
  } // setPos()
  
  // convert a character to its integer value
  static private int charValue(char ch) {
    
    if ( ch == ' ' ) return -1;
    
    if ( ch >= '0' && ch <= '9' ) return (ch - '0');
    if ( ch >= 'A' && ch <= 'Z' ) return (ch - 'A' + 10);
    if ( ch >= 'a' && ch <= 'z' ) return (ch - 'a' + 36);
    
    assert( false );
    return -1;
    
  } // charValue()
  
  // modify the blocks over time
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {
    
    boolean changed = false;
    
    if ( mStartRate > 0 ) {
      if ( ++mStartTimer >= mStartRate ) {
        mStartTimer = 0;
        mStart += 1;
        if ( mStart >= mMinVal+1 && mStart <= mMaxVal+1 ) changed = true;
      }
    } else if ( mStartRate < 0 ) {
      if ( --mStartTimer <= mStartRate ) {
        mStartTimer = 0;
        mStart -= 1;
        if ( mStart >= mMinVal && mStart <= mMaxVal ) changed = true;
      }
    }
    
    if ( mEndRate > 0 ) {
      if ( ++mEndTimer >= mEndRate ) {
        mEndTimer = 0;
        mEnd += 1;
        if ( mEnd >= mMinVal && mEnd <= mMaxVal ) changed = true;
      }
    } else if ( mEndRate < 0 ) {
      if ( --mEndTimer <= mEndRate ) {
        mEndTimer = 0;
        mEnd -= 1;
        if ( mEnd >= mMinVal-1 && mEnd <= mMaxVal-1 ) changed = true;
      }
    }
    
    if ( changed ) {
      updateBlocks();
    }
    
  } // Sprite.advance()

} // class BlockPattern
