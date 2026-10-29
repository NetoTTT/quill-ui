package com.quillui.widget;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.quillui.layout.QuillRect;
import com.quillui.theme.QuillThemeData;
import org.junit.jupiter.api.Test;

class QuillScrollBarTest {
	@Test
	void scrollbarOnlyAppearsWhenContentOverflows() {
		QuillRect viewport = new QuillRect(0, 0, 100, 80);
		QuillScrollBar overflowing = new QuillScrollBar(
				viewport, () -> 120, () -> 0, value -> {
		}, QuillThemeData.DEFAULT
		).visibleWhenNeeded();
		assertTrue(overflowing.visible);

		QuillScrollBar fitting = new QuillScrollBar(
				viewport, () -> 0, () -> 0, value -> {
		}, QuillThemeData.DEFAULT
		).visibleWhenNeeded();
		assertFalse(fitting.visible);
	}
}
