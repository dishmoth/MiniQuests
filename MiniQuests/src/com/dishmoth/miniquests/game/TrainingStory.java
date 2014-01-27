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
  private static final int kKeyDelay          = 10, //15,
                           kIntroChangeDelay  = 30,
                           kChangeDelay       = 10, //15,
                           kRevealChangeDelay = 15, //25,
                           kFinalChangeDelay  = 30;
  
  // text (and background) images
  private static EgaImage kStartText   = null,
                          kStartPic    = null,
                          kMoveText    = null,
                          kFireText    = null,
                          kEndText     = null,
                          kLesson1Text = null,
                          kLesson2Text = null,
                          kLesson3Text = null;
  
  // list of all rooms that make up this story
  private Room mRoomList[];
  
  // reference to the current room
  private Room mCurrentRoom;

  // reference to the player (or null if the player has just died)
  private Player mPlayer;

  // whether the main sprites are active
  private boolean mFrozen;

  // reference to the current text and background (or null)
  private Picture mTextPic,
                  mBackgroundPic;
  private TouchArrows mArrows;

  // count down until key presses are recognized
  private int mKeyTimer;

  // count down until text changes
  private int mChangeTimer;
  
  // a key press will start the game
  private boolean mKeyReady;

  // what's currently happening
  private int mStage;
  
  // keep track of whether the escape key is held down
  private boolean mEscPressed;

  // constructor
  public TrainingStory() {

    if ( kStartText == null ) {
      String gdxText = ( Env.platform()==Env.Platform.OUYA    ? "Controller" 
                       : Env.platform()==Env.Platform.ANDROID ? "Android" 
                                                              : "" );      
      kStartText   = Env.resources().loadEgaImage("TrainingText.png");
      kStartPic    = Env.resources().loadEgaImage("TrainingPic.png");
      kMoveText    = Env.resources().loadEgaImage("MoveText"+gdxText+".png");
      kFireText    = Env.resources().loadEgaImage("FireText"+gdxText+".png");
      kEndText     = Env.resources().loadEgaImage("TrainedText.png");
      kLesson1Text = Env.resources().loadEgaImage("Lesson1Text.png");
      kLesson2Text = Env.resources().loadEgaImage("Lesson2Text.png");
      kLesson3Text = Env.resources().loadEgaImage("Lesson3Text.png");
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
        mChangeTimer = kIntroChangeDelay;
        Env.keys().setMode(KeyMonitor.MODE_GAME);
        Env.keys().setButtonDetails(0, 0);
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
        if ( Env.keys().any() ) {
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
            Env.keys().setButtonDetails( ((mStage>=4 && mStage<12) ? 1 : 0), 
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
            mTextPic = new Picture(kStartText);
            Env.sounds().play(Sounds.HERO_GRUNT);
          } break;
          case 2: {
            assert( mTextPic == null );
            assert( mBackgroundPic != null );
            spriteManager.removeSprite(mBackgroundPic);
            mBackgroundPic = null;
            mChangeTimer = kRevealChangeDelay;
          } break;
          case 3: {
            mTextPic = new Picture(kLesson1Text);
          } break;
          case 4: {
            mTextPic = new Picture(kMoveText);
            if ( Env.platform() == Env.Platform.ANDROID &&
                 !Env.keys().usingButtons() ) {
              mArrows = new TouchArrows(1);
            }
            Env.keys().setButtonDetails(2, 0);
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
            if ( Env.platform() == Env.Platform.ANDROID &&
                 !Env.keys().usingButtons() ) {
              mArrows = new TouchArrows(2);
            }
            Env.keys().setButtonDetails(1, 2);
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
            Env.keys().setButtonDetails(0, 0);
            //Env.sounds().playQuestDoneSound();
          } break;
          case 13: {
            spriteManager.removeAllSprites();
            newStory = new MapStory(-1);
            storyEvents.add(new Story.EventGameBegins());
            Env.saveState().setTrainingDone();
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
    if ( Env.keys().escape() && Env.platform() == Env.Platform.ANDROID ) {
      if ( !mEscPressed && newStory == null ) {
        newStory = new QuitStory(this);
        storyEvents.add(new Story.EventGameBegins());
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
  
  // retrieve a room based on its class
  private Room findRoom(Class<?> roomClass) {

    Room foundRoom = null;
    for ( Room room : mRoomList ) {
      if ( roomClass.isInstance(room) ) {
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

} // class TrainingStory
