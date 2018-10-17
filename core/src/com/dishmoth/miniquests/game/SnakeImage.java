/*
 *  SnakeImage.java
 *  Copyright (c) 2018 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

// collection of monster images
public class SnakeImage {

  // details of the basic head image
  private static final int   kHeadWidth   = 4,
                             kHeadHeight  = 4;
  private static final int   kHeadRefXPos = 1,
                             kHeadRefYPos = 3;
  private static final float kHeadDepth   = -0.02f;

  // data for the basic head image
  private static final String kHeadPixels[] = { " 00 " // x+ (0)
                                              + "0000"
                                              + "0000"
                                              + " 00 ",
                                             
                                                " 00 " // y+ (1) 
                                              + "0000"
                                              + "0000"
                                              + " 00 ",
                                               
                                                " 00 " // x- (2) 
                                              + "0100"
                                              + "0000"
                                              + " 00 ",
                                               
                                                " 00 " // y- (3)
                                              + "0010"
                                              + "0000"
                                              + " 00 " };
  
  // data for the stepping head image
  private static final String kHeadStepPixels[] = { "    " // x+ (0)
                                                  + "000 "
                                                  + "000 "
                                                  + "000 ",
                                                 
                                                    "    " // y+ (1) 
                                                  + " 000"
                                                  + " 000"
                                                  + " 000",
                                                   
                                                    " 000" // x- (2) 
                                                  + " 010"
                                                  + " 000"
                                                  + "    ",
                                                   
                                                    "000 " // y- (3)
                                                  + "010 "
                                                  + "000 "
                                                  + "    " };
  
  // head image objects 
  private final EgaImage mHeadImages[];
  private final EgaImage mHeadStepImages[];

  // details of the basic body image
  private static final int   kBodyWidth   = 2,
                             kBodyHeight  = 3;
  private static final int   kBodyRefXPos = 0,
                             kBodyRefYPos = 2;
  private static final float kBodyDepth   = -0.02f;

  // data for the basic body image
  private static final String kBodyPixels[] = { "11" // x
                                              + "10"
                                              + "00",
                                              
                                                "11" // y
                                              + "01"
                                              + "00",
                                              
                                                "11" // x/y front
                                              + "00"
                                              + "00",
                                              
                                                "11" // x/y back
                                              + "11"
                                              + "00" };

  // data for the stepping body image
  private static final String kBodyStepPixels[] = { " 1" // x+ (0)
                                                  + " 0"
                                                  + "  ",
                                                  
                                                    "1 " // y+ (1)
                                                  + "0 "
                                                  + "  ",
                                                  
                                                    "  " // x- (2)
                                                  + "1 "
                                                  + "0 ",
                                                  
                                                    "  " // y- (3)
                                                  + " 1"
                                                  + " 0" };

  // body image object 
  private final EgaImage mBodyImages[];
  private final EgaImage mBodyStepImages[];

  // constructor
  public SnakeImage(char colourMap[]) {
   
    mHeadImages = new EgaImage[kHeadPixels.length];
    for ( int k = 0 ; k < kHeadPixels.length ; k++ ) {
      mHeadImages[k] = new EgaImage(kHeadRefXPos, kHeadRefYPos,
                                    kHeadWidth, kHeadHeight,
                                    EgaTools.convertColours(kHeadPixels[k],
                                                            colourMap), 
                                    kHeadDepth);
    }
    
    mHeadStepImages = new EgaImage[kHeadStepPixels.length];
    for ( int k = 0 ; k < kHeadStepPixels.length ; k++ ) {
      mHeadStepImages[k] = new EgaImage(kHeadRefXPos, kHeadRefYPos,
                                        kHeadWidth, kHeadHeight,
                                        EgaTools.convertColours(
                                                        kHeadStepPixels[k],
                                                        colourMap), 
                                        kHeadDepth);
    }
    
    mBodyImages = new EgaImage[kBodyPixels.length];
    for ( int k = 0 ; k < kBodyPixels.length ; k++ ) {
      mBodyImages[k] = new EgaImage(kBodyRefXPos, kBodyRefYPos,
                                    kBodyWidth, kBodyHeight,
                                    EgaTools.convertColours(kBodyPixels[k],
                                                            colourMap), 
                                    kBodyDepth);
    }
    
    mBodyStepImages = new EgaImage[kBodyStepPixels.length];
    for ( int k = 0 ; k < kBodyStepPixels.length ; k++ ) {
      mBodyStepImages[k] = new EgaImage(kBodyRefXPos, kBodyRefYPos,
                                        kBodyWidth, kBodyHeight,
                                        EgaTools.convertColours(
                                                        kBodyStepPixels[k],
                                                        colourMap), 
                                        kBodyDepth);
    }
    
  } // constructor
  
  // display the head
  public void drawHead(EgaCanvas canvas,
                       int xPos, int yPos, int zPos, int direc) {

    assert( direc >= 0 && direc < 4 );
    mHeadImages[direc].draw3D(canvas, 2*xPos, 2*yPos, zPos);
    
  } // drawHead()
  
  // display the head mid-step
  // (position is where the head is moving to)
  public void drawHeadStep(EgaCanvas canvas,
                           int xPos, int yPos, int zPos, int direc) {

    assert( direc >= 0 && direc < 4 );
    mHeadStepImages[direc].draw3D(canvas, 2*xPos, 2*yPos, zPos);
    
  } // drawHeadStep()
  
  // display the body
  public void drawBody(EgaCanvas canvas,
                       int xPos, int yPos, int zPos,
                       int direc, int direcPrev,
                       boolean endTailStep) {

    assert( direc >= 0 && direc < 4 );
    assert( direcPrev >= -1 && direcPrev < 4 );
    
    if ( endTailStep ) {
      assert( direcPrev == Env.NONE );
      mBodyStepImages[direc].draw3D(canvas, 2*xPos, 2*yPos, zPos);
    } else {
      final int indices[][] = { { 0, 1, 0, 2 },   // RIGHT
                                { 0, 1, 2, 1 },   // UP
                                { 0, 3, 0, 0 },   // LEFT
                                { 3, 1, 1, 1 } }; // DOWN
      
      if ( direcPrev == Env.NONE ) direcPrev = (direc + 2) % 4;
      final int index = indices[direc][direcPrev];
      
      mBodyImages[index].draw3D(canvas, 2*xPos, 2*yPos, zPos);
    }
    
  } // drawBody()
  
} // class SnakeImage
