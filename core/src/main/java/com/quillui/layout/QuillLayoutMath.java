package com.quillui.layout;

/** Overflow-safe arithmetic for layout sums. */
final class QuillLayoutMath {
	private QuillLayoutMath() {
	}

	/**
	 * Adds two non-negative sizes, saturating at {@link QuillBoxConstraints#UNBOUNDED} instead of
	 * wrapping into a negative value.
	 */
	static int saturatedAdd(int left, int right) {
		long sum = (long) left + right;
		return (int) Math.min(QuillBoxConstraints.UNBOUNDED, sum);
	}
}
