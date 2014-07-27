package com.dvdfu.gems.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dvdfu.gems.MainGame;
import com.dvdfu.gems.handlers.Input;
import com.dvdfu.gems.handlers.Res;
import com.dvdfu.gems.model.EditorBlock;
import com.dvdfu.gems.model.EditorBoard;
import com.dvdfu.gems.model.Special;

public class EditorScreen extends AbstractScreen {
	private EditorBoard board;
	private SpriteBatch sprites;
	private Viewport viewport;
	private OrthographicCamera camera;
	private Vector3 mouse;
	private Button[] tools;
	private int boardOffsetX;
	private int boardOffsetY;
	private int timer;

	public EditorScreen(MainGame game) {
		super(game);
		board = new EditorBoard("", 1, 1);
		Preferences prefs = Gdx.app.getPreferences("prefs");
		String data = prefs.getString("level", "-;1;1;");
		board.setState(data);
		board.pushState();
		sprites = new SpriteBatch();
		viewport = new ScreenViewport();
		mouse = new Vector3();
		camera = (OrthographicCamera) viewport.getCamera();
		camera.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);
		timer = 0;
		tools = new Button[15];
		for (int i = 0; i < tools.length; i++) {
			tools[i] = new Button(64 + (i % 4) * 34, 80 - (i / 4) * 34, 32, 32);
			switch (i) {
			case 0:
				tools[i].type = Res.Cursors.BLOCK_STATIC;
				tools[i].filename = "block_static";
				break;
			case 1:
				tools[i].type = Res.Cursors.BLOCK_ACTIVE;
				tools[i].filename = "block_active";
				break;
			case 2:
				tools[i].type = Res.Cursors.BLOCK_MOVE;
				tools[i].filename = "block_move";
				break;
			case 3:
				tools[i].type = Res.Cursors.BOMB;
				tools[i].filename = "bomb";
				break;
			case 4:
				tools[i].type = Res.Cursors.WIND;
				tools[i].filename = "wind_u";
				break;
			case 5:
				tools[i].type = Res.Cursors.WATER;
				tools[i].filename = "water_body";
				break;
			case 6:
				tools[i].type = Res.Cursors.GATE;
				tools[i].filename = "gate";
				break;
			case 7:
				tools[i].type = Res.Cursors.PATH;
				tools[i].filename = "path";
				break;
			case 8:
				tools[i].type = Res.Cursors.GEM_UP;
				tools[i].filename = "block_gem_u";
				break;
			case 9:
				tools[i].type = Res.Cursors.GEM_DOWN;
				tools[i].filename = "block_gem_d";
				break;
			case 10:
				tools[i].type = Res.Cursors.GEM_RIGHT;
				tools[i].filename = "block_gem_r";
				break;
			case 11:
				tools[i].type = Res.Cursors.GEM_LEFT;
				tools[i].filename = "block_gem_l";
				break;
			case 12:
				tools[i].type = Res.Cursors.GEM_CENTER;
				tools[i].filename = "block_gem_c";
				break;
			case 13:
				tools[i].type = Res.Cursors.FALL;
				tools[i].filename = "block_falling";
				break;
			case 14:
				tools[i].type = Res.Cursors.ERASER;
				tools[i].filename = "trans";
				break;
			}
		}
	}

	private class Button {
		Res.Cursors type;
		private String filename;
		private int x;
		private int y;
		private int width;
		private int height;

		private Button(int x, int y, int width, int height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		private boolean hasMouse() {
			return mouse.x > x && mouse.x < x + width && mouse.y > y && mouse.y < y + height;
		}
	}

	public void dispose() {
		sprites.dispose();
	}

	private void setBoard(EditorBoard board) {
		this.board = board;
		boardOffsetX = (Gdx.graphics.getWidth() - board.getWidth() * Res.fullSize) / 2;
		boardOffsetY = (Gdx.graphics.getHeight() - board.getHeight() * Res.fullSize) / 6;
	}

	private void setAlpha(float alpha) {
		Color c = sprites.getColor();
		sprites.setColor(c.r, c.g, c.b, alpha);
	}

	/*private void drawBlocks() {
		for (int i = 0; i < board.getWidth(); i++) {
			for (int j = 0; j < board.getHeight(); j++) {
				int drawX = boardOffsetX + i * Res.fullSize;
				int drawY = boardOffsetY + (board.getHeight() - 1 - j) * Res.fullSize;
				EditorBlock block = board.getGrid()[i][j];
				Special special = board.getSpecial()[i][j];
				drawBlock("grid", drawX, drawY);
				if (block != null) {
					if (block.move) {
						if (block.bomb) drawBlock("bomb", drawX, drawY);
						else drawBlock("block_move", drawX, drawY);
					} else {
						if (block.active) drawBlock("block_active", drawX, drawY);
						else if (block.wind) {
							if (block.direction == 0) drawBlock("wind_r", drawX, drawY);
							else if (block.direction == 1) drawBlock("wind_u", drawX, drawY);
							else if (block.direction == 2) drawBlock("wind_l", drawX, drawY);
							else if (block.direction == 3) drawBlock("wind_d", drawX, drawY);
						} else drawBlock("block_static", drawX, drawY);
					}

					if (block.gemC) drawBlock("block_gem_c", drawX, drawY);
					else {
						if (block.gemD) drawBlock("block_gem_d", drawX, drawY);
						if (block.gemU) drawBlock("block_gem_u", drawX, drawY);
						if (block.gemL) drawBlock("block_gem_l", drawX, drawY);
						if (block.gemR) drawBlock("block_gem_r", drawX, drawY);
					}
					if (block.fall) drawBlock("block_falling", drawX, drawY);
				}
				if (special != null) {
					if (block != null) setAlpha(0.5f);
					if (special.path) drawBlock("path", drawX, drawY);
					else if (special.button) {
						if (special.toggled) drawBlock("button", drawX, drawY);
						else drawBlock("button", drawX, drawY);
					} else if (special.gate) {
						if (!special.original) setAlpha(0.5f);
						drawBlock("gate", drawX, drawY);
						setAlpha(1f);
					} else if (special.water) {
						drawBlock("water_body", drawX, drawY);
						if (board.gridValid(i, j - 1)
							&& (board.getSpecial()[i][j - 1] == null || !board.getSpecial()[i][j - 1].water)) drawBlock(
							"water_head", drawX, drawY + Res.fullSize);
					}
					setAlpha(1);
				}
			}
		}
	}

	private void drawBlock(Sprite sprite, int x, int y) {
		sprites.draw(sprite, x, y, Res.fullSize, Res.fullSize);
	}

	private void drawCursor() {
		int cx = board.getCursorX();
		int cy = board.getCursorY();
		int cursorX = boardOffsetX + cx * Res.fullSize;
		int cursorY = boardOffsetY + (board.getHeight() - 1 - cy) * Res.fullSize;
		if (board.isSelected()) drawBlock("cursor_select", cursorX, cursorY);
		else drawBlock("cursor_unselect", cursorX, cursorY);
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
		if (board.placingGate()) {
			drawBlock("button", cursorX, cursorY);
			drawBlock("gate", boardOffsetX + board.placeX() * Res.fullSize,
				boardOffsetY + (board.getHeight() - 1 - board.placeY()) * Res.fullSize);
			drawLine(cx, cy, board.placeX(), board.placeY());
		} else if (board.placingPath()) {
			drawBlock("path", cursorX, cursorY);
			drawBlock("path", boardOffsetX + board.placeX() * Res.fullSize,
				boardOffsetY + (board.getHeight() - 1 - board.placeY()) * Res.fullSize);
			drawLine(cx, cy, board.placeX(), board.placeY());
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

	private void drawGUI() {
		for (Button button : tools) {
			sprites.draw(Res.atlas.createSprite(button.filename), button.x, button.y, 32, 32);

			switch (button.type) {
			case BLOCK_ACTIVE:
			case BLOCK_MOVE:
			case BLOCK_STATIC:
			case BOMB:
			case WIND:
			case WATER:
			case GATE:
			case PATH:
			case ERASER:
				if (Input.MousePressed() && button.hasMouse()) board.setCursorState(button.type);
				if (board.getCursorState() == button.type) {
					sprites.draw(Res.atlas.createSprite("cursor_select"), button.x, button.y, 32, 32);
				}
				break;
			case GEM_UP:
				if (Input.MousePressed() && button.hasMouse()) board.cursorGemU ^= true;
				if (board.cursorGemU) {
					sprites.draw(Res.atlas.createSprite("cursor_select"), button.x, button.y, 32, 32);
				}
				break;
			case GEM_DOWN:
				if (Input.MousePressed() && button.hasMouse()) board.cursorGemD ^= true;
				if (board.cursorGemD) {
					sprites.draw(Res.atlas.createSprite("cursor_select"), button.x, button.y, 32, 32);
				}
				break;
			case GEM_RIGHT:
				if (Input.MousePressed() && button.hasMouse()) board.cursorGemR ^= true;
				if (board.cursorGemR) {
					sprites.draw(Res.atlas.createSprite("cursor_select"), button.x, button.y, 32, 32);
				}
				break;
			case GEM_LEFT:
				if (Input.MousePressed() && button.hasMouse()) board.cursorGemL ^= true;
				if (board.cursorGemL) {
					sprites.draw(Res.atlas.createSprite("cursor_select"), button.x, button.y, 32, 32);
				}
				break;
			case GEM_CENTER:
				if (Input.MousePressed() && button.hasMouse()) board.cursorGemC ^= true;
				if (board.cursorGemC) {
					sprites.draw(Res.atlas.createSprite("cursor_select"), button.x, button.y, 32, 32);
				}
				break;
			case FALL:
				if (Input.MousePressed() && button.hasMouse()) board.cursorFall ^= true;
				if (board.cursorFall) {
					sprites.draw(Res.atlas.createSprite("cursor_select"), button.x, button.y, 32, 32);
				}
			default:
				break;
			}
		}
	}*/

	private void update(float x, float y) {
		/* resets screen and projects sprite batch, shape renderer, mouse and camera based on the screen */
		mouse.set(x, y, 0);
		camera.unproject(mouse);

		/* sends mouse information to the board and plays appropriate sound effects */
		float cursorX = (mouse.x - boardOffsetX) / Res.fullSize;
		float cursorY = board.getHeight() - 1 - (mouse.y - boardOffsetY) / Res.fullSize;
		board.setCursor((int) cursorX, (int) (cursorY + 1));
		

		if (Input.KeyPressed(Input.ENTER)) {
			Preferences prefs = Gdx.app.getPreferences("prefs");
			String data = prefs.getString("level", "name;8;10;");
			board.setState(data);
			setBoard(board);
			game.exitScreen();
		}
	}

	public void resize(int width, int height) {
		int resolution = 1;
		int zoomW = resolution * height / Res.fullSize / (board.getHeight() + 1);
		int zoomH = resolution * width / Res.fullSize / (board.getWidth() + 1);
		boardOffsetX = (Gdx.graphics.getWidth() - board.getWidth() * Res.fullSize) / 2;
		boardOffsetY = (Gdx.graphics.getHeight() - board.getHeight() * Res.fullSize) / 2;
		camera.zoom = 1f * resolution / Math.min(zoomW, zoomH);
		camera.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);
		viewport.update(width, height);
		sprites.setProjectionMatrix(camera.combined);
	}

	public void render(float delta) {
		Gdx.gl.glClearColor(0.3f, 0.25f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		board.update();
		update(Input.mouse.x, Input.mouse.y);
		
		sprites.begin();
		sprites.end();
		if (timer < 16) timer++;
		else timer = 0;
	}

	public void show() {}

	public void hide() {}

	public void pause() {}

	public void resume() {}
}
