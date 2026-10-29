package com.quillui.widget;

import com.quillui.layout.QuillRect;
import com.mojang.blaze3d.platform.InputConstants;
import com.quillui.theme.QuillThemeData;
import java.util.function.IntSupplier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

/**
 * Scrollable viewport for custom content. The renderer draws in content coordinates; the viewport
 * clips and translates it. Clicks are reported in content coordinates so long forms can keep their
 * own hit-testing without registering every row as a widget.
 *
 * @deprecated Use {@link QuillClipView} with a {@code ContentRenderer} plus
 *         {@link QuillScrollBar} for new code. This widget remains for compatibility.
 */
@Deprecated(since = "0.2.0-alpha.7")
public final class QuillScrollView extends AbstractWidget {
	public interface ContentRenderer {
		void render(
				GuiGraphicsExtractor graphics,
				int x,
				int y,
				int width,
				int contentHeight,
				int scrollOffset,
				int contentMouseX,
				int contentMouseY,
				float delta
		);
	}

	public interface ClickHandler {
		boolean onClick(double contentX, double contentY);
	}

	private static final int SCROLLBAR_WIDTH = 4;
	private static final int SCROLLBAR_GAP = 6;
	private static final int SCROLL_STEP = 24;

	private final IntSupplier contentHeight;
	private final ContentRenderer renderer;
	private final ClickHandler clickHandler;
	private final QuillThemeData theme;
	private double scroll;
	private boolean draggingScrollbar;
	private double scrollbarDragOffset;

	public QuillScrollView(
			int x,
			int y,
			int width,
			int height,
			IntSupplier contentHeight,
			ContentRenderer renderer,
			ClickHandler clickHandler,
			QuillThemeData theme
	) {
		super(x, y, width, height, Component.empty());
		this.contentHeight = contentHeight == null ? () -> height : contentHeight;
		this.renderer = renderer;
		this.clickHandler = clickHandler;
		this.theme = theme == null ? QuillThemeData.DEFAULT : theme;
	}

	public int contentHeight() {
		return Math.max(0, contentHeight.getAsInt());
	}

	public int scrollOffset() {
		return Mth.floor(scroll);
	}

	public int viewportWidth() {
		return Math.max(1, getWidth() - (maxScroll() > 0 ? SCROLLBAR_WIDTH + SCROLLBAR_GAP : 0));
	}

	public int maxScroll() {
		return Math.max(0, contentHeight() - getHeight());
	}

	private int scrollbarX() {
		return getX() + getWidth() - SCROLLBAR_WIDTH;
	}

	private int thumbHeight() {
		return Math.max(
				16,
				(int) ((float) getHeight() * getHeight() / Math.max(1, contentHeight()))
		);
	}

	private int thumbY() {
		int travel = getHeight() - thumbHeight();
		if (travel <= 0 || maxScroll() <= 0) {
			return getY();
		}
		return getY() + (int) ((float) travel * scroll / maxScroll());
	}

	private void scrollToThumbY(double thumbTop) {
		int travel = getHeight() - thumbHeight();
		if (travel <= 0) {
			return;
		}
		double ratio = Mth.clamp((thumbTop - getY()) / travel, 0.0D, 1.0D);
		scroll = ratio * maxScroll();
	}

	public void scrollTo(double value) {
		scroll = Mth.clamp(value, 0.0D, maxScroll());
	}

	public int contentScreenY(int contentY) {
		return getY() + contentY - scrollOffset();
	}

	public boolean isContentVisible(QuillRect contentRect) {
		return contentRect.intersects(
				new QuillRect(getX(), getY() + scrollOffset(), viewportWidth(), getHeight())
		);
	}

	@Override
	protected void extractWidgetRenderState(
			GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta
	) {
		int contentHeight = contentHeight();
		scroll = Mth.clamp(scroll, 0.0D, maxScroll());
		int offset = scrollOffset();

		graphics.enableScissor(getX(), getY(), getX() + viewportWidth(), getY() + getHeight());
		graphics.pose().pushMatrix();
		graphics.pose().translate(0.0F, (float) -offset);
		if (renderer != null) {
			renderer.render(
					graphics,
					getX(),
					getY(),
					viewportWidth(),
					contentHeight,
					offset,
					mouseX,
					mouseY + offset,
					delta
			);
		}
		graphics.pose().popMatrix();
		graphics.disableScissor();

		if (maxScroll() > 0) {
			int barX = scrollbarX();
			int thumbHeight = thumbHeight();
			int thumbY = thumbY();
			boolean hovered = mouseX >= barX - 2 && mouseX < barX + SCROLLBAR_WIDTH
					&& mouseY >= getY() && mouseY < getY() + getHeight();
			graphics.fill(
					barX, getY(), barX + SCROLLBAR_WIDTH, getY() + getHeight(),
					theme.color(QuillThemeData.ColorRole.BUTTON_DISABLED)
			);
			graphics.fill(
					barX, thumbY, barX + SCROLLBAR_WIDTH, thumbY + thumbHeight,
					theme.color(hovered
							? QuillThemeData.ColorRole.ACCENT_HOVER
							: QuillThemeData.ColorRole.ACCENT)
			);
		}
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (!active || !visible || !isMouseOver(mouseX, mouseY) || maxScroll() <= 0) {
			return false;
		}
		scroll = Mth.clamp(scroll - scrollY * SCROLL_STEP, 0.0D, maxScroll());
		return true;
	}

	@Override
	public void onClick(MouseButtonEvent event, boolean doubleClick) {
		if (!active) {
			return;
		}
		draggingScrollbar = false;
		double mouseX = event.x();
		double mouseY = event.y();
		if (maxScroll() > 0 && mouseX >= scrollbarX() - 2) {
			int thumbHeight = thumbHeight();
			int thumbY = thumbY();
			if (mouseY >= thumbY && mouseY < thumbY + thumbHeight) {
				scrollbarDragOffset = mouseY - thumbY;
			} else {
				scrollbarDragOffset = thumbHeight / 2.0D;
				scrollToThumbY(mouseY - scrollbarDragOffset);
			}
			draggingScrollbar = true;
			return;
		}
		if (clickHandler == null) {
			return;
		}
		double localX = mouseX - getX();
		double localY = mouseY - getY();
		if (localX < 0 || localX >= viewportWidth() || localY < 0 || localY >= getHeight()) {
			return;
		}
		clickHandler.onClick(mouseX, mouseY + scrollOffset());
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
		if (!active || !visible || !draggingScrollbar) {
			return false;
		}
		if (event.button() != InputConstants.MOUSE_BUTTON_LEFT) {
			return false;
		}
		scrollToThumbY(event.y() - scrollbarDragOffset);
		return true;
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		if (!draggingScrollbar) {
			return false;
		}
		draggingScrollbar = false;
		return true;
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {
		defaultButtonNarrationText(output);
	}
}
