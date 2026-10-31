package com.quillui.widget;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.resources.Identifier;
import org.junit.jupiter.api.Test;

class QuillIconsTest {
	@Test
	void textureRegionUsesNormalisedCoordinates() {
		Identifier texture = Identifier.fromNamespaceAndPath("quillui", "textures/gui/icons.png");
		QuillIcon icon = QuillIcons.texture(texture, 4, 8, 16, 16, 64, 64);

		assertEquals(16, icon.width());
		assertEquals(16, icon.height());
	}

	@Test
	void textureRegionMustFitInsideTheTexture() {
		Identifier texture = Identifier.fromNamespaceAndPath("quillui", "textures/gui/icons.png");

		assertThrows(IllegalArgumentException.class,
				() -> QuillIcons.texture(texture, 60, 0, 16, 16, 64, 64));
		assertThrows(IllegalArgumentException.class,
				() -> QuillIcons.texture(texture, 0, 0, 0, 16, 64, 64));
		assertThrows(IllegalArgumentException.class,
				() -> QuillIcons.texture(null, 0, 0, 16, 16, 64, 64));
		assertTrue(QuillIcons.texture(texture, 0, 0, 10, 20, 64, 64).height() == 20);
	}
}
