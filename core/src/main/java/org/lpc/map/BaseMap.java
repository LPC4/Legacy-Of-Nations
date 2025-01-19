package org.lpc.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import lombok.Getter;
import lombok.Setter;
import org.lpc.MainGame;
import org.lpc.map.maps.SurfaceMapGenerator;
import org.lpc.utility.Position;

@Getter
@Setter
public abstract class BaseMap {
    protected int width;
    protected int height;
    protected final IMapGenerator mapGenerator;
    protected final IMapRenderer renderer;
    protected final BaseTile[][] tiles;
    protected final MainGame game;

    // Each map type implements their own tile type
    @Getter
    @Setter
    public abstract static class BaseTile {
        protected Position position;

        public BaseTile(Position position) {
            this.position = position;
        }
    }

    public BaseMap(int width, int height, MainGame game, IMapGenerator mapGenerator, IMapRenderer renderer, BaseTile[][] tiles) {
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
}

