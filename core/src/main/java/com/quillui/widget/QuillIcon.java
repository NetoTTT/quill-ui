package com.quillui.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * A small paintable glyph used by buttons and rows. Implement it to draw an item, a texture region
 * or any custom content; the host decides the final size and keeps the widget metrics.
 */
public interface QuillIcon {
	/** Natural width in pixels. */
	int width();

	/** Natural height in pixels. */
	int height();

	/** Paints the icon with its top-left corner at {@code x, y}. */
	void paint(GuiGraphicsExtractor graphics, int x, int y);
}
