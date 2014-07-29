package com.dvdfu.gems.screens;

import com.dvdfu.gems.MainGame;
import com.dvdfu.gems.references.Input;

public class PauseScreen extends AbstractScreen {

	public PauseScreen(MainGame game) {
		super(game);
	}

	public void render(float delta) {
		if (Input.KeyPressed(Input.SPACEBAR)) {
			game.exitScreen();
		}
	}

	public void resize(int width, int height) {}

	public void show() {}

	public void hide() {}

	public void pause() {}

	public void resume() {}

	public void dispose() {}
}
