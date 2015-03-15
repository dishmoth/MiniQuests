/*
 *  RoomC13.java
 *  Copyright Simon Hern 2013
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BitBuffer;
import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.GlowPath;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.TinyStory;
import com.dishmoth.miniquests.game.WallSwitch;

// the room "C13"
public class RoomC13 extends Room {
  
  // unique identifier for this room
  public static final String NAME = "C13";
  
  // the basic blocks for the room
  private static final String kBlocks[][] = { { "00200     ",
                                                "00200     ",
                                                " 020      ",
                                                "  2       ",
                                                "  2       ",
                                                "  2     00",
                                                "       000",
                                                "    222222",
                                                "       000",
                                                "        00" },
  
                                              { "00200     ",
                                                "00200     ",
                                                " 020      ",
                                                "  2       ",
                                                "  2       ",
                                                "  2     00",
                                                "       000",
                                                "    222222",
                                                "       000",
                                                "        00" },
  
                                              { "00200     ",
                                                "00200     ",
                                                " 020      ",
                                                "  2       ",
                                                "  2       ",
                                                "  2     00",
                                                "       000",
                                                "    222222",
                                                "       000",
                                                "        00" },
  
                                              { "00200     ",
                                                "00200     ",
                                                " 020      ",
                                                "  2       ",
                                                "  2       ",
                                                "  2       ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
  
                                              { "00200     ",
                                                "00200     ",
                                                " 020      ",
                                                "  2       ",
                                                "  2       ",
                                                "  2       ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
  
                                              { "00200     ",
                                                "00200     ",
                                                " 020      ",
                                                "  2       ",
                                                "  2       ",
                                                "  2       ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
  
                                              { "00200     ",
                                                "00200     ",
                                                " 020      ",
                                                "  2       ",
                                                "  2       ",
                                                "  2       ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " } };
  
  // blocks for the lift
  private static final String kLiftLayers[][] = { { "121", "122", "111" },
                                                  { "131", "132", "111" },
                                                  { "131", "133", "111" } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "tk",   // basic
                                                  "tk",   // lift
                                                  "#h",   // path
                                                  "xh" }; // lit path
  
  // details of exit/entry points for the room
  private static final Exit kExits[] 
          = { new Exit(Env.UP,   2,8, "#h",1, -1, RoomC12.NAME, 1),
              new Exit(Env.RIGHT,2,0, "#h",1, -1, RoomC04.NAME, 1) }; 
  
  // parameters controlling for lift
  private static final int kLiftZMin  = -10,
                           kLiftZMax  = -2;
  private static final int kLiftDelay = 3;
  
  // colour of the glowing path
  private static final char kPathColour = 'x';
  
  // glowing paths
  private static final String kGlowPath1[] = { "X","+","+","+","+",
                                               "+","+","+","+" },
                              kGlowPath2[] = { "X++++++++" };
  
  // whether the paths have been walked yet (0, 1 or 2)
  private int mPathDone;
  
  // reference to the lift blocks
  private BlockArray mLift;

  // where the lift is (+1 top, -1 bottom, 0 undecided)
  private int mLiftState;
  
  // which direction the lift is moving in (+1 up, -1 down, 0 stopped)
  private int mLiftDirec;
  
  // how long until the lift moves again
  private int mLiftTimer;

  // references to the switches
  private WallSwitch mSwitchLow,
                     mSwitchHigh;
  
  // the glowing paths
  private GlowPath mPath1,
                   mPath2;

  // constructor
  public RoomC13() {

    super(NAME);

    mPathDone = 0;
    mLiftState = 0;
  
  } // constructor

  // serialize the room state by writing bits to the specified buffer
  @Override
  public void save(BitBuffer buffer) {
    
    buffer.write(mPathDone, 2);
    buffer.write(mLiftState+1, 2);
    
  } // Room.save()

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  @Override
  public boolean restore(int version, BitBuffer buffer) { 
    
    if ( buffer.numBitsToRead() < 4 ) return false;
    mPathDone = buffer.read(2);
    mLiftState = buffer.read(2)-1;
    return true; 
    
  } // Room.restore() 
  
  // whether the double path is complete
  // (note: this function may be called by RoomC04)
  public boolean pathComplete() { return (mPathDone==2); }
  
  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < kExits.length );
    setPlayerAtExit(kExits[entryPoint]);
    
    if ( mLiftState == 0 ) {
      mLiftState = (entryPoint == 0) ? -1 : +1;
      int liftZPos = ( mLiftState>0 ? kLiftZMax : kLiftZMin );
      mLift.setPos( mLift.getXPos(), mLift.getYPos(), liftZPos );
      int switchState = (mLiftState > 0) ? 1 : 0;
      mSwitchLow.setState(switchState);
      mSwitchHigh.setState(switchState);
    }
    
    return mPlayer;
    
  } // createPlayer()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mPath1 = mPath2 = null;
    mLift = null;
    mSwitchHigh = mSwitchLow = null;
    
  } // Room.discardResources()

  // create (or recreate) the lift blocks
  private void makeLift(SpriteManager spriteManager) {
    
    int liftZPos;
    if ( mLift == null ) {
      liftZPos = ( mLiftState>0 ? kLiftZMax : kLiftZMin );
    } else {
      liftZPos = mLift.getZPos();
      spriteManager.removeSprite(mLift);
      mLift = null;
    }
    
    String liftLayer[] = kLiftLayers[mPathDone];
    String liftBlocks[][] = { liftLayer, liftLayer, liftLayer, 
                              liftLayer, liftLayer, liftLayer };
    mLift = new BlockArray(liftBlocks, kBlockColours, 1,1,liftZPos);
    spriteManager.addSprite(mLift);

  } // makeLift()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {

    addBasicWalls(kExits, spriteManager);

    spriteManager.addSprite(new BlockArray(kBlocks, kBlockColours, 0,0,-4));

    makeLift(spriteManager);
    mLiftDirec = 0;
    mLiftTimer = 0;
    
    mSwitchLow = new WallSwitch(Env.RIGHT, 4, 2, 
                                new String[]{"ju","Iu"}, true);
    mSwitchHigh = new WallSwitch(Env.UP, 4, 10, 
                                 new String[]{"j7","I7"}, true);
    if ( mLiftState > 0 ) {
      mSwitchLow.setState(1);
      mSwitchHigh.setState(1);
    }
    spriteManager.addSprite(mSwitchLow);
    spriteManager.addSprite(mSwitchHigh);
        
    RoomC12 roomUp = (RoomC12)findRoom(RoomC12.NAME);
    assert( roomUp != null );
    boolean pathAvailable = roomUp.pathComplete();
    if ( pathAvailable ) {
      int d1 = (mPathDone >= 1) ? 2 : 0;
      mPath1 = new GlowPath(kGlowPath1, 2, 2+d1, 8, kPathColour);
      spriteManager.addSprite(mPath1);    
      if ( mPathDone >= 1 ) {
        mPath1.setComplete();
        mPath2 = new GlowPath(kGlowPath2, 2, 2, 0, kPathColour);
        spriteManager.addSprite(mPath2);
        if ( mPathDone >= 2 ) {
          mPath2.setComplete();
        }
      }
    }
  
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

    // check switches
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();
      if ( event instanceof WallSwitch.EventStateChange ) {
        WallSwitch switchA = ((WallSwitch.EventStateChange)event).mSwitch;
        WallSwitch switchB = (switchA==mSwitchLow) ? mSwitchHigh : mSwitchLow;
        switchB.setState( switchA.getState() );
        mLiftState = (switchA.getState()==1) ? +1 : -1;
        it.remove();
      }
    }
    
    // animate the lift
    boolean stopAtTop = (mSwitchHigh.getState() == 1);
    assert( mLiftDirec >= -1 && mLiftDirec <= +1 );
    if ( ( stopAtTop && mLift.getZPos() < kLiftZMax) ||
         (!stopAtTop && mLift.getZPos() > kLiftZMin) ) {
      if ( --mLiftTimer <= 0 ) {
        mLiftTimer = kLiftDelay;
        mLift.shiftPos(0, 0, mLiftDirec);
        if ( mLift.getZPos() == kLiftZMax ) {
          if ( mLiftDirec > 0 ) Env.sounds().play(Sounds.SWITCH_OFF);
          mLiftDirec = (stopAtTop ? 0 : mLiftDirec-1);
        }
        if ( mLift.getZPos() == kLiftZMin ) {
          if ( mLiftDirec < 0 ) Env.sounds().play(Sounds.SWITCH_OFF);
          mLiftDirec = (stopAtTop ? mLiftDirec+1 : 0);
        }
        if ( mLiftDirec == 0 ) mLiftTimer *= 2;
      }
    } else {
      mLiftDirec = 0;
      mLiftTimer = 1;
    }
    
    // check the path
    if ( mPathDone == 0 && mPath1 != null && mPath1.complete() ) {
      mPathDone = 1;
      spriteManager.removeSprite(mPath1);
      mPath1 = new GlowPath(kGlowPath1, 2, 4, 8, kPathColour);
      mPath1.setComplete();
      spriteManager.addSprite(mPath1);
      Env.sounds().play(Sounds.SUCCESS);
      storyEvents.add(new TinyStory.EventSaveGame());
      assert( mPath2 == null );      
      makeLift(spriteManager);
    }
    if ( mPathDone == 1 && mPath2 != null && mPath2.complete() ) {
      mPathDone = 2;
    }
        
  } // Room.advance()

} // class RoomC13
