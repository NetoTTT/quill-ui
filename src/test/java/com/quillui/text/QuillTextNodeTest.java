package com.quillui.text;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.quillui.layout.QuillBoxConstraints;
import com.quillui.layout.QuillRect;
import com.quillui.layout.QuillSize;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import org.junit.jupiter.api.Test;

class QuillTextNodeTest {
	private static final int GLYPH_WIDTH = 5;
	private static final int LINE_HEIGHT = 9;

	@Test
	void measuresASingleLineWithoutABound() {
		QuillTextNode node = node("abcde", QuillTextStyle.DEFAULT);

		assertEquals(QuillSize.of(25, LINE_HEIGHT), node.measure(QuillBoxConstraints.unbounded()));
		assertEquals(1, node.lineCount());
	}

	@Test
	void wrapsAtTheIncomingWidth() {
		QuillTextNode node = node("abcdefghij", QuillTextStyle.DEFAULT);

		assertEquals(QuillSize.of(30, LINE_HEIGHT * 2),
				node.measure(QuillBoxConstraints.loose(30, 200)));
		assertEquals(2, node.lineCount());
	}

	@Test
	void maxLinesClampsTheMeasuredHeight() {
		QuillTextNode node = node("abcdefghij", QuillTextStyle.DEFAULT.withMaxLines(1));

		assertEquals(QuillSize.of(30, LINE_HEIGHT),
				node.measure(QuillBoxConstraints.loose(30, 200)));
		assertEquals(1, node.lineCount());
	}

	@Test
	void styleWidthWrapsBelowAWiderParent() {
		QuillTextNode node = node("abcdefghij", QuillTextStyle.DEFAULT.withMaxWidth(20));

		assertEquals(QuillSize.of(20, LINE_HEIGHT * 3),
				node.measure(QuillBoxConstraints.loose(200, 200)));
	}

	@Test
	void minimumConstraintsPadTheMeasuredWidth() {
		QuillTextNode node = node("abc", QuillTextStyle.DEFAULT);

		assertEquals(QuillSize.of(60, LINE_HEIGHT),
				node.measure(QuillBoxConstraints.loose(200, 200).withMinWidth(60)));
	}

	@Test
	void emptyTextKeepsOneLineOfHeight() {
		QuillTextNode node = node("", QuillTextStyle.DEFAULT);

		assertEquals(QuillSize.of(0, LINE_HEIGHT), node.measure(QuillBoxConstraints.unbounded()));
		assertEquals(1, node.lineCount());
	}

	@Test
	void arrangeStoresTheAssignedRectangle() {
		QuillTextNode node = node("abcde", QuillTextStyle.DEFAULT);
		node.measure(QuillBoxConstraints.unbounded());
		node.arrange(new QuillRect(4, 8, 30, LINE_HEIGHT));

		assertEquals(new QuillRect(4, 8, 30, LINE_HEIGHT), node.bounds());
	}

	private static QuillTextNode node(String text, QuillTextStyle style) {
		return new QuillTextNode(Component.literal(text), style, measurer());
	}

	/** Fixed-width fake: every glyph is five pixels and every line is nine pixels tall. */
	private static QuillTextMeasurer measurer() {
		return new QuillTextMeasurer() {
			@Override
			public int lineHeight() {
				return LINE_HEIGHT;
			}

			@Override
			public int width(FormattedText text) {
				return text.getString().length() * GLYPH_WIDTH;
			}

			@Override
			public List<Integer> lineWidths(FormattedText text, int maxWidth) {
				String raw = text.getString();
				if (raw.isEmpty()) {
					return List.of(0);
				}
				if (maxWidth <= 0) {
					return List.of(width(text));
				}

				int perLine = Math.max(1, maxWidth / GLYPH_WIDTH);
				List<Integer> widths = new ArrayList<>();
				for (int start = 0; start < raw.length(); start += perLine) {
					int end = Math.min(raw.length(), start + perLine);
					widths.add((end - start) * GLYPH_WIDTH);
				}
				return widths;
			}
		};
	}
}
