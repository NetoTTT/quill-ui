package com.quillui;

/**
 * Design tokens for Quill UI. Consumers should prefer these tokens over literal colours or
 * spacing values; changing a token then updates every screen that uses it.
 */
public class QuillTheme {
	/* Spacing aliases retained for source compatibility; prefer QuillSpacing. */
	public static final int SPACE_1 = QuillSpacing.SPACE_1;
	public static final int SPACE_2 = QuillSpacing.SPACE_2;
	public static final int SPACE_3 = QuillSpacing.SPACE_3;
	public static final int SPACE_4 = QuillSpacing.SPACE_4;
	public static final int SPACE_5 = QuillSpacing.SPACE_5;
	public static final int SPACE_6 = QuillSpacing.SPACE_6;

	/* Panel and header. */
	public static final int PANEL_BG = 0xF00E1217;
	public static final int PANEL_BORDER = 0xFF2B3440;
	public static final int PANEL_SHADOW = 0x66000000;
	public static final int HEADER_BG = 0xE60A0E14;
	public static final int DIVIDER = 0xFF242C36;

	/* Type. */
	public static final int TITLE = 0xFFF2F5F8;
	public static final int TEXT = 0xFFC6D0DA;
	public static final int TEXT_SOFT = 0xFFA9B5C1;
	public static final int MUTED = 0xFF7F8B99;
	public static final int MUTED_DARK = 0xFF5F6B78;

	/* Brand/accent. */
	public static final int ACCENT = 0xFFE0A93B;
	public static final int ACCENT_HOVER = 0xFFF3C55E;
	public static final int PRIMARY_BG = 0xFF241C11;
	public static final int PRIMARY_BG_HOVER = 0xFF2F2517;

	/* Semantic. */
	public static final int DANGER = 0xFFE06A6A;
	public static final int DANGER_BG = 0xFF352024;
	public static final int SUCCESS = 0xFF65D39A;
	public static final int INFO = 0xFF7FB4F5;

	/* Interactive surfaces. */
	public static final int BUTTON_BG = 0xFF1A212A;
	public static final int BUTTON_HOVER = 0xFF232D38;
	public static final int BUTTON_BORDER = 0xFF354150;
	public static final int BUTTON_DISABLED = 0xFF141920;
	public static final int BUTTON_DISABLED_BORDER = 0xFF232A33;
	public static final int BUTTON_DISABLED_TEXT = 0xFF626D7A;
	public static final int SELECTED_BG = 0xFF2A3140;

	/* Content surfaces. */
	public static final int SURFACE = 0x99151C25;
	public static final int SURFACE_ALT = 0x661A222C;
	public static final int SURFACE_BORDER = 0xFF26313D;
	public static final int SURFACE_HEADER = 0xFF10161E;
	public static final int CHIP_BG = 0xFF1B242E;
	public static final int CHIP_BORDER = 0xFF33404E;

	protected QuillTheme() {
	}
}
