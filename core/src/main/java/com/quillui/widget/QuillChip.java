package com.quillui.widget;

import com.quillui.layout.QuillBoxConstraints;
import com.quillui.layout.QuillRect;
import com.quillui.layout.QuillSize;
import com.quillui.render.QuillRender;
import com.quillui.text.QuillTextStyle;
import com.quillui.theme.QuillThemeData;
import com.quillui.theme.QuillThemeScope;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

/**
 * A compact read-only pill for header accessories and rows: optional icon, short text and a
 * theme-coloured surface. It never takes focus.
 */
public final class QuillChip extends AbstractWidget implements QuillMeasuredWidget {
	private static final int HEIGHT = 18;
	private static final int PADDING = 7;
	private static final int ICON_GAP = 4;

	private final QuillThemeData theme;
	private QuillTextStyle style;
	private QuillIcon icon;

	public QuillChip(Component text, QuillThemeData theme) {
		super(0, 0, 0, HEIGHT, text == null ? Component.empty() : text);
		this.theme = theme == null ? QuillThemeData.DEFAULT : theme;
		this.style = QuillTextStyle.of(this.theme, QuillThemeData.ColorRole.TEXT_SOFT);
		this.active = false;
	}

	public static QuillChip of(Component text) {
		return new QuillChip(text, QuillThemeScope.current());
	}

	public static QuillChip of(Component text, QuillThemeData theme) {
		return new QuillChip(text, theme);
	}

	public QuillChip icon(QuillIcon icon) {
		this.icon = icon;
		return this;
	}

	public QuillChip icon(net.minecraft.world.item.ItemStack stack) {
		return icon(QuillIcons.item(stack));
	}

	public QuillChip color(QuillThemeData.ColorRole role) {
		this.style = QuillTextStyle.of(theme, role);
		return this;
	}

	public QuillChip muted() {
		return color(QuillThemeData.ColorRole.MUTED);
	}

	public QuillChip title() {
		return color(QuillThemeData.ColorRole.TITLE);
	}

	public QuillChip accent() {
		return color(QuillThemeData.ColorRole.ACCENT);
	}

	public QuillTextStyle style() {
		return style;
	}

	public QuillIcon icon() {
		return icon;
	}

	public QuillSize measure(Font font, int maxWidth) {
		return measure(QuillBoxConstraints.loose(maxWidth, QuillBoxConstraints.UNBOUNDED));
	}

	@Override
	public QuillSize measure(QuillBoxConstraints constraints) {
		Font font = Minecraft.getInstance().font;
		int iconSize = icon == null ? 0 : Math.min(icon.height(), HEIGHT - 4);
		int textWidth = font.width(getMessage());
		int width = PADDING * 2 + textWidth + (iconSize > 0 ? iconSize + ICON_GAP : 0);
		return constraints.constrain(QuillSize.of(width, HEIGHT));
	}

	@Override
	protected void extractWidgetRenderState(
			GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta
	) {
		QuillRender.box(
				graphics,
				getX(),
				getY(),
				getWidth(),
				getHeight(),
				theme.color(QuillThemeData.ColorRole.CHIP_BG),
				theme.color(QuillThemeData.ColorRole.CHIP_BORDER)
		);

		Font font = Minecraft.getInstance().font;
		int textX = getX() + PADDING;
		if (icon != null) {
			int iconHeight = defineIconHeight();
			float scale = iconHeight / (float) Math.max(1, icon.height());
			int iconWidth = Math.max(1, Math.round(icon.width() * scale));
			int iconY = getY() + (getHeight() - iconHeight) / 2;
			graphics.pose().pushMatrix();
			graphics.pose().translate((float) textX, (float) iconY);
			graphics.pose().scale(scale, scale);
			icon.paint(graphics, 0, 0);
			graphics.pose().popMatrix();
			textX += iconWidth + ICON_GAP;
		}

		QuillRender.text(
				graphics,
				font,
				getMessage(),
				textX,
				getY() + (getHeight() - 8) / 2 + 1,
				style.color()
		);
	}

	private int defineIconHeight() {
		return Math.max(1, Math.min(icon.height(), getHeight() - 4));
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {
		defaultButtonNarrationText(output);
	}
}
