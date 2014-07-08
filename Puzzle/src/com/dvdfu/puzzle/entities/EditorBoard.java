package com.dvdfu.puzzle.entities;

import com.badlogic.gdx.Gdx;


public class EditorBoard {
	private Block grid[][];
	private Block cursorBlock;
	private Special specials[][];
	private int width;
	private int height;
	private int cursorX;
	private int cursorY;

	public EditorBoard(int width, int height) {
		this.width = width;
		this.height = height;
		cursorX = 0;
		cursorY = 0;
		grid = new Block[width][height];
		specials = new Special[width][height];
		reset();
	}

	public void reset() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				grid[i][j] = null;
				specials[i][j] = null;
			}
		}
		cursorBlock = null;
	}

	public void addBlock(Block block, int x, int y) {
		grid[x][y] = block;
	}

	public void addPath(int x1, int y1, int x2, int y2) {
		if (gridValid(x1, y1) && gridValid(x2, y2) && !(x1 == x2 && y1 == y2)) {
			specials[x1][y1] = new Special().setPath(x2, y2);
			specials[x2][y2] = new Special().setPath(x1, y1);
		}
	}

	public void addButton(int buttonX, int buttonY, int gateX, int gateY, boolean gateOriginal) {
		if (gridValid(buttonX, buttonY) && gridValid(gateX, gateY) && !(buttonX == gateX && buttonY == gateY)) {
			specials[buttonX][buttonY] = new Special().setButton();
			specials[gateX][gateY] = new Special().setGate(buttonX, buttonY, gateOriginal);
		}
	}

	private final boolean gridHas(int x, int y) {
		return gridValid(x, y) && grid[x][y] != null;
	}

	public final boolean gridEmpty(int x, int y) {
		return gridValid(x, y)
			&& grid[x][y] != null && grid[x][y].command == Block.Command.FALL;
	}

	public final boolean gridValid(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}
	
	/* PUBLIC FINAL VIEW FUNCTIONS The following functions have public access and are intended for use by the viewer which retrieves grid information to draw */

	public final Block[][] getGrid() {
		return grid;
	}

	public final Special[][] getSpecial() {
		return specials;
	}

	public final int getWidth() {
		return width;
	}

	public final int getHeight() {
		return height;
	}

	public final boolean isSelected() {
		return cursorBlock != null;
	}

	public final int getCursorX() {
		return cursorX;
	}

	public final int getCursorY() {
		return cursorY;
	}
	
	public boolean select() {
		if (cursorBlock == null && gridHas(cursorX, cursorY) && grid[cursorX][cursorY].move) {
			cursorBlock = grid[cursorX][cursorY];
			return true;
		}
		return false;
	}

	public boolean unselect() {
		if (isSelected()) {
			cursorBlock = null;
			return true;
		}
		return false;
	}

	public void setCursor(int x, int y) {
		if (x < 0) cursorX = 0;
		else if (x >= width) cursorX = width - 1;
		else cursorX = x;

		if (y < 0) cursorY = 0;
		else if (y >= height) cursorY = height - 1;
		else cursorY = y;
	}
	
	public void placeBlock() {
		if (Gdx.input.justTouched() && gridEmpty(cursorX, cursorY)) {
			grid[cursorX][cursorY] = new Block().setStatic();
		}
	}
	
	public void modifyBlock() {
	}
}
