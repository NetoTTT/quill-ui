package com.quillui.widget;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.quillui.layout.QuillRect;
import com.mojang.blaze3d.platform.InputConstants;
import com.quillui.theme.QuillThemeData;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
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

	@Test
	void keepsOnlyOneFocusedChild() {
		TestWidget first = new TestWidget();
		TestWidget second = new TestWidget();
		QuillClipView view = new QuillClipView(
				new QuillRect(0, 0, 100, 100),
				() -> 0,
				() -> 0,
				value -> {
				},
				QuillThemeData.DEFAULT
		);
		view.add(first);
		view.add(second);

		view.mouseClicked(new MouseButtonEvent(10, 10, new MouseButtonInfo(0, 0)), false);
		assertFalse(first.isFocused());
		assertTrue(second.isFocused());

		view.mouseClicked(new MouseButtonEvent(10, 10, new MouseButtonInfo(0, 0)), false);
		assertFalse(first.isFocused());
		assertTrue(second.isFocused());
	}

	@Test
	void forwardsTypedCharactersToFocusedChild() {
		TestWidget widget = new TestWidget();
		QuillClipView view = new QuillClipView(
				new QuillRect(0, 0, 100, 100),
				() -> 0,
				() -> 0,
				value -> {
				},
				QuillThemeData.DEFAULT
		);
		view.add(widget);
		view.mouseClicked(new MouseButtonEvent(10, 10, new MouseButtonInfo(0, 0)), false);

		assertTrue(view.charTyped(new CharacterEvent('a')));
		assertEquals('a', widget.typed);
		assertTrue(view.capturesInput());
	}

	@Test
	void tabMovesFocusBetweenChildren() {
		TestWidget first = new TestWidget();
		TestWidget second = new TestWidget();
		QuillClipView view = new QuillClipView(
				new QuillRect(0, 0, 100, 100),
				() -> 0,
				() -> 0,
				value -> {
				},
				QuillThemeData.DEFAULT
		);
		view.add(first);
		view.add(second);
		view.setFocused(true);

		assertTrue(view.keyPressed(new KeyEvent(InputConstants.KEY_TAB, 0, 0)));
		assertTrue(first.isFocused());
		assertFalse(second.isFocused());

		assertTrue(view.keyPressed(new KeyEvent(InputConstants.KEY_TAB, 0, 0)));
		assertFalse(first.isFocused());
		assertTrue(second.isFocused());
	}

	@Test
	void tabScrollsFocusedChildIntoView() {
		int[] offset = {0};
		TestWidget first = new TestWidget();
		TestWidget second = new TestWidget();
		first.setY(80);
		second.setY(120);
		QuillClipView view = new QuillClipView(
				new QuillRect(0, 0, 100, 100),
				() -> 200,
				() -> offset[0],
				value -> offset[0] = value,
				QuillThemeData.DEFAULT
		);
		view.add(first);
		view.add(second);
		view.setFocused(true);

		view.keyPressed(new KeyEvent(InputConstants.KEY_TAB, 0, 0));
		view.keyPressed(new KeyEvent(InputConstants.KEY_TAB, 0, 0));
		assertTrue(second.isFocused());
		assertEquals(40, offset[0]);
	}

	@Test
	void mouseContextMapsContentToScreen() {
		QuillMouseContext.push(0, 50);
		try {
			assertEquals(10, QuillMouseContext.screenY(60));
		}
		finally {
			QuillMouseContext.pop();
		}
		assertEquals(60, QuillMouseContext.screenY(60));
	}

	private static final class TestWidget extends AbstractWidget {
		private int clickedY = -1;
		private int typed = -1;

		private TestWidget() {
			super(0, 0, 80, 20, Component.literal("test"));
		}

		@Override
		public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
			clickedY = (int) event.y();
			return true;
		}

		@Override
		public boolean charTyped(CharacterEvent event) {
			typed = event.codepoint();
			return true;
		}

		@Override
		public boolean capturesInput() {
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
