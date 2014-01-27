/*
 *  Tree.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// a decorative obstacle
public class Tree extends Sprite3D implements Obstacle {

  // image of the gate
  private static EgaImage kImage = null;
  
  // position of base point of tree
  final private int mXPos,
                    mYPos,
                    mZPos;

  // which version of a tree to show
  final private int mType;
  
  // prepare image
  public static void initialize() {
    
    if ( kImage != null ) return;

    EgaImage pic = Env.resources().loadEgaImage("Tree1.png");
    
    final int   width   = pic.width(),
                height  = pic.height();
    final int   refXPos = width/2,
                refYPos = height - 1;
    final float depth   = -0.01f;
    kImage = new EgaImage(refXPos, refYPos, width, height,
                          pic.pixels().clone(), depth);
    
  } // initialize()
  
  // constructor
  public Tree(int xPos, int yPos, int zPos, int type) {
    
    initialize();
    
    mXPos = xPos;
    mYPos = yPos;
    mZPos = zPos;
    
    assert( type >= 0 && type < 2 );
    mType = type;

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

    int x = 2*xPos,
        y = 2*yPos;
    if ( mType == 1 ) {
      x += 1;
    }
    
    kImage.draw3D(canvas, x, y, zPos);
    
  } // Sprite.draw()

} // class Tree
