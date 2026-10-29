package com.quillui.map;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

/**
 * A labeled point of interest on a {@link QuillMap}.
 */
public record QuillMapMarker(BlockPos position, Component label, int color) {
}
