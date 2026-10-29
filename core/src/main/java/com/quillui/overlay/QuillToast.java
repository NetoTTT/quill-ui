package com.quillui.overlay;

import com.quillui.render.QuillRender;
import com.quillui.theme.QuillColors;
import com.quillui.theme.QuillThemeData;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

/**
 * Short-lived corner notification. Toasts never capture input, so overlays below them keep working
 * while the message is visible. The lifetime is measured in wall-clock time, not render ticks.
 */
public final class QuillToast implements QuillOverlay {
	private static final int MARGIN = 12;
	private static final int PADDING_X = 10;
	private static final int PADDING_Y = 7;
	private static final int ACCENT_WIDTH = 3;
	private static final int MAX_TEXT_WIDTH = 260;
	private static final long FADE_IN_MILLIS = 150L;
	private static final long FADE_OUT_MILLIS = 350L;

	private final Component message;
	private final QuillThemeData theme;
	private final long lifetimeMillis;
	private final long expiresAt;
	private boolean open = true;

	public QuillToast(Component message, float seconds, QuillThemeData theme) {
		this.message = message == null ? Component.empty() : message;
		this.theme = theme == null ? QuillThemeData.DEFAULT : theme;
		this.lifetimeMillis = Math.max(500L, Math.round(Math.max(0.5F, seconds) * 1000.0F));
		this.expiresAt = System.currentTimeMillis() + lifetimeMillis;
	}

	@Override
	public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		if (!isOpen()) {
			return;
		}
		Font font = Minecraft.getInstance().font;
		List<FormattedCharSequence> lines = font.split(message, MAX_TEXT_WIDTH);
		if (lines.isEmpty()) {
			lines = List.of(FormattedCharSequence.EMPTY);
		}

		int textWidth = 0;
		for (FormattedCharSequence line : lines) {
			textWidth = Math.max(textWidth, font.width(line));
		}
		int width = textWidth + PADDING_X * 2 + ACCENT_WIDTH;
		int height = lines.size() * font.lineHeight + PADDING_Y * 2;
		int x = graphics.guiWidth() - width - MARGIN;
		int y = MARGIN;

		float fade = fade();
		int panelBackground = QuillColors.withAlpha(
				theme.color(QuillThemeData.ColorRole.PANEL_BG), fade);
		int border = QuillColors.withAlpha(
				theme.color(QuillThemeData.ColorRole.PANEL_BORDER), fade);
		int accent = QuillColors.withAlpha(
				theme.color(QuillThemeData.ColorRole.ACCENT), fade);
		int textColor = QuillColors.withAlpha(
				theme.color(QuillThemeData.ColorRole.TEXT), fade);

		graphics.fill(x, y, x + width, y + height, panelBackground);
		graphics.fill(x, y, x + ACCENT_WIDTH, y + height, accent);
		QuillRender.border(graphics, x, y, width, height, border);

		int lineX = x + ACCENT_WIDTH + PADDING_X;
		int lineY = y + PADDING_Y;
		for (FormattedCharSequence line : lines) {
			graphics.text(font, line, lineX, lineY, textColor, false);
			lineY += font.lineHeight;
		}
	}

	@Override
	public boolean isOpen() {
		return open && System.currentTimeMillis() < expiresAt;
	}

	@Override
	public void close() {
		open = false;
	}

	@Override
	public boolean dismissOnOutsideClick() {
		return false;
	}

	@Override
	public boolean blocksInput() {
		return false;
	}

	private float fade() {
		long now = System.currentTimeMillis();
		long elapsed = lifetimeMillis - Math.max(0L, expiresAt - now);
		float fadeIn = Math.min(1.0F, elapsed / (float) FADE_IN_MILLIS);
		float fadeOut = Math.min(1.0F, Math.max(0L, expiresAt - now) / (float) FADE_OUT_MILLIS);
		return Math.max(0.0F, Math.min(fadeIn, fadeOut));
	}
}
