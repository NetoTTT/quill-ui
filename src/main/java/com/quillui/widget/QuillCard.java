package com.quillui.widget;

import com.quillui.QuillRender;
import com.quillui.QuillSpacing;
import com.quillui.QuillTheme;
import com.quillui.layout.QuillInsets;
import com.quillui.layout.QuillRect;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

/**
 * A bordered content card with an optional compact heading. It is a render/layout helper, not an
 * interactive widget; child controls are registered by the owning screen.
 */
public final class QuillCard {
	private static final int HEADER_HEIGHT = 18;
	private static final int PADDING = QuillSpacing.SPACE_2;

	private final QuillRect bounds;
	private final Component title;

	public QuillCard(int x, int y, int width, int height, Component title) {
		this.bounds = new QuillRect(x, y, width, height);
		this.title = title == null || title.getString().isBlank() ? null : title;
	}

	public QuillRect bounds() {
		return bounds;
	}

	public Component title() {
		return title;
	}

	public void render(GuiGraphicsExtractor graphics, Font font) {
		QuillRender.section(graphics, bounds.x(), bounds.y(), bounds.width(), bounds.height());
		if (title != null) {
			graphics.text(
					font,
					title,
					bounds.x() + PADDING,
					bounds.y() + 5,
					QuillTheme.TEXT_SOFT
			);
		}
	}

	/**
	 * Interior rectangle below the heading, with the standard 8 px padding.
	 */
	public QuillRect content() {
		return content(QuillInsets.all(PADDING));
	}

	/**
	 * Interior rectangle with caller-provided padding. The card heading is always reserved.
	 */
	public QuillRect content(QuillInsets padding) {
		int top = (title == null ? 0 : HEADER_HEIGHT) + padding.top();
		return bounds.inset(new QuillInsets(top, padding.right(), padding.bottom(), padding.left()));
	}
}
