/*
 *  MainApplet.java
 *  Copyright Simon Hern 2010
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import com.dishmoth.miniquests.game.EgaCanvas;
import com.dishmoth.miniquests.game.EgaImage;
import com.dishmoth.miniquests.game.EgaTools;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.GameManager;
import com.dishmoth.miniquests.game.MapStory;
import com.dishmoth.miniquests.game.QuestStats;
import com.dishmoth.miniquests.game.TinyStory;
import com.dishmoth.miniquests.game.TitleStory;

// the game applet
public class MainApplet extends Applet 
                        implements Runnable, FocusListener, MouseListener {

  private static final long serialVersionUID = 1L;
  
  // assorted objects
  private Thread           mMainLoop       = null; 
  private Canvas           mGameCanvas     = null;
  private BufferStrategy   mBufferStrategy = null;
  private GameManager      mGameManager    = null;
  private EgaCanvas        mGameScreen     = null;
  private TimingControl    mTimingControl  = null;
  private volatile boolean mPaused         = false;
  private boolean          mPauseImageDone = false;
  private EgaImage         mPauseScreen    = null;
  private RecolourTool     mRecolourTool   = null;

  // EGA data converted to pixels
  private BufferedImage mImage;
  
  // constructor
  public MainApplet() {
    
    setLayout(new BorderLayout());
    
  } // constructor

  // initialize the applet
  @Override
  public void init() {

    mGameCanvas = new Canvas();
    mGameCanvas.setFocusable(true);
    mGameCanvas.setIgnoreRepaint(true);
    mGameCanvas.addFocusListener(this);
    mGameCanvas.addMouseListener(this);

    EnvBitsApplet envBits = new EnvBitsApplet(this); 
    if ( Env.debugMode() ) {
      envBits.debug("Mini Quests (v3.1.2, 2nd September 2013)");
      envBits.debug("Contact: dishmoth@yahoo.co.uk, www.dishmoth.com");
      envBits.debug("");
      
      envBits.debug("Applet.init()");
    }
    
    Env.initialize( envBits,
                    new ResourcesApplet(),
                    new SoundsApplet() );
    Env.addKeyMonitor( new KeyMonitorApplet(mGameCanvas) );

    //QuestStats.decode("GXq5MkuCSI0JY16QeCCfkaIcqBOjwRUq2ZavAlg0EWm7KcsCQkyRW14OcHAdiBGao7MhxiSo0HZv6Oe0CYk7ImqFOhwJUp2Qbx8Vg0Edm6KhsAQpyKWu5Ec1Bfi6GZoDNguISj0PYu6TeyCakkIhq8OmwXUr2Naz8dg6GQnDKMsFQlyQcj4XcOAci3Gdo8MiuZSn0TYs6Qe7CXk4IuqKO4wRUz2YaC8diPFkm4KpsHQwyOW");

    Env.saveState().load();
    //Env.saveState().load("000000900083D3FFA0"); // <- dragon
    //Env.saveState().load("010030"); // <- done dungeon C
    
    add(mGameCanvas, BorderLayout.CENTER);

    mGameScreen = new EgaCanvas(Env.screenWidth(), Env.screenHeight());
    
    mGameManager = new GameManager(new TitleStory());
    //mGameManager = new GameManager(new StartupStory());
    //mGameManager = new GameManager(new TrainingStory());
    //mGameManager = new GameManager(new MapStory(-1));
    //mGameManager = new GameManager(new TinyStory(2));
    mGameManager.advance();
    
    mTimingControl = new TimingControl();
    
    mPaused = true;
    mPauseImageDone = false;
    
    mPauseScreen = Env.resources().loadEgaImage("PauseScreen.png");

    //if ( Env.debugMode() ) mRecolourTool = new RecolourTool(mGameCanvas);
    
    Env.debug("Screen size: " + getWidth() + " x " + getHeight());
    
    mMainLoop = new Thread(this);
    mMainLoop.start();

  } // Applet.init()
  
  // start running
  @Override
  public void start() {

    Env.debug("Applet.start()");
    //mPaused = false;
    
  } // Applet.start()
  
  // stop running (pause)
  @Override
  public void stop() {

    if ( mPaused ) Env.debug("Applet.stop()");
    else           Env.debug("Applet.stop() -> pausing");
    mPaused = true;

    Env.saveState().save();
    
  } // Applet.stop()

  // discard resources and exit
  @Override
  public void destroy() {
    
    Env.debug("Applet.destroy()");
    
    Env.saveState().save();
    
    Env.sounds().stopAll();

    super.destroy();
    
    Thread thread = mMainLoop;
    mMainLoop = null;
    if ( thread != null ) {
      try {
        thread.join();
      } catch (InterruptedException e) {
      }
    }
    
  } // Applet.destroy()
  
  // change of focus
  public void focusGained(FocusEvent event) { 

    Env.debug("Focus gained");
    start(); 
  
  } // FocusListener.focusGained()
  
  public void focusLost(FocusEvent event) { 
    
    Env.debug("Focus lost");
    stop(); 
    
  } // FocusListener.focusLost()

  // click in window for focus
  public void mouseClicked(MouseEvent e) {
    
    if ( mPaused ) Env.debug("Applet clicked -> un-pausing");
    else           Env.debug("Applet clicked");
    mPaused = false;
    mGameCanvas.requestFocusInWindow();
    
  } // MouseListener.mouseClicked()

  // MouseListener interface
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}
  public void mousePressed(MouseEvent e) {}
  public void mouseReleased(MouseEvent e) {}
 
  // execute the game
  public void run() {
    
    while ( mMainLoop != null ) {
      if ( mPaused ) runPaused();
      else           runMain();
    }
    
  } // Runnable.run()

  // run the main game loop
  private void runMain() {
    
    Env.debug("Running");
    Env.keys().reset();
    mTimingControl.reset();
    
    while ( mMainLoop != null && !mPaused ) {
    
      // advance
      mGameManager.advance();
      long nanosAfterAdvance = System.nanoTime();

      // draw
      boolean skipDraw = mTimingControl.gameRunningSlow();
      if ( !skipDraw ) drawGameScreen();
      long nanosAfterDraw = System.nanoTime();

      // wait
      mTimingControl.tick(nanosAfterAdvance, nanosAfterDraw, skipDraw);
        
    }
    
    mTimingControl.report();
    
  } // runMain()
  
  // run the paused loop
  private void runPaused() {

    Env.debug("Paused");

    Env.sounds().stopAll();
    
    while ( mMainLoop != null && mPaused ) {
      try { Thread.sleep(250); } catch (InterruptedException ex) { return; }
      drawGameScreen();
    }
    
  } // runPaused()
  
  // draw the game screen
  private void drawGameScreen() {

    if ( mBufferStrategy == null ) {
      mGameCanvas.createBufferStrategy(2);
      mBufferStrategy = mGameCanvas.getBufferStrategy();
    }

    if ( mPaused ) {
      if ( !mPauseImageDone ) {
        mGameScreen.clear();
        mGameManager.draw(mGameScreen);
        EgaTools.fadeImage(mGameScreen);
        EgaTools.limitColours(mGameScreen, 14);
        mPauseScreen.draw(mGameScreen, 0, 0);
        mPauseImageDone = true;
      }
    } else {
      mGameScreen.clear();
      mGameManager.draw(mGameScreen);
      mPauseImageDone = false;
    }
    
    if ( mRecolourTool != null ) mRecolourTool.recolour(mGameScreen);
    
    Graphics g = mBufferStrategy.getDrawGraphics();
    assert( g instanceof Graphics2D );

    final float wScale  = getWidth() / (float)Env.screenWidth(),
                hScale  = getHeight() / (float)Env.screenHeight();
    final int   iwScale = (int)Math.floor(wScale + 0.1f),
                ihScale = (int)Math.floor(hScale + 0.1f);
    final int   scale   = Math.max(1, Math.min(iwScale, ihScale));
    
    drawEgaCanvas((Graphics2D)g, scale);
    
    g.dispose();
    if ( !mBufferStrategy.contentsLost() ) mBufferStrategy.show();
    
    if ( Env.debugMode() ) mGameScreen.checkColourCount();
    
  } // drawGameScreen()

  // display the EGA pixels
  private void drawEgaCanvas(Graphics2D g2, int scale) {
    
    if ( mImage == null ) {
      mImage = new BufferedImage(Env.screenWidth(), Env.screenHeight(), 
                                 BufferedImage.TYPE_INT_RGB);
    }

    DataBufferInt buffer = (DataBufferInt)mImage.getRaster().getDataBuffer();
    int rgbPixels[] = buffer.getData();
    
    byte pixels[] = mGameScreen.pixels();
    assert( rgbPixels.length == pixels.length );
    
    for ( int k = 0 ; k < pixels.length ; k++ ) {
      rgbPixels[k] = EgaTools.EGA_PALETTE[ pixels[k] ];
    }
    
    g2.drawImage(mImage, 
                 0, 0, scale*Env.screenWidth(), scale*Env.screenHeight(), 
                 null);

  } // drawEgaCanvas()
  
} // class MainApplet
