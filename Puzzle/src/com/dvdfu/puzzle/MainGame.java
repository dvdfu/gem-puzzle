package com.dvdfu.puzzle;

import com.badlogic.gdx.ApplicationListener;
import com.dvdfu.puzzle.entities.Board;
import com.dvdfu.puzzle.handlers.Vars;
import com.dvdfu.puzzle.handlers.View;

public class MainGame implements ApplicationListener {
	private Board board;
	private View view;

	public void create() {
		board = new Board(Vars.boardWidth, Vars.boardHeight);
		view = new View(board);
	}

	public void dispose() {
		view.dispose();
	}

	public void render() {
		view.update();
		view.draw();
	}

	public void resize(int width, int height) {
		view.resize(width, height);
	}

	public void pause() {}

	public void resume() {}
}
