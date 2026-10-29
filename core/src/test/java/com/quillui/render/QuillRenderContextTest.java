package com.quillui.render;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class QuillRenderContextTest {
	@Test
	void clipOffsetIsStackedAndRestored() {
		QuillRenderContext.pushClipOffset(0, 40);
		try {
			assertEquals(10, QuillRenderContext.screenY(50));
			QuillRenderContext.pushClipOffset(0, 10);
			try {
				assertEquals(0, QuillRenderContext.screenY(50));
			}
			finally {
				QuillRenderContext.popClipOffset();
			}
			assertEquals(10, QuillRenderContext.screenY(50));
		}
		finally {
			QuillRenderContext.popClipOffset();
		}
		assertEquals(50, QuillRenderContext.screenY(50));
	}
}
