package com.dvdfu.gems.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dvdfu.gems.MainGame;
import com.dvdfu.gems.abstracts.Block;
import com.dvdfu.gems.abstracts.Board;
import com.dvdfu.gems.abstracts.Special;
import com.dvdfu.gems.references.Assets;
import com.dvdfu.gems.references.Input;
import com.dvdfu.gems.references.Res;
import com.dvdfu.gems.visuals.Animation;
import com.dvdfu.gems.visuals.Particle;

public class PlayScreen extends AbstractScreen {
	private Board board;
	private SpriteBatch sprites;
	private Viewport viewport;
	private Array<Particle> particles;
	private Pool<Particle> particlePool;
	private int timer;
	private int boardOffsetX;
	private int boardOffsetY;

	public PlayScreen(MainGame game) {
		super(game);
		createBoard();
		sprites = new SpriteBatch();
		viewport = new ScreenViewport();
		timer = 0;
		particles = new Array<Particle>();
		particlePool = new Pool<Particle>() {
			protected Particle newObject() {
				return new Particle();
			}
		};
	}

	private void createBoard() {
		board = new Board("", 1, 1);
		Preferences prefs = Gdx.app.getPreferences("prefs");
		String data = prefs.getString("level", "-;1;1;");
		board.setState(data);
	}

	private void createParticle(Res.Part type, int x, int y) {
		createParticle(type, x, y, 0, 0, 1);
	}

