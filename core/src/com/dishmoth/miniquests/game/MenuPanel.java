/*
 *  MenuPanel.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// one screen in the start-up menu
abstract public class MenuPanel {

  // how fast the panel text animates
  protected static final int kAnimTitleDelay = 60,
                             kAnimBeginDelay = 60,
                             kAnimBlankDelay = 20;
  
  // general 'press-a-key' text image
  protected static EgaImage kBeginText = null;
  
  // border on top of the panel background
  protected static EgaImage kFrameImage = null;
  
  // main image for the panel
  protected Picture mBackground = null;
  
  // load resources
  static void initialize() {
    
    if ( kBeginText == null ) {
      String gdxText = ( Env.platform()==Env.Platform.OUYA    ? "Controller" 
                       : Env.platform()==Env.Platform.ANDROID ? "Android" 
                       : Env.platform()==Env.Platform.IOS     ? "Android" 
                                                              : "" );      
      kBeginText = Env.resources().loadEgaImage("StartText"+gdxText+".png");
      
      kFrameImage = Env.resources().loadEgaImage("MenuFramePic.png");
    }
    
  } // initialize()
      
  // set up the panel
  public void prepare(SpriteManager spriteManager) {
    
    initialize();
    spriteManager.addSprite(mBackground);
    
  } // prepare()
  
  // update the vertical display position of the panel
  public void setYPos(int yPos) {
    
    mBackground.setYPos(yPos);
    
  } // setYPos()
  
  // called when the panel becomes active (scrolled onto)
  abstract public void enable(SpriteManager spriteManager);
  
  // called every frame when the panel is active (returns true to exit menu)
  abstract public boolean advance(SpriteManager spriteManager);

  // called when the panel stops being active (scrolled away from)
  abstract public void disable(SpriteManager spriteManager);

  // set up the next story for when the menu closes (some panels only)
  public Story exitMenu(LinkedList<StoryEvent> storyEvents,
                        SpriteManager          spriteManager) {
    
    assert(false);
    return null;
    
  } // exitMenu()
  
  // list all the colours used by the panel (so we can restrict the maximum)
  public int[] colours() {
    
    int count[] = EgaTools.colourHistogram(mBackground.image());
    
    // extra colours used for text and arrows
    count[0] += 1;
    count[38] += 1;
    count[52] += 1;
    count[59] += 1;
    count[63] += 1;

    int num = 0;
    for ( int k = 0 ; k < count.length ; k++ ) {
      if ( count[k] > 0 ) num++;
    }
    
    int cols[] = new int[num];
    for ( int k = 0, i = 0 ; k < count.length ; k++ ) {
      if ( count[k] > 0 ) cols[i++] = k;
    }
    return cols;
    
  } // colours()
  
} // class MenuPanel
