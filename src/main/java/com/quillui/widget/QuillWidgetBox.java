package com.quillui.widget;

import com.quillui.layout.QuillBoxConstraints;
import com.quillui.layout.QuillLayoutNode;
import com.quillui.layout.QuillNode;
import com.quillui.layout.QuillRect;
import com.quillui.layout.QuillSize;
import java.util.List;
import net.minecraft.client.gui.components.AbstractWidget;

/** Adapter between a measured layout tree and an existing vanilla/Quill widget. */
public final class QuillWidgetBox extends QuillNode {
	private final AbstractWidget widget;
	private QuillRect clipBounds;

	public QuillWidgetBox(AbstractWidget widget) {
		if (widget == null) {
			throw new IllegalArgumentException("widget must not be null");
		}
		this.widget = widget;
	}

	public QuillWidgetBox clipTo(QuillRect clipBounds) {
		this.clipBounds = clipBounds;
		return this;
	}

	@Override
	public QuillSize measure(QuillBoxConstraints constraints) {
		QuillSize preferred = QuillSize.of(widget.getWidth(), widget.getHeight());
		if (preferred.width() == 0 && preferred.height() == 0) {
			preferred = QuillSize.of(constraints.minWidth(), constraints.minHeight());
		}
		return constraints.constrain(preferred);
	}

	@Override
	public void arrange(QuillRect bounds) {
		setBounds(bounds);
		widget.setX(bounds.x());
		widget.setY(bounds.y());
		widget.setWidth(bounds.width());
		widget.setHeight(bounds.height());
		if (clipBounds != null) {
			boolean inside = clipBounds.contains(bounds.x(), bounds.y())
					&& clipBounds.contains(
							bounds.x() + Math.max(0, bounds.width() - 1),
							bounds.y() + Math.max(0, bounds.height() - 1)
					);
			widget.visible = inside;
		}
	}

	public AbstractWidget widget() {
		return widget;
	}

	@Override
	public List<QuillLayoutNode> children() {
		return List.of();
	}
}
