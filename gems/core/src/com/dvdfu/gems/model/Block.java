package com.dvdfu.gems.model;

import com.dvdfu.gems.handlers.Enums;
import com.dvdfu.gems.handlers.Enums.Command;

public class Block {

	public Enums.Command command;
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