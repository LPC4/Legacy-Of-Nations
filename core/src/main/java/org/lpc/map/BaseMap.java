package org.lpc.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import lombok.Getter;
import org.lpc.utility.Position;

@Getter
public abstract class BaseMap {
    protected int width;
    protected int height;
    protected float zoom;

    // Each map type implements their own tile type
    protected abstract static class BaseTile {
        protected Position position;
    }

    public abstract void render(ShapeRenderer shapeRenderer, SpriteBatch batch);
    public abstract void update();
    protected abstract void generateMap();
}

