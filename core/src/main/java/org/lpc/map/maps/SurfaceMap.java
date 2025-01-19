package org.lpc.map.maps;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import lombok.Getter;
import lombok.Setter;
import org.lpc.MainGame;
import org.lpc.map.BaseMap;
import org.lpc.terrain.TerrainType;
import org.lpc.terrain.buildings.BaseBuilding;
import org.lpc.terrain.resources.ResourceNode;
import org.lpc.utility.Position;

@Getter
@Setter
public class SurfaceMap extends BaseMap {
    //private final VegetationManager vegetationManager;

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
            super(pos);
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
        super(
            width,
            height,
            game,
            new SurfaceMapGenerator(),
            new SurfaceMapRenderer(game),
            new SurfaceTile[width][height]
        );
        //this.vegetationManager = new VegetationManager();
    }

    @Override
    public void update() {
        // Update logic if needed
    }
}
