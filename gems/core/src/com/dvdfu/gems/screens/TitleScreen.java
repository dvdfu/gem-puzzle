package com.dvdfu.gems.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.dvdfu.gems.MainGame;
import com.dvdfu.gems.handlers.Input;

public class TitleScreen extends AbstractScreen {
	public TitleScreen(MainGame game) {
		super(game);
	}

	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(0.2f, 0.25f, 0.3f, 1);

		if (Input.MousePressed()) {
			game.enterScreen(new PlayScreen(game));
		}
	}

	public void resize(int width, int height) {}

	public void show() {}

	public void hide() {}

	public void pause() {}

	public void resume() {}

	public void dispose() {}
}
