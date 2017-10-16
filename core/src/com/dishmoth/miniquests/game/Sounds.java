/*
 *  Sounds.java
 *  Copyright Simon Hern 2010
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
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
                             CRITTER_DEATH    = 22,
                             CRITTER_STUN     = 23,
                             SPINNER_STOP     = 24,
                             SPIKES           = 25,
                             SPIKES_QUIET     = 26,
                             TRIFFID_HIT      = 27,
                             TRIFFID_DEATH    = 28,
                             TRIFFID_FIRE     = 29,
                             TRIFFID_EMERGE   = 30,
                             SPOOK_DEATH      = 31,
                             SPOOK_EMERGE     = 32,
                             TBOSS_HIT        = 33,
                             TBOSS_DEATH      = 34,
                             TBOSS_FIRE       = 35,
                             TBOSS_EMERGE     = 36,
                             TBOSS_SPLAT      = 37,
                             TBOSS_GRUNT      = 38,
                             DRAGON_HIT       = 39,
                             DRAGON_FIRE      = 40,
                             DRAGON_EMERGE    = 41,
                             FLOOR_MUNCH_A    = 42,
                             FLOOR_MUNCH_B    = 43,
                             FLOOR_BLAST      = 44,
                             FLOOR_PEEK       = 45,
                             FLOOR_HIT        = 46,
                             FLOOR_DEATH      = 47,
                             SUCCESS          = 48,
                             QUEST_DONE       = 49,
                             MENU_1           = 50,
                             MENU_2           = 51,
                             MAP              = 52,
                             DUNGEON          = 53,
                             TITLE            = 54,
                             VENTURE          = 55;
  protected static final int kNumSounds       = 56; 
  
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
