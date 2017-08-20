/*
 *  SaveState.java
 *  Copyright Simon Hern 2012
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.Arrays;

// keep track of the game's save information
public class SaveState {
  
  // current save version number
  private static final int kVersion = 3;
  
  // how much prompting is needed based on player's progress 
  // (0 => none, 1 => reminders, 2 => new game, 3 => hero training)
  private int mPrompting;
  
  // size factor for the game screen, if adjustable (-1 => undefined)
  public static final int MAX_SCREEN_SIZE = 20;
  private int mScreenSize;
  
  // format for on-screen buttons (-1 => undefined, 0 => corners, 1 => buttons)
  private int mTouchScreenControls;
  
  // size factor for the on-screen buttons, if present (-1 => undefined) 
  public static final int MAX_BUTTON_SIZE = 5;
  private int mButtonSize;
  
  // best hero rating for each quest (0 if not completed yet)
  private int mQuestScores[];

  // save/restore information for the current quest
  private int       mRestartVersion;
  private BitBuffer mRestartData;

  // reference to the quest stats (times, deaths, etc. for debugging)
  private QuestStats mQuestStats;
  
  // whether there is anything to save
  private boolean mNeedToSave;
  
  // constructor
  public SaveState() {
    
    mPrompting = 3;

    mScreenSize = -1;
    mTouchScreenControls = -1;
    mButtonSize = -1;
    
    mQuestScores = new int[ TinyStory.NUM_QUESTS ];
    Arrays.fill(mQuestScores, 0);
    
    mRestartVersion = kVersion;
    mRestartData = new BitBuffer();

    mQuestStats = null;
    
    mNeedToSave = false;
    
  } // constructor
  
  // restore the game state
  public void load() {

    byte data[] = Env.load();

    if ( data == null || data.length == 0 ) {
      Env.debug("No saved data found");
      return;
    }
    
    BitBuffer buffer = new BitBuffer();
    buffer.fromBytes(data);
    decode(buffer);
    
  } // load()
  
  // restore the game state from a string (for debugging)
  public void load(String data) {

    BitBuffer buffer = new BitBuffer();
    buffer.fromString(data);
    decode(buffer);
    
  } // load(String)
  
  // restore state from the encoded bits
  private void decode(BitBuffer buffer) {
    
    Env.debug("Loading game state (\"" + buffer + "\")");
    
    mPrompting = 3;
    mRestartData.clear();
    buffer.toStart();
    
    // version (8 bits)
    int version = buffer.read(8);
    if ( version < 0 || version > kVersion ) {
      Env.debug("Failed to read saved version");
      return;
    } else {
      Env.debug("Loading version " + version);
    }
    
    // screen size (8 bits, since version 3)
    if ( version >= 3 ) {
      int size = buffer.read(8);
      if ( size < 0 ) {
        Env.debug("Failed to read screen size value");
        return;
      } else if ( size <= MAX_SCREEN_SIZE ) {
        Env.debug("Loaded screen size value: " + size);
        mScreenSize = size;
      }
    }

    // touch-screen control scheme (4 bits, since version 3)
    if ( version >= 3 ) {
      int val = buffer.read(4);
      if ( val < 0 ) {
        Env.debug("Failed to read touch-screen control value");
        return;
      } else if ( val == 0 || val == 1 ) {
        Env.debug("Loaded touch-screen control value: " + val);
        mTouchScreenControls = val;
      }
    }

    // touch-screen button size (4 bits, since version 3)
    if ( version >= 3 ) {
      int size = buffer.read(4);
      if ( size < 0 ) {
        Env.debug("Failed to read touch-screen button size");
        return;
      } else if ( size <= MAX_BUTTON_SIZE ) {
        Env.debug("Loaded touch-screen button size: " + size);
        mButtonSize = size;
      }
    }

    // if the game hasn't been played yet then only the settings are saved 
    if ( version >= 3 ) {
      if ( buffer.numBitsToRead() == 0 ) {
        Env.debug("Only loaded settings");
        return;
      }
    }
    
    // user has played before, reduce the prompting needed
    mPrompting = ( Env.platform() == Env.Platform.ANDROID || 
                   Env.platform() == Env.Platform.IOS || 
                   Env.platform() == Env.Platform.OUYA ) ? 0 : 1;
    
    // quest scores (num quests x4 bits, padded to multiple of 8 bits)
    final int numQuestsToRead = (version == 0) ? 2
                              : (version == 1) ? 3
                              : (version == 2) ? 4
                                               : 4;
    for ( int k = 0 ; k < numQuestsToRead ; k++ ) {
      int score = buffer.read(4);
      if ( score < 0 || score > 5 ) {
        Env.debug("Failed to read score for quest " + k);
        Arrays.fill(mQuestScores, 0);
        return;
      } else {
        Env.debug("Loaded quest " + k + ": score " + score);
        mQuestScores[k] = score;
      }
    }
    if ( (numQuestsToRead % 2) == 1 && buffer.numBitsToRead() > 0 ) {
      int padding = buffer.read(4);
      if ( padding != 0 ) {
        Env.debug("Failed to read quest scores (unexpected data)");
        return;
      }
    }

    // quest restart data
    if ( buffer.numBitsToRead() > 0 ) {
      Env.debug("Game state includes quest data");
      // restart version (8 bits, since version 3)
      if ( version >= 3 ) {
        int ver = buffer.read(8);
        if ( ver < 0 || ver > version || buffer.numBitsToRead() == 0 ) {
          Env.debug("Failed to read quest restart version");
          return;
        }
        Env.debug("Loaded quest restart version: " + ver);
        mRestartVersion = ver;
      } else {
        mRestartVersion = version;
      }
      // restart data (the rest of the buffer)
      mRestartData.append(buffer);
      mRestartData.toStart();
    }

  } // decode()
  
  // save the game state
  public void save() {
    
    if ( !mNeedToSave ) return;
    mNeedToSave = false;
        
    BitBuffer buffer = new BitBuffer();
    boolean startedGame = (mPrompting <= 1);

    // version (8 bits)
    buffer.write(kVersion, 8);
    
    // screen size (8 bits, -1 if not defined)
    if ( mScreenSize >= 0 && mScreenSize <= MAX_SCREEN_SIZE ) {
      buffer.write(mScreenSize, 8);
    } else {
      buffer.write(255, 8);
    }
    
    // touch-screen control scheme (4 bits, -1 if not defined)
    if ( mTouchScreenControls == 0 || mTouchScreenControls == 1 ) {
      buffer.write(mTouchScreenControls, 4);
    } else {
      buffer.write(15, 4);
    }
    
    // touch-screen button size (4 bits, -1 if not defined)
    if ( mButtonSize >= 0 && mButtonSize <= MAX_BUTTON_SIZE ) {
      buffer.write(mButtonSize, 4);
    } else {
      buffer.write(15, 4);
    }
    
    // quest scores (num quests x4 bits, padded to multiple of 8 bits)
    if ( startedGame ) {
      for ( int k = 0 ; k < mQuestScores.length ; k++ ) {
        buffer.write(mQuestScores[k], 4);
      }
      if ( (mQuestScores.length % 2) == 1 ) {
        buffer.write(0, 4);
      }
    }
  
    // quest restart data (8 bits for version, data appended)
    if ( startedGame && mRestartData.numBits() > 0 ) {
      buffer.write(mRestartVersion, 8);
      mRestartData.toStart();
      buffer.append(mRestartData);
      mRestartData.toStart();
    }
    
    Env.debug("Saving game state (\"" + buffer + "\")");
    
    Env.save( buffer.toBytes() );
    
    reportQuestStats();
    
  } // save()
  
  // save now if the platform needs it; otherwise only save when paused/stopped
  public void saveMaybe() {
    
    if ( Env.platform() == Env.Platform.HTML ) save();
    
  } // saveMaybe()
  
  // whether to prompt the player to do hero training
  public boolean heroTrainingNeeded() { return (mPrompting >= 3); }

  // player has done the training
  public void heroTrainingDone() { mPrompting = Math.min(mPrompting, 2); }

  // whether the player has started a game yet
  public boolean newGameNeeded() { return (mPrompting >= 2); }

  // the player has started playing (seen the map screen at least)
  public void newGameDone() { 
    
    if ( mPrompting >= 2 ) {
      mNeedToSave = true;
      mPrompting = Math.min(mPrompting, 1);
      if ( Env.platform() == Env.Platform.ANDROID || 
           Env.platform() == Env.Platform.IOS || 
           Env.platform() == Env.Platform.OUYA ) {
        mPrompting = 0;
      }
    }
    
  } // setPlayedBefore()
  
  // whether to give the player a reminder of the controls
  public boolean remindersNeeded() { return (mPrompting >= 1); }

  // player has seen enough reminders
  public void remindersDone() { mPrompting = 0; }
  
  // size factor for the game screen (-1 if undecided) 
  public int screenSize() { return mScreenSize; }
  
  // set the size factor for the game screen
  public void setScreenSize(int size) { 
    
    assert( size >= 0 && size <= MAX_SCREEN_SIZE );
    if ( mScreenSize != size ) mNeedToSave = true;
    mScreenSize = size;
    
  } // setScreenSize()
  
  // format for on-screen buttons (-1 => undefined, 0 => corners, 1 => buttons)
  public int touchScreenControls() { return mTouchScreenControls; }
  
  // set the on-screen button format
  public void setTouchScreenControls(int scheme) { 
    
    assert( scheme >= 0 && scheme <= 1 );
    if ( mTouchScreenControls != scheme ) mNeedToSave = true;
    mTouchScreenControls = scheme;
    
  } // setScreenSize()
  
  // size factor for the on-screen buttons (-1 if undecided)
  public int buttonSize() { return mButtonSize; }
  
  // set the size factor for the on-screen buttons
  public void setButtonSize(int size) {
    
    assert( size >= 0 && size <= MAX_BUTTON_SIZE );
    if ( mButtonSize != size ) mNeedToSave = true;
    mButtonSize = size;
    
  } // setButtonSize()
  
  // best hero rating for a quest (0 if not completed yet)
  public int questScore(int questNum) {
    
    assert( questNum >= 0 && questNum < TinyStory.NUM_QUESTS );
    return mQuestScores[questNum];
    
  } // questScore()

  // update the best score for a quest
  public void updateQuestScore(int questNum, int score) {
    
    assert( questNum >= 0 && questNum < TinyStory.NUM_QUESTS );
    assert( score >= 1 && score <= 5 );
    if ( mQuestScores[questNum] < score ) {
      mQuestScores[questNum] = score;
      mNeedToSave = true;
    }
    
  } // updateQuestScore()
  
  // reference to the restart buffer 
  public BitBuffer restartData() { return mRestartData; }

  // whether the state includes a saved quest
  public boolean hasRestartData() { return (mRestartData.numBits() > 0); }

  // version of the saved quest data
  public int restartVersion() { return mRestartVersion; }

  // new saved data from the current quest 
  public void setRestartData(BitBuffer data) {
    
    mRestartData = data;
    mRestartVersion = kVersion;
    mNeedToSave = true;
    
  } // setRestartData()
  
  // clear any saved data from a quest 
  public void clearRestartData() {
    
    mRestartData.clear();
    mRestartVersion = kVersion;
    mNeedToSave = true;
    
  } // setRestartData()
  
  // return the quest stats
  public QuestStats questStats() { return mQuestStats; }
  
  // set the reference to the quest stats
  public void setQuestStats(QuestStats stats) {  mQuestStats = stats; } 
  
  // output a log string and send a copy back to HQ 
  public void reportQuestStats() {

    if ( mQuestStats == null ) return;

    String statsString = mQuestStats.encode();
    Env.debug("\"" + statsString + "\"");

    Env.report(statsString);
    
  } // reportQuestStats()
  
} // class SaveState
