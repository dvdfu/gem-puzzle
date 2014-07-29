package com.dvdfu.gems;

import java.util.Stack;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.dvdfu.gems.references.Input;
import com.dvdfu.gems.references.InputController;
import com.dvdfu.gems.screens.AbstractScreen;
import com.dvdfu.gems.screens.LogoScreen;

public class MainGame extends Game {
	private Stack<AbstractScreen> screens;

	public void create() {
		Gdx.input.setInputProcessor(new InputController());
		screens = new Stack<AbstractScreen>();
		enterScreen(new LogoScreen(this));
	}

	public void enterScreen(AbstractScreen screen) {
		if (!screens.isEmpty()) screens.peek().pause();
		screens.push(screen);
		setScreen(screens.peek());
	}
	
	public void changeScreen(AbstractScreen screen) {
		if (screens.isEmpty()) return;
		screens.pop();
		screens.push(screen);
		setScreen(screens.peek());
	}

	public void exitScreen() {
		if (screens.isEmpty()) return;
		screens.pop();
		screens.peek().resume();
		setScreen(screens.peek());
	}

	public void dispose() {}

	public void render() {
		if (getScreen() != null) super.render();
		Input.update();
	}

	public void resize(int width, int height) {
		super.resize(width, height);
	}

	public void pause() {}

	public void resume() {}
}
