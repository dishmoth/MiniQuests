/*
 *  FlameParticles.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// particles that look a bit like fire
abstract public class FlameParticles extends Particles {

  // run the flame for a few frames (usually when it is created)
  protected static final int kWarmUpTime = 10;
  
  // maximum particle speed
  protected float mSpeed;

  // particle acceleration
  protected float mGravity;
  
  // how long particles live for
  protected int mLifeTimeMin,
                mLifeTimeMax;

  // colour changes during lifetime
  protected byte mColours[];
  
  // whether the flame is currently active
  protected boolean mIsOn;

  // constructor
  public FlameParticles() {

    super(-1);
    
    mSpeed = 0.06f;
    mGravity = 0.01f;
    
    mLifeTimeMin = 15;
    mLifeTimeMax = 25;

    mColours = new byte[]{ 54, 52, 4 }; 
    
    mIsOn = true;
    
  } // constructor
  
  // whether the flame is currently burning
  public boolean isOn() { return mIsOn; }
  
  // change the state of the flame
  public void setFlame(boolean on) { mIsOn = on; }
  
  // change physical constants for the particles
  public void setPhysics(float speed, float gravity) {
  
    mSpeed = speed;
    mGravity = gravity;
    
  } // setPhysics()
  
  // change lifetime constants for the particles
  public void setLifeTime(int min, int max) {
    
    mLifeTimeMin = min;
    mLifeTimeMax = max;
    
  } // setLifeTime()
  
  // change the colour scheme for the particles
  public void setColours(byte colours[]) {
    
    mColours = colours;
    
  } // setColours()
  
  // run the flame for a bit
  public void warmUp(int warmUpTime) {

    assert( warmUpTime > 0 );
    for ( int k = 0 ; k < warmUpTime ; k++ ) {
      advance(null, null, null);
    }
        
  } // warmUp()
  
  // run the flame for a bit
  public void warmUp() { warmUp(kWarmUpTime); }
  
  // how many particles to create each frame on average
  abstract protected float newParticlesPerFrame();

  // get position and direction for a new particle
  abstract protected void newParticlePosAndDirec(float pos[], float direc[]);
  
  // create a new particle
  protected void addParticle() {

    float pos[] = new float[3];
    float direc[] = new float[3];

    newParticlePosAndDirec(pos, direc);
    
    float xVel = mSpeed*direc[0];
    float yVel = mSpeed*direc[1];
    float zVel = mSpeed*direc[2];
    
    Particle particle = new Particle(pos[0], pos[1], pos[2], 
                                     xVel, yVel, zVel, 
                                     mGravity, 
                                     Env.randomInt(mLifeTimeMin, mLifeTimeMax),
                                     mColours[0]);
    add(particle);
    
  } // addParticle()
  
  // create more fire particles
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {

    if ( mIsOn ) {
      float numParticles = newParticlesPerFrame();
      while ( numParticles > 1.0f ) {
        addParticle();
        numParticles--;
      }
      if ( Env.randomFloat() < numParticles ) addParticle();
    }
    
    super.advance(addTheseSprites, killTheseSprites, newStoryEvents);

    for ( Particle p : mParticles ) {
      final float age = p.age() / (float)p.lifeTime();
      assert( age >= 0.0f && age < 1.0f );
      final int colIndex = (int)Math.floor( age*mColours.length );
      p.setColour( mColours[colIndex] );
    }
    
  } // Sprite3D.advance()

  // display the particles
  @Override
  public void draw(EgaCanvas canvas) {

    super.draw(canvas);
    
  } // Sprite3D.draw()

} // class FlameParticles
