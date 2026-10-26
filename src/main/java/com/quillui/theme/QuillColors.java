package com.quillui.theme;

/**
 * Colour helpers for Minecraft's packed ARGB integers.
 *
 * <p>A colour is {@code 0xAARRGGBB}. {@code AA} is opacity ({@code FF} = opaque), followed by
 * red, green and blue. Hex strings accept {@code #RRGGBB}, {@code RRGGBB}, {@code #AARRGGBB} and
 * {@code AARRGGBB}, with or without a leading {@code #} or {@code 0x}.</p>
 */
public final class QuillColors {
	private QuillColors() {
	}

	public static int rgb(int red, int green, int blue) {
		return argb(255, red, green, blue);
	}

	public static int argb(int alpha, int red, int green, int blue) {
		check(alpha, "alpha");
		check(red, "red");
		check(green, "green");
		check(blue, "blue");
		return (alpha << 24) | (red << 16) | (green << 8) | blue;
	}

	public static int fromHex(String value) {
		if (value == null) {
			throw new IllegalArgumentException("Colour must not be null");
		}

		String hex = value.trim();
		if (hex.startsWith("#")) {
			hex = hex.substring(1);
		} else if (hex.startsWith("0x") || hex.startsWith("0X")) {
			hex = hex.substring(2);
		}

		if (hex.length() == 3) {
			hex = "" + hex.charAt(0) + hex.charAt(0)
					+ hex.charAt(1) + hex.charAt(1)
					+ hex.charAt(2) + hex.charAt(2);
		}

		return switch (hex.length()) {
			case 6 -> 0xFF000000 | Integer.parseUnsignedInt(hex, 16);
			case 8 -> Integer.parseUnsignedInt(hex, 16);
			default -> throw new IllegalArgumentException(
					"Expected #RRGGBB or #AARRGGBB, got: " + value
			);
		};
	}

	public static int withAlpha(int color, int alpha) {
		return (clamp(alpha) << 24) | (color & 0xFFFFFF);
	}

	public static int withAlpha(int color, float alpha) {
		return withAlpha(color, Math.round(clamp(alpha) * 255.0F));
	}

	public static int alpha(int color) {
		return color >>> 24;
	}

	public static int red(int color) {
		return (color >>> 16) & 0xFF;
	}

	public static int green(int color) {
		return (color >>> 8) & 0xFF;
	}

	public static int blue(int color) {
		return color & 0xFF;
	}

	public static String toHex(int color) {
		return String.format("#%08X", color);
	}

	public static String toRgbHex(int color) {
		return String.format("#%06X", color & 0xFFFFFF);
	}

	private static void check(int component, String name) {
		if (component < 0 || component > 255) {
			throw new IllegalArgumentException(name + " must be between 0 and 255");
		}
	}

	private static int clamp(int value) {
		return Math.max(0, Math.min(255, value));
	}

	private static float clamp(float value) {
		return Math.max(0.0F, Math.min(1.0F, value));
	}
}
