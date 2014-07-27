package com.dvdfu.gems;

import java.util.Stack;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.dvdfu.gems.handlers.Input;
import com.dvdfu.gems.handlers.InputController;
import com.dvdfu.gems.screens.AbstractScreen;
import com.dvdfu.gems.screens.LogoScreen;

public class MainGame extends Game {
	private Stack<AbstractScreen> screens;
	/*private Board board;
	private GameScreen view;
	private EditorBoard editorBoard;
	private EditorScreen editorView;*/

	public void create() {
		Gdx.input.setInputProcessor(new InputController());
		screens = new Stack<AbstractScreen>();
		screens.push(new LogoScreen(this));
		setScreen(screens.peek());
		/*
		Gdx.input.setInputProcessor(new InputController());
		ScreenManager.getInstance().initialize(this);
		board = new Board("", 1, 1);
		view = new GameScreen(board);
		editorBoard = new EditorBoard("", 1, 1);
		editorView = new EditorScreen(editorBoard);
		loadLevel();
		editorBoard.pushState();
		setScreen(view);*/
	}
	
	public void enterScreen(AbstractScreen screen) {
		screens.peek().pause();
		screens.push(screen);
		setScreen(screens.peek());
	}
	
	public void exitScreen() {
		screens.pop();
		screens.peek().resume();
		setScreen(screens.peek());
	}

	/*private void loadLevel() {
		Preferences prefs = Gdx.app.getPreferences("prefs");
		String data = prefs.getString("level", "name;8;10;");
		board.setState(data);
		view.setBoard(board);
		editorBoard.setState(data);
		editorView.setBoard(editorBoard);
	}*/

	public void dispose() {
		/*view.dispose();
		editorView.dispose();*/
	}

	public void render() {
		super.render();
		Input.update();
	}

	public void resize(int width, int height) {
		super.resize(width, height);
	}

	public void pause() {}

	public void resume() {}
}
