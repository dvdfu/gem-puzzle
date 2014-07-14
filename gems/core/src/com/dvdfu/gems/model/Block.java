package com.dvdfu.gems.model;

import com.dvdfu.gems.handlers.Res;
import com.dvdfu.gems.handlers.Res.Command;

public class Block {

	public Res.Command command;
	public boolean destructable;
	public boolean move;
	public boolean fall;
	public boolean bomb;
	public boolean wind;
	public int direction;
	public boolean gemU;
	public boolean gemD;
	public boolean gemR;
	public boolean gemL;
	public boolean gemC;
	public boolean visited;
	public int timer;

	public Block() {
		command = Command.HOLD;
	}

	public boolean isGem() {
		return gemC || gemU || gemD || gemR || gemL;
	}
}