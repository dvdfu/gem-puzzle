package com.dvdfu.gems.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Res {
	public static final boolean editor = false;

	// public static final int screenWidth = 32 * 9;
	// public static final int screenHeight = 32 * 11;
	public static final int screenWidth = 720 / 2;
	public static final int screenHeight = 1280 / 2;

	public static final int ticksPerSecond = 60;
	public static final int ticksPerFrame = 4;
	public static final float framesPerSecond = ticksPerSecond / ticksPerFrame;

	public static final int fullSize = 32;
	public static final int halfSize = fullSize / 2;
	public static final int timeMove = 4;
	public static final int timePath = 16;
	public static final int timeGem = 16;
	public static final int timeDrown = 2;
	public static final int timeExplode = 2;

	public static final TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("data/images.atlas"));

	// public static final Sprite particle_sparkle = atlas.createSprite("particle_sparkle");

	public static enum Command {
		HOLD, BREAK, EXPLODE, DROWN, MOVE_UP, MOVE_DOWN, MOVE_RIGHT, MOVE_LEFT, PATH, FALL
	};

	public static enum Cursors {
		ERASER, BLOCK_STATIC, BLOCK_ACTIVE, BLOCK_MOVE, BOMB, FALL, GEM_UP, GEM_DOWN, GEM_RIGHT, GEM_LEFT, GEM_CENTER, WATER, PATH, GATE
	};
}
