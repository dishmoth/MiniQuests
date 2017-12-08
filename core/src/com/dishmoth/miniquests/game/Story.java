/*
 *  Story.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.*;

// base class for Story objects
// a Story object dictates what happens during one game tick
// (Sprite objects are autonomous, the Story object orchestrates them)
abstract public class Story {

  // story event: the game manager has just been constructed
  public static class EventGameBegins extends StoryEvent {}

  // story event: just returned from another story
  public static class EventStoryContinue extends StoryEvent {}

  // game moves on by one frame
  // a list of StoryEvent objects is processed, possibly added to
  // new sprites may be added to the sprite manager
  // returns a new Story to take over from this one (or null for no change)
  abstract public Story advance(LinkedList<StoryEvent> storyEvents,
                                SpriteManager          spriteManager);
    
} // class Story
