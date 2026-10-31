package com.quillui.widget;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.quillui.theme.QuillColors;
import com.quillui.theme.QuillThemeData;
import com.quillui.theme.QuillThemeScope;
import net.minecraft.network.chat.Component;
import org.junit.jupiter.api.Test;

class QuillChipTest {
	@Test
	void fluentColoursReadTheAmbientTheme() {
		QuillThemeData custom = QuillThemeData.DEFAULT.with(
				QuillThemeData.ColorRole.MUTED,
				QuillColors.rgb(10, 20, 30)
		);

		QuillChip chip = QuillThemeScope.supply(
				custom,
				() -> QuillChip.of(Component.literal("120 coins")).muted()
		);

		assertEquals(QuillColors.rgb(10, 20, 30), chip.style().color());
		assertEquals("120 coins", chip.getMessage().getString());
	}
}
