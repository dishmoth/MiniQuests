/*
 *  Tree.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// a decorative obstacle
public class Tree extends Sprite3D implements Obstacle {

  // details of the image
  private static final int   kWidth   = 5,
                             kHeight  = 11;
  private static final int   kRefXPos = 2,
                             kRefYPos = 10;
  private static final float kDepth   = -0.01f;

  // data for the basic images
  private static final String kPixels[] = { "     "
                                          + " 332 " 
                                          + "32221"
                                          + "32321"
                                          + "32221"
                                          + "22221"
                                          + "23221"
                                          + "22221"
                                          + " 221 "
                                          + "  0  "
                                          + "  0  ",
                                       
                                            "  3  " 
                                          + "  3  "
                                          + " 322 "
                                          + " 321 "
                                          + " 321 "
                                          + "32231"
                                          + "32221"
                                          + "32321"
                                          + " 221 "
                                          + "  0  "
                                          + "  0  ",
                                       
                                            "  3  " 
                                          + "  3  "
                                          + " 322 "
                                          + " 323 "
                                          + " 322 "
                                          + "32322"
                                          + "32222"
                                          + "32232"
                                          + " 322 "
                                          + "  0  "
                                          + "  0  " };
  
  // different colours for trees
  private static final char kColourSchemes[][] = { { 'm', 'G', 'Y', 'M' },
                                                   { '0', 'G', '2', 'M' },
                                                   { '0', 'W', 'K', 'E' },
                                                   { 'm', 'm', 'K', '6' } };
  
  // images of trees, different colour and types, [type][colour]
  private static EgaImage kImages[][] = null;
  
  // position of base point of tree
  final private int mXPos,
                    mYPos,
                    mZPos;

  // whether to shift the tree in the x-direction (0 or 1)
  final private int mShift;
  
  // image type
  final private int mType;
  
  // colour type
  final private int mColour;
  
  // prepare image
  public static void initialize(int type, int colour) {
    
    if ( kImages == null ) {
      kImages = new EgaImage[kPixels.length][kColourSchemes.length];
    }

    if ( kImages[type][colour] == null ) {
      String pixels = kPixels[type];
      char colours[] = kColourSchemes[colour];
      kImages[type][colour] = new EgaImage(kRefXPos, kRefYPos,
                                           kWidth, kHeight,
                                           EgaTools.convertColours(pixels, 
                                                                   colours),
                                           kDepth);
    }

  } // initialize()
  
  // constructor
  public Tree(int xPos, int yPos, int zPos, int shift, int type, int colour) {
    
    mType = type;
    assert( type >= 0 && type < kPixels.length );
    
    assert( colour >= 0 && colour < kColourSchemes.length );
    mColour = colour;
    
    initialize(mType, mColour);
    
    mXPos = xPos;
    mYPos = yPos;
    mZPos = zPos;
    
    assert( shift >= 0 && shift < 2 );
    mShift = shift;

  } // constructor
  
  // whether the player can stand at the specified position
  public boolean isPlatform(int x, int y, int z) {

    return false;
    
  } // Obstacle.isPlatform()

  // whether there is space at the specified position
  public boolean isEmpty(int x, int y, int z) {

    if ( x == mXPos && y == mYPos && z >= mZPos && z < mZPos+2 ) return false;
    return true;
  
  } // Obstacle.isEmpty()

  // whether the position is outside of the game world
  public boolean isVoid(int x, int y, int z) { 

    return false;
    
  } // Obstacle.isVoid()

  // nothing to do here
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {

  } // Sprite.advance()

  // display the object
  @Override
  public void draw(EgaCanvas canvas) {

    final int xPos = mXPos - mCamera.xPos(),
              yPos = mYPos - mCamera.yPos(),
              zPos = mZPos - mCamera.zPos();

    int x = 2*xPos + mShift,
        y = 2*yPos;
    
    kImages[mType][mColour].draw3D(canvas, x, y, zPos);
    
  } // Sprite.draw()

} // class Tree
