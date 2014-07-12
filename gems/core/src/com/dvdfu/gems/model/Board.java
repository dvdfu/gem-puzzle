package com.dvdfu.gems.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.dvdfu.gems.handlers.Enums;
import com.dvdfu.gems.handlers.Vars;

public class Board {
	private String name;
	private Block gridBlocks[][];
	private Block cursorBlock;
	private Special gridSpecials[][];
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
		gridBlocks = new Block[width][height];
		gridSpecials = new Special[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				gridBlocks[i][j] = null;
				gridSpecials[i][j] = null;
			}
		}
	}
	
	public void setState(String data) {
		Array<char[]> dataArray = new Array<char[]>();
		while (data.length() > 1) {
			dataArray.add(data.substring(0, data.indexOf(';')).toCharArray());
			data = data.substring(data.indexOf(';') + 1);
		}
		String newName = new String(dataArray.removeIndex(0));
		int width = Integer.parseInt(new String(dataArray.removeIndex(0)));
		int height = Integer.parseInt(new String(dataArray.removeIndex(0)));
		resetBoard(newName, width, height);
		for (char[] cell : dataArray)
			addByID(cell);
	}
	
	public void resetBoard(String name, int width, int height) {
		this.name = name;
		if (this.width != width || this.height != height) {
			this.width = width;
			this.height = height;
			gridBlocks = new Block[width][height];
			gridSpecials = new Special[width][height];
		}
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				gridBlocks[i][j] = null;
				gridSpecials[i][j] = null;
			}
		}
	}

	public void addByID(char[] id) {
		int x = id[0] - 48;
		int y = id[1] - 48;
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
			gridBlocks[x][y] = block;
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
			gridSpecials[x][y] = special;
		}
	}

	public void update() {
		// handle button and gate toggling
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Special special = gridSpecials[i][j];
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
				Block block = gridBlocks[i][j];
				Special special = gridSpecials[i][j];
				if (block != null) {
					if (block.active) {
						// drops block if it falls
						if (block.fall && gridEmpty(i, j + 1) && block.command != Enums.Command.BREAK) block.command = Enums.Command.FALL;
						// destroy block if it is active and surrounded
						if (gridHas(i, j + 1) && gridBlocks[i][j + 1].gemU && gridHas(i, j - 1) && gridBlocks[i][j - 1].gemD
							&& gridHas(i + 1, j) && gridBlocks[i + 1][j].gemL && gridHas(i - 1, j) && gridBlocks[i - 1][j].gemR) {
							if (block.isGem()) block.command = Enums.Command.BREAK;
							else block.command = Enums.Command.BREAK;
							gridBlocks[i][j + 1].command = Enums.Command.BREAK;
							gridBlocks[i][j - 1].command = Enums.Command.BREAK;
							gridBlocks[i + 1][j].command = Enums.Command.BREAK;
							gridBlocks[i - 1][j].command = Enums.Command.BREAK;
						}
					}
					// destroy block if in toggled gate or water
					if (special != null) {
						if (special.water) block.command = Enums.Command.DROWN;
						else if (special.gate && special.toggled) block.command = Enums.Command.BREAK;
					}
					// destroy block if it is a gem and matched
					if (block.isGem()) {
						if (block.gemU && gridHas(i, j - 1) && gridBlocks[i][j - 1].gemD) {
							block.command = Enums.Command.BREAK;
							gridBlocks[i][j - 1].command = Enums.Command.BREAK;
						}
						if (block.gemD && gridHas(i, j + 1) && gridBlocks[i][j + 1].gemU) {
							block.command = Enums.Command.BREAK;
							gridBlocks[i][j + 1].command = Enums.Command.BREAK;
						}
						if (block.gemR && gridHas(i + 1, j) && gridBlocks[i + 1][j].gemL) {
							block.command = Enums.Command.BREAK;
							gridBlocks[i + 1][j].command = Enums.Command.BREAK;
						}
						if (block.gemL && gridHas(i - 1, j) && gridBlocks[i - 1][j].gemR) {
							block.command = Enums.Command.BREAK;
							gridBlocks[i - 1][j].command = Enums.Command.BREAK;
						}
					}
					// destroy block if it is a bomb and matched
					else if (block.bomb) {
						if (gridHas(i, j - 1) && gridBlocks[i][j - 1].active) {
							block.command = Enums.Command.EXPLODE;
							gridBlocks[i][j - 1].command = Enums.Command.EXPLODE;
						}
						if (gridHas(i, j + 1) && gridBlocks[i][j + 1].active) {
							block.command = Enums.Command.EXPLODE;
							gridBlocks[i][j + 1].command = Enums.Command.EXPLODE;
						}
						if (gridHas(i - 1, j) && gridBlocks[i - 1][j].active) {
							block.command = Enums.Command.EXPLODE;
							gridBlocks[i - 1][j].command = Enums.Command.EXPLODE;
						}
						if (gridHas(i + 1, j) && gridBlocks[i + 1][j].active) {
							block.command = Enums.Command.EXPLODE;
							gridBlocks[i + 1][j].command = Enums.Command.EXPLODE;
						}
					}
				}
			}
		}
		if (!isBuffered()) {
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					Block block = gridBlocks[i][j];
					// moves selected block closer to cursor, lowest priority
					if (block != null && block == cursorBlock && cursorBlock.move) {
						if (cursorX < i && gridEmpty(i - 1, j)) block.command = Enums.Command.MOVE_LEFT;
						else if (cursorX > i && gridEmpty(i + 1, j)) block.command = Enums.Command.MOVE_RIGHT;
						else if (cursorBlock.active && !cursorBlock.fall) {
							if (cursorY < j && gridEmpty(i, j - 1)) block.command = Enums.Command.MOVE_UP;
							else if (cursorY > j && gridEmpty(i, j + 1)) block.command = Enums.Command.MOVE_DOWN;
						}
					}
				}
			}
		}
	}

	public final boolean gridHas(int x, int y) {
		return gridValid(x, y) && gridBlocks[x][y] != null;
	}

	public final boolean gridEmpty(int x, int y) {
		return gridValid(x, y)
			&& (gridBlocks[x][y] == null && !gridHasGate(x, y) || gridBlocks[x][y] != null
				&& gridBlocks[x][y].command == Enums.Command.FALL);
	}

	public final boolean gridValid(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}

	private final boolean gridHasGate(int x, int y) {
		return gridSpecials[x][y] != null && gridSpecials[x][y].gate && gridSpecials[x][y].toggled;
	}

	public boolean isBuffered() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Block block = gridBlocks[i][j];
				if (block != null && block.command != Enums.Command.HOLD) return true;
			}
		}
		return false;
	}

	/* TIMER FUNCTIONS */

	public int[][] getTimer() {
		int[][] timer = new int[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				timer[i][j] = gridBlocks[i][j] != null ? gridBlocks[i][j].timer : 0;
			}
		}
		return timer;
	}

	public boolean checkTimer() {
		/* when the timer is ready, checks board to see if any of the timers need to be set */
		boolean modified = false;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Block block = gridBlocks[i][j];
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
				Block block = gridBlocks[i][j];
				if (block != null && block.timer > 0) block.timer--;
			}
		}
	}

	public boolean timerReady() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (gridBlocks[i][j] != null && gridBlocks[i][j].timer > 0) return false;
			}
		}
		return true;
	}

	/* PUBLIC FINAL VIEW FUNCTIONS The following functions have public access and are intended for use by the viewer which retrieves grid information to draw */

	public final Block[][] getGrid() {
		return gridBlocks;
	}

	public final Special[][] getSpecial() {
		return gridSpecials;
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
				if (gridBlocks[i][j] != null) gridBlocks[i][j].visited = false;
			}
		}
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Block block = gridBlocks[i][j];
				if (block != null && !block.visited) {
					block.visited = true;
					boolean hold = true;
					switch (block.command) {
					case DROWN:
					case EXPLODE:
					case BREAK:
						if (block == cursorBlock) unselect();
						gridBlocks[i][j] = null;
						break;
					case MOVE_UP:
						gridBlocks[i][j] = null;
						gridBlocks[i][j - 1] = block;
						Special pathU = gridSpecials[i][j - 1];
						if (pathU != null && pathU.path && gridEmpty(pathU.destX, pathU.destY)) {
							gridBlocks[i][j - 1].command = Enums.Command.PATH;
							hold = false;
						}
						break;
					case FALL:
						gridBlocks[i][j] = null;
						gridBlocks[i][j + 1] = block;
						Special pathFall = gridSpecials[i][j + 1];
						if (pathFall != null && pathFall.path && gridEmpty(pathFall.destX, pathFall.destY)) {
							gridBlocks[i][j + 1].command = Enums.Command.PATH;
							hold = false;
						}
						if (gridEmpty(i, j + 2)) {
							block.command = Enums.Command.FALL;
							hold = false;
						}
						break;
					case MOVE_DOWN:
						gridBlocks[i][j] = null;
						gridBlocks[i][j + 1] = block;
						Special pathDown = gridSpecials[i][j + 1];
						if (pathDown != null && pathDown.path && gridEmpty(pathDown.destX, pathDown.destY)) {
							gridBlocks[i][j + 1].command = Enums.Command.PATH;
							hold = false;
						}
						break;
					case MOVE_RIGHT:
						gridBlocks[i][j] = null;
						gridBlocks[i + 1][j] = block;
						Special pathRight = gridSpecials[i + 1][j];
						if (pathRight != null && pathRight.path && gridEmpty(pathRight.destX, pathRight.destY)) {
							gridBlocks[i + 1][j].command = Enums.Command.PATH;
							hold = false;
						}
						break;
					case MOVE_LEFT:
						gridBlocks[i][j] = null;
						gridBlocks[i - 1][j] = block;
						Special pathLeft = gridSpecials[i - 1][j];
						if (pathLeft != null && pathLeft.path && gridEmpty(pathLeft.destX, pathLeft.destY)) {
							gridBlocks[i - 1][j].command = Enums.Command.PATH;
							hold = false;
						}
						break;
					case PATH:
						unselect();
						gridBlocks[i][j] = null;
						gridBlocks[gridSpecials[i][j].destX][gridSpecials[i][j].destY] = block;
						break;
					default:
						break;
					}
					if (hold) block.command = Enums.Command.HOLD;
				}
			}
		}
	}

	public boolean select() {
		if (cursorBlock == null && gridHas(cursorX, cursorY) && gridBlocks[cursorX][cursorY].move) {
			cursorBlock = gridBlocks[cursorX][cursorY];
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
