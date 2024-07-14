/*
 *  FlameArea.java
 *  Copyright (c) 2018 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// flames over a rectangular area
public class FlameArea extends FlameParticles {

  // number of new particles per frame per area
  private static final float kParticleDensity = 0.2f;
  
  // particle physics
  private static final float kSpeed       = 0.5f,
                             kGravity     = 0.0f;
  private static final int   kLifeTimeMin = 10,
                             kLifeTimeMax = 15;
  
  // rough height of the flames (for collision checks)
  private static final float kFlameHeight = 5.0f; 
  
  // cycle behaviour times
  private static final int kTimeChange = 30;
  
  // colour scheme
  private static final byte kColours[] = { 40, 13, 21 }; // purple
  
  // bottom-left corner position of the area
  protected int mXPos,
                mYPos,
                mZPos;
  
  // full size of the area
  protected int mXSize,
                mYSize;
  
  // number of flaming squares
  protected int mArea;
  
  // which parts of the area have flames
  // ('O' = deadly flame, 'o' = harmless flame, '.' = deadly but no flame)
  protected String mPattern[] = null; 
  
  // how long the on/off cycle lasts (zero by default for no cycle)
  protected int mTimeOn,
                mTimeOff;
  
  // behaviour change time (increasing)
  protected int mTimer;
  
  // current strength based on time
  protected float mStrength;
  
  // constructor
  public FlameArea(int x, int y, int z, String pattern[]) {

    mXPos = x;
    mYPos = y;
    mZPos = z;
    
    setPattern(pattern);
    
    setPhysics(kSpeed, kGravity);
    setLifeTime(kLifeTimeMin, kLifeTimeMax);
    setColours(kColours);
    
    mIsOn = true;
    mTimeOn = mTimeOff = 0;
    mTimer = 0;
    updateStrength();
    
  } // constructor
  
  // update strength of flame and initial speed of particles
  protected void updateStrength() {

    if ( mTimer < kTimeChange ) {
      mStrength = mTimer/(float)kTimeChange;
    } else if ( mTimer < kTimeChange + mTimeOn ) {
      mStrength = 1.0f;
    } else if ( mTimer < 2*kTimeChange + mTimeOn ) {
      mStrength = (2*kTimeChange + mTimeOn - mTimer) / (float)kTimeChange;
    } else {
      mStrength = 0.0f;
    }
    
    mSpeed = kSpeed * mStrength;

  } // updateStrength()

  // set which parts of the area have flames
  // ('O' = deadly flame, 'o' = harmless flame, '.' = deadly but no flame)
  public void setPattern(String pattern[]) {

    assert( pattern != null && pattern[0] != null );

    mYSize = pattern.length;
    mXSize = pattern[0].length();
    
    mArea = 0;
    for ( int iy = 0 ; iy < mYSize ; iy++ ) {
      String row = pattern[iy];
      assert( row != null && row.length() == mXSize );
      for ( int ix = 0 ; ix < mXSize; ix++ ) {
        char ch = row.charAt(ix);
        assert( ch == ' ' || ch == 'O' || ch == 'o' || ch == '.' );
        if ( ch == 'O' || ch == 'o' ) mArea++;
      }
    }
    
    mPattern = pattern;
    
  } // setPattern()
  
  // set the flames to cycle on and off (zero values for infinite times)
  public void setTimeCycle(int timeOn, int timeOff) {

    assert( timeOn >= 0 && timeOff >= 0 );

    if ( mTimer < kTimeChange ) {
      // no change
    } else if ( mTimer < kTimeChange + mTimeOn ) {
      mTimer = kTimeChange;
    } else if ( mTimer < 2*kTimeChange + mTimeOn ) {
      mTimer += timeOn - mTimeOn;
    } else {
      mTimer = 2*kTimeChange + timeOn;
    }
    mTimeOn = timeOn;
    mTimeOff = timeOff;
    updateStrength();
    
  } // setTimeCycle()
  
  // change the state of the flame
  @Override
  public void setFlame(boolean on) {
    
    mIsOn = on;

    if ( mIsOn ) {
      if ( mTimer >= 2*kTimeChange + mTimeOn ) {
        mTimer = 0;
      } else if ( mTimer >= kTimeChange + mTimeOn ) {
        mTimer = 2*kTimeChange + mTimeOn - mTimer;
      }
    } else {
      if ( mTimer < kTimeChange ) {
        mTimer = 2*kTimeChange + mTimeOn - mTimer;
      } else if ( mTimer < kTimeChange + mTimeOn ) {
        mTimer = kTimeChange + mTimeOn;
      }
    }
    updateStrength();
    
  } // FlameParticles.setFlame()
  
  // run the flame for a bit
  @Override
  public void warmUp(int warmUpTime) {

    if ( mIsOn && mTimeOn == 0 ) mTimer = kTimeChange; 
    super.warmUp(warmUpTime);
        
  } // FlameParticles.warmUp()

  // how many particles to create each frame on average
  @Override
  protected float newParticlesPerFrame() {
    
    return (kParticleDensity * mArea * mStrength); 
    
  } // FlameParticles.newParticlesPerFrame()

  // get position and direction for a new particle
  @Override
  protected void newParticlePosAndDirec(float pos[], float direc[]) {

    assert( pos != null && pos.length == 3 );
    assert( direc != null && direc.length == 3 );

    int n = Env.randomInt(mArea);
    for ( int iy = 0 ; iy < mYSize && n >= 0 ; iy++ ) {
      String row = mPattern[mYSize - 1 - iy];
      for ( int ix = 0 ; ix < mXSize && n >= 0  ; ix++ ) {
        char ch = row.charAt(ix);
        if ( ch == 'O' || ch == 'o' ) {
          if ( n == 0 ) {
            pos[0] = mXPos + ix + Env.randomFloat();
            pos[1] = mYPos + iy + Env.randomFloat();
            pos[2] = mZPos;
          }
          n--;
        }
      }
    }
    assert( n < 0 );

    direc[0] = 0.0f;
    direc[1] = 0.0f;
    direc[2] = 1.0f;
    
  } // FlameParticles.newParticlePosAndDirec()
  
  // create more fire particles
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {

    super.advance(addTheseSprites, killTheseSprites, newStoryEvents);

    if ( mIsOn && mTimeOn == 0 ) {
      mTimer = Math.min(mTimer+1, kTimeChange);
    } else if ( !mIsOn && mTimeOff == 0 ) {
      mTimer = Math.min(mTimer+1, 2*kTimeChange+mTimeOn);
    } else if ( mIsOn || mTimer > 0 ) {
      if ( ++mTimer > mTimeOn + mTimeOff + 2*kTimeChange ) mTimer = 0;
    }
    updateStrength();
    
  } // Sprite3D.advance()

  // whether the flames hit a point
  public boolean hits(int x, int y, int z) {

    if ( !mIsOn || !mIsLethal || mStrength < 0.75f ) return false;
    
    if ( x < mXPos || x >= mXPos + mXSize ||
         y < mYPos || y >= mYPos + mYSize ||
         z < mZPos || z > mZPos + kFlameHeight ) return false;
    
    char ch = mPattern[mYPos+mYSize-1-y].charAt(x-mXPos);
    return ( ch == 'O' || ch == '.' );
    
  } // hits()

  // display the particles
  @Override
  public void draw(EgaCanvas canvas) {

    super.draw(canvas);
    
  } // Sprite3D.draw()

} // class FlameArea
