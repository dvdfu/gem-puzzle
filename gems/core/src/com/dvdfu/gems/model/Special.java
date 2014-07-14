package com.dvdfu.gems.model;

public class Special {
	public boolean path;
	public boolean water;
	public boolean button;
	public boolean gate;
	public boolean toggled;
	public boolean original;
	public int destX;
	public int destY;
	
	public String getID() {
		String id = "s";
		if (path) id += "p" + destX + "" + destY;
		else if (water) id += "h";
		else if (button) id += "b";
		else if (gate) id += "g" + destX + "" + destY + (original ? "t" : "f");
		return id + ";";
	}

	public Special setPath(int destX, int destY) {
		path = true;
		this.destX = destX;
		this.destY = destY;
		return this;
	}

	public Special setWater() {
		water = true;
		return this;
	}

	public Special setButton() {
		button = true;
		return this;
	}

	public Special setGate(int buttonX, int buttonY, boolean original) {
		gate = true;
		this.original = original;
		toggled = original;
		destX = buttonX;
		destY = buttonY;
		return this;
	}
}
