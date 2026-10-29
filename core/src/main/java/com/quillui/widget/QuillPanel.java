package com.quillui.widget;

import com.quillui.layout.QuillColumn;
import com.quillui.layout.QuillRect;
import com.quillui.theme.QuillThemeData;
import com.quillui.theme.QuillThemeScope;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

/**
 * Composes controls inside a {@link QuillCard}. The panel positions children in a vertical
 * column; use {@link #addRow(AbstractWidget, AbstractWidget)} for a two-column action row.
 *
 * <p>The panel is a layout helper, not an input widget. Register it with
 * {@code panel.addTo(this::addRenderableWidget)} and render it before the widgets.</p>
 */
public final class QuillPanel {
	private final QuillCard card;
	private final QuillColumn column;
	private final List<AbstractWidget> children = new ArrayList<>();
	private final QuillThemeData theme;
	private final int gap;

	public QuillPanel(int x, int y, int width, int height, Component title) {
		this(x, y, width, height, title, QuillThemeScope.current());
	}

	public QuillPanel(
			int x, int y, int width, int height, Component title, QuillThemeData theme
	) {
		this(x, y, width, height, title, theme, QuillThemeScope.current().metrics().rowGap());
	}

	public QuillPanel(
			int x,
			int y,
			int width,
			int height,
			Component title,
			QuillThemeData theme,
			int gap
	) {
		this.card = new QuillCard(x, y, width, height, title, theme);
		this.theme = theme;
		this.gap = gap;
		QuillRect content = card.content();
		this.column = new QuillColumn(content.x(), content.y(), content.width(), gap);
	}

	public QuillCard card() {
		return card;
	}

	public QuillRect content() {
		return card.content();
	}

	public List<AbstractWidget> children() {
		return Collections.unmodifiableList(children);
	}

	/**
	 * Adds a widget at full content width in the next vertical slot.
	 */
	public <T extends AbstractWidget> T add(T widget) {
		QuillRect slot = column.next(widget.getHeight());
		place(widget, slot.x(), slot.y(), slot.width(), widget.getHeight());
		return widget;
	}

	/**
	 * Adds two widgets side by side in the next vertical slot.
	 */
	public void addRow(AbstractWidget left, AbstractWidget right) {
		QuillRect row = column.next(Math.max(left.getHeight(), right.getHeight()));
		int leftWidth = Math.max(1, (row.width() - gap) / 2);
		int rightWidth = Math.max(1, row.width() - leftWidth - gap);
		place(left, row.x(), row.y(), leftWidth, left.getHeight());
		place(right, row.x() + leftWidth + gap, row.y(), rightWidth, right.getHeight());
	}

	public void addTo(Consumer<AbstractWidget> consumer) {
		children.forEach(consumer);
	}

	public void render(GuiGraphicsExtractor graphics, Font font) {
		card.render(graphics, font);
	}

	private void place(AbstractWidget widget, int x, int y, int width, int height) {
		widget.setX(x);
		widget.setY(y);
		widget.setWidth(width);
		widget.setHeight(height);
		children.add(widget);
	}
}
