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
	}

	public boolean isGem() {
		return gemC || gemU || gemD || gemR || gemL;
	}
}