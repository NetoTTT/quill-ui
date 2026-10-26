package com.quillui;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * Drawing primitives used by Quill UI. All geometry is in GUI pixels and is intentionally
 * one-pixel sharp so it scales cleanly with Minecraft's GUI scale.
 */
public class QuillRender {
	protected QuillRender() {
	}

	public static void panel(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
		int shadow = QuillElevation.PANEL_OFFSET;
		graphics.fill(x + shadow, y + shadow, x + width + shadow, y + height + shadow, QuillTheme.PANEL_SHADOW);
		graphics.fill(x, y, x + width, y + height, QuillTheme.PANEL_BG);
		border(graphics, x, y, width, height, QuillTheme.PANEL_BORDER);
	}

	public static void border(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color) {
		graphics.fill(x, y, x + width, y + 1, color);
		graphics.fill(x, y + height - 1, x + width, y + height, color);
		graphics.fill(x, y, x + 1, y + height, color);
		graphics.fill(x + width - 1, y, x + width, y + height, color);
	}

	public static void divider(GuiGraphicsExtractor graphics, int x, int y, int width) {
		graphics.fill(x, y, x + width, y + 1, QuillTheme.DIVIDER);
	}

	public static void header(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
		graphics.fill(x, y, x + width, y + height, QuillTheme.HEADER_BG);
		graphics.fill(x, y + height - 1, x + width, y + height, QuillTheme.DIVIDER);
	}

	public static void tabStrip(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
		graphics.fill(x, y, x + width, y + height, QuillTheme.CHIP_BG);
		border(graphics, x, y, width, height, QuillTheme.CHIP_BORDER);
	}

	public static void section(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
		graphics.fill(x, y, x + width, y + height, QuillTheme.SURFACE);
		graphics.fill(x, y, x + width, y + 1, QuillTheme.SURFACE_HEADER);
		border(graphics, x, y, width, height, QuillTheme.SURFACE_BORDER);
	}

	public static void inset(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
		graphics.fill(x, y, x + width, y + height, QuillTheme.SURFACE_ALT);
	}
}
