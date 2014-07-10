package com.dvdfu.gems.entities;

import com.dvdfu.gems.handlers.Vars;

public class Board {
	private Block grid[][];
	private Block cursorBlock;
	private Special specials[][];
	private int width;
	private int height;
	private int cursorX;
	private int cursorY;

	public Board(int width, int height) {
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
				if (i >= 5 && i < 8 && j >= 5) grid[i][j] = new Block("0");
				if (i < 5 && j == 9) specials[i][j] = new Special().setHazard();
				if (i == 5 && j >= 3 && j < 5) addButton(1, 5, i, j, true);
			}
		}
		addBlock(new Block().setGem(true, false, false, false, false, false), 2, 0);
		grid[3][0] = new Block().setGem(true, true, true, false, false, false);
		grid[4][0] = new Block().setGem(true, false, false, true, false, false);
		grid[0][1] = new Block().setGem(true, false, false, false, true, false);
		grid[1][1] = new Block().setGem(true, false, true, false, true, false);
		grid[2][1] = new Block().setGem(true, false, false, true, true, false);
		grid[3][2] = new Block().setGem(true, false, false, false, false, true);
		grid[4][1] = new Block().setGem(true, false, false, false, false, true);
		grid[0][2] = new Block().setGem(true, false, true, true, true, true);
		grid[1][2] = new Block().setActive(true, false);
		grid[2][3] = new Block().setGem(true, false, true, true, false, true);
		grid[3][4] = new Block().setGem(true, false, false, false, false, false);
		grid[5][0] = new Block().setActive(true, false);
		grid[5][1] = new Block().setActive(true, true);
		grid[5][2] = new Block().setActive(false, true);
		grid[7][2] = new Block().setBomb(false);

		cursorBlock = null;
		addPath(2, 1, 7, 4);
		/*for (int i = 0; i < height; i++) { for (int j = 0; j < width; j++) { Block block = grid[j][i]; if (block != null) System.out.print(block.getID()); else System.out.print("  "); } System.out.println(); } */
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

	public void update() {
		// handle button and gate toggling
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Special special = specials[i][j];
				if (special != null) {
					if (special.button) special.toggled = gridHas(i, j);
					else if (special.gate) {
						special.toggled = gridHas(special.destX, special.destY) ? !special.gateOriginal : special.gateOriginal;
					}
				}
			}
		}
		// check every block and special. Simply changes state, no modifications to the board
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Block block = grid[i][j];
				Special special = specials[i][j];
				if (block != null) {
					if (block.active) {
						// drops falling blocks
						if (block.fall && gridEmpty(i, j + 1) && block.command != Block.Command.BREAK) block.command = Block.Command.FALL;
						// destroys any fully surrounded block
						if (gridHas(i, j + 1) && grid[i][j + 1].gemU && gridHas(i, j - 1) && grid[i][j - 1].gemD
							&& gridHas(i + 1, j) && grid[i + 1][j].gemL && gridHas(i - 1, j) && grid[i - 1][j].gemR) {
							if (block.isGem()) block.command = Block.Command.BREAK;
							else block.command = Block.Command.BREAK;
							grid[i][j + 1].command = Block.Command.BREAK;
							grid[i][j - 1].command = Block.Command.BREAK;
							grid[i + 1][j].command = Block.Command.BREAK;
							grid[i - 1][j].command = Block.Command.BREAK;
						}
					}
					// destroys blocks caught in water or gates
					if (special != null) {
						if (special.hazard) block.command = Block.Command.DROWN;
						else if (special.gate && special.toggled) block.command = Block.Command.BREAK;
					}
					// checks for gem destruction, higher priority
					if (block.isGem()) {
						if (block.gemU && gridHas(i, j - 1) && grid[i][j - 1].gemD) {
							block.command = Block.Command.BREAK;
							grid[i][j - 1].command = Block.Command.BREAK;
						}
						if (block.gemD && gridHas(i, j + 1) && grid[i][j + 1].gemU) {
							block.command = Block.Command.BREAK;
							grid[i][j + 1].command = Block.Command.BREAK;
						}
						if (block.gemR && gridHas(i + 1, j) && grid[i + 1][j].gemL) {
							block.command = Block.Command.BREAK;
							grid[i + 1][j].command = Block.Command.BREAK;
						}
						if (block.gemL && gridHas(i - 1, j) && grid[i - 1][j].gemR) {
							block.command = Block.Command.BREAK;
							grid[i - 1][j].command = Block.Command.BREAK;
						}
					}
					// checks for bomb destruction
					else if (block.bomb) {
						if (gridHas(i, j - 1) && grid[i][j - 1].active) {
							block.command = Block.Command.EXPLODE;
							grid[i][j - 1].command = Block.Command.EXPLODE;
						}
						if (gridHas(i, j + 1) && grid[i][j + 1].active) {
							block.command = Block.Command.EXPLODE;
							grid[i][j + 1].command = Block.Command.EXPLODE;
						}
						if (gridHas(i - 1, j) && grid[i - 1][j].active) {
							block.command = Block.Command.EXPLODE;
							grid[i - 1][j].command = Block.Command.EXPLODE;
						}
						if (gridHas(i + 1, j) && grid[i + 1][j].active) {
							block.command = Block.Command.EXPLODE;
							grid[i + 1][j].command = Block.Command.EXPLODE;
						}
					}
				}
			}
		}
		if (!isBuffered()) {
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					Block block = grid[i][j];
					// moves selected block closer to cursor, lowest priority
					if (block != null && block == cursorBlock && cursorBlock.move) {
						if (cursorX < i && gridEmpty(i - 1, j)) block.command = Block.Command.MOVE_LEFT;
						else if (cursorX > i && gridEmpty(i + 1, j)) block.command = Block.Command.MOVE_RIGHT;
						else if (cursorBlock.active && !cursorBlock.fall) {
							if (cursorY < j && gridEmpty(i, j - 1)) block.command = Block.Command.MOVE_UP;
							else if (cursorY > j && gridEmpty(i, j + 1)) block.command = Block.Command.MOVE_DOWN;
						}
					}
				}
			}
		}
	}

	public final boolean gridHas(int x, int y) {
		return gridValid(x, y) && grid[x][y] != null;
	}

	public final boolean gridEmpty(int x, int y) {
		return gridValid(x, y)
			&& (grid[x][y] == null && !gridHasGate(x, y) || grid[x][y] != null && grid[x][y].command == Block.Command.FALL);
	}

	public final boolean gridValid(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}

	private final boolean gridHasGate(int x, int y) {
		return specials[x][y] != null && specials[x][y].gate && specials[x][y].toggled;
	}

	private boolean isBuffered() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Block block = grid[i][j];
				if (block != null && block.command != Block.Command.HOLD) return true;
			}
		}
		return false;
	}

	/* TIMER FUNCTIONS */

	public int[][] getTimer() {
		int[][] timer = new int[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				timer[i][j] = grid[i][j] != null ? grid[i][j].timer : 0;
			}
		}
		return timer;
	}

	public boolean checkTimer() {
		/* when the timer is ready, checks board to see if any of the timers need to be set */
		boolean modified = false;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Block block = grid[i][j];
				if (block != null) {
					switch (block.command) {
					case EXPLODE:
						block.timer = Vars.timeExplode;
						modified = true;
						break;
					case DROWN:
						block.timer = Vars.timeDrown;
						modified = true;
						break;
					case FALL:
					case MOVE_UP:
					case MOVE_DOWN:
					case MOVE_RIGHT:
					case MOVE_LEFT:
						block.timer = Vars.timeMove;
						modified = true;
						break;
					case BREAK:
						block.timer = Vars.timeGem;
						modified = true;
						break;
					case PATH:
						block.timer = Vars.timePath;
						modified = true;
						break;
					default:
						break;
					}
				}
			}
		}
		return modified;
	}

	public void updateTimer() {
		/* when timer is not ready, decrements all timer values */
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Block block = grid[i][j];
				if (block != null && block.timer > 0) block.timer--;
			}
		}
	}

	public boolean timerReady() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (grid[i][j] != null && grid[i][j].timer > 0) return false;
			}
		}
		return true;
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

	/* PUBLIC CONTROLLER FUNCTIONS The following functions have public access and are intended for use by the controller which allows the board to be manipulated */

	/* Used at the VERY end of a buffering sequence */
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
					case DROWN:
					case EXPLODE:
					case BREAK:
						if (block == cursorBlock) unselect();
						grid[i][j] = null;
						break;
					case MOVE_UP:
						grid[i][j] = null;
						grid[i][j - 1] = block;
						Special pathU = specials[i][j - 1];
						if (pathU != null && pathU.path && gridEmpty(pathU.destX, pathU.destY)) {
							grid[i][j - 1].command = Block.Command.PATH;
							hold = false;
						}
						break;
					case FALL:
					case MOVE_DOWN:
						grid[i][j] = null;
						grid[i][j + 1] = block;
						Special pathDown = specials[i][j + 1];
						if (pathDown != null && pathDown.path && gridEmpty(pathDown.destX, pathDown.destY)) {
							grid[i][j + 1].command = Block.Command.PATH;
							hold = false;
						}
						break;
					case MOVE_RIGHT:
						grid[i][j] = null;
						grid[i + 1][j] = block;
						Special pathRight = specials[i + 1][j];
						if (pathRight != null && pathRight.path && gridEmpty(pathRight.destX, pathRight.destY)) {
							grid[i + 1][j].command = Block.Command.PATH;
							hold = false;
						}
						break;
					case MOVE_LEFT:
						grid[i][j] = null;
						grid[i - 1][j] = block;
						Special pathLeft = specials[i - 1][j];
						if (pathLeft != null && pathLeft.path && gridEmpty(pathLeft.destX, pathLeft.destY)) {
							grid[i - 1][j].command = Block.Command.PATH;
							hold = false;
						}
						break;
					case PATH:
						unselect();
						grid[i][j] = null;
						grid[specials[i][j].destX][specials[i][j].destY] = block;
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
}
