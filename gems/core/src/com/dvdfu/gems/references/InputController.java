package com.dvdfu.gems.references;

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
		case Keys.ENTER:
			Input.setKey(Input.ENTER, true);
			break;
		case Keys.SPACE:
			Input.setKey(Input.SPACEBAR, true);
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
		case Keys.ENTER:
			Input.setKey(Input.ENTER, false);
			break;
		case Keys.SPACE:
			Input.setKey(Input.SPACEBAR, false);
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