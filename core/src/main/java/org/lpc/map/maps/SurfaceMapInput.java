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
    private static final float ZOOM_SENSITIVITY = 0.5f;

    private final MainGame game;
    private boolean tileSelected;

    public SurfaceMapInput(MainGame game) {
        this.game = game;
        this.tileSelected = false;
    }

    @Override
    public void handleClick(BaseInputHandler handler, int button, int screenX, int screenY) {
        switch (button) {
            case Input.Buttons.LEFT -> handleLeftClick(screenX, screenY);
            case Input.Buttons.RIGHT -> unselectTile();
        }
    }

    private void handleLeftClick(int screenX, int screenY) {
        if (isControlKeyPressed()) {
            handleCtrlClick(screenX, screenY);
        }
    }

    private boolean isControlKeyPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ||
            Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);
    }

    @Override
    public void handleDrag(BaseInputHandler handler, int screenX, int screenY) {
        clearSelection();
        adjustCameraPosition(handler, screenX, screenY);
    }

    private void adjustCameraPosition(BaseInputHandler handler, int screenX, int screenY) {
        OrthographicCamera camera = game.getGameScreen().getCamera();
        float deltaX = (handler.getLastX() - screenX) * camera.zoom;
        float deltaY = (screenY - handler.getLastY()) * camera.zoom;

        camera.translate(deltaX, deltaY);
        camera.update();
    }

    @Override
    public void handleKey(BaseInputHandler handler, int keycode) {
        Settings settings = game.getSettings();
        switch (keycode) {
            case Input.Keys.R -> toggleSetting(settings::toggleRenderResources, "Render Resources");
            case Input.Keys.G -> toggleSetting(settings::toggleRenderGrid, "Render Grid");
            case Input.Keys.RIGHT -> adjustTickRate(settings::increaseTPS, "Increased");
            case Input.Keys.LEFT -> adjustTickRate(settings::decreaseTPS, "Decreased");
        }
    }

    private void toggleSetting(Runnable toggleAction, String settingName) {
        toggleAction.run();
        LOGGER.info("{} toggled to {}", settingName, game.getSettings().isRenderResources());
    }

    private void adjustTickRate(Runnable adjustAction, String action) {
        adjustAction.run();
        LOGGER.info("{} TPS to {}", action, game.getSettings().getTicksPerSecond());
    }

    @Override
    public void handleScroll(BaseInputHandler handler, float amountX, float amountY) {
        clearSelection();
        adjustZoomLevel(amountY * ZOOM_SENSITIVITY);
    }

    private void adjustZoomLevel(float zoomDelta) {
        OrthographicCamera camera = game.getGameScreen().getCamera();
        Vector3 mouseBefore = getWorldMousePosition(camera);

        camera.zoom = MathUtils.clamp(camera.zoom + zoomDelta, MIN_ZOOM, MAX_ZOOM);
        camera.update();

        Vector3 mouseAfter = getWorldMousePosition(camera);
        camera.position.add(mouseBefore.sub(mouseAfter));
        camera.update();
    }

    private Vector3 getWorldMousePosition(OrthographicCamera camera) {
        return camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
    }

    private void handleCtrlClick(int screenX, int screenY) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Control click at [{},{}]", screenX, screenY);
        }

        SurfaceMap.SurfaceTile tile = getTileAtScreenPosition(screenX, screenY);
        if (tile != null) {
            selectTile(tile, screenX, screenY);
        }
    }

    private SurfaceMap.SurfaceTile getTileAtScreenPosition(int screenX, int screenY) {
        OrthographicCamera camera = game.getGameScreen().getCamera();
        Vector3 worldPos = camera.unproject(new Vector3(screenX, screenY, 0));
        SurfaceMap map = game.getGameStateManager().getMapSystem().getSurfaceMap();
        Vector2 gridPos = map.getGridPosition(worldPos.x, worldPos.y);

        if (map.isWithinBounds(gridPos)) {
            return map.getTile((int) gridPos.x, (int) gridPos.y);
        }

        return null;
    }

    private void selectTile(SurfaceMap.SurfaceTile tile, int screenX, int screenY) {
        game.getUiRenderer().updateSelectedTile(tile, screenX, invertScreenY(screenY));
        tileSelected = true;
    }

    private int invertScreenY(int screenY) {
        return Gdx.graphics.getHeight() - screenY;
    }

    private void clearSelection() {
        if (tileSelected) {
            game.getUiRenderer().clearTileSelection();
            tileSelected = false;
        }
    }

    private void unselectTile() {
        clearSelection();
    }
}
