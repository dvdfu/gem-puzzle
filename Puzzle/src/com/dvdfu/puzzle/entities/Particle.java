package com.dvdfu.puzzle.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dvdfu.puzzle.handlers.Vars;

public class Particle implements Poolable {
	public enum Type {
		SPARKLE, DIRT, DUST, DUST_L, DUST_R, DROP, GEM
	}

	public Type type;
	public String filename;
	private float x;
	private float y;
	private float dx;
	private float dy;
	private float ax;
	private float ay;
	private int tick;
	private int frameLimit;

	public Particle() {}

	public void update() {
		tick++;
		dx += ax;
		dy += ay;
		x += dx;
		y += dy;
	}

	public final int getX() {
		return (int) x;
	}

	public final int getY() {
		return (int) y;
	}

	public final int frame() {
		return tick / Vars.ticksPerFrame;
	}

	public final boolean dead() {
		return tick > frameLimit * Vars.ticksPerFrame;
	}
	
	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void setVelocity(float dx, float dy) {
		this.dx = dx;
		this.dy = dy;
	}
	
	public void setVector(float speed, float angle) {
		dx = speed * MathUtils.cos(angle);
		dy = speed * MathUtils.sin(angle);
	}
	
	public void setAcceleration(float ax, float ay) {
		this.ax = ax;
		this.ay = ay;
	}
	
	public void setDuration(int tick, int frameLimit) {
		this.tick = tick;
		this.frameLimit = frameLimit;
	}

	public void reset() {
		tick = 0;
		dx = 0;
		dy = 0;
		ax = 0;
		ay = 0;
	}
}