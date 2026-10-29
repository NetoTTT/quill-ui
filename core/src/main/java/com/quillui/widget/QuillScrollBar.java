package com.quillui.widget;

import com.quillui.layout.QuillRect;
import com.quillui.render.QuillRender;
import com.quillui.theme.QuillThemeData;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

/**
 * Thin scrollbar for a measured scroll viewport. The owner supplies the current offset and the
 * callback that re-arranges the content; the bar only handles painting and thumb dragging.
 */
public final class QuillScrollBar extends AbstractWidget {
	private static final int WIDTH = 4;
	private static final int MIN_THUMB = 16;

	private final IntSupplier maxScroll;
	private final IntSupplier offset;
	private final IntConsumer onScroll;
	private final QuillThemeData theme;
	private boolean dragging;
	private double dragOffset;

	public QuillScrollBar(
			QuillRect viewport,
			IntSupplier maxScroll,
			IntSupplier offset,
			IntConsumer onScroll,
			QuillThemeData theme
	) {
		super(
				viewport.right() - WIDTH,
				viewport.y(),
				WIDTH,
				viewport.height(),
				Component.empty()
		);
		this.maxScroll = maxScroll == null ? () -> 0 : maxScroll;
		this.offset = offset == null ? () -> 0 : offset;
		this.onScroll = onScroll;
		this.theme = theme == null ? QuillThemeData.DEFAULT : theme;
	}

	public QuillScrollBar visibleWhenNeeded() {
		boolean needed = maxScroll.getAsInt() > 0;
		visible = needed;
		active = needed;
		return this;
	}

	private int thumbHeight() {
		int max = maxScroll.getAsInt();
		if (max <= 0) {
			return 0;
		}
		return Math.max(MIN_THUMB, (int) ((float) getHeight() * getHeight() / (getHeight() + max)));
	}

	private int thumbY() {
		int max = maxScroll.getAsInt();
		int thumbHeight = thumbHeight();
		int travel = getHeight() - thumbHeight;
		if (max <= 0 || travel <= 0) {
			return getY();
		}
		int current = Mth.clamp(offset.getAsInt(), 0, max);
		return getY() + (int) ((float) travel * current / max);
	}

	private void scrollToThumbY(double thumbTop) {
		int max = maxScroll.getAsInt();
		int travel = getHeight() - thumbHeight();
		if (max <= 0 || travel <= 0) {
			return;
		}
		double ratio = Mth.clamp((thumbTop - getY()) / travel, 0.0D, 1.0D);
		if (onScroll != null) {
			onScroll.accept((int) Math.round(ratio * max));
		}
	}

	@Override
	protected void extractWidgetRenderState(
			GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta
	) {
		if (maxScroll.getAsInt() <= 0) {
			return;
		}
		int trackColor = theme.color(QuillThemeData.ColorRole.BUTTON_DISABLED);
		int thumbColor = theme.color(QuillThemeData.ColorRole.ACCENT);
		int thumbHeight = thumbHeight();
		int thumbY = thumbY();
		boolean hovered = isMouseOver(mouseX, mouseY);
		graphics.fill(getX(), getY(), getX() + WIDTH, getY() + getHeight(), trackColor);
		graphics.fill(
				getX(),
				thumbY,
				getX() + WIDTH,
				thumbY + thumbHeight,
				hovered
						? theme.color(QuillThemeData.ColorRole.ACCENT_HOVER)
						: thumbColor
		);
	}

	@Override
	public void onClick(MouseButtonEvent event, boolean doubleClick) {
		dragging = false;
		if (maxScroll.getAsInt() <= 0) {
			return;
		}
		int thumbHeight = thumbHeight();
		int thumbY = thumbY();
		if (event.y() >= thumbY && event.y() < thumbY + thumbHeight) {
			dragOffset = event.y() - thumbY;
		} else {
			dragOffset = thumbHeight / 2.0D;
			scrollToThumbY(event.y() - dragOffset);
		}
		dragging = true;
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
		if (!dragging || event.button() != InputConstants.MOUSE_BUTTON_LEFT) {
			return false;
		}
		scrollToThumbY(event.y() - dragOffset);
		return true;
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		if (!dragging) {
			return false;
		}
		dragging = false;
		return true;
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {
		defaultButtonNarrationText(output);
	}
}
