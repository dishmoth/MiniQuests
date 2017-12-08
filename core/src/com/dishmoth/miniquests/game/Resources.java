/*
 *  Resources.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

// manager for graphics, sounds, etc.
public interface Resources {

  // return an EgaImage from file (exception if not known)
  public EgaImage loadEgaImage(String resourceName);
  
} // class Resources
