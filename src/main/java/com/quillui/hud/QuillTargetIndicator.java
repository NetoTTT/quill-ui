package com.quillui.hud;

import com.quillui.render.QuillRender;
import com.quillui.theme.QuillColors;
import com.quillui.theme.QuillThemeData;
import java.util.function.Function;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/**
 * Generic off-screen target indicator. It places a badge and an outward chevron on a ring around
 * the crosshair when a world position leaves the camera frustum, with an optional distance label.
 * It has no dependency on a map, team, claim or economy system; callers provide the target, colour
 * and optional icon/label renderers.
 */
public final class QuillTargetIndicator {
	@FunctionalInterface
	public interface IconRenderer {
		void render(GuiGraphicsExtractor graphics, int x, int y, int size);
	}

	private Vec3 target;
	private QuillThemeData theme = QuillThemeData.DEFAULT;
	private QuillThemeData.ColorRole colorRole = QuillThemeData.ColorRole.ACCENT;
	private QuillThemeData.ColorRole backgroundColorRole = QuillThemeData.ColorRole.SURFACE_HEADER;
	private Integer customColor;
	private Integer customBackground;
	private IconRenderer iconRenderer;
	private Function<Double, Component> distanceFormatter = distance ->
			Component.literal(Math.round(distance) + " m");
	private boolean distanceVisible = true;
	private boolean chevronVisible = true;
	private boolean showWhenOnScreen;
	private double maxRange = 96.0D;
	private double ringFactor = 0.30D;
	private double minRingRadius = 60.0D;
	private double maxRingRadius = 180.0D;
	private int edgeMargin = 24;
	private int badgeSize = 11;
	private double onScreenThreshold = 0.98D;

	public QuillTargetIndicator() {
	}

	public QuillTargetIndicator(QuillThemeData theme) {
		this(theme, QuillThemeData.ColorRole.ACCENT);
	}

	public QuillTargetIndicator(QuillThemeData.ColorRole colorRole) {
		this(QuillThemeData.DEFAULT, colorRole);
	}

	public QuillTargetIndicator(QuillThemeData theme, QuillThemeData.ColorRole colorRole) {
		this.theme = theme == null ? QuillThemeData.DEFAULT : theme;
		this.colorRole = colorRole == null ? QuillThemeData.ColorRole.ACCENT : colorRole;
	}

	public QuillTargetIndicator(int color) {
		this.customColor = color;
	}

	public QuillTargetIndicator setTarget(Vec3 target) {
		this.target = target;
		return this;
	}

	public QuillTargetIndicator setTarget(BlockPos target) {
		this.target = target == null ? null : Vec3.atCenterOf(target);
		return this;
	}

	public QuillTargetIndicator clearTarget() {
		target = null;
		return this;
	}

	public Vec3 target() {
		return target;
	}

	public QuillTargetIndicator setColor(QuillThemeData.ColorRole colorRole) {
		this.colorRole = colorRole == null ? QuillThemeData.ColorRole.ACCENT : colorRole;
		this.customColor = null;
		return this;
	}

	public QuillTargetIndicator setColor(QuillThemeData theme, QuillThemeData.ColorRole colorRole) {
		this.theme = theme == null ? QuillThemeData.DEFAULT : theme;
		return setColor(colorRole);
	}

	public QuillTargetIndicator setColor(int color) {
		this.customColor = color;
		return this;
	}

	public QuillTargetIndicator setBackgroundColor(QuillThemeData.ColorRole backgroundColorRole) {
		this.backgroundColorRole = backgroundColorRole == null
				? QuillThemeData.ColorRole.SURFACE_HEADER
				: backgroundColorRole;
		this.customBackground = null;
		return this;
	}

	public QuillTargetIndicator setBackgroundColor(int backgroundColor) {
		this.customBackground = backgroundColor;
		return this;
	}

	public QuillTargetIndicator setIcon(IconRenderer iconRenderer) {
		this.iconRenderer = iconRenderer;
		return this;
	}

	public QuillTargetIndicator setDistanceFormatter(Function<Double, Component> distanceFormatter) {
		this.distanceFormatter = distanceFormatter;
		return this;
	}

	public QuillTargetIndicator setDistanceVisible(boolean distanceVisible) {
		this.distanceVisible = distanceVisible;
		return this;
	}

	public QuillTargetIndicator setChevronVisible(boolean chevronVisible) {
		this.chevronVisible = chevronVisible;
		return this;
	}

	public QuillTargetIndicator setShowWhenOnScreen(boolean showWhenOnScreen) {
		this.showWhenOnScreen = showWhenOnScreen;
		return this;
	}

	public QuillTargetIndicator setMaxRange(double maxRange) {
		this.maxRange = Math.max(1.0D, maxRange);
		return this;
	}

