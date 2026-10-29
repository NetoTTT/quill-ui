package com.quillui.widget;

import com.quillui.layout.QuillBoxConstraints;
import com.quillui.layout.QuillRect;
import com.quillui.layout.QuillSize;
import com.quillui.text.QuillTextMeasurer;
import com.quillui.text.QuillTextNode;
import com.quillui.text.QuillTextStyle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

/**
 * Non-interactive text widget. {@link #measure(Font, int)} returns the preferred size so screens
 * can size a row from its label instead of hardcoding a height.
 */
public final class QuillLabel extends AbstractWidget {
	private final QuillTextStyle style;

	public QuillLabel(int x, int y, int width, int height, Component text, QuillTextStyle style) {
		super(x, y, width, height, text == null ? Component.empty() : text);
		this.style = style == null ? QuillTextStyle.DEFAULT : style;
		this.active = false;
	}

	public static QuillLabel of(Component text, QuillTextStyle style) {
		return new QuillLabel(0, 0, 0, 0, text, style);
	}

	/** Preferred size for the current message, wrapping at {@code maxWidth}. */
	public QuillSize measure(Font font, int maxWidth) {
		return node(font).measure(
				QuillBoxConstraints.loose(maxWidth, QuillBoxConstraints.UNBOUNDED)
		);
	}

	public QuillTextStyle style() {
		return style;
	}

	@Override
	protected void extractWidgetRenderState(
			GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta
	) {
		Font font = Minecraft.getInstance().font;
		QuillTextNode node = node(font);
		node.arrange(new QuillRect(getX(), getY(), getWidth(), getHeight()));
		node.render(graphics, font);
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {
		defaultButtonNarrationText(output);
	}

	private QuillTextNode node(Font font) {
		return new QuillTextNode(getMessage(), style, QuillTextMeasurer.of(font));
	}
}
