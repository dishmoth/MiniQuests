/*
 *  Sprite3D.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

// 3D sprites must be drawn relative to the camera position
abstract public class Sprite3D extends Sprite {

  // reference to the game's Camera object
  protected Camera mCamera = null;
  
  // maintain a reference to the game's Camera sprite
  @Override
  public void observeArrival(Sprite newSprite) { 
    
    if ( newSprite instanceof Camera ) {
      assert( mCamera == null );
      mCamera = (Camera)newSprite;
    }
    
  } // Sprite.observeArrival()
  
  // when a Sprite leaves the game it is forgotten about by all other Sprites
  @Override
  public void observeDeparture(Sprite deadSprite) {
    
    if ( deadSprite instanceof Camera ) {
      assert( mCamera == deadSprite );
      mCamera = null;
    } else {
      mSpritesToWatch.remove(deadSprite);
    }
  
  } // Sprite.observeDeparture()  

} // class Sprite3D
