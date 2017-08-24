/*
 *  RoomB03.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
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
import com.dishmoth.miniquests.game.WallSwitch;

// the room "B03"
public class RoomB03 extends Room {

  // unique identifier for this room
  public static final String NAME = "B03";
  
  // main blocks for the room
  private static final String kBlocks0[][] = { { "1111111111",
                                                 "1111111111",
                                                 "1111111111",
                                                 "1111111111",
                                                 "1111111111",
                                                 "1111111111",
                                                 "1111111111",
                                                 "1111111111",
                                                 "1111111111",
                                                 "1111111111" } };
  
  private static final String kBlocks1[][] = { { "     000  ",
                                                 "00000000  ",
                                                 "0    000  ",
                                                 "0         ",
                                                 "0         ",
                                                 "0         ",
                                                 "0         ",
                                                 "0         ",
                                                 "0         ",
                                                 "00000000  " } };
  
  private static final String kBlocks2[][] = { { "       00 " },
                                               { "        00" } };
  
  private static final String kBlocks3[][] = 
    { { " "," "," "," "," "," "," "," ","0","0" },
      { " "," "," "," "," "," "," ","0","0"," " },
      { " "," "," "," "," "," ","0","0"," "," " },
      { " "," "," "," "," ","0","0"," "," "," " },
      { " "," "," "," ","0","0"," "," "," "," " } };

  private static final String kBlocks4[][] = { { "        00" },
                                               { "       00 " },
                                               { "      00  " },
                                               { "     00   " },
                                               { "    00    " },
                                               { "   00     " },
                                               { "  00      " },
                                               { " 00       " },
                                               { "00        " } }; 
  
  private static final String kBlocks5[][] = 
    { { " "," "," ","0","0"," "," "," "," "," " },
      { " "," ","0","0"," "," "," "," "," "," " },
      { " ","0","0"," "," "," "," "," "," "," " },
      { "0","0"," "," "," "," "," "," "," "," " } };

  private static final String kBlocks6[][] = { { "    22222 ",
                                                 "      2   ",
                                                 "   2  2   ",
                                                 "   2222   ",
                                                 "   2  2   ",
                                                 "   2  2222",
                                                 " 222  2  2",
                                                 "   2  2  2",
                                                 "   2  2  2",
                                                 "          " },
                                                 
                                               { "222222222 ",
                                                 "   2  2   ",
                                                 "   2  2   ",
                                                 "   2222   ",
                                                 "2  2  2   ",
                                                 "2  2  2222",
                                                 "2222  2  2",
                                                 "2  2  2  2",
                                                 "2  2  2  2",
                                                 "2222222222" } };
                                                 
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#k",   // orange 
                                                  "Em",   // grey
                                                  "tw" }; // green
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.LEFT,  3,12, "#k",0, 0, RoomB02.NAME, 1),
              new Exit(Env.UP,    8,54, "#w",0, 3, RoomB04.NAME, 0),
              new Exit(Env.RIGHT, 2,54, "#w",0, 3, RoomB07.NAME, 0),
              new Exit(Env.LEFT,  5,54, "#w",1, 3, RoomB06.NAME, 1) };

  // not real exits, but used as restart points
  private static final Exit kFakeExits[] 
          = { new Exit(Env.LEFT,  9,54, "xx",0, 3, RoomB03.NAME, 0) };
  
  // details of different camera height levels
  private static final CameraLevel kCameraLevels[]
                                     = { new CameraLevel(10,-100, 16),
                                         new CameraLevel(22,  16, 26),
                                         new CameraLevel(38,  26, 52),
                                         new CameraLevel(54,  50,+100) };
  
  // state of triffid (0 => hidden, 1 => active, 2 => dead)
  private int mPopupTriffidState;

  // reference to triffid blocking the route
  private Triffid mPopupTriffid;

  // references to the barrier triffids
  private Triffid mBarrierTriffids[];

  // directions of the barrier triffids (0 or 1)
  private int mBarrierTriffidState;
  
  // keep a private record of the most recent entry point
  private int mLastEntryPoint;
  
  // constructor
  public RoomB03() {

    super(NAME);

    mPopupTriffidState = 0;
    mBarrierTriffidState = 0;
    
  } // constructor

  // serialize the room state by writing bits to the specified buffer
  @Override
  public void save(BitBuffer buffer) {
    
    buffer.write(mPopupTriffidState, 2);
    buffer.write(mBarrierTriffidState, 1);
    
  } // Room.save()

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  @Override
  public boolean restore(int version, BitBuffer buffer) { 
    
    mPopupTriffidState = buffer.read(2);
    if ( mPopupTriffidState < 0 || mPopupTriffidState > 2 ) return false;
    mBarrierTriffidState = buffer.read(1);
    if ( mBarrierTriffidState < 0 || mBarrierTriffidState > 1 ) return false;
    return true; 
    
  } // Room.restore() 
  
  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    mLastEntryPoint = entryPoint;
    
    assert( entryPoint >= 0 && entryPoint < kExits.length+kFakeExits.length );
    if ( entryPoint < kExits.length ) {
      setPlayerAtExit(kExits[entryPoint], kCameraLevels);
    } else {
      int restartPoint = entryPoint - kExits.length;
      setPlayerAtExit(kFakeExits[restartPoint], kCameraLevels);
      int direc = mPlayer.getDirec();
      mPlayer.shiftPos(Env.STEP_X[direc], Env.STEP_Y[direc], 0);
    }
    return mPlayer;
    
  } // createPlayer()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {
    
    addBasicWalls(kExits, spriteManager);

    spriteManager.addSprite( new BlockArray(kBlocks0, kBlockColours, 0,0,-1) );
    spriteManager.addSprite( new BlockArray(kBlocks1, kBlockColours, 0,0,12) );
    spriteManager.addSprite( new BlockArray(kBlocks2, kBlockColours, 0,0,14) );
    spriteManager.addSprite( new BlockArray(kBlocks3, kBlockColours, 9,0,18) );
    spriteManager.addSprite( new BlockArray(kBlocks4, kBlockColours, 0,5,28) );
    spriteManager.addSprite( new BlockArray(kBlocks5, kBlockColours, 0,0,46) );
    spriteManager.addSprite( new BlockArray(kBlocks6, kBlockColours, 0,0,52) );

    Triffid tr1 = new Triffid(6, 8, 12, Env.DOWN);
    tr1.setFullyGrown();
    spriteManager.addSprite( tr1 );

    if ( mPopupTriffidState == 1 ) {
      Triffid tr2 = makePopupTriffid();
      tr2.setFullyGrown();
      spriteManager.addSprite( tr2 );
    }

    mBarrierTriffids = new Triffid[3];
    mBarrierTriffids[0] = new Triffid(5, 6, 54, Env.UP);
    mBarrierTriffids[1] = new Triffid(7, 4, 54, Env.LEFT);
    mBarrierTriffids[2] = new Triffid(1, 3, 54, Env.RIGHT);
    for ( Triffid tr : mBarrierTriffids ) {
      tr.setFullyGrown();
      spriteManager.addSprite( tr );
    }
    setBarrierTriffids();

    final String switchColours[] = { "Ju", "bu" };
    WallSwitch ws = new WallSwitch(Env.RIGHT, 5, 56, switchColours, true);
    ws.setState(mBarrierTriffidState);
    spriteManager.addSprite(ws);
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mPopupTriffid = null;
    mBarrierTriffids = null;
    
  } // Room.discardResources()
  
  // create a triffid in an awkward place
  private Triffid makePopupTriffid() {

    assert( mPopupTriffid == null );
    mPopupTriffid = new Triffid(6, 0, 12, Env.LEFT);
    return mPopupTriffid;
    
  } // makePopupTriffid()

  // point the barrier triffids in the correct directions
  private void setBarrierTriffids() {
    
    assert( mBarrierTriffids != null && mBarrierTriffids.length == 3 );
    
    switch ( mBarrierTriffidState ) {
    
      case 0: {
        mBarrierTriffids[0].setDirec(Env.UP);
        mBarrierTriffids[1].setDirec(Env.LEFT);
        mBarrierTriffids[2].setDirec(Env.RIGHT);
      } break;
      
      case 1: {
        mBarrierTriffids[0].setDirec(Env.LEFT);
        mBarrierTriffids[1].setDirec(Env.DOWN);
        mBarrierTriffids[2].setDirec(Env.DOWN);
      } break;
      
      default: assert(false);
    }
    
  } // setBarrierTriffids()
  
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

    // special restart point
    if ( mLastEntryPoint == 0 && mPlayer != null && mPlayer.getZPos() == 54 ) {
      int e = kExits.length;
      storyEvents.add(new EventNewEntryPoint(e));
      mLastEntryPoint = e;
    }
    
    // check camera level
    EventRoomScroll scroll = checkVerticalScroll(kCameraLevels);
    if ( scroll != null ) storyEvents.add(scroll);

    // process the story event list
    boolean saveGameEvent = false;
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();

      // pop-up triffid is shot
      if ( event instanceof Triffid.EventKilled ) {
        if ( mPopupTriffid == ((Triffid.EventKilled)event).mSource ) {
          assert( mPopupTriffidState == 1 );
          mPopupTriffidState = 2;
          mPopupTriffid = null;
          saveGameEvent = true;
        }
        it.remove();
      }

      // barrier switch is shot
      if ( event instanceof WallSwitch.EventStateChange ) {
        WallSwitch.EventStateChange e = (WallSwitch.EventStateChange)event;
        mBarrierTriffidState = e.mNewState;
        setBarrierTriffids();
        it.remove();
      }

    }
    if ( saveGameEvent ) storyEvents.add(new QuestStory.EventSaveGame());
    
    // check pop-up triffid
    if ( mPopupTriffidState == 0 && mPlayer != null &&
         mPlayer.getXPos() == 0 && mPlayer.getYPos() == 2 ) {
      mPopupTriffidState = 1;
      spriteManager.addSprite( makePopupTriffid() );
      storyEvents.add(new QuestStory.EventSaveGame());
      Env.sounds().play(Sounds.TRIFFID_EMERGE);
    }

  } // Room.advance()

} // class RoomB03
