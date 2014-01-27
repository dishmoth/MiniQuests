/*
 *  Resources.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

// manager for graphics, sounds, etc.
public interface Resources {

  // return an EgaImage from file (exception if not known)
  public EgaImage loadEgaImage(String resourceName);
  
} // class Resources
