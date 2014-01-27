/*
 *  StartupStory.java
 *  Copyright Simon Hern 2012
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

// continue a saved game, or go to the map/training screen
public class StartupStory extends Story {

  // times until things happen
  private static final int kCloseDelay        = 20,
                           kStartDelayApplet  = 2,
                           kStartDelayAndroid = 20;
  
  // text sizes and positions
  private static final int kResumeTextXPos   = 0,
                           kResumeTextYPos   = 0,
                           kResumeTextWidth  = 40,
                           kResumeTextHeight = 15;
  private static final int kYesTextXPos      = 0,
                           kYesTextYPos      = 15,
                           kYesTextWidth     = 20,
                           kYesTextHeight    = 15;
  private static final int kNoTextXPos       = 20,
                           kNoTextYPos       = 15,
                           kNoTextWidth      = 20,
                           kNoTextHeight     = 15;                           
  
  // text images
  private static EgaImage kResumeText = null,
                          kYesText[]  = null,
                          kNoText[]   = null;
  
  // different versions of the yes and no images
  private static final byte kYesNoColours[] = { 63, 59, 56 };

  // z-depth used for the text
  private static final float kDepth = -10.0f;

  // the quest we are potentially restarting (null => go to title screen)
  private TinyStory mRestartStory;
  
  // count down until options appear
  private int mStartTimer;
  
  // count down until the menu closes (or 0)
  private int mFinishedTimer;
  
  // references to this story's objects
  private Picture mBackground,
                  mResumePic,
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
    
    if ( kResumeText != null ) return;
    
    EgaImage image = Env.resources().loadEgaImage("ResumeText.png");

    kResumeText = new EgaImage(0, 0, kResumeTextWidth, kResumeTextHeight);
    image.draw(kResumeText, -kResumeTextXPos, -kResumeTextYPos, 0.0f);

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
  public StartupStory() {

    initialize();

    if ( Env.saveState().hasRestartData() ) {
    
      Env.debug("Restart data available "
                + "(version " + Env.saveState().restartVersion() + ")");
      mRestartStory = new TinyStory();
      boolean okay = mRestartStory.restore(Env.saveState().restartVersion(), 
                                           Env.saveState().restartData());
      if ( !okay ) {
        Env.debug("Could not restore quest restart data");
        mRestartStory = null;
      }
      
    } else {
      
      Env.debug("No quest restart data found");
      mRestartStory = null;
      
    }
    
    Env.saveState().restartData().clear();
    
  } // constructor
  
  // whether we are running on a touchscreen device (android but not ouya)
  private boolean usingTouchscreen() { 
    
    return ( Env.platform() == Env.Platform.ANDROID );
    
  } // isTouchscreen()
  
  // process events and advance 
  @Override
  public Story advance(LinkedList<StoryEvent> storyEvents,
                       SpriteManager          spriteManager) {

    // go straight to map or training screen if there is no game to restore
    if ( mRestartStory == null ) {
      if ( Env.saveState().fullTrainingNeeded() ) {
        return new TrainingStory();
      } else {
        return new MapStory(-1);
      }
    }
    
    Story newStory = null;
    
    // process the story event list
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();

      if ( event instanceof Story.EventGameBegins ) {
        // first frame of the story, so set everything up
        LinkedList<StoryEvent> tempEvents = new LinkedList<StoryEvent>();
        tempEvents.add(new Story.EventGameBegins());
        mRestartStory.advance(tempEvents, spriteManager);        
        makeBackground(spriteManager);
        freezeSprites(spriteManager);
        spriteManager.addSprite(mBackground);
        mStartTimer = ( Env.platform()==Env.Platform.ANDROID ||
                        Env.platform()==Env.Platform.OUYA ) 
                      ? kStartDelayAndroid 
                      : kStartDelayApplet;
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
    if ( mFinishedTimer == 0 && mStartTimer == 0 ) {
      if ( usingTouchscreen() ) {
        
        // android (phone/tablet) behaviour
        boolean keyYes = Env.keys().left(),
                keyNo  = Env.keys().right();
        
        if ( keyYes ) {
          mYesSelected = true;
          mFinishedTimer = kCloseDelay;
          updateYesPic(spriteManager);
          updateNoPic(spriteManager);
          Env.sounds().play(Sounds.MENU_2);
        } else if ( keyNo ) {
          mYesSelected = false;
          mFinishedTimer = kCloseDelay;
          updateYesPic(spriteManager);
          updateNoPic(spriteManager);
          Env.sounds().play(Sounds.MENU_2);
        }
        
      } else {

        // applet or ouya behaviour
        final boolean keyYes  = ( Env.keys().left()  || Env.keys().up() ),
                      keyNo   = ( Env.keys().right() || Env.keys().down() ),
                      keyFire = Env.keys().fire();
  
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
          mFinishedTimer = kCloseDelay;
          updateYesPic(spriteManager);
          updateNoPic(spriteManager);
          Env.sounds().play(Sounds.MENU_2);
        }
        
      }
    }

    // check whether menu is finished
    if ( mFinishedTimer > 0 && mStartTimer == 0 ) {
      if ( --mFinishedTimer == 0 ) {
        if ( mYesSelected ) {
          Env.debug("Resuming existing game");
          if ( mBackground != null ) spriteManager.removeSprite(mBackground);
          if ( mResumePic != null )  spriteManager.removeSprite(mResumePic);
          if ( mYesPic != null )     spriteManager.removeSprite(mYesPic);
          if ( mNoPic != null )      spriteManager.removeSprite(mNoPic);
          unfreezeSprites();
          newStory = mRestartStory;
          storyEvents.add(new Story.EventStoryContinue());
        } else {
          Env.debug("Starting new game");
          Env.saveState().updateRestartData();
          spriteManager.removeAllSprites();
          newStory = new MapStory(-1);
          storyEvents.add(new Story.EventGameBegins());
        }
      }
    }

    // delay before showing options
    if ( mStartTimer > 0 ) {
      if ( --mStartTimer == 0 ) {
        EgaTools.fadeImage(mBackground.image());
        EgaTools.limitColours(mBackground.image(), 13);
        mResumePic = new Picture(kResumeText, 
                                 kResumeTextXPos, kResumeTextYPos, 
                                 kDepth);
        spriteManager.addSprite(mResumePic);
        mYesSelected = true;
        updateYesPic(spriteManager);
        updateNoPic(spriteManager);
      }
    }
    
    // check for abort
    if ( Env.keys().escape() ) {
      if ( !mEscPressed && newStory == null && usingTouchscreen() ) {
        Env.exit();
      }
      mEscPressed = true;
    } else {
      mEscPressed = false;
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
  
} // class StartupStory
