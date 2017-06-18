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
import com.dishmoth.miniquests.game.SaveState;

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

  // size range of the buttons (relative to screen size)
  private static final float kButtonsFracMin = 0.04f,
                             kButtonsFracMax = 0.18f;

  // button colours
  private static final float kColourDark   = 0.2f,
                             kColourBright = 0.7f,
                             kColourPeriod = 14.0f;
  
  // button touch size (unscaled pixels)
  private static final int kButtonSize = 15;
  
  // pixel scale range for the button images
  private int mScaleRange[],
              mScaleDefault;
  
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

    mArrowStyle = mFireStyle = 1;

    setScaleRange();
    mScaleDefault = 1;
    
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

  // set the min and max scaling factors
  private void setScaleRange() {
    
    final int screenSize = Math.min(Gdx.graphics.getWidth(),
                                    Gdx.graphics.getHeight());
    float minSize = Math.round(kButtonsFracMin * screenSize),
          maxSize = Math.round(kButtonsFracMax * screenSize);
    
    final int buttonPixels = 2*kArrowHeight + kArrowGapY;
    final int numScales    = SaveState.MAX_BUTTON_SIZE + 1;
    int scaleMin = Math.max(1, Math.round(minSize / buttonPixels)),
        scaleMax = Math.max(Math.round(maxSize / buttonPixels),
                            scaleMin + numScales - 1);
    Env.debug("Button pixel scale: " + scaleMin + " to " + scaleMax);

    mScaleRange = new int[numScales];
    int scaleStep = (scaleMax - scaleMin)/(numScales - 1),
        bigSteps  = (scaleMax - scaleMin) - scaleStep*(numScales - 1);
    mScaleRange[0] = scaleMin;
    for ( int k = 1 ; k < numScales ; k++ ) {
      mScaleRange[k] = mScaleRange[k-1] + scaleStep;
      if ( k >= numScales - bigSteps ) mScaleRange[k] += 1;
    }
    assert( mScaleRange[numScales-1] == scaleMax );
    
  } // setScaleRange()
  
  // specify the target width (in pixels) for the arrow buttons 
  public void setDefaultSize(int pixels) {

    assert( pixels > 0 );

    final int buttonPixels = 2*kArrowWidth + kArrowGapX;
    float targetScale = pixels/(float)buttonPixels;
    
    int best = 0;
    for ( int k = 1 ; k < mScaleRange.length ; k++ ) {
      if ( Math.abs(mScaleRange[k] - targetScale) 
                          < Math.abs(mScaleRange[best] - targetScale) ) {
        best = k;
      }
    }
    mScaleDefault = mScaleRange[best];
    Env.debug("Button pixel scale: default " + mScaleDefault 
              + " (target " + targetScale + ")");
    
    if ( Env.saveState().buttonSize() < 0 ) {
      Env.saveState().setButtonSize(best);
    }
    
  } // setDefaultSize()

  // current pixel scaling factor
  public int scale() {
    
    if ( buttonScheme() == 0 ) {
      return mScaleDefault;
    } else {
      int index = Env.saveState().buttonSize();
      assert( index >= 0 && index < mScaleRange.length );
      return mScaleRange[index];
    }
    
  } // scale()
  
  // return the current control scheme (0 => corners, 1 => buttons)
  public int buttonScheme() {
    
    int s = Env.saveState().touchScreenControls();
    assert( s >= 0 && s <= 1 );
    return s;
    
  } // scheme()
  
  // number of pixels needed for buttons on left/right side of screen
  public int marginXSize() {
    
    return (buttonScheme() == 0) ? 0 
                                 : scale()*( kArrowMarginX + 2*kArrowWidth 
                                             + kArrowGapX + kArrowMarginInner );
    
  } // marginXSize()
  
  // number of pixels needed for buttons at bottom of screen
  public int marginYSize() {
    
    return (buttonScheme() == 0) ? 0 
                                 : scale()*( kArrowMarginY + 2*kArrowHeight 
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

      final int scale = scale();
      int size = scale*kButtonSize;
      int x = ( scale*(kArrowMarginX+kArrowWidth) + (scale*kArrowGapX)/2 ),
          y = Gdx.graphics.getHeight() -
              ( scale*(kArrowMarginY+kArrowHeight) + (scale*kArrowGapY)/2 );
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
    
      final int scale = scale();
      int size = scale*kButtonSize;
      int x = Gdx.graphics.getWidth() - 
              ( scale*(kArrowMarginX+kArrowWidth) + (scale*kArrowGapX)/2 ),
          y = Gdx.graphics.getHeight() -
              ( scale*(kArrowMarginY+kArrowHeight) + (scale*kArrowGapY)/2 );
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
    final int scale = scale();
    
    final int arrWidth  = scale*kArrowWidth,
              arrHeight = scale*kArrowHeight;
    int arrX0, arrY0, arrX1, arrY1;

    if ( scheme == 0 ) {
      arrX0 = scale*kArrowMargin0 + arrWidth;
      arrY0 = scale*kArrowMargin0 + arrHeight;
      arrX1 = Gdx.graphics.getWidth() - arrX0;
      arrY1 = Gdx.graphics.getHeight() - arrY0;
    } else {
      arrX0 = scale*kArrowMarginX + arrWidth;
      arrY0 = scale*kArrowMarginY + arrHeight;
      arrX1 = arrX0 + scale*kArrowGapX;
      arrY1 = arrY0 + scale*kArrowGapY;
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
    final int fireWidth  = scale*kFireWidth,
              fireHeight = scale*kFireHeight;

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
