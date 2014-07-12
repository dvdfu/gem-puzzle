package com.dvdfu.gems.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.dvdfu.gems.MainGame;
import com.dvdfu.gems.handlers.Res;

public class DesktopLauncher {
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "";
		config.width = Res.screenWidth;
		config.height = Res.screenHeight;
		new LwjglApplication(new MainGame(), config);
	}
}
