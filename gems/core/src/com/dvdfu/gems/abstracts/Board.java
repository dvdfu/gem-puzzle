package com.dvdfu.gems.abstracts;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.dvdfu.gems.references.Res;

public class Board {
	private String name;
	private Block gridBlocks[][];
	private Block cursorBlock;
	private boolean gridOccupied[][];
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
		gridOccupied = new boolean[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				gridBlocks[i][j] = null;
				gridSpecials[i][j] = null;
				gridOccupied[i][j] = false;
			}
		}
	}

	public void setState(String data) {
		Array<char[]> dataArray = new Array<char[]>();
		while (data.length() > 1) {
			dataArray.add(data.substring(0, data.indexOf(';')).toCharArray());
			data = data.substring(data.indexOf(';') + 1);
		}
		this.name = new String(dataArray.removeIndex(0));
		int newWidth = Integer.parseInt(new String(dataArray.removeIndex(0)));
		int newHeight = Integer.parseInt(new String(dataArray.removeIndex(0)));
		if (width != newWidth || height != newHeight) {
			width = newWidth;
			height = newHeight;
			gridBlocks = new Block[width][height];
			gridSpecials = new Special[width][height];
			gridOccupied = new boolean[width][height];
		}
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				gridBlocks[i][j] = null;
				gridSpecials[i][j] = null;
				gridOccupied[i][j] = false;
			}
		}
		for (char[] cell : dataArray)
			addByID(cell);
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
				case 'w':
					block.wind = true;
					block.direction = id[i + 1] - 48;
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
				special = new Special().setWater();
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

	private void updateButtons() {
		// handle button and gate toggling
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Special special = gridSpecials[i][j];
				if (special != null) {
					if (special.button) special.toggled = gridHas(i, j);
					else if (special.gate) {
						special.toggled = gridHas(special.destX, special.destY) ^ special.original;
					}
				}
			}
		}
	}

	private void updatePaths() {
		// handle button and gate toggling
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Special special = gridSpecials[i][j];
				Block block = gridBlocks[i][j];
				if (special != null && special.path && block != null && block.throughPath
					&& gridEmpty(special.destX, special.destY)) {
					block.command = Res.Command.PATH;
					block.throughPath = false;
				}
			}
		}
	}

	private void updateFalling() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Block block = gridBlocks[i][j];
				// fall condition: block falls, space below empty, no wind up/left/right
				if (block != null && block.fall && gridEmpty(i, j + 1) && !checkWind(i, j, 0) && !checkWind(i, j, 1)
					&& !checkWind(i, j, 2)) {
					block.command = Res.Command.FALL;
					gridOccupied[i][j + 1] = true;
				}
			}
		}
	}

	private void updateBreak() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Block block = gridBlocks[i][j];
				Special special = gridSpecials[i][j];
				if (block != null && block.active) {
					// break block with water or active gate
					if (special != null) {
						if (special.water) block.command = Res.Command.DROWN;
						else if (special.gate && special.toggled) block.command = Res.Command.BREAK;
					}
					// break block with surroundings
					if (gridHas(i, j + 1) && gridBlocks[i][j + 1].gemU && gridHas(i, j - 1) && gridBlocks[i][j - 1].gemD
						&& gridHas(i + 1, j) && gridBlocks[i + 1][j].gemL && gridHas(i - 1, j) && gridBlocks[i - 1][j].gemR) {
						block.command = Res.Command.BREAK;
						gridBlocks[i][j + 1].command = Res.Command.BREAK;
						gridBlocks[i][j - 1].command = Res.Command.BREAK;
						gridBlocks[i + 1][j].command = Res.Command.BREAK;
						gridBlocks[i - 1][j].command = Res.Command.BREAK;
					}
					// break block with connection
					else if (block.isGem()) {
						if (block.gemU && gridHas(i, j - 1) && gridBlocks[i][j - 1].gemD) {
							block.command = Res.Command.BREAK;
							gridBlocks[i][j - 1].command = Res.Command.BREAK;
						}
						if (block.gemD && gridHas(i, j + 1) && gridBlocks[i][j + 1].gemU) {
							block.command = Res.Command.BREAK;
							gridBlocks[i][j + 1].command = Res.Command.BREAK;
						}
						if (block.gemR && gridHas(i + 1, j) && gridBlocks[i + 1][j].gemL) {
							block.command = Res.Command.BREAK;
							gridBlocks[i + 1][j].command = Res.Command.BREAK;
						}
						if (block.gemL && gridHas(i - 1, j) && gridBlocks[i - 1][j].gemR) {
							block.command = Res.Command.BREAK;
							gridBlocks[i - 1][j].command = Res.Command.BREAK;
						}
					}
					else if (block.bomb) {
						if (gridHas(i, j - 1) && gridBlocks[i][j - 1].active) {
							block.command = Res.Command.EXPLODE;
							gridBlocks[i][j - 1].command = Res.Command.EXPLODE;
						}
						if (gridHas(i, j + 1) && gridBlocks[i][j + 1].active) {
							block.command = Res.Command.EXPLODE;
							gridBlocks[i][j + 1].command = Res.Command.EXPLODE;
						}
						if (gridHas(i - 1, j) && gridBlocks[i - 1][j].active) {
							block.command = Res.Command.EXPLODE;
							gridBlocks[i - 1][j].command = Res.Command.EXPLODE;
						}
						if (gridHas(i + 1, j) && gridBlocks[i + 1][j].active) {
							block.command = Res.Command.EXPLODE;
							gridBlocks[i + 1][j].command = Res.Command.EXPLODE;
						}
					}
				}
			}
		}
	}

	private void updateCursor() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Block block = gridBlocks[i][j];
				// block existed, is selected, and moves
				if (block != null && block == cursorBlock && cursorBlock.move) {
					// cursor is away from block, space is empty, no opposing wind
					if (cursorX < i && gridEmpty(i - 1, j) && !checkWind(i, j, 0)) {
						block.command = Res.Command.MOVE_LEFT;
						gridOccupied[i - 1][j] = true;
					} else if (cursorX > i && gridEmpty(i + 1, j) && !checkWind(i, j, 2)) {
						block.command = Res.Command.MOVE_RIGHT;
						gridOccupied[i + 1][j] = true;
					} else if (!block.fall && cursorY < j && gridEmpty(i, j - 1) && !checkWind(i, j, 3)) {
						block.command = Res.Command.MOVE_UP;
						gridOccupied[i][j - 1] = true;
					} else if (cursorY > j && gridEmpty(i, j + 1) && !checkWind(i, j, 1)) {
						block.command = Res.Command.MOVE_DOWN;
						gridOccupied[i][j + 1] = true;
					}
				}
			}
		}
	}

	private void updateWind() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Block block = gridBlocks[i][j];
				if (block != null && block.move) {
					if (checkWind(i, j, 0) && gridEmpty(i + 1, j)) {
						block.command = Res.Command.MOVE_RIGHT;
						gridOccupied[i + 1][j] = true;
					}
					if (checkWind(i, j, 1) && gridEmpty(i, j - 1)) {
						block.command = Res.Command.MOVE_UP;
						gridOccupied[i][j - 1] = true;
					}
					if (checkWind(i, j, 2) && gridEmpty(i - 1, j)) {
						block.command = Res.Command.MOVE_LEFT;
						gridOccupied[i - 1][j] = true;
					}
					if (checkWind(i, j, 3) && gridEmpty(i, j + 1)) {
						block.command = Res.Command.MOVE_DOWN;
						gridOccupied[i][j + 1] = true;
					}
				}
			}
		}
	}

	public final boolean checkWind(int x, int y, final int direction) {
		if (gridBlocks[x][y] != null && !gridBlocks[x][y].move) return false;
		if (direction == 0) x--;
		else if (direction == 1) y++;
		else if (direction == 2) x++;
		else if (direction == 3) y--;
		while (x >= 0 && x < width && y >= 0 && y < height) {
			Block block = gridBlocks[x][y];
			Special special = gridSpecials[x][y];
			if (block != null) {
				if (block.wind && block.direction == direction) return true;
				if (!block.move) return false;
			}
			if (special != null && special != null && special.gate && special.toggled) return false;
			if (direction == 0) x--;
			else if (direction == 1) y++;
			else if (direction == 2) x++;
			else if (direction == 3) y--;
		}
		return false;
	}

	public void update() {
		updateButtons();
		updateFalling();
		updateWind();
		updatePaths();
		updateBreak();
		if (!isBuffered()) updateCursor();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				gridOccupied[i][j] = false;
			}
		}
	}

	public final boolean gridHas(int x, int y) {
		return gridValid(x, y) && gridBlocks[x][y] != null;
	}

	public final boolean gridEmpty(int x, int y) {
		return gridValid(x, y) && gridBlocks[x][y] == null && !gridOccupied[x][y] && !gridHasGate(x, y);
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
				if (block != null && block.command != Res.Command.HOLD) return true;
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
						block.timer = Res.timeExplode;
						modified = true;
						break;
					case DROWN:
						block.timer = Res.timeDrown;
						modified = true;
						break;
					case FALL:
					case MOVE_UP:
					case MOVE_DOWN:
					case MOVE_RIGHT:
					case MOVE_LEFT:
						block.timer = Res.timeMove;
						modified = true;
						break;
					case BREAK:
						block.timer = Res.timeGem;
						modified = true;
						break;
					case PATH:
						block.timer = Res.timePath;
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
						block.throughPath = true;
						break;
					case FALL:
					case MOVE_DOWN:
						gridBlocks[i][j] = null;
						gridBlocks[i][j + 1] = block;
						block.throughPath = true;
						break;
					case MOVE_RIGHT:
						gridBlocks[i][j] = null;
						gridBlocks[i + 1][j] = block;
						block.throughPath = true;
						break;
					case MOVE_LEFT:
						gridBlocks[i][j] = null;
						gridBlocks[i - 1][j] = block;
						block.throughPath = true;
						break;
					case PATH:
						gridBlocks[i][j] = null;
						gridBlocks[gridSpecials[i][j].destX][gridSpecials[i][j].destY] = block;
						break;
					default:
						break;
					}
					block.command = Res.Command.HOLD;
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
