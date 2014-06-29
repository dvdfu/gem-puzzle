package puzzle;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.dvdfu.puzzle.MainGame;
import com.dvdfu.puzzle.handlers.Vars;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "";
		cfg.width = Vars.screenWidth;
		cfg.height = Vars.screenHeight;
		new LwjglApplication(new MainGame(), cfg);
	}
}
