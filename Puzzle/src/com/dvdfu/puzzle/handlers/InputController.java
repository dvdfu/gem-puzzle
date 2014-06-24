package com.dvdfu.puzzle.handlers;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Gdx;
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
		}
		return true;
	}
	
	private void updateMouse(int x, int y) {
		Input.mouse.x = x;
		Input.mouse.y = y;
	}
}