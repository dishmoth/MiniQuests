/*
 *  WallSwitch.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// switch on the wall that changes colour when shot
public class WallSwitch extends Sprite3D {

  // story event: the switch has changed state
  public class EventStateChange extends StoryEvent {
    public WallSwitch mSwitch;
    public int mNewState;
    public EventStateChange(WallSwitch ws, int state) { 
      mSwitch = ws; 
      mNewState = state; 
    }
  } // class WallSwitch.EventStateChange

  // small tweak to depth values to make the switch stand out from the wall
  private static final float kDepthEpsilon = 0.007f;
  
  // which wall the switch is on (enumerated in Env)
  final private int mWallSide;
  
  // position (x, y block units, z pixel units)
  final private int mXPos,
                    mYPos,
                    mZPos;
  
  // colours of the switch (value pairs, middle and edge colours)
  final private byte mColours[][];
  
  // whether the states wrap around
  final private boolean mCyclic;
  
  // current state of the switch (0, 1, ...)
  private int mState;

  // if true then the switch's state is due to be changed
  private boolean mAdvanceState;
  
  // constructor
  public WallSwitch(int wallSide, int xy, int z, 
                    String colours[], boolean cyclic) {
    
    assert( wallSide == Env.UP || wallSide == Env.RIGHT );
    mWallSide = wallSide;

    switch ( mWallSide ) {
      case Env.RIGHT: { mXPos = Room.kSize; mYPos = xy;         } break;
      case Env.UP:    { mXPos = xy;         mYPos = Room.kSize; } break;
      default:        { mXPos = mYPos = 0; assert(false); }
    }
    
    mZPos = z;
    
    assert( colours != null );
    mColours = new byte[colours.length][];
    for ( int k = 0 ; k < mColours.length ; k++ ) {
      assert( colours[k] != null && colours[k].length() == 2 );
      mColours[k] = EgaTools.decodePixels(colours[k]);
    }
    
    mCyclic = cyclic;
    mState = 0;
    mAdvanceState = false;
    
  } // constructor

  // constructor
  public WallSwitch(int zoneX, int zoneY,
                    int wallSide, int xy, int z, 
                    String colours[], boolean cyclic) {
    
    assert( wallSide == Env.UP || wallSide == Env.RIGHT );
    mWallSide = wallSide;

    switch ( mWallSide ) {
      case Env.RIGHT: { 
        mXPos = Room.kSize*zoneX + Room.kSize;
        mYPos = Room.kSize*zoneY + xy;
      } break;
      case Env.UP: { 
        mXPos = Room.kSize*zoneX + xy;
        mYPos = Room.kSize*zoneY + Room.kSize;
      } break;
      default: { 
        mXPos = mYPos = 0; 
        assert(false); 
      }
    }

    mZPos = z;
    
    assert( colours != null );
    mColours = new byte[colours.length][];
    for ( int k = 0 ; k < mColours.length ; k++ ) {
      assert( colours[k] != null && colours[k].length() == 2 );
      mColours[k] = EgaTools.decodePixels(colours[k]);
    }    
    
    mCyclic = cyclic;
    mState = 0;
    mAdvanceState = false;
    
  } // constructor

  // whether the switch can change state further
  public boolean isActive() { return (mCyclic || mState < mColours.length-1); }
  
  // the switch's current state
  public int getState() { return mState; }
  
  // the switch's current centre colour
  public byte colour() { return mColours[mState][0]; }

  // notify the switch that something has hit it
  public void hit() { 
    
    assert( mCyclic || mState < mColours.length-1 );
    mAdvanceState = true; 
    
  } // hit()

  // force the switch into a particular state
  public void setState(int state) { 
    
    assert( state >= 0 && state < mColours.length );
    mState = state;
    
  } // setState()
  
  // access position details
  public int getXPos() { return mXPos; }
  public int getYPos() { return mYPos; }
  public int getZPos() { return mZPos; }
  
  // check for Sprites we want to keep track of
  @Override
  public void observeArrival(Sprite newSprite) { 

    super.observeArrival(newSprite);
    
    if ( newSprite instanceof Bullet ) {
      mSpritesToWatch.add(newSprite);
    }
    
  } // Sprite.observeArrival()
  
  // keep track of Sprites that have been destroyed
  @Override
  public void observeDeparture(Sprite deadSprite) {

    mSpritesToWatch.remove(deadSprite);
    
    super.observeDeparture(deadSprite);
    
  } // Sprite.observeDeparture()  
  
  // nothing needs doing here
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {
  } // Sprite.advance()

  // change the state of the switch
  @Override
  public void aftermath(LinkedList<Sprite>     addTheseSprites, 
                        LinkedList<Sprite>     killTheseSprites,
                        LinkedList<StoryEvent> newStoryEvents) { 
    
    if ( mAdvanceState ) {
      mAdvanceState = false;
      assert( isActive() );
      mState += 1;
      if ( mCyclic ) mState = (mState % mColours.length);
      assert( mState < mColours.length );
      newStoryEvents.add( new EventStateChange(this, mState) );
    }
    
  } // Sprite.aftermath()
  
  // display the object
  @Override
  public void draw(EgaCanvas canvas) {

    final int x0 = mXPos - mCamera.xPos(),
              y0 = mYPos - mCamera.yPos(),
              z0 = mZPos - mCamera.zPos();

    int xPixel = Env.originXPixel() + 2*x0 - 2*y0,
        yPixel = Env.originYPixel() - x0 - y0 - z0;  
    final float depth = x0 + y0 - kDepthEpsilon;
    
    float depthLeft  = depth,
          depthRight = depth;
    switch ( mWallSide ) {
      case Env.RIGHT: { /*depthLeft += 1.0f;*/ } break;
      case Env.UP:    { xPixel += 1; /*depthRight += 1.0f;*/ } break;
    }
    
    byte colour[] = mColours[mState];
    
    canvas.plot(xPixel-1, yPixel,   depthLeft, colour[1]);
    canvas.plot(xPixel,   yPixel-1, depth,      colour[1]);
    canvas.plot(xPixel,   yPixel,   depth,      colour[0]);
    canvas.plot(xPixel,   yPixel+1, depth,      colour[1]);
    canvas.plot(xPixel+1, yPixel,   depthRight, colour[1]);
    
  } // Sprite.draw()

} // class WallSwitch
