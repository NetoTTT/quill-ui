package com.quillui.layout;

import java.util.List;

/** Applies extra constraints before measuring a child. */
public final class QuillConstrainedBox extends QuillNode {
	private final QuillBoxConstraints additional;
	private final QuillLayoutNode child;
	private QuillSize childSize = QuillSize.ZERO;

	public QuillConstrainedBox(QuillBoxConstraints additional, QuillLayoutNode child) {
		this.additional = additional == null ? QuillBoxConstraints.LOOSE : additional;
		this.child = child;
	}

	@Override
	public QuillSize measure(QuillBoxConstraints constraints) {
		QuillBoxConstraints childConstraints = additional.enforce(constraints);
		if (child == null) {
			return constraints.constrain(QuillSize.of(
					childConstraints.minWidth(),
					childConstraints.minHeight()
			));
		}
		childSize = child.measure(childConstraints);
		return childConstraints.constrain(childSize);
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
}
