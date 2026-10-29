package com.quillui.layout;

/** Places a child rectangle inside a parent rectangle with optional alignment. */
public final class QuillStack {
	private final QuillRect bounds;

	public QuillStack(QuillRect bounds) {
		this.bounds = bounds == null ? new QuillRect(0, 0, 0, 0) : bounds;
	}

	public QuillRect bounds() {
		return bounds;
	}

	public QuillRect fill() {
		return bounds;
	}

	public QuillRect fill(QuillInsets insets) {
		return bounds.inset(insets);
	}

	public QuillRect center(int width, int height) {
		return place(width, height, QuillAlign.CENTER, QuillAlign.CENTER);
	}

	public QuillRect place(
			int width, int height, QuillAlign horizontal, QuillAlign vertical
	) {
		int childWidth = Math.max(1, Math.min(width, bounds.width()));
		int childHeight = Math.max(1, Math.min(height, bounds.height()));
		int childX = bounds.x() + horizontal.offset(bounds.width(), childWidth);
		int childY = bounds.y() + vertical.offset(bounds.height(), childHeight);
		return new QuillRect(childX, childY, childWidth, childHeight);
	}
}
