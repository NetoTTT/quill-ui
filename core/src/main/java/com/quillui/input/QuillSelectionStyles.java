package com.quillui.input;

import com.quillui.render.QuillRender;
import com.quillui.theme.QuillThemeData;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * Built-in selection-control styles. Applications may supply their own
 * {@link QuillSelectionStyle} without changing the checkbox or radio widgets.
 */
public final class QuillSelectionStyles {
	public static final QuillSelectionStyle CLASSIC =
			(graphics, context) -> renderClassic(graphics, context, false);

	public static final QuillSelectionStyle FILLED =
			(graphics, context) -> renderClassic(graphics, context, true);

	public static final QuillSelectionStyle MINIMAL = QuillSelectionStyles::renderMinimal;

	private QuillSelectionStyles() {
	}

	private static void renderClassic(
			GuiGraphicsExtractor graphics, QuillSelectionStyle.Context context, boolean filled
	) {
		QuillThemeData theme = context.theme();
		boolean filledSelected = filled && context.selected();
		int background = !context.enabled()
				? theme.color(QuillThemeData.ColorRole.BUTTON_DISABLED)
				: (filledSelected
						? theme.color(QuillThemeData.ColorRole.ACCENT)
						: (context.selected()
								? theme.color(QuillThemeData.ColorRole.PRIMARY_BG)
								: (filled
										? theme.color(QuillThemeData.ColorRole.PRIMARY_BG)
										: theme.color(QuillThemeData.ColorRole.BUTTON_BG))));
		int border = !context.enabled()
				? theme.color(QuillThemeData.ColorRole.BUTTON_DISABLED_BORDER)
				: (context.selected()
						? theme.color(filledSelected
								? QuillThemeData.ColorRole.ACCENT_HOVER
								: QuillThemeData.ColorRole.ACCENT)
						: (context.hovered()
								? theme.color(QuillThemeData.ColorRole.ACCENT_HOVER)
								: theme.color(QuillThemeData.ColorRole.BUTTON_BORDER)));
		int mark = !context.enabled()
				? theme.color(QuillThemeData.ColorRole.BUTTON_DISABLED_TEXT)
				: theme.color(filledSelected
						? QuillThemeData.ColorRole.PANEL_BG
						: QuillThemeData.ColorRole.ON_ACCENT);

		if (context.type() == QuillSelectionStyle.Type.RADIO) {
			renderRadio(graphics, context, background, border, mark);
			return;
		}

		graphics.fill(
				context.x(), context.y(),
				context.x() + context.size(), context.y() + context.size(),
				background
		);
		QuillRender.border(
				graphics, context.x(), context.y(), context.size(), context.size(), border
		);
		if (context.selected()) {
			drawCheck(graphics, context.x(), context.y(), mark);
		}
		drawFocus(graphics, context);
	}

	private static void renderRadio(
			GuiGraphicsExtractor graphics,
			QuillSelectionStyle.Context context,
			int background,
			int border,
			int mark
	) {
		int centerX = context.x() + context.size() / 2;
		int centerY = context.y() + context.size() / 2;
		int radius = context.size() / 2;
		if (context.focused()) {
			QuillRender.circle(graphics, centerX, centerY, radius + 2,
					context.color(QuillThemeData.ColorRole.FOCUS_RING));
		}
		QuillRender.circle(graphics, centerX, centerY, radius, border);
		QuillRender.circle(graphics, centerX, centerY, radius - 2, background);
		if (context.selected()) {
			QuillRender.circle(graphics, centerX, centerY, 2, mark);
		}
	}

	private static void renderMinimal(
			GuiGraphicsExtractor graphics, QuillSelectionStyle.Context context
	) {
		QuillThemeData theme = context.theme();
		int border = !context.enabled()
				? theme.color(QuillThemeData.ColorRole.BUTTON_DISABLED_BORDER)
				: (context.selected()
						? theme.color(QuillThemeData.ColorRole.ACCENT)
						: theme.color(QuillThemeData.ColorRole.BUTTON_BORDER));
		int mark = context.enabled()
				? theme.color(QuillThemeData.ColorRole.ACCENT)
				: theme.color(QuillThemeData.ColorRole.BUTTON_DISABLED_TEXT);

		if (context.type() == QuillSelectionStyle.Type.RADIO) {
			int centerX = context.x() + context.size() / 2;
			int centerY = context.y() + context.size() / 2;
			int radius = context.size() / 2;
			QuillRender.circle(graphics, centerX, centerY, radius, border);
			QuillRender.circle(graphics, centerX, centerY, radius - 1,
					theme.color(QuillThemeData.ColorRole.PANEL_BG));
			if (context.selected()) {
				QuillRender.circle(graphics, centerX, centerY, 2, mark);
			}
		} else {
			QuillRender.border(
					graphics, context.x(), context.y(), context.size(), context.size(), border
			);
			if (context.selected()) {
				drawCheck(graphics, context.x(), context.y(), mark);
			}
		}
		drawFocus(graphics, context);
	}

	private static void drawFocus(
			GuiGraphicsExtractor graphics, QuillSelectionStyle.Context context
	) {
		if (!context.focused()) {
			return;
		}
		int focus = context.color(QuillThemeData.ColorRole.FOCUS_RING);
		QuillRender.border(
				graphics,
				context.x() - 1,
				context.y() - 1,
				context.size() + 2,
				context.size() + 2,
				focus
		);
	}

	private static void drawCheck(GuiGraphicsExtractor graphics, int x, int y, int color) {
		QuillRender.line(graphics, x + 2, y + 6, x + 5, y + 9, color);
		QuillRender.line(graphics, x + 3, y + 6, x + 5, y + 8, color);
		QuillRender.line(graphics, x + 5, y + 9, x + 9, y + 3, color);
		QuillRender.line(graphics, x + 5, y + 8, x + 9, y + 2, color);
	}
}
