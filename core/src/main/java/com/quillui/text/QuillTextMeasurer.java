package com.quillui.text;

import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

/**
 * Font-backed measurement used by {@link QuillTextNode}. Keeping measurement behind this interface
 * lets layout tests run without a live {@link Font}.
 */
public interface QuillTextMeasurer {
	int lineHeight();

	int width(FormattedText text);

	/**
	 * Width of each visual line after wrapping at {@code maxWidth}. A non-positive or unbounded
	 * width means a single, unwrapped line.
	 */
	List<Integer> lineWidths(FormattedText text, int maxWidth);

	static QuillTextMeasurer of(Font font) {
		if (font == null) {
			throw new IllegalArgumentException("font must not be null");
		}
		return new QuillTextMeasurer() {
			@Override
			public int lineHeight() {
				return font.lineHeight;
			}

			@Override
			public int width(FormattedText text) {
				return font.width(text);
			}

			@Override
			public List<Integer> lineWidths(FormattedText text, int maxWidth) {
				if (maxWidth <= 0) {
					return List.of(font.width(text));
				}
				List<FormattedCharSequence> lines = font.split(text, maxWidth);
				if (lines.isEmpty()) {
					return List.of(0);
				}
				return lines.stream().map(font::width).toList();
			}
		};
	}
}
