/*
 *  MenuStory.java
 *  Copyright Simon Hern 2017
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

// menu for the game on start-up
public class MenuStory extends Story {

  // delay before things happen
  private static final int kPauseDelay      = 10,
                           kPauseStartDelay = 20;
  private static final int kExitDelay       = 15;
  
  // menu option objects
  private ArrayList<MenuPanel> mPanels;

  // which panel to display first
  private boolean mStartOnTraining;
  private int     mStartOnMap[];  
  
  // position of the current menu view relative to the top of the panels
  private int mYPos;
  
  // scroll direction (+1 down, -1 up) or zero if not scrolling
  private int mScroll;
  
  // references to direction arrows (borrowed from the map) if present
  private MapArrow mArrows[];
  
  // time to delay for a new panel
  private int mPauseTimer;
  
  // time to delay before exiting the menu
  private int mExitTimer;
  
  // keep track of whether the escape key is held down
  private boolean mEscPressed;
  
  // constructor
  public MenuStory() {

    mStartOnTraining = false;
    mStartOnMap = null;
    
  } // constructor
  
  // display the training panel first
  public void startOnTraining() {
    
    mStartOnTraining = true;
    mStartOnMap = null;
    
  } // startOnTraining()
  
  // display the map panel first
  public void startOnMap(int restartData[]) {
    
    mStartOnMap = restartData;
    mStartOnTraining = false;
    
  } // startOnMap()
  
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
        mYPos = 0;
        mScroll = 0;
        makePanels(spriteManager);
        mPauseTimer = kPauseStartDelay;
        mExitTimer = 0;
        mEscPressed = true;
        Env.keys().setMode(KeyMonitor.MODE_MAP);
        Env.sounds().stopAll();
        it.remove();
      } // Story.EventGameBegins

      else {
        Env.debug("event ignored: " + event.getClass());
        it.remove();
      }
      
    } // for each story event

    final boolean keyUp    = Env.keys().up(),
                  keyDown  = Env.keys().down();
    
    if ( mExitTimer > 0 ) {
      
      // pause before exiting the menu
      
      if ( --mExitTimer == 0 ) {
        int panel = mYPos/Env.screenHeight();
        newStory = mPanels.get(panel).exitMenu(storyEvents, spriteManager);
      }
      
    } else if ( mPauseTimer > 0 ) {
      
      // pause at the start of a new location, etc.
      
      if ( --mPauseTimer == 0 ) {
        int panel = mYPos/Env.screenHeight();
        mPanels.get(panel).enable(spriteManager);
      }
      
    } else if ( mScroll == 0 ) {
      
      // wait for a key to be pressed
      
      int panel = mYPos/Env.screenHeight();
      
      if ( mArrows == null ) {
        mArrows = new MapArrow[2];
        if ( panel > 0 ) {
          mArrows[0] = new MapArrow(Env.UP);
          spriteManager.addSprite(mArrows[0]);
        }
        if ( panel < mPanels.size() - 1 ) {
          mArrows[1] = new MapArrow(Env.DOWN);
          spriteManager.addSprite(mArrows[1]);
        }
      }

      boolean exit = mPanels.get(panel).advance(spriteManager);
      
      if ( exit ) {
        clearArrows(spriteManager);
        mPanels.get(panel).disable(spriteManager);
        Env.sounds().play(Sounds.MENU_2);
        mExitTimer = kExitDelay;
      } else {
        if ( keyUp && mArrows[0] != null ) {
          mScroll = -1;
        } else if ( keyDown && mArrows[1] != null ) {
          mScroll = +1;
        }
        if ( mScroll != 0 ) {
          clearArrows(spriteManager);
          mPanels.get(panel).disable(spriteManager);
          Env.sounds().play(Sounds.MAP);
        }
      }

    } else {

      // scroll to a new location

      mYPos += 3*mScroll;
      if ( mYPos % Env.screenHeight() == 0 ) {
        mScroll = 0;
        mPauseTimer = kPauseDelay;
      }
      setPanelPositions();
      
    }

    // quest aborted
    if ( Env.keys().escape() ) {
      if ( !mEscPressed && newStory == null ) {
        if ( Env.platform() == Env.Platform.ANDROID ) {
          Env.exit();
          if ( mScroll == 0 ) mPauseTimer = 300; // delay while the 'exit' takes hold
        }
      }
      mEscPressed = true;
    } else {
      mEscPressed = false;
    }    
    
    return newStory;
    
  } // Story.advance()

  // create an appropriate set of panels
  private void makePanels(SpriteManager spriteManager) {
    
    mPanels = new ArrayList<MenuPanel>();
    
    mPanels.add(new MenuTraining());

    if ( Env.saveState().heroTrainingNeeded() ) {
      mPanels.add(new MenuMap());
    } else {
      boolean newGame = Env.saveState().newGameNeeded();
      int usedColours[] = mPanels.get(0).colours();
      mPanels.add(0, new MenuMap(newGame, mStartOnMap, usedColours));
    }
    
    if ( Env.saveState().hasRestartData() ) {
    
      Env.debug("Restart data available "
                + "(version " + Env.saveState().restartVersion() + ")");
      TinyStory restartStory = new TinyStory();
      boolean okay = restartStory.restore();
      if ( okay ) {
        int usedColours[] = mPanels.get(0).colours();
        MenuPanel restartPanel = new MenuRestart(restartStory, usedColours);
        mPanels.add(0, restartPanel);
      } else {
        Env.debug("Could not restore quest restart data");
        Env.saveState().clearRestartData();
      }
      
    } else {
      
      Env.debug("No quest restart data found");
      
    }

    if ( Env.platform() == Env.Platform.ANDROID || 
         Env.platform() == Env.Platform.OUYA ) {
      mPanels.add(new MenuResize());
    }
    
    if ( Env.platform() == Env.Platform.ANDROID ) {
      mPanels.add(new MenuControls());
    }
    
    mPanels.add(new MenuCredits());
    
    int startPanel = 0;
    for ( int k = 0 ; k < mPanels.size() ; k++ ) {
      if ( mStartOnTraining && mPanels.get(k) instanceof MenuTraining ) {
        startPanel = k;
        break;
      } else if ( mStartOnMap != null && mPanels.get(k) instanceof MenuMap ) {
        startPanel = k;
        break;
      }
    }
    mYPos = startPanel*Env.screenHeight();
    
    setPanelPositions();
    for ( MenuPanel panel : mPanels ) panel.prepare(spriteManager);

  } // makePanels()

  // update the vertical positions of all panels
  private void setPanelPositions() {
    
    for ( int k = 0 ; k < mPanels.size(); k++ ) {
      mPanels.get(k).setYPos( k*Env.screenHeight() - mYPos );
    }

  } // setPanelPositions()
  
  // remove any direction arrows
  private void clearArrows(SpriteManager spriteManager) {

    if ( mArrows != null ) {
      for ( MapArrow arrow : mArrows ) {
        if ( arrow != null ) spriteManager.removeSprite(arrow);
      }
      mArrows = null;
    }
    
  } // clearArrows()
  
} // class MenuStory
