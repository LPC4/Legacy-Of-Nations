package org.lpc.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lpc.GameStateManager;
import org.lpc.MainGame;
import org.lpc.map.BaseMap;
import org.lpc.map.MapScale;
import org.lpc.map.MapSystem;

import static org.lpc.utility.Constants.MAX_ZOOM;

@Getter @Setter
public class GameScreen implements Screen {
    private static final Logger LOGGER = LogManager.getLogger(GameScreen.class);

    private final MainGame game;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final GameStateManager gameStateManager;
    private OrthographicCamera camera;

    public GameScreen(MainGame game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.gameStateManager = game.getGameStateManager();
        this.camera = gameStateManager.getMapSystem().getMap().getRenderer().getCamera();
        this.camera.setToOrtho(false);

        setCameraToMapCenter();
    }

    private void setCameraToMapCenter() {
        BaseMap map = gameStateManager.getMapSystem().getMap();
        MapScale scale = gameStateManager.getMapSystem().getCurrentScale();
        camera.position.set(map.getWidth() * scale.getPixelsPerTile() / 2f, map.getHeight() * scale.getPixelsPerTile() / 2f, 0);
        camera.zoom = MAX_ZOOM / 2f;
        camera.update();
    }

    @Override
    public void show() {
        this.camera = game.getGameStateManager().getMapSystem().getMap().getRenderer().getCamera();
        LOGGER.info("Game screen shown");
        Gdx.input.setInputProcessor(game.getInputHandler());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderMap();
    }

    private void renderMap() {
        BaseMap map = game.getGameStateManager().getMapSystem().getMap();
        map.render(shapeRenderer, batch);
    }

    @Override
    public void resize(int i, int i1) {
        Vector3 prevPos = camera.unproject(new Vector3(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f, 0));
        camera.setToOrtho(false, i, i1);
        camera.position.set(prevPos);
        camera.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        LOGGER.info("Disposing GameScreen resources");
        batch.dispose();
        shapeRenderer.dispose();
    }
}
