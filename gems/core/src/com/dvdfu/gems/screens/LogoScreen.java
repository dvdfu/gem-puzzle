package com.dvdfu.gems.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.dvdfu.gems.MainGame;
import com.dvdfu.gems.handlers.Input;

public class LogoScreen extends AbstractScreen {
	private int timer;

	public LogoScreen(MainGame game) {
		super(game);
		timer = 0;
	}

	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(timer / 40f, timer / 40f, timer / 40f, 1);
		timer++;
		
		if (Input.MousePressed() || timer == 100) {
			timer = 0;
			game.enterScreen(new TitleScreen(game));
		}
	}

	public void resize(int width, int height) {}

	public void show() {}

	public void hide() {}

	public void pause() {}

	public void resume() {}

	public void dispose() {}
}
