package org.lpc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lpc.civilisation.Civilisation;
import org.lpc.map.maps.SurfaceMap;

@Getter @Setter
public class UIRenderer {
    private static final Logger LOGGER = LogManager.getLogger(UIRenderer.class);
    private static final int PADDING = 10;
    private static final int LINE_HEIGHT = 20;

    private final MainGame game;
    private final BitmapFont font;

    private SurfaceMap.SurfaceTile selectedTile;
    private int mouseX, mouseY;
    private TileInfoMode tileInfoMode = null;

    private enum TileInfoMode {
        OVERLAY,
        TOOLTIP
    }

    public UIRenderer(MainGame game) {
        this.game = game;
        this.font = new BitmapFont();
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, GameStateManager gameStateManager, OrthographicCamera camera) {
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        renderCivilisationInfo(batch, gameStateManager.getCivilisation());

        if (selectedTile != null) {
            renderSurfaceTileInfo(batch, selectedTile, mouseX, mouseY);
        }

        batch.end();
    }

    private void renderCivilisationInfo(SpriteBatch batch, Civilisation civilisation) {
        int y = Gdx.graphics.getHeight() - PADDING;
        font.setColor(Color.WHITE);

        // Assuming these are the getters in your Civilisation class
        font.draw(batch, civilisation.toString(), PADDING, y);
    }

    public void renderSurfaceTileInfo(SpriteBatch batch, SurfaceMap.SurfaceTile tile, int mouseX, int mouseY) {
        StringBuilder tooltip = new StringBuilder();
        tooltip.append("Terrain: ").append(tile.getTerrain()).append("\n");

        if (tile.getBuilding() != null) {
            tooltip.append(tile.getBuilding()).append("\n");
        }

        tooltip.append(tile.getResources().toString());

        GlyphLayout layout = new GlyphLayout(font, tooltip.toString());

        int renderX = mouseX + 10; // Offset to avoid overlapping the cursor
        int renderY = mouseY + (int) layout.height - 10; // Render above the cursor, idk why -10 works

        // Adjust to ensure it doesn't go off-screen
        if (renderX + layout.width > Gdx.graphics.getWidth()) {
            renderX = Gdx.graphics.getWidth() - (int) layout.width - 10;
        }
        if (renderY > Gdx.graphics.getHeight()) {
            renderY = Gdx.graphics.getHeight() - 10;
        }

        // Render the tooltip
        font.setColor(Color.WHITE);
        font.draw(batch, tooltip.toString(), renderX, renderY);
    }

    public void setSelectedTile(SurfaceMap.SurfaceTile tile, int screenX, int screenY) {
        this.selectedTile = tile;
        this.mouseX = screenX;
        this.mouseY = screenY;
    }

    public void dispose() {
        font.dispose();
    }
}
