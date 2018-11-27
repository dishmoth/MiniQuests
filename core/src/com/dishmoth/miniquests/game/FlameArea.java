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
  private static final int kTimeOn     = 20,
                           kTimeOff    = 75,
                           kTimeChange = 30;
  
  // colour scheme
  private static final byte kColours[] = { 40, 13, 21 }; // purple
  
  // corner positions of the area
  protected float mXPos0,
                  mXPos1,
                  mYPos0,
                  mYPos1,
                  mZPos;
  
  // behaviour change time (increasing)
  protected int mTimer;
  
  // current strength based on time
  protected float mStrength;
  
  // constructor
  public FlameArea(float x0, float x1, float y0, float y1, float z) {

    mXPos0 = Math.min(x0, x1);
    mXPos1 = Math.max(x0, x1);
    mYPos0 = Math.min(y0, y1);
    mYPos1 = Math.max(y0, y1);
    mZPos  = z;
    
    setPhysics(kSpeed, kGravity);
    setLifeTime(kLifeTimeMin, kLifeTimeMax);
    setColours(kColours);
    
    mIsOn = true;
    mTimer = 0;
    updateStrength();
    
  } // constructor
  
  // update strength of flame and initial speed of particles
  protected void updateStrength() {

    if ( mTimer < kTimeChange ) {
      mStrength = mTimer/(float)kTimeChange;
    } else if ( mTimer < kTimeChange + kTimeOn ) {
      mStrength = 1.0f;
    } else if ( mTimer < 2*kTimeChange + kTimeOn ) {
      mStrength = (2*kTimeChange + kTimeOn - mTimer) / (float)kTimeChange;
    } else {
      mStrength = 0.0f;
    }
    
    mSpeed = kSpeed * mStrength;

  } // updateStrength()

  // change position
  public void setArea(float x0, float x1, float y0, float y1, float z) {

    mXPos0 = Math.min(x0, x1);
    mXPos1 = Math.max(x0, x1);
    mYPos0 = Math.min(y0, y1);
    mYPos1 = Math.max(y0, y1);
    mZPos  = z;
  
  } // setArea()
  
  // change the state of the flame
  @Override
  public void setFlame(boolean on) {
    
    mIsOn = on;

    if ( !mIsOn ) {
      if ( mTimer < kTimeChange ) {
        mTimer = 2*kTimeChange + kTimeOn - mTimer;
      } else if ( mTimer < kTimeChange + kTimeOn ) {
        mTimer = kTimeChange + kTimeOn;
      }
    }
    
  } // FlameParticles.setFlame()
  
  // how many particles to create each frame on average
  @Override
  protected float newParticlesPerFrame() {
    
    final float area = Math.abs((mXPos1 - mXPos0) * (mYPos1 - mYPos0));
    return (kParticleDensity * area * mStrength); 
    
  } // FlameParticles.newParticlesPerFrame()

  // get position and direction for a new particle
  @Override
  protected void newParticlePosAndDirec(float pos[], float direc[]) {

    assert( pos != null && pos.length == 3 );
    assert( direc != null && direc.length == 3 );

    pos[0] = Env.randomFloat(mXPos0, mXPos1);
    pos[1] = Env.randomFloat(mYPos0, mYPos1);
    pos[2] = mZPos;

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

    if ( mIsOn || mTimer > 0 ) {
      if ( ++mTimer > kTimeOn + kTimeOff + 2*kTimeChange ) mTimer = 0;
      updateStrength();
    }
    
  } // Sprite3D.advance()

  // whether the flames hit a point (within specified tolerances)
  public boolean hits(float x, float y, float z, float xyTol, float zTol) {

    if ( !mIsOn || mStrength < 0.75f ) return false;
    
    return ( x > mXPos0 - xyTol && x < mXPos1 + xyTol && 
             y > mYPos0 - xyTol && y < mYPos1 + xyTol &&
             z > mZPos - zTol && z < mZPos + kFlameHeight + zTol );
    
  } // hits()

  // display the particles
  @Override
  public void draw(EgaCanvas canvas) {

    super.draw(canvas);
    
  } // Sprite3D.draw()

} // class FlameArea
