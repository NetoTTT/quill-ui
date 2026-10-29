package com.quillui.layout;

import java.util.ArrayList;
import java.util.List;

/**
 * One-dimensional flex layout. Content-sized items keep their measured size; flexible items share
 * the remaining main-axis space by weight. Cross-axis alignment is configurable.
 */
public final class QuillFlexBox extends QuillNode {
	public enum Direction {
		HORIZONTAL,
		VERTICAL
	}

	private record Item(QuillLayoutNode node, int flex) {
	}

	private final Direction direction;
	private final int gap;
	private final List<Item> items = new ArrayList<>();
	private QuillCrossAxisAlignment crossAxisAlignment = QuillCrossAxisAlignment.START;
	private boolean stretchCross;
	private List<QuillSize> childSizes = List.of();

	public QuillFlexBox(Direction direction, int gap) {
		this.direction = direction == null ? Direction.HORIZONTAL : direction;
		this.gap = Math.max(0, gap);
	}

	public static QuillFlexBox row(int gap) {
		return new QuillFlexBox(Direction.HORIZONTAL, gap);
	}

	public static QuillFlexBox column(int gap) {
		return new QuillFlexBox(Direction.VERTICAL, gap);
	}

	public QuillFlexBox add(QuillLayoutNode node) {
		if (node instanceof QuillFlexible flexible) {
			items.add(new Item(flexible.node(), flexible.weight()));
		}
		else {
			items.add(new Item(node, 0));
		}
		return this;
	}

	public QuillFlexBox addFlexible(QuillLayoutNode node, int flex) {
		items.add(new Item(node, Math.max(0, flex)));
		return this;
	}

	public QuillFlexBox crossAxisAlignment(QuillCrossAxisAlignment alignment) {
		this.crossAxisAlignment = alignment == null
				? QuillCrossAxisAlignment.START
				: alignment;
		return this;
	}

	public QuillCrossAxisAlignment crossAxisAlignment() {
		return crossAxisAlignment;
	}

	public int count() {
		return items.size();
	}

	@Override
	public QuillSize measure(QuillBoxConstraints constraints) {
		if (items.isEmpty()) {
			return constraints.constrain(QuillSize.ZERO);
		}

		boolean horizontal = direction == Direction.HORIZONTAL;
		int maxMain = horizontal ? constraints.maxWidth() : constraints.maxHeight();
		int maxCross = horizontal ? constraints.maxHeight() : constraints.maxWidth();
		stretchCross = crossAxisAlignment == QuillCrossAxisAlignment.STRETCH
				&& maxCross != QuillBoxConstraints.UNBOUNDED;
		long totalGap = (long) gap * (items.size() - 1);
		int availableMain = maxMain == QuillBoxConstraints.UNBOUNDED
				? QuillBoxConstraints.UNBOUNDED
				: (int) Math.max(0L, maxMain - totalGap);

		QuillSize[] sizes = new QuillSize[items.size()];
		int fixedMain = 0;
		int flexTotal = 0;
		for (int index = 0; index < items.size(); index++) {
			Item item = items.get(index);
			if (item.flex() > 0) {
				flexTotal += item.flex();
				continue;
			}
			QuillSize size = item.node().measure(looseChild(horizontal, maxMain, maxCross, stretchCross));
			sizes[index] = size;
			fixedMain = QuillLayoutMath.saturatedAdd(fixedMain, mainOf(size, horizontal));
		}

		if (flexTotal == 0 || availableMain == QuillBoxConstraints.UNBOUNDED) {
			for (int index = 0; index < items.size(); index++) {
				if (sizes[index] == null) {
					sizes[index] = items.get(index).node().measure(
							looseChild(horizontal, maxMain, maxCross, stretchCross)
					);
				}
			}
		}
		else {
			int free = Math.max(0, availableMain - fixedMain);
			int[] shares = new int[items.size()];
			int distributed = 0;
			for (int index = 0; index < items.size(); index++) {
				Item item = items.get(index);
				if (item.flex() > 0) {
					shares[index] = (int) ((long) free * item.flex() / flexTotal);
					distributed += shares[index];
				}
			}
			int remainder = free - distributed;
			for (int index = 0; index < items.size() && remainder > 0; index++) {
				if (items.get(index).flex() > 0) {
					shares[index]++;
					remainder--;
				}
			}
			for (int index = 0; index < items.size(); index++) {
				if (shares[index] > 0 || items.get(index).flex() > 0) {
					sizes[index] = items.get(index).node().measure(
							tightChild(horizontal, shares[index], maxCross, stretchCross)
					);
				}
			}
		}

		int main = 0;
		int cross = 0;
		for (int index = 0; index < sizes.length; index++) {
			QuillSize size = sizes[index];
			main = QuillLayoutMath.saturatedAdd(main, mainOf(size, horizontal));
			cross = Math.max(cross, crossOf(size, horizontal));
		}
		main = QuillLayoutMath.saturatedAdd(main, (int) totalGap);
		if (availableMain != QuillBoxConstraints.UNBOUNDED && flexTotal > 0) {
			main = availableMain + (int) totalGap;
		}
		if (stretchCross) {
			cross = maxCross;
		}

		childSizes = List.of(sizes);
		QuillSize own = horizontal ? QuillSize.of(main, cross) : QuillSize.of(cross, main);
		return constraints.constrain(own);
	}

