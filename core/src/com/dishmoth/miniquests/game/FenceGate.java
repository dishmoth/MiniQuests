/*
 *  FenceGate.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// a decorative bit of barrier that fits in with the Fence class
public class FenceGate extends Sprite3D implements Obstacle {

  // details of the image
  private static final int   kImageWidth   = 5,
                             kImageHeight  = 9;
  private static final int   kRefXPosRight = 0,
                             kRefXPosUp    = 4,
                             kRefYPos      = 8;

  // pixels for the image
  private static final String kPixelsRightClosed = "  mmm"
                                                 + "mmm m"
                                                 + "m mmm"
                                                 + "mmm m"
                                                 + "m m m"
                                                 + "m mmm"
                                                 + "mmm m"
                                                 + "m m  "
                                                 + "m    ",
                                                 
                              kPixelsRightOpen   = "  mmm"
                                                 + "mm  m"
                                                 + "m   m"
                                                 + "m   m"
                                                 + "m   m"
                                                 + "m   m"
                                                 + "m   m"
                                                 + "m    "
                                                 + "m    ",
                                                 
                              kPixelsUpClosed    = "mmm  "
                                                 + "m mmm"
                                                 + "mmm m"
                                                 + "m mmm"
                                                 + "m m m"
                                                 + "mmm m"
                                                 + "m mmm"
                                                 + "  m m"
                                                 + "    m",

                              kPixelsUpOpen      = "mmm  "
                                                 + "m  mm"
                                                 + "m   m"
                                                 + "m   m"
                                                 + "m   m"
                                                 + "m   m"
                                                 + "m   m"
                                                 + "    m"
                                                 + "    m";

  // depths for one row of the image
  private static final float kDepthsRight[] = { 0.0f, 0.0f, 1.0f, 1.0f, 2.0f },
                             kDepthsUp[]    = { 2.0f, 2.0f, 1.0f, 1.0f, 0.0f };
  private static final float kDepthOffset   = -0.01f;
  
  // image of the gate
  private static EgaImage kImageRightClosed = null,
                          kImageRightOpen   = null,
                          kImageUpClosed    = null,
                          kImageUpOpen      = null;
  
  // position of base point of fence
  final private int mXPos,
                    mYPos,
                    mZPos;
  
  // which direction the fence runs along
  private int mDirec;
  
  // state of the gate
  private boolean mIsClosed;

  // prepare image
  public static void initialize() {
    
    if ( kImageRightClosed != null ) return;

    assert( kDepthsRight.length == kImageWidth );
    float depthsRight[] = new float[kImageWidth*kImageHeight];
    for ( int k = 0 ; k < depthsRight.length ; k++ ) {
      depthsRight[k] = kDepthsRight[k % kDepthsRight.length] + kDepthOffset;
    }

    assert( kDepthsUp.length == kImageWidth );
    float depthsUp[] = new float[kImageWidth*kImageHeight];
    for ( int k = 0 ; k < depthsUp.length ; k++ ) {
      depthsUp[k] = kDepthsUp[k % kDepthsUp.length] + kDepthOffset;
    }

    kImageRightClosed = new EgaImage(kRefXPosRight, kRefYPos, 
                                     kImageWidth, kImageHeight,
                                     kPixelsRightClosed, depthsRight);
    kImageRightOpen = new EgaImage(kRefXPosRight, kRefYPos, 
                                   kImageWidth, kImageHeight,
                                   kPixelsRightOpen, depthsRight);
    
    kImageUpClosed = new EgaImage(kRefXPosUp, kRefYPos, 
                                  kImageWidth, kImageHeight,
                                  kPixelsUpClosed, depthsUp);
    kImageUpOpen = new EgaImage(kRefXPosUp, kRefYPos, 
                                kImageWidth, kImageHeight,
                                kPixelsUpOpen, depthsUp);
    
  } // initialize()
  
  // constructor
  public FenceGate(int xPos, int yPos, int zPos, int direc) {
    
    initialize();
    
    mXPos = xPos;
    mYPos = yPos;
    mZPos = zPos;

    assert( direc == Env.RIGHT || direc == Env.UP );
    mDirec = direc;
    
    mIsClosed = false;
    
  } // constructor
  
  // open or close the gate
  public void setClosed(boolean c) { mIsClosed = c; }
  
  // whether the player can stand at the specified position
  public boolean isPlatform(int x, int y, int z) {

    return false;
    
  } // Obstacle.isPlatform()

  // whether there is space at the specified position
  public boolean isEmpty(int x, int y, int z) {

    if ( z != mZPos+1 ) return true;
    if ( mDirec == Env.RIGHT ) {
      if ( y == mYPos ) {
        if ( x == mXPos || x == mXPos+2 ) return false;
        if ( mIsClosed && x == mXPos+1 ) return false;
      }
    } else {
      if ( x == mXPos ) {
        if ( y == mYPos || y == mYPos+2 ) return false;
        if ( mIsClosed && y == mYPos+1 ) return false;
      }
    }
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

    EgaImage image = ( mDirec == Env.RIGHT 
                     ? ( mIsClosed ? kImageRightClosed : kImageRightOpen )
                     : ( mIsClosed ? kImageUpClosed    : kImageUpOpen    ) );
    image.draw3D(canvas, 2*xPos, 2*yPos, zPos);
    
  } // Sprite.draw()

} // class FenceGate
