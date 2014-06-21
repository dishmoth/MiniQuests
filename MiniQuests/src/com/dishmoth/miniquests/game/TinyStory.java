/*
 *  TinyStory.java
 *  Copyright Simon Hern 2010
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.rooms.RoomA01;
import com.dishmoth.miniquests.rooms.RoomA02;
import com.dishmoth.miniquests.rooms.RoomA03;
import com.dishmoth.miniquests.rooms.RoomA04;
import com.dishmoth.miniquests.rooms.RoomA05;
import com.dishmoth.miniquests.rooms.RoomA06;
import com.dishmoth.miniquests.rooms.RoomA07;
import com.dishmoth.miniquests.rooms.RoomA08;
import com.dishmoth.miniquests.rooms.RoomA09;
import com.dishmoth.miniquests.rooms.RoomA10;
import com.dishmoth.miniquests.rooms.RoomA11;
import com.dishmoth.miniquests.rooms.RoomA12;
import com.dishmoth.miniquests.rooms.RoomA13;
import com.dishmoth.miniquests.rooms.RoomB01;
import com.dishmoth.miniquests.rooms.RoomB02;
import com.dishmoth.miniquests.rooms.RoomB03;
import com.dishmoth.miniquests.rooms.RoomB04;
import com.dishmoth.miniquests.rooms.RoomB05;
import com.dishmoth.miniquests.rooms.RoomB06;
import com.dishmoth.miniquests.rooms.RoomB07;
import com.dishmoth.miniquests.rooms.RoomB08;
import com.dishmoth.miniquests.rooms.RoomB09;
import com.dishmoth.miniquests.rooms.RoomB10;
import com.dishmoth.miniquests.rooms.RoomB11;
import com.dishmoth.miniquests.rooms.RoomB12;
import com.dishmoth.miniquests.rooms.RoomB13;
import com.dishmoth.miniquests.rooms.RoomC01;
import com.dishmoth.miniquests.rooms.RoomC02;
import com.dishmoth.miniquests.rooms.RoomC03;
import com.dishmoth.miniquests.rooms.RoomC04;
import com.dishmoth.miniquests.rooms.RoomC05;
import com.dishmoth.miniquests.rooms.RoomC06;
import com.dishmoth.miniquests.rooms.RoomC07;
import com.dishmoth.miniquests.rooms.RoomC08;
import com.dishmoth.miniquests.rooms.RoomC09;
import com.dishmoth.miniquests.rooms.RoomC10;
import com.dishmoth.miniquests.rooms.RoomC11;
import com.dishmoth.miniquests.rooms.RoomC12;
import com.dishmoth.miniquests.rooms.RoomC13;
import com.dishmoth.miniquests.rooms.RoomC14;
import com.dishmoth.miniquests.rooms.RoomC15;
import com.dishmoth.miniquests.rooms.RoomC16;
import com.dishmoth.miniquests.rooms.RoomD01;
import com.dishmoth.miniquests.rooms.RoomD02;
import com.dishmoth.miniquests.rooms.RoomD03;
import com.dishmoth.miniquests.rooms.RoomD04;
import com.dishmoth.miniquests.rooms.RoomD05;
import com.dishmoth.miniquests.rooms.RoomD06;
import com.dishmoth.miniquests.rooms.RoomD07;
import com.dishmoth.miniquests.rooms.RoomD08;
import com.dishmoth.miniquests.rooms.RoomD09;
import com.dishmoth.miniquests.rooms.RoomD10;
import com.dishmoth.miniquests.rooms.RoomD11;
import com.dishmoth.miniquests.rooms.RoomD12;
import com.dishmoth.miniquests.rooms.RoomD13;
import com.dishmoth.miniquests.rooms.RoomD14;
import com.dishmoth.miniquests.rooms.RoomD15;
import com.dishmoth.miniquests.rooms.RoomD16;
import com.dishmoth.miniquests.rooms.RoomD17;
import com.dishmoth.miniquests.rooms.RoomD18;

// the main game class
public class TinyStory extends Story {

  // story event: the quest has been completed
  public static class EventPlayerWins extends StoryEvent {
    public EventPlayerWins() {}
  } // class TinyStory.EventPlayerWins

  // story event: something has happened that makes it worth saving
  public static class EventSaveGame extends StoryEvent {
    public EventSaveGame() {}
  } // class TinyStory.EventSaveGame
  
  // how many quests there are
  public static final int NUM_QUESTS = 4;
  
  // times for certain actions
  private static final int kPlayerDeathTime = 30;

  // range of quest results for calculating the overall rating
  private static final int   kQuestDeathBest[]  = {    0,    0,    0 },
                             kQuestDeathWorst[] = {  100,  100,  100 };
  private static final float kQuestTimeBest[]   = {  290,  265,  380 },
                                   // personal best: 247,  216,  332
                             kQuestTimeWorst[]  = { 3000, 3000, 3000 };

  // which quest we're doing
  private int mQuestNum;
  
  // list of all rooms that make up this story
  private Room mRoomList[];
  
  // reference to the current room
  private Room mCurrentRoom;

  // reference to the player (or null if the player has just died)
  private Player mPlayer;

  // entry index for the current room (respawn point)
  private int mLastEntryPoint;
  
  // time remaining until the player respawns
  private int mPlayerDeathTimer;

  // keep track of whether the escape key is held down
  private boolean mEscPressed;

  // constructor (for a specific quest)
  public TinyStory(int questNum) {

    mQuestNum = questNum;
    
    makeRoomList();

    mCurrentRoom = null;
    mPlayer = null;

    Env.saveState().setQuestStats( new QuestStats(mQuestNum) );

  } // constructor

  // constructor (for restore from a save; must call restart() before playing)
  public TinyStory() {

    mQuestNum = -1;
    mRoomList = null;
    mCurrentRoom = null;
    mPlayer = null;
    
    Env.saveState().setQuestStats(null);

  } // constructor

  // create the rooms for the quest
  private void makeRoomList() {
    
    switch ( mQuestNum ) {
      case 0: {
        mRoomList = new Room[]{ new RoomA01(),
                                new RoomA02(),
                                new RoomA03(),
                                new RoomA04(),
                                new RoomA05(),
                                new RoomA06(),
                                new RoomA07(),
                                new RoomA08(),
                                new RoomA09(),
                                new RoomA10(),
                                new RoomA11(),
                                new RoomA12(),
                                new RoomA13() };
      } break;
      case 1: {
        mRoomList = new Room[]{ new RoomB01(),
                                new RoomB02(),
                                new RoomB03(),
                                new RoomB04(),
                                new RoomB05(),
                                new RoomB06(),
                                new RoomB07(),
                                new RoomB08(),
                                new RoomB09(),
                                new RoomB10(),
                                new RoomB11(),
                                new RoomB12(),
                                new RoomB13() };
      } break;
      case 2: {
        mRoomList = new Room[]{ new RoomC01(),
                                new RoomC02(),
                                new RoomC03(),
                                new RoomC04(),
                                new RoomC05(),
                                new RoomC06(),
                                new RoomC07(),
                                new RoomC08(),
                                new RoomC09(),
                                new RoomC10(),
                                new RoomC11(),
                                new RoomC12(),
                                new RoomC13(),
                                new RoomC14(),
                                new RoomC15(),
                                new RoomC16() };
      } break;
      case 3: {
        mRoomList = new Room[]{ new RoomD01(),
                                new RoomD02(),
                                new RoomD03(),
                                new RoomD04(),
                                new RoomD05(),
                                new RoomD06(),
                                new RoomD07(),
                                new RoomD08(),
                                new RoomD09(),
                                new RoomD10(),
                                new RoomD11(),
                                new RoomD12(),
                                new RoomD13(),
                                new RoomD14(),
                                new RoomD15(),
                                new RoomD16(),
                                new RoomD17(),
                                new RoomD18() };
      } break;
      /*
      case 4: {
        mRoomList = new Room[]{ new RoomE01(),
                                new RoomE02(),
                                new RoomE03(),
                                new RoomE04(),
                                new RoomE05() };
      } break;
      */
      default: {
        assert( false );
      } break;
    }
    
    for ( Room room : mRoomList ) room.setRoomList(mRoomList);
    
  } // makeRoomList()
  
  // which quest we're playing
  public int questNumber() { return mQuestNum; }
  
  // process events and advance 
  @Override
  public Story advance(LinkedList<StoryEvent> storyEvents,
                       SpriteManager          spriteManager) {

    // check for a story restart (skip this advance in such cases)
    boolean firstAdvanceOfGame = false;
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();
      if ( event instanceof Story.EventStoryContinue ) {
        Env.keys().setMode(KeyMonitor.MODE_GAME);
        it.remove();
        return null;
      }
      if ( event instanceof Story.EventGameBegins ) {
        firstAdvanceOfGame = true;
      }
    }
    
    // update the current room
    if ( !firstAdvanceOfGame ) {
      mCurrentRoom.advance(storyEvents, spriteManager);
    }

    Story newStory = null;
    boolean questComplete = false;
    boolean saveGame = false;
    
    // process the story event list
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();

      if ( event instanceof Story.EventGameBegins ) {
        // first frame of the story, so set everything up
        assert( firstAdvanceOfGame );
        Camera camera = new Camera();
        for ( Room room : mRoomList ) room.setCamera(camera);
        spriteManager.addSprite(camera);
        if ( mCurrentRoom == null ) {
          Env.debug("New game started");
          mCurrentRoom = mRoomList[0];
          mCurrentRoom = findRoom( RoomD05.NAME ); //!!!
          mLastEntryPoint = 0; //0;
        }
        mCurrentRoom.createSprites(spriteManager);
        mPlayer = mCurrentRoom.createPlayer(mLastEntryPoint);
        spriteManager.addSprite(mPlayer);
        Env.saveState().questStats().newRoom( currentRoomIndex() );
        mPlayerDeathTimer = 0;
        Env.keys().setMode(KeyMonitor.MODE_GAME);
        Env.keys().setButtonDetails(1, 1);
        mEscPressed = true;
        it.remove();
      } // Story.EventGameBegins

      else if ( event instanceof Room.EventRoomChange ) {
        Room.EventRoomChange e = (Room.EventRoomChange)event;
        mCurrentRoom.removePlayer();
        mCurrentRoom.discardResources();
        clearRoom(spriteManager);
        mCurrentRoom = findRoom(e.mNewRoom);
        mCurrentRoom.createSprites(spriteManager);
        mLastEntryPoint = e.mEntryPoint;
        mPlayer = mCurrentRoom.createPlayer(mLastEntryPoint);
        spriteManager.addSprite(mPlayer);
        mPlayerDeathTimer = 0; // in case respawning in a different room
        Env.saveState().questStats().newRoom( currentRoomIndex() );
        saveGame = true;
        it.remove();
      } // Room.EventRoomChange 
      
      else if ( event instanceof Room.EventRoomScroll ) {
        Room.EventRoomScroll e = (Room.EventRoomScroll)event;
        newStory = new ScrollStory(this, e.mShiftX, e.mShiftY, e.mShiftZ);
        it.remove();
      } // Room.EventRoomScroll
      
      else if ( event instanceof Room.EventNewEntryPoint ) {
        Room.EventNewEntryPoint e = (Room.EventNewEntryPoint)event;
        mLastEntryPoint = e.mEntryPoint;
        saveGame = true;
        it.remove();
      } // Room.EventNewEntryPoint
      
      else if ( event instanceof Player.EventKilled ) {
        mCurrentRoom.removePlayer();
        mPlayer = null;
        mPlayerDeathTimer = kPlayerDeathTime;
        Env.sounds().play(Sounds.HERO_DEATH);
        Env.saveState().questStats().countDeath();
        saveGame = true;
        it.remove();
      } // Player.EventKilled
      
      else if ( event instanceof EventSaveGame ) {
        saveGame = true;
        it.remove();
      } // EventSaveGame

      else if ( event instanceof EventPlayerWins ) {
        questComplete = true;
        it.remove();
      } // EventPlayerWins

      else {
        Env.debug("event ignored: " + event.getClass());
        it.remove();
      }
      
    } // for each story event

    if ( saveGame ) recordRestartState();
    
    Env.saveState().questStats().countTime();
    
    // respawn player
    if ( mPlayerDeathTimer > 0 && !mCurrentRoom.paused() ) {
      if ( --mPlayerDeathTimer == 0 ) {
        assert( mPlayer == null );
        mPlayer = mCurrentRoom.createPlayer(mLastEntryPoint);
        spriteManager.addSprite(mPlayer);
        Env.sounds().play(Sounds.HERO_GRUNT);
      }
    }

    // quest finished
    if ( questComplete ) {
      Env.debug("Game finished");
      Env.keys().setButtonDetails(0, 0);
      final int score = questScore();
      Env.saveState().updateQuestScore(mQuestNum, score);
      Env.saveState().restartData().clear();
      Env.saveState().updateRestartData();
      Env.saveState().reportQuestStats();
      Env.saveState().setQuestStats(null);
      Env.saveState().saveMaybe();
      newStory = new EndStory(score, mQuestNum);
      storyEvents.add(new Story.EventGameBegins());
    }

    // quest aborted
    if ( Env.keys().escape() ) {
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

  // retrieve a room based on its uniquename
  private Room findRoom(String roomName) {

    Room foundRoom = null;
    for ( Room room : mRoomList ) {
      if ( room.name().equals(roomName)  ) {
        assert( foundRoom == null );
        foundRoom = room;
      }
    }
    assert( foundRoom != null );
    return foundRoom;
    
  } // findRoom()

  // the index of the current room, for debugging
  private int currentRoomIndex() {
    
    for ( int index = 0 ; index < mRoomList.length ; index++ ) {
      if ( mCurrentRoom == mRoomList[index] ) return index;
    }
    assert(false);
    return -1;
    
  } // currentRoomIndex()
  
  // remove all of the sprites for the room
  private void clearRoom(SpriteManager spriteManager) {

    LinkedList<Sprite> deadSprites = new LinkedList<Sprite>();
    for (Sprite s : spriteManager.list()) {
      if ( s instanceof Camera ) continue; 
      deadSprites.add(s);
    }
    if (deadSprites.size() > 0) spriteManager.removeSprites(deadSprites);
    
  } // clearRoom()

  // save the game state 
  private void recordRestartState() {
    
    BitBuffer buffer = Env.saveState().restartData();
    buffer.clear();

    buffer.write(mQuestNum, 4);
    buffer.write(currentRoomIndex(), 8);
    buffer.write(mLastEntryPoint, 6);

    Env.saveState().questStats().save(buffer);
    
    for ( Room room : mRoomList ) room.save(buffer);

    Env.saveState().updateRestartData();
    Env.saveState().saveMaybe();
    
  } // recordRestartState()

  // restore the state of a quest from save data 
  public boolean restore(int version, BitBuffer buffer) {

    mQuestNum = buffer.read(4);
    if ( mQuestNum < 0 || mQuestNum >= NUM_QUESTS ) return false;
    
    makeRoomList();
    
    int roomIndex = buffer.read(8);
    if ( roomIndex < 0 || roomIndex >= mRoomList.length ) return false;
    mCurrentRoom = mRoomList[roomIndex];
    
    mLastEntryPoint = buffer.read(6);
    if ( mLastEntryPoint < 0 ) return false;
    
    Env.saveState().setQuestStats( new QuestStats(mQuestNum) );
    Env.saveState().questStats().restore(version, buffer);
    
    for ( Room room : mRoomList ) {
      boolean okay = room.restore(version, buffer);
      if ( !okay ) return false;
    }

    Env.debug("Restored data for quest " + mQuestNum 
              + ", room " + roomIndex
              + ", entry point " + mLastEntryPoint); 
    
    return true;
    
  } // restore()
  
  // returns a result in the range [0,1]
  private float interp(float val, float val0, float val1) {

    assert( val0 != val1 );
    float y = (val - val0) / (val1 - val0);
    return Math.max(0.0f, Math.min(1.0f, y));
    
  } // interp()
  
  // judge the player's rating at the end of the quest (1 to 5)
  private int questScore() {

    QuestStats stats = Env.saveState().questStats();
    
    final float time = stats.totalTime();
    Env.debug("Completion time: " + Math.round(10*time)/10.0f + " seconds");
    Env.debug("Number of deaths: " + stats.totalDeaths());

    final float timeVal  = interp(time, 
                                  kQuestTimeWorst[mQuestNum], 
                                  kQuestTimeBest[mQuestNum]);
    final float timeVal3 = timeVal*timeVal*timeVal;

    final float deathVal  = interp(stats.totalDeaths(), 
                                   kQuestDeathWorst[mQuestNum], 
                                   kQuestDeathBest[mQuestNum]);
    final float deathVal3 = deathVal*deathVal*deathVal;
    
    final int score = (int)Math.floor( 1 + 2*timeVal3 + 2*deathVal3 );

    return score;
    
  } // questScore()
  
} // class TinyStory
