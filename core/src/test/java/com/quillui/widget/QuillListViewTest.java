package com.quillui.widget;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.quillui.layout.QuillInsets;
import com.quillui.layout.QuillSizedBox;
import java.util.List;
import org.junit.jupiter.api.Test;

class QuillListViewTest {
	@Test
	void selectionFollowsTheItem() {
		QuillListView<String> list = list();
		list.replaceItems(List.of("a", "b"));

		list.setSelected("b");

		assertTrue(list.isSelected("b"));
		assertFalse(list.isSelected("a"));
	}

	@Test
	void replacingItemsDropsAStaleSelection() {
		QuillListView<String> list = list();
		list.replaceItems(List.of("a", "b"));
		list.setSelected("b");

		list.replaceItems(List.of("a"));

		assertNull(list.selectedItem());
	}

	@Test
	void scrollOffsetIsClampedToTheContent() {
		QuillListView<String> list = list();
		list.replaceItems(List.of("a", "b", "c", "d", "e"));

		list.setScrollOffset(40);
		assertEquals(40, list.scrollOffset());

		list.setScrollOffset(10_000);
		assertEquals(40, list.scrollOffset());
	}

	@Test
	void rowWidthExcludesTheDefaultPaddingAndScrollbar() {
		QuillListView<String> list = new QuillListView<>(
				0, 0, 100, 100, 20,
				(item, index) -> new QuillSizedBox(100, 20),
				(item, index) -> {
				}
		);
		list.replaceItems(List.of("a"));

		assertEquals(80, list.rowWidth());
	}

	@Test
	void paddingCanBeCleared() {
		QuillListView<String> list = list();
		list.replaceItems(List.of("a", "b", "c", "d", "e"));
		list.padding(QuillInsets.all(0));

		assertEquals(92, list.rowWidth());
	}

	private static QuillListView<String> list() {
		return new QuillListView<>(
				0, 0, 100, 60, 20,
				(item, index) -> new QuillSizedBox(100, 20),
				(item, index) -> {
				}
		);
	}
}
