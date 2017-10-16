/*
 *  SoundsGdx.java
 *  Copyright Simon Hern 2012
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Sounds;

// class for controlling audio
public class SoundsGdx extends Sounds {

  // where the sound files live
  private static final String kFileRoot = "data/";
  
  // the sound clips
  private Sound mSounds[];
  private Music mLoops[];
  
  // constructor
  public SoundsGdx() {

    super();
    
    mSounds = new Sound[kNumSounds];
    mLoops  = new Music[kNumSounds];

  } // constructor
  
  // treat the sound effect as a Music object 
  private boolean playAsMusic(int id) {
    
    return ( id == TITLE || 
             id == VENTURE ||
             id == DUNGEON ||
             id == QUEST_DONE );
    
  } // playAsMusic()
  
  // prepare a sound resource
  @Override
  protected void loadSound(int id, String fileName, int numVersions) {
    
    assert( id >= 0 && id < kNumSounds );
    assert( fileName != null );
    
    String file = kFileRoot + fileName;

    try {

      if ( isLooped(id) ) {
        if ( mLoops[id] != null ) return; // already loaded
        mLoops[id] = Gdx.audio.newMusic(Gdx.files.internal(file));
        mLoops[id].setLooping(true);
      } else if ( playAsMusic(id) ) {
        if ( mLoops[id] != null ) return; // already loaded
        mLoops[id] = Gdx.audio.newMusic(Gdx.files.internal(file));
        mLoops[id].setLooping(false);
      } else {
        if ( mSounds[id] != null ) return; // already loaded
        mSounds[id] = Gdx.audio.newSound(Gdx.files.internal(file));
      }
      
    } catch (Exception ex) {
      Env.debug(ex.getMessage());
      mSounds[id] = null;
      mLoops[id] = null;
    }

  } // Sounds.loadSound()

  // check that all sounds have loaded
  @Override
  protected void checkSounds() {

    mAvailable = true;
    
    for ( int id = 0 ; id < kNumSounds ; id++ ) {
      if ( mSounds[id] == null && mLoops[id] == null ) {
        mAvailable = false;
        return;
      }
    }
    
  } // Sounds.checkSound()

  // play a sound effect
  @Override
  public void play(int id) {
    
    if ( !mAvailable || mMuted ) return;
    
    assert( id >= 0 && id < kNumSounds );
    assert( !isLooped(id) );

    if      ( mSounds[id] != null ) mSounds[id].play();
    else if ( mLoops[id]  != null ) mLoops[id].play();
    
  } // Sounds.play()

  // start a sound looping (if it isn't already)
  @Override
  public void loop(int id) {
    
    if ( !mAvailable || mMuted ) return;
    
    assert( id >= 0 && id < kNumSounds );
    assert( isLooped(id) );
    
    if ( !mLoops[id].isPlaying() ) mLoops[id].play();
    
  } // Sounds.loop()
  
  // stop a looping sound
  @Override
  public void stop(int id) {

    if ( !mAvailable || mMuted ) return;
    
    assert( id >= 0 && id < kNumSounds );
    assert( isLooped(id) );
    
    if ( mLoops[id].isPlaying() ) mLoops[id].stop();
    
  } // Sounds.stop()

} // class SoundsGdx
