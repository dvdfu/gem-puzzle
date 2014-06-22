package com.dvdfu.puzzle.handlers;

public class Block {
	private boolean hasL;
	private boolean hasR;
	private boolean hasU;
	private boolean hasD;
	private boolean hasC;
	private boolean falls;
	private boolean moves;
	private boolean used;

	public Block(boolean falls, boolean moves, boolean hasC, boolean hasL, boolean hasR, boolean hasU, boolean hasD) {
		if (hasC) {
			this.hasL = false;
			this.hasR = false;
			this.hasU = false;
			this.hasD = false;
		} else {
			this.hasL = hasL;
			this.hasR = hasR;
			this.hasU = hasU;
			this.hasD = hasD;
		}
		this.falls = falls;
		this.moves = moves;
		used = hasC || hasR || hasL || hasU || hasD;
	}

	public boolean hasC() {
		return hasC;
	}

	public boolean hasL() {
		return hasL;
	}

	public boolean hasR() {
		return hasR;
	}

	public boolean hasU() {
		return hasU;
	}

	public boolean hasD() {
		return hasD;
	}

	public boolean falls() {
		return falls;
	}

	public boolean moves() {
		return moves;
	}

	public boolean used() {
		return used;
	}
}