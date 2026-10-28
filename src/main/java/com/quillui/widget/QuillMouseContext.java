package com.quillui.widget;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Keeps the difference between content coordinates and screen coordinates while a clipped host is
 * rendering. Widgets still use content coordinates for hit-testing, while tooltips must anchor to
 * the real cursor position.
 */
final class QuillMouseContext {
	private static final Deque<int[]> STACK = new ArrayDeque<>();
	private static int offsetX;
	private static int offsetY;

	private QuillMouseContext() {
	}

	static void push(int x, int y) {
		STACK.push(new int[] {offsetX, offsetY});
		offsetX += x;
		offsetY += y;
	}

	static void pop() {
		if (STACK.isEmpty()) {
			offsetX = 0;
			offsetY = 0;
			return;
		}
		int[] previous = STACK.pop();
		offsetX = previous[0];
		offsetY = previous[1];
	}

	static int screenX(int contentX) {
		return contentX - offsetX;
	}

	static int screenY(int contentY) {
		return contentY - offsetY;
	}
}
