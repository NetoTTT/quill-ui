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
}
