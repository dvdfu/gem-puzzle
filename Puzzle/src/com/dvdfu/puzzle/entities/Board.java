package com.dvdfu.puzzle.entities;

public class Board {
	private Block grid[][];
	private Block cursorBlock;
	private Special specials[][];
	private int width;
	private int height;
	private int cursorX;
	private int cursorY;

	public Board(String seed, int width, int height) {
		cursorX = 0;
		cursorY = 0;
		grid = new Block[width][height];
		specials = new Special[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				grid[i][j] = null;
				specials[i][j] = null;
			}
		}

		grid[0][0] = new Block().setGem(true, false, false, false, false, false);
		grid[1][0] = new Block().setGem(true, false, false, false, false, false);
		grid[2][0] = new Block().setGem(true, false, false, false, false, false);
		grid[3][0] = new Block().setGem(true, true, true, false, false, false);
		grid[4][0] = new Block().setGem(true, false, false, true, false, false);
		grid[0][1] = new Block().setGem(true, false, false, false, true, false);
		grid[1][1] = new Block().setGem(true, false, true, false, true, false);
		grid[2][1] = new Block().setGem(true, false, false, true, true, false);
		grid[3][2] = new Block().setGem(true, false, false, false, false, true);
		grid[4][1] = new Block().setGem(true, true, false, false, false, true);
		grid[0][2] = new Block().setGem(true, false, true, true, true, true);
		grid[1][2] = new Block().setActive(true, false);
		grid[2][3] = new Block().setGem(true, false, true, true, false, true);
		grid[3][4] = new Block().setGem(true, true, false, false, false, false);
		grid[4][5] = new Block().setGem(true, false, false, false, false, true);

