package com.quillui.widget;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.junit.jupiter.api.Test;

class QuillButtonTest {
	@Test
	void factoriesUseTheDefaultControlSize() {
		QuillButton button = QuillButton.primary("Save", () -> {
		});

		assertEquals(120, button.getWidth());
		assertEquals(20, button.getHeight());
		assertEquals("Save", button.getMessage().getString());
		assertEquals(QuillButton.Variant.PRIMARY, button.variant());
	}

	@Test
	void factoriesKeepTheirVariant() {
		assertEquals(QuillButton.Variant.SECONDARY,
				QuillButton.secondary("Cancel", () -> {
				}).variant());
		assertEquals(QuillButton.Variant.DANGER,
				QuillButton.danger("Delete", () -> {
				}).variant());
		assertEquals(QuillButton.Variant.GHOST,
				QuillButton.ghost("Back", () -> {
				}).variant());
	}

	@Test
	void iconIsFluentAndKeepsAnOptionalLabel() {
		QuillIcon icon = new QuillIcon() {
			@Override
			public int width() {
				return 16;
			}

			@Override
			public int height() {
				return 16;
			}

			@Override
			public void paint(GuiGraphicsExtractor graphics, int x, int y) {
			}
		};

		QuillButton button = QuillButton.ghost("", () -> {
		});
		assertSame(button, button.icon(icon));
		assertSame(icon, button.icon());
		assertSame(button, button.iconGap(6));
		assertEquals(6, button.iconGap());
		assertSame(button, button.iconGap(-3));
		assertEquals(0, button.iconGap());
	}
}
