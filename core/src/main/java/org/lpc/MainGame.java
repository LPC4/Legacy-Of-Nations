package org.lpc;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.LoggerAdapter;
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
    private InputHandler inputHandler;
    private float accumulator;

    /// Features to be implemented:
    ///  - Add audio to the game (low hum in main menu, music in game)
    ///  - Add movement to the main menu (twinkling stars, moving clouds, starship flying by)
    ///  - Good logo for the game
    ///  - Fix map system

    @Override
    public void create() {
        LOGGER.info("Initializing game");

        this.gameStateManager = new GameStateManager(this);
        this.inputHandler = new InputHandler(this);
        this.accumulator = 0f;

        this.gameScreen = new GameScreen(this);
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

        final float fixedTimeStep = 1f / 20f; // 20 updates per second
        if (accumulator >= fixedTimeStep) {
            gameStateManager.update();
            accumulator -= fixedTimeStep;
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
