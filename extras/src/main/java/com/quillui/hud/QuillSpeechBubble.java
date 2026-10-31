package com.quillui.hud;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

/**
 * One world-anchored speech bubble. {@code key} keeps the fade/smoothing state stable across
 * frames; use the entity UUID or any stable object.
 */
public record QuillSpeechBubble(
		Object key,
		Vec3 position,
		Component text,
		int accentColor,
		double maxDistance,
		boolean throughWalls
) {
	public QuillSpeechBubble {
		if (key == null) {
			throw new IllegalArgumentException("key must not be null");
		}
		if (position == null) {
			throw new IllegalArgumentException("position must not be null");
		}
		text = text == null ? Component.empty() : text;
		maxDistance = Math.max(0.5D, maxDistance);
	}
}
