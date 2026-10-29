package com.quillui.widget;

import com.quillui.layout.QuillBoxConstraints;
import com.quillui.layout.QuillInsets;
import com.quillui.layout.QuillLayoutNode;
import com.quillui.layout.QuillNode;
import com.quillui.layout.QuillRect;
import com.quillui.layout.QuillSize;
import com.quillui.render.QuillPaintable;
import com.quillui.render.QuillRender;
import com.quillui.theme.QuillSpacing;
import com.quillui.theme.QuillThemeData;
import com.quillui.theme.QuillThemeScope;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

/**
 * A measured card: paints a bordered surface with an optional heading and arranges its child in
 * the padded body. It lets forms group controls without absolute coordinates.
 */
public final class QuillCardBox extends QuillNode implements QuillPaintable {
	private static final int HEADER_HEIGHT = 18;

	private final Component title;
	private final QuillThemeData theme;
	private final QuillInsets padding;
	private final QuillLayoutNode child;
	private QuillSize childSize = QuillSize.ZERO;
	private QuillRect contentBounds = new QuillRect(0, 0, 0, 0);

	public QuillCardBox(Component title, QuillLayoutNode child) {
		this(
				title,
				QuillThemeScope.current(),
				QuillInsets.all(QuillSpacing.SPACE_2),
				child
		);
	}

	public QuillCardBox(
			Component title,
			QuillThemeData theme,
			QuillInsets padding,
			QuillLayoutNode child
	) {
		this.title = title == null || title.getString().isBlank() ? null : title;
		this.theme = theme == null ? QuillThemeData.DEFAULT : theme;
		this.padding = padding == null ? QuillInsets.all(0) : padding;
		this.child = child;
	}

	@Override
	public QuillSize measure(QuillBoxConstraints constraints) {
		int header = headerHeight();
		QuillBoxConstraints childConstraints = constraints.deflate(
				padding.horizontal(),
				saturatedAdd(padding.vertical(), header)
		);
		if (child != null) {
			childSize = child.measure(childConstraints);
		}
		else {
			childSize = QuillSize.ZERO;
		}
		return constraints.constrain(QuillSize.of(
				saturatedAdd(childSize.width(), padding.horizontal()),
				saturatedAdd(childSize.height(), saturatedAdd(padding.vertical(), header))
		));
	}

	@Override
	public void arrange(QuillRect bounds) {
		setBounds(bounds);
		contentBounds = new QuillRect(
				bounds.x() + padding.left(),
				bounds.y() + headerHeight() + padding.top(),
				Math.max(0, bounds.width() - padding.horizontal()),
				Math.max(0, bounds.height() - headerHeight() - padding.vertical())
		);
		if (child != null) {
			child.arrange(contentBounds);
		}
	}

	@Override
	public void paint(GuiGraphicsExtractor graphics, Font font) {
		QuillRect bounds = bounds();
		QuillRender.section(graphics, bounds.x(), bounds.y(), bounds.width(), bounds.height(), theme);
		if (title != null) {
			QuillRender.text(
					graphics,
					font,
					title,
					bounds.x() + padding.left(),
					bounds.y() + 5,
					theme.color(QuillThemeData.ColorRole.TEXT_SOFT)
			);
		}
	}

	public QuillRect content() {
		return contentBounds;
	}

	public QuillSize childSize() {
		return childSize;
	}

	public Component title() {
		return title;
	}

	@Override
	public List<QuillLayoutNode> children() {
		return child == null ? List.of() : List.of(child);
	}

	private int headerHeight() {
		return title == null ? 0 : HEADER_HEIGHT;
	}

	private static int saturatedAdd(int left, int right) {
		return (int) Math.min(
				QuillBoxConstraints.UNBOUNDED,
				(long) left + right
		);
	}
}
