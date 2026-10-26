package com.quillui.screen;

import com.quillui.QuillRender;
import com.quillui.QuillTheme;
import com.quillui.QuillTooltips;
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
	private final int panelHeight;
	protected int panelX;
	protected int panelY;

	protected QuillScreen(Component title, int panelWidth, int panelHeight) {
		super(title);
		this.panelWidth = panelWidth;
		this.panelHeight = panelHeight;
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
		QuillRender.panel(graphics, panelX, panelY, panelWidth, panelHeight);
	}

	@Override
	public void extractRenderState(
			GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta
	) {
		QuillRender.header(graphics, panelX, panelY, panelWidth, 48);
		graphics.fill(panelX + 12, panelY + 14, panelX + 15, panelY + 34, QuillTheme.ACCENT);
		graphics.text(font, title, panelX + 22, panelY + 14, QuillTheme.TITLE);
		graphics.text(font, subtitle(), panelX + 22, panelY + 29, QuillTheme.TEXT_SOFT);

		super.extractRenderState(graphics, mouseX, mouseY, delta);

		Component footer = footer();
		if (!footer.getString().isBlank()) {
			QuillRender.divider(graphics, panelX + 20, panelY + panelHeight - 28, panelWidth - 40);
			graphics.centeredText(
					font,
					footer,
					panelX + panelWidth / 2,
					panelY + panelHeight - 16,
					QuillTheme.MUTED_DARK
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
				action
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
