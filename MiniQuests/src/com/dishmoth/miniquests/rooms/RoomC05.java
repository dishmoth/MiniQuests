/*
 *  RoomC05.java
 *  Copyright Simon Hern 2013
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Spinner;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;

// the room "C05"
public class RoomC05 extends Room {

  // the basic blocks for the room
  private static final String kBlocks[][] = { { "0000000000",
                                                "0  0  0  0",
                                                "0  0  0  0",
                                                "0000000000",
                                                "0  0  0  0",
                                                "0  0  0  0",
                                                "0000000000",
                                                "0  0  0  0",
                                                "0  0  0  0",
                                                "0000000000" } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "VZ" }; // blue
  
  // details of exit/entry points for the room
  private static final Exit kExits[] 
          = { new Exit(Env.DOWN, 4,0, "VZ",0, -1, RoomC04.class, 2),
              new Exit(Env.UP,   5,0, "#Z",0, -1, RoomC06.class, 0) }; 
  
  // length of time spinners delay for
  // (fiddly: some track edges are one pixel longer than others) 
  private static final int kWait0 = 27,
                           kWait1 = kWait0 + 1;  

  // paths taken by spinning baddies
  private static final int kSpinnerTracks[][][]                           //x,y
        = { { {3,0,kWait1}, {0,0,kWait0}, {0,3,kWait1}, {3,3,kWait0} },   //0,0
            { {6,3,kWait0}, {6,0,kWait1}, {9,0,kWait0}, {9,3,kWait1} },   //2,0
            { {3,3,kWait1}, {6,3,kWait0}, {6,6,kWait1}, {3,6,kWait0} },   //1,1
            { {6,6,kWait1}, {3,6,kWait0}, {3,3,kWait1}, {6,3,kWait0} },   //1,1
            { {3,6,kWait0}, {3,9,kWait1}, {0,9,kWait0}, {0,6,kWait1} },   //0,2
            { {6,9,kWait1}, {9,9,kWait0}, {9,6,kWait1}, {6,6,kWait0} } }; //2,2
  
  // references to the spinners
  private Spinner mSpinners[];
  
  // constructor
  public RoomC05() {

  } // constructor

  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < kExits.length );
    setPlayerAtExit(kExits[entryPoint]);
    return mPlayer;
    
  } // createPlayer()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mSpinners = null;
    
  } // Room.discardResources()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {

    addBasicWalls(kExits, spriteManager);

    spriteManager.addSprite(new BlockArray(kBlocks, kBlockColours, 0,0,0));

    mSpinners = new Spinner[ kSpinnerTracks.length ];
    for ( int k = 0 ; k < mSpinners.length ; k++ ) {
      int track[][] = kSpinnerTracks[k];
      int last = track.length-1;
      int x = track[last][0],
          y = track[last][1];
      int dx = x - track[last-1][0],
          dy = y - track[last-1][1];
      boolean pixRight = ( dy > 0 || dx < 0 );
      Spinner sp = new Spinner(x, y, 0, pixRight);
      mSpinners[k] = sp;
      sp.setTargets(track, true);
      if ( k > 0 ) sp.setSilent(true);
      spriteManager.addSprite(sp);
    }
    
  } // Room.createSprites()
  
  // update the room (events may be added or processed)
  @Override
  public void advance(LinkedList<StoryEvent> storyEvents,
                      SpriteManager          spriteManager) {

    final int exitIndex = checkExits(kExits);
    if ( exitIndex != -1 ) {
      storyEvents.add(new EventRoomChange(kExits[exitIndex].mDestination,
                                          kExits[exitIndex].mEntryPoint));
      return;
    }

  } // Room.advance()

} // class RoomC05
