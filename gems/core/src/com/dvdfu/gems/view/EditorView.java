package com.dvdfu.gems.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dvdfu.gems.handlers.Vars;
import com.dvdfu.gems.model.Board;
import com.dvdfu.gems.model.EditorBlock;
import com.dvdfu.gems.model.EditorBoard;
import com.dvdfu.gems.model.Special;

public class EditorView implements Screen {
	private EditorBoard board;
	private AssetManager assets = new AssetManager();
	private SpriteBatch sprites;
	private Viewport viewport;
	private OrthographicCamera camera;
	private Vector3 mouse;
	private int boardOffsetX;
	private int boardOffsetY;
	private int timer;

	public EditorView(EditorBoard board) {
		setBoard(board);
		loadAssets();
		sprites = new SpriteBatch();
		viewport = new ScreenViewport();
		mouse = new Vector3();
		camera = (OrthographicCamera) viewport.getCamera();
		camera.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);
		timer = 0;
	}

	public void dispose() {
		assets.dispose();
		sprites.dispose();
	}

	public void setBoard(EditorBoard board) {
		this.board = board;
		boardOffsetX = (Gdx.graphics.getWidth() - board.getWidth() * Vars.fullSize) / 2;
		boardOffsetY = (Gdx.graphics.getHeight() - board.getHeight() * Vars.fullSize) / 2;
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
		assets.load("img/cursor.png", Texture.class);
		assets.load("img/cursorSelect.png", Texture.class);
		assets.load("img/dot.png", Texture.class);
		assets.load("img/cracks.png", Texture.class);
		assets.load("img/fire.png", Texture.class);
		assets.load("img/fire2.png", Texture.class);
		assets.load("img/blockstatic.png", Texture.class);
		assets.load("aud/select.wav", Sound.class);
		assets.load("aud/deselect.wav", Sound.class);
		assets.load("aud/remove.wav", Sound.class);
		assets.load("aud/splash.mp3", Sound.class);
		assets.load("aud/break.mp3", Sound.class);
		assets.finishLoading();
	}

	public void update(float x, float y) {
		/* resets screen and projects sprite batch, shape renderer, mouse and camera based on the screen */
		mouse.set(x, y, 0);
		sprites.setProjectionMatrix(camera.combined);
		camera.unproject(mouse);

		/* sends mouse information to the board and plays appropriate sound effects */
		int cursorX = (int) (mouse.x - boardOffsetX) / Vars.fullSize;
		int cursorY = board.getHeight() - 1 - (int) (mouse.y - boardOffsetY) / Vars.fullSize;
		board.setCursor(cursorX, cursorY);

	}

	public void resize(int width, int height) {
		int zoomW = height / Vars.fullSize / board.getHeight();
		int zoomH = width / Vars.fullSize / board.getWidth();
		camera.zoom = 1f / Math.min(zoomW, zoomH);
		Gdx.gl20.glLineWidth(2 / camera.zoom);
		viewport.update(width, height);
	}

	private void setAlpha(float alpha) {
		Color c = sprites.getColor();
		sprites.setColor(c.r, c.g, c.b, alpha);
	}

	private void drawBlocks() {
		/* draw blocks at their position and transparency based on their properties and buffer timers */
		for (int i = 0; i < board.getWidth(); i++) {
			for (int j = 0; j < board.getHeight(); j++) {
				int drawX = boardOffsetX + i * Vars.fullSize;
				int drawY = boardOffsetY + (board.getHeight() - 1 - j) * Vars.fullSize;
				EditorBlock block = board.getGrid()[i][j];
				Special special = board.getSpecial()[i][j];
				drawBlock("grid", drawX, drawY);
				if (block != null) {
					if (block.move) {
						if (block.bomb) drawBlock("bomb", drawX, drawY);
						else drawBlock("block", drawX, drawY);
					} else {
						if (block.active) drawBlock("fixed", drawX, drawY);
						else drawBlock("blockstatic", drawX, drawY);
					}

					if (block.gemC) drawBlock("gemsC", drawX, drawY);
					else {
						if (block.gemD) drawBlock("gemsD", drawX, drawY);
						if (block.gemU) drawBlock("gemsU", drawX, drawY);
						if (block.gemL) drawBlock("gemsL", drawX, drawY);
						if (block.gemR) drawBlock("gemsR", drawX, drawY);
					}
					if (block.fall) drawBlock("fall", drawX, drawY);
				}
				if (special != null) {
					if (block != null) setAlpha(0.5f);
					if (special.path) drawBlock("path", drawX, drawY);
					else if (special.button) {
						if (special.toggled) drawBlock("button", drawX, drawY);
						else drawBlock("button", drawX, drawY);
					} else if (special.gate && special.toggled) drawBlock("gate", drawX, drawY);
					else if (special.water) {
						drawBlock("waterF", drawX, drawY);
						if (board.gridValid(i, j - 1) && board.getSpecial()[i][j - 1] == null) drawBlock("water", drawX, drawY
							+ Vars.fullSize);
					}
					setAlpha(1);
				}
			}
		}
	}

	private void drawBlock(String filename, int x, int y) {
		sprites.draw(assets.get("img/" + filename + ".png", Texture.class), x, y, Vars.fullSize, Vars.fullSize);
	}

	private void drawCursor() {
		int cx = board.getCursorX();
		int cy = board.getCursorY();
		int cursorX = boardOffsetX + cx * Vars.fullSize;
		int cursorY = boardOffsetY + (board.getHeight() - 1 - cy) * Vars.fullSize;
		if (board.isSelected()) drawBlock("cursorSelect", cursorX, cursorY);
		else drawBlock("cursor", cursorX, cursorY);
		Special special = board.getSpecial()[cx][cy];
		if (special != null) {
			if (special.path) drawLine(cx, cy, special.destX, special.destY);
			else if (special.button) {
				for (int i = 0; i < board.getWidth(); i++) {
					for (int j = 0; j < board.getHeight(); j++) {
						Special target = board.getSpecial()[i][j];
						if (target != null && target.destX == cx && target.destY == cy) drawLine(cx, cy, i, j);
					}
				}
			} else if (special.gate) drawLine(cx, cy, special.destX, special.destY);
		}
		/* if (board.placingGate) { drawBlock("button", cursorX, cursorY); drawBlock("gate", boardOffsetX + board.placeX * Vars.fullSize, boardOffsetY + (board.getHeight() - 1 - board.placeY) * Vars.fullSize); drawLine(cx, cy, board.placeX, board.placeY);
		 * } else if (board.placingPath) { drawBlock("path", cursorX, cursorY); drawBlock("path", boardOffsetX + board.placeX * Vars.fullSize, boardOffsetY + (board.getHeight() - 1 - board.placeY) * Vars.fullSize); drawLine(cx, cy, board.placeX,
		 * board.placeY); } */
	}

	private void drawLine(int x1, int y1, int x2, int y2) {
		int xo = boardOffsetX + x1 * Vars.fullSize + Vars.halfSize;
		int yo = boardOffsetY + (board.getHeight() - 1 - y1) * Vars.fullSize + Vars.halfSize;
		drawBlock("cursor", boardOffsetX + x2 * Vars.fullSize, boardOffsetY + (board.getHeight() - 1 - y2) * Vars.fullSize);
		int dx = (x2 - x1) * Vars.fullSize;
		int dy = (y1 - y2) * Vars.fullSize;
		int length = (int) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
		int numDots = length / 10;
		float angle = MathUtils.atan2(dy, dx);
		for (int i = 0; i <= numDots; i++) {
			float x = xo + ((i + timer / 16f) % numDots) * length * MathUtils.cos(angle) / numDots - 1;
			float y = yo + ((i + timer / 16f) % numDots) * length * MathUtils.sin(angle) / numDots - 1;
			sprites.draw(assets.get("img/dot.png", Texture.class), x, y);
		}
	}

	private void drawGUI() {
		switch (board.getCursorState()) {
		case BLOCK_ACTIVE: break;
		case BLOCK_MOVE: break;
		case BLOCK_STATIC: break;
		case BOMB: break;
		case ERASER: break;
		case FALL: break;
		case GATE: break;
		case GEM_CENTER: break;
		case GEM_DOWN: break;
		case GEM_LEFT: break;
		case GEM_RIGHT: break;
		case GEM_UP: break;
		case PATH: break;
		case WATER: break;
		default: break;
		}
	}

	public void render(float delta) {
		Gdx.gl.glClearColor(0.3f, 0.25f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		sprites.begin();
		drawBlocks();
		drawCursor();
		sprites.end();
		if (timer < 16) timer++;
		else timer = 0;
	}

	public void show() {}

	public void hide() {}

	public void pause() {}

	public void resume() {}
}
