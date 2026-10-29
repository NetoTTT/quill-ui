package com.quillui.input;

import com.quillui.theme.QuillThemeData;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * Strategy for painting the indicator of selection controls (checkbox and radio). The widget owns
 * layout, label and input handling; implementations only paint the square/circle and its mark.
 */
@FunctionalInterface
public interface QuillSelectionStyle {
	enum Type {
		CHECKBOX,
		RADIO
	}

	record Context(
			Type type,
			int x,
			int y,
			int size,
			boolean selected,
			boolean hovered,
			boolean focused,
			boolean enabled,
			QuillThemeData theme
	) {
		public int color(QuillThemeData.ColorRole role) {
			return theme.color(role);
		}
	}

	void render(GuiGraphicsExtractor graphics, Context context);
}
