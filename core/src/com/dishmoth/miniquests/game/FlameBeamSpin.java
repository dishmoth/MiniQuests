/*
 *  FlameBeamSpin.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// rotating flame beam
public class FlameBeamSpin extends FlameBeam {

  // the particles don't move quite as fast as their source
  private static final float kAngLag = 0.005f;

  // angle (radians, anti-clockwise from east)
  private float mAngle;
  
  // length of the beam
  private float mRadiusMin,
                mRadiusMax;

  // pivot position
  private float mXPos,
                mYPos,
                mZPos;
  
  
  // angular speed (radians per second)
  private float mAngSpeed;
  
  // which axis to rotate around (0,1,2 for x,y,z)
  private int mAxis;
  
  // whether the beam makes a sound
  private boolean mIsSilent;
  
  // constructor
  public FlameBeamSpin(float x, float y, float z,
                       float radiusMin, float radiusMax, 
                       float angSpeed, float startAngle, 
                       int axis) {

    super(x,y,z, x,y,z);

    assert( radiusMin >= 0.0f );
    assert( radiusMax > radiusMin );
    mRadiusMin = radiusMin;
    mRadiusMax = radiusMax;

    mXPos = x;
    mYPos = y;
    mZPos = z;

    assert( axis >= 0 && axis <= 2 );
    mAxis = axis;

    mIsSilent = true;

    mAngSpeed = angSpeed;
    mAngle = startAngle - kWarmUpTime*mAngSpeed;
    updateEndPos();
    warmUp();
    
  } // constructor
  
  // change the noisiness of the flame
  public void setSilent(boolean silent) { mIsSilent = silent; } 
  
  // change the rotation radii
  public void setRadii(float radiusMin, float radiusMax) {
    
    mRadiusMin = radiusMin;
    mRadiusMax = radiusMax;
    
  } // setRadii()
  
  // set the beam's end positions
  private void updateEndPos() {
    
    float cth = (float)Math.cos(mAngle),
          sth = (float)Math.sin(mAngle);
    
    float dx = 0.0f, dy = 0.0f, dz = 0.0f;
    switch ( mAxis ) {
      case 0: { dy = cth; dz = sth; } break;
      case 1: { dz = cth; dx = sth; } break;
      case 2: { dx = cth; dy = sth; } break;
    }
    
    mXPos0 = mXPos + mRadiusMin*dx;
    mYPos0 = mYPos + mRadiusMin*dy;
    mZPos0 = mZPos + mRadiusMin*dz;
    
    mXPos1 = mXPos + mRadiusMax*dx;
    mYPos1 = mYPos + mRadiusMax*dy;
    mZPos1 = mZPos + mRadiusMax*dz;
    
  } // updateEndPos()
  
  // move the beam
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {

    mAngle += mAngSpeed;
    mAngle = Env.fold(mAngle, 2.0f*(float)Math.PI);
    
    updateEndPos();
    
    super.advance(addTheseSprites, killTheseSprites, newStoryEvents);

    // shift the particles with the beam
    final float dAng = mAngSpeed - kAngLag;
    final float sth = (float)Math.sin(dAng),
                cth = (float)Math.cos(dAng);
    switch ( mAxis ) {
      case 0: {
        for ( Particle p : mParticles ) {
          final float yOld = p.getYPos() - mYPos0,
                      zOld = p.getZPos() - mZPos0;
          final float yNew = yOld*cth - zOld*sth,
                      zNew = yOld*sth + zOld*cth;
          p.setPos(p.getXPos(), mYPos0+yNew, mZPos0+zNew);
        }
      } break;
      
      case 1: {
        for ( Particle p : mParticles ) {
          final float zOld = p.getZPos() - mZPos0,
                      xOld = p.getXPos() - mXPos0;
          final float zNew = zOld*cth - xOld*sth,
                      xNew = zOld*sth + xOld*cth;
          p.setPos(mXPos0+xNew, p.getYPos(), mZPos0+zNew);
        }
      } break;
      
      case 2: {
        for ( Particle p : mParticles ) {
          final float xOld = p.getXPos() - mXPos0,
                      yOld = p.getYPos() - mYPos0;
          final float xNew = xOld*cth - yOld*sth,
                      yNew = xOld*sth + yOld*cth;
          p.setPos(mXPos0+xNew, mYPos0+yNew, p.getZPos());
        }
      } break;
    }
    
    if ( mIsOn && !mIsSilent ) Env.sounds().loop(Sounds.FLAME);
    
  } // Sprite3D.advance()

} // class FlameBeamSpin
