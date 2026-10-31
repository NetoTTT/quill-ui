package com.quillui.map;

import net.minecraft.core.BlockPos;

/**
 * A thin line between two block positions, drawn in block space so it pans and zooms with the map.
 */
public record QuillMapPath(BlockPos from, BlockPos to, int color) {
}
