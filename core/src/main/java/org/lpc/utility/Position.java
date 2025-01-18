package org.lpc.utility;

import com.badlogic.gdx.graphics.Camera;
import lombok.Getter;
import lombok.Setter;
import org.lpc.map.MapScale;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class Position {
    private int gridX;    // Grid coordinates (tile position)
    private int gridY;
    private int pixelsPerTile;

    public Position(int gridX, int gridY, MapScale scale) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.pixelsPerTile = scale.getPixelsPerTile();
    }

    // Create from world coordinates
    public static Position fromWorld(float worldX, float worldY, MapScale scale, Camera camera) {
        int gridX = (int) (worldX / scale.getPixelsPerTile());
        int gridY = (int) (worldY / scale.getPixelsPerTile());
        return new Position(gridX, gridY, scale);
    }

    public boolean isAdjacentTo(Position other) {
        int dx = Math.abs(this.gridX - other.gridX);
        int dy = Math.abs(this.gridY - other.gridY);
        return (dx == 1 && dy == 0) || (dx == 0 && dy == 1);
    }

    public int getDistanceTo(Position other) {
        return Math.abs(this.gridX - other.gridX) + Math.abs(this.gridY - other.gridY);
    }

    public List<Position> getPositionsInRange(int range, int mapWidth, int mapHeight, MapScale scale) {
        List<Position> positions = new ArrayList<>();
        for (int x = Math.max(0, gridX - range); x <= Math.min(mapWidth - 1, gridX + range); x++) {
            for (int y = Math.max(0, gridY - range); y <= Math.min(mapHeight - 1, gridY + range); y++) {
                Position pos = new Position(x, y, scale);
                if (this.getDistanceTo(pos) <= range) {
                    positions.add(pos);
                }
            }
        }
        return positions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return gridX == position.gridX && gridY == position.gridY;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gridX, gridY);
    }

    @Override
    public String toString() {
        return String.format("Position(grid:[%d,%d])", gridX, gridY);
    }
}
