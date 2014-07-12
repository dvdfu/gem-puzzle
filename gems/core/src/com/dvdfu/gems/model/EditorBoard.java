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
	private boolean cursorSet;
	private boolean cursorPlacing;
	private int placeX;
	private int placeY;
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
		cursorSet = true; // this boolean tells the cursor whether a click will add or remove cell properties depending on the property of the first cell clicked
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

	private void addBlock(EditorBlock block, int x, int y) {
		gridBlocks[x][y] = block;
	}

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
			gridSpecials[x][y] = null;
		} else if (special.button) {
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					Special gate = gridSpecials[i][j];
					if (gate != null && gate.gate && gate.destX == x && gate.destY == y) {
						gridSpecials[i][j] = null;
					}
				}
			}
			gridSpecials[x][y] = null;
		}
	}

	private void handleBlocks() {
		if (Input.MousePressed()) {
			// tells the cursor if the property of the first clicked cell is true or not. Determines what behaviour the cursor will have later
			if (cursorBlock == null) {
				cursorSet = true;
			} else {
				switch (cursorState) {
				case BLOCK_ACTIVE:
				case BLOCK_MOVE:
				case BLOCK_STATIC:
					cursorSet = true;
					break;
				case BOMB:
					cursorSet = !cursorBlock.bomb;
					break;
				case FALL:
					cursorSet = !cursorBlock.fall;
					break;
				case GEM_CENTER:
					cursorSet = !cursorBlock.gemC;
					break;
				case GEM_DOWN:
					cursorSet = !cursorBlock.gemD;
					break;
				case GEM_LEFT:
					cursorSet = !cursorBlock.gemL;
					break;
				case GEM_RIGHT:
					cursorSet = !cursorBlock.gemR;
					break;
				case GEM_UP:
					cursorSet = !cursorBlock.gemU;
					break;
				default:
					break;
				}
			}
		}
		if (Input.MouseDown()) {
			boolean setNew = cursorBlock == null && cursorSet;
			boolean setOld = cursorBlock != null && !cursorVisited[cX][cY];
			switch (cursorState) {
			case BLOCK_ACTIVE:
				if (setNew) gridBlocks[cX][cY] = new EditorBlock().setActive(true);
				else if (setOld) cursorBlock.setActive(true);
				break;
			case BLOCK_MOVE:
				if (setNew) gridBlocks[cX][cY] = new EditorBlock().setMove(true);
				else if (setOld) cursorBlock.setMove(true);
				break;
			case BLOCK_STATIC:
				if (setNew || setOld) gridBlocks[cX][cY] = new EditorBlock().setActive(false);
				break;
			case BOMB:
				if (setNew) gridBlocks[cX][cY] = new EditorBlock().setBomb(true);
				else if (setOld) cursorBlock.setBomb(cursorSet);
				break;
			case ERASER:
				gridBlocks[cX][cY] = null;
				break;
			case FALL:
				if (setNew) gridBlocks[cX][cY] = new EditorBlock().setFall(true);
				else if (setOld) cursorBlock.setFall(cursorSet);
				break;
			case GEM_CENTER:
				if (setNew) gridBlocks[cX][cY] = new EditorBlock().setGemC(true);
				else if (setOld) cursorBlock.setGemC(cursorSet);
				break;
			case GEM_DOWN:
				if (setNew) gridBlocks[cX][cY] = new EditorBlock().setGemD(true);
				else if (setOld) cursorBlock.setGemD(cursorSet);
				break;
			case GEM_LEFT:
				if (setNew) gridBlocks[cX][cY] = new EditorBlock().setGemL(true);
				else if (setOld) cursorBlock.setGemL(cursorSet);
				break;
			case GEM_RIGHT:
				if (setNew) gridBlocks[cX][cY] = new EditorBlock().setGemR(true);
				else if (setOld) cursorBlock.setGemR(cursorSet);
				break;
			case GEM_UP:
				if (setNew) gridBlocks[cX][cY] = new EditorBlock().setGemU(true);
				else if (setOld) cursorBlock.setGemU(cursorSet);
				break;
			default:
				break;
			}
			if (setNew || setOld) {
				cursorVisited[cX][cY] = true;
				modified = true;
			}
		}
	}

	private void handleSpecials() {
		if (Input.MousePressed()) {
			switch (cursorState) {
			case GATE:
				break;
			case PATH:
				break;
			case WATER:
				break;
			default:
				break;
			}
		}
		if (Input.MousePressed()) {

		}
	}

	public void update() {
		cursorBlock = gridBlocks[cX][cY];
		cursorSpecial = gridSpecials[cX][cY];
		handleBlocks();
		handleSpecials();
		if (Input.MouseReleased()) {
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					cursorVisited[i][j] = false;
				}
			}
		}
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
		} else if (Input.KeyPressed(Input.M)) {
			cursorState = Res.Cursors.BLOCK_MOVE;
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
}
