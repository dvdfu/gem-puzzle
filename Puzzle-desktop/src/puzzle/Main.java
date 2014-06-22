package puzzle;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.dvdfu.puzzle.MainGame;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Puzzle";
		cfg.width = 360;
		cfg.height = 640;
		cfg.resizable = false;
		new LwjglApplication(new MainGame(), cfg);
	}
}
