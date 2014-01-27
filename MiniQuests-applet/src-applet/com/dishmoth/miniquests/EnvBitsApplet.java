/*
 *  EnvBitsApplet.java
 *  Copyright Simon Hern 2012
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests;

import java.applet.Applet;

import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.EnvBits;

// platform-dependent code for Env (Applet version)
public class EnvBitsApplet implements EnvBits {

  // cookie name
  private static final String kSaveName = "MiniQuests_save"; 
  
  // reference to the owner
  private Applet mOwner;
  
  // constructor
  public EnvBitsApplet(Applet owner) {
    
    mOwner = owner;
    
  } // constructor()
  
  // prepare before use
  public void initialize() {

    Storage.initializeCookies(mOwner);
    
  } // EnvBits.initialize()
  
  // display debug text
  public void debug(String message) {

    System.out.println(message);
    
  } // EnvBits.debug()

  // which platform we're running on
  public Env.Platform platform() { return Env.Platform.APPLET; }
  
  // whether we are running on an Android device
  public boolean isAndroid() { return false; }
  
  // whether we are on a console/non-touchscreen (e.g. Ouya)
  public boolean onTelly() { return false; }
  
  // terminate the program
  public void exit() {

    System.exit(0);
    
  } // EnvBits.exit()

  // save some game data
  public void save(byte data[]) {

    Storage.save(kSaveName, data);
    Storage.flushCache();
    
  } // EnvBits.save()
  
  // load the game data
  public byte[] load() {
    
    return Storage.load(kSaveName);
    
  } // EnvBits.load()

} // class EnvBitsApplet
