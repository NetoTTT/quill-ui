package com.quillui.theme;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Immutable colour theme. Use {@link #DEFAULT}, derive a variant with {@link #with(ColorRole, int)}
 * or build a custom palette with {@link #builder()}.
 */
public final class QuillThemeData {
	public enum ColorRole {
		SCREEN_BG,
		PANEL_BG,
		PANEL_BORDER,
		PANEL_SHADOW,
		HEADER_BG,
		DIVIDER,
		TITLE,
		TEXT,
		TEXT_SOFT,
		MUTED,
		MUTED_DARK,
		ACCENT,
		ACCENT_HOVER,
		PRIMARY_BG,
		PRIMARY_BG_HOVER,
		ON_ACCENT,
		ON_DANGER,
		GHOST_BG,
		GHOST_BORDER,
		GHOST_TEXT,
		FOCUS_RING,
		DANGER,
		DANGER_BG,
		SUCCESS,
		INFO,
		BUTTON_BG,
		BUTTON_HOVER,
		BUTTON_BORDER,
		BUTTON_DISABLED,
		BUTTON_DISABLED_BORDER,
		BUTTON_DISABLED_TEXT,
		SELECTED_BG,
		SURFACE,
		SURFACE_ALT,
		SURFACE_BORDER,
		SURFACE_HEADER,
		CHIP_BG,
		CHIP_BORDER,
		MAP_BG,
		MAP_GRID,
		MAP_UNKNOWN
	}

	public static final QuillThemeData DEFAULT = new QuillThemeData(defaultColors());

	private final Map<ColorRole, Integer> colors;

	private QuillThemeData(Map<ColorRole, Integer> colors) {
		this.colors = Map.copyOf(colors);
	}

	public int color(ColorRole role) {
		return colors.get(role);
	}

	public QuillThemeData with(ColorRole role, int color) {
		EnumMap<ColorRole, Integer> copy = new EnumMap<>(ColorRole.class);
		copy.putAll(colors);
		copy.put(role, color);
		return new QuillThemeData(copy);
	}

	public List<String> contrastWarnings() {
		List<String> warnings = new ArrayList<>();
		check(warnings, "TEXT on PANEL_BG", ColorRole.TEXT, ColorRole.PANEL_BG);
		check(warnings, "TEXT on SURFACE", ColorRole.TEXT, ColorRole.SURFACE);
		check(warnings, "ON_ACCENT on PRIMARY_BG", ColorRole.ON_ACCENT, ColorRole.PRIMARY_BG);
		check(warnings, "ON_DANGER on DANGER_BG", ColorRole.ON_DANGER, ColorRole.DANGER_BG);
		check(warnings, "GHOST_TEXT on PANEL_BG", ColorRole.GHOST_TEXT, ColorRole.PANEL_BG);
		return Collections.unmodifiableList(warnings);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private final EnumMap<ColorRole, Integer> colors = new EnumMap<>(ColorRole.class);

		private Builder() {
			colors.putAll(defaultColors());
		}

		public Builder set(ColorRole role, int color) {
			colors.put(role, color);
			return this;
		}

		public QuillThemeData build() {
			return new QuillThemeData(colors);
		}
	}

	private void check(List<String> warnings, String name, ColorRole foreground, ColorRole background) {
		if (!QuillContrast.meetsText(color(foreground), color(background))) {
			warnings.add(name + " is " + QuillContrast.describe(color(foreground), color(background)));
		}
	}

	private static EnumMap<ColorRole, Integer> defaultColors() {
		EnumMap<ColorRole, Integer> colors = new EnumMap<>(ColorRole.class);
		colors.put(ColorRole.SCREEN_BG, 0x00000000);
		colors.put(ColorRole.PANEL_BG, 0xF00E1217);
		colors.put(ColorRole.PANEL_BORDER, 0xFF2B3440);
		colors.put(ColorRole.PANEL_SHADOW, 0x66000000);
		colors.put(ColorRole.HEADER_BG, 0xE60A0E14);
		colors.put(ColorRole.DIVIDER, 0xFF242C36);
		colors.put(ColorRole.TITLE, 0xFFF2F5F8);
		colors.put(ColorRole.TEXT, 0xFFC6D0DA);
		colors.put(ColorRole.TEXT_SOFT, 0xFFA9B5C1);
		colors.put(ColorRole.MUTED, 0xFF7F8B99);
		colors.put(ColorRole.MUTED_DARK, 0xFF5F6B78);
		colors.put(ColorRole.ACCENT, 0xFFE0A93B);
		colors.put(ColorRole.ACCENT_HOVER, 0xFFF3C55E);
		colors.put(ColorRole.PRIMARY_BG, 0xFF241C11);
		colors.put(ColorRole.PRIMARY_BG_HOVER, 0xFF2F2517);
		colors.put(ColorRole.ON_ACCENT, 0xFFE0A93B);
		colors.put(ColorRole.ON_DANGER, 0xFFE06A6A);
		colors.put(ColorRole.GHOST_BG, 0x00000000);
		colors.put(ColorRole.GHOST_BORDER, 0x00000000);
		colors.put(ColorRole.GHOST_TEXT, 0xFF7F8B99);
		colors.put(ColorRole.FOCUS_RING, 0xFFE0A93B);
		colors.put(ColorRole.DANGER, 0xFFE06A6A);
		colors.put(ColorRole.DANGER_BG, 0xFF352024);
		colors.put(ColorRole.SUCCESS, 0xFF65D39A);
		colors.put(ColorRole.INFO, 0xFF7FB4F5);
		colors.put(ColorRole.BUTTON_BG, 0xFF1A212A);
		colors.put(ColorRole.BUTTON_HOVER, 0xFF232D38);
		colors.put(ColorRole.BUTTON_BORDER, 0xFF354150);
		colors.put(ColorRole.BUTTON_DISABLED, 0xFF141920);
		colors.put(ColorRole.BUTTON_DISABLED_BORDER, 0xFF232A33);
		colors.put(ColorRole.BUTTON_DISABLED_TEXT, 0xFF626D7A);
		colors.put(ColorRole.SELECTED_BG, 0xFF2A3140);
		colors.put(ColorRole.SURFACE, 0x99151C25);
		colors.put(ColorRole.SURFACE_ALT, 0x661A222C);
		colors.put(ColorRole.SURFACE_BORDER, 0xFF26313D);
		colors.put(ColorRole.SURFACE_HEADER, 0xFF10161E);
		colors.put(ColorRole.CHIP_BG, 0xFF1B242E);
		colors.put(ColorRole.CHIP_BORDER, 0xFF33404E);
		colors.put(ColorRole.MAP_BG, 0xFF0B1118);
		colors.put(ColorRole.MAP_GRID, 0x661E2936);
		colors.put(ColorRole.MAP_UNKNOWN, 0xFF18202A);
		return colors;
	}
}
