package com.quillui.overlay;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * Ordered overlay stack shared by screens. Input flows top-down and stops at the first blocking
 * overlay; closed overlays are pruned after each dispatch.
 */
public final class QuillOverlayStack {
	private final List<QuillOverlay> overlays = new ArrayList<>();

	public void add(QuillOverlay overlay) {
		if (overlay != null) {
			overlays.add(overlay);
		}
	}

	public void close(QuillOverlay overlay) {
		if (overlay != null) {
			overlay.close();
			overlays.remove(overlay);
		}
	}

	public void closeAll() {
		for (QuillOverlay overlay : overlays) {
			overlay.close();
		}
		overlays.clear();
	}

	public QuillOverlay top() {
		for (int index = overlays.size() - 1; index >= 0; index--) {
			QuillOverlay overlay = overlays.get(index);
			if (overlay.isOpen()) {
				return overlay;
			}
		}
		return null;
	}

	public boolean hasBlocking() {
		for (QuillOverlay overlay : overlays) {
			if (overlay.isOpen() && overlay.blocksInput()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Offers the event to each open overlay from top to bottom. Returning {@code true} from the
	 * handler consumes the event; otherwise a blocking overlay still swallows it.
	 */
	public boolean dispatch(Predicate<QuillOverlay> handler) {
		for (int index = overlays.size() - 1; index >= 0; index--) {
			QuillOverlay overlay = overlays.get(index);
			if (!overlay.isOpen()) {
				continue;
			}
			if (handler.test(overlay)) {
				prune();
				return true;
			}
			if (overlay.blocksInput()) {
				return true;
			}
		}
		return false;
	}

	public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		for (QuillOverlay overlay : overlays) {
			if (overlay.isOpen()) {
				overlay.render(graphics, mouseX, mouseY, delta);
			}
		}
		prune();
	}

	public void prune() {
		overlays.removeIf(overlay -> !overlay.isOpen());
	}

	public int size() {
		return overlays.size();
	}
}
