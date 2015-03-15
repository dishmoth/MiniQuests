/*
 *  FloorSwitch.java
 *  Copyright Simon Hern 2010
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// switch that is activated when stepped on
public class FloorSwitch extends Sprite3D {

  // story event: the switch has changed state
  public class EventStateChange extends StoryEvent {
    public FloorSwitch mSwitch;
    public EventStateChange(FloorSwitch s) { mSwitch = s; }
  } // class FloorSwitch.EventStateChange
  
  // depth values at different points may need to be tweaked
  final private static float kDepthEpsilon = 0.003f,
                             kDepthTop     = 0.95f - kDepthEpsilon,
                             kDepthCentre  = -kDepthEpsilon,
                             kDepthBottom1 = -0.95f - kDepthEpsilon,
                             kDepthBottom2 = -1.00f - kDepthEpsilon,
                             kDepthBottom3 = -1.05f - kDepthEpsilon,
                             kDepthSide1    = -kDepthEpsilon,
                             kDepthSide2    = -0.05f - kDepthEpsilon;
  
  // position (x, y block units, z pixel units)
  final private int mXPos,
                    mYPos,
                    mZPos;
  
  // colours of the switch (value pairs, middle and edge colours)
  final private byte mColourOn[],
                     mColourOff[];
  
  // state of the switch
  private boolean mIsOn;

  // if true then the switch's state has just been changed
  private boolean mToggleState;
  
  // if true then the switch is fixed in its current state
  private boolean mStateFrozen;
  
  // keep track of blocks (so that the appearance of the switch is right)
  private LinkedList<Obstacle> mObstacles = new LinkedList<Obstacle>();

  // tweak to the switch's appearance depending on neighbouring blocks
  private float mDepthLeft,
                mDepthRight,
                mDepthBottom;
  
  // constructor
  public FloorSwitch(int x, int y, int z, String colourOff, String colourOn) {
    
    mXPos = x;
    mYPos = y;
    mZPos = z;
    
    assert( colourOff != null && colourOff.length() == 2 );
    mColourOff = EgaTools.decodePixels(colourOff);

    assert( colourOn != null && colourOn.length() == 2 );
    mColourOn = EgaTools.decodePixels(colourOn);
    
    mIsOn = false;
    mToggleState = false;
    mStateFrozen = false;

    mDepthLeft = mDepthRight = mDepthBottom = 0.0f;
    
  } // constructor

  // the switch's current state
  public boolean isOn() { return mIsOn; }
  
  // freeze the state of the switch
  public void freezeState(boolean on) { mIsOn = on; mStateFrozen = true; }

  // unfreeze the state of the switch
  public void unfreezeState() { mIsOn = false; mStateFrozen = false; }

  // access to position
  public int getXPos() { return mXPos; }
  public int getYPos() { return mYPos; }
  public int getZPos() { return mZPos; }
  
  // check for Sprites we want to keep track of
  @Override
  public void observeArrival(Sprite newSprite) { 

    super.observeArrival(newSprite);
    
    if ( newSprite instanceof Obstacle ) {
      mObstacles.add((Obstacle)newSprite);
    }
    
    if ( newSprite instanceof Player ||
         newSprite instanceof Critter ) {
      mSpritesToWatch.add(newSprite);
    }
    
  } // Sprite.observeArrival()
  
  // keep track of Sprites that have been destroyed
  @Override
  public void observeDeparture(Sprite deadSprite) {

    if ( deadSprite instanceof Obstacle ) {
      mObstacles.remove(deadSprite);
    }

    else {
      mSpritesToWatch.remove(deadSprite);
    }
    
    super.observeDeparture(deadSprite);
    
  } // Sprite.observeDeparture()  
  
  // nothing needs doing here
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {
  } // Sprite.advance()

  // check for the switch being activated
  @Override
  public void interact() {

    boolean somethingOnSwitch = false;
    for ( Sprite sp : mSpritesToWatch ) {
      
      if ( sp instanceof Critter) {
        Critter cr = (Critter)sp;
        if ( cr.getXPos() == mXPos && 
             cr.getYPos() == mYPos &&
             cr.getZPos() == mZPos ) somethingOnSwitch = true;
      }
      
      else if ( sp instanceof Player) {
        Player pl = (Player)sp;
        if ( pl.getXPos() == mXPos && 
             pl.getYPos() == mYPos &&
             pl.getZPos() == mZPos ) somethingOnSwitch = true;
      }

      if ( somethingOnSwitch ) break;
    }
    
    if ( !mStateFrozen ) {
      mToggleState = ( somethingOnSwitch != mIsOn );
    }
    
    boolean platformExistsBelow = false;
    mDepthLeft = kDepthSide1;
    mDepthRight = kDepthSide1;
    mDepthBottom = kDepthBottom1;
    for ( Obstacle obs : mObstacles ) {
      if ( obs.isPlatform(mXPos, mYPos, mZPos) ) {
        platformExistsBelow = true;
      }
      if ( obs.isPlatform(mXPos-1, mYPos, mZPos) ) {
        mDepthLeft = Math.min(mDepthLeft, kDepthSide2);
        mDepthBottom = Math.min(mDepthBottom, kDepthBottom2);
      }
      if ( obs.isPlatform(mXPos, mYPos-1, mZPos) ) {
        mDepthRight = Math.min(mDepthRight, kDepthSide2);
        mDepthBottom = Math.min(mDepthBottom, kDepthBottom2);
      }
      if ( obs.isPlatform(mXPos-1, mYPos-1, mZPos) ) {
        mDepthBottom = Math.min(mDepthBottom, kDepthBottom3);
      }
    }
    assert( platformExistsBelow );
    
  } // Sprite.interact()
  
  // change the state of the switch
  @Override
  public void aftermath(LinkedList<Sprite>     addTheseSprites, 
                        LinkedList<Sprite>     killTheseSprites,
                        LinkedList<StoryEvent> newStoryEvents) { 
    
    if ( mToggleState ) {
      assert( !mStateFrozen );
      mToggleState = false;
      mIsOn = !mIsOn;
      newStoryEvents.add( new EventStateChange(this) );
    }
    
  } // Sprite.aftermath()
  
  // display the object
  @Override
  public void draw(EgaCanvas canvas) {

    final int x0 = mXPos - mCamera.xPos(),
              y0 = mYPos - mCamera.yPos(),
              z0 = mZPos - mCamera.zPos();

    final int depth = x0 + y0;
    final int xPixel = Env.originXPixel() + 2*x0 - 2*y0,
              yPixel = Env.originYPixel() - depth - z0;  
    
    byte colour[] = ( mIsOn ? mColourOn : mColourOff );
    
    canvas.fill(xPixel, xPixel+1, yPixel-1, yPixel-1, 
                depth+kDepthTop, colour[1]);
    canvas.fill(xPixel, xPixel+1, yPixel, yPixel, 
                depth+kDepthCentre, colour[0]);
    canvas.fill(xPixel, xPixel+1, yPixel+1, yPixel+1, 
                depth+mDepthBottom, colour[1]);

    canvas.plot(xPixel-1, yPixel, depth+mDepthLeft, colour[1]);
    canvas.plot(xPixel+2, yPixel, depth+mDepthRight, colour[1]);
    
  } // Sprite.draw()

} // class FloorSwitch
