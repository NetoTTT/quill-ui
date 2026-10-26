package com.quillui.widget;

import com.quillui.QuillRender;
import com.quillui.QuillTheme;
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
 * A compact on/off control with a label on the left and a switch on the right. The widget only
 * changes its local display state; the caller applies the change through an action sink or payload.
 */
public class QuillToggle extends AbstractWidget {
	private static final int TRACK_WIDTH = 26;
	private static final int TRACK_HEIGHT = 12;
	private static final int KNOB_SIZE = 8;
	private static final int KNOB_INSET = 2;

	private final Consumer<Boolean> onChange;
	private boolean value;

	public QuillToggle(
			int x,
			int y,
			int width,
			int height,
			Component message,
			boolean value,
			Consumer<Boolean> onChange
	) {
		super(x, y, width, height, message);
		this.value = value;
		this.onChange = onChange;
	}

	public boolean value() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	@Override
	protected void extractWidgetRenderState(
			GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta
	) {
		int x = getX();
		int y = getY();
		boolean hovered = isMouseOver(mouseX, mouseY) && active;

		int trackX = x + width - TRACK_WIDTH;
		int trackY = y + (height - TRACK_HEIGHT) / 2;
		int trackBackground;
		int trackBorder;

		if (!active) {
			trackBackground = QuillTheme.BUTTON_DISABLED;
			trackBorder = QuillTheme.BUTTON_DISABLED_BORDER;
		} else if (value) {
			trackBackground = hovered ? QuillTheme.ACCENT_HOVER : QuillTheme.ACCENT;
			trackBorder = QuillTheme.ACCENT_HOVER;
		} else {
			trackBackground = hovered ? QuillTheme.BUTTON_HOVER : QuillTheme.BUTTON_BG;
			trackBorder = QuillTheme.BUTTON_BORDER;
		}

		graphics.fill(trackX, trackY, trackX + TRACK_WIDTH, trackY + TRACK_HEIGHT, trackBackground);
		QuillRender.border(graphics, trackX, trackY, TRACK_WIDTH, TRACK_HEIGHT, trackBorder);

		int knobX = value
				? trackX + TRACK_WIDTH - KNOB_SIZE - KNOB_INSET
				: trackX + KNOB_INSET;
		int knobColor = active ? QuillTheme.TITLE : QuillTheme.BUTTON_DISABLED_TEXT;
		graphics.fill(
				knobX,
				trackY + KNOB_INSET,
				knobX + KNOB_SIZE,
				trackY + TRACK_HEIGHT - KNOB_INSET,
				knobColor
		);

		if (active && isFocused()) {
			QuillRender.border(
					graphics,
					trackX - 1,
					trackY - 1,
					TRACK_WIDTH + 2,
					TRACK_HEIGHT + 2,
					QuillTheme.ACCENT
			);
		}

		int textColor = active ? QuillTheme.TEXT : QuillTheme.BUTTON_DISABLED_TEXT;
		graphics.text(
				Minecraft.getInstance().font,
				getMessage(),
				x,
				y + (height - 8) / 2,
				textColor
		);
	}

	@Override
	public void onClick(MouseButtonEvent event, boolean doubleClick) {
		if (!active) {
			return;
		}

		value = !value;
		playDownSound(Minecraft.getInstance().getSoundManager());
		onChange.accept(value);
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (!active) {
			return false;
		}

		int key = event.key();
		if (key == InputConstants.KEY_RETURN || key == InputConstants.KEY_SPACE) {
			value = !value;
			playDownSound(Minecraft.getInstance().getSoundManager());
			onChange.accept(value);
			return true;
		}

		return false;
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {
		defaultButtonNarrationText(output);
	}
}
