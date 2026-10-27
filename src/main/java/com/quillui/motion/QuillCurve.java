package com.quillui.motion;

/**
 * Maps a normalized linear progress value to an eased progress value.
 */
@FunctionalInterface
public interface QuillCurve {
	float transform(float progress);
}
