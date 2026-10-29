package com.quillui.theme;

import java.util.function.Supplier;

/**
 * Ambient theme used while a screen builds its widget tree. Widgets created without an explicit
 * theme inherit the theme of the screen that is currently building.
 */
public final class QuillThemeScope {
	private static final ThreadLocal<QuillThemeData> CURRENT = new ThreadLocal<>();

	private QuillThemeScope() {
	}

	public static QuillThemeData current() {
		QuillThemeData theme = CURRENT.get();
		return theme == null ? QuillThemeData.DEFAULT : theme;
	}

	public static void run(QuillThemeData theme, Runnable action) {
		QuillThemeData previous = CURRENT.get();
		CURRENT.set(theme == null ? QuillThemeData.DEFAULT : theme);
		try {
			action.run();
		}
		finally {
			if (previous == null) {
				CURRENT.remove();
			}
			else {
				CURRENT.set(previous);
			}
		}
	}

	/** Scoped variant for builders that return a value. */
	public static <T> T supply(QuillThemeData theme, Supplier<T> action) {
		QuillThemeData previous = CURRENT.get();
		CURRENT.set(theme == null ? QuillThemeData.DEFAULT : theme);
		try {
			return action.get();
		}
		finally {
			if (previous == null) {
				CURRENT.remove();
			}
			else {
				CURRENT.set(previous);
			}
		}
	}
}
