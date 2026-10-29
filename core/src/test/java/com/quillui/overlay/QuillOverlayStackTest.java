package com.quillui.overlay;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class QuillOverlayStackTest {
	@Test
	void dispatchRunsFromTopToBottom() {
		QuillOverlayStack stack = new QuillOverlayStack();
		List<String> seen = new ArrayList<>();
		stack.add(new TestOverlay("first", seen, false));
		stack.add(new TestOverlay("second", seen, false));

		assertTrue(stack.dispatch(overlay -> {
			((TestOverlay) overlay).record();
			return true;
		}));
		assertEquals(List.of("second"), seen);
	}

	@Test
	void blockingOverlaySwallowsUnhandledEvents() {
		QuillOverlayStack stack = new QuillOverlayStack();
		stack.add(new TestOverlay("blocking", new ArrayList<>(), true));

		assertTrue(stack.dispatch(overlay -> false));
	}

	@Test
	void closedOverlaysArePrunedAfterDispatch() {
		QuillOverlayStack stack = new QuillOverlayStack();
		TestOverlay overlay = new TestOverlay("closing", new ArrayList<>(), false);
		stack.add(overlay);

		assertTrue(stack.dispatch(candidate -> {
			overlay.closed = true;
			return true;
		}));
		assertEquals(0, stack.size());
		assertNull(stack.top());
	}

	@Test
	void closeAllClosesEveryOverlay() {
		QuillOverlayStack stack = new QuillOverlayStack();
		TestOverlay first = new TestOverlay("first", new ArrayList<>(), false);
		TestOverlay second = new TestOverlay("second", new ArrayList<>(), false);
		stack.add(first);
		stack.add(second);

		stack.closeAll();

		assertTrue(first.closed);
		assertTrue(second.closed);
		assertEquals(0, stack.size());
	}

	@Test
	void blockingIsReportedOnlyWhileTheOverlayIsOpen() {
		QuillOverlayStack stack = new QuillOverlayStack();
		TestOverlay overlay = new TestOverlay("blocking", new ArrayList<>(), true);
		stack.add(overlay);

		assertTrue(stack.hasBlocking());
		overlay.closed = true;
		assertFalse(stack.hasBlocking());
	}

	private static final class TestOverlay implements QuillOverlay {
		private final String name;
		private final List<String> seen;
		private final boolean blocking;
		private boolean closed;

		private TestOverlay(String name, List<String> seen, boolean blocking) {
			this.name = name;
			this.seen = seen;
			this.blocking = blocking;
		}

		private void record() {
			seen.add(name);
		}

		@Override
		public void render(
				net.minecraft.client.gui.GuiGraphicsExtractor graphics,
				int mouseX,
				int mouseY,
				float delta
		) {
		}

		@Override
		public boolean isOpen() {
			return !closed;
		}

		@Override
		public boolean blocksInput() {
			return blocking;
		}

		@Override
		public void close() {
			closed = true;
		}
	}
}
