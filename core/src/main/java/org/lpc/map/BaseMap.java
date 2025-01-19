package org.lpc.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import lombok.Getter;
import lombok.Setter;
import org.lpc.MainGame;
import org.lpc.utility.Position;

@Getter
@Setter
public abstract class BaseMap {
    protected int width;
    protected int height;

    protected final MapGenerator mapGenerator;
    protected final MapRenderer renderer;

    private final MainGame game;

    // Each map type implements their own tile type
    @Getter
    @Setter
    protected abstract static class BaseTile {
        protected Position position;

        public Vector2 getVector2() {
            return new Vector2(position.getGridX(), position.getGridY());
        }
    }

    public BaseMap(int width, int height, MainGame game) {
        this.width = width;
        this.height = height;
        this.mapGenerator = new MapGenerator();
        this.renderer = new MapRenderer(game);
        this.game = game;
    }

    public abstract void render(ShapeRenderer shapeRenderer, SpriteBatch batch);
    public abstract void update();
    protected abstract void generateMap();
}

