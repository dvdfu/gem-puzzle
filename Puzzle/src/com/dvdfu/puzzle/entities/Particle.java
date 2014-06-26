package com.dvdfu.puzzle.entities;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.dvdfu.puzzle.handlers.Vars;

public class Particle implements Poolable {
	public String filename;
	public float x;
	public float y;
	public float dx;
	public float dy;
	public float ax;
	public float ay;
	public int ticks;

	public Particle() {
	}
	
	public void update() {
		ticks++;
		dx += ax;
		dy += ay;
		x += dx;
		y += dy;
	}
	
	public int frame() {
		return ticks / Vars.ticksPerFrame;
	}

	public void reset() {
		ticks = 0;
	}
}