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

import static com.badlogic.gdx.Input.Keys.F11;
import static org.lpc.utility.Constants.WINDOW_HEIGHT;
import static org.lpc.utility.Constants.WINDOW_WIDTH;

@Getter
public class MenuScreen implements Screen {
    private static final Skin DEFAULT_BUTTON_SKIN    = new Skin(Gdx.files.internal("menu/default/uiskin.json"));
    private static final int MENU_BUTTON_WIDTH       = 200;
    private static final int MENU_BUTTON_HEIGHT      = 40;
    private static final Logger LOGGER = LogManager.getLogger(MenuScreen.class);

    private final MainGame game;
    private final Stage stage;
    private final Viewport viewport;

    private boolean firstEscapePress = true;

    public MenuScreen(final MainGame game) {
        LOGGER.info("Menu screen created");
        this.game = game;
        this.viewport = new ScreenViewport();
        this.stage = new Stage(viewport);
    }

    @Override
    public void show() {
        LOGGER.info("Menu screen shown");
        firstEscapePress = true;
        Gdx.input.setInputProcessor(stage);

        setupBackground();
        setupButtons();
        setupLogo();
    }

    private void setupBackground() {
        Texture backgroundTexture = new Texture("menu/background.png");
        Drawable backgroundDrawable = new TextureRegionDrawable(backgroundTexture);
        Table backgroundTable = new Table();
        backgroundTable.setBackground(backgroundDrawable);
        backgroundTable.setFillParent(true);
        stage.addActor(backgroundTable);
    }

    private void setupButtons() {
        Table table = new Table(DEFAULT_BUTTON_SKIN);
        table.top().center();
        table.setFillParent(true);

        Button startButton = createButton("Resume Game", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                LOGGER.info("Start button clicked");
                game.setScreen(game.getGameScreen());
            }
        });

        Button settingsButton = createButton("Settings", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                LOGGER.info("Settings button clicked");
            }
        });

        Button exitButton = createButton("Exit", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                LOGGER.info("Exit button clicked");
                Gdx.app.exit();
            }
        });

        table.add(startButton).width(MENU_BUTTON_WIDTH).height(MENU_BUTTON_HEIGHT).padTop(20).row();
        table.add(settingsButton).width(MENU_BUTTON_WIDTH).height(MENU_BUTTON_HEIGHT).padTop(20).row();
        table.add(exitButton).width(MENU_BUTTON_WIDTH).height(MENU_BUTTON_HEIGHT).padTop(20).row();

        stage.addActor(table);
    }


    private Button createButton(String text, ClickListener clickListener) {
        Button button = new Button(DEFAULT_BUTTON_SKIN);
        button.add(text);
        button.addListener(clickListener);
        return button;
    }

    private void setupLogo() {
        Texture logoTexture = new Texture("menu/start/legacy_white.png");
        Image logoImage = new Image(new TextureRegionDrawable(logoTexture));
        Table logoTable = new Table();
        logoTable.top().left();
        logoTable.setFillParent(true);
        logoTable.add(logoImage).height(20).width(160).padTop(10).padLeft(10);
        stage.addActor(logoTable);
    }

    @Override
    public void render(float delta) {
        clearScreen();
        checkInput();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void checkInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            toggleClose();
        }
        if (Gdx.input.isKeyJustPressed(F11)) {
            toggleFullscreen();
        }
    }

    private void toggleFullscreen() {
        if (Gdx.graphics.isFullscreen()) {
            Gdx.graphics.setWindowedMode(WINDOW_WIDTH, WINDOW_HEIGHT);
            LOGGER.info("Switched to windowed mode");
        } else {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            LOGGER.info("Switched to fullscreen mode");
        }
    }

    private void toggleClose() {
        if (firstEscapePress) {
            LOGGER.info("Press escape again to exit");
            firstEscapePress = false;
            return;
        }
        game.setScreen(game.getGameScreen());
        LOGGER.info("Escape key pressed");
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
        LOGGER.info("Menu screen disposed");
        stage.dispose();
        DEFAULT_BUTTON_SKIN.dispose();
    }
}
