/*
 *  MenuStart.java
 *  Copyright Simon Hern 2017
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// menu option for continuing a game from the map screen
public class MenuStart extends MenuPanel {

  // text and background images
  private static EgaImage kBackgroundImage   = null,
                          kNewGameTextImage  = null,
                          kContinueTextImage = null;

  // reference to the text sprite
  private EgaImage    mTextImage;
  private AnimPicture mText;

  // check on the fire key
  private boolean mReady;
  
  // prepare resources
  static void initialize() {
    
    MenuPanel.initialize();

    if ( kBackgroundImage == null ) {
      final int width  = Env.screenWidth(),
                height = Env.screenHeight();
      EgaImage image = Env.resources().loadEgaImage("Map.png");
      kBackgroundImage = new EgaImage(0, 0, width, height);
      image.draw(kBackgroundImage, -2*(width+1), -2*(height+1));
      EgaTools.fadeImage(kBackgroundImage);
      kFrameImage.draw(kBackgroundImage, 0, 0);

      kNewGameTextImage = Env.resources().loadEgaImage("NewGameText.png");
      kContinueTextImage = Env.resources().loadEgaImage("ContGameText.png");
    }

  } // initialize()
  
  // constructor
  public MenuStart(boolean newGame) {

    initialize();

    mBackground = new Picture(kBackgroundImage, 0.0f);
    mTextImage = ( newGame ? kNewGameTextImage : kContinueTextImage );
    mText = null;
    
  } // constructor

  // called when the panel becomes active
  public void enable(SpriteManager spriteManager) {

    mText = new AnimPicture(0, mTextImage, 
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
    return new MapStory(-1);
    
  } // MenuPanel.exitMenu()
 
} // class MenuStart
