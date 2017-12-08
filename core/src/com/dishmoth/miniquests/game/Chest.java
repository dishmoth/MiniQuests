/*
 *  Chest.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// a bit of scenery with simple animation
public class Chest extends Sprite3D implements Obstacle {

  // details of the images
  private static final int kRefXPos = 4,
                           kRefYPos = 10;
  
  // images of the chest, open and closed, facing two directions
  private static EgaImage kImageOpenDown,
                          kImageClosedDown,
                          kImageOpenLeft,
                          kImageClosedLeft;
  
  // position (of bottom-left of object)
  private int mXPos,
              mYPos,
              mZPos;
  
  // which image to show
  private boolean mIsOpen;
  
  // which direction to show
  private boolean mDirecDown;
  
  // prepare images
  public static void initialize() {

    if ( kImageOpenDown != null ) return;
    
    EgaImage im1 = Env.resources().loadEgaImage("Chest1.png");
    final int width  = im1.width(),
              height = im1.height();
    byte pixels1[] = im1.pixels();
    
    float depths[] = new float[width*height];
    int index = 0;
    for ( int ix = 0 ; ix < width ; ix++ ) {
      depths[index] = (int)Math.abs( Math.floor( 0.5f*(ix - kRefXPos) ) );
      index++;
    }
    for ( int iy = 1 ; iy < height ; iy++ ) {
      for ( int ix = 0 ; ix < width ; ix++ ) {
        depths[index] = depths[index - width];
        index++;
      }
    }
    
    EgaImage im2 = Env.resources().loadEgaImage("Chest2.png");
    byte pixels2[] = im2.pixels();
    
    byte pixelsFlip1[] = new byte[width*height],
         pixelsFlip2[] = new byte[width*height];
    for ( int iy = 0 ; iy < height ; iy++ ) {
      int row = iy*width;
      for ( int ix = 0 ; ix < width ; ix++ ) {
        pixelsFlip1[row+ix] = im1.pixels()[row+(width-1-ix)];
        pixelsFlip2[row+ix] = pixels2[row+(width-1-ix)];
      }
    }
    
    kImageClosedDown = new EgaImage(kRefXPos, kRefYPos, 
                                    width, height, pixels1, depths);
    
    kImageOpenDown = new EgaImage(kRefXPos, kRefYPos, width, height,
                                  pixels2, Env.copyOf(depths));
    
    kImageClosedLeft = new EgaImage(kRefXPos, kRefYPos, 
                                    width, height, pixelsFlip1, depths);
    
    kImageOpenLeft = new EgaImage(kRefXPos, kRefYPos, width, height,
                                  pixelsFlip2, Env.copyOf(depths));
    
  } // initialize()
  
  // constructor
  public Chest(int xPos, int yPos, int zPos, int direc) {
    
    initialize();
    
    mXPos = xPos;
    mYPos = yPos;
    mZPos = zPos;
  
    mIsOpen = false;

    assert( direc == Env.DOWN || direc == Env.LEFT );
    mDirecDown = (direc == Env.DOWN);
    
  } // constructor
  
  // whether the chest is open or closed
  public boolean isOpen() { return mIsOpen; }
  
  // open or close the object
  public void setOpen(boolean open) { mIsOpen = open; }
  
  // shift position
  public void shiftPos(int dx, int dy, int dz) {
    
    mXPos += dx;
    mYPos += dy;
    mZPos += dz;
    
  } // shiftPos()
  
  // methods required for the Obstacle interface
  public boolean isPlatform(int x, int y, int z) { return false; }
  public boolean isVoid(int x, int y, int z) { return false; }

  // whether there is space at the specified position
  public boolean isEmpty(int x, int y, int z) {
    
    int dx = ( mDirecDown ? 2 : 1 ),
        dy = ( mDirecDown ? 1 : 2 );
    if ( x >= mXPos && x <= mXPos+dx && 
         y >= mYPos && y <= mYPos+dy && 
         z >= mZPos && z <= mZPos+4 ) {
      return false;
    }
    return true;
 
  } // Obstacle.isEmpty()

  // update (nothing to do here)
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

    EgaImage image = ( mDirecDown 
                       ? ( mIsOpen ? kImageOpenDown : kImageClosedDown )
                       : ( mIsOpen ? kImageOpenLeft : kImageClosedLeft ) );
    image.draw3D(canvas, 2*xPos, 2*yPos, zPos);
    
  } // Sprite.draw()

} // class Chest
