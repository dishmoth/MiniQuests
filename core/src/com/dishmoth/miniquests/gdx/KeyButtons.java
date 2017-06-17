/*
 *  KeyButtons.java
 *  Copyright Simon Hern 2013
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dishmoth.miniquests.game.Env;

// on-screen buttons for large-screen android devices
public class KeyButtons {

  // image sizes (pixels)
  private static final int kArrowWidth  = 7,
                           kArrowHeight = 4;
  private static final int kArrowGapX   = 2,
                           kArrowGapY   = 1;
  private static final int kFireWidth   = 7,
                           kFireHeight  = 7;
  
  // number of pixels to leave between the buttons and the screen edge
  private static final int kArrowMarginX     = 7,
                           kArrowMarginY     = 9,
                           kArrowMargin0     = 4;
  private static final int kArrowMarginInner = 5;

  // button colours
  private static final float kColourDark   = 0.2f,
                             kColourBright = 0.7f,
                             kColourPeriod = 14.0f;
  
  // button touch size (unscaled pixels)
  private static final int kButtonSize = 15;
  
  // pixel scale for the button images
  private int mScale;
  
  // texture holding images
  private Texture mButtonTexture;
  
  // images for direction buttons and fire button
  private TextureRegion mArrowImage,
                        mFireImage;

  // button styles (0 => hidden, 1 => normal, 2 => bright) 
  private int mArrowStyle,
              mFireStyle;
  
  // constructor
  public KeyButtons() {
    
    mButtonTexture = null;
    mArrowImage = mFireImage = null;

    mScale = 1;

    mArrowStyle = mFireStyle = 1;
    
  } // constructor

  // load resources
  public void prepare() {
    
    if ( mButtonTexture != null ) return;
    
    mButtonTexture = ResourcesGdx.loadTexture("Buttons.png");
    mArrowImage = new TextureRegion(mButtonTexture, 
                                    0, 0, 
                                    kArrowWidth, kArrowHeight);
    mFireImage = new TextureRegion(mButtonTexture, 
                                   0, 4, 
                                   kFireWidth, kFireHeight);
    
  } // prepare()
  
  // discard resources
  public void dispose() {
    
    if ( mButtonTexture != null ) {
      mButtonTexture.dispose();
      mButtonTexture = null;
    }
    
    mArrowImage = mFireImage = null;
    
  } // dispose()
  
  // specify the target width (in pixels) for the arrow buttons 
  public void setRefSize(int pixels) {

    assert( pixels > 0 );

    final int buttonPixels = 2*kArrowWidth + kArrowGapX;
    mScale = (int)Math.ceil( pixels/(float)buttonPixels );
    
  } // setRefSize()
  
  // return the current control scheme (0 => corners, 1 => buttons)
  public int buttonScheme() {
    
    int s = Env.saveState().touchScreenControls();
    assert( s >= 0 && s <= 1 );
    return s;
    
  } // scheme()
  
  // number of pixels needed for buttons on left/right side of screen
  public int marginXSize() {
    
    return (buttonScheme() == 0) ? 0 
                                 : mScale*( kArrowMarginX + 2*kArrowWidth 
                                            + kArrowGapX + kArrowMarginInner );
    
  } // marginXSize()
  
  // number of pixels needed for buttons at bottom of screen
  public int marginYSize() {
    
    return (buttonScheme() == 0) ? 0 
                                 : mScale*( kArrowMarginY + 2*kArrowHeight 
                                            + kArrowGapY + kArrowMarginInner );
    
  } // marginYSize()
    
  // check whether one of the arrow buttons is currently pressed
  public boolean arrowTouched(int dx, int dy) {

    assert( dx == -1 || dx == +1 );
    assert( dy == -1 || dy == +1 );
    
    if ( buttonScheme() == 0 ) {

      return KeyMonitorAndroid.isTouchedFrac( 0.5f + dx*0.5f, 
                                              0.5f - dy*0.5f, 
                                              -dx*0.5f,
                                              dy*1.0f/3.0f );
        
    } else {

      int size = mScale*kButtonSize;
      int x = ( mScale*(kArrowMarginX+kArrowWidth) + (mScale*kArrowGapX)/2 ),
          y = Gdx.graphics.getHeight() -
              ( mScale*(kArrowMarginY+kArrowHeight) + (mScale*kArrowGapY)/2 );
      return KeyMonitorAndroid.isTouched(x, y, dx*size, -dy*size);
      
    }
    
  } // arrowTouched()
  
  // check whether the up button is currently pressed
  public boolean up() { return arrowTouched(-1, +1); }
  
  // check whether the down button is currently pressed
  public boolean down() { return arrowTouched(+1, -1); }
  
  // check whether the left button is currently pressed
  public boolean left() { return arrowTouched(-1, -1); }
  
  // check whether the right button is currently pressed
  public boolean right() { return arrowTouched(+1, +1); }
  
  // check whether the fire button is currently pressed
  public boolean fire() {
    
    if ( buttonScheme() == 0 ) {

      return KeyMonitorAndroid.isTouchedFrac( 0.0f, 
                                              1.0f/3.0f, 
                                              1.0f,
                                              1.0f/3.0f );

    } else {
    
      int size = mScale*kButtonSize;
      int x = Gdx.graphics.getWidth() - 
              ( mScale*(kArrowMarginX+kArrowWidth) + (mScale*kArrowGapX)/2 ),
          y = Gdx.graphics.getHeight() -
              ( mScale*(kArrowMarginY+kArrowHeight) + (mScale*kArrowGapY)/2 );
      return KeyMonitorAndroid.isTouched(x-size, y-size, 2*size, 2*size);
    
    }
    
  } // fire()
  
  // set various details of the on-screen buttons
  // (0 => hidden, 1 => usually visible, 2 => highlighted, 3 => always visible)
  public void setDetails(int arrowStyle, int fireStyle) {

    assert( arrowStyle >= 0 && arrowStyle <= 3 );
    assert( fireStyle >= 0 && fireStyle <= 3 );
    
    mArrowStyle = arrowStyle;
    mFireStyle = fireStyle;
    
  } // setDetails()
  
  // show the buttons
  public void display(SpriteBatch spriteBatch, boolean gameMode) {
    
    prepare();

    Color oldColour = spriteBatch.getColor();
    
    float c = (float)Math.sin(2*Math.PI*((Env.ticks()/kColourPeriod)%1.0f));
    float colour = (0.5f+0.5f*c)*kColourDark + (0.5f-0.5f*c)*kColourBright;
    
    final int scheme = buttonScheme();
    
    final int arrWidth  = mScale*kArrowWidth,
              arrHeight = mScale*kArrowHeight;
    int arrX0, arrY0, arrX1, arrY1;

    if ( scheme == 0 ) {
      arrX0 = mScale*kArrowMargin0 + arrWidth;
      arrY0 = mScale*kArrowMargin0 + arrHeight;
      arrX1 = Gdx.graphics.getWidth() - arrX0;
      arrY1 = Gdx.graphics.getHeight() - arrY0;
    } else {
      arrX0 = mScale*kArrowMarginX + arrWidth;
      arrY0 = mScale*kArrowMarginY + arrHeight;
      arrX1 = arrX0 + mScale*kArrowGapX;
      arrY1 = arrY0 + mScale*kArrowGapY;
    }

    boolean arrowsOn = (mArrowStyle == 3) ||
                       (mArrowStyle == 2 && scheme == 1) || 
                       (mArrowStyle == 1 && scheme == 1 && gameMode);
    
    if ( arrowsOn ) {
      float col = (mArrowStyle>=2) ? colour : kColourDark;
      spriteBatch.setColor(col, col, col, 1.0f );
      spriteBatch.draw(mArrowImage, arrX0, arrY0, -arrWidth, -arrHeight);
      spriteBatch.draw(mArrowImage, arrX1, arrY0, +arrWidth, -arrHeight);
      spriteBatch.draw(mArrowImage, arrX0, arrY1, -arrWidth, +arrHeight);
      spriteBatch.draw(mArrowImage, arrX1, arrY1, +arrWidth, +arrHeight);
    }

    final int fireX      = Gdx.graphics.getWidth() - (arrX0 + arrX1)/2,
              fireY      = (arrY0 + arrY1)/2;
    final int fireWidth  = mScale*kFireWidth,
              fireHeight = mScale*kFireHeight;

    boolean fireOn = (mFireStyle == 3) || 
                     (mFireStyle == 2 && scheme == 1) || 
                     (mFireStyle == 1 && scheme == 1 && gameMode);
    
    if ( fireOn ) {
      float col = (mFireStyle>=2) ? colour : kColourDark;
      spriteBatch.setColor(col, col, col, 1.0f);
      if ( scheme == 0 ) {
        spriteBatch.draw(mFireImage,
                         arrX0-fireWidth, fireY-fireHeight/2,
                         fireWidth, fireHeight);
        spriteBatch.draw(mFireImage,
                         Gdx.graphics.getWidth()-arrX0, fireY-fireHeight/2,
                         fireWidth, fireHeight);
      } else {
        spriteBatch.draw(mFireImage,
                         fireX-fireWidth/2, fireY-fireHeight/2,
                         fireWidth, fireHeight);
      }
    }
                     
    spriteBatch.setColor(oldColour);
    
  } // display()
  
} // class KeyButtons
