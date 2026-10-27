package com.quillui.map;

import com.quillui.theme.QuillColors;
import com.quillui.theme.QuillThemeData;
import com.quillui.render.QuillRender;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.NativeImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.MapColor;

/**
 * Interactive top-down world map. It renders terrain into a dynamic texture once per refresh and
 * exposes a generic selection rectangle, overlays and markers. It has no LaborCraft dependency.
 */
public final class QuillMap extends AbstractWidget implements AutoCloseable {
	public enum Layer {
		AUTO,
		SURFACE,
		UNDERGROUND;

		public Layer next() {
			Layer[] all = values();
			return all[(ordinal() + 1) % all.length];
		}
	}

	public enum InteractionMode {
		SELECT,
		PAN
	}

	public enum MouseAction {
		SELECT,
		PAN,
		NONE
	}

	public record MouseBindings(MouseAction left, MouseAction right, MouseAction middle) {
		public static final MouseBindings DEFAULT = new MouseBindings(MouseAction.SELECT, MouseAction.PAN, MouseAction.PAN);

		public MouseAction action(int button) {
			return switch (button) {
				case InputConstants.MOUSE_BUTTON_LEFT -> left;
				case InputConstants.MOUSE_BUTTON_RIGHT -> right;
				case InputConstants.MOUSE_BUTTON_MIDDLE -> middle;
				default -> MouseAction.NONE;
			};
		}
	}

	private record TileKey(ResourceKey<Level> dimension, int chunkX, int chunkZ, int pivot, boolean underground) {
	}

	private static final class MapTile {
		private final int[] colors;
		private final long generatedTick;

		private MapTile(int[] colors, long generatedTick) {
			this.colors = colors;
			this.generatedTick = generatedTick;
		}
	}

	private record ChunkCoord(int x, int z, int distance) {
	}

	private static final int BG_DARK = 0xFF0B1118;
	private static final int GRID_OVERLAY = 0x661E2936;
	private static final int NO_FLOOR = Integer.MIN_VALUE;
	private static final int MAX_SELECTION = 32;

	private BlockPos center;
	private final int maxRadius;
	private final int maxSelectionSize;
	private final QuillThemeData theme;
	private final Identifier textureId;
	private final List<QuillMapZone> zones = new ArrayList<>();
	private final List<QuillMapMarker> markers = new ArrayList<>();

	private DynamicTexture texture;
	private boolean dirty = true;
	private long lastUpdateTick;
	private Layer layer = Layer.AUTO;
	private boolean lastUnderground;
	private int pivotY;
	private Component layerLabel = Component.empty();
	private BlockPos from;
	private BlockPos to;
	private boolean dragging;
	private boolean readOnly;
	private int pixelsPerBlock = 2;
	private InteractionMode interactionMode = InteractionMode.SELECT;
	private MouseBindings mouseBindings = MouseBindings.DEFAULT;
	private boolean showRadiusLimit;
	private final Map<TileKey, MapTile> tileCache = new LinkedHashMap<>(512, 0.75F, true) {
		@Override
		protected boolean removeEldestEntry(Map.Entry<TileKey, MapTile> eldest) {
			return size() > 4096;
		}
	};
	private boolean panning;
	private BlockPos panStartCenter;
	private double panStartMouseX;
	private double panStartMouseY;
	private int undergroundScanDepth = 48;
	private long lastUpdateNanos;
	private int lastTilesGenerated;
	private int lastTileHits;
	private boolean tilesPending;
	private long tileBudgetNanos = 4_000_000L;
	private long tileRefreshTicks = 600L;
	private Consumer<QuillMap> selectionListener;
	private Consumer<QuillMap> selectionCommitListener;
	private Predicate<Entity> entityFilter;
	private Function<Entity, QuillMapMarker> entityMarkerFactory;

	public QuillMap(
			int x,
			int y,
			int width,
			int height,
			BlockPos center,
			int maxRadius,
			QuillThemeData theme
	) {
		this(x, y, width, height, center, maxRadius, 32, theme);
	}

