/*
 *  RoomF01.java
 *  Copyright (c) 2024 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import com.dishmoth.miniquests.game.*;

import java.util.Iterator;
import java.util.LinkedList;

// the room "F01"
public class RoomF01 extends Room {

  // unique identifier for this room
  public static final String NAME = "F01";

  // main blocks for the floor (height data for terrain)
  private static final String kTerrainBlocks[] = {
          //         0        01        12        23        3         //
          "                                                            ",
          "                                                            ",
          "                                                            ",
          "                                                            ",
          "               8                                            ",
          "             88899999999999999999999999999999               ",
          "             888999999999999999999999999999999              ",
          "            78889999999999999999999999999999999             ",
          "           7778888999999999999988889999999999999            ",
          "        1177777788889999999888888888899999888899999         ",
          "        12555555555555666666666666666666666666688999        ", // 3
          "        124455555555555555566666666666666666666668899       ",
          "        114444455555555555555555666666666666666666899       ",
          "        113444444444444455555555555566666666666666899       ",
          "        1134444444444444444455555555556666666666668999      ",
          "        11344444444444444444445555555555566666666689999     ",
          "        11444444444444444444444445555555555666666689999     ",
          "        12444444444444444444444444445555555556666689999     ",
          "        12444444444444444444444444444555555555555589999     ",
          "        12444444444444444444444444444455555555555589999     ", // 3
          "        12444444444444444444444444444455555555555589999     ", // 2
          "        11444444444444444444444444444455555555555589999     ",
          "        11344444444444444444444444444444555555555589999     ",
          "        01344444444444444444444444444444555555555578999     ",
          "        01244444444444444444444444444444455555555578999     ",
          "         0044444444444444444444444444444455555555578999     ",
          "         0044444444444444444444444444444455555555588999     ",
          "         0044444444444444444444444444444445555555588999     ",
          "         0044444444444444444444444444444445555555688899     ",
          "        00044444444444444444444444444444445555555688899     ", // 2
          "        02244444444444444444444444444444445555555688899     ", // 1
          "        02244444444444444444444444444444445555555688899     ",
          "        01244444444444444444444444444444445555555668899     ",
          "        01244444444444444444444444444444445555555668899     ",
          "        01244444444444444444444444444444445555555668899     ",
          "        01444444444444444444444444444444444555555668889     ",
          "        01444444444444444444444444444444444555555688889     ",
          "        00444444444444444444444444444444444555555578889     ",
          "         0444444444444444444444444444444444555555578889     ",
          "         0144444444444444444444444444444444555555577889     ", // 1
          "         01444444444444444444444444444444445555555778888    ", // 0
          "         00444444444444444444444444444444445555555778888    ",
          "         00344444444444444444444444444444445555555777888    ",
          "         00344444444444444444444444444444444555555777888    ",
          "         00334444444444444444444444444444444555555777888    ",
          "         0033444444444444444444444444444444455555577778     ",
          "         0033333444444444444444444444444444455555577777     ",
          "         000333334444444444444444444444444444555557777      ",
          "         00033333334444444444444411113444444445551177       ",
          "           00000111112222444444441111333444444422227        ", // 0
          "            0000001111111111111111111111222222222222        ",
          "                    111111111111111111111111111111          ",
          "                                                            ",
          "                                                            ",
          "                                                            ",
          "                                                            ",
          "                                                            ",
          "                                                            ",
          "                                                            ",
          "                                                            " };
          //         0        01        12        23        3         //

  // details of trees (x, y, z, shift)
  private static final int kTreeData[][] = { {  6, 38,  2, 0 },
                                             {  1, 40,  6, 0 },
                                             {  9, 41, 10, 1 },
                                             { 13, 42, 10, 0 },
                                             { 14, 40, 10, 0 },
                                             { 27, 37,  4, 0 },
                                             { 29, 41, 10, 0 },
                                             { 33, 42, 10, 1 } };

  // details of stones
  private static final int kStoneData[][] = { {  4,  6, 0 },
                                              { 36,  5, 2 },
                                              { 11, 33, 0 },
                                              { 37, 34, 4 },
                                              { 27, 15, 0 } };

  // the portal stones
  private PortalStone mStones[] = null;

  // which stone is currently doing stuff
  private int mCurrentStone;

  //
  private PortalSightBase mPortalSight = null;

  //
  private TerrainPath mPath = null;

  // invisible barrier at the player start
  private BlockArray mInvisiBlock = null;

  // constructor
  public RoomF01() {

    super(NAME);

    mCurrentStone = 0;

  } // constructor

  // serialize the room state by writing bits to the specified buffer
  @Override
  public void save(BitBuffer buffer) {
    
    //buffer.writeBit(mSwitchDone);
    
  } // Room.save()

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  @Override
  public boolean restore(int version, BitBuffer buffer) { 
    
    //if ( buffer.numBitsToRead() < 1 ) return false;
    //mSwitchDone = buffer.readBit();
    return true; 
    
  } // Room.restore() 
  
  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint <= 3 );
    
    if ( entryPoint == 0 ) {
      // special case: start of game
      final int zoneX = 0,
                zoneY = 2;
      mPlayer = new Player(zoneX*Room.kSize - 1,
                           zoneY*Room.kSize + 5,
                           -6, Env.RIGHT);
      mPlayer.addBrain(new Brain.ZombieModule(new int[]{ Env.NONE,5,
                                                         Env.RIGHT,7,
                                                         Env.UP, 8,
                                                         Env.RIGHT,20 }));
      mCameraLevel = -1;
      mCamera.set(zoneX*Room.kSize, zoneY*Room.kSize, 0);
    } else {
      //setPlayerAtExit(kExits[entryPoint-1]);
    }

    return mPlayer;
    
  } // createPlayer()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    // TODO

  } // Room.discardResources()

  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {

    // main terrain
    Terrain.makeBlocks(spriteManager, -1, -1, -8, kTerrainBlocks);

    // add trees
    for ( int tree[] : kTreeData ) {
      int x     = tree[0],
          y     = tree[1],
          z     = tree[2],
          shift = tree[3];
      spriteManager.addSprite(new Tree(x, y, z, shift, 0,0));
    }

    // add stones
    mStones = new PortalStone[kStoneData.length];
    for ( int i = 0 ; i < kStoneData.length ; i++ ) {
      int x = kStoneData[i][0],
          y = kStoneData[i][1],
          z = kStoneData[i][2];
      mStones[i] = new PortalStone(x, y, z, i);
      spriteManager.addSprite(mStones[i]);
    }
    mStones[mCurrentStone].setAlert();

    // add ruins
    spriteManager.addSprite(new Hedge(Room.kSize, Room.kSize,
                                      0, new String[]{"##########",
                                                           "#        #",
                                                           "         #",
                                                           "         #",
                                                           "          ",
                                                           "          ",
                                                           "         #",
                                                           "#        #",
                                                           "#        #",
                                                           "####    ##"}, 1));

    //
    mPath = Terrain.makePath(-1, -1, -8, kTerrainBlocks, 13, 15,
            new String[]{
                    "            0",
                    "            0",
                    "            0",
                    "            0",
                    "            0",
                    "            0",
                    "            0",
                    "000         0",
                    "0 00000000000",
                    "000          "}, 'h');

  } // Room.createSprites()
  
  // check if the player is next to the stone
  private boolean playerAtStone(PortalStone stone) {

    if ( mPlayer == null || stone == null ) return false;
    int dx = Math.abs(mPlayer.getXPos() - stone.getXPos()),
        dy = Math.abs(mPlayer.getYPos() - stone.getYPos());
    return ( dx <= 1 && dy <= 1 );

  } // playerAtStone()

  // update the room (events may be added or processed)
  @Override
  public void advance(LinkedList<StoryEvent> storyEvents,
                      SpriteManager          spriteManager) {

    // check exits
    //
    //final int exitIndex = checkExits(kExits);
    //if ( exitIndex != -1 ) {
    //  storyEvents.add(new EventRoomChange(kExits[exitIndex].mDestination,
    //                                      kExits[exitIndex].mEntryPoint));
    //  return;
    //}
    
    // check for scrolling
    
    EventRoomScroll scroll = checkHorizontalScroll();
    if ( scroll != null ) {
      if ( mPlayer.getXPos() >= 0 && mPlayer.getXPos() < 40 &&
           mPlayer.getYPos() >= 0 && mPlayer.getYPos() < 40 ) {
        storyEvents.add(scroll);
      }
    }
    
    // process the story event list

    int newEntryPoint = -1;
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();

      if ( event instanceof PortalStone.EventActivated ) {
        PortalStone stone = ((PortalStone.EventActivated)event).mStone;
        assert( stone == mStones[mCurrentStone] );
        if (playerAtStone(stone)) {
          mPortalSight = new PortalSight();
          spriteManager.addSprite(mPortalSight);
          spriteManager.addSprite(mPath);
        } else {
          stone.setAlert();
        }
        it.remove();
      }

      if ( event instanceof PortalSightBase.EventEnded ) {
        assert(mPortalSight != null);
        spriteManager.removeSprite(mPath);
        spriteManager.removeSprite(mPortalSight);
        mPortalSight = null;
        mStones[mCurrentStone].setAlert();
        it.remove();
      }

    } // for (event)
    if ( newEntryPoint >= 0 ) {
      storyEvents.add(new EventNewEntryPoint(newEntryPoint));
    }

    // check the player is still on the path
    if ( mStones[mCurrentStone].isActive() && mPlayer != null ) {
      if ( !mPath.onPath(mPlayer.getXPos(), mPlayer.getYPos()) ) {
        mPortalSight.shutdown();
      }
    }

    // HACK: the sight effect must be the last thing that gets drawn
    if ( mPortalSight != null ) {
      mPortalSight.checkSpriteOrder(spriteManager);
    }

    // add an invisible barrier at the player start
    if ( mInvisiBlock == null && mPlayer != null && mPlayer.getZPos() > -4 ) {
      mInvisiBlock = new BlockArray(new String[][]{{"*"}}, new String[]{},
                                 -1, 25, -4);
      spriteManager.addSprite(mInvisiBlock);
    }

  } // Room.advance()

} // class RoomF01
