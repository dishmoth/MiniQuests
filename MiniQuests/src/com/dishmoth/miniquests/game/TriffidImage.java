/*
 *  TriffidImage.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.Arrays;

// collection of turret images
public class TriffidImage {

  // details of the basic image
  private static final int   kWidth      = 4,
                             kHeight     = 6;
  private static final int   kRefXPos    = 1,
                             kRefYPos    = 4;
  private static final float kBasicDepth = -0.02f,
                             kSideDepth  = -0.06f,
                             kFrontDepth = -1.055f;

  // data for the basic rotating image
  private static final String kBasicPixels[] = { "  1 " // x+ (0)
                                               + " 001"
                                               + " 00 "
                                               + " 00 "
                                               + "1001"
                                               + " 11 ",
                                               
                                                 " 1  " // y+ (1) 
                                               + "100 "
                                               + " 00 "
                                               + " 00 "
                                               + "1001"
                                               + " 11 ",
                                               
                                                 " 1  " // x- (2) 
                                               + "101 "
                                               + " 10 "
                                               + " 00 "
                                               + "1001"
                                               + " 11 ",
                                               
                                                 "  1 " // y- (3)
                                               + " 101"
                                               + " 01 "
                                               + " 00 "
                                               + "1001"
                                               + " 11 " };
  
  // basic image objects 
  private final EgaImage mBasicImages[];
  
  // data for the basic rotating image
  private static final String kGrowPixels[] = { "    " // (0)
                                              + "    "
                                              + "    "
                                              + "    "
                                              + " 11 "
                                              + "    ",
                                               
                                                "    " // (1) 
                                              + "    "
                                              + "    "
                                              + " 11 "
                                              + "1001"
                                              + " 11 ",
                                               
                                                "    " // (2) 
                                              + "    "
                                              + "    "
                                              + " 00 "
                                              + "1001"
                                              + " 11 ",
                                               
                                                "    " // (3) 
                                              + "    "
                                              + " 00 "
                                              + " 00 "
                                              + "1001"
                                              + " 11 ",
                                               
                                                "    " // (4) 
                                              + " 00 "
                                              + " 00 "
                                              + " 00 "
                                              + "1001"
                                              + " 11 " };
                                               
  // growing image objects 
  private final EgaImage mGrowImages[];

  // constructor (either colour map may be null)
  public TriffidImage(char colourMap[]) {
    
    assert( colourMap != null && colourMap.length == 2 );
    
    float depths[] = new float[kWidth*kHeight];
    Arrays.fill(depths, kBasicDepth);
    depths[kRefYPos*kWidth]   = kSideDepth;
    depths[kRefYPos*kWidth+3] = kSideDepth;
    depths[(kRefYPos+1)*kWidth+1] = kFrontDepth;
    depths[(kRefYPos+1)*kWidth+2] = kFrontDepth;
    
    mBasicImages = new EgaImage[kBasicPixels.length];
    for ( int k = 0 ; k < kBasicPixels.length ; k++ ) {
      mBasicImages[k] = new EgaImage(kRefXPos, kRefYPos,
                                     kWidth, kHeight,
                                     EgaTools.convertColours(kBasicPixels[k],
                                                             colourMap), 
                                     depths);
    }
  
    mGrowImages = new EgaImage[kGrowPixels.length];
    for ( int k = 0 ; k < kGrowPixels.length ; k++ ) {
      mGrowImages[k] = new EgaImage(kRefXPos, kRefYPos,
                                    kWidth, kHeight,
                                    EgaTools.convertColours(kGrowPixels[k],
                                                            colourMap), 
                                    depths);
    }
    
  } // constructor

  // returns the number of different growth images
  static public int growthStages() { return kGrowPixels.length; }
  
  // display the basic triffid
  public void drawBasic(EgaCanvas canvas,
                        int xPos, int yPos, int zPos, int direc) {

    assert( mBasicImages != null );
    assert( direc >= 0 && direc < 4 );
    mBasicImages[direc].draw3D(canvas, 2*xPos, 2*yPos, zPos);
    
  } // drawBasic()
  
  // display the growing triffid
  public void drawGrowing(EgaCanvas canvas,
                          int xPos, int yPos, int zPos, int stage) {

    assert( mGrowImages != null );
    assert( stage >= 0 && stage < mGrowImages.length );
    mGrowImages[stage].draw3D(canvas, 2*xPos, 2*yPos, zPos);
    
  } // drawGrowing()
  
} // class TriffidImage
