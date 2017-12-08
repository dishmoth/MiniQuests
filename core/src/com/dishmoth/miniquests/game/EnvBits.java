/*
 *  EnvBits.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

// platform-dependent code for Env
public interface EnvBits {

  // prepare before use
  public void initialize();
  
  // display debug text
  public void debug(String message);
  
  // which platform we're running on
  public Env.Platform platform();
  
  // terminate the program (on Android there may be a delay)
  public void exit();
  
  // save some game data
  public void save(byte data[]);
  
  // load the game data
  public byte[] load();
  
  // send a log message back to HQ (for beta testing only)
  public void report(String address, String message);
  
} // interface EnvBits
