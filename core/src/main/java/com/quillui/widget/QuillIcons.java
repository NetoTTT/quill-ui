package com.quillui.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/** Factories for the common {@link QuillIcon} sources: items and texture regions. */
public final class QuillIcons {
	private QuillIcons() {
	}

	/** An item stack rendered at its native 16x16 size. */
	public static QuillIcon item(ItemStack stack) {
		if (stack == null) {
			throw new IllegalArgumentException("stack must not be null");
		}
		ItemStack icon = stack.copy();
		return new QuillIcon() {
			@Override
			public int width() {
				return 16;
			}

			@Override
			public int height() {
				return 16;
			}

			@Override
			public void paint(GuiGraphicsExtractor graphics, int x, int y) {
				graphics.item(icon, x, y);
			}
		};
	}

	/**
	 * A region of a texture, addressed in pixels. The region is drawn with the icon's natural size
	 * and scaled by the host.
	 */
	public static QuillIcon texture(
			Identifier texture,
			int u,
			int v,
			int width,
			int height,
			int textureWidth,
			int textureHeight
	) {
		if (texture == null) {
			throw new IllegalArgumentException("texture must not be null");
		}
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException("icon size must be positive");
		}
		if (textureWidth <= 0 || textureHeight <= 0 || u < 0 || v < 0
				|| u + width > textureWidth || v + height > textureHeight) {
			throw new IllegalArgumentException("icon region must fit inside the texture");
		}

		float u0 = u / (float) textureWidth;
		float u1 = (u + width) / (float) textureWidth;
		float v0 = v / (float) textureHeight;
		float v1 = (v + height) / (float) textureHeight;
		return new QuillIcon() {
			@Override
			public int width() {
				return width;
			}

			@Override
			public int height() {
				return height;
			}

			@Override
			public void paint(GuiGraphicsExtractor graphics, int x, int y) {
				graphics.blit(
						texture,
						x,
						y,
						x + width,
						y + height,
						u0,
						u1,
						v0,
						v1
				);
			}
		};
	}
}
