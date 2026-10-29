package com.quillui.widget;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.quillui.text.QuillTextStyle;
import com.quillui.theme.QuillColors;
import com.quillui.theme.QuillThemeData;
import com.quillui.theme.QuillThemeScope;
import net.minecraft.network.chat.Component;
import org.junit.jupiter.api.Test;

class QuillLabelTest {
	@Test
	void fluentColoursReadTheAmbientTheme() {
		QuillThemeData custom = QuillThemeData.DEFAULT.with(
				QuillThemeData.ColorRole.MUTED,
				QuillColors.rgb(10, 20, 30)
		);

		QuillLabel label = QuillThemeScope.supply(
				custom,
				() -> QuillLabel.of(Component.literal("Status"), QuillTextStyle.DEFAULT).muted()
		);

		assertEquals(
				QuillColors.rgb(10, 20, 30),
				label.style().color()
		);
	}

	@Test
	void titleAndAccentOverrideTheBaseStyle() {
		QuillThemeData theme = QuillThemeData.DEFAULT;
		QuillLabel label = QuillLabel.of(Component.literal("Status"), QuillTextStyle.DEFAULT);

		label.title();
		assertEquals(theme.color(QuillThemeData.ColorRole.TITLE), label.style().color());

		label.accent();
		assertEquals(theme.color(QuillThemeData.ColorRole.ACCENT), label.style().color());
	}
}
