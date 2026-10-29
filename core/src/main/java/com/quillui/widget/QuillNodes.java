package com.quillui.widget;

import com.quillui.layout.QuillFlexBox;
import com.quillui.layout.QuillFlexible;
import com.quillui.layout.QuillLayoutNode;
import com.quillui.layout.QuillSizedBox;
import com.quillui.theme.QuillThemeData;
import com.quillui.theme.QuillThemeScope;
import java.util.List;
import java.util.function.IntConsumer;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

/** Static builders for measured widget trees. */
public final class QuillNodes {
	private QuillNodes() {
	}

	public static QuillFlexBox row(int gap, QuillLayoutNode... children) {
		QuillFlexBox box = QuillFlexBox.row(gap);
		for (QuillLayoutNode child : children) {
			box.add(child);
		}
		return box;
	}

	public static QuillFlexBox column(int gap, QuillLayoutNode... children) {
		QuillFlexBox box = QuillFlexBox.column(gap);
		for (QuillLayoutNode child : children) {
			box.add(child);
		}
		return box;
	}

	public static QuillFlexible flexible(QuillLayoutNode node, int weight) {
		return new QuillFlexible(node, weight);
	}

	public static QuillFlexible flexible(QuillLayoutNode node) {
		return new QuillFlexible(node, 1);
	}

	public static QuillSizedBox spacer() {
		return new QuillSizedBox(0, 0);
	}

	public static QuillSizedBox spacer(int width) {
		return new QuillSizedBox(width, 0);
	}

	public static QuillSizedBox sized(int width, int height, QuillLayoutNode child) {
		return new QuillSizedBox(width, height, child);
	}

	public static QuillSizedBox fixedWidth(int width, QuillLayoutNode child) {
		return new QuillSizedBox(width, null, child);
	}

	public static QuillSizedBox fixedHeight(int height, QuillLayoutNode child) {
		return new QuillSizedBox(null, height, child);
	}

	/** Wraps a widget with an explicit measured size. */
	public static QuillWidgetBox widget(AbstractWidget widget, int width, int height) {
		widget.setWidth(width);
		widget.setHeight(height);
		return new QuillWidgetBox(widget);
	}

	/** Wraps a widget at its current size. */
	public static QuillWidgetBox widget(AbstractWidget widget) {
		return new QuillWidgetBox(widget);
	}

	/**
	 * Equal-width tab row as a layout node. The active tab uses the primary variant; the rest use
	 * ghost. Selection is owned by the caller, usually followed by a content rebuild.
	 */
	public static QuillFlexBox tabs(
			int gap,
			List<Component> labels,
			int selected,
			IntConsumer onChange
	) {
		return tabs(QuillThemeScope.current(), gap, labels, selected, onChange);
	}

	public static QuillFlexBox tabs(
			QuillThemeData theme,
			int gap,
			List<Component> labels,
			int selected,
			IntConsumer onChange
	) {
		QuillThemeData resolved = theme == null ? QuillThemeData.DEFAULT : theme;
		QuillFlexBox row = QuillFlexBox.row(gap);
		int height = resolved.metrics().controlHeight();
		for (int index = 0; index < labels.size(); index++) {
			int tabIndex = index;
			QuillButton button = new QuillButton(
					0,
					0,
					resolved.metrics().buttonWidth(),
					height,
					labels.get(index),
					index == selected ? QuillButton.Variant.PRIMARY : QuillButton.Variant.GHOST,
					() -> {
						if (onChange != null) {
							onChange.accept(tabIndex);
						}
					},
					resolved
			);
			row.add(new QuillFlexible(new QuillWidgetBox(button), 1));
		}
		return row;
	}
}
