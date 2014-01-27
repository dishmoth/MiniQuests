/*
 *  FlameBolt.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// a moving flame beam
public class FlameBolt extends FlameBeam {

  // length the bolt has to travel
  private final float mTotalLength;
  
  // speed of the bolt
  private final float mSpeed;

  // current distance of the bolt's ends from the starting position
  private float mHeadPos,
                mTailPos;
  
  // start position of the bolt's path
  private float mXStart,
                mYStart,
                mZStart;
  
  // unit vector direction of the bolt
  private float mXDirec,
                mYDirec,
                mZDirec;
  
  // constructor, given start and end points
  public FlameBolt(float x0, float y0, float z0,
                   float x1, float y1, float z1,
                   float length, float speed) {

    super(x0,y0,z0, x0,y0,z0);

    assert( length > 0 );
    assert( speed > 0 );

    final float dx = x1 - x0,
                dy = y1 - y0,
                dz = z1 - z0;
    mTotalLength = (float)Math.sqrt(dx*dx + dy*dy + dz*dz);

    mXStart = x0;
    mYStart = y0;
    mZStart = z0;
    
    mXDirec = dx/mTotalLength;
    mYDirec = dy/mTotalLength;
    mZDirec = dz/mTotalLength;
    
    mSpeed = speed;
    
    mHeadPos = 0.0f;
    mTailPos = -length; 

  } // constructor
  
  // whether the bolt has reached its end-point yet
  public boolean atTarget() { return (mHeadPos >= mTotalLength); }
  
  // move the beam
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {

    super.advance(addTheseSprites, killTheseSprites, newStoryEvents);

    if ( !isOn() ) {
      if ( mParticles.size() == 0 ) killTheseSprites.add(this);
      return;
    }
    
    mHeadPos += mSpeed;
    mTailPos += mSpeed;

    final float h0 = Math.min(mTotalLength, Math.max(0.0f, mTailPos)),
                h1 = Math.min(mTotalLength, mHeadPos);
    
    mXPos0 = mXStart + h0*mXDirec;
    mYPos0 = mYStart + h0*mYDirec;
    mZPos0 = mZStart + h0*mZDirec;
    
    mXPos1 = mXStart + h1*mXDirec;
    mYPos1 = mYStart + h1*mYDirec;
    mZPos1 = mZStart + h1*mZDirec;
    
    if ( h0 >= mTotalLength ) setFlame(false);
    
  } // Sprite3D.advance()

} // class FlameBolt
