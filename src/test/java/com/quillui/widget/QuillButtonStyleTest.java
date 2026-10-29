package com.quillui.widget;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.quillui.theme.QuillThemeData;
import com.quillui.theme.QuillThemeData.ColorRole;
import org.junit.jupiter.api.Test;

class QuillButtonStyleTest {
	@Test
	void primaryResolvesFromThemeRoles() {
		QuillThemeData theme = QuillThemeData.DEFAULT;
		QuillButtonStyle style = QuillButtonStyle.primary(theme);

		assertEquals(theme.color(ColorRole.PRIMARY_BG), style.background());
		assertEquals(theme.color(ColorRole.PRIMARY_BG_HOVER), style.backgroundHover());
		assertEquals(theme.color(ColorRole.ACCENT), style.border());
		assertEquals(theme.color(ColorRole.ON_ACCENT), style.text());
		assertEquals(theme.color(ColorRole.FOCUS_RING), style.focusRing());
	}

	@Test
	void hoverInterpolationKeepsBothEndpointsExact() {
		QuillButtonStyle style = QuillButtonStyle.ghost(QuillThemeData.DEFAULT);

		assertEquals(style.background(), style.backgroundAt(0.0F));
		assertEquals(style.backgroundHover(), style.backgroundAt(1.0F));
		assertEquals(style.border(), style.borderAt(0.0F));
		assertEquals(style.borderHover(), style.borderAt(1.0F));
		assertEquals(style.text(), style.textAt(0.0F));
		assertEquals(style.textHover(), style.textAt(1.0F));
	}

	@Test
	void disabledReusesTheSameColoursInEveryState() {
		QuillButtonStyle style = QuillButtonStyle.disabled(QuillThemeData.DEFAULT);

		assertEquals(style.background(), style.backgroundHover());
		assertEquals(style.border(), style.borderHover());
		assertEquals(style.text(), style.textHover());
	}

	@Test
	void selectedUsesTheSelectedSurface() {
		QuillThemeData theme = QuillThemeData.DEFAULT;
		QuillButtonStyle style = QuillButtonStyle.selected(theme);

		assertEquals(theme.color(ColorRole.SELECTED_BG), style.background());
		assertEquals(theme.color(ColorRole.ACCENT), style.border());
		assertEquals(theme.color(ColorRole.TITLE), style.text());
	}

	@Test
	void customStylesAreAcceptedDirectly() {
		QuillButtonStyle custom = new QuillButtonStyle(
				0xFF101010,
				0xFF202020,
				0xFF303030,
				0xFF404040,
				0xFFAABBCC,
				0xFFDDEEFF,
				0xFFFFFFFF
		);

		assertEquals(0xFF101010, custom.background());
		assertEquals(0xFFDDEEFF, custom.textAt(1.0F));
	}
}
