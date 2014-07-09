package com.dvdfu.gems.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dvdfu.gems.entities.Block;
import com.dvdfu.gems.entities.Board;
import com.dvdfu.gems.entities.Particle;
import com.dvdfu.gems.entities.Special;

public class View {
	private Board board;
	private AssetManager assets = new AssetManager();
	private SpriteBatch sprites;
	private Viewport viewport;
	private OrthographicCamera camera;
	private Vector3 mouse;
	private int boardOffsetX;
	private int boardOffsetY;
	private Sprite sparkle1;
	private Sprite dirt1;
	private Sprite dust1;
	private Sprite drop;
	private Sprite gem;
	private Sprite cracks;
	private Sprite fire;
	private Sprite fire2;
	private Array<Particle> particles;
	private Pool<Particle> particlePool;
	private int timer;

	public View(Board board) {
		this.board = board;
		boardOffsetX = (Gdx.graphics.getWidth() - board.getWidth() * Vars.fullSize) / 2;
		boardOffsetY = (Gdx.graphics.getHeight() - board.getHeight() * Vars.fullSize) / 2;
		loadAssets();

		sprites = new SpriteBatch();

		viewport = new ScreenViewport();
		mouse = new Vector3();
		camera = (OrthographicCamera) viewport.getCamera();
		camera.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);

		particles = new Array<Particle>();
		particlePool = new Pool<Particle>() {
			protected Particle newObject() {
				return new Particle();
			}
		};

