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
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Fountain;
import com.dishmoth.miniquests.game.Liquid;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.Spook;
import com.dishmoth.miniquests.game.SpookTrack;
import com.dishmoth.miniquests.game.Sprite;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;

// the room "D19"
public class RoomD19 extends Room {

  // unique identifier for this room
  public static final String NAME = "D19";
  
  // main blocks for the floor
  private static final String kBlocks[][] 
                          = { { "000000000000010101000000000000",
                                "000000000000010101000000000000",
                                "000000000000010101000000000000",
                                "000000000000010101000000000000",
                                "000000000000010101000000000000",
                                "000000000000010101000000000000",
                                "000000000000010101000000000000",
                                "000000001111111111111110000000",
                                "000000001000000100000010000000",
                                "000000001011111111111010000000",
                                "000000001010010101001010000000",
                                "000000001010000000001010000000",
                                "111111111011000000011011111111",
                                "000000001010000000001010000000",
                                "111111111111000000011111111111",
                                "000000001010000000001010000000",
                                "111111111011000000011011111111",
                                "000000001010000000001010000000",
                                "000000001010010101001010000000",
                                "000000001011111111111010000000",
                                "000000001000000100000010000000",
                                "000000001111111111111110000000",
                                "000000000000010101000000000000",
                                "000000000000010101000000000000",
                                "000000000000010101000000000000",
                                "000000000000010101000000000000",
                                "000000000000010101000000000000",
                                "000000000000010101000000000000",
                                "000000000000010101000000000000",
                                "000000000000010101000000000000" } };
  
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
  private static final String kBlockColours[] = { "00",   // grass
                                                  "e0",   // path
                                                  "#E" }; // block
  
  // relative to fountain centre
  private static final int kTrackPoints[][] = { { 18,  0 },
                                                {  7,  0 },
                                                {  7, -7 },
                                                { -7, -7 },
                                                { -7,  0 },
                                                { -5,  0 },
                                                { -5, -5 },
                                                {  5, -5 },
                                                {  5,  0 },
                                                {  2,  0 } };
  
  // how long until monsters appear
  private static final int kSpookInitialDelay = 30,
                           kSpookGapDelay     = 10,
                           kSpookWaveDelay    = 180;
  
  // how long the water changes colour for
  private static final int kWaterZapPause = 8,
                           kWaterZapFlash = 12,
                           kWaterZapDelay = kWaterZapPause + kWaterZapFlash;
  
  // time delay before the player respawns - in the room above
  // (note: we're overriding the default behaviour in TinyStory)
  private static final int kPlayerDeathDelay = 30;
  
  // time until returning to original room
  private static final int kFinishDelay = 75;
  
  // whether the room has been completed
  private boolean mRoomDone;

  // time until water changes back to normal
  private int mWaterZapTimer;
  
  // time until the player respawns (overriding the behaviour in TinyStory)
  private int mPlayerDeathTimer;
  
  // number of monsters spawned so far
  private int mSpookCount;
  
  // how many monsters have been killed
  private int mDeadSpookCount;
  
  // time until the next monster appears (non-zero until monsters all spawned)
  private int mSpookTimer;
  
  // count down until return to original room 
  private int mFinishTimer;

  // reference to the fountain water sprite
  private Liquid mWater;
  
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

