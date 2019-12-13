/*
 *  TriffidBoss.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.Arrays;
import java.util.LinkedList;

// a big enemy
public class TriffidBoss extends Sprite3D implements Obstacle {

  // story event: the boss has been killed
  public class EventKilled extends StoryEvent {
  } // class TriffidBoss.EventKilled

  // details of the basic image
  private static final int   kWidth      = 8,
                             kHeight     = 12;
  private static final int   kRefXPos    = 3,
                             kRefYPos    = 10;
  private static final float kBasicDepth = -0.02f,
                             kBaseDepth  = -0.05f;

  // images in various colour schemes 
  private static final char kBasicColours[] = {'a','4','W'},
                            kHitColours[]   = {'b','b','e'};

  // data for the basic rotating image
  private static final String kBasicPixels[] = {  "    22  " // x+ (0) 
                                                + "   0022 "
                                                + "  00002 "
                                                + "  00012 "
                                                + "  0001  "
                                                + "  0001  "
                                                + "  0001  "
                                                + "  0001  "
                                                + " 200012 "
                                                + "22101122"
                                                + " 222222 "
                                                + "   22   ",
                                                
                                                  "  22    " // y+ (1) 
                                                + " 2200   "
                                                + " 20000  "
                                                + " 20001  "
                                                + "  0001  "
                                                + "  0001  "
                                                + "  0001  "
                                                + "  0001  "
                                                + " 200012 "
                                                + "22101122"
                                                + " 222222 "
                                                + "   22   ",
                                                
                                                  "        " // x- (2) 
                                                + "  220   "
                                                + " 21120  "
                                                + " 22121  "
                                                + " 21121  "
                                                + "  2201  "
                                                + "  0001  "
                                                + "  0001  "
                                                + " 200012 "
                                                + "22101122"
                                                + " 222222 "
                                                + "   22   ",
                                                
                                                  "        " // y- (3) 
                                                + "   022  "
                                                + "  02112 "
                                                + "  02122 "
                                                + "  02112 "
                                                + "  0022  "
                                                + "  0001  "
                                                + "  0001  "
                                                + " 200012 "
                                                + "22101122"
                                                + " 222222 "
                                                + "   22   " };
  
  // data for the basic rotating image
  private static final String kGrowPixels[] = { "        " // (0) 
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "   22   "
                                              + "        "
                                              + "        ",
                                              
                                                "        " // (1) 
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "   22   "
                                              + "  2222  "
                                              + "   22   "
                                              + "        ",
                                              
                                                "        " // (2) 
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "  2222  "
                                              + " 222222 "
                                              + "  2222  "
                                              + "        ",
                                              
                                                "        " // (3) 
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "   22   "
                                              + " 222222 "
                                              + "22211222"
                                              + " 222222 "
                                              + "   22   ",
                                              
                                                "        " // (4) 
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "   22   "
                                              + " 222222 "
                                              + "22200222"
                                              + " 222222 "
                                              + "   22   ",
                                              
                                                "        " // (5) 
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "   22   "
                                              + " 220022 "
                                              + "22200222"
                                              + " 222222 "
                                              + "   22   ",
                                              
                                                "        " // (6) 
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "   00   "
                                              + " 200002 "
                                              + "22100122"
                                              + " 222222 "
                                              + "   22   ",
                                              
                                                "        " // (7) 
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "   00   "
                                              + "  0000  "
                                              + " 200012 "
                                              + "22101122"
                                              + " 222222 "
                                              + "   22   ",
                                              
                                                "        " // (8) 
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "   00   "
                                              + "  0000  "
                                              + "  0001  "
                                              + " 200012 "
                                              + "22101122"
                                              + " 222222 "
                                              + "   22   ",
                                              
                                                "        " // (9) 
                                              + "        "
                                              + "        "
                                              + "        "
                                              + "   00   "
                                              + "  0000  "
                                              + "  0001  "
                                              + "  0001  "
                                              + " 200012 "
                                              + "22101122"
                                              + " 222222 "
                                              + "   22   ",
                                              
                                                "        " // (10) 
                                              + "        "
                                              + "        "
                                              + "   00   "
                                              + "  0000  "
                                              + "  0001  "
                                              + "  0001  "
                                              + "  0001  "
                                              + " 200012 "
                                              + "22101122"
                                              + " 222222 "
                                              + "   22   ",
                                              
                                                "        " // (11) 
                                              + "        "
                                              + "   00   "
                                              + "  0000  "
                                              + "  0001  "
                                              + "  0001  "
                                              + "  0001  "
                                              + "  0001  "
                                              + " 200012 "
                                              + "22101122"
                                              + " 222222 "
                                              + "   22   ",
                                              
                                                "        " // (12) 
                                              + "   00   "
                                              + "  0000  "
                                              + "  0001  "
                                              + "  0001  "
                                              + "  0001  "
                                              + "  0001  "
                                              + "  0001  "
                                              + " 200012 "
                                              + "22101122"
                                              + " 222222 "
                                              + "   22   ",
                                              
                                                "        " // (13) 
                                              + "   00   "
                                              + "  0000  "
                                              + "  0022  "
                                              + "  0001  "
                                              + "  0001  "
                                              + "  0001  "
                                              + "  0001  "
                                              + " 200012 "
                                              + "22101122"
                                              + " 222222 "
                                              + "   22   ",
                                              
                                                "        " // (14) 
                                              + "   00   "
                                              + "  0022  "
                                              + "  02112 "
                                              + "  0022  "
                                              + "  0001  "
                                              + "  0001  "
                                              + "  0001  "
                                              + " 200012 "
                                              + "22101122"
                                              + " 222222 "
                                              + "   22   " };
  
  // image objects 
  private static EgaImage kBasicImages[],
                          kHitImages[],
                          kGrowImages[],
                          kHitGrowImages[];

  // different states
  enum TriffidState { kGrowing, kSearching, kHurting, kAttacking, kDying }

  // time (ticks) that the boss searches for
  private static final int kSearchDelay      = 200,
                           kSearchDelayStart = 100,
                           kSearchDelayEnd   = 50;
  
  // time (ticks) for recovery after being hit
  private static final int kHurtDelay = 7;

  // time (ticks) for an attack
  private static final int kAttackDelay = 30;
  
  // time (ticks) for the creature to grow a stage
  private static final int kGrowthDelay = 2,
                           kDeathDelay  = 2;
  
  // time (ticks) for the blast to detonate
  private static final int kBlastDelay = 15;
  
  // how fast the boss spins
  private static final int kRotateDelayMin = 3,
                           kRotateDelayMax = 15;

  // details of the boss's attack
  private static final float kBoltLength  = 1.0f,
                             kBoltSpeed   = 0.5f,
                             kBlastRange  = 4.0f,
                             kBlastLength = 3.0f,
                             kBlastSpeed  = 0.3f; 
  
  // position (x, y in block units, z in pixels)
  private int mXPos,
              mYPos,
              mZPos;

  // direction (see enumeration in Env)
  private int mDirec;

  // clockwise or anti-clockwise
  private boolean mRotateDirec;
  
  // how long until the triffid turns
  private int mRotateTimer;

  // which stage of growth the triffid is in
  private int mGrowth;

  // direction to face after being attacked (or -1)
  private int mRetaliateDirec;

  // current direction to the target (or -1)
  private int mTargetDirec;

  // how long the boss appears hurt for
  private int mHurtTimer;
  
  // how long until the boss goes back to searching
  private int mAttackTimer;
  
  // which direction the boss has fired in
  private int mBlastDirec;
  
  // how long until the blast detonates
  private int mBlastTimer;
  
  // how long until state changes
  private int mTimer;

  // current state
  private TriffidState mState;

  // notify that the boss is dead
  private boolean mDeathNotice;
  
  // prepare resources
  static public void initialize() {
    
    if ( kBasicImages != null ) return;
    
    float depths[] = new float[kWidth*kHeight];
    Arrays.fill(depths, kBasicDepth);
    for ( int iy = kRefYPos ; iy < kHeight ; iy++ ) {
      float val = kBaseDepth - (iy-kRefYPos);
      Arrays.fill(depths, iy*kWidth, (iy+1)*kWidth, val);
    }
    
    kBasicImages = makeImages(kBasicPixels, kBasicColours, depths);
    kHitImages = makeImages(kBasicPixels, kHitColours, depths);
    kGrowImages = makeImages(kGrowPixels, kBasicColours, depths);
    kHitGrowImages = makeImages(kGrowPixels, kHitColours, depths);
    
  } // initialize()

  // build a set of images
  static private EgaImage[] makeImages(String pixels[], 
                                       char colourMap[],
                                       float depths[]) {
    
    assert( colourMap != null && colourMap.length == 3 );
    
    EgaImage images[] = new EgaImage[pixels.length];
    for ( int k = 0 ; k < images.length ; k++ ) {
      images[k] = new EgaImage(kRefXPos, kRefYPos,
                               kWidth, kHeight,
                               EgaTools.convertColours(pixels[k], colourMap), 
                               depths);
    }

    return images;
    
  } // makeImages()
  
  // constructor
  public TriffidBoss(int x, int y, int z) {

    initialize();
    
    mXPos = x;
    mYPos = y;
    mZPos = z;

    mDirec = Env.DOWN;
    mRotateDirec = true;
    
    mRotateTimer = 0;
    
    mTimer = kGrowthDelay;
    mGrowth = 0;

    mRetaliateDirec = -1;
    mTargetDirec = -1;

    mHurtTimer = 0;
    mAttackTimer = 0;

    mBlastDirec = -1;
    mBlastTimer = 0;
    
    mState = TriffidState.kGrowing;

    mDeathNotice = false;
    
  } // constructor
  
  // current height of the triffid
  public int height() { return Math.max(0, Math.min(12,mGrowth)-5);  }
  
  // check for Sprites we want to keep track of
  @Override
  public void observeArrival(Sprite newSprite) { 

    super.observeArrival(newSprite);
    
    if ( newSprite instanceof Player ) {
      mSpritesToWatch.add(newSprite);
    }
    
  } // Sprite.observeArrival()
  
  // keep track of Sprites that have been destroyed
  @Override
  public void observeDeparture(Sprite deadSprite) {

    mSpritesToWatch.remove(deadSprite);

    super.observeDeparture(deadSprite);
    
  } // Sprite.observeDeparture()

  // methods required for the Obstacle interface
  public boolean isPlatform(int x, int y, int z) { return false; }
  public boolean isVoid(int x, int y, int z) { return false; }

  // whether there is space at the specified position
  public boolean isEmpty(int x, int y, int z) {
    
    if ( x >= mXPos && x <= mXPos+1 && 
         y >= mYPos && y <= mYPos+1 && 
         z >= mZPos && z <= mZPos+height() ) {
      return false;
    }
    return true;
 
  } // Obstacle.isEmpty()

  // whether the triffid intersects a particular position
  public boolean hits(int x, int y, int z) {
    
    if ( x >= mXPos && x <= mXPos+1 &&
         y >= mYPos && y <= mYPos+1 &&
         z >= mZPos && z <= mZPos+height() ) {
      return true;
    }
    return false;
    
  } // hits()
  
  // report that the triffid has been shot
  public void stun(int direc, boolean lethal) {

    if ( mState == TriffidState.kDying ) return;

    if ( lethal ) {
      mState = TriffidState.kDying;
      mTimer = kDeathDelay;
      mHurtTimer = 2*kHurtDelay;
      mDeathNotice = true;
      Env.sounds().play(Sounds.TBOSS_DEATH);
      return;
    }
    
    mHurtTimer = kHurtDelay;
    Env.sounds().play(Sounds.TBOSS_HIT);
    
    if ( mState != TriffidState.kGrowing ) {
      mState = TriffidState.kHurting;
      if ( mAttackTimer == 0 ) mAttackTimer = kAttackDelay;
    }

    if ( direc != -1 ) mRetaliateDirec = (direc+2) % 4;
    
  } // stun()

  // update the turret
  @Override
  public void advance(LinkedList<Sprite>     addTheseSprites,
                      LinkedList<Sprite>     killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {

    switch ( mState ) {
      
      case kGrowing: {
        if ( mHurtTimer > 0 ) mHurtTimer--;
        if ( --mTimer <= 0 ) {
          mTimer = kGrowthDelay;
          mGrowth += 1;
          if ( mGrowth == kGrowPixels.length-1 ) {
            if ( mHurtTimer == 0 ) {
              mState = TriffidState.kSearching;
              mTimer = kSearchDelay;
              mRotateTimer = kRotateDelayMax;
              mRotateDirec = Env.randomBoolean();
              mRetaliateDirec = -1;
              Env.sounds().play(Sounds.TBOSS_GRUNT);
            } else {
              mState = TriffidState.kHurting;
              mTimer = 0;
              mAttackTimer = kAttackDelay;
            }
          }
        }
      } break;

      case kSearching: {
        if ( --mRotateTimer <= 0 ) {
          double s = 1.0;
          if ( mTimer >= kSearchDelay - kSearchDelayStart ) {
            s = (kSearchDelay - mTimer)/(double)kSearchDelayStart;
          } else if ( mTimer <= kSearchDelayEnd ) {
            s = mTimer/(double)kSearchDelayEnd;
          }
          assert( s >= 0.0 && s <= 1.0 );
          double h = s*s;
          mRotateTimer = (int)Math.ceil( (1-h)*kRotateDelayMax 
                                         + h*kRotateDelayMin );
          mDirec = (mDirec + (mRotateDirec ? 1 : 3))%4;
        }
        if ( mTargetDirec == -1 ) {
          if ( mTimer <= kSearchDelayEnd ) {
            double s = mTimer/(double)kSearchDelayEnd;
            mTimer = kSearchDelay - (int)Math.floor(s*kSearchDelayStart);
          } else if ( mTimer < kSearchDelay - kSearchDelayStart ) {
            mTimer = kSearchDelay - kSearchDelayStart;
          } else if ( mTimer < kSearchDelay ) {
            mTimer++;
          }
        } else {
          if ( mTimer > 0 ) {
            mTimer--;
          } else {
            if ( mDirec == mTargetDirec ) {
              mState = TriffidState.kAttacking;
              mAttackTimer = kAttackDelay;
              Env.sounds().play(Sounds.TBOSS_GRUNT);
            }
          }
        }
      } break;
      
      case kHurting: {
        if ( mHurtTimer > 0 ) {
          if ( --mHurtTimer == 0 ) {
            mState = TriffidState.kAttacking;
            if ( mRetaliateDirec != -1 ) mDirec = mRetaliateDirec;
            mRetaliateDirec = -1;
          }
        }
      } break;
      
      case kAttacking: {
        if ( mAttackTimer > 0 ) {
          mAttackTimer--;
        } else {
          mState = TriffidState.kSearching;
          mTimer = kSearchDelay;
          mRotateTimer = kRotateDelayMax;
          mRotateDirec = Env.randomBoolean();
          mBlastDirec = mDirec;
          mBlastTimer = kBlastDelay;
          float x0 = (mXPos+0.5f) + 1.0f*Env.STEP_X[mBlastDirec],
                y0 = (mYPos+0.5f) + 1.0f*Env.STEP_Y[mBlastDirec],
                z0 = mZPos + 7.0f,
                x1 = (mXPos+0.5f) + 4.5f*Env.STEP_X[mBlastDirec],
                y1 = (mYPos+0.5f) + 4.5f*Env.STEP_Y[mBlastDirec],
                z1 = mZPos + 3.0f;
          FlameBolt f = new FlameBolt(x0,y0,z0, x1,y1,z1,
                                      kBoltLength, kBoltSpeed);
          f.setColourScheme(2);
          f.setPhysicsMode(0);
          addTheseSprites.add(f);
          Env.sounds().play(Sounds.TBOSS_FIRE);
        }
      } break;
      
      case kDying: {
        if ( mHurtTimer > 0 ) mHurtTimer--;
        if ( --mTimer <= 0 ) {
          mTimer = kDeathDelay;
          mGrowth -= 1;
          if ( mGrowth < 0 ) {
            killTheseSprites.add(this);
            addTheseSprites.add(new Splatter(mXPos, mYPos, mZPos, 
                                             -1, 2, (byte)4, -1));
            addTheseSprites.add(new Splatter(mXPos+1, mYPos, mZPos, 
                                             -1, 2, (byte)4, -1));
            addTheseSprites.add(new Splatter(mXPos, mYPos+1, mZPos, 
                                             -1, 2, (byte)4, -1));
            addTheseSprites.add(new Splatter(mXPos+1, mYPos+1, mZPos, 
                                             -1, 2, (byte)4, -1));
            Env.sounds().play(Sounds.TBOSS_SPLAT);
          }
        }
      } break;

      default: assert(false);
    }
    
    // detonate the blast
    if ( mBlastTimer > 0 ) {
      if ( --mBlastTimer == 0 ) {
        float x0 = (mXPos+0.5f) + 4.5f*Env.STEP_X[mBlastDirec],
              y0 = (mYPos+0.5f) + 4.5f*Env.STEP_Y[mBlastDirec],
              z  = mZPos + 3.0f,
              x1 = x0 + kBlastRange*Env.STEP_X[(mBlastDirec+1)%4],
              y1 = y0 + kBlastRange*Env.STEP_Y[(mBlastDirec+1)%4],
              x2 = x0 + kBlastRange*Env.STEP_X[(mBlastDirec+3)%4],
              y2 = y0 + kBlastRange*Env.STEP_Y[(mBlastDirec+3)%4];
        FlameBolt f1 = new FlameBolt(x0,y0,z, x1,y1,z, 
                                     kBlastLength, kBlastSpeed);
        f1.setColourScheme(2);
        addTheseSprites.add(f1);
        FlameBolt f2 = new FlameBolt(x0,y0,z, x2,y2,z,
                                     kBlastLength, kBlastSpeed);
        f2.setColourScheme(2);
        addTheseSprites.add(f2);
        mBlastDirec = -1;
      }
    }
    
  } // Sprite.advance()

  // check the player's position
  @Override
  public void interact() {

    mTargetDirec = -1;
    
    for ( Sprite sp : mSpritesToWatch ) {
      
      if ( sp instanceof Player ) {
        Player target = (Player)sp;
        float dx = target.getXPos() - (mXPos+0.5f),
              dy = target.getYPos() - (mYPos+0.5f);
        if ( Math.abs(dx) < 5 && Math.abs(dy) < 5 ) {
          if ( Math.abs(dx) > Math.abs(dy) ) {
            mTargetDirec = ( (dx > 0) ? Env.RIGHT : Env.LEFT );
          } else {
            mTargetDirec = ( (dy > 0) ? Env.UP : Env.DOWN );
          }
        }
      }
      
    }
    
  } // Sprite.interact()
  
  // pass on notification that the boss is dead
  @Override
  public void aftermath(LinkedList<Sprite>     addTheseSprites, 
                        LinkedList<Sprite>     killTheseSprites,
                        LinkedList<StoryEvent> newStoryEvents) {

    if ( mDeathNotice ) {
      newStoryEvents.add(new EventKilled());
      mDeathNotice = false;
    }
    
  } // Sprite.aftermath()
  
  // display the creature
  @Override
  public void draw(EgaCanvas canvas) {

    final int x = mXPos - mCamera.xPos(),
              y = mYPos - mCamera.yPos(),
              z = mZPos - mCamera.zPos();

    switch ( mState ) {
    
      case kSearching: 
      case kAttacking: {
        kBasicImages[mDirec].draw3D(canvas, 2*x, 2*y, z);
      } break;
    
      case kGrowing:
      case kDying: {
        if ( mHurtTimer > 0 ) {
          kHitGrowImages[mGrowth].draw3D(canvas, 2*x, 2*y, z);
        } else {
          kGrowImages[mGrowth].draw3D(canvas, 2*x, 2*y, z);
        }
      } break;
    
      case kHurting: {
        kHitImages[mDirec].draw3D(canvas, 2*x, 2*y, z);
      } break;
    
      default: assert(false);
    }

  } // Sprite.draw()

} // class TriffidBoss
