/*
 *  Particle.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

// a simple animated pixel
public class Particle {

  // position (x, y in blocks, z in pixels)
  private float mXPos,
                mYPos,
                mZPos;
  
  // velocity
  private float mXVel,
                mYVel,
                mZVel;

  // acceleration
  private float mXAccel,
                mYAccel,
                mZAccel;
  
  // pixel colour
  private byte mColour;
  
  // time the particle has existed for
  private int mAge;
  
  // age at which the particle expires
  final private int mLifeTime;
  
  // constructor
  public Particle(float xPos, float yPos, float zPos,
                  float xVel, float yVel, float zVel,
                  float zAcc, int life, byte colour) {
    
    mXPos = xPos;
    mYPos = yPos;
    mZPos = zPos;
  
    mXVel = xVel;
    mYVel = yVel;
    mZVel = zVel;

    mXAccel = 0.0f;
    mYAccel = 0.0f;
    mZAccel = zAcc;
    
    assert( colour >= 0 && colour < 64 );
    mColour = colour;

    mAge = 0;
    mLifeTime = life;
    
  } // constructor

  // access to position
  public float getXPos() { return mXPos; }
  public float getYPos() { return mYPos; }
  public float getZPos() { return mZPos; }
  
  // modify position
  public void setPos(float x, float y, float z) { mXPos=x; mYPos=y; mZPos=z; }
  
  // whether the particle is still active
  public boolean alive() { return ( mAge < mLifeTime ); }
  
  // age access
  public int age() { return mAge; }
  public int lifeTime() { return mLifeTime; }
  
  // change the colour
  public void setColour(byte c) { assert(c >= 0 && c < 64); mColour = c; }
  
  // move the particle
  public void advance() {

    mXPos += mXVel;
    mYPos += mYVel;
    mZPos += mZVel;

    mXVel += mXAccel;
    mYVel += mYAccel;
    mZVel += mZAccel;

    if ( mAge < mLifeTime ) mAge++;
    
  } // advance()

  // whether the particle collides with an obstacle
  public boolean hits(Obstacle ob) {

    return ( !ob.isEmpty(Math.round(mXPos), 
                         Math.round(mYPos), 
                         Math.round(mZPos+1)) );
    
  } // hits()
  
  // draw the particle
  public void draw(EgaCanvas canvas, Camera camera) {
    
    final float xPos = mXPos - camera.xPos(),
                yPos = mYPos - camera.yPos(),
                zPos = mZPos - camera.zPos();
    
    final int x = Env.originXPixel() + (int)Math.ceil( 2.0f*(xPos - yPos) ),
              y = Env.originYPixel() - (int)Math.floor( xPos + yPos + zPos + 0.2f);
    final float depth = xPos + yPos - 0.25f;
    
    canvas.plot(x, y, depth, mColour);
    
  } // draw()
  
} // class Particle
