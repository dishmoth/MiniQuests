/*
 *  MainGame.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
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
import com.dishmoth.miniquests.game.StartupStory;
import com.dishmoth.miniquests.game.TinyStory;
import com.dishmoth.miniquests.game.TitleStory;
import com.dishmoth.miniquests.gdx.EnvBitsGdx;
import com.dishmoth.miniquests.gdx.KeyMonitorAndroid;
import com.dishmoth.miniquests.gdx.KeyMonitorDesktop;
import com.dishmoth.miniquests.gdx.KeyMonitorGdx;
import com.dishmoth.miniquests.gdx.KeyMonitorOuya;
import com.dishmoth.miniquests.gdx.ResourcesGdx;
import com.dishmoth.miniquests.gdx.SoundsGdx;

// libgdx application wrapper for the game
public class MainGame implements ApplicationListener {

  // target size for game screen relative to full display
  private static final float kShrinkTouchscreen = 0.75f,
                             kShrinkTelevision  = 0.85f;
  
  // how big the screen needs to be (centimetres) for on-screen buttons
  private static final float kBigScreenXCm = 13.0f,
                             kBigScreenYCm = 7.0f;
  private static final int   kSafePixPerCm = 20;
  
  // size of the on-screen buttons (centimetres)
  private static final float kButtonsCmMin  = 1.9f,
                             kButtonsCmMax  = 2.5f;
  private static final float kScreenCmSmall = 17.5f,
                             kScreenCmBig   = 25.0f;
  
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
      envBits.debug("Mini Quests (v4.0.0, 29th November 2014)");
      envBits.debug("Contact: dishmoth@yahoo.co.uk, www.dishmoth.com");
      envBits.debug("");
 
      envBits.debug("ApplicationListener.create()");      
    }

    Env.initialize( envBits,
                    new ResourcesGdx(),
                    new SoundsGdx() );
    //envBits.setPlatform( Env.Platform.ANDROID ); //!!!
    
    Env.addKeyMonitor( 
          (Env.platform() == Env.Platform.ANDROID) ? new KeyMonitorAndroid()
        : (Env.platform() == Env.Platform.OUYA)    ? new KeyMonitorOuya()
                                                   : new KeyMonitorDesktop() );

    // enable on-screen buttons (if screen is big enough)
    if ( Gdx.graphics.getPpcX() > kSafePixPerCm &&
         Gdx.graphics.getPpcY() > kSafePixPerCm &&
         Env.platform() == Env.Platform.ANDROID ) {
      float xcm = Gdx.graphics.getWidth() / Gdx.graphics.getPpcX(),
            ycm = Gdx.graphics.getHeight() / Gdx.graphics.getPpcY();
      float diag = (float)Math.sqrt(xcm*xcm + ycm*ycm);
      Env.debug("Screen size: " + xcm + " x " + ycm 
                + " cm (diag " + diag + " cm)");
  
      if ( xcm > kBigScreenXCm && ycm > kBigScreenYCm ) {
        Env.debug("Enabling on-screen buttons");
        float h = (diag - kScreenCmSmall)/(kScreenCmBig - kScreenCmSmall);
        h = Math.max(0.0f, Math.min(1.0f, h));
        float buttonsCm = h*kButtonsCmMax + (1-h)*kButtonsCmMin;
        int buttonsPix = Math.round( buttonsCm*Gdx.graphics.getPpcX() );
        buttonsPix = Math.min( buttonsPix, Gdx.graphics.getWidth()/5 );
        ((KeyMonitorGdx)Env.keys()).useButtons(buttonsPix);
      }
    }
    
    // enable physical controllers (if any)
    ((KeyMonitorGdx)Env.keys()).useControllers();
    
    Env.saveState().load();
    
    mGameScreen = new EgaCanvas(Env.screenWidth(), Env.screenHeight());
    
    mGameManager = new GameManager(new TitleStory());
    //mGameManager = new GameManager(new StartupStory());
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

    Env.debug("ApplicationListener.resize()");
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
      Gdx.gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, 
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

    final float shrink =
                    (Env.platform()==Env.Platform.ANDROID) ? kShrinkTouchscreen
                  : (Env.platform()==Env.Platform.OUYA)    ? kShrinkTelevision
                                                           : 1.0f;

    KeyMonitorGdx keyMonitor = (KeyMonitorGdx)Env.keys();
    
    int xScale = (int)Math.floor( shrink * Gdx.graphics.getWidth() 
                                  / (float)Env.screenWidth() ),
        yScale = (int)Math.floor( shrink * Gdx.graphics.getHeight() 
                                  / (float)Env.screenHeight() );
    int scale = Math.min(xScale, yScale);
    
    if ( keyMonitor.usingButtons() ) {
      int maxWidth  = Gdx.graphics.getWidth() - 2*keyMonitor.buttonsXSize(),
          maxHeight = Gdx.graphics.getHeight() - 2*keyMonitor.buttonsYSize();
      maxWidth = Math.max(maxWidth, Gdx.graphics.getWidth()/3);
      maxHeight = Math.max(maxHeight, Gdx.graphics.getHeight()/3);
      int xScale2 = (int)Math.floor( maxWidth / (float)Env.screenWidth() ),
          yScale2 = (int)Math.floor( maxHeight / (float)Env.screenHeight() );
      scale = Math.min(scale, Math.max(xScale2, yScale2));
    }

    scale = Math.max(1, scale);
    keyMonitor.setScreenScaleFactor(scale);
    
    int xSize   = scale*Env.screenWidth(), 
        ySize   = scale*Env.screenHeight();
    int xOffset = (Gdx.graphics.getWidth() - xSize)/2,
        yOffset = (Gdx.graphics.getHeight() - ySize)/2;

    float clear = Env.whiteBackground() ? 1.0f : 0.0f;
    Gdx.gl.glClearColor(clear, clear, clear, 1.0f);
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    mScreenBatch.disableBlending();
    mScreenBatch.begin();
    ((KeyMonitorGdx)Env.keys()).displayButtons(mScreenBatch);
    mScreenBatch.draw(mScreenTexture, 
                      xOffset, yOffset, xSize, ySize,
                      0, 0, Env.screenWidth(), Env.screenHeight(), 
                      false, false);
    mScreenBatch.end();
    
  } // drawGameScreen()
  
} // class MainGame
