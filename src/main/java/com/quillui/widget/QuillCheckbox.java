package com.quillui.widget;

import com.quillui.input.QuillSelectionStyle;
import com.quillui.input.QuillSelectionStyles;
import com.quillui.render.QuillRender;
import com.quillui.theme.QuillThemeData;
import com.quillui.theme.QuillThemeScope;
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
 * Themed checkbox with a label and keyboard activation.
 */
public final class QuillCheckbox extends AbstractWidget {
	private static final int BOX_SIZE = 12;

	private final QuillThemeData theme;
	private final Consumer<Boolean> onChange;
	private QuillSelectionStyle style;
	private boolean checked;

	public static QuillCheckbox of(
			Component message, boolean checked, Consumer<Boolean> onChange
	) {
		QuillThemeData theme = QuillThemeScope.current();
		return new QuillCheckbox(
				0,
				0,
				theme.metrics().fieldWidth(),
				theme.metrics().controlHeight(),
				message,
				checked,
				onChange,
				theme
		);
	}

	public QuillCheckbox(
			int x,
			int y,
			int width,
			int height,
			Component message,
			boolean checked,
			Consumer<Boolean> onChange,
			QuillThemeData theme
	) {
		this(x, y, width, height, message, checked, onChange, theme, QuillSelectionStyles.CLASSIC);
	}

	public QuillCheckbox(
			int x,
			int y,
			int width,
			int height,
			Component message,
			boolean checked,
			Consumer<Boolean> onChange,
			QuillThemeData theme,
			QuillSelectionStyle style
	) {
		super(x, y, width, height, message);
		this.theme = theme == null ? QuillThemeData.DEFAULT : theme;
		this.checked = checked;
		this.onChange = onChange;
		this.style = style == null ? QuillSelectionStyles.CLASSIC : style;
	}

	public QuillCheckbox setStyle(QuillSelectionStyle style) {
		this.style = style == null ? QuillSelectionStyles.CLASSIC : style;
		return this;
	}

	public boolean checked() {
		return checked;
	}

	public QuillCheckbox setChecked(boolean checked) {
		this.checked = checked;
		return this;
	}

	@Override
	protected void extractWidgetRenderState(
			GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta
	) {
		int boxX = getX();
		int boxY = getY() + (getHeight() - BOX_SIZE) / 2;
		boolean hovered = active && isMouseOver(mouseX, mouseY);

		style.render(graphics, new QuillSelectionStyle.Context(
				QuillSelectionStyle.Type.CHECKBOX,
				boxX,
				boxY,
				BOX_SIZE,
				checked,
				hovered,
				isFocused(),
				active,
				theme
		));

		QuillRender.text(
				graphics,
				Minecraft.getInstance().font,
				getMessage(),
				boxX + BOX_SIZE + 6,
				getY() + (getHeight() - 8) / 2,
				active
						? theme.color(QuillThemeData.ColorRole.TEXT)
						: theme.color(QuillThemeData.ColorRole.BUTTON_DISABLED_TEXT)
		);
	}

	@Override
	public void onClick(MouseButtonEvent event, boolean doubleClick) {
		if (!active) {
			return;
		}
		checked = !checked;
		if (onChange != null) {
			onChange.accept(checked);
		}
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (!active) {
			return false;
		}
		if (event.key() == InputConstants.KEY_RETURN || event.key() == InputConstants.KEY_SPACE) {
			checked = !checked;
			if (onChange != null) {
				onChange.accept(checked);
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
