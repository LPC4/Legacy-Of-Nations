package org.lpc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.lpc.utility.Constants.*;

public class InputHandler implements InputProcessor {
    private static final Logger LOGGER = LogManager.getLogger(InputHandler.class);

    private final MainGame game;

    private boolean isDragging;
    private int lastX;
    private int lastY;

    public InputHandler(MainGame game) {
        this.game = game;
        Gdx.input.setInputProcessor(this);
        LOGGER.info("InputHandler initialized");
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        OrthographicCamera camera = game.getGameScreen().getCamera();

        // Store mouse position before zoom
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        Vector3 worldPos = camera.unproject(mousePos.cpy());

        // Zoom in when scrolling up, out when scrolling down
        float newZoom = camera.zoom + amountY * ZOOM_SPEED;
        camera.zoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, newZoom));

        // Update camera matrix
        camera.update();
        Vector3 newWorldPos = camera.unproject(mousePos);
        camera.position.add(worldPos.x - newWorldPos.x, worldPos.y - newWorldPos.y, 0);

        LOGGER.debug("Scrolled: amountX={}, amountY={}, newZoom={}", amountX, amountY, newZoom);
        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
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
    public boolean keyUp(int keycode) {
        // Implement keyUp functionality if needed
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        // Implement keyTyped functionality if needed
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            isDragging = true;
            lastX = screenX;
            lastY = screenY;
            LOGGER.debug("Touch down: screenX={}, screenY={}", screenX, screenY);
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            isDragging = false;
            LOGGER.debug("Touch up: screenX={}, screenY={}", screenX, screenY);
        }
        return true;
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        // Implement touchCancelled functionality if needed
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (isDragging) {
            OrthographicCamera camera = game.getGameScreen().getCamera();

            float deltaX = screenX - lastX;
            float deltaY = screenY - lastY;

            camera.position.x -= deltaX * camera.zoom;
            camera.position.y += deltaY * camera.zoom;

            lastX = screenX;
            lastY = screenY;

            camera.update();
            LOGGER.debug("Touch dragged: deltaX={}, deltaY={}", deltaX, deltaY);
        }
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // Implement mouseMoved functionality if needed
        return false;
    }
}
