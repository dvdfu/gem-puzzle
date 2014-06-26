package com.dvdfu.puzzle.handlers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Sprite {
	private TextureRegion[] frames;

	public Sprite(Texture reg, int width, int height) {
		frames = new TextureRegion[reg.getWidth() / width];
		for (int i = 0; i < frames.length; i++) {
			frames[i] = new TextureRegion(reg, i * width, 0, width, height);
		}
	}

	public TextureRegion getFrameAt(int frame) {
		return frames[frame % frames.length];
	}
}