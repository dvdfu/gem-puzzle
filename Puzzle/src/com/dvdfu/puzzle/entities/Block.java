package com.dvdfu.puzzle.entities;

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
	
	public Block(String id) {
		active = id.charAt(0) == '1';
		if (active) {
			move = id.charAt(1) == '1';
			fall = id.charAt(2) == '1';
			bomb = id.charAt(3) == '1';
			gemU = id.charAt(4) == '1';
			gemD = id.charAt(5) == '1';
			gemR = id.charAt(6) == '1';
			gemL = id.charAt(7) == '1';
		} else setStatic();
		command = Command.HOLD;
		visited = false;
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

	public String getID() {
		String id = "";
		boolean[] properties = { active, move, fall, bomb, gemU, gemD, gemR, gemL };
		for (boolean property : properties) {
			id = id + (property ? "1" : "0");
		}
		return "";
	}
}