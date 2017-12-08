/*
 *  TreesUp.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

// boundary on the up-side (positive y) of a room
public class TreesUp extends Wall {

  // tree images
  private static final String kImageNames[] = { "TreesUp.png",
                                                null,
                                                "TreesUp3.png" };
  private static EgaImage kImages[] = null;
  
  // which image to show
  private int mImageIndex;
  
  // constructor
  public TreesUp(int x, int y, int z, int imageIndex) {

    super(x, y, z);

    assert( imageIndex >= 0 && imageIndex < kImageNames.length );
    mImageIndex = imageIndex;
    
    prepareImages();
    
  } // constructor

  // load all of the tree images
  static private void prepareImages() {
    
    if ( kImages != null ) return;
    kImages = new EgaImage[kImageNames.length];
    
    for ( int k = 0 ; k < kImages.length ; k++ ) {
      if ( kImageNames[k] == null ) continue;
      kImages[k] = prepareImage(kImageNames[k]);
    }
    
  } // prepareImages()
  
  // load the tree image and give it depth
  static private EgaImage prepareImage(String fileName) {

    EgaImage pic = Env.resources().loadEgaImage(fileName);
    
    final int width  = pic.width(),
              height = pic.height();
    assert( width == 2*Room.kSize );
    
    final int refXPos = 0,
              refYPos = height - 1;
    
    byte pixels[] = pic.pixels();

    float depths[] = new float[width*height];
    for ( int ix = 0 ; ix < width ; ix++ ) {
      depths[ix] = ix/2;
    }
    
    int index = width;
    for ( int iy = 1 ; iy < height ; iy++ ) {
      for ( int ix = 0 ; ix < width ; ix++ ) {
        depths[index++] = depths[ix];
      }
    }
    
    return new EgaImage(refXPos, refYPos, width, height, pixels, depths);
    
  } // prepareImage()
  
  // doors cannot be added to tree walls
  @Override
  public Door addDoor(int yPos, int zPos, byte floorColour[], int floorDrop) {
    
    assert( false );
    return null;
    
  } // Wall.addDoor()
  
  // whether the player can stand at the specified position
  @Override
  public boolean isPlatform(int x, int y, int z) {

    return false;
    
  } // Obstacle.isPlatform()

  // whether there is space at the specified position
  @Override
  public boolean isEmpty(int x, int y, int z) {

    return true;
  
  } // Obstacle.isEmpty()

  // whether the position is outside of the game world
  public boolean isVoid(int x, int y, int z) { 

    x -= mXPos;
    y -= mYPos;
    z -= mZPos;
    if ( y != Room.kSize+1 || (x < 0 || x >= Room.kSize) ) return false;
    return true;
    
  } // Obstacle.isVoid()

  // display the wall
  @Override
  public void draw(EgaCanvas canvas) {

    final int xOrigin = mXPos - mCamera.xPos(),
              yOrigin = mYPos - mCamera.yPos(),
              zOrigin = mZPos - mCamera.zPos();

    if ( xOrigin + yOrigin < -10 ) return;
    
    EgaImage image = kImages[mImageIndex];
    image.draw3D(canvas, 2*xOrigin+1, 2*(yOrigin+Room.kSize), zOrigin);
    
  } // Sprite.draw()

} // class TreesUp
