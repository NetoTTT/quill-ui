package com.quillui.widget;

import com.quillui.layout.QuillBoxConstraints;
import com.quillui.layout.QuillInsets;
import com.quillui.layout.QuillLayoutNode;
import com.quillui.layout.QuillRect;
import com.quillui.render.QuillPaintable;
import com.quillui.render.QuillRender;
import com.quillui.theme.QuillThemeData;
import com.quillui.theme.QuillThemeScope;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.InputConstants;

/**
 * Virtualized-looking list whose rows are measured layout trees. Each row can contain real
 * widgets; the list paints the row surface, clips, scrolls and routes input to the widgets.
 */
public final class QuillListView<T> extends AbstractWidget {
	public interface RowBuilder<T> {
		QuillLayoutNode build(T item, int index);
	}

	public interface ClickHandler<T> {
		void onClick(T item, int index);
	}

	private static final int SCROLLBAR_WIDTH = 4;
	private static final int SCROLLBAR_GAP = 4;

	private record Row(
			QuillLayoutNode root,
			List<AbstractWidget> widgets,
			List<QuillPaintable> paintables
	) {
	}

	private final List<T> items = new ArrayList<>();
	private final List<Row> rows = new ArrayList<>();
	private final RowBuilder<T> rowBuilder;
	private final ClickHandler<T> clickHandler;
	private final QuillThemeData theme;
	private final int rowHeight;
	private QuillInsets padding = QuillInsets.symmetric(0, 10);
	private double scroll;
	private boolean dirty = true;
	private int builtWidth = -1;
	private T selectedItem;
	private AbstractWidget focusedChild;
	private boolean draggingScrollbar;
	private double scrollbarDragOffset;

	public QuillListView(
			int x,
			int y,
			int width,
			int height,
			int rowHeight,
			RowBuilder<T> rowBuilder,
			ClickHandler<T> clickHandler
	) {
		this(x, y, width, height, rowHeight, rowBuilder, clickHandler, QuillThemeScope.current());
	}

	public QuillListView(
			int x,
			int y,
			int width,
			int height,
			int rowHeight,
			RowBuilder<T> rowBuilder,
			ClickHandler<T> clickHandler,
			QuillThemeData theme
	) {
		super(x, y, width, height, Component.empty());
		if (rowBuilder == null) {
			throw new IllegalArgumentException("rowBuilder must not be null");
		}
		this.rowHeight = Math.max(1, rowHeight);
		this.rowBuilder = rowBuilder;
		this.clickHandler = clickHandler;
		this.theme = theme == null ? QuillThemeData.DEFAULT : theme;
	}

	public void replaceItems(List<T> newItems) {
		items.clear();
		items.addAll(newItems);
		rows.clear();
		dirty = true;
		scroll = 0.0D;
		focusChild(null);
		if (selectedItem != null && !items.contains(selectedItem)) {
			selectedItem = null;
		}
	}

	public int size() {
		return items.size();
	}

	public T get(int index) {
		return items.get(index);
	}

	public void setSelected(T item) {
		selectedItem = item;
	}

	public T selectedItem() {
		return selectedItem;
	}

	public boolean isSelected(T item) {
		return Objects.equals(selectedItem, item);
	}

	public int rowHeight() {
		return rowHeight;
	}

	/** Horizontal/vertical inset between the row surface and its content. */
	public QuillListView<T> padding(QuillInsets padding) {
		this.padding = padding == null ? QuillInsets.all(0) : padding;
		this.dirty = true;
		return this;
	}

	public int scrollOffset() {
		return Mth.floor(scroll);
	}

	public void setScrollOffset(int offset) {
		scroll = Mth.clamp(offset, 0, maxScroll());
	}

	public int rowWidth() {
		return Math.max(1, listWidth() - padding.horizontal());
	}

	public int rowTop(int index) {
		return getY() + index * rowHeight - Mth.floor(scroll);
	}

	public int rowAt(double mouseX, double mouseY) {
		return indexAt(mouseX, mouseY + Mth.floor(scroll));
	}

	private int indexAt(double mouseX, double contentY) {
		if (!active || !visible
				|| mouseX < getX() || mouseX >= getX() + listWidth()
				|| contentY < getY() || contentY >= getY() + height) {
			return -1;
		}
		int index = (int) ((contentY - getY()) / rowHeight);
		return index >= 0 && index < items.size() ? index : -1;
	}

	private int contentHeight() {
		return items.size() * rowHeight;
	}

	private int maxScroll() {
		return Math.max(0, contentHeight() - getHeight());
	}

	private int scrollbarX() {
		return getX() + getWidth() - SCROLLBAR_WIDTH;
	}

	private int listWidth() {
		return Math.max(1, getWidth() - (maxScroll() > 0 ? SCROLLBAR_WIDTH + SCROLLBAR_GAP : 0));
	}

