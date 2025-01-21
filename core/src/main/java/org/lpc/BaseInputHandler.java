package org.lpc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lpc.map.IMapInput;

import static org.lpc.utility.Constants.*;

/**
 * Base input handler for the game
 * Handles input events and delegates them to the map-specific input handler
 * Also handles common input events like ESC key and fullscreen toggle
 */
public class BaseInputHandler implements InputProcessor {
    private static final Logger LOGGER = LogManager.getLogger(BaseInputHandler.class);
    private final MainGame game;

    private static final int CLICK_THRESHOLD = 10; // Tolerance for detecting a click
    private static final int DRAG_THRESHOLD = 10;  // Tolerance for detecting dragging

    // Map specific input handler
    private IMapInput mapSpecificInput;

    // Mouse utility
    @Getter private boolean isDragging;
    @Getter private int lastX;
    @Getter private int lastY;

    public BaseInputHandler(MainGame game, IMapInput mapSpecificInput) {
        this.game = game;
        this.mapSpecificInput = mapSpecificInput;
        Gdx.input.setInputProcessor(this);
        LOGGER.info("BaseInputHandler initialized");
    }

    public void setMapInputHandler(IMapInput mapInputHandler) {
        if (mapInputHandler == null) {
            LOGGER.error("MapInputHandler is null");
            return;
        }
        this.mapSpecificInput = mapInputHandler;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        mapSpecificInput.handleScroll(this, amountX, amountY);
        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        mapSpecificInput.handleKey(this, keycode);

        if (keycode == Input.Keys.ESCAPE) {
            game.setScreen(game.getMenuScreen());
            LOGGER.info("ESC key pressed, switching to MenuScreen");
        } else if (keycode == Input.Keys.F11) {
            if (Gdx.graphics.isFullscreen()) {
                Gdx.graphics.setWindowedMode(WINDOW_WIDTH, WINDOW_HEIGHT);
                LOGGER.info("Switched to windowed mode");
            } else {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                LOGGER.info("Switched to fullscreen mode");
            }
        }

        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            isDragging = false; // Reset dragging flag
            lastX = screenX;    // Record initial press coordinates
            lastY = screenY;
            LOGGER.debug("Touch down: screenX={}, screenY={}", screenX, screenY);
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (!isDragging && Math.abs(screenX - lastX) < CLICK_THRESHOLD && Math.abs(screenY - lastY) < CLICK_THRESHOLD) {
            handleClick(button, screenX, screenY);
        }

        if (button == Input.Buttons.LEFT) {
            isDragging = false; // Reset dragging flag
            LOGGER.debug("Touch up: screenX={}, screenY={}", screenX, screenY);
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (!isDragging) {
            // If the pointer moves significantly, it's no longer a click
            if (Math.abs(screenX - lastX) > DRAG_THRESHOLD || Math.abs(screenY - lastY) > DRAG_THRESHOLD) {
                isDragging = true;
            }
        }

        if (isDragging && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            handleDrag(screenX, screenY);
        }
        return true;
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    private void handleClick(int button, int screenX, int screenY) {
        mapSpecificInput.handleClick(this, button, screenX, screenY);
    }

    public void handleDrag(int screenX, int screenY) {
        mapSpecificInput.handleDrag(this, screenX, screenY);

        lastX = screenX;
        lastY = screenY;
    }
}
