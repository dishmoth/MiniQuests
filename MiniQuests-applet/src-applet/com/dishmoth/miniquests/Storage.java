/*
 *  Storage.java
 *  Copyright Simon Hern 2010
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests;

import java.applet.Applet;

// access to cookies (Applet) or muffins (Web Start)
public class Storage {
  
  // use muffins (default) or cookies
  static private boolean mUseMuffins = true;

  // initialize to use cookies
  static public void initializeCookies(Applet applet) {
    
    assert( applet != null );
    StorageCookies.initialize(applet);
    mUseMuffins = false;

  } // initializeCookies()

  // initialize to use muffins
  static public void initializeMuffins() {

    mUseMuffins = true;

  } // initializeMuffins()
  
  // create a new save entry, overwrite if it already exists
  static public void save(String name, byte[] data) {

    if ( mUseMuffins ) StorageMuffins.saveToMuffin(name, data);
    else               StorageCookies.saveToCookie(name, bytesToString(data));
    
  } // save()
  
  // read data from a saved entry (returns null if no data)
  static public byte[] load(String name) {
    
    if ( mUseMuffins ) {
      return StorageMuffins.loadFromMuffin(name);
    } else {
      String data = StorageCookies.loadFromCookie(name);
      return ( (data == null) ? null : stringToBytes(data) );
    }
        
  } // load()

  // remove a saved entry
  static public void delete(String name) {

    if ( mUseMuffins ) StorageMuffins.deleteMuffin(name);
    else               StorageCookies.deleteCookie(name);

  } // delete()

  // update the true storage based on our cached data
  static public void flushCache() {
    
    if ( mUseMuffins ) StorageMuffins.flushCache();
    
  } // flushCache()
  
  // utility function for debugging, list the available saved entries
  static public void list() {

    if ( mUseMuffins ) StorageMuffins.listMuffins();
    else               StorageCookies.listCookies();

  } // list()

  // convert a bytes sequence to a hex string
  static private String bytesToString(byte data[]) {
    
    assert( data != null && data.length > 0 );
    
    String result = "";
    for ( byte b : data) {
      String hex = Integer.toHexString(((int)b) & 0xFF);
      if ( hex.length() < 2 ) hex = "0" + hex;
      result += hex;
    }
    return result;
    
  } // bytesToString()
  
  // convert a hex string to a sequence of bytes
  static private byte[] stringToBytes(String data) {

    assert( data != null && data.length() > 0 );

    if ( (data.length() % 2) != 0 ) {
      data = "0" + data;
    }
    
    byte result[] = new byte[data.length()/2];
    for ( int k = 0 ; k < result.length ; k++ ) {
      try {
        int val = Integer.parseInt(data.substring(2*k, 2*k+2), 16);
        if ( val < 0 || val > 255 ) return null;
        result[k] = (byte)val;
      } catch ( Exception ex ) {
        return null;
      }
    }
    return result;
    
  } // stringToBytes()
  
} // class Storage
