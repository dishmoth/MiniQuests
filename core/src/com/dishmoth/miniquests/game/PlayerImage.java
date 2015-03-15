/*
 *  PlayerImage.java
 *  Copyright Simon Hern 2010
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

// collection of player images in a particular colour scheme
public class PlayerImage {

  // details of the basic standing image
  private static final int   kBasicWidth   = 4,
                             kBasicHeight  = 5;
  private static final int   kBasicRefXPos = 1,
                             kBasicRefYPos = 4;
  private static final float kBasicDepth   = -0.02f;

  // data for the basic standing image
  private static final String kBasicPixels[] = { " 00 " // x+ (0)
                                               + "101 "
                                               + "1011"
                                               + " 01 "
                                               + " 00 ",
                                               
                                                 " 00 " // y+ (1) 
                                               + " 101"
                                               + "1101"
                                               + " 10 "
                                               + " 00 ",
                                               
                                                 " 00 " // x- (2) 
                                               + "1001"
                                               + "1001"
                                               + " 011"
                                               + " 00 ",
                                               
                                                 " 00 " // y- (3)
                                               + "1001"
                                               + "1001"
                                               + "110 "
                                               + " 00 " };
  
  // basic image objects 
  private final EgaImage mBasicImages[];

  // details of the stepping image
  private static final int   kStepWidth   = 4,
                             kStepHeight  = 6;
  private static final int   kStepRefXPos[] = { 0, 2, 2, 0 },
                             kStepRefYPos[] = { 5, 5, 4, 4 };
  private static final float kStepDepths[][] = { {kBasicDepth,kBasicDepth+1},
                                                 {kBasicDepth+1,kBasicDepth},
                                                 {kBasicDepth-1,kBasicDepth},
                                                 {kBasicDepth,kBasicDepth-1} };

  // data for the stepping images
  private static final String kStepPixels[] = { " 00 " // x+ (0)
                                              + " 01 "
                                              + " 11 "
                                              + " 10 "
                                              + " 00 "
                                              + " 0  ",

                                                " 00 " // y+ (1)
                                              + " 10 "
                                              + " 11 "
                                              + " 01 "
                                              + " 00 "
                                              + "  0 ",

                                                " 00 " // x- (2)
                                              + " 001"
                                              + " 011"
                                              + " 11 "
                                              + " 00 "
                                              + " 0  ",

                                                " 00 " // y- (3)
                                              + "100 "
                                              + "110 "
                                              + " 11 "
                                              + " 00 "
                                              + "  0 " };
  
  // stepping image objects 
  private final EgaImage mStepImages[];

  // data for the firing image
  private static final String kFiringPixels[] = { " 00 " // x+ (0)
                                                + "1011"
                                                + " 011"
                                                + " 01 "
                                                + " 00 ",
                                               
                                                  " 00 " // y+ (1) 
                                                + "1101"
                                                + "110 "
                                                + " 10 "
                                                + " 00 ",
                                               
                                                  " 00 " // x- (2) 
                                                + "1001"
                                                + "1111"
                                                + " 00 "
                                                + " 00 ",
                                               
                                                  " 00 " // y- (3)
                                                + "1001"
                                                + "1111"
                                                + " 00 "
                                                + " 00 " };
  
  // firing image objects 
  private final EgaImage mFiringImages[];

  // static constructor
  public PlayerImage(char colours[]) {
   
    mBasicImages = new EgaImage[kBasicPixels.length];
    for ( int k = 0 ; k < kBasicPixels.length ; k++ ) {
      mBasicImages[k] = new EgaImage(kBasicRefXPos, kBasicRefYPos,
                                     kBasicWidth, kBasicHeight,
                                     EgaTools.convertColours(kBasicPixels[k],
                                                             colours), 
                                     kBasicDepth);
    }
    
    mStepImages = new EgaImage[kStepPixels.length];
    for ( int k = 0 ; k < kStepPixels.length ; k++ ) {
      mStepImages[k] = new EgaImage(kStepRefXPos[k], kStepRefYPos[k],
                                    kStepWidth, kStepHeight,
                                    EgaTools.convertColours(kStepPixels[k],
                                                            colours), 
                                    stepDepths(kStepDepths[k]));
    }
    
    mFiringImages = new EgaImage[kFiringPixels.length];
    for ( int k = 0 ; k < kFiringPixels.length ; k++ ) {
      mFiringImages[k] = new EgaImage(kBasicRefXPos, kBasicRefYPos,
                                      kBasicWidth, kBasicHeight,
                                      EgaTools.convertColours(kFiringPixels[k],
                                                              colours), 
                                      kBasicDepth);
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
  
  // construct a depth array for the stepping player image
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

  // display the basic player
  public void drawBasic(EgaCanvas canvas,
                        int xPos, int yPos, int zPos, int direc) {

    assert( direc >= 0 && direc < 4 );
    mBasicImages[direc].draw3D(canvas, 2*xPos, 2*yPos, zPos);
    
  } // drawBasic()
  
  // display the player mid-step
  // (position is where the player is stepping to)
  public void drawStep(EgaCanvas canvas,
                       int xPos, int yPos, int zPos, int direc) {

    assert( direc >= 0 && direc < 4 );
    mStepImages[direc].draw3D(canvas, 2*xPos, 2*yPos, zPos);
    
  } // drawStep()
  
  // display the firing player
  public void drawFiring(EgaCanvas canvas,
                         int xPos, int yPos, int zPos, int direc) {

    assert( direc >= 0 && direc < 4 );
    mFiringImages[direc].draw3D(canvas, 2*xPos, 2*yPos, zPos);
    
  } // drawFiring()
  
} // class PlayerImage
