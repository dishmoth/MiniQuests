/*
 *  MenuControls.java
 *  Copyright Simon Hern 2017
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

// menu option for changing the control scheme
public class MenuControls extends MenuPanel {

  // text and background images
  private static EgaImage kBackgroundImage = null,
                          kTextImage       = null;

  // reference to the text sprite
  private Picture mText;
  
  // check on the fire key
  private boolean mReady;
  
  // prepare resources
  static void initialize() {
    
    MenuPanel.initialize();
    
    if ( kBackgroundImage == null ) {
      kBackgroundImage = Env.resources().loadEgaImage("ScreenSizePic.png");
      kFrameImage.draw(kBackgroundImage, 0, 0);
      kTextImage = Env.resources().loadEgaImage("ScreenSizeText.png");
    }

  } // initialize()
  
  // constructor
  public MenuControls() {

    initialize();

    mBackground = new Picture(kBackgroundImage, 0.0f);
    mText = null;
    
  } // constructor
  
  // called when the panel becomes active
  public void enable(SpriteManager spriteManager) {
    
    mText = new Picture(kTextImage);
    spriteManager.addSprite(mText);

    Env.keys().setButtonDisplay(true);
    
    mReady = false;
    
  } // MenuPanel.enable()
  
  // called every frame when the panel is active
  public boolean advance(SpriteManager spriteManager) {

    final boolean keyFire = Env.keys().fire();

    if ( keyFire && mReady ) {
      int scheme = Env.saveState().touchScreenControls();
      assert( scheme >= 0 );
      scheme = (scheme + 1) % 2;
      Env.saveState().setTouchScreenControls(scheme);
      Env.sounds().play(Sounds.MENU_2);
    }
    mReady = !keyFire;    
    
    return false;
    
  } // MenuPanel.advance()

  // called when the panel stops being active
  public void disable(SpriteManager spriteManager) {
    
    spriteManager.removeSprite(mText);

    Env.keys().setButtonDisplay(false);
    
  } // MenuPanel.disable()
  
} // class MenuControls
