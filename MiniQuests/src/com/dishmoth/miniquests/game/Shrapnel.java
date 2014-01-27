/*
 *  Shrapnel.java
 *  Copyright Simon Hern 2010
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

// debris from a Bullet hitting an obstacle
public class Shrapnel extends Particles {

  // number of particles
  private static final int kNumParticles = 3;
  
  // particle dynamics
  private static final float kSpeed   = 0.15f,
                             kGravity = 0.05f;
  
  // angle away from direct reflection (radians)
  private static final float kBreakAngle = 1.0f;
  private static final float kBreakXVel  = kSpeed*(float)Math.cos(kBreakAngle),
                             kBreakYZVel = kSpeed*(float)Math.sin(kBreakAngle);
  
  // particles don't collide initially
  private static final int kCollisionDelay = 8;
  
  // life of a particle
  private static final int kLifeTimeMin = 5,
                           kLifeTimeMax = 8;
  
  // constructor (given position of bullet at collision)
  public Shrapnel(int x, int y, int z, int direc, 
                  boolean isWall, byte colour) {
    
    super(kCollisionDelay);
    
    float xPos = x,
          yPos = y,
          zPos = z;
    final float nudge = (isWall ? 0.1f : 0.6f);
    switch ( direc ) {
      case Env.RIGHT: { xPos -= nudge; } break;
      case Env.UP:    { yPos -= nudge; } break;
      case Env.LEFT:  { xPos += nudge; } break;
      case Env.DOWN:  { yPos += nudge; } break;
      default: { assert(false); }
    }

    final float twistStart = 2.0f*(float)Math.PI*Env.randomFloat();
    for ( int k = 0 ; k < kNumParticles ; k++ ) {

      final float frac = k/(float)kNumParticles,
                  twistAngle = twistStart + frac*(float)(2.0f*Math.PI); 
      float xVel = kBreakXVel,
            yVel = kBreakYZVel*(float)Math.sin(twistAngle),
            zVel = kBreakYZVel*(float)Math.cos(twistAngle);
      
      if ( direc == Env.RIGHT || direc == Env.UP ) {
        xVel = -xVel;
      }
      if ( direc == Env.UP || direc == Env.DOWN ) {
        final float temp = xVel;
        xVel = yVel;
        yVel = temp;
      }
      
      add(new Particle(xPos, yPos, zPos, 
                       xVel, yVel, zVel, 
                       -kGravity, 
                       Env.randomInt(kLifeTimeMin, kLifeTimeMax),
                       colour));
    }
    
  } // constructor
  
} // class Shrapnel