	@Override
	public void arrange(QuillRect bounds) {
		setBounds(bounds);
		boolean horizontal = direction == Direction.HORIZONTAL;
		int cursor = horizontal ? bounds.x() : bounds.y();
		int crossStart = horizontal ? bounds.y() : bounds.x();
		int crossSize = horizontal ? bounds.height() : bounds.width();
		for (int index = 0; index < items.size(); index++) {
			QuillSize size = childSizes.get(index);
			int childCross = stretchCross
					? crossSize
					: crossOf(size, horizontal);
			int crossOffset = crossAxisAlignment.offset(crossSize, childCross);
			QuillRect childBounds = horizontal
					? new QuillRect(
							cursor,
							crossStart + crossOffset,
							size.width(),
							childCross
					)
					: new QuillRect(
							crossStart + crossOffset,
							cursor,
							childCross,
							size.height()
					);
			items.get(index).node().arrange(childBounds);
			cursor = QuillLayoutMath.saturatedAdd(
					cursor,
					QuillLayoutMath.saturatedAdd(mainOf(size, horizontal), gap)
			);
		}
	}

	@Override
	public List<QuillLayoutNode> children() {
		return items.stream().map(Item::node).toList();
	}

	public List<QuillSize> childSizes() {
		return childSizes;
	}

	private static QuillBoxConstraints looseChild(
			boolean horizontal, int maxMain, int maxCross, boolean stretch
	) {
		if (horizontal) {
			return stretch
					? new QuillBoxConstraints(0, maxCross, maxMain, maxCross)
					: QuillBoxConstraints.loose(maxMain, maxCross);
		}
		return stretch
				? new QuillBoxConstraints(maxCross, 0, maxCross, maxMain)
				: QuillBoxConstraints.loose(maxCross, maxMain);
	}

	private static QuillBoxConstraints tightChild(
			boolean horizontal, int main, int maxCross, boolean stretch
	) {
		if (horizontal) {
			return new QuillBoxConstraints(
					main,
					stretch ? maxCross : 0,
					main,
					maxCross
			);
		}
		return new QuillBoxConstraints(
				stretch ? maxCross : 0,
				main,
				maxCross,
				main
		);
	}

	private static int mainOf(QuillSize size, boolean horizontal) {
		return horizontal ? size.width() : size.height();
	}

	private static int crossOf(QuillSize size, boolean horizontal) {
		return horizontal ? size.height() : size.width();
	}
}
