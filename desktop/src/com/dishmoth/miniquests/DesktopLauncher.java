package com.dishmoth.miniquests;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setForegroundFPS(60);
        config.setTitle("Mini Quests");
        config.setWindowedMode(400, 300);
        config.setWindowIcon(FileType.Internal, "DesktopIcon128.png",
                             "DesktopIcon32.png", "DesktopIcon16.png");
        new Lwjgl3Application(new MiniQuestsGame(), config);
	}
}
