/*
 *  MapStones.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// glowing stones that show which quests have been completed
public class MapStones extends Sprite {

  // where the stones are within the image
  private final static int kStonePos[][] = { { 13, 10 },
                                             { 10, 19 },
                                             { 22,  7 },
                                             { 29, 13 },
                                             { 22, 21 } };
  
  // depth for the images
  private static final float kDepth = -1;

  // times for stone flashing on and off
  private static final int kFlashTime = 3,
                           kStartTime = 20;
  private static final int kTimes[]   = { 60, 30, 15, 10, 2 };
  
  // which quest each stone corresponds to
  private static final int kRemap[] = { 0, 4, 2, 1, 3 };
  
  // details of the image
  private static final int    kImageWidth   = 4,
                              kImageHeight  = 4;
  private static final int    kImageRefXPos = 1,
                              kImageRefYPos = 1;
  private static final String kImagePixels  = " 00 "
                                            + "0110"
                                            + "0100"
                                            + "  0 ";
  
  // different colours for different stones
  private static final char kImageColours[][] = { {'4','a'},
                                                  {'H','h'},
                                                  {'K','c'},
                                                  {'Y','o'},
                                                  {'5','j'} };
  
  // flash images for each stone
  private static final EgaImage kImages[];
  
  // which quests have been completed
  private final boolean mQuests[];

  // count of how many quests have been completed
  private int mNumQuests;

  // which stone is flashing currently (or -1)
  private int mStone;
  
  // time remaining for current stone
  private int mTimer;
  
  // images
  static {

    kImages = new EgaImage[ kImageColours.length ];
    for ( int k = 0 ; k < kImageColours.length ; k++ ) {
      kImages[k] = new EgaImage(kImageRefXPos, kImageRefYPos,
                                kImageWidth, kImageHeight,
                                EgaTools.convertColours(kImagePixels,
                                                        kImageColours[k]));
    }
    
  } // static

  // constructor
  public MapStones(boolean quests[]) {
    
    mQuests = quests;
    
    mNumQuests = 0;
    for ( boolean done : mQuests ) mNumQuests += (done?1:0);
    
    mStone = -1;
    mTimer = kStartTime;
    
  } // constructor
  
  // animate the stones
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {
    
    if ( mNumQuests == 0 ) return;

    if ( --mTimer < 0 ) {
      do {
        mStone = (mStone+1) % mQuests.length;
      } while ( mQuests[kRemap[mStone]] == false);
      mTimer = kTimes[mNumQuests-1];
    }
    
  } // Sprite.advance()

  // make the stones flash
  @Override
  public void draw(EgaCanvas canvas) {

    if ( mStone < 0 ) return;

    boolean flash = ( mTimer < kFlashTime );
    if ( !flash ) return;
    
    int xy[] = kStonePos[kRemap[ mStone ]];
    EgaImage image = kImages[kRemap[ mStone ]];
    image.draw(canvas, xy[0], xy[1], kDepth);
    
  } // Sprite.draw()

} // class MapStones
