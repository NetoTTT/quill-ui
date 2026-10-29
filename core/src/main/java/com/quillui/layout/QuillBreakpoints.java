package com.quillui.layout;

/** Named viewport breakpoints and column helpers for responsive layouts. */
public final class QuillBreakpoints {
	public static final int MEDIUM_MIN_WIDTH = 480;
	public static final int EXPANDED_MIN_WIDTH = 840;

	public enum Breakpoint {
		COMPACT,
		MEDIUM,
		EXPANDED
	}

	private QuillBreakpoints() {
	}

	public static Breakpoint of(int width) {
		if (width >= EXPANDED_MIN_WIDTH) {
			return Breakpoint.EXPANDED;
		}
		if (width >= MEDIUM_MIN_WIDTH) {
			return Breakpoint.MEDIUM;
		}
		return Breakpoint.COMPACT;
	}

	public static int columns(int width, int minColumnWidth, int maxColumns) {
		return columns(width, minColumnWidth, maxColumns, 0);
	}

	public static int columns(int width, int minColumnWidth, int maxColumns, int gap) {
		if (width <= 0 || minColumnWidth <= 0) {
			return 1;
		}
		int spacing = Math.max(0, gap);
		int columns = (width + spacing) / (minColumnWidth + spacing);
		return Math.max(1, Math.min(columns, Math.max(1, maxColumns)));
	}
}
