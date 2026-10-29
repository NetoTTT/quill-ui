package com.quillui.theme;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class QuillThemeMetricsTest {
	@Test
	void defaultsMatchTheBaselineControlSizes() {
		QuillThemeMetrics metrics = QuillThemeMetrics.DEFAULT;

		assertEquals(20, metrics.controlHeight());
		assertEquals(220, metrics.fieldWidth());
		assertEquals(320, metrics.sliderWidth());
		assertEquals(120, metrics.buttonWidth());
		assertEquals(8, metrics.rowGap());
		assertEquals(24, metrics.contentInset());
		assertEquals(8, metrics.cardPadding());
	}

	@Test
	void themesPreserveMetricsWhenColoursChange() {
		QuillThemeMetrics compact = new QuillThemeMetrics(18, 180, 260, 100, 6, 16, 6);
		QuillThemeData theme = QuillThemeData.DEFAULT.withMetrics(compact);

		assertEquals(compact, theme.metrics());
		assertEquals(
				compact,
				theme.with(QuillThemeData.ColorRole.ACCENT, QuillColors.rgb(1, 2, 3)).metrics()
		);
	}

	@Test
	void rejectsNonPositiveSizes() {
		assertThrows(IllegalArgumentException.class, () -> new QuillThemeMetrics(
				0, 220, 320, 120, 8, 24, 8
		));
		assertThrows(IllegalArgumentException.class, () -> new QuillThemeMetrics(
				20, 220, 320, 120, -1, 24, 8
		));
	}
}
