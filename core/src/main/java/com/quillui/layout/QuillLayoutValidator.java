package com.quillui.layout;

import java.util.ArrayList;
import java.util.List;

/** Debug-only layout invariants. Scroll content is allowed to exceed its viewport. */
public final class QuillLayoutValidator {
	private QuillLayoutValidator() {
	}

	public static List<String> validate(QuillLayoutNode root) {
		List<String> issues = new ArrayList<>();
		if (root == null) {
			issues.add("root is null");
			return List.copyOf(issues);
		}
		validate(root, null, false, issues);
		return List.copyOf(issues);
	}

	private static void validate(
			QuillLayoutNode node,
			QuillLayoutNode parent,
			boolean allowOverflow,
			List<String> issues
	) {
		QuillRect bounds = node.bounds();
		if (bounds.width() < 0 || bounds.height() < 0) {
			issues.add(node.getClass().getSimpleName() + " has a negative size: " + bounds);
		}
		if (parent != null && !allowOverflow && !contains(parent.bounds(), bounds)) {
			issues.add(node.getClass().getSimpleName() + " exceeds "
					+ parent.getClass().getSimpleName() + ": child=" + bounds
					+ " parent=" + parent.bounds());
		}
		boolean childOverflow = allowOverflow || node instanceof QuillScrollBox;
		for (QuillLayoutNode child : node.children()) {
			if (child == null) {
				issues.add(node.getClass().getSimpleName() + " has a null child");
				continue;
			}
			validate(child, node, childOverflow, issues);
		}
	}

	private static boolean contains(QuillRect parent, QuillRect child) {
		return child.x() >= parent.x()
				&& child.y() >= parent.y()
				&& child.right() <= parent.right()
				&& child.bottom() <= parent.bottom();
	}
}
