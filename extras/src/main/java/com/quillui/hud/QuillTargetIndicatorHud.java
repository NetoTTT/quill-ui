package com.quillui.hud;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * Thin HUD adapter for {@link QuillTargetIndicator}. Register it with Fabric's HudElementRegistry
 * and update the target whenever the highlighted object changes.
 */
public final class QuillTargetIndicatorHud implements HudElement {
	private final QuillTargetIndicator indicator;

	public QuillTargetIndicatorHud(QuillTargetIndicator indicator) {
		this.indicator = indicator;
	}

	public QuillTargetIndicator indicator() {
		return indicator;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker delta) {
		indicator.render(graphics);
	}
}
