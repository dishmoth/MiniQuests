/*
 *  IntroStory.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.Iterator;
import java.util.LinkedList;

// instruction screens
public class IntroStory extends Story {

  // times until things happen
  private static final int kInitialDelay = 30,
                           kKeyDelay     = 7, //15,
                           kFadeOutDelay = 12,
                           kChangeDelay  = 10,
                           kFinalDelay   = 35;
  
  // text images
  private static EgaImage kMissionText = null,
                          kMoveText    = null,
                          kFireText    = null;

  // which quest we're doing
  private int mQuestNum;
  
  // which text is being displayed (0=start, 1=mission, 2=move, 3=fire, 4=done)
  private int mStage;
  
  // count down until key presses are recognized
  private int mKeyTimer;

  // count down until text changes
  private int mChangeTimer;
  
  // a key press will start the game
  private boolean mKeyReady;

  // references to current text object
  private Picture mTextPic;

  // reference to touch screen hints (Android only)
  private TouchArrows mArrows;
  
  // reference to the fade-out object
  private FadeOut mFadeOut;
  
  // keep track of whether the escape key is held down
  private boolean mEscPressed;
  
  // constructor
  public IntroStory(int questNum) {

    if ( kMissionText == null ) {
      kMissionText = Env.resources().loadEgaImage("MissionText.png");
    }

    if ( kMoveText == null && Env.saveState().quickTrainingNeeded() ) {
      String gdxText = ( Env.platform()==Env.Platform.ANDROID ? "Android" 
                                                              : "" );
      kMoveText = Env.resources().loadEgaImage("MoveText"+gdxText+".png");
      kFireText = Env.resources().loadEgaImage("FireText"+gdxText+".png");
    }
    
    mQuestNum = questNum;
    
    mTextPic = null;
    mArrows  = null;
    mFadeOut = null;
    
  } // constructor
  
  // process events and advance 
  @Override
  public Story advance(LinkedList<StoryEvent> storyEvents,
                       SpriteManager          spriteManager) {

    Story newStory = null;
    
    // process the story event list
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();

      if ( event instanceof Story.EventGameBegins ) {
        // first frame of the story, so set everything up
        mFadeOut = new FadeOut();
        spriteManager.addSprite(mFadeOut);
        mStage = 0;
        mKeyTimer = 0;
        mChangeTimer = kInitialDelay;
        mEscPressed = true;
        mKeyReady = false;
        it.remove();
      } // Story.EventGameBegins

      else {
        Env.debug("event ignored: " + event.getClass());
        it.remove();
      }
      
    } // for each story event

    // pause the fade-out part-way
    if ( mFadeOut.time() == kFadeOutDelay ) mFadeOut.pause(true);
    
    // check key presses
    if ( mKeyTimer > 0 ) mKeyTimer--;
    if ( mKeyTimer == 0 && mChangeTimer == 0 ) {
      if ( Env.keys().any() ) {
        if ( mKeyReady ) {
          mChangeTimer = kChangeDelay;
          spriteManager.removeSprite(mTextPic);
          mTextPic = null;
          if ( mArrows != null ) spriteManager.removeSprite(mArrows);
          mArrows = null;
          Env.sounds().play(Sounds.MENU_1);
        }
        mKeyReady = false;
      } else {
        mKeyReady = true;
      }
    }
    
    // stage advance or story handover
    if ( mChangeTimer > 0  ) {
      if ( --mChangeTimer == 0 ) {
        mStage++;
        if ( mStage == 5 ) {
          newStory = new TinyStory(mQuestNum);
          storyEvents.add(new Story.EventGameBegins());
          spriteManager.removeAllSprites();
          Env.saveState().setTrainingDone();
        } else if ( mStage == 4 ) {
          mFadeOut.pause(false);
          mChangeTimer = kFinalDelay;
        } else {
          mTextPic = (mStage == 1) ? new Picture(kMissionText, -2)
                   : (mStage == 2) ? new Picture(kMoveText, -2)
                                   : new Picture(kFireText, -2);
          spriteManager.addSprite(mTextPic);
          if ( Env.platform() == Env.Platform.ANDROID ) {
            if      ( mStage == 2 ) mArrows = new TouchArrows(1);
            else if ( mStage == 3 ) mArrows = new TouchArrows(2);
            else                    mArrows = null;
            if ( mArrows != null ) spriteManager.addSprite(mArrows);
          }
          mKeyTimer = kKeyDelay;
          if ( !Env.saveState().quickTrainingNeeded() ) {
            if ( mStage == 1 ) mStage = 3;
          }
        }
      }
    }

    // quest aborted
    if ( Env.keys().escape() ) {
      if ( !mEscPressed && newStory == null ) {
        newStory = new MapStory(mQuestNum);
        storyEvents.add(new Story.EventGameBegins());
        spriteManager.removeAllSprites();
      }
      mEscPressed = true;
    } else {
      mEscPressed = false;
    }    
    
    return newStory;
    
  } // Story.advance()

} // class IntroStory
