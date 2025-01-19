package org.lpc.civilisation;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lpc.map.BaseMap;
import org.lpc.map.MapScale;
import org.lpc.map.maps.SurfaceMap;
import org.lpc.terrain.TerrainType;
import org.lpc.utility.Position;

import java.util.*;

@Getter
@Setter
public class TerritoryHandler {
    private static final Logger LOGGER = LogManager.getLogger(TerritoryHandler.class);

    private final Civilisation civilisation;
    private final List<BaseMap.BaseTile> territory;

    public TerritoryHandler(Civilisation civilisation) {
        this.civilisation = civilisation;
        this.territory = new ArrayList<>();
    }

    public void addTile(BaseMap.BaseTile tile) {
        territory.add(tile);
    }

    public void removeTile(BaseMap.BaseTile tile) {
        territory.remove(tile);
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

}
