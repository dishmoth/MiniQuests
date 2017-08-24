/*
 *  MenuRestart.java
 *  Copyright Simon Hern 2017
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// menu option to continue from a save point
public class MenuRestart extends MenuPanel {

  // text and background images
  private static EgaImage kTextImage = null;
  
  // reference to the text sprite
  private AnimPicture mText;
  
  // data to continue the quest
  private QuestStory             mRestartStory   = null;
  private SpriteManager          mRestartSprites = new SpriteManager();
  private LinkedList<StoryEvent> mRestartEvents  = new LinkedList<StoryEvent>();
  
  // check on the fire key
  private boolean mReady;
  
  // prepare resources
  static void initialize() {
    
    MenuPanel.initialize();

    if ( kTextImage == null ) {
      kTextImage = Env.resources().loadEgaImage("ContQuestText.png");
    }

  } // initialize()
  
  // constructor
  public MenuRestart(QuestStory restartStory, int requiredColours[]) {

    initialize();

    mRestartStory = restartStory;
    makeBackground(requiredColours);
    
    mText = null;
    
  } // constructor
  
  // construct a copy of the saved game screen for a background
  private void makeBackground(int requiredColours[]) {
    
    mRestartEvents.add(new Story.EventGameBegins());
    mRestartStory.advance(mRestartEvents, mRestartSprites);

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
    
    mText = new AnimPicture(0, kTextImage, 
                            kAnimTitleDelay, kAnimBlankDelay,
                            kBeginText, 
                            kAnimBeginDelay, kAnimBlankDelay);
    spriteManager.addSprite(mText);
    mReady = false;
    
  } // MenuPanel.enable()
  
  // called every frame when the panel is active
  public boolean advance(SpriteManager spriteManager) {
    
    final boolean keyFire = Env.keys().fire();
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
    
    storyEvents.add(new Story.EventStoryContinue());
    for ( StoryEvent e : mRestartEvents ) storyEvents.add(e);
    
    spriteManager.removeAllSprites();
    spriteManager.list().addAll(mRestartSprites.list());
    
    return mRestartStory;
    
  } // MenuPanel.exitMenu()
 
} // class MenuRestart
