/*
 *  EnvBitsGdx.java
 *  Copyright Simon Hern 2012
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.gdx;

import com.dishmoth.miniquests.game.BitBuffer;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.EnvBits;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.controllers.mappings.Ouya;

// platform-dependent code for Env (libgdx version)
public class EnvBitsGdx implements EnvBits {

  // label to use for debug logging
  private static final String kLogTag = "MiniQuests";
  
  // use shared preferences to store game data
  private static final String kPreferencesName = "miniquests",
                              kSaveName        = "save";

  // which platform we're running on
  private Env.Platform mPlatform;
  
  // prepare before use
  public void initialize() {
    
    if ( Gdx.app.getType() == ApplicationType.Desktop ) {
      mPlatform = Env.Platform.DESKTOP;
    } else if ( Gdx.app.getType() == ApplicationType.WebGL ) {
      mPlatform = Env.Platform.HTML;
    } else if ( Gdx.app.getType() == ApplicationType.Android ) {
      mPlatform = Env.Platform.ANDROID;
    
      if ( Ouya.runningOnOuya ) {
          debug("Running on Ouya");
          mPlatform = Env.Platform.OUYA;        
      }
      
      /*
      String device = null;
      try {
        Class<?> buildClass = Class.forName("android.os.Build");
        Field deviceField = buildClass.getDeclaredField("DEVICE");
        Object obj = deviceField.get(null);
        if ( obj != null ) device = obj.toString();
      } catch( Exception ex ) {}
  
      if ( device != null ) {
        debug("Device: " + device);
        device = device.toLowerCase();
        if ( device.startsWith("ouya") || device.startsWith("cardhu") ) {
          debug("Running on Ouya");
          mPlatform = Env.Platform.OUYA;
        }
      }
      */
    } else {
      assert(false);
    }
      
  } // EnvBits.initialize()
  
  // display debug text
  public void debug(String message) {

    Gdx.app.log(kLogTag, message);
    
  } // EnvBits.debug()

  // which platform we're running on
  public Env.Platform platform() { return mPlatform; }
  
  // for debugging: override the actual platform
  public void setPlatform(Env.Platform platform) { mPlatform = platform; }
  
  // terminate the program (there may be a delay before it takes effect)
  public void exit() {

    Gdx.app.exit();
    Env.debug("exit pending...");
    
  } // EnvBits.exit()

  // save some game data (on Android devices only)
  public void save(byte data[]) {

    assert ( data != null );
    
    //if ( Gdx.app.getType() != Application.ApplicationType.Android ) {
    //  return;
    //}

    BitBuffer converter = new BitBuffer(data);
    String str = converter.toString();
    
    try {
      Preferences pref = Gdx.app.getPreferences(kPreferencesName);
      pref.putString(kSaveName, str);
      pref.flush();
    } catch ( Exception ex ) {
      Gdx.app.log(kLogTag, "Error trying to save preferences", ex);
    }
    
  } // EnvBits.save()
  
  // load the game data (on Android devices only)
  public byte[] load() {
    
    //if ( Gdx.app.getType() != Application.ApplicationType.Android ) {
    //  return null;
    //}

    String str = null;
    try {
      Preferences pref = Gdx.app.getPreferences(kPreferencesName);
      str = pref.getString(kSaveName, null);
    } catch ( Exception ex ) {
      Gdx.app.log(kLogTag, "Error trying to load preferences", ex);
      return null;
    }
    
    if ( str == null ) {
      Gdx.app.log(kLogTag, "No saved data found");
      return null;
    } else {
      Gdx.app.log(kLogTag, "Loaded saved data: " + str);
      BitBuffer converter = new BitBuffer(str);
      return converter.toBytes();
    }
    
  } // EnvBits.load()
  
  // send a log message back to HQ (for beta testing only)
  public void report(String address, String string) {
    
    HttpRequest httpGet = new HttpRequest(HttpMethods.GET);
    httpGet.setUrl(address);
    httpGet.setContent(string);
    
    Gdx.net.sendHttpRequest( httpGet, new HttpResponseListener() {
      public void handleHttpResponse(HttpResponse httpResponse) {
        //System.out.println(httpResponse.getResultAsString());
        Env.debug("Log report: sent okay");
      }
      public void failed(Throwable thr) {
        Env.debug("Log report: failure (" + thr.toString() + ")");
        }
      @Override
      public void cancelled() {
        Env.debug("Log report: cancelled");
        }
      } );
    
  } // EnvBits.report()
  
} // class EnvBitsGdx
