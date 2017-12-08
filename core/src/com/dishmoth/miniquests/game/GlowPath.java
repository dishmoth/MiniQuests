/*
 *  GlowPath.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.ArrayList;
import java.util.LinkedList;

// a path of tiles that lights up as they are walked on
public class GlowPath extends Sprite3D {

  // time delay as the path resets
  private static final int kTimePathRetreat = 5;
  
  // list of positions (x,y in block coords; z in pixels) on the path
  private ArrayList<int[]> mPath;

  // colour of the tiles when lit
  final private byte mColour;
  
  // reference to the current player object
  private Player mPlayer;
  
  // current head of the path
  private int mPathIndex;

  // delay time as the path resets
  private int mRetreatTimer;

  // constructor (pattern: 'X' => start of path, '+' => rest of path)
  public GlowPath(String pattern[],
                  int xOffset, int yOffset, int zOffset,
                  char colour) {
    
    buildPath(pattern, xOffset, yOffset, zOffset);
    
    mColour = EgaTools.decodePixel(colour);

    mPlayer = null;
    
    mPathIndex = 0;
    mRetreatTimer = 0;
    
  } // constructor

  // whether a position is on the path
  private boolean onPath(String pattern[], int ix, int iy) {
    
    if ( iy < 0 || iy >= pattern.length ) return false;
    String row = pattern[ pattern.length-1-iy ];
    if ( ix < 0 || ix >= row.length() ) return false;
    char ch = row.charAt(ix);
    return ( ch != ' ' );
    
  } // onPath()
  
  // contruct the path coordinates
  private void buildPath(String pattern[], 
                         int xOffset, int yOffset, int zOffset) {
    
    mPath = new ArrayList<int[]>();
    
    assert( pattern != null );
    for ( int j = 0 ; j <= pattern.length ; j++ ) {
      assert( pattern[j] != null && pattern[j].length() > 0 );
      int i = pattern[j].indexOf('X');
      if ( i != -1 ) {
        int start[] = { xOffset+i, yOffset+(pattern.length-1-j), zOffset };
        mPath.add(start);
        break;
      }
    }
    assert( mPath.size() > 0 );
    
    for ( int index = 0 ; ; index++ ) {
      
      int x = mPath.get(index)[0],
          y = mPath.get(index)[1],
          z = mPath.get(index)[2];
      
      int ix = x - xOffset,
          iy = y - yOffset;
      if ( onPath(pattern, ix+1, iy) && 
           (index==0 || !checkPath(index-1,x+1,y,z)) ) {
        mPath.add(new int[]{x+1,y,z});
      } else if ( onPath(pattern, ix-1, iy) && 
                  (index==0 || !checkPath(index-1,x-1,y,z)) ) {
        mPath.add(new int[]{x-1,y,z});
      } else if ( onPath(pattern, ix, iy+1) && 
                  (index==0 || !checkPath(index-1,x,y+1,z)) ) {
        mPath.add(new int[]{x,y+1,z});
      } else if ( onPath(pattern, ix, iy-1) && 
                  (index==0 || !checkPath(index-1,x,y-1,z)) ) {
        mPath.add(new int[]{x,y-1,z});
      } else {
        break;
      }
      
    }
    
  } // buildPath()
  
  // make the path all light up
  public void setComplete() { mPathIndex = mPath.size()-1; }
  
  // whether the path is complete or not
  public boolean complete() { return (mPathIndex == mPath.size()-1); }

  // returns the current path position
  public int index() { return mPathIndex; }
  
  // modify the current path position
  public void setIndex(int i) { assert(i>=0 && i<mPath.size()); mPathIndex=i; }

  // whether a particular position is on the path
  public boolean includes(int x, int y, int z) {
    
    for ( int xyz[] : mPath ) {
      if ( xyz[0] == x && xyz[1] == y && xyz[2] == z ) return true;
    }
    return false;
    
  } // includes()
  
  // keep track of the player object appearing
  @Override
  public void observeArrival(Sprite newSprite) { 
    
    if ( newSprite instanceof Player ) {
      assert( mPlayer == null );
      mPlayer = (Player)newSprite;
    } else {
      super.observeArrival(newSprite);
    }
    
  } // Sprite.observeArrival()
  
  // keep track of the player object disappearing
  @Override
  public void observeDeparture(Sprite deadSprite) {

    if ( deadSprite instanceof Player ) {
      assert( deadSprite == mPlayer );
      mPlayer = null;
    } else {
      super.observeDeparture(deadSprite);
    }
  
  } // Sprite.observeDeparture()
  
  // nothing to do here (the path updates during the 'aftermath' phase)
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {}

  // whether a path position has the expected value
  private boolean checkPath(int index, int x, int y, int z) {
    
    assert( index >= 0 && index < mPath.size() );
    int pos[] = mPath.get(index);
    assert( pos != null && pos.length == 3 );
    return ( pos[0] == x && pos[1] == y && pos[2] == z );
    
  } // checkPath()
  
  // update the path based on the player's position
  @Override
  public void aftermath(LinkedList<Sprite>     addTheseSprites, 
                        LinkedList<Sprite>     killTheseSprites,
                        LinkedList<StoryEvent> newStoryEvents) {
    
    if ( complete() ) return;
    
    int x = ( (mPlayer!=null) ? mPlayer.getXPos() : -1 ),
        y = ( (mPlayer!=null) ? mPlayer.getYPos() : -1 ),
        z = ( (mPlayer!=null) ? mPlayer.getZPos() : -1 );
    if ( checkPath(mPathIndex, x, y, z) ) {
      
      mRetreatTimer = kTimePathRetreat;
      
    } else if ( checkPath(mPathIndex+1, x, y, z) ) {
      
      mPathIndex += 1;
      mRetreatTimer = kTimePathRetreat;
      
    } else {
      
      if ( --mRetreatTimer <= 0 ) {
        mRetreatTimer = kTimePathRetreat;
        if ( mPathIndex > 0 ) mPathIndex -= 1;
      }
      
    }
    
  } // Sprite.aftermath()
  
  // display the glowing tiles
  @Override
  public void draw(EgaCanvas canvas) {

    final float depthOffset = -0.003f;
    
    for ( int k = 0 ; k <= mPathIndex; k++ ) {
      
      final int xyz[] = mPath.get(k);
      final int x0 = xyz[0] - mCamera.xPos(),
                y0 = xyz[1] - mCamera.yPos(),
                z0 = xyz[2] - mCamera.zPos();

      final int depth = x0 + y0;
      final int xPixel = Env.originXPixel() + 2*x0 - 2*y0,
                yPixel = Env.originYPixel() - depth - z0;  
    
      canvas.fill(xPixel, xPixel+1, yPixel, yPixel, 
                  depth+depthOffset, mColour);
      
    }
    
  } // Sprite.draw()

} // GlowPath
