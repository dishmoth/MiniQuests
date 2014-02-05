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
                           kArrowMarginY     = 9;
  private static final int kArrowMarginInner = 5;

  // button colours
  private static final Color kColourDark   = new Color(0.2f, 0.2f, 0.2f, 1.0f),
                             kColourBright = new Color(1.0f, 1.0f, 1.0f, 1.0f);
  
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
  
  // specify the target width for the arrow buttons 
  public void setRefSize(int pixels) {

    assert( pixels > 0 );

    final int buttonPixels = 2*kArrowWidth + kArrowGapX;
    mScale = (int)Math.ceil( pixels/(float)buttonPixels );
    
  } // setRefSize()
  
  // number of pixels needed for buttons on left/right side of screen
  public int marginXSize() {
    
    return mScale*( kArrowMarginX + 2*kArrowWidth 
                    + kArrowGapX + kArrowMarginInner );
    
  } // marginXSize()
  
  // number of pixels needed for buttons at bottom of screen
  public int marginYSize() {
    
    return mScale*( kArrowMarginY + 2*kArrowHeight 
                    + kArrowGapY + kArrowMarginInner );
    
  } // marginYSize()
    
  // check whether one of the arrow buttons is currently pressed
  public boolean arrowTouched(int dx, int dy) {

    assert( dx == -1 || dx == +1 );
    assert( dy == -1 || dy == +1 );
    
    int size = mScale*kButtonSize;
    int x = ( mScale*(kArrowMarginX + kArrowWidth) + (mScale*kArrowGapX)/2 ),
        y = Gdx.graphics.getHeight() -
            ( mScale*(kArrowMarginY + kArrowHeight) + (mScale*kArrowGapY)/2 );

    return KeyMonitorAndroid.isTouched(x, y, dx*size, -dy*size);
    
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
    
    int size = mScale*kButtonSize;
    int x = Gdx.graphics.getWidth() - 
            ( mScale*(kArrowMarginX + kArrowWidth) + (mScale*kArrowGapX)/2 ),
        y = Gdx.graphics.getHeight() -
            ( mScale*(kArrowMarginY + kArrowHeight) + (mScale*kArrowGapY)/2 );
    return KeyMonitorAndroid.isTouched(x-size, y-size, 2*size, 2*size);
    
  } // fire()
  
  // set various details of the on-screen buttons
  public void setDetails(int arrowStyle, int fireStyle) {

    assert( arrowStyle >= 0 && arrowStyle <= 2 );
    assert( fireStyle >= 0 && fireStyle <= 2 );
    
    mArrowStyle = arrowStyle;
    mFireStyle = fireStyle;
    
  } // setDetails()
  
  // show the buttons
  public void display(SpriteBatch spriteBatch) {
    
    prepare();
    
    Color oldColour = spriteBatch.getColor();
    
    final int arrWidth  = mScale*kArrowWidth,
              arrHeight = mScale*kArrowHeight;
    final int arrX0     = mScale*kArrowMarginX + arrWidth,
              arrY0     = mScale*kArrowMarginY + arrHeight;
    final int arrX1     = arrX0 + mScale*kArrowGapX,
              arrY1     = arrY0 + mScale*kArrowGapY;

    if ( mArrowStyle > 0 ) {
      spriteBatch.setColor( (mArrowStyle==2) ? kColourBright : kColourDark );
      spriteBatch.draw(mArrowImage, arrX0, arrY0, -arrWidth, -arrHeight);
      spriteBatch.draw(mArrowImage, arrX1, arrY0, +arrWidth, -arrHeight);
      spriteBatch.draw(mArrowImage, arrX0, arrY1, -arrWidth, +arrHeight);
      spriteBatch.draw(mArrowImage, arrX1, arrY1, +arrWidth, +arrHeight);
    }

    final int fireX      = Gdx.graphics.getWidth() - (arrX0 + arrX1)/2,
              fireY      = (arrY0 + arrY1)/2;
    final int fireWidth  = mScale*kFireWidth,
              fireHeight = mScale*kFireHeight;
    
    if ( mFireStyle > 0 ) {
      spriteBatch.setColor( (mFireStyle==2) ? kColourBright : kColourDark );
      spriteBatch.draw(mFireImage,
                       fireX-fireWidth/2, fireY-fireHeight/2,
                       fireWidth, fireHeight);
    }
                     
    spriteBatch.setColor(oldColour);
    
  } // display()
  
} // class KeyButtons
