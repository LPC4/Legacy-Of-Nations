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
import org.lpc.MainGame;
import org.lpc.map.BaseMap;

@Getter @Setter
public class GameScreen implements Screen {
    MainGame game;

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;

    public GameScreen(MainGame game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.setToOrtho(false);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(game.getInputHandler());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderMap();
    }


    private void renderMap() {
        BaseMap map = game.getGameStateManager().getMap();
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

    }
}
