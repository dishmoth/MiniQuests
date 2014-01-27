/*
 *  FlameBeam.java
 *  Copyright Simon Hern 2010
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// a deadly line of fire
public class FlameBeam extends FlameParticles {

  // particle dynamics (different physics modes)
  private static final float kSpeeds[]    = { 0.03f, 0.06f, 0.06f },
                             kGravities[] = { 0.00f, 0.01f, 0.01f };

  // life of a particle (different physics modes)
  private static final int kLifeTimeMin[] = {  5, 15, 15 },
                           kLifeTimeMax[] = { 15, 25, 25 };
  
  // changing colour
  private static final byte kColours[][] = { { 54, 52,  4 },   // fire 1
                                             { 54, 38,  4 },   // fire 2 
                                             { 37, 57, 40 } }; // purple
  
  // new particles per frame per unit length of the beam (physics mode)
  private static final float kParticleDensities[] = { 3.0f, 1.0f, 2.0f };

  // new particles per frame per unit length of the beam
  private float mParticleDensity;
  
  // start and end positions of beam
  protected float mXPos0,
                  mYPos0,
                  mZPos0;
  protected float mXPos1,
                  mYPos1,
                  mZPos1;

  // constructor
  public FlameBeam(float x0, float y0, float z0,
                   float x1, float y1, float z1) {

    mXPos0 = x0;
    mYPos0 = y0;
    mZPos0 = z0;
    
    mXPos1 = x1;
    mYPos1 = y1;
    mZPos1 = z1;

    setPhysicsMode(1);
    setColourScheme(0);
    
    mIsOn = true;
    
  } // constructor

  // choose the behaviour of the particles
  public void setPhysicsMode(int mode) {

    assert( mode >= 0 && mode < kParticleDensities.length );
    
    setPhysics( kSpeeds[mode], kGravities[mode] );
    setLifeTime( kLifeTimeMin[mode], kLifeTimeMax[mode] );

    mParticleDensity = kParticleDensities[mode];
    
  } // setPhysicsMode()

  // choose the colour of the particles
  public void setColourScheme(int scheme) {
    
    assert( scheme >= 0 && scheme < kColours.length );
    setColours( kColours[scheme] );
    
  } // setColourScheme()
  
  // how many particles to create each frame on average
  @Override
  protected float newParticlesPerFrame() {
    
    final float len = (float)Math.sqrt( (mXPos1-mXPos0)*(mXPos1-mXPos0) 
                                      + (mYPos1-mYPos0)*(mYPos1-mYPos0)
                                      + (mZPos1-mZPos0)*(mZPos1-mZPos0) );
    return len*mParticleDensity;
    
  } // FlameParticles.newParticlesPerFrame()

  // get position and direction for a new particle
  @Override
  protected void newParticlePosAndDirec(float pos[], float direc[]) {

    assert( pos != null && pos.length == 3 );
    assert( direc != null && direc.length == 3 );
    
    final float t = Env.randomFloat();
    final float xDelta = mXPos1 - mXPos0,
                yDelta = mYPos1 - mYPos0,
                zDelta = mZPos1 - mZPos0;
    final float delta2 = xDelta*xDelta + yDelta*yDelta + zDelta*zDelta;
    
    pos[0] = mXPos0 + t*xDelta;
    pos[1] = mYPos0 + t*yDelta;
    pos[2] = mZPos0 + t*zDelta;

    float dx=0.0f, dy=0.0f, dz=0.0f;
    do {
      dx = Env.randomFloat(-1.0f, +1.0f);
      dy = Env.randomFloat(-1.0f, +1.0f);
      dz = Env.randomFloat(-1.0f, +1.0f);
    } while ( dx*dx + dy*dy + dz*dz > 1.0f );
      
    final float dot = dx*xDelta + dy*yDelta + dz*zDelta;
    final float fac = dot/delta2;
    dx -= fac*xDelta;
    dy -= fac*yDelta;
    dz -= fac*zDelta;
    
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
    
    final float dx = mXPos1 - mXPos0,
                dy = mYPos1 - mYPos0,
                dz = mZPos1 - mZPos0;
    final float d2 = dx*dx + dy*dy + dz*dz;
    
    float t = ( d2 > 1.0e-4 ) 
              ? ( (x-mXPos0)*dx + (y-mYPos0)*dy + (z-mZPos0)*dz )/d2
              : 0.5f;
    t = Math.max(0.0f, Math.min(1.0f, t));

    final float xMin = mXPos0 + t*dx,
                yMin = mYPos0 + t*dy,
                zMin = mZPos0 + t*dz;

    final float xyDist2 = (xMin-x)*(xMin-x) + (yMin-y)*(yMin-y),
                zDist   = Math.abs(zMin-z);
    return ( xyDist2 <= xyTol*xyTol && zDist <= zTol );
    
  } // hits()
  
  // display the particles
  @Override
  public void draw(EgaCanvas canvas) {

    super.draw(canvas);
    
  } // Sprite3D.draw()

} // class FlameBeam