	public QuillMap(
			int x,
			int y,
			int width,
			int height,
			BlockPos center,
			int maxRadius,
			int maxSelectionSize,
			QuillThemeData theme
	) {
		super(x, y, width, height, Component.translatable("quillui.map"));
		this.center = center;
		this.maxRadius = Math.max(1, Math.min(128, maxRadius));
		this.maxSelectionSize = Math.max(1, Math.min(64, maxSelectionSize));
		this.theme = theme;
		this.textureId = Identifier.fromNamespaceAndPath(
				"quillui",
				"dynamic/map_" + System.identityHashCode(this)
		);

		int span = this.maxRadius * 2 + 1;
		this.pixelsPerBlock = Mth.clamp(Math.min(width / span, height / span), 1, 4);
		this.lastUnderground = undergroundActive();
		this.layerLabel = buildLayerLabel(this.lastUnderground, effectivePivotY());
	}

	public BlockPos center() {
		return center;
	}

	public void setCenter(BlockPos center) {
		if (center == null || center.equals(this.center)) {
			return;
		}
		this.center = center;
		this.dirty = true;
	}

	public void setUndergroundScanDepth(int depth) {
		undergroundScanDepth = Math.max(4, Math.min(256, depth));
		tileCache.clear();
		dirty = true;
	}

	public long lastUpdateNanos() {
		return lastUpdateNanos;
	}

	public void resize(int width, int height) {
		this.width = Math.max(1, width);
		this.height = Math.max(1, height);
		dirty = true;
	}

	public void panBy(int dx, int dz) {
		if (dx == 0 && dz == 0) {
			return;
		}
		center = center.offset(dx, 0, dz);
		dirty = true;
	}

	public InteractionMode interactionMode() {
		return interactionMode;
	}

	public void setInteractionMode(InteractionMode mode) {
		if (mode != null) {
			interactionMode = mode;
		}
	}

	public MouseBindings mouseBindings() {
		return mouseBindings;
	}

	public void setMouseBindings(MouseBindings bindings) {
		if (bindings != null) {
			mouseBindings = bindings;
			interactionMode = bindings.left() == MouseAction.PAN
					? InteractionMode.PAN
					: InteractionMode.SELECT;
		}
	}

	public boolean radiusLimitVisible() {
		return showRadiusLimit;
	}

	public void setRadiusLimitVisible(boolean visible) {
		showRadiusLimit = visible;
	}

	public BlockPos from() {
		return from;
	}

	public BlockPos to() {
		return to;
	}

	public Layer layer() {
		return layer;
	}

	public void setLayer(Layer layer) {
		if (layer == null || this.layer == layer) {
			return;
		}
		this.layer = layer;
		this.dirty = true;
	}

	public boolean readOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public void setSelection(BlockPos from, BlockPos to) {
		this.from = from;
		this.to = to;
		notifySelection();
	}

	public void clearSelection() {
		from = null;
		to = null;
		notifySelection();
	}

	public int selectionArea() {
		return selectionWidth() * selectionDepth();
	}

	public boolean hasSelection() {
		return from != null && to != null;
	}

	public int selectionWidth() {
		return hasSelection() ? Math.abs(to.getX() - from.getX()) + 1 : 0;
	}

	public int selectionDepth() {
		return hasSelection() ? Math.abs(to.getZ() - from.getZ()) + 1 : 0;
	}

	public boolean selectionFitsLimit() {
		return hasSelection()
				&& selectionWidth() <= maxSelectionSize
				&& selectionDepth() <= maxSelectionSize;
	}

	public void setZones(List<QuillMapZone> zones) {
		this.zones.clear();
		if (zones != null) {
			this.zones.addAll(zones);
		}
	}

	public void addZone(QuillMapZone zone) {
		if (zone != null) {
			zones.add(zone);
		}
	}

	public List<QuillMapZone> zones() {
		return List.copyOf(zones);
	}

	public void setMarkers(List<QuillMapMarker> markers) {
		this.markers.clear();
		if (markers != null) {
			this.markers.addAll(markers);
		}
	}

	public void addMarker(QuillMapMarker marker) {
		if (marker != null) {
			markers.add(marker);
		}
	}

	public void setSelectionListener(Consumer<QuillMap> selectionListener) {
		this.selectionListener = selectionListener;
	}

