/*
 *  Particles.java
 *  Copyright (c) 2017 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.miniquests.game;

import java.util.Iterator;
import java.util.LinkedList;

// a collection of animated particles
public class Particles extends Sprite3D {

  // active particles
  protected LinkedList<Particle> mParticles;
  
  // time remaining before the particles collide with objects
  private int mCollisionCountDown;
  
  // list of objects that the particles may collide with
  private LinkedList<Obstacle> mObstacles = new LinkedList<Obstacle>();
  
  // constructor (collisionDelay = -1 for no collisions)
  public Particles(int collisionDelay) {
    
    mParticles = new LinkedList<Particle>();
    
    assert( collisionDelay >= -1 );
    mCollisionCountDown = collisionDelay;
    
  } // constructor

  // add a particle to the set
  public void add(Particle p) { mParticles.add(p); }
  
  // check for Sprites we want to keep track of
  @Override
  public void observeArrival(Sprite newSprite) { 

    super.observeArrival(newSprite);
    
    if ( newSprite instanceof Obstacle ) {
      if ( newSprite instanceof Barrier ) {
        if ( !((Barrier)newSprite).blocks(this) ) return;
      }
      mObstacles.add((Obstacle)newSprite);
    }
    
  } // Sprite.observeArrival()
  
  // keep track of Sprites that have been destroyed
  @Override
  public void observeDeparture(Sprite deadSprite) {

    if ( deadSprite instanceof Obstacle ) {
      mObstacles.remove(deadSprite);
    }
    
    super.observeDeparture(deadSprite);
    
  } // Sprite.observeDeparture()
  
  // move and animate the particles
  @Override
  public void advance(LinkedList<Sprite>     addTheseSprites,
                      LinkedList<Sprite>     killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {

    for ( Iterator<Particle> it = mParticles.iterator() ; it.hasNext() ; ) {
      Particle p = it.next();
      p.advance();
      if ( deadParticle(p) ) it.remove();
    }

    if ( mCollisionCountDown > 0 ) mCollisionCountDown -= 1;
    
  } // Sprite.advance()

  // whether the particle is finished with (due to old age or collision)
  protected boolean deadParticle(Particle p) {
    
    if ( !p.alive() ) return true;
    if ( mCollisionCountDown != 0 ) return false;
    for ( Obstacle ob : mObstacles ) {
      if ( p.hits(ob) ) return true;
    }  
    return false;
      
  } // deadParticle()
  
  // display the particles
  @Override
  public void draw(EgaCanvas canvas) {

    for ( Particle p : mParticles ) p.draw(canvas, mCamera);
    
  } // Sprite.draw()

} // class Particles
