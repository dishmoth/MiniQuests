/*
 *  RoomD02.java
 *  Copyright Simon Hern 2014
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
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
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.Tree;

// the room "D02"
public class RoomD02 extends Room {

  // unique identifier for this room
  public static final String NAME = "D02";
  
  // main blocks for the floor
  private static final String kBlocks[][] 
                          = { { "000000000000004440000000000000",
                                "000000000000000200000000000000",
                                "001110000111000200011000011100",
                                "001110000111000200011000011100",
                                "001110000111000200011000011100",
                                "000000000111000200011000000000",
                                "000000000000000200000000000000",
                                "000000000000000200000000000000",
                                "000000000000000200000000000000",
                                "001111000000000200000000111100",
                                "001111000002222222220000111100",
                                "000000000002222222220000000000",
                                "000000000002233333220000000000",
                                "400000000002233333220000000004",
                                "422222222222233333222222222224",
                                "400000000002233333220000000004",
                                "000000000002233333220000000000",
                                "000000000002222222220000000000",
                                "001111000002222222220000111100",
                                "001111000000000200000000111100",
                                "001111000000000200000000111100",
                                "000000000000000200000000000000",
                                "000000000000000200000000000000",
                                "000000000000000200000000000000",
                                "000000000111000200011000000000",
                                "001110000111000200011000011100",
                                "001110000111000200011000011100",
                                "001110000111000200011000011100",
                                "000000000000000200000000000000",
                                "000000000000004440000000000000" } };
  
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
  
  // water in the fountain
  private static final String kWaterPattern[] = { "#####",
                                                  "#####",
                                                  "#####",
                                                  "#####",
                                                  "#####" };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "Y2",   // grass green
                                                  "mm",   // soil
                                                  "Ym",   // path
                                                  "#E",   // fountain block
                                                  "Em" }; // door block
  
  // colour map for flower beds
  private static final char kFlowerColours[] = {'m','q','D','B','s','#'};
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(0,1, Env.LEFT,  5,0, "Em",0, -1, RoomD02.NAME, 1),
              new Exit(2,1, Env.RIGHT, 5,0, "#m",0, -1, RoomD02.NAME, 0),
              new Exit(1,2, Env.UP,    5,0, "#m",0, -1, RoomD02.NAME, 3),
              new Exit(1,0, Env.DOWN , 5,0, "Em",0, -1, RoomD02.NAME, 2) };

  // constructor
  public RoomD02() {

    super(NAME);

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

  } // Room.discardResources()

  // make a rectangle of hedges
  private void addHedgeBox(SpriteManager spriteManager,
                           int x, int y, int xLen, int yLen) {
    
    spriteManager.addSprite(new Hedge(x, y,          0, xLen,   Env.RIGHT, 0));
    spriteManager.addSprite(new Hedge(x, y+yLen-1,   0, xLen,   Env.RIGHT, 0));
    spriteManager.addSprite(new Hedge(x, y+1,        0, yLen-2, Env.UP, 0));
    spriteManager.addSprite(new Hedge(x+xLen-1, y+1, 0, yLen-2, Env.UP, 0));
    
  } // addHedgeBox()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {

    int zoneX, zoneY;

    // zone (0,0)
    
    zoneX = 0;
    zoneY = 0;
    BlockArray mainBlocks = new BlockArray(kBlocks, kBlockColours, 
                                           zoneX*Room.kSize, zoneY*Room.kSize,
                                           0);
    spriteManager.addSprite(mainBlocks); 
    addBasicZone(zoneX, zoneY, 
                 false, false, true, true, 
                 kExits, spriteManager);
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
                                     1, 1));

    EgaImage flowers00A = new EgaImage(4, 6, 12, 7, 
                                       EgaTools.convertColours(
                                                    "      00    "
                                                  + "    005005  "
                                                  + "  0300003000"
                                                  + "500003000030"
                                                  + "0030000500  "
                                                  + "  005000    "
                                                  + "    03      ",
                                       kFlowerColours));
    mainBlocks.paint(flowers00A, zoneX*Room.kSize+2, zoneY*Room.kSize+9, 0);
    
    EgaImage flowers00B = new EgaImage(4, 5, 10, 6, 
                                       EgaTools.convertColours(
                                                    "    00    "
                                                  + "  000000  "
                                                  + "0000000000"
                                                  + "0000000000"
                                                  + "  000000  "
                                                  + "    00    ",
                                       kFlowerColours));
    mainBlocks.paint(flowers00B, zoneX*Room.kSize+2, zoneY*Room.kSize+2, 0);

    EgaImage flowers00C = new EgaImage(6, 6, 12, 7, 
                                       EgaTools.convertColours(
                                                    "    00      "
                                                  + "  001050    "
                                                  + "0100000010  "
                                                  + "000005000001"
                                                  + "  5010000500"
                                                  + "    000100  "
                                                  + "      50    ",
                                       kFlowerColours));
    mainBlocks.paint(flowers00C, zoneX*Room.kSize+9, zoneY*Room.kSize+2, 0);
    
    // zone (1,0)
    
    zoneX = 1;
    zoneY = 0;
    addBasicZone(zoneX, zoneY, 
                 false, false, false, true, 
                 kExits, spriteManager);
    
    // zone (2,0)
    
    zoneX = 2;
    zoneY = 0;
    addBasicZone(zoneX, zoneY, 
                 true, false, false, true, 
                 kExits, spriteManager);
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
                                     1, 1));

    EgaImage flowers20A = new EgaImage(4, 6, 12, 7, 
                                       EgaTools.convertColours(
                                                    "      02    "
                                                  + "    050000  "
                                                  + "  0000205020"
                                                  + "020500000005"
                                                  + "0000000200  "
                                                  + "  020500    "
                                                  + "    00      ",
                                       kFlowerColours));
    mainBlocks.paint(flowers20A, zoneX*Room.kSize+4, zoneY*Room.kSize+9, 0);
    
    EgaImage flowers20B = new EgaImage(4, 5, 10, 6, 
                                       EgaTools.convertColours(
                                                    "    00    "
                                                  + "  000000  "
                                                  + "0000000000"
                                                  + "0000000000"
                                                  + "  000000  "
                                                  + "    00    ",
                                       kFlowerColours));
    mainBlocks.paint(flowers20B, zoneX*Room.kSize+5, zoneY*Room.kSize+2, 0);

    EgaImage flowers20C = new EgaImage(6, 5, 10, 6, 
                                       EgaTools.convertColours(
                                                    "  50      "
                                                  + "100010    "
                                                  + "05000000  "
                                                  + "  00501001"
                                                  + "    000050"
                                                  + "      10  ",
                                       kFlowerColours));
    mainBlocks.paint(flowers20C, zoneX*Room.kSize-1, zoneY*Room.kSize+2, 0);

    // zone (0,1)
    
    zoneX = 0;
    zoneY = 1;
    addBasicZone(zoneX, zoneY, 
                 false, false, true, false, 
                 kExits, spriteManager);

    // zone (1,1)
    
    zoneX = 1;
    zoneY = 1;
    spriteManager.addSprite(
                 new BlockArray(kFountainBlocks, kBlockColours,
                                zoneX*Room.kSize, zoneY*Room.kSize, 2));
    spriteManager.addSprite(new Liquid(zoneX*Room.kSize+3, 
                                       zoneY*Room.kSize+3, 
                                       1, 0, kWaterPattern));
    spriteManager.addSprite(new Fountain(zoneX*Room.kSize+5, 
                                         zoneY*Room.kSize+5,
                                         2));
    
    // zone (2,1)
    
    zoneX = 2;
    zoneY = 1;
    addBasicZone(zoneX, zoneY, 
                 true, false, false, false, 
                 kExits, spriteManager);

    // zone (0,2)
    
    zoneX = 0;
    zoneY = 2;
    addBasicZone(zoneX, zoneY, 
                 false, true, true, false, 
                 kExits, spriteManager);
    addHedgeBox(spriteManager, 
                zoneX*Room.kSize+1, zoneY*Room.kSize+4,
                5, 5);
    addHedgeBox(spriteManager, 
                zoneX*Room.kSize+8, zoneY*Room.kSize+3,
                5, 6);
    addHedgeBox(spriteManager, 
                zoneX*Room.kSize+1, zoneY*Room.kSize-2,
                6, 4);
    spriteManager.addSprite(new Tree(zoneX*Room.kSize+3, zoneY*Room.kSize+6, 0,
                                     1, 1));

    EgaImage flowers02A = new EgaImage(2, 5, 10, 6, 
                                       EgaTools.convertColours(
                                                    "      05  "
                                                  + "    003003"
                                                  + "  30500000"
                                                  + "00000305  "
                                                  + "030500    "
                                                  + "  00      ",
                                       kFlowerColours));
    mainBlocks.paint(flowers02A, zoneX*Room.kSize+2, zoneY*Room.kSize-1, 0);
    
    EgaImage flowers02B = new EgaImage(4, 5, 10, 6, 
                                       EgaTools.convertColours(
                                                    "    00    "
                                                  + "  000000  "
                                                  + "0000000000"
                                                  + "0000000000"
                                                  + "  000000  "
                                                  + "    00    ",
                                       kFlowerColours));
    mainBlocks.paint(flowers02B, zoneX*Room.kSize+2, zoneY*Room.kSize+5, 0);

    EgaImage flowers02C = new EgaImage(6, 6, 12, 7, 
                                       EgaTools.convertColours(
                                                    "    05      "
                                                  + "  040040    "
                                                  + "5000000040  "
                                                  + "004050000050"
                                                  + "  0000050000"
                                                  + "    040004  "
                                                  + "      00    ",
                                       kFlowerColours));
    mainBlocks.paint(flowers02C, zoneX*Room.kSize+9, zoneY*Room.kSize+4, 0);

    // zone (1,2)
    
    zoneX = 1;
    zoneY = 2;
    addBasicZone(zoneX, zoneY, 
                 false, true, false, false, 
                 kExits, spriteManager);

    // zone (2,2)
    
    zoneX = 2;
    zoneY = 2;
    addBasicZone(zoneX, zoneY, 
                 true, true, false, false, 
                 kExits, spriteManager);
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
                                     1, 1));
    
    EgaImage flowers22A = new EgaImage(2, 5, 10, 6, 
                                       EgaTools.convertColours(
                                                    "      02  "
                                                  + "    050000"
                                                  + "  00200205"
                                                  + "50200000  "
                                                  + "000050    "
                                                  + "  02      ",
                                       kFlowerColours));
    mainBlocks.paint(flowers22A, zoneX*Room.kSize+4, zoneY*Room.kSize-1, 0);
    
    EgaImage flowers22B = new EgaImage(4, 5, 10, 6, 
                                       EgaTools.convertColours(
                                                    "    00    "
                                                  + "  000000  "
                                                  + "0000000000"
                                                  + "0000000000"
                                                  + "  000000  "
                                                  + "    00    ",
                                       kFlowerColours));
    mainBlocks.paint(flowers22B, zoneX*Room.kSize+5, zoneY*Room.kSize+5, 0);

    EgaImage flowers22C = new EgaImage(6, 5, 10, 6, 
                                       EgaTools.convertColours(
                                                    "  04      "
                                                  + "040005    "
                                                  + "00004000  "
                                                  + "  50000405"
                                                  + "    040000"
                                                  + "      05  ",
                                       kFlowerColours));
    mainBlocks.paint(flowers22C, zoneX*Room.kSize-1, zoneY*Room.kSize+4, 0);

  } // Room.createSprites()
  
  // returns true if the room is frozen (e.g., during a cut-scene)
  @Override
  public boolean paused() { return false; }

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
    
    // check for scrolling
    
    EventRoomScroll scroll = checkHorizontalScroll();
    if ( scroll != null ) {
      storyEvents.add(scroll);
    }
    
  } // Room.advance()

} // class RoomD02
