package com.dvdfu.gems.abstracts;

import java.util.Stack;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;
import com.dvdfu.gems.references.Input;
import com.dvdfu.gems.references.Res;
import com.dvdfu.gems.references.Res.Cursors;

public class EditorBoard {
	private EditorBlock cursorBlock;
	private EditorBlock gridBlocks[][];
	private Special cursorSpecial;
	private Special gridSpecials[][];
	private boolean cursorVisited[][];
	private Stack<String> undoStack;
	private Stack<String> redoStack;
	private Res.Cursors cursorState;
	public boolean cursorGemU;
	public boolean cursorGemD;
	public boolean cursorGemR;
	public boolean cursorGemL;
	public boolean cursorGemC;
	public boolean cursorFall;
	private String name;
	private int width;
	private int height;
	private int cX;
	private int cY;
	private boolean cIn;
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
		prefs.flush();
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

	// CURSOR PLACEMENT

	public void setCursorState(Res.Cursors state) {
		cursorState = state;
	}

	public Res.Cursors getCursorState() {
		return cursorState;
	}

	public void setCursor(int x, int y) {
		if (x < 0) {
			cX = 0;
			cIn = false;
		} else if (x > width - 1) {
			cX = width - 1;
			cIn = false;
		} else {
			cX = x;
			cIn = true;
		}
		if (y < 0) {
			cY = 0;
			cIn = false;
		} else if (y > height - 1) {
			cY = height - 1;
			cIn = false;
		} else {
			cY = y;
			cIn = true;
		}
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
			clearSpecial(buttonX, buttonY);
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
		boolean addNew = true;
		if (Input.MousePressed()) {
			switch (cursorState) {
			case GATE:
				if (cursorSpecial != null) addNew = !cursorSpecial.gate;
				if (addNew) {
					placeX = cX;
					placeY = cY;
				}
				break;
			case PATH:
				placeX = cX;
				placeY = cY;
				break;
			case WIND:
				if (cursorBlock != null) addNew = !cursorBlock.wind;
				break;
			default:
				break;
			}
		}
		if (Input.MouseDown()) {
			EditorBlock newBlock = null;
			switch (cursorState) {
			case BLOCK_ACTIVE:
				newBlock = new EditorBlock().setActive(true);
				newBlock.setGemU(cursorGemU);
				newBlock.setGemD(cursorGemD);
				newBlock.setGemR(cursorGemR);
				newBlock.setGemL(cursorGemL);
				newBlock.setGemC(cursorGemC);
				newBlock.setFall(cursorFall);
				break;
			case BLOCK_MOVE:
				newBlock = new EditorBlock().setMove(true);
				newBlock.setGemU(cursorGemU);
				newBlock.setGemD(cursorGemD);
				newBlock.setGemR(cursorGemR);
				newBlock.setGemL(cursorGemL);
				newBlock.setGemC(cursorGemC);
				newBlock.setFall(cursorFall);
				break;
			case BLOCK_STATIC:
				newBlock = new EditorBlock().setActive(false);
				clearSpecial(cX, cY);
				break;
			case BOMB:
				newBlock = new EditorBlock().setBomb(true);
				newBlock.setFall(cursorFall);
				break;
			case WIND:
				newBlock = new EditorBlock();
				if (addNew) newBlock.setWind(true, 0);
				else newBlock.setWind(true, (cursorBlock.direction + 1) % 4);
				break;
			case ERASER:
				gridBlocks[cX][cY] = null;
				clearSpecial(cX, cY);
				modified = true;
				cursorVisited[cX][cY] = true;
				break;
			case GATE:
				if (addNew) cursorPlacing = true;
				else cursorSpecial.original ^= true;
				break;
			case PATH:
				cursorPlacing = true;
				break;
			case WATER:
				clearSpecial(cX, cY);
				gridBlocks[cX][cY] = null;
				gridSpecials[cX][cY] = new Special().setWater();
				modified = true;
				cursorVisited[cX][cY] = true;
				break;
			default:
				break;
			}
			// conditions to place new block: new block must be defined, cell has not been visited previously, original block doesn't exist or is different
			if (newBlock != null && !cursorVisited[cX][cY]
				&& (cursorBlock == null || !cursorBlock.getID().equals(newBlock.getID()))) {
				modified = true;
				cursorVisited[cX][cY] = true;
				gridBlocks[cX][cY] = newBlock;
				if (gridSpecials[cX][cY] != null && gridSpecials[cX][cY].water) {
					clearSpecial(cX, cY);
				}
			}
		}
		if (Input.MouseReleased()) {
			if (cursorPlacing) {
				if (cursorState == Res.Cursors.GATE) {
					addGate(placeX, placeY, cX, cY, true);
					modified = true;
				} else if (cursorState == Res.Cursors.PATH) {
					addPath(placeX, placeY, cX, cY);
					modified = true;
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
		if (cIn) handleAdd();
		// UNDO
		if (Input.MouseReleased() && modified) pushState();
		if (Input.KeyDown(Input.CTRL)) {
			if (Input.KeyPressed(Input.Z)) undoState();
			if (Input.KeyPressed(Input.Y)) redoState();
		}
		if (Input.KeyPressed(Input.ENTER)) saveState();
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

	public final int placeX() {
		return placeX;
	}

	public final int placeY() {
		return placeY;
	}
}
