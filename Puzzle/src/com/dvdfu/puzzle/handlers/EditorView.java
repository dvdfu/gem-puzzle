package com.dvdfu.puzzle.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dvdfu.puzzle.entities.Block;
import com.dvdfu.puzzle.entities.Board;
import com.dvdfu.puzzle.entities.Special;

public class EditorView {
	private Board board;
	private AssetManager assets = new AssetManager();
	private SpriteBatch sprites;
	private ShapeRenderer shapes;
	private Viewport viewport;
	private OrthographicCamera camera;
	private Vector3 mouse;
	private int boardOffsetX;
	private int boardOffsetY;

	public EditorView(Board board) {
		this.board = board;
		boardOffsetX = (Gdx.graphics.getWidth() - board.getWidth() * Vars.fullSize) / 2;
		boardOffsetY = (Gdx.graphics.getHeight() - board.getHeight() * Vars.fullSize) / 2;
		loadAssets();

		sprites = new SpriteBatch();
		shapes = new ShapeRenderer();

		viewport = new ScreenViewport();
		mouse = new Vector3();
		camera = (OrthographicCamera) viewport.getCamera();
		camera.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);
	}

	public void dispose() {
		assets.dispose();
		sprites.dispose();
		shapes.dispose();
	}

	private void loadAssets() {
		assets.load("img/block.png", Texture.class);
		assets.load("img/fixed.png", Texture.class);
		assets.load("img/gem.png", Texture.class);
		assets.load("img/gemsC.png", Texture.class);
		assets.load("img/gemsD.png", Texture.class);
		assets.load("img/gemsL.png", Texture.class);
		assets.load("img/gemsR.png", Texture.class);
		assets.load("img/gemsU.png", Texture.class);
		assets.load("img/fall.png", Texture.class);
		assets.load("img/path.png", Texture.class);
		assets.load("img/drops.png", Texture.class);
		assets.load("img/water.png", Texture.class);
		assets.load("img/waterF.png", Texture.class);
		assets.load("img/grid.png", Texture.class);
		assets.load("img/sparkle1.png", Texture.class);
		assets.load("img/dirt1.png", Texture.class);
		assets.load("img/dust1.png", Texture.class);
		assets.load("img/bomb.png", Texture.class);
		assets.load("img/gate.png", Texture.class);
		assets.load("img/button.png", Texture.class);
		assets.load("aud/select.wav", Sound.class);
		assets.load("aud/deselect.wav", Sound.class);
		assets.load("aud/remove.wav", Sound.class);
		assets.load("aud/splash.mp3", Sound.class);
		assets.load("aud/break.mp3", Sound.class);
		assets.finishLoading();
	}

	public void update() {
		/* resets screen and projects sprite batch, shape renderer, mouse and camera based on the screen */
		Gdx.gl.glClearColor(0.3f, 0.25f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (Gdx.input.justTouched() && board.select()) assets.get("aud/select.wav", Sound.class).play();
		if (!Gdx.input.isTouched() && board.unselect()) assets.get("aud/deselect.wav", Sound.class).play();
		mouse.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		sprites.setProjectionMatrix(camera.combined);
		shapes.setProjectionMatrix(camera.combined);
		camera.unproject(mouse);

		/* sends mouse information to the board and plays appropriate sound effects */
		int cursorX = (int) (mouse.x - boardOffsetX) / Vars.fullSize;
		int cursorY = board.getHeight() - 1 - (int) (mouse.y - boardOffsetY) / Vars.fullSize;
		board.setCursor(cursorX, cursorY);
	}

	public void draw() {
		sprites.begin();
		drawGridUnder();
		drawBlocks();
		drawGridOver();
		sprites.end();
		drawCursor();
	}

	public void resize(int width, int height) {
		int zoomW = height / Vars.fullSize / board.getHeight();
		int zoomH = width / Vars.fullSize / board.getWidth();
		camera.zoom = 1f / Math.min(zoomW, zoomH);
		Gdx.gl20.glLineWidth(1 / camera.zoom);
		viewport.update(width, height);
	}

	private void setAlpha(float alpha) {
		Color c = sprites.getColor();
		sprites.setColor(c.r, c.g, c.b, alpha);
	}

	private void drawGridUnder() {
		for (int i = 0; i < board.getWidth(); i++) {
			for (int j = 0; j < board.getHeight(); j++) {
				int drawX = boardOffsetX + i * Vars.fullSize;
				int drawY = boardOffsetY + (board.getHeight() - 1 - j) * Vars.fullSize;
				drawBlock("grid", drawX, drawY);
				Special special = board.getSpecial()[i][j];
				if (special != null) {
					if (special.path) drawBlock("path", drawX, drawY);
					else if (special.button) {
						if (special.toggled) drawBlock("button", drawX, drawY);
						else drawBlock("button", drawX, drawY);
					}
				}
			}
		}
	}

	private void drawBlocks() {
		/* draw blocks at their position and transparency based on their properties and buffer timers */
		for (int i = 0; i < board.getWidth(); i++) {
			for (int j = 0; j < board.getHeight(); j++) {
				int drawX = boardOffsetX + i * Vars.fullSize;
				int drawY = boardOffsetY + (board.getHeight() - 1 - j) * Vars.fullSize;
				Block block = board.getGrid()[i][j];
				if (block.move) {
					if (block.bomb) drawBlock("bomb", drawX, drawY);
					else drawBlock("block", drawX, drawY);
				} else drawBlock("fixed", drawX, drawY);

				if (block.gemC) drawBlock("gemsC", drawX, drawY);
				else {
					if (block.gemD) drawBlock("gemsD", drawX, drawY);
					if (block.gemU) drawBlock("gemsU", drawX, drawY);
					if (block.gemL) drawBlock("gemsL", drawX, drawY);
					if (block.gemR) drawBlock("gemsR", drawX, drawY);
				}
				if (block.fall) drawBlock("fall", drawX, drawY);
				setAlpha(1);
			}
		}
	}

	private void drawBlock(String filename, int x, int y) {
		sprites.draw(assets.get("img/" + filename + ".png", Texture.class), x, y, Vars.fullSize, Vars.fullSize);
	}

	private void drawGridOver() {
		for (int i = 0; i < board.getWidth(); i++) {
			for (int j = 0; j < board.getHeight(); j++) {
				int drawX = boardOffsetX + i * Vars.fullSize;
				int drawY = boardOffsetY + (board.getHeight() - 1 - j) * Vars.fullSize;
				Special special = board.getSpecial()[i][j];
				if (special != null) {
					if (special.hazard) {
						if (board.gridValid(i, j - 1) && board.getSpecial()[i][j - 1] == null) drawBlock("water", drawX, drawY);
						else drawBlock("waterF", drawX, drawY);
					} else if (special.gate && special.toggled) drawBlock("gate", drawX, drawY);
				}
			}
		}
	}

	private void drawCursor() {
		shapes.begin(ShapeType.Line);
		if (board.isSelected()) shapes.setColor(Color.RED);
		else shapes.setColor(Color.BLUE);
		int cursorX = boardOffsetX + board.getCursorX() * Vars.fullSize;
		int cursorY = boardOffsetY + (board.getHeight() - 1 - board.getCursorY()) * Vars.fullSize;
		shapes.rect(cursorX, cursorY, Vars.fullSize, Vars.fullSize);
		shapes.end();
	}
}