package com.quillui.theme;

/**
 * Sizing metrics owned by a theme. Widgets created through the concise factories read these values,
 * so a theme can tighten or widen a whole screen without magic numbers.
 */
public record QuillThemeMetrics(
		int controlHeight,
		int fieldWidth,
		int sliderWidth,
		int buttonWidth,
		int rowGap,
		int contentInset,
		int cardPadding
) {
	public static final QuillThemeMetrics DEFAULT =
			new QuillThemeMetrics(20, 220, 320, 120, 8, 24, 8);

	public QuillThemeMetrics {
		if (controlHeight <= 0) {
			throw new IllegalArgumentException("controlHeight must be > 0");
		}
		if (fieldWidth <= 0 || sliderWidth <= 0 || buttonWidth <= 0) {
			throw new IllegalArgumentException("widths must be > 0");
		}
		if (rowGap < 0 || contentInset < 0 || cardPadding < 0) {
			throw new IllegalArgumentException("spacing must be >= 0");
		}
	}
}
