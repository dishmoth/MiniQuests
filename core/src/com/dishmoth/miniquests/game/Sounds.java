/*
 *  Sounds.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.Iterator;
import java.util.LinkedList;

// base class for controlling audio
abstract public class Sounds {

  // identifiers for the different effects
  public static final int    STEP             =  0,
                             SPLASH           =  1,
                             ARROW            =  2,
                             ARROW_HIT        =  3,
                             HERO_GRUNT       =  4,
                             HERO_DEATH       =  5,
                             SWITCH_ON        =  6,
                             SWITCH_OFF       =  7,
                             SWITCH_DEEP      =  8,
                             FLAME            =  9,
                             FLAME_WHOOSH     = 10,
                             GRIND            = 11,
                             CHEST            = 12,
                             GATE             = 13,
                             TICK             = 14,
                             TOCK             = 15,
                             MATERIALIZE      = 16,
                             WRENCH           = 17,
                             TREE_TWIST       = 18,
                             FOUNTAIN         = 19,
                             FOUNTAIN_TWIST   = 20,
                             FOUNTAIN_UNTWIST = 21,
                             PUZZLE_ON        = 22,
                             PUZZLE_OFF       = 23,
                             PUZZLE_CHANGE    = 24,
                             CRITTER_DEATH    = 25,
                             CRITTER_STUN     = 26,
                             SPINNER_STOP     = 27,
                             SPIKES           = 28,
                             SPIKES_QUIET     = 29,
                             TRIFFID_HIT      = 30,
                             TRIFFID_DEATH    = 31,
                             TRIFFID_FIRE     = 32,
                             TRIFFID_EMERGE   = 33,
                             SPOOK_DEATH      = 34,
                             SPOOK_EMERGE     = 35,
                             TBOSS_HIT        = 36,
                             TBOSS_DEATH      = 37,
                             TBOSS_FIRE       = 38,
                             TBOSS_EMERGE     = 39,
                             TBOSS_SPLAT      = 40,
                             TBOSS_GRUNT      = 41,
                             DRAGON_HIT       = 42,
                             DRAGON_FIRE      = 43,
                             DRAGON_EMERGE    = 44,
                             FLOOR_MUNCH_A    = 45,
                             FLOOR_MUNCH_B    = 46,
                             FLOOR_BLAST      = 47,
                             FLOOR_PEEK       = 48,
                             FLOOR_HIT        = 49,
                             FLOOR_DEATH      = 50,
                             SNAKE_EGG        = 51,
                             SNAKE_HATCH      = 52,
                             SNAKE_HIT_0      = 53,
                             SNAKE_HIT_1      = 54,
                             SNAKE_HIT_2      = 55,
                             SNAKE_TRANSFORM  = 56,
                             SNAKE_REVENGE    = 57,
                             SNAKE_DEATH      = 58,
                             SUCCESS          = 59,
                             QUEST_DONE       = 60,
                             MENU_1           = 61,
                             MENU_2           = 62,
                             MAP              = 63,
                             DUNGEON          = 64,
                             TITLE            = 65,
                             VENTURE          = 66;
  protected static final int kNumSounds       = 67; 
 
  // true if sounds have been loaded and all is operational
  protected boolean mAvailable;
  
  // true if audio has been turned off by the user
  protected boolean mMuted;

  // queued sound effects [delay,id]
  protected LinkedList<int[]> mDelayedSounds;
  
  // constructor
  public Sounds() {
    
    mAvailable = false;
    mMuted = false;

    mDelayedSounds = new LinkedList<int[]>();
    
  } // constructor
  
  // mute or unmute the sound
  public void mute() { mMuted = true; }
  public void unmute() { mMuted = false; }
  public boolean isMuted() { return mMuted; }
  
  // load and prepare all the sound effects
  public void initialize() {

    if ( mAvailable ) return;

    Env.debug("Loading sound files");
    
    // hack around a bug in libgdx (some ogg files crash when loading)
    String gdxType = "wav";
    
    loadSound(STEP, "steps.ogg", 3);
    loadSound(SPLASH, "splash.ogg", 2);
    loadSound(ARROW, "arrow.ogg", 1);
    loadSound(ARROW_HIT, "arrow_hit.ogg", 1);
    loadSound(HERO_GRUNT, "hmmph.ogg", 1);
    loadSound(HERO_DEATH, "death.ogg", 1);
    loadSound(SWITCH_ON, "switch_on.ogg", 1);
    loadSound(SWITCH_OFF, "switch_off.ogg", 2);
    loadSound(SWITCH_DEEP, "switch_deep.ogg", 1);
    loadSound(GRIND, "grind.ogg", 1);
    loadSound(CHEST, "chest_open.ogg", 1);
    loadSound(GATE, "gate.ogg", 1);
    loadSound(TICK, "tick."+gdxType, 1); 
    loadSound(TOCK, "tock."+gdxType, 1);
    loadSound(FLAME, "flame.ogg", 1);
    loadSound(FLAME_WHOOSH, "whoosh.ogg", 1);
    loadSound(MATERIALIZE, "pop.ogg", 1);
    loadSound(WRENCH, "wrench.ogg", 1);
    loadSound(TREE_TWIST, "tree_twist.ogg", 1);
    loadSound(FOUNTAIN, "fountain.ogg", 1);
    loadSound(FOUNTAIN_TWIST, "fountain_twist.ogg", 1);
    loadSound(FOUNTAIN_UNTWIST, "fountain_untwist.ogg", 1);
    loadSound(PUZZLE_ON, "puzzle_on.ogg", 1);
    loadSound(PUZZLE_OFF, "puzzle_off.ogg", 1);
    loadSound(PUZZLE_CHANGE, "puzzle_change.ogg", 1);
    loadSound(CRITTER_DEATH, "critter_death.ogg", 1);
    loadSound(CRITTER_STUN, "critter_stun.ogg", 1);
    loadSound(SPINNER_STOP, "spinner_stop.ogg", 1);
    loadSound(SPIKES, "spikes.wav", 1);
    loadSound(SPIKES_QUIET, "spikes_quiet.wav", 1);
    loadSound(TRIFFID_HIT, "triffid_hit.ogg", 1);
    loadSound(TRIFFID_DEATH, "triffid_death.ogg", 1);
    loadSound(TRIFFID_FIRE, "triffid_fire.ogg", 1);
    loadSound(TRIFFID_EMERGE, "triffid_emerge.ogg", 1);
    loadSound(SPOOK_DEATH, "spook_death.ogg", 1);
    loadSound(SPOOK_EMERGE, "spook_emerge.ogg", 1);
    loadSound(TBOSS_HIT, "tboss_hit.ogg", 1);
    loadSound(TBOSS_DEATH, "tboss_death.ogg", 1);
    loadSound(TBOSS_FIRE, "tboss_fire.ogg", 1);
    loadSound(TBOSS_EMERGE, "tboss_emerge.ogg", 1);
    loadSound(TBOSS_SPLAT, "tboss_splat.ogg", 1);
    loadSound(TBOSS_GRUNT, "tboss_grunt.ogg", 1);
    loadSound(DRAGON_HIT, "dragon_hit.ogg", 1);
    loadSound(DRAGON_FIRE, "dragon_fire.ogg", 1);
    loadSound(DRAGON_EMERGE, "dragon_emerge.ogg", 1);
    loadSound(FLOOR_MUNCH_A, "floor_munch_A.ogg", 1);
    loadSound(FLOOR_MUNCH_B, "floor_munch_B.ogg", 1);
    loadSound(FLOOR_BLAST, "floor_blast.ogg", 1);
    loadSound(FLOOR_PEEK, "floor_peek.ogg", 1);
    loadSound(FLOOR_HIT, "floor_hit.ogg", 1);
    loadSound(FLOOR_DEATH, "floor_death.ogg", 1);
    loadSound(SNAKE_EGG, "snake_egg.ogg", 1);
    loadSound(SNAKE_HATCH, "snake_hatch.ogg", 1);
    loadSound(SNAKE_HIT_0, "snake_hit_0.ogg", 1);
    loadSound(SNAKE_HIT_1, "snake_hit_1.ogg", 1);
    loadSound(SNAKE_HIT_2, "snake_hit_2.ogg", 1);
    loadSound(SNAKE_TRANSFORM, "snake_transform.ogg", 1);
    loadSound(SNAKE_REVENGE, "snake_revenge.ogg", 1);
    loadSound(SNAKE_DEATH, "snake_death.ogg", 1);
    loadSound(SUCCESS, "chimes.ogg", 1);
    loadSound(QUEST_DONE, "big_chimes.ogg", 1);
    loadSound(MENU_1, "menu1."+gdxType, 1);
    loadSound(MENU_2, "menu2.ogg", 1);
    loadSound(MAP, "map.ogg", 1);
    loadSound(DUNGEON, "dungeon.ogg", 1);
    loadSound(VENTURE, "salute_far.ogg", 1);
    loadSound(TITLE, "salute.ogg", 1);

    checkSounds();
    if ( mAvailable ) Env.debug("Sounds loaded successfully");
    else              Env.debug("Sound disabled; effects failed to load");
    
  } // initialize()

  // identify which sounds must be looped
  protected boolean isLooped(int id) {
    
    return ( id == FLAME || id == FOUNTAIN );
    
  } // isLooped()
  
  // prepare a sound resource
  abstract protected void loadSound(int id, String fileName, int numVersions);

  // check that all sounds have loaded
  abstract protected void checkSounds();
  
  // note that a frame has passed (and play delayed sounds)
  public void advance() {
    
    for ( Iterator<int[]> it = mDelayedSounds.iterator() ; it.hasNext() ; ) {
      int details[] = it.next();
      assert( details != null && details.length == 2 );
      assert( details[0] > 0 );
      details[0] -= 1;
      if ( details[0] == 0 ) {
        play(details[1]);
        it.remove();
      }
    }
    
  } // advance()
  
  // play a sound effect
  abstract public void play(int id);
  
  // play a sound effect after a delay 
  public void play(int id, int delay) {
    
    assert( id >= 0 && id < kNumSounds );
    assert( delay >= 0 );
    if ( delay == 0 ) {
      play(id);
    } else {
      mDelayedSounds.add(new int[]{ delay, id });
    }
    
  } // play(delay)
  
  // start a sound looping (if it isn't already)
  abstract public void loop(int id);
  
  // stop a looping sound
  abstract public void stop(int id);

  // stop all looping sounds
  public void stopAll() {

    for ( int id = 0 ; id < kNumSounds ; id++ ) {
      if ( isLooped(id) ) stop(id);
    }
    mDelayedSounds.clear();
    
  } // Sounds.stopAll()

} // class Sounds
