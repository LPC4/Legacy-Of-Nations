package org.lpc.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.javatuples.Pair;
import org.lpc.MainGame;
import org.lpc.civilisation.Civilisation;
import org.lpc.terrain.resources.ResourceType;
import org.lpc.utility.Position;

import java.util.Optional;

/**
 * Base class for all game maps containing grid-based tiles.
 * @param <T> Concrete tile type extending BaseTile
 */
@Getter
@Setter
public abstract class BaseMap<T extends BaseMap.BaseTile> {
    protected static final int MIN_MAP_DIMENSION = 1;

    protected final @NonNull MainGame game;
    protected final @NonNull MapScale scale;
    protected final @NonNull IMapGenerator<T> mapGenerator;
    protected final @NonNull IMapRenderer<T> renderer;
    protected final @NonNull IMapInput input;
    protected final @NonNull T[][] tiles;

    protected int width;
    protected int height;

    /**
     * Base class for all map tiles.
     */
    @Getter
    @Setter
    public abstract static class BaseTile {
        protected final @NonNull Position position;
        protected final @NonNull MapScale scale;
        protected Civilisation owner;

        protected BaseTile(@NonNull Position position, @NonNull MapScale scale) {
            this.position = position;
            this.scale = scale;
        }

        /**
         * Attempts to harvest resources from this tile.
         * @return Optional containing resource type and amount if available,
         *         empty Optional if no resources can be harvested
         */
        public abstract Optional<Pair<ResourceType, Integer>> harvestResource();

        /**
         * Updates tile state each game tick
         */
        public abstract void update();
    }

    public BaseMap(
        @NonNull MapScale scale,
        int width,
        int height,
        @NonNull MainGame game,
        @NonNull IMapGenerator<T> mapGenerator,
        @NonNull IMapRenderer<T> renderer,
        @NonNull T[][] tiles,
        @NonNull IMapInput input
    ) {
        validateDimensions(width, height);
        validateTileArrayDimensions(tiles, width, height);

        this.scale = scale;
        this.width = width;
        this.height = height;
        this.game = game;
        this.mapGenerator = mapGenerator;
        this.renderer = renderer;
        this.tiles = tiles;
        this.input = input;

        generateMap();
    }

    public abstract void update();

    public void render(@NonNull ShapeRenderer shapeRenderer, @NonNull SpriteBatch batch) {
        renderer.render(tiles, shapeRenderer, batch);
    }

    protected final void generateMap() {
        mapGenerator.generateTerrain(tiles, width, height);
        mapGenerator.generateResources(tiles);
    }

    private static void validateDimensions(int width, int height) {
        if (width < MIN_MAP_DIMENSION || height < MIN_MAP_DIMENSION) {
            throw new IllegalArgumentException(
                "Map dimensions must be at least " + MIN_MAP_DIMENSION + "x" + MIN_MAP_DIMENSION
            );
        }
    }

    private static <T extends BaseTile> void validateTileArrayDimensions(T[][] tiles, int expectedWidth, int expectedHeight) {
        if (tiles.length != expectedWidth) {
            throw new IllegalArgumentException(
                "Tile array width mismatch. Expected: " + expectedWidth + ", Actual: " + tiles.length
            );
        }

        for (int x = 0; x < tiles.length; x++) {
            if (tiles[x].length != expectedHeight) {
                throw new IllegalArgumentException(
                    "Tile array height mismatch at column " + x + ". Expected: " + expectedHeight + ", Actual: " + tiles[x].length
                );
            }
        }
    }
}
