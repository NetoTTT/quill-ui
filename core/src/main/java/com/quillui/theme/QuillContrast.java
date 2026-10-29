package com.quillui.theme;

/**
 * WCAG-style contrast helpers for opaque UI colours.
 *
 * <p>Use {@link #textRatio(int, int)} for normal text and {@link #uiRatio(int, int)} for borders,
 * focus rings and large UI elements. Alpha channels are ignored.</p>
 */
public final class QuillContrast {
	private QuillContrast() {
	}

	public static double textRatio(int foreground, int background) {
		return ratio(foreground, background);
	}

	public static double uiRatio(int foreground, int background) {
		return ratio(foreground, background);
	}

	public static boolean meetsText(int foreground, int background) {
		return textRatio(foreground, background) >= 4.5D;
	}

	public static boolean meetsUi(int foreground, int background) {
		return uiRatio(foreground, background) >= 3.0D;
	}

	public static double ratio(int foreground, int background) {
		double light = relativeLuminance(foreground);
		double dark = relativeLuminance(background);
		double brighter = Math.max(light, dark);
		double darker = Math.min(light, dark);
		return (brighter + 0.05D) / (darker + 0.05D);
	}

	public static double relativeLuminance(int color) {
		return 0.2126D * linear(red(color))
				+ 0.7152D * linear(green(color))
				+ 0.0722D * linear(blue(color));
	}

	public static int readableText(int background) {
		return ratio(0xFF111827, background) >= ratio(0xFFFFFFFF, background)
				? 0xFF111827
				: 0xFFFFFFFF;
	}

	public static String describe(int foreground, int background) {
		return String.format("%.2f:1", ratio(foreground, background));
	}

	private static double linear(int channel) {
		double value = channel / 255.0D;
		return value <= 0.03928D
				? value / 12.92D
				: Math.pow((value + 0.055D) / 1.055D, 2.4D);
	}

	private static int red(int color) {
		return (color >>> 16) & 0xFF;
	}

	private static int green(int color) {
		return (color >>> 8) & 0xFF;
	}

	private static int blue(int color) {
		return color & 0xFF;
	}
}
