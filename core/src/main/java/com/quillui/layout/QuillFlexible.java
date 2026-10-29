package com.quillui.layout;

import java.util.List;

/**
 * Marks a child as flexible inside {@link QuillFlexBox}. {@code QuillFlexBox.add(...)} unwraps it,
 * so static builders can mix fixed and weighted children in one list.
 */
public record QuillFlexible(QuillLayoutNode node, int weight) implements QuillLayoutNode {
	public QuillFlexible {
		if (node == null) {
			throw new IllegalArgumentException("node must not be null");
		}
		if (weight < 0) {
			throw new IllegalArgumentException("weight must be >= 0");
		}
	}

	@Override
	public QuillSize measure(QuillBoxConstraints constraints) {
		return node.measure(constraints);
	}

	@Override
	public void arrange(QuillRect bounds) {
		node.arrange(bounds);
	}

	@Override
	public QuillRect bounds() {
		return node.bounds();
	}

	@Override
	public List<QuillLayoutNode> children() {
		return node.children();
	}
}