	/**
	 * Called once when a selection drag finishes. Use this to turn the selected area into a claim,
	 * build region, query or any other generic area-based action.
	 */
	public void setSelectionCommitListener(Consumer<QuillMap> selectionCommitListener) {
		this.selectionCommitListener = selectionCommitListener;
	}

	/**
	 * Supplies generic entity markers. The filter and factory are evaluated against the current
	 * client level for each map render.
	 */
	public void setEntityProvider(
			Predicate<Entity> filter, Function<Entity, QuillMapMarker> markerFactory
	) {
		this.entityFilter = filter;
		this.entityMarkerFactory = markerFactory;
	}

	public void clearEntityProvider() {
		entityFilter = null;
		entityMarkerFactory = null;
	}

	public Component layerLabel() {
		return layerLabel;
	}

	@Override
	public void close() {
		tileCache.clear();
		tilesPending = false;
		if (texture != null) {
			try {
				Minecraft.getInstance().getTextureManager().release(textureId);
			} catch (Exception ignored) {
			}
			texture = null;
		}
	}

	@Override
	protected void extractWidgetRenderState(
			GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta
	) {
		long currentTick = Minecraft.getInstance().level == null
				? 0L
				: Minecraft.getInstance().level.getGameTime();
		boolean underground = undergroundActive();
		int pivot = effectivePivotY();
		long refreshTicks = underground ? 400L : 100L;
		boolean pivotMoved = underground && Math.abs(pivot - pivotY) >= 8;

		if (dirty || texture == null || underground != lastUnderground || pivotMoved
				|| currentTick - lastUpdateTick >= refreshTicks) {
			pivotY = pivot;
			lastUnderground = underground;
			updateMapTexture(underground);
			lastUpdateTick = currentTick;
		}

		updateLayerLabel(underground, pivot);
		graphics.fill(getX(), getY(), getX() + width, getY() + height, BG_DARK);
		graphics.enableScissor(getX(), getY(), getX() + width, getY() + height);
		graphics.blit(textureId, getX(), getY(), getX() + width, getY() + height, 0.0F, 1.0F, 0.0F, 1.0F);
		drawGrid(graphics);
		drawZones(graphics);
		if (showRadiusLimit) {
			drawRadiusLimit(graphics);
		}
		drawSelection(graphics);
		drawMarkers(graphics);
		drawCenterMarker(graphics);
		drawHud(graphics);
		graphics.disableScissor();
		QuillRender.border(
				graphics, getX(), getY(), width, height,
				theme.color(QuillThemeData.ColorRole.SURFACE_BORDER)
		);
	}

	private void updateMapTexture(boolean underground) {
		long updateStart = System.nanoTime();
		lastTilesGenerated = 0;
		lastTileHits = 0;
		ensureTexture();
		NativeImage image = texture.getPixels();
		if (image == null) {
			return;
		}

		ClientLevel level = Minecraft.getInstance().level;
		if (level == null) {
			image.fillRect(0, 0, width, height, BG_DARK);
			texture.upload();
			dirty = false;
			tilesPending = false;
			lastUpdateNanos = System.nanoTime() - updateStart;
			return;
		}

		image.fillRect(0, 0, width, height, BG_DARK);
		ensureVisibleTiles(level, underground, level.getGameTime(), updateStart);
		composeTiles(level, underground, image);
		texture.upload();
		dirty = tilesPending;
		lastUpdateNanos = System.nanoTime() - updateStart;
		if (underground && !tilesPending) {
			System.out.printf("[QuillMap] underground %dx%d zoom=%d: %.1f ms, generated=%d, hits=%d, pending=%s%n",
					width, height, pixelsPerBlock, lastUpdateNanos / 1_000_000.0D,
					lastTilesGenerated, lastTileHits, tilesPending);
		}
	}

	private void ensureTexture() {
		if (texture != null) {
			NativeImage pixels = texture.getPixels();
			if (pixels != null && pixels.getWidth() == width && pixels.getHeight() == height) {
				return;
			}
			try {
				Minecraft.getInstance().getTextureManager().release(textureId);
			} catch (Exception ignored) {
			}
			texture = null;
		}
		texture = new DynamicTexture("quillui_map", width, height, false);
		Minecraft.getInstance().getTextureManager().register(textureId, texture);
	}

