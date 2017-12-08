/*
 *  EgaCanvas.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.Arrays;

// screen image based on pixels in EGA format
public class EgaCanvas extends EgaImage {
  
  // number of different EGA colours used (for sanity checking)
  private int mMaxDistinctColours;
  
  // constructor
  public EgaCanvas(int width, int height) {

    super(0, 0, width, height);
    
    clear();
    
    mMaxDistinctColours = 0;
    
  } // constructor

  // clear the canvas (pixels and depths)
  public void clear() {
    
    Arrays.fill(mPixels, (byte)63);
    Arrays.fill(mDepths, 1.0e6f);
    
  } // clear()
  
  // check that no more than 16 different pixel colours are used at once
  public void checkColourCount() {

    final int numDistinctColours = EgaTools.numDistinctColours(this);
    
    if ( numDistinctColours > mMaxDistinctColours ) {
      mMaxDistinctColours = numDistinctColours;
      Env.debug("Max distinct EGA colours: " + numDistinctColours);
      assert( mMaxDistinctColours <= 16 );
    }
    
  } // checkColourCount()
  
} // class EgaCanvas
