/*
 *  RoomD08.java
 *  Copyright Simon Hern 2014
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BitBuffer;
import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.EgaImage;
import com.dishmoth.miniquests.game.EgaTools;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.Spikes;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.WallSwitch;

// the room "D08"
public class RoomD08 extends Room {

  // unique identifier for this room
  public static final String NAME = "D08";
  
  // main blocks for the room
  private static final String kBlocks[][] = { { "4444444444",
                                                "4444444444",
                                                "4444444444",
                                                "4444444444",
                                                "4444444444",
                                                "4444444444",
                                                "4444444444",
                                                "4444444444",
                                                "4444444444",
                                                "4444444444" },
  
                                              { "          ",
                                                " 44444444 ",
                                                " 44444444 ",
                                                " 44444444 ",
                                                " 44444444 ",
                                                " 44444444 ",
                                                " 44444444 ",
                                                " 44444444 ",
                                                " 44444444 ",
                                                "          " },
                                                
                                              { "          ",
                                                " 33001111 ",
                                                " 33001111 ",
                                                " 22003300 ",
                                                " 22003300 ",
                                                " 112222333",
                                                " 11222233 ",
                                                " 00330011 ",
                                                " 00330011 ",
                                                "  0       " } };

  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "Z#", // blue
                                                  "c#", // yellow
                                                  "L#", // purple
                                                  "4#", // red
                                                  "u0" }; 

  // fill-in colours between grid squares
  private static final char kGridColours[] = { 'V', ':', 'l', 'd' };
  
  // order of the grid colours
  private static final int kOrder[] = { 0, 1, 2, 0, 3, 0, 1, 3 };
  
  // details of exit/entry points for the room 
  private static final Exit kExits[]
        = { new Exit(Env.RIGHT, 4,4, kBlockColours[3],1, 0, RoomD07.NAME, 1),
            new Exit(Env.DOWN,  2,4, kBlockColours[0],1, 0, RoomD09.NAME, 0) };
  
  // details of different camera height levels
  private static final CameraLevel kCameraLevels[]
                                     = { new CameraLevel(3, -100, +100) };

  // how long until the switches reset
  private static final int kResetDelay = 3;
  
  // whether the room has been completed
  private boolean mRoomDone;
  
  // references to the switches
  private WallSwitch mSwitches[];

  // index of currently lit switch (-1 if inactive)
  private int mSwitchIndex;

  // countdown as switches reset
  private int mResetTimer;
  
  // colour of the grid square the player is currently on
  private int mCurrentColour;

  // references to grid of spikes
  private Spikes mSpikes[][];
  
  // constructor
  public RoomD08() {

    super(NAME);

    mRoomDone = false;
    
  } // constructor

  // serialize the room state by writing bits to the specified buffer
  @Override
  public void save(BitBuffer buffer) {
    
    buffer.writeBit(mRoomDone);
    
  } // Room.save()

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  @Override
  public boolean restore(int version, BitBuffer buffer) { 
    
    if ( buffer.numBitsToRead() < 1 ) return false;
    mRoomDone = buffer.readBit();
    return true;
    
  } // Room.restore() 
  
  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < kExits.length );
    setPlayerAtExit(kExits[entryPoint], kCameraLevels);
    mCurrentColour = ( entryPoint==0 ? 3 : 0 );
    return mPlayer;
    
  } // createPlayer()
  
  // colour (0 to 3) of the grid square (x,y), or -1 if off grid
  static private int colourAt(int x, int y) {
    
    if ( x < 0 || x >= 4 || y < 0 || y >= 4 ) return -1;
    
    int iy = 8 - 2*y,
        ix = 1 + 2*x;
    String row = kBlocks[kBlocks.length-1][iy];
    char ch = row.charAt(ix);

    int col = ch - '0';
    assert( col >= 0 && col < 4 );
    
    return col;
    
  } // colourAt()

  // colour (0 to 3) of the grid 
  static private int colourAt(Player player) {
    
    if ( player == null ) return -1;
    
    int x = player.getXPos() - 1,
        y = player.getYPos() - 1;
    if ( x < 0 || x >= 8 || y < 0 || y >= 8 ) return -1;
    
    return colourAt(x/2, y/2);
    
  } // colourAt()

  // fill in the grid colours
  private void paintBlocks(BlockArray blocks) {
    
    EgaImage image = new EgaImage(14, 14, 30, 15);
    for ( int iy = 0 ; iy < 4 ; iy++ ) {
      for ( int ix = 0 ; ix < 4 ; ix++ ) {
        int col = colourAt(ix,iy);
        int x = 4*ix - 4*iy,
            y = -(2*ix + 2*iy);
        byte b = EgaTools.decodePixel( kGridColours[col] );
        image.fill(x,x+1, y,y, b);
        if ( col == colourAt(ix+1,iy) ) image.fill(x+2,x+3, y-1,y-1, b);
        if ( col == colourAt(ix,iy+1) ) image.fill(x-2,x-1, y-1,y-1, b);
      }
    }
    blocks.paint(image, 1, 1, 5);
    
  } // paintBlocks()
  
  // create the switches
  private void makeSwitches(SpriteManager spriteManager) {
    
    mSwitches = new WallSwitch[kOrder.length];
    for ( int k = 0 ; k < mSwitches.length ; k++ ) {
      int col = kOrder[k];
      char ch = kBlockColours[col].charAt(0);
      mSwitches[k] = new WallSwitch(Env.UP, k+1, 9,
                                    new String[]{ 
                                            new String(new char[]{ch,'7'}), 
                                            new String(new char[]{ch,'#'}), 
                                            "u7" }, 
                                    false);
      spriteManager.addSprite( mSwitches[k] );
    }

    mSwitchIndex = ( mRoomDone ? -1 : 0 );
    updateSwitches();
    
  } // makeSwitches()

  // change the states of the switches as necessary
  private void updateSwitches() {
    
    for ( int k = 0 ; k < mSwitches.length ; k++ ) {
      if ( mRoomDone )              mSwitches[k].setState(2);
      else if ( k == mSwitchIndex ) mSwitches[k].setState(1);
      else                          mSwitches[k].setState(0);
    }
    
  } // updateSwitches()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {

    BlockArray blocks = new BlockArray(kBlocks, kBlockColours, 0,0,0);
    paintBlocks(blocks);
    spriteManager.addSprite( blocks );
    
    addBasicWalls(kExits, spriteManager);

    makeSwitches(spriteManager);

    mCurrentColour = -1;

    mSpikes = new Spikes[4][4];
    for ( int iy = 0 ; iy < mSpikes.length ; iy++ ) {
      for ( int ix = 0 ; ix < mSpikes[iy].length ; ix++ ) {
        char col = kBlockColours[ colourAt(ix,iy) ].charAt(0);
        mSpikes[ix][iy] = new Spikes(2*ix+1,2*iy+1,4, 2,2, false, 
                                     new String(new char[]{col,'0'}));
        mSpikes[ix][iy].setSilent(true);
        spriteManager.addSprite(mSpikes[ix][iy]);
      }
    }

    mResetTimer = 0;
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mSwitches = null;
    
  } // Room.discardResources()
  
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
    
    // check current grid square
    if ( !mRoomDone ) {
      if ( mPlayer != null && colourAt(mPlayer) != mCurrentColour ) {
        int col = colourAt(mPlayer);
        if ( col == -1 ) {
          if ( mPlayer.getXPos() == 9 ) {
            mRoomDone = true;
            updateSwitches();
            Env.sounds().play(Sounds.SUCCESS);
          }
        } else if ( mSwitchIndex >= kOrder.length-1 || 
                    col != kOrder[mSwitchIndex+1] ) {
          boolean playSound = false;
          for ( int ix = 0 ; ix < mSpikes.length ; ix++ ) {
            for ( int iy = 0 ; iy < mSpikes[ix].length ; iy++ ) {
              if ( colourAt(ix,iy) == col ) {
                if ( !mSpikes[ix][iy].active() ) playSound = true;
                mSpikes[ix][iy].trigger();
              }
            }
          }
          if ( playSound ) Env.sounds().play(Sounds.SPIKES);
          mResetTimer = kResetDelay;
        } else {
          mSwitchIndex += 1;
          updateSwitches();
          mCurrentColour = col;
          Env.sounds().play(Sounds.SWITCH_ON);
        }
      }
    }

    // check if player is respawning
    if ( !mRoomDone ) {
      if ( mPlayer == null ) {
        if ( mSwitchIndex > 0 ) {
          if ( --mResetTimer <= 0 ) {
            mResetTimer = kResetDelay;
            mSwitchIndex -= 1;
            updateSwitches();
          }
        }
      }
    }
    
  } // Room.advance()

} // class RoomD08
