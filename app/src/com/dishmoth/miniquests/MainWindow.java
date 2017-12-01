/*
 *  MainWindow.java
 *  Copyright Simon Hern 2010
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.BufferStrategy;

import com.dishmoth.miniquests.game.EgaCanvas;
import com.dishmoth.miniquests.game.EgaTools;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.GameManager;
import com.dishmoth.miniquests.game.MapStory;
import com.dishmoth.miniquests.game.MenuStory;
import com.dishmoth.miniquests.game.QuestStats;
import com.dishmoth.miniquests.game.QuestStory;
import com.dishmoth.miniquests.game.TitleStory;

// the main game window (pure Java, non-libGDX version)
public class MainWindow extends Frame implements Runnable {

  // main method
  public static void main(String args[]) {

    new MainWindow();

  } // main()

  private static final long serialVersionUID = 1L;

  // assorted objects
  private Thread         mMainLoop       = null; 
  private Canvas         mGameCanvas     = null;
  private BufferStrategy mBufferStrategy = null;
  private GameManager    mGameManager    = null;
  private TimingControl  mTimingControl  = null;
  private EgaCanvas      mGameScreen     = null;
  private RecolourTool   mRecolourTool   = null;

  // EGA data converted to pixels
  private BufferedImage mImage;
  
  // constructor
  public MainWindow() {

    mGameCanvas = new Canvas();
    Dimension canvasDim = new Dimension(10*Env.screenWidth(), 
                                        10*Env.screenHeight());
    mGameCanvas.setSize(canvasDim);
    mGameCanvas.setPreferredSize(canvasDim);
    
    add(mGameCanvas);
    
    Dimension screenDim =  Toolkit.getDefaultToolkit().getScreenSize();
    setLocation((int)(0.49*(screenDim.width - canvasDim.width)), 
                (int)(0.40*(screenDim.height - canvasDim.height)));
    
    setIgnoreRepaint(true);
    setResizable(true);
    pack();   
    
    EnvBitsApp envBits = new EnvBitsApp(); 
    if ( Env.debugMode() ) {
      envBits.debug("Mini Quests (v4.1.0, 19th November 2017)");
      envBits.debug("Contact: dishmoth@yahoo.co.uk, www.dishmoth.com");
      envBits.debug("");
      
      envBits.debug("MainWindow()");
    }
    Env.initialize( envBits,
                    new ResourcesApp(),
                    new SoundsApp() );
    Env.addKeyMonitor( new KeyMonitorApp(this) );

    //QuestStats.decode("uBVj0NYr6TewCgk5IfqFPuwKUr2Pa48Sg2EWmJKdsCQqySWx4ec4Abi2Gbo6M4uESl0IYA6Qe0CfkEImqEOtwJUp2Ta68TgyErmIKfsERvyUWq4bc1Agi2GcoDMeuHS10bYq6WeCCWk");

    Env.saveState().load();
    //Env.saveState().load("000000900083D3FFA0"); // <- dragon
    //Env.saveState().load("02000030104000644400"); // <- garden 
    //Env.saveState().load("010030"); // <- done dungeon C
    
    addWindowListener(
      new WindowAdapter() {
        public void windowClosing(WindowEvent e) { exit(); }
        public void windowDeiconified(WindowEvent e) { start(); }
        public void windowIconified(WindowEvent e) { stop(); }
      }
    );

    addMouseListener(
      new MouseAdapter() {
        public void mouseClicked(MouseEvent e) { requestFocusInWindow(); }
      }
    );
    
    if ( mGameCanvas != null ) mGameCanvas.addMouseListener(
      new MouseAdapter() {
        public void mouseClicked(MouseEvent e) { requestFocusInWindow(); }
      }
    );
    
    requestFocusInWindow();
    validate();
    setVisible(true);

    mGameScreen = new EgaCanvas(Env.screenWidth(), Env.screenHeight());
    
    mGameManager = new GameManager(new TitleStory());
    //mGameManager = new GameManager(new MenuStory());
    //mGameManager = new GameManager(new TrainingStory());
    //mGameManager = new GameManager(new MapStory(-1));
    //mGameManager = new GameManager(new QuestStory(3));
    mGameManager.advance();
    
    mTimingControl = new TimingControl();
    
    if ( Env.debugMode() ) mRecolourTool = new RecolourTool(mGameCanvas);
    
    Env.debug("Window size: " + getWidth() + " x " + getHeight());
    Env.debug("Screen size: " + mGameCanvas.getWidth() + " x " 
                              + mGameCanvas.getHeight());
    
    prepareBufferStrategy();

    setTitle("Mini Quests");

    start();
        
  } // constructor
  
  // start the game loop in a new thread
  public void start() {

    Env.debug("start()");
    
    if ( mMainLoop == null ) {
      mMainLoop = new Thread(this);
      mMainLoop.start();
    }
    requestFocusInWindow();

  } // start()
  
  // interrupt the game thread
  public void stop() {
    
    Env.debug("stop()");
    
    if ( mMainLoop != null ) {
      mMainLoop.interrupt();
    }
    mMainLoop = null;

    Env.keys().reset();
    Env.saveState().save();
    Env.sounds().stopAll();
    
    mTimingControl.report();
    
  } // stop()

  // end the game
  public void exit() {
 
    Env.debug("exit()");
    stop();
    System.exit(0);
    
  } // exit()
  
  // the game loop (mostly concerned with counting nanoseconds)
  public void run() {

    Env.debug("run()");
    requestFocus();

    Env.keys().reset();
    mTimingControl.reset();

    while ( mMainLoop == Thread.currentThread() ) {
      
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
    
    mMainLoop = null;
    
  } // Runnable.run()

  // black magic to try and get buffer strategy working
  private void prepareBufferStrategy() {
  
    try {
      EventQueue.invokeAndWait(new Runnable() { 
        public void run() {mGameCanvas.createBufferStrategy(2);} 
      });
    } catch (Exception ex) {
      Env.debug("BufferStrategy: " + ex);
    }
    
    try {
      Thread.sleep(500);
    } catch ( InterruptedException ex ) {}

    mBufferStrategy = mGameCanvas.getBufferStrategy();
    
  } // prepareBufferStrategy()
  
  // draw the game image onto the off-screen buffer, then flip to the screen
  private void drawGameScreen() {
    
    mGameScreen.clear();
    mGameManager.draw(mGameScreen);
    
    if ( mRecolourTool != null ) mRecolourTool.recolour(mGameScreen);
    
    Graphics g = mBufferStrategy.getDrawGraphics();
    assert( g instanceof Graphics2D );

    final int width   = mGameCanvas.getWidth(),
              height  = mGameCanvas.getHeight();
    final int scale   = Env.screenScale().scale(width+1, height+1);
    final int xOffset = (width - scale*Env.screenWidth())/2,
              yOffset = (height - scale*Env.screenHeight())/2;

    drawEgaCanvas((Graphics2D)g, scale, xOffset, yOffset);
    
    g.dispose();
    if ( !mBufferStrategy.contentsLost() ) mBufferStrategy.show();
    
    if ( Env.debugMode() ) mGameScreen.checkColourCount();

  } // drawGameScreen()

  // display the EGA pixels
  private void drawEgaCanvas(Graphics2D g2, int scale, int xOffset, int yOffset) {
    
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
                 xOffset, yOffset, 
                 scale*Env.screenWidth(), scale*Env.screenHeight(), 
                 null);

  } // drawEgaCanvas()
  
} // class MainWindow
  