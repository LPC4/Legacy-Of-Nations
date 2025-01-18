package org.lpc.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lpc.MainGame;

import javax.swing.*;

import static com.badlogic.gdx.Input.Keys.F11;
import static com.badlogic.gdx.Input.Keys.SPACE;
import static org.lpc.utility.Constants.WINDOW_HEIGHT;
import static org.lpc.utility.Constants.WINDOW_WIDTH;

public class StartScreen implements Screen {
    private static final Logger LOGGER = LogManager.getLogger(StartScreen.class);

    private final MainGame game;
    private Stage stage;

    public StartScreen(final MainGame game) {
        LOGGER.info("Start screen created");
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Setup background image
        Texture backgroundTexture = new Texture("menu/background.png");
        Image backgroundImage = new Image(new TextureRegionDrawable(backgroundTexture));
        backgroundImage.setScaling(Scaling.fill);

        Texture titleTexture = new Texture("menu/start/legacy_white.png");
        Image titleImage = new Image(new TextureRegionDrawable(titleTexture));

        Texture pressSpaceTexture = new Texture("menu/start/press_space_2.png");
        Image pressSpaceImage = new Image(new TextureRegionDrawable(pressSpaceTexture));
        pressSpaceImage.getColor().a = 0;

        Table table = new Table();
        table.top().center();
        table.setFillParent(true);
        table.add(titleImage).padTop(0).padLeft(20).padRight(20).row();
        table.add(pressSpaceImage).padTop(120).row();

        Table backgroundTable = new Table();
        backgroundTable.setFillParent(true);
        backgroundTable.add(backgroundImage).expand().fill();

        stage.addActor(backgroundTable);
        stage.addActor(table);

        pressSpaceImage.addAction(
            Actions.run(() -> pressSpaceImage.addAction(Actions.forever(Actions.sequence(
                Actions.fadeIn(3f),
                Actions.run(() -> LOGGER.info("Press space to start")),
                Actions.fadeOut(1f)
            ))))
        );
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        checkInput();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    private void checkInput() {
        if (Gdx.input.isKeyJustPressed(SPACE)) {
            game.setScreen(game.getGameScreen());
        }
        if (Gdx.input.isKeyJustPressed(F11)) {
            if (Gdx.graphics.isFullscreen()) {
                Gdx.graphics.setWindowedMode(WINDOW_WIDTH, WINDOW_HEIGHT);
                LOGGER.info("Switched to windowed mode");
            } else {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                LOGGER.info("Switched to fullscreen mode");
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        // Handle pause if needed
    }

    @Override
    public void resume() {
        // Handle resume if needed
    }

    @Override
    public void hide() {
        // Cleanup when this screen is no longer visible
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
