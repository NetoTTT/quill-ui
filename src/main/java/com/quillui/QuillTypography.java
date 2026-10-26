package com.quillui;

/**
 * Typographic measurements for Minecraft's bitmap font.
 *
 * <p>{@link #TEXT_HEIGHT} is the rendered glyph height; {@link #LINE_HEIGHT} includes the normal
 * one-pixel leading. Scale values are multipliers for future scaled-text helpers.</p>
 */
public final class QuillTypography {
	public static final int TEXT_HEIGHT = 8;
	public static final int LINE_HEIGHT = 9;
	public static final int SMALL_LINE_HEIGHT = 8;
	public static final float TITLE_SCALE = 1.0F;
	public static final float BODY_SCALE = 1.0F;
	public static final float SMALL_SCALE = 0.8F;

	private QuillTypography() {
	}

	public static int centeredTextY(int height) {
		return (height - TEXT_HEIGHT) / 2;
	}

	public static int scaledLineHeight(float scale) {
		return Math.max(1, Math.round(LINE_HEIGHT * scale));
	}
}
