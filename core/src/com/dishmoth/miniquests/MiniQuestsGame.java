/*
 *  MiniQuestsGame.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.dishmoth.miniquests.game.EgaCanvas;
import com.dishmoth.miniquests.game.EgaTools;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.GameManager;
import com.dishmoth.miniquests.game.MapStory;
import com.dishmoth.miniquests.game.MenuStory;
import com.dishmoth.miniquests.game.TinyStory;
import com.dishmoth.miniquests.game.TitleStory;
import com.dishmoth.miniquests.gdx.EnvBitsGdx;
import com.dishmoth.miniquests.gdx.KeyMonitorAndroid;
import com.dishmoth.miniquests.gdx.KeyMonitorDesktop;
import com.dishmoth.miniquests.gdx.KeyMonitorGdx;
import com.dishmoth.miniquests.gdx.KeyMonitorOuya;
import com.dishmoth.miniquests.gdx.ResourcesGdx;
import com.dishmoth.miniquests.gdx.ScreenScaleAndroid;
import com.dishmoth.miniquests.gdx.SoundsGdx;

// libgdx application wrapper for the game
public class MiniQuestsGame extends ApplicationAdapter {

  // assorted objects
  private GameManager mGameManager   = null;
  private EgaCanvas   mGameScreen    = null;
  private SpriteBatch mScreenBatch   = null;
  private Pixmap      mScreenPixmap  = null;
  private Texture     mScreenTexture = null;

  // seconds since the last advance
  private double mTimeSince;
  
  // called when the application is first created
  @Override
  public void create() {

    EnvBitsGdx envBits = new EnvBitsGdx();
    if ( Env.debugMode() ) {
      envBits.debug("Mini Quests (v4.0.1, 17th February 2015)");
      envBits.debug("Contact: dishmoth@yahoo.co.uk, www.dishmoth.com");
      envBits.debug("");
 
      envBits.debug("ApplicationListener.create()");      
    }

    Env.initialize( envBits,
                    new ResourcesGdx(),
                    new SoundsGdx() );
    //envBits.setPlatform( Env.Platform.ANDROID ); //!!!
    
    Env.saveState().load();
    
    Env.addKeyMonitor( 
          (Env.platform() == Env.Platform.ANDROID) ? new KeyMonitorAndroid()
        : (Env.platform() == Env.Platform.IOS)     ? new KeyMonitorAndroid()
        : (Env.platform() == Env.Platform.OUYA)    ? new KeyMonitorOuya()
                                                   : new KeyMonitorDesktop() );

    if ( Env.platform() == Env.Platform.ANDROID ||
         Env.platform() == Env.Platform.IOS ||
         Env.platform() == Env.Platform.OUYA ) {
      Env.setScreenScale( new ScreenScaleAndroid(Gdx.graphics.getWidth(),
                                                 Gdx.graphics.getHeight()) );
    }
    
    // enable physical controllers (if any)
    ((KeyMonitorGdx)Env.keys()).useControllers();
    
    mGameScreen = new EgaCanvas(Env.screenWidth(), Env.screenHeight());
    
    mGameManager = new GameManager(new TitleStory());
    //mGameManager = new GameManager(new MenuStory());
    //mGameManager = new GameManager(new TrainingStory());
    //mGameManager = new GameManager(new MapStory(-1));
    //mGameManager = new GameManager(new TinyStory(3));
    mGameManager.advance();
    
    mScreenBatch = new SpriteBatch();
    mScreenPixmap = new Pixmap(MathUtils.nextPowerOfTwo(Env.screenWidth()), 
                               MathUtils.nextPowerOfTwo(Env.screenHeight()),
                               Format.RGB888);

    mTimeSince = 0.0;
    
  } // ApplicationListener.create()

  // called when the application is resized
  @Override
  public void resize(int width, int height) {

    Env.debug("ApplicationListener.resize( " + width + " x " + height + " )");
    Env.screenScale().refresh(width, height);
    mScreenBatch = new SpriteBatch();
    
  } // ApplicationListener.resize()

  // called when the application is paused
  @Override
  public void pause() {

    Env.debug("ApplicationListener.pause()");
    
    Env.saveState().save();
    
    mScreenTexture.dispose();
    mScreenTexture = null;
    
  } // ApplicationListener.pause()

  // called when the application is resumed from a paused state
  @Override
  public void resume() {

    Env.debug("ApplicationListener.resume()");
    
  } // ApplicationListener.resume()

  // called when the application is destroyed
  @Override
  public void dispose() {

    Env.debug("ApplicationListener.dispose()");
    
    mScreenBatch.dispose();
    mScreenPixmap.dispose();
    Env.dispose();

  } // ApplicationListener.dispose()

  // called during the game loop
  @Override
  public void render() {

    double dt = Gdx.graphics.getDeltaTime();
    dt = Math.min(dt, 0.1);
    mTimeSince += dt;

    boolean screenChanged = false;
    while ( mTimeSince > 1.0/Env.ticksPerSecond() ) {
      mGameManager.advance();
      mTimeSince -= 1.0/Env.ticksPerSecond();
      screenChanged = true;
    }
    
    if ( screenChanged || mScreenTexture == null ) {
      mGameScreen.clear();
      mGameManager.draw(mGameScreen);
      drawScreenToTexture();
      if ( Env.debugMode() ) mGameScreen.checkColourCount();
    }
    
    drawGameScreen();

  } // ApplicationListener.render()

  // draw the game screen
  private void drawScreenToTexture() {

    int pixIndex = 0;
    byte pixels[] = mGameScreen.pixels();
    for ( int iy = 0 ; iy < Env.screenHeight() ; iy++ ) {
      for ( int ix = 0 ; ix < Env.screenWidth() ; ix++ ) {
        int col = EgaTools.EGA_PALETTE[ pixels[pixIndex++] ];
        int r = (col >> 16) & 0xFF,
            g = (col >>  8) & 0xFF,
            b = (col      ) & 0xFF;
        mScreenPixmap.setColor(r/255.0f, g/255.0f, b/255.0f, 1.0f);
        mScreenPixmap.drawPixel(ix, iy);
      }
    }

    if ( mScreenTexture == null ) {
      mScreenTexture = new Texture( mScreenPixmap.getWidth(), 
                                    mScreenPixmap.getHeight(),
                                    Format.RGB888 );
    }

    if ( Env.platform() == Env.Platform.HTML ) {
      // Texture.draw() uses glTexSubImage2D(), which won't run in WebGL
      // for some reason, so we hack around it instead
      mScreenTexture.bind();
      Gdx.gl.glTexImage2D(GL20.GL_TEXTURE_2D, 0, 
                          mScreenPixmap.getGLInternalFormat(), 
                          mScreenPixmap.getWidth(), mScreenPixmap.getHeight(), 
                          0,
                          mScreenPixmap.getGLFormat(), 
                          mScreenPixmap.getGLType(), 
                          mScreenPixmap.getPixels());
    } else {
      mScreenTexture.draw(mScreenPixmap, 0, 0);
    }
  
  } // drawScreenToTexture()
    
  // draw the game screen
  private void drawGameScreen() {

    final int scale = Env.screenScale().scale();

    int xSize   = scale*Env.screenWidth(), 
        ySize   = scale*Env.screenHeight();
    int xOffset = (Gdx.graphics.getWidth() - xSize)/2,
        yOffset = (Gdx.graphics.getHeight() - ySize)/2;

    float clear = Env.whiteBackground() ? 1.0f : 0.0f;
    Gdx.gl.glClearColor(clear, clear, clear, 1.0f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    mScreenBatch.enableBlending();
    mScreenBatch.begin();
    mScreenBatch.draw(mScreenTexture, 
                      xOffset, yOffset, xSize, ySize,
                      0, 0, Env.screenWidth(), Env.screenHeight(), 
                      false, false);
    ((KeyMonitorGdx)Env.keys()).displayButtons(mScreenBatch);
    mScreenBatch.end();
    
  } // drawGameScreen()
  
} // class MiniQuestsGame
