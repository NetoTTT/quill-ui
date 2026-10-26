package com.quillui.theme;

/**
 * Default-theme compatibility facade.
 *
 * <p>New code should use {@link QuillThemeData} and pass the instance to widgets/screens. The
 * static colour fields remain for existing consumers and resolve to {@link QuillThemeData#DEFAULT}.</p>
 */
public class QuillTheme {
	/* Spacing aliases retained for source compatibility; prefer QuillSpacing. */
	public static final int SPACE_1 = QuillSpacing.SPACE_1;
	public static final int SPACE_2 = QuillSpacing.SPACE_2;
	public static final int SPACE_3 = QuillSpacing.SPACE_3;
	public static final int SPACE_4 = QuillSpacing.SPACE_4;
	public static final int SPACE_5 = QuillSpacing.SPACE_5;
	public static final int SPACE_6 = QuillSpacing.SPACE_6;

	public static final int SCREEN_BG = color(QuillThemeData.ColorRole.SCREEN_BG);
	public static final int PANEL_BG = color(QuillThemeData.ColorRole.PANEL_BG);
	public static final int PANEL_BORDER = color(QuillThemeData.ColorRole.PANEL_BORDER);
	public static final int PANEL_SHADOW = color(QuillThemeData.ColorRole.PANEL_SHADOW);
	public static final int HEADER_BG = color(QuillThemeData.ColorRole.HEADER_BG);
	public static final int DIVIDER = color(QuillThemeData.ColorRole.DIVIDER);

	public static final int TITLE = color(QuillThemeData.ColorRole.TITLE);
	public static final int TEXT = color(QuillThemeData.ColorRole.TEXT);
	public static final int TEXT_SOFT = color(QuillThemeData.ColorRole.TEXT_SOFT);
	public static final int MUTED = color(QuillThemeData.ColorRole.MUTED);
	public static final int MUTED_DARK = color(QuillThemeData.ColorRole.MUTED_DARK);

	public static final int ACCENT = color(QuillThemeData.ColorRole.ACCENT);
	public static final int ACCENT_HOVER = color(QuillThemeData.ColorRole.ACCENT_HOVER);
	public static final int PRIMARY_BG = color(QuillThemeData.ColorRole.PRIMARY_BG);
	public static final int PRIMARY_BG_HOVER = color(QuillThemeData.ColorRole.PRIMARY_BG_HOVER);
	public static final int ON_ACCENT = color(QuillThemeData.ColorRole.ON_ACCENT);
	public static final int ON_DANGER = color(QuillThemeData.ColorRole.ON_DANGER);
	public static final int GHOST_BG = color(QuillThemeData.ColorRole.GHOST_BG);
	public static final int GHOST_BORDER = color(QuillThemeData.ColorRole.GHOST_BORDER);
	public static final int GHOST_TEXT = color(QuillThemeData.ColorRole.GHOST_TEXT);
	public static final int FOCUS_RING = color(QuillThemeData.ColorRole.FOCUS_RING);

	public static final int DANGER = color(QuillThemeData.ColorRole.DANGER);
	public static final int DANGER_BG = color(QuillThemeData.ColorRole.DANGER_BG);
	public static final int SUCCESS = color(QuillThemeData.ColorRole.SUCCESS);
	public static final int INFO = color(QuillThemeData.ColorRole.INFO);

	public static final int BUTTON_BG = color(QuillThemeData.ColorRole.BUTTON_BG);
	public static final int BUTTON_HOVER = color(QuillThemeData.ColorRole.BUTTON_HOVER);
	public static final int BUTTON_BORDER = color(QuillThemeData.ColorRole.BUTTON_BORDER);
	public static final int BUTTON_DISABLED = color(QuillThemeData.ColorRole.BUTTON_DISABLED);
	public static final int BUTTON_DISABLED_BORDER = color(QuillThemeData.ColorRole.BUTTON_DISABLED_BORDER);
	public static final int BUTTON_DISABLED_TEXT = color(QuillThemeData.ColorRole.BUTTON_DISABLED_TEXT);
	public static final int SELECTED_BG = color(QuillThemeData.ColorRole.SELECTED_BG);

	public static final int SURFACE = color(QuillThemeData.ColorRole.SURFACE);
	public static final int SURFACE_ALT = color(QuillThemeData.ColorRole.SURFACE_ALT);
	public static final int SURFACE_BORDER = color(QuillThemeData.ColorRole.SURFACE_BORDER);
	public static final int SURFACE_HEADER = color(QuillThemeData.ColorRole.SURFACE_HEADER);
	public static final int CHIP_BG = color(QuillThemeData.ColorRole.CHIP_BG);
	public static final int CHIP_BORDER = color(QuillThemeData.ColorRole.CHIP_BORDER);

	protected QuillTheme() {
	}

	private static int color(QuillThemeData.ColorRole role) {
		return QuillThemeData.DEFAULT.color(role);
	}
}
