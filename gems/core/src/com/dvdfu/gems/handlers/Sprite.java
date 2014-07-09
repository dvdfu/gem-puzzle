package com.dvdfu.gems.handlers;

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

	public TextureRegion getFrame(int frame) {
		return frames[frame % frames.length];
	}
	
	public int getWidth() {
		return frames[0].getRegionWidth();
	}
	
	public int getHeight() {
		return frames[0].getRegionHeight();
	}
}