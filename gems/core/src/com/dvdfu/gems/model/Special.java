package com.dvdfu.gems.model;

public class Special {
	public boolean path;
	public boolean water;
	public boolean button;
	public boolean gate;
	public boolean gateOriginal;
	public boolean toggled;
	public int destX;
	public int destY;

	public Special setPath(int destX, int destY) {
		path = true;
		water = false;
		this.destX = destX;
		this.destY = destY;
		return this;
	}
	
	public String getID() {
		String id = "s";
		if (path) id += "p" + destX + "" + destY;
		else if (water) id += "h";
		else if (button) id += "b";
		else if (gate) id += "g" + destX + "" + destY + (gateOriginal ? "t" : "f");
		return id + ";";
	}

	public Special setWater() {
		path = false;
		water = true;
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
