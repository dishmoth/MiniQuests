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
  private static final int kVersion = 1;
  
  // version for loaded state
  private int mRestartVersion;
  
  // what training is needed (0 => none, 1 => quick, 2 => full) 
  private int mTraining;
  
  // best hero rating for each quest (0 if not completed yet)
  private int mQuestScores[];

  // save/restore information for the game
  private BitBuffer mRestartData;

  // reference to the quest stats (times, deaths, etc. for debugging)
  private QuestStats mQuestStats;
  
  // whether there is anything to save
  private boolean mNeedToSave;
  
  // constructor
  public SaveState() {
    
    mRestartVersion = kVersion;
    
    mTraining = 2;
    
    mQuestScores = new int[ TinyStory.NUM_QUESTS ];
    Arrays.fill(mQuestScores, 0);
    
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
    
    mRestartData.fromBytes(data);
    decode();
    
  } // load()
  
  // restore the game state from a string (for debugging)
  public void load(String data) {

    mRestartData.fromString(data);
    
    decode();
    
  } // load(String)
  
  // restore state from the encoded bits
  private void decode() {
    
    Env.debug("Loading game state (\"" + mRestartData + "\")");
    
    int version = mRestartData.read(8);
    if ( version < 0 || version > kVersion ) {
      Env.debug("Failed to read saved version");
      mRestartData.clear();
      return;
    } else {
      Env.debug("Loading version " + version);
    }
    
    final int numQuestsToRead = (version == 0) ? 2
                                               : 3;
    for ( int k = 0 ; k < numQuestsToRead ; k++ ) {
      int score = mRestartData.read(4);
      if ( score < 0 || score > 5 ) {
        Env.debug("Failed to read score for quest " + k);
        Arrays.fill(mQuestScores, 0);
        mRestartData.clear();
        return;
      } else {
        Env.debug("Loaded quest " + k + ": score " + score);
        mQuestScores[k] = score;
      }
    }
    if ( (numQuestsToRead % 2) == 1 && mRestartData.numBitsToRead() > 0 ) {
      int padding = mRestartData.read(4);
      if ( padding != 0 ) mRestartData.clear();
    }
    
    if ( mTraining == 2 ) {
      mTraining = ( (Env.platform()==Env.Platform.ANDROID || 
                     Env.platform()==Env.Platform.OUYA) ? 0 : 1 );
    }
    
    if ( mRestartData.numBitsToRead() > 0 ) {
      Env.debug("Game state includes quest data");
      mRestartVersion = version;
    }
    
  } // decode()
  
  // save the game state
  public void save() {
    
    if ( !mNeedToSave ) return;
    mNeedToSave = false;
        
    BitBuffer buffer = new BitBuffer();
    
    buffer.write(kVersion, 8);
    
    for ( int k = 0 ; k < mQuestScores.length ; k++ ) {
      buffer.write(mQuestScores[k], 4);
    }
    
    buffer.appendBytes(mRestartData);
    
    Env.debug("Saving game state (\"" + buffer + "\")");
    
    Env.save( buffer.toBytes() );
    
    reportQuestStats();
    
  } // save()
  
  // whether to put the player through full hero training
  public boolean fullTrainingNeeded() { return (mTraining == 2); }

  // whether to give the player a reminder of the controls
  public boolean quickTrainingNeeded() { return (mTraining == 1); }

  // the player does not need further training
  public void setTrainingDone() { mTraining = 0; }

  // best hero rating for a quest (0 if not completed yet)
  public int questScore(int questNum) {
    
    assert( questNum >= 0 && questNum < TinyStory.NUM_QUESTS );
    return mQuestScores[questNum];
    
  } // questScore()

  // update the best score for a quest
  public void updateQuestScore(int questNum, int score) {
    
    assert( questNum >= 0 && questNum < TinyStory.NUM_QUESTS );
    assert( score >= 1 && score <= 5 );
    if ( mQuestScores[questNum] < score ) mQuestScores[questNum] = score;
    
  } // updateQuestScore()
  
  // reference to the restart buffer 
  public BitBuffer restartData() { return mRestartData; }

  // whether the state includes a saved quest
  public boolean hasRestartData() { return (mRestartData.numBitsToRead()>0); }

  // version of the saved quest data
  public int restartVersion() { return mRestartVersion; }

  // called if the saved data has been redefined
  public void updateRestartData() { mNeedToSave = true; }

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
