/*
 *  TimingStats.java
 *  Copyright Simon Hern 2010
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests;

// collect timing statistics as the game is running
class TimingStats {

  // expected frame rate
  private long mNanosPerTick;
  
  // assorted counters
  private int  mNumTicks, mNumOverruns, mNumDrawSkips;
  private long mNumNanos, mNumNanosInAdvance, mNumNanosInDraw;
  private long mPeakNanos, mPeakNanosInAdvance, mPeakNanosInDraw;
  
  // constructor
  public TimingStats(long nanosPerTick) { 

    mNanosPerTick = nanosPerTick;
    clear(); 
  
  } // constructor
  
  // reset counters
  public void clear() {
  
    mNumTicks = mNumOverruns = mNumDrawSkips = 0;
    mNumNanos = mNumNanosInAdvance = mNumNanosInDraw = 0;
    mPeakNanos = mPeakNanosInAdvance = mPeakNanosInDraw = 0;
    
  } // clear()
  
  // update timing statistics after each tick 
  public void update(long    nanosInTick,
                     long    nanosInAdvance,
                     long    nanosInDraw,
                     boolean tickHasOverrun,
                     boolean tickSkippedDraw) {
    
    mNumTicks++;
    
    if ( tickHasOverrun ) mNumOverruns++;
    if ( tickSkippedDraw ) mNumDrawSkips++;
    
    mNumNanos += nanosInTick;
    mPeakNanos = Math.max(nanosInTick, mPeakNanos);
    mNumNanosInAdvance += nanosInAdvance;
    mPeakNanosInAdvance = Math.max(nanosInAdvance, mPeakNanosInAdvance);
    mNumNanosInDraw += nanosInDraw;
    mPeakNanosInDraw = Math.max(nanosInDraw, mPeakNanosInDraw);
  
  } // update()
  
  // text output
  public String toString() {

    int  numTicks = Math.max(1, mNumTicks);
    long numNanos = Math.max(1, mNumNanos);

    return new String(mNumTicks
                      + " frames, "
                      + String.format("%.1f", mNumTicks/(numNanos*1.0e-9f))
                      + " per sec (max "
                      + String.format("%.1f", mPeakNanos*1.0e-6f)
                      + "ms), "
                      + (100*mNumNanosInAdvance)/(numTicks*mNanosPerTick)
                      + "% in advance (peak "
                      + (100*mPeakNanosInAdvance)/mNanosPerTick
                      + "%), "
                      + (100*mNumNanosInDraw)/(numTicks*mNanosPerTick)
                      + "% in draw (peak "
                      + (100*mPeakNanosInDraw)/mNanosPerTick
                      + "%), "
                      + mNumOverruns 
                      + " overrun (" 
                      + (100*mNumOverruns)/numTicks
                      + "%), "
                      + mNumDrawSkips
                      + " no draw ("
                      + (100*mNumDrawSkips)/numTicks
                      + "%)");
    
  } // toString()
  
} // class TimingStats
