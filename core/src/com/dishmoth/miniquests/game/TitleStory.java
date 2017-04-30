/*
 *  TitleStory.java
 *  Copyright Simon Hern 2010
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.Iterator;
import java.util.LinkedList;

// the game's title screen
public class TitleStory extends Story {

  // times until things happen
  private static final int kTextDelay  = 20,
                           kKeyDelay   = 40,
                           kBeginDelay = 150,
                           kStartDelay = 10;
  
  // how fast the text animates
  private static final int kAnimTitleDelay = 60,
                           kAnimBeginDelay = 60,
                           kAnimBlankDelay = 20;
  
  // title screen images
  private static EgaImage kTitleImage = null,
                          kTitleText  = null,
                          kBeginText  = null;

  // count down until key presses are recognized
  private int mKeyTimer;

  // count down until the text starts animating
  private int mBeginTimer;

  // count down until game starts
  private int mStartTimer;
  
  // a key press will start the game
  private boolean mKeyReady;

  // keep track of whether the escape key is held down
  private boolean mEscPressed;
  
  // reference to text object
  private AnimPicture mTextPic;
  
  // constructor
  public TitleStory() {

    if ( kTitleImage == null ) {
      kTitleImage = Env.resources().loadEgaImage("TitleScreen.png");
      EgaTools.limitColours(kTitleImage, 14);

      kTitleText = Env.resources().loadEgaImage("TitleText.png");
      
      String gdxText = ( Env.platform()==Env.Platform.OUYA    ? "Controller" 
                       : Env.platform()==Env.Platform.ANDROID ? "Android" 
                                                              : "" );      
      kBeginText = Env.resources().loadEgaImage("BeginText"+gdxText+".png");
    }
    
    mTextPic = null;
    
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
        spriteManager.addSprite(new Picture(kTitleImage, -1));
        mTextPic = new AnimPicture(kTextDelay, kTitleText, -1, 0);
        spriteManager.addSprite(mTextPic);
        Env.sounds().play(Sounds.TITLE, kTextDelay);
        mKeyTimer = kKeyDelay;
        mBeginTimer = kBeginDelay;
        mStartTimer = 0;
        mEscPressed = true;
        mKeyReady = false;
        Env.keys().setMode(KeyMonitor.MODE_MAP);
        it.remove();
      } // Story.EventGameBegins

      else {
        Env.debug("event ignored: " + event.getClass());
        it.remove();
      }
      
    } // for each story event

    // switch to animating text
    if ( mBeginTimer > 0 && mStartTimer == 0 ) {
      if ( --mBeginTimer == 0 ) {
        spriteManager.removeSprite(mTextPic);
        mTextPic = new AnimPicture(0, kTitleText, 
                                      kAnimTitleDelay, kAnimBlankDelay,
                                      kBeginText, 
                                      kAnimBeginDelay, kAnimBlankDelay);
        spriteManager.addSprite(mTextPic);
      }
    }

    // check key presses
    if ( mKeyTimer > 0 ) mKeyTimer--;
    if ( mKeyTimer == 0 && mStartTimer == 0 ) {
      if ( Env.keys().any() && !Env.keys().escape() ) {
        if ( mKeyReady ) {
          mStartTimer = kStartDelay;
          spriteManager.removeSprite(mTextPic);
          mTextPic = null;
          Env.sounds().play(Sounds.MENU_1);
        }
      } else {
        mKeyReady = true;
      }
    }
    
    // story handover
    if ( mStartTimer > 0  ) {
      if ( --mStartTimer == 0 ) {
        newStory = new MenuStory();
        storyEvents.add(new Story.EventGameBegins());
        spriteManager.removeAllSprites();
      }
    }
    
    // quest aborted
    if ( Env.keys().escape() ) {
      if ( !mEscPressed && newStory == null ) {
        if ( Env.platform() == Env.Platform.ANDROID ) {
          Env.exit();
          mKeyTimer = 300; // delay while the 'exit' takes hold
        }
      }
      mEscPressed = true;
    } else {
      mEscPressed = false;
    }    
    
    return newStory;
    
  } // Story.advance()

} // class TitleStory
