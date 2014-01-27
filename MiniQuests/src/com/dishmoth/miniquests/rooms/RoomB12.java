/*
 *  RoomB12.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.EgaImage;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Fence;
import com.dishmoth.miniquests.game.Picture;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.Wall;
import com.dishmoth.miniquests.game.WallDown;
import com.dishmoth.miniquests.game.WallLeft;
import com.dishmoth.miniquests.game.WallUp;

// the room "B12"
public class RoomB12 extends Room {

  // blocks for the room
  private static final String kBlocks[][] = { { "00000     ",
                                                "00000     ",
                                                "00000     ",
                                                "00000     ",
                                                "00000     ",
                                                "00000     ",
                                                "000000000 ",
                                                "000000000 ",
                                                "000000000 ",
                                                "000000000 " },
                                                
                                              { " 000      ",
                                                " 000      ",
                                                " 000      ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { " 000      ",
                                                " 000      ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " },
                                                
                                              { " 000      ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " } };
  
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#k" }; // 
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.DOWN, 3,0, "#k",0, -1, RoomB07.class, 3),
              new Exit(Env.UP,   2,6, "#k",1, -1, RoomB13.class, 0) };
  
  // changing water colours (1 => light, 2 => dark)
  private static final byte   kWaterColour1    = 43,
                              kWaterColour2    = 11;
  private static final double kWaterFraction1  = 0.5;
  private static final int    kWaterChangeRate = 5;
  
  // image for the room's background
  private static EgaImage kBackdropImage;
  private static int      kBackdropWater[];
  
  // prepare resources
  static public void initialize() {
    
    if ( kBackdropImage != null ) return;
    kBackdropImage = Env.resources().loadEgaImage("TowerTop.png");

    int num = 0;
    byte pixels[] = kBackdropImage.pixels();
    for ( int k = 0 ; k < pixels.length ; k++ ) {
      if ( pixels[k] == kWaterColour1 ) num++;
    }
    
    kBackdropWater = new int[num];
    int index = 0;
    for ( int k = 0 ; k < pixels.length ; k++ ) {
      if ( pixels[k] == kWaterColour1 ) {
        kBackdropWater[index++] = k;
      }
    }

    for ( int k = 0 ; k < kBackdropWater.length ; k++ ) {
      int pix = kBackdropWater[k];
      pixels[pix] = (Env.randomDouble() < kWaterFraction1) 
                    ? kWaterColour1 : kWaterColour2;
    }
  
  } // initialize()
  
  // constructor
  public RoomB12() {

    initialize();

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

    final int upShift = 1;
    Wall walls[] = new Wall[4];
    walls[Env.RIGHT] = null;
    walls[Env.UP]    = new WallUp(-upShift, 0, 0);
    walls[Env.LEFT]  = new WallLeft(0, 0, 0);
    walls[Env.DOWN]  = new WallDown(0, 0, 0);
    for ( Exit exit : kExits ) {
      final Wall wall = walls[ exit.mWallSide ];
      int shift = (wall == walls[Env.UP]) ? upShift : 0;
      exit.mDoor = wall.addDoor(exit.mDoorXYPos+shift, exit.mDoorZPos, 
                                exit.mFloorColour, exit.mFloorDrop);
    }
    for ( Wall w : walls ) {
      if ( w != null ) spriteManager.addSprite(w);
    }

    spriteManager.addSprite(new Fence(9,-1,0, 5, Env.UP, 1));
    spriteManager.addSprite(new Fence(5,3,0,  5, Env.RIGHT, 1));
    spriteManager.addSprite(new Fence(5,3,0,  7, Env.UP, 1));

    spriteManager.addSprite(new Picture(kBackdropImage, 17, 0, 100));
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {

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

    // animate the water
    byte pixels[] = kBackdropImage.pixels();
    for ( int n = 0 ; n < kWaterChangeRate ; n++ ) {
      int pixelNum = kBackdropWater[ Env.randomInt(kBackdropWater.length) ];
      pixels[pixelNum] = (Env.randomDouble() < kWaterFraction1) 
                         ? kWaterColour1 : kWaterColour2;
    }
    
  } // Room.advance()
  
} // class RoomB12
