/*
 *  CritterImage.java
 *  Copyright Simon Hern 2010
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

// collection of monster images
public class CritterImage {

  // details of the basic standing image
  private static final int   kBasicWidth   = 4,
                             kBasicHeight  = 4;
  private static final int   kBasicRefXPos = 1,
                             kBasicRefYPos = 3;
  private static final float kBasicDepth   = -0.02f;

  // data for the basic standing image
  private static final String kBasicPixels[] = { " 00 " // x+ (0)
                                               + " 001"
                                               + " 00 "
                                               + " 00 ",
                                               
                                                 " 00 " // y+ (1) 
                                               + "100 "
                                               + " 00 "
                                               + " 00 ",
                                               
                                                 " 00 " // x- (2) 
                                               + "101 "
                                               + " 00 "
                                               + " 00 ",
                                               
                                                 " 00 " // y- (3)
                                               + " 101"
                                               + " 00 "
                                               + " 00 " };
  
  // basic image objects 
  private final EgaImage mBasicImages[];

  // details of the stepping image
  private static final int   kStepWidth   = 4,
                             kStepHeight  = 5;
  private static final int   kStepRefXPos[] = { 0, 2, 2, 0 },
                             kStepRefYPos[] = { 4, 4, 3, 3 };
  private static final float kStepDepths[][] = { {kBasicDepth,kBasicDepth+1},
                                                 {kBasicDepth+1,kBasicDepth},
                                                 {kBasicDepth-1,kBasicDepth},
                                                 {kBasicDepth,kBasicDepth-1} };

  // data for the stepping images
  private static final String kStepPixels[] = { " 00 " // x+ (0)
                                              + " 001"
                                              + " 00 "
                                              + " 00 "
                                              + " 0  ",

                                                " 00 " // y+ (1)
                                              + "100 "
                                              + " 00 "
                                              + " 00 "
                                              + "  0 ",

                                                " 00 " // x- (2)
                                              + "100 "
                                              + " 01 "
                                              + " 00 "
                                              + " 0  ",

                                                " 00 " // y- (3)
                                              + " 001"
                                              + " 10 "
                                              + " 00 "
                                              + "  0 " };
  
  // stepping image objects 
  private final EgaImage mStepImages[];

  // constructor
  public CritterImage(char colourMap[]) {
   
    mBasicImages = new EgaImage[kBasicPixels.length];
    for ( int k = 0 ; k < kBasicPixels.length ; k++ ) {
      mBasicImages[k] = new EgaImage(kBasicRefXPos, kBasicRefYPos,
                                     kBasicWidth, kBasicHeight,
                                     EgaTools.convertColours(kBasicPixels[k],
                                                             colourMap), 
                                     kBasicDepth);
    }
    
    mStepImages = new EgaImage[kStepPixels.length];
    for ( int k = 0 ; k < kStepPixels.length ; k++ ) {
      mStepImages[k] = new EgaImage(kStepRefXPos[k], kStepRefYPos[k],
                                    kStepWidth, kStepHeight,
                                    EgaTools.convertColours(kStepPixels[k],
                                                            colourMap), 
                                    stepDepths(kStepDepths[k]));
    }
    
  } // constructor

  // convert from colour scheme to EGA colours
  /*
  static private String convertColours(String source, char colourMap[]) {
    
    char colours[] = new char[source.length()];
    for ( int k = 0 ; k < colours.length ; k++ ) {
      final char ch = source.charAt(k);
      if ( ch == ' ' ) {
        colours[k] = ' ';
      } else {
        final int index = (int)(ch - '0');
        assert( index >= 0 && index < colourMap.length );
        colours[k] = colourMap[index];
      }
    }
    return new String(colours);
    
  } // convertColours()
  */
  
  // construct a depth array for the stepping critter image
  static private float[] stepDepths(float depthsInfo[]) {
    
    float depths[] = new float[kStepWidth*kStepHeight];

    int index = 0;
    for ( int iy = 0 ; iy < kStepHeight ; iy++ ) {
      for ( int ix = 0 ; ix < kStepWidth ; ix++ ) {
        final float d = depthsInfo[ix/2];
        depths[index++] = d;
      }
    }
    
    return depths;
    
  } // stepDepths()

  // display the basic critter
  public void drawBasic(EgaCanvas canvas,
                        int xPos, int yPos, int zPos, int direc) {

    assert( direc >= 0 && direc < 4 );
    mBasicImages[direc].draw3D(canvas, 2*xPos, 2*yPos, zPos);
    
  } // drawBasic()
  
  // display the critter mid-step
  // (position is where the critter is stepping to)
  public void drawStep(EgaCanvas canvas,
                       int xPos, int yPos, int zPos, int direc) {

    assert( direc >= 0 && direc < 4 );
    mStepImages[direc].draw3D(canvas, 2*xPos, 2*yPos, zPos);
    
  } // drawStep()
  
} // class CritterImage
