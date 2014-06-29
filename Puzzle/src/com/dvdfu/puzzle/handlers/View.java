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

public class View {
	private Board board;
	private AssetManager assets = new AssetManager();
	private SpriteBatch sprites;
	private ShapeRenderer shapes;
	private Viewport viewport;
	private OrthographicCamera camera;
	private Vector3 mouse;
	private int[][] timer;
	private int boardOffsetX;
	private int boardOffsetY;
	private Sprite sparkle1;
	private Sprite dirt1;
	private Sprite dust1;
	private Sprite drop;
	private Sprite gem;
	private Array<Particle> particles;
	private Pool<Particle> particlePool;

	public View(Board board) {
		this.board = board;
		boardOffsetX = (Gdx.graphics.getWidth() - board.getWidth() * Vars.blockSize) / 2;
		boardOffsetY = (Gdx.graphics.getHeight() - board.getHeight() * Vars.blockSize) / 2;
		loadAssets();

		sprites = new SpriteBatch();
		shapes = new ShapeRenderer();

		viewport = new ScreenViewport();
		mouse = new Vector3();
		camera = (OrthographicCamera) viewport.getCamera();
		camera.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);

		timer = new int[board.getWidth()][board.getHeight()];
		resetTimer();

		particles = new Array<Particle>();
		particlePool = new Pool<Particle>() {
			protected Particle newObject() {
				return new Particle();
			}
		};
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
		assets.load("aud/select.wav", Sound.class);
		assets.load("aud/deselect.wav", Sound.class);
		assets.load("aud/remove.wav", Sound.class);
		assets.load("aud/splash.mp3", Sound.class);
		assets.finishLoading();
		sparkle1 = new Sprite(assets.get("img/sparkle1.png", Texture.class), 16, 16);
		dirt1 = new Sprite(assets.get("img/dirt1.png", Texture.class), 8, 8);
		dust1 = new Sprite(assets.get("img/dust1.png", Texture.class), 8, 8);
		drop = new Sprite(assets.get("img/drops.png", Texture.class), 8, 8);
		gem = new Sprite(assets.get("img/gem.png", Texture.class), 16, 16);
	}

	public void update() {
		updateView();
		updateCursor();
		if (timerReady() && board.isBuffered()) updateBuffer();
		if (!board.isBuffered()) board.update();
		if (timerReady()) updateTimer();
	}

	public void draw() {
		sprites.begin();
		drawGrid();
		drawBlocks();
		drawWater();
		drawParticles();
		sprites.end();
		drawCursor();
	}

	public void resize(int width, int height) {
		int zoomW = height / Vars.blockSize / board.getHeight();
		int zoomH = width / Vars.blockSize / board.getWidth();
		camera.zoom = 1f / Math.min(zoomW, zoomH);
		Gdx.gl20.glLineWidth(1 / camera.zoom);
		viewport.update(width, height);
	}

	private void updateView() {
		/* resets screen and projects sprite batch, shape renderer, mouse and camera based on the screen */
		Gdx.gl.glClearColor(0.3f, 0.25f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		mouse.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		sprites.setProjectionMatrix(camera.combined);
		shapes.setProjectionMatrix(camera.combined);
		camera.unproject(mouse);
	}

	private void updateBuffer() {
		/* if the board is buffered and the view is processing the events, this method is used to finalize animations and sounds and then update the board using the buffer */
		for (int i = 0; i < board.getWidth(); i++) {
			for (int j = 0; j < board.getHeight(); j++) {
				int drawX = boardOffsetX + i * Vars.blockSize;
				int drawY = boardOffsetY + (board.getHeight() - 1 - j) * Vars.blockSize;
				Block block = board.getGrid()[i][j];
				if (block != null) {
					switch (block.command) {
					case FALL:
						if (board.gridValid(i, j + 2)) {
							if (!board.gridEmpty(i, j + 2)) {
								createParticle(Particle.Type.DUST_L, drawX + Vars.blockSize / 2, drawY - Vars.blockSize, 4);
								createParticle(Particle.Type.DUST_R, drawX + Vars.blockSize / 2, drawY - Vars.blockSize, 4);
								assets.get("aud/remove.wav", Sound.class).play(0.1f);
							}
						}
					case MOVE_UP:
					case MOVE_DOWN:
					case MOVE_RIGHT:
					case MOVE_LEFT:
						break;
					case BREAK:
						if (block.isGem()) {
							createParticle(Particle.Type.SPARKLE, drawX + Vars.blockSize / 2, drawY + Vars.blockSize / 2, 8);
							createParticle(Particle.Type.GEM, drawX + Vars.blockSize / 2, drawY + Vars.blockSize / 2);
						}
						break;
					case DROWN:
						if (block.isGem()) {
							createParticle(Particle.Type.SPARKLE, drawX + Vars.blockSize / 2, drawY + Vars.blockSize / 2, 8);
							createParticle(Particle.Type.GEM, drawX + Vars.blockSize / 2, drawY + Vars.blockSize / 2);
						}
						if (board.getSpecial()[i][j] != null && board.getSpecial()[i][j].hazard) {
							createParticle(Particle.Type.DROP, drawX + Vars.blockSize / 2, drawY + Vars.blockSize / 2, 16);
							assets.get("aud/splash.mp3", Sound.class).play(0.1f);
						}
						break;
					case PATH_ENTER:
					case PATH_EXIT:
						break;
					default:
						break;
					}
				}
			}
		}
		board.useBuffer();
	}

	private void updateCursor() {
		/* sends mouse information to the board and plays appropriate sound effects */
		int cursorX = (int) (mouse.x - boardOffsetX) / Vars.blockSize;
		int cursorY = board.getHeight() - 1 - (int) (mouse.y - boardOffsetY) / Vars.blockSize;
		board.setCursor(cursorX, cursorY);
		if (Gdx.input.justTouched() && board.select()) assets.get("aud/select.wav", Sound.class).play();
		if (!Gdx.input.isTouched() && board.unselect()) assets.get("aud/deselect.wav", Sound.class).play();
	}

	private void updateTimer() {
		/* when the timer is ready, checks board to see if any of the timers need to be set */
		for (int i = 0; i < board.getWidth(); i++) {
			for (int j = 0; j < board.getHeight(); j++) {
				Block block = board.getGrid()[i][j];
				if (block != null) {
					switch (block.command) {
					case DROWN:
					case FALL:
					case MOVE_UP:
					case MOVE_DOWN:
					case MOVE_RIGHT:
					case MOVE_LEFT:
						timer[i][j] = Vars.timeMove;
						break;
					case BREAK:
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

	private boolean timerReady() {
		for (int i = 0; i < board.getWidth(); i++) {
			for (int j = 0; j < board.getHeight(); j++) {
				if (timer[i][j] > 0) return false;
			}
		}
		return true;
	}

	private void resetTimer() {
		for (int i = 0; i < board.getWidth(); i++) {
			for (int j = 0; j < board.getHeight(); j++) {
				timer[i][j] = 0;
			}
		}
	}

	private void createParticle(Particle.Type type, int x, int y) {
		createParticle(type, x, y, 1);
	}

	private void createParticle(Particle.Type type, int x, int y, int num) {
		for (int i = 0; i < num; i++) {
			Particle newParticle = particlePool.obtain();
			newParticle.type = type;
			switch (type) {
			case SPARKLE:
				newParticle.setPosition(x - sparkle1.getWidth() / 2, y - sparkle1.getHeight() / 2);
				newParticle.setVector(MathUtils.random(2f), MathUtils.random(2 * MathUtils.PI));
				newParticle.setDuration(MathUtils.random(20), 12);
				break;
			case DIRT:
				newParticle.setPosition(x - dirt1.getWidth() / 2, y - Vars.blockSize / 2);
				newParticle.setVelocity(MathUtils.random(-1.5f, 1.5f), MathUtils.random(1f, 3f));
				newParticle.setAcceleration(0, -0.1f);
				newParticle.setDuration(MathUtils.random(20), 12);
				break;
			case DUST:
				newParticle.setPosition(x - dust1.getWidth() / 2, y - dust1.getHeight() / 2);
				newParticle.setVector(MathUtils.random(2f), MathUtils.random(2 * MathUtils.PI));
				newParticle.setDuration(MathUtils.random(4), 6);
				break;
			case DUST_L:
				newParticle.setPosition(x - dust1.getWidth() / 2, y - dust1.getHeight() / 2);
				newParticle.setVelocity(MathUtils.random(-2f, 0), MathUtils.random(0.2f, 0.5f));
				newParticle.setDuration(MathUtils.random(4), 6);
				break;
			case DUST_R:
				newParticle.setPosition(x - dust1.getWidth() / 2, y - dust1.getHeight() / 2);
				newParticle.setVelocity(MathUtils.random(0, 2f), MathUtils.random(0.2f, 0.5f));
				newParticle.setDuration(MathUtils.random(4), 6);
				break;
			case DROP:
				newParticle.setPosition(x - drop.getWidth() / 2, y - drop.getHeight() / 2);
				newParticle.setVelocity(MathUtils.random(-1.5f, 1.5f), MathUtils.random(1f, 4f));
				newParticle.setAcceleration(0, -0.1f);
				newParticle.setDuration(MathUtils.random(4), 12);
				break;
			case GEM:
				newParticle.setPosition(x - gem.getWidth() / 2, y - gem.getHeight() / 2);
				newParticle.setVelocity(0, 3.2f);
				newParticle.setAcceleration(0, -0.1f);
				newParticle.setDuration(0, 8);
				break;
			}
			particles.add(newParticle);
		}
	}

	private void setAlpha(float alpha) {
		Color c = sprites.getColor();
		sprites.setColor(c.r, c.g, c.b, alpha);
	}

	private void drawGrid() {
		for (int i = 0; i < board.getWidth(); i++) {
			for (int j = 0; j < board.getHeight(); j++) {
				int drawX = boardOffsetX + i * Vars.blockSize;
				int drawY = boardOffsetY + (board.getHeight() - 1 - j) * Vars.blockSize;
				drawBlock("grid", drawX, drawY);
				Special special = board.getSpecial()[i][j];
				if (special != null && special.path) drawBlock("path", drawX, drawY);
			}
		}
	}

	private void drawBlocks() {
		/* draw blocks at their position and transparency based on their properties and buffer timers */
		for (int i = 0; i < board.getWidth(); i++) {
			for (int j = 0; j < board.getHeight(); j++) {
				int drawX = boardOffsetX + i * Vars.blockSize;
				int drawY = boardOffsetY + (board.getHeight() - 1 - j) * Vars.blockSize;
				Block block = board.getGrid()[i][j];
				if (block != null) {
					if (timer[i][j] > 0) timer[i][j]--;
					int bufferX = 0;
					int bufferY = 0;
					switch (block.command) {
					case MOVE_UP:
						bufferY = (Vars.timeMove - timer[i][j]) * Vars.blockSize / Vars.timeMove;
						break;
					case DROWN:
					case FALL:
					case MOVE_DOWN:
						bufferY = -(Vars.timeMove - timer[i][j]) * Vars.blockSize / Vars.timeMove;
						break;
					case MOVE_RIGHT:
						bufferX = (Vars.timeMove - timer[i][j]) * Vars.blockSize / Vars.timeMove;
						if (block.fall) createParticle(Particle.Type.DUST_L, drawX + bufferX, drawY);
						break;
					case MOVE_LEFT:
						bufferX = -(Vars.timeMove - timer[i][j]) * Vars.blockSize / Vars.timeMove;
						if (block.fall) createParticle(Particle.Type.DUST_R, drawX + bufferX + Vars.blockSize, drawY);
						break;
					case BREAK:
						if (board.getSpecial()[i][j] == null || !board.getSpecial()[i][j].hazard) {
							createParticle(Particle.Type.DIRT, drawX + Vars.blockSize / 2, drawY + Vars.blockSize / 2);
						}
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
	}

	private void drawBlock(String filename, int x, int y) {
		sprites.draw(assets.get("img/" + filename + ".png", Texture.class), x, y, Vars.blockSize, Vars.blockSize);
	}

	private void drawWater() {
		// setAlpha(0.8f);
		for (int i = 0; i < board.getWidth(); i++) {
			for (int j = 0; j < board.getHeight(); j++) {
				int drawX = boardOffsetX + i * Vars.blockSize;
				int drawY = boardOffsetY + (board.getHeight() - 1 - j) * Vars.blockSize;
				Special special = board.getSpecial()[i][j];
				if (special != null && special.hazard) {
					if (board.gridValid(i, j - 1) && board.getSpecial()[i][j - 1] == null) drawBlock("water", drawX, drawY);
					else drawBlock("waterF", drawX, drawY);
				}
			}
		}
		// setAlpha(1);
	}

	private void drawCursor() {
		shapes.begin(ShapeType.Line);
		if (board.isSelected()) shapes.setColor(Color.RED);
		else shapes.setColor(Color.BLUE);
		int cursorX = boardOffsetX + board.getCursorX() * Vars.blockSize;
		int cursorY = boardOffsetY + (board.getHeight() - 1 - board.getCursorY()) * Vars.blockSize;
		shapes.rect(cursorX, cursorY, Vars.blockSize, Vars.blockSize);
		shapes.end();
	}

	private void drawParticles() {
		SnapshotArray<Particle> snapshotParticles = new SnapshotArray<Particle>(particles);
		for (Particle particle : snapshotParticles) {
			particle.update();
			if (particle.dead()) {
				particles.removeValue(particle, false);
				particlePool.free(particle);
			} else {
				Sprite sprite = null;
				switch (particle.type) {
				case SPARKLE:
					sprite = sparkle1;
					break;
				case GEM:
					sprite = gem;
					break;
				case DIRT:
					sprite = dirt1;
					break;
				case DROP:
					sprite = drop;
					break;
				case DUST:
				case DUST_L:
				case DUST_R:
					sprite = dust1;
					break;
				}
				if (sprite != null) sprites.draw(sprite.getFrame(particle.frame()), particle.getX(), particle.getY(),
					sprite.getWidth(), sprite.getHeight());
			}
		}
	}
}
