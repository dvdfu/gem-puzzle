package com.dvdfu.puzzle;

import com.badlogic.gdx.ApplicationListener;
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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dvdfu.puzzle.entities.Block;
import com.dvdfu.puzzle.entities.Board;
import com.dvdfu.puzzle.entities.Particle;
import com.dvdfu.puzzle.entities.Special;
import com.dvdfu.puzzle.handlers.Input;
import com.dvdfu.puzzle.handlers.InputController;
import com.dvdfu.puzzle.handlers.Sprite;
import com.dvdfu.puzzle.handlers.Vars;

public class MainGame implements ApplicationListener {
	private Board board;
	private AssetManager assets = new AssetManager();
	private SpriteBatch sprites;
	private ShapeRenderer shapes;
	private Viewport view;
	private OrthographicCamera camera;
	private Vector3 mouse;
	private int[][] timer;
	private int boardOffsetX;
	private int boardOffsetY;
	private boolean gemSound;
	private Sprite sparkle1;
	private Array<Particle> particles;
	private final Pool<Particle> particlePool = new Pool<Particle>() {
		protected Particle newObject() {
			return new Particle();
		}
	};

	public void create() {
		Gdx.input.setInputProcessor(new InputController());
		board = new Board("", 6, 8);
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
		assets.load("img/sparkle1.png", Texture.class);
		assets.load("aud/select.wav", Sound.class);
		assets.load("aud/deselect.wav", Sound.class);
		assets.load("aud/remove.wav", Sound.class);
		assets.finishLoading();
		sprites = new SpriteBatch();
		shapes = new ShapeRenderer();
		view = new ScreenViewport();
		camera = (OrthographicCamera) view.getCamera();
		camera.zoom = 1 / 2f;
		camera.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);
		mouse = new Vector3();
		boardOffsetX = (Gdx.graphics.getWidth() - board.getWidth() * Vars.blockSize) / 2;
		boardOffsetY = (Gdx.graphics.getHeight() - board.getHeight() * Vars.blockSize) / 2;
		timer = new int[board.getWidth()][board.getHeight()];
		timerReset();
		gemSound = false;
		particles = new Array<Particle>();
		sparkle1 = new Sprite(assets.get("img/sparkle1.png", Texture.class), 16, 16);
	}

	public void dispose() {
		assets.dispose();
		sprites.dispose();
		shapes.dispose();
	}

	public void render() {
		setView();
		sprites.begin();
		Special[][] specials = board.getSpecial();
		for (int i = 0; i < board.getWidth(); i++) {
			for (int j = 0; j < board.getHeight(); j++) {
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
							timer[i][j] = Vars.timeMove;
							break;
						case GEM:
						case BIG_GEM:
							timer[i][j] = Vars.timeGem;
							break;
						case PATH_ENTER:
						case PATH_EXIT:
							timer[i][j] = Vars.timePath;
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
				int drawX = boardOffsetX + i * Vars.blockSize;
				int drawY = boardOffsetY + (board.getHeight() - 1 - j) * Vars.blockSize;
				drawBlock("grid", drawX, drawY);
				Special special = specials[i][j];
				if (special != null && special.path) {
					drawBlock("path", drawX, drawY);
				}
			}
		}
		for (int i = 0; i < board.getWidth(); i++) {
			for (int j = 0; j < board.getHeight(); j++) {
				int drawX = boardOffsetX + i * Vars.blockSize;
				int drawY = boardOffsetY + (board.getHeight() - 1 - j) * Vars.blockSize;
				Block block = blocks[i][j];
				if (block != null) {
					if (timer[i][j] > 0) {
						timer[i][j]--;
					}
					int bufferX = 0;
					int bufferY = 0;

					switch (block.command) {
					case MOVE_UP:
						bufferY = (Vars.timeMove - timer[i][j]) * Vars.blockSize / Vars.timeMove;
						break;
					case FALL:
					case MOVE_DOWN:
						bufferY = -(Vars.timeMove - timer[i][j]) * Vars.blockSize / Vars.timeMove;
						break;
					case MOVE_RIGHT:
						bufferX = (Vars.timeMove - timer[i][j]) * Vars.blockSize / Vars.timeMove;
						break;
					case MOVE_LEFT:
						bufferX = -(Vars.timeMove - timer[i][j]) * Vars.blockSize / Vars.timeMove;
						break;
					case GEM:
					case BIG_GEM:
						gemSound = true;
						createParticle(Particle.Type.SPARKLE_1, drawX + Vars.blockSize / 2, drawY + Vars.blockSize / 2);
						break;
					case PATH_ENTER:
						setAlpha(timer[i][j] * 1f / Vars.timePath);
						break;
					case PATH_EXIT:
						setAlpha((Vars.timePath - timer[i][j]) * 1f / Vars.timePath);
						break;
					default:
						break;
					}

					drawY += bufferY;
					drawX += bufferX;

					if (block.move) {
						drawBlock("block", drawX, drawY);
					} else {
						drawBlock("fixed", drawX, drawY);
					}
					if (block.gemC) {
						drawBlock("gemsC", drawX, drawY);
					} else {
						if (block.gemD) {
							drawBlock("gemsD", drawX, drawY);
						}
						if (block.gemU) {
							drawBlock("gemsU", drawX, drawY);
						}
						if (block.gemL) {
							drawBlock("gemsL", drawX, drawY);
						}
						if (block.gemR) {
							drawBlock("gemsR", drawX, drawY);
						}
					}
					if (block.fall) {
						drawBlock("fall", drawX, drawY);
					}
					setAlpha(1);
				}
			}
		}
		drawParticles();
		sprites.end();
		drawCursor();
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
	

	
	private void createParticle(Particle.Type type, int x, int y) {
		createParticle(type, x, y, 1);
	}
	
	private void createParticle(Particle.Type type, int x, int y, int num) {
		for (int i = 0; i < num; i++) {
			Particle newParticle = particlePool.obtain();
			switch (type) {
			case SPARKLE_1:
				newParticle.x = x - sparkle1.getWidth() / 2;
				newParticle.y = y - sparkle1.getHeight() / 2;
				newParticle.dx = MathUtils.random(-2f, 2f);
				newParticle.dy = MathUtils.random(-2f, 2f);
				newParticle.ticks = MathUtils.random(20);
				break;
			}
			particles.add(newParticle);
		}
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
	
	private void setView() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		mouse.set(Input.mouse.x, Input.mouse.y, 0);
		sprites.setProjectionMatrix(camera.combined);
		shapes.setProjectionMatrix(camera.combined);
		camera.unproject(mouse);
	}

	private void updateCursor() {
		int cursorX = (int) (mouse.x - boardOffsetX) / Vars.blockSize;
		int cursorY = board.getHeight() - 1 - (int) (mouse.y - boardOffsetY) / Vars.blockSize;
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

	private void drawBlock(String filename, int x, int y) {
		sprites.draw(assets.get("img/" + filename + ".png", Texture.class), x, y, Vars.blockSize, Vars.blockSize);
	}
	
	private void drawCursor() {
		shapes.begin(ShapeType.Line);
		if (board.isSelected()) {
			shapes.setColor(Color.RED);
		} else {
			shapes.setColor(Color.BLUE);
		}
		int cursorX = boardOffsetX + board.getCursorX() * Vars.blockSize;
		int cursorY = boardOffsetY + (board.getHeight() - 1 - board.getCursorY()) * Vars.blockSize;
		shapes.rect(cursorX, cursorY, Vars.blockSize, Vars.blockSize);
		shapes.end();
	}

	private void drawParticles() {
		SnapshotArray<Particle> snapshotParticles = new SnapshotArray<Particle>(particles);
		for (Particle particle : snapshotParticles) {
			particle.update();
			if (particle.frame() > 11) {
				particles.removeValue(particle, false);
				particlePool.free(particle);
			} else {
				sprites.draw(sparkle1.getFrameAt(particle.frame()), particle.x, particle.y, sparkle1.getWidth(), sparkle1.getHeight());
			}
		}
	}

	public void resize(int width, int height) {
		view.update(width, height);
	}

	public void pause() {
	}

	public void resume() {
	}
}
