/*
 *  MapStory.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.Iterator;
import java.util.LinkedList;

// the main game class
public class MapStory extends Story {

  // quest names
  private static EgaImage  kQuestImages[] = null;

  // other text images
  private static EgaImage kIntroImage,
                          kEnterImage,
                          kHiscoreImage;
  
  // map data (images, exits, etc)
  private static MapData kMapData;
  
  // times for things to happen
  private static final int kInitDelay       = 20,
                           kIntroDelay      = 50,
                           kIntroPause      = 10,
                           kQuestIntroDelay = 20,
                           kQuestNameDelay  = 60,
                           kQuestEnterDelay = 60,
                           kQuestBlankDelay = 20,
                           kQuestMapPause   = 40,
                           kBeginDelay      = 5;
  
  // reference to the map object
  private Map mMap;
  
  // reference to the current text object
  private AnimPicture mText;

  // reference to the magic stones (or null)
  private MapStones mStones;
  
  // current quest on offer (or -1)
  private int mQuest;
  
  // time until the quest begins
  private int mTimer;
  
  // keep track of whether the escape key is held down
  private boolean mEscPressed;
  
  // load resources
  static public void initialize() {
    
    if ( kQuestImages != null ) return;
    
    kQuestImages = new EgaImage[TinyStory.NUM_QUESTS];
    for ( int k = 0 ; k < TinyStory.NUM_QUESTS ; k++ ) {
      kQuestImages[k] = Env.resources().loadEgaImage("Quest" + (k+1) 
                                                     + "Text.png");
    }

    String gdxText = ( Env.platform()==Env.Platform.OUYA    ? "Controller" 
                     : Env.platform()==Env.Platform.ANDROID ? "Android" 
                                                            : "" );      
    kIntroImage = Env.resources().loadEgaImage("IntroText.png");
    kEnterImage = Env.resources().loadEgaImage("EnterText"+gdxText+".png");
    kHiscoreImage = Env.resources().loadEgaImage("HiscoreText.png");
    
    kMapData = new MapDataMain();
    
  } // initialize()
  
  // constructor
  public MapStory(int quest) {

    initialize();

    assert( quest >= -1 && quest < TinyStory.NUM_QUESTS );
    mQuest = quest;

    mTimer = 0;
    
  } // constructor
  
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
        mText = null;
        mStones = null;
        mMap = new Map(kMapData, mQuest);
        spriteManager.addSprite(mMap);
        if ( mQuest == -1 ) {
          spriteManager.addSprite(new AnimPicture(kInitDelay, kIntroImage, 
                                                  kIntroDelay, -1));
          mMap.pause(kInitDelay + kIntroDelay + kIntroPause);
          Env.sounds().play(Sounds.VENTURE, kInitDelay);
        }
        mEscPressed = true;
        Env.keys().setMode(KeyMonitor.MODE_MAP);
        it.remove();
      } // Story.EventGameBegins

      else if ( event instanceof MapData.EventAtDungeon ) {
        // the player has reached a dungeon entrance
        mQuest = ((MapData.EventAtDungeon)event).mNum;
        int score = Env.saveState().questScore(mQuest);
        EgaImage secondImage = (score == 0) ? kEnterImage
                               : makeHiscoreImage(score);
        mText = new AnimPicture(kQuestIntroDelay, 
                                kQuestImages[mQuest], 
                                kQuestNameDelay, kQuestBlankDelay, 
                                secondImage, 
                                kQuestEnterDelay, kQuestBlankDelay);
        spriteManager.addSprite(mText);
        mMap.pause(kQuestMapPause);
        mMap.dungeonEntrance();
        Env.sounds().play(Sounds.DUNGEON, kQuestIntroDelay);
        it.remove();
      } // MapData.EventAtDungeon
        
      else if ( event instanceof MapDataMain.EventAtStones ) {
        // the player is at the stones
        boolean questsComplete[] = new boolean[TinyStory.NUM_QUESTS];
        for ( int k = 0 ; k < TinyStory.NUM_QUESTS ; k++ ) {
          questsComplete[k] = ( Env.saveState().questScore(k) > 0 );
        }
        mStones = new MapStones(questsComplete);
        spriteManager.addSprite(mStones);
        it.remove();
      } // MapDataMain.EventAtStones
      
      else if ( event instanceof Map.EventLeftLocation ) {
        // the player has left a new map location
        mQuest = -1;
        if ( mText != null ) {
          spriteManager.removeSprite(mText);
          mText = null;
        }
        if ( mStones != null ) {
          spriteManager.removeSprite(mStones);
          mStones = null;
        }
        it.remove();
      } // Map.EventLeftLocation

      else if ( event instanceof Map.EventEnterDungeon ) {
        // the player has entered a dungeon
        assert( mQuest != -1 );
        if ( mText != null ) {
          spriteManager.removeSprite(mText);
          mText = null;
        }
        if ( mStones != null ) {
          spriteManager.removeSprite(mStones);
          mStones = null;
        }
        mTimer = kBeginDelay;
        it.remove();
      } // Map.EventEnterDungeon
      
      else {
        Env.debug("event ignored: " + event.getClass());
        it.remove();
      }
      
    } // for each story event

    // enter a dungeon
    if ( mTimer > 0 ) {
      if ( --mTimer == 0 ) {
        newStory = new IntroStory(mQuest);
        storyEvents.add(new Story.EventGameBegins());
        tidySprites(spriteManager);
      }
    }
    
    // quest aborted
    if ( Env.keys().escape() ) {
      if ( !mEscPressed && newStory == null ) {
        if ( Env.platform() == Env.Platform.ANDROID ) {
          Env.exit();
          mTimer = 300; // delay while the 'exit' takes hold
        }
      }
      mEscPressed = true;
    } else {
      mEscPressed = false;
    }    
    
    return newStory;
    
  } // Story.advance()

  // return an image with the correct number of stars filled in
  private EgaImage makeHiscoreImage(int score) {
    
    assert( score >= 0 && score <= 5 );
    if ( score == 0 ) return kHiscoreImage;
    
    EgaImage image = kHiscoreImage.clone();
    EgaImage star = EndStory.starImage();
    for ( int k = 0 ; k < score ; k++ ) {
      star.draw(image, 6+6*k, 22);
    }
    return image;
    
  } // makeHiscoreImage()
  
  // remove all sprites except the map
  private void tidySprites(SpriteManager spriteManager) {

    LinkedList<Sprite> deadSprites = new LinkedList<Sprite>();
    for ( Sprite s : spriteManager.list() ) {
      if ( s instanceof Map ) continue; 
      deadSprites.add(s);
    }
    if (deadSprites.size() > 0) spriteManager.removeSprites(deadSprites);
    
  } // tidySprite()
  
} // class MapStory