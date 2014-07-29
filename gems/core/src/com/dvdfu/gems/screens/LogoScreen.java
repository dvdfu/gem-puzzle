package com.dvdfu.gems.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dvdfu.gems.MainGame;
import com.dvdfu.gems.references.Assets;
import com.dvdfu.gems.references.Input;
import com.dvdfu.gems.visuals.Label;

public class LogoScreen extends AbstractScreen {
	private SpriteBatch sprites;
	private Label title;
	private int timer;
	private int length;

	public LogoScreen(MainGame game) {
		super(game);
		sprites = new SpriteBatch();
		title = new Label("dvdfu @ twitter, github, tumblr");
		timer = 0;
		length = 200;
	}

	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(1, 0.4f, 0.3f, 1);
		
		sprites.begin();
		title.drawC(sprites, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		if (timer < 40) {
			Color c = sprites.getColor();
			sprites.setColor(c.r, c.g, c.b, (40 - timer) / 40f);
		} else if (length - timer < 40) {
			Color c = sprites.getColor();
			sprites.setColor(c.r, c.g, c.b, (40 - length + timer) / 40f);
		} else {
			Color c = sprites.getColor();
			sprites.setColor(c.r, c.g, c.b, 0);
		}
		sprites.draw(Assets.path, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		sprites.end();
		if (Input.MousePressed() || timer == length) {
			Gdx.gl.glClearColor(0, 0, 0, 1);
			game.changeScreen(new TitleScreen(game));
		}
		timer++;
	}

	public void resize(int width, int height) {}

	public void show() {}

	public void hide() {}

	public void pause() {}

	public void resume() {
		timer = 0;
	}

	public void dispose() {
		sprites.dispose();
	}
}
