package com.quillui.layout;

import java.util.ArrayList;
import java.util.List;

/** Overlays children inside a shared rectangle using horizontal/vertical alignment. */
public final class QuillStackBox extends QuillNode {
	private record Item(
			QuillLayoutNode node,
			QuillAlign horizontal,
			QuillAlign vertical
	) {
	}

	private final List<Item> items = new ArrayList<>();
	private List<QuillSize> childSizes = List.of();

	public QuillStackBox add(QuillLayoutNode node) {
		return add(node, QuillAlign.START, QuillAlign.START);
	}

	public QuillStackBox add(
			QuillLayoutNode node, QuillAlign horizontal, QuillAlign vertical
	) {
		items.add(new Item(
				node,
				horizontal == null ? QuillAlign.START : horizontal,
				vertical == null ? QuillAlign.START : vertical
		));
		return this;
	}

	@Override
	public QuillSize measure(QuillBoxConstraints constraints) {
		if (items.isEmpty()) {
			return constraints.constrain(QuillSize.ZERO);
		}
		QuillSize[] sizes = new QuillSize[items.size()];
		int width = 0;
		int height = 0;
		QuillBoxConstraints childConstraints = QuillBoxConstraints.loose(
				constraints.maxWidth(),
				constraints.maxHeight()
		);
		for (int index = 0; index < items.size(); index++) {
			sizes[index] = items.get(index).node().measure(childConstraints);
			width = Math.max(width, sizes[index].width());
			height = Math.max(height, sizes[index].height());
		}
		childSizes = List.of(sizes);
		return constraints.constrain(QuillSize.of(width, height));
	}

	@Override
	public void arrange(QuillRect bounds) {
		setBounds(bounds);
		for (int index = 0; index < items.size(); index++) {
			Item item = items.get(index);
			QuillSize size = childSizes.get(index);
			int x = bounds.x() + item.horizontal().offset(bounds.width(), size.width());
			int y = bounds.y() + item.vertical().offset(bounds.height(), size.height());
			item.node().arrange(new QuillRect(x, y, size.width(), size.height()));
		}
	}

	@Override
	public List<QuillLayoutNode> children() {
		return items.stream().map(Item::node).toList();
	}

	public List<QuillSize> childSizes() {
		return childSizes;
	}
}