	private void createParticle(Res.Part type, int x, int y, int num) {
		createParticle(type, x, y, 0, 0, num);
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
				sprite = Assets.sparkle;
				newParticle.setVector(MathUtils.random(2f), MathUtils.random(2 * MathUtils.PI));
				newParticle.setDuration(MathUtils.random(20), 12);
				break;
			case DIRT:
				sprite = Assets.dirt;
				newParticle.setVelocity(MathUtils.random(-1.5f, 1.5f), MathUtils.random(1f, 3f));
				newParticle.setAcceleration(0, -0.1f);
				newParticle.setDuration(MathUtils.random(20), 12);
				break;
			case DUST:
				sprite = Assets.dust;
				newParticle.setVector(MathUtils.random(3f), MathUtils.random(2 * MathUtils.PI));
				newParticle.setDuration(MathUtils.random(12), 6);
				break;
			case DUST_L:
				sprite = Assets.dust;
				newParticle.setVelocity(MathUtils.random(-2f, 0), MathUtils.random(0.2f, 0.5f));
				newParticle.setDuration(MathUtils.random(12), 6);
				break;
			case DUST_R:
				sprite = Assets.dust;
				newParticle.setVelocity(MathUtils.random(0, 2f), MathUtils.random(0.2f, 0.5f));
				newParticle.setDuration(MathUtils.random(12), 6);
				break;
			case DROP:
				sprite = Assets.droplet;
				newParticle.setVector(MathUtils.random(2f, 4f), MathUtils.random(MathUtils.PI / 3, MathUtils.PI * 2 / 3));
				newParticle.setAcceleration(0, -0.1f);
				newParticle.setDuration(MathUtils.random(20), 12);
				break;
			case GEM:
				sprite = Assets.blockGem;
				newParticle.setVelocity(0, 3.2f);
				newParticle.setAcceleration(0, -0.1f);
				newParticle.setDuration(0, 8);
				break;
			case FIRE:
				if (MathUtils.randomBoolean()) sprite = Assets.fireSmall;
				else sprite = Assets.fireBig;
				newParticle.setVector(MathUtils.random(2f), MathUtils.random(2 * MathUtils.PI));
				newParticle.setDuration(MathUtils.random(12), 8);
				break;
			case SINK:
				sprite = Assets.sink;
				newParticle.setDuration(0, 8);
				break;
			default:
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

	private void drawFullFront(Sprite sprite, int x, int y) {
		sprites.draw(sprite, x, y, Res.fullSize, Res.fullSize);
	}

	private void drawFullBack(Sprite sprite, int x, int y) {
		sprites.draw(sprite, x, y + Res.halfSize, Res.fullSize, Res.fullSize);
	}

	private void drawHalfTop(Sprite sprite, int x, int y) {
		sprites.draw(sprite, x, y + Res.fullSize, Res.fullSize, Res.halfSize);
	}

	private void drawGrid() {
		for (int i = 0; i < board.getWidth(); i++) {
			for (int j = 0; j < board.getHeight(); j++) {
				int drawX = boardOffsetX + i * Res.fullSize;
				int drawY = boardOffsetY + (board.getHeight() - 1 - j) * Res.fullSize;
				drawFullBack(Assets.grid, drawX, drawY);
				Special special = board.getSpecial()[i][j];
				if (special != null) {
					if (special.path) drawFullBack(Assets.path, drawX, drawY);
					if (special.gate && !special.toggled) drawFullBack(Assets.gateClosed, drawX, drawY);
				}
			}
		}
	}

	private void drawBlocksTop() {
		int[][] timer = board.getTimer();
		for (int i = 0; i < board.getWidth(); i++) {
			for (int j = board.getHeight() - 1; j >= 0; j--) {
				int drawX = boardOffsetX + i * Res.fullSize;
				int drawY = boardOffsetY + (board.getHeight() - 1 - j) * Res.fullSize;
				Block block = board.getGrid()[i][j];
				Special special = board.getSpecial()[i][j];
				if (block != null) {
					switch (block.command) {
					case MOVE_UP:
						drawY += (Res.timeMove - timer[i][j]) * Res.fullSize / Res.timeMove;
						break;
					case FALL:
					case MOVE_DOWN:
						drawY -= (Res.timeMove - timer[i][j]) * Res.fullSize / Res.timeMove;
						break;
					case MOVE_RIGHT:
						drawX += (Res.timeMove - timer[i][j]) * Res.fullSize / Res.timeMove;
						break;
					case MOVE_LEFT:
						drawX -= (Res.timeMove - timer[i][j]) * Res.fullSize / Res.timeMove;
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
					if (block.active) {
						if (block.move) drawHalfTop(Assets.blockMoveB, drawX, drawY);
						else drawHalfTop(Assets.blockStaticB, drawX, drawY);
					} else drawHalfTop(Assets.blockStaticB, drawX, drawY);
					setAlpha(1);
				}

				if (special != null) {
					if (special.water) {
						if (board.gridValid(i, j - 1)
							&& (board.getSpecial()[i][j - 1] == null || !board.getSpecial()[i][j - 1].water)) {
							drawHalfTop(Assets.waterHead, drawX, drawY);
						}
					} else if (special.gate && special.toggled) drawHalfTop(Assets.gateB, drawX, drawY);
				}
			}
		}
	}

	private void drawSpecialUnder() {
		for (int i = 0; i < board.getWidth(); i++) {
			for (int j = 0; j < board.getHeight(); j++) {
				int drawX = boardOffsetX + i * Res.fullSize;
				int drawY = boardOffsetY + (board.getHeight() - 1 - j) * Res.fullSize;
				Special special = board.getSpecial()[i][j];
				if (special != null) {
					if (special.button) {
						if (special.toggled) drawFullFront(Assets.button, drawX, drawY);
						else drawFullFront(Assets.button, drawX, drawY);
					} else if (special.gate && special.toggled) drawFullFront(Assets.gate, drawX, drawY);
				}
			}
		}
	}

	private void drawBlocks() {
		/* draw blocks at their position and transparency based on their properties and buffer timers */
		int[][] timer = board.getTimer();
		for (int i = 0; i < board.getWidth(); i++) {
			for (int j = board.getHeight() - 1; j >= 0; j--) {
				int drawX = boardOffsetX + i * Res.fullSize;
				int drawY = boardOffsetY + (board.getHeight() - 1 - j) * Res.fullSize;
				/* if (board.checkWind(i, j, 0)) { createParticle(Res.Part.WIND_R, drawX, drawY + Res.halfSize, Res.fullSize, Res.halfSize); } if (board.checkWind(i, j, 1)) { createParticle(Res.Part.WIND_U, drawX + Res.halfSize, drawY, Res.halfSize,
				 * Res.fullSize); } if (board.checkWind(i, j, 2)) { createParticle(Res.Part.WIND_L, drawX + Res.fullSize, drawY + Res.halfSize, Res.fullSize, Res.halfSize); } if (board.checkWind(i, j, 3)) { createParticle(Res.Part.WIND_D, drawX +
				 * Res.halfSize, drawY + Res.fullSize, Res.halfSize, Res.fullSize); } */
				Block block = board.getGrid()[i][j];
				Special special = board.getSpecial()[i][j];
				if (block != null) {
					switch (block.command) {
					case MOVE_UP:
						drawY += (Res.timeMove - timer[i][j]) * Res.fullSize / Res.timeMove;
						break;
					case FALL:
					case MOVE_DOWN:
						drawY -= (Res.timeMove - timer[i][j]) * Res.fullSize / Res.timeMove;
						break;
					case MOVE_RIGHT:
						drawX += (Res.timeMove - timer[i][j]) * Res.fullSize / Res.timeMove;
						break;
					case MOVE_LEFT:
						drawX -= (Res.timeMove - timer[i][j]) * Res.fullSize / Res.timeMove;
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
					if (block.move) {
						if (block.bomb) drawFullFront(Assets.bomb, drawX, drawY);
						else drawFullFront(Assets.blockMove, drawX, drawY);
					} else {
						if (block.active) drawFullFront(Assets.blockActive, drawX, drawY);
						else if (block.wind) {
							if (block.direction == 0) drawFullFront(Assets.windR, drawX, drawY);
							else if (block.direction == 1) drawFullFront(Assets.windU, drawX, drawY);
							else if (block.direction == 2) drawFullFront(Assets.windL, drawX, drawY);
							else if (block.direction == 3) drawFullFront(Assets.windD, drawX, drawY);
						} else drawFullFront(Assets.blockStatic, drawX, drawY);
					}

					if (block.gemC) drawFullFront(Assets.blockGemC, drawX, drawY);
					else {
						if (block.gemD) drawFullFront(Assets.blockGemD, drawX, drawY);
						if (block.gemU) drawFullFront(Assets.blockGemU, drawX, drawY);
						if (block.gemL) drawFullFront(Assets.blockGemL, drawX, drawY);
						if (block.gemR) drawFullFront(Assets.blockGemR, drawX, drawY);
					}
					if (block.fall) drawFullFront(Assets.blockFalling, drawX, drawY);

					if (block.command == Res.Command.BREAK) {
						sprites.draw(Assets.blockCrack.getFrame((Res.timeGem - timer[i][j]) * 7 / Res.timeGem), drawX, drawY);
						sprites.draw(Assets.blockCrack.getFrame((Res.timeGem - timer[i][j]) * 7 / Res.timeGem), drawX, drawY
							+ Res.fullSize, Res.fullSize, Res.halfSize);
					}
					setAlpha(1);
				}
			}
		}
	}

	private void drawGridOver() {
		for (int i = 0; i < board.getWidth(); i++) {
			for (int j = 0; j < board.getHeight(); j++) {
				int drawX = boardOffsetX + i * Res.fullSize;
				int drawY = boardOffsetY + (board.getHeight() - 1 - j) * Res.fullSize;
				Special special = board.getSpecial()[i][j];
				if (special != null) {
					if (special.water) drawFullFront(Assets.waterBody, drawX, drawY);
				}
			}
		}
	}

	private void drawCursor() {
		int cx = board.getCursorX();
		int cy = board.getCursorY();
		int cursorX = boardOffsetX + cx * Res.fullSize;
		int cursorY = boardOffsetY + (board.getHeight() - 1 - cy) * Res.fullSize;
		setAlpha(0.5f);
		drawFullFront(Assets.cursorSelect, cursorX, cursorY);
		setAlpha(0.25f);
		drawHalfTop(Assets.cursorSelect, cursorX, cursorY);
		setAlpha(1);
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
		drawFullFront(Assets.cursorUnselect, boardOffsetX + x2 * Res.fullSize, boardOffsetY + (board.getHeight() - 1 - y2)
			* Res.fullSize);
		int dx = (x2 - x1) * Res.fullSize;
		int dy = (y1 - y2) * Res.fullSize;
		int length = (int) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
		int numDots = length / 10;
		float angle = MathUtils.atan2(dy, dx);
		for (int i = 0; i <= numDots; i++) {
			float x = xo + ((i + timer / 16f) % numDots) * length * MathUtils.cos(angle) / numDots - 1;
			float y = yo + ((i + timer / 16f) % numDots) * length * MathUtils.sin(angle) / numDots - 1;
			sprites.draw(Assets.dot, x, y);
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

	public void updateCursor(float x, float y) {
		Vector3 mouse = new Vector3(x, y, 0);
		viewport.getCamera().unproject(mouse);

		int cursorX = (int) (mouse.x - boardOffsetX) / Res.fullSize;
		int cursorY = board.getHeight() - 1 - (int) (mouse.y - boardOffsetY) / Res.fullSize;
		board.setCursor(cursorX, cursorY);
		if (Input.MouseDown()) board.select();
		if (!Input.MouseDown()) board.unselect();
		if (Input.KeyPressed(Input.ENTER)) game.enterScreen(new EditorScreen(game));
		if (Input.KeyPressed(Input.SPACEBAR)) game.enterScreen(new PauseScreen(game));
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
						createParticle(Res.Part.SINK, drawX + Res.halfSize, drawY + Res.fullSize + 8, 16);
						createParticle(Res.Part.DROP, drawX + Res.halfSize, drawY + Res.fullSize, 16, 0, 16);
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
						if (j + 2 >= board.getHeight() || board.getGrid()[i][j + 2] != null
							&& board.getGrid()[i][j + 2].command == Res.Command.HOLD) {
							Special special = board.getSpecial()[i][j + 1];
							if (board.gridValid(i, j + 1) && (special == null || !special.water)) {
								createParticle(Res.Part.DUST_L, drawX + Res.halfSize, drawY - Res.fullSize, 4);
								createParticle(Res.Part.DUST_R, drawX + Res.halfSize, drawY - Res.fullSize, 4);
							}
						}
						break;
					case EXPLODE:
						createParticle(Res.Part.DUST, drawX + Res.halfSize, drawY + Res.halfSize, 16, 16, 16);
						createParticle(Res.Part.DIRT, drawX + Res.halfSize, drawY + Res.halfSize, 16, 16, 16);
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

	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(0.3f, 0.25f, 0.2f, 1);

		updateCursor(Input.mouse.x, Input.mouse.y);
		if (board.timerReady()) {
			endBuffer(); // apply end-buffer view changes to all buffered blocks
			board.useBuffer(); // apply end-buffer board changes to grid
			// at this point all blocks should have timer = 0 and command = hold
			board.update(); // timer is ready, board looks for buffers
			if (board.checkTimer()) beginBuffer();
		} else board.updateTimer();

		sprites.begin();
		drawGrid();
		drawBlocksTop();
		drawSpecialUnder();
		drawBlocks();
		drawGridOver();
		drawCursor();
		drawParticles();
		drawFullFront(Assets.path, -Gdx.graphics.getWidth() / 2, -Gdx.graphics.getHeight() / 2);
		drawFullFront(Assets.path, -Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 - 32);
		drawFullFront(Assets.path, Gdx.graphics.getWidth() / 2 - 32, -Gdx.graphics.getHeight() / 2);
		drawFullFront(Assets.path, Gdx.graphics.getWidth() / 2 - 32, Gdx.graphics.getHeight() / 2 - 32);
		sprites.end();
		if (timer < 16) timer++;
		else timer = 0;
	}

	public void resize(int width, int height) {
		boardOffsetX = -board.getWidth() * Res.fullSize / 2;
		boardOffsetY = (int) (-(board.getHeight() + 0.5f) * Res.fullSize / 2);
		viewport.update(width, height);
		sprites.setProjectionMatrix(viewport.getCamera().combined);
	}

	public void show() {}

	public void hide() {}

	public void pause() {}

	public void resume() {
		// createBoard();
	}

	public void dispose() {
		sprites.dispose();
	}
}
