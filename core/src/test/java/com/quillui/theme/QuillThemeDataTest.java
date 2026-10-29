package com.quillui.theme;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class QuillThemeDataTest {
	@Test
	void defaultThemeExposesTheDefaultAccent() {
		assertEquals(
				QuillColors.rgb(224, 169, 59),
				QuillThemeData.DEFAULT.color(QuillThemeData.ColorRole.ACCENT)
		);
	}

	@Test
	void derivesAndBuildsCustomThemes() {
		QuillThemeData derived = QuillThemeData.DEFAULT.with(
				QuillThemeData.ColorRole.ACCENT,
				QuillColors.rgb(255, 0, 0)
		);
		assertEquals(QuillColors.rgb(255, 0, 0), derived.color(QuillThemeData.ColorRole.ACCENT));
		assertEquals(
				QuillColors.rgb(224, 169, 59),
				QuillThemeData.DEFAULT.color(QuillThemeData.ColorRole.ACCENT)
		);

		QuillThemeData built = QuillThemeData.builder()
				.set(QuillThemeData.ColorRole.ACCENT, QuillColors.rgb(0, 255, 0))
				.build();
		assertEquals(QuillColors.rgb(0, 255, 0), built.color(QuillThemeData.ColorRole.ACCENT));
		assertNotEquals(
				QuillThemeData.DEFAULT.color(QuillThemeData.ColorRole.ACCENT),
				built.color(QuillThemeData.ColorRole.ACCENT)
		);
	}
}
