package com.quillui.theme;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class QuillThemeDataTest {
	@Test
	void defaultThemeMatchesLegacyFacade() {
		assertEquals(
				QuillThemeData.DEFAULT.color(QuillThemeData.ColorRole.ACCENT),
				QuillTheme.ACCENT
		);
		assertEquals(
				QuillThemeData.DEFAULT.color(QuillThemeData.ColorRole.TEXT),
				QuillTheme.TEXT
		);
	}

	@Test
	void derivesAndBuildsCustomThemes() {
		QuillThemeData derived = QuillThemeData.DEFAULT.with(QuillThemeData.ColorRole.ACCENT, 0xFFFF0000);
		assertEquals(0xFFFF0000, derived.color(QuillThemeData.ColorRole.ACCENT));
		assertEquals(0xFFE0A93B, QuillThemeData.DEFAULT.color(QuillThemeData.ColorRole.ACCENT));

		QuillThemeData built = QuillThemeData.builder()
				.set(QuillThemeData.ColorRole.ACCENT, 0xFF00FF00)
				.build();
		assertEquals(0xFF00FF00, built.color(QuillThemeData.ColorRole.ACCENT));
		assertNotEquals(
				QuillThemeData.DEFAULT.color(QuillThemeData.ColorRole.ACCENT),
				built.color(QuillThemeData.ColorRole.ACCENT)
		);
	}
}
