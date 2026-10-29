package com.quillui.widget;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class QuillScrollListTest {
	@Test
	void selectionFollowsTheItemInsteadOfTheIndex() {
		QuillScrollList<String> list = list();
		list.replaceItems(List.of("a", "b"));

		list.setSelected("b");

		assertTrue(list.isSelected("b"));
		assertFalse(list.isSelected("a"));
	}

	@Test
	void replacingItemsDropsAStaleSelection() {
		QuillScrollList<String> list = list();
		list.replaceItems(List.of("a", "b"));
		list.setSelected("b");

		list.replaceItems(List.of("a"));

		assertNull(list.selectedItem());
	}

	@Test
	void replacingItemsKeepsAPresentSelection() {
		QuillScrollList<String> list = list();
		list.replaceItems(List.of("a", "b"));
		list.setSelected("a");

		list.replaceItems(List.of("a", "b"));

		assertTrue(list.isSelected("a"));
	}

	@Test
	void scrollOffsetIsClampedToTheContent() {
		QuillScrollList<String> list = list();
		list.replaceItems(List.of("a", "b", "c", "d", "e"));

		list.setScrollOffset(40);
		assertEquals(40, list.scrollOffset());

		list.setScrollOffset(-10);
		assertEquals(0, list.scrollOffset());

		list.setScrollOffset(10_000);
		assertEquals(40, list.scrollOffset());
	}

	@Test
	void buttonEnabledIsFluent() {
		QuillButton button = QuillButton.primary("Save", () -> {
		});

		assertTrue(button.active);
		assertEquals(button, button.enabled(false));
		assertFalse(button.active);
	}

	private static QuillScrollList<String> list() {
		return new QuillScrollList<>(
				0, 0, 100, 60, 20,
				(graphics, item, index, x, y, width, height, mouseX, mouseY, hovered) -> {
				},
				(item, index) -> {
				}
		);
	}
}
