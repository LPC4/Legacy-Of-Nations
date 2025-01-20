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
    protected int width;
    protected int height;
    protected final IMapGenerator<T> mapGenerator;
    protected final IMapRenderer<T> renderer;
    protected final T[][] tiles;
    protected final MainGame game;

    // Each map type implements their own tile type
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

    public BaseMap(int width, int height, MainGame game, IMapGenerator<T> mapGenerator, IMapRenderer<T> renderer, T[][] tiles) {
        this.width = width;
        this.height = height;
        this.mapGenerator = mapGenerator;
        this.renderer = renderer;
        this.game = game;
        this.tiles = tiles;

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

    public BaseTile[] getNeighbours(BaseTile tile) {
        int x = tile.getPosition().getGridX();
        int y = tile.getPosition().getGridY();
        BaseTile[] neighbours = new BaseTile[4];

        if (x > 0) {
            neighbours[0] = tiles[x - 1][y];
        }
        if (x < width - 1) {
            neighbours[1] = tiles[x + 1][y];
        }
        if (y > 0) {
            neighbours[2] = tiles[x][y - 1];
        }
        if (y < height - 1) {
            neighbours[3] = tiles[x][y + 1];
        }

        return neighbours;
    }
}

