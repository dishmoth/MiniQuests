/*
 *  RoomC12.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BitBuffer;
import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.GlowPath;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.Spinner;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;

// the room "C12"
public class RoomC12 extends Room {

  // unique identifier for this room
  public static final String NAME = "C12";
  
  // the basic blocks for the room
  private static final String kBlocks[][] = { { "0000000000",
                                                "0     0  0",
                                                "0     0  0",
                                                "0000  0000",
                                                "0  0     0",
                                                "0  0     0",
                                                "0000  0000",
                                                "0     0  0",
                                                "0     0  0",
                                                "0000000000" },
                                                
                                              { "0000111000",
                                                "0000001  0",
                                                "0000001  0",
                                                "0001111000",
                                                "0  1000000",
                                                "0  1000000",
                                                "0001111000",
                                                "0000001  0",
                                                "0000001  0",
                                                "0000111000" } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "NY",   //
                                                  "#c" }; // 
  
  // details of exit/entry points for the room
  private static final Exit kExits[] 
          = { new Exit(Env.UP,  4,0, "#c",0, -1, RoomC07.NAME, 4),
              new Exit(Env.DOWN,4,0, "#c",0, -1, RoomC13.NAME, 0) }; 
  
  // colour of the glowing path
  private static final char kPathColour = 's';
  
  // glowing path
  private static final String kGlowPath[] = { "    X     ",
                                              "    +++   ",
                                              "      +   ",
                                              "      +   ",
                                              "   ++++   ",
                                              "   +      ",
                                              "   +      ",
                                              "   ++++   ",
                                              "      +   ",
                                              "      +   ",
                                              "    +++   ",
                                              "    +     " };
  
  // length of time spinners delay for
  // (fiddly: some track edges are one pixel longer than others) 
  private static final int kWait0 = 23,
                           kWait1 = kWait0 + 1;  

  // paths taken by spinning baddies
  private static final int kSpinnerTracks[][][]                           //x,y
        = { { {6,3,kWait0}, {6,0,kWait1}, {9,0,kWait0}, {9,3,kWait1} },   //2,0
            { {3,6,kWait1}, {3,3,kWait0}, {0,3,kWait1}, {0,6,kWait0} },   //0,1
            { {6,9,kWait0}, {6,6,kWait1}, {9,6,kWait0}, {9,9,kWait1} } }; //2,2
  
  // references to the spinners
  private Spinner mSpinners[];
  
  // whether the spinners are currently visible
  private boolean mSpinnersVisible;
  
  // whether the path has been walked yet
  private boolean mPathDone;
  
  // the glowing path
  private GlowPath mPath;

  // constructor
  public RoomC12() {

    super(NAME);

    mPathDone = false;
    
  } // constructor

  // serialize the room state by writing bits to the specified buffer
  @Override
  public void save(BitBuffer buffer) {
    
    buffer.writeBit(mPathDone);
    
  } // Room.save()

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  @Override
  public boolean restore(int version, BitBuffer buffer) { 
    
    if ( buffer.numBitsToRead() < 1 ) return false;
    mPathDone = buffer.readBit();
    return true; 
    
  } // Room.restore() 
  
  // whether the path is complete
  // (note: this function may be called by RoomC13)
  public boolean pathComplete() { return mPathDone; }
  
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
    mPath = null;
    
  } // Room.discardResources()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {

    addBasicWalls(kExits, spriteManager);

    spriteManager.addSprite(new BlockArray(kBlocks, kBlockColours, 0,0,-2));

    mPath = new GlowPath(kGlowPath, 0, -1, 0, kPathColour);
    if ( mPathDone ) {
      mPath.setComplete();
    }
    spriteManager.addSprite(mPath);
  
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
      if ( !mPathDone ) sp.setVisible(false);
      spriteManager.addSprite(sp);
    }
    
    mSpinnersVisible = false;
    
  } // Room.createSprites()
  
  // update the room (events may be added or processed)
  @Override
  public void advance(LinkedList<StoryEvent> storyEvents,
                      SpriteManager          spriteManager) {

    // check exits
    final int exitIndex = checkExits(kExits);
    if ( exitIndex != -1 ) {
      storyEvents.add(new EventRoomChange(kExits[exitIndex].mDestination,
                                          kExits[exitIndex].mEntryPoint));
      return;
    }

    // check the path
    if ( !mPathDone && mPath.complete() ) {
      mPathDone = true;
    }
    
    // check visibility
    if ( mPlayer != null && !mPathDone ) {
      boolean onPath = mPath.includes(mPlayer.getXPos(), 
                                      mPlayer.getYPos(), 
                                      mPlayer.getZPos());
      if ( mSpinnersVisible && onPath ) {
        mSpinnersVisible = false;
        for ( Spinner sp : mSpinners ) {
          sp.setVisible(false);
          sp.flash();
        }
        Env.sounds().play(Sounds.MATERIALIZE);
      } else if ( !mSpinnersVisible && !onPath ) {
        mSpinnersVisible = true;
        for ( Spinner sp : mSpinners ) {
          sp.setVisible(true);
          sp.flash();
        }
        Env.sounds().play(Sounds.MATERIALIZE);
      }
    }
    
  } // Room.advance()

} // class RoomC12
