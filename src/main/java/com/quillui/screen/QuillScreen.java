package com.quillui.screen;

import com.quillui.overlay.QuillOverlay;
import com.quillui.overlay.QuillToast;
import com.quillui.render.QuillRender;
import com.quillui.render.QuillTooltips;
import com.quillui.theme.QuillThemeData;
import com.quillui.widget.QuillButton;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

/**
 * Base screen with a Quill panel, header, optional footer and close button. Content coordinates
 * are absolute GUI coordinates; use {@code QuillColumn}/{@code QuillGrid} to derive child bounds.
 */
public abstract class QuillScreen extends Screen {
	private static final int DEFAULT_SCREEN_MARGIN = 16;

	private final int requestedPanelWidth;
	private final int requestedPanelHeight;
	private int panelWidth;
	private int panelHeight;
	private final QuillThemeData theme;
	private final QuillScreenLayout layout;
	private final List<QuillShortcut> shortcuts = new ArrayList<>();
	private final List<QuillOverlay> overlays = new ArrayList<>();
	protected int panelX;
	protected int panelY;

	protected QuillScreen(Component title, int panelWidth, int panelHeight) {
		this(title, panelWidth, panelHeight, QuillThemeData.DEFAULT, QuillScreenLayout.DEFAULT);
	}

	protected QuillScreen(
			Component title, int panelWidth, int panelHeight, QuillThemeData theme
	) {
		this(title, panelWidth, panelHeight, theme, QuillScreenLayout.DEFAULT);
	}

	protected QuillScreen(
			Component title,
			int panelWidth,
			int panelHeight,
			QuillThemeData theme,
			QuillScreenLayout layout
	) {
		super(title);
		this.requestedPanelWidth = panelWidth;
		this.requestedPanelHeight = panelHeight;
		this.theme = theme;
		this.layout = layout;
	}

	@Override
	protected void init() {
		int margin = Math.max(0, screenMargin());
		int availableWidth = Math.max(1, width - margin * 2);
		int availableHeight = Math.max(1, height - margin * 2);
		this.panelWidth = Math.min(requestedPanelWidth, availableWidth);
		this.panelHeight = Math.min(requestedPanelHeight, availableHeight);
		this.panelX = (width - panelWidth) / 2;
		this.panelY = (height - panelHeight) / 2;
		buildContent();
	}

