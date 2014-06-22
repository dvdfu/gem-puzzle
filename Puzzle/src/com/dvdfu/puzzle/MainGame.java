package com.dvdfu.puzzle;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.dvdfu.puzzle.handlers.Block;
import com.dvdfu.puzzle.handlers.Board;
import com.dvdfu.puzzle.handlers.Input;
import com.dvdfu.puzzle.handlers.InputController;

public class MainGame implements ApplicationListener {
	private Board board;
	private AssetManager assets = new AssetManager();
	private SpriteBatch sprites;
	private ShapeRenderer shapes;
	private int boardOffsetX;
	private int boardOffsetY;
	private int scale = 1;
	private int blockSize = (int) (32 * scale);
	private int timer = 0;
	private int timerMax = 4;

	public void create() {
		Gdx.input.setInputProcessor(new InputController());
		loadBoard("level");
		board = new Board("81 b0 ca 28 19 e8 e8 75 13 21 9a 45 6d d5 46 98 0c 07 49 7d d6 bf af 32 27 7b b4 3d bf dc 37 0c fd e8 0b 12 4c d5 e8 fa cd 66 54 76 3a f9 35 97 51 87 0c 1d 05 fa e9 38 44 f1 39 27 7c a7 c1 87 4d bc 81 1f ed d8 3a a3 6b e5 6d a0 cf 81 28 e6 99 b4 a5 d1 5a 1e 79 b7 5c ab 16 c4 d6 5b ad 73 ", 8, 12);
		assets.load("img/block.png", Texture.class);
		assets.load("img/fixed.png", Texture.class);
		assets.load("img/gemsC.png", Texture.class);
		assets.load("img/gemsD.png", Texture.class);
		assets.load("img/gemsL.png", Texture.class);
		assets.load("img/gemsR.png", Texture.class);
		assets.load("img/gemsU.png", Texture.class);
		assets.load("img/fall.png", Texture.class);
		assets.load("aud/select.wav", Sound.class);
		assets.load("aud/deselect.wav", Sound.class);
		assets.load("aud/remove.wav", Sound.class);
		assets.finishLoading();
		sprites = new SpriteBatch();
		shapes = new ShapeRenderer();
		Gdx.gl20.glLineWidth(scale);
		boardOffsetX = (Gdx.graphics.getWidth() - board.getWidth() * blockSize) / 2;
		boardOffsetY = (Gdx.graphics.getHeight() - board.getHeight() * blockSize) / 2;
		timer = 0;
	}

	public void dispose() {
		assets.dispose();
		sprites.dispose();
		shapes.dispose();
	}

	private void loadBoard(String filename) {
		TiledMapTileLayer layerBlock = (TiledMapTileLayer) (new TmxMapLoader().load("data/" + filename + ".tmx")).getLayers().get("blocks");
		TiledMapTileLayer layerFall = (TiledMapTileLayer) (new TmxMapLoader().load("data/" + filename + ".tmx")).getLayers().get("fall");
		TiledMapTileLayer layerU = (TiledMapTileLayer) (new TmxMapLoader().load("data/" + filename + ".tmx")).getLayers().get("U");
		TiledMapTileLayer layerD = (TiledMapTileLayer) (new TmxMapLoader().load("data/" + filename + ".tmx")).getLayers().get("D");
		TiledMapTileLayer layerL = (TiledMapTileLayer) (new TmxMapLoader().load("data/" + filename + ".tmx")).getLayers().get("L");
		TiledMapTileLayer layerR = (TiledMapTileLayer) (new TmxMapLoader().load("data/" + filename + ".tmx")).getLayers().get("R");
		TiledMapTileLayer layerC = (TiledMapTileLayer) (new TmxMapLoader().load("data/" + filename + ".tmx")).getLayers().get("C");
	}

	public void render() {
		if (timer > 0) {
			timer--;
			if (timer == 0) {
				board.useBuffer();
			}
		} else {
			board.setCursor((int) (Input.mouse.x - boardOffsetX) / blockSize, (int) (Input.mouse.y - boardOffsetY) / blockSize);
			if (Input.MousePressed()) {
				if (board.select()) {
					assets.get("aud/select.wav", Sound.class).play();
				}
			}
			if (board.isBuffered()) {
				timer = timerMax;
			}
		}
		if (Input.MouseReleased()) {
			if (board.unselect()) {
				assets.get("aud/deselect.wav", Sound.class).play();
			}
		}
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		shapes.begin(ShapeType.Line);
		{
			shapes.setColor(Color.LIGHT_GRAY);
			for (int i = 0; i < board.getWidth(); i++) {
				for (int j = 0; j < board.getHeight(); j++) {
					shapes.rect(boardOffsetX + i * blockSize, boardOffsetY + j * blockSize, blockSize, blockSize);
				}
			}
		}
		shapes.end();
		sprites.begin();
		boolean destroy = false;
		Block[][] blocks = board.getGrid();
		for (int i = 0; i < board.getWidth(); i++) {
			for (int j = 0; j < board.getHeight(); j++) {
				Block block = blocks[i][j];
				if (block != null) {
					int[][] buffer = board.getBuffer();
					int bufferX = 0;
					int bufferY = 0;
					if (buffer[i][j] == 0) {
						destroy = true;
					}
					if (buffer[i][j] == 1) {
						bufferX = -blockSize + timer * blockSize / timerMax;
					} else if (buffer[i][j] == 2) {
						bufferX = blockSize - timer * blockSize / timerMax;
					} else if (buffer[i][j] == 3) {
						bufferY = blockSize - timer * blockSize / timerMax;
					} else if (buffer[i][j] == 4) {
						bufferY = -blockSize + timer * blockSize / timerMax;
					}
					int drawX = boardOffsetX + i * blockSize + bufferX;
					int drawY = boardOffsetY + j * blockSize + bufferY;

					if (block.moves()) {
						sprites.draw(assets.get("img/block.png", Texture.class), drawX, drawY, blockSize, blockSize);
					} else {
						sprites.draw(assets.get("img/fixed.png", Texture.class), drawX, drawY, blockSize, blockSize);
					}
					if (block.falls()) {
						sprites.draw(assets.get("img/fall.png", Texture.class), drawX, drawY, blockSize, blockSize);
					}
					if (block.hasC()) {
						sprites.draw(assets.get("img/gemsC.png", Texture.class), drawX, drawY, blockSize, blockSize);
					} else {
						if (block.hasD()) {
							sprites.draw(assets.get("img/gemsD.png", Texture.class), drawX, drawY, blockSize, blockSize);
						}
						if (block.hasU()) {
							sprites.draw(assets.get("img/gemsU.png", Texture.class), drawX, drawY, blockSize, blockSize);
						}
						if (block.hasL()) {
							sprites.draw(assets.get("img/gemsL.png", Texture.class), drawX, drawY, blockSize, blockSize);
						}
						if (block.hasR()) {
							sprites.draw(assets.get("img/gemsR.png", Texture.class), drawX, drawY, blockSize, blockSize);
						}
					}
				}
			}
		}
		if (destroy) {
			assets.get("aud/remove.wav", Sound.class).play(0.1f);
		}
		sprites.end();
		shapes.begin(ShapeType.Line);
		{
			if (board.isSelected()) {
				shapes.setColor(Color.RED);
			} else {
				shapes.setColor(Color.BLUE);
			}
			shapes.rect(boardOffsetX + board.getCursorX() * blockSize, boardOffsetY + board.getCursorY() * blockSize, blockSize, blockSize);
		}
		shapes.end();
		Input.update();
	}

	public void resize(int width, int height) {
	}

	public void pause() {
	}

	public void resume() {
	}
}
