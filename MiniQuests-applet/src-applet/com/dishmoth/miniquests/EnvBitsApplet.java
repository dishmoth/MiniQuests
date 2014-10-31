/*
 *  EnvBitsApplet.java
 *  Copyright Simon Hern 2012
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests;

import java.applet.Applet;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

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

  // send a log message back to HQ (for beta testing only)
  public void report(String string) {
    
    try {
      URL url = new URL("http", "dishmoth.com", "/log.html?"+string);
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
  
} // class EnvBitsApplet
