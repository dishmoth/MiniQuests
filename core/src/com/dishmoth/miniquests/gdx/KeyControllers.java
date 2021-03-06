/*
 *  KeyControllers.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.gdx;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.controllers.mappings.Ouya;
import com.badlogic.gdx.controllers.mappings.Xbox;
import com.badlogic.gdx.math.Vector3;
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
  
  // what type of controller this is, if recognized
  private int mControllerType;
  
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
      Env.debug("Controller " + index + ": " + c.getName()
                + " (" + typeString(c) + ")");
    }
    
    Controllers.addListener(this);

    if ( controllers.size > 0 ) {
      mControllerIndex = 0;
      mControllerType = identifyType(controllers.get(0));      
    } else {
      mControllerIndex = -1;
      mControllerType = kTypeUnknown;
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
    
    Env.debug("Controller connected: " + controller.getName()
              + " (" + typeString(controller) + ")");
    
    if ( mControllerIndex < 0 ) {
      mControllerIndex = controllerIndex(controller);
      mControllerType = identifyType(controller);
    }
    
  } // ControllerListener.connected()

  // controller disconnected
  @Override
  public void disconnected(Controller controller) {
    
    Env.debug("Controller disconnected: " + controller.getName());
        
  } // ControllerListener.disconnected()

  // see if we recognize the controller's type (enumerated above)
  private int identifyType(Controller controller) {
    
    String id = controller.getName().toLowerCase();
    if      ( id.contains("ouya") )               return kTypeOuya;
    else if ( Xbox.isXboxController(controller) ) return kTypeXBox;
    else                                          return kTypeUnknown;
    
  } // identifyType()
  
  // for debugging, return the type of the controller as a string
  private String typeString(Controller controller) {
    
    int type = identifyType(controller);
    if      ( type == kTypeOuya ) return "OUYA";
    else if ( type == kTypeXBox ) return "XBOX";
    else                          return "UNKNOWN";
    
  } // typeString()
  
  // decode buttons for DPad directions, enumerated in Env (or -1)
  private int dPadDirection(int buttonCode) {

    if ( mControllerType == kTypeOuya ) {
      if ( Env.platform() == Env.Platform.OUYA ) {
        if      ( buttonCode == Ouya.BUTTON_DPAD_UP    ) return Env.UP;
        else if ( buttonCode == Ouya.BUTTON_DPAD_DOWN  ) return Env.DOWN;
        else if ( buttonCode == Ouya.BUTTON_DPAD_LEFT  ) return Env.LEFT;
        else if ( buttonCode == Ouya.BUTTON_DPAD_RIGHT ) return Env.RIGHT;
        else                                             return -1;
      }
    }
    return -1;
    
  } // dPadDirec()
  
  // whether the button is 'home', 'menu', 'start', etc.
  private boolean isEscapeButton(int buttonCode) {

    if ( mControllerType == kTypeOuya ) {
      if ( Env.platform() == Env.Platform.OUYA ) {
        return ( buttonCode == Ouya.BUTTON_MENU );
      }
    } else if ( mControllerType == kTypeXBox ) {
      return ( buttonCode == Xbox.START ||
               buttonCode == Xbox.BACK );
    }
    return false;
    
  } // isEscapeButton()
  
  // decode the axis (0 => x-axis, 1 => y-axis, -1 => unknown)
  private int axisNumber(int axisCode) {

    if ( mControllerType == kTypeXBox ) {
      if      ( axisCode == Xbox.L_STICK_HORIZONTAL_AXIS ) return 0;
      else if ( axisCode == Xbox.L_STICK_VERTICAL_AXIS )   return 1;
    } else {
      if      ( axisCode == 0 ) return 0;
      else if ( axisCode == 1 ) return 1;
    }
    return -1;
    
  } // whichAxis()
  
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
      mControllerType = identifyType(controller);
      reset();
    }

    int dPad = dPadDirection(buttonCode);
    if ( dPad == Env.UP ) {
      mDPadUp = true;
    } else if ( dPad == Env.DOWN ) {
      mDPadDown = true;
    } else if ( dPad == Env.LEFT ) {
      mDPadLeft = true;
    } else if ( dPad == Env.RIGHT ) {
      mDPadRight = true;
    } else if ( isEscapeButton(buttonCode) ) {
      mEscape = true;
    } else {
      mFire = true;
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
    
    int dPad = dPadDirection(buttonCode);
    if ( dPad == Env.UP ) {
      mDPadUp = false;
    } else if ( dPad == Env.DOWN ) {
      mDPadDown = false;
    } else if ( dPad == Env.LEFT ) {
      mDPadLeft = false;
    } else if ( dPad == Env.RIGHT ) {
      mDPadRight = false;
    } else if ( isEscapeButton(buttonCode) ) {
      // do nothing
    } else {
      mFire = false;
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

    int axis = axisNumber(axisCode);
    if      ( axis == 0 ) mAxisX = value;
    else if ( axis == 1 ) mAxisY = value;
    
    return true;
    
  } // ControllerListener.axisMoved()

  // PoV switch events (not interested)
  @Override
  public boolean povMoved(Controller controller, 
                          int povCode, 
                          PovDirection value) {
  
    return false;

  } // ControllerListener.povMoved()

  // x-slider events (not interested)
  @Override
  public boolean xSliderMoved(Controller controller, 
                              int sliderCode,
                              boolean value) {
    return false;

  } // ControllerListener.xSliderMoved()

  // y-slider events (not interested)
  @Override
  public boolean ySliderMoved(Controller controller, 
                              int sliderCode,
                              boolean value) {

    return false;

  } // ControllerListener.ySliderMoved()

  // accelerometer events (not interested)
  @Override
  public boolean accelerometerMoved(Controller controller,
                                    int accelerometerCode, 
                                    Vector3 value) {

    return false;
  
  } // ControllerListener.accelerometerMoved()

} // class KeyControllers
