/*
 *  KeyMonitor.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

// keep track of what keys are currently pressed
public interface KeyMonitor {

  // assert that all keys are currently not pressed
  public void reset();
  
  // check whether any of a group of keys is currently pressed
  public boolean up();
  public boolean down();
  public boolean left();
  public boolean right();
  public boolean fire();
  public boolean escape();
  
  // check whether any key is currently pressed
  public boolean any();
  
  // how the touch screen maps to controls (Android only)
  public static final int MODE_GAME  = 0, // (default)
                          MODE_MAP   = 1,
                          MODE_QUERY = 2;
  public void setMode(int mode);
  
  // set details of the on-screen buttons (Android only)
  public void setButtonDetails(int arrowStyle, int fireStyle);
  
} // class KeyMonitor
