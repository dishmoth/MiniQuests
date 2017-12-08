/*
 *  RoomA07.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Liquid;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;

// the room "A07"
public class RoomA07 extends Room {

  // unique identifier for this room
  public static final String NAME = "A07";
  
  // main blocks for the room
  private static final String kBlocks[][] = { { "1         ",
                                                "0         ",
                                                "0         ",
                                                "1         ",
                                                "          ",
                                                "          ",
                                                "1         ",
                                                "0         ",
                                                "0         ",
                                                "1         " },
                                                
                                              { "1         ",
                                                "0         ",
                                                "0         ",
                                                "1         ",
                                                "          ",
                                                "          ",
                                                "1         ",
                                                "0         ",
                                                "0         ",
                                                "1         " } };

  // chunks of moving blocks
  private static final String kBlocksX[][] = { { "1001" }, 
                                               { "1001" } },
                              kBlocksY[][] = { { "1","0","0","1" },
                                               { "1","0","0","1" } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "Ng", 
                                                  "#g" }; 
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.LEFT, 7,2, "Ng",1, -1, RoomA06.NAME, 1), 
              new Exit(Env.LEFT, 1,2, "Ng",1, -1, RoomA08.NAME, 0) };

  // positions of block chunks (x, y, 1/2 for horizontal/vertical)  
  private static final int kChunkDetails[][] = { {0,0,1}, {3,0,1}, {6,0,1},
                                                 {0,3,1}, {3,3,1}, {6,3,1},
                                                 {0,6,1}, {3,6,1}, {6,6,1},
                                                 {0,9,1}, {3,9,1}, {6,9,1},
                                                 {3,0,2}, {6,0,2}, {9,0,2},
                                                 {6,3,2}, {9,3,2},
                                                 {3,6,2}, {6,6,2}, {9,6,2} };
  
  // which blocks are active during which stages
  private static final int kNumStages = 3;
  private static final String kStages[] = { "#00#11#00#",
                                            "#  1  2  1",
                                            "#  1  2  1",
                                            "#22#00#22#",
                                            "      2  0",
                                            "      2  0",
                                            "#00#11#11#",
                                            "#  0  1  2",
                                            "#  0  1  2",
                                            "#11#22#00#" };

  // range of z-positions of blocks
  private static final int kBlockMinHeight = -4,
                           kBlockMaxHeight = 0;

  // how fast the block chunks move
  private static final int kBlockMoveTime  = 3,
                           kBlockDropDelay = 3;
  
  // duration of each stage
  private static final int kStageTime = 45;

  // when the sound effect plays during each stage
  private static final int kSoundTime = 6;
  
  // moving block chunks
  private BlockArray mBlockChunks[];

  // which stage the block pattern is at
  private int mStage;

  // tick counter within the stage
  private int mTimer;
  
  // constructor
  public RoomA07() {

    super(NAME);

  } // constructor

  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < kExits.length );
    setPlayerAtExit(kExits[entryPoint]);
    return mPlayer;
    
  } // createPlayer()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {
    
    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,0) );
    
    addBasicWalls(kExits, spriteManager);

    Liquid lava = new Liquid(0,0,-1, 1);
    spriteManager.addSprite(lava);

    mStage = 1;
    mTimer = 0;
    
    mBlockChunks = new BlockArray[kChunkDetails.length];
    for ( int k = 0 ; k < kChunkDetails.length ; k++ ) {
      assert( kChunkDetails[k].length == 3 );      
      final int x    = kChunkDetails[k][0],
                y    = kChunkDetails[k][1],
                type = kChunkDetails[k][2];
      assert( type == 1 || type == 2 );
      
      final int z = kBlockMaxHeight; //(chunkIsUp(k, mStage) ? kBlockMaxHeight : kBlockMinHeight);
      
      mBlockChunks[k] = new BlockArray((type == 1 ? kBlocksX : kBlocksY ), 
                                       kBlockColours, x, y, z);
      spriteManager.addSprite( mBlockChunks[k] );
    }
    
  } // Room.createSprites()
  
  // whether a block chunk is active in the current stage
  private boolean chunkIsUp(int chunkIndex, int stage) {
    
    assert( chunkIndex >= 0 && chunkIndex < kChunkDetails.length );
    assert( stage >= 0 && stage < kNumStages );
    
    final int type = kChunkDetails[chunkIndex][2];
    assert( type == 1 || type == 2 );
    final int x = kChunkDetails[chunkIndex][0] + ((type==1) ? 1 : 0),
              y = kChunkDetails[chunkIndex][1] + ((type==2) ? 1 : 0);
    
    assert( y >= 0 && y < kStages.length );
    String row = kStages[Room.kSize-1-y];
    assert( x >= 0 && x < row.length() );
    final char ch = row.charAt(x);
    assert( ch >= '0' && ch < ('0'+kNumStages) );
    
    return ( ch == ('0'+stage) );
    
  } // chunkIsUp()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mBlockChunks = null;
    
  } // Room.discardResources()
  
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

    // animate the block chunks
    if ( mTimer % kBlockMoveTime == 0 ) {
      for ( int k = 0 ; k < mBlockChunks.length ; k++ ) {
        BlockArray chunk = mBlockChunks[k];
        final boolean isUp = chunkIsUp(k, mStage);
        final int z = chunk.getZPos();
        if ( !isUp && z == kBlockMaxHeight && 
             mTimer < kBlockMoveTime*kBlockDropDelay ) continue;
        final int zTarget = ( isUp ? kBlockMaxHeight : kBlockMinHeight );
        if      ( z < zTarget ) chunk.shiftPos(0,0,+1);
        else if ( z > zTarget ) chunk.shiftPos(0,0,-1); 
      }
    }

    // advance the timer
    mTimer += 1;
    if ( mTimer == kStageTime ) {
      mTimer = 0;
      mStage = ( (mStage+1) % kNumStages );
    }
    if ( mTimer == kSoundTime ) {
      Env.sounds().play(Sounds.GRIND);
    }
    
  } // Room.advance()

} // class RoomA07
