package com.dvdfu.gems.model;

import com.badlogic.gdx.math.MathUtils;
import com.dvdfu.gems.handlers.Vars;

public class Board {
	private String name;
	private Block gridBlock[][];
	private Block cursorBlock;
	private Special gridSpecial[][];
	private int width;
	private int height;
	private int cursorX;
	private int cursorY;

	public Board(String name, int width, int height) {
		this.name = name;
		this.width = width;
		this.height = height;
		cursorX = 0;
		cursorY = 0;
		gridBlock = new Block[width][height];
		gridSpecial = new Special[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				gridBlock[i][j] = null;
				gridSpecial[i][j] = null;
			}
		}
	}
	
	public void resetBoard(String name, int width, int height) {
		this.name = name;
		this.width = width;
		this.height = height;
		gridBlock = new Block[width][height];
		gridSpecial = new Special[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				gridBlock[i][j] = null;
				gridSpecial[i][j] = null;
			}
		}
	}

	public void addByID(char[] id) {
		int x = Character.getNumericValue(id[0]);
		int y = Character.getNumericValue(id[1]);
		if (id[2] == 'b') {
			Block block = new Block();
			for (int i = 3; i < id.length; i++) {
				switch (id[i]) {
				case 'a':
					block.active = true;
					break;
				case 'm':
					block.move = true;
					break;
				case 'f':
					block.fall = true;
					break;
				case 'b':
					block.bomb = true;
					break;
				case 'c':
					block.gemC = true;
					break;
				case 'u':
					block.gemU = true;
					break;
				case 'd':
					block.gemD = true;
					break;
				case 'r':
					block.gemR = true;
					break;
				case 'l':
					block.gemL = true;
					break;
				}
			}
			gridBlock[x][y] = block;
		} else if (id[2] == 's') {
			Special special = null;
			int destX;
			int destY;
			switch (id[3]) {
			case 'p':
				destX = id[4] - 48;
				destY = id[5] - 48;
				special = new Special().setPath(destX, destY);
				break;
			case 'h':
				special = new Special().setHazard();
				break;
			case 'b':
				special = new Special().setButton();
				break;
			case 'g':
				boolean gateOriginal = id[6] == 't';
				destX = id[4] - 48;
				destY = id[5] - 48;
				special = new Special().setGate(destX, destY, gateOriginal);
				break;
			}
			gridSpecial[x][y] = special;
		}
	}

	public void update() {
		// handle button and gate toggling
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Special special = gridSpecial[i][j];
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
				Block block = gridBlock[i][j];
				Special special = gridSpecial[i][j];
				if (block != null) {
					if (block.active) {
						// drops block if it falls
						if (block.fall && gridEmpty(i, j + 1) && block.command != Block.Command.BREAK) block.command = Block.Command.FALL;
						// destroy block if it is active and surrounded
						if (gridHas(i, j + 1) && gridBlock[i][j + 1].gemU && gridHas(i, j - 1) && gridBlock[i][j - 1].gemD
							&& gridHas(i + 1, j) && gridBlock[i + 1][j].gemL && gridHas(i - 1, j) && gridBlock[i - 1][j].gemR) {
							if (block.isGem()) block.command = Block.Command.BREAK;
							else block.command = Block.Command.BREAK;
							gridBlock[i][j + 1].command = Block.Command.BREAK;
							gridBlock[i][j - 1].command = Block.Command.BREAK;
							gridBlock[i + 1][j].command = Block.Command.BREAK;
							gridBlock[i - 1][j].command = Block.Command.BREAK;
						}
					}
					// destroy block if in toggled gate or water
					if (special != null) {
						if (special.hazard) block.command = Block.Command.DROWN;
						else if (special.gate && special.toggled) block.command = Block.Command.BREAK;
					}
					// destroy block if it is a gem and matched
					if (block.isGem()) {
						if (block.gemU && gridHas(i, j - 1) && gridBlock[i][j - 1].gemD) {
							block.command = Block.Command.BREAK;
							gridBlock[i][j - 1].command = Block.Command.BREAK;
						}
						if (block.gemD && gridHas(i, j + 1) && gridBlock[i][j + 1].gemU) {
							block.command = Block.Command.BREAK;
							gridBlock[i][j + 1].command = Block.Command.BREAK;
						}
						if (block.gemR && gridHas(i + 1, j) && gridBlock[i + 1][j].gemL) {
							block.command = Block.Command.BREAK;
							gridBlock[i + 1][j].command = Block.Command.BREAK;
						}
						if (block.gemL && gridHas(i - 1, j) && gridBlock[i - 1][j].gemR) {
							block.command = Block.Command.BREAK;
							gridBlock[i - 1][j].command = Block.Command.BREAK;
						}
					}
					// destroy block if it is a bomb and matched
					else if (block.bomb) {
						if (gridHas(i, j - 1) && gridBlock[i][j - 1].active) {
							block.command = Block.Command.EXPLODE;
							gridBlock[i][j - 1].command = Block.Command.EXPLODE;
						}
						if (gridHas(i, j + 1) && gridBlock[i][j + 1].active) {
							block.command = Block.Command.EXPLODE;
							gridBlock[i][j + 1].command = Block.Command.EXPLODE;
						}
						if (gridHas(i - 1, j) && gridBlock[i - 1][j].active) {
							block.command = Block.Command.EXPLODE;
							gridBlock[i - 1][j].command = Block.Command.EXPLODE;
						}
						if (gridHas(i + 1, j) && gridBlock[i + 1][j].active) {
							block.command = Block.Command.EXPLODE;
							gridBlock[i + 1][j].command = Block.Command.EXPLODE;
						}
					}
				}
			}
		}
		if (!isBuffered()) {
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					Block block = gridBlock[i][j];
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
		return gridValid(x, y) && gridBlock[x][y] != null;
	}

	public final boolean gridEmpty(int x, int y) {
		return gridValid(x, y)
			&& (gridBlock[x][y] == null && !gridHasGate(x, y) || gridBlock[x][y] != null
				&& gridBlock[x][y].command == Block.Command.FALL);
	}

	public final boolean gridValid(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}

	private final boolean gridHasGate(int x, int y) {
		return gridSpecial[x][y] != null && gridSpecial[x][y].gate && gridSpecial[x][y].toggled;
	}

	public boolean isBuffered() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Block block = gridBlock[i][j];
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
				timer[i][j] = gridBlock[i][j] != null ? gridBlock[i][j].timer : 0;
			}
		}
		return timer;
	}

	public boolean checkTimer() {
		/* when the timer is ready, checks board to see if any of the timers need to be set */
		boolean modified = false;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Block block = gridBlock[i][j];
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
				Block block = gridBlock[i][j];
				if (block != null && block.timer > 0) block.timer--;
			}
		}
	}

	public boolean timerReady() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (gridBlock[i][j] != null && gridBlock[i][j].timer > 0) return false;
			}
		}
		return true;
	}

	/* PUBLIC FINAL VIEW FUNCTIONS The following functions have public access and are intended for use by the viewer which retrieves grid information to draw */

	public final Block[][] getGrid() {
		return gridBlock;
	}

	public final Special[][] getSpecial() {
		return gridSpecial;
	}

	public final String getName() {
		return name;
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
				if (gridBlock[i][j] != null) gridBlock[i][j].visited = false;
			}
		}
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Block block = gridBlock[i][j];
				if (block != null && !block.visited) {
					block.visited = true;
					boolean hold = true;
					switch (block.command) {
					case DROWN:
					case EXPLODE:
					case BREAK:
						if (block == cursorBlock) unselect();
						gridBlock[i][j] = null;
						break;
					case MOVE_UP:
						gridBlock[i][j] = null;
						gridBlock[i][j - 1] = block;
						Special pathU = gridSpecial[i][j - 1];
						if (pathU != null && pathU.path && gridEmpty(pathU.destX, pathU.destY)) {
							gridBlock[i][j - 1].command = Block.Command.PATH;
							hold = false;
						}
						break;
					case FALL:
						gridBlock[i][j] = null;
						gridBlock[i][j + 1] = block;
						Special pathFall = gridSpecial[i][j + 1];
						if (pathFall != null && pathFall.path && gridEmpty(pathFall.destX, pathFall.destY)) {
							gridBlock[i][j + 1].command = Block.Command.PATH;
							hold = false;
						}
						if (gridEmpty(i, j + 2)) {
							block.command = Block.Command.FALL;
							hold = false;
						}
						break;
					case MOVE_DOWN:
						gridBlock[i][j] = null;
						gridBlock[i][j + 1] = block;
						Special pathDown = gridSpecial[i][j + 1];
						if (pathDown != null && pathDown.path && gridEmpty(pathDown.destX, pathDown.destY)) {
							gridBlock[i][j + 1].command = Block.Command.PATH;
							hold = false;
						}
						break;
					case MOVE_RIGHT:
						gridBlock[i][j] = null;
						gridBlock[i + 1][j] = block;
						Special pathRight = gridSpecial[i + 1][j];
						if (pathRight != null && pathRight.path && gridEmpty(pathRight.destX, pathRight.destY)) {
							gridBlock[i + 1][j].command = Block.Command.PATH;
							hold = false;
						}
						break;
					case MOVE_LEFT:
						gridBlock[i][j] = null;
						gridBlock[i - 1][j] = block;
						Special pathLeft = gridSpecial[i - 1][j];
						if (pathLeft != null && pathLeft.path && gridEmpty(pathLeft.destX, pathLeft.destY)) {
							gridBlock[i - 1][j].command = Block.Command.PATH;
							hold = false;
						}
						break;
					case PATH:
						unselect();
						gridBlock[i][j] = null;
						gridBlock[gridSpecial[i][j].destX][gridSpecial[i][j].destY] = block;
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
		if (cursorBlock == null && gridHas(cursorX, cursorY) && gridBlock[cursorX][cursorY].move) {
			cursorBlock = gridBlock[cursorX][cursorY];
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
		cursorX = MathUtils.clamp(x, 0, width - 1);
		cursorY = MathUtils.clamp(y, 0, height - 1);
	}
}