	private void ensureVisibleTiles(
			ClientLevel level, boolean underground, long tick, long updateStart
	) {
		List<ChunkCoord> chunks = visibleChunks();
		long deadline = updateStart + tileBudgetNanos;
		ResourceKey<Level> dimension = level.dimension();
		boolean pending = false;

		for (ChunkCoord chunk : chunks) {
			TileKey key = new TileKey(dimension, chunk.x(), chunk.z(), underground ? pivotY : 0, underground);
			MapTile existing = tileCache.get(key);
			if (existing != null && tick - existing.generatedTick <= tileRefreshTicks) {
				lastTileHits++;
				continue;
			}
			if (!level.hasChunkAt((chunk.x() << 4) + 8, (chunk.z() << 4) + 8)) {
				continue;
			}
			if (lastTilesGenerated > 0 && System.nanoTime() >= deadline) {
				pending = true;
				continue;
			}

			MapTile built = buildTile(level, chunk.x(), chunk.z(), underground, tick);
			if (built == null) {
				pending = true;
			} else {
				tileCache.put(key, built);
				lastTilesGenerated++;
			}
		}
		tilesPending = pending;
	}

	private List<ChunkCoord> visibleChunks() {
		int minChunkX = Math.floorDiv(screenToBlockX(getX()), 16);
		int maxChunkX = Math.floorDiv(screenToBlockX(getX() + width - 1), 16);
		int minChunkZ = Math.floorDiv(screenToBlockZ(getY()), 16);
		int maxChunkZ = Math.floorDiv(screenToBlockZ(getY() + height - 1), 16);
		int centerChunkX = Math.floorDiv(center.getX(), 16);
		int centerChunkZ = Math.floorDiv(center.getZ(), 16);
		List<ChunkCoord> chunks = new ArrayList<>();
		for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
			for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
				int distance = Math.abs(chunkX - centerChunkX) + Math.abs(chunkZ - centerChunkZ);
				chunks.add(new ChunkCoord(chunkX, chunkZ, distance));
			}
		}
		chunks.sort(Comparator.comparingInt(ChunkCoord::distance));
		return chunks;
	}

	private MapTile buildTile(
			ClientLevel level, int chunkX, int chunkZ, boolean underground, long tick
	) {
		int baseX = chunkX << 4;
		int baseZ = chunkZ << 4;
		if (!level.hasChunkAt(baseX + 8, baseZ + 8)) {
			return null;
		}
		int[] colors = new int[256];
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		BlockPos.MutableBlockPos north = new BlockPos.MutableBlockPos();
		for (int localX = 0; localX < 16; localX++) {
			int worldX = baseX + localX;
			for (int localZ = 0; localZ < 16; localZ++) {
				int worldZ = baseZ + localZ;
				if (!level.hasChunkAt(worldX, worldZ)) {
					continue;
				}
				int color = underground
						? undergroundColumnColor(level, worldX, worldZ, pos)
						: surfaceColumnColor(level, worldX, worldZ, pos, north);
				colors[(localZ << 4) | localX] = color;
			}
		}
		return new MapTile(colors, tick);
	}

	private int surfaceColumnColor(
			ClientLevel level, int worldX, int worldZ,
			BlockPos.MutableBlockPos pos, BlockPos.MutableBlockPos north
	) {
		int surfaceY = level.getHeightmapPos(
				Heightmap.Types.MOTION_BLOCKING,
				pos.set(worldX, center.getY(), worldZ)
		).getY() - 1;
		BlockState state = level.getBlockState(pos.set(worldX, surfaceY, worldZ));
		MapColor mapColor = state.getMapColor(level, pos);
		if (mapColor == MapColor.NONE) {
			return 0xFF18202A;
		}
		int northY = surfaceY;
		if (level.hasChunkAt(worldX, worldZ - 1)) {
			northY = level.getHeightmapPos(
					Heightmap.Types.MOTION_BLOCKING,
					north.set(worldX, center.getY(), worldZ - 1)
			).getY() - 1;
		}
		int dy = surfaceY - northY;
		MapColor.Brightness brightness = dy > 0
				? MapColor.Brightness.HIGH
				: (dy < 0 ? MapColor.Brightness.LOW : MapColor.Brightness.NORMAL);
		return mapColor.calculateARGBColor(brightness);
	}

	private void composeTiles(ClientLevel level, boolean underground, NativeImage image) {
		ResourceKey<Level> dimension = level.dimension();
		int minChunkX = Math.floorDiv(screenToBlockX(getX()), 16);
		int maxChunkX = Math.floorDiv(screenToBlockX(getX() + width - 1), 16);
		int minChunkZ = Math.floorDiv(screenToBlockZ(getY()), 16);
		int maxChunkZ = Math.floorDiv(screenToBlockZ(getY() + height - 1), 16);

		for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
			for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
				TileKey key = new TileKey(dimension, chunkX, chunkZ, underground ? pivotY : 0, underground);
				MapTile tile = tileCache.get(key);
				if (tile == null) {
					continue;
				}
				for (int localX = 0; localX < 16; localX++) {
					int worldX = (chunkX << 4) + localX;
					int screenX = blockToScreenX(worldX);
					if (screenX + pixelsPerBlock <= getX() || screenX >= getX() + width) {
						continue;
					}
					for (int localZ = 0; localZ < 16; localZ++) {
						int worldZ = (chunkZ << 4) + localZ;
						int screenY = blockToScreenZ(worldZ);
						if (screenY + pixelsPerBlock <= getY() || screenY >= getY() + height) {
							continue;
						}
						int color = tile.colors[(localZ << 4) | localX];
						if (color == 0) {
							color = BG_DARK;
						}
						fillBlock(image, screenX - getX(), screenY - getY(), color);
					}
				}
			}
		}
	}

	private void fillBlock(NativeImage image, int imageX, int imageY, int color) {
		int x0 = Math.max(0, imageX);
		int y0 = Math.max(0, imageY);
		int x1 = Math.min(width, imageX + pixelsPerBlock);
		int y1 = Math.min(height, imageY + pixelsPerBlock);
		if (x1 <= x0 || y1 <= y0) {
			return;
		}
		if (x1 - x0 == 1 && y1 - y0 == 1) {
			image.setPixel(x0, y0, color);
		} else {
			image.fillRect(x0, y0, x1 - x0, y1 - y0, color);
		}
	}

	private int undergroundColumnColor(
			ClientLevel level, int x, int z, BlockPos.MutableBlockPos pos
	) {
		int floorY = scanFloor(level, x, z, pivotY, pos);
		if (floorY == NO_FLOOR) {
			return 0;
		}
		pos.set(x, floorY - 1, z);
		MapColor mapColor = level.getBlockState(pos).getMapColor(level, pos);
		if (mapColor == MapColor.NONE) {
			return 0;
		}
		int depth = pivotY - (floorY - 1);
		MapColor.Brightness brightness = depth <= 3
				? MapColor.Brightness.NORMAL
				: (depth <= 8 ? MapColor.Brightness.LOW : MapColor.Brightness.LOWEST);
		return mapColor.calculateARGBColor(brightness);
	}

	private int scanFloor(
			ClientLevel level, int x, int z, int pivot, BlockPos.MutableBlockPos pos
	) {
		BlockState state = level.getBlockState(pos.set(x, pivot, z));
		if (!occludes(state)) {
			int bottom = Math.max(level.getMinY(), pivot - undergroundScanDepth);
			for (int y = pivot - 1; y >= bottom; y--) {
				state = level.getBlockState(pos.set(x, y, z));
				if (occludes(state)) {
					return y + 1;
				}
			}
			return NO_FLOOR;
		}
		int top = Math.min(pivot + 10, level.getMaxY());
		for (int y = pivot + 1; y <= top; y++) {
			state = level.getBlockState(pos.set(x, y, z));
			if (!occludes(state)) {
				return y;
			}
		}
		return NO_FLOOR;
	}

	private static boolean occludes(BlockState state) {
		return state.canOcclude() || state.is(Blocks.LAVA);
	}

	private boolean undergroundActive() {
		return switch (layer) {
			case SURFACE -> false;
			case UNDERGROUND -> true;
			case AUTO -> playerUnderground();
		};
	}

	private int effectivePivotY() {
		Minecraft client = Minecraft.getInstance();
		return client.player == null ? center.getY() : Mth.floor(client.player.getY());
	}

	private static boolean playerUnderground() {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.level == null) {
			return false;
		}
		BlockPos pos = client.player.blockPosition();
		if (client.level.getBrightness(LightLayer.SKY, pos) <= 0) {
			return true;
		}
		int surfaceY = client.level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, pos).getY();
		return pos.getY() < surfaceY - 2;
	}

	private void updateLayerLabel(boolean underground, int pivot) {
		Component next = buildLayerLabel(underground, pivot);
		if (!next.getString().equals(layerLabel.getString())) {
			layerLabel = next;
		}
	}

	private Component buildLayerLabel(boolean underground, int pivot) {
		return switch (layer) {
			case SURFACE -> Component.translatable("quillui.map.layer.surface");
			case UNDERGROUND -> Component.translatable("quillui.map.layer.underground", pivot);
			case AUTO -> underground
					? Component.translatable("quillui.map.layer.auto_underground", pivot)
					: Component.translatable("quillui.map.layer.auto_surface");
		};
	}

	private void drawGrid(GuiGraphicsExtractor graphics) {
		int minX = screenToBlockX(getX());
		int maxX = screenToBlockX(getX() + width);
		int minZ = screenToBlockZ(getY());
		int maxZ = screenToBlockZ(getY() + height);
		for (int x = Math.floorDiv(minX, 16) * 16; x <= maxX; x += 16) {
			int screenX = blockToScreenX(x);
			graphics.fill(screenX, getY(), screenX + 1, getY() + height, GRID_OVERLAY);
		}
		for (int z = Math.floorDiv(minZ, 16) * 16; z <= maxZ; z += 16) {
			int screenZ = blockToScreenZ(z);
			graphics.fill(getX(), screenZ, getX() + width, screenZ + 1, GRID_OVERLAY);
		}
	}

	private void drawZones(GuiGraphicsExtractor graphics) {
		for (QuillMapZone zone : zones) {
			int x0 = blockToScreenX(zone.minX());
			int y0 = blockToScreenZ(zone.minZ());
			int x1 = blockToScreenX(zone.maxX()) + pixelsPerBlock;
			int y1 = blockToScreenZ(zone.maxZ()) + pixelsPerBlock;
			graphics.fill(x0, y0, x1, y1, zone.fillColor());
			if (zone.dashed()) {
				drawDashedRect(graphics, x0, y0, x1, y1, zone.borderColor());
			} else {
				QuillRender.border(graphics, x0, y0, x1 - x0, y1 - y0, zone.borderColor());
			}
		}
	}

	private void drawRadiusLimit(GuiGraphicsExtractor graphics) {
		int x0 = blockToScreenX(center.getX() - maxRadius);
		int y0 = blockToScreenZ(center.getZ() - maxRadius);
		int x1 = blockToScreenX(center.getX() + maxRadius) + pixelsPerBlock;
		int y1 = blockToScreenZ(center.getZ() + maxRadius) + pixelsPerBlock;
		drawDashedRect(graphics, x0, y0, x1, y1, withAlpha(theme.color(QuillThemeData.ColorRole.INFO), 0x99));
	}

	private void drawSelection(GuiGraphicsExtractor graphics) {
		if (!hasSelection()) {
			return;
		}
		int x0 = blockToScreenX(Math.min(from.getX(), to.getX()));
		int y0 = blockToScreenZ(Math.min(from.getZ(), to.getZ()));
		int x1 = blockToScreenX(Math.max(from.getX(), to.getX())) + pixelsPerBlock;
		int y1 = blockToScreenZ(Math.max(from.getZ(), to.getZ())) + pixelsPerBlock;
		boolean fits = selectionFitsLimit();
		int color = fits
				? theme.color(QuillThemeData.ColorRole.SUCCESS)
				: theme.color(QuillThemeData.ColorRole.DANGER);
		graphics.fill(x0, y0, x1, y1, withAlpha(color, 0x45));
		QuillRender.border(graphics, x0, y0, x1 - x0, y1 - y0, color);
	}

	private void drawMarkers(GuiGraphicsExtractor graphics) {
		for (QuillMapMarker marker : markers) {
			drawMarker(graphics, marker);
		}

		if (entityFilter == null || entityMarkerFactory == null || Minecraft.getInstance().level == null) {
			return;
		}
		for (Entity entity : Minecraft.getInstance().level.entitiesForRendering()) {
			if (!entityFilter.test(entity)) {
				continue;
			}
			QuillMapMarker marker = entityMarkerFactory.apply(entity);
			if (marker != null) {
				drawMarker(graphics, marker);
			}
		}
	}

	private void drawMarker(GuiGraphicsExtractor graphics, QuillMapMarker marker) {
		int x = blockToScreenX(marker.position().getX()) + pixelsPerBlock / 2;
		int y = blockToScreenZ(marker.position().getZ()) + pixelsPerBlock / 2;
		graphics.fill(x - 4, y - 4, x + 5, y + 5, withAlpha(marker.color(), 0xDD));
		QuillRender.border(graphics, x - 4, y - 4, 9, 9, 0xFFFFFFFF);
		if (marker.label() != null && !marker.label().getString().isBlank()) {
			Font font = Minecraft.getInstance().font;
			int textWidth = font.width(marker.label());
			int badgeX = x - textWidth / 2 - 3;
			int badgeY = y + 7;
			graphics.fill(badgeX, badgeY, badgeX + textWidth + 6, badgeY + 11, 0xDD111820);
			QuillRender.text(graphics, font, marker.label(), badgeX + 3, badgeY + 2, 0xFFFFFFFF);
		}
	}

	private void drawCenterMarker(GuiGraphicsExtractor graphics) {
		int x = blockToScreenX(center.getX()) + pixelsPerBlock / 2;
		int y = blockToScreenZ(center.getZ()) + pixelsPerBlock / 2;
		graphics.fill(x - 4, y - 4, x + 5, y + 5, 0x99000000);
		graphics.fill(x - 3, y - 1, x + 4, y + 2, 0xFFFFC65A);
		graphics.fill(x - 1, y - 3, x + 2, y + 4, 0xFFFFC65A);
		graphics.fill(x - 1, y - 1, x + 2, y + 2, 0xFFFFFFFF);
	}

	private void drawHud(GuiGraphicsExtractor graphics) {
		Font font = Minecraft.getInstance().font;
		String zoom = pixelsPerBlock + "x";
		int zoomWidth = font.width(zoom);
		graphics.fill(
				getX() + width - zoomWidth - 10,
				getY() + 6,
				getX() + width - 4,
				getY() + 18,
				0xAA111820
		);
		QuillRender.text(
				graphics, font, Component.literal(zoom),
				getX() + width - zoomWidth - 7, getY() + 8, 0xFFB5C2D0
		);

		if (hasSelection()) {
			int w = selectionWidth();
			int d = selectionDepth();
			int blocks = w * d;
			boolean fits = selectionFitsLimit();
			String text = w + "x" + d + " (" + blocks + " bl)";
			int textWidth = font.width(text);
			graphics.fill(
					getX() + 6,
					getY() + height - 19,
					getX() + textWidth + 12,
					getY() + height - 5,
					0xDD111820
			);
			QuillRender.text(
					graphics, font, Component.literal(text),
					getX() + 9, getY() + height - 16,
					fits ? 0xFF48D7A0 : 0xFFFF5555
			);
		}
	}

	private void drawDashedRect(
			GuiGraphicsExtractor graphics, int x0, int y0, int x1, int y1, int color
	) {
		int dash = 4;
		for (int x = x0; x < x1; x += dash * 2) {
			int end = Math.min(x + dash, x1);
			graphics.fill(x, y0, end, y0 + 1, color);
			graphics.fill(x, y1 - 1, end, y1, color);
		}
		for (int y = y0; y < y1; y += dash * 2) {
			int end = Math.min(y + dash, y1);
			graphics.fill(x0, y, x0 + 1, end, color);
			graphics.fill(x1 - 1, y, x1, end, color);
		}
	}

	public int blockToScreenX(int blockX) {
		return getX() + width / 2 + (blockX - center.getX()) * pixelsPerBlock;
	}

	public int blockToScreenZ(int blockZ) {
		return getY() + height / 2 + (blockZ - center.getZ()) * pixelsPerBlock;
	}

	public int screenToBlockX(double mouseX) {
		double relX = mouseX - (getX() + width / 2.0D);
		int delta = (int) Math.floor(relX / pixelsPerBlock);
		return center.getX() + delta;
	}

	public int screenToBlockZ(double mouseY) {
		double relY = mouseY - (getY() + height / 2.0D);
		int delta = (int) Math.floor(relY / pixelsPerBlock);
		return center.getZ() + delta;
	}

	public BlockPos blockAt(double mouseX, double mouseY) {
		return new BlockPos(screenToBlockX(mouseX), center.getY(), screenToBlockZ(mouseY));
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (!active || !visible || !isMouseOver(event.x(), event.y())) {
			return false;
		}

		panning = false;
		dragging = false;
		MouseAction action = mouseBindings.action(event.button());
		if (action == MouseAction.PAN) {
			panning = true;
			panStartCenter = center;
			panStartMouseX = event.x();
			panStartMouseY = event.y();
			return true;
		}

		if (!readOnly && action == MouseAction.SELECT) {
			from = blockAt(event.x(), event.y());
			to = from;
			dragging = true;
			notifySelection();
			return true;
		}
		return false;
	}

	@Override
	public void onClick(MouseButtonEvent event, boolean doubleClick) {
		if (readOnly || mouseBindings.left() != MouseAction.SELECT
				|| event.button() != InputConstants.MOUSE_BUTTON_LEFT) {
			return;
		}
		from = blockAt(event.x(), event.y());
		to = from;
		dragging = true;
		notifySelection();
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
		if (panning) {
			updatePan(event);
			return true;
		}
		if (dragging) {
			to = blockAt(event.x(), event.y());
			notifySelection();
			return true;
		}
		return super.mouseDragged(event, deltaX, deltaY);
	}

	@Override
	protected void onDrag(MouseButtonEvent event, double deltaX, double deltaY) {
		if (panning) {
			updatePan(event);
		} else if (dragging) {
			to = blockAt(event.x(), event.y());
			notifySelection();
		}
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		if (panning) {
			panning = false;
			return true;
		}
		if (dragging) {
			to = blockAt(event.x(), event.y());
			dragging = false;
			notifySelection();
			notifySelectionCommit();
			return true;
		}
		return super.mouseReleased(event);
	}

	@Override
	public void onRelease(MouseButtonEvent event) {
		if (dragging) {
			to = blockAt(event.x(), event.y());
			notifySelection();
			notifySelectionCommit();
		}
		dragging = false;
		panning = false;
	}

	private void updatePan(MouseButtonEvent event) {
		int dx = (int) Math.round((panStartMouseX - event.x()) / (double) pixelsPerBlock);
		int dz = (int) Math.round((panStartMouseY - event.y()) / (double) pixelsPerBlock);
		center = panStartCenter.offset(dx, 0, dz);
		dirty = true;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (!active || !visible || !isMouseOver(mouseX, mouseY)) {
			return false;
		}
		int next = Mth.clamp(pixelsPerBlock + (scrollY > 0 ? 1 : -1), 1, 4);
		if (next != pixelsPerBlock) {
			pixelsPerBlock = next;
			dirty = true;
		}
		return true;
	}

	private void notifySelection() {
		if (selectionListener != null) {
			selectionListener.accept(this);
		}
	}

	private void notifySelectionCommit() {
		if (selectionCommitListener != null) {
			selectionCommitListener.accept(this);
		}
	}

	private static int withAlpha(int color, int alpha) {
		return QuillColors.withAlpha(color, alpha);
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {
		defaultButtonNarrationText(output);
	}
}
