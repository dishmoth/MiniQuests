/*
 *  MenuCredits.java
 *  Copyright Simon Hern 2017
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

// menu option for the credits scroll
public class MenuCredits extends MenuPanel {

  // delay until the next y-pos scroll
  private static final int kScrollDelay        = 4,
                           kScrollDelayInitial = 40;

  // gap between the image after wrap-around
  private static final int kScrollYGap = 16;
  
  // text and background images
  private static EgaImage kBackgroundImage = null,
                          kTextImage       = null;
  
  // reference to the text sprite (two versions so it can wrap around)
  private Picture mText[];
  
  // delay until the next scroll
  private int mTimer;

  // prepare resources
  static void initialize() {
    
    MenuPanel.initialize();
    
    if ( kBackgroundImage == null ) {
      kBackgroundImage = Env.resources().loadEgaImage("TitleScreen.png");
      kFrameImage.draw(kBackgroundImage, 0, 0);
      kTextImage = Env.resources().loadEgaImage("CreditsText.png");
    }

  } // initialize()
  
  // constructor
  public MenuCredits() {

    initialize();

    mBackground = new Picture(kBackgroundImage, 0.0f);
    mText = new Picture[]{ null, null };

    mTimer = 0;
    
  } // constructor
  
  // called when the panel becomes active
  public void enable(SpriteManager spriteManager) {
    
    mText[0] = new Picture(kTextImage, -2.0f);
    mText[1] = new Picture(kTextImage, -2.0f);
    for ( Picture text : mText ) spriteManager.addSprite(text);
    mText[1].setYPos( -mText[1].image().height() );
    
    mTimer = kScrollDelayInitial;
    
  } // MenuPanel.enable()
  
  // called every frame when the panel is active
  public boolean advance(SpriteManager spriteManager) {

    
    if ( --mTimer < 0 ) {
      for ( Picture text : mText ) text.setYPos(text.getYPos()-1);
      for ( int k = 0 ; k < mText.length ; k++ ) {
        if ( mText[k].getYPos() + mText[k].image().height() <= 0 ) {
          int i = 1 - k;
          int y = mText[i].getYPos() + mText[i].image().height() + kScrollYGap;
          mText[k].setYPos(y);
        }
      }
      mTimer = kScrollDelay;
    }
    
    return false;
    
  } // MenuPanel.advance()

  // called when the panel stops being active
  public void disable(SpriteManager spriteManager) {

    for ( Picture text : mText ) spriteManager.removeSprite(text);
    
  } // MenuPanel.disable()
  
} // class MenuCredits
