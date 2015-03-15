/*
 *  Obstacle.java
 *  Copyright Simon Hern 2010
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

// details of an object that could be stood on or walked around 
public interface Obstacle {

  // whether the player can stand at the specified position
  public boolean isPlatform(int x, int y, int z);

  // whether there is space at the specified position
  // (note: a platform position is never empty by this definition)
  public boolean isEmpty(int x, int y, int z);

  // whether the position is outside of the game world
  public boolean isVoid(int x, int y, int z);
  
} // interface Obstacle
