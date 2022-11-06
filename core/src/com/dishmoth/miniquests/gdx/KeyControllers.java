/*
 *  KeyControllers.java
 *  Copyright (c) 2022 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.gdx;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.ControllerMapping;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.utils.Array;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.KeyMonitor;

// try to derive key presses from attached controllers
public class KeyControllers implements ControllerListener {

  // ignore axis values around zero
  private static final float kDeadZone = 0.25f,
                             kDiagZone = 0.01f;
  
  // whether to limit debug output
  private static final boolean kVeryVerbose = false;
  
  // enumeration of different types of controller
  private static final int kTypeUnknown = 0,
                           kTypeOuya    = 1,
                           kTypeXBox    = 2;
  
  // current touch screen configuration (see enum in KeyMonitor)
  private int mMode;

  // current controller index (the last one that pressed a button, or -1)
  private int mControllerIndex;
  
  // current state of main stick
  private float mAxisX,
                mAxisY;
  
  // current state of dpad
  private boolean mDPadLeft,
                  mDPadRight,
                  mDPadUp,
                  mDPadDown;
  
  // current state of fire button (basically any button apart from 'menu')
  private boolean mFire;
  
  // current state of escape button ('menu' button, if we have one)
  private boolean mEscape;
  
  // constructor
  public KeyControllers() {
    
    Array<Controller> controllers = Controllers.getControllers(); 
    for ( int index = 0 ; index < controllers.size ; index++  ) {
      Controller c = controllers.get(index);
      Env.debug("Controller " + index + ": " + c.getName());
    }
    
    Controllers.addListener(this);

    if ( controllers.size > 0 ) {
      mControllerIndex = 0;
    } else {
      mControllerIndex = -1;
    }
    
    mMode = KeyMonitor.MODE_GAME;
    reset();
    
  } // constructor
  
  // assert that no buttons are currently pressed
  public void reset() {
    
    mAxisX = mAxisY = 0.0f;
    mDPadLeft = mDPadRight = mDPadUp = mDPadDown = false;
    mFire = mEscape = false;
    
  } // reset()

  // whether to ignore the stick (it's roughly central)
  private boolean deadZone() {
    
    return ( Math.abs(mAxisX) < kDeadZone && Math.abs(mAxisY) < kDeadZone );
    
  } // deadZone()
  
  // check whether the up button is currently pressed
  public boolean up() {

    if ( mMode == KeyMonitor.MODE_GAME ) {
      return ( mDPadUp || 
               (!deadZone() && mAxisX < -kDiagZone && mAxisY < -kDiagZone) );
    } else {
      return ( mDPadUp || 
               mAxisY < -kDeadZone );
    }
    
  } // up()
  
  // check whether the down button is currently pressed
  public boolean down() {
  
    if ( mMode == KeyMonitor.MODE_GAME ) {
      return ( mDPadDown || 
               (!deadZone() && mAxisX > +kDiagZone && mAxisY > +kDiagZone) );
    } else {
      return ( mDPadDown || 
               mAxisY > +kDeadZone );
    }
    
  } // down()
  
  // check whether the left button is currently pressed
  public boolean left() {
  
    if ( mMode == KeyMonitor.MODE_GAME ) {
      return ( mDPadLeft ||
               (!deadZone() && mAxisX < -kDiagZone && mAxisY > +kDiagZone) );
    } else {
      return ( mDPadLeft || 
               mAxisX < -kDeadZone );
    }
    
  } // left()
  
  // check whether the right button is currently pressed
  public boolean right() {
  
    if ( mMode == KeyMonitor.MODE_GAME ) {
      return ( mDPadRight || 
               (!deadZone() && mAxisX > +kDiagZone && mAxisY < -kDiagZone) );
    } else {
      return ( mDPadRight || 
               mAxisX > +kDeadZone );
    }
    
  } // right()
  
  // check whether the fire button is currently pressed
  public boolean fire() { return mFire; }
  
  // check whether the escape button is currently pressed
  public boolean escape() { return mEscape; }

  // check whether any key is currently pressed
  public boolean any() { 
    
    return ( mDPadLeft || mDPadRight || mDPadUp || mDPadDown || mFire );
  
  } // any()
  
  // whether we're moving on diagonal axes
  public void setMode(int mode) {
    
    assert( mode >= KeyMonitor.MODE_GAME && mode <= KeyMonitor.MODE_QUERY );
    if ( mMode != mode ) {
      mMode = mode;
      reset();
    }
    
  } // setMode()
  
  // controller number
  private int controllerIndex(Controller controller) {
    
    return Controllers.getControllers().indexOf(controller, true);
    
  } // controllerIndex()

  // controller attached
  @Override
  public void connected(Controller controller) {
    
    Env.debug("Controller connected: " + controller.getName());
    
    if ( mControllerIndex < 0 ) {
      mControllerIndex = controllerIndex(controller);
    }
    
  } // ControllerListener.connected()

  // controller disconnected
  @Override
  public void disconnected(Controller controller) {
    
    Env.debug("Controller disconnected: " + controller.getName());
        
  } // ControllerListener.disconnected()

  // button pressed
  @Override
  public boolean buttonDown(Controller controller, int buttonCode) {

    if ( kVeryVerbose ) {
      Env.debug("Controller " + controllerIndex(controller) 
                + " ("+ controller.getName() + ") " 
                + "button pressed " + buttonCode);
    }
    
    int index = controllerIndex(controller);
    if ( index != -1 && index != mControllerIndex ) {
      mControllerIndex = index;
      reset();
    }

    ControllerMapping mapping = controller.getMapping();
    if ( buttonCode == mapping.buttonDpadUp ) {
      mDPadUp = true;
    } else if ( buttonCode == mapping.buttonDpadDown ) {
      mDPadDown = true;
    } else if ( buttonCode == mapping.buttonDpadLeft ) {
      mDPadLeft = true;
    } else if ( buttonCode == mapping.buttonDpadRight ) {
      mDPadRight = true;
    } else if ( buttonCode == mapping.buttonA ||
                buttonCode == mapping.buttonB ||
                buttonCode == mapping.buttonX ||
                buttonCode == mapping.buttonY ) {
      mFire = true;
    } else if ( buttonCode == mapping.buttonBack ||
                buttonCode == mapping.buttonStart ) {
      mEscape = true;
    }

    return true;
    
  } // ControllerListener.buttonDown()

  // button released
  @Override
  public boolean buttonUp(Controller controller, int buttonCode) {
    
    if ( kVeryVerbose ) {
      Env.debug("Controller " + controllerIndex(controller) 
                + " ("+ controller.getName() + ") " 
                + "button released " + buttonCode);
    }

    if ( mControllerIndex == -1 || 
         controllerIndex(controller) != mControllerIndex ) {
      return true;
    }

    ControllerMapping mapping = controller.getMapping();
    if ( buttonCode == mapping.buttonDpadUp ) {
      mDPadUp = false;
    } else if ( buttonCode == mapping.buttonDpadDown ) {
      mDPadDown = false;
    } else if ( buttonCode == mapping.buttonDpadLeft ) {
      mDPadLeft = false;
    } else if ( buttonCode == mapping.buttonDpadRight ) {
      mDPadRight = false;
    } else if ( buttonCode == mapping.buttonA ||
                buttonCode == mapping.buttonB ||
                buttonCode == mapping.buttonX ||
                buttonCode == mapping.buttonY ) {
      mFire = false;
    } else if ( buttonCode == mapping.buttonBack ||
                buttonCode == mapping.buttonStart ) {
      // do nothing
    }

    return true;
    
  } // ControllerListener.buttonUp()

  // stick moved
  @Override
  public boolean axisMoved(Controller controller, int axisCode, float value) {
    
    if ( kVeryVerbose ) {
      Env.debug("Controller " + controllerIndex(controller) 
                + " ("+ controller.getName() + ") " 
                + "axis " + axisCode + " value " + value);
    }
    
    if ( mControllerIndex == -1 || 
         controllerIndex(controller) != mControllerIndex ) {
      return true;
    }

    ControllerMapping mapping = controller.getMapping();
    if      ( axisCode == mapping.axisLeftX ) mAxisX = value;
    else if ( axisCode == mapping.axisLeftY ) mAxisY = value;

    return true;
    
  } // ControllerListener.axisMoved()

} // class KeyControllers
