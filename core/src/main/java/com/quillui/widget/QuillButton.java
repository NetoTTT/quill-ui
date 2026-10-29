package com.quillui.widget;

import com.quillui.motion.QuillAnimation;
import com.quillui.motion.QuillCurves;
import com.quillui.motion.QuillTicker;
import com.quillui.render.QuillRender;
import com.quillui.theme.QuillThemeData;
import com.quillui.theme.QuillThemeScope;
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
	private final QuillThemeData theme;
	private final Runnable action;
	private QuillButtonStyle style;
	private boolean selected;
	private Tooltip tooltip;
	private SoundEvent clickSound;
	private final QuillTicker ticker = new QuillTicker();
	private final QuillAnimation hoverAnimation = new QuillAnimation(0.12F).setCurve(QuillCurves.EASE_OUT);
	private boolean hoveredLast;

	public QuillButton(
			int x, int y, int width, int height, Component message, Variant variant, Runnable action
	) {
		this(x, y, width, height, message, variant, action, QuillThemeScope.current());
	}

	public QuillButton(
			int x,
			int y,
			int width,
			int height,
			Component message,
			Variant variant,
			Runnable action,
			QuillThemeData theme
	) {
		super(x, y, width, height, message);
		this.variant = variant;
		this.action = action;
		this.theme = theme;
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

	/** Overrides the variant colours with a custom component style. */
	public QuillButton style(QuillButtonStyle style) {
		this.style = style;
		return this;
	}

	/** Fluent replacement for setting the public {@code active} field. */
	public QuillButton enabled(boolean enabled) {
		this.active = enabled;
		return this;
	}

	public static QuillButton primary(String text, Runnable action) {
		return factory(text, Variant.PRIMARY, action);
	}

	public static QuillButton secondary(String text, Runnable action) {
		return factory(text, Variant.SECONDARY, action);
	}

	public static QuillButton danger(String text, Runnable action) {
		return factory(text, Variant.DANGER, action);
	}

	public static QuillButton ghost(String text, Runnable action) {
		return factory(text, Variant.GHOST, action);
	}

	public Variant variant() {
		return variant;
	}

	private static QuillButton factory(String text, Variant variant, Runnable action) {
		QuillThemeData theme = QuillThemeScope.current();
		return new QuillButton(
				0,
				0,
				theme.metrics().buttonWidth(),
				theme.metrics().controlHeight(),
				Component.literal(text),
				variant,
				action,
				theme
		);
	}

	@Override
	protected void extractWidgetRenderState(
			GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta
	) {
		int x = getX();
		int y = getY();
		boolean hovered = isMouseOver(mouseX, mouseY) && active;

		float frameDelta = ticker.tick();
		if (hovered != hoveredLast) {
			hoverAnimation.playTo(hovered ? 1.0F : 0.0F);
			hoveredLast = hovered;
		}
		hoverAnimation.update(frameDelta);
		float hover = active ? hoverAnimation.value() : 0.0F;

		QuillButtonStyle resolved = style != null ? style : defaultStyle();
		int background = resolved.backgroundAt(hover);
		int border = resolved.borderAt(hover);
		int textColor = resolved.textAt(hover);
		if (active && isFocused() && variant == Variant.GHOST && style == null) {
			border = resolved.focusRing();
		}

		if (background != 0) {
			graphics.fill(x, y, x + width, y + height, background);
		}

		if (border != 0) {
			QuillRender.border(graphics, x, y, width, height, border);
		}

		if (active && isFocused()) {
			QuillRender.border(graphics, x - 1, y - 1, width + 2, height + 2, resolved.focusRing());
		}

		if (selected) {
			graphics.fill(x, y, x + 2, y + height, theme.color(QuillThemeData.ColorRole.ACCENT));
		}

		QuillRender.centeredText(
				graphics,
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
					QuillMouseContext.screenX(mouseX),
					QuillMouseContext.screenY(mouseY)
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

	private QuillButtonStyle defaultStyle() {
		if (!active) {
			return QuillButtonStyle.disabled(theme);
		}
		if (selected) {
			return QuillButtonStyle.selected(theme);
		}
		return switch (variant) {
			case PRIMARY -> QuillButtonStyle.primary(theme);
			case DANGER -> QuillButtonStyle.danger(theme);
			case GHOST -> QuillButtonStyle.ghost(theme);
			default -> QuillButtonStyle.secondary(theme);
		};
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
