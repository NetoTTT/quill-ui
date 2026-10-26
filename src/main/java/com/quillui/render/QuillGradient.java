package com.quillui.render;

/**
 * Two-colour gradient description. The renderer supports vertical and horizontal directions.
 */
public record QuillGradient(int startColor, int endColor, Direction direction) {
	public enum Direction {
		VERTICAL,
		HORIZONTAL
	}

	public static QuillGradient vertical(int startColor, int endColor) {
		return new QuillGradient(startColor, endColor, Direction.VERTICAL);
	}

	public static QuillGradient horizontal(int startColor, int endColor) {
		return new QuillGradient(startColor, endColor, Direction.HORIZONTAL);
	}
}
