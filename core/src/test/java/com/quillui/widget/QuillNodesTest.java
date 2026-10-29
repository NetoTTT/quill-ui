package com.quillui.widget;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.quillui.layout.QuillBoxConstraints;
import com.quillui.layout.QuillFlexBox;
import com.quillui.layout.QuillRect;
import com.quillui.layout.QuillSize;
import com.quillui.layout.QuillSizedBox;
import java.util.List;
import net.minecraft.network.chat.Component;
import org.junit.jupiter.api.Test;

class QuillNodesTest {
	@Test
	void rowMixesFixedAndFlexibleChildren() {
		QuillSizedBox fixed = new QuillSizedBox(20, 10);
		QuillSizedBox grow = new QuillSizedBox(0, 10);
		QuillFlexBox row = QuillNodes.row(
				4,
				fixed,
				QuillNodes.flexible(grow)
		);

		assertEquals(QuillSize.of(100, 10), row.measure(QuillBoxConstraints.loose(100, 50)));

		row.arrange(new QuillRect(0, 0, 100, 10));
		assertEquals(0, fixed.bounds().x());
		assertEquals(24, grow.bounds().x());
		assertEquals(76, grow.bounds().width());
	}

	@Test
	void columnUsesTheSameBuilder() {
		QuillFlexBox column = QuillNodes.column(
				4,
				new QuillSizedBox(20, 10),
				new QuillSizedBox(20, 10)
		);

		assertEquals(QuillSize.of(20, 24), column.measure(QuillBoxConstraints.loose(100, 50)));
	}

	@Test
	void tabsAreEqualWidthNodes() {
		QuillFlexBox tabs = QuillNodes.tabs(
				6,
				List.of(Component.literal("Work"), Component.literal("Config")),
				0,
				index -> {
				}
		);

		assertEquals(QuillSize.of(200, 20), tabs.measure(QuillBoxConstraints.loose(200, 50)));
		assertEquals(2, tabs.count());
		assertEquals(97, tabs.childSizes().get(0).width());
		assertEquals(97, tabs.childSizes().get(1).width());
	}
}
