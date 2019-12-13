/*
 *  BlockArray.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.Arrays;
import java.util.LinkedList;

// a three-dimensional array of block objects
public class BlockArray extends Sprite3D implements Obstacle {

  // pixel height of a block
  private static final int kBlockHeight = 2; 
  
  // array of blocks 
  // (' ' for empty, '0' to '9' for different colours,
  // '-' for invisible platform, '*' for invisible barrier)
  private String mBlocks[][];

  // colours for different block types (value pairs, middle and edge colours)
  private final byte mColours[][];
  
  // size of the block array
  private int mXSize,
              mYSize,
              mZSize;
  
  // position of origin
  private int mXPos,
              mYPos,
              mZPos;

  // complete image (with depth)
  private EgaImage mImage;
  
  // constructor
  public BlockArray(String blocks[][], String colours[], 
                    int x, int y, int z) {

    assert( colours != null );
    assert( colours.length >= countColours(blocks) );

    mColours = new byte[colours.length][];
    for ( int ic = 0 ; ic < colours.length ; ic++ ) {
      assert( colours[ic] != null );
      assert( colours[ic].length() == 2 );
      mColours[ic] = EgaTools.decodePixels(colours[ic]);
    }

    setBlocks(blocks, x, y, z);
    
  } // constructor

  // number of colours used by the blocks (only used for sanity checking)
  private int countColours(String[][] blocks) {
    
    if ( blocks == null ) return 0;
    
    int numColours = 0;
    for ( int iz = 0 ; iz < blocks.length ; iz++ ) {
      String zLayer[] = blocks[iz];
      assert( zLayer != null );
      for ( int iy = 0 ; iy < zLayer.length ; iy++ ) {
        String yRow = zLayer[iy];
        assert( yRow != null );
        for ( int ix = 0 ; ix < yRow.length() ; ix++ ) {
          char ch = yRow.charAt(ix);
          if ( ch == ' ' || ch == '-' || ch == '*' ) continue;
          assert( ch >= '0' && ch <= '9' );
          numColours = Math.max(numColours, (ch - '0')+1);
        }
      }
    }
    return numColours;
    
  } // countColours()
  
  // define (or update) the block array
  protected void setBlocks(String blocks[][], int x, int y, int z) {
    
    mBlocks = blocks;
    
    mXPos = x;
    mYPos = y;
    mZPos = z;

    if ( mBlocks == null ) {
      mXSize = mYSize = mZSize = 0;
      mImage = null;
      return;
    }
    
    assert( mBlocks[0] != null && mBlocks[0][0] != null );

    mXSize = mBlocks[0][0].length();
    mYSize = mBlocks[0].length;
    mZSize = mBlocks.length;

    assert( mXSize > 0 && mYSize > 0 && mZSize > 0 );
    for ( int iz = 0 ; iz < mZSize ; iz++ ) {
      String zLayer[] = blocks[iz];
      assert( zLayer != null && zLayer.length == mYSize );
      for ( int iy = 0 ; iy < mYSize ; iy++ ) {
        String yRow = zLayer[iy];
        assert( yRow != null && yRow.length() == mXSize );
      }
    }
    
    buildImage();
    
  } // setBlocks()
  
  // access to position
  public int getXPos() { return mXPos; }
  public int getYPos() { return mYPos; }
  public int getZPos() { return mZPos; }
  
  // shift position
  public void shiftPos(int dx, int dy, int dz) {
    
    mXPos += dx;
    mYPos += dy;
    mZPos += dz;
    
  } // shiftPos()
  
  // set position
  public void setPos(int x, int y, int z) {
    
    mXPos = x;
    mYPos = y;
    mZPos = z;
    
  } // setPos()
  
  // returns the block at the index position (or ' ' if out-of-range)
  private char getBlock(int x, int y, int z) {

    if ( x < 0 || x >= mXSize || 
         y < 0 || y >= mYSize || 
         z < 0 || z >= mZSize ) return ' ';    
    return mBlocks[z][mYSize-1-y].charAt(x);
    
  } // getBlock()
  
  // paint the image onto the blocks ignoring depths
  // the image's ref point applies to the (x,y,z) blocks 
  public void paint(EgaImage colours, int x, int y, int z) {

    int xPos = 2*( x - y ),
        yPos = -( x + y + z );
    colours.draw(mImage, xPos, yPos);
    
  } // paint()
  
  // whether the player can stand at the specified position
  public boolean isPlatform(int x, int y, int z) {
    
    final int dx = x - mXPos,
              dy = y - mYPos,
              dz = z - mZPos;

    if ( dz < 0 || (dz % kBlockHeight) != 0 ) return false;
    
    final char ch = getBlock(dx, dy, dz/kBlockHeight);
    if ( (ch >= '0' && ch <= '9') || ch == '-' ) return true;
    
    return false;
    
  } // Obstacle.isPlatform()
  
  // whether there is space at the specified position
  public boolean isEmpty(int x, int y, int z) {
    
    final int dx = x - mXPos,
              dy = y - mYPos,
              dz = z - mZPos;
    
    final int zz = (int)Math.ceil(dz/(float)kBlockHeight);
    
    final char ch = getBlock(dx, dy, zz);
    if ( ch == ' ' ) return true;

    return false;
    
  } // Obstacle.isEmpty()
  
  // whether the position is outside of the game world
  public boolean isVoid(int x, int y, int z) { return false; }

  // nothing to do here
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {
  } // Sprite.advance()

  // construct the image of the blocks
  protected void buildImage() {
    
    int xMin = Integer.MAX_VALUE,
        xMax = Integer.MIN_VALUE,
        yMin = Integer.MAX_VALUE,
        yMax = Integer.MIN_VALUE;
    
    for ( int iz = 0 ; iz < mZSize ; iz++ ) {
      for ( int iy = 0 ; iy < mYSize ; iy++ ) {
        for ( int ix = 0 ; ix < mXSize ; ix++ ) {
          final char ch = getBlock(ix, iy, iz);
          if ( ch == ' ' ) continue;
          final int x = 2*ix - 2*iy,
                    y = -( ix + iy + kBlockHeight*iz );
          xMin = Math.min(xMin, x);
          xMax = Math.max(xMax, x);
          yMin = Math.min(yMin, y);
          yMax = Math.max(yMax, y);
        }
      }
    }
    assert( xMax >= xMin && yMax >= yMin );

    xMin += Block.boundXMin();
    xMax += Block.boundXMax();
    yMin += Block.boundYMin();
    yMax += Block.boundYMax() + kBlockHeight;
    
    final int xRef   = -xMin,
              yRef   = -yMin,
              width  = xMax - xMin + 1,
              height = yMax - yMin + 1;
    mImage = new EgaImage(xRef, yRef, width, height);

    int edgeIndex[][] = new int[mYSize+1][mXSize+1]; 
    for ( int iz = 0 ; iz < mZSize ; iz++ ) {
      
      for ( int iy = 0 ; iy < mYSize+1 ; iy++ ) Arrays.fill(edgeIndex[iy], -1);
      
      for ( int iy = 0 ; iy < mYSize ; iy++ ) {
        for ( int ix = 0 ; ix < mXSize ; ix++ ) {
          final char ch = getBlock(ix, iy, iz);
          if ( ch == ' ' || ch == '-' || ch == '*' ) continue;
          final int colIndex = (ch - '0');

          edgeIndex[iy  ][ix  ] = Math.max(edgeIndex[iy  ][ix  ], colIndex);
          edgeIndex[iy  ][ix+1] = Math.max(edgeIndex[iy  ][ix+1], colIndex);
          edgeIndex[iy+1][ix  ] = Math.max(edgeIndex[iy+1][ix  ], colIndex);
          edgeIndex[iy+1][ix+1] = Math.max(edgeIndex[iy+1][ix+1], colIndex);
        }
      }
      
      for ( int iy = 0 ; iy < mYSize ; iy++ ) {
        for ( int ix = 0 ; ix < mXSize ; ix++ ) {
          final char ch = getBlock(ix, iy, iz);
          if ( ch == ' ' || ch == '-' || ch == '*' ) continue;
          final int colIndex = (ch - '0');

          final int depth = ix + iy;
          final int x = 2*ix - 2*iy,
                    y = -( depth + kBlockHeight*iz );

          Block.draw(mImage, x, y, depth, 
                     mColours[ edgeIndex[iy+1][ix  ] ][1],
                     mColours[ edgeIndex[iy  ][ix+1] ][1],
                     mColours[ edgeIndex[iy+1][ix+1] ][1],
                     mColours[ edgeIndex[iy  ][ix  ] ][1],
                     mColours[colIndex][0],
                     kBlockHeight);
        }
      }
    }
    
  } // buildImage()
  
  // display the blocks
  @Override
  public void draw(EgaCanvas canvas) {

    if ( mImage == null ) return;
    
    final int x0 = mXPos - mCamera.xPos(),
              y0 = mYPos - mCamera.yPos(),
              z0 = mZPos - mCamera.zPos();
    mImage.draw3D(canvas, 2*x0, 2*y0, z0);
    
  } // Sprite.draw()

  // display the blocks on a canvas using a specified camera
  public void draw(EgaCanvas canvas, Camera camera) {

    if ( mImage == null ) return;
    
    final int x0 = mXPos - camera.xPos(),
              y0 = mYPos - camera.yPos(),
              z0 = mZPos - camera.zPos();
    mImage.draw3D(canvas, 2*x0, 2*y0, z0);
    
  } // draw(Camera)

} // class BlockArray
