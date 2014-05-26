/*
 *  Spikes.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// a hidden hazard
public class Spikes extends Sprite3D {

  // time between states
  private static final int kDelay = 5;
  
  // position of base point of the spike region
  final private int mXPos,
                    mYPos,
                    mZPos;
  
  // size of the spike region
  final private int mXSize,
                    mYSize;

  // whether to shift the spikes over to the right
  final private boolean mShift;
  
  // colour scheme
  final private byte mBaseColour,
                     mSpikeColour;
  
  // whether to turn off the sound effect
  private boolean mSilent;
  
  // whether the spike is hidden (0) or extended (1, 2, 3)
  private int mState;
  
  // ticks until the next state change
  private int mTimer;
  
  // constructor
  public Spikes(int xPos, int yPos, int zPos, 
                int xSize, int ySize, 
                boolean shift, String colours) {
    
    mXPos = xPos;
    mYPos = yPos;
    mZPos = zPos;
  
    assert( xSize > 0 && ySize > 0 );
    mXSize = xSize;
    mYSize = ySize;
    
    mShift = shift;
    
    assert( colours != null && colours.length() == 2 );
    mBaseColour = EgaTools.decodePixel( colours.charAt(0) );
    mSpikeColour = EgaTools.decodePixel( colours.charAt(1) );

    mState = 0;
    mTimer = 0;
    
    mSilent = false;
    
  } // constructor
  
  // accessors
  public int getXPos()  { return mXPos; }
  public int getYPos()  { return mYPos; }
  public int getZPos()  { return mZPos; }
  public int getXSize() { return mXSize; }
  public int getYSize() { return mYSize; }
  
  // extend the spikes
  public void trigger() {
    
    if ( mState == 0 ) {
      mState = 1;
      mTimer = kDelay;
    }
    
  } // trigger()
  
  // control the sound effect
  public void setSilent(boolean silent) { mSilent = silent; }
  
  // check for Sprites we want to keep track of
  @Override
  public void observeArrival(Sprite newSprite) { 

    super.observeArrival(newSprite);
    
    if ( newSprite instanceof Critter ||
         newSprite instanceof Player ) {
      mSpritesToWatch.add(newSprite);
    }
    
  } // Sprite.observeArrival()
  
  // keep track of Sprites that have been destroyed
  @Override
  public void observeDeparture(Sprite deadSprite) {

    super.observeDeparture(deadSprite);
    
    if ( deadSprite instanceof Critter ||
         deadSprite instanceof Player ) {
      mSpritesToWatch.remove(deadSprite);
    }
    
  } // Sprite.observeDeparture()

  // extend and retract
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {

    if ( mState != 0 ) {
      if ( --mTimer <= 0 ) {
        mTimer = kDelay;
        mState += 1;
        if ( mState == 4 ) {
          mState = 0;
          mTimer = 0;
        }
      }
      if ( !mSilent && mState == 1 && mTimer == 1 ) {
        Env.sounds().play(Sounds.SPIKES);
      }
    }
    
  } // Sprite.advance()

  // check for collisions
  @Override
  public void interact() { 
    
    if ( mState >= 1 && mState <= 2 ) {
      
      for ( Sprite sp : mSpritesToWatch ) {
        
        if ( sp instanceof Critter ) {
          Critter target = (Critter)sp;
          if ( target.hits(mXPos, mXPos+mXSize-1, 
                           mYPos, mYPos+mYSize-1, 
                           mZPos, mZPos) ) {
            target.stun(-1);
          }
        }
        
        else if ( sp instanceof Player ) {
          Player target = (Player)sp;
          if ( target.hits(mXPos, mXPos+mXSize-1, 
                           mYPos, mYPos+mYSize-1, 
                           mZPos, mZPos) ) {
            target.destroy(-1);
          }
        }
        
      }
      
    }
    
  } // Sprite.interact()

  // display the spikes
  @Override
  public void draw(EgaCanvas canvas) {

    final int xPos = mXPos - mCamera.xPos(),
              yPos = mYPos - mCamera.yPos(),
              zPos = mZPos - mCamera.zPos();

    final int x0 = Env.originXPixel() + 2*xPos - 2*yPos,
              y0 = Env.originYPixel() - xPos - yPos - zPos;
    final float depth0 = xPos + yPos;
    
    float depth = depth0 - 0.01f;
    
    byte colour = (mState==0) ? mBaseColour : mSpikeColour;;

    for ( int iy = 0 ; iy < mYSize ; iy++ ) {
      for ( int ix = 0 ; ix < mXSize ; ix++ ) {
        int x = x0 + 2*ix - 2*iy + (mShift?1:0),
            y = y0 - ix - iy;
        float d = depth + ix + iy;
        canvas.plot(x, y, d, colour);
        if ( mState == 2 ) {
          canvas.plot(x, y-1, d, colour);
        }
      }
    }
    
  } // Sprite.draw()

} // class Spikes
