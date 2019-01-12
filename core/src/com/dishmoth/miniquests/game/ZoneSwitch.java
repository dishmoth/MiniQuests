/*
 *  ZoneSwitch.java
 *  Copyright (c) 2019 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// invisible switch that is activated when a zone is entered
public class ZoneSwitch extends Sprite3D {

  // story event: the switch has changed state
  public class EventStateChange extends StoryEvent {
    public ZoneSwitch mSwitch;
    public EventStateChange(ZoneSwitch s) { mSwitch = s; }
  } // class ZoneSwitch.EventStateChange
  
  // position (x, y block units, z pixel units)
  final private int mXMin,
                    mXMax,
                    mYMin,
                    mYMax,
                    mZMin,
                    mZMax;
  
  // state of the switch
  private boolean mIsOn;

  // if true then the switch's state has just been changed
  private boolean mToggleState;
  
  // if true then the switch is fixed in its current state
  private boolean mStateFrozen;
  
  // constructor (infinite column)
  public ZoneSwitch(int x, int y) {
    
    mXMin = mXMax = x;
    mYMin = mYMax = y;
    mZMin = Integer.MIN_VALUE;
    mZMax = Integer.MAX_VALUE;
   
    mIsOn = false;
    mToggleState = false;
    mStateFrozen = false;
    
  } // constructor

  // constructor (rectangular zone)
  public ZoneSwitch(int x0, int y0, int z0, int x1, int y1, int z1) {
    
    mXMin = Math.min(x0, x1);
    mXMax = Math.max(x0, x1);
    mYMin = Math.min(y0, y1);
    mYMax = Math.max(y0, y1);
    mZMin = Math.min(z0, z1);
    mZMax = Math.max(z0, z1);
   
    mIsOn = false;
    mToggleState = false;
    mStateFrozen = false;
    
  } // constructor

  // the switch's current state
  public boolean isOn() { return mIsOn; }
  
  // freeze the state of the switch
  public void freezeState(boolean on) { mIsOn = on; mStateFrozen = true; }

  // unfreeze the state of the switch
  public void unfreezeState() { mIsOn = false; mStateFrozen = false; }

  // check for Sprites we want to keep track of
  @Override
  public void observeArrival(Sprite newSprite) { 

    super.observeArrival(newSprite);
    
    if ( newSprite instanceof Player ||
         newSprite instanceof Critter ) {
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

  // check whether a point is inside the switch zone
  private boolean inside(int x, int y, int z) {
    
    return ( x >= mXMin && x <= mXMax &&
             y >= mYMin && y <= mYMax &&
             z >= mZMin && z <= mZMax );
    
  } // inside()
  
  // check for the switch being activated
  @Override
  public void interact() {

    boolean somethingOnSwitch = false;
    for ( Sprite sp : mSpritesToWatch ) {
      
      if ( sp instanceof Critter) {
        Critter cr = (Critter)sp;
        if ( inside(cr.getXPos(), cr.getYPos(), cr.getZPos()) ) {
          somethingOnSwitch = true;
        }
      }
      
      else if ( sp instanceof Player) {
        Player pl = (Player)sp;
        if ( inside(pl.getXPos(), pl.getYPos(), pl.getZPos()) ) {
          somethingOnSwitch = true;
        }
      }

      if ( somethingOnSwitch ) break;
    }
    
    if ( !mStateFrozen ) {
      mToggleState = ( somethingOnSwitch != mIsOn );
    }
    
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
  
  // display the object (nothing to see)
  @Override
  public void draw(EgaCanvas canvas) {
  } // Sprite.draw()

} // class ZoneSwitch
