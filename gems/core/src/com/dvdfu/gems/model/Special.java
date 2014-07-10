package com.dvdfu.gems.model;

public class Special {
	public boolean path;
	public boolean hazard;
	public boolean button;
	public boolean gate;
	public boolean gateOriginal;
	public boolean toggled;
	public int destX;
	public int destY;

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

	public Special setButton() {
		button = true;
		return this;
	}

	public Special setGate(int buttonX, int buttonY, boolean gateOriginal) {
		gate = true;
		this.gateOriginal = gateOriginal;
		toggled = gateOriginal;
		destX = buttonX;
		destY = buttonY;
		return this;
	}
}
