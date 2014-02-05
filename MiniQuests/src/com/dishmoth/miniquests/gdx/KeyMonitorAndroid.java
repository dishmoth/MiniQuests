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

  // current magnification factor used by the game screen
  private int mScreenScale;
  
  // helper object for managing on-screen buttons (or null)
  private KeyButtons mKeyButtons;
  
  // constructor
  public KeyMonitorAndroid() {

    super();

    mScreenScale = 1;
    mKeyButtons = null;

  } // constructor
  
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
      if ( mKeyButtons != null ) {
        return mKeyButtons.up();
      } else {
        return isTouchedPix( Env.screenWidth()/4, 
                             Env.screenHeight()/3, 
                             -100,
                             -100 );
      }
    }
    if ( mMode == KeyMonitor.MODE_MAP ) {
      return isTouchedPix( 0,
                           0, 
                           Env.screenWidth(), 
                           -100 )
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
      if ( mKeyButtons != null ) {
        return mKeyButtons.down();
      } else {
        return isTouchedPix( 3*Env.screenWidth()/4,
                             2*Env.screenHeight()/3, 
                             +100,
                             +100 );
      }
    }
    if ( mMode == KeyMonitor.MODE_MAP ) {
      return isTouchedPix( 0,
                           Env.screenHeight(),
                           Env.screenWidth(),
                           +100 )
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
      if ( mKeyButtons != null ) {
        return mKeyButtons.left();
      } else {
        return isTouchedPix( Env.screenWidth()/4, 
                             2*Env.screenHeight()/3, 
                             -100,
                             +100 );
      }
    }
    if ( mMode == KeyMonitor.MODE_MAP ) {
      return isTouchedPix( 0,
                           0, 
                           -100, 
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
      if ( mKeyButtons != null ) {
        return mKeyButtons.right();
      } else {
        return isTouchedPix( 3*Env.screenWidth()/4, 
                             Env.screenHeight()/3, 
                             +100,
                             -100 );
      }
    }
    if ( mMode == KeyMonitor.MODE_MAP ) {
      return isTouchedPix( Env.screenWidth(),
                           0, 
                           +100, 
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
      if ( mKeyButtons != null ) {
        return mKeyButtons.fire();
      } else {
        return isTouchedPix( -100, 
                             Env.screenHeight()/3, 
                             +300,
                             Env.screenHeight()/3 );
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
  private boolean isTouchedPix(int x, int y, int width, int height) {
  
    int xx = Gdx.graphics.getWidth()/2  
             + mScreenScale*(x - Env.screenWidth()/2),
        yy = Gdx.graphics.getHeight()/2 
             + mScreenScale*(y - Env.screenHeight()/2);
    return isTouched(xx, yy, width*mScreenScale, height*mScreenScale);
    
  } // isTouchedPix()

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
  
  // magnification factor for the game screen
  public void setScreenScaleFactor(int scale) { mScreenScale = scale; }

  // enable on-screen buttons with target size (zero for no buttons)
  public void useButtons(int pixelWidth) {

    if ( pixelWidth > 0 ) {
      // enable
      if ( mKeyButtons == null ) mKeyButtons = new KeyButtons();
      mKeyButtons.setRefSize(pixelWidth);
    } else {
      // disable
      if ( mKeyButtons != null ) {
        mKeyButtons.dispose();
        mKeyButtons = null;
      }
    }
    
  } // useButtons()
  
  // whether on-screen buttons are enabled
  @Override
  public boolean usingButtons() { return (mKeyButtons != null); }
  
  // set details of the on-screen buttons
  @Override
  public void setButtonDetails(int arrowStyle, int fireStyle) {
    
    if ( mKeyButtons != null ) {
      mKeyButtons.setDetails(arrowStyle, fireStyle);
    }
    
  } // KeyMonitor.setButtonDetails()
  
  // number of pixels needed for buttons on left/right side of screen
  @Override
  public int buttonsXSize() { 
    
    return ( (mKeyButtons!=null) ? mKeyButtons.marginXSize() : 0 );
    
  } // buttonsXSize()
  
  // number of pixels needed for buttons at bottom of screen
  @Override
  public int buttonsYSize() {
    
    return ( (mKeyButtons!=null) ? mKeyButtons.marginYSize() : 0 );
    
  } // buttonsYSize()
  
  // draw the on-screen buttons, if used
  @Override
  public void displayButtons(SpriteBatch spriteBatch) {

    if ( mKeyButtons != null && mMode == MODE_GAME ) {
      mKeyButtons.display(spriteBatch);
    }
    
  } // displayButtons()
  
} // class KeyMonitorAndroid
