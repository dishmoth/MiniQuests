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
                          kTextImage1      = null,
                          kTextImage2      = null;

  // reference to the text sprite
  private AnimPicture mText;
  
  // check on the fire key
  private boolean mReady;
  
  // button size controls (left and right/smaller and bigger)
  private MapArrow mArrows[];

  // prepare resources
  static void initialize() {
    
    MenuPanel.initialize();
    
    if ( kBackgroundImage == null ) {
      kBackgroundImage = Env.resources().loadEgaImage("ControlSchemePic.png");
      kFrameImage.draw(kBackgroundImage, 0, 0);
      kTextImage1 = Env.resources().loadEgaImage("ControlSchemeText.png");
      kTextImage2 = Env.resources().loadEgaImage("ChangeTextAndroid.png");
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
    
    mText = new AnimPicture(0, kTextImage1, 
                            kAnimTitleDelay, kAnimBlankDelay,
                            kTextImage2, 
                            kAnimBeginDelay, kAnimBlankDelay);
    spriteManager.addSprite(mText);

    Env.keys().setButtonDetails(3, 3);
    
    mArrows = null;

    mReady = false;
    
  } // MenuPanel.enable()
  
  // called every frame when the panel is active
  public boolean advance(SpriteManager spriteManager) {

    final boolean keyFire  = Env.keys().fire(),
                  keyLeft  = Env.keys().left(),
                  keyRight = Env.keys().right();

    int scheme  = Env.saveState().touchScreenControls(),
        size    = Env.saveState().buttonSize(),
        maxSize = SaveState.MAX_BUTTON_SIZE;
    assert( scheme >= 0 );
    assert( size >= 0 && size <= maxSize );
    if ( mReady ) {
      if ( keyFire ) {
        scheme = (scheme + 1) % 2;
        Env.saveState().setTouchScreenControls(scheme);
        Env.sounds().play(Sounds.MENU_2);
      } else if ( keyLeft && scheme == 1 && size > 0 ) {
        size -= 1;
        Env.saveState().setButtonSize(size);
        Env.sounds().play(Sounds.MENU_2);
      } else if ( keyRight && scheme == 1 && size < maxSize ) {
        size += 1;
        Env.saveState().setButtonSize(size);
        Env.sounds().play(Sounds.MENU_2);
      }
    }
    mReady = (!keyFire && !keyLeft && !keyRight);    
    
    if ( mArrows == null ) {
      mArrows = new MapArrow[]{ new MapArrow(Env.LEFT),
                                new MapArrow(Env.RIGHT) };
      for ( int k = 0 ; k < mArrows.length ; k++ ) {
        mArrows[k].setColour(1);
        spriteManager.addSprite(mArrows[k]);
      }
    }

    mArrows[0].mDrawDisabled = (scheme == 0 || size == 0);
    mArrows[1].mDrawDisabled = (scheme == 0 || size == maxSize);

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
