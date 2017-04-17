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
                          kTextImage       = null;

  // reference to the text sprite
  private AnimPicture mText;
  
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
    }

  } // initialize()
  
  // constructor
  public MenuTraining() {

    initialize();

    mBackground = new Picture(kBackgroundImage, 0.0f);
    mText = null;
    
  } // constructor
  
  // called when the panel becomes active
  public void enable(SpriteManager spriteManager) {
    
    mText = new AnimPicture(0, kTextImage, 
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
    
    storyEvents.add(new Story.EventGameBegins());
    spriteManager.removeAllSprites();
    return new TrainingStory();
    
  } // MenuPanel.exitMenu()
 
} // class MenuTraining
