package org.lpc.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lpc.MainGame;

import static org.lpc.utility.Constants.SKIN;

@Getter
public class MenuScreen implements Screen {
    private static final Logger LOGGER = LogManager.getLogger(MenuScreen.class);

    private final MainGame game;
    private Stage stage;
    private Viewport viewport;

    private boolean firstEscapePress = true;

    public MenuScreen(final MainGame game) {
        LOGGER.info("Menu screen created");
        this.game = game;
        this.viewport = new ScreenViewport();
        this.stage = new Stage(viewport);
    }

    @Override
    public void show() {
        firstEscapePress = true;
        Gdx.input.setInputProcessor(stage);

        Texture backgroundTexture = new Texture("menu/background.png");
        Drawable backgroundDrawable = new TextureRegionDrawable(backgroundTexture);

        Button startButton = new Button(SKIN);
        startButton.add("Resume Game");
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(game.getGameScreen());
            }
        });

        Button settingsButton = new Button(SKIN);
        settingsButton.add("Settings");
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                LOGGER.info("Settings button clicked");
            }
        });

        Button exitButton = new Button(SKIN);
        exitButton.add("Exit");
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        Table table = new Table(SKIN);
        table.setBackground(backgroundDrawable);
        table.setFillParent(true);
        table.top().center();
        float buttonWidth = 200; // Set a uniform width for all buttons
        float buttonHeight = 40; // Set a uniform height for all buttons
        table.add(startButton).width(buttonWidth).height(buttonHeight).padTop(20).row();
        table.add(settingsButton).width(buttonWidth).height(buttonHeight).padTop(20).row();
        table.add(exitButton).width(buttonWidth).height(buttonHeight).padTop(20).row();

        Texture logoTexture = new Texture("menu/start/legacy_white.png");
        Image logoImage = new Image(new TextureRegionDrawable(logoTexture));

        Table logoTable = new Table();
        logoTable.top().left();
        logoTable.setFillParent(true);
        logoTable.add(logoImage).height(20).width(160).padTop(10).padLeft(10);

        stage.addActor(table);
        stage.addActor(logoTable);
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (firstEscapePress) {
                firstEscapePress = false;
                return;
            }
            game.setScreen(game.getGameScreen());
            LOGGER.info("Escape key pressed");
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
        SKIN.dispose();
    }
}
