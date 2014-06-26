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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.SnapshotArray;
import com.dvdfu.puzzle.entities.Block;
import com.dvdfu.puzzle.entities.Board;
import com.dvdfu.puzzle.entities.Particle;
import com.dvdfu.puzzle.entities.Special;
import com.dvdfu.puzzle.handlers.Input;
import com.dvdfu.puzzle.handlers.InputController;
import com.dvdfu.puzzle.handlers.Sprite;

public class MainGame implements ApplicationListener {
	private Board board;
	private AssetManager assets = new AssetManager();
	private SpriteBatch sprites;
	private ShapeRenderer shapes;
	private int scale;
	private int blockSize;
	private int boardOffsetX;
	private int boardOffsetY;
	private int[][] timer;
	private int timeMove;
	private int timePath;
	private int timeGem;
	private boolean gemSound;
	private Sprite star;
	private Array<Particle> particles;
	private final Pool<Particle> particlePool = new Pool<Particle>() {
		protected Particle newObject() {
			return new Particle();
		}
	};

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
		assets.load("img/path.png", Texture.class);
		assets.load("img/grid.png", Texture.class);
		assets.load("img/star1.png", Texture.class);
		assets.load("aud/select.wav", Sound.class);
		assets.load("aud/deselect.wav", Sound.class);
		assets.load("aud/remove.wav", Sound.class);
		assets.finishLoading();
		sprites = new SpriteBatch();
		shapes = new ShapeRenderer();
		scale = 1;
		Gdx.gl20.glLineWidth(scale);
		blockSize = 32 * scale;
		boardOffsetX = (Gdx.graphics.getWidth() - board.getWidth() * blockSize) / 2;
		boardOffsetY = (Gdx.graphics.getHeight() - board.getHeight() * blockSize) / 2;
		timer = new int[board.getWidth()][board.getHeight()];
		timerReset();
		timeMove = 4;
		timePath = 12;
		timeGem = 16;
		gemSound = false;
		particles = new Array<Particle>();
		star = new Sprite(assets.get("img/star1.png", Texture.class), 16, 16);
	}

	public void dispose() {
		assets.dispose();
		sprites.dispose();
		shapes.dispose();
	}

	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		sprites.begin();
		Special[][] specials = board.getSpecial();
		for (int i = 0; i < board.getWidth(); i++) {
			for (int j = 0; j < board.getHeight(); j++) {
				int x = boardOffsetX + i * blockSize;
				int y = boardOffsetY + (board.getHeight() - 1 - j) * blockSize;
				drawBlock("grid", x, y, blockSize);
			}
		}
		for (int i = 0; i < board.getWidth(); i++) {
			for (int j = 0; j < board.getHeight(); j++) {
				Special special = specials[i][j];
				if (special != null && special.path) {
					int x = boardOffsetX + i * blockSize - blockSize / 4;
					int y = boardOffsetY + (board.getHeight() - 1 - j) * blockSize - blockSize / 4;
					sprites.draw(assets.get("img/path.png", Texture.class), x, y, blockSize * 3 / 2, blockSize * 3 / 2);
				}
			}
		}
		board.update();
		updateCursor();
		Block[][] blocks = board.getGrid();
		gemSound = false;
		if (timerReady()) {
			// timers must be set in a separate loop because timerReady()
			// evaluates to false as soon as one timer is set
			for (int i = 0; i < board.getWidth(); i++) {
				for (int j = 0; j < board.getHeight(); j++) {
					Block block = blocks[i][j];
					if (block != null) {
						switch (block.command) {
						case MOVE_UP:
						case MOVE_DOWN:
						case MOVE_RIGHT:
						case MOVE_LEFT:
						case FALL:
							timer[i][j] = timeMove;
							break;
						case GEM:
						case BIG_GEM:
							timer[i][j] = timeGem;
							break;
						case PATH_ENTER:
						case PATH_EXIT:
							timer[i][j] = timePath;
							break;
						default:
							break;
						}
					}
				}
			}
		}
		for (int i = 0; i < board.getWidth(); i++) {
			for (int j = 0; j < board.getHeight(); j++) {
				Block block = blocks[i][j];
				if (block != null) {
					if (timer[i][j] > 0) {
						timer[i][j]--;
					}
					int drawX = boardOffsetX + i * blockSize;
					int drawY = boardOffsetY + (board.getHeight() - 1 - j) * blockSize;
					int bufferX = 0;
					int bufferY = 0;

					switch (block.command) {
					case MOVE_UP:
						bufferY = (timeMove - timer[i][j]) * blockSize / timeMove;
						break;
					case FALL:
					case MOVE_DOWN:
						bufferY = -(timeMove - timer[i][j]) * blockSize / timeMove;
						break;
					case MOVE_RIGHT:
						bufferX = (timeMove - timer[i][j]) * blockSize / timeMove;
						break;
					case MOVE_LEFT:
						bufferX = -(timeMove - timer[i][j]) * blockSize / timeMove;
						break;
					case GEM:
					case BIG_GEM:
						gemSound = true;
						Particle newParticle = particlePool.obtain();
						newParticle.x = drawX + 8;
						newParticle.y = drawY + 8;
						newParticle.dx = MathUtils.random(-2f, 2f);
						newParticle.dy = MathUtils.random(-2f, 2f);
						newParticle.ay = -0.1f;
						newParticle.ticks = MathUtils.random(4);
						particles.add(newParticle);
						break;
					case PATH_ENTER:
						setAlpha(timer[i][j] * 1f / timePath);
						break;
					case PATH_EXIT:
						setAlpha((timePath - timer[i][j]) * 1f / timePath);
						break;
					default:
						break;
					}

					drawY += bufferY;
					drawX += bufferX;

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
					setAlpha(1);
				}
			}
		}
		SnapshotArray<Particle> snapshotParticles = new SnapshotArray<Particle>(particles);
		for (Particle particle : snapshotParticles) {
			particle.update();
			if (particle.frame() > 5) {
				particles.removeValue(particle, false);
				particlePool.free(particle);
			} else {
				sprites.draw(star.getFrameAt(particle.frame()), particle.x, particle.y);
			}
		}
		sprites.end();
		shapes.begin(ShapeType.Line);
		if (board.isSelected()) {
			shapes.setColor(Color.RED);
		} else {
			shapes.setColor(Color.BLUE);
		}
		int cursorX = boardOffsetX + board.getCursorX() * blockSize;
		int cursorY = boardOffsetY + (board.getHeight() - 1 - board.getCursorY()) * blockSize;
		shapes.rect(cursorX, cursorY, blockSize, blockSize);
		shapes.circle(Input.mouse.x, Input.mouse.y, blockSize, blockSize);
		shapes.end();
		if (timerReady()) {
			if (gemSound) {
				assets.get("aud/remove.wav", Sound.class).play(0.1f);
				gemSound = false;
			}
			if (board.isBuffered()) {
				board.useBuffer();
			}
		}
		Input.update();
	}

	private void timerReset() {
		for (int i = 0; i < board.getWidth(); i++) {
			for (int j = 0; j < board.getHeight(); j++) {
				timer[i][j] = 0;
			}
		}
	}

	private boolean timerReady() {
		for (int i = 0; i < board.getWidth(); i++) {
			for (int j = 0; j < board.getHeight(); j++) {
				if (timer[i][j] > 0) {
					return false;
				}
			}
		}
		return true;
	}

	private void updateCursor() {
		int cursorX = (int) (Input.mouse.x - boardOffsetX) / blockSize;
		int cursorY = board.getHeight() - 1 - (int) (Input.mouse.y - boardOffsetY) / blockSize;
		board.setCursor(cursorX, cursorY);
		if (Input.MousePressed()) {
			if (board.select()) {
				assets.get("aud/select.wav", Sound.class).play();
			}
		}
		if (!Input.MouseDown()) {
			if (board.unselect()) {
				assets.get("aud/deselect.wav", Sound.class).play();
			}
		}
	}

	private void setAlpha(float alpha) {
		Color c = sprites.getColor();
		sprites.setColor(c.r, c.g, c.b, alpha);
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
