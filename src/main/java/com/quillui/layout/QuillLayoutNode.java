package com.quillui.layout;

import java.util.List;

/**
 * A node in the layout tree. Measurement flows down with constraints; arrangement assigns a
 * concrete rectangle after the parent has decided the final size.
 */
public interface QuillLayoutNode {
	QuillSize measure(QuillBoxConstraints constraints);

	void arrange(QuillRect bounds);

	QuillRect bounds();

	default List<QuillLayoutNode> children() {
		return List.of();
	}
}
