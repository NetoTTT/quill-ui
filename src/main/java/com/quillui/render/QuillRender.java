package com.quillui.render;

import com.quillui.theme.QuillElevation;
import com.quillui.theme.QuillThemeData;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

/**
 * Drawing primitives used by Quill UI. All geometry is in GUI pixels and is intentionally
 * one-pixel sharp so it scales cleanly with Minecraft's GUI scale.
 */
public class QuillRender {
	protected QuillRender() {
	}

	public static void panel(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
		panel(graphics, x, y, width, height, QuillThemeData.DEFAULT);
	}

	public static void panel(
			GuiGraphicsExtractor graphics,
			int x,
			int y,
			int width,
			int height,
			QuillThemeData theme
	) {
		int shadow = QuillElevation.PANEL_OFFSET;
		graphics.fill(
				x + shadow,
				y + shadow,
				x + width + shadow,
				y + height + shadow,
				theme.color(QuillThemeData.ColorRole.PANEL_SHADOW)
		);
		graphics.fill(x, y, x + width, y + height, theme.color(QuillThemeData.ColorRole.PANEL_BG));
		border(graphics, x, y, width, height, theme.color(QuillThemeData.ColorRole.PANEL_BORDER));
	}

	public static void text(
			GuiGraphicsExtractor graphics, Font font, Component text, int x, int y, int color
	) {
		graphics.text(font, text, x, y, color, false);
	}

	public static void centeredText(
			GuiGraphicsExtractor graphics, Font font, Component text, int centerX, int y, int color
	) {
		int x = centerX - font.width(text) / 2;
		graphics.text(font, text, x, y, color, false);
	}

	public static void gradient(
			GuiGraphicsExtractor graphics,
			int x,
			int y,
			int width,
			int height,
			QuillGradient gradient
	) {
		if (gradient.direction() == QuillGradient.Direction.VERTICAL) {
			graphics.fillGradient(x, y, x + width, y + height, gradient.startColor(), gradient.endColor());
			return;
		}

		for (int column = 0; column < width; column++) {
			int color = lerp(gradient.startColor(), gradient.endColor(), column / (float) Math.max(1, width - 1));
			graphics.fill(x + column, y, x + column + 1, y + height, color);
		}
	}

	private static int lerp(int from, int to, float amount) {
		int a = (int) (((from >>> 24) & 0xFF) + ((((to >>> 24) & 0xFF) - ((from >>> 24) & 0xFF)) * amount));
		int r = (int) (((from >>> 16) & 0xFF) + ((((to >>> 16) & 0xFF) - ((from >>> 16) & 0xFF)) * amount));
		int g = (int) (((from >>> 8) & 0xFF) + ((((to >>> 8) & 0xFF) - ((from >>> 8) & 0xFF)) * amount));
		int b = (int) ((from & 0xFF) + (((to & 0xFF) - (from & 0xFF)) * amount));
		return (a << 24) | (r << 16) | (g << 8) | b;
	}

	public static void border(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color) {
		graphics.fill(x, y, x + width, y + 1, color);
		graphics.fill(x, y + height - 1, x + width, y + height, color);
		graphics.fill(x, y, x + 1, y + height, color);
		graphics.fill(x + width - 1, y, x + width, y + height, color);
	}

	public static void divider(GuiGraphicsExtractor graphics, int x, int y, int width) {
		divider(graphics, x, y, width, QuillThemeData.DEFAULT);
	}

	public static void divider(GuiGraphicsExtractor graphics, int x, int y, int width, QuillThemeData theme) {
		graphics.fill(x, y, x + width, y + 1, theme.color(QuillThemeData.ColorRole.DIVIDER));
	}

	public static void header(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
		header(graphics, x, y, width, height, QuillThemeData.DEFAULT);
	}

	public static void header(
			GuiGraphicsExtractor graphics,
			int x,
			int y,
			int width,
			int height,
			QuillThemeData theme
	) {
		graphics.fill(x, y, x + width, y + height, theme.color(QuillThemeData.ColorRole.HEADER_BG));
		graphics.fill(
				x,
				y + height - 1,
				x + width,
				y + height,
				theme.color(QuillThemeData.ColorRole.DIVIDER)
		);
	}

	public static void tabStrip(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
		tabStrip(graphics, x, y, width, height, QuillThemeData.DEFAULT);
	}

	public static void tabStrip(
			GuiGraphicsExtractor graphics,
			int x,
			int y,
			int width,
			int height,
			QuillThemeData theme
	) {
		graphics.fill(x, y, x + width, y + height, theme.color(QuillThemeData.ColorRole.CHIP_BG));
		border(graphics, x, y, width, height, theme.color(QuillThemeData.ColorRole.CHIP_BORDER));
	}

	public static void section(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
		section(graphics, x, y, width, height, QuillThemeData.DEFAULT);
	}

	public static void section(
			GuiGraphicsExtractor graphics,
			int x,
			int y,
			int width,
			int height,
			QuillThemeData theme
	) {
		graphics.fill(x, y, x + width, y + height, theme.color(QuillThemeData.ColorRole.SURFACE));
		graphics.fill(
				x,
				y,
				x + width,
				y + 1,
				theme.color(QuillThemeData.ColorRole.SURFACE_HEADER)
		);
		border(graphics, x, y, width, height, theme.color(QuillThemeData.ColorRole.SURFACE_BORDER));
	}

	public static void inset(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
		inset(graphics, x, y, width, height, QuillThemeData.DEFAULT);
	}

	public static void inset(
			GuiGraphicsExtractor graphics,
			int x,
			int y,
			int width,
			int height,
			QuillThemeData theme
	) {
		graphics.fill(x, y, x + width, y + height, theme.color(QuillThemeData.ColorRole.SURFACE_ALT));
	}
}
