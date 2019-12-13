/*
 *  Dragon.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// a big enemy
public class Dragon extends Sprite3D implements Obstacle {

  // details of the basic image
  private static final int   kWidth      = 8,
                             kHeight     = 11;
  private static final int   kRefXPos    = 3,
                             kRefYPos    = 9;
  private static final float kBasicDepth = -0.02f;

  // images in various colour schemes 
  private static final char kBasicColours[] = {'W','K'},
                            kHitColours[]   = {'s','s'};

  // data for the basic image
  private static final String kBasicPixels[] = {  "   1 1  " // y- (0) 
                                                + "   100  "
                                                + "   1111 "
                                                + "   0101 "
                                                + "   00   "
                                                + "   00   "
                                                + "   00   "
                                                + "   00   "
                                                + "  0010  "
                                                + "  0010  "
                                                + "   00   ",
                                                
                                                  "   1 1  " // x+ (1) 
                                                + "   001  "
                                                + "   0011 "
                                                + "   0001 "
                                                + "   00   "
                                                + "   00   "
                                                + "   00   "
                                                + "   00   "
                                                + "  0000  "
                                                + "  0000  "
                                                + "   00   " };
  
  // data for the basic depths
  private static final String kBasicDepths[] = {  "   5 5  " // y- (0) 
                                                + "   555  "
                                                + "   5777 "
                                                + "   5777 "
                                                + "   55   "
                                                + "   55   "
                                                + "   55   "
                                                + "   55   "
                                                + "  5665  "
                                                + "  5665  "
                                                + "   66   ",
                                                
                                                  "   5 5  " // x+ (1) 
                                                + "   555  "
                                                + "   5553 "
                                                + "   5553 "
                                                + "   55   "
                                                + "   55   "
                                                + "   55   "
                                                + "   55   "
                                                + "  5665  "
                                                + "  5665  "
                                                + "   66   " };
  
  // data for the attacking image
  private static final String kAttackPixels[] = {  "        " // y- (0) 
                                                 + "    1 1 "
                                                 + "    100 "
                                                 + "   01111"
                                                 + "   00101"
                                                 + "   00   "
                                                 + "   00   "
                                                 + "   00   "
                                                 + "  0010  "
                                                 + "  0010  "
                                                 + "   00   ",
                                                
                                                   "        " // x+ (1) 
                                                 + "    1 1 "
                                                 + "    001 "
                                                 + "    0011"
                                                 + "   00001"
                                                 + "   000  "
                                                 + "   00   "
                                                 + "   00   "
                                                 + "  0000  "
                                                 + "  0000  "
                                                 + "   00   " };
  
  // data for the basic depths
  private static final String kAttackDepths[] = {  "        " // y- (0) 
                                                 + "    7 7 "
                                                 + "    999 "
                                                 + "   57999"
                                                 + "   55999"
                                                 + "   55   "
                                                 + "   55   "
                                                 + "   55   "
                                                 + "  5665  "
                                                 + "  5665  "
                                                 + "   66   ",
                                                 
                                                   "        " // x+ (1) 
                                                 + "    3 3 "
                                                 + "    333 "
                                                 + "    4443"
                                                 + "   55533"
                                                 + "   553  "
                                                 + "   55   "
                                                 + "   55   "
                                                 + "  5665  "
                                                 + "  5665  "
                                                 + "   66   " };
   
  // image objects 
  private static EgaImage kBasicImages[],
                          kAttackImages[],
                          kHitBasicImages[],
                          kHitAttackImages[];

  // different modes of behaviour
  enum DragonState { kRising, kWaiting, kAttacking, kSinking, kDying }
  
  // final height above liquid
  private static final int kFullHeight = 9,
                           kHalfHeight = 7;
  
  // times for things to happen
  private static final int kRiseStartDelay   = 20,
                           kRiseRate         = 2,
                           kRiseBubbles1Time = 25,
                           kRiseBubbles2Time = 15;
  private static final int kWait1Delay       = 25,
                           kWait2Delay       = 10,
                           kAttackDelay      = 20;
  private static final int kBlastEndDelay    = 20,
                           kBlastKillDelay   = 50;
  private static final int kSinkRate         = 3,
                           kSinkBubbles1Time = 20,
                           kSinkBubbles2Time = 10;
  private static final int kDeathDelay       = 10;

  // details of the dragon's attack
  private static final float kAttackOffsetXy = 1.5f,
                             kAttackOffsetZ1 = 4.0f,
                             kAttackOffsetZ2 = 3.0f;
  private static final float kBoltLength     = 4.0f,
                             kBoltSpeed      = 0.5f;

  // how long the image changes for when hit
  private static final int kHitDelay = 5;
  
  // current behaviour
  private DragonState mState;
  
  // how long different states last for
  private int mTimer;

  // position (x, y in block units, z in pixels)
  private int mXPos,
              mYPos,
              mZPos;

  // direction (see enumeration in Env)
  private int mDirec;

  // how high the dragon will rise
  private int mTargetHeight;
  
  // height (pixels) of dragon above base
  private int mHeight;
  
  // reference to the attack bolt
  private FlameBolt mFlameBolt;
  
  // reference to the blast flame
  private Flame mBlast;

  // how long the blast has been around for
  private int mBlastTimer;

  // how long the hit image lasts for
  private int mHitTimer;
  
  // attack target
  private int mTargetXPos,
              mTargetYPos,
              mTargetZPos;
  
  // whether a hit will kill the dragon
  private boolean mKillable;
  
  // prepare resources
  static public void initialize() {
    
    if ( kBasicImages != null ) return;
    
    kBasicImages = makeImages(kBasicPixels, kBasicColours, kBasicDepths);
    kAttackImages = makeImages(kAttackPixels, kBasicColours, kAttackDepths);
    kHitBasicImages = makeImages(kBasicPixels, kHitColours, kBasicDepths);
    kHitAttackImages = makeImages(kAttackPixels, kHitColours, kBasicDepths);
    
  } // initialize()

  // build a set of images
  static private EgaImage[] makeImages(String pixels[], 
                                       char colourMap[],
                                       String depthStrings[]) {
    
    assert( colourMap != null && colourMap.length == 2 );
    assert( pixels.length == depthStrings.length );
    
    EgaImage images[] = new EgaImage[pixels.length];
    for ( int k = 0 ; k < images.length ; k++ ) {
      images[k] = new EgaImage(kRefXPos, kRefYPos,
                               kWidth, kHeight,
                               EgaTools.convertColours(pixels[k], colourMap), 
                               makeDepths(depthStrings[k]));
    }

    return reorderImages(images);
    
  } // makeImages()
  
  // construct pixel depths from a string
  static private float[] makeDepths(String depthString) {
  
    float depths[] = new float[kWidth*kHeight];
    for ( int iy = 0 ; iy < kHeight ; iy++ ) {
      for ( int ix = 0 ; ix < kWidth ; ix++ ) {
        int ind = iy*kWidth + ix;
        char ch = depthString.charAt(ind);
        if ( ch == ' ' ) continue;
        float val = kBasicDepth - 0.5f*(ch-'5');
        depths[ind] = val;
      }
    }
    return depths;
    
  } // makeDepths()

  // reflect some images and make the list match the standard directions
  static private EgaImage[] reorderImages(EgaImage oldImages[]) {
    
    assert( oldImages.length == 2 );
    
    EgaImage newImages[] = new EgaImage[4];

    newImages[Env.RIGHT] = oldImages[1];
    newImages[Env.UP]    = EgaTools.reflectX(oldImages[1], kRefXPos);
    newImages[Env.LEFT]  = EgaTools.reflectX(oldImages[0], kRefXPos);
    newImages[Env.DOWN]  = oldImages[0];
    
    return newImages;
    
  } // reorderImages()
  
  // constructor
  public Dragon(int x, int y, int z, 
                int direc, boolean fullHeight,
                int targetRange, int targetZPos) {

    initialize();
    
    mXPos = x;
    mYPos = y;
    mZPos = z;

    assert( direc >= 0 && direc < 4 );
    mDirec = direc;

    mTargetHeight = (fullHeight ? kFullHeight : kHalfHeight);
    mHeight = 0;

    mTargetXPos = mXPos + targetRange*Env.STEP_X[direc];
    mTargetYPos = mYPos + targetRange*Env.STEP_Y[direc];
    mTargetZPos = targetZPos;

    mState = DragonState.kRising;
    mTimer = 0;

    mFlameBolt = null;
    mBlast = null;
    mBlastTimer = 0;
    mHitTimer = 0;
    
    mKillable = false;
    
  } // constructor
  
  // z-position adjusted for height
  protected int zBase() { return (mZPos + mHeight - kFullHeight); } 

  // specify what happens to the dragon when it is shot
  public void setKillable(boolean v) { mKillable = v; }
  
  // methods required for the Obstacle interface
  public boolean isPlatform(int x, int y, int z) { return false; }
  public boolean isVoid(int x, int y, int z) { return false; }

  // whether there is space at the specified position
  public boolean isEmpty(int x, int y, int z) {
    
    if ( x >= mXPos && x <= mXPos && 
         y >= mYPos && y <= mYPos && 
         z >= mZPos && z <= mZPos+mHeight ) {
      return false;
    }
    return true;
 
  } // Obstacle.isEmpty()

  // whether the dragon intersects a particular position
  public boolean hits(int x, int y, int z) {
    
    if ( x >= mXPos && x <= mXPos &&
         y >= mYPos && y <= mYPos &&
         z >= mZPos && z <  mZPos+mHeight ) {
      return true;
    }
    return false;
  
  } // hits()

  // the dragon takes a hit
  public void stun() {
    
    if ( mState == DragonState.kRising || 
         mState == DragonState.kWaiting ) {
      mState = ( mKillable ? DragonState.kDying : DragonState.kSinking );
      mTimer = 0;
      Env.sounds().play(Sounds.DRAGON_HIT);
    }
      
    mHitTimer = kHitDelay;
    
  } // stun()
  
  // update the dragon
  @Override
  public void advance(LinkedList<Sprite>     addTheseSprites,
                      LinkedList<Sprite>     killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {

    final boolean fullHeight = (mTargetHeight == kFullHeight);
    
    switch ( mState ) {
      
      case kRising: {
        if ( mTimer == 0 ) {
          final int t = (fullHeight ? kRiseBubbles1Time : kRiseBubbles2Time);
          addTheseSprites.add( new DragonBubbles(mXPos, mYPos, mZPos, t) );
        }
        int timeSince = mTimer - kRiseStartDelay; 
        if ( timeSince >= 0 && (timeSince % kRiseRate) == 0 ) {
          mHeight += 1;
          if ( mHeight == mTargetHeight ) {
            mState = DragonState.kWaiting;
            mTimer = 0;
          }
          if ( mHeight == 1 ) Env.sounds().play(Sounds.DRAGON_EMERGE);
        }
      
      } break;
      
      case kWaiting: {
        final int waitDelay = ( fullHeight ? kWait1Delay : kWait2Delay ); 
        if ( mTimer == waitDelay ) {
          mState = DragonState.kAttacking;
          mTimer = 0;
        }
      } break;
      
      case kAttacking: {
        if ( mTimer == 1 ) {
          boolean away = ( mDirec == Env.RIGHT || mDirec == Env.UP );
          float x = mXPos + kAttackOffsetXy*Env.STEP_X[mDirec],
                y = mYPos + kAttackOffsetXy*Env.STEP_Y[mDirec],
                z = mZPos + mHeight - (away?kAttackOffsetZ1:kAttackOffsetZ2);
          mFlameBolt = new FlameBolt(x, y, z, 
                                     mTargetXPos, mTargetYPos, mTargetZPos,
                                     kBoltLength, kBoltSpeed);
          mFlameBolt.setColourScheme(1);
          mFlameBolt.setPhysicsMode(0);
          addTheseSprites.add(mFlameBolt);
          Env.sounds().play(Sounds.DRAGON_FIRE);
        } else if ( mTimer >= kAttackDelay ) {
          mState = DragonState.kSinking;
          mTimer = 0;
        }
      } break;
      
      case kSinking: {
        // sink below the lava and vanish
        if ( mTimer == 1 ) {
          final int t = (fullHeight ? kSinkBubbles1Time : kSinkBubbles2Time);
          DragonBubbles bubbles = new DragonBubbles(mXPos, mYPos, mZPos, t);
          bubbles.warmUp();
          addTheseSprites.add(bubbles);
        }
        if ( mTimer % kSinkRate == 0 ) {
          mHeight -= 1;
          assert( mHeight > -100 );
          if ( mHeight <= 0 && mFlameBolt == null && mBlast == null ) {
            killTheseSprites.add(this);
          }
        }
      } break;
      
      case kDying: {
        if ( mTimer == kDeathDelay ) {
          killTheseSprites.add(this);
          addTheseSprites.add(new Splatter(mXPos, mYPos, mZPos, -1, 
                                           mHeight, (byte)54, -1));
          Env.sounds().play(Sounds.TBOSS_SPLAT);
        }
        mHitTimer++;
      } break;
      
    }
    
    mTimer++;
    if ( mHitTimer > 0 ) mHitTimer--;
    
    // control the flame bolt
    if ( mFlameBolt != null && mFlameBolt.atTarget() ) {
      mFlameBolt = null;
      mBlast = new Flame(mTargetXPos, mTargetYPos, mTargetZPos);
      mBlast.setColourScheme(1);
      addTheseSprites.add(mBlast);
      mBlastTimer = 0;
    }
    
    // control the attack blast
    if ( mBlast != null ) {
      mBlastTimer++;
      if ( mBlastTimer == kBlastEndDelay ) {
        mBlast.setFlame(false);
      } else if ( mBlastTimer == kBlastKillDelay ) {
        killTheseSprites.add(mBlast);
        mBlast = null;
      }
    }
    
  } // Sprite.advance()

  // display the creature
  @Override
  public void draw(EgaCanvas canvas) {

    final int x = mXPos - mCamera.xPos(),
              y = mYPos - mCamera.yPos(),
              z = zBase() - mCamera.zPos();

    EgaImage images[] = kBasicImages;
    if ( mHitTimer == 0 ) {
      if ( mState == DragonState.kAttacking ) images = kAttackImages;
    } else {
      images = kHitBasicImages;
      if ( mState == DragonState.kAttacking ) images = kHitAttackImages;
    }
    
    images[mDirec].draw3D(canvas, 2*x, 2*y, z);

  } // Sprite.draw()

} // class Dragon
