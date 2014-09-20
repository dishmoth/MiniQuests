/*
 *  Fountain.java
 *  Copyright Simon Hern 2014
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.Arrays;
import java.util.LinkedList;

// a water feature 
public class Fountain extends Sprite3D implements Obstacle {

  // particle dynamics
  private static final float kSpeedXY = 0.05f,
                             kSpeedZ  = 0.10f,
                             kGravity = -0.01f;

  // life of a particle
  private static final int kLifeTimeMin = 35,
                           kLifeTimeMax = 40;
  
  // colours
  private static final byte kColours[] = { 11, 59, 63 }; // 25 }; 
  
  // new particles per frame
  private static final float kParticleRate = 2.0f;

  // details of the image
  private static final int   kWidth      = 4,
                             kHeight     = 4;
  private static final int   kRefXPos    = 1,
                             kRefYPos    = 2;
  private static final float kBasicDepth = -0.003f,
                             kSideDepth  = -0.003f,
                             kFrontDepth = -1.003f;

  // data for the basic image, two frames
  private static final String kPixels = " 77 " 
                                      + " 7u "
                                      + "77uu"
                                      + " 7u ";
  
  // images in different colour schemes
  private static final EgaImage kImage;
  static {
    
    float depths[] = new float[kWidth*kHeight];
    Arrays.fill(depths, kBasicDepth);
    depths[kRefYPos*kWidth]   = kSideDepth;
    depths[kRefYPos*kWidth+3] = kSideDepth;
    depths[(kRefYPos+1)*kWidth+1] = kFrontDepth;
    depths[(kRefYPos+1)*kWidth+2] = kFrontDepth;
    
    kImage = new EgaImage(kRefXPos, kRefYPos,
                          kWidth, kHeight,
                          kPixels, depths);
    
  } // static
  
  // position of fountain base
  final private int mXPos,
                    mYPos,
                    mZPos;
  
  // water particles
  final private Particles mParticles;
  
  // constructor
  public Fountain(int xPos, int yPos, int zPos, boolean on) {
    
    mXPos = xPos;
    mYPos = yPos;
    mZPos = zPos;

    if ( on ) {
      mParticles = new Particles(-1);
      warmUp(kLifeTimeMin);
    } else {
      mParticles = null;
    }
    
  } // constructor

  // run the fountain for a bit
  private void warmUp(int warmUpTime) {

    assert( warmUpTime > 0 );
    for ( int k = 0 ; k < warmUpTime ; k++ ) {
      advance(null, null, null);
    }
        
  } // warmUp()
  // maintain a reference to the game's Camera sprite
  @Override
  public void observeArrival(Sprite newSprite) { 

    super.observeArrival(newSprite);
    if ( newSprite instanceof Camera ) {
      if ( mParticles != null ) mParticles.observeArrival(newSprite);
    }
    
  } // Sprite.observeArrival()
  
  // when a Sprite leaves the game it is forgotten about by all other Sprites
  @Override
  public void observeDeparture(Sprite deadSprite) {
    
    super.observeDeparture(deadSprite);
    if ( deadSprite instanceof Camera ) {
      if ( mParticles != null ) mParticles.observeDeparture(deadSprite);
    }
  
  } // Sprite.observeDeparture()  

  // whether the player can stand at the specified position
  public boolean isPlatform(int x, int y, int z) {

    return false;
    
  } // Obstacle.isPlatform()

  // whether there is space at the specified position
  public boolean isEmpty(int x, int y, int z) {

    if ( x == mXPos && y == mYPos && z >= mZPos && z <= mZPos+3 ) return false;
    return true;
  
  } // Obstacle.isEmpty()

  // whether the position is outside of the game world
  public boolean isVoid(int x, int y, int z) { 

    return false;
    
  } // Obstacle.isVoid()

  // create a new particle
  private void addParticle() {

    float x = mXPos;
    float y = mYPos;
    float z = mZPos + kHeight;

    float ang = 2*(float)Math.PI*Env.randomFloat();
    float xVel = kSpeedXY*(float)Math.cos(ang);
    float yVel = kSpeedXY*(float)Math.sin(ang);
    float zVel = kSpeedZ;
    
    Particle particle = new Particle(x, y, z, xVel, yVel, zVel, 
                                     kGravity, 
                                     Env.randomInt(kLifeTimeMin, kLifeTimeMax),
                                     kColours[Env.randomInt(kColours.length)]);
    mParticles.add(particle);
    
  } // addParticle()
  
  // animate the water
  @Override
  public void advance(LinkedList<Sprite> addTheseSprites,
                      LinkedList<Sprite> killTheseSprites,
                      LinkedList<StoryEvent> newStoryEvents) {

    if ( mParticles == null ) return;
    
    float numParticles = kParticleRate;
    while ( numParticles > 1.0f ) {
      addParticle();
      numParticles--;
    }
    if ( Env.randomFloat() < numParticles ) addParticle();
    
    mParticles.advance(null, null, null);
    
  } // Sprite.advance()

  // display the object
  @Override
  public void draw(EgaCanvas canvas) {

    final int xPos = mXPos - mCamera.xPos(),
              yPos = mYPos - mCamera.yPos(),
              zPos = mZPos - mCamera.zPos();

    kImage.draw3D(canvas, 2*xPos, 2*yPos, zPos);

    if ( mParticles != null ) mParticles.draw(canvas);
    
  } // Sprite.draw()

} // class Fountain
