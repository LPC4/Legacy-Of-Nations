package org.lpc.map.maps;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lombok.Getter;
import lombok.Setter;
import org.lpc.MainGame;
import org.lpc.map.BaseMap;
import org.lpc.map.MapScale;
import org.lpc.terrain.buildings.BaseBuilding;
import org.lpc.terrain.resources.ResourceNode;
import org.lpc.terrain.resources.ResourceType;
import org.lpc.terrain.TerrainType;
import org.lpc.utility.Constants;
import org.lpc.utility.PerlinNoise;
import org.lpc.utility.Position;

import java.sql.Array;
import java.util.*;

@Getter
public class SurfaceMap extends BaseMap {
    private final SurfaceTile[][] tiles;
    private final PerlinNoise heightNoise;
    private final PerlinNoise moistureNoise;
    private final MainGame game;
    private final ArrayList<Sprite> vegetation;

    @Getter
    @Setter
    public static class SurfaceTile extends BaseTile {
        private TerrainType terrain;
        private ResourceNode resources;
        private BaseBuilding building;
        private boolean explored;
        private float moisture;
        private float height;
        private float movementModifier;
        private float vegetationDensity;

        public SurfaceTile(Position pos, TerrainType terrain, float moisture, float height) {
            this.position = pos;
            this.terrain = terrain;
            this.explored = false;
            this.resources = new ResourceNode();
            this.moisture = moisture;
            this.height = height;
            this.movementModifier = TerrainType.getMovementModifier(terrain);
            this.vegetationDensity = TerrainType.calculateVegetationDensity(terrain, moisture, height);
        }
    }


    public SurfaceMap(int width, int height, MainGame game) {
        this.width = width;
        this.height = height;
        this.tiles = new SurfaceTile[width][height];
        this.heightNoise = new PerlinNoise();
        this.moistureNoise = new PerlinNoise();
        this.game = game;
        this.vegetation = new ArrayList<>();
        generateMap();
        generateResources();
        generateVegetation();
    }

