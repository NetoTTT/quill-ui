package com.quillui.widget;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.quillui.layout.QuillBoxConstraints;
import com.quillui.layout.QuillInsets;
import com.quillui.layout.QuillRect;
import com.quillui.layout.QuillSize;
import com.quillui.layout.QuillSizedBox;
import com.quillui.theme.QuillThemeData;
import net.minecraft.network.chat.Component;
import org.junit.jupiter.api.Test;

class QuillCardBoxTest {
	@Test
	void measuresHeaderPaddingAndChild() {
		QuillSizedBox child = new QuillSizedBox(100, 20);
		QuillCardBox card = new QuillCardBox(
				Component.literal("Status"),
				QuillThemeData.DEFAULT,
				QuillInsets.all(8),
				child
		);

		assertEquals(
				QuillSize.of(116, 54),
				card.measure(QuillBoxConstraints.loose(300, 200))
		);

		card.arrange(new QuillRect(0, 0, 116, 54));
		assertEquals(new QuillRect(8, 26, 100, 20), card.content());
		assertEquals(new QuillRect(8, 26, 100, 20), child.bounds());
	}

	@Test
	void titlelessCardOmitsTheHeader() {
		QuillCardBox card = new QuillCardBox(
				null,
				QuillThemeData.DEFAULT,
				QuillInsets.all(8),
				new QuillSizedBox(100, 20)
		);

		assertEquals(
				QuillSize.of(116, 36),
				card.measure(QuillBoxConstraints.loose(300, 200))
		);
	}
}
