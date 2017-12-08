/*
 *  Sprite.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.*;

// a Sprite is an independent game entity
// usually each Sprite has an associated image on the game screen
//
// the basic behaviour of a Sprite is to "update" itself and to "draw" itself
//
// how a Sprite "updates" itself during one game tick is a little convoluted
// there are three stages:
// 1) advance(), the Sprite changes its state independently of other Sprites
//    (for example, the Sprite moves across the screen)
// 2) interact(), the Sprite considers what other Sprites are up to 
//    (for example, two Sprites may collide)
// 3) aftermath(), the Sprite adjusts itself following any interactions
//    (for example, the Sprite explodes because of a collision)
//
// see the SpriteManager class for more details
abstract public class Sprite {

  // in general a Sprite will keep track of other Sprites it may interact with
  protected LinkedList<Sprite> mSpritesToWatch;
  
  // flags set by Story and read by SpriteManager
  // advance(), interact() and aftermath() are switched off by the first flag
  // draw() is switched off by the second flag
  public boolean mAdvanceDisabled,
                 mDrawDisabled;
  
  // constructor
  public Sprite() {
    
    mSpritesToWatch  = new LinkedList<Sprite>();
    mAdvanceDisabled = false;
    mDrawDisabled    = false;
    
  } // constructor

  // when a new Sprite is introduced into the game, each existing Sprite
  // has the option of whether or not to add it to their mSpritesToWatch list
  // (by default all new Sprites are ignored)
  public void observeArrival(Sprite newSprite) { }
  
  // when a Sprite leaves the game it is forgotten about by all other Sprites
  public void observeDeparture(Sprite deadSprite) {
    
    mSpritesToWatch.remove(deadSprite);
  
  } // observeDeparture()
  
  // update stage 1: move or otherwise change the state of the Sprite
  // - if the Sprite spawns new Sprites (e.g., it fires a bullet) then these
  //   should be created and added to the addTheseSprites list
  // - if the Sprite leaves the game it adds itself to the killTheseSprites 
  //   list (in general Sprites only removes themselves; other Sprites may
  //   be told to "do the decent thing" during the "interact" stage)
  // - any announcements are added as events to the newStoryEvents list
  abstract public void advance(LinkedList<Sprite>     addTheseSprites, 
                               LinkedList<Sprite>     killTheseSprites,
                               LinkedList<StoryEvent> newStoryEvents);
  
  // stage 2: compare notes with other Sprites (e.g., check for collisions)
  // - the mSpritesToWatch list is usually consulted at this point
  // - no Sprites are created or destroyed during this stage since that
  //   could mess up interactions between other Sprites 
  // - the results of an interaction are usually stored up to be applied
  //   during the "aftermath" stage
  // - which Sprite is "active" and which is "passive" during an interaction
  //   (i.e., which of them's interact() function does the work) is left
  //   to the Sprites in question to decide
  public void interact() { }

  // stage 3: react to anything that happened during the "interact" stage
  // - the outputs from this function are identical to those described above
  //   for the "advance" stage
  // - in general this function should be used to implement behaviour that
  //   happens as a consequence of the "interact" stage, whereas behaviour
  //   of the Sprite in isolation should happen during the "advance" stage
  public void aftermath(LinkedList<Sprite>     addTheseSprites, 
                        LinkedList<Sprite>     killTheseSprites,
                        LinkedList<StoryEvent> newStoryEvents) { }
  
  // display the Sprite's image on the screen
  abstract public void draw(EgaCanvas canvas);
  
} // class Sprite
