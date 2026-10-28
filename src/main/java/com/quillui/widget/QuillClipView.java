package com.quillui.widget;

import com.quillui.layout.QuillRect;
import com.quillui.theme.QuillThemeData;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

/**
 * A widget host that clips its children to a viewport with a scissor and maps input through a
 * scroll offset. Children keep their content coordinates; the host translates rendering and mouse
 * input, so partially visible widgets are clipped instead of disappearing entirely.
 */
public final class QuillClipView extends AbstractWidget {
	private static final int SCROLL_STEP = 24;
	private static final int SCROLLBAR_HIT_WIDTH = 6;

	private final List<AbstractWidget> children = new ArrayList<>();
	private final IntSupplier maxScroll;
	private final IntSupplier offset;
	private final IntConsumer onScroll;
	private final QuillThemeData theme;
	private AbstractWidget focusedChild;

	public QuillClipView(
			QuillRect viewport,
			IntSupplier maxScroll,
			IntSupplier offset,
			IntConsumer onScroll,
			QuillThemeData theme
	) {
		super(viewport.x(), viewport.y(), viewport.width(), viewport.height(), Component.empty());
		this.maxScroll = maxScroll == null ? () -> 0 : maxScroll;
		this.offset = offset == null ? () -> 0 : offset;
		this.onScroll = onScroll;
		this.theme = theme == null ? QuillThemeData.DEFAULT : theme;
	}

	public <T extends AbstractWidget> T add(T widget) {
		children.add(widget);
		return widget;
	}

	public List<AbstractWidget> children() {
		return List.copyOf(children);
	}

	private int currentOffset() {
		return Math.max(0, Math.min(offset.getAsInt(), maxScroll.getAsInt()));
	}

	private MouseButtonEvent map(MouseButtonEvent event) {
		return new MouseButtonEvent(
				event.x(),
				event.y() + currentOffset(),
				event.buttonInfo()
		);
	}

	@Override
	protected void extractWidgetRenderState(
			GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta
	) {
		int scroll = currentOffset();
		graphics.enableScissor(getX(), getY(), getX() + getWidth(), getY() + getHeight());
		graphics.pose().pushMatrix();
		graphics.pose().translate(0.0F, (float) -scroll);
		for (AbstractWidget child : children) {
			if (child.visible) {
				child.extractRenderState(graphics, mouseX, mouseY + scroll, delta);
			}
		}
		graphics.pose().popMatrix();
		graphics.disableScissor();
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return super.isMouseOver(mouseX, mouseY)
				&& mouseX < getX() + getWidth() - SCROLLBAR_HIT_WIDTH;
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (!active || !visible || !isMouseOver(event.x(), event.y())) {
			return false;
		}
		MouseButtonEvent mapped = map(event);
		for (int index = children.size() - 1; index >= 0; index--) {
			AbstractWidget child = children.get(index);
			if (child.visible && child.mouseClicked(mapped, doubleClick)) {
				focusChild(child);
				return true;
			}
		}
		focusChild(null);
		return true;
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

	@Override
	public void setFocused(boolean focused) {
		super.setFocused(focused);
		if (!focused) {
			focusChild(null);
		}
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		if (focusedChild == null) {
			return false;
		}
		boolean handled = focusedChild.mouseReleased(map(event));
		return handled;
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
		if (focusedChild == null) {
			return false;
		}
		return focusedChild.mouseDragged(map(event), deltaX, deltaY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (!active || !visible || !isMouseOver(mouseX, mouseY) || maxScroll.getAsInt() <= 0) {
			return false;
		}
		int next = (int) Math.round(currentOffset() - scrollY * SCROLL_STEP);
		if (onScroll != null) {
			onScroll.accept(Mth.clamp(next, 0, maxScroll.getAsInt()));
		}
		return true;
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		return focusedChild != null && focusedChild.keyPressed(event);
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {
		defaultButtonNarrationText(output);
	}
}
