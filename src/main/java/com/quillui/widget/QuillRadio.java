package com.quillui.widget;

import com.quillui.input.QuillSelectionStyle;
import com.quillui.input.QuillSelectionStyles;
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
 * Individual radio control. Group exclusivity belongs to the caller; selecting one radio should
 * clear the others in the same group.
 */
public final class QuillRadio extends AbstractWidget {
	private static final int RADIUS = 6;

	private final QuillThemeData theme;
	private final Consumer<Boolean> onChange;
	private QuillSelectionStyle style;
	private boolean selected;
	private boolean deselectable;

	public QuillRadio(
			int x,
			int y,
			int width,
			int height,
			Component message,
			boolean selected,
			Runnable onSelect,
			QuillThemeData theme
	) {
		this(x, y, width, height, message, selected,
				value -> {
					if (value && onSelect != null) {
						onSelect.run();
					}
				},
				theme, QuillSelectionStyles.CLASSIC);
	}

	public QuillRadio(
			int x,
			int y,
			int width,
			int height,
			Component message,
			boolean selected,
			Runnable onSelect,
			QuillThemeData theme,
			QuillSelectionStyle style
	) {
		this(x, y, width, height, message, selected,
				value -> {
					if (value && onSelect != null) {
						onSelect.run();
					}
				},
				theme, style);
	}

	public QuillRadio(
			int x,
			int y,
			int width,
			int height,
			Component message,
			boolean selected,
			Consumer<Boolean> onChange,
			QuillThemeData theme
	) {
		this(x, y, width, height, message, selected, onChange, theme, QuillSelectionStyles.CLASSIC);
	}

	public QuillRadio(
			int x,
			int y,
			int width,
			int height,
			Component message,
			boolean selected,
			Consumer<Boolean> onChange,
			QuillThemeData theme,
			QuillSelectionStyle style
	) {
		super(x, y, width, height, message);
		this.theme = theme == null ? QuillThemeData.DEFAULT : theme;
		this.selected = selected;
		this.onChange = onChange;
		this.style = style == null ? QuillSelectionStyles.CLASSIC : style;
	}

	public QuillRadio setStyle(QuillSelectionStyle style) {
		this.style = style == null ? QuillSelectionStyles.CLASSIC : style;
		return this;
	}

	public QuillRadio deselectable(boolean deselectable) {
		this.deselectable = deselectable;
		return this;
	}

	public boolean selected() {
		return selected;
	}

	public QuillRadio setSelected(boolean selected) {
		this.selected = selected;
		return this;
	}

	@Override
	protected void extractWidgetRenderState(
			GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta
	) {
		int boxX = getX();
		int boxY = getY() + (getHeight() - RADIUS * 2) / 2;
		boolean hovered = active && isMouseOver(mouseX, mouseY);
		style.render(graphics, new QuillSelectionStyle.Context(
				QuillSelectionStyle.Type.RADIO,
				boxX,
				boxY,
				RADIUS * 2,
				selected,
				hovered,
				isFocused(),
				active,
				theme
		));

		QuillRender.text(
				graphics,
				Minecraft.getInstance().font,
				getMessage(),
				getX() + RADIUS * 2 + 6,
				getY() + (getHeight() - 8) / 2,
				active
						? theme.color(QuillThemeData.ColorRole.TEXT)
						: theme.color(QuillThemeData.ColorRole.BUTTON_DISABLED_TEXT)
		);
	}

	@Override
	public void onClick(MouseButtonEvent event, boolean doubleClick) {
		if (!active || (selected && !deselectable)) {
			return;
		}
		selected = !selected;
		if (onChange != null) {
			onChange.accept(selected);
		}
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (!active) {
			return false;
		}
		if (event.key() == InputConstants.KEY_RETURN || event.key() == InputConstants.KEY_SPACE) {
			if (selected && !deselectable) {
				return true;
			}
			selected = !selected;
			if (onChange != null) {
				onChange.accept(selected);
			}
			return true;
		}
		return false;
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {
		defaultButtonNarrationText(output);
	}
}
