/*
 *  MapDataMain.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

// the main world map
public class MapDataMain extends MapData {

  // story event: the player has entered a dungeon location
  public static class EventAtStones extends StoryEvent {
    public EventAtStones() { }
  } // class MapDataMain.EventAtStones
  
  // size of the basic map
  private static final int kNumX = 6,
                           kNumY = 5;
  
  // locations of things
  private static final int kHomePos    =   2*kNumX + 2; 
  private static final int kQuestPos[] = { 1*kNumX + 1, 
                                           3*kNumX + 0,
                                           0*kNumX + 5,
                                           1*kNumX + 5 };
  private static final int kStonesPos  =   3*kNumX + 3;
  
  // constructor
  public MapDataMain() {
    
    super("Map.png");
    
    rewireForest();
    recolourForest();
    
  } // constructor
  
  // rearrange the map around the forest dungeon
  private void rewireForest() {
    
    final int numOld = mExits.length;
    final int numExtra = 5;
    
    EgaImage newImages[] = new EgaImage[numOld+numExtra];
    int newExits[][] = new int[numOld+numExtra][4];
    for ( int k = 0 ; k < numOld ; k++ ) {
      newImages[k] = mImages[k];
      newExits[k] = mExits[k];
    }
    mImages = newImages;
    mExits = newExits;
    
    final int dungeonPos = 1*kNumX + 5,
              lostPos    = 2*kNumX + 5,
              exitPos    = 3*kNumX + 5,
              startPos   = numOld;
    
    mExits[dungeonPos][Env.LEFT] = mExits[dungeonPos][Env.RIGHT] 
                                 = mExits[dungeonPos][Env.UP] = lostPos;
    mExits[dungeonPos][Env.DOWN] = exitPos;
    
    mExits[lostPos][Env.LEFT] = mExits[lostPos][Env.RIGHT] 
                              = mExits[lostPos][Env.UP] = lostPos;
    mExits[lostPos][Env.DOWN] = exitPos;
    
    mExits[exitPos][Env.UP] = startPos;
    
    for ( int pos = startPos ; pos < startPos+numExtra ; pos++ ) {
      mImages[pos] = mImages[lostPos].clone();
      mExits[pos][Env.LEFT] = mExits[pos][Env.RIGHT] 
                            = mExits[pos][Env.UP] = lostPos;
      mExits[pos][Env.DOWN] = exitPos;
    }
    
    mExits[startPos+0][Env.RIGHT] = startPos+1;
    mExits[startPos+1][Env.RIGHT] = startPos+2;
    mExits[startPos+2][Env.UP]    = startPos+3;
    mExits[startPos+3][Env.LEFT]  = startPos+4;
    mExits[startPos+4][Env.RIGHT] = dungeonPos;
    
  } // rewireForest()
  
  // change some tree colours
  private void recolourForest() {
    
    final EgaImage tree = new EgaImage(0,0, 3,5, " K "
                                               + "KKW"
                                               + "KWW"
                                               + "WWW"
                                               + " 0 ");
    final int startPos = kNumX*kNumY;
    
    tree.draw(mImages[startPos], 13, 20);
    tree.draw(mImages[startPos], 17, 12);
    tree.draw(mImages[startPos], 26, 10);
    tree.draw(mImages[startPos], 36,  8);
    
    tree.draw(mImages[startPos+1],  6, 16);
    tree.draw(mImages[startPos+1], 12, 11);
    tree.draw(mImages[startPos+1], 21, 15);
    tree.draw(mImages[startPos+1], 30, 12);
    
    tree.draw(mImages[startPos+2],  0, 18);
    tree.draw(mImages[startPos+2], 14, 15);
    tree.draw(mImages[startPos+2], 20,  9);
    tree.draw(mImages[startPos+2], 15,  1);
    
    tree.draw(mImages[startPos+3], 24, 22);
    tree.draw(mImages[startPos+3], 21, 15);
    tree.draw(mImages[startPos+3], 12, 11);
    tree.draw(mImages[startPos+3],  1,  7);
    
    tree.draw(mImages[startPos+4], 35, 17);
    tree.draw(mImages[startPos+4], 28, 16);
    tree.draw(mImages[startPos+4], 26, 10);
    tree.draw(mImages[startPos+4], 36,  8);
    
  } // recolourForest()
  
  // a starting location for the map (home -1, or a quest number)
  public int startPos(int type) {
    
    assert( type >= -1 && type < kQuestPos.length );
    
    if ( type == -1 ) return kHomePos;
    else              return kQuestPos[type];
    
  } // MapData.startPos()
  
  // whether there's anything special at a location
  public StoryEvent eventLocation(int pos) { 
    
    for ( int num = 0 ; num < kQuestPos.length ; num++ ) {
      if ( pos == kQuestPos[num] ) {
        return new EventAtDungeon(num);
      }
    }
    
    if ( pos == kStonesPos ) {
      return new EventAtStones();
    }
    
    return null;
    
  } // MapData.eventLocation()
  
} // class MapDataMain
