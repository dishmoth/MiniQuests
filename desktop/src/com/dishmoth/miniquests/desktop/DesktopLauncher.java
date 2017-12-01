package com.dishmoth.miniquests.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.dishmoth.miniquests.MiniQuestsGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Mini Quests";
		config.width = 400;
		config.height = 300;
    config.addIcon("DesktopIcon128.png", FileType.Internal);
    config.addIcon("DesktopIcon32.png", FileType.Internal);
    config.addIcon("DesktopIcon16.png", FileType.Internal);
		new LwjglApplication(new MiniQuestsGame(), config);
	}
}