		timer = 0;
	}

	public void dispose() {
		assets.dispose();
		sprites.dispose();
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
		assets.load("aud/select.wav", Sound.class);
		assets.load("aud/deselect.wav", Sound.class);
		assets.load("aud/remove.wav", Sound.class);
		assets.load("aud/splash.mp3", Sound.class);
		assets.load("aud/break.mp3", Sound.class);
		assets.finishLoading();
		sparkle1 = new Sprite(assets.get("img/sparkle1.png", Texture.class), 16, 16);
		dirt1 = new Sprite(assets.get("img/dirt1.png", Texture.class), 8, 8);
		dust1 = new Sprite(assets.get("img/dust1.png", Texture.class), 8, 8);
		drop = new Sprite(assets.get("img/drops.png", Texture.class), 8, 8);
		gem = new Sprite(assets.get("img/gem.png", Texture.class), 16, 16);
		cracks = new Sprite(assets.get("img/cracks.png", Texture.class), 32, 32);
		fire = new Sprite(assets.get("img/fire.png", Texture.class), 4, 4);
		fire2 = new Sprite(assets.get("img/fire2.png", Texture.class), 8, 8);
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
		if (Input.MouseDown() && board.select()) assets.get("aud/select.wav", Sound.class).play();
		if (!Input.MouseDown() && board.unselect()) assets.get("aud/deselect.wav", Sound.class).play();

	}

	public void draw() {
		Gdx.gl.glClearColor(0.3f, 0.25f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		sprites.begin();
		drawGridUnder();
		drawBlocks();
		drawGridOver();
		drawCursor();
		drawParticles();
		sprites.end();
		if (timer <= 0) timer = 16;
		else timer--;
	}

	public void resize(int width, int height) {
		int zoomW = height / Vars.fullSize / board.getHeight();
		int zoomH = width / Vars.fullSize / board.getWidth();
		camera.zoom = 1f / Math.min(zoomW, zoomH);
		Gdx.gl20.glLineWidth(2 / camera.zoom);
		viewport.update(width, height);
	}

	public void beginBuffer() {
		for (int i = 0; i < board.getWidth(); i++) {
			for (int j = 0; j < board.getHeight(); j++) {
				int drawX = boardOffsetX + i * Vars.fullSize;
				int drawY = boardOffsetY + (board.getHeight() - 1 - j) * Vars.fullSize;
				Block block = board.getGrid()[i][j];
				if (block != null) {
					switch (block.command) {
					case DROWN:
						createParticle(Particle.Type.DROP, drawX + Vars.halfSize, drawY + Vars.fullSize, 16, 0, 16);
						assets.get("aud/splash.mp3", Sound.class).play(0.3f);
						break;
					default:
						break;
					}
				}
			}
		}
	}

	public void endBuffer() {
		/* if the board is buffered and the view is processing the events, this method is used to finalize animations and sounds and then update the board using the buffer */
		for (int i = 0; i < board.getWidth(); i++) {
			for (int j = 0; j < board.getHeight(); j++) {
				int drawX = boardOffsetX + i * Vars.fullSize;
				int drawY = boardOffsetY + (board.getHeight() - 1 - j) * Vars.fullSize;
				Block block = board.getGrid()[i][j];
				if (block != null) {
					switch (block.command) {
					case FALL:
						if (!board.gridEmpty(i, j + 2)) {
							Special special = board.getSpecial()[i][j + 1];
							if (board.gridValid(i, j + 1) && (special == null || !special.hazard)) {
								createParticle(Particle.Type.DUST_L, drawX + Vars.halfSize, drawY - Vars.fullSize, 4);
								createParticle(Particle.Type.DUST_R, drawX + Vars.halfSize, drawY - Vars.fullSize, 4);
								assets.get("aud/remove.wav", Sound.class).play(0.1f);
							}
						}
						break;
					case EXPLODE:
						createParticle(Particle.Type.DIRT, drawX + Vars.halfSize, drawY + Vars.halfSize, 16, 16, 16);
						createParticle(Particle.Type.DUST, drawX + Vars.halfSize, drawY + Vars.halfSize, 16, 16, 16);
						createParticle(Particle.Type.FIRE, drawX + Vars.halfSize, drawY + Vars.halfSize, 4, 4, 16);
					case BREAK:
						createParticle(Particle.Type.DIRT, drawX + Vars.halfSize, drawY + Vars.halfSize, 16, 16, 16);
						assets.get("aud/break.mp3", Sound.class).play();
					case DROWN:
						if (block.isGem()) {
							createParticle(Particle.Type.SPARKLE, drawX + Vars.halfSize, drawY + Vars.halfSize, 16, 16, 8);
							createParticle(Particle.Type.GEM, drawX + Vars.halfSize, drawY + Vars.halfSize);
						}
						break;
					default:
						break;
					}
				}
			}
		}
	}

	private void createParticle(Particle.Type type, int x, int y) {
		createParticle(type, x, y, 0, 0, 1);
	}

	private void createParticle(Particle.Type type, int x, int y, int num) {
		createParticle(type, x, y, 0, 0, num);
	}

	private void createParticle(Particle.Type type, int x, int y, int randX, int randY) {
		createParticle(type, x, y, randX, randY, 1);
	}

	private void createParticle(Particle.Type type, int x, int y, int randX, int randY, int num) {
		for (int i = 0; i < num; i++) {
			Particle newParticle = particlePool.obtain();
			newParticle.type = type;
			Sprite sprite;
			int xr = x + MathUtils.random(-randX, randX);
			int yr = y + MathUtils.random(-randY, randY);
			switch (type) {
			case SPARKLE:
				newParticle.setPosition(xr - sparkle1.getWidth() / 2, yr - sparkle1.getHeight() / 2);
				newParticle.setVector(MathUtils.random(2f), MathUtils.random(2 * MathUtils.PI));
				newParticle.setDuration(MathUtils.random(20), 12);
				newParticle.setSprite(sparkle1);
				break;
			case DIRT:
				newParticle.setPosition(xr - dirt1.getWidth() / 2, yr - Vars.halfSize);
				newParticle.setVelocity(MathUtils.random(-1.5f, 1.5f), MathUtils.random(1f, 3f));
				newParticle.setAcceleration(0, -0.1f);
				newParticle.setDuration(MathUtils.random(20), 12);
				newParticle.setSprite(dirt1);
				break;
			case DUST:
				newParticle.setPosition(xr - dust1.getWidth() / 2, yr - dust1.getHeight() / 2);
				newParticle.setVector(MathUtils.random(2f), MathUtils.random(2 * MathUtils.PI));
				newParticle.setDuration(MathUtils.random(4), 6);
				newParticle.setSprite(dust1);
				break;
			case DUST_L:
				newParticle.setPosition(xr - dust1.getWidth() / 2, yr - dust1.getHeight() / 2);
				newParticle.setVelocity(MathUtils.random(-2f, 0), MathUtils.random(0.2f, 0.5f));
				newParticle.setDuration(MathUtils.random(12), 6);
				newParticle.setSprite(dust1);
				break;
			case DUST_R:
				newParticle.setPosition(xr - dust1.getWidth() / 2, yr - dust1.getHeight() / 2);
				newParticle.setVelocity(MathUtils.random(0, 2f), MathUtils.random(0.2f, 0.5f));
				newParticle.setDuration(MathUtils.random(12), 6);
				newParticle.setSprite(dust1);
				break;
			case DROP:
				newParticle.setPosition(xr - drop.getWidth() / 2, yr - drop.getHeight() / 2);
				newParticle.setVelocity(MathUtils.random(-1.5f, 1.5f), MathUtils.random(1f, 4f));
				newParticle.setAcceleration(0, -0.1f);
				newParticle.setDuration(MathUtils.random(20), 12);
				newParticle.setSprite(drop);
				break;
			case GEM:
				newParticle.setPosition(xr - gem.getWidth() / 2, yr - gem.getHeight() / 2);
				newParticle.setVelocity(0, 3.2f);
				newParticle.setAcceleration(0, -0.1f);
				newParticle.setDuration(0, 8);
				newParticle.setSprite(gem);
				break;
			case FIRE:
				if (MathUtils.randomBoolean()) sprite = fire;
				else sprite = fire2;
				newParticle.setPosition(xr - sprite.getWidth() / 2, yr - sprite.getHeight() / 2);
				newParticle.setVector(MathUtils.random(2f), MathUtils.random(2 * MathUtils.PI));
				newParticle.setDuration(MathUtils.random(12), 8);
				newParticle.setSprite(sprite);
				break;
			}
			particles.add(newParticle);
		}
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
					} else if (special.gate && special.toggled) drawBlock("gate", drawX, drawY);
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
				Special special = board.getSpecial()[i][j];
				if (block != null) {
					int[][] timer = board.getTimer();
					int bufferX = 0;
					int bufferY = 0;
					switch (block.command) {
					case MOVE_UP:
						bufferY = (Vars.timeMove - timer[i][j]) * Vars.fullSize / Vars.timeMove;
						break;
					case FALL:
					case MOVE_DOWN:
						bufferY = -(Vars.timeMove - timer[i][j]) * Vars.fullSize / Vars.timeMove;
						break;
					case MOVE_RIGHT:
						bufferX = (Vars.timeMove - timer[i][j]) * Vars.fullSize / Vars.timeMove;
						if (block.fall) createParticle(Particle.Type.DUST_L, drawX + bufferX, drawY);
						break;
					case MOVE_LEFT:
						bufferX = -(Vars.timeMove - timer[i][j]) * Vars.fullSize / Vars.timeMove;
						if (block.fall) createParticle(Particle.Type.DUST_R, drawX + bufferX + Vars.fullSize, drawY);
						break;
					case PATH:
						setAlpha(Math.abs(1 - 2f * timer[i][j] / Vars.timePath));
						if (timer[i][j] * 2 < Vars.timePath) {
							drawX = boardOffsetX + special.destX * Vars.fullSize;
							drawY = boardOffsetY + (board.getHeight() - 1 - special.destY) * Vars.fullSize;
						}
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
					if (block.command == Block.Command.BREAK) {
						sprites.draw(cracks.getFrame((Vars.timeGem - timer[i][j]) * 7 / Vars.timeGem), drawX, drawY);
					}
					setAlpha(1);
				}
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
						// setAlpha(0.6f);
						drawBlock("waterF", drawX, drawY);
						if (board.gridValid(i, j - 1) && board.getSpecial()[i][j - 1] == null) drawBlock("water", drawX, drawY
							+ Vars.fullSize);
						// setAlpha(1);
					}
				}
			}
		}
	}

	private void drawCursor() {
		int cursorX = boardOffsetX + board.getCursorX() * Vars.fullSize;
		int cursorY = boardOffsetY + (board.getHeight() - 1 - board.getCursorY()) * Vars.fullSize;
		if (board.isSelected()) drawBlock("cursorSelect", cursorX, cursorY);
		else drawBlock("cursor", cursorX, cursorY);
		Special special = board.getSpecial()[board.getCursorX()][board.getCursorY()];
		if (special != null) {
			int cx = board.getCursorX();
			int cy = board.getCursorY();
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

	private void drawParticles() {
		SnapshotArray<Particle> snapshotParticles = new SnapshotArray<Particle>(particles);
		for (Particle particle : snapshotParticles) {
			particle.update();
			if (particle.dead()) {
				particles.removeValue(particle, false);
				particlePool.free(particle);
			} else {
				if (particle.getSprite() != null) sprites.draw(particle.getSprite().getFrame(particle.frame()),
					particle.getX(), particle.getY(), particle.getSprite().getWidth(), particle.getSprite().getHeight());
			}
		}
	}
}
