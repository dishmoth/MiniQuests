/*
 *  RoomE05.java
 *  Copyright (c) 2019 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.rooms;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import com.dishmoth.miniquests.game.Barrier;
import com.dishmoth.miniquests.game.BlockArray;
import com.dishmoth.miniquests.game.Critter;
import com.dishmoth.miniquests.game.CritterTrack;
import com.dishmoth.miniquests.game.Env;
import com.dishmoth.miniquests.game.Exit;
import com.dishmoth.miniquests.game.Liquid;
import com.dishmoth.miniquests.game.Player;
import com.dishmoth.miniquests.game.Room;
import com.dishmoth.miniquests.game.Sounds;
import com.dishmoth.miniquests.game.Sprite;
import com.dishmoth.miniquests.game.SpriteManager;
import com.dishmoth.miniquests.game.StoryEvent;
import com.dishmoth.miniquests.game.ZoneSwitch;

// the room "E05"
public class RoomE05 extends Room {

  // time for the next critter
  private static final int kCritterDelay = 40;

  // unique identifier for this room
  public static final String NAME = "E05";
  
  // main blocks for the room
  private static final String kBlocks[][] = { { " 000      ",
                                                " 0        ",
                                                " 0        ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "         0",
                                                "         0",
                                                "       000",
                                                "          " },
                                              
                                              { " 00       ",
                                                " 0        ",
                                                " 0        ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "         0",
                                                "       000",
                                                "          " },
                                              
                                              { " 0        ",
                                                " 0        ",
                                                " 0        ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "       000",
                                                "          " },
                                              
                                              { "          ",
                                                " 0        ",
                                                " 0        ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "          ",
                                                "       00 ",
                                                "          " },
                                              
                                              { "          ",
                                                "          ",
                                                " 111111100",
                                                " 1  1  1  ",
                                                " 1  1  1  ",
                                                " 1111111  ",
                                                " 1  1  1  ",
                                                " 1  1  1  ",
                                                " 1111111  ",
                                                " 0        " } };

  // different block colours (corresponding to '0', '1', '2', etc)
  private static final String kBlockColours[] = { "U2",   // green
                                                  "U2" }; // green
  
  // details of exit/entry points for the room 
  private static final Exit kExits[] 
          = { new Exit(Env.RIGHT, 7,1, "#2",1, -1, RoomE02.NAME, 1),
              new Exit(Env.DOWN,  1,1, "U2",1, -1, RoomE03.NAME, 0) };

  // details of the paths followed by enemies
  private static final CritterTrack kCritterTracks[] = {
                           new CritterTrack(new String[]{ " +++      ",
                                                          " +        ",
                                                          " +        ",
                                                          " +        ",
                                                          " +        ",
                                                          " +        ",
                                                          " +       +",
                                                          " +       +",
                                                          " +++++++++",
                                                          "          " }),
                           new CritterTrack(new String[]{ " +++      ",
                                                          " +        ",
                                                          " +        ",
                                                          " +        ",
                                                          " +        ",
                                                          " ++++     ",
                                                          "    +    +",
                                                          "    +    +",
                                                          "    ++++++",
                                                          "          " }),
                           new CritterTrack(new String[]{ " +++      ",
                                                          " +        ",
                                                          " ++++     ",
                                                          "    +     ",
                                                          "    +     ",
                                                          "    +     ",
                                                          "    +    +",
                                                          "    +    +",
                                                          "    ++++++",
                                                          "          " }),
                           new CritterTrack(new String[]{ " +++      ",
                                                          " +        ",
                                                          " +        ",
                                                          " +        ",
                                                          " +        ",
                                                          " +++++++  ",
                                                          "       + +",
                                                          "       + +",
                                                          "       +++",
                                                          "          " }),
                           new CritterTrack(new String[]{ " +++      ",
                                                          " +        ",
                                                          " ++++     ",
                                                          "    +     ",
                                                          "    +     ",
                                                          "    ++++  ",
                                                          "       + +",
                                                          "       + +",
                                                          "       +++",
                                                          "          " }),
                           new CritterTrack(new String[]{ " +++      ",
                                                          " +        ",
                                                          " +++++++  ",
                                                          "       +  ",
                                                          "       +  ",
                                                          "       +  ",
                                                          "       + +",
                                                          "       + +",
                                                          "       +++",
                                                          "          " }) };
  
  // whether the room is complete yet
  private boolean mDone;
  
  // time until the next critter spawns
  private int mTimer;

  // which type of critter to spawn next
  private LinkedList<Integer> mCritterType = new LinkedList<Integer>(
                                               Arrays.asList(0, 0, 0, 1, 1));
  private int mNextCritterType;
  
  // which type of track to follow next
  private LinkedList<Integer> mCritterRoute = new LinkedList<Integer>(
                                           Arrays.asList(-1, -1, 0, 0, 1, 1));
  private int mNextCritterRoute;
  
  // constructor
  public RoomE05() {

    super(NAME);

    mDone = false;
    
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
    
    spriteManager.addSprite( new BlockArray(kBlocks, kBlockColours, 0,0,-7) );
    
    addBasicWalls(kExits, spriteManager);

    spriteManager.addSprite(new Liquid(0,0,-1, 2));

    spriteManager.addSprite(new Barrier(1, 9, 2, Player.class));
    spriteManager.addSprite(new Barrier(9, 1, 2, Player.class));
    
    if ( !mDone ) {
      Env.shuffle(mCritterType);
      Critter critters[] = { new Critter(1,5,1, Env.UP,   kCritterTracks[0]),
                             new Critter(7,7,1, Env.UP,   kCritterTracks[5]),
                             new Critter(4,1,1, Env.LEFT, kCritterTracks[1]),
                             new Critter(7,1,1, Env.LEFT, kCritterTracks[2]) };
      for ( int k = 0 ; k < critters.length ; k++ ) {
        if ( mCritterType.get(k) == 0 ) {
          critters[k].easilyKilled(true);
          critters[k].setColour(3);
        } else {
          critters[k].setStunTime(0);
          critters[k].setColour(2);
        }
        spriteManager.addSprite(critters[k]);
      }

      spriteManager.addSprite(new ZoneSwitch(1, 0));
    }
    
    Env.shuffle(mCritterType);
    mNextCritterType = 0;
    
    Env.shuffle(mCritterRoute);
    mNextCritterRoute = 0;
    
    mTimer = 1;
    
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
    
    // process the story event list
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();
      
      if ( event instanceof ZoneSwitch.EventStateChange ) {
        if ( !mDone ) {
          mDone = true;
          for ( Sprite sp : spriteManager.list() ) {
            if ( sp instanceof Critter ) {
              ((Critter)sp).setSilent(true);
              ((Critter)sp).destroy(-1);
            }
          }
          Env.sounds().play(Sounds.CRITTER_DEATH);
          Env.sounds().play(Sounds.CRITTER_DEATH, 2);
        }
        it.remove();
      }
      
    } // for (event)

    // remove critters at the end of the track
    for ( Sprite sp : spriteManager.list() ) {
      if ( sp instanceof Critter ) {
        Critter cr = (Critter)sp;
        if ( !cr.isActing() && cr.getXPos() == 3 && cr.getYPos() == 9 ) {
          spriteManager.removeSprite(sp);
          break;
        }
      }
    }

    // add critters at the start of the track
    if ( !mDone && --mTimer == 0 ) {
      final int route = mCritterRoute.get(mNextCritterRoute);
      if ( ++ mNextCritterRoute >= mCritterRoute.size() ) {
        Env.shuffle(mCritterRoute);
        mNextCritterRoute = 0;
      }
      final int track = ( route == -1 ? 0
                        : route == +1 ? 5 
                                      : Env.randomInt(1, 4) );
      Critter critter = new Critter(9,3,-7, Env.DOWN, kCritterTracks[track]);
      final int type = mCritterType.get(mNextCritterType);
      if ( ++mNextCritterType >= mCritterType.size() ) {
        Env.shuffle(mCritterType);
        mNextCritterType = 0;
      }
      if ( type == 0 ) {
        critter.easilyKilled(true);
        critter.setColour(3);
      } else {
        critter.setStunTime(0);
        critter.setColour(2);
      }
      spriteManager.addSprite(critter);
      mTimer = kCritterDelay;
    }
    
  } // Room.advance()

} // class RoomE05
