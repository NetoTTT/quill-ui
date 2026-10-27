package com.quillui.widget;

import com.quillui.render.QuillRender;
import com.quillui.theme.QuillThemeData;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

/**
 * Horizontal value slider in the 0..1 range. It is intentionally value-only: callers decide
 * whether the value is a percentage, volume, radius or any other domain value.
 */
public final class QuillSlider extends AbstractWidget {
	private final QuillThemeData theme;
	private final Consumer<Float> onChange;
	private float value;
	private float step;
	private boolean dragging;

	public QuillSlider(
			int x,
			int y,
			int width,
			int height,
			Component message,
			float value,
			Consumer<Float> onChange,
			QuillThemeData theme
	) {
		super(x, y, width, height, message);
		this.theme = theme == null ? QuillThemeData.DEFAULT : theme;
		this.onChange = onChange;
		this.value = clamp(value);
	}

	public float value() {
		return value;
	}

	public QuillSlider setValue(float value) {
		this.value = snap(clamp(value));
		return this;
	}

	public QuillSlider step(float step) {
		this.step = Math.max(0.0F, Math.min(1.0F, step));
		this.value = snap(this.value);
		return this;
	}

	@Override
	protected void extractWidgetRenderState(
			GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta
	) {
		int x = getX();
		int y = getY();
		int trackLeft = x + 6;
		int trackRight = x + getWidth() - 6;
		int trackY = y + getHeight() / 2 - 1;
		int accent = theme.color(QuillThemeData.ColorRole.ACCENT);
		int border = active
				? (isFocused()
						? theme.color(QuillThemeData.ColorRole.FOCUS_RING)
						: theme.color(QuillThemeData.ColorRole.BUTTON_BORDER))
				: theme.color(QuillThemeData.ColorRole.BUTTON_DISABLED_BORDER);

		graphics.fill(trackLeft, trackY, trackRight, trackY + 2,
				theme.color(QuillThemeData.ColorRole.BUTTON_BG));
		int filled = trackLeft + Math.round((trackRight - trackLeft) * value);
		graphics.fill(trackLeft, trackY, filled, trackY + 2, accent);

		int knobCenter = filled;
		int knobX = knobCenter - 3;
		int knobY = y + getHeight() / 2 - 5;
		graphics.fill(knobX, knobY, knobX + 7, knobY + 10,
				active ? theme.color(QuillThemeData.ColorRole.TITLE)
						: theme.color(QuillThemeData.ColorRole.BUTTON_DISABLED_TEXT));
		QuillRender.border(graphics, knobX, knobY, 7, 10, border);

		if (active) {
			Minecraft client = Minecraft.getInstance();
			QuillRender.text(
					graphics,
					client.font,
					getMessage(),
					x,
					y - 10,
					theme.color(QuillThemeData.ColorRole.TEXT_SOFT)
			);
		}
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (!active || !isMouseOver(event.x(), event.y())
				|| event.button() != InputConstants.MOUSE_BUTTON_LEFT) {
			return false;
		}
		dragging = true;
		updateFromMouse(event.x());
		return true;
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
		if (!dragging) {
			return false;
		}
		updateFromMouse(event.x());
		return true;
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		if (!dragging) {
			return false;
		}
		updateFromMouse(event.x());
		dragging = false;
		return true;
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (!active) {
			return false;
		}
		float increment = step > 0.0F ? step : 0.05F;
		if (event.key() == InputConstants.KEY_LEFT) {
			setValue(value - increment);
			notifyChange();
			return true;
		}
		if (event.key() == InputConstants.KEY_RIGHT) {
			setValue(value + increment);
			notifyChange();
			return true;
		}
		return false;
	}

	private void updateFromMouse(double mouseX) {
		int trackLeft = getX() + 6;
		int trackRight = getX() + getWidth() - 6;
		float next = (float) ((mouseX - trackLeft) / Math.max(1.0D, trackRight - trackLeft));
		setValue(next);
		notifyChange();
	}

	private void notifyChange() {
		if (onChange != null) {
			onChange.accept(value);
		}
	}

	private float snap(float value) {
		if (step <= 0.0F) {
			return value;
		}
		return Math.round(value / step) * step;
	}

	private static float clamp(float value) {
		return Math.max(0.0F, Math.min(1.0F, value));
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {
		defaultButtonNarrationText(output);
	}
}
