package org.lpc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import lombok.Getter;
import org.lpc.civilisation.Civilisation;

@Getter
public class UIRenderer {
    private final MainGame game;
    private final BitmapFont font;
    private static final int PADDING = 10;
    private static final int LINE_HEIGHT = 20;

    public UIRenderer(MainGame game) {
        this.game = game;
        this.font = new BitmapFont();
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, GameStateManager gameStateManager, OrthographicCamera camera) {
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        renderCivilisationInfo(batch, gameStateManager.getCivilisation());
    }

    private void renderCivilisationInfo(SpriteBatch batch, Civilisation civilisation) {
        batch.begin();

        int y = Gdx.graphics.getHeight() - PADDING;
        font.setColor(Color.WHITE);

        // Assuming these are the getters in your Civilisation class
        font.draw(batch, "Civilisation: " + civilisation.getName(), PADDING, y);
        y -= LINE_HEIGHT;
        font.draw(batch, "Population: " + civilisation.getPopulation(), PADDING, y);
        y -= LINE_HEIGHT;
        font.draw(batch, "Resources: " + civilisation.getResourceHandler().getResources(), PADDING, y);
        // Add more civilisation stats as needed

        batch.end();
    }

    public void dispose() {
        font.dispose();
    }
}
