package com.dvdfu.gems.updated;

public class Piece {
	public boolean moves; // m
	public boolean breaks; // b
	public boolean falls; // f
	public boolean gemU; // u
	public boolean gemD; // d
	public boolean gemR; // l
	public boolean gemL; // r
	public boolean gemC; // c
	public boolean bomb; // e
	public boolean wind; // w
	public int direction; // #
	
	public boolean water; // h
	public boolean path; // p
	private int destX; // ##
	private int destY; // ##
	public boolean gate; // g
	public boolean lever; // s
	public boolean original; // o/x
	public boolean toggled;
	
	public String getID() {
		String id = "";
		if (moves) id += "m";
		if (breaks) id += "b";
		if (falls) id += "f";
		if (gemU) id += "u";
		if (gemD) id += "d";
		if (gemR) id += "r";
		if (gemL) id += "l";
		if (gemC) id += "c";
		if (bomb) id += "e";
		if (wind) id += "w" + direction;
		if (water) id += "h";
		if (path) id += "p" + (destX < 10 ? "0" + destX : destX) + (destY < 10 ? "0" + destY : destY);
		if (gate) id += "b" + (original ? "o" : "x") + (destX < 10 ? "0" + destX : destX) + (destY < 10 ? "0" + destY : destY);
		if (lever) id += "s";
		return id;
	}
	
	public void setID(String id) {
		moves = false;
		breaks = false;
		falls = false;
		gemU = false;
		gemD = false;
		gemR = false;
		gemL = false;
		gemC = false;
		bomb= false;
		wind = false;
		water = false;
		path = false;
		gate = false;
		lever = false;
		char[] charID = id.toCharArray();
		for (int i = 0; i < charID.length; i++) {
			char charKey = charID[i];
			if (charKey == 'm') moves = true;
			if (charKey == 'b') breaks = true;
			if (charKey == 'f') falls = true;
			if (charKey == 'u') gemU = true;
			if (charKey == 'd') gemD = true;
			if (charKey == 'r') gemR = true;
			if (charKey == 'l') gemL = true;
			if (charKey == 'c') gemC = true;
			if (charKey == 'e') bomb = true;
			if (charKey == 'w') {
				wind = true;
				direction = charID[i++] - 48;
			}
			if (charKey == 'h') water = true;
			if (charKey == 'p') {
				path = true;
				original = charID[i++] == 'o';
				destX = (charID[i++] - 48) * 10 + (charID[i++] - 48);
				destY = (charID[i++] - 48) * 10 + (charID[i++] - 48);
			}
			if (charKey == 'g') {
				gate = true;
				destX = (charID[i++] - 48) * 10 + (charID[i++] - 48);
				destY = (charID[i++] - 48) * 10 + (charID[i++] - 48);
			}
			if (charKey == 's') lever = true;
		}
	}
}
