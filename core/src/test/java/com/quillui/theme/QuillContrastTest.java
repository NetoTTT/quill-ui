package com.quillui.theme;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class QuillContrastTest {
	@Test
	void blackOnWhiteIsMaximumContrast() {
		assertEquals(21.0D, QuillContrast.ratio(
				QuillColors.rgb(0, 0, 0),
				QuillColors.rgb(255, 255, 255)
		), 0.01D);
	}

	@Test
	void sameColourHasNoContrast() {
		int colour = QuillColors.rgb(51, 102, 153);
		assertEquals(1.0D, QuillContrast.ratio(colour, colour), 0.001D);
	}

	@Test
	void classifiesTextAndUiThresholds() {
		int black = QuillColors.rgb(0, 0, 0);
		int white = QuillColors.rgb(255, 255, 255);
		assertTrue(QuillContrast.meetsText(black, white));
		assertFalse(QuillContrast.meetsText(QuillColors.rgb(170, 170, 170), white));
		assertTrue(QuillContrast.meetsUi(QuillColors.rgb(118, 118, 118), white));
	}
}
