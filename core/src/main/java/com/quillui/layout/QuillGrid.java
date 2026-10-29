package com.quillui.layout;

/** Fixed-column grid for forms and dashboard cards. */
public final class QuillGrid {
	private final int x;
	private final int y;
	private final int width;
	private final int columns;
	private final int gap;
	private final int rowHeight;

	public QuillGrid(int x, int y, int width, int columns, int gap, int rowHeight) {
		if (columns < 1) {
			throw new IllegalArgumentException("columns must be >= 1");
		}
		this.x = x;
		this.y = y;
		this.width = width;
		this.columns = columns;
		this.gap = gap;
		this.rowHeight = rowHeight;
	}

	public QuillRect cell(int index) {
		if (index < 0) {
			throw new IllegalArgumentException("index must be >= 0");
		}
		int cellWidth = (width - gap * (columns - 1)) / columns;
		int column = index % columns;
		int row = index / columns;
		return new QuillRect(
				x + column * (cellWidth + gap),
				y + row * (rowHeight + gap),
				cellWidth,
				rowHeight
		);
	}

	public int columns() {
		return columns;
	}
}
