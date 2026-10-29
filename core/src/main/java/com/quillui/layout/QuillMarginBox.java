package com.quillui.layout;

import java.util.List;

/** Adds outer spacing around a child. Unlike padding, margin is outside the child's own box. */
public final class QuillMarginBox extends QuillNode {
	private final QuillInsets margin;
	private final QuillLayoutNode child;
	private QuillSize childSize = QuillSize.ZERO;

	public QuillMarginBox(QuillInsets margin, QuillLayoutNode child) {
		if (margin == null) {
			throw new IllegalArgumentException("margin must not be null");
		}
		if (margin.top() < 0 || margin.right() < 0
				|| margin.bottom() < 0 || margin.left() < 0) {
			throw new IllegalArgumentException("margin must be >= 0");
		}
		this.margin = margin;
		this.child = child;
	}

	@Override
	public QuillSize measure(QuillBoxConstraints constraints) {
		QuillBoxConstraints childConstraints = constraints.deflate(margin);
		if (child == null) {
			return constraints.constrain(QuillSize.of(
					Math.min(margin.horizontal(), constraints.maxWidth()),
					Math.min(margin.vertical(), constraints.maxHeight())
			));
		}
		childSize = child.measure(childConstraints);
		return constraints.constrain(childSize.plus(margin));
	}

	@Override
	public void arrange(QuillRect bounds) {
		setBounds(bounds);
		if (child != null) {
			child.arrange(margin.contentRect(bounds));
		}
	}

	@Override
	public List<QuillLayoutNode> children() {
		return child == null ? List.of() : List.of(child);
	}

	public QuillSize childSize() {
		return childSize;
	}

	public QuillInsets margin() {
		return margin;
	}
}
