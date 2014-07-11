package com.dvdfu.gems.handlers;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

public class InputController extends InputAdapter {

	public boolean mouseMoved(int x, int y) {
		updateMouse(x, y);
		return true;
	}

	public boolean touchDragged(int x, int y, int pointer) {
		updateMouse(x, y);
		Input.mouseClick = true;
		return true;
	}

	public boolean touchDown(int x, int y, int pointer, int button) {
		updateMouse(x, y);
		Input.mouseClick = true;
		return true;
	}

	public boolean touchUp(int x, int y, int pointer, int button) {
		updateMouse(x, y);
		Input.mouseClick = false;
		return true;
	}

	public boolean keyDown(int k) {
		switch (k) {
		case Keys.UP:
			Input.setKey(Input.ARROW_UP, true);
			break;
		case Keys.DOWN:
			Input.setKey(Input.ARROW_DOWN, true);
			break;
		case Keys.LEFT:
			Input.setKey(Input.ARROW_LEFT, true);
			break;
		case Keys.RIGHT:
			Input.setKey(Input.ARROW_RIGHT, true);
			break;
		case Keys.SPACE:
			Input.setKey(Input.SPACEBAR, true);
			break;
		case Keys.TAB:
			Input.setKey(Input.TAB, true);
			break;
		case Keys.DEL:
			Input.setKey(Input.BACKSPACE, true);
			break;
		case Keys.A:
			Input.setKey(Input.A, true);
			break;
		case Keys.B:
			Input.setKey(Input.B, true);
			break;
		case Keys.F:
			Input.setKey(Input.F, true);
			break;
		case Keys.G:
			Input.setKey(Input.G, true);
			break;
		case Keys.H:
			Input.setKey(Input.H, true);
			break;
		case Keys.M:
			Input.setKey(Input.M, true);
			break;
		case Keys.P:
			Input.setKey(Input.P, true);
			break;
		case Keys.Y:
			Input.setKey(Input.Y, true);
			break;
		case Keys.Z:
			Input.setKey(Input.Z, true);
			break;
		case Keys.CONTROL_LEFT:
			Input.setKey(Input.CTRL, true);
			break;
		}
		return true;
	}

	public boolean keyUp(int k) {
		switch (k) {
		case Keys.UP:
			Input.setKey(Input.ARROW_UP, false);
			break;
		case Keys.DOWN:
			Input.setKey(Input.ARROW_DOWN, false);
			break;
		case Keys.LEFT:
			Input.setKey(Input.ARROW_LEFT, false);
			break;
		case Keys.RIGHT:
			Input.setKey(Input.ARROW_RIGHT, false);
			break;
		case Keys.SPACE:
			Input.setKey(Input.SPACEBAR, false);
			break;
		case Keys.TAB:
			Input.setKey(Input.TAB, false);
			break;
		case Keys.BACKSPACE:
			Input.setKey(Input.BACKSPACE, false);
			break;
		case Keys.A:
			Input.setKey(Input.A, false);
			break;
		case Keys.B:
			Input.setKey(Input.B, false);
			break;
		case Keys.F:
			Input.setKey(Input.F, false);
			break;
		case Keys.G:
			Input.setKey(Input.G, false);
			break;
		case Keys.H:
			Input.setKey(Input.H, false);
			break;
		case Keys.M:
			Input.setKey(Input.M, false);
			break;
		case Keys.P:
			Input.setKey(Input.P, false);
			break;
		case Keys.Y:
			Input.setKey(Input.Y, false);
			break;
		case Keys.Z:
			Input.setKey(Input.Z, false);
			break;
		case Keys.CONTROL_LEFT:
			Input.setKey(Input.CTRL, false);
			break;
		}
		return true;
	}

	private void updateMouse(int x, int y) {
		Input.mouse.x = x;
		Input.mouse.y = y;
	}
}