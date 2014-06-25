package com.dvdfu.puzzle.entities;

public class Block {
	public enum Command {
		HOLD, GEM, BIG_GEM, MOVE_UP, MOVE_DOWN, MOVE_RIGHT, MOVE_LEFT, PATH_ENTER, PATH_EXIT
	};
	public Command command;
	public boolean active;
	public boolean move;
	public boolean fall;
	public boolean gemC;
	public boolean gemU;
	public boolean gemD;
	public boolean gemR;
	public boolean gemL;
	
	public Block() {
		command = Command.HOLD;
		setStatic();
	}
	
	public Block setStatic() {
		active = false;
		move = false;
		fall = false;
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
		gemC = !u && !d && !r && !l;
		gemU = u;
		gemD = d;
		gemR = r;
		gemL = l;
		return this;
	}
	
	public boolean isGem() {
		return gemC || gemU || gemD || gemR || gemL;
	}
}