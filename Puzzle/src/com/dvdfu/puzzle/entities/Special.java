package com.dvdfu.puzzle.entities;

public class Special {
	public boolean path;
	public boolean hazard;
	public int destX;
	public int destY;
	
	public Special() {
	}
	
	public Special setPath(int destX, int destY) {
		path = true;
		hazard = false;
		this.destX = destX;
		this.destY = destY;
		return this;
	}
	
	public Special hazard() {
		path = false;
		hazard = true;
		return this;
	}
}
