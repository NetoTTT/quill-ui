package com.quillui.theme;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.quillui.theme.QuillThemeData.ColorRole;
import org.junit.jupiter.api.Test;

class QuillThemeScopeTest {
	@Test
	void scopesThemeDuringABuildAndRestoresThePreviousValue() {
		QuillThemeData custom = QuillThemeData.DEFAULT.with(
				ColorRole.ACCENT,
				QuillColors.argb(255, 18, 52, 86)
		);

		QuillThemeScope.run(custom, () ->
				assertEquals(custom.color(ColorRole.ACCENT),
						QuillThemeScope.current().color(ColorRole.ACCENT))
		);

		assertEquals(QuillThemeData.DEFAULT.color(ColorRole.ACCENT),
				QuillThemeScope.current().color(ColorRole.ACCENT));
	}

	@Test
	void supplyReturnsTheValueAndRestoresTheTheme() {
		QuillThemeData custom = QuillThemeData.DEFAULT.with(
				ColorRole.ACCENT,
				QuillColors.argb(255, 18, 52, 86)
		);

		QuillThemeData seen = QuillThemeScope.supply(custom, QuillThemeScope::current);

		assertEquals(custom.color(ColorRole.ACCENT), seen.color(ColorRole.ACCENT));
		assertEquals(QuillThemeData.DEFAULT.color(ColorRole.ACCENT),
				QuillThemeScope.current().color(ColorRole.ACCENT));
	}
}
