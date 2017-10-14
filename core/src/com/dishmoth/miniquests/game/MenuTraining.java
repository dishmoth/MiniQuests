/*
 *  MenuTraining.java
 *  Copyright Simon Hern 2017
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// menu option for hero training
public class MenuTraining extends MenuPanel {

  // text and background images
  private static EgaImage kBackgroundImage = null,
                          kTextImage       = null,
                          kContinueImage   = null;

  // reference to the text sprite
  private AnimPicture mText;
  
  // data to continue a paused story
  private Story         mRestartStory   = null;
  private SpriteManager mRestartSprites = null;

  // check on the fire key
  private boolean mReady;
  
  // prepare resources
  static void initialize() {
    
    MenuPanel.initialize();
    
    if ( kBackgroundImage == null ) {
      kBackgroundImage = Env.resources().loadEgaImage("TrainingPic.png");
      EgaTools.fadeImage(kBackgroundImage);
      kFrameImage.draw(kBackgroundImage, 0, 0);
      kTextImage = Env.resources().loadEgaImage("TrainingText.png");
      kContinueImage = Env.resources().loadEgaImage("TrainingContinueText.png");
    }

  } // initialize()
  
  // constructor
  public MenuTraining() {

    initialize();

    mBackground = new Picture(kBackgroundImage, 0.0f);
    mText = null;
    
  } // constructor
  
  // constructor (from paused story)
  public MenuTraining(Story restartStory,
                      SpriteManager restartSprites,
                      int requiredColours[]) {

    initialize();

    mRestartStory = restartStory;
    mRestartSprites = restartSprites;
    
    makeBackground(requiredColours);
    mText = null;
    
  } // constructor
  
  // construct a copy of the paused game screen for a background
  private void makeBackground(int requiredColours[]) {
    
    EgaCanvas oldScreen = new EgaCanvas(Env.screenWidth(), Env.screenHeight());
    mRestartSprites.draw(oldScreen);

    EgaImage image = new EgaImage(0, 0, 
                                  Env.screenWidth(), Env.screenHeight(),
                                  oldScreen.pixels(), 0.0f);
    EgaTools.fadeImage(image);
    EgaTools.limitColours(image, 16, requiredColours);
    kFrameImage.draw(image, 0, 0);
    mBackground = new Picture(image, 0.0f);
    
  } // makeBackground()
  
  // called when the panel becomes active
  public void enable(SpriteManager spriteManager) {
    
    EgaImage image = ( mRestartStory == null ? kTextImage : kContinueImage );
    mText = new AnimPicture(0, image, 
                            kAnimTitleDelay, kAnimBlankDelay,
                            kBeginText, 
                            kAnimBeginDelay, kAnimBlankDelay);
    spriteManager.addSprite(mText);
    mReady = false;
    
  } // MenuPanel.enable()
  
  // called every frame when the panel is active
  public boolean advance(SpriteManager spriteManager) {
    
    final boolean keyFire  = Env.keys().fire();
    if ( keyFire ) {
      if ( mReady ) return true;
    } else {
      mReady = true;
    }
    return false;
    
  } // MenuPanel.advance()

  // called when the panel stops being active
  public void disable(SpriteManager spriteManager) {
    
    spriteManager.removeSprite(mText);
    
  } // MenuPanel.disable()
  
  // set up the next story for when the menu closes (some panels only)
  public Story exitMenu(LinkedList<StoryEvent> storyEvents,
                        SpriteManager          spriteManager) {

    if ( mRestartStory == null ) {
      storyEvents.add(new Story.EventGameBegins());
      spriteManager.removeAllSprites();
      return new TrainingStory();
    } else {
      storyEvents.add(new Story.EventStoryContinue());
      spriteManager.removeAllSprites();
      spriteManager.copySprites(mRestartSprites);
      return mRestartStory;
    }
    
  } // MenuPanel.exitMenu()
 
} // class MenuTraining
