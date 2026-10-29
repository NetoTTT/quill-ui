package com.quillui.screen;

import com.quillui.layout.QuillBoxConstraints;
import com.quillui.layout.QuillColumn;
import com.quillui.layout.QuillLayoutNode;
import com.quillui.layout.QuillRect;
import com.quillui.layout.QuillRow;
import com.quillui.layout.QuillSize;
import com.quillui.overlay.QuillOverlay;
import com.quillui.overlay.QuillOverlayStack;
import com.quillui.overlay.QuillToast;
import com.quillui.render.QuillPaintable;
import com.quillui.render.QuillRender;
import com.quillui.render.QuillTooltips;
import com.quillui.theme.QuillThemeData;
import com.quillui.theme.QuillThemeScope;
import com.quillui.text.QuillTextStyle;
import com.quillui.widget.QuillButton;
import com.quillui.widget.QuillClipView;
import com.quillui.widget.QuillLabel;
import com.quillui.widget.QuillScrollBar;
import com.quillui.widget.QuillWidgetBox;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
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
	private static final int HEADER_CONTENT_GAP = 18;
	private static final int FOOTER_CONTENT_GAP = 12;
	private static final int FOOTER_HEIGHT = 28;
	private static final int FOOTER_SIDE_INSET = 20;
	private static final int FOOTER_TEXT_OFFSET = 16;

	private final int requestedPanelWidth;
	private final int requestedPanelHeight;
	private int panelWidth;
	private int panelHeight;
	private final QuillThemeData theme;
	private final QuillScreenLayout layout;
	private final List<QuillShortcut> shortcuts = new ArrayList<>();
	private final QuillOverlayStack overlays = new QuillOverlayStack();
	private int contentOffset;
	private int contentMaxScroll;
	private boolean contentScrolls;
	private boolean contentDirty;
	private final List<QuillPaintable> contentPaintables = new ArrayList<>();
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
		contentDirty = false;
		QuillThemeScope.run(theme, this::buildContent);
	}

	/**
	 * Requests a content rebuild after local state changes. The rebuild runs on the next tick so
	 * widget lists are never mutated while input is being dispatched, and the scroll offset is kept.
	 */
	protected void refreshContent() {
		contentDirty = true;
	}

	@Override
	public void tick() {
		super.tick();
		if (!contentDirty) {
			return;
		}
		contentDirty = false;
		int previousOffset = contentOffset;
		clearWidgets();
		setFocused((GuiEventListener) null);
		QuillThemeScope.run(theme, this::buildContent);
		contentOffset = Math.min(previousOffset, contentMaxScroll);
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
		return panelY + headerHeight() + HEADER_CONTENT_GAP;
	}

	/** Bottom edge available to content, above the footer. */
	protected int contentBottom() {
		return panelY + panelHeight - footerHeight() - FOOTER_CONTENT_GAP;
	}

	protected int footerHeight() {
		return footer().getString().isBlank() ? 0 : FOOTER_HEIGHT;
	}

	/** Default horizontal inset for screen content. */
	protected int contentInset() {
		QuillThemeData resolved = theme == null ? QuillThemeData.DEFAULT : theme;
		return resolved.metrics().contentInset();
	}

	/** Content area between the header and footer. */
	protected QuillRect contentBounds() {
		return contentBounds(contentInset());
	}

	/** Content area between the header and footer with a custom inset. */
	protected QuillRect contentBounds(int horizontalInset) {
		return contentBounds(horizontalInset, footerHeight() + FOOTER_CONTENT_GAP);
	}

	/** Content area with an explicit bottom inset, for screens with fixed controls below it. */
	protected QuillRect contentBounds(int horizontalInset, int bottomInset) {
		int top = contentTop();
		int bottom = panelY + panelHeight - bottomInset;
		return new QuillRect(
				panelX + horizontalInset,
				top,
				Math.max(1, panelWidth() - horizontalInset * 2),
				Math.max(1, bottom - top)
		);
	}

	protected QuillThemeData theme() {
		return theme;
	}

	/** Vertical content column starting at the current content bounds. */
	protected QuillColumn contentColumn(int gap) {
		QuillRect content = contentBounds();
		return new QuillColumn(content.x(), content.y(), content.width(), gap);
	}

	/** Horizontal content row starting at the current content bounds. */
	protected QuillRow contentRow(int gap) {
		QuillRect content = contentBounds();
		return new QuillRow(content.x(), content.y(), content.height(), gap);
	}

	/** Registers a widget and applies a measured rectangle in one call. */
	protected <T extends AbstractWidget> T add(T widget, QuillRect bounds) {
		widget.setX(bounds.x());
		widget.setY(bounds.y());
		widget.setWidth(bounds.width());
		widget.setHeight(bounds.height());
		return addRenderableWidget(widget);
	}

	/**
	 * Measures and arranges a layout tree inside {@link #contentBounds()}, then registers every
	 * {@link QuillWidgetBox} it contains. When the tree is taller than the viewport it is hosted in
	 * a clipping scroll view with a scrollbar. Call from {@link #buildContent()}.
	 */
	protected void setContent(QuillLayoutNode root) {
		setContent(root, contentBounds());
	}

	/** Like {@link #setContent(QuillLayoutNode)}, using an explicit content area. */
	protected void setContent(QuillLayoutNode root, QuillRect content) {
		if (root == null) {
			throw new IllegalArgumentException("root must not be null");
		}
		if (content == null) {
			throw new IllegalArgumentException("content must not be null");
		}
		contentOffset = 0;
		contentMaxScroll = 0;
		contentScrolls = false;
		contentPaintables.clear();
		QuillSize natural = root.measure(
				QuillBoxConstraints.loose(content.width(), QuillBoxConstraints.UNBOUNDED)
		);
		int maxScroll = Math.max(0, natural.height() - content.height());

		if (maxScroll == 0) {
			QuillSize size = root.measure(
					QuillBoxConstraints.loose(content.width(), content.height())
			);
			root.arrange(new QuillRect(content.x(), content.y(), content.width(), size.height()));
			registerWidgets(root, null);
			return;
		}

		contentMaxScroll = maxScroll;
		contentScrolls = true;
		root.arrange(new QuillRect(
				content.x(),
				content.y(),
				content.width(),
				natural.height()
		));
		QuillClipView host = new QuillClipView(
				content,
				() -> contentMaxScroll,
				() -> contentOffset,
				value -> contentOffset = value,
				theme,
				(graphics, vx, vy, vw, vh, offset, mx, my, delta) -> paintContent(graphics)
		);
		registerWidgets(root, host);
		addRenderableWidget(host);
		addRenderableWidget(new QuillScrollBar(
				content,
				() -> contentMaxScroll,
				() -> contentOffset,
				value -> contentOffset = value,
				theme
		).visibleWhenNeeded());
	}

	/** Current scroll offset of the measured content tree. */
	protected int contentScrollOffset() {
		return contentOffset;
	}

	/** Maximum scroll offset of the measured content tree, or {@code 0} when it fits. */
	protected int contentMaxScroll() {
		return contentMaxScroll;
	}

	/** Wraps a widget for the measured tree, using its current height. */
	protected QuillWidgetBox control(AbstractWidget widget) {
		return new QuillWidgetBox(widget);
	}

	/** Wraps a widget for the measured tree and fixes its height. */
	protected QuillWidgetBox control(AbstractWidget widget, int height) {
		widget.setHeight(height);
		return new QuillWidgetBox(widget);
	}

	/** A measured text leaf for the content tree. */
	protected QuillWidgetBox text(Component text, QuillTextStyle style) {
		QuillTextStyle resolved = style == null
				? QuillTextStyle.of(theme, QuillThemeData.ColorRole.TEXT)
				: style;
		return new QuillWidgetBox(QuillLabel.of(text, resolved));
	}

	/** A measured text leaf using a theme colour role. */
	protected QuillWidgetBox text(Component text, QuillThemeData.ColorRole role) {
		return text(text, QuillTextStyle.of(theme, role));
	}

	protected QuillWidgetBox text(Component text) {
		return text(text, (QuillTextStyle) null);
	}

	/** Resolved colour for a role on this screen's theme. */
	protected int color(QuillThemeData.ColorRole role) {
		QuillThemeData resolved = theme == null ? QuillThemeData.DEFAULT : theme;
		return resolved.color(role);
	}

	private void registerWidgets(QuillLayoutNode node, QuillClipView host) {
		if (node instanceof QuillWidgetBox box) {
			if (host == null) {
				addRenderableWidget(box.widget());
			}
			else {
				host.add(box.widget());
			}
		}
		if (node instanceof QuillPaintable paintable) {
			contentPaintables.add(paintable);
		}
		for (QuillLayoutNode child : node.children()) {
			registerWidgets(child, host);
		}
	}

	private void paintContent(GuiGraphicsExtractor graphics) {
		for (QuillPaintable paintable : contentPaintables) {
			paintable.paint(graphics, font);
		}
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
		overlays.close(overlay);
	}

	public void closeOverlays() {
		overlays.closeAll();
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
		return overlays.top();
	}

	private boolean hasBlockingOverlay() {
		return overlays.hasBlocking();
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		boolean consumed = overlays.dispatch(overlay -> {
			if (overlay.mouseClicked(event, doubleClick)) {
				return true;
			}
			if (overlay.dismissOnOutsideClick()) {
				overlay.close();
				return true;
			}
			return false;
		});
		if (consumed) {
			return true;
		}

		boolean handled = super.mouseClicked(event, doubleClick);
		if (!handled && getFocused() != null) {
			setFocused((GuiEventListener) null);
		}
		return handled;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (overlays.dispatch(overlay -> overlay.mouseScrolled(mouseX, mouseY, scrollX, scrollY))) {
			return true;
		}
		return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
		if (overlays.dispatch(overlay -> overlay.mouseDragged(event, deltaX, deltaY))) {
			return true;
		}
		GuiEventListener target = mouseButtonTarget(event);
		if (target != null && target.mouseDragged(event, deltaX, deltaY)) {
			return true;
		}
		return super.mouseDragged(event, deltaX, deltaY);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		if (overlays.dispatch(overlay -> overlay.mouseReleased(event))) {
			return true;
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

		if (!contentScrolls) {
			paintContent(graphics);
		}

		boolean inputBlocked = hasBlockingOverlay();
		super.extractRenderState(
				graphics,
				inputBlocked ? -1 : mouseX,
				inputBlocked ? -1 : mouseY,
				delta
		);

		Component footer = footer();
		if (!footer.getString().isBlank()) {
			QuillRender.divider(
					graphics,
					panelX + FOOTER_SIDE_INSET,
					panelY + panelHeight - FOOTER_HEIGHT,
					panelWidth - FOOTER_SIDE_INSET * 2,
					theme
			);
			QuillRender.centeredText(
					graphics,
					font,
					footer,
					panelX + panelWidth / 2,
					panelY + panelHeight - FOOTER_TEXT_OFFSET,
					theme.color(QuillThemeData.ColorRole.MUTED_DARK)
			);
		}

		overlays.render(graphics, mouseX, mouseY, delta);
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (overlays.dispatch(overlay -> overlay.keyPressed(event))) {
			return true;
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
