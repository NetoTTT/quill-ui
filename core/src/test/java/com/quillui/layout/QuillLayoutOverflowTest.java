package com.quillui.layout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class QuillLayoutOverflowTest {
	@Test
	void sizePlusSaturatesInsteadOfOverflowing() {
		QuillSize size = QuillSize.of(QuillBoxConstraints.UNBOUNDED, 10)
				.plus(QuillInsets.all(8));

		assertEquals(QuillBoxConstraints.UNBOUNDED, size.width());
		assertEquals(26, size.height());
	}

	@Test
	void paddingBoxAcceptsAnUnboundedChild() {
		QuillPaddingBox box = new QuillPaddingBox(
				QuillInsets.all(8),
				new UnboundedWidthNode()
		);

		QuillSize size = assertDoesNotThrow(
				() -> box.measure(QuillBoxConstraints.unbounded())
		);
		assertEquals(QuillBoxConstraints.UNBOUNDED, size.width());
	}

	@Test
	void flexRowAcceptsAnUnboundedChild() {
		QuillFlexBox row = QuillFlexBox.row(4)
				.add(new UnboundedWidthNode())
				.add(new UnboundedWidthNode());

		QuillSize size = assertDoesNotThrow(
				() -> row.measure(QuillBoxConstraints.unbounded())
		);
		assertEquals(QuillBoxConstraints.UNBOUNDED, size.width());
	}

	@Test
	void wrapBoxAcceptsAnUnboundedChild() {
		QuillWrapBox wrap = new QuillWrapBox(4)
				.add(new UnboundedWidthNode())
				.add(new UnboundedWidthNode());

		QuillSize size = assertDoesNotThrow(
				() -> wrap.measure(QuillBoxConstraints.unbounded())
		);
		assertEquals(QuillBoxConstraints.UNBOUNDED, size.width());
	}

	private static final class UnboundedWidthNode extends QuillNode {
		@Override
		public QuillSize measure(QuillBoxConstraints constraints) {
			return constraints.constrain(QuillSize.of(constraints.maxWidth(), 10));
		}

		@Override
		public void arrange(QuillRect bounds) {
			setBounds(bounds);
		}
	}
}
