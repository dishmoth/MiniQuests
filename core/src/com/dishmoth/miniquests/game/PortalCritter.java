/*
 *  PortalCritter.java
 *  Copyright (c) 2024 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// a type of Critter affected by the PortalSight effect
public class PortalCritter extends Critter {

  // reference to the active portal sight effect
  private PortalSightBase mPortalSight = null;

  // constructor
  public PortalCritter(int x, int y, int z, int direc, Track track) {

    super(x, y, z, direc, track);

  } // constructor

  // check for Sprites we want to keep track of
  @Override
  public void observeArrival(Sprite newSprite) {

    super.observeArrival(newSprite);

    if ( newSprite instanceof PortalSightBase ) {
      assert( mPortalSight == null );
      mPortalSight = (PortalSightBase)newSprite;
    }

  } // Sprite.observeArrival()

  // keep track of Sprites that have been destroyed
  @Override
  public void observeDeparture(Sprite deadSprite) {

    if ( deadSprite instanceof PortalSightBase ) {
      mPortalSight = null;
    }

    super.observeDeparture(deadSprite);

  } // Sprite.observeDeparture()

  // advance timers, etc.
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {

    super.advance(addTheseSprites, killTheseSprites, newStoryEvents);

  } // Sprite.advance()

  // display the object
  @Override
  public void draw(EgaCanvas canvas) {

    final int x = mXPos - mCamera.xPos(),
              y = mYPos - mCamera.yPos(),
              z = getZPosStepping() - mCamera.zPos();

    CritterImage images = getCritterImage();
    EgaImage image = (mStepping ? images.getStepImage(mDirec)
                                : images.getBasicImage(mDirec));

    if ( mPortalSight == null ) {
      image.draw3D(canvas, 2*x, 2*y, z);
    } else {
      mPortalSight.draw3D(canvas, image, 2*x, 2*y, z, false);
    }

  } // Sprite.draw()

} // class PortalCritter
