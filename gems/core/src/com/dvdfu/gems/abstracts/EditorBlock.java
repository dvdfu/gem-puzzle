package com.dvdfu.gems.abstracts;

public class EditorBlock {
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

	public String getID() {
		String id = "b";
		if (active) id += "a";
		if (move) id += "m";
		if (fall) id += "f";
		if (bomb) id += "b";
		if (wind) id += "w" + direction;
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
		wind = false;
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
		wind = false;
		bomb = false;
		if (move) active = true;
		return this;
	}

	public EditorBlock setFall(boolean set) {
		fall = set;
		wind = false;
		if (fall) active = true;
		return this;
	}

	public EditorBlock setBomb(boolean set) {
		bomb = set;
		wind = false;
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
		wind = false;
		if (gemU) {
			active = true;
			move = true;
			gemC = false;
			bomb = false;
		}
		return this;
	}

	public EditorBlock setGemD(boolean set) {
		gemD = set;
		wind = false;
		if (gemD) {
			active = true;
			move = true;
			gemC = false;
			bomb = false;
		}
		return this;
	}

	public EditorBlock setGemR(boolean set) {
		gemR = set;
		wind = false;
		if (gemR) {
			active = true;
			move = true;
			gemC = false;
			bomb = false;
		}
		return this;
	}

	public EditorBlock setGemL(boolean set) {
		gemL = set;
		wind = false;
		if (gemL) {
			active = true;
			move = true;
			gemC = false;
			bomb = false;
		}
		return this;
	}

	public EditorBlock setGemC(boolean set) {
		gemC = set;
		wind = false;
		if (gemC) {
			active = true;
			move = true;
			gemU = false;
			gemD = false;
			gemR = false;
			gemL = false;
			bomb = false;
		}
		return this;
	}
	
	public EditorBlock setWind(boolean set, int direction) {
		wind = set;
		this.direction = direction;
		if (wind) {
			active = false;
			move = false;
			fall = false;
			bomb = false;
			gemU = false;
			gemD = false;
			gemR = false;
			gemL = false;
			gemC = false;
		}
		return this;
	}
}