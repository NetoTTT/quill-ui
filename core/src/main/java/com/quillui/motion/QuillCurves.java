package com.quillui.motion;

/**
 * Built-in easing curves. Inputs are clamped to the 0..1 range.
 */
public final class QuillCurves {
	public static final QuillCurve LINEAR = progress -> clamp(progress);

	public static final QuillCurve EASE_IN = progress -> {
		float t = clamp(progress);
		return t * t * t;
	};

	public static final QuillCurve EASE_OUT = progress -> {
		float t = clamp(progress);
		float inverted = 1.0F - t;
		return 1.0F - inverted * inverted * inverted;
	};

	public static final QuillCurve EASE_IN_OUT = progress -> {
		float t = clamp(progress);
		return t < 0.5F
				? 4.0F * t * t * t
				: 1.0F - (float) Math.pow(-2.0F * t + 2.0F, 3.0D) / 2.0F;
	};

	public static final QuillCurve SMOOTHSTEP = progress -> {
		float t = clamp(progress);
		return t * t * (3.0F - 2.0F * t);
	};

	private QuillCurves() {
	}

	public static QuillCurve steps(int steps) {
		int count = Math.max(1, steps);
		return progress -> Math.min(1.0F, (float) Math.floor(clamp(progress) * count) / count);
	}

	private static float clamp(float value) {
		return Math.max(0.0F, Math.min(1.0F, value));
	}
}
