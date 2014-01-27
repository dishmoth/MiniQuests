/*
 *  RecolourTool.java
 *  Copyright Simon Hern 2013
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

import com.dishmoth.miniquests.game.EgaCanvas;
import com.dishmoth.miniquests.game.EgaTools;
import com.dishmoth.miniquests.game.Env;

// utility to tweak colours on the fly
public class RecolourTool implements KeyListener, MouseListener {

  // reference to the game component
  private Component mOwner;
  
  // a pixel has been clicked (or -1)
  private int mPixelX,
              mPixelY;
  
  // pixel colour currently being tweaked
  private byte mCurrentColour;
  
  // colour values to replace (or -1)
  private byte mColourMap[];
  
  // constructor
  public RecolourTool(Component owner) {
    
    Env.debug("RecolourTool active");
    
    mOwner = owner;
    
    mOwner.addKeyListener(this);
    mOwner.addMouseListener(this);
    
    mPixelX = mPixelY = -1;
    mCurrentColour = -1;

    mColourMap = new byte[64];
    Arrays.fill(mColourMap, (byte)-1);
  
  } // constructor
  
  // change image colours
  public void recolour(EgaCanvas screen) {

    byte pixels[] = screen.pixels();
    
    if ( mPixelX != -1 && mPixelY != -1 ) {
      mCurrentColour = pixels[ mPixelY*screen.width() + mPixelX ];
      mPixelX = mPixelY = -1;
      Env.debug("Recolour: colour selected '" 
                + EgaTools.recodePixel(mCurrentColour) + "'");
    }
    
    for ( int k = 0 ; k < pixels.length ; k++ ) {
      byte p = mColourMap[ pixels[k] ];
      if ( p != -1 ) pixels[k] = p;
    }
    
  } // recolour()

  // implementation of KeyListener interface
  public void keyTyped(KeyEvent e) {

    if ( mCurrentColour == -1 ) return;
    
    byte newColour = EgaTools.decodePixel(e.getKeyChar());
    if ( newColour == -1 ) return;
    
    mColourMap[mCurrentColour] = (newColour==mCurrentColour) ? -1 : newColour;

    String str = "";
    for ( int k = 0 ; k < 64 ; k++ ) {
      if ( mColourMap[k] != -1 ) {
        if ( str.length() > 0 ) str += ", ";
        str += "'" + EgaTools.recodePixel((byte)k)
             + "'->'" + EgaTools.recodePixel(mColourMap[k]) + "'";
      }
    }
    if ( str.length() > 0 ) {
      Env.debug("Recolour: change " + str);
    }
    
  } // KeyListener.keyTyped()

  // implementation of KeyListener interface
  public void keyPressed(KeyEvent e) {
  } // KeyListener.keyPressed()

  // implementation of KeyListener interface
  public void keyReleased(KeyEvent e) {
  } // KeyListener.keyReleased()

  // implementation of MouseListener interface
  public void mouseClicked(MouseEvent e) {

    if ( e.getButton() != 3 ) return;
    
    final int screenScale = mOwner.getWidth()/Env.screenWidth();
    int x = e.getX() / screenScale,
        y = e.getY() / screenScale;

    if ( x < 0 || x >= Env.screenWidth() || 
         y < 0 || y >= Env.screenHeight() ) return;
    
    mPixelX = x;
    mPixelY = y;
    
  } // MouseListener.mouseClicked()

  // implementation of MouseListener interface
  public void mousePressed(MouseEvent e) {
  } // MouseListener.mousePressed()

  // implementation of MouseListener interface
  public void mouseReleased(MouseEvent e) {
  } // MouseListener.mouseReleased()

  // implementation of MouseListener interface
  public void mouseEntered(MouseEvent e) {
  } // MouseListener.mouseEntered()

  // implementation of MouseListener interface
  public void mouseExited(MouseEvent e) {
  } // MouseListener.mouseExited()
  
} // class RecolourTool