	public QuillTargetIndicator setRing(double factor, double minRadius, double maxRadius) {
		ringFactor = Math.max(0.01D, factor);
		minRingRadius = Math.max(1.0D, minRadius);
		maxRingRadius = Math.max(minRingRadius, maxRadius);
		return this;
	}

	public QuillTargetIndicator setEdgeMargin(int edgeMargin) {
		this.edgeMargin = Math.max(0, edgeMargin);
		return this;
	}

	public QuillTargetIndicator setBadgeSize(int badgeSize) {
		this.badgeSize = Math.max(2, Math.min(64, badgeSize));
		return this;
	}

	public QuillTargetIndicator setOnScreenThreshold(double onScreenThreshold) {
		this.onScreenThreshold = Mth.clamp(onScreenThreshold, 0.0D, 2.0D);
		return this;
	}

	public void render(GuiGraphicsExtractor graphics) {
		Minecraft client = Minecraft.getInstance();
		if (target == null || client.player == null || client.level == null
				|| graphics.guiWidth() <= 0 || graphics.guiHeight() <= 0) {
			return;
		}

		Camera camera = client.gameRenderer.mainCamera();
		if (!camera.isInitialized()) {
			return;
		}

		Vec3 toTarget = target.subtract(camera.position());
		double distance = toTarget.length();
		if (distance < 0.5D || distance > maxRange) {
			return;
		}

		Vec3 forward = new Vec3(camera.forwardVector());
		Vec3 left = new Vec3(camera.leftVector());
		Vec3 up = new Vec3(camera.upVector());
		double z = toTarget.dot(forward);
		double x = -toTarget.dot(left);
		double y = toTarget.dot(up);

		double verticalHalf = Math.toRadians(camera.getFov()) * 0.5D;
		double aspect = graphics.guiWidth() / (double) Math.max(1, graphics.guiHeight());
		double horizontalHalf = Math.atan(Math.tan(verticalHalf) * aspect);

		if (z > 0.0D) {
			double screenX = x / (z * Math.tan(horizontalHalf));
			double screenY = y / (z * Math.tan(verticalHalf));
			if (!showWhenOnScreen
					&& Math.abs(screenX) < onScreenThreshold
					&& Math.abs(screenY) < onScreenThreshold) {
				return;
			}
		}

		double dx = x;
		double dy = -y;
		if (Math.abs(dx) < 1.0E-4D && Math.abs(dy) < 1.0E-4D) {
			dy = 1.0D;
		}
		double length = Math.hypot(dx, dy);
		double ux = dx / length;
		double uy = dy / length;

		int width = graphics.guiWidth();
		int height = graphics.guiHeight();
		double radius = Mth.clamp(
				Math.min(width, height) * ringFactor,
				minRingRadius,
				maxRingRadius
		);
		double px = Mth.clamp(
				width / 2.0D + ux * radius,
				edgeMargin,
				width - edgeMargin
		);
		double py = Mth.clamp(
				height / 2.0D + uy * radius,
				edgeMargin,
				height - edgeMargin
		);

		int cx = (int) px;
		int cy = (int) py;
		int color = customColor != null ? customColor : theme.color(colorRole);
		int backgroundColor = customBackground != null
				? customBackground
				: QuillColors.withAlpha(theme.color(backgroundColorRole), 0xCC);
		graphics.fill(cx - badgeSize, cy - badgeSize, cx + badgeSize, cy + badgeSize, backgroundColor);
		QuillRender.border(graphics, cx - badgeSize, cy - badgeSize, badgeSize * 2, badgeSize * 2, color);

		if (iconRenderer != null) {
			int iconSize = Math.max(1, badgeSize * 2 - 6);
			iconRenderer.render(graphics, cx - iconSize / 2, cy - iconSize / 2, iconSize);
		} else {
			graphics.fill(cx - 3, cy - 3, cx + 4, cy + 4, color);
		}

		if (chevronVisible) {
			float angle = (float) Math.atan2(uy, ux);
			graphics.pose().pushMatrix();
			graphics.pose().translate((float) cx, (float) cy);
			graphics.pose().rotate(angle);
			int base = badgeSize + 2;
			for (int step = 0; step < 6; step++) {
				int half = 5 - step;
				graphics.fill(base + step, -half, base + step + 1, half + 1, color);
			}
			graphics.pose().popMatrix();
		}

		if (distanceVisible && distanceFormatter != null) {
			Component label = distanceFormatter.apply(distance);
			if (label != null && !label.getString().isBlank()) {
				int textWidth = client.font.width(label);
				int labelY = cy + badgeSize + 5;
				if (labelY + 9 > height) {
					labelY = cy - badgeSize - 13;
				}
				graphics.fill(
						cx - textWidth / 2 - 3,
						labelY - 2,
						cx + textWidth / 2 + 3,
						labelY + 9,
						backgroundColor
				);
				QuillRender.centeredText(graphics, client.font, label, cx, labelY, color);
			}
		}
	}
}
