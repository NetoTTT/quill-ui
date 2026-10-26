package com.quillui.screen;

import com.quillui.render.QuillRender;
import com.quillui.theme.QuillThemeData;
import com.quillui.render.QuillTooltips;
import com.quillui.widget.QuillButton;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Base screen with a Quill panel, header, optional footer and close button. Content coordinates
 * are absolute GUI coordinates; use {@code QuillColumn}/{@code QuillGrid} to derive child bounds.
 */
public abstract class QuillScreen extends Screen {
	private final int panelWidth;
	private final QuillThemeData theme;
	private final int panelHeight;
	protected int panelX;
	protected int panelY;

	protected QuillScreen(Component title, int panelWidth, int panelHeight) {
		this(title, panelWidth, panelHeight, QuillThemeData.DEFAULT);
	}

	protected QuillScreen(
			Component title, int panelWidth, int panelHeight, QuillThemeData theme
	) {
		super(title);
		this.panelWidth = panelWidth;
		this.panelHeight = panelHeight;
		this.theme = theme;
	}

	@Override
	protected void init() {
		this.panelX = (this.width - panelWidth) / 2;
		this.panelY = (this.height - panelHeight) / 2;
		buildContent();
	}

	protected abstract void buildContent();

	protected Component subtitle() {
		return Component.empty();
	}

	protected Component footer() {
		return Component.empty();
	}

	protected int panelWidth() {
		return panelWidth;
	}

	protected int panelHeight() {
		return panelHeight;
	}

	@Override
	public void extractBackground(
			GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta
	) {
		super.extractBackground(graphics, mouseX, mouseY, delta);
		int screenBackground = theme.color(QuillThemeData.ColorRole.SCREEN_BG);
		if ((screenBackground >>> 24) != 0) {
			graphics.fill(0, 0, width, height, screenBackground);
		}
		QuillRender.panel(graphics, panelX, panelY, panelWidth, panelHeight, theme);
	}

	@Override
	public void extractRenderState(
			GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta
	) {
		QuillRender.header(graphics, panelX, panelY, panelWidth, 48, theme);
		graphics.fill(panelX + 12, panelY + 14, panelX + 15, panelY + 34, theme.color(QuillThemeData.ColorRole.ACCENT));
		graphics.text(font, title, panelX + 22, panelY + 14, theme.color(QuillThemeData.ColorRole.TITLE));
		graphics.text(font, subtitle(), panelX + 22, panelY + 29, theme.color(QuillThemeData.ColorRole.TEXT_SOFT));

		super.extractRenderState(graphics, mouseX, mouseY, delta);

		Component footer = footer();
		if (!footer.getString().isBlank()) {
			QuillRender.divider(graphics, panelX + 20, panelY + panelHeight - 28, panelWidth - 40, theme);
			graphics.centeredText(
					font,
					footer,
					panelX + panelWidth / 2,
					panelY + panelHeight - 16,
					theme.color(QuillThemeData.ColorRole.MUTED_DARK)
			);
		}
	}

	protected QuillButton closeButton(Runnable action) {
		QuillButton close = new QuillButton(
				panelX + panelWidth - 30,
				panelY + 8,
				20,
				20,
				Component.literal("X"),
				QuillButton.Variant.GHOST,
				action,
				theme
		);
		close.tip(QuillTooltips.of(
				Component.translatable("quillui.close"),
				Component.translatable("quillui.close.tip"),
				null
		));
		return close;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
