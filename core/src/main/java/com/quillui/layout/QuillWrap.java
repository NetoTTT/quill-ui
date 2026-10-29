package com.quillui.layout;

/** Wraps equal-sized items into rows inside a fixed bounds rectangle. */
public final class QuillWrap {
	private final QuillRect bounds;
	private final int itemWidth;
	private final int itemHeight;
	private final int gapX;
	private final int gapY;

	public QuillWrap(
			QuillRect bounds, int itemWidth, int itemHeight, int gapX, int gapY
	) {
		this.bounds = bounds == null ? new QuillRect(0, 0, 0, 0) : bounds;
		this.itemWidth = Math.max(1, Math.min(itemWidth, Math.max(1, bounds.width())));
		this.itemHeight = Math.max(1, itemHeight);
		this.gapX = Math.max(0, gapX);
		this.gapY = Math.max(0, gapY);
	}

	public QuillWrap(QuillRect bounds, int itemWidth, int itemHeight, int gap) {
		this(bounds, itemWidth, itemHeight, gap, gap);
	}

	public int columns() {
		return Math.max(1, (bounds.width() + gapX) / (itemWidth + gapX));
	}

	public int rows(int count) {
		if (count <= 0) {
			return 0;
		}
		return (count + columns() - 1) / columns();
	}

	public QuillRect cell(int index) {
		if (index < 0) {
			throw new IllegalArgumentException("index must be >= 0");
		}
		int columns = columns();
		int column = index % columns;
		int row = index / columns;
		return new QuillRect(
				bounds.x() + column * (itemWidth + gapX),
				bounds.y() + row * (itemHeight + gapY),
				itemWidth,
				itemHeight
		);
	}

	public int contentHeight(int count) {
		int rows = rows(count);
		return rows == 0 ? 0 : rows * itemHeight + (rows - 1) * gapY;
	}
}
