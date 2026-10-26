package com.quillui.widget;

import com.quillui.QuillRender;
import com.quillui.QuillTheme;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;

/**
 * A themed button with four visual variants, a selected state and optional action tooltip.
 * This intentionally does not use the vanilla button texture, so UIs keep one consistent look.
 */
public class QuillButton extends AbstractWidget {
	public enum Variant {
		PRIMARY,
		SECONDARY,
		DANGER,
		GHOST
	}

	private final Variant variant;
	private final Runnable action;
	private boolean selected;
	private Tooltip tooltip;
	private SoundEvent clickSound;

	public QuillButton(
			int x, int y, int width, int height, Component message, Variant variant, Runnable action
	) {
		super(x, y, width, height, message);
		this.variant = variant;
		this.action = action;
	}

	public QuillButton selected(boolean selected) {
		this.selected = selected;
		return this;
	}

	public QuillButton tip(Tooltip tooltip) {
		this.tooltip = tooltip;
		return this;
	}

	public QuillButton clickSound(SoundEvent sound) {
		this.clickSound = sound;
		return this;
	}

	@Override
	protected void extractWidgetRenderState(
			GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta
	) {
		int x = getX();
		int y = getY();
		boolean hovered = isMouseOver(mouseX, mouseY) && active;

		int background;
		int border;
		int textColor;

		if (!active) {
			background = QuillTheme.BUTTON_DISABLED;
			border = QuillTheme.BUTTON_DISABLED_BORDER;
			textColor = QuillTheme.BUTTON_DISABLED_TEXT;
		} else {
			switch (variant) {
				case PRIMARY -> {
					background = hovered ? QuillTheme.PRIMARY_BG_HOVER : QuillTheme.PRIMARY_BG;
					border = hovered ? QuillTheme.ACCENT_HOVER : QuillTheme.ACCENT;
					textColor = hovered ? QuillTheme.ACCENT_HOVER : QuillTheme.ACCENT;
				}
				case DANGER -> {
					background = hovered ? QuillTheme.DANGER_BG : QuillTheme.BUTTON_BG;
					border = QuillTheme.DANGER;
					textColor = QuillTheme.DANGER;
				}
				case GHOST -> {
					background = hovered || isFocused() ? QuillTheme.BUTTON_BG : 0;
					border = isFocused() ? QuillTheme.ACCENT : 0;
					textColor = hovered ? QuillTheme.TITLE : QuillTheme.MUTED;
				}
				default -> {
					background = hovered ? QuillTheme.BUTTON_HOVER : QuillTheme.BUTTON_BG;
					border = QuillTheme.BUTTON_BORDER;
					textColor = QuillTheme.TEXT;
				}
			}

			if (selected) {
				background = QuillTheme.SELECTED_BG;
				border = QuillTheme.ACCENT;
				textColor = QuillTheme.TITLE;
			}
		}

		if (background != 0) {
			graphics.fill(x, y, x + width, y + height, background);
		}

		if (border != 0) {
			QuillRender.border(graphics, x, y, width, height, border);
		}

		if (active && isFocused()) {
			QuillRender.border(graphics, x - 1, y - 1, width + 2, height + 2, QuillTheme.ACCENT);
		}

		if (selected) {
			graphics.fill(x, y, x + 2, y + height, QuillTheme.ACCENT);
		}

		graphics.centeredText(
				Minecraft.getInstance().font,
				getMessage(),
				x + width / 2,
				y + (height - 8) / 2 + 1,
				textColor
		);

		if (tooltip != null && isMouseOver(mouseX, mouseY)) {
			graphics.setTooltipForNextFrame(
					Minecraft.getInstance().font,
					tooltip.toCharSequence(Minecraft.getInstance()),
					mouseX,
					mouseY
			);
		}
	}

	@Override
	public void onClick(MouseButtonEvent event, boolean doubleClick) {
		if (!active) {
			return;
		}
		playClickSound();
		action.run();
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		boolean handled = super.mouseClicked(event, doubleClick);
		if (handled) {
			setFocused(true);
		}
		return handled;
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (!active) {
			return false;
		}

		int key = event.key();
		if (key == InputConstants.KEY_RETURN || key == InputConstants.KEY_SPACE) {
			playClickSound();
			action.run();
			return true;
		}

		return false;
	}

	private void playClickSound() {
		Minecraft client = Minecraft.getInstance();
		if (clickSound == null) {
			playDownSound(client.getSoundManager());
			return;
		}
		client.getSoundManager().play(SimpleSoundInstance.forUI(clickSound, 1.0F));
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {
		defaultButtonNarrationText(output);
	}
}
