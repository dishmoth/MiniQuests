/*
 *  MenuResize.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

// menu option for changing the screen size
public class MenuResize extends MenuPanel {

  // text and background images
  private static EgaImage kBackgroundImage = null,
                          kTextImage       = null;

  // reference to the text sprite
  private Picture mText;
  
  // check on the fire key
  private boolean mReady;
  
  // resize controls (left and right/smaller and bigger)
  private MapArrow mArrows[];
  
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
  public MenuResize() {

    initialize();

    mBackground = new Picture(kBackgroundImage, 0.0f);
    mText = null;
    
  } // constructor
  
  // called when the panel becomes active
  public void enable(SpriteManager spriteManager) {
    
    mText = new Picture(kTextImage, -2.0f);
    spriteManager.addSprite(mText);

    mArrows = null;
    
    mReady = false;
    
  } // MenuPanel.enable()
  
  // called every frame when the panel is active
  public boolean advance(SpriteManager spriteManager) {
    
    final boolean keyLeft  = Env.keys().left(),
                  keyRight = Env.keys().right();

    final int maxSize = Env.screenScale().maxSizeVal(),
              minSize = Env.screenScale().minSizeVal();
    int size = Env.screenScale().sizeVal();
    
    if ( keyLeft && mReady && size > minSize ) {
      size -= 1;
      Env.saveState().setScreenSize(size);
      Env.sounds().play(Sounds.MENU_2);
    } else if ( keyRight && mReady && size < maxSize ) {
      size += 1;
      Env.saveState().setScreenSize(size);
      Env.sounds().play(Sounds.MENU_2);
    }
    mReady = (!keyLeft && !keyRight);
    
    if ( mArrows == null ) {
      mArrows = new MapArrow[]{ new MapArrow(Env.LEFT),
                                new MapArrow(Env.RIGHT) };
      for ( int k = 0 ; k < mArrows.length ; k++ ) {
        mArrows[k].setColour(1);
        spriteManager.addSprite(mArrows[k]);
      }
    }

    mArrows[0].mDrawDisabled = (size == minSize);
    mArrows[1].mDrawDisabled = (size == maxSize);
    
    return false;
    
  } // MenuPanel.advance()

  // called when the panel stops being active
  public void disable(SpriteManager spriteManager) {
    
    spriteManager.removeSprite(mText);

    if ( mArrows != null ) {
      for ( MapArrow arrow : mArrows ) {
        if ( arrow != null ) spriteManager.removeSprite(arrow);
      }
      mArrows = null;
    }
    
  } // MenuPanel.disable()
  
} // class MenuResize
