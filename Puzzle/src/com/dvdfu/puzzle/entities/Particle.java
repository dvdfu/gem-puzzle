package com.dvdfu.puzzle.entities;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.dvdfu.puzzle.handlers.Vars;

public class Particle implements Poolable {
	public enum Type {
		SPARKLE, DIRT, DUST, DUST_L, DUST_R, DROP, GEM
	}
	public Type type;
	public String filename;
	public boolean polar;
	public float x;
	public float y;
	public float dx;
	public float dy;
	public float ax;
	public float ay;
	public int ticks;
	public int frameLimit;

	public Particle() {
	}
	
	public void update() {
		ticks++;
		dx += ax;
		dy += ay;
		x += dx;
		y += dy;
	}
	
	public int getX() {
		return (int) x;
	}
	
	public int getY() {
		return (int) y;
	}
	
	public int frame() {
		return ticks / Vars.ticksPerFrame;
	}
	
	public boolean dead() {
		return ticks > frameLimit * Vars.ticksPerFrame;
	}

	public void reset() {
		ticks = 0;
		dx = 0;
		dy = 0;
		ax = 0;
		ay = 0;
	}
}