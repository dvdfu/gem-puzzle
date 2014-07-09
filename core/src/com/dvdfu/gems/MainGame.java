package com.dvdfu.gems;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.dvdfu.gems.entities.Board;
import com.dvdfu.gems.handlers.Input;
import com.dvdfu.gems.handlers.InputController;
import com.dvdfu.gems.handlers.Vars;
import com.dvdfu.gems.handlers.View;

public class MainGame implements ApplicationListener {
	private Board board;
	private View view;

	public void create() {
		Gdx.input.setInputProcessor(new InputController());
		board = new Board(Vars.boardWidth, Vars.boardHeight);
		view = new View(board);
	}

	public void dispose() {
		view.dispose();
	}

	public void render() {
		view.update(Input.mouse.x, Input.mouse.y);
		if (board.timerReady()) {
			view.endBuffer(); // apply end-buffer view changes to all buffered blocks
			board.useBuffer(); // apply end-buffer board changes to grid
			// at this point all blocks should have timer = 0 and command = hold
			board.update(); /* timer is ready, board looks for buffers */
			if (board.checkTimer()) view.beginBuffer();
		} else board.updateTimer();
		view.draw();
		if (Input.KeyPressed(Input.SPACEBAR)) board.reset();
		Input.update();
	}

	public void resize(int width, int height) {
		view.resize(width, height);
	}

	public void pause() {}

	public void resume() {}
}
