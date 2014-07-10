package com.dvdfu.gems.model;

public class Block {
	public enum Command {
		HOLD, BREAK, EXPLODE, DROWN, MOVE_UP, MOVE_DOWN, MOVE_RIGHT, MOVE_LEFT, PATH, FALL
	};

	public Command command;

	public boolean active;
	public boolean move;
	public boolean fall;
	public boolean bomb;
	public boolean gemU;
	public boolean gemD;
	public boolean gemR;
	public boolean gemL;

	public boolean gemC;
	public boolean visited;
	
	public int timer;

	public Block() {
		command = Command.HOLD;
		visited = false;
		timer = 0;
		setStatic();
	}

	public Block setStatic() {
		active = false;
		move = false;
		fall = false;
		bomb = false;
		gemC = false;
		gemU = false;
		gemD = false;
		gemR = false;
		gemL = false;
		return this;
	}

	public Block setActive(boolean move, boolean fall) {
		active = true;
		this.move = move;
		this.fall = fall;
		bomb = false;
		gemC = false;
		gemU = false;
		gemD = false;
		gemR = false;
		gemL = false;
		return this;
	}

	public Block setGem(boolean move, boolean fall, boolean u, boolean d, boolean r, boolean l) {
		active = true;
		this.move = move;
		this.fall = fall;
		bomb = false;
		gemC = !u && !d && !r && !l;
		gemU = u;
		gemD = d;
		gemR = r;
		gemL = l;
		return this;
	}

	public Block setBomb(boolean fall) {
		active = true;
		move = true;
		this.fall = fall;
		bomb = true;
		gemC = false;
		gemU = false;
		gemD = false;
		gemR = false;
		gemL = false;
		return this;
	}

	public boolean isGem() {
		return gemC || gemU || gemD || gemR || gemL;
	}
}