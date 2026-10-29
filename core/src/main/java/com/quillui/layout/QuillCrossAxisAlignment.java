package com.quillui.layout;

/** Cross-axis alignment for flex and stack layouts. */
public enum QuillCrossAxisAlignment {
	START,
	CENTER,
	END,
	STRETCH;

	public int offset(int available, int child) {
		return switch (this) {
			case START, STRETCH -> 0;
			case CENTER -> (available - child) / 2;
			case END -> available - child;
		};
	}
}
