package com.dvdfu.puzzle.entities;

public class Board {
	private Block grid[][];
	private int width;
	private int height;
	private Block cursorBlock;
	private int cursorX;
	private int cursorY;

	public Board(String seed, int width, int height) {
		cursorX = 0;
		cursorY = 0;
		grid = new Block[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				grid[i][j] = null;
			}
		}
		grid[0][0] = new Block().setGem(true, false, false, false, false, false);
		grid[1][0] = new Block().setGem(true, false, false, false, false, false);
		grid[2][0] = new Block().setGem(true, false, false, false, false, false);
		grid[3][0] = new Block().setGem(true, false, true, false, false, false);
		grid[4][0] = new Block().setGem(true, false, false, true, false, false);
		grid[0][1] = new Block().setGem(true, false, false, false, true, false);
		grid[1][1] = new Block().setGem(true, false, true, false, true, false);
		grid[2][1] = new Block().setGem(true, false, false, true, true, false);
		grid[3][2] = new Block().setGem(true, false, false, false, false, true);
		grid[4][1] = new Block().setGem(true, false, false, false, false, true);
		grid[0][2] = new Block().setGem(true, false, true, true, false, true);
		this.width = width;
		this.height = height;
		cursorBlock = null;
	}

	/**
	 * Called whenever the cursor moves. Checks every block to see if there are
	 * blocks that need to be updated by moving down. Also checks if there are
	 * blocks waiting to be moved by cursor. All empty or static blocks are set
	 * to unbuffered
	 */
	private void updateBlocks() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Block block = grid[i][j];
				if (block != null && block.active) {
					if (block.fall && gridEmpty(i, j - 1)) {
						block.command = Block.Command.MOVE_DOWN;
					} else if (block.move && block == cursorBlock) {
						moveBlockToCursor(i, j);
					}
				} else {
					// buffer.add(new Action(Action.Command.HOLD, i, j));
				}
			}
		}
	}

	/**
	 * Called whenever a block is waiting to be moved by the cursor. Moves such
	 * a block one tile in the direction of the cursor. Prioritizes horizontal
	 * movement over vertical. Will NOT act when buffered
	 */
	private void moveBlockToCursor(int x, int y) {
		if (isBuffered() || !cursorBlock.active) {
			return;
		}
		if (cursorX < x && gridEmpty(x - 1, y)) {

			grid[x][y].command = Block.Command.MOVE_LEFT;
		} else if (cursorX > x && gridEmpty(x + 1, y)) {

			grid[x][y].command = Block.Command.MOVE_RIGHT;
			;
		} else if (cursorBlock.active) {
			if (cursorY > y && gridEmpty(x, y + 1) && !cursorBlock.fall) {
				grid[x][y].command = Block.Command.MOVE_UP;
			} else if (cursorY < y && gridEmpty(x, y - 1) && !cursorBlock.fall) {
				grid[x][y].command = Block.Command.MOVE_DOWN;
			}
		}
	}

	/**
	 * Called whenever cursor moves. Creates an array of all blocks and returns
	 * true where a block needs to be removed.
	 */
	private void clearBlocks() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Block block = grid[i][j];
				if (block != null && block.isGem()) {
					if (block.gemU && gridHas(i, j + 1) && grid[i][j + 1].gemD) {
						block.command = Block.Command.GEM;
						grid[i][j + 1].command = Block.Command.GEM;
					}
					if (block.gemD && gridHas(i, j - 1) && grid[i][j - 1].gemU) {
						block.command = Block.Command.GEM;
						grid[i][j - 1].command = Block.Command.GEM;
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
						if (gridHas(i, j + 1) && grid[i][j + 1].gemD && gridHas(i, j - 1) && grid[i][j - 1].gemU && gridHas(i + 1, j) && grid[i + 1][j].gemL && gridHas(i - 1, j) && grid[i - 1][j].gemR) {
							block.command = Block.Command.GEM;
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

	private boolean gridHas(int x, int y) {
		return gridValid(x, y) && grid[x][y] != null;
	}

	private boolean gridEmpty(int x, int y) {
		return gridValid(x, y) && grid[x][y] == null;
	}

	private boolean gridValid(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}

	/**
	 * PUBLIC FINAL VIEW FUNCTIONS The following functions have public access
	 * and are intended for use by the viewer which retrieves grid information
	 * to draw
	 */

	public final Block[][] getGrid() {
		return grid;
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

	public final boolean isBuffered() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (grid[i][j] != null && grid[i][j].command != Block.Command.HOLD) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * PUBLIC CONTROLLER FUNCTIONS The following functions have public access
	 * and are intended for use by the controller which allows the board to be
	 * manipulated
	 */

	public void useBuffer() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Block block = grid[i][j];
				if (block != null) {
					switch (block.command) {
					case GEM:
						if (block == cursorBlock) {
							unselect();
						}
						grid[i][j] = null;
						break;
					case BIG_GEM:
						if (block == cursorBlock) {
							unselect();
						}
						grid[i][j] = null;
						break;
					case MOVE_UP:
						grid[i][j] = null;
						grid[i][j + 1] = block;
						break;
					case MOVE_DOWN:
						grid[i][j] = null;
						grid[i][j - 1] = block;
						break;
					case MOVE_RIGHT:
						grid[i][j] = null;
						grid[i + 1][j] = block;
						break;
					case MOVE_LEFT:
						grid[i][j] = null;
						grid[i - 1][j] = block;
						break;
					case HOLD:
						break;
					}
					block.command = Block.Command.HOLD;
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
		if (x < 0) {
			cursorX = 0;
		} else if (x >= width) {
			cursorX = width - 1;
		} else {
			cursorX = x;
		}
		if (y < 0) {
			cursorY = 0;
		} else if (y >= height) {
			cursorY = height - 1;
		} else {
			cursorY = y;
		}
		updateBlocks();
		clearBlocks();
	}

	public int getCursorX() {
		return cursorX;
	}

	public int getCursorY() {
		return cursorY;
	}
}
