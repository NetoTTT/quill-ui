package com.quillui.api;

import net.fabricmc.loader.api.FabricLoader;

/**
 * Stable entry point for API compatibility checks.
 *
 * <p>{@link #API_LEVEL} increases only when a change is binary incompatible. Additive changes do
 * not increase it. Consumers that need to support older runtimes can compare this level against
 * the level they were compiled for.</p>
 */
public final class QuillApi {
	public static final String MOD_ID = "quillui";

	/**
	 * Binary API level. Increment this when an existing public signature is removed or changed.
	 */
	public static final int API_LEVEL = 3;

	private QuillApi() {
	}

	/**
	 * Returns the loaded Quill UI version, or {@code "unknown"} outside a Fabric runtime.
	 */
	public static String version() {
		return FabricLoader.getInstance()
				.getModContainer(MOD_ID)
				.map(container -> container.getMetadata().getVersion().getFriendlyString())
				.orElse("unknown");
	}

	/**
	 * Returns whether this runtime supports the requested binary API level.
	 */
	public static boolean supports(int requiredLevel) {
		return API_LEVEL >= requiredLevel;
	}
}
