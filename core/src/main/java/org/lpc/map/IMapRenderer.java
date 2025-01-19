package org.lpc.map;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public interface IMapRenderer {
    void render(BaseMap.BaseTile[][] tiles, ShapeRenderer shapeRenderer, SpriteBatch batch);
    OrthographicCamera getCamera();
}
