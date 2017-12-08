/*
 *  RoomB02.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BitBuffer;
import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.QuestStory;
import com.dishmoth.miniquests.game.Triffid;

// the room "B02"
public class RoomB02 extends Room {

  // unique identifier for this room
  public static final String NAME = "B02";
  
  // blocks for the room (except for the main floor)
  private static final String kBlocks[][] = { { "0         ",
                                                "0         ",
                                                "000       ",
                                                "000       ",
                                                "000       ",
                                                "000       ",
                                                "000       ",
                                                "000       ",
                                                "000       ",
                                                "0000000000" },
                                              
                                              { "0         ",
                                                "0         ",
                                                "000       ",
                                                "  0       ",
                                                "  0       ",
                                                "  0       ",
                                                "  000     ",
                                                "  000     ",
                                                "  000     ",
                                                "  00000000" },
                                              
                                              { "0         ",
                                                "0         ",
                                                "000       ",
                                                "  0       ",
                                                "  0       ",
                                                "  0       ",
                                                "  000     ",
                                                "   00     ",
                                                "   00     ",
                                                "   0000000" },
                                              
                                              { "0000000000",
                                                "0000000000",
                                                "0000000000",
                                                "  00000000",
                                                "  00000000",
                                                "  00000000",
                                                "  00000000",
                                                "    000000",
                                                "    000000",
                                                "    000000" },
                                              
                                              
                                              { " 000000000",
                                                "         0",
                                                "         0",
                                                "         0",
                                                "         0",
                                                "         0",
                                                "         0",
                                                "          ",
                                                "          ",
                                                "          " },
                                              
                                              { "  00000000",
                                                "         0",
                                                "         0",
                                                "         0",
                                                "         0",
                                                "         0",
                                                "         0",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { "   0000000",
                                                "         0",
                                                "         0",
                                                "         0",
                                                "         0",
                                                "         0",
                                                "         0",
                                                "          ",
                                                "          ",
                                                "          " } };
  
  // blocks for the main floor
  private static final String kFloorBlocks[] = { "0000000000",
                                                 "0000000000",
                                                 "0000000000",
                                                 "  00000000",
                                                 "  00000000",
                                                 "  00000000",
                                                 "  00000000",
                                                 "    000000",
                                                 "    000000",
                                                 "    000000" };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "ES",
                                                  "4S" }; // or ClY
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.LEFT,  5, 0, "ES",0, -1, RoomB01.NAME, 2),
              new Exit(Env.RIGHT, 4,12, "ES",3, -1, RoomB03.NAME, 0),
              new Exit(Env.DOWN,  5, 6, "ES",0, -1, RoomB01.NAME, 1) };

  // positions the triffids appear at (x, y, z)
  private static final int kTriffidPos[][] = { { 6, 4, 6 },
                                               { 3, 7, 6 },
                                               { 9, 1, 6 } };

  // speed at which triffids rotate
  private static final int kTriffidRotateDelay = +20;

  // time delay next triffid appears
  private static final int kTimeNewTriffid = 45,
                           kTimeOpenDoor   = 20;
  
  // time for the floor lines to appear
  private static final int kTimeFloorLines  = 17,
                           kStepFloorLines  = 2,
                           kRangeFloorLines = 9;
  
  // references to the existing triffids
  private Triffid mTriffids[];

  // how many triffids have been shot so far
  private int mNumTriffidsDone;

  // a slight delay before the next triffid appears
  private int mNewTriffidTimer;
  
  // reference to the main floor blocks
  private BlockArray mFloorBlocks;
  
  // countdown as new floor lines appear
  private int mFloorLineTimer;
  
  // constructor
  public RoomB02() {

    super(NAME);

    mNumTriffidsDone = 0;
    
  } // constructor

  // serialize the room state by writing bits to the specified buffer
  @Override
  public void save(BitBuffer buffer) {
    
    buffer.write(mNumTriffidsDone, 2);
    
  } // Room.save()

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  @Override
  public boolean restore(int version, BitBuffer buffer) { 
    
    mNumTriffidsDone = buffer.read(2);
    if ( mNumTriffidsDone < 0 || mNumTriffidsDone > 3 ) return false;
    return true; 
    
  } // Room.restore() 
  
  // whether the room has been completed yet
  // (note: this function may be called by RoomB01)
  public boolean doorsUnlocked() { return (mNumTriffidsDone == 3); }
  
  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < kExits.length );
    setPlayerAtExit(kExits[entryPoint]);
    
    mCamera.shift(0, 0, +3);
    
    return mPlayer;
    
  } // createPlayer()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {

    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,0) );

    addBasicWalls(kExits, spriteManager);
    
    updateFloorBlocks(spriteManager);

    mTriffids = new Triffid[kTriffidPos.length];
    for ( int k = 0 ; k < mTriffids.length ; k++ ) {
      mTriffids[k] = new Triffid(kTriffidPos[k][0], 
                                 kTriffidPos[k][1], 
                                 kTriffidPos[k][2], (k%4));
      spriteManager.addSprite(mTriffids[k]);
      mTriffids[k].setRotateRate(kTriffidRotateDelay);
      mTriffids[k].setFullyGrown();
      if ( k == mNumTriffidsDone ) {
        mTriffids[k].setSleepMode();
        break;
      }
    }

    if ( mNumTriffidsDone < mTriffids.length ) {
      kExits[1].mDoor.setClosed(true);
      kExits[2].mDoor.setClosed(true);
    }
    
    mNewTriffidTimer = 0;
    mFloorLineTimer = 0;
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mTriffids = null;
    mFloorBlocks = null;
    
  } // Room.discardResources()

  // create or change the main floor blocks
  private void updateFloorBlocks(SpriteManager spriteManager) {
    
    if ( mFloorBlocks != null ) spriteManager.removeSprite(mFloorBlocks);
    
    int rangeNewLines = Math.max(0, kRangeFloorLines 
                                    - (mFloorLineTimer/kStepFloorLines));
    
    String rows[] = new String[ kFloorBlocks.length ];
    for ( int iy = 0 ; iy < rows.length ; iy++ ) {
      StringBuilder row = new StringBuilder( kFloorBlocks[iy] );
      for ( int ix = 0 ; ix < Room.kSize ; ix++ ) {
        char ch = row.charAt(ix);
        if ( ch == '0' ) {
          for ( int tr = 0 ; tr < mNumTriffidsDone ; tr++ ) {
            int range = (tr == (mNumTriffidsDone-1)) 
                        ? rangeNewLines : kRangeFloorLines;
            int dx = ix - kTriffidPos[tr][0],
                dy = (Room.kSize-1-iy) - kTriffidPos[tr][1];
            if ( ( dx == 0 && Math.abs(dy) <= range ) ||
                 ( dy == 0 && Math.abs(dx) <= range ) ){
              row.setCharAt(ix, '1');
              break;
            }
          }
        }
      }
      rows[iy] = row.toString();
    }

    mFloorBlocks = new BlockArray(new String[][]{rows}, kBlockColours, 0,0,6);
    spriteManager.addSprite(mFloorBlocks);
    
  } // updateFloorBlocks()
  
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

    // add a new triffid
    if ( mNumTriffidsDone < mTriffids.length &&
         mTriffids[mNumTriffidsDone] == null ) {
      int x = kTriffidPos[mNumTriffidsDone][0],
          y = kTriffidPos[mNumTriffidsDone][1],
          z = kTriffidPos[mNumTriffidsDone][2];
      if ( mNewTriffidTimer > 0 ) {
        mNewTriffidTimer -= 1;
      } else if ( mPlayer == null || !mPlayer.hits(x, y, z) ) {
        Triffid tr = new Triffid(x, y, z, 0);
        tr.setSleepMode();
        tr.setRotateRate(kTriffidRotateDelay);
        spriteManager.addSprite(tr);
        mTriffids[mNumTriffidsDone] = tr;
        Env.sounds().play(Sounds.TRIFFID_EMERGE);
      }
    }

    // unlock the door
    if ( mNumTriffidsDone == mTriffids.length ) {
      if ( mNewTriffidTimer > 0 ) {
        if ( --mNewTriffidTimer == 0 ) {
          kExits[1].mDoor.setClosed(false);
          kExits[2].mDoor.setClosed(false);
          Env.sounds().play(Sounds.SUCCESS);
          storyEvents.add(new QuestStory.EventSaveGame());
        }
      }
    }
    
    // check whether a triffid has been activated
    if ( mNumTriffidsDone < mTriffids.length &&
         mTriffids[mNumTriffidsDone] != null &&
         !mTriffids[mNumTriffidsDone].isAsleep() ) {
      mNumTriffidsDone += 1;
      mNewTriffidTimer = ( (mNumTriffidsDone < 3)
                         ? kTimeNewTriffid : kTimeOpenDoor );
      mFloorLineTimer = kTimeFloorLines;
    }

    // draw lines on the floor
    if ( mFloorLineTimer > 0 ) {
      mFloorLineTimer--;
      if ( (mFloorLineTimer % kStepFloorLines) == 0 ) {
        updateFloorBlocks(spriteManager);
      }
    }
    
  } // Room.advance()

} // class RoomB02
