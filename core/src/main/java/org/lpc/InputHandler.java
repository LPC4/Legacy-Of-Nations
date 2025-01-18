package org.lpc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;

import static org.lpc.utility.Constants.*;

public class InputHandler implements InputProcessor {
    private final MainGame game;

    private boolean isDragging;
    private int lastX;
    private int lastY;

    public InputHandler(MainGame game) {
        this.game = game;
        Gdx.input.setInputProcessor(this);
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

        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {

            game.setScreen(game.getMenuScreen());
        }

        if (keycode == Input.Keys.F11) {
            if (Gdx.graphics.isFullscreen()) {
                Gdx.graphics.setWindowedMode(WINDOW_WIDTH, WINDOW_HEIGHT);
            } else {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            }
        }
        return false;
    }


    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) { return false; }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            isDragging = true;
            lastX = screenX;
            lastY = screenY;
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            isDragging = false;
        }
        return true;
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
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
        }
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) { return false; }
}
