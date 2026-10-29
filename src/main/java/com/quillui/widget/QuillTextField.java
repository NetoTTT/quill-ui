package com.quillui.widget;

import com.quillui.render.QuillRender;
import com.quillui.theme.QuillThemeData;
import com.quillui.theme.QuillThemeScope;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

/**
 * Themed single-line text field built on vanilla {@link EditBox}. It keeps vanilla cursor,
 * selection and IME behaviour, while Quill owns the surface, focus ring and validation border.
 */
public final class QuillTextField extends EditBox {
	private static final int DEFAULT_WIDTH = 220;
	private static final int DEFAULT_HEIGHT = 20;

	private final QuillThemeData theme;
	private Predicate<String> validator = value -> true;
	private Consumer<String> externalResponder;
	private boolean invalid;
	private Component hint = Component.empty();
	private Runnable onSubmit;

	public static QuillTextField of(Font font, Component hint) {
		return new QuillTextField(
				font,
				0,
				0,
				DEFAULT_WIDTH,
				DEFAULT_HEIGHT,
				hint,
				QuillThemeScope.current()
		);
	}

	public QuillTextField(
			Font font,
			int x,
			int y,
			int width,
			int height,
			Component hint,
			QuillThemeData theme
	) {
		super(font, x, y, width, height, Component.empty());
		this.theme = theme == null ? QuillThemeData.DEFAULT : theme;
		setBordered(false);
		setCanLoseFocus(true);
		setTextShadow(false);
		setHint(hint);
		setTextColor(this.theme.color(QuillThemeData.ColorRole.TEXT));
		setTextColorUneditable(this.theme.color(QuillThemeData.ColorRole.BUTTON_DISABLED_TEXT));
	}

	@Override
	public void setHint(Component hint) {
		this.hint = hint == null ? Component.empty() : hint;
		super.setHint(Component.empty());
	}

	public QuillTextField setPlaceholder(Component hint) {
		setHint(hint);
		return this;
	}

	public QuillTextField onSubmit(Runnable onSubmit) {
		this.onSubmit = onSubmit;
		return this;
	}

	public QuillTextField setValidator(Predicate<String> validator) {
		this.validator = validator == null ? value -> true : validator;
		this.invalid = !this.validator.test(getValue());
		return this;
	}

	public boolean isValid() {
		return !invalid;
	}

	@Override
	public void setResponder(Consumer<String> responder) {
		externalResponder = responder;
		super.setResponder(value -> {
			invalid = !validator.test(value);
			if (externalResponder != null) {
				externalResponder.accept(value);
			}
		});
	}

	@Override
	public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
		if (event.key() == InputConstants.KEY_RETURN
				|| event.key() == InputConstants.KEY_NUMPADENTER) {
			if (onSubmit != null) {
				onSubmit.run();
			}
			setFocused(false);
			return true;
		}
		return super.keyPressed(event);
	}

	@Override
	public void extractWidgetRenderState(
			GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta
	) {
		invalid = !validator.test(getValue());
		int background = active
				? (isFocused()
						? theme.color(QuillThemeData.ColorRole.SURFACE_ALT)
						: theme.color(QuillThemeData.ColorRole.BUTTON_BG))
				: theme.color(QuillThemeData.ColorRole.BUTTON_DISABLED);
		int border = !active
				? theme.color(QuillThemeData.ColorRole.BUTTON_DISABLED_BORDER)
				: (invalid
						? theme.color(QuillThemeData.ColorRole.DANGER)
						: (isFocused()
								? theme.color(QuillThemeData.ColorRole.FOCUS_RING)
								: theme.color(QuillThemeData.ColorRole.BUTTON_BORDER)));
		graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), background);
		QuillRender.border(graphics, getX(), getY(), getWidth(), getHeight(), border);
		int verticalOffset = Math.max(0, (getHeight() - 8) / 2);
		graphics.pose().pushMatrix();
		graphics.pose().translate(4.0F, (float) verticalOffset);
		super.extractWidgetRenderState(graphics, mouseX, mouseY, delta);
		graphics.pose().popMatrix();

		if (getValue().isEmpty() && !isFocused() && !hint.getString().isEmpty()) {
			QuillRender.text(
					graphics,
					Minecraft.getInstance().font,
					hint,
					getX() + 4,
					getY() + (getHeight() - 8) / 2,
					theme.color(QuillThemeData.ColorRole.MUTED)
			);
		}
	}
}
