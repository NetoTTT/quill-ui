package com.quillui.layout;

import java.util.List;

/** Adds padding around a child and deflates the child constraints accordingly. */
public final class QuillPaddingBox extends QuillNode {
	private final QuillInsets insets;
	private final QuillLayoutNode child;
	private QuillSize childSize = QuillSize.ZERO;

	public QuillPaddingBox(QuillInsets insets, QuillLayoutNode child) {
		this.insets = insets == null ? QuillInsets.all(0) : insets;
		this.child = child;
	}

	@Override
	public QuillSize measure(QuillBoxConstraints constraints) {
		QuillBoxConstraints childConstraints = constraints.deflate(insets);
		if (child == null) {
			return constraints.constrain(QuillSize.of(
					Math.min(insets.horizontal(), constraints.maxWidth()),
					Math.min(insets.vertical(), constraints.maxHeight())
			));
		}
		childSize = child.measure(childConstraints);
		return constraints.constrain(childSize.plus(insets));
	}

	@Override
	public void arrange(QuillRect bounds) {
		setBounds(bounds);
		if (child != null) {
			child.arrange(contentRect(bounds));
		}
	}

	@Override
	public List<QuillLayoutNode> children() {
		return child == null ? List.of() : List.of(child);
	}

	public QuillSize childSize() {
		return childSize;
	}

	public QuillInsets insets() {
		return insets;
	}

	private QuillRect contentRect(QuillRect bounds) {
		int left = Math.min(insets.left(), bounds.width());
		int top = Math.min(insets.top(), bounds.height());
		int width = Math.max(0, bounds.width() - insets.horizontal());
		int height = Math.max(0, bounds.height() - insets.vertical());
		return new QuillRect(bounds.x() + left, bounds.y() + top, width, height);
	}
}
