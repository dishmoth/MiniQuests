/*
 *  RoomD12.java
 *  Copyright Simon Hern 2014
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Spikes;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;

// the room "D12"
public class RoomD12 extends Room {

  // unique identifier for this room
  public static final String NAME = "D12";
  
  // main blocks for the room
  private static final String kBlocks[][] = { { "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "0000000000" },
                                                
                                              { "11      11",
                                                "11      11",
                                                "11      11",
                                                "11      11",
                                                "11      11",
                                                "11      11",
                                                "11      11",
                                                "11      11",
                                                "1111111111",
                                                "1111111111" },

                                              { "11      11",
                                                "11      11",
                                                "11      11",
                                                "11      11",
                                                "11      11",
                                                "11      11",
                                                "11      11",
                                                "11      11",
                                                "1111111111",
                                                "1111111111" },

                                              { "11      11",
                                                "11      11",
                                                "11      11",
                                                "11      11",
                                                "11      11",
                                                "11      11",
                                                "11      11",
                                                "11      11",
                                                "1111111111",
                                                "1111111111" } };

  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "uO",
                                                  "#l" }; 
  
  // details of exit/entry points for the room 
  private static final Exit kExits[][] 
          = { { new Exit(Env.LEFT,  8,0, "#l",0, -1, RoomD11.NAME, 1),
                new Exit(Env.RIGHT, 8,0, "#l",0, -1, "",0) },
  
              { new Exit(Env.LEFT,  8,0, "#l",0, -1, RoomD11.NAME, 1),
                new Exit(Env.RIGHT, 8,0, "#l",0, -1, RoomD04.NAME, 2) },
              
              { new Exit(Env.LEFT,  8,0, "#l",0, -1, RoomD11.NAME, 1),
                new Exit(Env.RIGHT, 8,0, "#l",0, -1, "",0) },
              
              { new Exit(Env.LEFT,  8,0, "#l",0, -1, RoomD11.NAME, 1),
                new Exit(Env.RIGHT, 8,0, "#l",0, -1, "",0) } };
  
  // times for things to happen
  private static final int kSpikeDelay = 5,
                           kOffDelay   = 20;
  
  // the current exits, based on room D02's twist
  private Exit mExits[];
  
  // arrangement of spikes
  private Spikes mSpikes[][];

  // spike countdown
  private int mTimer;

  // which direction the spikes are going
  private int mPhase;
  
  // constructor
  public RoomD12() {

    super(NAME);

  } // constructor

  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < mExits.length );
    setPlayerAtExit(mExits[entryPoint]);
    return mPlayer;
    
  } // createPlayer()
  
  // configure exits based on the room D02's twist
  private void prepareExits() {
    
    RoomD02 twistRoom = (RoomD02)findRoom(RoomD02.NAME);
    assert( twistRoom != null );
    mExits = kExits[ twistRoom.twist() ];    
    
  } // prepareExist()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {
    
    prepareExits();
    
    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,-6) );
    
    addBasicWalls(mExits, spriteManager);

    mSpikes = new Spikes[6][3];
    for ( int k = 0 ; k < mSpikes.length ; k++ ) {
      mSpikes[k][0] = new Spikes(8,2+k,0, 2,1, false, "S0");
      mSpikes[k][1] = new Spikes(2+k,0,0, 1,2, false, "S0");
      mSpikes[k][2] = new Spikes(0,7-k,0, 2,1, false, "S0");
      for ( int n = 0 ; n < mSpikes[k].length ; n++ ) {
        if ( n > 0 ) mSpikes[k][n].setSilent(true);
        spriteManager.addSprite(mSpikes[k][n]);
      }
    }

    mTimer = 0;
    mPhase = 0;

  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mSpikes = null;
    
  } // Room.discardResources()
  
  // update the room (events may be added or processed)
  @Override
  public void advance(LinkedList<StoryEvent> storyEvents,
                      SpriteManager          spriteManager) {

    final int exitIndex = checkExits(mExits);
    if ( exitIndex != -1 ) {
      storyEvents.add(new EventRoomChange(mExits[exitIndex].mDestination,
                                          mExits[exitIndex].mEntryPoint));
      return;
    }

    if ( --mTimer < 0 ) {
      mTimer = kOffDelay + kSpikeDelay*mSpikes.length;
      mPhase = (mPhase+1)%4;
    }
    
    if ( mTimer % kSpikeDelay == 0 ) {
      int num = mTimer/kSpikeDelay;
      if ( num < mSpikes.length ) {
        for ( int k = 0 ; k < mSpikes[num].length ; k++ ) {
          boolean rev = (k==1) ? (mPhase==0||mPhase==3) : (mPhase<2);
          int n = ( rev ? num : (mSpikes.length - 1 - num) );
          mSpikes[n][k].trigger();
        }
      }
    }

  } // Room.advance()

} // class RoomD12
