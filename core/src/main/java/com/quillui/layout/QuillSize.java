package com.quillui.layout;

/** Immutable width/height pair used by the layout engine. */
public record QuillSize(int width, int height) {
	public static final QuillSize ZERO = new QuillSize(0, 0);

	public QuillSize {
		if (width < 0 || height < 0) {
			throw new IllegalArgumentException("size must be >= 0");
		}
	}

	public static QuillSize of(int width, int height) {
		return new QuillSize(width, height);
	}

	public boolean isEmpty() {
		return width == 0 || height == 0;
	}

	public QuillSize plus(QuillInsets insets) {
		return new QuillSize(
				QuillLayoutMath.saturatedAdd(width, insets.horizontal()),
				QuillLayoutMath.saturatedAdd(height, insets.vertical())
		);
	}
}
