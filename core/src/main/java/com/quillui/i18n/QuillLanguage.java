package com.quillui.i18n;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.network.chat.Component;

/**
 * Thin bridge over Minecraft's language manager. UI text should still use
 * {@link Component#translatable(String, Object...)} and conventional
 * {@code assets/<namespace>/lang/<locale>.json} files; this helper only enumerates and switches
 * the active language at runtime.
 */
public final class QuillLanguage {
	public record LanguageOption(String code, Component name, boolean bidirectional) {
	}

	private QuillLanguage() {
	}

	public static Component text(String key, Object... arguments) {
		return Component.translatable(key, arguments);
	}

	public static String selectedCode() {
		return Minecraft.getInstance().getLanguageManager().getSelected();
	}

	public static LanguageOption selected() {
		LanguageManager manager = Minecraft.getInstance().getLanguageManager();
		LanguageInfo info = manager.getLanguage(manager.getSelected());
		return info == null ? null : option(manager.getSelected(), info);
	}

	public static List<LanguageOption> available() {
		LanguageManager manager = Minecraft.getInstance().getLanguageManager();
		return manager.getLanguages().entrySet().stream()
				.map(entry -> option(entry.getKey(), entry.getValue()))
				.toList();
	}

	public static boolean setSelected(String code) {
		if (code == null) {
			return false;
		}
		Minecraft client = Minecraft.getInstance();
		LanguageManager manager = client.getLanguageManager();
		if (!manager.getLanguages().containsKey(code)) {
			return false;
		}
		manager.setSelected(code);
		client.options.languageCode = code;
		client.options.save();
		client.reloadResourcePacks();
		return true;
	}

	/**
	 * Switches language and rebuilds the supplied screen after the resource reload finishes.
	 * Use this when the screen keeps prebuilt literal text; screens built from translatable
	 * components can simply call {@link #setSelected(String)}.
	 */
	public static boolean setSelected(String code, Supplier<Screen> reopen) {
		Minecraft client = Minecraft.getInstance();
		LanguageManager manager = client.getLanguageManager();
		if (code == null || !manager.getLanguages().containsKey(code)) {
			return false;
		}
		manager.setSelected(code);
		client.options.languageCode = code;
		client.options.save();
		client.reloadResourcePacks().thenRun(() ->
				client.execute(() -> {
					if (reopen != null) {
						client.setScreenAndShow(reopen.get());
					}
				})
		);
		return true;
	}

	private static LanguageOption option(String code, LanguageInfo info) {
		return new LanguageOption(code, info.toComponent(), info.bidirectional());
	}
}
