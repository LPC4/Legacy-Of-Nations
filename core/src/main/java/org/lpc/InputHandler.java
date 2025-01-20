package org.lpc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import jdk.jshell.spi.ExecutionControl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lpc.map.BaseMap;
import org.lpc.map.maps.SurfaceMap;
import org.lpc.terrain.buildings.buildings.Farm;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import static org.lpc.utility.Constants.*;

public class InputHandler implements InputProcessor {
    private static final Logger LOGGER = LogManager.getLogger(InputHandler.class);

    private static final int CLICK_THRESHOLD = 10; // Tolerance for detecting a click
    private static final int DRAG_THRESHOLD = 10;  // Tolerance for detecting dragging

    private final MainGame game;
    private final Settings settings;
    private boolean isDragging;
    private int lastX;
    private int lastY;

    public InputHandler(MainGame game, Settings settings) {
        this.game = game;
        this.settings = settings;
        Gdx.input.setInputProcessor(this);
        LOGGER.info("InputHandler initialized");

    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        handleZoom(amountY);
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
        if (keycode == Input.Keys.R) {
            LOGGER.info("R key pressed, changing renderResources to {}", settings.changeRenderResources());
        }
        if (keycode == Input.Keys.G) {
            LOGGER.info("G key pressed, changing renderGrid to {}", settings.changeRenderGrid());
        }
        if (keycode == Input.Keys.RIGHT) {
            settings.increaseTPS();
            LOGGER.info("RIGHT key pressed, increasing ticks per second to {}", settings.getTicksPerSecond());
        }
        if (keycode == Input.Keys.LEFT) {
            settings.decreaseTPS();
            LOGGER.info("LEFT key pressed, decreasing ticks per second to {}", settings.getTicksPerSecond());
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
        if (button == Input.Buttons.LEFT) {
            if (!isDragging && Math.abs(screenX - lastX) < CLICK_THRESHOLD && Math.abs(screenY - lastY) < CLICK_THRESHOLD) {
                handleClick(screenX, screenY);
            }
            isDragging = false; // Reset dragging flag
            LOGGER.debug("Touch up: screenX={}, screenY={}", screenX, screenY);
        } else if (button == Input.Buttons.RIGHT) {
            // Reset the selected tile on right-click
            unselectTile();
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

    private void handleClick(int screenX, int screenY) {
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)) {
            handleCtrlClick(screenX, screenY);
        }
        // normal click, nothing for now
    }

    private void handleCtrlClick(int screenX, int screenY) {
        LOGGER.debug("Handling click at screenX={}, screenY={}", screenX, screenY);
        OrthographicCamera camera = game.getGameScreen().getCamera();
        Vector3 worldCoordinates = camera.unproject(new Vector3(screenX, screenY, 0));
        Vector2 screenCoordinates = new Vector2(screenX, Gdx.graphics.getHeight() - screenY); // Correct Y inversion

        switch (game.getGameStateManager().getMapSystem().getCurrentScale()) {
            case SURFACE:
                handleSurfaceClick(worldCoordinates, screenCoordinates);
                break;
            default:
                throw new UnsupportedOperationException("Scale not implemented: " +
                    game.getGameStateManager().getMapSystem().getCurrentScale());
        }
    }


    private void handleSurfaceClick(Vector3 worldCoordinates, Vector2 screenCoordinates) {
        SurfaceMap surfaceMap = game.getGameStateManager().getMapSystem().getSurfaceMap();
        Vector2 gridPos = surfaceMap.getGridPosition(worldCoordinates.x, worldCoordinates.y);

        SurfaceMap.SurfaceTile tile = surfaceMap.getTile((int) gridPos.x, (int) gridPos.y);

        if (tile == null) {
            LOGGER.warn("No tile found at gridCoordinates=({}, {})", gridPos.x, gridPos.y);
            return;
        }

        selectTile(tile, (int) screenCoordinates.x, (int) screenCoordinates.y);
    }

    private void selectTile(SurfaceMap.SurfaceTile tile, int screenX, int screenY) {
        game.getUiRenderer().setSelectedTile(tile, screenX, screenY);
    }

    private void unselectTile() {
        game.getUiRenderer().setSelectedTile(null, 0, 0);
    }


    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        // Implement touchCancelled functionality if needed
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // Implement mouseMoved functionality if needed
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


    public void handleDrag(int screenX, int screenY) {
        unselectTile();

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

    private void handleZoom(float amountY) {
        OrthographicCamera camera = game.getGameScreen().getCamera();

        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        Vector3 worldPos = camera.unproject(mousePos.cpy());

        float newZoom = camera.zoom + amountY * ZOOM_SPEED;
        camera.zoom = MathUtils.clamp(newZoom, MIN_ZOOM, MAX_ZOOM);
        camera.update();
        Vector3 newWorldPos = camera.unproject(mousePos);

        camera.position.add(worldPos.x - newWorldPos.x, worldPos.y - newWorldPos.y, 0);

        LOGGER.debug("Scrolled: amountY={}, newZoom={}",  amountY, newZoom);
    }
}
