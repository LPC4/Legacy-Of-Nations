package org.lpc;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lpc.screens.GameScreen;
import org.lpc.screens.MenuScreen;
import org.lpc.screens.StartScreen;

import java.io.File;
import java.util.Objects;

@Getter
public class MainGame extends Game {
    private static final Logger LOGGER = LogManager.getLogger(MainGame.class);

    private GameStateManager gameStateManager;
    private GameScreen gameScreen;
    private StartScreen startScreen;
    private MenuScreen menuScreen;
    private BaseInputHandler inputHandler;
    private Settings settings;
    private UIRenderer uiRenderer;

    private ShaderProgram blurShader;
    private float accumulator;

    /// Features to be implemented:
    ///  - Add audio to the game (low hum in main menu, music in game)
    ///  - Add movement to the main menu (twinkling stars, moving clouds, starship flying by)
    ///  - Good logo for the game
    ///  - Fix map system

    @Override
    public void create() {
        LOGGER.info("Initializing game");

        initShaders();

        this.settings = new Settings();
        this.uiRenderer = new UIRenderer(this);
        this.gameStateManager = new GameStateManager(this);
        this.inputHandler = new BaseInputHandler(this, gameStateManager.getMap().getInput());
        this.accumulator = 0f;

        this.gameScreen = new GameScreen(this, gameStateManager, uiRenderer);
        this.menuScreen = new MenuScreen(this);
        this.startScreen = new StartScreen(this);
        setScreen(startScreen);

        LOGGER.info("Game created and set to StartScreen");
    }

    @Override
    public void render() {
        super.render(); // Delegate render to the current screen
        handleUpdate();
    }

    private void handleUpdate() {
        accumulator += Gdx.graphics.getDeltaTime();

        final float fixedTimeStep = 1f / settings.getTicksPerSecond();
        if (accumulator >= fixedTimeStep) {
            accumulator -= fixedTimeStep;

            if (screen == gameScreen) {
                gameStateManager.update();
            }
        }
    }

    @Override
    public void dispose() {
        LOGGER.info("Disposing game resources");

        super.dispose();
        gameScreen.dispose();
        startScreen.dispose();
        menuScreen.dispose();

        LOGGER.info("Game disposed, exiting");

        Gdx.app.exit();
        LogManager.shutdown();
        deleteLogFiles();
    }

    private void initShaders() {
        blurShader = new ShaderProgram(Gdx.files.internal("shaders/vertex_shader.glsl"), Gdx.files.internal("shaders/blur.frag"));
        if (!blurShader.isCompiled()) {
            Gdx.app.error("Shader", "Error compiling shader: " + blurShader.getLog());
        }
    }

    private void deleteLogFiles() {
        File logDir = new File("logs");
        if (!logDir.exists()) return;

        for (File file : Objects.requireNonNull(logDir.listFiles())) {
            if (!file.getName().endsWith(".log")) continue;

            if (!file.delete())
                throw new RuntimeException("Failed to delete log file: " + file.getName());
        }
    }
}