	private int scrollbarThumbHeight() {
		if (maxScroll() <= 0) {
			return 0;
		}
		return Math.max(16, (int) ((float) getHeight() * getHeight() / contentHeight()));
	}

	private int scrollbarThumbY() {
		int thumbHeight = scrollbarThumbHeight();
		if (thumbHeight <= 0) {
			return getY();
		}
		int travel = getHeight() - thumbHeight;
		return getY() + (int) ((float) travel * (float) scroll / maxScroll());
	}

	private void scrollToThumbY(double thumbTop) {
		int thumbHeight = scrollbarThumbHeight();
		int travel = getHeight() - thumbHeight;
		if (travel <= 0) {
			return;
		}
		double ratio = Mth.clamp((thumbTop - getY()) / travel, 0.0D, 1.0D);
		scroll = ratio * maxScroll();
	}

	private void ensureRows() {
		int width = rowWidth();
		if (!dirty && width == builtWidth) {
			return;
		}

		scroll = Mth.clamp(scroll, 0, maxScroll());
		rows.clear();
		focusChild(null);
		int contentHeight = Math.max(1, rowHeight - padding.vertical());
		for (int index = 0; index < items.size(); index++) {
			int rowIndex = index;
			QuillLayoutNode root = QuillThemeScope.supply(
					theme,
					() -> rowBuilder.build(items.get(rowIndex), rowIndex)
			);
			root.measure(QuillBoxConstraints.tightFor(width, contentHeight));
			root.arrange(new QuillRect(
					getX() + padding.left(),
					getY() + index * rowHeight + padding.top(),
					width,
					contentHeight
			));
			List<AbstractWidget> widgets = new ArrayList<>();
			List<QuillPaintable> paintables = new ArrayList<>();
			collect(root, widgets, paintables);
			rows.add(new Row(root, widgets, paintables));
		}
		dirty = false;
		builtWidth = width;
	}

	private static void collect(
			QuillLayoutNode node,
			List<AbstractWidget> widgets,
			List<QuillPaintable> paintables
	) {
		if (node instanceof QuillWidgetBox box) {
			widgets.add(box.widget());
		}
		if (node instanceof QuillPaintable paintable) {
			paintables.add(paintable);
		}
		for (QuillLayoutNode child : node.children()) {
			collect(child, widgets, paintables);
		}
	}

	@Override
	protected void extractWidgetRenderState(
			GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta
	) {
		ensureRows();
		int offset = Mth.floor(scroll);
		int contentMouseY = mouseY + offset;
		int hovered = indexAt(mouseX, contentMouseY);

		graphics.enableScissor(getX(), getY(), getX() + getWidth(), getY() + getHeight());
		graphics.pose().pushMatrix();
		graphics.pose().translate(0.0F, (float) -offset);
		QuillMouseContext.push(0, offset);
		try {
			for (int index = 0; index < rows.size(); index++) {
				int rowY = getY() + index * rowHeight;
				if (rowY + rowHeight <= getY() + offset
						|| rowY >= getY() + offset + getHeight()) {
					continue;
				}

				boolean selected = isSelected(items.get(index));
				QuillRender.box(
						graphics,
						getX(),
						rowY,
						listWidth(),
						rowHeight - 2,
						theme.color(selected
								? QuillThemeData.ColorRole.SELECTED_BG
								: index == hovered
										? QuillThemeData.ColorRole.BUTTON_HOVER
										: QuillThemeData.ColorRole.BUTTON_BG),
						theme.color(selected
								? QuillThemeData.ColorRole.ACCENT
								: QuillThemeData.ColorRole.BUTTON_BORDER)
				);

				Row row = rows.get(index);
				for (QuillPaintable paintable : row.paintables()) {
					paintable.paint(graphics, Minecraft.getInstance().font);
				}
				for (AbstractWidget widget : row.widgets()) {
					if (widget.visible) {
						widget.extractRenderState(graphics, mouseX, contentMouseY, delta);
					}
				}
			}
		}
		finally {
			QuillMouseContext.pop();
		}
		graphics.pose().popMatrix();
		graphics.disableScissor();

		if (maxScroll() > 0) {
			int barX = scrollbarX();
			graphics.fill(
					barX, getY(), barX + SCROLLBAR_WIDTH, getY() + getHeight(),
					theme.color(QuillThemeData.ColorRole.BUTTON_DISABLED)
			);
			graphics.fill(
					barX, scrollbarThumbY(), barX + SCROLLBAR_WIDTH,
					scrollbarThumbY() + scrollbarThumbHeight(),
					theme.color(QuillThemeData.ColorRole.ACCENT)
			);
		}
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (!active || !visible || !isMouseOver(mouseX, mouseY) || maxScroll() <= 0) {
			return false;
		}
		scroll = Mth.clamp(scroll - scrollY * rowHeight, 0.0D, maxScroll());
		return true;
	}

