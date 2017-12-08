/*
 *  RoomA08.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Critter;
import com.dishmoth.miniquests.game.CritterTrack;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Liquid;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.Splatter;
import com.dishmoth.miniquests.game.Sprite;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;

// the room "A08"
public class RoomA08 extends Room {

  // unique identifier for this room
  public static final String NAME = "A08";
  
  // main blocks for the room
  private static final String kBlocks[][] = { { "0000000  0",
                                                "0     0  0",
                                                "0     0  0",
                                                "0     0  0",
                                                "0     0  0",
                                                "0 00000  0",
                                                "0 0      0",
                                                "0 0      0",
                                                "0 0      0",
                                                "0 00000000" },
                                                
                                              { "0000000  0",
                                                "      0  0",
                                                "      0  0",
                                                "      0  0",
                                                "      0  0",
                                                "  00000  0",
                                                "  0      0",
                                                "  0      0",
                                                "0 0      0",
                                                "1 00000000" },
                                                
                                              { "   0000  0",
                                                "      0  0",
                                                "      0  0",
                                                "      0  0",
                                                "      0  0",
                                                "  00000  0",
                                                "  0      0",
                                                "  0      0",
                                                "  0      0",
                                                "  00000000" },
                                                
                                              { "      0  1",
                                                "      0  0",
                                                "      0  0",
                                                "      0  0",
                                                "      0  0",
                                                "  00000   ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "   0000   " } };

  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "Vh",   // blue
                                                  "#h" }; // blue 
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.RIGHT, 6,6, "#h",0, -1, RoomA07.NAME, 1), 
              new Exit(Env.LEFT,  4,0, "Vh",0, -1, RoomA03.NAME, 2) };

  // path followed by enemies in this room
  private static final CritterTrack kCritterTrack 
                    =  new CritterTrack(new String[]{ "+++++++  +",
                                                      "+     +  +",
                                                      "+     +  +",
                                                      "+     +  +",
                                                      "+     +  +",
                                                      "+ +++++  +",
                                                      "+ +      +",
                                                      "+ +      +",
                                                      "+ +      +",
                                                      "+ ++++++++" });

  // delay until next enemy appears
  private static final int kEnemySpawnTime = 120,
                           kEnemyStartTime = 20;
  
  // time until next enemy appears
  private int mTimer;
  
  // constructor
  public RoomA08() {

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

    // lava (the second small patch tidies the effect at the room's edge)
    spriteManager.addSprite( new Liquid(0,0,0, 1) );
    spriteManager.addSprite( new Liquid(1,0,-1, 1, new String[]{"@"}) );

    mTimer = kEnemyStartTime;
    
  } // Room.createSprites()
  
  // room is no longer current, delete any unnecessary references 
  @Override
  public void discardResources() {
    
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

    // add enemies
    if ( --mTimer == 0 ) {
      mTimer = kEnemySpawnTime;
      Critter c = new Critter(0, 0, 2, Env.UP, kCritterTrack);
      c.easilyKilled(true);
      c.setColour(1);
      spriteManager.addSprite(c);
      spriteManager.addSprite(
             new Splatter(c.getXPos(), c.getYPos(), c.getZPos(),
                          -1, 4, (byte)5, -1));
      Env.sounds().play(Sounds.MATERIALIZE);
    }

    // remove enemies
    for ( Sprite sp : spriteManager.list() ) {
      if ( sp instanceof Critter ) {
        Critter c = (Critter)sp;
        if ( c.getXPos() == 9 && c.getYPos() == 9 ) {
          c.destroy(-1);
        }
      }
    }
    
  } // Room.advance()

} // class RoomA08
