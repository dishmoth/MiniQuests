/*
 *  ResourcesGdx.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import com.dishmoth.miniquests.game.EgaImage;
import com.dishmoth.miniquests.game.EgaTools;
import com.dishmoth.miniquests.game.Resources;

// manager for graphics, sounds, etc.
public class ResourcesGdx implements Resources {

  // path for data files
  private static final String kResourcePath = "data/";
  
  // return an EgaImage from file (exception if not known)
  public EgaImage loadEgaImage(String resourceName) {

    Pixmap image = new Pixmap( Gdx.files.internal(kResourcePath + resourceName) );
    
    final int width  = image.getWidth(),
              height = image.getHeight();
    
    byte pixels[] = new byte[width*height];
    int index = 0;
    for ( int y = 0 ; y < height ; y++ ) {
      for ( int x = 0 ; x < width ; x++ ) {
        final int rgba = image.getPixel(x, y);
        pixels[index] = (byte)EgaTools.rgbaToEga(rgba);
        index++;
      }
    }
    
    image.dispose();
    
    final int refXPos = 0,
              refYPos = 0;
    final float depth = 0.0f;
    
    return new EgaImage(refXPos, refYPos, width, height, pixels, depth);
    
  } // Resources.loadEgaImage()

  // simple wrapper for gdx texture loading
  static public Texture loadTexture(String textureName) {
    
    return new Texture( Gdx.files.internal(kResourcePath + textureName) );
    
  } // loadTexture()
  
} // class ResourcesGdx
