package com.quillui.text;

import com.quillui.layout.QuillBoxConstraints;
import com.quillui.layout.QuillNode;
import com.quillui.layout.QuillRect;
import com.quillui.layout.QuillSize;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

/**
 * A measured text leaf. The node wraps at the tightest of its style width and the incoming
 * constraints, so labels size themselves instead of relying on magic numbers in the screen.
 */
public final class QuillTextNode extends QuillNode {
	private final Component text;
	private final QuillTextStyle style;
	private final QuillTextMeasurer measurer;
	private int lineCount;
	private QuillSize measured = QuillSize.ZERO;

	public QuillTextNode(Component text, QuillTextStyle style, QuillTextMeasurer measurer) {
		if (measurer == null) {
			throw new IllegalArgumentException("measurer must not be null");
		}
		this.text = text == null ? Component.empty() : text;
		this.style = style == null ? QuillTextStyle.DEFAULT : style;
		this.measurer = measurer;
	}

	@Override
	public QuillSize measure(QuillBoxConstraints constraints) {
		int lineHeight = measurer.lineHeight();
		int maxWidth = maxWidth(constraints);
		List<Integer> widths = measurer.lineWidths(text, maxWidth);
		int limit = style.maxLines() > 0
				? Math.min(widths.size(), style.maxLines())
				: widths.size();

		int width = 0;
		for (int index = 0; index < limit; index++) {
			width = Math.max(width, widths.get(index));
		}

		lineCount = limit;
		measured = constraints.constrain(QuillSize.of(width, limit * lineHeight));
		return measured;
	}

	@Override
	public void arrange(QuillRect bounds) {
		setBounds(bounds);
	}

	/** Draws the text at the arranged rectangle using the given font. */
	public void render(GuiGraphicsExtractor graphics, Font font) {
		QuillRect bounds = bounds();
		QuillTextStyle effective = style;
		if (bounds.width() > 0) {
			int wrapWidth = style.maxWidth() > 0
					? Math.min(style.maxWidth(), bounds.width())
					: bounds.width();
			effective = style.withMaxWidth(wrapWidth);
		}
		QuillText.draw(graphics, font, text, bounds.x(), bounds.y(), effective);
	}

	/** Wrapped line count from the last measurement. */
	public int lineCount() {
		return lineCount;
	}

	/** Size chosen by the last measurement, before the parent assigns the final rectangle. */
	public QuillSize measured() {
		return measured;
	}

	public Component text() {
		return text;
	}

	public QuillTextStyle style() {
		return style;
	}

	private int maxWidth(QuillBoxConstraints constraints) {
		if (!constraints.hasBoundedWidth()) {
			return style.maxWidth();
		}
		if (style.maxWidth() <= 0) {
			return constraints.maxWidth();
		}
		return Math.min(style.maxWidth(), constraints.maxWidth());
	}
}
