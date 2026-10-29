package com.quillui.map;

import net.minecraft.core.BlockPos;

/**
 * A generic rectangular overlay on a {@link QuillMap}. Coordinates are world block positions.
 */
public record QuillMapZone(
		BlockPos from,
		BlockPos to,
		int fillColor,
		int borderColor,
		boolean dashed
) {
	public int minX() {
		return Math.min(from.getX(), to.getX());
	}

	public int maxX() {
		return Math.max(from.getX(), to.getX());
	}

	public int minZ() {
		return Math.min(from.getZ(), to.getZ());
	}

	public int maxZ() {
		return Math.max(from.getZ(), to.getZ());
	}
}
