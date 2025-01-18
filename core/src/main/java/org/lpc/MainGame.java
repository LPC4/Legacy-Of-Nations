package org.lpc;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lpc.screens.GameScreen;
import org.lpc.screens.MenuScreen;
import org.lpc.screens.StartScreen;

@Getter
public class MainGame extends Game {
    private static final Logger LOGGER = LogManager.getLogger(MainGame.class);

    private GameStateManager gameStateManager;
    private GameScreen gameScreen;
    private StartScreen startScreen;
    private MenuScreen menuScreen;
    private InputHandler inputHandler;
    private float accumulator;

    @Override
    public void create() {
        LOGGER.info("Initializing game");

        this.gameStateManager = new GameStateManager(this);
        this.gameScreen = new GameScreen(this);
        this.startScreen = new StartScreen(this);
        this.menuScreen = new MenuScreen(this);
        this.inputHandler = new InputHandler(this);
        this.accumulator = 0;

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
        super.dispose();
        LOGGER.info("Disposing game resources");

        gameScreen.dispose();
        startScreen.dispose();
        menuScreen.dispose();

        LOGGER.info("Game disposed");
    }
}
