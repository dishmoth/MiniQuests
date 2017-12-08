/*
 *  EnvBitsApp.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.EnvBits;

// platform-dependent code for Env (pure Java version)
public class EnvBitsApp implements EnvBits {

  // constructor
  public EnvBitsApp() {
  } // constructor()
  
  // prepare before use
  public void initialize() {
  } // EnvBits.initialize()
  
  // display debug text
  public void debug(String message) {

    System.out.println(message);
    
  } // EnvBits.debug()

  // which platform we're running on
  public Env.Platform platform() { return Env.Platform.APP; }

  // terminate the program
  public void exit() {

    System.exit(0);
    
  } // EnvBits.exit()

  // save some game data (not currently implemented for the 'App' project)
  public void save(byte data[]) {
  } // EnvBits.save()
  
  // load the game data (not currently implemented for the 'App' project)
  public byte[] load() {
    
    return null;
    
  } // EnvBits.load()

  // send a log message back to HQ (for beta testing only)
  public void report(String address, String message) {
    
    try {
      URL url = new URL(address + "?" + message);
      URLConnection conn = url.openConnection();
      InputStream is = conn.getInputStream();
      BufferedReader in = new BufferedReader( new InputStreamReader(is) );
      //String line;
      //while ( (line=in.readLine()) != null ) System.out.println(line);
      in.close();
      Env.debug("Log report: sent okay");
    } catch ( Exception ex ) {
      Env.debug("Log report: failed (" + ex.toString() + ")");
    }
    
  } // EnvBits.report()
  
} // class EnvBitsApp
