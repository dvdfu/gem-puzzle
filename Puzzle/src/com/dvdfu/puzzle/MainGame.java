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
import com.dvdfu.puzzle.entities.Block;
import com.dvdfu.puzzle.entities.Board;
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
		board = new Board("", 8, 12);
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
					int bufferX = 0;
					int bufferY = 0;
					switch (block.command) {
					case MOVE_UP:
						bufferY = blockSize * (timerMax - timer) / timerMax;
						break;
					case MOVE_DOWN:
						bufferY = -blockSize * (timerMax - timer) / timerMax;
						break;
					case MOVE_RIGHT:
						bufferX = blockSize * (timerMax - timer) / timerMax;
						break;
					case MOVE_LEFT:
						bufferX = -blockSize * (timerMax - timer) / timerMax;
						break;
					default:
						break;
					}
					int drawX = boardOffsetX + i * blockSize + bufferX;
					int drawY = boardOffsetY + j * blockSize + bufferY;

					if (block.move) {
						drawBlock("block", drawX, drawY, blockSize);
					} else {
						drawBlock("fixed", drawX, drawY, blockSize);
					}
					if (block.gemC) {
						drawBlock("gemsC", drawX, drawY, blockSize);
					} else {
						if (block.gemD) {
							drawBlock("gemsD", drawX, drawY, blockSize);
						}
						if (block.gemU) {
							drawBlock("gemsU", drawX, drawY, blockSize);
						}
						if (block.gemL) {
							drawBlock("gemsL", drawX, drawY, blockSize);
						}
						if (block.gemR) {
							drawBlock("gemsR", drawX, drawY, blockSize);
						}
					}
					if (block.fall) {
						drawBlock("fall", drawX, drawY, blockSize);
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

	private void drawBlock(String filename, int x, int y, int size) {
		sprites.draw(assets.get("img/" + filename + ".png", Texture.class), x, y, size, size);
	}

	public void resize(int width, int height) {
	}

	public void pause() {
	}

	public void resume() {
	}
}
