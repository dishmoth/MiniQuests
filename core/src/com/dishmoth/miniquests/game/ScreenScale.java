/*
 *  ScreenScale.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

// class for keeping track of the pixel scaling factor (overridden for Android)
public class ScreenScale {

  // the current pixel scaling
  private int mScale;
  
  // constructor
  public ScreenScale() {
    
    mScale = 1;
    
  } // constructor
  
  // update the scale for the current screen size
  public void refresh(int width, int height) {
    
    int xScale = (int)Math.floor( width / (float)Env.screenWidth() ),
        yScale = (int)Math.floor( height /(float)Env.screenHeight() );
    mScale = Math.max(1, Math.min(xScale, yScale));
    
  } // refresh()
  
  // the current pixel size for the game screen
  public int scale() { return mScale; }
  
  // the current pixel scale after updating the screen size
  public int scale(int width, int height) {
    
    refresh(width, height);
    return scale();
    
  } // scale()
  
  // return the range that the screen size value can take (Android only)
  public int maxSizeVal() { assert(false); return -1; }
  public int minSizeVal() { assert(false); return -1; }

  // return the current screen size value (Android only)
  public int sizeVal() { assert(false); return -1; }

  // change the size value (Android only)
  public void setSizeVal(int size) { assert(false); }
  
} // class ScreenScale
