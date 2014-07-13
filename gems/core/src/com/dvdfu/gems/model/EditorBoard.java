package com.dvdfu.gems.model;

import java.util.Stack;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.dvdfu.gems.handlers.Input;
import com.dvdfu.gems.handlers.Res;
import com.dvdfu.gems.handlers.Res.Cursors;

public class EditorBoard {
	private EditorBlock cursorBlock;
	private EditorBlock gridBlocks[][];
	private Special cursorSpecial;
	private Special gridSpecials[][];
	private boolean cursorVisited[][];
	private Stack<String> undoStack;
	private Stack<String> redoStack;
	private Res.Cursors cursorState;
	private String name;
	private int width;
	private int height;
	private int cX;
	private int cY;
	private boolean cursorSetBlock;
	private boolean cursorSetSpecial;
	private boolean cursorPlacing;
	public int placeX;
	public int placeY;
	private boolean modified;

	public EditorBoard(String name, int width, int height) {
		this.name = name;
		this.width = width;
		this.height = height;
		cursorState = Cursors.BLOCK_STATIC;
		cursorBlock = null;
		cX = 0;
		cY = 0;
		gridBlocks = new EditorBlock[width][height];
		gridSpecials = new Special[width][height];
		cursorVisited = new boolean[width][height];
		cursorSetBlock = true; // this boolean tells the cursor whether a click will add or remove cell properties depending on the property of the first cell clicked
		cursorSetSpecial = true;
		cursorPlacing = false;
		placeX = 0;
		placeY = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				gridBlocks[i][j] = null;
				gridSpecials[i][j] = null;
				cursorVisited[i][j] = false;
			}
		}
		modified = false;
		undoStack = new Stack<String>();
		redoStack = new Stack<String>();
	}

	// GRIDSTATES

	private void saveState() {
		String id = getState();
		Preferences prefs = Gdx.app.getPreferences("prefs");
		prefs.putString("level", id);
	}

	public void setState(String data) {
		Array<char[]> dataArray = new Array<char[]>();
		while (data.length() > 1) {
			dataArray.add(data.substring(0, data.indexOf(';')).toCharArray());
			data = data.substring(data.indexOf(';') + 1);
		}
		String newName = new String(dataArray.removeIndex(0));
		int newWidth = Integer.parseInt(new String(dataArray.removeIndex(0)));
		int newHeight = Integer.parseInt(new String(dataArray.removeIndex(0)));
		resetBoard(newName, newWidth, newHeight);
		for (char[] cell : dataArray) {
			addCell(cell);
		}
	}

	private String getState() {
		String id = "";
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				EditorBlock block = gridBlocks[i][j];
				if (block != null) {
					id += i + "" + j + block.getID();
				}
				Special special = gridSpecials[i][j];
				if (special != null) {
					id += i + "" + j + special.getID();
				}
			}
		}
		return name + ";" + width + ";" + height + ";" + id;
	}

	public void pushState() {
		undoStack.push(getState());
		redoStack.clear();
		modified = false;
	}

	private void undoState() {
		if (undoStack.empty()) { return; }
		String temp = undoStack.pop();
		if (undoStack.empty()) {
			undoStack.push(temp);
			return;
		}
		setState(undoStack.peek());
		redoStack.push(temp);
	}

	private void redoState() {
		if (redoStack.empty()) { return; }
		setState(redoStack.pop());
		undoStack.push(getState());
	}

	public void resetBoard(String name, int width, int height) {
		this.name = name;
		if (this.width != width || this.height != height) {
			this.width = width;
			this.height = height;
			gridBlocks = new EditorBlock[width][height];
			gridSpecials = new Special[width][height];
			cursorVisited = new boolean[width][height];
		}
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				gridBlocks[i][j] = null;
				gridSpecials[i][j] = null;
				cursorVisited[i][j] = false;
			}
		}
	}

	public void addCell(char[] id) {
		int x = id[0] - 48;
		int y = id[1] - 48;
		if (id[2] == 'b') {
			EditorBlock block = new EditorBlock();
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

	// CURSOR PLACEMENT

	public void setCursorState(Res.Cursors state) {
		cursorState = state;
	}

	public Res.Cursors getCursorState() {
		return cursorState;
	}

	public void setCursor(int x, int y) {
		cX = MathUtils.clamp(x, 0, width - 1);
		cY = MathUtils.clamp(y, 0, height - 1);
	}

	public final int getCursorX() {
		return cX;
	}

	public final int getCursorY() {
		return cY;
	}

	// ADDING BLOCKS

	private void addPath(int x1, int y1, int x2, int y2) {
		if (gridValid(x1, y1) && gridValid(x2, y2) && !(x1 == x2 && y1 == y2)) {
			clearSpecial(x1, y1);
			clearSpecial(x2, y2);
			gridSpecials[x1][y1] = new Special().setPath(x2, y2);
			gridSpecials[x2][y2] = new Special().setPath(x1, y1);
		}
	}

	private void addGate(int gateX, int gateY, int buttonX, int buttonY, boolean gateOriginal) {
		if (gridValid(buttonX, buttonY) && gridValid(gateX, gateY) && !(buttonX == gateX && buttonY == gateY)) {
			clearSpecial(gateX, gateY);
			gridSpecials[buttonX][buttonY] = new Special().setButton();
			gridSpecials[gateX][gateY] = new Special().setGate(buttonX, buttonY, gateOriginal);
		}
	}

	private void clearSpecial(int x, int y) {
		Special special = gridSpecials[x][y];
		if (special == null) { return; }
		if (special.path) {
			gridSpecials[special.destX][special.destY] = null;
		} else if (special.button) {
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					Special gate = gridSpecials[i][j];
					if (gate != null && gate.gate && gate.destX == x && gate.destY == y) {
						gridSpecials[i][j] = null;
					}
				}
			}
		}
		gridSpecials[x][y] = null;
	}

	private void handleAdd() {
		if (Input.MousePressed()) {
			// tells the cursor if the property of the first clicked cell is true or not. Determines what behaviour the cursor will have later

			switch (cursorState) {
			case BLOCK_ACTIVE:
			case BLOCK_MOVE:
			case BLOCK_STATIC:
				cursorSetBlock = true;
				break;
			case BOMB:
				cursorSetBlock = cursorBlock == null || !cursorBlock.bomb;
				break;
			case FALL:
				cursorSetBlock = cursorBlock == null || !cursorBlock.fall;
				break;
			case GEM_CENTER:
				cursorSetBlock = cursorBlock == null || !cursorBlock.gemC;
				break;
			case GEM_DOWN:
				cursorSetBlock = cursorBlock == null || !cursorBlock.gemD;
				break;
			case GEM_LEFT:
				cursorSetBlock = cursorBlock == null || !cursorBlock.gemL;
				break;
			case GEM_RIGHT:
				cursorSetBlock = cursorBlock == null || !cursorBlock.gemR;
				break;
			case GEM_UP:
				cursorSetBlock = cursorBlock == null || !cursorBlock.gemU;
				break;
			case GATE:
				placeX = cX;
				placeY = cY;
				cursorSetSpecial = cursorSpecial == null || !cursorSpecial.gate;
				break;
			case PATH:
				placeX = cX;
				placeY = cY;
				cursorSetSpecial = cursorSpecial == null || !cursorSpecial.path;
				break;
			case WATER:
				cursorSetSpecial = cursorSpecial == null || !cursorSpecial.water;
				break;
			default:
				break;
			}
		}
		if (Input.MouseDown()) {
			boolean setNewBlock = cursorBlock == null && cursorSetBlock;
			boolean setOldBlock = cursorBlock != null && !cursorVisited[cX][cY];
			boolean setNewSpecial = cursorSetSpecial;
			boolean setOldSpecial = cursorSpecial != null && !cursorVisited[cX][cY];
			switch (cursorState) {
			case BLOCK_ACTIVE:
				if (setNewBlock) gridBlocks[cX][cY] = new EditorBlock().setActive(true);
				else if (setOldBlock) cursorBlock.setActive(true);
				break;
			case BLOCK_MOVE:
				if (setNewBlock) gridBlocks[cX][cY] = new EditorBlock().setMove(true);
				else if (setOldBlock) cursorBlock.setMove(true);
				break;
			case BLOCK_STATIC:
				if (setNewBlock || setOldBlock) gridBlocks[cX][cY] = new EditorBlock().setActive(false);
				break;
			case BOMB:
				if (setNewBlock) gridBlocks[cX][cY] = new EditorBlock().setBomb(true);
				else if (setOldBlock) cursorBlock.setBomb(cursorSetBlock);
				break;
			case ERASER:
				clearSpecial(cX, cY);
				gridBlocks[cX][cY] = null;
				break;
			case FALL:
				if (setNewBlock) gridBlocks[cX][cY] = new EditorBlock().setFall(true);
				else if (setOldBlock) cursorBlock.setFall(cursorSetBlock);
				break;
			case GEM_CENTER:
				if (setNewBlock) gridBlocks[cX][cY] = new EditorBlock().setGemC(true);
				else if (setOldBlock) cursorBlock.setGemC(cursorSetBlock);
				break;
			case GEM_DOWN:
				if (setNewBlock) gridBlocks[cX][cY] = new EditorBlock().setGemD(true);
				else if (setOldBlock) cursorBlock.setGemD(cursorSetBlock);
				break;
			case GEM_LEFT:
				if (setNewBlock) gridBlocks[cX][cY] = new EditorBlock().setGemL(true);
				else if (setOldBlock) cursorBlock.setGemL(cursorSetBlock);
				break;
			case GEM_RIGHT:
				if (setNewBlock) gridBlocks[cX][cY] = new EditorBlock().setGemR(true);
				else if (setOldBlock) cursorBlock.setGemR(cursorSetBlock);
				break;
			case GEM_UP:
				if (setNewBlock) gridBlocks[cX][cY] = new EditorBlock().setGemU(true);
				else if (setOldBlock) cursorBlock.setGemU(cursorSetBlock);
				break;
			case GATE:
				if (setNewSpecial) cursorPlacing = true;
				else if (setOldSpecial) cursorSpecial.gateOriginal ^= true;
				break;
			case PATH:
				cursorPlacing = true;
				break;
			case WATER:
				if (setNewSpecial) {
					clearSpecial(cX, cY);
					gridSpecials[cX][cY] = new Special().setWater();
				}
				else if (setOldSpecial && cursorSpecial.water) gridSpecials[cX][cY] = null;
				break;
			default:
				break;
			}
			if (setNewBlock || setOldBlock || setNewSpecial || setOldSpecial) {
				cursorVisited[cX][cY] = true;
				modified = true;
			}
		}
		if (Input.MouseReleased()) {
			if (cursorPlacing) {
				if (cursorState == Res.Cursors.GATE) {
					addGate(placeX, placeY, cX, cY, true);
				} else if (cursorState == Res.Cursors.PATH) {
					addPath(placeX, placeY, cX, cY);
				}
				cursorPlacing = false;
			}
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					cursorVisited[i][j] = false;
				}
			}
		}
	}

	public void update() {
		cursorBlock = gridBlocks[cX][cY];
		cursorSpecial = gridSpecials[cX][cY];
		handleAdd();
		if (Input.KeyPressed(Input.SPACEBAR)) {
			cursorState = Res.Cursors.GEM_CENTER;
		} else if (Input.KeyPressed(Input.ARROW_UP)) {
			cursorState = Res.Cursors.GEM_UP;
		} else if (Input.KeyPressed(Input.ARROW_DOWN)) {
			cursorState = Res.Cursors.GEM_DOWN;
		} else if (Input.KeyPressed(Input.ARROW_RIGHT)) {
			cursorState = Res.Cursors.GEM_RIGHT;
		} else if (Input.KeyPressed(Input.ARROW_LEFT)) {
			cursorState = Res.Cursors.GEM_LEFT;
		} else if (Input.KeyPressed(Input.A)) {
			cursorState = Res.Cursors.BLOCK_ACTIVE;
		} else if (Input.KeyPressed(Input.B)) {
			cursorState = Res.Cursors.BOMB;
		} else if (Input.KeyPressed(Input.F)) {
			cursorState = Res.Cursors.FALL;
		} else if (Input.KeyPressed(Input.G)) {
			cursorState = Res.Cursors.GATE;
		} else if (Input.KeyPressed(Input.H)) {
			cursorState = Res.Cursors.WATER;
		} else if (Input.KeyPressed(Input.M)) {
			cursorState = Res.Cursors.BLOCK_MOVE;
		} else if (Input.KeyPressed(Input.P)) {
			cursorState = Res.Cursors.PATH;
		} else if (Input.KeyPressed(Input.BACKSPACE)) {
			cursorState = Res.Cursors.ERASER;
		}
		// UNDO
		if (Input.MouseReleased() && modified) pushState();
		if (Input.KeyDown(Input.CTRL)) {
			if (Input.KeyPressed(Input.Z)) undoState();
			if (Input.KeyPressed(Input.Y)) redoState();
		}
		if (Input.KeyPressed(Input.TAB)) saveState();
	}

	/* PUBLIC FINAL VIEW FUNCTIONS The following functions have private access and are intended for use by the viewer which retrieves grid information to draw */

	public final EditorBlock[][] getGrid() {
		return gridBlocks;
	}

	public final Special[][] getSpecial() {
		return gridSpecials;
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

	public final boolean gridValid(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}

	public final boolean placingGate() {
		return cursorPlacing && cursorState == Res.Cursors.GATE;
	}
	
	public final boolean placingPath() {
		return cursorPlacing && cursorState == Res.Cursors.PATH;
	}
}
