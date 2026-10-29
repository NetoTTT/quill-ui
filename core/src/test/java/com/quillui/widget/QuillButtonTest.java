package com.quillui.widget;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
