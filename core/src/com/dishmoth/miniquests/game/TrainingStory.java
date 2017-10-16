/*
 *  TrainingStory.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.rooms.RoomZ01;
import com.dishmoth.miniquests.rooms.RoomZ02;
import com.dishmoth.miniquests.rooms.RoomZ03;

// the training level for beginners
public class TrainingStory extends Story {

  // times until things happen
  private static final int kKeyDelay          = 10,
                           kIntroChangeDelay  = 20,
                           kChangeDelay       = 10,
                           kRevealChangeDelay = 15,
                           kFinalChangeDelay  = 30;
  
  // how fast the text animates
  private static final int kAnimTextDelay   = 90,
                           kAnimAnyKeyDelay = 60,
                           kAnimBlankDelay  = 20;
  
  // text (and background) images
  private static EgaImage kStartPic    = null,
                          kMoveText    = null,
                          kFireText    = null,
                          kEndText     = null,
                          kLesson1Text = null,
                          kLesson2Text = null,
                          kLesson3Text = null,
                          kAnyKeyText  = null;
  
  // list of all rooms that make up this story
  private Room mRoomList[];
  
  // reference to the current room
  private Room mCurrentRoom;

  // reference to the player (or null if the player has just died)
  private Player mPlayer;

  // whether the main sprites are active
  private boolean mFrozen;

  // reference to the current text and background (or null)
  private Sprite      mTextPic;
  private Picture     mBackgroundPic;
  private TouchArrows mArrows;

  // count down until key presses are recognized
  private int mKeyTimer;

  // count down until text changes
  private int mChangeTimer;
  
  // a key press will start the game
  private boolean mKeyReady;

  // what's currently happening
  private int mStage;

  // current style of the on-screen buttons (Android only)
  private int mButtonArrow,
              mButtonFire;

  // keep track of whether the escape key is held down
  private boolean mEscPressed;

  // sprites that are not displayed when paused
  private LinkedList<Sprite> mHiddenSprites = new LinkedList<Sprite>();
  
  // constructor
  public TrainingStory() {

    if ( kStartPic == null ) {
      String gdxText = ( Env.platform()==Env.Platform.OUYA    ? "Controller" 
                       : Env.platform()==Env.Platform.ANDROID ? "Android" 
                       : Env.platform()==Env.Platform.IOS     ? "Android" 
                                                              : "" );      
      kStartPic    = Env.resources().loadEgaImage("TrainingPic.png");
      kMoveText    = Env.resources().loadEgaImage("MoveText"+gdxText+".png");
      kFireText    = Env.resources().loadEgaImage("FireText"+gdxText+".png");
      kEndText     = Env.resources().loadEgaImage("TrainedText.png");
      kLesson1Text = Env.resources().loadEgaImage("Lesson1Text.png");
      kLesson2Text = Env.resources().loadEgaImage("Lesson2Text.png");
      kLesson3Text = Env.resources().loadEgaImage("Lesson3Text.png");
      kAnyKeyText  = Env.resources().loadEgaImage("StartText"+gdxText+".png");
    }
    
    mRoomList = new Room[]{ new RoomZ01(),
                            new RoomZ02(),
                            new RoomZ03() };
    
    for ( Room room : mRoomList ) room.setRoomList(mRoomList);
    
    mCurrentRoom = null;
    mPlayer = null;

  } // constructor

  // process events and advance
  @Override
  public Story advance(LinkedList<StoryEvent> storyEvents,
                       SpriteManager          spriteManager) {

    // check for a story restart (skip this advance in such cases) 
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      if ( it.next() instanceof Story.EventStoryContinue ) {
        Env.keys().setMode(KeyMonitor.MODE_GAME);
        setButtonDetails(mButtonArrow, mButtonFire);
        for ( Sprite s : mHiddenSprites ) s.mDrawDisabled = false;
        mHiddenSprites.clear();
        mEscPressed = true;
        mKeyReady = false;
        it.remove();
        return null;
      }
    }
    
    // update the current room
    if ( mCurrentRoom != null && !mFrozen ) {
      mCurrentRoom.advance(storyEvents, spriteManager);
    }

    Story newStory = null;
    
    // process the story event list
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();

      if ( event instanceof Story.EventGameBegins ) {
        // first frame of the story, so set everything up
        Camera camera = new Camera();
        for ( Room room : mRoomList ) room.setCamera(camera);
        spriteManager.addSprite(camera);
        mCurrentRoom = mRoomList[0];
        mCurrentRoom.createSprites(spriteManager);
        mPlayer = mCurrentRoom.createPlayer(0);
        spriteManager.addSprite(mPlayer);
        mTextPic = null;
        mArrows = null;
        freezeScene(spriteManager);
        mBackgroundPic = new Picture(kStartPic);
        spriteManager.addSprite(mBackgroundPic);
        mStage = 0;
        mKeyTimer = kKeyDelay;
        mChangeTimer = kChangeDelay;
        Env.keys().setMode(KeyMonitor.MODE_GAME);
        setButtonDetails(0, 0);
        mEscPressed = true;
        mKeyReady = false;
        it.remove();
      } // Story.EventGameBegins

      else if ( event instanceof Room.EventRoomChange ) {
        Room.EventRoomChange e = (Room.EventRoomChange)event;
        if ( e.mEntryPoint == 99 ) {
          assert( mStage == 11 );
          mChangeTimer = kRevealChangeDelay;
          mPlayer.addBrain(new Brain.ZombieModule(new int[]{ Env.UP,100 }));
        } else {
          mCurrentRoom.removePlayer();
          mCurrentRoom.discardResources();
          clearRoom(spriteManager);
          mCurrentRoom = findRoom(e.mNewRoom);
          mCurrentRoom.createSprites(spriteManager);
          mPlayer = mCurrentRoom.createPlayer(e.mEntryPoint);
          spriteManager.addSprite(mPlayer);
          freezeScene(spriteManager);
          mChangeTimer = kRevealChangeDelay;
        }
        it.remove();
      } // Room.EventRoomChange 
      
      else {
        Env.debug("event ignored: " + event.getClass());
        it.remove();
      }
      
    } // for each story event

    // player has triggered the first switch
    if ( mStage == 7 ) {
      if ( ((RoomZ02)mCurrentRoom).firstBitDone() && !mFrozen ) {
        freezeScene(spriteManager);
        mChangeTimer = kChangeDelay;
      }
    }
    
    // check key presses
    if ( mTextPic != null && mChangeTimer == 0 ) {
      if ( mKeyTimer > 0 ) mKeyTimer--;
      if ( mKeyTimer == 0 ) {
        if ( Env.keys().any() && !Env.keys().escape() ) {
          if ( mKeyReady ) {
            mChangeTimer = kChangeDelay;
            if ( mStage ==  1 ) mChangeTimer = kRevealChangeDelay;
            if ( mStage == 13 ) mChangeTimer = kFinalChangeDelay;
            spriteManager.removeSprite(mTextPic);
            mTextPic = null;
            if ( mArrows != null ) {
              spriteManager.removeSprite(mArrows);
              mArrows = null;
            }
            setButtonDetails( ((mStage>=4 && mStage<12) ? 1 : 0), 
                              ((mStage>=8 && mStage<12) ? 1 : 0) );
            Env.sounds().play(Sounds.MENU_1);
          }
          mKeyReady = false;
        } else {
          mKeyReady = true;
        }
      }
    }
    
    // stage advance or story handover
    if ( mChangeTimer > 0  ) {
      if ( --mChangeTimer == 0 ) {
        mKeyTimer = 0;
        mStage++;
        switch (mStage) {
          case 1: {
            Env.sounds().play(Sounds.HERO_GRUNT);
            mChangeTimer = kIntroChangeDelay;
          } break;
          case 2: {
            assert( mBackgroundPic != null );
            spriteManager.removeSprite(mBackgroundPic);
            mBackgroundPic = null;
            mChangeTimer = kRevealChangeDelay;
          } break;
          case 3: {
            mTextPic = new AnimPicture(0, kLesson1Text, 
                                       kAnimTextDelay, kAnimBlankDelay,
                                       kAnyKeyText, 
                                       kAnimAnyKeyDelay, kAnimBlankDelay);
          } break;
          case 4: {
            mTextPic = new Picture(kMoveText);
            if ( (Env.platform() == Env.Platform.ANDROID ||
                  Env.platform() == Env.Platform.IOS) &&
                 Env.saveState().touchScreenControls() == 0 ) {
              mArrows = new TouchArrows(1);
            }
            setButtonDetails(2, 0);
          } break;
          case 5: {
            unfreezeScene(spriteManager);
          } break;
          case 6: {
            mTextPic = new Picture(kLesson2Text);
          } break;
          case 7: {
            unfreezeScene(spriteManager);
          } break;
          case 8: {
            mTextPic = new Picture(kFireText);
            if ( (Env.platform() == Env.Platform.ANDROID ||
                  Env.platform() == Env.Platform.IOS) &&
                 Env.saveState().touchScreenControls() == 0 ) {
              mArrows = new TouchArrows(2);
            }
            setButtonDetails(1, 2);
            mPlayer.removeBrain();
            mKeyTimer += kKeyDelay;
          } break;
          case 9: {
            unfreezeScene(spriteManager);
          } break;
          case 10: {
            mTextPic = new Picture(kLesson3Text);
          } break;
          case 11: {
            unfreezeScene(spriteManager);
          } break;
          case 12: {
            freezeScene(spriteManager);
            mTextPic = new Picture(kEndText);
            setButtonDetails(0, 0);
            //Env.sounds().playQuestDoneSound();
          } break;
          case 13: {
            spriteManager.removeAllSprites();
            newStory = new MenuStory();
            storyEvents.add(new Story.EventGameBegins());
            Env.saveState().heroTrainingDone();
            Env.saveState().saveMaybe();
          } break;
          default: {
            assert(false);
          } break;
        }
        if ( mTextPic != null ) {
          spriteManager.addSprite(mTextPic);
          mKeyTimer += kKeyDelay;
        }
        if ( mArrows != null ) {
          spriteManager.addSprite(mArrows);
        }
      }
    }
    
    // quest aborted
    if ( Env.keys().escape() ) {
      if ( !mEscPressed && newStory == null ) {
        Env.sounds().stopAll();
        //newStory = new QuitStory(this);
        newStory = new MenuStory();
        ((MenuStory)newStory).startOnTraining(this);
        storyEvents.add(new Story.EventGameBegins());
        if ( mTextPic != null && !mTextPic.mDrawDisabled ) {
          mTextPic.mDrawDisabled = true;
          mHiddenSprites.add(mTextPic);
        }
        if ( mArrows != null && !mArrows.mDrawDisabled ) {
          mArrows.mDrawDisabled = true;
          mHiddenSprites.add(mArrows);
        }
      }
      mEscPressed = true;
    } else {
      mEscPressed = false;
    }    
    
    return newStory;
    
  } // Story.advance()

  // stop all of the sprites from moving
  private void freezeScene(SpriteManager spriteManager) {
    
    assert( !mFrozen );
    mFrozen = true;
    spriteManager.disableAdvanceForAll();
    
  } // freezeScene()
  
  // allow the sprites to move
  private void unfreezeScene(SpriteManager spriteManager) {
    
    assert( mFrozen );
    mFrozen = false;
    spriteManager.enableAdvanceForAll();
    
  } // unfreezeScene()
  
  // retrieve a room based on its unique name
  private Room findRoom(String roomName) {

    Room foundRoom = null;
    for ( Room room : mRoomList ) {
      if ( room.name().equals(roomName) ) {
        assert( foundRoom == null );
        foundRoom = room;
      }
    }
    assert( foundRoom != null );
    return foundRoom;
    
  } // findRoom()

  // remove all of the sprites for the room
  private void clearRoom(SpriteManager spriteManager) {

    LinkedList<Sprite> deadSprites = new LinkedList<Sprite>();
    for (Sprite s : spriteManager.list()) {
      if ( s instanceof Camera ) continue; 
      deadSprites.add(s);
    }
    if (deadSprites.size() > 0) spriteManager.removeSprites(deadSprites);
    
  } // clearRoom()

  // set and record the style of the on-screen buttons (Android only)
  private void setButtonDetails(int arrowStyle, int fireStyle) {
    
    mButtonArrow = arrowStyle;
    mButtonFire = fireStyle;
    Env.keys().setButtonDetails(mButtonArrow, mButtonFire);

  } // setButtonDetails()
  
} // class TrainingStory
