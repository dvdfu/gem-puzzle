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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dvdfu.gems.handlers.Input;
import com.dvdfu.gems.handlers.Res;
import com.dvdfu.gems.model.Block;
import com.dvdfu.gems.model.Board;
import com.dvdfu.gems.model.Special;

public class View implements Screen {
	private Board board;
	private AssetManager assets = new AssetManager();
	private SpriteBatch sprites;
	private Viewport viewport;
	private OrthographicCamera camera;
	private Vector3 mouse;
	private int boardOffsetX;
	private int boardOffsetY;
	private Animation sparkle1;
	private Animation dirt1;
	private Animation dust1;
	private Animation drop;
	private Animation gem;
	private Animation cracks;
	private Animation fire;
	private Animation fire2;
	private Array<Particle> particles;
	private Pool<Particle> particlePool;
	private int timer;

	public View(Board board) {
		setBoard(board);
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

	public void setBoard(Board board) {
		this.board = board;
		boardOffsetX = (Gdx.graphics.getWidth() - board.getWidth() * Res.fullSize) / 2;
		boardOffsetY = (Gdx.graphics.getHeight() - board.getHeight() * Res.fullSize) / 6;
	}

	public void dispose() {
		assets.dispose();
		sprites.dispose();
	}

	private void loadAssets() {
		assets.load("img/path.png", Texture.class);
		assets.load("aud/select.wav", Sound.class);
		assets.load("aud/deselect.wav", Sound.class);
		assets.load("aud/remove.wav", Sound.class);
		assets.load("aud/splash.mp3", Sound.class);
		assets.load("aud/break.mp3", Sound.class);
		assets.finishLoading();
		sparkle1 = new Animation(Res.atlas.createSprite("particle_sparkle"), 16, 16);
		dirt1 = new Animation(Res.atlas.createSprite("particle_dirt"), 8, 8);
		dust1 = new Animation(Res.atlas.createSprite("particle_dust"), 8, 8);
		drop = new Animation(Res.atlas.createSprite("particle_droplet"), 8, 8);
		gem = new Animation(Res.atlas.createSprite("particle_gem"), 16, 16);
		cracks = new Animation(Res.atlas.createSprite("block_cracks"), 32, 32);
		fire = new Animation(Res.atlas.createSprite("particle_fire_small"), 4, 4);
		fire2 = new Animation(Res.atlas.createSprite("particle_fire_big"), 8, 8);
	}

	public void update(float x, float y) {
		/* resets screen and projects sprite batch, shape renderer, mouse and camera based on the screen */
		mouse.set(x, y, 0);
		sprites.setProjectionMatrix(camera.combined);
		camera.unproject(mouse);

		/* sends mouse information to the board and plays appropriate sound effects */
		int cursorX = (int) (mouse.x - boardOffsetX) / Res.fullSize;
		int cursorY = board.getHeight() - 1 - (int) (mouse.y - boardOffsetY) / Res.fullSize;
		board.setCursor(cursorX, cursorY);
		if (Input.MouseDown() && board.select()) assets.get("aud/select.wav", Sound.class).play();
		if (!Input.MouseDown() && board.unselect()) assets.get("aud/deselect.wav", Sound.class).play();

	}

	public void resize(int width, int height) {
		float zoomW = 1f * height / Res.fullSize / (board.getHeight() + 1);
		float zoomH = 1f * width / Res.fullSize / (board.getWidth() + 1);
		boardOffsetX = (Gdx.graphics.getWidth() - board.getWidth() * Res.fullSize) / 2;
		boardOffsetY = (Gdx.graphics.getHeight() - board.getHeight() * Res.fullSize) / 6;
		camera.zoom = 1f / Math.min(zoomW, zoomH);
		camera.position.set(boardOffsetX + board.getWidth() * Res.fullSize / 2, boardOffsetY + board.getHeight() * Res.fullSize
			/ 2, 0);
		viewport.update(width, height);
	}

	public void beginBuffer() {
		for (int i = 0; i < board.getWidth(); i++) {
			for (int j = 0; j < board.getHeight(); j++) {
				int drawX = boardOffsetX + i * Res.fullSize;
				int drawY = boardOffsetY + (board.getHeight() - 1 - j) * Res.fullSize;
				Block block = board.getGrid()[i][j];
				if (block != null) {
					switch (block.command) {
					case DROWN:
						createParticle(Res.Part.DROP, drawX + Res.halfSize, drawY + Res.fullSize, 16, 0, 16);
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
				int drawX = boardOffsetX + i * Res.fullSize;
				int drawY = boardOffsetY + (board.getHeight() - 1 - j) * Res.fullSize;
				Block block = board.getGrid()[i][j];
				if (block != null) {
					switch (block.command) {
					case FALL:
						if (j + 2 >= board.getHeight() || board.getGrid()[i][j + 2] != null && board.getGrid()[i][j + 2].command == Res.Command.HOLD) {
							Special special = board.getSpecial()[i][j + 1];
							if (board.gridValid(i, j + 1) && (special == null || !special.water)) {
								createParticle(Res.Part.DUST_L, drawX + Res.halfSize, drawY - Res.fullSize, 4);
								createParticle(Res.Part.DUST_R, drawX + Res.halfSize, drawY - Res.fullSize, 4);
								assets.get("aud/remove.wav", Sound.class).play(0.1f);
							}
						}
						break;
					case EXPLODE:
						createParticle(Res.Part.DUST, drawX + Res.halfSize, drawY + Res.halfSize, 16, 16, 16);
						createParticle(Res.Part.FIRE, drawX + Res.halfSize, drawY + Res.halfSize, 4, 4, 32);
						if (block.isGem()) {
							createParticle(Res.Part.SPARKLE, drawX + Res.halfSize, drawY + Res.halfSize, 16, 16, 8);
							createParticle(Res.Part.GEM, drawX + Res.halfSize, drawY + Res.halfSize);
						}
						break;
					case BREAK:
						createParticle(Res.Part.DIRT, drawX + Res.halfSize, drawY + Res.halfSize, 16, 16, 16);
						if (block.isGem()) {
							createParticle(Res.Part.SPARKLE, drawX + Res.halfSize, drawY + Res.halfSize, 16, 16, 8);
							createParticle(Res.Part.GEM, drawX + Res.halfSize, drawY + Res.halfSize);
						}
						assets.get("aud/break.mp3", Sound.class).play();
						break;
					case DROWN:
						if (block.isGem()) {
							createParticle(Res.Part.SPARKLE, drawX + Res.halfSize, drawY + Res.halfSize, 16, 16, 8);
							createParticle(Res.Part.GEM, drawX + Res.halfSize, drawY + Res.halfSize);
						}
						break;
					default:
						break;
					}
				}
			}
		}
	}

	private void createParticle(Res.Part type, int x, int y) {
		createParticle(type, x, y, 0, 0, 1);
	}

	private void createParticle(Res.Part type, int x, int y, int num) {
		createParticle(type, x, y, 0, 0, num);
	}

	private void createParticle(Res.Part type, int x, int y, int randX, int randY) {
		createParticle(type, x, y, randX, randY, 1);
	}

	private void createParticle(Res.Part type, int x, int y, int randX, int randY, int num) {
		for (int i = 0; i < num; i++) {
			Particle newParticle = particlePool.obtain();
			newParticle.type = type;
			Animation sprite = null;
			int xr = x + MathUtils.random(-randX, randX);
			int yr = y + MathUtils.random(-randY, randY);
			switch (type) {
			case SPARKLE:
				sprite = sparkle1;
				newParticle.setVector(MathUtils.random(2f), MathUtils.random(2 * MathUtils.PI));
				newParticle.setDuration(MathUtils.random(20), 12);
				break;
			case DIRT:
				sprite = dirt1;
				newParticle.setVelocity(MathUtils.random(-1.5f, 1.5f), MathUtils.random(1f, 3f));
				newParticle.setAcceleration(0, -0.1f);
				newParticle.setDuration(MathUtils.random(20), 12);
				break;
			case DUST:
				sprite = dust1;
				newParticle.setVector(MathUtils.random(3f), MathUtils.random(2 * MathUtils.PI));
				newParticle.setDuration(MathUtils.random(12), 6);
				break;
			case DUST_L:
				sprite = dust1;
				newParticle.setVelocity(MathUtils.random(-2f, 0), MathUtils.random(0.2f, 0.5f));
				newParticle.setDuration(MathUtils.random(12), 6);
				break;
			case DUST_R:
				sprite = dust1;
				newParticle.setVelocity(MathUtils.random(0, 2f), MathUtils.random(0.2f, 0.5f));
				newParticle.setDuration(MathUtils.random(12), 6);
				break;
			case DROP:
				sprite = drop;
				newParticle.setVelocity(MathUtils.random(-1.5f, 1.5f), MathUtils.random(1f, 4f));
				newParticle.setAcceleration(0, -0.1f);
				newParticle.setDuration(MathUtils.random(20), 12);
				break;
			case GEM:
				sprite = gem;
				newParticle.setVelocity(0, 3.2f);
				newParticle.setAcceleration(0, -0.1f);
				newParticle.setDuration(0, 8);
				break;
			case FIRE:
				if (MathUtils.randomBoolean()) sprite = fire;
				else sprite = fire2;
				newParticle.setVector(MathUtils.random(2f), MathUtils.random(2 * MathUtils.PI));
				newParticle.setDuration(MathUtils.random(12), 8);
				break;
			}
			newParticle.setPosition(xr - sprite.getWidth() / 2, yr - sprite.getHeight() / 2);
			newParticle.setSprite(sprite);
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
				int drawX = boardOffsetX + i * Res.fullSize;
				int drawY = boardOffsetY + (board.getHeight() - 1 - j) * Res.fullSize;
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
				int drawX = boardOffsetX + i * Res.fullSize;
				int drawY = boardOffsetY + (board.getHeight() - 1 - j) * Res.fullSize;
				if (board.checkWind(i, j, 0) && MathUtils.randomBoolean(1 / 4f)) {
					createParticle(Res.Part.DUST_R, drawX, drawY + Res.halfSize, Res.fullSize, Res.halfSize);
				}
				Block block = board.getGrid()[i][j];
				Special special = board.getSpecial()[i][j];
				if (block != null) {
					int[][] timer = board.getTimer();
					int bufferX = 0;
					int bufferY = 0;
					switch (block.command) {
					case MOVE_UP:
						bufferY = (Res.timeMove - timer[i][j]) * Res.fullSize / Res.timeMove;
						break;
					case FALL:
					case MOVE_DOWN:
						bufferY = -(Res.timeMove - timer[i][j]) * Res.fullSize / Res.timeMove;
						break;
					case MOVE_RIGHT:
						bufferX = (Res.timeMove - timer[i][j]) * Res.fullSize / Res.timeMove;
						break;
					case MOVE_LEFT:
						bufferX = -(Res.timeMove - timer[i][j]) * Res.fullSize / Res.timeMove;
						break;
					case PATH:
						setAlpha(Math.abs(1 - 2f * timer[i][j] / Res.timePath));
						if (timer[i][j] * 2 < Res.timePath) {
							drawX = boardOffsetX + special.destX * Res.fullSize;
							drawY = boardOffsetY + (board.getHeight() - 1 - special.destY) * Res.fullSize;
						}
						break;
					default:
						break;
					}
					drawY += bufferY;
					drawX += bufferX;
					if (block.move) {
						if (block.bomb) drawBlock("bomb", drawX, drawY);
						else drawBlock("block_move", drawX, drawY);
					} else {
						if (block.destructable) drawBlock("block_active", drawX, drawY);
						else if (block.wind) {
							if (block.direction == 0) drawBlock("wind_r", drawX, drawY);
							else if (block.direction == 1) drawBlock("wind_u", drawX, drawY);
							else if (block.direction == 2) drawBlock("wind_l", drawX, drawY);
							else if (block.direction == 3) drawBlock("wind_d", drawX, drawY);
						}
						else drawBlock("block_static", drawX, drawY);
					}

					if (block.gemC) drawBlock("block_gem_c", drawX, drawY);
					else {
						if (block.gemD) drawBlock("block_gem_d", drawX, drawY);
						if (block.gemU) drawBlock("block_gem_u", drawX, drawY);
						if (block.gemL) drawBlock("block_gem_l", drawX, drawY);
						if (block.gemR) drawBlock("block_gem_r", drawX, drawY);
					}
					if (block.fall) drawBlock("block_falling", drawX, drawY);

					if (block.command == Res.Command.BREAK) {
						sprites.draw(cracks.getFrame((Res.timeGem - timer[i][j]) * 7 / Res.timeGem), drawX, drawY);
					}
					setAlpha(1);
				}
			}
		}
	}

	private void drawBlock(String file, int x, int y) {
		sprites.draw(Res.atlas.createSprite(file), x, y, Res.fullSize, Res.fullSize);
	}

	private void drawGridOver() {
		for (int i = 0; i < board.getWidth(); i++) {
			for (int j = 0; j < board.getHeight(); j++) {
				int drawX = boardOffsetX + i * Res.fullSize;
				int drawY = boardOffsetY + (board.getHeight() - 1 - j) * Res.fullSize;
				Special special = board.getSpecial()[i][j];
				if (special != null) {
					if (special.water) {
						drawBlock("water_body", drawX, drawY);
						if (board.gridValid(i, j - 1) && (board.getSpecial()[i][j - 1] == null || !board.getSpecial()[i][j - 1].water)) {
							drawBlock("water_head", drawX, drawY + Res.fullSize);
						}
					}
				}
			}
		}
	}

	private void drawCursor() {
		int cx = board.getCursorX();
		int cy = board.getCursorY();
		int cursorX = boardOffsetX + cx * Res.fullSize;
		int cursorY = boardOffsetY + (board.getHeight() - 1 - cy) * Res.fullSize;
		if (board.isSelected()) drawBlock("cursor_select", cursorX, cursorY);
		else drawBlock("cursor_unselect", cursorX, cursorY);
		Special special = board.getSpecial()[cx][cy];
		if (special != null && !board.isSelected()) {
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
		int xo = boardOffsetX + x1 * Res.fullSize + Res.halfSize;
		int yo = boardOffsetY + (board.getHeight() - 1 - y1) * Res.fullSize + Res.halfSize;
		drawBlock("cursor_unselect", boardOffsetX + x2 * Res.fullSize, boardOffsetY + (board.getHeight() - 1 - y2)
			* Res.fullSize);
		int dx = (x2 - x1) * Res.fullSize;
		int dy = (y1 - y2) * Res.fullSize;
		int length = (int) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
		int numDots = length / 10;
		float angle = MathUtils.atan2(dy, dx);
		for (int i = 0; i <= numDots; i++) {
			float x = xo + ((i + timer / 16f) % numDots) * length * MathUtils.cos(angle) / numDots - 1;
			float y = yo + ((i + timer / 16f) % numDots) * length * MathUtils.sin(angle) / numDots - 1;
			sprites.draw(Res.atlas.createSprite("dot"), x, y);
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

	public void render(float delta) {
		Gdx.gl.glClearColor(0.3f, 0.25f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		sprites.begin();
		drawGridUnder();
		drawBlocks();
		drawGridOver();
		drawCursor();
		drawParticles();
		sprites.end();
		if (timer < 16) timer++;
		else timer = 0;
	}

	public void show() {}

	public void hide() {}

	public void pause() {}

	public void resume() {}
}
