package com.quillui.layout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class QuillLayoutTest {
	private static final QuillRect BOUNDS = new QuillRect(10, 20, 300, 100);

	@Test
	void flexRowDistributesFreeSpaceByWeight() {
		List<QuillRect> rects = QuillFlex.row(BOUNDS, 10)
				.add(80)
				.addFlexible(1)
				.addFlexible(2)
				.layout();

		assertEquals(3, rects.size());
		assertEquals(80, rects.get(0).width());
		assertEquals(67, rects.get(1).width());
		assertEquals(133, rects.get(2).width());
		assertEquals(10, rects.get(0).x());
		assertEquals(100, rects.get(1).x());
		assertEquals(177, rects.get(2).x());
		assertEquals(310, rects.get(2).right());
	}

	@Test
	void flexColumnClampsMainAxisSize() {
		List<QuillRect> rects = QuillFlex.column(BOUNDS, 0)
				.add(80, 0, QuillConstraints.of(0, 0, Integer.MAX_VALUE, 30))
				.layout();

		assertEquals(1, rects.size());
		assertEquals(300, rects.get(0).width());
		assertEquals(30, rects.get(0).height());
		assertEquals(20, rects.get(0).y());
	}

	@Test
	void flexShrinksFixedItemsWhenTheyOverflow() {
		List<QuillRect> rects = QuillFlex.row(new QuillRect(0, 0, 100, 20), 0)
				.add(80)
				.add(80)
				.layout();

		assertEquals(50, rects.get(0).width());
		assertEquals(50, rects.get(1).width());
	}

	@Test
	void wrapBreaksIntoRows() {
		QuillWrap wrap = new QuillWrap(BOUNDS, 90, 30, 10);
		assertEquals(3, wrap.columns());
		assertEquals(2, wrap.rows(5));
		assertEquals(70, wrap.contentHeight(5));
		assertEquals(10, wrap.cell(0).x());
		assertEquals(110, wrap.cell(1).x());
		assertEquals(210, wrap.cell(2).x());
		assertEquals(60, wrap.cell(3).y());
	}

	@Test
	void stackAlignsInsideBounds() {
		QuillStack stack = new QuillStack(BOUNDS);
		QuillRect centered = stack.center(100, 50);
		assertEquals(110, centered.x());
		assertEquals(45, centered.y());
		assertEquals(310, stack.place(100, 50, QuillAlign.END, QuillAlign.END).right());
		assertEquals(120, stack.place(100, 50, QuillAlign.END, QuillAlign.END).bottom());
	}

	@Test
	void breakpointsAndColumnsFollowWidth() {
		assertEquals(QuillBreakpoints.Breakpoint.COMPACT, QuillBreakpoints.of(479));
		assertEquals(QuillBreakpoints.Breakpoint.MEDIUM, QuillBreakpoints.of(480));
		assertEquals(QuillBreakpoints.Breakpoint.EXPANDED, QuillBreakpoints.of(840));
		assertEquals(1, QuillBreakpoints.columns(300, 200, 4, 12));
		assertEquals(2, QuillBreakpoints.columns(412, 200, 4, 12));
		assertEquals(4, QuillBreakpoints.columns(2000, 200, 4, 12));
	}

	@Test
	void rectIntersectionAndVisibility() {
		QuillRect viewport = new QuillRect(0, 0, 100, 50);
		QuillRect visible = new QuillRect(20, 10, 30, 30);
		QuillRect hidden = new QuillRect(0, 80, 30, 30);
		assertTrue(visible.intersects(viewport));
		assertFalse(hidden.intersects(viewport));
		assertEquals(new QuillRect(20, 10, 30, 30), visible.intersection(viewport));
		assertEquals(0, hidden.intersection(viewport).height());
	}

	@Test
	void constraintsClampRect() {
		QuillConstraints constraints = QuillConstraints.of(40, 20, 100, 80);
		assertEquals(new QuillRect(5, 5, 100, 80), constraints.clamp(new QuillRect(5, 5, 200, 200)));
		assertEquals(new QuillRect(5, 5, 40, 20), constraints.clamp(new QuillRect(5, 5, 10, 10)));
	}
}
