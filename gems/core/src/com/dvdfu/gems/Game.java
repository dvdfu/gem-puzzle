package com.dvdfu.gems;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.dvdfu.gems.handlers.Input;
import com.dvdfu.gems.handlers.InputController;
import com.dvdfu.gems.model.Board;
import com.dvdfu.gems.view.View;

public class Game implements ApplicationListener {
	private Board board;
	private View view;

	public void create() {
		Gdx.input.setInputProcessor(new InputController());
		loadLevel();
		view = new View(board);
	}

	private void loadLevel() {
		FileHandle file = Gdx.files.local("data/test.txt");
		String data = file.readString();
		Array<char[]> dataArray = new Array<char[]>();
		while (data.length() > 1) {
			dataArray.add(data.substring(0, data.indexOf(';')).toCharArray());
			data = data.substring(data.indexOf(';') + 1);
		}

		String name = new String(dataArray.removeIndex(0));
		int width = Integer.parseInt(new String(dataArray.removeIndex(0)));
		int height = Integer.parseInt(new String(dataArray.removeIndex(0)));
		board = new Board(name, width, height);

		for (char[] cell : dataArray)
			board.addByID(cell);
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
		Input.update();
	}

	public void resize(int width, int height) {
		view.resize(width, height);
	}

	public void pause() {}

	public void resume() {}
}
