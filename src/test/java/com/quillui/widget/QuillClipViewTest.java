package com.quillui.widget;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.quillui.layout.QuillRect;
import com.quillui.theme.QuillThemeData;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.network.chat.Component;
import org.junit.jupiter.api.Test;

class QuillClipViewTest {
	@Test
	void mapsInputThroughScrollOffset() {
		int[] offset = {50};
		TestWidget widget = new TestWidget();
		QuillClipView view = new QuillClipView(
				new QuillRect(0, 0, 100, 100),
				() -> 200,
				() -> offset[0],
				value -> offset[0] = value,
				QuillThemeData.DEFAULT
		);
		view.add(widget);

		assertTrue(view.mouseClicked(
				new MouseButtonEvent(10, 60, new MouseButtonInfo(0, 0)),
				false
		));
		assertEquals(110, widget.clickedY);

		assertTrue(view.mouseScrolled(10, 60, 0, -1));
		assertEquals(74, offset[0]);
	}

	private static final class TestWidget extends AbstractWidget {
		private int clickedY = -1;

		private TestWidget() {
			super(0, 0, 80, 20, Component.literal("test"));
		}

		@Override
		public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
			clickedY = (int) event.y();
			return true;
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
