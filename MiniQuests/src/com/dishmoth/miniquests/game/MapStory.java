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

  // quest details
  private static final int kNumQuests = 3;
  private static final int kQuestPos[][] = { { -1, -1 },
                                             { -2, +1 },
                                             { +3, -2 } };
  private static EgaImage  kQuestImages[] = null;

  // location of the magic stones
  private static final int kStonesPos[] = { +1, +1 }; 
  
  // other text images
  private static EgaImage kIntroImage,
                          kEnterImage,
                          kHiscoreImage;
  
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
    
    kQuestImages = new EgaImage[kNumQuests];
    for ( int k = 0 ; k < kNumQuests ; k++ ) {
      kQuestImages[k] = Env.resources().loadEgaImage("Quest" + (k+1) 
                                                     + "Text.png");
    }

    String gdxText = ( Env.platform()==Env.Platform.OUYA    ? "Controller" 
                     : Env.platform()==Env.Platform.ANDROID ? "Android" 
                                                            : "" );      
    kIntroImage = Env.resources().loadEgaImage("IntroText.png");
    kEnterImage = Env.resources().loadEgaImage("EnterText"+gdxText+".png");
    kHiscoreImage = Env.resources().loadEgaImage("HiscoreText.png");
    
  } // initialize()
  
  // constructor
  public MapStory(int quest) {

    initialize();

    assert( quest >= -1 && quest < kNumQuests );
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
        int x = ( (mQuest >= 0) ? kQuestPos[mQuest][0] : 0 ),
            y = ( (mQuest >= 0) ? kQuestPos[mQuest][1] : 0 );
        mMap = new Map(x, y);
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

      else if ( event instanceof Map.EventNewLocation ) {
        // the player has entered a new map location
        int x = ((Map.EventNewLocation)event).mXPos,
            y = ((Map.EventNewLocation)event).mYPos;
        for ( int index = 0 ; index < kNumQuests ; index++ ) {
          if ( x == kQuestPos[index][0] && y == kQuestPos[index][1] ) {
            mQuest = index;
            int score = Env.saveState().questScore(mQuest);
            EgaImage secondImage = (score == 0) ? kEnterImage
                                   : makeHiscoreImage(score);
            mText = new AnimPicture(kQuestIntroDelay, 
                                    kQuestImages[index], 
                                    kQuestNameDelay, kQuestBlankDelay, 
                                    secondImage, 
                                    kQuestEnterDelay, kQuestBlankDelay);
            spriteManager.addSprite(mText);
            mMap.pause(kQuestMapPause);
            mMap.dungeonEntrance();
            Env.sounds().play(Sounds.DUNGEON, kQuestIntroDelay);
            break;
          }
        }
        if ( x == kStonesPos[0] && y == kStonesPos[1] ) {
          boolean questsComplete[] = new boolean[kNumQuests];
          for ( int k = 0 ; k < kNumQuests ; k++ ) {
            questsComplete[k] = ( Env.saveState().questScore(k) > 0 );
          }
          mStones = new MapStones(questsComplete);
          spriteManager.addSprite(mStones);
        }
        it.remove();
      } // Map.EventNewLocation

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
