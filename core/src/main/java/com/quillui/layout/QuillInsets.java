package com.quillui.layout;

/** Padding/border insets for a GUI rectangle. */
public record QuillInsets(int top, int right, int bottom, int left) {
	public static QuillInsets all(int value) {
		return new QuillInsets(value, value, value, value);
	}

	public static QuillInsets symmetric(int vertical, int horizontal) {
		return new QuillInsets(vertical, horizontal, vertical, horizontal);
	}

	public int horizontal() {
		return left + right;
	}

	public int vertical() {
		return top + bottom;
	}

	/** Content rectangle after applying these insets, clamped to the parent bounds. */
	public QuillRect contentRect(QuillRect bounds) {
		int contentLeft = Math.min(left, bounds.width());
		int contentTop = Math.min(top, bounds.height());
		int contentWidth = Math.max(0, bounds.width() - horizontal());
		int contentHeight = Math.max(0, bounds.height() - vertical());
		return new QuillRect(
				bounds.x() + contentLeft,
				bounds.y() + contentTop,
				contentWidth,
				contentHeight
		);
	}
}
