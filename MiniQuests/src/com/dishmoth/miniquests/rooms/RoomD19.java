/*
 *  RoomD19.java
 *  Copyright Simon Hern 2014
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BitBuffer;
import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.CritterTrack;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Fountain;
import com.dishmoth.miniquests.game.Liquid;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.Spook;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;

// the room "D19"
public class RoomD19 extends Room {

  // unique identifier for this room
  public static final String NAME = "D19";
  
  // main blocks for the floor
  private static final String kBlocks[][] 
                          = { { "000000000000000100000000000000",
                                "011111001111100100111100111110",
                                "010001001000100100100100100010",
                                "010001001000100100100100100010",
                                "010001001000100100100100100010",
                                "011111001000100100100100111110",
                                "000000001111100100111100000000",
                                "000000000000000100000000000000",
                                "011111110000000100000001111110",
                                "010000010011111111111001000010",
                                "010000010010000000001001000010",
                                "011111110010000000001001111110",
                                "000000000010000000001000000000",
                                "000000000010000000001000000000",
                                "111111111110000000001111111111",
                                "000000000010000000001000000000",
                                "000000000010000000001000000000",
                                "011111110010000000001001111110",
                                "010000010010000000001001000010",
                                "010000010011111111111001000010",
                                "010000010000000100000001000010",
                                "011111110000000100000001111110",
                                "000000001111100100111100000000",
                                "000000001000100100100100000000",
                                "011111001000100100100100111110",
                                "010001001000100100100100100010",
                                "010001001000100100100100100010",
                                "010001001000100100100100100010",
                                "011111001111100100111100111110",
                                "000000000000000100000000000000" } };
  
  // fountain blocks in the middle of the room
  private static final String kFountainBlocks[][] = { { "          ",
                                                        "  2222222 ",
                                                        "  2     2 ",
                                                        "  2     2 ",
                                                        "  2  2  2 ",
                                                        "  2     2 ",
                                                        "  2     2 ",
                                                        "  2222222 ",
                                                        "          ",
                                                        "          " },
                                                        
                                                      { "          ",
                                                        "          ",
                                                        "          ",
                                                        "          ",
                                                        "          ",
                                                        "          ",
                                                        "          ",
                                                        "          ",
                                                        "          ",
                                                        "          " },
                                                        
                                                      { "          ",
                                                        "  ------- ",
                                                        "  -     - ",
                                                        "  -     - ",
                                                        "  -     - ",
                                                        "  -     - ",
                                                        "  -     - ",
                                                        "  ------- ",
                                                        "          ",
                                                        "          " } };
  
  // water in the fountain
  private static final String kWaterPattern[] = { "#####",
                                                  "#####",
                                                  "#####",
                                                  "#####",
                                                  "#####" };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "eW",   // grass
                                                  "XW",   // path
                                                  "e0" }; // block
  
  //
  private static final CritterTrack kPath = new CritterTrack(
                                              new String[]{ "###########",
                                                            "#         #",
                                                            "#         #",
                                                            "#         #",
                                                            "#         #",
                                                            "#         #",
                                                            "#         #",
                                                            "#         #",
                                                            "#         #",
                                                            "#         #",
                                                            "###########"},
                                                            10, 10);
  
  // time delay before the player respawns - in the room above
  // (note: we're overriding the default behaviour in TinyStory)
  private static final int kPlayerDeathDelay = 30;
  
  // time until returning to original room
  private static final int kFinishDelay = 45;
  
  // whether the room has been completed
  private boolean mRoomDone;
  
  // time until the player respawns (overriding the behaviour in TinyStory)
  private int mPlayerDeathTimer;
  
  // ??
  private int mDeadSpookCount;
  
  // count down until return to original room 
  private int mFinishTimer;
  
  // constructor
  public RoomD19() {

    super(NAME);
    
    mRoomDone = false;

  } // constructor

  // serialize the room state by writing bits to the specified buffer
  @Override
  public void save(BitBuffer buffer) {
    
  } // Room.save()

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  @Override
  public boolean restore(int version, BitBuffer buffer) { 
    
    return true; 
    
  } // Room.restore() 
  
  // whether the room has been completed
  // (note: this function may be called by room D02)
  public boolean done() { return mRoomDone; }
  
  // remove the player sprite from the room
  // (special behaviour: the player may be null already, since the respawn
  // in this case can also require a change of room)
  @Override
  public void removePlayer() { mPlayer = null; }
  
  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint == 0 );
    assert( !mRoomDone );
    
    RoomD02 originRoom = (RoomD02)findRoom(RoomD02.NAME);
    assert( originRoom != null );
    mPlayer = new Player( originRoom.inFountainXPos()+13,
                          originRoom.inFountainYPos()+13, 
                          0,
                          originRoom.inFountainDirec() );

    mCamera.set(kSize, kSize, 0);

    return mPlayer;
    
  } // createPlayer()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

  } // Room.discardResources()

  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {

    int zoneX, zoneY;
    
    // zone (0,0)
    
    zoneX = 0;
    zoneY = 0;
    spriteManager.addSprite(new BlockArray(kBlocks, kBlockColours, 
                                           zoneX*Room.kSize, 
                                           zoneY*Room.kSize,
                                           0)); 

    // zone (1,1)
    
    zoneX = 1;
    zoneY = 1;
    spriteManager.addSprite(
                 new BlockArray(kFountainBlocks, kBlockColours,
                                zoneX*Room.kSize, zoneY*Room.kSize, 2));
    spriteManager.addSprite(new Liquid(zoneX*Room.kSize+3, 
                                       zoneY*Room.kSize+3, 
                                       1, 2, kWaterPattern));    
    spriteManager.addSprite( new Fountain(zoneX*Room.kSize+5, 
                                          zoneY*Room.kSize+5,
                                          2, false));

    mPlayerDeathTimer = 0;
    mFinishTimer = 0;
    
    //???
    spriteManager.addSprite( new Spook(12,10,0, Env.RIGHT, kPath) );
    spriteManager.addSprite( new Spook(15,10,0, Env.RIGHT, kPath) );
    spriteManager.addSprite( new Spook(18,10,0, Env.RIGHT, kPath) );
    
    spriteManager.addSprite( new Spook(20,12,0, Env.UP, kPath) );
    spriteManager.addSprite( new Spook(20,15,0, Env.UP, kPath) );
    spriteManager.addSprite( new Spook(20,18,0, Env.UP, kPath) );
    
    spriteManager.addSprite( new Spook(12,20,0, Env.LEFT, kPath) );
    spriteManager.addSprite( new Spook(15,20,0, Env.LEFT, kPath) );
    spriteManager.addSprite( new Spook(18,20,0, Env.LEFT, kPath) );
    
    spriteManager.addSprite( new Spook(10,12,0, Env.DOWN, kPath) );
    spriteManager.addSprite( new Spook(10,15,0, Env.DOWN, kPath) );
    spriteManager.addSprite( new Spook(10,18,0, Env.DOWN, kPath) );
    
    mDeadSpookCount = 0;
    
  } // Room.createSprites()
  
  // returns true if the room is frozen (e.g., during a cut-scene)
  @Override
  public boolean paused() { return false; }

  // update the room (events may be added or processed)
  @Override
  public void advance(LinkedList<StoryEvent> storyEvents,
                      SpriteManager          spriteManager) {

    // process the story event list
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();
      
      // monster has been hit
      if ( event instanceof Spook.EventKilled ) {
        mDeadSpookCount += 1;
        if ( mDeadSpookCount == 12 ) mFinishTimer = kFinishDelay;
        it.remove();
      }

      // player has died - respawn back in the fountain
      else if ( event instanceof Player.EventKilled ) {
        mPlayerDeathTimer = kPlayerDeathDelay;
      }
    }

    // respawn back in the fountain (after dying)
    if ( mPlayerDeathTimer > 0 ) {
      if ( --mPlayerDeathTimer == 0 ) {
        assert( mPlayer == null );
        storyEvents.add(new EventRoomChange(RoomD02.NAME, 6));
      }
    }
    
    // respawn back in the fountain (after completing)
    if ( mFinishTimer > 0 && mPlayer != null ) {
      if ( --mFinishTimer == 0 ) {
        if ( mPlayer.isActing() ) {
          mFinishTimer = 1;
        } else {
          mRoomDone = true;
          RoomD02 originRoom = (RoomD02)findRoom(RoomD02.NAME);
          assert( originRoom != null );
          originRoom.recordInFountainInfo(mPlayer.getXPos()-13, 
                                          mPlayer.getYPos()-13, 
                                          mPlayer.getDirec());
          storyEvents.add(new EventRoomChange(RoomD02.NAME, 7));
        }
      }
    }
    
  } // Room.advance()

} // class RoomD02
