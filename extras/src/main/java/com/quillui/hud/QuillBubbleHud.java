package com.quillui.hud;

import com.quillui.render.QuillRender;
import com.quillui.theme.QuillColors;
import com.quillui.theme.QuillThemeData;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * World-anchored speech bubbles. The host supplies {@link QuillSpeechBubble}s every frame; this HUD
 * projects them onto the screen, smooths movement, fades by distance and draws a themed bubble with
 * a tail. Occlusion and the through-walls flag are handled here, so screens/mobs stay independent.
 */
public final class QuillBubbleHud implements HudElement {
	private static final int MAX_TEXT_WIDTH = 150;

	private final QuillSpeechBubbleSource source;
	private final Supplier<QuillThemeData> theme;
	private final Map<Object, Bubble> bubbles = new HashMap<>();

	public QuillBubbleHud(QuillSpeechBubbleSource source, Supplier<QuillThemeData> theme) {
		this.source = source;
		this.theme = theme;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker delta) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.level == null || client.gui.screen() != null) {
			bubbles.clear();
			return;
		}

		Camera camera = client.gameRenderer.mainCamera();
		if (!camera.isInitialized()) {
			bubbles.clear();
			return;
		}

		float partialTick = delta.getGameTimeDeltaPartialTick(false);
		float seconds = Math.max(0.001F, delta.getRealtimeDeltaTicks() / 20.0F);
		float fadeStep = 1.0F - (float) Math.exp(-10.0F * seconds);
		float smoothStep = 1.0F - (float) Math.exp(-16.0F * seconds);

		Vec3 forward = new Vec3(camera.forwardVector());
		Vec3 left = new Vec3(camera.leftVector());
		Vec3 up = new Vec3(camera.upVector());
		double verticalHalf = Math.toRadians(camera.getFov()) * 0.5D;
		double aspect = graphics.guiWidth() / (double) Math.max(1, graphics.guiHeight());
		double horizontalHalf = Math.atan(Math.tan(verticalHalf) * aspect);
		int screenWidth = graphics.guiWidth();
		int screenHeight = graphics.guiHeight();

		Set<Object> visible = new HashSet<>();
		List<QuillSpeechBubble> supplied = source == null ? List.of() : source.bubbles(partialTick);
		if (supplied == null) {
			supplied = List.of();
		}

		for (QuillSpeechBubble entry : supplied) {
			if (entry == null || entry.text().getString().isBlank()) {
				continue;
			}

			Vec3 toBubble = entry.position().subtract(camera.position());
			double distance = toBubble.length();
			if (distance > entry.maxDistance() || distance < 0.2D) {
				continue;
			}
			if (!entry.throughWalls() && occluded(client, camera.position(), entry.position())) {
				continue;
			}

			double z = toBubble.dot(forward);
			if (z <= 0.1D) {
				continue;
			}

			double x = -toBubble.dot(left);
			double y = toBubble.dot(up);
			double normX = x / (z * Math.tan(horizontalHalf));
			double normY = y / (z * Math.tan(verticalHalf));
			if (Math.abs(normX) > 1.05D || Math.abs(normY) > 1.05D) {
				continue;
			}

			float targetX = (float) (screenWidth * 0.5D + normX * screenWidth * 0.5D);
			float targetY = (float) (screenHeight * 0.5D - normY * screenHeight * 0.5D);

			Bubble bubble = bubbles.computeIfAbsent(entry.key(), key -> new Bubble(key.hashCode()));
			bubble.setMessage(entry.text(), client.font);
			bubble.accent = entry.accentColor();
			bubble.visible = true;
			bubble.targetAlpha = (float) Mth.clamp((entry.maxDistance() - distance) / 2.0D, 0.0D, 1.0D);

			if (!bubble.positioned || Math.abs(targetX - bubble.x) + Math.abs(targetY - bubble.y) > 96.0F) {
				bubble.x = targetX;
				bubble.y = targetY;
				bubble.positioned = true;
			}
			else {
				bubble.x += (targetX - bubble.x) * smoothStep;
				bubble.y += (targetY - bubble.y) * smoothStep;
			}
			visible.add(entry.key());
		}

		Iterator<Map.Entry<Object, Bubble>> iterator = bubbles.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Object, Bubble> entry = iterator.next();
			Bubble bubble = entry.getValue();
			if (!visible.contains(entry.getKey())) {
				bubble.targetAlpha = 0.0F;
			}

			bubble.alpha += (bubble.targetAlpha - bubble.alpha) * fadeStep;
			if (!bubble.visible && bubble.alpha < 0.02F) {
				iterator.remove();
				continue;
			}
			if (bubble.alpha < 0.02F) {
				continue;
			}

			float bob = (float) Math.sin((client.level.getGameTime() + partialTick) * 0.16D
					+ bubble.phase) * 1.4F;
			drawBubble(graphics, bubble, screenWidth, screenHeight, bob);
			bubble.visible = false;
		}
	}

	private void drawBubble(
			GuiGraphicsExtractor graphics, Bubble bubble, int screenWidth, int screenHeight, float bob
	) {
		int x = Math.round(bubble.x - bubble.width / 2.0F);
		int y = Math.round(bubble.y - bubble.height - 13.0F + bob);
		x = Math.max(4, Math.min(x, screenWidth - bubble.width - 4));
		y = Math.max(4, y);

		QuillThemeData resolved = theme == null ? null : theme.get();
		if (resolved == null) {
			resolved = QuillThemeData.DEFAULT;
		}

		float alpha = Mth.clamp(bubble.alpha, 0.0F, 1.0F);
		int border = withAlpha(bubble.accent, alpha);
		int text = withAlpha(resolved.color(QuillThemeData.ColorRole.TEXT), alpha);
		int shadow = withAlpha(resolved.color(QuillThemeData.ColorRole.PANEL_SHADOW), alpha);
		int background = withAlpha(resolved.color(QuillThemeData.ColorRole.PANEL_BG), alpha);

		graphics.fill(x + 2, y + 2, x + bubble.width + 2, y + bubble.height + 3, shadow);
		graphics.fill(x, y, x + bubble.width, y + bubble.height, background);
		QuillRender.border(graphics, x, y, bubble.width, bubble.height, border);

		int tailX = Math.round(bubble.x);
		graphics.fill(
				tailX - 3,
				y + bubble.height,
				tailX + 3,
				y + bubble.height + 4,
				background
		);
		graphics.fill(
				tailX - 3,
				y + bubble.height,
				tailX + 3,
				y + bubble.height + 1,
				border
		);

		Font font = Minecraft.getInstance().font;
		for (int index = 0; index < bubble.lines.size(); index++) {
			graphics.text(
					font,
					bubble.lines.get(index),
					x + 6,
					y + 5 + index * 9,
					text,
					false
			);
		}
	}

	private static boolean occluded(Minecraft client, Vec3 from, Vec3 to) {
		ClipContext context = new ClipContext(
				from,
				to,
				ClipContext.Block.COLLIDER,
				ClipContext.Fluid.NONE,
				client.player
		);
		BlockHitResult hit = client.level.clip(context);
		return hit.getType() != HitResult.Type.MISS;
	}

	public void clear() {
		bubbles.clear();
	}

	private static int withAlpha(int color, float alpha) {
		int original = QuillColors.alpha(color);
		int applied = Mth.clamp((int) (original * alpha), 0, 255);
		return QuillColors.withAlpha(color, applied);
	}

	private static final class Bubble {
		private String key;
		private List<FormattedCharSequence> lines = List.of();
		private int width;
		private int height;
		private int accent = QuillColors.rgb(224, 169, 59);
		private float x;
		private float y;
		private float alpha;
		private float targetAlpha;
		private float phase;
		private boolean visible;
		private boolean positioned;

		private Bubble(int phaseSeed) {
			this.phase = (phaseSeed & 0xFFFF) / 65535.0F * (float) (Math.PI * 2.0D);
		}

		private void setMessage(Component message, Font font) {
			String nextKey = message.getString();
			if (nextKey.equals(key)) {
				return;
			}

			key = nextKey;
			lines = font.split(message, MAX_TEXT_WIDTH);
			int textWidth = 0;
			for (FormattedCharSequence line : lines) {
				textWidth = Math.max(textWidth, font.width(line));
			}
			width = Math.max(34, textWidth + 12);
			height = lines.size() * 9 + 8;
			alpha = Math.min(alpha, 0.25F);
		}
	}
}
