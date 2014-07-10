package com.dvdfu.gems;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.dvdfu.gems.handlers.Input;
import com.dvdfu.gems.handlers.InputController;
import com.dvdfu.gems.handlers.Vars;
import com.dvdfu.gems.model.EditorBoard;
import com.dvdfu.gems.view.EditorView;

public class Editor implements ApplicationListener {
	private EditorBoard board;
	private EditorView view;

	public void create() {
		Gdx.input.setInputProcessor(new InputController());
		board = new EditorBoard(Vars.boardWidth, Vars.boardHeight);
		view = new EditorView(board);
	}

	public void dispose() {
		view.dispose();
	}

	public void render() {
		board.update();
		view.update(Input.mouse.x, Input.mouse.y);
		view.draw();
		Input.update();
	}

	public void resize(int width, int height) {
		view.resize(width, height);
	}

	public void pause() {}

	public void resume() {}
}
