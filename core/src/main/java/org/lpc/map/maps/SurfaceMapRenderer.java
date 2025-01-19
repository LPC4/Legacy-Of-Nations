package org.lpc.map.maps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import lombok.Getter;
import org.lpc.MainGame;
import org.lpc.map.BaseMap;
import org.lpc.map.IMapRenderer;
import org.lpc.map.MapScale;
import org.lpc.terrain.TerrainType;
import org.lpc.terrain.resources.ResourceType;
import org.lpc.utility.Constants;

import java.util.List;

@Getter
public class SurfaceMapRenderer implements IMapRenderer {
    private final MainGame game;
    private final OrthographicCamera camera;

    public SurfaceMapRenderer(MainGame game) {
        this.game = game;
        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render(BaseMap.BaseTile[][] tiles, ShapeRenderer shapeRenderer, SpriteBatch batch) {
        if (!(tiles instanceof SurfaceMap.SurfaceTile[][])) {
           throw new IllegalArgumentException("SurfaceMapRenderer can only render SurfaceMap tiles");
        }
        SurfaceMap.SurfaceTile[][] surfaceTiles = (SurfaceMap.SurfaceTile[][]) tiles;

        OrthographicCamera camera = game.getGameScreen().getCamera();
        camera.update();

        // Calculate visible area
        float tileSize = MapScale.SURFACE.getPixelsPerTile();
        float leftX = camera.position.x - camera.viewportWidth / 2 * camera.zoom - tileSize;
        float rightX = camera.position.x + camera.viewportWidth / 2 * camera.zoom + tileSize;
        float bottomY = camera.position.y - camera.viewportHeight / 2 * camera.zoom - tileSize;
        float topY = camera.position.y + camera.viewportHeight / 2 * camera.zoom + tileSize;

        int startX = Math.max(0, (int) (leftX / tileSize));
        int endX = Math.min(tiles.length - 1, (int) (rightX / tileSize));
        int startY = Math.max(0, (int) (bottomY / tileSize));
        int endY = Math.min(tiles[0].length - 1, (int) (topY / tileSize));

        // Render terrain
        renderTerrain(surfaceTiles, startX, endX, startY, endY, shapeRenderer);
    }

    private void renderTerrain(SurfaceMap.SurfaceTile[][] tiles, int startX, int endX, int startY, int endY,
                               ShapeRenderer shapeRenderer) {
        OrthographicCamera camera = game.getGameScreen().getCamera();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float tileSize = MapScale.SURFACE.getPixelsPerTile();

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                SurfaceMap.SurfaceTile tile = tiles[x][y];
                renderTile(shapeRenderer, tile, x, y, tileSize);
            }
        }

        shapeRenderer.end();
    }

    private void renderTile(ShapeRenderer shapeRenderer, SurfaceMap.SurfaceTile tile, int x, int y, float tileSize) {
        float tileX = x * tileSize;
        float tileY = y * tileSize;
        float tileHeight = tile.getHeight();

        Color finalColor;
        if (tile.getTerrain() == TerrainType.WATER) {
            Color heightShade = new Color(tileHeight, tileHeight, tileHeight, 0.1f);
            finalColor = getTerrainColor(tile.getTerrain()).cpy();
            finalColor.add(heightShade);
        } else {
            finalColor = getTerrainColor(tile.getTerrain()).cpy();
        }

        shapeRenderer.setColor(finalColor);
        shapeRenderer.rect(tileX, tileY, tileSize, tileSize);
    }

    private void renderVegetation(List<Sprite> vegetation, float leftX, float rightX,
                                  float bottomY, float topY, SpriteBatch batch) {
        OrthographicCamera camera = game.getGameScreen().getCamera();
        batch.begin();
        batch.setProjectionMatrix(camera.combined);

        for (Sprite sprite : vegetation) {
            if (sprite.getX() < rightX && sprite.getX() + sprite.getWidth() > leftX &&
                sprite.getY() < topY && sprite.getY() + sprite.getHeight() > bottomY) {
                sprite.draw(batch);
            }
        }

        batch.end();
    }

    public void renderResources(SurfaceMap.SurfaceTile tile, int x, int y, ShapeRenderer shapeRenderer) {
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
