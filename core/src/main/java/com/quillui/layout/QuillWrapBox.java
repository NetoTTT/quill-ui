package com.quillui.layout;

import java.util.ArrayList;
import java.util.List;

/** Places content-sized children left-to-right and wraps to a new row when needed. */
public final class QuillWrapBox extends QuillNode {
	private final int gapX;
	private final int gapY;
	private final List<QuillLayoutNode> items = new ArrayList<>();
	private List<QuillSize> childSizes = List.of();

	public QuillWrapBox(int gapX, int gapY) {
		this.gapX = Math.max(0, gapX);
		this.gapY = Math.max(0, gapY);
	}

	public QuillWrapBox(int gap) {
		this(gap, gap);
	}

	public QuillWrapBox add(QuillLayoutNode node) {
		items.add(node);
		return this;
	}

	@Override
	public QuillSize measure(QuillBoxConstraints constraints) {
		if (items.isEmpty()) {
			return constraints.constrain(QuillSize.ZERO);
		}

		QuillBoxConstraints childConstraints = QuillBoxConstraints.loose(
				constraints.maxWidth(),
				constraints.maxHeight()
		);
		QuillSize[] sizes = new QuillSize[items.size()];
		int contentWidth = 0;
		for (int index = 0; index < items.size(); index++) {
			sizes[index] = items.get(index).measure(childConstraints);
			contentWidth = QuillLayoutMath.saturatedAdd(contentWidth, sizes[index].width());
		}

		int availableWidth = constraints.hasBoundedWidth()
				? constraints.maxWidth()
				: QuillLayoutMath.saturatedAdd(
						contentWidth,
						gapX * Math.max(0, items.size() - 1)
				);
		int[] rows = rowLayout(sizes, availableWidth);
		childSizes = List.of(sizes);
		return constraints.constrain(QuillSize.of(rows[0], rows[1]));
	}

	@Override
	public void arrange(QuillRect bounds) {
		setBounds(bounds);
		int cursorX = bounds.x();
		int cursorY = bounds.y();
		int rowHeight = 0;
		for (int index = 0; index < items.size(); index++) {
			QuillSize size = childSizes.get(index);
			if (cursorX > bounds.x() && QuillLayoutMath.saturatedAdd(cursorX, size.width()) > bounds.right()) {
				cursorX = bounds.x();
				cursorY = QuillLayoutMath.saturatedAdd(
						cursorY,
						QuillLayoutMath.saturatedAdd(rowHeight, gapY)
				);
				rowHeight = 0;
			}
			items.get(index).arrange(new QuillRect(
					cursorX, cursorY, size.width(), size.height()
			));
			cursorX = QuillLayoutMath.saturatedAdd(
					cursorX,
					QuillLayoutMath.saturatedAdd(size.width(), gapX)
			);
			rowHeight = Math.max(rowHeight, size.height());
		}
	}

	@Override
	public List<QuillLayoutNode> children() {
		return List.copyOf(items);
	}

	public List<QuillSize> childSizes() {
		return childSizes;
	}

	private int[] rowLayout(QuillSize[] sizes, int availableWidth) {
		int rowWidth = 0;
		int rowHeight = 0;
		int maxWidth = 0;
		int totalHeight = 0;
		boolean rowHasItems = false;
		for (QuillSize size : sizes) {
			boolean wrap = rowHasItems && QuillLayoutMath.saturatedAdd(
					QuillLayoutMath.saturatedAdd(rowWidth, gapX),
					size.width()
			) > availableWidth;
			if (wrap) {
				maxWidth = Math.max(maxWidth, rowWidth);
				totalHeight = QuillLayoutMath.saturatedAdd(
						totalHeight,
						QuillLayoutMath.saturatedAdd(rowHeight, gapY)
				);
				rowWidth = 0;
				rowHeight = 0;
				rowHasItems = false;
			}
			rowWidth = QuillLayoutMath.saturatedAdd(
					rowWidth,
					QuillLayoutMath.saturatedAdd(rowHasItems ? gapX : 0, size.width())
			);
			rowHeight = Math.max(rowHeight, size.height());
			rowHasItems = true;
		}
		maxWidth = Math.max(maxWidth, rowWidth);
		totalHeight = QuillLayoutMath.saturatedAdd(totalHeight, rowHeight);
		return new int[] {maxWidth, totalHeight};
	}
}
