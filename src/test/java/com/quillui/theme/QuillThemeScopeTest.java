package com.quillui.theme;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.quillui.theme.QuillThemeData.ColorRole;
import org.junit.jupiter.api.Test;

class QuillThemeScopeTest {
	@Test
	void scopesThemeDuringABuildAndRestoresThePreviousValue() {
		QuillThemeData custom = QuillThemeData.DEFAULT.with(
				ColorRole.ACCENT,
				0xFF123456
		);

		QuillThemeScope.run(custom, () ->
				assertEquals(custom.color(ColorRole.ACCENT),
						QuillThemeScope.current().color(ColorRole.ACCENT))
		);

		assertEquals(QuillThemeData.DEFAULT.color(ColorRole.ACCENT),
				QuillThemeScope.current().color(ColorRole.ACCENT));
	}
}
