/*
 *  RoomA11.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.rooms;

import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.FlameBeamSpin;
import com.dishmoth.miniquests.game.Liquid;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;

// the room "A11"
public class RoomA11 extends Room {

  // unique identifier for this room
  public static final String NAME = "A11";
  
  // main blocks for the room
  private static final String kBlocks[][] = { { "     0    ",
                                                "     0    ",
                                                "     0    ",
                                                "     0    ",
                                                "     0    ",
                                                "000000    ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          " } };
                                              
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "#o",   // green
                                                  "tK" }; // burnt red
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.LEFT, 4,0, "#o",0, -1, RoomA10.NAME, 2),
              new Exit(Env.UP,   5,0, "#o",1, -1, RoomA10.NAME, 5) };

  // flame details
  private static final int   kFlameNum         = 4;
  private static final float kFlameAngSpeed    = 0.045f,
                             kFlameAngFraction = 0.5f;
  private static final float kFlameMinRadius   = 2.5f,
                             kFlameMaxRadius   = 3.5f;
 
  // constructor
  public RoomA11() {

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

    spriteManager.addSprite( new Liquid(0,0,-1, 1) );

    for ( int k = 0 ; k < kFlameNum ; k++ ) {
      float ang = (2.0f*(float)Math.PI*kFlameAngFraction)/(kFlameNum-1);
      FlameBeamSpin flame = new FlameBeamSpin(5.0f, 4.0f, 0.0f, 
                                              kFlameMinRadius, kFlameMaxRadius,
                                              kFlameAngSpeed, -k*ang, 2);
      flame.setColourScheme(1);
      flame.setPhysicsMode(2);
      spriteManager.addSprite(flame);
    }
    
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

  } // Room.advance()

} // class RoomA11
