/*
 *  TouchArrows.java
 *  Copyright Simon Hern 2012
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// animated arrows to illustrate touch controls (Android only)
public class TouchArrows extends Sprite {

  // details for basic move image (NW arrow)
  private static final int kMoveWidth   = 7,
                           kMoveHeight  = 5;
  private static final int kMoveRefXPos = 1,
                           kMoveRefYPos = 1;

  // pixel data for basic move image (NW arrow)
  private static final String kMovePixels = "000000 " 
                                          + "011110 "
                                          + "0011000"
                                          + " 010110"
                                          + " 000000";
  
  // image objects for the different move arrows (NW, NE, SW, SE)
  private static EgaImage kMoveImages[];

  // details for basic fire image (left arrow)
  private static final int kFireWidth   = 6,
                           kFireHeight  = 7;
  private static final int kFireRefXPos = 1,
                           kFireRefYPos = 3;

  // pixel data for basic fire image (left arrow)
  private static final String kFirePixels = "  000 " 
                                          + " 0010 "
                                          + "001000"
                                          + "011110"
                                          + "001000"
                                          + " 0010 "
                                          + "  000 ";
  
  // image objects for the different fire arrows (left, right)
  private static EgaImage kFireImages[];

  // colours for the arrows
  private static final char kColourMap[] = { '0', 'x' };
  
  // time for one period of animation
  private static final int kPeriod = 18;
  
  // which type of arrows (1 -> move, 2 -> fire)
  private int mType;
  
  // timer for animation
  private int mTimer;
  
  // offset amount for images
  private int mOffset;
  
  // prepare images
  public static void initialize() {
    
    if ( kMoveImages != null ) return;
    
    kMoveImages = new EgaImage[4];

    String nwPixels = EgaTools.convertColours(kMovePixels, kColourMap);
    kMoveImages[0] = new EgaImage(kMoveRefXPos, kMoveRefYPos,
                                  kMoveWidth, kMoveHeight,
                                  nwPixels, 0.0f);
    kMoveImages[1] = EgaTools.reflectX(kMoveImages[0], 
                                       kMoveWidth-kMoveRefXPos-1);
    kMoveImages[2] = EgaTools.reflectY(kMoveImages[0], 
                                       kMoveHeight-kMoveRefYPos-1);
    kMoveImages[3] = EgaTools.reflectX(kMoveImages[2], 
                                       kMoveWidth-kMoveRefXPos-1);
    
    kFireImages = new EgaImage[2];
    
    String leftPixels = EgaTools.convertColours(kFirePixels, kColourMap);
    kFireImages[0] = new EgaImage(kFireRefXPos, kFireRefYPos,
                                  kFireWidth, kFireHeight,
                                  leftPixels, 0.0f);
    kFireImages[1] = EgaTools.reflectX(kFireImages[0], 
                                       kFireWidth-kFireRefXPos-1);
    
  } // initialize()
  
  // constructor
  public TouchArrows(int type) {
  
    initialize();
    
    assert( type >= 1 && type <= 2 );
    mType = type;
    
    mTimer = 0;
    mOffset = 0;
    
  } // constructor
  
  // animate the arrows
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {

    if ( ++mTimer > kPeriod ) mTimer = 0;
    mOffset = (mTimer < kPeriod/2) ? 0 : 1;
    
  } // Sprite.advance()

  // display the arrows
  @Override
  public void draw(EgaCanvas canvas) {

    if ( mType == 1 ) {
      
      kMoveImages[0].draw(canvas, 
                          2-mOffset, 
                          2-mOffset);
      kMoveImages[1].draw(canvas, 
                          Env.screenWidth()-3+mOffset, 
                          2-mOffset);
      kMoveImages[2].draw(canvas, 
                          2-mOffset, 
                          Env.screenHeight()-3+mOffset);
      kMoveImages[3].draw(canvas, 
                          Env.screenWidth()-3+mOffset, 
                          Env.screenHeight()-3+mOffset);
      
    } else {
      
      kFireImages[0].draw(canvas, 
                          2-mOffset, 
                          15);
      kFireImages[1].draw(canvas, 
                          Env.screenWidth()-3+mOffset, 
                          15);

    }
    
  } // Sprite.draw()

} // class TouchArrows