	@Override
	public void onClick(MouseButtonEvent event, boolean doubleClick) {
		if (!active) {
			return;
		}

		double mouseX = event.x();
		double mouseY = event.y();
		if (maxScroll() > 0 && mouseX >= scrollbarX() - 2.0D) {
			int thumbHeight = scrollbarThumbHeight();
			int thumbY = scrollbarThumbY();
			if (mouseY >= thumbY && mouseY < thumbY + thumbHeight) {
				scrollbarDragOffset = mouseY - thumbY;
			}
			else {
				scrollbarDragOffset = thumbHeight / 2.0D;
				scrollToThumbY(mouseY - scrollbarDragOffset);
			}
			draggingScrollbar = true;
			return;
		}

		ensureRows();
		int offset = Mth.floor(scroll);
		int contentY = (int) mouseY + offset;
		int index = indexAt(mouseX, contentY);
		if (index < 0) {
			focusChild(null);
			return;
		}

		MouseButtonEvent mapped = new MouseButtonEvent(mouseX, contentY, event.buttonInfo());
		Row row = rows.get(index);
		for (int widgetIndex = row.widgets().size() - 1; widgetIndex >= 0; widgetIndex--) {
			AbstractWidget widget = row.widgets().get(widgetIndex);
			if (!widget.visible || !widget.active) {
				continue;
			}
			if (widget.isMouseOver(mouseX, contentY) && widget.mouseClicked(mapped, doubleClick)) {
				if (widget.capturesInput()) {
					focusChild(widget);
				}
				else {
					widget.setFocused(false);
					focusChild(null);
				}
				return;
			}
		}

		playDownSound(Minecraft.getInstance().getSoundManager());
		selectedItem = items.get(index);
		focusChild(null);
		if (clickHandler != null) {
			clickHandler.onClick(items.get(index), index);
		}
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		if (draggingScrollbar) {
			draggingScrollbar = false;
			return true;
		}
		if (focusedChild == null) {
			return false;
		}
		return focusedChild.mouseReleased(map(event));
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
		if (draggingScrollbar) {
			scrollToThumbY(event.y() - scrollbarDragOffset);
			return true;
		}
		if (focusedChild == null) {
			return false;
		}
		return focusedChild.mouseDragged(map(event), deltaX, deltaY);
	}

	private MouseButtonEvent map(MouseButtonEvent event) {
		return new MouseButtonEvent(
				event.x(),
				event.y() + Mth.floor(scroll),
				event.buttonInfo()
		);
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (event.key() == InputConstants.KEY_TAB) {
			boolean shift = InputConstants.isKeyDown(InputConstants.KEY_LSHIFT)
					|| InputConstants.isKeyDown(InputConstants.KEY_RSHIFT);
			return moveFocus(shift ? -1 : 1);
		}
		return focusedChild != null && focusedChild.keyPressed(event);
	}

	@Override
	public boolean charTyped(CharacterEvent event) {
		return focusedChild != null && focusedChild.charTyped(event);
	}

	@Override
	public boolean capturesInput() {
		return focusedChild != null && focusedChild.capturesInput();
	}

	@Override
	public void setFocused(boolean focused) {
		super.setFocused(focused);
		if (!focused) {
			focusChild(null);
		}
		else if (focusedChild == null) {
			moveFocus(1);
		}
	}

	private void focusChild(AbstractWidget child) {
		if (focusedChild == child) {
			return;
		}
		if (focusedChild != null) {
			focusedChild.setFocused(false);
		}
		focusedChild = child;
		if (focusedChild != null) {
			focusedChild.setFocused(true);
		}
	}

	private boolean moveFocus(int direction) {
		List<AbstractWidget> focusable = new ArrayList<>();
		for (Row row : rows) {
			for (AbstractWidget widget : row.widgets()) {
				if (widget.visible && widget.active) {
					focusable.add(widget);
				}
			}
		}
		if (focusable.isEmpty()) {
			return false;
		}

		int current = focusedChild == null ? -1 : focusable.indexOf(focusedChild);
		for (int step = 1; step <= focusable.size(); step++) {
			int index = Math.floorMod(current + direction * step, focusable.size());
			AbstractWidget candidate = focusable.get(index);
			focusChild(candidate);
			ensureVisible(candidate);
			return true;
		}
		return false;
	}

	private void ensureVisible(AbstractWidget child) {
		int offset = Mth.floor(scroll);
		int viewTop = getY();
		int viewBottom = getY() + getHeight();
		int childTop = child.getY();
		int childBottom = child.getY() + child.getHeight();
		int next = offset;
		if (childTop < viewTop + offset) {
			next = childTop - viewTop;
		}
		else if (childBottom > viewBottom + offset) {
			next = childBottom - viewBottom;
		}
		if (next != offset) {
			scroll = Mth.clamp(next, 0, maxScroll());
		}
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {
		defaultButtonNarrationText(output);
	}
}
