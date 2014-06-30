package com.dvdfu.puzzle.entities;

public class Special {
	public boolean path;
	public boolean hazard;
	public boolean button;
	public boolean buttonToggled;
	public boolean gate;
	public boolean gateToggled;
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
	
	public Special setHazard() {
		path = false;
		hazard = true;
		return this;
	}
	
	public Special setButton(int destX, int destY) {
		button = true;
		this.destX = destX;
		this.destY = destY;
		return this;
	}
	
	public Special setGate(boolean toggled) {
		button = true;
		gateToggled = toggled;
		return this;
	}
}
