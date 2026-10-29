package com.quillui.layout;

/** Immutable GUI rectangle used by the layout primitives. */
public record QuillRect(int x, int y, int width, int height) {
	public int right() {
		return x + width;
	}

	public int bottom() {
		return y + height;
	}

	public int centerX() {
		return x + width / 2;
	}

	public int centerY() {
		return y + height / 2;
	}

	public boolean intersects(QuillRect other) {
		return x < other.right() && right() > other.x()
				&& y < other.bottom() && bottom() > other.y();
	}

	public QuillRect intersection(QuillRect other) {
		int left = Math.max(x, other.x());
		int top = Math.max(y, other.y());
		int right = Math.min(right(), other.right());
		int bottom = Math.min(bottom(), other.bottom());
		return new QuillRect(left, top, Math.max(0, right - left), Math.max(0, bottom - top));
	}

	public boolean contains(double mouseX, double mouseY) {
		return mouseX >= x && mouseX < right() && mouseY >= y && mouseY < bottom();
	}

	public QuillRect inset(QuillInsets insets) {
		return new QuillRect(
				x + insets.left(),
				y + insets.top(),
				width - insets.horizontal(),
				height - insets.vertical()
		);
	}

	public QuillRect offset(int dx, int dy) {
		return new QuillRect(x + dx, y + dy, width, height);
	}
}
