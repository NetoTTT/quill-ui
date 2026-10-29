package com.quillui.motion;

import com.quillui.theme.QuillColors;

/**
 * Small helpers for the common fade, slide and scale transitions. Pass a value already eased by a
 * {@link QuillAnimation} to avoid duplicating curve math in every screen.
 */
public final class QuillTransition {
	private QuillTransition() {
	}

	public static float opacity(float progress) {
		return clamp(progress);
	}

	public static int withOpacity(int color, float progress) {
		return QuillColors.withAlpha(color, opacity(progress));
	}

	public static int slideX(float progress, int distance, boolean fromRight) {
		int offset = Math.round(distance * (1.0F - clamp(progress)));
		return fromRight ? offset : -offset;
	}

	public static int slideY(float progress, int distance, boolean fromBottom) {
		int offset = Math.round(distance * (1.0F - clamp(progress)));
		return fromBottom ? offset : -offset;
	}

	public static float scale(float progress, float fromScale, float toScale) {
		float value = clamp(progress);
		return fromScale + (toScale - fromScale) * value;
	}

	public static float fadeIn(float progress) {
		return clamp(progress);
	}

	public static float fadeOut(float progress) {
		return 1.0F - clamp(progress);
	}

	private static float clamp(float value) {
		return Math.max(0.0F, Math.min(1.0F, value));
	}
}
