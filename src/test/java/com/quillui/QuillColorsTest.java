package com.quillui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class QuillColorsTest {
	@Test
	void parsesRgbHexWithFullAlpha() {
		assertEquals(0xFFE0A93B, QuillColors.fromHex("#E0A93B"));
		assertEquals(0xFFE0A93B, QuillColors.fromHex("E0A93B"));
		assertEquals(0xFFEEAA33, QuillColors.fromHex("#ea3"));
	}

	@Test
	void parsesArgbHex() {
		assertEquals(0x80E0A93B, QuillColors.fromHex("#80E0A93B"));
		assertEquals(0x80E0A93B, QuillColors.fromHex("80E0A93B"));
	}

	@Test
	void buildsFromComponents() {
		assertEquals(0xFFE0A93B, QuillColors.rgb(224, 169, 59));
		assertEquals(0x80E0A93B, QuillColors.argb(128, 224, 169, 59));
	}

	@Test
	void readsAndReplacesChannels() {
		int color = QuillColors.fromHex("#80E0A93B");
		assertEquals(128, QuillColors.alpha(color));
		assertEquals(224, QuillColors.red(color));
		assertEquals(169, QuillColors.green(color));
		assertEquals(59, QuillColors.blue(color));
		assertEquals(0x40E0A93B, QuillColors.withAlpha(color, 64));
		assertEquals(0x80E0A93B, QuillColors.withAlpha(0xFFE0A93B, 0.5F));
	}

	@Test
	void formatsHex() {
		assertEquals("#80E0A93B", QuillColors.toHex(0x80E0A93B));
		assertEquals("#E0A93B", QuillColors.toRgbHex(0xFFE0A93B));
	}

	@Test
	void rejectsInvalidValues() {
		assertThrows(IllegalArgumentException.class, () -> QuillColors.rgb(256, 0, 0));
		assertThrows(IllegalArgumentException.class, () -> QuillColors.fromHex("#12"));
		assertThrows(IllegalArgumentException.class, () -> QuillColors.fromHex(null));
	}
}
