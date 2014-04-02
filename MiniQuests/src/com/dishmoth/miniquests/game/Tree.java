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
                             kHeight  = 10;
  private static final int   kRefXPos = 2,
                             kRefYPos = 9;
  private static final float kDepth   = -0.01f;

  // data for the basic image
  private static final String kPixels = " 332 " 
                                      + "32221"
                                      + "32321"
                                      + "32221"
                                      + "22221"
                                      + "23221"
                                      + "22221"
                                      + " 221 "
                                      + "  0  "
                                      + "  0  ";
  
  // different colours for tree
  private static final char kColourSchemes[][] = { { 'm', 'G', 'Y', 'M' },
                                                   { '0', 'G', '2', 'M' } };
  
  // image of the tree, different colour schemes
  private static EgaImage kImages[] = null;
  
  // position of base point of tree
  final private int mXPos,
                    mYPos,
                    mZPos;

  // whether to shift the tree in the x-direction (0 or 1)
  final private int mShift;
  
  // colour scheme
  final private int mColour;
  
  // prepare image
  public static void initialize() {
    
    if ( kImages != null ) return;

    kImages = new EgaImage[kColourSchemes.length];
    for ( int k = 0 ; k < kImages.length ; k++ ) {
      kImages[k] = new EgaImage(kRefXPos, kRefYPos,
                                kWidth, kHeight,
                                EgaTools.convertColours(kPixels,
                                                        kColourSchemes[k]),
                                kDepth);
    }

    /*
    EgaImage pic = Env.resources().loadEgaImage("Tree1.png");
    
    final int   width   = pic.width(),
                height  = pic.height();
    final int   refXPos = width/2,
                refYPos = height - 1;
    final float depth   = -0.01f;
    kImage = new EgaImage(refXPos, refYPos, width, height,
                          pic.pixelsCopy(), depth);
    */
    
  } // initialize()
  
  // constructor
  public Tree(int xPos, int yPos, int zPos, int shift, int colour) {
    
    initialize();
    
    mXPos = xPos;
    mYPos = yPos;
    mZPos = zPos;
    
    assert( shift >= 0 && shift < 2 );
    mShift = shift;

    assert( colour >= 0 && colour < kColourSchemes.length );
    mColour = colour;
    
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
    
    kImages[mColour].draw3D(canvas, x, y, zPos);
    
  } // Sprite.draw()

} // class Tree
