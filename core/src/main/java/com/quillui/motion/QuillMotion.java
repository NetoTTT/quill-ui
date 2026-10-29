package com.quillui.motion;

/**
 * Global motion preferences. Minecraft has no portable reduced-motion query, so Quill exposes an
 * explicit flag that hosts can bind to their own accessibility/config screen.
 */
public final class QuillMotion {
	private static volatile boolean reducedMotion;

	private QuillMotion() {
	}

	public static boolean reducedMotion() {
		return reducedMotion;
	}

	public static void setReducedMotion(boolean enabled) {
		reducedMotion = enabled;
	}

	public static float duration(float seconds) {
		return reducedMotion ? 0.0F : Math.max(0.0F, seconds);
	}

	public static float duration(float seconds, float reducedMotionDuration) {
		return reducedMotion ? Math.max(0.0F, reducedMotionDuration) : Math.max(0.0F, seconds);
	}
}
