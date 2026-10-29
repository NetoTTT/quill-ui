package com.quillui.layout;

/** Minimum and maximum bounds applied to a layout rectangle. */
public record QuillConstraints(int minWidth, int minHeight, int maxWidth, int maxHeight) {
	public QuillConstraints {
		if (minWidth < 0 || minHeight < 0) {
			throw new IllegalArgumentException("minimum sizes must be >= 0");
		}
		if (maxWidth < minWidth || maxHeight < minHeight) {
			throw new IllegalArgumentException("maximum sizes must be >= minimum sizes");
		}
	}

	public static QuillConstraints none() {
		return new QuillConstraints(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	public static QuillConstraints of(int minWidth, int minHeight, int maxWidth, int maxHeight) {
		return new QuillConstraints(minWidth, minHeight, maxWidth, maxHeight);
	}

	public int clampWidth(int width) {
		return Math.max(minWidth, Math.min(width, maxWidth));
	}

	public int clampHeight(int height) {
		return Math.max(minHeight, Math.min(height, maxHeight));
	}

	public QuillRect clamp(QuillRect rect) {
		int width = clampWidth(rect.width());
		int height = clampHeight(rect.height());
		return new QuillRect(rect.x(), rect.y(), width, height);
	}
}
