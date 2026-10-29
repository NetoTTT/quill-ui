package com.quillui.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

/**
 * Optional grouping policy for {@link QuillRadio}. The group enforces exclusivity and can require
 * one selected option without putting that policy into each radio control.
 */
public final class QuillRadioGroup {
	private final List<QuillRadio> radios = new ArrayList<>();
	private boolean required;
	private IntConsumer onChange;

	public QuillRadioGroup() {
	}

	public QuillRadioGroup(boolean required) {
		this.required = required;
	}

	public QuillRadioGroup onChange(IntConsumer onChange) {
		this.onChange = onChange;
		return this;
	}

	public QuillRadioGroup add(QuillRadio radio) {
		if (radio == null || radios.contains(radio)) {
			return this;
		}
		radios.add(radio);
		radio.deselectable(!required);
		radio.onChange(value -> {
			if (value) {
				select(radio);
			} else if (selected() == radio) {
				notifyChange(-1);
			}
		});
		return this;
	}

	public List<QuillRadio> radios() {
		return List.copyOf(radios);
	}

	public QuillRadioGroup required(boolean required) {
		this.required = required;
		for (QuillRadio radio : radios) {
			radio.deselectable(!required);
		}
		return this;
	}

	public boolean required() {
		return required;
	}

	public QuillRadio selected() {
		for (QuillRadio radio : radios) {
			if (radio.selected()) {
				return radio;
			}
		}
		return null;
	}

	public int selectedIndex() {
		return radios.indexOf(selected());
	}

	public void select(int index) {
		if (index < 0 || index >= radios.size()) {
			return;
		}
		select(radios.get(index));
	}

	public void select(QuillRadio radio) {
		if (radio == null || !radios.contains(radio)) {
			return;
		}
		for (QuillRadio candidate : radios) {
			candidate.setSelected(candidate == radio);
		}
		notifyChange(radios.indexOf(radio));
	}

	public boolean clear() {
		if (required) {
			return false;
		}
		for (QuillRadio radio : radios) {
			radio.setSelected(false);
		}
		notifyChange(-1);
		return true;
	}

	private void notifyChange(int index) {
		if (onChange != null) {
			onChange.accept(index);
		}
	}
}
