package com.dvdfu.gems;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.dvdfu.gems.handlers.Input;
import com.dvdfu.gems.handlers.InputController;
import com.dvdfu.gems.model.Board;
import com.dvdfu.gems.model.EditorBoard;
import com.dvdfu.gems.view.EditorView;
import com.dvdfu.gems.view.View;

public class MainGame extends Game implements ApplicationListener {
	private Board board;
	private View view;
	private EditorBoard editorBoard;
	private EditorView editorView;

	public void create() {
		Gdx.input.setInputProcessor(new InputController());
		board = new Board("", 1, 1);
		view = new View(board);
		editorBoard = new EditorBoard("", 1, 1);
		editorView = new EditorView(editorBoard);
		loadLevel();
		editorBoard.pushState();
		setScreen(view);
	}

	private void loadLevel() {
		Preferences prefs = Gdx.app.getPreferences("prefs");
		String data = prefs.getString("level", "name;8;10;");
		board.setState(data);
		view.setBoard(board);
		editorBoard.setState(data);
		editorView.setBoard(editorBoard);
	}

	public void dispose() {
		view.dispose();
		editorView.dispose();
	}

	public void render() {
		if (getScreen() == view) {
			view.update(Input.mouse.x, Input.mouse.y);
			if (board.timerReady()) {
				view.endBuffer(); // apply end-buffer view changes to all buffered blocks
				board.useBuffer(); // apply end-buffer board changes to grid
				// at this point all blocks should have timer = 0 and command = hold
				board.update(); // timer is ready, board looks for buffers
				if (board.checkTimer()) view.beginBuffer();
			} else board.updateTimer();
			if (Input.KeyPressed(Input.ENTER)) setScreen(editorView);
		} else if (getScreen() == editorView) {
			editorBoard.update();
			editorView.update(Input.mouse.x, Input.mouse.y);
			if (Input.KeyPressed(Input.ENTER)) {
				loadLevel();
				setScreen(view);
			}
		}

		super.render();
		Input.update();
	}

	public void resize(int width, int height) {
		view.resize(width, height);
		editorView.resize(width, height);
	}

	public void pause() {}

	public void resume() {}
}
