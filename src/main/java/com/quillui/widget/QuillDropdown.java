package com.quillui.widget;

import com.quillui.overlay.QuillPopupMenu;
import com.quillui.render.QuillRender;
import com.quillui.render.QuillRenderContext;
import com.quillui.screen.QuillScreen;
import com.quillui.theme.QuillThemeData;
import com.quillui.theme.QuillThemeScope;
import java.util.List;
import java.util.function.IntConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

/**
 * Themed select control. The popup is a screen overlay so it renders above later widgets and can
 * dismiss itself on an outside click.
 */
public final class QuillDropdown extends AbstractWidget {
	private static final int DEFAULT_WIDTH = 220;
	private static final int DEFAULT_HEIGHT = 20;

	private final List<Component> options;
	private final IntConsumer onChange;
	private final QuillThemeData theme;
	private final Component label;
	private int selected;

	public static QuillDropdown of(
			Component label,
			List<Component> options,
			int selected,
			IntConsumer onChange
	) {
		return new QuillDropdown(
				0,
				0,
				DEFAULT_WIDTH,
				DEFAULT_HEIGHT,
				label,
				options,
				selected,
				onChange,
				QuillThemeScope.current()
		);
	}

	public QuillDropdown(
			int x,
			int y,
			int width,
			int height,
			Component label,
			List<Component> options,
			int selected,
			IntConsumer onChange,
			QuillThemeData theme
	) {
		super(x, y, width, height, label == null ? Component.empty() : label);
		this.label = label == null ? Component.empty() : label;
		this.options = options == null ? List.of() : List.copyOf(options);
		this.selected = selected >= 0 && selected < this.options.size() ? selected : -1;
		this.onChange = onChange;
		this.theme = theme == null ? QuillThemeData.DEFAULT : theme;
	}

	public int selectedIndex() {
		return selected;
	}

	public QuillDropdown setSelectedIndex(int selected) {
		this.selected = selected >= 0 && selected < options.size() ? selected : -1;
		return this;
	}

	@Override
	protected void extractWidgetRenderState(
			GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta
	) {
		boolean hovered = active && isMouseOver(mouseX, mouseY);
		int background = hovered
				? theme.color(QuillThemeData.ColorRole.BUTTON_HOVER)
				: theme.color(QuillThemeData.ColorRole.BUTTON_BG);
		int border = isFocused()
				? theme.color(QuillThemeData.ColorRole.FOCUS_RING)
				: theme.color(QuillThemeData.ColorRole.BUTTON_BORDER);
		graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), background);
		QuillRender.border(graphics, getX(), getY(), getWidth(), getHeight(), border);

		Component text = selected >= 0 && selected < options.size()
				? options.get(selected)
				: label;
		QuillRender.text(
				graphics,
				Minecraft.getInstance().font,
				text,
				getX() + 6,
				getY() + (getHeight() - 8) / 2,
				theme.color(QuillThemeData.ColorRole.TEXT)
		);

		int chevronX = getX() + getWidth() - 12;
		int chevronY = getY() + getHeight() / 2;
		QuillRender.line(graphics, chevronX, chevronY - 2, chevronX + 4, chevronY + 2,
				theme.color(QuillThemeData.ColorRole.TEXT_SOFT));
		QuillRender.line(graphics, chevronX + 4, chevronY + 2, chevronX + 8, chevronY - 2,
				theme.color(QuillThemeData.ColorRole.TEXT_SOFT));
	}

	@Override
	public void onClick(MouseButtonEvent event, boolean doubleClick) {
		if (!active || options.isEmpty()) {
			return;
		}
		if (Minecraft.getInstance().gui.screen() instanceof QuillScreen screen) {
			screen.openOverlay(new QuillPopupMenu(
					QuillRenderContext.screenX(getX()),
					QuillRenderContext.screenY(getY() + getHeight()),
					getWidth(),
					18,
					options,
					selected,
					theme,
					index -> {
						selected = index;
						if (onChange != null) {
							onChange.accept(index);
						}
					},
					null
			));
		}
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {
		defaultButtonNarrationText(output);
	}
}
