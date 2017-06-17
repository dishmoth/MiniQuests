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
  
  // size controls (left and right/smaller and bigger)
  private MapArrow mArrows[];

  // prepare resources
  static void initialize() {
    
    MenuPanel.initialize();
    
    if ( kBackgroundImage == null ) {
      kBackgroundImage = Env.resources().loadEgaImage("ControlSchemePic.png");
      kFrameImage.draw(kBackgroundImage, 0, 0);
      kTextImage = Env.resources().loadEgaImage("ControlSchemeText.png");
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
    
    mText = new Picture(kTextImage, -2.0f);
    spriteManager.addSprite(mText);

    Env.keys().setButtonDetails(3, 3);
    
    mArrows = null;

    mReady = false;
    
  } // MenuPanel.enable()
  
  // called every frame when the panel is active
  public boolean advance(SpriteManager spriteManager) {

    final boolean keyFire = Env.keys().fire();

    int scheme = Env.saveState().touchScreenControls();
    assert( scheme >= 0 );
    if ( keyFire && mReady ) {
      scheme = (scheme + 1) % 2;
      Env.saveState().setTouchScreenControls(scheme);
      Env.sounds().play(Sounds.MENU_2);
    }
    mReady = !keyFire;    
    
    if ( mArrows == null ) {
      mArrows = new MapArrow[]{ new MapArrow(Env.LEFT),
                                new MapArrow(Env.RIGHT) };
      for ( int k = 0 ; k < mArrows.length ; k++ ) {
        mArrows[k].setColour(1);
        spriteManager.addSprite(mArrows[k]);
      }
    }

    mArrows[0].mDrawDisabled = mArrows[1].mDrawDisabled = (scheme == 0);

    return false;
    
  } // MenuPanel.advance()

  // called when the panel stops being active
  public void disable(SpriteManager spriteManager) {
    
    spriteManager.removeSprite(mText);

    Env.keys().setButtonDetails(1, 1);
    
    if ( mArrows != null ) {
      for ( MapArrow arrow : mArrows ) {
        if ( arrow != null ) spriteManager.removeSprite(arrow);
      }
      mArrows = null;
    }
    
  } // MenuPanel.disable()
  
} // class MenuControls
