/*
 *  Flame.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// a deadly patch of fire
public class Flame extends FlameParticles {

  // particle dynamics (different physics modes)
  private static final float kSpeeds[]    = { 0.06f, 0.08f },
                             kGravities[] = { 0.01f, 0.01f };

  // life of a particle (different physics modes)
  private static final int kLifeTimeMin[] = { 15, 15 },
                           kLifeTimeMax[] = { 25, 25 };
  
  // changing colour
  private static final byte kColours[][] = { { 54, 52, 4 },   // fire 1
                                             { 54, 38, 4 } }; // fire 2 
  
  // new particles per frame (physics mode)
  private static final float kParticleRates[] = { 2.0f, 4.0f };

  // basic height of the flame
  private static final float kHeight = 2.5f;
  
  // new particles per frame per unit length of the beam
  private float mParticleRate;
  
  // start and end positions of beam
  protected float mXPos,
                  mYPos,
                  mZPos;

  // constructor
  public Flame(float x, float y, float z) {

    mXPos = x;
    mYPos = y;
    mZPos = z;
    
    setPhysicsMode(0);
    setColourScheme(0);
    
    mIsOn = true;
    
  } // constructor

  // choose the behaviour of the particles
  public void setPhysicsMode(int mode) {

    assert( mode >= 0 && mode < kParticleRates.length );
    
    setPhysics( kSpeeds[mode], kGravities[mode] );
    setLifeTime( kLifeTimeMin[mode], kLifeTimeMax[mode] );

    mParticleRate = kParticleRates[mode];
    
  } // setPhysicsMode()

  // choose the colour of the particles
  public void setColourScheme(int scheme) {
    
    assert( scheme >= 0 && scheme < kColours.length );
    setColours( kColours[scheme] );
    
  } // setColourScheme()
  
  // how many particles to create each frame on average
  @Override
  protected float newParticlesPerFrame() {
    
    return mParticleRate;
    
  } // FlameParticles.newParticlesPerFrame()

  // get position and direction for a new particle
  @Override
  protected void newParticlePosAndDirec(float pos[], float direc[]) {

    assert( pos != null && pos.length == 3 );
    assert( direc != null && direc.length == 3 );
    
    final float h = kHeight*Env.randomFloat();
    pos[0] = mXPos;
    pos[1] = mYPos;
    pos[2] = mZPos + h;

    float dx=0.0f, dy=0.0f, dz=0.0f;
    do {
      dx = Env.randomFloat(-1.0f, +1.0f);
      dy = Env.randomFloat(-1.0f, +1.0f);
      dz = Env.randomFloat( 0.0f, +1.0f);
    } while ( dx*dx + dy*dy + dz*dz > 1.0f );
    
    direc[0] = dx;
    direc[1] = dy;
    direc[2] = dz;
    
  } // FlameParticles.newParticlePosAndDirec()
  
  // create more fire particles
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {

    super.advance(addTheseSprites, killTheseSprites, newStoryEvents);

  } // Sprite3D.advance()

  // whether the beam hits a point (within certain tolerances)
  public boolean hits(float x, float y, float z, float xyTol, float zTol) {

    if ( !mIsOn ) return false;
    
    assert( xyTol > 0 && zTol > 0 );
    
    final float dx = x - mXPos,
                dy = y - mYPos,
                dz = z - mZPos;
    final float xyDist2 = dx*dx + dy*dy,
                zDist   = Math.abs(dz);
    return ( xyDist2 <= xyTol*xyTol && zDist <= zTol );
    
  } // hits()
  
  // display the particles
  @Override
  public void draw(EgaCanvas canvas) {

    super.draw(canvas);
    
  } // Sprite3D.draw()

} // class FlameBeam
