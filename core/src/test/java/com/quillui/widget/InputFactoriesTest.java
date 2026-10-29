package com.quillui.widget;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.quillui.theme.QuillThemeData;
import com.quillui.theme.QuillThemeMetrics;
import com.quillui.theme.QuillThemeScope;
import java.util.List;
import net.minecraft.network.chat.Component;
import org.junit.jupiter.api.Test;

class InputFactoriesTest {
	@Test
	void checkboxFactoryUsesTheDefaultControlSize() {
		QuillCheckbox checkbox = QuillCheckbox.of(
				Component.literal("Enabled"), true, value -> {
				});

		assertEquals(220, checkbox.getWidth());
		assertEquals(20, checkbox.getHeight());
		assertEquals("Enabled", checkbox.getMessage().getString());
	}

	@Test
	void radioFactoryUsesTheDefaultControlSize() {
		QuillRadio radio = QuillRadio.of(
				Component.literal("Mode A"), false, value -> {
				});

		assertEquals(220, radio.getWidth());
		assertEquals(20, radio.getHeight());
	}

	@Test
	void sliderFactoryUsesTheWideDefault() {
		QuillSlider slider = QuillSlider.of(
				Component.literal("Intensity"), 0.5F, value -> {
				});

		assertEquals(320, slider.getWidth());
		assertEquals(20, slider.getHeight());
	}

	@Test
	void dropdownFactoryUsesTheDefaultControlSize() {
		QuillDropdown dropdown = QuillDropdown.of(
				Component.literal("Choose..."),
				List.of(Component.literal("Low"), Component.literal("High")),
				0,
				index -> {
				}
		);

		assertEquals(220, dropdown.getWidth());
		assertEquals(20, dropdown.getHeight());
	}

	@Test
	void toggleFactoryUsesTheDefaultControlSize() {
		QuillToggle toggle = QuillToggle.of(
				Component.literal("Enabled"), true, value -> {
				});

		assertEquals(220, toggle.getWidth());
		assertEquals(20, toggle.getHeight());
	}

	@Test
	void factoriesFollowThemeMetrics() {
		QuillThemeMetrics compact = new QuillThemeMetrics(18, 180, 260, 100, 6, 16, 6);
		QuillThemeData theme = QuillThemeData.DEFAULT.withMetrics(compact);

		QuillThemeScope.run(theme, () -> {
			QuillCheckbox checkbox = QuillCheckbox.of(
					Component.literal("Enabled"), false, value -> {
					});
			QuillSlider slider = QuillSlider.of(
					Component.literal("Intensity"), 0.5F, value -> {
					});
			QuillButton button = QuillButton.primary("Save", () -> {
			});

			assertEquals(180, checkbox.getWidth());
			assertEquals(18, checkbox.getHeight());
			assertEquals(260, slider.getWidth());
			assertEquals(100, button.getWidth());
			assertEquals(18, button.getHeight());
		});
	}
}
