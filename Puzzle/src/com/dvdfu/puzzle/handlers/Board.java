package com.dvdfu.puzzle.handlers;


public class Board {
	private Block grid[][];
	private int buffer[][];
	private int width;
	private int height;
	private Block selectedBlock;
	private int cursorX;
	private int cursorY;

	public Board(String seed, int width, int height) {
		String seedTrim = seed.replaceAll("\\s","");
		cursorX = 0;
		cursorY = 0;
		grid = new Block[width][height];
		buffer = new int[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				grid[i][j] = null;
				buffer[i][j] = -1;
			}
		}
		for (int i = 0; i < seedTrim.length(); i += 2) {
			String blockSeed = seedTrim.substring(i, i + 2);
			int value = Integer.parseInt(blockSeed, 16);
			boolean[] b = new boolean[8];
			for (int j = 0; j < 8; j++) {
				b[j] = (value & 1 << j) != 0;
			}
			if (b[7]) {
				grid[(i / 2) % width][height - 1 - (i / 2) / width] = new Block(b[0], b[1], b[2], b[3], b[4], b[5], b[6]);
			}
		}
		this.width = width;
		this.height = height;
		selectedBlock = null;
	}

	/**
	 * Called whenever the cursor moves. Checks every block to see if there are
	 * blocks that need to be updated by moving down. Also checks if there are
	 * blocks waiting to be moved by cursor. All empty blocks are set to
	 * unbuffered
	 */
	private void updateBlocks() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Block currentBlock = grid[i][j];
				if (currentBlock == null) {
					buffer[i][j] = -1;
				} else {
					if (currentBlock.falls() && gridEmpty(i, j - 1)) {
						buffer[i][j] = 4;
					} else if (currentBlock == selectedBlock) {
						moveBlockToCursor(i, j);
					}
				}
			}
		}
	}

	/**
	 * Called whenever a block is waiting to be moved by the cursor. Moves such
	 * a block one tile in the direction of the cursor. Prioritizes horizontal
	 * movement over vertical. Will NOT act when buffered
	 */
	private void moveBlockToCursor(int i, int j) {
		if (!isBuffered()) {
			if (cursorX < i && gridEmpty(i - 1, j)) {
				buffer[i][j] = 1;
			} else if (cursorX > i && gridEmpty(i + 1, j)) {
				buffer[i][j] = 2;
			} else if (cursorY > j && gridEmpty(i, j + 1) && !selectedBlock.falls()) {
				buffer[i][j] = 3;
			} else if (cursorY < j && gridEmpty(i, j - 1) && !selectedBlock.falls()) {
				buffer[i][j] = 4;
			}
		}
	}

	/**
	 * Called whenever cursor moves. Creates an array of all blocks and returns
	 * true where a block needs to be removed.
	 */
	private void clearBlocks() {
		boolean[][] death = new boolean[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				death[i][j] = false;
			}
		}
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Block currentBlock = grid[i][j];
				if (currentBlock != null && currentBlock.used()) {
					if (currentBlock.hasU() && gridHas(i, j + 1) && grid[i][j + 1].hasD()) {
						death[i][j] = true;
						death[i][j + 1] = true;
					}
					if (currentBlock.hasD() && gridHas(i, j - 1) && grid[i][j - 1].hasU()) {
						death[i][j] = true;
						death[i][j - 1] = true;
					}
					if (currentBlock.hasR() && gridHas(i + 1, j) && grid[i + 1][j].hasL()) {
						death[i][j] = true;
						death[i + 1][j] = true;
					}
					if (currentBlock.hasL() && gridHas(i - 1, j) && grid[i - 1][j].hasR()) {
						death[i][j] = true;
						death[i - 1][j] = true;
					}
					if (currentBlock.hasC()) {
						if (gridHas(i, j + 1) && grid[i][j + 1].hasD() && gridHas(i, j - 1) && grid[i][j - 1].hasU() && gridHas(i + 1, j) && grid[i + 1][j].hasL() && gridHas(i - 1, j) && grid[i - 1][j].hasR()) {
							death[i][j] = true;
							death[i][j + 1] = true;
							death[i][j - 1] = true;
							death[i + 1][j] = true;
							death[i - 1][j] = true;
						}
					}
				}
			}
		}
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (death[i][j]) {
					buffer[i][j] = 0;
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

	public final int[][] getBuffer() {
		return buffer;
	}

	public final boolean isBuffered() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (buffer[i][j] >= 0) {
					return true;
				}
			}
		}
		return false;
	}

	public final int getWidth() {
		return width;
	}

	public final int getHeight() {
		return height;
	}

	public final boolean isSelected() {
		return selectedBlock != null;
	}

	/**
	 * PUBLIC CONTROLLER FUNCTIONS The following functions have public access
	 * and are intended for use by the controller which allows the board to be
	 * manipulated
	 */

	public void useBuffer() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Block currentBlock = grid[i][j];
				switch (buffer[i][j]) {
				case 0: // destroy
					if (currentBlock == selectedBlock) {
						unselect();
					}
					grid[i][j] = null;
					break;
				case 1: // left
					grid[i][j] = null;
					grid[i - 1][j] = currentBlock;
					break;
				case 2: // right
					grid[i][j] = null;
					grid[i + 1][j] = currentBlock;
					break;
				case 3: // up
					grid[i][j] = null;
					grid[i][j + 1] = currentBlock;
					break;
				case 4: // down
					grid[i][j] = null;
					grid[i][j - 1] = currentBlock;
					break;
				}
			}
		}
	}

	public boolean select() {
		if (!gridEmpty(cursorX, cursorY) && selectedBlock == null && grid[cursorX][cursorY].moves()) {
			selectedBlock = grid[cursorX][cursorY];
			return true;
		}
		return false;
	}

	public boolean unselect() {
		if (isSelected()) {
			selectedBlock = null;
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
