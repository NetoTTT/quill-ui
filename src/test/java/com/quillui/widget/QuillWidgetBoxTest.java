package com.quillui.widget;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.quillui.layout.QuillBoxConstraints;
import com.quillui.layout.QuillRect;
import com.quillui.layout.QuillSize;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.junit.jupiter.api.Test;

class QuillWidgetBoxTest {
	@Test
	void adapterMeasuresAndMovesWidget() {
		TestWidget widget = new TestWidget(80, 20);
		QuillWidgetBox box = new QuillWidgetBox(widget);

		assertEquals(QuillSize.of(80, 20), box.measure(QuillBoxConstraints.loose(200, 100)));

		box.arrange(new QuillRect(10, 20, 90, 30));
		assertEquals(10, widget.getX());
		assertEquals(20, widget.getY());
		assertEquals(90, widget.getWidth());
		assertEquals(30, widget.getHeight());
		assertTrue(widget.visible);
	}

	@Test
	void clipHidesWidgetsOutsideViewport() {
		TestWidget widget = new TestWidget(80, 20);
		QuillWidgetBox box = new QuillWidgetBox(widget)
				.clipTo(new QuillRect(0, 0, 100, 100));

		box.arrange(new QuillRect(10, 10, 80, 20));
		assertTrue(widget.visible);

		box.arrange(new QuillRect(10, 90, 80, 20));
		assertFalse(widget.visible);
	}

	private static final class TestWidget extends AbstractWidget {
		private TestWidget(int width, int height) {
			super(0, 0, width, height, Component.literal("test"));
		}

		@Override
		protected void extractWidgetRenderState(
				GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta
		) {
		}

		@Override
		protected void updateWidgetNarration(NarrationElementOutput output) {
		}
	}
}
