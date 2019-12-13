/*
 *  SoundsGdx.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Sounds;

import java.util.Arrays;

// class for controlling audio
public class SoundsGdx extends Sounds {

  // where the sound files live
  private static final String kFileRoot = "data/";
  
  // the sound clips
  private Sound mSounds[];
  private Music mLongSounds[];

  // libGDX codes for the looping sounds (-1 if not looping/playing)
  private long mLoopId[];
  
  // constructor
  public SoundsGdx() {

    super();
    
    mSounds      = new Sound[kNumSounds];
    mLongSounds  = new Music[kNumSounds];
    mLoopId      = new long[kNumSounds];
    Arrays.fill(mLoopId, -1);

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

      if ( playAsMusic(id) ) {
        if ( mLongSounds[id] != null ) return; // already loaded
        mLongSounds[id] = Gdx.audio.newMusic(Gdx.files.internal(file));
        mLongSounds[id].setLooping(false);
      } else {
        if ( mSounds[id] != null ) return; // already loaded
        mSounds[id] = Gdx.audio.newSound(Gdx.files.internal(file));
      }
      
    } catch (Exception ex) {
      Env.debug(ex.getMessage());
      mSounds[id] = null;
      mLongSounds[id] = null;
    }

  } // Sounds.loadSound()

  // check that all sounds have loaded
  @Override
  protected void checkSounds() {

    mAvailable = true;
    
    for ( int id = 0 ; id < kNumSounds ; id++ ) {
      if ( mSounds[id] == null && mLongSounds[id] == null ) {
        mAvailable = false;
        return;
      }
    }
    
  } // Sounds.checkSound()

  // play a (non-looping) sound effect
  @Override
  public void play(int id) {
    
    if ( !mAvailable || mMuted ) return;
    
    assert( id >= 0 && id < kNumSounds );
    assert( !isLooped(id) );

    if      ( mSounds[id]     != null ) mSounds[id].play();
    else if ( mLongSounds[id] != null ) mLongSounds[id].play();
    
  } // Sounds.play()

  // start a sound looping (if it isn't already)
  @Override
  public void loop(int id) {
    
    if ( !mAvailable || mMuted ) return;
    
    assert( id >= 0 && id < kNumSounds );
    assert( isLooped(id) );
    assert( mSounds[id] != null );
    
    if ( mLoopId[id] == -1 ) mLoopId[id] = mSounds[id].loop();
    
  } // Sounds.loop()
  
  // stop a looping sound
  @Override
  public void stop(int id) {

    if ( !mAvailable || mMuted ) return;
    
    assert( id >= 0 && id < kNumSounds );
    assert( isLooped(id) );
    assert( mSounds[id] != null );

    mSounds[id].stop();
    mLoopId[id] = -1;
    
  } // Sounds.stop()

} // class SoundsGdx
