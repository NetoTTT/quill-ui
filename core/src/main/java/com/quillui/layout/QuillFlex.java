package com.quillui.layout;

import java.util.ArrayList;
import java.util.List;

/**
 * One-dimensional flex layout. Fixed items keep their requested size; flexible items share the
 * remaining space by weight. When fixed items overflow, all items with a positive basis shrink
 * proportionally so the layout stays inside its bounds.
 */
public final class QuillFlex {
	public enum Axis {
		ROW,
		COLUMN
	}

	private record Item(int basis, int weight, QuillConstraints constraints) {
	}

	private final QuillRect bounds;
	private final Axis axis;
	private final int gap;
	private final List<Item> items = new ArrayList<>();

	public QuillFlex(QuillRect bounds, Axis axis, int gap) {
		this.bounds = bounds == null ? new QuillRect(0, 0, 0, 0) : bounds;
		this.axis = axis == null ? Axis.ROW : axis;
		this.gap = Math.max(0, gap);
	}

	public static QuillFlex row(QuillRect bounds, int gap) {
		return new QuillFlex(bounds, Axis.ROW, gap);
	}

	public static QuillFlex column(QuillRect bounds, int gap) {
		return new QuillFlex(bounds, Axis.COLUMN, gap);
	}

	public QuillFlex add(int basis) {
		return add(basis, 0, QuillConstraints.none());
	}

	public QuillFlex addFlexible(int weight) {
		return add(0, weight, QuillConstraints.none());
	}

	public QuillFlex add(int basis, int weight) {
		return add(basis, weight, QuillConstraints.none());
	}

	public QuillFlex add(int basis, int weight, QuillConstraints constraints) {
		items.add(new Item(
				Math.max(0, basis),
				Math.max(0, weight),
				constraints == null ? QuillConstraints.none() : constraints
		));
		return this;
	}

	public int count() {
		return items.size();
	}

	public List<QuillRect> layout() {
		if (items.isEmpty()) {
			return List.of();
		}

		boolean row = axis == Axis.ROW;
		int mainStart = row ? bounds.x() : bounds.y();
		int mainSize = row ? bounds.width() : bounds.height();
		int crossSize = row ? bounds.height() : bounds.width();
		int totalGap = gap * (items.size() - 1);
		int available = Math.max(0, mainSize - totalGap);

		int basisTotal = 0;
		int weightTotal = 0;
		for (Item item : items) {
			basisTotal += item.basis();
			weightTotal += item.weight();
		}

		int[] sizes = new int[items.size()];
		if (basisTotal > available && basisTotal > 0) {
			for (int index = 0; index < items.size(); index++) {
				Item item = items.get(index);
				sizes[index] = (int) ((long) item.basis() * available / basisTotal);
			}
		} else {
			int free = Math.max(0, available - basisTotal);
			int distributed = 0;
			for (int index = 0; index < items.size(); index++) {
				Item item = items.get(index);
				int share = weightTotal == 0
						? 0
						: (int) ((long) free * item.weight() / weightTotal);
				sizes[index] = item.basis() + share;
				distributed += share;
			}
			int remainder = free - distributed;
			for (int index = 0; index < items.size() && remainder > 0; index++) {
				if (items.get(index).weight() > 0) {
					sizes[index]++;
					remainder--;
				}
			}
		}

		List<QuillRect> rects = new ArrayList<>(items.size());
		int cursor = mainStart;
		for (int index = 0; index < items.size(); index++) {
			Item item = items.get(index);
			int main = row
					? item.constraints().clampWidth(sizes[index])
					: item.constraints().clampHeight(sizes[index]);
			int cross = row
					? item.constraints().clampHeight(crossSize)
					: item.constraints().clampWidth(crossSize);
			QuillRect rect = row
					? new QuillRect(cursor, bounds.y(), main, cross)
					: new QuillRect(bounds.x(), cursor, cross, main);
			rects.add(rect);
			cursor += main + gap;
		}
		return List.copyOf(rects);
	}
}
