/*
 *  KeyMonitorApplet.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests;

import com.dishmoth.miniquests.game.KeyMonitor;

import java.awt.*;
import java.awt.event.*;

// keep track of what keys are currently pressed
public class KeyMonitorApplet implements KeyListener, KeyMonitor {

  // number of keys codes that we pay attention to
  static private final int kNumKeyCodes = 256;

  // table of states (pressed or not) for the key codes we're listening to
  private boolean mKeyStates[];

  // constructor: declare as a key listener to the owner
  public KeyMonitorApplet(Component owner) {
    
    reset();
    if ( owner != null ) owner.addKeyListener(this);
    
  } // constructor
  
  // monitor the keys on other components
  public void monitor(Component target) {
    
    if ( target != null ) target.addKeyListener(this);
    
  } // monitor()
  
  // assert that all keys are currently not pressed
  public void reset() {
    
    if ( mKeyStates == null ) mKeyStates = new boolean[kNumKeyCodes];
    for ( int i = 0 ; i < kNumKeyCodes ; i++ ) mKeyStates[i] = false;
    
  } // KeyMonitor.reset()
  
  // check whether a specific key is currently pressed
  public boolean pressed(int keyCode) {
  
    if ( keyCode < 0 || keyCode >= kNumKeyCodes ) return false;
    return (mKeyStates[keyCode]);
  
  } // pressed()
  
  // check whether any of a group of keys is currently pressed
  public boolean up()    { return ( pressed(KeyEvent.VK_UP)
                                 || pressed(KeyEvent.VK_Q)
                                 || pressed(KeyEvent.VK_W)
                                 || pressed(KeyEvent.VK_I)
                                 || pressed(KeyEvent.VK_T) ); }
  public boolean down()  { return ( pressed(KeyEvent.VK_DOWN)
                                 || pressed(KeyEvent.VK_S)
                                 || pressed(KeyEvent.VK_L)
                                 || pressed(KeyEvent.VK_H) ); }
  public boolean left()  { return ( pressed(KeyEvent.VK_LEFT)
                                 || pressed(KeyEvent.VK_K)
                                 || pressed(KeyEvent.VK_A)
                                 || pressed(KeyEvent.VK_G) ); }
  public boolean right() { return ( pressed(KeyEvent.VK_RIGHT)
                                 || pressed(KeyEvent.VK_P)
                                 || pressed(KeyEvent.VK_D)
                                 || pressed(KeyEvent.VK_O)
                                 || pressed(KeyEvent.VK_Y) ); }
  public boolean fire()  { return ( pressed(KeyEvent.VK_CONTROL)
                                 || pressed(KeyEvent.VK_SPACE)
                                 || pressed(KeyEvent.VK_ENTER)
                                 || pressed(KeyEvent.VK_SHIFT)
                                 || pressed(KeyEvent.VK_M)
                                 || pressed(KeyEvent.VK_N)
                                 || pressed(KeyEvent.VK_C)
                                 || pressed(KeyEvent.VK_X)
                                 || pressed(KeyEvent.VK_Z) ); }
  public boolean escape() { return ( pressed(KeyEvent.VK_ESCAPE) ); }

  // check whether any of our main keys is currently pressed
  public boolean any() {

    for ( int i = 0 ; i < kNumKeyCodes ; i++ ) {
      if ( mKeyStates[i] ) return true;
    }
    return false; 
  
  } // KeyMonitor.any()
  
  // how the touch screen maps to controls (not relevant here)
  public void setMode(int mode) {}
  
  // whether on-screen buttons are enabled
  public boolean usingButtons() { return false; }
  
  // set details of the on-screen buttons (not relevant here)
  public void setButtonDetails(int arrowStyle, int fireStyle) {}
  
  // implementation of KeyListener interface
  public void keyPressed(KeyEvent e) {
    
    int keyCode = e.getKeyCode();
    if ( keyCode < 0 || keyCode >= kNumKeyCodes ) return;
    mKeyStates[keyCode] = true;
    
  } // KeyListener.keyPressed()
  
  // implementation of KeyListener interface
  public void keyReleased(KeyEvent e) {

    int keyCode = e.getKeyCode();
    if ( keyCode < 0 || keyCode >= kNumKeyCodes ) return;
    mKeyStates[keyCode] = false;

  } // KeyListener.keyReleased()
  
  // implementation of KeyListener interface
  public void keyTyped(KeyEvent e) {
  } // KeyListener.keyTyped()

} // class KeyMonitorApplet
