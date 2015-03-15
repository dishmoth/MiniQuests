/*
 *  MapArrow.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// an animated arrow showing directions on the map
public class MapArrow extends Sprite {

  // details for image (right arrow)
  private static final int kWidth   = 5,
                           kHeight  = 7;
  private static final int kRefXPos = 3,
                           kRefYPos = 3;

  // pixel data for image (right arrow)
  private static final String kPixels = " 0   " 
                                      + "010  "
                                      + " 010 "
                                      + "  010"
                                      + " 010 "
                                      + "010  "
                                      + " 0   ";
  
  // colours for the arrow
  private static final char kColourMap[] = { 'c', 'q' };
  
  // image objects for the different directions
  private static EgaImage kImages[];

  // basic arrow positions
  private static final int kXYPos[][] = { { 38, 15 },   // right
                                          { 20,  1 },   // up
                                          {  1, 15 },   // left
                                          { 20, 28 } }; // down
  
  // stages of animation
  private static final int kTimeIn  =  8,
                           kTimeOut = 16;

  // which direction the arrow points in
  private int mDirec;

  // timer for animation
  private int mTimer;
  
  // prepare images
  public static void initialize() {

    if ( kImages != null ) return;
    
    kImages = new EgaImage[4];

    String rightPixels = EgaTools.convertColours(kPixels, kColourMap);
    kImages[Env.RIGHT] = new EgaImage(kRefXPos, kRefYPos,
                                      kWidth, kHeight,
                                      rightPixels, 0.0f);

    String leftPixels = flipLeftRight(rightPixels, kWidth, kHeight);
    kImages[Env.LEFT] = new EgaImage(kWidth-1-kRefXPos, kRefYPos,
                                     kWidth, kHeight,
                                     leftPixels, 0.0f);

    String downPixels = flipDiagonal(rightPixels, kWidth, kHeight);
    kImages[Env.DOWN] = new EgaImage(kRefYPos, kRefXPos,
                                     kHeight, kWidth,
                                     downPixels, 0.0f);

    String upPixels = flipDiagonal(leftPixels, kWidth, kHeight);
    kImages[Env.UP] = new EgaImage(kRefYPos, kWidth-1-kRefXPos,
                                   kHeight, kWidth,
                                   upPixels, 0.0f);

  } // initialize()
  
  // reflect the pixels for an image
  private static String flipLeftRight(String pixels, int width, int height) {
    
    char result[] = new char[pixels.length()];
    for ( int iy = 0 ; iy < height ; iy++ ) {
      for ( int ix = 0 ; ix < width ; ix++ ) {
        int sx = width - 1 - ix,
            sy = iy;
        result[iy*width+ix] = pixels.charAt(sy*width+sx);
      }
    }
    return new String(result);
    
  } // flipLeftRight()
  
  // reflect the pixels for an image
  private static String flipDiagonal(String pixels, int width, int height) {
    
    char result[] = new char[pixels.length()];
    for ( int iy = 0 ; iy < height ; iy++ ) {
      for ( int ix = 0 ; ix < width ; ix++ ) {
        int dx = iy,
            dy = ix;
        result[dy*height+dx] = pixels.charAt(iy*width+ix);
      }
    }
    return new String(result);
    
  } // flipDiagonal()
  
  // constructor
  public MapArrow(int direc) {
    
    initialize();
    
    assert( direc >= 0 && direc < 4 );
    mDirec = direc;

    mTimer = 0;
    
  } // constructor
  
  // animate the arrow
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {

    if ( ++mTimer >= kTimeOut ) mTimer = 0;
    
  } // Sprite.advance()

  // display the arrow
  @Override
  public void draw(EgaCanvas canvas) {

    int delta = ( (mTimer < kTimeIn) ? 0 : 1 );
    
    int x = kXYPos[mDirec][0] + delta*Map.STEP_X[mDirec],
        y = kXYPos[mDirec][1] + delta*Map.STEP_Y[mDirec];
    kImages[mDirec].draw(canvas, x, y, -1.0f);
    
  } // Sprite.draw()

} // class MapArrow
