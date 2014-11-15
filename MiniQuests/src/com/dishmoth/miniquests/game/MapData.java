/*
 *  MapData.java
 *  Copyright Simon Hern 2014
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.Arrays;

// images, exits and useful positions for a map
abstract public class MapData {

  // story event: the player has entered a dungeon location
  public static class EventAtDungeon extends StoryEvent {
    public int mNum;
    public EventAtDungeon(int num) { mNum=num; }
  } // class MapData.EventAtDungeon
  
  // the map, broken up into sub-images
  protected EgaImage mImages[];

  // which directions are possible, [index][direc] = dest or -1
  protected int mExits[][];
  
  // constructor
  public MapData(String mapName) {

    EgaImage mapImage = Env.resources().loadEgaImage(mapName);
    parseMap(mapImage);
    
  } // constructor
  
  // convert the map into images and exits
  protected void parseMap(EgaImage image) {
    
    final int sizeX = Env.screenWidth(),
              sizeY = Env.screenHeight();
    int numX = (image.width() + 1)/(sizeX + 1),
        numY = (image.height() + 1)/(sizeY + 1);
    assert( image.width() == sizeX*numX + (numX-1) );
    assert( image.height() == sizeY*numY + (numY-1) );
    
    mImages = new EgaImage[numX*numY];

    int index = 0;
    for ( int iy = 0 ; iy < numY ; iy++ ) {
      for ( int ix = 0 ; ix < numX ; ix++ ) {
        mImages[index] = new EgaImage(0, 0, sizeX, sizeY); 
        image.draw(mImages[index], -ix*(sizeX+1), -iy*(sizeY+1));
        index += 1;
      }
    }

    mExits = new int[numX*numY][4];
    for ( int i = 0 ; i < mExits.length ; i++ ) {
      Arrays.fill(mExits[i], -1);
    }
    
    index = 0;
    byte pixels[] = image.pixels();
    for ( int iy = 0 ; iy < numY ; iy++ ) {
      for ( int ix = 0 ; ix < numX ; ix++ ) {
        int pix = iy*(sizeY+1)*image.width() + ix*(sizeX+1);
        byte hKey = ( (ix<numX-1) ? pixels[pix+sizeX] : 63 ),
             vKey = ( (iy<numY-1) ? pixels[pix+sizeY*image.width()] : 63 );
        assert( hKey == 0 || hKey == 63 );
        assert( vKey == 0 || vKey == 63 );
        if ( hKey == 0 ) {
          mExits[index][Env.RIGHT] = index+1; 
          mExits[index+1][Env.LEFT] = index;
        }
        if ( vKey == 0 ) {
          mExits[index][Env.DOWN] = index+numX; 
          mExits[index+numX][Env.UP] = index;
        }
        index += 1;
      }
    }
    
  } // constructor

  // a starting location for the map 
  abstract public int startPos(int type);
  
  // image for a location
  public EgaImage image(int pos) { return mImages[pos]; }
  
  // whether there is an exit from a location
  public boolean exit(int pos, int direc) {    
    
    return ( mExits[pos][direc] != -1 );
    
  } // exit()
  
  // where a location leads
  public int destination(int pos, int direc) {
    
    int dest = mExits[pos][direc];
    assert( dest >= 0 );
    return dest;
    
  } // destination()
  
  // whether there's anything special at a location
  public StoryEvent eventLocation(int pos) { return null; }
  
} // class MapData
