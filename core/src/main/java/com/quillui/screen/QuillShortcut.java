package com.quillui.screen;

/**
 * A keyboard shortcut registered on a {@link QuillScreen}.
 *
 * @param key Minecraft/InputConstants key code
 * @param action action to run when the key is pressed
 * @param allowWhenFocused whether the shortcut should still run while a child widget has focus
 */
public record QuillShortcut(int key, Runnable action, boolean allowWhenFocused) {
	public static QuillShortcut of(int key, Runnable action) {
		return new QuillShortcut(key, action, false);
	}

	public static QuillShortcut global(int key, Runnable action) {
		return new QuillShortcut(key, action, true);
	}
}
