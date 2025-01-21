package org.lpc.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import lombok.Getter;
import lombok.Setter;
import org.javatuples.Pair;
import org.lpc.MainGame;
import org.lpc.civilisation.Civilisation;
import org.lpc.map.maps.SurfaceMapGenerator;
import org.lpc.terrain.resources.ResourceType;
import org.lpc.utility.Position;

@Getter
@Setter
public abstract class BaseMap<T extends BaseMap.BaseTile> {
    protected final MainGame game;
    protected MapScale scale;
    protected int width;
    protected int height;
    // Map specific implementations
    protected final IMapGenerator<T> mapGenerator;
    protected final IMapRenderer<T> renderer;
    protected final IMapInput input;
    protected final T[][] tiles;

    // Each map type implements their own tile type which extends this class
    @Getter
    @Setter
    public abstract static class BaseTile {
        protected final Position position;
        protected final MapScale scale;
        protected Civilisation owner;

        public BaseTile(Position position, MapScale scale) {
            this.position = position;
            this.scale = scale;
            this.owner = null;
        }

        public abstract Pair<ResourceType, Integer> harvestResources();
        public abstract void update();
    }

    public BaseMap(MapScale scale, int width, int height, MainGame game, IMapGenerator<T> mapGenerator, IMapRenderer<T> renderer, T[][] tiles, IMapInput input) {
        this.scale = scale;
        this.width = width;
        this.height = height;
        this.mapGenerator = mapGenerator;
        this.renderer = renderer;
        this.game = game;
        this.tiles = tiles;
        this.input = input;

        generateMap();
    }

    public abstract void update();

    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        renderer.render(tiles, shapeRenderer, batch);
    }

    protected void generateMap() {
        mapGenerator.generateTerrain(tiles, width, height);
        mapGenerator.generateResources(tiles);
    }
}

