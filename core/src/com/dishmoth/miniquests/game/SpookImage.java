/*
 *  SpookImage.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

// collection of monster images
public class SpookImage {

  // details of the basic standing image
  private static final int   kBasicWidth   = 4,
                             kBasicHeight  = 3;
  private static final int   kBasicRefXPos = 1,
                             kBasicRefYPos = 2;
  private static final float kBasicDepth   = -0.02f;

  // data for the basic standing image
  private static final String kBasicPixels[] = { " 00 " // x+ (0)
                                               + " 001"
                                               + " 00 ",
                                               
                                                 " 00 " // y+ (1) 
                                               + "100 "
                                               + " 00 ",
                                               
                                                 " 00 " // x- (2) 
                                               + "101 "
                                               + " 00 ",
                                               
                                                 " 00 " // y- (3)
                                               + " 101"
                                               + " 00 " };
  
  // basic image objects 
  private final EgaImage mBasicImages[];

  // small version of basic image
  private final EgaImage mBasicSmallImage;
  
  // details of the stepping image
  private static final int   kStepWidth      = 4,
                             kStepHeight     = 4;
  private static final int   kStepRefXPos[]  = { 0, 2, 2, 0 },
                             kStepRefYPos[]  = { 3, 3, 1, 1 };
  private static final float kStepDepths[][] = { { 0, 0,+1,+1},
                                                 {+1,+1, 0, 0},
                                                 {-1,-1,-2, 0},
                                                 { 0,-2,-1,-1} };
  private static final float kStepDepthTweak = 0.04f;

  // data for the stepping images
  private static final String kStepPixels[] = { " 00 " // x+ (0)
                                              + " 001"
                                              + "000 "
                                              + "    ",

                                                " 00 " // y+ (1)
                                              + "100 "
                                              + " 000"
                                              + "    ",

                                                " 00 " // x- (2)
                                              + "1010"
                                              + " 00 "
                                              + "    ",

                                                " 00 " // y- (3)
                                              + "0101"
                                              + " 00 "
                                              + "    " };
  
  // data for the small versions of the stepping images
  private static final String kStepSmallPixels[] = { "    " // x+ (0)
                                                   + " 00 "
                                                   + " 001"
                                                   + "00  ",

                                                     "    " // y+ (1)
                                                   + " 00 "
                                                   + "100 "
                                                   + "  00",

                                                     "    " // x- (2)
                                                   + " 000"
                                                   + "101 "
                                                   + "    ",

                                                     "    " // y- (3)
                                                   + "000 "
                                                   + " 101"
                                                   + "    " };
  
  // stepping image objects 
  private final EgaImage mStepImages[];

  // small stepping image objects 
  private final EgaImage mStepSmallImages[];

  // details of the trail images
  private static final int   kBasicTrailWidth      = 3,
                             kBasicTrailHeight     = 2;
  private static final int   kBasicTrailRefXPos[]  = { 2,-1,-1, 2 },
                             kBasicTrailRefYPos[]  = { 0, 0, 1, 1 };
  private static final float kTrailDepthTweak = -0.05f;
  
  // data for the basic trail images
  private static final String kBasicTrailPixels[] = { " 00" // x+ (0)
                                                    + "000",
                                                    
                                                      "00 " // y+ (1)
                                                    + "000",
                                                    
                                                      "000" // x- (2)
                                                    + "00 ",
                                                    
                                                      "000" // y- (3)
                                                    + " 00" };
  
  // basic trail image objects
  private final EgaImage mBasicTrailImages[];
  
  // details of the step trail images
  private static final int   kStepTrailWidth      = 3,
                             kStepTrailHeight     = 2;
  private static final int   kStepTrailRefXPos[]  = { 1, 0, 0, 1 },
                             kStepTrailRefYPos[]  = { 0, 0, 1, 1 };
  
  // data for the step trail images
  private static final String kStepTrailPixels[] = { " 00" // x+ (0)
                                                   + "00 ",
                                                    
                                                     "00 " // y+ (1)
                                                   + " 00",
                                                    
                                                     "000" // x- (2)
                                                   + "00 ",
                                                    
                                                     "000" // y- (3)
                                                   + " 00" };
  
  // step trail image objects
  private final EgaImage mStepTrailImages[];
  
  // constructor
  public SpookImage(char colourMap[]) {
   
    mBasicImages = new EgaImage[kBasicPixels.length];
    for ( int k = 0 ; k < kBasicPixels.length ; k++ ) {
      mBasicImages[k] = new EgaImage(kBasicRefXPos, kBasicRefYPos,
                                     kBasicWidth, kBasicHeight,
                                     EgaTools.convertColours(kBasicPixels[k],
                                                             colourMap), 
                                     kBasicDepth);
    }
    
    mBasicSmallImage = new EgaImage(0, 0, 2, 1,
                                    EgaTools.convertColours("00", colourMap),
                                    0.0f);
    
    mStepImages = new EgaImage[kStepPixels.length];
    mStepSmallImages = new EgaImage[kStepSmallPixels.length];
    for ( int k = 0 ; k < kStepPixels.length ; k++ ) {
      float depths[] = stepDepths(kStepDepths[k]);
      mStepImages[k] = new EgaImage(kStepRefXPos[k], kStepRefYPos[k],
                                    kStepWidth, kStepHeight,
                                    EgaTools.convertColours(
                                          kStepPixels[k],
                                          colourMap), 
                                    depths);
      mStepSmallImages[k] = new EgaImage(kStepRefXPos[k], kStepRefYPos[k],
                                         kStepWidth, kStepHeight,
                                         EgaTools.convertColours(
                                               kStepSmallPixels[k], 
                                               colourMap), 
                                         depths);
    }
    
    mBasicTrailImages = new EgaImage[kBasicTrailPixels.length];
    for ( int k = 0 ; k < kBasicTrailPixels.length ; k++ ) {
      String pixels = EgaTools.convertColours(kBasicTrailPixels[k], colourMap);
      mBasicTrailImages[k] = new EgaImage(kBasicTrailRefXPos[k], 
                                          kBasicTrailRefYPos[k],
                                          kBasicTrailWidth, kBasicTrailHeight,
                                          pixels, 
                                          trailDepths(kBasicTrailRefYPos[k]));
    }
    
    mStepTrailImages = new EgaImage[kStepTrailPixels.length];
    for ( int k = 0 ; k < kStepTrailPixels.length ; k++ ) {
      String pixels = EgaTools.convertColours(kStepTrailPixels[k], colourMap);
      mStepTrailImages[k] = new EgaImage(kStepTrailRefXPos[k],
                                         kStepTrailRefYPos[k],
                                         kStepTrailWidth, kStepTrailHeight,
                                         pixels, 
                                         trailDepths(kStepTrailRefYPos[k]));
    }
    
  } // constructor

  // construct a depth array for the stepping image
  static private float[] stepDepths(float depthsInfo[]) {
    
    float depths[] = new float[kStepWidth*kStepHeight];

    int index = 0;
    for ( int iy = 0 ; iy < kStepHeight ; iy++ ) {
      for ( int ix = 0 ; ix < kStepWidth ; ix++ ) {
        float d = depthsInfo[ix];
        if ( d == -2 ) {
          if ( iy == kStepHeight-2 ) d = -(1 + kStepDepthTweak);
          else                       d = -1;
        }
        depths[index++] = kBasicDepth + d;
      }
    }
    
    return depths;
    
  } // stepDepths()

  // construct a depth array for the trail image
  static private float[] trailDepths(int refYPos) {
    
    float depths[] = new float[kBasicTrailWidth*kBasicTrailHeight];
    
    int index = 0;
    for ( int iy = 0 ; iy < kBasicTrailHeight ; iy++ ) {
      for ( int ix = 0 ; ix < kBasicTrailWidth ; ix++ ) {
        depths[index++] = kTrailDepthTweak + (refYPos - iy);
      }
    }
    
    return depths;
    
  } // trailDepths()
  
  // display the basic critter
  public void drawBasic(EgaCanvas canvas,
                        int xPos, int yPos, int zPos, 
                        int direc, int prevDirec,
                        boolean small) {

    if ( prevDirec != Env.NONE ) {
      assert( prevDirec >= 0 && prevDirec < 4 );
      mBasicTrailImages[prevDirec].draw3D(canvas, 2*xPos, 2*yPos, zPos);
    }

    assert( direc >= 0 && direc < 4 );
    if ( small ) {
      mBasicSmallImage.draw3D(canvas, 2*xPos, 2*yPos, zPos);
    } else {
      mBasicImages[direc].draw3D(canvas, 2*xPos, 2*yPos, zPos);
    }
    
  } // drawBasic()
  
  // display the critter mid-step
  // (position is where the critter is stepping to)
  public void drawStep(EgaCanvas canvas,
                       int xPos, int yPos, int zPos,
                       int direc, int prevDirec,
                       boolean small) {

    if ( prevDirec != Env.NONE ) {
      assert( prevDirec >= 0 && prevDirec < 4 );
      mStepTrailImages[prevDirec].draw3D(canvas, 2*xPos, 2*yPos, zPos);
    }
    
    assert( direc >= 0 && direc < 4 );
    if ( small ) {
      mStepSmallImages[direc].draw3D(canvas, 2*xPos, 2*yPos, zPos);
    } else {
      mStepImages[direc].draw3D(canvas, 2*xPos, 2*yPos, zPos);
    }
    
  } // drawStep()
  
} // class SpookImage
