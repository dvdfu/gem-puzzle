package com.dvdfu.gems.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.dvdfu.gems.Editor;
import com.dvdfu.gems.handlers.Vars;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "";
		config.width = Vars.screenWidth;
		config.height = Vars.screenHeight;
		new LwjglApplication(new Editor(), config);
	}
}
