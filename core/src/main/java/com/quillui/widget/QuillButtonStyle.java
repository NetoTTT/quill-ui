package com.quillui.widget;

import com.quillui.theme.QuillColors;
import com.quillui.theme.QuillThemeData;

/**
 * Resolved colours for one button state. Splitting them out of {@link QuillButton} lets a consumer
 * register a custom style without adding a new {@link QuillButton.Variant}.
 */
public record QuillButtonStyle(
		int background,
		int backgroundHover,
		int border,
		int borderHover,
		int text,
		int textHover,
		int focusRing
) {
	public static QuillButtonStyle primary(QuillThemeData theme) {
		QuillThemeData resolved = theme(theme);
		return new QuillButtonStyle(
				resolved.color(QuillThemeData.ColorRole.PRIMARY_BG),
				resolved.color(QuillThemeData.ColorRole.PRIMARY_BG_HOVER),
				resolved.color(QuillThemeData.ColorRole.ACCENT),
				resolved.color(QuillThemeData.ColorRole.ACCENT_HOVER),
				resolved.color(QuillThemeData.ColorRole.ON_ACCENT),
				resolved.color(QuillThemeData.ColorRole.ON_ACCENT),
				resolved.color(QuillThemeData.ColorRole.FOCUS_RING)
		);
	}

	public static QuillButtonStyle secondary(QuillThemeData theme) {
		QuillThemeData resolved = theme(theme);
		return new QuillButtonStyle(
				resolved.color(QuillThemeData.ColorRole.BUTTON_BG),
				resolved.color(QuillThemeData.ColorRole.BUTTON_HOVER),
				resolved.color(QuillThemeData.ColorRole.BUTTON_BORDER),
				resolved.color(QuillThemeData.ColorRole.BUTTON_BORDER),
				resolved.color(QuillThemeData.ColorRole.TEXT),
				resolved.color(QuillThemeData.ColorRole.TEXT),
				resolved.color(QuillThemeData.ColorRole.FOCUS_RING)
		);
	}

	public static QuillButtonStyle danger(QuillThemeData theme) {
		QuillThemeData resolved = theme(theme);
		return new QuillButtonStyle(
				resolved.color(QuillThemeData.ColorRole.BUTTON_BG),
				resolved.color(QuillThemeData.ColorRole.DANGER_BG),
				resolved.color(QuillThemeData.ColorRole.DANGER),
				resolved.color(QuillThemeData.ColorRole.DANGER),
				resolved.color(QuillThemeData.ColorRole.ON_DANGER),
				resolved.color(QuillThemeData.ColorRole.ON_DANGER),
				resolved.color(QuillThemeData.ColorRole.FOCUS_RING)
		);
	}

	public static QuillButtonStyle ghost(QuillThemeData theme) {
		QuillThemeData resolved = theme(theme);
		return new QuillButtonStyle(
				resolved.color(QuillThemeData.ColorRole.GHOST_BG),
				resolved.color(QuillThemeData.ColorRole.BUTTON_HOVER),
				resolved.color(QuillThemeData.ColorRole.GHOST_BORDER),
				resolved.color(QuillThemeData.ColorRole.GHOST_BORDER),
				resolved.color(QuillThemeData.ColorRole.GHOST_TEXT),
				resolved.color(QuillThemeData.ColorRole.TITLE),
				resolved.color(QuillThemeData.ColorRole.FOCUS_RING)
		);
	}

	public static QuillButtonStyle selected(QuillThemeData theme) {
		QuillThemeData resolved = theme(theme);
		return new QuillButtonStyle(
				resolved.color(QuillThemeData.ColorRole.SELECTED_BG),
				resolved.color(QuillThemeData.ColorRole.SELECTED_BG),
				resolved.color(QuillThemeData.ColorRole.ACCENT),
				resolved.color(QuillThemeData.ColorRole.ACCENT_HOVER),
				resolved.color(QuillThemeData.ColorRole.TITLE),
				resolved.color(QuillThemeData.ColorRole.TITLE),
				resolved.color(QuillThemeData.ColorRole.FOCUS_RING)
		);
	}

	public static QuillButtonStyle disabled(QuillThemeData theme) {
		QuillThemeData resolved = theme(theme);
		return new QuillButtonStyle(
				resolved.color(QuillThemeData.ColorRole.BUTTON_DISABLED),
				resolved.color(QuillThemeData.ColorRole.BUTTON_DISABLED),
				resolved.color(QuillThemeData.ColorRole.BUTTON_DISABLED_BORDER),
				resolved.color(QuillThemeData.ColorRole.BUTTON_DISABLED_BORDER),
				resolved.color(QuillThemeData.ColorRole.BUTTON_DISABLED_TEXT),
				resolved.color(QuillThemeData.ColorRole.BUTTON_DISABLED_TEXT),
				resolved.color(QuillThemeData.ColorRole.FOCUS_RING)
		);
	}

	public int backgroundAt(float hover) {
		return QuillColors.lerp(background, backgroundHover, hover);
	}

	public int borderAt(float hover) {
		return QuillColors.lerp(border, borderHover, hover);
	}

	public int textAt(float hover) {
		return QuillColors.lerp(text, textHover, hover);
	}

	private static QuillThemeData theme(QuillThemeData theme) {
		return theme == null ? QuillThemeData.DEFAULT : theme;
	}
}
