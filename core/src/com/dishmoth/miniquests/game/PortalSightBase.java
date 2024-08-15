/*
 *  PortalSightBase.java
 *  Copyright (c) 2024 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// a visibility post-process effect related to portal stones
abstract public class PortalSightBase extends Sprite3D {

  // story event: effect has ended
  public class EventEnded extends StoryEvent {
    public EventEnded() {}
  } // class PortalSight.EventEnded

  // reference to the player
  private Player mPlayer;

  // centre of the effect
  private float mXPos,
                mYPos,
                mZPos;

  // radius of the effect
  protected float mRadius = 5.0f;

  // random noise around the sight region
  private static final int kNoiseWidth = 10;
  private static final int kNoiseFlipRate = 3;
  private boolean mNoise[];

  // colour re-map table inside and outside the radius
  protected byte mRecolourInside[]  = null;
  protected byte mRecolourOutside[] = null;

  // utility to change how colours are mapped
  static protected void recolour(byte table[], char changes[][]) {

    assert( table.length == 64 );
    for ( char change[] : changes ) {
      assert( change.length == 2 );
      byte from = EgaTools.decodePixel(change[0]),
           to   = EgaTools.decodePixel(change[1]);
      assert( from >= 0 && from < 64 && to >= 0 && to < 64 );
      table[from] = to;
    }

  } // recolour()

  // constructor
  public PortalSightBase() {

    mRecolourInside  = new byte[64];
    mRecolourOutside = new byte[64];
    for ( byte i = 0 ; i < mRecolourInside.length ; i++ ) {
      mRecolourInside[i] = mRecolourOutside[i] = i;
    }

    mNoise = new boolean[kNoiseWidth * kNoiseWidth * 2];
    for ( int i = 0 ; i < mNoise.length ; i++ ) {
      mNoise[i] = Env.randomBoolean();
    }

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
      mPlayer = null;
    }

    super.observeDeparture(deadSprite);

  } // Sprite.observeDeparture()

  // end the effect
  abstract public void shutdown();

  // update the effect
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {

    for (int k = 0; k < kNoiseFlipRate; k++ ) {
      final int index = Env.randomInt(mNoise.length);
      mNoise[index] = !mNoise[index];
    }

  } // Sprite.advance()

  // update position to follow the player
  @Override
  public void aftermath(LinkedList<Sprite>     addTheseSprites,
                        LinkedList<Sprite>     killTheseSprites,
                        LinkedList<StoryEvent> newStoryEvents) {

    if ( mPlayer != null ) {
      mXPos = mPlayer.getXPos();
      mYPos = mPlayer.getYPos();
      mZPos = mPlayer.getZPos();
    }

  } // Sprite.aftermath()

  // HACK: ensure this sprite is always the last drawn
  public void checkSpriteOrder(SpriteManager spriteManager) {

    LinkedList<Sprite> sprites = spriteManager.list();
    if ( sprites.getLast() != this ) {
      sprites.remove(this);
      sprites.addLast(this);
    }

  } // checkSpriteOrder()

  // display the effect
  // note: this sprite must be the last thing drawn to the canvas
  @Override
  public void draw(EgaCanvas canvas) {

    byte pixels[] = canvas.pixels();
    float depths[] = canvas.depths();

    final float ix0 = Env.originXPixel() + 0.5f;
    final float iy0 = Env.originYPixel();

    int index = 0;
    for ( int iy = 0 ; iy < canvas.height() ; iy++ ) {
      for ( int ix = 0 ; ix < canvas.width() ; ix++ ) {
        final float depth = depths[index];

        final int bx = (ix % kNoiseWidth) + kNoiseWidth *(iy % kNoiseWidth);
        final int by = bx + kNoiseWidth * kNoiseWidth;
        final float jx = (ix - ix0) + 0.5f*(mNoise[bx] ? +1 : -1);
        final float jy = (iy - iy0) + 0.5f*(mNoise[by] ? +1 : -1);

        // depth = xPos + yPos;
        // 0.5*(x - Env.originXPixel()) = xPos - yPos,
        // (y - Env.originYPixel()) = - depth - zPos;
        // =>
        // xPos = 0.5*depth + 0.25*(x - Env.originXPixel()) // in blocks
        // yPos = 0.5*depth - 0.25*(x - Env.originXPixel()) // in blocks
        // zPos = -depth - ((y - Env.originYPixel())        // in pixels

        final float x = 0.5f*depth + 0.25f*jx + mCamera.xPos();
        final float y = 0.5f*depth - 0.25f*jx + mCamera.yPos();
        final float z = -(depth + jy) + mCamera.zPos();

        final float dx = x - mXPos;
        final float dy = y - mYPos;
        if ( dx*dx + dy*dy < mRadius*mRadius ) {
          pixels[index] = mRecolourInside[pixels[index]];
        } else {
          pixels[index] = mRecolourOutside[pixels[index]];
        }

        index += 1;
      }
    }

  } // Sprite.draw()

  // version of EgaImage.draw3D() that applies the sight effect
  public void draw3D(EgaCanvas canvas, EgaImage image,
                     int xPos, int yPos, int zPos, boolean inside) {

    final int depth = (int)Math.floor(0.5f*xPos) + (int)Math.ceil(0.5f*yPos);
    final int xPix  = Env.originXPixel() + xPos - yPos,
              yPix  = Env.originYPixel() - depth - zPos;
    final int x0    = xPix - image.refXPos() + canvas.refXPos(),
              y0    = yPix - image.refYPos() + canvas.refYPos();

    final int sx = Math.max(0, -x0),
              sy = Math.max(0, -y0),
              nx = Math.min(image.width(), canvas.width()-x0) - sx,
              ny = Math.min(image.height(), canvas.height()-y0) - sy,
              dx = Math.max(0, x0),
              dy = Math.max(0, y0);
    if ( nx <= 0 || ny <= 0 ) return;

    byte  srcPixels[]  = image.pixels(),
          destPixels[] = canvas.pixels();
    float srcDepths[]  = image.depths(),
          destDepths[] = canvas.depths();

    final int sGap = image.width() - nx;
    assert( sGap >= 0 );
    int sInd = sy*image.width() + sx;

    final float ix0 = Env.originXPixel() + 0.5f;
    final float iy0 = Env.originYPixel();

    for ( int ky = 0 ; ky < ny ; ky++, sInd+=sGap ) {
      for ( int kx = 0 ; kx < nx ; kx++, sInd++ ) {
        final byte pixel = srcPixels[sInd];
        if ( pixel < 0 ) continue;

        final int ix = dx + kx,
                  iy = dy + ky;
        final int dInd = iy*canvas.width() + ix;
        final float pixDepth = srcDepths[sInd] + depth;
        if ( pixDepth > destDepths[dInd] ) continue;

        final int bx = (ix % kNoiseWidth) + kNoiseWidth *(iy % kNoiseWidth);
        final int by = bx + kNoiseWidth * kNoiseWidth;
        final float jx = (ix - ix0) + 0.5f*(mNoise[bx] ? +1 : -1);
        final float jy = (iy - iy0) + 0.5f*(mNoise[by] ? +1 : -1);

        final float x = 0.5f*depth + 0.25f*jx + mCamera.xPos();
        final float y = 0.5f*depth - 0.25f*jx + mCamera.yPos();
        final float z = -(depth + jy) + mCamera.zPos();

        final float rx = x - mXPos;
        final float ry = y - mYPos;
        final boolean in = ( rx*rx + ry*ry < mRadius*mRadius );
        if ( in != inside ) continue;

        destPixels[dInd] = pixel;
        destDepths[dInd] = pixDepth;
      }
    }

  } // draw3D()

} // class PortalSightBase
