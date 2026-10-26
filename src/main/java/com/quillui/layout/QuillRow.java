package com.quillui.layout;

/** Left-to-right row of fixed-width slots with a fixed gap. */
public final class QuillRow {
	private final int y;
	private final int height;
	private final int gap;
	private int cursorX;

	public QuillRow(int x, int y, int height, int gap) {
		this.y = y;
		this.height = height;
		this.gap = gap;
		this.cursorX = x;
	}

	public QuillRect next(int width) {
		QuillRect rect = new QuillRect(cursorX, y, width, height);
		cursorX += width + gap;
		return rect;
	}

	public QuillRect next(int width, QuillInsets insets) {
		return next(width).inset(insets);
	}

	public int x() {
		return cursorX;
	}

	public int height() {
		return height;
	}
}
