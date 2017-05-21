/*
 *  ScreenScale.java
 *  Copyright Simon Hern 2017
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import com.badlogic.gdx.Gdx;
import com.dishmoth.miniquests.gdx.KeyMonitorGdx;

// small class for keeping track of the pixel scaling factor
public class ScreenScale {

  // target size for game screen relative to full display
  private static final float kShrinkTouchscreen = 0.75f,
                             kShrinkTelevision  = 0.85f;
  
  // maximum that the (saved) screen size value can take
  private static final int kMaxSizeVal = 20;
  
  // minimum that the (saved) screen size value can take
  private int mMinSizeVal;
  
  // range that the pixel scale for the game screen can take
  private int mMaxScale,
              mMinScale;
  
  // the size value translates to the pixel scale by mixing big and small steps 
  private int mNumStepsSmall,
              mNumStepsBig;
  private int mStepSmall,
              mStepBig;
  
  // constructor
  public ScreenScale() {
    
    mMinSizeVal = 0;
    mMaxScale = mMinScale = 0;
    mNumStepsSmall = mNumStepsBig = 0;
    mStepSmall = mStepBig = 0;
    
  } // constructor
  
  // calculate the pixel scale for a given size value
  private int scale(int size) {
    
    if ( mMaxScale == 0 ) refresh();
    
    int steps = size - mMinSizeVal;
    int scale = mMinScale
              + Math.min(steps, mNumStepsBig) * mStepBig
              + Math.max(0, steps - mNumStepsBig) * mStepSmall;
    assert( scale >= mMinScale && scale <= mMaxScale );
    return scale;
    
  } // scale()
  
  // the current pixel size for the game screen
  public int scale() { return scale( sizeVal() ); }
  
  // return the range that the screen size value can take
  public int maxSizeVal() { return kMaxSizeVal; }
  public int minSizeVal() { return mMinSizeVal; }

  // return the current screen size value
  public int sizeVal() {
    
    int size = Env.saveState().screenSize();
    if ( size < 0 ) {
      refresh();
      size = defaultSizeVal();
      Env.saveState().setScreenSize(size);
      Env.debug("Pixel scale: default " + scale() + " (size val " + size + ")");
    }
    if ( size < mMinSizeVal || size > kMaxSizeVal ) {
      size = Math.max(mMinSizeVal, Math.min(kMaxSizeVal, size));
      Env.saveState().setScreenSize(size);
    }
    return size;
    
  } // sizeVal()

  // change the size value
  public void setSizeVal(int size) {

    size = Math.max(mMinSizeVal, Math.min(kMaxSizeVal, size));
    Env.saveState().setScreenSize( size );

  } // setSizeVal()
  
  // choose the default pixel scaling
  private float defaultScale() {

    final float shrink =
                  (Env.platform()==Env.Platform.ANDROID) ? kShrinkTouchscreen
                : (Env.platform()==Env.Platform.OUYA)    ? kShrinkTelevision
                                                         : 1.0f;
    float scale = shrink * mMaxScale;

    if ( Env.platform() == Env.Platform.ANDROID ) {
      KeyMonitorGdx keyMonitor = (KeyMonitorGdx)Env.keys();
      if ( keyMonitor.usingButtons() ) {
        int maxWidth  = Gdx.graphics.getWidth() - 2*keyMonitor.buttonsXSize(),
            maxHeight = Gdx.graphics.getHeight() - 2*keyMonitor.buttonsYSize();
        maxWidth = Math.max(maxWidth, Gdx.graphics.getWidth()/3);
        maxHeight = Math.max(maxHeight, Gdx.graphics.getHeight()/3);
        int xScale = (int)Math.floor( maxWidth / (float)Env.screenWidth() ),
            yScale = (int)Math.floor( maxHeight / (float)Env.screenHeight() );
        scale = Math.min(scale, Math.max(xScale, yScale));
      }
    }

    return scale;
    
  } // defaultScale()
  
  // choose the default size value
  private int defaultSizeVal() {
    
    final float targetScale = defaultScale();

    for ( int size = mMinSizeVal ; size < kMaxSizeVal ; size++ ) {
      int scale0 = scale(size),
          scale1 = scale(size+1);
      if ( scale1 >= targetScale ) {
        if ( Math.abs(scale1-targetScale) < Math.abs(scale0-targetScale) ) {
          return size+1;
        } else {
          return size;
        }
      }
    }

    return kMaxSizeVal;
    
  } // defaultSizeVal()
  
  // update the factors for conversion from size value to pixel scale
  public void refresh() {
    
    int xScale = (int)Math.floor( Gdx.graphics.getWidth() 
                                  / (float)Env.screenWidth() ),
        yScale = (int)Math.floor( Gdx.graphics.getHeight()
                                  / (float)Env.screenHeight() );
    
    mMaxScale = Math.max(1, Math.min(xScale, yScale));

    if ( Env.platform() == Env.Platform.ANDROID || 
         Env.platform() == Env.Platform.OUYA ) {
      mMinScale = Math.max(1, (int)Math.ceil(mMaxScale / 50.0));
      Env.debug("Pixel scale: range " + mMinScale + " to " + mMaxScale);
    } else {
      mMinScale = mMaxScale;
      Env.debug("Pixel scale: " + mMaxScale);
    }
        
    int numSteps = Math.min(kMaxSizeVal, mMaxScale - mMinScale);
    mMinSizeVal = kMaxSizeVal - numSteps;
    
    mStepSmall = (mMaxScale - mMinScale) / Math.max(1, numSteps);
    mStepBig   = mStepSmall + 1;
    
    mNumStepsBig   = (mMaxScale - mMinScale) - numSteps*mStepSmall;
    mNumStepsSmall = numSteps - mNumStepsBig;
    
    assert( mNumStepsSmall >= 0 && mNumStepsBig >= 0 );
    assert( mMinScale + mNumStepsBig*mStepBig + mNumStepsSmall*mStepSmall
            == mMaxScale );
    
  } // refresh()
  
} // class ScreenScale
