/*
 *  Splatter.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

// gore from an enemy being destroyed
public class Splatter extends Particles {

  // particle dynamics
  private static final float kSpeed     = 0.10f,
                             kZSpeedMin = 0.05f,
                             kZSpeedMax = 0.20f,
                             kGravity   = 0.05f;
  
  // effect of direction of hit on speed
  private static final float kHitSpeed = 0.10f;
  
  // particles don't collide initially
  private static final int kCollisionDelay = 4;
  
  // life of a particle
  private static final int kLifeTimeMin = 10,
                           kLifeTimeMax = 15;
  
  // constructor, given step direction (or -1) and direction of hit (or -1)
  public Splatter(int x, int y, int z, int stepDirec, 
                  int height, byte colour, int hitDirec) {
    
    super(kCollisionDelay);
    
    assert( stepDirec >= -1 && stepDirec < 4 );
    assert( height > 0 );
    assert( colour < 64 );
    assert( hitDirec >= -1 && hitDirec < 4 );
    
    final float xHitVel = (hitDirec>=0 ? kHitSpeed*Env.STEP_X[hitDirec] : 0),
                yHitVel = (hitDirec>=0 ? kHitSpeed*Env.STEP_Y[hitDirec] : 0);
    
    for ( int h = 0 ; h < height ; h++ ) {
      for ( int side = 0 ; side < 4 ; side++ ) {

        float xPos = x,
              yPos = y,
              zPos = z + h + 0.5f;
        float hfac    = ( height > 1 ? h/(height-1.0f) : 0.5f );
        float xVel = xHitVel,
              yVel = yHitVel,
              zVel = kZSpeedMin + (kZSpeedMax-kZSpeedMin)*hfac;

        final float theta = 0.25f*(float)Math.PI*Env.randomFloat(-1,+1);
        final float v1 = kSpeed*(float)Math.cos(theta),
                    v2 = kSpeed*(float)Math.sin(theta);

        if ( stepDirec == -1 ) {
          if ( side == Env.RIGHT || side == Env.DOWN ) {
            xPos += 0.25f;
          } else {
            yPos += 0.25f;
          }
        } else {
          if ( side == Env.RIGHT || side == Env.DOWN ) {
            yPos += 0.25f;
            if      ( stepDirec == Env.RIGHT ) xPos += 1;
            else if ( stepDirec == Env.DOWN  ) yPos -= 1;
          } else {
            xPos += 0.25f;
            if      ( stepDirec == Env.LEFT ) xPos -= 1;
            else if ( stepDirec == Env.UP   ) yPos += 1;
          }
        }
        
        switch (side) {
          case Env.RIGHT: { xVel+=v1; yVel+=v2; } break;
          case Env.UP:    { yVel+=v1; xVel+=v2; } break;
          case Env.LEFT:  { xVel-=v1; yVel+=v2; } break;
          case Env.DOWN:  { yVel-=v1; xVel+=v2; } break;
        }
        
        add(new Particle(xPos, yPos, zPos, 
                         xVel, yVel, zVel, 
                         -kGravity, 
                         Env.randomInt(kLifeTimeMin, kLifeTimeMax),
                         colour));
        
      }
    }
    
  } // constructor
  
} // class Splatter
