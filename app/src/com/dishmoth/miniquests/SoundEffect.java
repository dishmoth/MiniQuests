/*
 *  SoundEffect.java
 *  Copyright Simon Hern 2008
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */
 
package com.dishmoth.miniquests;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import javax.sound.sampled.*;

import com.dishmoth.miniquests.game.Env;

// a single sound effect that tries to limit the number of open Clips
public class SoundEffect implements LineListener {

  // the source data for a Clip (if both are null then mClip must be defined)
  private AudioFormat mFormat;
  private byte[]      mData;

  // whether the effect should be played in a loop (false by default)
  private boolean mLooped;
  
  // the current clip (null if one needs to be generated)
  private Clip mClip;

  // details of the sound mixer being used
  static private Mixer.Info kMixerInfo = null;

  // old clips that are finished with but still need to be closed
  static private Queue<Clip> kDeadClips = new LinkedBlockingQueue<Clip>();
  
  // maximum number of dead clips that we will allow to build up
  static private int     kMaxDeadClips         = 16;
  static private boolean kMaxClipsUnrestricted = false;
  
  // check which mixer we are using and how many Clips it supports
  static private void initialize(AudioFormat format) throws IOException {

    if ( kMixerInfo != null ) return;

    Mixer.Info mixerInfo[] = AudioSystem.getMixerInfo();
    if ( mixerInfo.length == 0 ) {
      throw new IOException("Sound Mixer not available");
    }

    DataLine.Info clipInfo = new DataLine.Info(Clip.class, format);
    
    kMixerInfo = AudioSystem.getMixer(null).getMixerInfo();
    int maxLines = AudioSystem.getMixer(kMixerInfo).getMaxLines(clipInfo);
    
    // search for a better mixer
    if ( maxLines != AudioSystem.NOT_SPECIFIED ) {
      Env.debug("Default Sound Mixer: " + kMixerInfo + ", max lines "
                + ((maxLines==AudioSystem.NOT_SPECIFIED)
                   ? "unrestricted" : maxLines));

      for ( int k = 0 ; k < mixerInfo.length ; k++ ) {
        int n = AudioSystem.getMixer(mixerInfo[k]).getMaxLines(clipInfo);
        if ( n != 0 ) {
          Env.debug("Sound Mixer " + k + ": " + mixerInfo[k] 
                    + ", max lines " + ((n==AudioSystem.NOT_SPECIFIED)
                                        ? "unrestricted" : n));
        }
        if ( maxLines != AudioSystem.NOT_SPECIFIED &&
             ( n == AudioSystem.NOT_SPECIFIED || n > maxLines ) ) {
          kMixerInfo = mixerInfo[k];
          maxLines = n;
        }
      }
    }
    if ( maxLines == 0 ) {
      throw new IOException("Sound Mixer does not support Sound Clips");
    }
    
    if ( maxLines == AudioSystem.NOT_SPECIFIED ) {
      kMaxClipsUnrestricted = true;
    } else {
      kMaxClipsUnrestricted = false;
      kMaxDeadClips = Math.min(kMaxDeadClips, Math.max(maxLines-6,6));
    }
    Env.debug("Using Sound Mixer: " + kMixerInfo + ", max lines " 
              + (kMaxClipsUnrestricted?"unrestricted":maxLines));
    
  } // initialize()
  
  // constructor using a pre-existing Clip
  // (in this case the source Clip is never destroyed)
  public SoundEffect(Clip clip) {

    mFormat = null;
    mData = null;
    mClip = clip;
    mLooped = false;
    
  } // constructor (Clip)
  
  // constructor using a buffer of PCM data
  // (in this case a Clip is only created when needed)
  public SoundEffect(AudioFormat format, byte pcmData[]) throws IOException {

    assert( pcmData != null && format != null );
    
    initialize(format);
  
    mFormat = format;
    mData = pcmData;
    mClip = null;
    mLooped = false;

  } // constructor (bytes)

  // set the effect to loop forever when played
  public void setLooped(boolean val) { mLooped = val; }
  
  // start (or restart) the sound effect playing
  public void play() {

    if ( mClip == null ) {
    
      // create a new Clip from source data
      if ( mData == null || mFormat == null ) return;
      try {
        Clip clip = AudioSystem.getClip(kMixerInfo);
        clip.open(mFormat, mData, 0, mData.length);
        if ( !kMaxClipsUnrestricted ) clip.addLineListener(this);

        if ( mLooped ) clip.loop(Clip.LOOP_CONTINUOUSLY);
        else           clip.start();
        
        // if there is no restriction on clips, then make this one permanent
        // alternatively, keep a reference to the clip if it is looping
        if ( kMaxClipsUnrestricted || mLooped ) mClip = clip;
    
      } catch ( Exception ex ) {
        Env.debug("SoundEffect: could not play: " + ex);
        mData = null;
        mFormat = null;
      }
      
    } else {

      // replay (or restart) an existing Clip
      if ( mLooped ) {
        if ( !mClip.isActive() ) {
          mClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
      } else {
        mClip.stop();
        mClip.setFramePosition(0);
        mClip.start();
      }

    }
    
    // tidy up old Clips so that we never run out
    while ( kDeadClips.size() > kMaxDeadClips ) {
      Clip oldestDeadClip = kDeadClips.poll();
      if ( oldestDeadClip != null ) oldestDeadClip.close();
    }
  
  } // play()
  
  // stop the sound playing (in particular, looping)
  public void stop() {
    
    if ( mClip == null ) return;
    if ( mLooped && mClip.isRunning() ) {
      mClip.loop(0);
      mClip.stop();
    } else {
      mClip.stop();
    }
    
  } // stop()
  
  // receive information about the state of the clip
  // (we want to close clips when they stop playing, but we cannot do this
  // immediately, so we add them to a list to be dealt with later)
  public void update(LineEvent event) {
  
    if ( kMaxClipsUnrestricted ) return;

    if ( event.getType() == LineEvent.Type.STOP ) {
      Clip clip = (Clip)event.getLine();
      clip.removeLineListener(this);
      kDeadClips.add(clip);
    }
  
  } // LineListener.update()
  
} // class SoundEffect
