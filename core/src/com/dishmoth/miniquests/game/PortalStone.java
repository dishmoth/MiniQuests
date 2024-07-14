/*
 *  PortalStone.java
 *  Copyright (c) 2024 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// a mysterious standing stone
public class PortalStone extends Sprite3D implements Obstacle {

  // story event: stone has been activated
  public class EventActivated extends StoryEvent {
    public PortalStone mStone;
    public EventActivated(PortalStone s) { mStone = s; }
  } // class PortalStone.EventActivated

  // details of the basic image
  private static final int   kWidth   = 4,
                             kHeight  = 9;
  private static final int   kRefXPos = 1,
                             kRefYPos = 8;
  private static final float kDepth   = -0.01f;

  // data for the basic image
  private static final String kPixels = " 11 "
                                      + " 11 "
                                      + "1111"
                                      + "1110"
                                      + "1110"
                                      + "1110"
                                      + "0100"
                                      + "0000"
                                      + " 00 ";

  // details of the shadow images
  private static final int   kShadowWidth      = 6,
                             kShadowHeight     = 2,
                             kShadowBigWidth   = 10,
                             kShadowBigHeight  = 4;
  private static final int   kShadowRefXPos    = 2,
                             kShadowRefYPos    = 0,
                             kShadowBigRefXPos = 4,
                             kShadowBigRefYPos = 1;

  // data for the shadow images
  private static final String kPixelsShadow    = "GG  GG"
                                               + "  GG  ";
  private static final String kPixelsShadowBig = "  0    0  "
                                               + "00      00"
                                               + "  00  00  "
                                               + "    00    ";

  // different colours for stones
  private static final char kColourSchemes[][] = { { 'H', 'h' },   // blue
                                                   { '4', 'a' },   // red
                                                   { 'K', 'c' },   // orange
                                                   { 'Y', 'o' },   // green
                                                   { '5', 'j' },   // pink
                                                   { 'u', '7' } }; // grey

  // different colours for shadows
  private static final char kShadowColours[] = { 'h', '4', 'K', 'Y', '5' };

  // images of stones, different colours
  private static EgaImage kImages[]       = null;

  // images of shadows
  private static EgaImage kImageShadow      = null,
                          kImageShadowBig[] = null;

  // different states
  private enum State { kInactive, kAlert, kTriggered, kActive };

  // time (ticks) for colour flashes
  private static final int kFlashPeriod = 60,
                           kFlashDelay  = 4;

  // position of base point of stone
  final private int mXPos,
                    mYPos,
                    mZPos;

  // colour type
  final private int mColour;

  // current state
  private State mState;

  // how long until the colour flashes when alert
  private int mFlashTimer;

  // activation effect
  private FlameBeam mParticles;

  // prepare image
  public static void initialize(int colour) {

    if ( kImages == null ) {
      kImages         = new EgaImage[kColourSchemes.length];
      kImageShadowBig = new EgaImage[kShadowColours.length];
    }

    if ( kImages[colour] == null ) {
      char colours[] = kColourSchemes[colour];
      kImages[colour] = new EgaImage(kRefXPos, kRefYPos,
                                     kWidth, kHeight,
                                     EgaTools.convertColours(kPixels,
                                                             colours),
                                     kDepth);
    }

    if ( kImageShadow == null ) {
      float depths[] = new float[kShadowWidth * kShadowHeight];
      int index = 0;
      for ( int iy = 0 ; iy < kShadowHeight ; iy++ ) {
        for ( int ix = 0 ; ix < kShadowWidth ; ix++ ) {
          boolean flip = ((ix/2 + iy) % 2 == 0);
          depths[index++] = (kShadowRefYPos - iy) - (flip ? 0.06f : 0.003f);
        }
      }
      kImageShadow = new EgaImage(kShadowRefXPos, kShadowRefYPos,
                                  kShadowWidth, kShadowHeight,
                                  kPixelsShadow, depths);
    }

    if ( colour < kShadowColours.length && kImageShadowBig[colour] == null ) {
      float depths[] = new float[kShadowBigWidth * kShadowBigHeight];
      int index = 0;
      for ( int iy = 0 ; iy < kShadowBigHeight ; iy++ ) {
        for ( int ix = 0 ; ix < kShadowBigWidth ; ix++ ) {
          boolean flip = ((ix/2 + iy) % 2 == 0);
          depths[index++] = (kShadowBigRefYPos - iy) - (flip ? 0.06f : 0.003f);
        }
      }
      char colours[] = { kShadowColours[colour] };
      kImageShadowBig[colour] = new EgaImage(
                                     kShadowBigRefXPos, kShadowBigRefYPos,
                                     kShadowBigWidth, kShadowBigHeight,
                                     EgaTools.convertColours(kPixelsShadowBig,
                                                             colours),
                                     depths);
    }

  } // initialize()

  // constructor
  public PortalStone(int xPos, int yPos, int zPos, int colour) {
    
    assert( colour >= 0 && colour < kColourSchemes.length-1 );
    mColour = colour;

    initialize(mColour);
    initialize(kColourSchemes.length - 1);

    mXPos = xPos;
    mYPos = yPos;
    mZPos = zPos;

    mState = State.kInactive;
    mFlashTimer = -1;

  } // constructor

  // is the stone awake?
  public boolean isActive() { return (mState == State.kActive); }

  // get ready to wake the stone
  public void setAlert() {

    assert( mState == State.kInactive );
    mState = State.kAlert;
    mFlashTimer = kFlashPeriod;

  } // setAlert()

  // put the stone back to sleep
  public void setInactive() {

    assert( mState == State.kAlert || mState == State.kActive );
    mState = State.kInactive;
    mFlashTimer = -1;

  } // setInactive()

  // position access
  public int getXPos() { return mXPos; }
  public int getYPos() { return mYPos; }

  // whether the player can stand at the specified position
  public boolean isPlatform(int x, int y, int z) {

    return false;
    
  } // Obstacle.isPlatform()

  // whether there is space at the specified position
  public boolean isEmpty(int x, int y, int z) {

    if ( x == mXPos && y == mYPos && z >= mZPos && z < mZPos+kHeight ) {
      return false;
    }
    return true;
  
  } // Obstacle.isEmpty()

  // whether the position is outside of the game world
  public boolean isVoid(int x, int y, int z) { 

    return false;
    
  } // Obstacle.isVoid()

  // register a hit by a bullet
  public void hit() {

    if (mState == State.kAlert) {
      mState = State.kTriggered;
      mFlashTimer = 8;
      mParticles.setFlame(true);
      mParticles.warmUp(15);
    }

  } // hit()

  // advance timers, etc.
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {

    if ( mParticles == null && mState == State.kAlert ) {
      mParticles = new FlameBeam(mXPos + 0.5f, mYPos + 0.5f, mZPos - 1,
                                 mXPos + 0.5f, mYPos + 0.5f, mZPos + 7);
      byte c0 = EgaTools.decodePixel(kColourSchemes[mColour][0]);
      byte c1 = EgaTools.decodePixel(kColourSchemes[mColour][1]);
      byte colsByte[] = {c0, c0, c0, c1};
      mParticles.setColours(colsByte);
      mParticles.setLethal(false);
      mParticles.setFlame(false);
      addTheseSprites.add(mParticles);
    }

    if ( mState == State.kAlert ) {
      if ( --mFlashTimer < 0 ) mFlashTimer += kFlashPeriod;
    } else if ( mState == State.kTriggered ) {
      if ( mFlashTimer >= 0 ) {
        mFlashTimer -= 1;
      } else {
        mState = State.kActive;
        newStoryEvents.add(new EventActivated(this));
      }
      mParticles.setFlame(false);
    }

  } // Sprite.advance()

  // display the object
  @Override
  public void draw(EgaCanvas canvas) {

    final int xPos = mXPos - mCamera.xPos(),
              yPos = mYPos - mCamera.yPos(),
              zPos = mZPos - mCamera.zPos();

    int col = (kColourSchemes.length - 1);
    if ( mState == State.kAlert &&
         mFlashTimer >= 0 && mFlashTimer < kFlashDelay ) {
      col = mColour;
    }
    kImages[col].draw3D(canvas, 2*xPos, 2*yPos, zPos);

    kImageShadow.draw3D(canvas, 2 * xPos, 2 * yPos, zPos);

    if ( mState == State.kTriggered ) {
      kImageShadowBig[mColour].draw3D(canvas, 2 * xPos, 2 * yPos, zPos);
    }

  } // Sprite.draw()

} // class PortalStone
