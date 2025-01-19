package org.lpc.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import lombok.Getter;
import lombok.Setter;
import org.lpc.utility.Position;

@Getter
@Setter
public abstract class BaseMap {
    protected int width;
    protected int height;

    // Each map type implements their own tile type
    @Getter
    @Setter
    protected abstract static class BaseTile {
        protected Position position;

        public Vector2 getVector2() {
            return new Vector2(position.getGridX(), position.getGridY());
        }
    }

    public abstract void render(ShapeRenderer shapeRenderer, SpriteBatch batch);
    public abstract void update();
    protected abstract void generateMap();
}

