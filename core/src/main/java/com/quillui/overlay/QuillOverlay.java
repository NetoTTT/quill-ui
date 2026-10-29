package com.quillui.overlay;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

/**
 * Screen-level overlay rendered above the widget tree. Overlays own their input routing and can be
 * dismissed by the host screen when a click lands outside their bounds.
 */
public interface QuillOverlay {
	void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta);

	default boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		return false;
	}

	default boolean mouseReleased(MouseButtonEvent event) {
		return false;
	}

	default boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
		return false;
	}

	default boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		return false;
	}

	default boolean keyPressed(KeyEvent event) {
		return false;
	}

	default boolean isOpen() {
		return true;
	}

	default void close() {
	}

	default boolean dismissOnOutsideClick() {
		return true;
	}

	default boolean blocksInput() {
		return false;
	}
}
