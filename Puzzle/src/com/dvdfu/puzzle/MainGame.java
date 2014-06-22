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
	private int blockSize = (int) (32 * 2);
	private int timer = 0;
	private int timerMax = 8;

	public void create() {
		Gdx.input.setInputProcessor(new InputController());
		board = new Board("2f e6 34 6d 9b 2c fb 3d f6 b6 f6 a6 a6 28 9d 56 cc 06 03 7e 33 2f cd 60 ", 4, 6);
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
		boardOffsetX = (Gdx.graphics.getWidth() - board.getWidth() * blockSize) / 2;
		boardOffsetY = (Gdx.graphics.getHeight() - board.getHeight() * blockSize) / 2;
		timer = 0;
	}

	public void dispose() {
		assets.dispose();
		sprites.dispose();
		shapes.dispose();
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
				board.select();
				assets.get("aud/select.wav", Sound.class).play();
			}
			if (board.isBuffered()) {
				timer = timerMax;
			}
		}
		if (Input.MouseReleased()) {
			board.unselect();
			assets.get("aud/deselect.wav", Sound.class).play();
		}
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		shapes.begin(ShapeType.Line);
		{
			shapes.setColor(Color.LIGHT_GRAY);
			{
				for (int i = 0; i < board.getWidth(); i++) {
					for (int j = 0; j < board.getHeight(); j++) {
						shapes.rect(boardOffsetX + i * blockSize, boardOffsetY + j * blockSize, blockSize, blockSize);
					}
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
					if (block.used()) {
						if (!block.hasD() && !block.hasL() && !block.hasR() && !block.hasU()) {
							sprites.draw(assets.get("img/gemsC.png", Texture.class), drawX, drawY, blockSize, blockSize);
						}
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
			assets.get("aud/remove.wav", Sound.class).play();
		}
		sprites.end();
		Input.update();
	}

	public void resize(int width, int height) {
	}

	public void pause() {
	}

	public void resume() {
	}
}
