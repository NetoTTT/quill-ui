package com.quillui.action;

import net.minecraft.client.Minecraft;

/** Sends a slash command through the vanilla client command path. */
public final class QuillCommandSink implements QuillActionSink<String> {
	private static final QuillCommandSink INSTANCE = new QuillCommandSink();

	private QuillCommandSink() {
	}

	public static QuillCommandSink get() {
		return INSTANCE;
	}

	@Override
	public void execute(String action) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || action == null || action.isBlank()) {
			return;
		}
		client.player.connection.sendCommand(action);
	}
}
