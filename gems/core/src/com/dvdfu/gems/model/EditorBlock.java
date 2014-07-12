package com.dvdfu.gems.model;

public class EditorBlock {
	public boolean active;
	public boolean move;
	public boolean fall;
	public boolean bomb;
	public boolean gemU;
	public boolean gemD;
	public boolean gemR;
	public boolean gemL;
	public boolean gemC;

	public String getID() {
		String id = "b";
		if (active) id += "a";
		if (move) id += "m";
		if (fall) id += "f";
		if (bomb) id += "b";
		if (gemC) id += "c";
		if (gemU) id += "u";
		if (gemD) id += "d";
		if (gemR) id += "r";
		if (gemL) id += "l";
		return id + ";";
	}

	public EditorBlock setActive(boolean set) {
		active = set;
		move = false;
		bomb = false;
		if (!active) {
			fall = false;
			gemU = false;
			gemD = false;
			gemR = false;
			gemL = false;
		}
		return this;
	}

	public EditorBlock setMove(boolean set) {
		move = set;
		bomb = false;
		if (move) active = true;
		return this;
	}

	public EditorBlock setFall(boolean set) {
		fall = set;
		if (fall) active = true;
		return this;
	}

	public EditorBlock setBomb(boolean set) {
		bomb = set;
		if (bomb) {
			active = true;
			move = true;
			gemC = false;
			gemU = false;
			gemD = false;
			gemR = false;
			gemL = false;
		}
		return this;
	}

	public EditorBlock setGemU(boolean set) {
		gemU = set;
		if (gemU) {
			active = true;
			gemC = false;
			bomb = false;
		}
		return this;
	}

	public EditorBlock setGemD(boolean set) {
		gemD = set;
		if (gemD) {
			active = true;
			gemC = false;
			bomb = false;
		}
		return this;
	}

	public EditorBlock setGemR(boolean set) {
		gemR = set;
		if (gemR) {
			active = true;
			gemC = false;
			bomb = false;
		}
		return this;
	}

	public EditorBlock setGemL(boolean set) {
		gemL = set;
		if (gemL) {
			active = true;
			gemC = false;
			bomb = false;
		}
		return this;
	}

	public EditorBlock setGemC(boolean set) {
		gemC = set;
		if (gemC) {
			active = true;
			gemU = false;
			gemD = false;
			gemR = false;
			gemL = false;
			bomb = false;
		}
		return this;
	}
}