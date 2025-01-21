package org.lpc.civilisation;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lpc.map.BaseMap;
import org.lpc.map.MapScale;
import org.lpc.map.maps.SurfaceMap;
import org.lpc.terrain.TerrainType;
import org.lpc.terrain.buildings.buildings.Farm;
import org.lpc.terrain.buildings.buildings.Sawmill;
import org.lpc.terrain.resources.ResourceType;
import org.lpc.utility.Position;

import java.util.*;

@Getter
@Setter
public class TerritoryHandler {
    private static final Logger LOGGER = LogManager.getLogger(TerritoryHandler.class);

    private final Civilisation civilisation;
    private final List<BaseMap.BaseTile> territory;
    private final List<SurfaceMap.SurfaceTile> surfaceTerritory;

    public TerritoryHandler(Civilisation civilisation) {
        this.civilisation = civilisation;
        this.territory = new ArrayList<>();
        this.surfaceTerritory = new ArrayList<>();
    }

    public void update() {
        // update all tiles, which in turn updates buildings for harvest etc.
        for (BaseMap.BaseTile tile : territory) {
            tile.update();
        }
    }

    public void setStartingBuildings() {
        SurfaceMap.SurfaceTile bestFoodTile = null;
        SurfaceMap.SurfaceTile bestWoodTile = null;
        SurfaceMap.SurfaceTile secondBestWoodTile = null;

        for (SurfaceMap.SurfaceTile tile : surfaceTerritory) {
            int foodQuantity = tile.getResources().getResourceQuantity(ResourceType.FOOD);
            int woodQuantity = tile.getResources().getResourceQuantity(ResourceType.WOOD);

            // Find the best food tile
            if (bestFoodTile == null || foodQuantity > bestFoodTile.getResources().getResourceQuantity(ResourceType.FOOD)) {
                bestFoodTile = tile;
            }

            // Find the best wood tile
            if (bestWoodTile == null || woodQuantity > bestWoodTile.getResources().getResourceQuantity(ResourceType.WOOD)) {
                secondBestWoodTile = bestWoodTile; // Update the second-best wood tile
                bestWoodTile = tile;
            } else if (secondBestWoodTile == null || woodQuantity > secondBestWoodTile.getResources().getResourceQuantity(ResourceType.WOOD)) {
                secondBestWoodTile = tile;
            }
        }

        // Ensure the tiles are different
        if (bestFoodTile != null && bestFoodTile == bestWoodTile) {
            bestWoodTile = secondBestWoodTile;
        }

        // Place buildings
        if (bestFoodTile != null) {
            bestFoodTile.setBuilding(new Farm(bestFoodTile));
        }
        if (bestWoodTile != null) {
            bestWoodTile.setBuilding(new Sawmill(bestWoodTile));
        }
    }


    public void claimStartingTerritory(int startX, int startY, int radius) {
        SurfaceMap map = civilisation.getGameStateManager().getMapSystem().getSurfaceMap();
        Set<Position> visited = new HashSet<>();

        if (!findAndClaimTerritory(startX, startY, radius, map, visited)) {
            LOGGER.error("Could not find suitable starting tile.");
        } else {
            LOGGER.info("Territory claimed successfully, size: {}", territory.size());
        }
    }

    private boolean findAndClaimTerritory(int startX, int startY, int radius, SurfaceMap map, Set<Position> visited) {
        Queue<Position> queue = new LinkedList<>();
        queue.add(new Position(startX, startY, map.getScale()));
        visited.add(new Position(startX, startY, map.getScale()));

        while (!queue.isEmpty()) {
            Position pos = queue.poll();
            int x = pos.getGridX();
            int y = pos.getGridY();

            if (isSuitableStartingTile(x, y, radius)) {
                for (int i = x - radius; i <= x + radius; i++) {
                    for (int j = y - radius; j <= y + radius; j++) {
                        if (i < 0 || i >= map.getWidth() || j < 0 || j >= map.getHeight()) {
                            continue;
                        }
                        BaseMap.BaseTile tile = map.getTile(i, j);
                        addTile(tile);
                        tile.setOwner(civilisation);
                    }
                }
                return true; // Territory claimed
            }

            // Add neighboring positions to the queue
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int newX = x + dx;
                    int newY = y + dy;
                    Position newPos = new Position(newX, newY, map.getScale());
                    if (newX >= 0 && newX < map.getWidth() && newY >= 0 && newY < map.getHeight() && !visited.contains(newPos)) {
                        queue.add(newPos);
                        visited.add(newPos);
                    }
                }
            }
        }

        return false; // Suitable tile not found
    }

    public boolean isSuitableStartingTile(int x, int y, int radius) {
        SurfaceMap map = civilisation.getGameStateManager().getMapSystem().getSurfaceMap();

        for (int i = x - radius; i <= x + radius; i++) {
            for (int j = y - radius; j <= y + radius; j++) {
                if (i < 0 || i >= map.getWidth() || j < 0 || j >= map.getHeight()) {
                    return false;
                }
                if (map.getTile(i, j).getTerrain().equals(TerrainType.WATER)) {
                    return false;
                }
                // Check if tile is already claimed, implementation needed
            }
        }

        return true;
    }

    public void addTile(BaseMap.BaseTile tile) {
        territory.add(tile);

        if (tile.getScale() == MapScale.SURFACE) {
            surfaceTerritory.add((SurfaceMap.SurfaceTile) tile);
        }
    }

    public void removeTile(BaseMap.BaseTile tile) {
        territory.remove(tile);

        if (tile.getScale() == MapScale.SURFACE) {
            surfaceTerritory.remove((SurfaceMap.SurfaceTile) tile);
        }
    }
}
