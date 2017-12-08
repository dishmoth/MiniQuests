/*
 *  TimingControl.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests;

import com.dishmoth.miniquests.game.Env;


// class for controlling frame rate
public class TimingControl {

  // desired frame rate
  static private final long kNanosPerTick = Math.round(1.0e9f
                                                       /Env.ticksPerSecond());
  
  // aim to even out the time-per-tick over this number of ticks
  static private final int kTickHistoryLength = 4;
  
  // game loop must yield to other threads eventually
  static private final int kMaxTicksWithoutYield = 10;
  
  // number of seconds between progress reports on the console (0 to disable)
  static private final long kReportProgressInterval = 10;
  
  // don't collect stats until performance has settled down
  static private final int kNoStatsForEarlyTicks = 1*30;
  
  // if the game runs slow then skip draw updates (in a random pattern)
  static private final int   kMinTicksWithDraw   = 1,
                             kMaxTicksWithDraw   = 3;
  static private final float kAcceptableSlowdown = 1.1f;

  // record the times (nanos) of previous ticks
  private long[] mTickHistory      = null;
  private int    mTickHistoryIndex = 0;
  
  // timing statistics
  private TimingStats mTotalStats = null,
                      mLocalStats = null;
  
  // number of times we have skimped on yield
  private int mNumTicksWithoutYield;
  
  // number of frames before considering skipping a redraw
  private int mNumDrawsBeforeSkip;

  // number of frames before stats start
  private int mSkipEarlyStats;

  // time to display next report
  private long mNextUpdateTime;  
  
  // time of previous tick
  private long mPrevTickNanos;
  
  // constructor
  public TimingControl() {
    
    mTotalStats = new TimingStats(kNanosPerTick);
    mLocalStats = new TimingStats(kNanosPerTick);

    mTickHistory      = new long[kTickHistoryLength];
    mTickHistoryIndex = 0;

  } // constructor

  // display final progress report
  public void report() {
    
    Env.debug("Overall: " + mTotalStats.toString());
    
  } // report()
  
  // reset the timers
  public void reset() {
    
    mNumTicksWithoutYield  = 0;
    mNumDrawsBeforeSkip = 0;
    
    mPrevTickNanos = System.nanoTime();
    prepareTickHistory(mPrevTickNanos);

    mNextUpdateTime = mPrevTickNanos + kReportProgressInterval*1000000000;
    mLocalStats.clear();

    mSkipEarlyStats = kNoStatsForEarlyTicks;
    
  } // reset()
  
  // update the timers and delay until it's time for the next frame
  public void tick(long    nanosAfterAdvance, 
                   long    nanosAfterDraw, 
                   boolean tickSkipsDraw) {
    
    // wait
    boolean tickHasOverrun = delayUntil(nextTickFromHistory());
    long newTickNanos = System.nanoTime();

    // collect statistics
    updateTickHistory(newTickNanos);
    if ( mSkipEarlyStats > 0 ) {
      mSkipEarlyStats--;
    } else {
      updateProgress(newTickNanos - mPrevTickNanos,
                     nanosAfterAdvance - mPrevTickNanos,
                     nanosAfterDraw - nanosAfterAdvance,
                     tickHasOverrun, tickSkipsDraw);
      if ( kReportProgressInterval >= 0 && newTickNanos > mNextUpdateTime ) {
        Env.debug(mLocalStats.toString());
        mLocalStats.clear();
        mNextUpdateTime = newTickNanos + kReportProgressInterval*1000000000;
      }
    }
    mPrevTickNanos = newTickNanos;

  } // tick()
  
  // wait until the system time has passed the target time
  // and yield to other threads when an opportunity arises
  // (returns true if the target time has passed already)
  // (note: actually sleep(1) instead of yield(), said to be more reliable)
  private boolean delayUntil(long targetNanos) {
    
    if ( targetNanos - System.nanoTime() < 1000 ) {

      if ( mNumTicksWithoutYield < kMaxTicksWithoutYield ) {
        mNumTicksWithoutYield++;
      } else {
        mNumTicksWithoutYield = 0;
        try { Thread.sleep(1); } catch (InterruptedException ex) {}
        //Thread.yield();
      }
      return true;
      
    } else {
    
      mNumTicksWithoutYield = 0;
      try { Thread.sleep(1); } catch (InterruptedException ex) {}
      //Thread.yield();
      while ( targetNanos - System.nanoTime() > 0 ) {}
      return false;
      
    }
    
  } // delayUntil()

  // define an ideal history of tick times based on the current time
  private void prepareTickHistory(long currentNanos) {
    
    for ( int k = 0 ; k < kTickHistoryLength ; k++ ) {
      mTickHistory[k] = currentNanos - (k+1-kTickHistoryLength)*kNanosPerTick;
    }
    mTickHistoryIndex = kTickHistoryLength - 1;
    
  } // prepareTickHistory()
  
  // nominate a time that this tick should end at
  // (try to balance out variations over the history of recorded ticks)
  private long nextTickFromHistory() {
    
    long soonestNanos = mTickHistory[mTickHistoryIndex] + kNanosPerTick;
    for ( int k = 1 ; k < kTickHistoryLength ; k++ ) {
      long t =  mTickHistory[(mTickHistoryIndex+k)%kTickHistoryLength] 
             + (kTickHistoryLength+1-k)*kNanosPerTick;
      if ( t < soonestNanos ) soonestNanos = t;
    }
    return soonestNanos;
    
  } // nextTickFromHistory()
  
  // append the current time to the tick history (a circular array)
  private void updateTickHistory(long currentNanos) {
    
    if ( ++mTickHistoryIndex == kTickHistoryLength ) mTickHistoryIndex = 0;
    mTickHistory[mTickHistoryIndex] = currentNanos;
    
  } // updateTickHistory()
  
  // check whether frame rate is too low
  public boolean gameRunningSlow() {
    
    if ( mNumDrawsBeforeSkip > 0 ) {
      mNumDrawsBeforeSkip--;
      return false;
    }
    
    long meanTick = ( mTickHistory[mTickHistoryIndex]
                    - mTickHistory[(mTickHistoryIndex+1)%kTickHistoryLength] )
                  / (kTickHistoryLength-1);
    if ( meanTick <= kNanosPerTick*kAcceptableSlowdown ) return false;
    
    mNumDrawsBeforeSkip = Env.randomInt(kMinTicksWithDraw, kMaxTicksWithDraw);
    return true;
    
  } // gameRunningSlow()
  
  // update the running statistics and the overall statistics
  private void updateProgress(long    nanosInTick,
                              long    nanosInAdvance,
                              long    nanosInDraw,
                              boolean tickHasOverrun,
                              boolean tickSkipsDraw) {
    
    mTotalStats.update(nanosInTick, nanosInAdvance, 
                       nanosInDraw, tickHasOverrun, tickSkipsDraw);
    mLocalStats.update(nanosInTick, nanosInAdvance, 
                       nanosInDraw, tickHasOverrun, tickSkipsDraw);
    
  } // updateProgress()
  
} // class TimingControl
