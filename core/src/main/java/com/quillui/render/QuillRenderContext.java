package com.quillui.render;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Tracks the content-to-screen translation while a clipped host is rendering. Widgets that use
 * their own scissor (for example the map) must translate the clip rectangle with this offset.
 */
public final class QuillRenderContext {
	private static final Deque<int[]> STACK = new ArrayDeque<>();
	private static int offsetX;
	private static int offsetY;

	private QuillRenderContext() {
	}

	public static void pushClipOffset(int x, int y) {
		STACK.push(new int[] {offsetX, offsetY});
		offsetX += x;
		offsetY += y;
	}

	public static void popClipOffset() {
		if (STACK.isEmpty()) {
			offsetX = 0;
			offsetY = 0;
			return;
		}
		int[] previous = STACK.pop();
		offsetX = previous[0];
		offsetY = previous[1];
	}

	public static int screenX(int contentX) {
		return contentX - offsetX;
	}

	public static int screenY(int contentY) {
		return contentY - offsetY;
	}
}
