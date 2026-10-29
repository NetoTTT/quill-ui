package com.quillui.text;

import com.quillui.render.QuillGradient;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

/**
 * Text layout helpers on top of the vanilla bitmap font. Normal text preserves Component styles;
 * gradient text is drawn glyph by glyph so the renderer can interpolate the colour.
 */
public final class QuillText {
	private static final String ELLIPSIS = "...";

	private QuillText() {
	}

	public static int draw(
			GuiGraphicsExtractor graphics,
			Font font,
			Component text,
			int x,
			int y,
			QuillTextStyle style
	) {
		if (text == null || text.getString().isEmpty()) {
			return 0;
		}
		if (style == null) {
			style = QuillTextStyle.DEFAULT;
		}
		if (style.gradient() != null) {
			return drawGradientLine(graphics, font, text.getString(), x, y, style);
		}

		int maxWidth = style.maxWidth() > 0 ? style.maxWidth() : Integer.MAX_VALUE;
		List<FormattedCharSequence> lines = font.split(text, maxWidth);
		int limit = style.maxLines() > 0 ? Math.min(lines.size(), style.maxLines()) : lines.size();
		for (int index = 0; index < limit; index++) {
			FormattedCharSequence line = lines.get(index);
			int lineWidth = font.width(line);
			int lineX = alignedX(x, maxWidth, lineWidth, style.align());
			int lineY = y + index * font.lineHeight;
			graphics.text(font, line, lineX, lineY, style.color(), style.shadow());
		}

		return limit * font.lineHeight;
	}

	public static int draw(
			GuiGraphicsExtractor graphics,
			Font font,
			Component text,
			int x,
			int y,
			int color
	) {
		return draw(graphics, font, text, x, y, QuillTextStyle.of(color));
	}

	/**
	 * Draws a single-line, horizontal per-glyph gradient. Multi-line wrapping and vertical
	 * gradients are not approximated here; use a custom textured-text renderer for those.
	 */
	public static int drawGradient(
			GuiGraphicsExtractor graphics,
			Font font,
			Component text,
			int x,
			int y,
			QuillGradient gradient,
			QuillTextStyle style
	) {
		QuillTextStyle resolved = style == null ? QuillTextStyle.DEFAULT : style;
		return draw(graphics, font, text, x, y, resolved.withGradient(gradient));
	}

	private static int drawEllipsized(
			GuiGraphicsExtractor graphics,
			Font font,
			String raw,
			int x,
			int y,
			QuillTextStyle style,
			int maxWidth
	) {
		List<FormattedText> lines = font.getSplitter().splitLines(raw, maxWidth, Style.EMPTY);
		int limit = style.maxLines() > 0 ? Math.min(lines.size(), style.maxLines()) : lines.size();
		for (int index = 0; index < limit; index++) {
			String line = lines.get(index).getString();
			if (index == limit - 1 && lines.size() > limit) {
				String body = font.plainSubstrByWidth(line, Math.max(1, maxWidth - font.width(ELLIPSIS)));
				line = body + ELLIPSIS;
			}
			int lineWidth = font.width(line);
			int lineX = alignedX(x, maxWidth, lineWidth, style.align());
			graphics.text(font, line, lineX, y + index * font.lineHeight, style.color(), style.shadow());
		}
		return limit * font.lineHeight;
	}
	private static int drawGradientLine(
			GuiGraphicsExtractor graphics,
			Font font,
			String raw,
			int x,
			int y,
			QuillTextStyle style
	) {
		String text = raw;
		if (style.maxWidth() > 0) {
			String clipped = font.plainSubstrByWidth(text, style.maxWidth());
			if (clipped.length() < text.length()) {
				if (style.ellipsis()) {
					int ellipsisWidth = font.width(ELLIPSIS);
					String body = font.plainSubstrByWidth(text, Math.max(1, style.maxWidth() - ellipsisWidth));
					text = body + ELLIPSIS;
				} else {
					text = clipped;
				}
			}
		}

		int totalWidth = font.width(text);
		int lineX = alignedX(x, style.maxWidth() > 0 ? style.maxWidth() : totalWidth, totalWidth, style.align());
		int cursor = 0;
		for (int index = 0; index < text.length(); ) {
			int codePoint = text.codePointAt(index);
			String glyph = new String(Character.toChars(codePoint));
			int glyphWidth = font.width(glyph);
			float progress = totalWidth <= 0 ? 0.0F : (cursor + glyphWidth * 0.5F) / totalWidth;
			int color = style.gradient().colorAt(progress);
			graphics.text(font, glyph, lineX + cursor, y, color, style.shadow());
			cursor += glyphWidth;
			index += Character.charCount(codePoint);
		}
		return font.lineHeight;
	}

	private static int alignedX(int x, int availableWidth, int lineWidth, QuillTextStyle.Align align) {
		return switch (align) {
			case CENTER -> x + (availableWidth - lineWidth) / 2;
			case RIGHT -> x + availableWidth - lineWidth;
			default -> x;
		};
	}
}
