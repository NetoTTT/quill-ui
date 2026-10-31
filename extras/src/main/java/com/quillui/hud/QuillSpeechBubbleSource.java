package com.quillui.hud;

import java.util.List;

/** Supplies the world bubbles for the current frame. Called once per HUD render. */
@FunctionalInterface
public interface QuillSpeechBubbleSource {
	List<QuillSpeechBubble> bubbles(float partialTick);
}
