package com.quillui.overlay;

import com.quillui.render.QuillRender;
import com.quillui.theme.QuillColors;
import com.quillui.theme.QuillThemeData;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

/**
 * Modal dialog with a title, wrapped body and confirm/cancel actions. It blocks click-through and
 * can be dismissed with Escape (cancel) or Enter (confirm).
 */
public final class QuillModal implements QuillOverlay {
	private static final int WIDTH = 360;
	private static final int HEIGHT = 190;
	private static final int BUTTON_WIDTH = 108;
	private static final int BUTTON_HEIGHT = 20;

	private final Component title;
	private final Component body;
	private final Component confirmLabel;
	private final Component cancelLabel;
	private final Runnable onConfirm;
	private final Runnable onCancel;
	private final QuillThemeData theme;
	private boolean open = true;
	private boolean closeNotified;
	private int panelX;
	private int panelY;
	private int confirmX;
	private int confirmY;
	private int cancelX;
	private int cancelY;

	public QuillModal(
			Component title,
			Component body,
			Component confirmLabel,
			Component cancelLabel,
			Runnable onConfirm,
			Runnable onCancel,
			QuillThemeData theme
	) {
		this.title = title == null ? Component.empty() : title;
		this.body = body == null ? Component.empty() : body;
		this.confirmLabel = confirmLabel == null ? Component.empty() : confirmLabel;
		this.cancelLabel = cancelLabel == null ? Component.empty() : cancelLabel;
		this.onConfirm = onConfirm;
		this.onCancel = onCancel;
		this.theme = theme == null ? QuillThemeData.DEFAULT : theme;
	}

	@Override
	public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		if (!open) {
			return;
		}
		panelX = (graphics.guiWidth() - WIDTH) / 2;
		panelY = (graphics.guiHeight() - HEIGHT) / 2;
		graphics.fill(
				0, 0, graphics.guiWidth(), graphics.guiHeight(),
				QuillColors.withAlpha(theme.color(QuillThemeData.ColorRole.PANEL_BG), 0xAA)
		);
		QuillRender.panel(graphics, panelX, panelY, WIDTH, HEIGHT, theme);
		Font font = Minecraft.getInstance().font;
		QuillRender.text(
				graphics, font, title,
				panelX + 20, panelY + 20,
				theme.color(QuillThemeData.ColorRole.TITLE)
		);

		List<FormattedCharSequence> lines = font.split(body, WIDTH - 40);
		for (int index = 0; index < Math.min(lines.size(), 5); index++) {
			graphics.text(
					font, lines.get(index),
					panelX + 20, panelY + 48 + index * font.lineHeight,
					theme.color(QuillThemeData.ColorRole.TEXT), false
			);
		}

		int buttonY = panelY + HEIGHT - 38;
		int confirmWidth = hasLabel(confirmLabel) ? BUTTON_WIDTH : 0;
		int cancelWidth = hasLabel(cancelLabel) ? BUTTON_WIDTH : 0;
		int gap = confirmWidth > 0 && cancelWidth > 0 ? 8 : 0;
		int startX = panelX + WIDTH - 20 - confirmWidth - gap - cancelWidth;
		cancelX = startX;
		cancelY = buttonY;
		confirmX = startX + cancelWidth + gap;
		confirmY = buttonY;

		if (cancelWidth > 0) {
			drawAction(graphics, font, cancelLabel, cancelX, cancelY, cancelWidth, mouseX, mouseY, false);
		}
		if (confirmWidth > 0) {
			drawAction(graphics, font, confirmLabel, confirmX, confirmY, confirmWidth, mouseX, mouseY, true);
		}
	}

	private void drawAction(
			GuiGraphicsExtractor graphics,
			Font font,
			Component label,
			int x,
			int y,
			int width,
			int mouseX,
			int mouseY,
			boolean primary
	) {
		boolean hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + BUTTON_HEIGHT;
		int background = primary
				? (hovered
						? theme.color(QuillThemeData.ColorRole.PRIMARY_BG_HOVER)
						: theme.color(QuillThemeData.ColorRole.PRIMARY_BG))
				: (hovered
						? theme.color(QuillThemeData.ColorRole.BUTTON_HOVER)
						: theme.color(QuillThemeData.ColorRole.BUTTON_BG));
		int border = primary
				? theme.color(QuillThemeData.ColorRole.ACCENT)
				: theme.color(QuillThemeData.ColorRole.BUTTON_BORDER);
		int textColor = primary
				? theme.color(QuillThemeData.ColorRole.ON_ACCENT)
				: theme.color(QuillThemeData.ColorRole.TEXT);
		graphics.fill(x, y, x + width, y + BUTTON_HEIGHT, background);
		QuillRender.border(graphics, x, y, width, BUTTON_HEIGHT, border);
		QuillRender.centeredText(graphics, font, label, x + width / 2, y + 6, textColor);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (!open) {
			return false;
		}
		if (inside(event.x(), event.y(), cancelX, cancelY, BUTTON_WIDTH, BUTTON_HEIGHT)
				&& hasLabel(cancelLabel)) {
			runCancel();
			close();
			return true;
		}
		if (inside(event.x(), event.y(), confirmX, confirmY, BUTTON_WIDTH, BUTTON_HEIGHT)
				&& hasLabel(confirmLabel)) {
			runConfirm();
			close();
			return true;
		}
		return true;
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (!open) {
			return false;
		}
		if (event.key() == InputConstants.KEY_ESCAPE) {
			runCancel();
			close();
			return true;
		}
		if (event.key() == InputConstants.KEY_RETURN || event.key() == InputConstants.KEY_NUMPADENTER) {
			runConfirm();
			close();
			return true;
		}
		return true;
	}

	@Override
	public boolean isOpen() {
		return open;
	}

	@Override
	public void close() {
		if (!open) {
			return;
		}
		open = false;
		if (!closeNotified && onCancel != null) {
			closeNotified = true;
			onCancel.run();
		}
	}

	@Override
	public boolean dismissOnOutsideClick() {
		return false;
	}

	@Override
	public boolean blocksInput() {
		return true;
	}

	private void runConfirm() {
		closeNotified = true;
		if (onConfirm != null) {
			onConfirm.run();
		}
	}

	private void runCancel() {
		closeNotified = true;
		if (onCancel != null) {
			onCancel.run();
		}
	}

	private static boolean hasLabel(Component label) {
		return label != null && !label.getString().isBlank();
	}

	private static boolean inside(double mouseX, double mouseY, int x, int y, int width, int height) {
		return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
	}
}
