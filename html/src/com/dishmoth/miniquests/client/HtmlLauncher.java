package com.dishmoth.miniquests.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.dishmoth.miniquests.MiniQuestsGame;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(400, 300);
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new MiniQuestsGame();
        }
}