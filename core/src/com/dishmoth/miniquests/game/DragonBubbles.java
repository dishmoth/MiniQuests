/*
 *  DragonBubbles.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// flame effect before the dragon appears
public class DragonBubbles extends Flame {

  // how long the effect lasts for
  private int mTimer;

  // constructor
  public DragonBubbles(int x, int y, int z, int lifetime) {

    super(x, y, z-2);

    assert( lifetime > 0 );
    mTimer = lifetime;
    
    setColours( new byte[]{ 36, 4 } );
    
  } // constructor
  
  // update the effect
  @Override
  public void advance(LinkedList<Sprite>     addTheseSprites,
                      LinkedList<Sprite>     killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {

    super.advance(addTheseSprites, killTheseSprites, newStoryEvents);
    
    if ( --mTimer == 0 ) {
      setFlame(false);
    }

    if ( !isOn() && mParticles.size() == 0 ) {
      killTheseSprites.add(this);
    }
    
  } // Sprite.advance()

} // class DragonBubbles
