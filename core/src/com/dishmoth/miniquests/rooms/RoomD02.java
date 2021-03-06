/*
 *  RoomD02.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BitBuffer;
import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.EgaImage;
import com.dishmoth.miniquests.game.EgaTools;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Fountain;
import com.dishmoth.miniquests.game.Hedge;
import com.dishmoth.miniquests.game.Liquid;
import com.dishmoth.miniquests.game.Mural;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.Splatter;
import com.dishmoth.miniquests.game.Sprite;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.Tree;
import com.dishmoth.miniquests.game.Wall;
import com.dishmoth.miniquests.game.WallUp;

// the room "D02"
public class RoomD02 extends Room {

  // unique identifier for this room
  public static final String NAME = "D02";
  
  // main blocks for the floor
  private static final String kBlocks[][] 
                          = { { "111111111111114441111111111111",
                                "100000110000011211000011000001",
                                "100000110000011211000011000001",
                                "100000110000011211000011000001",
                                "100000110000011211000011000001",
                                "100000110000011211000011000001",
                                "111111110000011211000011111111",
                                "111111111111111211111111111111",
                                "100000011111111211111110000001",
                                "100000011111111211111110000001",
                                "100000011112222222221110000001",
                                "100000011112222222221110000001",
                                "111111111112233333221111111111",
                                "411111111112233333221111111114",
                                "422222222222233333222222222224",
                                "411111111112233333221111111114",
                                "111111111112233333221111111111",
                                "100000011112222222221110000001",
                                "100000011112222222221110000001",
                                "100000011111111211111110000001",
                                "100000011111111211111110000001",
                                "100000011111111211111110000001",
                                "111111111111111211111111111111",
                                "111111110000011211000011111111",
                                "100000110000011211000011000001",
                                "100000110000011211000011000001",
                                "100000110000011211000011000001",
                                "100000110000011211000011000001",
                                "100000110000011211000011000001",
                                "111111111111114441111111111111" } };
  
  // fountain blocks in the middle of the room
  private static final String kFountainBlocks[][] = { { "          ",
                                                        "  3333333 ",
                                                        "  3     3 ",
                                                        "  3     3 ",
                                                        "  3  3  3 ",
                                                        "  3     3 ",
                                                        "  3     3 ",
                                                        "  3333333 ",
                                                        "          ",
                                                        "          " } };
  
  // blocks around the fountain preventing the player's escape
  private static final String kBarrierBlocks[][] = { { "          ",
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
  private static final String kBlockColours[] = { "mm",   // soil
                                                  "Y2",   // grass green
                                                  "Ym",   // path
                                                  "#E",   // fountain block
                                                  "Em" }; // door block
  
  // colour map for flower beds
  private static final char kFlowerColours[] = {'s','B','q','D'};
  
  // door overlap images to add a moment's colour
  private static final String kDoorImages[] = { "BBBBBBBBBB",
                                                "0qq00qq00q",
                                                "0DD00DD00D",
                                                "ssssssssss" };
  
  // details of exit/entry points for the room 
  // (extra exits: 4 and 5 below)
  // (dummy exits: 6 died in fountain, 7 escaped from fountain)
  private static final Exit kExits[][] 
        = { { new Exit(1,2, Env.UP,    5,0, "#m",0, -1, RoomD03.NAME, 1),
              new Exit(0,1, Env.LEFT,  5,0, "Em",0, -1, RoomD04.NAME, 0),
              new Exit(1,0, Env.DOWN , 5,0, "Em",0, -1, "",0),
              new Exit(2,1, Env.RIGHT, 5,0, "#m",0, -1, RoomD06.NAME, 2) },

            { new Exit(1,2, Env.UP,    5,0, "#m",0, -1, "",0),
              new Exit(0,1, Env.LEFT,  5,0, "Em",0, -1, RoomD03.NAME, 3),
              new Exit(1,0, Env.DOWN , 5,0, "Em",0, -1, RoomD04.NAME, 1),
              new Exit(2,1, Env.RIGHT, 5,0, "#m",0, -1, "",0) },

            { new Exit(1,2, Env.UP,    5,0, "#m",0, -1, "",0),
              new Exit(0,1, Env.LEFT,  5,0, "Em",0, -1, "",0),
              new Exit(1,0, Env.DOWN , 5,0, "Em",0, -1, RoomD03.NAME, 0),
              new Exit(2,1, Env.RIGHT, 5,0, "#m",0, -1, "",0) },

            { new Exit(1,2, Env.UP,    5,0, "#m",0, -1, RoomD04.NAME, 4),
              new Exit(0,1, Env.LEFT,  5,0, "Em",0, -1, RoomD05.NAME, 0),
              new Exit(1,0, Env.DOWN , 5,0, "Em",0, -1, "",0),
              new Exit(2,1, Env.RIGHT, 5,0, "#m",0, -1, RoomD03.NAME, 2) } };

  // additional exits that appear later (numbers 4 and 5)
  private static final Exit kExtraExits[] 
        = { new Exit(2,2, Env.RIGHT, 4,0, "#m",0, -1, RoomD16.NAME, 3),
            new Exit(2,2, Env.UP,    5,0, "#m",0, -1, RoomD18.NAME, 1) };
  
  // time until the twist animation completes
  private static final int kTwistDelayStart      = 40,
                           kTwistDelayChangeBed1 = 30,
                           kTwistDelayChangeBed2 = 25,
                           kTwistDelayChangeDoor = 15;

  // trigger point for the twist mechanism
  private static final int kTwistXPos = 2,
                           kTwistYPos = 25;
  
  // time until fountain teleportation
  private static final int kFountainStartPause   = 30,
                           kFountainRecolourTime = 30,
                           kFountainEndPause     = 20,
                           kFountainDelay        = kFountainStartPause
                                                 + kFountainRecolourTime
                                                 + kFountainEndPause;
  
  // times during the final door appearance
  private static final int kDoorAppearDelay   = 30,
                           kScrollToDoorDelay = kDoorAppearDelay + 10,
                           kCutSceneDelay     = kScrollToDoorDelay + 20;
  
  // the current exits, based on room twist
  private Exit mExits[];
  
  // reference to the main floor blocks
  private BlockArray mMainBlocks;

  // whether the room is complete (the final door has appeared)
  private boolean mRoomDone;

  // room twist (anti-clockwise, 0 to 3)
  private int mTwist;

  // time until turn is complete (+ve for anticlockwise, -1 for clockwise)
  private int mTwistTimer;

  // monitor the twist trigger (+1 anticlockwise, -1 clockwise, 0 none) 
  private int mTwistDirec;
  
  // keep track of the player's position (or -1)
  private int mLastXPos,
              mLastYPos;

  // keep track of the last proper entry point (for fountain respawn)
  private int mLastEntryPoint;

  // reference to the door colour overlay
  private Mural mDoorColour;
  
  // reference to the fountain sprite
  private Fountain mFountain;
  
  // reference to the fountain's water sprite
  private Liquid mWater;
  
  // countdown until fountain teleportation
  private int mFountainTimer;
  
  // position of player during fountain teleportation (relative to fountain)
  // (note: these variables are shared with RoomD19)
  private int mInFountainXPos,
              mInFountainYPos,
              mInFountainDirec;
  
  // time during which final door appears
  private int mCutSceneTimer;

  // save the game (specifically the updated entry point) at the next chance
  private boolean mTriggerSaveEvent;
  
  // constructor
  public RoomD02() {

    super(NAME);

    mTwist = 0;
    mRoomDone = false;
    
    mInFountainXPos = 1;
    mInFountainYPos = 3;
    mInFountainDirec = Env.DOWN;
    mLastEntryPoint = 0;
    
  } // constructor

  // serialize the room state by writing bits to the specified buffer
  @Override
  public void save(BitBuffer buffer) {
    
    buffer.write(mTwist, 2);
    buffer.writeBit(mRoomDone);
    
  } // Room.save()

  // de-serialize the room state from the bits in the buffer 
  // (returns false if the version is not supported, or something goes wrong)
  @Override
  public boolean restore(int version, BitBuffer buffer) { 
    
    if ( buffer.numBitsToRead() < 3 ) return false;
    mTwist = buffer.read(2);
    mRoomDone = buffer.readBit();
    return true;
    
  } // Room.restore() 
  
  // access to the room twist (0 to 3, anticlockwise)
  // (note: this function may be called by various other room)
  public int twist() { return mTwist; }
  
  // whether the final door has appeared
  // (note: this function may be called by room D18)
  public boolean completed() { return mRoomDone; }
  
  // record of where the player was in the fountain when the room changed 
  // (note: these functions may be called by RoomD19)
  public int inFountainXPos()  { return mInFountainXPos; }
  public int inFountainYPos()  { return mInFountainYPos; }
  public int inFountainDirec() { return mInFountainDirec; }
  public void recordInFountainInfo(int x, int y, int direc) {
    mInFountainXPos=x; mInFountainYPos=y; mInFountainDirec=direc;
  }
  
  // access to the fountain water sprite, shared between rooms D02 and D19
  // (note: this function may be called by RoomD19)
  public Liquid fountainWater() { return mWater; }
  
  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    if ( entryPoint == 7 ) {
      // dummy entry point: escaped from fountain
      assert( !mRoomDone );
      mCamera.set(kSize, kSize, 0);
      mPlayer = new Player( inFountainXPos()+13,
                            inFountainYPos()+13, 
                            0,
                            inFountainDirec() );
      mPlayer.mAdvanceDisabled = true;
      mCutSceneTimer = kCutSceneDelay;
    } else if ( entryPoint == 6 ) {
      // dummy entry point: died in fountain
      assert( !mRoomDone );
      entryPoint = mLastEntryPoint;
      setPlayerAtExit(mExits[entryPoint]);
      mTriggerSaveEvent = true;
    } else {
      // normal entry point
      assert( entryPoint >= 0 && entryPoint < mExits.length );
      setPlayerAtExit(mExits[entryPoint]);
      mLastEntryPoint = entryPoint;
    }  
    return mPlayer;
    
  } // createPlayer()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

    mMainBlocks = null;
    mExits = null;
    mFountain = null;
    mDoorColour = null;
    //mWater = null;
    
  } // Room.discardResources()

  // configure (or reconfigure) exits based on the room twist
  private void prepareExits() {
    
    assert( mTwist >= 0 && mTwist < kExits.length );
    
    Exit oldExits[] = mExits;
    Exit mainExits[] = kExits[mTwist];

    int numExits = mainExits.length;

    RoomD16 otherRoom = (RoomD16)findRoom(RoomD16.NAME);
    assert( otherRoom != null );
    if ( otherRoom.completed() ) {
      numExits += 1;
      if ( mRoomDone ) numExits += 1;
    } else {
      assert( !mRoomDone );
    }

    mExits = new Exit[numExits];
    for ( int k = 0 ; k < numExits ; k++ ) {
      if ( k < mainExits.length ) mExits[k] = mainExits[k];
      else                        mExits[k] = kExtraExits[k-mainExits.length];
    }
    
    if ( oldExits != null ) {
      for ( int k = 0 ; k < mainExits.length ; k++ ) {
        if ( mExits[k] != oldExits[k] ) {
          mExits[k].mDoor = oldExits[k].mDoor;
          oldExits[k].mDoor = null;
  
          boolean closed = ( mExits[k].mDestination.length() == 0 );
          mExits[k].mDoor.setClosed(closed);
        }
      }
    }
    
  } // prepareExits()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {

    int zoneX, zoneY;

    prepareExits();
    
    // zone (0,0)
    
    zoneX = 0;
    zoneY = 0;
    mMainBlocks = new BlockArray(kBlocks, kBlockColours, 
                                 zoneX*Room.kSize, zoneY*Room.kSize, 0);
    spriteManager.addSprite(mMainBlocks); 

    addBasicZone(zoneX, zoneY, 
                 false, false, true, true, 
                 mExits, spriteManager);
    addHedgeBox(spriteManager, 
                zoneX*Room.kSize+1, zoneY*Room.kSize+1,
                5, 5);
    addHedgeBox(spriteManager, 
                zoneX*Room.kSize+8, zoneY*Room.kSize+1,
                5, 6);
    addHedgeBox(spriteManager, 
                zoneX*Room.kSize+1, zoneY*Room.kSize+8,
                6, 5);
    spriteManager.addSprite(new Tree(zoneX*Room.kSize+3, zoneY*Room.kSize+3, 0,
                                     1, 0,1));

    // zone (1,0)
    
    zoneX = 1;
    zoneY = 0;
    addBasicZone(zoneX, zoneY, 
                 false, false, false, true, 
                 mExits, spriteManager);
    
    // zone (2,0)
    
    zoneX = 2;
    zoneY = 0;
    addBasicZone(zoneX, zoneY, 
                 true, false, false, true, 
                 mExits, spriteManager);
    addHedgeBox(spriteManager, 
                zoneX*Room.kSize+4, zoneY*Room.kSize+1,
                5, 5);
    addHedgeBox(spriteManager, 
                zoneX*Room.kSize-2, zoneY*Room.kSize+1,
                4, 6);
    addHedgeBox(spriteManager, 
                zoneX*Room.kSize+3, zoneY*Room.kSize+8,
                6, 5);
    spriteManager.addSprite(new Tree(zoneX*Room.kSize+6, zoneY*Room.kSize+3, 0,
                                     1, 0,1));

    // zone (0,1)
    
    zoneX = 0;
    zoneY = 1;
    addBasicZone(zoneX, zoneY, 
                 false, false, true, false, 
                 mExits, spriteManager);

    // zone (1,1)
    
    zoneX = 1;
    zoneY = 1;
    spriteManager.addSprite(
                 new BlockArray(kFountainBlocks, kBlockColours,
                                zoneX*Room.kSize, zoneY*Room.kSize, 2));
    mWater = new Liquid(zoneX*Room.kSize+3, 
                        zoneY*Room.kSize+3, 
                        1, 0, kWaterPattern);
    spriteManager.addSprite(mWater);
    
    RoomD17 waterRoom = (RoomD17)findRoom(RoomD17.NAME);
    assert( waterRoom != null );
    RoomD19 bossRoom = (RoomD19)findRoom(RoomD19.NAME);
    assert( bossRoom != null );
    boolean fountainOn = ( !waterRoom.done() || bossRoom.done() );
    mFountain = new Fountain(zoneX*Room.kSize+5, 
                             zoneY*Room.kSize+5,
                             2, fountainOn);
    if ( fountainOn ) {
      if ( !waterRoom.done() || mRoomDone ) mFountain.warmUp();
      else                                  mFountain.mAdvanceDisabled = true;
    }
    spriteManager.addSprite(mFountain);
    
    // zone (2,1)
    
    zoneX = 2;
    zoneY = 1;
    addBasicZone(zoneX, zoneY, 
                 true, false, false, false, 
                 mExits, spriteManager);

    // zone (0,2)
    
    zoneX = 0;
    zoneY = 2;
    addBasicZone(zoneX, zoneY, 
                 false, true, true, false, 
                 mExits, spriteManager);
    addHedgeBoxWithGap(spriteManager, 
                       zoneX*Room.kSize+1, zoneY*Room.kSize+4,
                       5, 5);
    addHedgeBox(spriteManager, 
                zoneX*Room.kSize+8, zoneY*Room.kSize+3,
                5, 6);
    addHedgeBox(spriteManager, 
                zoneX*Room.kSize+1, zoneY*Room.kSize-2,
                6, 4);
    spriteManager.addSprite(new Tree(zoneX*Room.kSize+3, zoneY*Room.kSize+6, 0,
                                     1, 2,2));

    // zone (1,2)
    
    zoneX = 1;
    zoneY = 2;
    addBasicZone(zoneX, zoneY, 
                 false, true, false, false, 
                 mExits, spriteManager);

    // zone (2,2)
    
    zoneX = 2;
    zoneY = 2;
    addBasicZone(zoneX, zoneY, 
                 true, true, false, false, 
                 mExits, spriteManager);
    addHedgeBox(spriteManager, 
                zoneX*Room.kSize+4, zoneY*Room.kSize+4,
                5, 5);
    addHedgeBox(spriteManager, 
                zoneX*Room.kSize-2, zoneY*Room.kSize+3,
                4, 6);
    addHedgeBox(spriteManager, 
                zoneX*Room.kSize+3, zoneY*Room.kSize-2,
                6, 4);
    spriteManager.addSprite(new Tree(zoneX*Room.kSize+6, zoneY*Room.kSize+6, 0,
                                     1, 0,1));
    
    mTwistTimer = 0;
    mTwistDirec = 0;
    mLastXPos = mLastYPos = -1;

    mFountainTimer = 0;
    mCutSceneTimer = 0;
    mTriggerSaveEvent = false;
    
    paintFlowerBeds(mMainBlocks, 0);
    //paintTreeBeds(mMainBlocks);
    //paintLongerPath(mMainBlocks);
    
  } // Room.createSprites()
  
  // make a rectangle of hedges
  private void addHedgeBox(SpriteManager spriteManager,
                           int x, int y, int xLen, int yLen) {
    
    spriteManager.addSprite(new Hedge(x, y,          0, xLen,   Env.RIGHT, 0));
    spriteManager.addSprite(new Hedge(x, y+yLen-1,   0, xLen,   Env.RIGHT, 0));
    spriteManager.addSprite(new Hedge(x, y+1,        0, yLen-2, Env.UP, 0));
    spriteManager.addSprite(new Hedge(x+xLen-1, y+1, 0, yLen-2, Env.UP, 0));
    
  } // addHedgeBox()
  
  // make a rectangle of hedges with a gap
  private void addHedgeBoxWithGap(SpriteManager spriteManager,
                                  int x, int y, int xLen, int yLen) {
    
    final int d = 0;
    spriteManager.addSprite(new Hedge(x,     y, 0, 1+d,      Env.RIGHT, 0));
    spriteManager.addSprite(new Hedge(x+2+d, y, 0, xLen-2-d, Env.RIGHT, 0));
    
    spriteManager.addSprite(new Hedge(x, y+yLen-1,   0, xLen,   Env.RIGHT, 0));
    spriteManager.addSprite(new Hedge(x, y+1,        0, yLen-2, Env.UP, 0));
    spriteManager.addSprite(new Hedge(x+xLen-1, y+1, 0, yLen-2, Env.UP, 0));
    
  } // addHedgeBox()
  
  // colour the flowers in the main beds
  private void paintFlowerBeds(BlockArray blocks, int partialTwist) {

    int zoneX, zoneY;
    char colours[] = { 'm', '?', '#' }; // brown, unknown, and white
    
    assert( mTwist >= 0 && mTwist < 4 );
    assert( partialTwist >= -1 && partialTwist <= +1 );
    int twist = Env.fold( 2*mTwist + partialTwist, 8 );
    
    zoneX = 0;
    zoneY = 0;

    final String pattern0 = "    00      "
                          + "  001020    "
                          + "0100000010  "
                          + "000002000001"
                          + "  2010000200"
                          + "    000100  "
                          + "      20    ";

    colours[1] = kFlowerColours[ ((twist+7)/2)%kFlowerColours.length ];
    EgaImage bed0 = new EgaImage(6, 6, 12, 7, 
                                 EgaTools.convertColours(pattern0, colours));
    blocks.paint(bed0, zoneX*Room.kSize+9, zoneY*Room.kSize+2, 0);
    
    zoneX = 2;
    zoneY = 0;

    final String pattern1 = "  20      "
                          + "100010    "
                          + "02000000  "
                          + "  00201001"
                          + "    000020"
                          + "      10  ";
    
    colours[1] = kFlowerColours[ ((twist+6)/2)%kFlowerColours.length ];
    EgaImage bed1 = new EgaImage(6, 5, 10, 6, 
                                 EgaTools.convertColours(pattern1, colours));
    blocks.paint(bed1, zoneX*Room.kSize-1, zoneY*Room.kSize+2, 0);

    final String pattern2 = "      01    "
                          + "    020000  "
                          + "  0000102010"
                          + "010200000002"
                          + "0000000100  "
                          + "  010200    "
                          + "    00      ";
    
    colours[1] = kFlowerColours[ ((twist+5)/2)%kFlowerColours.length ];
    EgaImage bed2 = new EgaImage(4, 6, 12, 7, 
                                 EgaTools.convertColours(pattern2, colours));
    blocks.paint(bed2, zoneX*Room.kSize+4, zoneY*Room.kSize+9, 0);
    
    zoneX = 2;
    zoneY = 2;

    final String pattern3 = "      01  "
                          + "    020000"
                          + "  00100102"
                          + "20100000  "
                          + "000020    "
                          + "  01      ";
    
    colours[1] = kFlowerColours[ ((twist+4)/2)%kFlowerColours.length ];
    EgaImage bed3 = new EgaImage(2, 5, 10, 6, 
                                 EgaTools.convertColours(pattern3, colours));
    blocks.paint(bed3, zoneX*Room.kSize+4, zoneY*Room.kSize-1, 0);
    
    final String pattern4 = "  01      "
                          + "010002    "
                          + "00001000  "
                          + "  20000102"
                          + "    010000"
                          + "      02  ";
    
    colours[1] = kFlowerColours[ ((twist+3)/2)%kFlowerColours.length ];
    EgaImage bed4 = new EgaImage(6, 5, 10, 6, 
                                 EgaTools.convertColours(pattern4, colours));
    blocks.paint(bed4, zoneX*Room.kSize-1, zoneY*Room.kSize+4, 0);
    
    zoneX = 0;
    zoneY = 2;

    final String pattern5 = "    02      "
                          + "  010010    "
                          + "2000000010  "
                          + "001020000020"
                          + "  0000020000"
                          + "    010001  "
                          + "      00    ";
    
    colours[1] = kFlowerColours[ ((twist+2)/2)%kFlowerColours.length ];
    EgaImage bed5 = new EgaImage(6, 6, 12, 7, 
                                 EgaTools.convertColours(pattern5, colours));
    blocks.paint(bed5, zoneX*Room.kSize+9, zoneY*Room.kSize+4, 0);

    final String pattern6 = "      02  "
                          + "    001001"
                          + "  10200000"
                          + "00000102  "
                          + "010200    "
                          + "  00      ";
    
    colours[1] = kFlowerColours[ ((twist+1)/2)%kFlowerColours.length ];
    EgaImage bed6 = new EgaImage(2, 5, 10, 6, 
                                 EgaTools.convertColours(pattern6, colours));
    blocks.paint(bed6, zoneX*Room.kSize+2, zoneY*Room.kSize-1, 0);
    
    zoneX = 0;
    zoneY = 0;

    final String pattern7 = "      00    "
                          + "    002002  "
                          + "  0100001000"
                          + "200001000010"
                          + "0010000200  "
                          + "  002000    "
                          + "    01      ";
    
    colours[1] = kFlowerColours[ ((twist+0)/2)%kFlowerColours.length ];
    EgaImage bed7 = new EgaImage(4, 6, 12, 7, 
                                 EgaTools.convertColours(pattern7, colours));
    blocks.paint(bed7, zoneX*Room.kSize+2, zoneY*Room.kSize+9, 0);
    
  } // paintFlowerBeds()
  
  // colour the flowers around the trees (currently not actually useful)
  private void paintTreeBeds(BlockArray blocks) {
    
    final String bedPattern = "    mm    "
                            + "  mmmmmm  "
                            + "mmmmmmmmmm"
                            + "mmmmmmmmmm"
                            + "  mmmmmm  "
                            + "    mm    ";
    
    int zoneX, zoneY;
    
    zoneX = 0;
    zoneY = 0;
    EgaImage bed00 = new EgaImage(4, 5, 10, 6, bedPattern);
    blocks.paint(bed00, zoneX*Room.kSize+2, zoneY*Room.kSize+2, 0);

    zoneX = 2;
    zoneY = 0;
    EgaImage bed20 = new EgaImage(4, 5, 10, 6, bedPattern);
    blocks.paint(bed20, zoneX*Room.kSize+5, zoneY*Room.kSize+2, 0);

    zoneX = 0;
    zoneY = 2;
    EgaImage bed02 = new EgaImage(4, 5, 10, 6, bedPattern);
    blocks.paint(bed02, zoneX*Room.kSize+2, zoneY*Room.kSize+5, 0);

    zoneX = 2;
    zoneY = 2;
    EgaImage bed22 = new EgaImage(4, 5, 10, 6, bedPattern);
    blocks.paint(bed22, zoneX*Room.kSize+5, zoneY*Room.kSize+5, 0);

  } // paintTreeBeds()
  
  // make the path around the twist tree more obvious
  private void paintLongerPath(BlockArray blocks) {
    
    final String pathPattern = "  m "
                             + " mmm";
    EgaImage path = new EgaImage(2, 1, 4, 2, pathPattern);
    blocks.paint(path, 2, 2*Room.kSize+3, 0);

  } // paintLongerPath()
  
  // colour the first doorway to match the adjoining room
  private void makeDoorColour(SpriteManager spriteManager) {

    mExits[0].mDoor.setClosed(false);
    
    if ( mDoorColour != null ) spriteManager.removeSprite(mDoorColour);
    
    mDoorColour = new Mural(1,2, Env.UP, 5, 0, 
                         new EgaImage(0,0, 2,5, kDoorImages[mTwist]));
    spriteManager.addSprite(mDoorColour);
    
  } // makeDoorColour()
  
  // returns true if the room is frozen (e.g., during a cut-scene)
  @Override
  public boolean paused() { return (mCutSceneTimer > 0); }

  // update the room (events may be added or processed)
  @Override
  public void advance(LinkedList<StoryEvent> storyEvents,
                      SpriteManager          spriteManager) {

    // advance the cut-scene
    if ( mCutSceneTimer > 0 ) {
      final boolean done = advanceCutScene(storyEvents, spriteManager);
      if ( done ) return;
    }
    
    // check exits
    final int exitIndex = checkExits(mExits);
    if ( exitIndex != -1 ) {
      storyEvents.add(new EventRoomChange(mExits[exitIndex].mDestination,
                                          mExits[exitIndex].mEntryPoint));
      Env.sounds().stop(Sounds.FOUNTAIN);
      return;
    }
    
    // check for scrolling
    EventRoomScroll scroll = checkHorizontalScroll();
    if ( scroll != null ) {
      storyEvents.add(scroll);
      //Env.sounds().stop(Sounds.FOUNTAIN);
    }

    // save the game
    if ( mTriggerSaveEvent ) {
      mTriggerSaveEvent = false;
      storyEvents.add(new Room.EventNewEntryPoint(mLastEntryPoint));
    }
    
    // twist the room, repaint the flower beds
    if ( mTwistTimer != 0 ) {
      int sign = (mTwistTimer > 0) ? +1 : -1;
      mTwistTimer -= sign;
      if ( Math.abs(mTwistTimer) == kTwistDelayStart ) {
        makeDoorColour(spriteManager);
      } else if ( Math.abs(mTwistTimer) == kTwistDelayChangeBed1 ) {
        Env.sounds().play(Sounds.TREE_TWIST);
        paintFlowerBeds(mMainBlocks, sign);
      } else if ( Math.abs(mTwistTimer) == kTwistDelayChangeBed2 ) {
        mTwist = Env.fold(mTwist + sign, 4);
        paintFlowerBeds(mMainBlocks, 0);
      } else if ( Math.abs(mTwistTimer) == kTwistDelayChangeDoor ) {
        makeDoorColour(spriteManager);
      } else if ( mTwistTimer == 0 ) {
        spriteManager.removeSprite(mDoorColour);
        mDoorColour = null;
        prepareExits();
      }
    }
    
    // check whether a twist has been triggered
    if ( mPlayer != null ) {
      int xPos = mPlayer.getXPos(),
          yPos = mPlayer.getYPos();
      if ( xPos != mLastXPos || yPos != mLastYPos ) {
        if ( mLastXPos == kTwistXPos && mLastYPos == kTwistYPos ) {
          // just stepped off the twist trigger
          if ( xPos == kTwistXPos+1 && yPos == kTwistYPos ) {
            mTwistDirec = +1;
          } else if ( xPos == kTwistXPos && yPos == kTwistYPos+1 ) {
            mTwistDirec = -1;            
          } else {
            mTwistDirec = 0;
          }
        } else if ( xPos == kTwistXPos && yPos == kTwistYPos ) {
          // just stepped onto the twist trigger
          if ( mTwistDirec == +1 &&
               mLastXPos == kTwistXPos && mLastYPos == kTwistYPos+1 ) {
            mTwistTimer = +(kTwistDelayStart+1);
          } else if ( mTwistDirec == -1 &&
                      mLastXPos == kTwistXPos+1 && mLastYPos == kTwistYPos ) {
            mTwistTimer = -(kTwistDelayStart+1);
          }
          mTwistDirec = 0;
        }          
      }
    }
    
    // track the player's last position (for twist)
    if ( mPlayer == null ) {
      mLastXPos = mLastYPos = -1;
    } else {
      mLastXPos = mPlayer.getXPos();
      mLastYPos = mPlayer.getYPos();
    }
    
    // check if the player is in the fountain
    if ( !mRoomDone && !mFountain.on() && mFountainTimer == 0 ) {
      if ( mPlayer != null && !mPlayer.isActing() &&
           mPlayer.getXPos() >= 13 && mPlayer.getXPos() <= 17 &&
           mPlayer.getYPos() >= 13 && mPlayer.getYPos() <= 17 &&
           mPlayer.getZPos() == 0 ) {
        spriteManager.addSprite(new BlockArray(kBarrierBlocks, kBlockColours,
                                               Room.kSize, Room.kSize, 6));
        mFountainTimer = kFountainDelay;
      }
    }
    
    // change the fountain water colour before teleporting
    if ( mFountainTimer > 0 ) {
      assert( mPlayer != null );
      if ( --mFountainTimer == 0 ) {
        if ( mPlayer.isActing() ) {
          mFountainTimer += 1;
        } else {
          recordInFountainInfo(mPlayer.getXPos()-13, 
                               mPlayer.getYPos()-13, 
                               mPlayer.getDirec());
          EventRoomChange event = new EventRoomChange(RoomD19.NAME, 0);
          event.mSavePoint = false;
          storyEvents.add(event);
        }
      } else {
        double t = 1.0 - (mFountainTimer - kFountainEndPause)
                         /(double)kFountainRecolourTime;
        if ( t >= 0.0 && t <= 1.0 ) {
          int width = mWater.image().width(),
              height = mWater.image().height();
          byte pixels[] = mWater.image().pixels();
          int types[] = mWater.pixelTypes();
          double rMin = (t>0.999) ? width : (width-2)*t,
                 rMax = rMin + 4.0;
          int px = mPlayer.getXPos() - 15,
              py = mPlayer.getYPos() - 15;
          assert( px >= -2 && px <= +2 && py >= -2 && py <= +2 );
          double xCentre = 0.5*(width-1) + 2*(px-py),
                 yCentre = 0.5*(height-1) - (px+py);
          boolean allDone = true;
          int index = 0;
          for ( int iy = 0 ; iy < height ; iy++ ) {
            for ( int ix = 0 ; ix < width ; ix++ ) {
              if ( pixels[index] >= 0 && types[index] != 2 ) {
                allDone = false;
                double dx = (ix - xCentre),
                       dy = 2.0*(iy - yCentre);
                double r2 = dx*dx + dy*dy;
                boolean recol = ( r2 <= rMin*rMin ) ? true
                              : ( r2 <= rMax*rMax ) ? (Env.randomFloat() < 0.1)
                                                    : false;
                if ( recol ) {
                  pixels[index] = -1;
                  types[index] = 2;
                }
              }
              index += 1;
            }
          }
          if ( allDone ) {
            mFountainTimer = Math.min(mFountainTimer, kFountainEndPause);
          } else {
            mWater.recolourPixels();
          }
        }
        if ( mFountainTimer == kFountainRecolourTime+kFountainEndPause+3 ) {
          Env.sounds().play(Sounds.FOUNTAIN_TWIST);
        }
      }
    }
    
  } // Room.advance()

  // stop the game and show the final door appearing
  private boolean advanceCutScene(LinkedList<StoryEvent> storyEvents,
                                  SpriteManager          spriteManager) {

    assert( mCutSceneTimer > 0 );
    mCutSceneTimer -= 1;

    boolean advanceFinished = true;

    assert( mPlayer != null );
    mPlayer.mAdvanceDisabled = true;
    mFountain.mAdvanceDisabled = true;
    
    // scroll to the door
    if ( mCutSceneTimer == kScrollToDoorDelay ) {
      EventRoomScroll scroll = scrollToZone(2, 2);
      assert( scroll != null );
      storyEvents.add(scroll);
    }

    // make door
    if ( mCutSceneTimer == kDoorAppearDelay ) {
      mRoomDone = true;
      prepareExits();
      Wall wall = null;
      for ( Sprite sp : spriteManager.list() ) {
        if ( sp instanceof WallUp && ((Wall)sp).getXPos() == 2*kSize ) {
          wall = (Wall)sp;
          break;
        }
      }
      assert( wall != null );
      wall.addDoor(kExtraExits[1].mDoorXYPos, 
                   kExtraExits[1].mDoorZPos, 
                   kExtraExits[1].mFloorColour, 
                   kExtraExits[1].mFloorDrop);
      spriteManager.addSprite( new Splatter(2*kSize+kExtraExits[1].mDoorXYPos,
                                            3*kSize, 
                                            kExtraExits[1].mDoorZPos,
                                            -1, 5,
                                            (byte)0, Env.DOWN) );
      Env.sounds().play(Sounds.SUCCESS);
    }
    
    // scroll back to the player
    if ( mCutSceneTimer == 1 ) {
      EventRoomScroll scroll = scrollToPlayer();
      assert( scroll != null );
      storyEvents.add(scroll);
    }

    // tidy up at the end
    if ( mCutSceneTimer == 0 ) {
      mPlayer.mAdvanceDisabled = false;
      mFountain.mAdvanceDisabled = false;
      mFountain.warmUp(15);
    }
    
    return advanceFinished;
    
  } // advanceCutScene()
  
} // class RoomD02
