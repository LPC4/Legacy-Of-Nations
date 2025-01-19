package org.lpc.map.maps;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import lombok.Getter;
import lombok.Setter;
import org.lpc.MainGame;
import org.lpc.map.BaseMap;
import org.lpc.map.MapGenerator;
import org.lpc.map.MapRenderer;
import org.lpc.map.VegetationManager;
import org.lpc.terrain.TerrainType;
import org.lpc.terrain.buildings.BaseBuilding;
import org.lpc.terrain.resources.ResourceNode;
import org.lpc.utility.Position;

@Getter
@Setter
public class SurfaceMap extends BaseMap {
    private final SurfaceTile[][] tiles;
    private final VegetationManager vegetationManager;

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
        super(width, height, game);
        this.tiles = new SurfaceTile[width][height];
        this.vegetationManager = new VegetationManager();

        generateMap();
    }

    @Override
    protected void generateMap() {
        mapGenerator.generateTerrain(tiles, width, height);
        mapGenerator.generateResources(tiles);
        vegetationManager.generateVegetation(tiles);
    }

    @Override
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        renderer.render(tiles, vegetationManager.getVegetation(), shapeRenderer, batch);
    }

    @Override
    public void update() {
        // Update logic if needed
    }
}
