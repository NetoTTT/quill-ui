package com.quillui.overlay;

import com.quillui.render.QuillRender;
import com.quillui.theme.QuillThemeData;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.List;
import java.util.function.IntConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

/**
 * A compact anchored popup list. The caller owns the anchor bounds; the popup owns selection,
 * outside-click dismissal and Escape handling.
 */
public final class QuillPopupMenu implements QuillOverlay {
	private final int x;
	private final int y;
	private final int width;
	private final int rowHeight;
	private final List<Component> items;
	private final QuillThemeData theme;
	private final IntConsumer onSelect;
	private final Runnable onClose;
	private int selected;
	private boolean open = true;
	private boolean closeNotified;

	public QuillPopupMenu(
			int x,
			int y,
			int width,
			int rowHeight,
			List<Component> items,
			int selected,
			QuillThemeData theme,
			IntConsumer onSelect,
			Runnable onClose
	) {
		this.x = x;
		this.y = y;
		this.width = Math.max(1, width);
		this.rowHeight = Math.max(12, rowHeight);
		this.items = items == null ? List.of() : List.copyOf(items);
		this.selected = selected;
		this.theme = theme == null ? QuillThemeData.DEFAULT : theme;
		this.onSelect = onSelect;
		this.onClose = onClose;
	}

	public int selected() {
		return selected;
	}

	@Override
	public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		if (!open || items.isEmpty()) {
			return;
		}
		int height = items.size() * rowHeight + 4;
		QuillRender.panel(graphics, x, y, width, height, theme);
		for (int index = 0; index < items.size(); index++) {
			int rowY = y + 2 + index * rowHeight;
			boolean hovered = mouseX >= x && mouseX < x + width
					&& mouseY >= rowY && mouseY < rowY + rowHeight;
			if (index == selected) {
				graphics.fill(x + 1, rowY, x + width - 1, rowY + rowHeight,
						theme.color(QuillThemeData.ColorRole.SELECTED_BG));
			} else if (hovered) {
				graphics.fill(x + 1, rowY, x + width - 1, rowY + rowHeight,
						theme.color(QuillThemeData.ColorRole.BUTTON_HOVER));
			}
			QuillRender.text(
					graphics,
					Minecraft.getInstance().font,
					items.get(index),
					x + 7,
					rowY + (rowHeight - 8) / 2,
					theme.color(QuillThemeData.ColorRole.TEXT)
			);
		}
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (!open) {
			return false;
		}
		if (!contains(event.x(), event.y())) {
			close();
			return true;
		}
		int index = (int) ((event.y() - y - 2) / rowHeight);
		if (index >= 0 && index < items.size()) {
			selected = index;
			if (onSelect != null) {
				onSelect.accept(index);
			}
			close();
		}
		return true;
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (open && event.key() == InputConstants.KEY_ESCAPE) {
			close();
			return true;
		}
		return false;
	}

	@Override
	public boolean isOpen() {
		return open;
	}

	@Override
	public void close() {
		if (!open) {
			return;
		}
		open = false;
		if (!closeNotified && onClose != null) {
			closeNotified = true;
			onClose.run();
		}
	}

	private boolean contains(double mouseX, double mouseY) {
		int height = items.size() * rowHeight + 4;
		return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
	}
}
