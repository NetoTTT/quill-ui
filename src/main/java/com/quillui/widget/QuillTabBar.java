package com.quillui.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import com.quillui.theme.QuillThemeData;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;

/**
 * Builds a row of equal-width tab buttons. The caller registers the buttons on the screen and
 * calls {@link #select(int)} when the active tab changes.
 */
public final class QuillTabBar {
	private final List<QuillButton> buttons = new ArrayList<>();
	private final IntConsumer onChange;
	private final QuillThemeData theme;
	private int selected = -1;

	public QuillTabBar(
			int x,
			int y,
			int width,
			int height,
			int gap,
			List<Component> labels,
			IntConsumer onChange
	) {
		this(x, y, width, height, gap, labels, onChange, QuillThemeData.DEFAULT);
	}

	public QuillTabBar(
			int x,
			int y,
			int width,
			int height,
			int gap,
			List<Component> labels,
			IntConsumer onChange,
			QuillThemeData theme
	) {
		this.onChange = onChange;
		this.theme = theme;
		if (labels.isEmpty()) {
			return;
		}

		int tabWidth = (width - gap * (labels.size() - 1)) / labels.size();
		for (int index = 0; index < labels.size(); index++) {
			int tabIndex = index;
			QuillButton button = new QuillButton(
					x + index * (tabWidth + gap),
					y,
					tabWidth,
					height,
					labels.get(index),
					QuillButton.Variant.GHOST,
					() -> select(tabIndex),
					theme
			);
			buttons.add(button);
		}
	}

	public QuillTabBar clickSound(SoundEvent sound) {
		buttons.forEach(button -> button.clickSound(sound));
		return this;
	}

	public void addTo(Consumer<QuillButton> consumer) {
		buttons.forEach(consumer);
	}

	public void select(int index) {
		if (index < 0 || index >= buttons.size()) {
			return;
		}
		selected = index;
		for (int i = 0; i < buttons.size(); i++) {
			QuillButton button = buttons.get(i);
			button.selected(i == index);
		}
		if (onChange != null) {
			onChange.accept(index);
		}
	}

	public int selected() {
		return selected;
	}

	public QuillButton button(int index) {
		return buttons.get(index);
	}
}
