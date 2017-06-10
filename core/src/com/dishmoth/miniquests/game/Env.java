/*
 *  Env.java
 *  Copyright Simon Hern 2010
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.*;

// some global variables and functions
public class Env {

  // enumeration of directions
  static public final int NONE  = -1,
                          RIGHT = 0, // x positive (NE) 
                          UP    = 1, // y positive (NW)
                          LEFT  = 2, // x negative (SW)
                          DOWN  = 3; // y negative (SE)
  
  // unit steps corresponding to each of the directions above
  static public final int STEP_X[] = { +1, 0, -1, 0 },
                          STEP_Y[] = { 0, +1, 0, -1 };

  // different platforms we might be running on
  public enum Platform { APP,     // pure java (non-libgdx) application 
                         DESKTOP, // libgdx, PC application
                         ANDROID, // libgdx, Android phone or tablet
                         OUYA,    // libgdx, Android console
                         HTML };  // HTML5/WebGL through GWT
  
  // whether to display debug messages, timing statistics, etc.
  static private boolean kDebugMode = true;
  
  // size of the canvas (pixels)
  static private final int kScreenWidth  = 40,
                           kScreenHeight = 30;
  
  // pixel coordinates of the origin of our isometric coordinates
  static private final int kOriginXPixel = kScreenWidth/2 - 1,
                           kOriginYPixel = kScreenHeight - 2;

  // frame rate
  static private final int kTicksPerSecond = 30;

  // assorted helper objects
  static private Random      kRandom;
  static private EnvBits     kEnvBits;
  static private KeyMonitor  kKeyMonitor;
  static private Resources   kResources;
  static private Sounds      kSounds;
  static private SaveState   kSaveState;
  static private ScreenScale kScreenScale;
  
  // this sets up a global Env for the application
  static public void initialize(EnvBits envBits,
                                Resources resources, 
                                Sounds sounds) {
  
    kRandom      = new Random();
    kEnvBits     = envBits;
    kKeyMonitor  = null;
    kResources   = resources;
    kSounds      = sounds;
    kSaveState   = new SaveState();
    kScreenScale = new ScreenScale();
    
    kEnvBits.initialize();
    kSounds.initialize();
    
  } // initialize()

  // add the key/mouse/etc controller after initialization
  static public void addKeyMonitor(KeyMonitor keyMonitor) {
    
    assert( kKeyMonitor == null && keyMonitor != null );
    kKeyMonitor = keyMonitor;
    
  } // addKeyMonitor()
  
  // replace the default screen-scale controller after initialization
  static public void setScreenScale(ScreenScale scale) {
    
    assert( scale != null );
    kScreenScale = scale;
    
  } // setScreenScale()
  
  // discard resources
  static public void dispose() {
    
    kRandom = null;
    kKeyMonitor = null;
    kResources = null;
    kSounds = null;
    
  } // dispose()

  // which device we're running on
  static public Platform platform() { return kEnvBits.platform(); }
  
  // size of screen (pixels)
  static public int screenWidth() { return kScreenWidth; }
  static public int screenHeight() { return kScreenHeight; }
  
  // pixel origin of isometric coordinates
  static public int originXPixel() { return kOriginXPixel; }
  static public int originYPixel() { return kOriginYPixel; }

  // colour for border and unused pixels 
  static public boolean whiteBackground() { 
  
    return ( Env.platform() == Env.Platform.DESKTOP
          || Env.platform() == Env.Platform.HTML 
          || Env.platform() == Env.Platform.APP );
    
  } // whiteBackground()
  
  // frame rate
  static public int ticksPerSecond() { return kTicksPerSecond; }
  
  // display debug text
  static public boolean debugMode() { return kDebugMode; }
  static public void debug(String s) { if (kDebugMode) kEnvBits.debug(s); }
  
  // terminate the game
  static public void exit() { kEnvBits.exit(); }
  
  // save some game data
  static public void save(byte data[]) { kEnvBits.save(data); }
  
  // load the game data
  static public byte[] load() { return kEnvBits.load(); }

  // return reference to keyboard and mouse monitors
  static public KeyMonitor keys()  { return kKeyMonitor; }
  
  // return reference to game resources
  static public Resources resources() { return kResources; }
  
  // return reference to game audio
  static public Sounds sounds() { return kSounds; }

  // return reference to game's save state
  static public SaveState saveState() { return kSaveState; }

  // return reference to game's screen scale helper
  static public ScreenScale screenScale() { return kScreenScale; }
  
  // send a log message back to HQ (for beta testing only)
  static public void report(String message) {
    //kEnvBits.report("http://dishmoth.com/log.html", message);
  }
  
  // assorted functions for returning random numbers
  static public float randomFloat() { // in range [0,1]
    return kRandom.nextFloat(); 
  } // randomFloat()
  static public float randomFloat(float a, float b) { // in range [a,b] 
    return ( a + (b-a)*kRandom.nextFloat() ); 
  } // randomFloat()
  static public double randomDouble() { // in range [0,1]
    return kRandom.nextDouble(); 
  } // randomDouble()
  static public double randomDouble(double a, double b) { // in range [a,b] 
    return ( a + (b-a)*kRandom.nextDouble() ); 
  } // randomDouble()
  static public int randomInt(int n) { // in range [0,n-1] 
    return kRandom.nextInt(n); 
  } // randomInt()
  static public int randomInt(int a, int b) { // in range [a,b]
    if ( a > b )      return ( b + kRandom.nextInt(a-b+1) );
    else if ( a < b ) return ( a + kRandom.nextInt(b-a+1) );
    else              return a;
  } // randomInt()
  static public boolean randomBoolean() { 
    return kRandom.nextBoolean(); 
  } // randomBoolean()

  // assorted modulo-type functions
  static public int fold(int a, int b) {
    // result is between 0 and (b-1)
    if ( a >= 0 ) return (a%b);
    else {
      int temp = b + (a%b);
      return ( (temp==b) ? 0 : temp );
    }
  } // fold()
  static public double fold(double a, double b) {
    // result is in interval [0,b)
    // (probably a more efficient way of doing this?)
    return ( a - b*Math.floor(a/b) );
  } // fold()
  static public float fold(float a, float b) {
    // result is in interval [0,b)
    // (probably a more efficient way of doing this?)
    return ( a - b*(float)Math.floor(a/b) );
  } // fold()
  
  // more modulo-type functions
  static public float foldNearTo(float a, float target, float modSize) {
    return ( target + fold(a-target+0.5f*modSize, modSize) - 0.5f*modSize );
  } // foldNearTo()

  // assorted functions for copying arrays (can't use Arrays.copyOf due to GWT)
  static public byte[] copyOf(byte array[]) {
    
    if ( array == null ) return null;
    byte copy[] = new byte[array.length];
    for ( int k = 0 ; k < array.length ; k++ ) copy[k] = array[k];
    return copy;
    
  } // copyOf(byte[])
  static public int[] copyOf(int array[]) {
    
    if ( array == null ) return null;
    int copy[] = new int[array.length];
    for ( int k = 0 ; k < array.length ; k++ ) copy[k] = array[k];
    return copy;
    
  } // copyOf(int[])
  static public float[] copyOf(float array[]) {
    
    if ( array == null ) return null;
    float copy[] = new float[array.length];
    for ( int k = 0 ; k < array.length ; k++ ) copy[k] = array[k];
    return copy;
    
  } // copyOf(float[])
  static public String[] copyOf(String array[]) {
    
    if ( array == null ) return null;
    String copy[] = new String[array.length];
    for ( int k = 0 ; k < array.length ; k++ ) copy[k] = array[k];
    return copy;
    
  } // copyOf(String[])
  
} // class Env
