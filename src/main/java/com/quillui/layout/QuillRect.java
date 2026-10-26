package com.quillui.layout;

/** Immutable GUI rectangle used by the layout primitives. */
public record QuillRect(int x, int y, int width, int height) {
	public int right() {
		return x + width;
	}

	public int bottom() {
		return y + height;
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
