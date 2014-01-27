/*
 *  StorageCookies.java
 *  Copyright Simon Hern 2010
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests;

import com.dishmoth.miniquests.game.Env;

import java.applet.Applet;
import java.util.Calendar;
import java.util.StringTokenizer;

import netscape.javascript.JSObject;

// persistent storage using cookies
public class StorageCookies {

  // status flag (false => not an Applet, storage unavailable)
  static private boolean mValid = false;

  // reference to the applet's document
  static private JSObject mDocument = null;
  
  // prepare for using cookies
  static public void initialize(Applet applet) {

    assert( applet != null );

    try {
      JSObject browser = (JSObject)JSObject.getWindow(applet);
      mDocument = (JSObject)browser.getMember("document");
      mValid = true;
    } catch (Exception ex) {
      Env.debug("StorageCookies: not available (" + ex.toString() + ")");
      mValid = false;
    }
    
  } // initialize()
  
  // display a list of all cookies
  static public void listCookies() {

    if ( !mValid ) return;

    String cookies = null;
    try {
      cookies = (String)mDocument.getMember("cookie");
    } catch (Exception ex) {
      Env.debug("StorageCookies: not available (" + ex.toString() + ")");
      mValid = false;
      return;
    }

    if ( cookies == null || cookies.length() == 0 ) {
      System.out.println("StorageCookies: no cookies found");
    }
    
    StringTokenizer tokens = new StringTokenizer(cookies, ";", false);
    for ( int num = 1 ; tokens.hasMoreTokens() ; num++ ) {
      String cookie = tokens.nextToken().trim();
      System.out.println("StorageCookies: cookie " + num + ": " + cookie);
    }

  } // listCookies()
  
  // create or overwrite a cookie
  static public void saveToCookie(String name, String data) {
    
    if ( !mValid ) return;

    assert( name != null && name.length() > 0 );
    assert( data != null && data.length() > 0 );
    
    Calendar cal = java.util.Calendar.getInstance();
    cal.add(java.util.Calendar.YEAR, 5);
    String expire = "; expires=" + cal.getTime().toString();
    
    try {
      String str = name + "=" + data + expire;       
      mDocument.setMember("cookie", str);
      Env.debug("StorageCookies: wrote cookie: " + str);
    } catch (Exception ex) {
      Env.debug("StorageCookies: not available (" + ex.toString() + ")");
      mValid = false;
      return;
    }
    
  } // saveToCookie()
  
  // read data from a cookie (returns null if no data)
  static public String loadFromCookie(String name) {
    
    if ( !mValid ) return null;

    assert( name != null && name.length() > 0 );
    
    String cookies = null;
    try {
      cookies = (String)mDocument.getMember("cookie");
    } catch (Exception ex) {
      Env.debug("StorageCookies: not available (" + ex.toString() + ")");
      mValid = false;
      return null;
    }

    if ( cookies == null || cookies.length() == 0 ) {
      Env.debug("StorageCookies: cookie undefined: " + name);
      return null;
    }
    
    StringTokenizer tokens = new StringTokenizer(cookies, ";", false);
    while ( tokens.hasMoreTokens() ) {
      String cookie = tokens.nextToken().trim();
      StringTokenizer tok = new StringTokenizer(cookie, "=", false);
      String key = tok.nextToken().trim();
      if ( key.equals(name) ) {
        if ( tok.hasMoreTokens() ) {
          String data = tok.nextToken().trim();
          Env.debug("StorageCookies: read cookie: " + cookie);
          return data;
        } else {
          Env.debug("StorageCookies: cookie empty: " + name); 
          return null;
        }
      }
    }

    Env.debug("StorageCookies: cookie undefined: " + name);
    return null;
    
  } // loadFromCookie()
  
  // remove a cookie
  static public void deleteCookie(String name) {
    
    if ( !mValid ) return;

    assert( name != null && name.length() > 0 );
    
    Calendar cal = java.util.Calendar.getInstance();
    cal.add(java.util.Calendar.MONTH, -1);
    String expire = "; expires=" + cal.getTime().toString();
    
    try {
      String str = name + expire;       
      mDocument.setMember("cookie", str);
      Env.debug("StorageCookies: deleted cookie: " + name); 
    } catch (Exception ex) {
      Env.debug("StorageCookies: not available (" + ex.toString() + ")");
      mValid = false;
      return;
    }
    
  } // deleteCookie()
  
} // class StorageCookies
