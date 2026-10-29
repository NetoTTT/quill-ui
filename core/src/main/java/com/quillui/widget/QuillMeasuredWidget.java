package com.quillui.widget;

import com.quillui.layout.QuillBoxConstraints;
import com.quillui.layout.QuillSize;

/**
 * A widget that reports its own preferred size to {@link QuillWidgetBox}. Plain widgets fall back
 * to their current {@code getWidth()}/{@code getHeight()}; measured widgets can adapt to the
 * incoming constraints, which is what text needs in order to wrap.
 */
public interface QuillMeasuredWidget {
	QuillSize measure(QuillBoxConstraints constraints);
}