    mWater = null;
    
  } // Room.discardResources()

  // make a monster track leading to the fountain
  private SpookTrack makeTrack(int rotate, int offset) {
    
    assert( rotate >= 0 && rotate < 4 );
    assert( offset >= 0 && offset < 3 );
    
    int points[][] = new int[kTrackPoints.length][2];
    for ( int k = 0 ; k < points.length ; k++ ) {
      int dx = kTrackPoints[k][0],
          dy = kTrackPoints[k][1];
      if ( k < 2 ) {
        dy += 2*(offset-1);
      } else if ( k >= points.length-2 ) {
        dy -= 2*(offset-1);
      }
      if ( rotate >= 2 ) {
        dx = -dx;
        dy = -dy;
      }
      if ( rotate%2 == 1 ) {
        int temp = dx;
        dx = -dy;
        dy = temp;
      } else {
        dy = -dy; // flip?
      }
      points[k][0] = 15 + dx;
      points[k][1] = 15 + dy;
    }
    
    return new SpookTrack(points);
    
  } // makeTrack()
  
  // make a monster following a particular track
  private Spook makeSpook(SpookTrack track, int colour) {
    
    int x0 = track.points()[0][0],
        y0 = track.points()[0][1],
        dx = track.points()[1][0] - x0,
        dy = track.points()[1][1] - y0;

    int direc = (dx > 0) ? Env.RIGHT
              : (dx < 0) ? Env.LEFT
              : (dy > 0) ? Env.UP
                         : Env.DOWN;
    
    Spook spook = new Spook(x0, y0, 0, direc, track);
    spook.setColour(colour);
    return spook;
    
  } // makeSpook()
  
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
    
    spriteManager.addSprite( new Fountain(zoneX*Room.kSize+5, 
                                          zoneY*Room.kSize+5,
                                          2, false));
    
    RoomD02 originRoom = (RoomD02)findRoom(RoomD02.NAME);
    assert( originRoom != null );
    mWater = originRoom.fountainWater();
    if ( mWater == null ) {
      mWater = new Liquid(Room.kSize+3,Room.kSize+3,1, 2, kWaterPattern);
    }
    spriteManager.addSprite(mWater);

    mWaterZapTimer = 0;
    mPlayerDeathTimer = 0;
    mFinishTimer = 0;
    mSpookCount = 0;
    mDeadSpookCount = 0;
    mSpookTimer = kSpookInitialDelay;

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
        if ( mDeadSpookCount == mSpookCount && mSpookTimer == 0 && 
             mPlayer != null && mPlayerDeathTimer == 0 ) {
          mFinishTimer = kFinishDelay;
          if ( mWaterZapTimer > kWaterZapDelay-kWaterZapPause ) {
            mWaterZapTimer = 0;
          }
          Env.sounds().play(Sounds.FOUNTAIN_UNTWIST, 30);
        }
        it.remove();
      }

      // player has died - respawn back in the fountain
      else if ( event instanceof Player.EventKilled ) {
        mPlayerDeathTimer = kPlayerDeathDelay;
        mFinishTimer = 0;
      }
    }

    // spawn monsters
    if ( mSpookTimer > 0 ) {
      if ( --mSpookTimer == 0 ) {
        assert( mSpookCount%2 == 0 );
        int rot0 = ( mSpookCount<6 ? 1 : 0 );
        int offset = (mSpookCount/2) % 3;
        int col = ( mSpookCount<6 ? 1 : 2 );
        for ( int rot = rot0 ; rot < 4 ; rot +=2 ) {
          spriteManager.addSprite( makeSpook( makeTrack(rot, offset), col ) );
          mSpookCount += 1;
        }
        Env.sounds().play(Sounds.SPOOK_EMERGE);
        mSpookTimer = (mSpookCount==12) ? 0
                    : (mSpookCount==6)  ? kSpookWaveDelay 
                                        : kSpookGapDelay;
      }
    }
    
    // check whether a spook have reached the fountain
    for ( Sprite sp : spriteManager.list() ) {
      if ( sp instanceof Spook ) {
        Spook spook = (Spook)sp;
        int dx = spook.getXPos() - 15,
            dy = spook.getYPos() - 15;
        if ( Math.abs(dx) <= 3 && Math.abs(dy) <= 3 ) {
          spook.vanish();
          if ( mWaterZapTimer == 0 && mPlayer != null ) {
            mWaterZapTimer = kWaterZapDelay;
          }
        }
      }
    }
    
    // turn the water back to normal
    if ( mWaterZapTimer > 0 ) {
      mWaterZapTimer -= 1;
      if ( mWaterZapTimer == kWaterZapDelay-kWaterZapPause ) {
        spriteManager.removeSprite(mWater);
        mWater = new Liquid(Room.kSize+3,Room.kSize+3,1, 3, kWaterPattern);
        spriteManager.addSprite(mWater);
        if ( mPlayer != null ) mPlayer.destroy(-1);
      } else if ( mWaterZapTimer == 0 ) {
        assert( mPlayer == null );
        spriteManager.removeSprite(mWater);
        mWater = new Liquid(Room.kSize+3,Room.kSize+3,1, 2, kWaterPattern);
        spriteManager.addSprite(mWater);
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
