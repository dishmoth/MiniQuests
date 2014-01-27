/*
 *  MainGdx.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

// launcher for libgdx desktop version
public class Main {
  
  // start here
	public static void main(String[] args) {
	  
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Mini Quests";
		cfg.useGL20 = false;
		
		cfg.width = 480; cfg.height = 320;
		
		new LwjglApplication(new MainGame(), cfg);
		
	} // main()
	
} // class Main
