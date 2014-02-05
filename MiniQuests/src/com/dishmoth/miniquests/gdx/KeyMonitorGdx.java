/*
 *  KeyMonitorGdx.java
 *  Copyright Simon Hern 2014
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dishmoth.miniquests.game.KeyMonitor;

// keep track of what 'keys' are currently pressed
abstract public class KeyMonitorGdx implements KeyMonitor {

  // current touch screen configuration (see enum in KeyMonitor)
  protected int mMode;

  // helper object for managing physical controllers
  protected KeyControllers mControllers;
  
  // constructor
  public KeyMonitorGdx() {
    
    mMode = 0;
    mControllers = null;

    Gdx.input.setCatchBackKey(true);

  } // constructor
  
  // assert that all keys are currently not pressed
  @Override
  public void reset() {
    
    if ( mControllers != null ) mControllers.reset();
    
  } // KeyMonitor.reset()

  // how the touch screen maps to controls
  public void setMode(int mode) {
    
    assert( mode >= MODE_GAME && mode <= MODE_QUERY );
    mMode = mode;
    
    if ( mControllers != null ) mControllers.setMode(mode);
    
  } // KeyMonitor.setMode()
  
  // magnification factor for the game screen
  public void setScreenScaleFactor(int scale) {}

  // enable on-screen buttons with target size (zero for no buttons)
  public void useButtons(int pixelWidth) {}
  
  // whether on-screen buttons are enabled
  public boolean usingButtons() { return false; }
  
  // set details of the on-screen buttons
  public void setButtonDetails(int arrowStyle, int fireStyle) {}
  
  // number of pixels needed for buttons on left/right side of screen
  public int buttonsXSize() { return 0; }

  // number of pixels needed for buttons at bottom of screen
  public int buttonsYSize() { return 0; }
  
  // draw the on-scren buttons, if used
  public void displayButtons(SpriteBatch spriteBatch) {}
  
  // enable use of physical controllers
  public void useControllers() {

    if ( mControllers == null ) mControllers = new KeyControllers();
    
  } // useControllers()
  
} // class KeyMonitorGdx
