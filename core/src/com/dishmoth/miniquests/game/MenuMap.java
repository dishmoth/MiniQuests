/*
 *  MenuMap.java
 *  Copyright Simon Hern 2017
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// menu option for continuing a game from the map screen
public class MenuMap extends MenuPanel {

  // text images
  private static EgaImage kNewGameTextImage  = null,
                          kNewQuestTextImage  = null,
                          kContinueTextImage = null;

  // reference to the text sprite
  private EgaImage    mTextImage;
  private AnimPicture mText;

  // data to restart the map from (or null)
  private int mMapRestartData[];
  
  // check on the fire key
  private boolean mReady;
  
  // prepare resources
  static void initialize() {
    
    MenuPanel.initialize();

    if ( kNewGameTextImage == null ) {
      kNewGameTextImage = Env.resources().loadEgaImage("NewGameText.png");
      kNewQuestTextImage = Env.resources().loadEgaImage("NewQuestText.png");
      kContinueTextImage = Env.resources().loadEgaImage("ContGameText.png");
    }

  } // initialize()
  
  // constructor (new game)
  public MenuMap() {

    initialize();

    mBackground = makeBackgroundImage(null, null);
    mTextImage = kNewGameTextImage;
    mText = null;
    mMapRestartData = null;
    
  } // constructor

  // constructor (0 => new game, 1 => continue game, 2 => new quest)
  public MenuMap(int textType,
                 int mapRestartData[],
                 int requiredColours[]) {

    initialize();

    mBackground = makeBackgroundImage(mapRestartData, requiredColours);
    assert( textType >= 0 && textType <= 2 );
    mTextImage = ( textType == 0 ? kNewGameTextImage
                 : textType == 1 ? kContinueTextImage
                                 : kNewQuestTextImage );
    mText = null;
    mMapRestartData = mapRestartData;
    
  } // constructor

  // make a background picture based on map location (or null)
  private Picture makeBackgroundImage(int mapRestartData[],
                                      int requiredColours[]) {

    EgaImage mapImage = MapStory.getMapImage(mapRestartData);
    
    EgaImage image = new EgaImage(0, 0, Env.screenWidth(), Env.screenHeight());
    mapImage.draw(image, 0, 0);
    EgaTools.fadeImage(image);
    if ( requiredColours != null ) {
      EgaTools.limitColours(image, 16, requiredColours);
    }
    kFrameImage.draw(image, 0, 0);
    
    return new Picture(image);
    
  } // makeBackgroundImage()
  
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
    if ( mMapRestartData == null ) {
      return new MapStory(-1);
    } else {
      return new MapStory(mMapRestartData);
    }
    
  } // MenuPanel.exitMenu()
 
} // class MenuMap
