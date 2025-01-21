package org.lpc.map.maps;

import com.badlogic.gdx.math.Vector2;
import lombok.Getter;
import lombok.Setter;
import org.javatuples.Pair;
import org.lpc.MainGame;
import org.lpc.map.BaseMap;
import org.lpc.map.MapScale;
import org.lpc.terrain.TerrainType;
import org.lpc.terrain.buildings.BaseBuilding;
import org.lpc.terrain.resources.ResourceNode;
import org.lpc.terrain.resources.ResourceType;
import org.lpc.utility.Position;

import java.util.Optional;

import static org.lpc.map.maps.SurfaceMap.*;

@Getter
@Setter
public class SurfaceMap extends BaseMap<SurfaceTile> {
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
            super(pos, MapScale.SURFACE);
            this.terrain = terrain;
            this.explored = false;
            this.resources = new ResourceNode();
            this.moisture = moisture;
            this.height = height;
            this.movementModifier = TerrainType.getMovementModifier(terrain);
            this.vegetationDensity = TerrainType.calculateVegetationDensity(terrain, moisture, height);
        }

        public Optional<Pair<ResourceType, Integer>> harvestResource() {
            if (building == null || !building.canHarvestResources()) {
                return Optional.empty();
            }

            return Optional.of(building.harvestResources());
        }

        public void update() {
            if (building != null) {
                building.update();
            }
        }
    }

    public SurfaceMap(int width, int height, MainGame game) {
        super(
            MapScale.SURFACE,
            width,
            height,
            game,
            new SurfaceMapGenerator(),
            new SurfaceMapRenderer(game),
            new SurfaceTile[width][height],
            new SurfaceMapInput(game)
        );
        //this.vegetationManager = new VegetationManager();
    }

    @Override
    public void update() {
        // Update logic if needed
    }

    public SurfaceTile getTile(int x, int y) {
        return (SurfaceTile) tiles[x][y];
    }

    public Vector2 getTilePosition(int x, int y) {
        return new Vector2(x * MapScale.SURFACE.getPixelsPerTile(), y * MapScale.SURFACE.getPixelsPerTile());
    }

    public Vector2 getGridPosition(float x, float y) {
        return new Vector2(x / MapScale.SURFACE.getPixelsPerTile(), y / MapScale.SURFACE.getPixelsPerTile());
    }

    public boolean isWithinBounds(Vector2 gridPos) {
        return gridPos.x >= 0 && gridPos.x < width && gridPos.y >= 0 && gridPos.y < height;
    }
}
