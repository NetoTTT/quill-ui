package com.quillui.layout;

import java.util.List;

/** Fixed or preferred width/height around an optional child. */
public final class QuillSizedBox extends QuillNode {
	private final Integer width;
	private final Integer height;
	private final QuillLayoutNode child;
	private QuillSize childSize = QuillSize.ZERO;

	public QuillSizedBox(Integer width, Integer height, QuillLayoutNode child) {
		if (width != null && width < 0) {
			throw new IllegalArgumentException("width must be >= 0");
		}
		if (height != null && height < 0) {
			throw new IllegalArgumentException("height must be >= 0");
		}
		this.width = width;
		this.height = height;
		this.child = child;
	}

	public QuillSizedBox(int width, int height) {
		this(width, height, null);
	}

	@Override
	public QuillSize measure(QuillBoxConstraints constraints) {
		int minWidth = constraintMin(width, constraints.minWidth(), constraints.maxWidth());
		int maxWidth = constraintMax(width, constraints.minWidth(), constraints.maxWidth());
		int minHeight = constraintMin(height, constraints.minHeight(), constraints.maxHeight());
		int maxHeight = constraintMax(height, constraints.minHeight(), constraints.maxHeight());
		QuillBoxConstraints childConstraints = new QuillBoxConstraints(
				minWidth, minHeight, maxWidth, maxHeight
		);

		if (child == null) {
			int ownWidth = width == null ? constraints.minWidth() : width;
			int ownHeight = height == null ? constraints.minHeight() : height;
			return constraints.constrain(QuillSize.of(ownWidth, ownHeight));
		}

		childSize = child.measure(childConstraints);
		int ownWidth = width == null ? childSize.width() : width;
		int ownHeight = height == null ? childSize.height() : height;
		return constraints.constrain(QuillSize.of(ownWidth, ownHeight));
	}

	@Override
	public void arrange(QuillRect bounds) {
		setBounds(bounds);
		if (child != null) {
			child.arrange(bounds);
		}
	}

	@Override
	public List<QuillLayoutNode> children() {
		return child == null ? List.of() : List.of(child);
	}

	public QuillSize childSize() {
		return childSize;
	}

	private static int constraintMin(Integer fixed, int parentMin, int parentMax) {
		int value = fixed == null ? parentMin : fixed;
		return clamp(value, parentMin, parentMax);
	}

	private static int constraintMax(Integer fixed, int parentMin, int parentMax) {
		int value = fixed == null ? parentMax : fixed;
		int clamped = clamp(value, parentMin, parentMax);
		return Math.max(clamped, constraintMin(fixed, parentMin, parentMax));
	}

	private static int clamp(int value, int minimum, int maximum) {
		return Math.min(maximum, Math.max(minimum, value));
	}
}
