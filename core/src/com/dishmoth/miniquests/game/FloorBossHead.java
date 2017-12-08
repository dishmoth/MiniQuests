/*
 *  FloorBossHead.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// part of the FloorBoss come up to take a look around
public class FloorBossHead extends Sprite3D implements Obstacle {

  // details of the basic image
  private static final int   kWidth      = 6,
                             kHeight     = 6;
  private static final int   kRefXPos    = 2,
                             kRefYPos    = 5;
  private static final float kBasicDepth = -0.02f;

  // images in various colour schemes 
  private static final char kBasicColours[] = {'D','5','e'},
                            kHitColours[]   = {'b','b','b'};

  // data for the basic image, two frames
  private static final String kBasicPixels[] = { "  00  " 
                                               + " 0201 "
                                               + " 0001 "
                                               + "020211"
                                               + "100111"
                                               + "  11  ",
                                               
                                                 "  00  " 
                                               + " 0021 "
                                               + " 0001 "
                                               + "002021"
                                               + "100111"
                                               + "  11  " };
                                                
  // data for the basic depths
  private static final String kBasicDepths = "  55  " 
                                           + " 5665 "
                                           + " 5665 "
                                           + "557755"
                                           + "557755"
                                           + "  77  ";

  // image objects 
  private static EgaImage kBasicImages[],
                          kHitImage;

  // vertical movement of the head
  private static final int kZPosStart = -17,
                           kZPosTop   = 0,
                           kZPosEnd   = -10;
  private static final int kZSize     = 5;

  // how fast things happen
  private static final int kZChangeDelay = 2,
                           kLookDelay    = 8;
  private static final int kHitDuration  = 7,
                           kKillDuration = 14;
  private static final int kNumLooks     = 3;
  
  // position (x, y in block units, z in pixels)
  private int mXPos,
              mYPos,
              mZPos;

  // current state (0 => appearing, 1 => looking, 2 => descending)
  private int mStage;
  
  // time until next thing happens
  private int mTimer;
  
  // number of head turns
  private int mLookCount;
  
  // which direction the boss is looking
  private boolean mLookingLeft;
  
  // time since head was shot (or zero)
  private int mHitCounter;
  
  // if true then the head dies if shot
  private boolean mKillable;
  
  // prepare resources
  static public void initialize() {
    
    if ( kBasicImages != null ) return;

    float depths[] = new float[kWidth*kHeight];
    for ( int iy = 0 ; iy < kHeight ; iy++ ) {
      for ( int ix = 0 ; ix < kWidth ; ix++ ) {
        int ind = iy*kWidth + ix;
        char ch = kBasicDepths.charAt(ind);
        if ( ch == ' ' ) continue;
        float val = kBasicDepth - 0.5f*(int)(ch-'5');
        depths[ind] = val;
      }
    }
    
    kBasicImages = new EgaImage[2];
    kBasicImages[0] = new EgaImage(kRefXPos, kRefYPos,
                                   kWidth, kHeight,
                                   EgaTools.convertColours(
                                       kBasicPixels[0], kBasicColours), 
                                   depths);
    kBasicImages[1] = new EgaImage(kRefXPos, kRefYPos,
                                   kWidth, kHeight,
                                   EgaTools.convertColours(
                                       kBasicPixels[1], kBasicColours), 
                                   depths);
    kHitImage = new EgaImage(kRefXPos, kRefYPos,
                             kWidth, kHeight,
                             EgaTools.convertColours(
                                 kBasicPixels[0], kHitColours), 
                             depths);
    
  } // initialize()

  // constructor
  public FloorBossHead(int x, int y) {

    initialize();
    
    mXPos = x;
    mYPos = y;
    mZPos = kZPosStart;

    mStage = 0;
    mTimer = kZChangeDelay;

    mLookCount = kNumLooks;
    mLookingLeft = Env.randomBoolean();
    
    mHitCounter = 0;
    mKillable = false;
    
  } // constructor

  // true when the head has done its stuff
  public boolean isDone() { return ( mStage == 2 && mZPos <= kZPosEnd ); }
  
  // true if the head has been shot
  public boolean isHit() { return (mHitCounter > 0); }
  
  // the head dies the next time it is shot
  public void setKillable() { mKillable = true; }
  
  // methods required for the Obstacle interface
  public boolean isPlatform(int x, int y, int z) { return false; }
  public boolean isVoid(int x, int y, int z) { return false; }

  // whether there is space at the specified position
  public boolean isEmpty(int x, int y, int z) {
    
    if ( x >= mXPos && x <= mXPos+1 && 
         y >= mYPos && y <= mYPos+1 && 
         z >= mZPos && z <  mZPos+kZSize ) {
      return false;
    }
    return true;
 
  } // Obstacle.isEmpty()

  // whether the head intersects a particular position
  public boolean hits(int x, int y, int z) {
    
    if ( x >= mXPos && x <= mXPos+1 &&
         y >= mYPos && y <= mYPos+1 &&
         z >= mZPos && z <  mZPos+kZSize ) {
      return true;
    }
    return false;
  
  } // hits()

  // the head takes a hit
  public void stun() {

    if ( mHitCounter == 0 ) {
      if ( mKillable ) {
        mStage = 1;
        mLookCount = 100;
      } else {
        mLookCount = 0;
      }
      mHitCounter = 1;
      Env.sounds().play(Sounds.FLOOR_HIT);
    }
    
  } // stun()
  
  // update the head
  @Override
  public void advance(LinkedList<Sprite>     addTheseSprites,
                      LinkedList<Sprite>     killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {

    switch (mStage) {
    
      case 0: {
        // moving up
        if ( --mTimer <= 0 ) {
          mZPos += 1;
          mTimer = kZChangeDelay;
          if ( mZPos == kZPosTop ) {
            mStage = 1;
            mTimer = kLookDelay;
          }
          if ( mZPos == -3 ) Env.sounds().play(Sounds.FLOOR_PEEK);
        }
      } break;
    
      case 1: {
        // looking around
        if ( --mTimer <= 0 ) {
          if ( mLookCount > 0 ) {
            mLookCount -= 1;
            mLookingLeft = !mLookingLeft;
            mTimer = kLookDelay;
          } else {
            mStage = 2;
            mTimer = kZChangeDelay;
          }
        }      
      } break;
    
      case 2: {
        // moving down
        if ( --mTimer <= 0 ) {
          mZPos -= 1;
          mTimer = kZChangeDelay;
        }
      } break;
      
      default: assert(false);
    
    }
    
    if ( mHitCounter > 0 ) {
      mHitCounter++;
      if ( mKillable && mHitCounter > kKillDuration ) {
        mStage = 2;
        mZPos = kZPosEnd;
        addTheseSprites.add(new Splatter(mXPos, mYPos, 0, -1, 
                                         kZSize, (byte)37, -1));
        Env.sounds().play(Sounds.FLOOR_DEATH);
      }
    }
    
  } // Sprite.advance()

  // display the head
  @Override
  public void draw(EgaCanvas canvas) {

    final int x = mXPos - mCamera.xPos(),
              y = mYPos - mCamera.yPos(),
              z = mZPos - mCamera.zPos();

    boolean hit = ( mHitCounter > 0 && 
                    (mHitCounter < kHitDuration || mKillable) );
    
    EgaImage image = ( hit ? kHitImage 
                           : kBasicImages[mLookingLeft ? 0 : 1] );
    image.draw3D(canvas, 2*x, 2*y, z);

  } // Sprite.draw()

} // class FloorBossHead
