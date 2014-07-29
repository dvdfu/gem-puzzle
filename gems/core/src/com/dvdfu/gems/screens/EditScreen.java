package com.dvdfu.gems.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dvdfu.gems.MainGame;
import com.dvdfu.gems.abstracts.EditorBoard;
import com.dvdfu.gems.references.Res;

public class EditScreen extends AbstractScreen {
	private EditorBoard board;
	private SpriteBatch sprites;
	private Viewport viewport;
	private OrthographicCamera camera;
	private Vector3 mouse;
	private Button[] tools;
	private int boardOffsetX;
	private int boardOffsetY;
	private int timer;
	
	private class Button {
		Res.Cursors type;
		private String filename;
		private int x;
		private int y;
		private int width;
		private int height;

		private Button(int x, int y, int width, int height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		private boolean hasMouse() {
			return mouse.x > x && mouse.x < x + width && mouse.y > y && mouse.y < y + height;
		}
	}

	public EditScreen(MainGame game) {
		super(game);
	}

	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(1, 1, 1, 1);
	}

	public void resize(int width, int height) {}

	public void show() {}

	public void hide() {}

	public void pause() {}

	public void resume() {}

	public void dispose() {}
}
