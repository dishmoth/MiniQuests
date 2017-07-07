/*
 *  QuitStory.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

// abort game screen
public class QuitStory extends Story {

  // times until things happen
  private static final int kCloseDelay       = 10,
                           kQuitDelay        = 30,
                           kQuitDelayAndroid = 10;
  
  // text sizes and positions
  private static final int kQuitTextXPos   = 0,
                           kQuitTextYPos   = 0,
                           kQuitTextWidth  = 40,
                           kQuitTextHeight = 15;
  private static final int kYesTextXPos    = 0,
                           kYesTextYPos    = 15,
                           kYesTextWidth   = 20,
                           kYesTextHeight  = 15;
  private static final int kNoTextXPos     = 20,
                           kNoTextYPos     = 15,
                           kNoTextWidth    = 20,
                           kNoTextHeight   = 15;                           
  
  // text images
  private static EgaImage kQuitText  = null,
                          kYesText[] = null,
                          kNoText[]  = null;
  
  // different versions of the yes and no images
  private static final byte kYesNoColours[] = { 63, 59, 56 };

  // z-depth used for the text
  private static final float kDepth = -10.0f;

  // which story we came from (and may return to)
  private Story mOldStory;
  
  // count down until the menu closes (or 0)
  private int mFinishedTimer;
  
  // references to this story's objects
  private Picture mBackground,
                  mQuitPic,
                  mYesPic,
                  mNoPic;
  
  // which option is currently highlighted
  private boolean mYesSelected;

  // monitor the escape key
  private boolean mEscPressed;
  
  // which sprites we've frozen
  private ArrayList<Sprite> mAdvanceDisabledSprites = new ArrayList<Sprite>(),
                            mDrawDisabledSprites = new ArrayList<Sprite>();
  
  // prepare resources
  static void initialize() {
    
    if ( kQuitText != null ) return;
    
    EgaImage image = Env.resources().loadEgaImage("QuitText.png");

    kQuitText = new EgaImage(0, 0, kQuitTextWidth, kQuitTextHeight);
    image.draw(kQuitText, -kQuitTextXPos, -kQuitTextYPos, 0.0f);

    final int numColours = kYesNoColours.length; 
    
    kYesText = new EgaImage[numColours];
    kYesText[0] = new EgaImage(0, 0, kYesTextWidth, kYesTextHeight);
    image.draw(kYesText[0], -kYesTextXPos, -kYesTextYPos, 0.0f);
    
    kNoText = new EgaImage[numColours];
    kNoText[0] = new EgaImage(0, 0, kNoTextWidth, kNoTextHeight);
    image.draw(kNoText[0], -kNoTextXPos, -kNoTextYPos, 0.0f);
    
    for ( int k = 1 ; k < numColours ; k++ ) {
      kYesText[k] = kYesText[0].clone();
      replaceColour(kYesText[k], kYesNoColours[0], kYesNoColours[k]);
      kNoText[k] = kNoText[0].clone();
      replaceColour(kNoText[k], kYesNoColours[0], kYesNoColours[k]);
    }
    
  } // initialize()
  
  // change the colour of an image
  static private void replaceColour(EgaImage image, 
                                    byte oldColour, byte newColour) {
    
    byte pixels[] = image.pixels();
    for ( int k = 0 ; k < pixels.length ; k++ ) {
      if ( pixels[k] == oldColour ) pixels[k] = newColour;
    }
    
  } // replaceColour()
  
  // constructor
  public QuitStory(Story oldStory) {

    initialize();
    
    mOldStory = oldStory;
    
  } // constructor
  
  // whether we are running on a touchscreen device (android but not ouya)
  private boolean usingTouchscreen() { 
    
    return ( Env.platform() == Env.Platform.ANDROID );
    
  } // isTouchscreen()
  
  // process events and advance 
  @Override
  public Story advance(LinkedList<StoryEvent> storyEvents,
                       SpriteManager          spriteManager) {

    Story newStory = null;
    
    // process the story event list
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();

      if ( event instanceof Story.EventGameBegins ) {
        // first frame of the story, so set everything up
        makeBackground(spriteManager);
        freezeSprites(spriteManager);
        spriteManager.addSprite(mBackground);
        mQuitPic = new Picture(kQuitText, 
                               kQuitTextXPos, kQuitTextYPos, 
                               kDepth);
        spriteManager.addSprite(mQuitPic);
        mYesSelected = false;
        updateYesPic(spriteManager);
        updateNoPic(spriteManager);
        mFinishedTimer = 0;
        mEscPressed = true;
        Env.keys().setMode(KeyMonitor.MODE_QUERY);
        Env.sounds().stopAll();
        it.remove();
      } // Story.EventGameBegins

      else {
        //Env.debug("event ignored: " + event.getClass());
        //it.remove();
      }
      
    } // for each story event

    // check key presses
    if ( mFinishedTimer == 0 ) {
      if ( usingTouchscreen() ) {
        
        // android (phone/tablet) behaviour
        boolean keyYes = Env.keys().left(),
                keyNo  = Env.keys().right();
        
        if ( Env.keys().escape() ) {
          if ( !mEscPressed ) keyYes = true;
          mEscPressed = true;
        } else {
          mEscPressed = false;
        }
        
        if ( keyYes ) {
          mYesSelected = true;
          mFinishedTimer = kQuitDelayAndroid;
          if ( mEscPressed ) mFinishedTimer /= 2;
          updateYesPic(spriteManager);
          updateNoPic(spriteManager);
          Env.sounds().play(Sounds.MENU_2);
          Env.debug("Quest aborted");
          Env.saveState().reportQuestStats();
        } else if ( keyNo ) {
          mYesSelected = false;
          mFinishedTimer = kCloseDelay;
          updateYesPic(spriteManager);
          updateNoPic(spriteManager);
        }
        
      } else {

        // desktop or console behaviour
        final boolean keyYes  = ( Env.keys().left()  || Env.keys().up() ),
                      keyNo   = ( Env.keys().right() || Env.keys().down() ),
                      keyFire = Env.keys().fire(),
                      keyEsc  = Env.keys().escape();
  
        boolean toggleSelected = false;
        if ( keyYes ) {
          if ( !mYesSelected ) {
            toggleSelected = true;
          }
        } else if ( keyNo ) {
          if ( mYesSelected ) {
            toggleSelected = true;
          }
        } 
        if ( toggleSelected ) {
          mYesSelected = !mYesSelected;
          updateYesPic(spriteManager);
          updateNoPic(spriteManager);
          Env.sounds().play(Sounds.MENU_1);
        }
        
        if ( keyFire ) {
          mFinishedTimer = ( mYesSelected ? kQuitDelay : kCloseDelay );
          updateYesPic(spriteManager);
          updateNoPic(spriteManager);
          if ( mYesSelected ) {
            Env.sounds().play(Sounds.MENU_2);
            Env.debug("Quest aborted");
            Env.saveState().reportQuestStats();
          }
        }

        if ( keyEsc ) {
          if ( !mEscPressed ) {
            mEscPressed = true;
            mYesSelected = false;
            mFinishedTimer = 1;
          }
        } else {
          mEscPressed = false;
        }
        
      }
    }

    // check whether menu is finished
    if ( mFinishedTimer > 0 ) {
      if ( --mFinishedTimer == 0 ) {
        if ( mYesSelected ) {
          spriteManager.removeAllSprites();
          if ( mOldStory instanceof TinyStory ) {
            Env.saveState().setQuestStats(null);
            int questNum = ((TinyStory)mOldStory).questNumber(); 
            newStory = new MapStory(questNum);
          } else if ( mOldStory instanceof ScrollStory ) {
            assert( Env.saveState().questStats() != null );
            int questNum = Env.saveState().questStats().questNum(); 
            Env.saveState().setQuestStats(null);
            newStory = new MapStory(questNum);
          } else {
            newStory = new MenuStory();
            ((MenuStory)newStory).startOnTraining();
          }
          Env.saveState().save();
          storyEvents.add(new Story.EventGameBegins());
        } else {
          if ( mBackground != null ) spriteManager.removeSprite(mBackground);
          if ( mQuitPic != null )    spriteManager.removeSprite(mQuitPic);
          if ( mYesPic != null )     spriteManager.removeSprite(mYesPic);
          if ( mNoPic != null )      spriteManager.removeSprite(mNoPic);
          unfreezeSprites();
          newStory = mOldStory;
          storyEvents.add(new Story.EventStoryContinue());
        }
      }
    }

    return newStory;
    
  } // Story.advance()

  // record a copy of the old screen for a background
  private void makeBackground(SpriteManager spriteManager) {
    
    EgaCanvas oldScreen = new EgaCanvas(Env.screenWidth(), Env.screenHeight());
    spriteManager.draw(oldScreen);

    EgaImage image = new EgaImage(0, 0, 
                                  Env.screenWidth(), Env.screenHeight(),
                                  oldScreen.pixels(), 0.0f);
    
    EgaTools.fadeImage(image);
    EgaTools.limitColours(image, 13);
    
    mBackground = new Picture(image, 0.0f);
    
  } // makeBackground()
  
  // set the current 'yes' image
  private void updateYesPic(SpriteManager spriteManager) {
    
    if ( mYesPic != null ) {
      spriteManager.removeSprite(mYesPic);
      mYesPic = null;
    }
    
    int col;
    if ( usingTouchscreen() ) {
      col = 1;
      if ( mFinishedTimer > 0 ) {
        if ( !mYesSelected ) return;
        else                 col = 0;
      }
    } else {
      if ( mYesSelected ) {
        col = 1;
      } else {
        if ( mFinishedTimer > 0 ) return;
        col = 2;
      }
    }
    
    mYesPic = new Picture(kYesText[col], kYesTextXPos, kYesTextYPos, kDepth);
    spriteManager.addSprite(mYesPic);
    
  } // updateYesPic()
  
  // set the current 'no' image
  private void updateNoPic(SpriteManager spriteManager) {
    
    if ( mNoPic != null ) {
      spriteManager.removeSprite(mNoPic);
      mNoPic = null;
    }
    
    int col;
    if ( usingTouchscreen() ) {
      col = 1;
      if ( mFinishedTimer > 0 ) {
        if ( mYesSelected ) return;
        else                col = 0;
      }
    } else {
      if ( !mYesSelected ) {
        col = 1;
      } else {
        if ( mFinishedTimer > 0 ) return;
        col = 2;
      }
    }
    
    mNoPic = new Picture(kNoText[col], kNoTextXPos, kNoTextYPos, kDepth);
    spriteManager.addSprite(mNoPic);
    
  } // updateNoPic()
  
  // disable advance and draw for all sprites
  private void freezeSprites(SpriteManager spriteManager) {

    for ( Sprite sp : spriteManager.list() ) {
      if ( sp.mAdvanceDisabled == false) {
        sp.mAdvanceDisabled = true;
        mAdvanceDisabledSprites.add(sp);
      }
    }
    for ( Sprite sp : spriteManager.list() ) {
      if ( sp.mDrawDisabled == false) {
        sp.mDrawDisabled = true;
        mDrawDisabledSprites.add(sp);
      }
    }
    
  } // freezeSprites()
  
  // re-enable advance and draw for all sprites
  private void unfreezeSprites() {
    
    for ( Sprite sp : mAdvanceDisabledSprites ) {
      sp.mAdvanceDisabled = false;
    }
    for ( Sprite sp : mDrawDisabledSprites ) {
      sp.mDrawDisabled = false;
    }
    
  } // unfreezeSprites()
  
} // class QuitStory
