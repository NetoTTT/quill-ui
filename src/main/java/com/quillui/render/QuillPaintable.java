package com.quillui.render;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * A layout node that paints something behind the widgets of a content tree. {@code QuillScreen}
 * paints these before the widget layer and inside the scroll host when content overflows.
 */
public interface QuillPaintable {
	void paint(GuiGraphicsExtractor graphics, Font font);
}
