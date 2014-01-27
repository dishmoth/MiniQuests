/*
 *  FadeOut.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.Arrays;
import java.util.LinkedList;

// a shrinking ring of darkness for the map screen
public class FadeOut extends Sprite {

  // how fast the fade happens
  private static final int kFadeTime  = 25,
                           kFadeDelta =  5;
  
  // z-depth at which the fade is drawn
  private static final float kDepth = -1.0f;
  
  // time at which the pixels fade
  private int mFadeTimes[][];
  
  // the image for the fade
  private EgaImage mImage;
  
  // how long the fade has gone on for
  private int mTime;
  
  // whether time is advancing
  private boolean mPaused;
  
  // constructor
  public FadeOut() {

    final int numX = Env.screenWidth(),
              numY = Env.screenHeight();
    final float maxDist = (float)Math.sqrt(2);
    
    mFadeTimes = new int[numY][numX]; 
    for ( int iy = 0 ; iy < numY ; iy++ ) {
      for ( int ix = 0 ; ix < numX ; ix++ ) {
        float x = (ix+0.5f)/(0.5f*numX) - 1.0f,
              y = (iy+0.5f)/(0.5f*numY) - 1.0f;
        float d = (float)Math.hypot(x, y);
        float f = 1.0f - d/maxDist;
        float r = kFadeDelta*Env.randomFloat();
        int t = (int)Math.round(f*kFadeTime + r);
        mFadeTimes[iy][ix] = t;
      }
    }

    byte pixels[] = new byte[numX*numY];
    Arrays.fill(pixels, (byte)-1);
    mImage = new EgaImage(0,0, Env.screenWidth(),Env.screenHeight(), pixels);
    
    mTime = 0;
    mPaused = false;
    updateImage();
    
  } // constructor
  
  // stop or start the fade
  public void pause(boolean val) { mPaused = val; } 
  
  // retrieve the current time
  public int time() { return mTime; }
  
  // update the image's pixels
  private void updateImage() {

    int index = 0;
    byte pixels[] = mImage.pixels();
    
    for ( int iy = 0 ; iy < mFadeTimes.length ; iy++ ) {
      for ( int ix = 0 ; ix < mFadeTimes[iy].length ; ix++ ) {
        boolean faded = ( mTime > mFadeTimes[iy][ix] );
        pixels[index++] = (byte)( faded ? 0 : -1 );
      }
    }
    
  } // updateImage();
  
  // animate the fade
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {

    if ( !mPaused && mTime < kFadeTime+kFadeDelta+1 ) {
      mTime++;
      updateImage();
    }
    
  } // Sprite.advance()

  // display the fade
  @Override
  public void draw(EgaCanvas canvas) {
    
    mImage.draw(canvas, 0, 0, kDepth);

  } // Sprite.draw()

} // class FadeOut