	/** Space kept between the panel and the screen edge on small windows. */
	protected int screenMargin() {
		return DEFAULT_SCREEN_MARGIN;
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

	protected int headerHeight() {
		return hasSubtitle()
				? layout.headerHeight()
				: layout.titleOnlyHeaderHeight();
	}

	/** Top edge for screen content, derived from the actual header height. */
	protected int contentTop() {
		return panelY + headerHeight() + 18;
	}

	protected boolean hasSubtitle() {
		return !subtitle().getString().isBlank();
	}

	/** Override to move the header, title, subtitle or close button. */
	protected QuillScreenLayout layout() {
		return layout;
	}

	protected void registerShortcut(int key, Runnable action) {
		registerShortcut(new QuillShortcut(key, action, false));
	}

	protected void registerShortcut(int key, Runnable action, boolean allowWhenFocused) {
		registerShortcut(new QuillShortcut(key, action, allowWhenFocused));
	}

	protected void registerShortcut(QuillShortcut shortcut) {
		shortcuts.add(shortcut);
	}

	public void openOverlay(QuillOverlay overlay) {
		if (overlay == null) {
			return;
		}
		overlays.add(overlay);
		setFocused((GuiEventListener) null);
	}

	public void closeOverlay(QuillOverlay overlay) {
		if (overlay != null) {
			overlay.close();
			overlays.remove(overlay);
		}
	}

	public void closeOverlays() {
		for (QuillOverlay overlay : overlays) {
			overlay.close();
		}
		overlays.clear();
	}

	public QuillToast showToast(Component message) {
		return showToast(message, 3.0F);
	}

	public QuillToast showToast(Component message, float seconds) {
		QuillToast toast = new QuillToast(message, seconds, theme);
		overlays.add(toast);
		return toast;
	}

	protected QuillOverlay topOverlay() {
		for (int index = overlays.size() - 1; index >= 0; index--) {
			QuillOverlay overlay = overlays.get(index);
			if (overlay.isOpen()) {
				return overlay;
			}
		}
		return null;
	}

	private void pruneOverlays() {
		overlays.removeIf(overlay -> !overlay.isOpen());
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		for (int index = overlays.size() - 1; index >= 0; index--) {
			QuillOverlay overlay = overlays.get(index);
			if (!overlay.isOpen()) {
				continue;
			}
			if (overlay.mouseClicked(event, doubleClick)) {
				pruneOverlays();
				return true;
			}
			if (overlay.dismissOnOutsideClick()) {
				overlay.close();
				pruneOverlays();
				return true;
			}
			if (overlay.blocksInput()) {
				return true;
			}
		}

		boolean handled = super.mouseClicked(event, doubleClick);
		if (!handled && getFocused() != null) {
			setFocused((GuiEventListener) null);
		}
		return handled;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		for (int index = overlays.size() - 1; index >= 0; index--) {
			QuillOverlay overlay = overlays.get(index);
			if (!overlay.isOpen()) {
				continue;
			}
			if (overlay.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
				pruneOverlays();
				return true;
			}
			if (overlay.blocksInput()) {
				return true;
			}
		}
		return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
		for (int index = overlays.size() - 1; index >= 0; index--) {
			QuillOverlay overlay = overlays.get(index);
			if (!overlay.isOpen()) {
				continue;
			}
			if (overlay.mouseDragged(event, deltaX, deltaY)) {
				pruneOverlays();
				return true;
			}
			if (overlay.blocksInput()) {
				return true;
			}
		}
		GuiEventListener target = mouseButtonTarget(event);
		if (target != null && target.mouseDragged(event, deltaX, deltaY)) {
			return true;
		}
		return super.mouseDragged(event, deltaX, deltaY);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		for (int index = overlays.size() - 1; index >= 0; index--) {
			QuillOverlay overlay = overlays.get(index);
			if (!overlay.isOpen()) {
				continue;
			}
			if (overlay.mouseReleased(event)) {
				pruneOverlays();
				return true;
			}
			if (overlay.blocksInput()) {
				return true;
			}
		}
		GuiEventListener target = mouseButtonTarget(event);
		if (target != null && target.mouseReleased(event)) {
			return true;
		}
		return super.mouseReleased(event);
	}

	private GuiEventListener mouseButtonTarget(MouseButtonEvent event) {
		GuiEventListener focused = getFocused();
		if (focused != null) {
			return focused;
		}
		return getChildAt(event.x(), event.y()).orElse(null);
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
		QuillScreenLayout current = layout();
		QuillRender.header(graphics, panelX, panelY, panelWidth, headerHeight(), theme);
		int accentHeight = Math.max(
				4,
				Math.min(
						20,
						headerHeight() - current.titleInsetY() - 6
				)
		);
		graphics.fill(
				panelX + current.titleInsetX() - 10,
				panelY + current.titleInsetY(),
				panelX + current.titleInsetX() - 7,
				panelY + current.titleInsetY() + accentHeight,
				theme.color(QuillThemeData.ColorRole.ACCENT)
		);
		QuillRender.text(
				graphics,
				font,
				title,
				panelX + current.titleInsetX(),
				panelY + current.titleInsetY(),
				theme.color(QuillThemeData.ColorRole.TITLE)
		);
		if (hasSubtitle()) {
			QuillRender.text(
					graphics,
					font,
					subtitle(),
					panelX + current.titleInsetX(),
					panelY + current.subtitleInsetY(),
					theme.color(QuillThemeData.ColorRole.TEXT_SOFT)
			);
		}

		super.extractRenderState(graphics, mouseX, mouseY, delta);

		Component footer = footer();
		if (!footer.getString().isBlank()) {
			QuillRender.divider(graphics, panelX + 20, panelY + panelHeight - 28, panelWidth - 40, theme);
			QuillRender.centeredText(
					graphics,
					font,
					footer,
					panelX + panelWidth / 2,
					panelY + panelHeight - 16,
					theme.color(QuillThemeData.ColorRole.MUTED_DARK)
			);
		}

		for (QuillOverlay overlay : overlays) {
			if (overlay.isOpen()) {
				overlay.render(graphics, mouseX, mouseY, delta);
			}
		}
		pruneOverlays();
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		for (int index = overlays.size() - 1; index >= 0; index--) {
			QuillOverlay overlay = overlays.get(index);
			if (!overlay.isOpen()) {
				continue;
			}
			if (overlay.keyPressed(event)) {
				pruneOverlays();
				return true;
			}
			if (overlay.blocksInput()) {
				return true;
			}
		}
		GuiEventListener focused = getFocused();
		boolean textInputFocused = focused != null && focused.capturesInput();
		if (!textInputFocused) {
			for (QuillShortcut shortcut : shortcuts) {
				if (shortcut.key() == event.key()
						&& (shortcut.allowWhenFocused() || focused == null)) {
					shortcut.action().run();
					return true;
				}
			}
		}
		return super.keyPressed(event);
	}

	protected QuillButton closeButton(Runnable action) {
		QuillScreenLayout current = layout();
		QuillButton close = new QuillButton(
				panelX + panelWidth - current.closeInsetRight() - current.closeSize(),
				panelY + current.closeInsetTop(),
				current.closeSize(),
				current.closeSize(),
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
