package com.quillui.screen;

/**
 * Relative measurements for the standard {@code QuillScreen} header and close button.
 *
 * <p>Override {@code QuillScreen.layout()} to move the title/subtitle or the close control without
 * reimplementing the whole screen.</p>
 */
public record QuillScreenLayout(
		int headerHeight,
		int titleInsetX,
		int titleInsetY,
		int subtitleInsetY,
		int closeInsetRight,
		int closeInsetTop,
		int closeSize
) {
	public static final QuillScreenLayout DEFAULT =
			new QuillScreenLayout(48, 22, 14, 29, 10, 8, 20);

	/** Header height when the screen has no subtitle line. */
	public int titleOnlyHeaderHeight() {
		return Math.max(titleInsetY + 16, headerHeight - 14);
	}
}
