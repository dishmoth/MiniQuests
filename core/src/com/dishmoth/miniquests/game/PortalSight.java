/*
 *  PortalSight.java
 *  Copyright (c) 2024 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// a visibility post-process effect for one of the portal stones
public class PortalSight extends PortalSightBase {

  // colour changes inside and outside the sight zone
  private static final char kRemapInside[][]  = { { '2', '0' },
                                                  { 'Y', '0' },
                                                  { 'G', '0' },
                                                  { 'm', 'u' },
                                                  { 'K', 'u' } };
  private static final char kRemapOutside[][] = { { 'h', 'Y' } };

  // lifetime of the effect
  private static final int   kDuration    = 500;
  private static final float kRadiusZero  = 2.5f,
                             kRadiusStart = 6.0f,
                             kRadiusEnd   = 2.5f;

  // different states
  private enum State { kStartUp, kActive, kShutdown };

  // current state
  private State mState;

  // countdown
  private int mTimer;

  // constructor
  public PortalSight() {

    recolour(mRecolourInside, kRemapInside);
    recolour(mRecolourOutside, kRemapOutside);

    mState = State.kStartUp;
    mRadius = kRadiusZero;
    mTimer = 0;

  } // constructor

  // end the effect
  public void shutdown() { mState = State.kShutdown; }

  // update the effect
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {

    super.advance(addTheseSprites, killTheseSprites, newStoryEvents);

    switch ( mState ) {
      case kStartUp: {
        mRadius += 0.5f;
        if ( mRadius >= kRadiusStart ) {
          mRadius = kRadiusStart;
          mState = State.kActive;
          mTimer = kDuration;
        }
      } break;

      case kActive: {
        final float t = 1.0f - (mTimer/(float)kDuration),
                    h = t*t;
        mRadius = kRadiusStart * (1-h) + kRadiusEnd * h;
        if ( --mTimer <= 0 ) mState = State.kShutdown;
      } break;

      case kShutdown: {
        mRadius -= 0.5f;
        if ( mRadius < 1.0f ) {
          newStoryEvents.add(new EventEnded());
        }
      } break;
    }

  } // Sprite.advance()

} // class PortalSight
