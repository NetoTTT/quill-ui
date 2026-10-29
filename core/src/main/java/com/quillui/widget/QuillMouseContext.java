package com.quillui.widget;

import com.quillui.render.QuillRenderContext;

/** Compatibility facade for the shared render context used by clipped hosts. */
final class QuillMouseContext {
	private QuillMouseContext() {
	}

	static void push(int x, int y) {
		QuillRenderContext.pushClipOffset(x, y);
	}

	static void pop() {
		QuillRenderContext.popClipOffset();
	}

	static int screenX(int contentX) {
		return QuillRenderContext.screenX(contentX);
	}

	static int screenY(int contentY) {
		return QuillRenderContext.screenY(contentY);
	}
}
