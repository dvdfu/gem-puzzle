package com.dvdfu.gems.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.dvdfu.gems.view.Animation;

public class Assets {
	private static final TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("data/images.atlas"));
	public static final Sprite grid = atlas.createSprite("grid");
	public static final Sprite path = atlas.createSprite("path");
	public static final Sprite blockFalling = atlas.createSprite("block_falling");
	public static final Sprite blockActive = atlas.createSprite("block_active");
	public static final Sprite blockMove = atlas.createSprite("block_move");
	public static final Sprite blockMoveB = atlas.createSprite("block_move_b");
	public static final Sprite blockStatic = atlas.createSprite("block_static");
	public static final Sprite blockStaticB = atlas.createSprite("block_static_b");
	public static final Sprite waterHead = atlas.createSprite("water_head");
	public static final Sprite waterBody = atlas.createSprite("water_body");
	public static final Sprite gateB = atlas.createSprite("gate_b");
	public static final Sprite gateClosed = atlas.createSprite("gate_closed");
	public static final Sprite gate = atlas.createSprite("gate");
	public static final Sprite button = atlas.createSprite("button");
	public static final Sprite bomb = atlas.createSprite("bomb");
	public static final Sprite dot = atlas.createSprite("dot");
	public static final Sprite cursorUnselect = atlas.createSprite("cursor_unselect");
	public static final Sprite cursorSelect = atlas.createSprite("cursor_select");
	public static final Sprite windR = atlas.createSprite("wind_r");
	public static final Sprite windL = atlas.createSprite("wind_l");
	public static final Sprite windU = atlas.createSprite("wind_u");
	public static final Sprite windD = atlas.createSprite("wind_d");
	public static final Sprite blockGemC = atlas.createSprite("block_gem_c");
	public static final Sprite blockGemU = atlas.createSprite("block_gem_u");
	public static final Sprite blockGemD = atlas.createSprite("block_gem_d");
	public static final Sprite blockGemR = atlas.createSprite("block_gem_r");
	public static final Sprite blockGemL = atlas.createSprite("block_gem_l");
	
	private static final Sprite particleSparkle = atlas.createSprite("particle_sparkle");
	private static final Sprite particleDirt = atlas.createSprite("particle_dirt");
	private static final Sprite particleDust = atlas.createSprite("particle_dust");
	private static final Sprite particleDroplet = atlas.createSprite("particle_droplet");
	private static final Sprite particleGem = atlas.createSprite("particle_gem");
	private static final Sprite particleCracks = atlas.createSprite("block_cracks");
	private static final Sprite particleFireSmall = atlas.createSprite("particle_fire_small");
	private static final Sprite particleFireBig = atlas.createSprite("particle_fire_big");
	private static final Sprite particleSink = atlas.createSprite("particle_sink");
	
	public static final Animation sparkle = new Animation(particleSparkle, 16, 16);
	public static final Animation dirt = new Animation(particleDirt, 8, 8);
	public static final Animation dust = new Animation(particleDust, 8, 8);
	public static final Animation droplet = new Animation(particleDroplet, 8, 8);
	public static final Animation blockGem = new Animation(particleGem, 16, 16);
	public static final Animation blockCrack = new Animation(particleCracks, 32, 32);
	public static final Animation fireSmall = new Animation(particleFireSmall, 4, 4);
	public static final Animation fireBig = new Animation(particleFireBig, 8, 8);
	public static final Animation sink = new Animation(particleSink, 32, 16);
}
