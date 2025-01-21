package org.lpc.map.maps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lpc.BaseInputHandler;
import org.lpc.MainGame;
import org.lpc.Settings;
import org.lpc.map.IMapInput;

import static org.lpc.utility.Constants.*;

public class SurfaceMapInput implements IMapInput {
    private static final Logger LOGGER = LogManager.getLogger(SurfaceMapInput.class);
    private final MainGame game;
    private boolean tileSelected;

    public SurfaceMapInput(MainGame game) {
        this.game = game;
        this.tileSelected = false;
    }

    // Overridden methods get called from BaseInputHandler
    @Override
    public void handleClick(BaseInputHandler handler, int button, int screenX, int screenY) {
        if (button == Input.Buttons.LEFT) {
            if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)) {
                handleCtrlClick(screenX, screenY);
            }
        }
        else if (button == Input.Buttons.RIGHT) {
            unselectTile();
        }
    }

    @Override
    public void handleDrag(BaseInputHandler handler, int screenX, int screenY) {
        if (tileSelected) {
            unselectTile();
        }

        moveCamera(handler, screenX, screenY);
    }

    private void moveCamera(BaseInputHandler handler, int screenX, int screenY) {
        OrthographicCamera camera = game.getGameScreen().getCamera();
        float deltaX = screenX - handler.getLastX();
        float deltaY = screenY - handler.getLastY();
        camera.position.x -= deltaX * camera.zoom;
        camera.position.y += deltaY * camera.zoom;
        camera.update();
    }

    @Override
    public void handleKey(BaseInputHandler handler, int keycode) {
        Settings settings = game.getSettings();

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
    }

    @Override
    public void handleScroll(BaseInputHandler handler, float amountX, float amountY) {
        handleZoom(amountY);
    }

    private void handleZoom(float amountY) {
        if (tileSelected) {
            unselectTile();
        }

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

    private void handleCtrlClick(int screenX, int screenY) {
        LOGGER.debug("Handling click at screenX={}, screenY={}", screenX, screenY);
        OrthographicCamera camera = game.getGameScreen().getCamera();
        Vector3 worldCoordinates = camera.unproject(new Vector3(screenX, screenY, 0));
        Vector2 screenCoordinates = new Vector2(screenX, Gdx.graphics.getHeight() - screenY); // Correct Y inversion

        showTileInfo(worldCoordinates, screenCoordinates);
    }

    private void showTileInfo(Vector3 worldCoordinates, Vector2 screenCoordinates) {
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
        tileSelected = true;
    }

    private void unselectTile() {
        game.getUiRenderer().setSelectedTile(null, 0, 0);
        tileSelected = false;
    }
}
