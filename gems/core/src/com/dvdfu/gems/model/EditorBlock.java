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
	
	public void toggleActive() {
		active ^= true;
		if (!active) {
			move = false;
			fall = false;
			bomb = false;
			gemU = false;
			gemD = false;
			gemR = false;
			gemL = false;
		}
	}
	
	public void toggleMove() {
		move ^= true;
		if (move) active = false;
		else bomb = false;
	}
	
	public void toggleFall() {
		fall ^= true;
		if (fall) active = true;
	}
	
	public void toggleBomb() {
		bomb ^= true;
		if (bomb) {
			active = true;
			move = true;
			gemC = false;
			gemU = false;
			gemD = false;
			gemR = false;
			gemL = false;
		}
	}
	
	public void toggleGemU() {
		gemU ^= true;
		if (gemU) {
			active = true;
			gemC = false;
			bomb = false;
		}
	}
	
	public void toggleGemD() {
		gemD ^= true;
		if (gemD) {
			active = true;
			gemC = false;
			bomb = false;
		}
	}
	
	public void toggleGemR() {
		gemR ^= true;
		if (gemR) {
			active = true;
			gemC = false;
			bomb = false;
		}
	}
	
	public void toggleGemL() {
		gemL ^= true;
		if (gemL) {
			active = true;
			gemC = false;
			bomb = false;
		}
	}
	
	public void toggleGemC() {
		gemC ^= true;
		if (gemC) {
			active = true;
			gemU = false;
			gemD = false;
			gemR = false;
			gemL = false;
			bomb = false;
		}
	}
}