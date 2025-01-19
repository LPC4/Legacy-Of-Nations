package org.lpc.map;

import com.badlogic.gdx.math.MathUtils;
import org.lpc.map.maps.SurfaceMap;
import org.lpc.terrain.TerrainType;
import org.lpc.terrain.resources.ResourceType;
import org.lpc.utility.PerlinNoise;
import org.lpc.utility.Position;

import java.util.Random;

public class MapGenerator  {
    private final PerlinNoise heightNoise;
    private final PerlinNoise moistureNoise;

    public MapGenerator() {
        this.heightNoise = new PerlinNoise();
        this.moistureNoise = new PerlinNoise();
    }

    public void generateTerrain(SurfaceMap.SurfaceTile[][] tiles, int width, int height) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = generateTile(x, y);
            }
        }
    }

    private SurfaceMap.SurfaceTile generateTile(int x, int y) {
        double scale = 0.04;

        double heightValue = generateHeight(x, y, scale);
        double moistureValue = generateMoisture(x, y, scale);
        double riverValue = generateRiver(x, y, scale);

        heightValue = (heightValue + 1) / 2;
        moistureValue = (moistureValue + 1) / 2;
        riverValue = (riverValue + 1) / 2;

        heightValue = applyRiverEffect(heightValue, riverValue);

        TerrainType terrain = determineTerrainType((float) heightValue, (float) moistureValue);

        return new SurfaceMap.SurfaceTile(new Position(x, y, MapScale.SURFACE), terrain, (float) moistureValue, (float) heightValue);
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


    public void generateResources(SurfaceMap.SurfaceTile[][] tiles) {
        for (SurfaceMap.SurfaceTile[] row : tiles) {
            for (SurfaceMap.SurfaceTile tile : row) {
                generateResourcesForTile(tile);
            }
        }
    }

    private void generateResourcesForTile(SurfaceMap.SurfaceTile tile) {
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

    private void generateResource(SurfaceMap.SurfaceTile tile, ResourceType type, int min, int max, float modifier) {
        Random random = new Random();
        int quantity = random.nextInt(max - min) + min;

        if (modifier > 0)
            quantity = (int) (quantity * modifier);

        quantity = MathUtils.clamp(quantity, min, max);
        tile.getResources().addResource(type, quantity);
    }
}
