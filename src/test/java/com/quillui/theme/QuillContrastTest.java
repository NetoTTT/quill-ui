package com.quillui.theme;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class QuillContrastTest {
	@Test
	void blackOnWhiteIsMaximumContrast() {
		assertEquals(21.0D, QuillContrast.ratio(0xFF000000, 0xFFFFFFFF), 0.01D);
	}

	@Test
	void sameColourHasNoContrast() {
		assertEquals(1.0D, QuillContrast.ratio(0xFF336699, 0xFF336699), 0.001D);
	}

	@Test
	void classifiesTextAndUiThresholds() {
		assertTrue(QuillContrast.meetsText(0xFF000000, 0xFFFFFFFF));
		assertFalse(QuillContrast.meetsText(0xFFAAAAAA, 0xFFFFFFFF));
		assertTrue(QuillContrast.meetsUi(0xFF767676, 0xFFFFFFFF));
	}
}
