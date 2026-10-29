package com.quillui.layout;

/**
 * Box constraints for the layout engine. The parent passes a minimum and maximum size; a node
 * chooses its size inside that range. {@link Integer#MAX_VALUE} means unbounded.
 */
public record QuillBoxConstraints(int minWidth, int minHeight, int maxWidth, int maxHeight) {
	public static final int UNBOUNDED = Integer.MAX_VALUE;
	public static final QuillBoxConstraints LOOSE =
			new QuillBoxConstraints(0, 0, UNBOUNDED, UNBOUNDED);

	public QuillBoxConstraints {
		if (minWidth < 0 || minHeight < 0) {
			throw new IllegalArgumentException("minimum sizes must be >= 0");
		}
		if (maxWidth < minWidth || maxHeight < minHeight) {
			throw new IllegalArgumentException("maximum sizes must be >= minimum sizes");
		}
	}

	public static QuillBoxConstraints tight(QuillSize size) {
		return new QuillBoxConstraints(size.width(), size.height(), size.width(), size.height());
	}

	public static QuillBoxConstraints tightFor(int width, int height) {
		return tight(QuillSize.of(width, height));
	}

	public static QuillBoxConstraints loose(QuillSize maximum) {
		return new QuillBoxConstraints(0, 0, maximum.width(), maximum.height());
	}

	public static QuillBoxConstraints loose(int maxWidth, int maxHeight) {
		return new QuillBoxConstraints(0, 0, maxWidth, maxHeight);
	}

	public static QuillBoxConstraints unbounded() {
		return new QuillBoxConstraints(0, 0, UNBOUNDED, UNBOUNDED);
	}

	public QuillSize constrain(QuillSize size) {
		return new QuillSize(
				Math.min(maxWidth, Math.max(minWidth, size.width())),
				Math.min(maxHeight, Math.max(minHeight, size.height()))
		);
	}

	public QuillBoxConstraints deflate(QuillInsets insets) {
		return deflate(insets.horizontal(), insets.vertical());
	}

	public QuillBoxConstraints deflate(int horizontal, int vertical) {
		int nextMinWidth = Math.max(0, minWidth - horizontal);
		int nextMinHeight = Math.max(0, minHeight - vertical);
		int nextMaxWidth = maxWidth == UNBOUNDED
				? UNBOUNDED
				: Math.max(nextMinWidth, maxWidth - horizontal);
		int nextMaxHeight = maxHeight == UNBOUNDED
				? UNBOUNDED
				: Math.max(nextMinHeight, maxHeight - vertical);
		return new QuillBoxConstraints(
				nextMinWidth,
				nextMinHeight,
				nextMaxWidth,
				nextMaxHeight
		);
	}

	public QuillBoxConstraints enforce(QuillBoxConstraints parent) {
		int nextMinWidth = clamp(minWidth, parent.minWidth, parent.maxWidth);
		int nextMaxWidth = clamp(maxWidth, parent.minWidth, parent.maxWidth);
		int nextMinHeight = clamp(minHeight, parent.minHeight, parent.maxHeight);
		int nextMaxHeight = clamp(maxHeight, parent.minHeight, parent.maxHeight);
		if (nextMaxWidth < nextMinWidth) {
			nextMaxWidth = nextMinWidth;
		}
		if (nextMaxHeight < nextMinHeight) {
			nextMaxHeight = nextMinHeight;
		}
		return new QuillBoxConstraints(
				nextMinWidth,
				nextMinHeight,
				nextMaxWidth,
				nextMaxHeight
		);
	}

	public QuillBoxConstraints withMinWidth(int value) {
		return new QuillBoxConstraints(value, minHeight, Math.max(value, maxWidth), maxHeight);
	}

	public QuillBoxConstraints withMinHeight(int value) {
		return new QuillBoxConstraints(minWidth, value, maxWidth, Math.max(value, maxHeight));
	}

	public QuillBoxConstraints withMaxWidth(int value) {
		return new QuillBoxConstraints(Math.min(minWidth, value), minHeight, value, maxHeight);
	}

	public QuillBoxConstraints withMaxHeight(int value) {
		return new QuillBoxConstraints(minWidth, Math.min(minHeight, value), maxWidth, value);
	}

	public boolean hasBoundedWidth() {
		return maxWidth != UNBOUNDED;
	}

	public boolean hasBoundedHeight() {
		return maxHeight != UNBOUNDED;
	}

	public boolean hasTightWidth() {
		return minWidth == maxWidth;
	}

	public boolean hasTightHeight() {
		return minHeight == maxHeight;
	}

	public boolean isTight() {
		return hasTightWidth() && hasTightHeight();
	}

	private static int clamp(int value, int minimum, int maximum) {
		return Math.min(maximum, Math.max(minimum, value));
	}
}
