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
		// FileHandle file = new FileHandle("/home/david/test.txt");
		// String data = file.readString();
		String data = "name;8;10;03b;04b;05b;06b;07b;09sh;10bam;11bamfl;12bam;13b;14b;15b;16b;17b;19sh;20bamu;21bamfudrl;22bamd;23bb;24b;25b;26b;27b;29sh;30bamc;31bamfr;32bam;33b;34b;35b;36b;37b;39sh;43b;44b;45b;46b;47b;49sh;53sg72t;59sh;63sg72t;69sh;72sb;73b;74b;75b;76b;77b;79sh;";
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
