package org.lpc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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
import org.lpc.terrain.resources.ResourceNode;
import org.lpc.terrain.resources.ResourceType;

@Getter
@Setter
public class UIRenderer {
    private static final Logger LOGGER = LogManager.getLogger(UIRenderer.class);
    private static final int PADDING = 10;
    private static final int LINE_HEIGHT = 20;
    private static final Color BACKGROUND_COLOR = new Color(0.1f, 0.1f, 0.1f, 0.1f);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final float TOOLTIP_OFFSET = 20f;

    private final MainGame game;
    private final BitmapFont font;
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();

    private SurfaceMap.SurfaceTile selectedTile;
    private int mouseX, mouseY;

    public UIRenderer(MainGame game) {
        this.game = game;
        this.font = new BitmapFont();
        this.font.getData().markupEnabled = true;
    }

    public void render(SpriteBatch batch, GameStateManager gameStateManager, OrthographicCamera camera) {
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        renderCivilisationInfo(batch, gameStateManager.getCivilisation());
        batch.end();

        if (selectedTile != null) {
            renderTileTooltip(batch, camera);
        }
    }

    private void renderCivilisationInfo(SpriteBatch batch, Civilisation civilisation) {
        String infoText = String.format(
            "[#89CFF0]Civilisation:[] %s\n" +
                "[#89CFF0]Resources:[]\n%s",
            civilisation.getName(),
            formatCivilisationResources(civilisation)
        );
        float yPos = Gdx.graphics.getHeight() - PADDING;

        font.draw(batch, infoText, PADDING, yPos);
    }

    private String formatCivilisationResources(Civilisation civilisation) {
        StringBuilder sb = new StringBuilder();
        for (ResourceType type : ResourceType.values()) {
            int amount = civilisation.getResourceHandler().getResources().get(type);
            if (amount > 0) {
                sb.append(String.format("[#AAAAAA]%-8s[]: [#FFFFFF]%6d[]\n",
                    type.toString(),
                    amount
                ));
            }
        }
        return sb.toString();
    }

    private void renderTileTooltip(SpriteBatch batch, OrthographicCamera camera) {
        String tooltipText = createTooltipText();
        GlyphLayout layout = new GlyphLayout(font, tooltipText);

        // Convert screen coordinates properly
        float tooltipX = mouseX + TOOLTIP_OFFSET;
        float tooltipY = mouseY + TOOLTIP_OFFSET; // Changed to + for proper positioning
        float tooltipWidth = layout.width + PADDING * 2;
        float tooltipHeight = layout.height + PADDING * 2;

        // Adjust for screen boundaries
        if (tooltipX + tooltipWidth > Gdx.graphics.getWidth()) {
            tooltipX = Gdx.graphics.getWidth() - tooltipWidth - PADDING;
        }
        if (tooltipY > Gdx.graphics.getHeight()) {
            tooltipY = Gdx.graphics.getHeight() - PADDING;
        }

        // Enable blending for transparency
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Draw background first
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(BACKGROUND_COLOR);
        shapeRenderer.rect(tooltipX, tooltipY - tooltipHeight, tooltipWidth, tooltipHeight);
        shapeRenderer.end();

        // Then draw text on top
        batch.begin();
        font.setColor(TEXT_COLOR);
        font.draw(batch, tooltipText,
            tooltipX + PADDING,
            tooltipY - PADDING // Corrected vertical alignment
        );
        batch.end();

        // Disable blending after drawing
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private String createTooltipText() {
        if (selectedTile == null) return "";

        StringBuilder sb = new StringBuilder();
        // Title section
        sb.append("[#89CFF0]Terrain:[] ").append(selectedTile.getTerrain()).append("\n");

        // Building section
        if (selectedTile.getBuilding() != null) {
            sb.append("[#89CFF0]Building:[] ").append(selectedTile.getBuilding()).append("\n");
        }

        // Resources section
        sb.append("[#89CFF0]Resources:[]\n").append(formatResources(selectedTile.getResources()));
        return sb.toString();
    }

    private String formatResources(ResourceNode resources) {
        StringBuilder sb = new StringBuilder();
        // Manually format each line for perfect alignment
        for (ResourceType type : ResourceType.values()) {
            int quantity = resources.getResourceQuantity(type);
            if (quantity > 0) {
                sb.append(String.format("[#AAAAAA]%-8s[]: [#FFFFFF]%6d[]\n",
                    type.toString(),
                    quantity
                ));
            }
        }
        return sb.toString();
    }

    private String formatResources(String resourceString) {
        // Align resource quantities using monospace formatting
        return resourceString.replaceAll("(?m)^- ", "[#AAAAAA]")
            .replaceAll(": ", ": [#FFFFFF]")
            .replaceAll("(?m)\\n", "[]\n");
    }

    public void dispose() {
        font.dispose();
        shapeRenderer.dispose();
    }

    public void updateSelectedTile(SurfaceMap.SurfaceTile tile, int screenX, int screenY) {
        selectedTile = tile;
        mouseX = screenX;
        mouseY = screenY;
    }

    public void clearTileSelection() {
        selectedTile = null;
    }
}
