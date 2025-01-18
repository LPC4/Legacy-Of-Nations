package org.lpc.map.maps;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import lombok.Getter;
import lombok.Setter;
import org.lpc.MainGame;
import org.lpc.map.BaseMap;
import org.lpc.map.MapScale;
import org.lpc.terrain.Building;
import org.lpc.terrain.ResourceNode;
import org.lpc.terrain.TerrainType;
import org.lpc.utility.Constants;
import org.lpc.utility.PerlinNoise;
import org.lpc.utility.Position;

@Getter
public class SurfaceMap extends BaseMap {
    private final SurfaceTile[][] tiles;
    private final PerlinNoise heightNoise;
    private final PerlinNoise moistureNoise;
    private final MainGame game;

    @Getter @Setter
    public static class SurfaceTile extends BaseTile {
        private TerrainType terrain;
        private ResourceNode resource;
        private Building building;
        private boolean explored;
        private float moisture;
        private float height;

        public SurfaceTile(Position pos, TerrainType terrain) {
            this.position = pos;
            this.terrain = terrain;
            this.explored = false;
        }
    }

    public SurfaceMap(int width, int height, MainGame game) {
        this.width = width;
        this.height = height;
        this.tiles = new SurfaceTile[width][height];
        this.heightNoise = new PerlinNoise();
        this.moistureNoise = new PerlinNoise();
        this.game = game;
        generateMap();
    }

    @Override
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        OrthographicCamera camera = game.getGameScreen().getCamera();
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float tileSize = MapScale.SURFACE.getPixelsPerTile();

        // Calculate visible range
        float leftX = camera.position.x - camera.viewportWidth / 2 * camera.zoom - tileSize;
        float rightX = camera.position.x + camera.viewportWidth / 2 * camera.zoom + tileSize;
        float bottomY = camera.position.y - camera.viewportHeight / 2 * camera.zoom - tileSize;
        float topY = camera.position.y + camera.viewportHeight / 2 * camera.zoom + tileSize;

        // Convert to tile coordinates
        int startX = Math.max(0, (int) (leftX / tileSize));
        int endX = Math.min(width - 1, (int) (rightX / tileSize));
        int startY = Math.max(0, (int) (bottomY / tileSize));
        int endY = Math.min(height - 1, (int) (topY / tileSize));

        // Render only visible tiles
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                SurfaceTile tile = tiles[x][y];
                renderTile(shapeRenderer, tile, x, y);
            }
        }

        shapeRenderer.end();
    }

    private void renderTile(ShapeRenderer shapeRenderer, SurfaceTile tile, int x, int y) {
        float tileSize = MapScale.SURFACE.getPixelsPerTile();
        float tileX = x * tileSize;
        float tileY = y * tileSize;

        shapeRenderer.setColor(getTerrainColor(tile.getTerrain()));
        shapeRenderer.rect(tileX, tileY, tileSize, tileSize);
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

    @Override
    protected void generateMap() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                SurfaceTile tile = generateTile(x, y);
                tiles[x][y] = tile;
            }
        }
    }

    private SurfaceTile generateTile(int x, int y) {
        double scale = 0.04;

        // Generate base noise values
        double heightValue = generateHeight(x, y, scale);
        double moistureValue = generateMoisture(x, y, scale);
        double riverValue = generateRiver(x, y, scale);

        // Normalize noise values
        heightValue = (heightValue + 1) / 2;
        moistureValue = (moistureValue + 1) / 2;
        riverValue = (riverValue + 1) / 2;

        // Adjust height based on river influence
        heightValue = applyRiverEffect(heightValue, riverValue);

        // Determine terrain type based on height and moisture
        TerrainType terrain = determineTerrainType((float) heightValue, (float) moistureValue);

        SurfaceTile tile = new SurfaceTile(new Position(x, y, MapScale.SURFACE), terrain);
        tile.setMoisture((float) moistureValue);
        tile.setHeight((float) heightValue);

        return tile;
    }

    private double generateHeight(int x, int y, double scale) {
        return heightNoise.octaveNoise(x, y, 5, 0.001, scale);
    }

    private double generateMoisture(int x, int y, double scale) {
        return moistureNoise.smoothNoise(x, y, 5, 0.5, scale, 1.5);
    }

    private double generateRiver(int x, int y, double scale) {
        return heightNoise.riverNoise(x, y, 5, 0.05, scale);
    }

    private double applyRiverEffect(double heightValue, double riverValue) {
        if (riverValue > 0.5) {
            return heightValue - riverValue * 0.1; // Adjust the strength of the river effect here
        }
        return heightValue;
    }

    private TerrainType determineTerrainType(float height, float moisture) {
        if (height < 0.2f) return TerrainType.WATER;
        else if (height < 0.3f) return TerrainType.BEACH;
        else if (height < 0.6f) {
            if (moisture > 0.7f) return TerrainType.FOREST;
            else if (moisture > 0.4f) return TerrainType.PLAINS;
            else return TerrainType.DESERT;
        }
        else if (height < 0.7f) {
            if (moisture > 0.5f) return TerrainType.FOREST;
            else return TerrainType.HILLS;
        }
        else {
            return TerrainType.MOUNTAIN;
        }
    }

    @Override
    public void update() {
        // Update logic if needed
    }
}
