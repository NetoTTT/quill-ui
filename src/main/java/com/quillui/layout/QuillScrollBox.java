package com.quillui.layout;

import java.util.List;

/**
 * Scroll viewport measurement. The child is measured with an unbounded main axis, then arranged
 * with an offset. Rendering and input remain the responsibility of the widget layer.
 */
public final class QuillScrollBox extends QuillNode {
	public enum Direction {
		VERTICAL,
		HORIZONTAL
	}

	private final Direction direction;
	private final QuillLayoutNode child;
	private QuillSize contentSize = QuillSize.ZERO;
	private QuillSize viewportSize = QuillSize.ZERO;
	private int scrollOffset;

	public QuillScrollBox(Direction direction, QuillLayoutNode child) {
		this.direction = direction == null ? Direction.VERTICAL : direction;
		this.child = child;
	}

	public static QuillScrollBox vertical(QuillLayoutNode child) {
		return new QuillScrollBox(Direction.VERTICAL, child);
	}

	public static QuillScrollBox horizontal(QuillLayoutNode child) {
		return new QuillScrollBox(Direction.HORIZONTAL, child);
	}

	@Override
	public QuillSize measure(QuillBoxConstraints constraints) {
		if (child == null) {
			return constraints.constrain(QuillSize.ZERO);
		}
		boolean vertical = direction == Direction.VERTICAL;
		QuillBoxConstraints childConstraints = vertical
				? new QuillBoxConstraints(
						constraints.hasBoundedWidth() ? constraints.maxWidth() : 0,
						0,
						constraints.hasBoundedWidth() ? constraints.maxWidth() : QuillBoxConstraints.UNBOUNDED,
						QuillBoxConstraints.UNBOUNDED
				)
				: new QuillBoxConstraints(
						0,
						constraints.hasBoundedHeight() ? constraints.maxHeight() : 0,
						QuillBoxConstraints.UNBOUNDED,
						constraints.hasBoundedHeight() ? constraints.maxHeight() : QuillBoxConstraints.UNBOUNDED
				);

		contentSize = child.measure(childConstraints);
		QuillSize own = direction == Direction.VERTICAL
				? QuillSize.of(
						constraints.hasBoundedWidth() ? constraints.maxWidth() : contentSize.width(),
						contentSize.height()
				)
				: QuillSize.of(
						contentSize.width(),
						constraints.hasBoundedHeight() ? constraints.maxHeight() : contentSize.height()
				);
		viewportSize = constraints.constrain(own);
		return viewportSize;
	}

	@Override
	public void arrange(QuillRect bounds) {
		setBounds(bounds);
		if (child == null) {
			return;
		}
		scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll()));
		QuillRect contentBounds = direction == Direction.VERTICAL
				? new QuillRect(
						bounds.x(),
						bounds.y() - scrollOffset,
						bounds.width(),
						contentSize.height()
				)
				: new QuillRect(
						bounds.x() - scrollOffset,
						bounds.y(),
						contentSize.width(),
						bounds.height()
				);
		child.arrange(contentBounds);
	}

	@Override
	public List<QuillLayoutNode> children() {
		return child == null ? List.of() : List.of(child);
	}

	public QuillSize contentSize() {
		return contentSize;
	}

	public int scrollOffset() {
		return scrollOffset;
	}

	public int maxScroll() {
		if (child == null) {
			return 0;
		}
		int viewportWidth = bounds().width() > 0 ? bounds().width() : viewportSize.width();
		int viewportHeight = bounds().height() > 0 ? bounds().height() : viewportSize.height();
		return direction == Direction.VERTICAL
				? Math.max(0, contentSize.height() - viewportHeight)
				: Math.max(0, contentSize.width() - viewportWidth);
	}

	public void scrollTo(int offset) {
		scrollOffset = Math.max(0, Math.min(offset, maxScroll()));
	}

	public void scrollBy(int delta) {
		scrollTo(scrollOffset + delta);
	}
}
