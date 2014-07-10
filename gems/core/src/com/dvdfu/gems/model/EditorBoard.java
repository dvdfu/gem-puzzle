package com.dvdfu.gems.model;

import java.util.Stack;
import com.badlogic.gdx.math.MathUtils;
import com.dvdfu.gems.handlers.Input;

public class EditorBoard {
	private EditorBlock cursorBlock;
	private EditorBlock gridBlock[][];
	private Special cursorSpecial;
	private Special gridSpecial[][];
	private Stack<GridState> undoStack;
	private Stack<GridState> redoStack;
	private int width;
	private int height;
	private int cursorX;
	private int cursorY;
	public boolean placingPath;
	public boolean placingGate;
	public int placeX;
	public int placeY;
	private boolean modified;

	private class GridState {
		public EditorBlock blocks[][];
		public Special specials[][];

		public GridState(int width, int height) {
			blocks = new EditorBlock[width][height];
			specials = new Special[width][height];
		}
	}

	public EditorBoard(int width, int height) {
		this.width = width;
		this.height = height;
		cursorX = 0;
		cursorY = 0;
		gridBlock = new EditorBlock[width][height];
		gridSpecial = new Special[width][height];
		placingPath = false;
		placingGate = false;
		placeX = 0;
		placeY = 0;
		reset();

		undoStack = new Stack<GridState>();
		redoStack = new Stack<GridState>();
		pushState();
		modified = false;
	}

