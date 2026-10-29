package com.quillui.layout;

/** Base class that stores the rectangle assigned by the parent. */
public abstract class QuillNode implements QuillLayoutNode {
	private QuillRect bounds = new QuillRect(0, 0, 0, 0);

	@Override
	public final QuillRect bounds() {
		return bounds;
	}

	protected final void setBounds(QuillRect bounds) {
		this.bounds = bounds == null ? new QuillRect(0, 0, 0, 0) : bounds;
	}
}
