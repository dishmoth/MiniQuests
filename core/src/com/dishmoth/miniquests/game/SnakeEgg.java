/*
 *  SnakeEgg.java
 *  Copyright (c) 2019 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// a dormant snake
public class SnakeEgg extends Sprite3D implements Obstacle {

  // details of the egg image
  private static final int   kImageWidth   = 4,
                             kImageHeight  = 4;
  private static final int   kImageRefXPos = 1,
                             kImageRefYPos = 3;
  private static final float kImageDepth   = -0.02f;

  // times for various actions
  private static final int kFlashDelayMin   = 20,
                           kFlashDelayMax   = 40,
                           kFlashTime       = 4;
  
  // data for the egg image
  private static final String kImagePixels = " 00 "
                                           + "0100"
                                           + "0100"
                                           + " 00 ";
  
  // different colour schemes
  private static final char kColourSchemes[][] = { { '0', 'u' },   // black
                                                   { 'G', 'G' },   // green
                                                   { 'q', 'q' },   // orange
                                                   { 'B', 'B' } }; // blue
                                                     
  // egg image objects (one for each colour scheme)
  private static final EgaImage kImages[];

  // position
  private final int mXPos,
                    mYPos,
                    mZPos;
  
  // which snake hatches from the egg (1, 2 or 3; 0 for inactive)
  private final int mType;

  // true when the egg is getting ready to hatch
  private boolean mHatching;

  // flashes until the egg hatches
  private int mNumFlashes;
  
  // time until the next flash
  private int mTimer;
  
  // time during a flash (zero when not flashing)
  private int mFlashTimer;

  // reference to the player (or null)
  private Player mPlayer;
  
  // initialize images
  static {
    
    kImages = new EgaImage[kColourSchemes.length];
    for ( int k = 0 ; k < kImages.length ; k++ ) {
      kImages[k] = new EgaImage(kImageRefXPos, kImageRefYPos,
                                kImageWidth, kImageHeight,
                                EgaTools.convertColours(kImagePixels,
                                                        kColourSchemes[k]), 
                                kImageDepth);
    }

  } // static

  // constructor
  public SnakeEgg(int x, int y, int z, int type) {
    
    mXPos = x;
    mYPos = y;
    mZPos = z;
    
    assert( type >= 0 && type <= 3 );
    mType = type;
    
    mHatching = false;
    
    mNumFlashes = 3;
    mTimer = Env.randomInt(kFlashDelayMin, kFlashDelayMax);
    mFlashTimer = 0;
    
  } // constructor
  
  // check for Sprites we want to keep track of
  @Override
  public void observeArrival(Sprite newSprite) { 

    super.observeArrival(newSprite);
    
    if ( newSprite instanceof Player ) {
      assert( mPlayer == null );
      mPlayer = (Player)newSprite;
    }
    
  } // Sprite.observeArrival()
  
  // keep track of Sprites that have been destroyed
  @Override
  public void observeDeparture(Sprite deadSprite) {

    if ( deadSprite instanceof Player ) {
      assert( mPlayer == deadSprite );
      mPlayer = null;
    }

    super.observeDeparture(deadSprite);
    
  } // Sprite.observeDeparture()
  
  // whether there is space at the specified position
  @Override
  public boolean isEmpty(int x, int y, int z) {
    
    return ( x != mXPos || y != mYPos || z < mZPos || z > mZPos+3 );
    
  } // Obstacle.isEmpty()

  // methods for the Obstacle interface
  @Override public boolean isPlatform(int x, int y, int z) { return false; }
  @Override public boolean isVoid(int x, int y, int z) { return false; }

  // hatch the egg
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {

    if ( mType == 0 ) return;
    
    if ( mFlashTimer > 0 ) mFlashTimer -= 1;
    
    if ( mHatching ) {

      if ( -- mTimer < 0 ) {
        if ( --mNumFlashes == 0 ) {
          killTheseSprites.add(this);
          addTheseSprites.add(new Splatter(mXPos, mYPos, mZPos,
                                           -1, 4, (byte)0, -1));
          final int direc = Env.randomInt(4);
          if ( mType == 1 ) {
            addTheseSprites.add(new SnakeBoss1(mXPos, mYPos, mZPos, direc));
          } else if ( mType == 2 ) {
            addTheseSprites.add(new SnakeBoss2(mXPos, mYPos, mZPos, direc));
          } else if ( mType == 3 ) {
            addTheseSprites.add(new SnakeBoss3(mXPos, mYPos, mZPos, direc));
          } else {
            assert(false);
          }
        } else {
          mTimer = Env.randomInt(kFlashDelayMin, kFlashDelayMax);
          mFlashTimer = kFlashTime;
          Env.sounds().play(Sounds.TICK); // TODO
        }
      }
      
    } else {
      
      final int xMin = Room.kSize * (mXPos / Room.kSize),
                yMin = Room.kSize * (mYPos / Room.kSize),
                xMax = xMin + (Room.kSize - 1),
                yMax = yMin + (Room.kSize - 1);
      if ( mPlayer != null &&
           mPlayer.getXPos() >= xMin && mPlayer.getXPos() <= xMax &&
           mPlayer.getYPos() >= yMin && mPlayer.getYPos() <= yMax ) {
        mHatching = true;
      }
      
    }
    
  } // Sprite3D.advance()

  // display the egg
  @Override
  public void draw(EgaCanvas canvas) {

    final int x = mXPos - mCamera.xPos(),
              y = mYPos - mCamera.yPos(),
              z = mZPos - mCamera.zPos();

    final int col = (mFlashTimer > 0 ? mType : 0);
    
    kImages[col].draw3D(canvas, 2*x, 2*y, z);
    
  } // Sprite3D.draw()
  
} // class SnakeEgg
