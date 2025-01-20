package org.lpc.map.maps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import lombok.Getter;
import org.lpc.MainGame;
import org.lpc.map.BaseMap;
import org.lpc.map.IMapRenderer;
import org.lpc.map.MapScale;
import org.lpc.terrain.TerrainType;
import org.lpc.terrain.buildings.BaseBuilding;
import org.lpc.terrain.resources.ResourceType;
import org.lpc.utility.Constants;

import java.util.List;

import static org.lpc.utility.Constants.MAX_ZOOM;

@Getter
public class SurfaceMapRenderer implements IMapRenderer<SurfaceMap.SurfaceTile> {
    private final MainGame game;
    private final OrthographicCamera camera;

    private static class ViewBounds {
        int startX, endX, startY, endY;
    }

    public SurfaceMapRenderer(MainGame game) {
        this.game = game;
        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render(SurfaceMap.SurfaceTile[][] tiles, ShapeRenderer shapeRenderer, SpriteBatch batch) {
        renderGameWorld(tiles, shapeRenderer, batch);
    }

    private void renderGameWorld(SurfaceMap.SurfaceTile[][] tiles, ShapeRenderer shapeRenderer, SpriteBatch batch) {
        OrthographicCamera gameCamera = game.getGameScreen().getCamera();
        gameCamera.update();

        // Calculate visible area
        float tileSize = MapScale.SURFACE.getPixelsPerTile();
        ViewBounds viewBounds = calculateViewBounds(gameCamera, tileSize, tiles);

        // Set the game camera for world rendering
        shapeRenderer.setProjectionMatrix(gameCamera.combined);
        batch.setProjectionMatrix(gameCamera.combined);

        // 1. Render solid terrain first
        beginRenderShapes(shapeRenderer);
        renderTerrain(tiles, viewBounds.startX, viewBounds.endX, viewBounds.startY, viewBounds.endY, shapeRenderer);
        endRenderShapes(shapeRenderer);

        // 2. Render transparent elements
        enableBlend();
        beginRenderShapes(shapeRenderer);
            renderCivilisationBorders(tiles, viewBounds.startX, viewBounds.endX, viewBounds.startY, viewBounds.endY, shapeRenderer);

            if (game.getSettings().isRenderGrid()) {
                renderGrid(shapeRenderer, tiles.length, tiles[0].length, tileSize);
            }
        endRenderShapes(shapeRenderer);

        // 3. Render all sprites
        beginRenderSprites(batch);
            renderBuildingSprites(tiles, viewBounds.startX, viewBounds.endX, viewBounds.startY, viewBounds.endY, batch);
        endRenderSprites(batch);

        // 4. Render progress bars
        beginRenderShapes(shapeRenderer);
            renderBuildingProgressBars(tiles, viewBounds.startX, viewBounds.endX, viewBounds.startY, viewBounds.endY, shapeRenderer);
        endRenderShapes(shapeRenderer);

        disableBlend();
    }

    private void renderTerrain(SurfaceMap.SurfaceTile[][] tiles, int startX, int endX, int startY, int endY,
                               ShapeRenderer shapeRenderer) {
        float tileSize = MapScale.SURFACE.getPixelsPerTile();

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                SurfaceMap.SurfaceTile tile = tiles[x][y];
                renderTile(shapeRenderer, tile, x, y, tileSize);

                if (game.getSettings().isRenderResources()) {
                    renderResources(tile, x, y, shapeRenderer);
                }
            }
        }
    }

    private void renderGrid(ShapeRenderer shapeRenderer, int width, int height, float tileSize) {
        shapeRenderer.setColor(new Color(0.8f, 0.8f, 0.8f, 0.1f));
        shapeRenderer.set(ShapeRenderer.ShapeType.Line);

        for (int x = 0; x < width; x++) {
            shapeRenderer.line(x * tileSize, 0, x * tileSize, height * tileSize);
        }

        for (int y = 0; y < height; y++) {
            shapeRenderer.line(0, y * tileSize, width * tileSize, y * tileSize);
        }
    }

    private void renderBuildingSprites(SurfaceMap.SurfaceTile[][] surfaceTiles, int startX, int endX, int startY, int endY, SpriteBatch batch) {
        float blur = 0;

        if (camera.zoom > 2f) {
            blur = camera.zoom - 2f;
        }

        batch.setShader(game.getBlurShader());
        game.getBlurShader().bind();
        game.getBlurShader().setUniformf("blur", blur);

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                SurfaceMap.SurfaceTile tile = surfaceTiles[x][y];
                if (tile.getBuilding() == null) continue;

                BaseBuilding building = tile.getBuilding();
                Sprite buildingSprite = building.getSprite();

                if (buildingSprite == null) continue;

                buildingSprite.setPosition(x * MapScale.SURFACE.getPixelsPerTile(), y * MapScale.SURFACE.getPixelsPerTile());
                buildingSprite.draw(batch);
            }
        }

        batch.setShader(null);
    }

    private void renderBuildingProgressBars(SurfaceMap.SurfaceTile[][] surfaceTiles, int startX, int endX, int startY, int endY, ShapeRenderer renderer) {
        if (camera.zoom > 2f) return;

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                SurfaceMap.SurfaceTile tile = surfaceTiles[x][y];
                if (tile.getBuilding() == null) continue;

                BaseBuilding building = tile.getBuilding();
                if (building.getProgressPercentage() == -1) continue;

                renderBuildingProgress(renderer, building, x, y);
            }
        }
    }

    private void renderBuildingProgress(ShapeRenderer renderer, BaseBuilding building, int x, int y) {
        float tileSize = MapScale.SURFACE.getPixelsPerTile();
        float padding = 2f;
        float tileX = x * tileSize + padding;
        float tileY = y * tileSize + padding;
        float innerTileSize = tileSize - (padding * 2);

        float progress = building.getProgressPercentage() / 100f;
        float progressWidth = innerTileSize * progress;
        float progressHeight = 4f;

        renderer.set(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.GREEN);

        renderer.rect(tileX, tileY, progressWidth, progressHeight);

        renderer.set(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.BLACK);

        renderer.rect(tileX, tileY, innerTileSize, progressHeight);
    }

    private void renderCivilisationBorders(SurfaceMap.SurfaceTile[][] tiles, int startX, int endX, int startY, int endY,
                                           ShapeRenderer shapeRenderer) {
        shapeRenderer.set(ShapeRenderer.ShapeType.Line);

        int tileSize = MapScale.SURFACE.getPixelsPerTile();

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                renderTileBorders(tiles, x, y, tileSize, shapeRenderer);
            }
        }
    }

    private void renderTileBorders(SurfaceMap.SurfaceTile[][] tiles, int x, int y, int tileSize, ShapeRenderer shapeRenderer) {
        SurfaceMap.SurfaceTile tile = tiles[x][y];

        if (tile.getOwner() == null) {
            return;
        }

        renderTopBorder(tiles, x, y, tile, tileSize, shapeRenderer);
        renderRightBorder(tiles, x, y, tile, tileSize, shapeRenderer);
        renderBottomBorder(tiles, x, y, tile, tileSize, shapeRenderer);
        renderLeftBorder(tiles, x, y, tile, tileSize, shapeRenderer);
    }

    private void renderTile(ShapeRenderer shapeRenderer, SurfaceMap.SurfaceTile tile, int x, int y, float tileSize) {
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);

        float tileX = x * tileSize;
        float tileY = y * tileSize;
        float tileHeight = tile.getHeight();

        Color finalColor;
        if (tile.getTerrain() == TerrainType.WATER) {
            Color heightShade = new Color(tileHeight, tileHeight, tileHeight, 1f);
            finalColor = getTerrainColor(tile.getTerrain()).cpy();
            finalColor.add(heightShade);
        }
        else if (tile.getTerrain() == TerrainType.MOUNTAIN) {
            Color heightShade = new Color(tileHeight / 4, tileHeight / 4, tileHeight / 4, 1f);
            finalColor = getTerrainColor(tile.getTerrain()).cpy();
            finalColor.sub(heightShade);
        }
        else {
            finalColor = getTerrainColor(tile.getTerrain()).cpy();
        }

        shapeRenderer.setColor(finalColor);
        shapeRenderer.rect(tileX, tileY, tileSize, tileSize);
    }

    public void renderResources(SurfaceMap.SurfaceTile tile, int x, int y, ShapeRenderer shapeRenderer) {
        if (camera.zoom > 2f) return;

        float tileSize = MapScale.SURFACE.getPixelsPerTile();
        float padding = 2f;
        float tileX = x * tileSize + padding;
        float tileY = y * tileSize + padding;
        float innerTileSize = tileSize - (padding * 2);

        renderResourceDots(shapeRenderer, tile, tileX, tileY, innerTileSize);
    }

    private void renderResourceDots(ShapeRenderer shapeRenderer, SurfaceMap.SurfaceTile tile,
                                    float tileX, float tileY, float innerTileSize) {
        int resourceSize = 4;
        float resourcePadding = 4f;
        float resourceX = tileX + resourcePadding;
        float resourceY = tileY + resourcePadding;

        for (ResourceType type : ResourceType.values()) {
            int quantity = tile.getResources().getResourceQuantity(type);
            if (quantity == 0) continue;

            int dots = (quantity + 9) / 10;
            Color color = getResourceColor(type);
            shapeRenderer.setColor(color);

            for (int i = 0; i < dots; i++) {
                shapeRenderer.circle(resourceX, resourceY, resourceSize);
                resourceX += resourceSize + resourcePadding;
                if (resourceX > tileX + innerTileSize) {
                    resourceX = tileX + resourcePadding;
                    resourceY += resourceSize + resourcePadding;
                }
            }
        }
    }

    private void renderTopBorder(SurfaceMap.SurfaceTile[][] tiles, int x, int y, SurfaceMap.SurfaceTile tile,
                                 int tileSize, ShapeRenderer shapeRenderer) {
        if (needsBorder(tiles, x, y + 1, tile)) {
            drawLineColor(tile.getOwner().getColor(),
                x * tileSize, (y + 1) * tileSize,
                (x + 1) * tileSize, (y + 1) * tileSize,
                shapeRenderer);
        }
    }

    private void renderRightBorder(SurfaceMap.SurfaceTile[][] tiles, int x, int y, SurfaceMap.SurfaceTile tile,
                                   int tileSize, ShapeRenderer shapeRenderer) {
        if (needsBorder(tiles, x + 1, y, tile)) {
            drawLineColor(tile.getOwner().getColor(),
                (x + 1) * tileSize, y * tileSize,
                (x + 1) * tileSize, (y + 1) * tileSize,
                shapeRenderer);
        }
    }

    private void renderBottomBorder(SurfaceMap.SurfaceTile[][] tiles, int x, int y, SurfaceMap.SurfaceTile tile,
                                    int tileSize, ShapeRenderer shapeRenderer) {
        if (y > 0 && isDifferentOwner(tiles[x][y - 1], tile)) {
            drawLineColor(tile.getOwner().getColor(),
                x * tileSize, y * tileSize,
                (x + 1) * tileSize, y * tileSize,
                shapeRenderer);
        }
    }

    private void renderLeftBorder(SurfaceMap.SurfaceTile[][] tiles, int x, int y, SurfaceMap.SurfaceTile tile,
                                  int tileSize, ShapeRenderer shapeRenderer) {
        if (x > 0 && isDifferentOwner(tiles[x - 1][y], tile)) {
            drawLineColor(tile.getOwner().getColor(),
                x * tileSize, y * tileSize,
                x * tileSize, (y + 1) * tileSize,
                shapeRenderer);
        }
    }

    private boolean needsBorder(SurfaceMap.SurfaceTile[][] tiles, int x, int y, SurfaceMap.SurfaceTile currentTile) {
        return isWithinBounds(tiles, x, y) && isDifferentOwner(tiles[x][y], currentTile);
    }

    private boolean isWithinBounds(SurfaceMap.SurfaceTile[][] tiles, int x, int y) {
        return x >= 0 && x < tiles.length && y >= 0 && y < tiles[0].length;
    }

    private boolean isDifferentOwner(SurfaceMap.SurfaceTile tile1, SurfaceMap.SurfaceTile tile2) {
        return tile1 == null || tile1.getOwner() != tile2.getOwner();
    }

    private void drawLineColor(Color color, float x1, float y1, float x2, float y2, ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(color);
        shapeRenderer.line(x1, y1, x2, y2);
    }

    private void beginRenderShapes(ShapeRenderer shapeRenderer) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setAutoShapeType(true);
    }

    private void endRenderShapes(ShapeRenderer shapeRenderer) {
        shapeRenderer.end();
    }

    private void beginRenderSprites(SpriteBatch batch) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
    }

    private void endRenderSprites(SpriteBatch batch) {
        batch.end();
    }

    private void enableBlend() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    private void disableBlend() {
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private ViewBounds calculateViewBounds(OrthographicCamera camera, float tileSize, SurfaceMap.SurfaceTile[][] tiles) {
        ViewBounds bounds = new ViewBounds();

        float leftX = camera.position.x - camera.viewportWidth / 2 * camera.zoom - tileSize;
        float rightX = camera.position.x + camera.viewportWidth / 2 * camera.zoom + tileSize;
        float bottomY = camera.position.y - camera.viewportHeight / 2 * camera.zoom - tileSize;
        float topY = camera.position.y + camera.viewportHeight / 2 * camera.zoom + tileSize;

        bounds.startX = Math.max(0, (int) (leftX / tileSize));
        bounds.endX = Math.min(tiles.length - 1, (int) (rightX / tileSize));
        bounds.startY = Math.max(0, (int) (bottomY / tileSize));
        bounds.endY = Math.min(tiles[0].length - 1, (int) (topY / tileSize));

        return bounds;
    }

    private Color getTerrainColor(TerrainType terrain) {
        switch (terrain) {
            case WATER: return Constants.WATER_COLOR;
            case BEACH: return Constants.BEACH_COLOR;
            case DESERT: return Constants.DESERT_COLOR;
            case FOREST: return Constants.FOREST_COLOR;
            case HILLS: return Constants.HILLS_COLOR;
            case MOUNTAIN: return Constants.MOUNTAIN_COLOR;
            case PLAINS: return Constants.PLAINS_COLOR;
            default: return Color.WHITE;
        }
    }

    private Color getResourceColor(ResourceType type) {
        switch (type) {
            case FOOD: return Constants.FOOD_COLOR;
            case WOOD: return Constants.WOOD_COLOR;
            case STONE: return Constants.STONE_COLOR;
            case IRON: return Constants.IRON_COLOR;
            case GOLD: return Constants.GOLD_COLOR;
            default: return Color.WHITE;
        }
    }
}
