/*
 *  KeyMonitorDesktop.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

// keep track of what 'keys' are currently pressed
public class KeyMonitorDesktop extends KeyMonitorGdx {

  // constructor
  public KeyMonitorDesktop() {

    super();
    
  } // constructor
  
  // check whether the up button is currently pressed
  @Override
  public boolean up() {
    
    if ( mControllers != null ) {
      if ( mControllers.up() ) return true;
    }
    
    return ( Gdx.input.isKeyPressed(Input.Keys.UP) ||
             Gdx.input.isKeyPressed(Input.Keys.DPAD_UP) ||
             Gdx.input.isKeyPressed(Input.Keys.Q) ||
             Gdx.input.isKeyPressed(Input.Keys.W) ||
             Gdx.input.isKeyPressed(Input.Keys.I) ||
             Gdx.input.isKeyPressed(Input.Keys.T) );

  } // KeyMonitor.up()

  // check whether the down button is currently pressed
  @Override
  public boolean down() {
    
    if ( mControllers != null ) {
      if ( mControllers.down() ) return true;
    }

    return ( Gdx.input.isKeyPressed(Input.Keys.DOWN) ||
             Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN) ||
             Gdx.input.isKeyPressed(Input.Keys.S) ||
             Gdx.input.isKeyPressed(Input.Keys.L) ||
             Gdx.input.isKeyPressed(Input.Keys.H) );
    
  } // KeyMonitor.down()

  // check whether the left button is currently pressed
  @Override
  public boolean left() {
    
    if ( mControllers != null ) {
      if ( mControllers.left() ) return true;
    }
    
    return ( Gdx.input.isKeyPressed(Input.Keys.LEFT) ||
             Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT) ||
             Gdx.input.isKeyPressed(Input.Keys.K) ||
             Gdx.input.isKeyPressed(Input.Keys.A) ||
             Gdx.input.isKeyPressed(Input.Keys.G) );

  } // KeyMonitor.left()

  // check whether the right button is currently pressed
  @Override
  public boolean right() {
    
    if ( mControllers != null ) {
      if ( mControllers.right() ) return true;
    }

    return ( Gdx.input.isKeyPressed(Input.Keys.RIGHT) ||
             Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT) ||
             Gdx.input.isKeyPressed(Input.Keys.P) ||
             Gdx.input.isKeyPressed(Input.Keys.D) ||
             Gdx.input.isKeyPressed(Input.Keys.O) ||
             Gdx.input.isKeyPressed(Input.Keys.Y) );

  } // KeyMonitor.right()

  // check whether the fire button is currently pressed
  @Override
  public boolean fire() {
    
    if ( mControllers != null ) {
      if ( mControllers.fire() ) return true;
    }
    
    return ( Gdx.input.isKeyPressed(Input.Keys.DPAD_CENTER) ||
             Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ||
             Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT) ||
             Gdx.input.isKeyPressed(Input.Keys.SPACE) ||
             Gdx.input.isKeyPressed(Input.Keys.ENTER) ||
             Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
             Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT) ||
             Gdx.input.isKeyPressed(Input.Keys.M) ||
             Gdx.input.isKeyPressed(Input.Keys.N) ||
             Gdx.input.isKeyPressed(Input.Keys.C) ||
             Gdx.input.isKeyPressed(Input.Keys.X) ||
             Gdx.input.isKeyPressed(Input.Keys.Z) );
    
  } // KeyMonitor.fire()

  // check whether the escape button is currently pressed
  @Override
  public boolean escape() {
    
    return ( Gdx.input.isKeyPressed(Input.Keys.BACK) ||
             Gdx.input.isKeyPressed(Input.Keys.ESCAPE) );
    
  } // KeyMonitor.escape()

  // check whether any key is currently pressed
  @Override
  public boolean any() {
    
    return ( (mControllers != null && mControllers.any()) || 
             Gdx.input.isKeyPressed(Input.Keys.ANY_KEY) ||
             fire() );
    
  } // KeyMonitor.any()
  
} // class KeyMonitorDesktop
