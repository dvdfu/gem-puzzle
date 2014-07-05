package com.dvdfu.puzzle.handlers;

public class Vars {
	public static final int boardWidth = 8;
	public static final int boardHeight = 10;
	public static final int screenWidth = (boardWidth + 1) * 32;
	public static final int screenHeight = (boardHeight + 1) * 32;
	
	public static final int ticksPerSecond = 60;
	public static final int ticksPerFrame = 4;
	public static final float framesPerSecond = ticksPerSecond / ticksPerFrame;
	
	public static final int blockSize = 32;
	public static final int timeMove = 4;
	public static final int timePath = 16;
	public static final int timeGem = 8;
}
