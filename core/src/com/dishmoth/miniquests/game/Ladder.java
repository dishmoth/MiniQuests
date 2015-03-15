/*
 *  Ladder.java
 *  Copyright Simon Hern 2010
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// a one-off bit of scenery
public class Ladder extends Sprite3D implements Obstacle {

  // colour of the ladder
  private static final byte kColour = 32;

  // position of base
  private static final int kXPos = 8,
                           kYPos = 4,
                           kZPos = 2;

  // reference to the Player
  private Player mPlayer;
  
  // constructor
  public Ladder() {
    
  } // constructor

  // check for Sprites we want to keep track of
  @Override
  public void observeArrival(Sprite newSprite) { 

    super.observeArrival(newSprite);

    if ( newSprite instanceof Player ) {
      assert( mPlayer == null );
      mPlayer = (Player)newSprite;
    }
    
  } // Sprite.observeArrival()
  
  // keep track of Sprites that have been destroyed
  @Override
  public void observeDeparture(Sprite deadSprite) {

    if ( deadSprite instanceof Player ) {
      assert( mPlayer == deadSprite );
      mPlayer = null;
    }

    super.observeDeparture(deadSprite);
    
  } // Sprite.observeDeparture()  

  // quick hack: don't let the player walk through the ladder
  public boolean isEmpty(int x, int y, int z) {

    if ( mPlayer == null ) return true;    
    if ( y != kYPos ) return true;
    if ( z != mPlayer.getZPos() + 4 ) return true;    
    if ( mPlayer.getYPos() != kYPos ) return true;
    if ( mPlayer.getXPos() == kXPos && x == kXPos+1 ) return false;
    if ( mPlayer.getXPos() == kXPos+1 && x == kXPos ) return false;
    return true;
    
  } // Obstacle.isEmpty()
  
  // unneeded bits of the Obstacle interface
  public boolean isPlatform(int x, int y, int z) { return false; }
  public boolean isVoid(int x, int y, int z) { return false; }

  // nothing to do here
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {

  } // Sprite.advance()

  // display the object
  @Override
  public void draw(EgaCanvas canvas) {

    final int xPos = kXPos - mCamera.xPos(),
              yPos = kYPos - mCamera.yPos(),
              zPos = kZPos - mCamera.zPos();

    final int x0 = Env.originXPixel() + 2*xPos - 2*yPos,
              y0 = Env.originYPixel() - xPos - yPos - zPos;
    final float depth = xPos + yPos;
    
    canvas.fill(x0, x0, 0, y0-1, depth+1, kColour);
    canvas.plot(x0, y0-1, depth+1-0.1f, kColour);
    canvas.fill(x0+2, x0+2, 0, y0, depth, kColour);
    canvas.plot(x0+2, y0, depth-0.1f, kColour);
    
    for ( int y = y0-2 ; y >= 0 ; y -= 2) {
      canvas.plot(x0+1, y, depth+1, kColour);
    }
    
  } // Sprite.draw()

} // class Ladder
