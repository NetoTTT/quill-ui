package com.quillui.layout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class QuillBoxLayoutTest {
	private static final QuillBoxConstraints LOOSE_300 = QuillBoxConstraints.loose(300, 100);

	@Test
	void constraintsConstrainAndDeflate() {
		assertEquals(QuillSize.of(100, 50), QuillBoxConstraints.tightFor(100, 50)
				.constrain(QuillSize.ZERO));
		assertEquals(QuillSize.of(100, 50), QuillBoxConstraints.loose(100, 50)
				.constrain(QuillSize.of(200, 200)));
		assertEquals(
				QuillBoxConstraints.tightFor(80, 30),
				QuillBoxConstraints.tightFor(100, 50).deflate(QuillInsets.all(10))
		);
		assertTrue(QuillBoxConstraints.tightFor(100, 50).isTight());
		assertFalse(QuillBoxConstraints.loose(100, 50).isTight());
		assertFalse(QuillBoxConstraints.unbounded().hasBoundedWidth());
	}

	@Test
	void constraintsEnforceParentBounds() {
		QuillBoxConstraints result = QuillBoxConstraints.loose(300, 300)
				.enforce(QuillBoxConstraints.tightFor(120, 80));
		assertEquals(QuillBoxConstraints.tightFor(120, 80), result);
	}

	@Test
	void sizedBoxUsesFixedSizeAndArrangesChild() {
		Leaf child = new Leaf(200, 200);
		QuillSizedBox box = new QuillSizedBox(100, 40, child);
		assertEquals(QuillSize.of(100, 40), box.measure(LOOSE_300));
		box.arrange(new QuillRect(10, 20, 100, 40));
		assertEquals(new QuillRect(10, 20, 100, 40), child.bounds());
	}

	@Test
	void sizedBoxWithoutFixedSizeFollowsChild() {
		QuillSizedBox box = new QuillSizedBox(null, null, new Leaf(80, 20));
		assertEquals(QuillSize.of(80, 20), box.measure(LOOSE_300));
	}

	@Test
	void paddingDeflatesChildAndAddsInsets() {
		Leaf child = new Leaf(80, 20);
		QuillPaddingBox box = new QuillPaddingBox(QuillInsets.all(10), child);
		assertEquals(QuillSize.of(100, 40), box.measure(LOOSE_300));
		box.arrange(new QuillRect(0, 0, 100, 40));
		assertEquals(new QuillRect(10, 10, 80, 20), child.bounds());
	}

	@Test
	void constrainedBoxAppliesAdditionalBounds() {
		Leaf child = new Leaf(10, 10);
		QuillConstrainedBox box = new QuillConstrainedBox(
				QuillBoxConstraints.tightFor(50, 50),
				child
		);
		assertEquals(QuillSize.of(50, 50), box.measure(LOOSE_300));
		box.arrange(new QuillRect(0, 0, 50, 50));
		assertEquals(new QuillRect(0, 0, 50, 50), child.bounds());
	}

	@Test
	void flexRowSharesRemainingSpaceByWeight() {
		Leaf fixed = new Leaf(50, 20);
		Leaf first = new Leaf(0, 0);
		Leaf second = new Leaf(0, 0);
		QuillFlexBox flex = QuillFlexBox.row(10)
				.add(fixed)
				.addFlexible(first, 1)
				.addFlexible(second, 2);
		assertEquals(QuillSize.of(300, 20), flex.measure(LOOSE_300));
		flex.arrange(new QuillRect(0, 0, 300, 20));
		assertEquals(new QuillRect(0, 0, 50, 20), fixed.bounds());
		assertEquals(77, first.bounds().width());
		assertEquals(153, second.bounds().width());
		assertEquals(60, first.bounds().x());
		assertEquals(147, second.bounds().x());
	}

	@Test
	void flexColumnUsesHeightAsMainAxis() {
		Leaf fixed = new Leaf(50, 20);
		Leaf flexible = new Leaf(0, 0);
		QuillFlexBox flex = QuillFlexBox.column(8)
				.add(fixed)
				.addFlexible(flexible, 1);
		assertEquals(QuillSize.of(50, 100), flex.measure(QuillBoxConstraints.loose(200, 100)));
		flex.arrange(new QuillRect(0, 0, 50, 100));
		assertEquals(new QuillRect(0, 0, 50, 20), fixed.bounds());
		assertEquals(new QuillRect(0, 28, 0, 72), flexible.bounds());
	}

	@Test
	void wrapBreaksRowsAtAvailableWidth() {
		Leaf first = new Leaf(100, 20);
		Leaf second = new Leaf(100, 20);
		Leaf third = new Leaf(100, 20);
		QuillWrapBox wrap = new QuillWrapBox(10).add(first).add(second).add(third);
		assertEquals(QuillSize.of(210, 50), wrap.measure(QuillBoxConstraints.loose(220, 100)));
		wrap.arrange(new QuillRect(0, 0, 220, 50));
		assertEquals(new QuillRect(0, 0, 100, 20), first.bounds());
		assertEquals(new QuillRect(110, 0, 100, 20), second.bounds());
		assertEquals(new QuillRect(0, 30, 100, 20), third.bounds());
	}

	@Test
	void wrapMeasureUsesOnlyContentWidthOnLaterRows() {
		QuillWrapBox wrap = new QuillWrapBox(10)
				.add(new Leaf(100, 20))
				.add(new Leaf(100, 20))
				.add(new Leaf(100, 20))
				.add(new Leaf(100, 20));
		assertEquals(QuillSize.of(210, 50), wrap.measure(QuillBoxConstraints.loose(220, 100)));
	}

	@Test
	void stackAlignsChildrenInParentBounds() {
		Leaf center = new Leaf(40, 10);
		Leaf end = new Leaf(20, 10);
		QuillStackBox stack = new QuillStackBox()
				.add(center, QuillAlign.CENTER, QuillAlign.CENTER)
				.add(end, QuillAlign.END, QuillAlign.END);
		assertEquals(QuillSize.of(40, 10), stack.measure(QuillBoxConstraints.loose(200, 100)));
		stack.arrange(new QuillRect(0, 0, 200, 100));
		assertEquals(new QuillRect(80, 45, 40, 10), center.bounds());
		assertEquals(new QuillRect(180, 90, 20, 10), end.bounds());
	}

	@Test
	void nestedTreeKeepsChildrenInsideParent() {
		Leaf cardA = new Leaf(100, 30);
		Leaf cardB = new Leaf(100, 30);
		QuillWrapBox cards = new QuillWrapBox(10).add(cardA).add(cardB);
		QuillPaddingBox padded = new QuillPaddingBox(QuillInsets.all(8), cards);
		QuillFlexBox row = QuillFlexBox.row(12)
				.add(new QuillSizedBox(40, 40))
				.addFlexible(padded, 1);

		QuillRect root = new QuillRect(0, 0, 320, 120);
		row.measure(QuillBoxConstraints.tight(QuillSize.of(320, 120)));
		row.arrange(root);

		assertEquals(new QuillRect(60, 8, 100, 30), cardA.bounds());
		assertEquals(new QuillRect(170, 8, 100, 30), cardB.bounds());
		assertTrue(row.bounds().contains(
				cardA.bounds().x() + cardA.bounds().width() - 1,
				cardA.bounds().y() + cardA.bounds().height() - 1
		));
	}

	private static final class Leaf extends QuillNode {
		private final QuillSize size;

		private Leaf(int width, int height) {
			this.size = QuillSize.of(width, height);
		}

		@Override
		public QuillSize measure(QuillBoxConstraints constraints) {
			return constraints.constrain(size);
		}

		@Override
		public void arrange(QuillRect bounds) {
			setBounds(bounds);
		}
	}
}
