/*
 *  FloorBoss.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// a big enemy that's also the floor
public class FloorBoss extends BlockArray {

  // story event: the boss has been killed
  public class EventKilled extends StoryEvent {
  } // class FloorBoss.EventKilled

  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#:",   // plain floor 
                                                  "D:",   // danger circle
                                                  "zD" }; // blast pattern

  // size of the circle
  private static final float kMaxRadius  = 3.5f,
                             kRoomRadius = 15;
  
  // how fast the radius changes
  private static final float kGrowRate     = 0.20f,
                             kGrowDeadRate = 0.40f,
                             kShrinkRate   = 0.35f;
  
  // initial position
  private static final float kStartXPos = 5.0f,
                             kStartYPos = 0.0f;

  // hunting parameters
  private static final float kSpeed = 0.1f;
  
  // mouth parameters
  private static final int kMouthPeriod[]     = { 42, 32 };
  private static final int kMouthSlowStart    = 55,
                           kMouthSwitchTime   = 10;
  private static final int kMouthSoundTimeA[] = { 27, 22 },
                           kMouthSoundTimeB[] = { 15, 15 };
  
  // time range until a switch appears or disappears
  private static final int kSwitchInitialDelay  = 200,
                           kSwitchMinDelay      = 30,
                           kSwitchMaxDelay      = 60;
  private static final int kSwitchVanishDelay[] = { 230, 150 }; //{ 220, 130 }: 
  
  // how long things take
  private static final int kWakeDelay  = 30,
                           kBlastDelay = 8,
                           kDyingDelay = 40;
  
  // how many times the head needs to be shot
  private static final int kNumHeadLives = kSwitchVanishDelay.length;
  
  // different modes of behaviour
  private enum State { kSleeping, kWaking, kHunting, kBlasting, 
                       kPeeking, kDying, kDead };
  
  // current centre position (between 0 and Room.kSize)
  private float mXPos,
                mYPos;
  
  // current movement rate
  private float mXVel,
                mYVel;

  // current behaviour
  private State mState;

  // current circle size
  private float mFullSize;
  
  // counter for mouth opening and closing
  private int mMouthTimer;
  
  // reference to the player
  private Player mPlayer;
  
  // reference to the slave switch (or null)
  private FloorSwitch mSwitch;

  // keep track of various times
  private int mTimer;

  // reference to the slave head object (or null)
  private FloorBossHead mHead;

  // number of hits on the head
  private int mHeadHits;
  
  // reference to the slave object underneath us
  private Liquid mLiquid;
  
  // nominal liquid position on which to base scrolling 
  private int mLiquidXPos,
              mLiquidYPos;

  // constructor
  public FloorBoss() {
    
    super(new String[][]{{"-"}}, kBlockColours, 0,0,0);

    mXPos = 0.0f;
    mYPos = 0.0f;
    
    mXVel = 0.0f;
    mYVel = 0.0f;

    mState = State.kSleeping;
    
    mPlayer = null;

    mFullSize = 0.0f;
    mMouthTimer = 0;

    mSwitch = null;
    mTimer = 0;

    mHead = null;
    mHeadHits = 0;
    
    mLiquid = null;
    mLiquidXPos = mLiquidYPos = 0;

    buildFloor();
    
  } // constructor

  // the boss has been killed already
  public void setDead() {
    
    mState = State.kDead; 
    mFullSize = kRoomRadius;
    mMouthTimer = 0;
    
  } // setDead()
  
  // whether the boss has been killed
  public boolean isDead() { return (mState == State.kDead); } 
  
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
      assert( deadSprite == mPlayer );
      mPlayer = null;
    }

    super.observeDeparture(deadSprite);
    
  } // Sprite.observeDeparture()

  // whether the player can stand at the specified position
  public boolean isPlatform(int x, int y, int z) { 
    
    return super.isPlatform(x, y, z); 
    
  } // Obstacle.isPlatform()
  
  // whether there is space at the specified position
  // (note: a platform position is never empty by this definition)
  public boolean isEmpty(int x, int y, int z) {
    
    return super.isEmpty(x, y, z);
 
  } // Obstacle.isEmpty()

  // method required for the Obstacle interface
  public boolean isVoid(int x, int y, int z) { return false; }

  // reconstruct the floor blocks
  private void buildFloor() {

    float period = kMouthPeriod[ Math.min(kMouthPeriod.length-1, mHeadHits) ]; 
    float th = Math.min(1.0f, mMouthTimer/period);
    float cth = (float)Math.cos(2.0*Math.PI*th);
    float mouthRadius = 0.5f*(1.0f-cth)*mFullSize;
    
    String blocks[][] = new String[1][Room.kSize];
    
    for ( int iy = 0 ; iy < Room.kSize ; iy++ ) {
      StringBuilder row = new StringBuilder();
      float y = Room.kSize - (iy + 0.5f);
      for ( int ix = 0 ; ix < Room.kSize ; ix++ ) {
        float x = ix + 0.5f;
        char ch = '0';

        float dx = x - mXPos,
              dy = y - mYPos;
        float d2 = dx*dx + dy*dy;
        if      ( d2 < mouthRadius*mouthRadius ) ch = ' ';
        else if ( d2 < mFullSize*mFullSize )     ch = '1';
        
        if ( mState == State.kBlasting) ch = '2';
        
        row.append(ch);
      }
      blocks[0][iy] = row.toString();
    }
    
    setBlocks(blocks, super.getXPos(), super.getYPos(), super.getZPos());
  
    if ( mSwitch != null ) {
      float dx = mSwitch.getXPos() + 0.5f - mXPos,
            dy = mSwitch.getYPos() + 0.5f - mYPos;
      float d2 = dx*dx + dy*dy;
      if ( d2 < mouthRadius*mouthRadius ) {
        mSwitch.mDrawDisabled = mSwitch.mAdvanceDisabled = true;
      } else {
        mSwitch.mDrawDisabled = mSwitch.mAdvanceDisabled = false;        
      }
    }
    
  } // buildFloor()
  
  // move to a convenient position for the head to appear
  private void adjustPosition() {
    
    mXPos = Math.max(2, Math.min(8, Math.round(mXPos)));
    mYPos = Math.max(2, Math.min(8, Math.round(mYPos)));
    
    if ( mPlayer == null ) return;

    // nasty (and probably unnecessary) hack to keep the head 
    // from appearing too near to the player
    
    final int maxRange = 10;
    final float minDist = 2.0f;
    
    for ( int range = 0 ; range <= maxRange ; range++ ) {
      
      int x = Math.round(mXPos),
          y = Math.round(mYPos);
      
      int xMin = Math.max(2, x-range),
          xMax = Math.min(8, x+range),
          yMin = Math.max(2, y-range),
          yMax = Math.min(8, y+range);
      
      x = Env.randomInt(xMin, xMax);
      y = Env.randomInt(yMin, yMax);
    
      float dx = (mPlayer.getXPos() + 0.5f) - x,
            dy = (mPlayer.getYPos() + 0.5f) - y;
      if ( dx*dx + dy*dy > minDist*minDist ) {
        mXPos = x;
        mYPos = y;
        return;
      }
      
    }
    
  } // adjustPosition()
  
  // update the boss
  @Override
  public void advance(LinkedList<Sprite>     addTheseSprites,
                      LinkedList<Sprite>     killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {

    mXPos += mXVel;
    mYPos += mYVel;
    
    switch (mState) {
    
      case kSleeping: {
        // waiting for the player
        if ( mMouthTimer > 0 ) {
          mMouthTimer--;
          playMunchSounds();
        } else {
          mFullSize = Math.max(0.0f, mFullSize-kShrinkRate);
        }
        
        if ( mFullSize == 0.0f && mPlayer != null && 
             mPlayer.getYPos() < Room.kSize && mPlayer.getZPos() == 0 ) {
          mXPos = kStartXPos;
          mYPos = kStartYPos;
          mXVel = mYVel = 0.0f;
          mTimer = kWakeDelay;
          mHeadHits = 0;
          mState = State.kWaking;
        }

        if ( mSwitch != null ) {
          killTheseSprites.add(mSwitch);
          mSwitch = null;
        }
      } break;
    
      case kWaking: {
        // materialize and start hunting
        if ( mTimer > 0 ) {
          mTimer -= 1;
        } else {
          mFullSize = Math.min(kMaxRadius, mFullSize+kGrowRate);
          mMouthTimer = 0;
          if ( mFullSize == kMaxRadius ) {
            mMouthTimer = kMouthPeriod[mHeadHits] + kMouthSlowStart;
            mState = State.kHunting;
            mTimer = kSwitchInitialDelay;
            assert( mSwitch == null );
          }
        }
      } break;
      
      case kHunting: {
        // chase after the player        
        boolean changeDirec = false;
        if ( mXPos < 0.0f || mXPos > Room.kSize ||
             mYPos < 0.0f || mYPos > Room.kSize ) {
          mXPos = Math.max(0.0f, Math.min(Room.kSize, mXPos));
          mYPos = Math.max(0.0f, Math.min(Room.kSize, mYPos));
          changeDirec = true;
        }
        if ( mXVel == 0.0f && mYVel == 0.0f ) {
          changeDirec = true;
        }
        if ( changeDirec && mPlayer != null ) {
          assert( mPlayer.getYPos() < Room.kSize );
          float dx = mPlayer.getXPos() + 0.5f - mXPos,
                dy = mPlayer.getYPos() + 0.5f - mYPos;
          float d  = (float)Math.sqrt(dx*dx + dy*dy);
          if ( d > 1.0e-3f ) {
            mXVel = kSpeed*dx/d;
            mYVel = kSpeed*dy/d;
          } else {
            mXVel = mYVel = 0.0f;
          }
        }        
        
        if ( --mMouthTimer < 0 ) mMouthTimer = kMouthPeriod[mHeadHits];
        playMunchSounds();
        
        if ( mPlayer == null || 
             mPlayer.getYPos() >= Room.kSize || mPlayer.getZPos() < -2 ) {
          mXVel = mYVel = 0.0f;
          mState = State.kSleeping;
          if ( mSwitch != null ) {
            killTheseSprites.add(mSwitch);
            mSwitch = null;
          }
        }
        
        if ( mSwitch == null ) {
          if ( mTimer > 0 ) mTimer--;
          if ( mTimer == 0 && mMouthTimer == kMouthSwitchTime ) {
            int x = (int)Math.floor(mXPos),
                y = (int)Math.floor(mYPos);
            mSwitch = new FloorSwitch(x,y,0, "zD", "#:");
            addTheseSprites.add(mSwitch);
            mTimer = kSwitchVanishDelay[mHeadHits];
          }
        } else if ( mSwitch != null ) {
          assert( mTimer > 0 );
          if ( --mTimer == 0 ) {
            killTheseSprites.add(mSwitch);
            mSwitch = null;
            mTimer = Env.randomInt(kSwitchMinDelay, kSwitchMaxDelay);
          } else if ( mSwitch.isOn() ) {
            mState = State.kBlasting;
            killTheseSprites.add(mSwitch);
            Env.sounds().play(Sounds.FLOOR_BLAST);
            mSwitch = null;
            mXVel = mYVel = 0.0f;
            mFullSize = 0.0f;
            mMouthTimer = 0;
            mTimer = kBlastDelay;
          }
        }
      } break;

      case kBlasting: {
        // clear the floor when switch triggered
        if ( --mTimer == 0 ) {
          adjustPosition();
          mState = State.kPeeking;
          mFullSize = 1.0f;
          mMouthTimer = kMouthPeriod[mHeadHits]/2;
        }
      } break;
      
      case kPeeking: {
        // head pops up and looks around
        if ( mHead == null ) {
          mHead = new FloorBossHead((int)Math.round(mXPos)-1,
                                    (int)Math.round(mYPos)-1);
          addTheseSprites.add(mHead);
          if ( mHeadHits >= kNumHeadLives-1 ) mHead.setKillable();
        }
        if ( mHead.isDone() ) {
          if ( mHead.isHit() ) mHeadHits += 1;
          if ( mHeadHits == kNumHeadLives ) {
            mState = State.kDying;
            mFullSize = 0;
            mTimer = kDyingDelay;
          } else {
            mTimer = 0;
            mState = State.kWaking;
          }
          mMouthTimer = 0;
          killTheseSprites.add(mHead);
          mHead = null;
        }
      } break;
      
      case kDying: {
        // spreading pool of death
        if ( mTimer > 0 ) {
          mTimer -= 1;
          if ( mTimer == 0 ) Env.sounds().play(Sounds.SUCCESS, 3);
          mFullSize = 0;
        } else {
          mFullSize += kGrowDeadRate;
          if ( mFullSize > kRoomRadius ) mState = State.kDead;
        }
        mMouthTimer = 0;
      } break;
      
      case kDead: {
        mFullSize = kRoomRadius;
        mMouthTimer = 0;
      } break;
      
      default: assert(false);
      
    }
        
    buildFloor();
    
    // animate the liquid
    if ( mLiquid == null ) {
      mLiquid = new Liquid(0,0,-2, 2);
      addTheseSprites.add(mLiquid);
      mLiquidXPos = (int)Math.floor(mXPos);
      mLiquidYPos = (int)Math.floor(mYPos);
    }
    int xScroll = (int)Math.floor(mXPos) - mLiquidXPos,
        yScroll = (int)Math.floor(mYPos) - mLiquidYPos;
    mLiquid.scrollImage(xScroll, yScroll);
    mLiquidXPos += xScroll;
    mLiquidYPos += yScroll;
    
  } // Sprite.advance()

  // sound effects for mouth opening and closing
  private void playMunchSounds() {
    
    assert( mHeadHits < kMouthSoundTimeA.length );
    
    if ( mMouthTimer == kMouthSoundTimeA[mHeadHits] ) {
      Env.sounds().play(Sounds.FLOOR_MUNCH_A);
    } else if ( mMouthTimer == kMouthSoundTimeB[mHeadHits] ) {
      Env.sounds().play(Sounds.FLOOR_MUNCH_B);
    }
        
  } // playMunchSounds()
  
  // check the player's position
  @Override
  public void interact() {
  } // Sprite.interact()
  
  // pass on notification that the boss is dead
  @Override
  public void aftermath(LinkedList<Sprite>     addTheseSprites, 
                        LinkedList<Sprite>     killTheseSprites,
                        LinkedList<StoryEvent> newStoryEvents) {
  } // Sprite.aftermath()
  
  // display the creature
  @Override
  public void draw(EgaCanvas canvas) {

    super.draw(canvas);

  } // Sprite.draw()

} // class FloorBoss
