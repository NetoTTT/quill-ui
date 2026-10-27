package com.quillui.render;

import com.quillui.layout.QuillRect;
import com.quillui.theme.QuillColors;
import com.quillui.theme.QuillThemeData;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/** Small top/bottom chevrons that reveal hidden scroll content. */
public final class QuillScrollHints {
	private QuillScrollHints() {
	}

	public static void render(
			GuiGraphicsExtractor graphics,
			QuillRect viewport,
			boolean moreAbove,
			boolean moreBelow,
			QuillThemeData theme
	) {
		if (viewport == null || (!moreAbove && !moreBelow)) {
			return;
		}
		int color = theme.color(QuillThemeData.ColorRole.TEXT_SOFT);
		int background = theme.color(QuillThemeData.ColorRole.PANEL_BG);
		int transparent = QuillColors.withAlpha(background, 0.0F);
		if (moreAbove) {
			graphics.fillGradient(
					viewport.x(), viewport.y(), viewport.right(), viewport.y() + 10,
					background, transparent
			);
		}
		if (moreBelow) {
			graphics.fillGradient(
					viewport.x(), viewport.bottom() - 10, viewport.right(), viewport.bottom(),
					transparent, background
			);
		}
		int centerX = viewport.centerX();
		if (moreAbove) {
			int y = viewport.y() + 8;
			QuillRender.line(graphics, centerX - 4, y + 3, centerX, y - 1, color);
			QuillRender.line(graphics, centerX + 4, y + 3, centerX, y - 1, color);
		}
		if (moreBelow) {
			int y = viewport.bottom() - 8;
			QuillRender.line(graphics, centerX - 4, y - 3, centerX, y + 1, color);
			QuillRender.line(graphics, centerX + 4, y - 3, centerX, y + 1, color);
		}
	}
}
