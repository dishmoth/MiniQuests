/*
 *  RoomE13.java
 *  Copyright (c) 2019 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Critter;
import com.dishmoth.miniquests.game.CritterTrack;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.FloorSwitch;
import com.dishmoth.miniquests.game.Liquid;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.SnakeEgg;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;

// the room "E13"
public class RoomE13 extends Room {

  // unique identifier for this room
  public static final String NAME = "E13";
  
  // main blocks for the room
  private static final String kBlocksA[][] = { { "     11111",
                                                 "     11111",
                                                 "      1111",
                                                 "      1111",
                                                 "      1111",
                                                 "      1111",
                                                 "      1111",
                                                 "       111",
                                                 "       111",
                                                 "         1" },
                                              
                                               { "        11",
                                                 "        11",
                                                 "        11",
                                                 "       111",
                                                 "       111",
                                                 "        11",
                                                 "        11",
                                                 "         1",
                                                 "          ",
                                                 "          " },
                                              
                                               { "         1",
                                                 "         1",
                                                 "          ",
                                                 "          ",
                                                 "          ",
                                                 "          ",
                                                 "          ",
                                                 "          ",
                                                 "          ",
                                                 "          " } };
                                              
  private static final String kBlocksB[][] = { { "  0       ",
                                                 "  0       ",
                                                 "  0       ",
                                                 "000       ",
                                                 "          ",
                                                 "          ",
                                                 "          ",
                                                 "          ",
                                                 "          ",
                                                 "          " } };
                                              
  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "XL",  // dark purple
                                                  "Ll"}; // pale purple
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.UP,   2,14, "#L",1, -1, RoomE07.NAME, 2),
              new Exit(Env.LEFT, 6,14, "XL",1, -1, RoomE04.NAME, 7) };

  // details of the paths followed by enemies
  private static final CritterTrack kCritterTrack
                    = new CritterTrack(new String[]{ "     +++++",
                                                     "     +++++",
                                                     "      ++++",
                                                     "      ++++",
                                                     "      ++++",
                                                     "      ++++",
                                                     "      ++++",
                                                     "       +++",
                                                     "       +++",
                                                     "         +" }); 

  // whether the door is open yet
  private boolean mDone;
  
  // constructor
  public RoomE13() {

    super(NAME);

    mDone = false;
    
  } // constructor

  // access to the room's status
  // (note: this function may be called by room E04)
  public boolean completed() { return mDone; }
  
  // create the player at the specified entry point to the room
  // (this function should also set the camera position) 
  @Override
  public Player createPlayer(int entryPoint) {

    assert( entryPoint >= 0 && entryPoint < kExits.length );
    setPlayerAtExit(kExits[entryPoint]);
    
    mCamera.shift(0, 0, +6);
    
    return mPlayer;
    
  } // createPlayer()
  
  // create the sprites for this room
  @Override
  public void createSprites(SpriteManager spriteManager) {
    
    addBasicZone(0, 0, true, true, true, false, kExits, spriteManager);
    
    spriteManager.addSprite(new Liquid(0, 0, -1, 2));
    spriteManager.addSprite(new Liquid(0, -Room.kSize, -1, 2));

    spriteManager.addSprite( new BlockArray(kBlocksA, kBlockColours, 0,0,0) );
    spriteManager.addSprite( new BlockArray(kBlocksB, kBlockColours, 0,0,14) );
    
    if ( !mDone ) {
      kExits[1].mDoor.setClosed(true);
      spriteManager.addSprite(new FloorSwitch(2, 6, 14, "lL", "XL"));
    }
    
    spriteManager.addSprite( new SnakeEgg(7, 4, 0, 0) );
    spriteManager.addSprite( new SnakeEgg(6, 8, 0, 0) );
    spriteManager.addSprite( new SnakeEgg(9, 1, 0, 0) );
    
    spriteManager.addSprite( new SnakeEgg(8, 8, 2, 0) );
    spriteManager.addSprite( new SnakeEgg(7, 6, 2, 0) );
    spriteManager.addSprite( new SnakeEgg(9, 6, 2, 0) );
    spriteManager.addSprite( new SnakeEgg(9, 3, 2, 0) );

    Critter critters[] = { new Critter(8, 7, 2, Env.DOWN, kCritterTrack),
                           new Critter(9, 4, 2, Env.LEFT, kCritterTrack) };
    for ( Critter c : critters ) {
      c.setColour(1);
      spriteManager.addSprite(c);
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
    
    // check the switch
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();
      if ( event instanceof FloorSwitch.EventStateChange ) {
        assert(!mDone);
        mDone = true;
        FloorSwitch s = ((FloorSwitch.EventStateChange)event).mSwitch;
        spriteManager.removeSprite(s);
        kExits[1].mDoor.setClosed(false);
        Env.sounds().play(Sounds.SWITCH_ON);
        it.remove();
      }
    }
  
  } // Room.advance()

} // class RoomE13