		this.width = width;
		this.height = height;
		cursorBlock = null;
		addPath(3, 7, 0, 0);
		addPath(2, 1, 3, 1);
	}

	public void addPath(int x1, int y1, int x2, int y2) {
		if (gridValid(x1, y1) && gridValid(x2, y2) && !(x1 == x2 && y1 == y2)) {
			specials[x1][y1] = new Special().setPath(x2, y2);
			specials[x2][y2] = new Special().setPath(x1, y1);
		}
	}

	public void update() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Block block = grid[i][j];
				if (block != null && block.command == Block.Command.HOLD) {
					// moves blocks that can fall or are selected
					if (block.active) {
						if (block.fall && gridEmpty(i, j + 1)) block.command = Block.Command.FALL;
						else if (block.move && block == cursorBlock) moveBlockToCursor(i, j);
					}
					// marks gems for deletion
					// takes priority over moving blocks
					if (block.isGem()) {
						if (block.gemU && gridHas(i, j - 1) && grid[i][j - 1].gemD) {
							block.command = Block.Command.GEM;
							grid[i][j - 1].command = Block.Command.GEM;
						}
						if (block.gemD && gridHas(i, j + 1) && grid[i][j + 1].gemU) {
							block.command = Block.Command.GEM;
							grid[i][j + 1].command = Block.Command.GEM;
						}
						if (block.gemR && gridHas(i + 1, j) && grid[i + 1][j].gemL) {
							block.command = Block.Command.GEM;
							grid[i + 1][j].command = Block.Command.GEM;
						}
						if (block.gemL && gridHas(i - 1, j) && grid[i - 1][j].gemR) {
							block.command = Block.Command.GEM;
							grid[i - 1][j].command = Block.Command.GEM;
						}
						if (block.gemC) {
							if (gridHas(i, j + 1) && grid[i][j + 1].gemU && gridHas(i, j - 1) && grid[i][j - 1].gemD && gridHas(i + 1, j) && grid[i + 1][j].gemL && gridHas(i - 1, j) && grid[i - 1][j].gemR) {
								block.command = Block.Command.BIG_GEM;
								grid[i][j + 1].command = Block.Command.GEM;
								grid[i][j - 1].command = Block.Command.GEM;
								grid[i + 1][j].command = Block.Command.GEM;
								grid[i - 1][j].command = Block.Command.GEM;
							}
						}
					}
				}
			}
		}
	}

	private void moveBlockToCursor(int x, int y) {
		if (isBuffered() || !cursorBlock.active) return;
		if (cursorX < x && gridEmpty(x - 1, y)) grid[x][y].command = Block.Command.MOVE_LEFT;
		else if (cursorX > x && gridEmpty(x + 1, y)) grid[x][y].command = Block.Command.MOVE_RIGHT;
		else if (cursorBlock.active && !cursorBlock.fall) {
			if (cursorY < y && gridEmpty(x, y - 1)) grid[x][y].command = Block.Command.MOVE_UP;
			else if (cursorY > y && gridEmpty(x, y + 1) && !cursorBlock.fall) grid[x][y].command = Block.Command.MOVE_DOWN;
		}
	}

	private boolean gridHas(int x, int y) {
		return gridValid(x, y) && grid[x][y] != null;
	}

	private boolean gridEmpty(int x, int y) {
		return gridValid(x, y) && grid[x][y] == null;
	}

	private boolean gridValid(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}

	/* PUBLIC FINAL VIEW FUNCTIONS The following functions have public access
	 * and are intended for use by the viewer which retrieves grid information
	 * to draw */

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

	/* PUBLIC CONTROLLER FUNCTIONS The following functions have public access
	 * and are intended for use by the controller which allows the board to be
	 * manipulated */

	public final boolean isBuffered() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (grid[i][j] != null && grid[i][j].command != Block.Command.HOLD) { return true; }
			}
		}
		return false;
	}

	public void useBuffer() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (grid[i][j] != null) grid[i][j].visited = false;
			}
		}
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Block block = grid[i][j];
				if (block != null && !block.visited) {
					block.visited = true;
					boolean hold = true;
					switch (block.command) {
					case GEM:
						unselect();
						grid[i][j] = null;
						break;
					case BIG_GEM:
						unselect();
						grid[i][j] = null;
						break;
					case MOVE_UP:
						grid[i][j] = null;
						grid[i][j - 1] = block;
						Special pathU = specials[i][j - 1];
						if (pathU != null && pathU.path && gridEmpty(pathU.destX, pathU.destY)) {
							grid[i][j - 1].command = Block.Command.PATH_ENTER;
							hold = false;
						}
						break;
					case FALL:
					case MOVE_DOWN:
						grid[i][j] = null;
						grid[i][j + 1] = block;
						Special pathDown = specials[i][j + 1];
						if (pathDown != null && pathDown.path && gridEmpty(pathDown.destX, pathDown.destY)) {
							grid[i][j + 1].command = Block.Command.PATH_ENTER;
							hold = false;
						}
						break;
					case MOVE_RIGHT:
						grid[i][j] = null;
						grid[i + 1][j] = block;
						Special pathRight = specials[i + 1][j];
						if (pathRight != null && pathRight.path && gridEmpty(pathRight.destX, pathRight.destY)) {
							grid[i + 1][j].command = Block.Command.PATH_ENTER;
							hold = false;
						}
						break;
					case MOVE_LEFT:
						grid[i][j] = null;
						grid[i - 1][j] = block;
						Special pathLeft = specials[i - 1][j];
						if (pathLeft != null && pathLeft.path && gridEmpty(pathLeft.destX, pathLeft.destY)) {
							grid[i - 1][j].command = Block.Command.PATH_ENTER;
							hold = false;
						}
						break;
					case PATH_ENTER:
						grid[i][j] = null;
						grid[specials[i][j].destX][specials[i][j].destY] = block;
						grid[specials[i][j].destX][specials[i][j].destY].command = Block.Command.PATH_EXIT;
						hold = false;
						break;
					case PATH_EXIT:
						unselect();
						break;
					default:
						break;
					}
					if (hold) block.command = Block.Command.HOLD;
				}
			}
		}
	}

	public boolean select() {
		if (cursorBlock == null && !gridEmpty(cursorX, cursorY) && grid[cursorX][cursorY].move) {
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
}
