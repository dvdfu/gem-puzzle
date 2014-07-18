package com.dvdfu.gems.handlers;

import com.badlogic.gdx.math.Vector2;

public class Input {
	public static Vector2 mouse;
	public static boolean mouseClick;
	private static boolean mouseClickPrev;
	public static boolean[] keys;
	private static boolean[] keysPrev;
	public static final int ARROW_UP = 0;
	public static final int ARROW_DOWN = 1;
	public static final int ARROW_LEFT = 2;
	public static final int ARROW_RIGHT = 3;
	public static final int SPACEBAR = 4;
	public static final int ENTER = 5;
	public static final int BACKSPACE = 6;
	public static final int A = 7;
	public static final int B = 8;
	public static final int F = 9;
	public static final int G = 10;
	public static final int H = 11;
	public static final int M = 12;
	public static final int P = 13;
	public static final int W = 14;
	public static final int Y = 15;
	public static final int Z = 16;
	public static final int CTRL = 17;
	private static final int NUM_KEYS = 18;
	static {
		mouse = new Vector2();
		keys = new boolean[NUM_KEYS];
		keysPrev = new boolean[NUM_KEYS];
	}

	public static void update() {
		mouseClickPrev = mouseClick;
		for (int i = 0; i < NUM_KEYS; i++) {
			keysPrev[i] = keys[i];
		}
	}

	public static boolean MouseDown() {
		return mouseClick;
	}

	public static boolean MousePressed() {
		return mouseClick && !mouseClickPrev;
	}

	public static boolean MouseReleased() {
		return !mouseClick && mouseClickPrev;
	}

	public static void setKey(int i, boolean b) {
		keys[i] = b;
	}

	public static boolean KeyDown(int i) {
		return keys[i];
	}

	public static boolean KeyPressed(int i) {
		return keys[i] && !keysPrev[i];
	}

	public static boolean KeyReleased(int i) {
		return !keys[i] && keysPrev[i];
	}
}