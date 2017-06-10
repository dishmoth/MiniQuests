/*
 *  KeyMonitorMonitor.java
 *  Copyright Simon Hern 2014
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.KeyMonitor;

// keep track of what 'keys' (on touch screen) are currently pressed
public class KeyMonitorAndroid extends KeyMonitorGdx {

  // how many pointers to check
  private static final int kNumPointers = 2;

  // a large distance in game coordinates
  private static final int kBig = 1000;
  
  // how big the screen needs to be (centimetres) for on-screen buttons
  private static final float kBigScreenXCm = 13.0f,
                             kBigScreenYCm = 7.0f;
  private static final int   kSafePixPerCm = 20;
  
  // size of the on-screen buttons (centimetres and fraction of screen height)
  private static final float kButtonsCmMin   = 1.9f,
                             kButtonsCmMax   = 2.5f;
  private static final float kButtonsFracMin = 0.10f,
                             kButtonsFracMax = 0.25f;
  private static final float kScreenCmSmall  = 17.5f,
                             kScreenCmBig    = 25.0f;
  
  // helper object for managing on-screen buttons (or null)
  private KeyButtons mKeyButtons;
  
  // make on-screen button visible even if they normally wouldn't be
  private boolean mButtonDisplay;
  
  // constructor
  public KeyMonitorAndroid() {

    super();

    mKeyButtons = new KeyButtons();
    setButtonSize();
    
    mButtonDisplay = false;
    
  } // constructor
  
  // choose button size and default on-screen control style
  private void setButtonSize() {
    
    final float ppcX = Math.max(Gdx.graphics.getPpcX(), kSafePixPerCm),
                ppcY = Math.max(Gdx.graphics.getPpcY(), kSafePixPerCm);
    
    final int width  = Gdx.graphics.getWidth(),
              height = Gdx.graphics.getHeight(),
              size   = Math.min(width, height);
    
    float xcm = width / ppcX,
          ycm = height / ppcY;
    float diag = (float)Math.sqrt(xcm*xcm + ycm*ycm);
    Env.debug("Screen size: " + xcm + " x " + ycm 
              + " cm (diag " + diag + " cm)");

    if ( Env.saveState().touchScreenControls() < 0 ) {
      boolean useCorners = ( xcm < kBigScreenXCm || ycm < kBigScreenYCm );
      Env.saveState().setTouchScreenControls( useCorners ? 0 : 1 );
    }
    
    float h = (diag - kScreenCmSmall)/(kScreenCmBig - kScreenCmSmall);
    h = Math.max(0.0f, Math.min(1.0f, h));
    float buttonsCm = h*kButtonsCmMax + (1-h)*kButtonsCmMin;
    Env.debug("Button width (cm): target " + buttonsCm); 
    int buttonsPix = Math.round( buttonsCm*ppcX );
    int pixMax = Math.round( size*kButtonsFracMax ),
        pixMin = Math.round( size*kButtonsFracMin );
    Env.debug("Button width (pixels): target " + buttonsPix 
              + ", min " + pixMin + ", max " + pixMax);
    buttonsPix = Math.min( pixMax, Math.max( pixMin, buttonsPix ) ); 
    mKeyButtons.setRefSize(buttonsPix);
    
  } // setButtonSize()
  
  // check whether the up button is currently pressed
  @Override
  public boolean up() {
    
    if ( mControllers != null && mMode != KeyMonitor.MODE_QUERY ) {
      if ( mControllers.up() ) return true;
    }
    
    if ( mMode != KeyMonitor.MODE_QUERY ) {
      if ( Gdx.input.isKeyPressed(Input.Keys.UP) ||
           Gdx.input.isKeyPressed(Input.Keys.DPAD_UP) ) return true;
    }

    if ( mMode == KeyMonitor.MODE_GAME ) {
      if ( Env.saveState().touchScreenControls() == 1 ) {
        return mKeyButtons.up();
      } else {
        return isTouchedFrac( 0.0f,
                              0.0f,
                              0.5f,
                              1.0f/3.0f );
      }
    }
    if ( mMode == KeyMonitor.MODE_MAP ) {
      return isTouchedPix( 0,
                           0, 
                           Env.screenWidth(), 
                           -kBig )
          || isTouchedPix( 5,
                           0, 
                           Env.screenWidth()-10, 
                           5 );
    }
    return false;
    
  } // KeyMonitor.up()

  // check whether the down button is currently pressed
  @Override
  public boolean down() {
    
    if ( mControllers != null && mMode != KeyMonitor.MODE_QUERY ) {
      if ( mControllers.down() ) return true;
    }
    
    if ( mMode != KeyMonitor.MODE_QUERY ) {
      if ( Gdx.input.isKeyPressed(Input.Keys.DOWN) ||
           Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN) ) return true;
    }
    
    if ( mMode == KeyMonitor.MODE_GAME ) {
      if ( Env.saveState().touchScreenControls() == 1 ) {
        return mKeyButtons.down();
      } else {
        return isTouchedFrac( 1.0f,
                              1.0f, 
                              -0.5f,
                              -1.0f/3.0f );
      }
    }
    if ( mMode == KeyMonitor.MODE_MAP ) {
      return isTouchedPix( 0,
                           Env.screenHeight(),
                           Env.screenWidth(),
                           +kBig )
          || isTouchedPix( 5,
                           Env.screenHeight(),
                           Env.screenWidth()-10,
                           -5 );
    }
    return false;
    
  } // KeyMonitor.down()

  // check whether the left button is currently pressed
  @Override
  public boolean left() {
    
    if ( mControllers != null && mMode != KeyMonitor.MODE_QUERY ) {
      if ( mControllers.left() ) return true;
    }
    
    if ( mMode != KeyMonitor.MODE_QUERY ) {
      if ( Gdx.input.isKeyPressed(Input.Keys.LEFT) ||
           Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT) ) return true;
    }
    
    if ( mMode == KeyMonitor.MODE_GAME ) {
      if ( Env.saveState().touchScreenControls() == 1 ) {
        return mKeyButtons.left();
      } else {
        return isTouchedFrac( 0.0f, 
                              1.0f, 
                              0.5f,
                              -1.0f/3.0f );
      }
    }
    if ( mMode == KeyMonitor.MODE_MAP ) {
      return isTouchedPix( 0,
                           0, 
                           -kBig, 
                           Env.screenHeight() )
          || isTouchedPix( 0,
                           5, 
                           5, 
                           Env.screenHeight()-10 );
    }
    if ( mMode == KeyMonitor.MODE_QUERY ) {
      return isTouchedPix( 0,
                           0,
                           Env.screenWidth()/2,
                           Env.screenHeight() );
    }
    return false;
    
  } // KeyMonitor.left()

  // check whether the right button is currently pressed
  @Override
  public boolean right() {
    
    if ( mControllers != null && mMode != KeyMonitor.MODE_QUERY ) {
      if ( mControllers.right() ) return true;
    }
    
    if ( mMode != KeyMonitor.MODE_QUERY ) {
      if ( Gdx.input.isKeyPressed(Input.Keys.RIGHT) ||
           Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT) ) return true;
    }
    
    if ( mMode == KeyMonitor.MODE_GAME ) {
      if ( Env.saveState().touchScreenControls() == 1 ) {
        return mKeyButtons.right();
      } else {
        return isTouchedFrac( 1.0f, 
                              0.0f, 
                              -0.5f,
                              1.0f/3.0f );
      }
    }
    if ( mMode == KeyMonitor.MODE_MAP ) {
      return isTouchedPix( Env.screenWidth(),
                           0, 
                           +kBig, 
                           Env.screenHeight() )
          || isTouchedPix( Env.screenWidth(),
                           5, 
                           -5, 
                           Env.screenHeight()-10 );
    }
    if ( mMode == KeyMonitor.MODE_QUERY ) {
      return isTouchedPix( Env.screenWidth()/2,
                           0,
                           Env.screenWidth()/2,
                           Env.screenHeight() );
    }
    return false;

  } // KeyMonitor.right()

  // check whether the fire button is currently pressed
  @Override
  public boolean fire() {
    
    if ( mControllers != null && mMode != KeyMonitor.MODE_QUERY ) {
      if ( mControllers.fire() ) return true;
    }
    
    if ( mMode != KeyMonitor.MODE_QUERY ) {
      if ( Gdx.input.isKeyPressed(Input.Keys.SPACE) ||
           Gdx.input.isKeyPressed(Input.Keys.DPAD_CENTER) ) return true;
    }
    
    if ( mMode == KeyMonitor.MODE_GAME ) {
      if ( Env.saveState().touchScreenControls() == 1 ) {
        return mKeyButtons.fire();
      } else {
        return isTouchedFrac( 0.0f, 
                              1.0f/3.0f, 
                              1.0f,
                              1.0f/3.0f );
      }
    }
    if ( mMode == KeyMonitor.MODE_MAP ) {
      return isTouchedPix( 5,
                           5,
                           Env.screenWidth()-10, 
                           Env.screenHeight()-10 );
    }
    return false;
    
  } // KeyMonitor.fire()

  // check for touch in a rectangular region (true screen coordinates)
  public static boolean isTouched(int x, int y, int width, int height) {

    if ( width < 0 ) {
      x += width;
      width = -width;
    }
    if ( height < 0 ) {
      y += height;
      height = -height;
    }
    
    for ( int ptrInd = 0 ; ptrInd < kNumPointers ; ptrInd++ ) {
      if ( Gdx.input.isTouched(ptrInd) ) {
        int xPtr = Gdx.input.getX(ptrInd),
            yPtr = Gdx.input.getY(ptrInd);
        if ( xPtr >= x && xPtr < x+width && yPtr >= y && yPtr < y+height ) {
          return true;
        }
      }
    }
    
    return false;
    
  } // isTouched()
  
  // check for touch in a rectangular region (game screen coordinates)
  public static boolean isTouchedPix(int x, int y, int width, int height) {

    int scale = Env.screenScale().scale();
    int xx = Gdx.graphics.getWidth()/2 + scale*(x - Env.screenWidth()/2),
        yy = Gdx.graphics.getHeight()/2 + scale*(y - Env.screenHeight()/2);
    return isTouched(xx, yy, width*scale, height*scale);
    
  } // isTouchedPix()

  // check for touch in a rectangular region (fraction of full screen, 0 to 1)
  public static boolean isTouchedFrac(float x, float y, 
                                      float width, float height) {

    int xx = (int)Math.round(x * Gdx.graphics.getWidth()),
        yy = (int)Math.round(y * Gdx.graphics.getHeight()),
        ww = (int)Math.round(width * Gdx.graphics.getWidth()),
        hh = (int)Math.round(height * Gdx.graphics.getHeight());
    return isTouched(xx, yy, ww, hh);
    
  } // isTouchedFrac()

  // check whether the escape button is currently pressed
  @Override
  public boolean escape() {
    
    return ( Gdx.input.isKeyPressed(Input.Keys.BACK) ||
             Gdx.input.isKeyPressed(Input.Keys.ESCAPE) );
    
  } // KeyMonitor.escape()

  // check whether any key is currently pressed
  @Override
  public boolean any() {
    
    return ( (mControllers != null && mControllers.any()) || 
             Gdx.input.isTouched() ||
             Gdx.input.isKeyPressed(Input.Keys.ANY_KEY) ||
             Gdx.input.isKeyPressed(Input.Keys.DPAD_CENTER) );
    
  } // KeyMonitor.any()
  
  // set details of the on-screen buttons
  @Override
  public void setButtonDetails(int arrowStyle, int fireStyle) {
    
    if ( mKeyButtons != null ) {
      mKeyButtons.setDetails(arrowStyle, fireStyle);
    }
    
  } // KeyMonitor.setButtonDetails()
  
  // number of pixels needed for buttons on left and right of screen
  public int buttonsXMargin() { 
    
    return ( (mKeyButtons!=null) ? mKeyButtons.marginXSize() : 0 );
    
  } // buttonsXSize()
  
  // number of pixels needed for buttons at top and bottom of screen
  public int buttonsYMargin() {
    
    return ( (mKeyButtons!=null) ? mKeyButtons.marginYSize() : 0 );
    
  } // buttonsYSize()
  
  // make on-screen buttons visible even if they normally wouldn't be
  public void setButtonDisplay(boolean v) { mButtonDisplay = v; }
  
  // draw the on-screen buttons, if used
  @Override
  public void displayButtons(SpriteBatch spriteBatch) {

    if ( mKeyButtons != null && (mMode == MODE_GAME || mButtonDisplay) ) {
      mKeyButtons.display(spriteBatch);
    }
    
  } // displayButtons()
  
} // class KeyMonitorAndroid
