package com.dvdfu.gems.abstracts;

import com.dvdfu.gems.references.Res;
import com.dvdfu.gems.references.Res.Command;

public class Block {
	public Res.Command command;
	public boolean active;
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
	public boolean throughPath;
	public int timer;

	public Block() {
		command = Command.HOLD;
	}

	public boolean isGem() {
		return gemC || gemU || gemD || gemR || gemL;
	}
}