package org.lpc.civilisation;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lpc.map.BaseMap;
import org.lpc.map.maps.SurfaceMap;
import org.lpc.terrain.TerrainType;
import org.lpc.terrain.buildings.BaseBuilding;
import org.lpc.terrain.buildings.buildings.Farm;
import org.lpc.terrain.buildings.buildings.Sawmill;
import org.lpc.terrain.resources.ResourceType;
import org.lpc.utility.Position;

import java.util.*;

@Getter
public class TerritoryHandler {
    private static final Logger LOGGER = LogManager.getLogger(TerritoryHandler.class);

    private final Civilisation civilisation;
    private final List<BaseMap.BaseTile> territory = new ArrayList<>();
    private final List<SurfaceMap.SurfaceTile> surfaceTerritory = new ArrayList<>();

    public TerritoryHandler(Civilisation civilisation) {
        this.civilisation = Objects.requireNonNull(civilisation, "Civilisation cannot be null");
    }

    public void update() {
        for (BaseMap.BaseTile tile : territory) {
            tile.update();
        }
    }

    public void setStartingBuildings() {
        SurfaceMap.SurfaceTile bestFoodTile = findBestResourceTile(ResourceType.FOOD);
        SurfaceMap.SurfaceTile bestWoodTile = findBestResourceTile(ResourceType.WOOD);
        SurfaceMap.SurfaceTile secondBestWoodTile = findSecondBestResourceTile(ResourceType.WOOD);

        placeStartingBuildings(bestFoodTile, bestWoodTile, secondBestWoodTile);
    }

    private SurfaceMap.SurfaceTile findBestResourceTile(ResourceType resourceType) {
        SurfaceMap.SurfaceTile bestTile = null;
        int bestAmount = -1;

        for (SurfaceMap.SurfaceTile tile : surfaceTerritory) {
            int currentAmount = tile.getResources().getResourceQuantity(resourceType);
            if (currentAmount > bestAmount) {
                bestAmount = currentAmount;
                bestTile = tile;
            }
        }
        return bestTile;
    }

    private SurfaceMap.SurfaceTile findSecondBestResourceTile(ResourceType resourceType) {
        SurfaceMap.SurfaceTile best = null;
        SurfaceMap.SurfaceTile secondBest = null;
        int bestAmount = -1;
        int secondBestAmount = -1;

        for (SurfaceMap.SurfaceTile tile : surfaceTerritory) {
            int currentAmount = tile.getResources().getResourceQuantity(resourceType);
            if (currentAmount > bestAmount) {
                secondBest = best;
                secondBestAmount = bestAmount;
                best = tile;
                bestAmount = currentAmount;
            } else if (currentAmount > secondBestAmount) {
                secondBest = tile;
                secondBestAmount = currentAmount;
            }
        }
        return secondBest;
    }

    private void placeStartingBuildings(SurfaceMap.SurfaceTile foodTile,
                                        SurfaceMap.SurfaceTile woodTile,
                                        SurfaceMap.SurfaceTile backupWoodTile) {
        if (foodTile == null) {
            LOGGER.warn("No suitable food tile found for starting buildings");
            return;
        }

        SurfaceMap.SurfaceTile actualWoodTile = woodTile;
        if (woodTile != null && woodTile == foodTile) {
            actualWoodTile = backupWoodTile;
        }

        placeBuilding(foodTile, new Farm(foodTile));
        if (actualWoodTile != null) {
            placeBuilding(actualWoodTile, new Sawmill(actualWoodTile));
        }
    }

    private void placeBuilding(SurfaceMap.SurfaceTile tile, BaseBuilding building) {
        if (tile != null && tile.getBuilding() == null) {
            tile.setBuilding(building);
            LOGGER.debug("Placed {} at {}", building.getClass().getSimpleName(), tile.getPosition());
        }
    }

    public void claimStartingTerritory(int startX, int startY, int populationRadius) {
        SurfaceMap map = getSurfaceMap();
        Position validStart = findValidStartingPosition(startX, startY, populationRadius, map);
        claimTerritoryArea(validStart, populationRadius, map);
    }

    private SurfaceMap getSurfaceMap() {
        return civilisation.getGameStateManager().getMapSystem().getSurfaceMap();
    }

    private Position findValidStartingPosition(int startX, int startY, int radius, SurfaceMap map) {
        Queue<Position> searchQueue = new LinkedList<>();
        Set<Position> visited = new HashSet<>();
        Position initialPosition = new Position(startX, startY, map.getScale());

        searchQueue.add(initialPosition);
        visited.add(initialPosition);

        while (!searchQueue.isEmpty()) {
            Position current = searchQueue.poll();

            if (isValidStartingArea(current, radius, map)) {
                LOGGER.info("Found valid starting position at {}", current);
                return current;
            }

            addNeighborPositions(current, map, searchQueue, visited);
        }

        throw new IllegalStateException("Failed to find valid starting position after checking "
            + visited.size() + " locations");
    }

    private boolean isValidStartingArea(Position center, int radius, SurfaceMap map) {
        for (int x = center.getGridX() - radius; x <= center.getGridX() + radius; x++) {
            for (int y = center.getGridY() - radius; y <= center.getGridY() + radius; y++) {
                if (!isValidTileForClaiming(x, y, map)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValidTileForClaiming(int x, int y, SurfaceMap map) {
        if (x < 0 || x >= map.getWidth() || y < 0 || y >= map.getHeight()) {
            return false;
        }

        SurfaceMap.SurfaceTile tile = map.getTile(x, y);
        return tile.getTerrain() != TerrainType.WATER && tile.getOwner() == null;
    }

    private void addNeighborPositions(Position current, SurfaceMap map,
                                      Queue<Position> queue, Set<Position> visited) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;

                int newX = current.getGridX() + dx;
                int newY = current.getGridY() + dy;

                if (isWithinMapBounds(newX, newY, map)) {
                    Position neighbor = new Position(newX, newY, map.getScale());
                    if (visited.add(neighbor)) {
                        queue.add(neighbor);
                    }
                }
            }
        }
    }

    private boolean isWithinMapBounds(int x, int y, SurfaceMap map) {
        return x >= 0 && x < map.getWidth() && y >= 0 && y < map.getHeight();
    }

    private void claimTerritoryArea(Position center, int radius, SurfaceMap map) {
        int tilesClaimed = 0;

        for (int x = center.getGridX() - radius; x <= center.getGridX() + radius; x++) {
            for (int y = center.getGridY() - radius; y <= center.getGridY() + radius; y++) {
                if (isWithinMapBounds(x, y, map)) {
                    SurfaceMap.SurfaceTile tile = map.getTile(x, y);
                    if (claimTile(tile)) {
                        tilesClaimed++;
                    }
                }
            }
        }

        LOGGER.info("Successfully claimed {} tiles around position {}", tilesClaimed, center);
    }

    private boolean claimTile(SurfaceMap.SurfaceTile tile) {
        if (tile.getOwner() != null) {
            return false;
        }

        tile.setOwner(civilisation);
        territory.add(tile);
        surfaceTerritory.add(tile);
        return true;
    }
}