	private void pushState() {
		GridState state = new GridState(width, height);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				state.blocks[i][j] = gridBlock[i][j];
				state.specials[i][j] = gridSpecial[i][j];
			}
		}
		undoStack.push(state);
		redoStack.clear();
		modified = false;
	}
	
	public void undoState() {
		if (undoStack.empty()) return;
		GridState temp = undoStack.pop();
		if (undoStack.empty()) {
			reset();
			return;
		}
		GridState state = undoStack.peek();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				gridBlock[i][j] = state.blocks[i][j];
				gridSpecial[i][j] = state.specials[i][j];
			}
		}
		redoStack.push(temp);
	}

	public void redoState() {
		if (redoStack.empty()) return;
		GridState state = redoStack.pop();
		GridState newState = new GridState(width, height);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				gridBlock[i][j] = state.blocks[i][j];
				gridSpecial[i][j] = state.specials[i][j];
				newState.blocks[i][j] = gridBlock[i][j];
				newState.specials[i][j] = gridSpecial[i][j];
			}
		}
		undoStack.push(newState);
	}

	public void reset() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				gridBlock[i][j] = null;
				gridSpecial[i][j] = null;
			}
		}
		cursorBlock = null;
	}

	public void addBlock(EditorBlock block, int x, int y) {
		gridBlock[x][y] = block;
	}

	public void addPath(int x1, int y1, int x2, int y2) {
		if (gridValid(x1, y1) && gridValid(x2, y2) && !(x1 == x2 && y1 == y2)) {
			clearSpecial(x1, y1);
			clearSpecial(x2, y2);
			gridSpecial[x1][y1] = new Special().setPath(x2, y2);
			gridSpecial[x2][y2] = new Special().setPath(x1, y1);
		}
	}

	public void addGate(int gateX, int gateY, int buttonX, int buttonY, boolean gateOriginal) {
		if (gridValid(buttonX, buttonY) && gridValid(gateX, gateY) && !(buttonX == gateX && buttonY == gateY)) {
			clearSpecial(gateX, gateY);
			gridSpecial[buttonX][buttonY] = new Special().setButton();
			gridSpecial[gateX][gateY] = new Special().setGate(buttonX, buttonY, gateOriginal);
		}
	}

	private void clearSpecial(int x, int y) {
		Special special = gridSpecial[x][y];
		if (special == null) return;
		if (special.path) {
			gridSpecial[special.destX][special.destY] = null;
			gridSpecial[x][y] = null;
		} else if (special.button) {
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					Special gate = gridSpecial[i][j];
					if (gate != null && gate.gate && gate.destX == x && gate.destY == y) gridSpecial[i][j] = null;
				}
			}
			gridSpecial[x][y] = null;
		}
	}

	public void update() {
		cursorBlock = gridBlock[cursorX][cursorY];
		cursorSpecial = gridSpecial[cursorX][cursorY];
		// EDITING BLOCKS
		if (Input.MouseDown() && cursorBlock == null) {
			addBlock(new EditorBlock(), cursorX, cursorY);
			modified = true;
		}
		if (Input.KeyDown(Input.BACKSPACE) && cursorBlock != null) {
			gridBlock[cursorX][cursorY] = null;
			modified = true;
		}
		if (cursorBlock != null) {
			if (Input.KeyPressed(Input.ARROW_UP)) {
				gridBlock[cursorX][cursorY].toggleGemU();
				modified = true;
			}
			if (Input.KeyPressed(Input.ARROW_DOWN)) {
				gridBlock[cursorX][cursorY].toggleGemD();
				modified = true;
			}
			if (Input.KeyPressed(Input.ARROW_RIGHT)) {
				gridBlock[cursorX][cursorY].toggleGemR();
				modified = true;
			}
			if (Input.KeyPressed(Input.ARROW_LEFT)) {
				gridBlock[cursorX][cursorY].toggleGemL();
				modified = true;
			}
			if (Input.KeyPressed(Input.SPACEBAR)) {
				gridBlock[cursorX][cursorY].toggleGemC();
				modified = true;
			}
			if (Input.KeyPressed(Input.A)) {
				gridBlock[cursorX][cursorY].toggleActive();
				modified = true;
			}
			if (Input.KeyPressed(Input.B)) {
				gridBlock[cursorX][cursorY].toggleBomb();
				modified = true;
			}
			if (Input.KeyPressed(Input.F)) {
				gridBlock[cursorX][cursorY].toggleFall();
				modified = true;
			}
			if (Input.KeyPressed(Input.M)) {
				gridBlock[cursorX][cursorY].toggleMove();
				modified = true;
			}
		}
		// EDITING SPECIALS
		if (Input.KeyDown(Input.BACKSPACE) && cursorSpecial != null) {
			clearSpecial(cursorX, cursorY);
			gridSpecial[cursorX][cursorY] = null;
			modified = true;
		}
		if (Input.KeyDown(Input.H)) {
			gridSpecial[cursorX][cursorY] = new Special().setHazard();
			gridBlock[cursorX][cursorY] = null;
			modified = true;
		}
		if (Input.KeyPressed(Input.G)) {
			Special special = gridSpecial[cursorX][cursorY];
			if (special != null && special.gate) {
				special.gateOriginal ^= true;
				modified = true;
			} else {
				placingGate = true;
				placingPath = false;
				placeX = cursorX;
				placeY = cursorY;
			}
		}
		if (Input.KeyReleased(Input.G) && placingGate) {
			placingGate = false;
			placingPath = false;
			if (placeX != cursorX || placeY != cursorY) {
				addGate(placeX, placeY, cursorX, cursorY, true);
				modified = true;
			}
		}
		if (Input.KeyPressed(Input.P)) {
			placingPath = true;
			placingGate = false;
			placeX = cursorX;
			placeY = cursorY;
		}
		if (Input.KeyReleased(Input.P) && placingPath) {
			placingPath = false;
			placingGate = false;
			if (placeX != cursorX || placeY != cursorY) {
				addPath(placeX, placeY, cursorX, cursorY);
				modified = true;
			}
		}
		
		// UNDO
		if (Input.MouseReleased() && modified) pushState();
		for (int i = 0; i < Input.keys.length; i++) {
			if (Input.KeyReleased(i) && modified) {
				pushState();
				break;
			}
		}
		if (Input.KeyPressed(Input.PGUP)) undoState();
		if (Input.KeyPressed(Input.PGDN)) redoState();
	}

	public final boolean gridHas(int x, int y) {
		return gridValid(x, y) && gridBlock[x][y] != null;
	}

	public final boolean gridEmpty(int x, int y) {
		return gridValid(x, y) && gridBlock[x][y] == null;
	}

	public final boolean gridValid(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}

	/* PUBLIC FINAL VIEW FUNCTIONS The following functions have public access and are intended for use by the viewer which retrieves grid information to draw */

	public final EditorBlock[][] getGrid() {
		return gridBlock;
	}

	public final Special[][] getSpecial() {
		return gridSpecial;
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

	public void setCursor(int x, int y) {
		cursorX = MathUtils.clamp(x, 0, width - 1);
		cursorY = MathUtils.clamp(y, 0, height - 1);
	}
}
