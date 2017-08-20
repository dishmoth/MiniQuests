/*
 *  EndStory.java
 *  Copyright Simon Hern 2010
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.Iterator;
import java.util.LinkedList;

// what happens at the end of a quest
public class EndStory extends Story {

  // times until things happen
  private static final int kKeyDelayLong = 60,
                           kKeyDelay     = 15,
                           kChangeDelay  = 10,
                           kScoreDelay   = 120;
  
  // assorted images
  private static EgaImage kEndText   = null,
                          kScoreText = null;
  private static EgaImage kStarImage = new EgaImage(0, 0, 5, 5,
                                                      "  c  "
                                                    + "  s  "
                                                    + "csssc"
                                                    + " csc "
                                                    + " s s ", 0.0f);
  
  // position details for gold stars
  private static final int kStarXPos = 5,
                           kStarYPos = 22,
                           kStarXGap = 6;
  
  // the player's rating (1 to 5)
  private int mScore;
  
  // which quest the player was doing
  private int mQuest;
  
  // which text is currently displaying (0 => complete, 1 => score, 2 => done)
  private int mStage;
  
  // count down until key presses are recognized
  private int mKeyTimer;
  
  // a key press will start the game
  private boolean mKeyReady;

  // keep track of whether the escape key is held down
  private boolean mEscPressed;
  
  // count down until the next stage
  private int mChangeTimer;
  
  // automatically change to the score screen after a delay
  private int mScoreTimer;
  
  // current image objects
  private Picture mText;
  private LinkedList<Sprite> mStars;

  // prepare resources
  static public void initialize() {
  
    if ( kEndText == null ) {
      kEndText = Env.resources().loadEgaImage("EndText.png");
      kScoreText = Env.resources().loadEgaImage("ScoreText.png");
    }
  
  } // initialize()

  // a gold star image
  static public EgaImage starImage() { return kStarImage; }
  
  // constructor
  public EndStory(int score, int quest) {

    initialize();
    
    assert( score >= 1 && score <= 5 );
    mScore = score;
  
    mQuest = quest;
    
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
        spriteManager.disableAdvanceForAll();
        mText = new Picture(kEndText, -1);
        spriteManager.addSprite(mText);
        mStage = 0;
        mKeyTimer = kKeyDelayLong;
        mKeyReady = false;
        mChangeTimer = 0;
        mScoreTimer = kScoreDelay;
        mEscPressed = true;
        Env.sounds().play(Sounds.QUEST_DONE);
        it.remove();
      } // Story.EventGameBegins

      else {
        Env.debug("event ignored: " + event.getClass());
        it.remove();
      }
      
    } // for each story event

    // check key presses
    if ( mKeyTimer > 0 ) mKeyTimer--;
    if ( mKeyTimer == 0 && mChangeTimer == 0 ) {
      if ( Env.keys().any() ) {
        if ( mKeyReady ) {
          mChangeTimer = kChangeDelay;
          cleanUpSprites(spriteManager);
          Env.sounds().play(Sounds.MENU_1);
        }
        mKeyReady = false;
      } else {
        mKeyReady = true;
      }
    }
    
    // automatically change to the score screen after a delay
    if ( mScoreTimer > 0 ) {
      if ( --mScoreTimer == 0 ) {
        if ( mStage == 0 && mChangeTimer == 0 ) {
          mChangeTimer = 1;
          cleanUpSprites(spriteManager);
          mKeyReady = false;
        }
      }
    }
    
    // stage advance or story handover
    if ( mChangeTimer > 0  ) {
      if ( --mChangeTimer == 0 ) {
        mStage++;
        assert( mStage == 1 || mStage == 2 );
        if ( mStage == 2 ) {
          newStory = new MapStory(mQuest);
          storyEvents.add(new Story.EventGameBegins());
          spriteManager.removeAllSprites();
        } else {
          mText = new Picture(kScoreText, -1);
          spriteManager.addSprite(mText);
          makeStars(mScore);
          for ( Sprite p : mStars ) spriteManager.addSprite(p);
          mKeyTimer = kKeyDelay;
        }
      }
    }
    
    // quest aborted (allow a quick exit on mobile devices)
    if ( Env.keys().escape() ) {
      if ( !mEscPressed && newStory == null &&
           (Env.platform() == Env.Platform.ANDROID || 
            Env.platform() == Env.Platform.IOS) ) {
        newStory = new MapStory(mQuest);
        storyEvents.add(new Story.EventGameBegins());
        spriteManager.removeAllSprites();
      }
      mEscPressed = true;
    } else {
      mEscPressed = false;
    }    
    
    return newStory;
    
  } // Story.advance()

  // create golden star objects
  private void makeStars(int num) {
    
    if ( mStars == null ) mStars = new LinkedList<Sprite>();
    else                  mStars.clear();
    
    assert( num >= 1 && num <= 5 );
    for ( int k = 0 ; k < num ; k++ ) {
      final int xPos = kStarXPos + k*kStarXGap;
      Picture star = new Picture(kStarImage, xPos, kStarYPos, -2);
      mStars.add(star);
    }
    
  } // makeStars()

  // remove any sprites we created
  private void cleanUpSprites(SpriteManager spriteManager) {
  
    if ( mText != null ) {
      spriteManager.removeSprite(mText);
      mText = null;
    }
    
    if ( mStars != null ) {
      spriteManager.removeSprites(mStars);
      mStars.clear();
    }
    
  } // cleanUpSprites()
  
} // class EndStory