    private void generateVegetation() {
        Random random = new Random();
        Texture vegetationTexture = Constants.VEGETATION_TEXTURE;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                SurfaceTile tile = tiles[x][y];
                if (TerrainType.hasVegetation(tile.getTerrain()) && random.nextFloat() < tile.getVegetationDensity()) {
                    Sprite sprite = new Sprite(vegetationTexture);
                    // Set sprite properties
                    sprite.setPosition(x * MapScale.SURFACE.getPixelsPerTile(), y * MapScale.SURFACE.getPixelsPerTile());
                    sprite.setSize(MapScale.SURFACE.getPixelsPerTile(), MapScale.SURFACE.getPixelsPerTile());
                    // Add some randomization
                    sprite.setRotation(random.nextFloat() * 360);
                    sprite.setScale(0.8f + random.nextFloat() * 0.4f);
                    vegetation.add(sprite);
                }
            }
        }
    }

    private void generateResources() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                SurfaceTile tile = tiles[x][y];
                generateResource(tile);
            }
        }
    }

    private void generateResource(SurfaceTile tile) {
        if (tile == null) throw new IllegalArgumentException("Tile cannot be null");

        TerrainType terrain = tile.getTerrain();

        float moisture = tile.getMoisture() * 2;
        float height = tile.getHeight() * 2;

        switch (terrain) {
            case WATER:
                generateResource(tile, ResourceType.FOOD, 40, 100, moisture);
                break;
            case BEACH:
                generateResource(tile, ResourceType.FOOD, 20, 40, moisture);
                generateResource(tile, ResourceType.STONE, 0, 50, height);
                generateResource(tile, ResourceType.IRON, 0, 40, height);
                break;
            case FOREST:
                generateResource(tile, ResourceType.FOOD, 50, 100, moisture);
                generateResource(tile, ResourceType.WOOD, 40, 80, height);
                generateResource(tile, ResourceType.STONE, 0, 40, height);
                generateResource(tile, ResourceType.IRON, 0, 20, height);
                break;
            case PLAINS:
                generateResource(tile, ResourceType.FOOD, 40, 80, moisture);
                generateResource(tile, ResourceType.WOOD, 10, 20, height);
                generateResource(tile, ResourceType.STONE, 0, 40, height);
                generateResource(tile, ResourceType.IRON, 0, 20, height);
                break;
            case DESERT:
                generateResource(tile, ResourceType.FOOD, 3, 30, moisture);
                generateResource(tile, ResourceType.GOLD, 0, 3, height / 10);
                generateResource(tile, ResourceType.IRON, 0, 10, height);
                break;
            case HILLS:
                generateResource(tile, ResourceType.FOOD, 20, 40, moisture);
                generateResource(tile, ResourceType.WOOD, 10, 20, height);
                generateResource(tile, ResourceType.STONE, 20, 80, height);
                generateResource(tile, ResourceType.IRON, 10, 30, height);
                break;
            case MOUNTAIN:
                generateResource(tile, ResourceType.FOOD, 10, 20, moisture);
                generateResource(tile, ResourceType.STONE, 40, 100, height);
                generateResource(tile, ResourceType.IRON, 20, 60, height);
                break;
        }
    }

    private void generateResource(SurfaceTile tile, ResourceType type, int min, int max, float modifier) {
        Random random = new Random();
        int quantity = random.nextInt(max - min) + min;

        if (modifier > 0)
            quantity = (int) (quantity * modifier);

        quantity = MathUtils.clamp(quantity, min, max);
        tile.getResources().addResource(type, quantity);
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

        double heightValue = generateHeight(x, y, scale);
        double moistureValue = generateMoisture(x, y, scale);
        double riverValue = generateRiver(x, y, scale);

        heightValue = (heightValue + 1) / 2;
        moistureValue = (moistureValue + 1) / 2;
        riverValue = (riverValue + 1) / 2;

        heightValue = applyRiverEffect(heightValue, riverValue);

        TerrainType terrain = determineTerrainType((float) heightValue, (float) moistureValue);

        return new SurfaceTile(new Position(x, y, MapScale.SURFACE), terrain, (float) moistureValue, (float) heightValue);
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
            return heightValue - riverValue * 0.1;
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
        } else if (height < 0.7f) {
            if (moisture > 0.5f) return TerrainType.FOREST;
            else return TerrainType.HILLS;
        } else {
            return TerrainType.MOUNTAIN;
        }
    }

    @Override
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        OrthographicCamera camera = game.getGameScreen().getCamera();
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float tileSize = MapScale.SURFACE.getPixelsPerTile();

        float leftX = camera.position.x - camera.viewportWidth / 2 * camera.zoom - tileSize;
        float rightX = camera.position.x + camera.viewportWidth / 2 * camera.zoom + tileSize;
        float bottomY = camera.position.y - camera.viewportHeight / 2 * camera.zoom - tileSize;
        float topY = camera.position.y + camera.viewportHeight / 2 * camera.zoom + tileSize;

        int startX = Math.max(0, (int) (leftX / tileSize));
        int endX = Math.min(width - 1, (int) (rightX / tileSize));
        int startY = Math.max(0, (int) (bottomY / tileSize));
        int endY = Math.min(height - 1, (int) (topY / tileSize));

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                SurfaceTile tile = tiles[x][y];
                renderTile(shapeRenderer, tile, x, y);
                //renderTileGrid(shapeRenderer, tile, x, y);
                //renderResources(shapeRenderer, tile, x, y);
            }
        }

        shapeRenderer.end();

        //renderVegetation(batch);
    }

    private void renderVegetation(SpriteBatch spriteBatch) {
        OrthographicCamera camera = game.getGameScreen().getCamera();
        float tileSize = MapScale.SURFACE.getPixelsPerTile();

        float leftX = camera.position.x - camera.viewportWidth / 2 * camera.zoom - tileSize;
        float rightX = camera.position.x + camera.viewportWidth / 2 * camera.zoom + tileSize;
        float bottomY = camera.position.y - camera.viewportHeight / 2 * camera.zoom - tileSize;
        float topY = camera.position.y + camera.viewportHeight / 2 * camera.zoom + tileSize;

        spriteBatch.begin();
        spriteBatch.setProjectionMatrix(camera.combined);

        for (Sprite sprite : vegetation) {
            if (sprite.getX() < rightX && sprite.getX() + sprite.getWidth() > leftX &&
                sprite.getY() < topY && sprite.getY() + sprite.getHeight() > bottomY) {
                sprite.draw(spriteBatch);
            }
        }

        spriteBatch.end();
    }

    private void renderTile(ShapeRenderer shapeRenderer, SurfaceTile tile, int x, int y) {
        float tileSize = MapScale.SURFACE.getPixelsPerTile();
        float tileX = x * tileSize;
        float tileY = y * tileSize;
        float tileHeight = tile.getHeight();

        Color heightShade;
        Color finalColor;

        // Render water with a slight shade of the height
        if (tile.getTerrain() == TerrainType.WATER) {
            heightShade = new Color(tileHeight, tileHeight, tileHeight, 0.1f);
            finalColor = getTerrainColor(tile.getTerrain()).cpy();
            finalColor.add(heightShade);
        } else {
            finalColor = getTerrainColor(tile.getTerrain()).cpy();
        }

        shapeRenderer.setColor(finalColor);
        shapeRenderer.rect(tileX, tileY, tileSize, tileSize);
    }

    private void renderTileGrid(ShapeRenderer shapeRenderer, SurfaceTile tile, int x, int y) {
        float tileSize = MapScale.SURFACE.getPixelsPerTile();
        float padding = 2f;
        float tileX = x * tileSize + padding;
        float tileY = y * tileSize + padding;
        float innerTileSize = tileSize - (padding * 2);

        shapeRenderer.setColor(new Color(0.2f, 0.2f, 0.2f, 0.3f));
        shapeRenderer.rect(x * tileSize, y * tileSize, tileSize, tileSize);

        shapeRenderer.setColor(getTerrainColor(tile.getTerrain()));
        shapeRenderer.rect(tileX, tileY, innerTileSize, innerTileSize);
    }

    private void renderResources(ShapeRenderer shapeRenderer, SurfaceTile tile, int x, int y) {
        float tileSize = MapScale.SURFACE.getPixelsPerTile();
        float padding = 2f;
        float tileX = x * tileSize + padding;
        float tileY = y * tileSize + padding;
        float innerTileSize = tileSize - (padding * 2);

        // Render tile background and terrain
        shapeRenderer.setColor(new Color(0.2f, 0.2f, 0.2f, 0.3f));
        shapeRenderer.rect(x * tileSize, y * tileSize, tileSize, tileSize);

        shapeRenderer.setColor(getTerrainColor(tile.getTerrain()));
        shapeRenderer.rect(tileX, tileY, innerTileSize, innerTileSize);

        renderResourceDots(shapeRenderer, tile, tileX, tileY, innerTileSize);
    }


    private void renderResourceDots(ShapeRenderer shapeRenderer, SurfaceTile tile, float tileX, float tileY, float innerTileSize) {
        int resourceSize = 4;
        float resourcePadding = 4f;
        float resourceX = tileX + resourcePadding;
        float resourceY = tileY + resourcePadding;

        for (ResourceType type : ResourceType.values()) {
            int quantity = tile.getResources().getResourceQuantity(type);
            int dots = (quantity + 9) / 10; // Always render at least 1 dot

            if (quantity == 0) continue;

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
    public void update() {
        // Update logic if needed
    }
}
