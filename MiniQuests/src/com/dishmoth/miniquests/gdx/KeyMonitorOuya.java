/*
 *  KeyMonitorOuya.java
 *  Copyright Simon Hern 2014
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

// keep track of what 'keys' (via controller) are currently pressed
public class KeyMonitorOuya extends KeyMonitorGdx {

  // constructor
  public KeyMonitorOuya() { 
    
    super();
    
  } // constructor
  
  // check whether the up button is currently pressed
  @Override
  public boolean up() {

    assert( mControllers != null );
    return mControllers.up();
    
  } // KeyMonitor.up()

  // check whether the down button is currently pressed
  @Override
  public boolean down() {
    
    assert( mControllers != null );
    return mControllers.down();
    
  } // KeyMonitor.down()

  // check whether the left button is currently pressed
  @Override
  public boolean left() {
    
    assert( mControllers != null );
    return mControllers.left();

  } // KeyMonitor.left()

  // check whether the right button is currently pressed
  @Override
  public boolean right() {
    
    assert( mControllers != null );
    return mControllers.right();

  } // KeyMonitor.right()

  // check whether the fire button is currently pressed
  @Override
  public boolean fire() {
    
    assert( mControllers != null );
    return mControllers.fire();
    
  } // KeyMonitor.fire()

  // check whether the escape button is currently pressed
  @Override
  public boolean escape() {
    
    assert( mControllers != null );
    return ( mControllers.escape() ||
             Gdx.input.isKeyPressed(Input.Keys.BACK) ||
             Gdx.input.isKeyPressed(Input.Keys.ESCAPE) );
    
  } // KeyMonitor.escape()

  // check whether any key is currently pressed
  @Override
  public boolean any() {
    
    assert( mControllers != null );
    return ( mControllers.any() || 
             Gdx.input.isKeyPressed(Input.Keys.ANY_KEY) );
    
  } // KeyMonitor.any()
  
} // class KeyMonitorOuya
