/*
 *  MainActivity.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests;

import android.os.Bundle;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

// launcher for android version of the game
public class MainActivity extends AndroidApplication {

  // start here
  @Override
  public void onCreate(Bundle savedInstanceState) {
    
    super.onCreate(savedInstanceState);
        
    AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
    cfg.useGL20 = false;
        
    initialize(new MainGame(), cfg);
    
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    
  } // AndroidActivity.onCreate()

} // class MainActivity