/*
 *  QuestStats.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// collect basic statistics during a quest
public class QuestStats {

  // statistics for one room
  private final class RoomStats {
    int roomNum, deaths, time;
    RoomStats(int r) { roomNum=r; deaths=0; time=0; }
  } // class QuestStats.RoomStats

  // how the stat summary is encoded in a string
  private static final int kNumEncodeChars = 10 + 26 + 26;
  
  // which quest we're doing (-1 for training)
  private final int mQuestNum;

  // collected stats
  private LinkedList<RoomStats> mStats = new LinkedList<RoomStats>();
  
  // extra stats that were restored from a restart
  private int mSavedDeaths,
              mSavedTime;
  
  // constructor
  public QuestStats(int questNum) {
  
    assert( questNum >= -1 );
    mQuestNum = questNum;
    
    mSavedDeaths = 0;
    mSavedTime = 0;
    
  } // constructor
  
  // record a change of room
  public void newRoom(int roomNum) {
    
    assert( roomNum >= 0 );
    mStats.add(new RoomStats(roomNum));
    
  } // newRoom()

  // record an advance in time
  public void countTime() {
    
    assert( mStats.size() > 0 );
    mStats.getLast().time += 1;
    
  } // countTime()
  
  // record a death
  public void countDeath() {
    
    assert( mStats.size() > 0 );
    mStats.getLast().deaths += 1;
    
  } // countDeath()

  // which quest we're doing (-1 for training)
  public int questNum() {
    
    return mQuestNum;
    
  } // questNum()
  
  // count the total time (in seconds)
  public float totalTime() {
    
    int t = mSavedTime;
    for ( RoomStats stats : mStats ) t += stats.time;
    return t/(float)Env.ticksPerSecond();
    
  } // totalTime()
  
  // count the total number of deaths
  public int totalDeaths() {
    
    int n = mSavedDeaths;
    for ( RoomStats stats : mStats ) n += stats.deaths;
    return n;
    
  } // totalDeaths()
  
  // record the parts of the stats that contribute to the quest score
  public void save(BitBuffer buffer) {
    
    int numDeaths = Math.min(255, totalDeaths());
    int numSecs = Math.min(4095, (int)Math.ceil(totalTime()));
    
    buffer.write(numDeaths, 8);
    buffer.write(numSecs, 12);
    
  } // save()
  
  // restore the parts of the stats that contribute to the quest score
  // (returns false if the data could not be read, or version was too old)
  public boolean restore(int version, BitBuffer buffer) {

    int numDeaths = buffer.read(8);
    if ( numDeaths < 0 || numDeaths > 255 ) return false;
    int numSecs = buffer.read(12);
    if ( numSecs < 0 || numSecs > 4095 ) return false;
    
    Env.debug("Restored stats: num deaths " + numDeaths 
              + ", num secs " + numSecs);
    
    mSavedDeaths = numDeaths;
    mSavedTime = numSecs*Env.ticksPerSecond();
    return true;
    
  } // restore()
  
  // convert the stats into a munged-up string
  public String encode() {
    
    if ( mQuestNum < 0 ) return null;
    
    int data[] = new int[3 + 4*mStats.size()];
    
    int index = 0;
    data[index++] = 0; // blank
    data[index++] = 0; // version
    data[index++] = mQuestNum;
    for ( RoomStats stats : mStats ) {
      assert( stats.roomNum >= 0 && stats.roomNum < kNumEncodeChars );
      data[index++] = stats.roomNum;
      
      assert( stats.deaths >= 0 );
      data[index++] = Math.min( stats.deaths, kNumEncodeChars-1 );
      
      int t = Math.round(stats.time/(float)Env.ticksPerSecond());
      t = Math.min(t, kNumEncodeChars*kNumEncodeChars-1);
      assert( t >= 0 );
      int tHi = t/kNumEncodeChars,
          tLo = t%kNumEncodeChars;
      data[index++] = tLo;
      data[index++] = tHi;
    }
    assert( index == data.length );
    
    int offset = Env.randomInt(kNumEncodeChars);
    for ( index = 0 ; index < data.length ; index++ ) {
      data[index] = (data[index]+offset) % kNumEncodeChars;
      offset += 17;
    }
    
    StringBuilder str = new StringBuilder(data.length);
    for ( index = 0 ; index < data.length ; index++ ) {
      str.append( intToChar(data[index]) );
    }
      
    return str.toString();
    
  } // encode()

  // summarize the quest based on the stats string
  static public void decode(String str) {
    
    int data[] = new int[str.length()];
    for ( int index = 0 ; index < data.length ; index++ ) {
      data[index] = charToInt( str.charAt(index) );
    }
    
    int offset = data[0];
    for ( int index = 0 ; index < data.length ; index++ ) {
      data[index] = Env.fold(data[index]-offset, kNumEncodeChars);
      offset += 17;
    }

    int index = 0;
    int blank = data[index++];
    assert( blank == 0 ); // blank
    
    int version = data[index++];
    assert( version == 0 ); // version
    
    int questNum = data[index++];
    assert( questNum >= 0 && questNum < QuestStory.NUM_QUESTS );
    char questTag = (char)('A'+questNum);
    Env.debug("Quest " + questTag + " stats");
    
    int numRooms = (data.length - 3)/4;
    assert( 4*numRooms + 3 == data.length );
    
    int totalDeaths = 0,
        totalTime   = 0;
    for ( int k = 0 ; k < numRooms ; k++ ) {
      int roomNum = data[index++] + 1;
      String roomStr = questTag + (roomNum < 10 ? "0" : "") + roomNum;
      int deaths = data[index++];
      int tLo = data[index++];
      int tHi = data[index++];
      int t = tHi*kNumEncodeChars + tLo;
      Env.debug("Room " + roomStr + ", " + deaths + " dead, " + t + " sec");
      totalDeaths += deaths;
      totalTime += t;
    }
    
    Env.debug("Total time: " + totalTime + " seconds");
    Env.debug("Total deaths: " + totalDeaths);
    Env.debug("");
    
  } // decode()

  // encode an integer
  static private char intToChar(int i) {
    
    assert( i >= 0 && i < kNumEncodeChars );
    if ( i < 10 )      return (char)(i+'0');
    else if ( i < 36 ) return (char)(i-10+'A');
    else               return (char)(i-36+'a');
    
  } // intToChar
  
  // decode a character
  static private int charToInt(char ch) {

    int i = -1;
    if      ( ch >= '0' && ch <= '9' ) i = (int)(ch-'0');
    else if ( ch >= 'A' && ch <= 'Z' ) i = (int)(ch-'A') + 10;
    else if ( ch >= 'a' && ch <= 'z' ) i = (int)(ch-'a') + 36;
    assert( i >= 0 && i < kNumEncodeChars );
    return i;
    
  } // intToChar
  
} // class QuestStats
