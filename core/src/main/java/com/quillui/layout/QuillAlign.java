package com.quillui.layout;

/** Alignment of a child rectangle inside a parent stack. */
public enum QuillAlign {
	START,
	CENTER,
	END;

	public int offset(int parentSize, int childSize) {
		return switch (this) {
			case START -> 0;
			case CENTER -> (parentSize - childSize) / 2;
			case END -> parentSize - childSize;
		};
	}
}
