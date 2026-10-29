package com.quillui.layout;

/** Top-to-bottom stack of fixed-height slots with a fixed gap. */
public final class QuillColumn {
	private final int x;
	private final int width;
	private final int gap;
	private int cursorY;

	public QuillColumn(int x, int y, int width, int gap) {
		this.x = x;
		this.width = width;
		this.gap = gap;
		this.cursorY = y;
	}

	public QuillRect next(int height) {
		QuillRect rect = new QuillRect(x, cursorY, width, height);
		cursorY += height + gap;
		return rect;
	}

	public QuillRect next(int height, QuillInsets insets) {
		return next(height).inset(insets);
	}

	public int y() {
		return cursorY;
	}

	public int width() {
		return width;
	}
}
