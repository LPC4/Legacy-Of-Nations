package org.lpc.map.maps;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import lombok.Getter;
import org.lpc.map.MapScale;
import org.lpc.terrain.TerrainType;
import org.lpc.utility.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
public class VegetationManager {
    private final List<Sprite> vegetation;
    private final Random random;

    public VegetationManager() {
        this.vegetation = new ArrayList<>();
        this.random = new Random();
    }

    public void generateVegetation(SurfaceMap.SurfaceTile[][] tiles) {
        clearVegetation();
        Texture vegetationTexture = Constants.VEGETATION_TEXTURE;

        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[0].length; y++) {
                generateVegetationForTile(tiles[x][y], x, y, vegetationTexture);
            }
        }
    }

    private void generateVegetationForTile(SurfaceMap.SurfaceTile tile, int x, int y, Texture vegetationTexture) {
        if (!TerrainType.hasVegetation(tile.getTerrain()))
            return;

        float density = tile.getVegetationDensity();
        int vegetationCount = calculateVegetationCount(density);

        for (int i = 0; i < vegetationCount; i++) {
            if (random.nextFloat() < density) {
                Sprite sprite = createVegetationSprite(x, y, vegetationTexture);
                vegetation.add(sprite);
            }
        }
    }

    private int calculateVegetationCount(float density) {
        // Base count is between 1-3 based on density
        return Math.max(1, (int)(density * 3));
    }

    private Sprite createVegetationSprite(int x, int y, Texture texture) {
        Sprite sprite = new Sprite(texture);
        float tileSize = MapScale.SURFACE.getPixelsPerTile();

        // Position within the tile (add some randomization)
        float offsetX = random.nextFloat() * (tileSize * 0.8f);
        float offsetY = random.nextFloat() * (tileSize * 0.8f);

        sprite.setPosition(
                x * tileSize + offsetX,
                y * tileSize + offsetY
        );

        // Size (vary between 60% and 100% of tile size)
        float scale = 0.6f + random.nextFloat() * 0.4f;
        sprite.setSize(tileSize * scale, tileSize * scale);

        // Rotation (0-360 degrees)
        sprite.setRotation(random.nextFloat() * 360);

        // Add slight color variation
        sprite.setColor(
                1f,  // R
                1f,  // G
                1f,  // B
                0.8f + random.nextFloat() * 0.2f  // A (80-100% opacity)
        );

        return sprite;
    }

    public void clearVegetation() {
        vegetation.clear();
    }

    public void removeVegetationInTile(int x, int y) {
        float tileSize = MapScale.SURFACE.getPixelsPerTile();
        float tileX = x * tileSize;
        float tileY = y * tileSize;

        vegetation.removeIf(sprite ->
                sprite.getX() >= tileX &&
                        sprite.getX() < tileX + tileSize &&
                        sprite.getY() >= tileY &&
                        sprite.getY() < tileY + tileSize
        );
    }

    public boolean hasVegetationInTile(int x, int y) {
        float tileSize = MapScale.SURFACE.getPixelsPerTile();
        float tileX = x * tileSize;
        float tileY = y * tileSize;

        return vegetation.stream().anyMatch(sprite ->
                sprite.getX() >= tileX &&
                        sprite.getX() < tileX + tileSize &&
                        sprite.getY() >= tileY &&
                        sprite.getY() < tileY + tileSize
        );
    }

    public void updateVegetation(float deltaTime) {
        // Optional: Add animation or movement effects
        for (Sprite sprite : vegetation) {
            // Example: Gentle swaying motion
            float originalRotation = sprite.getRotation();
            float sway = (float) Math.sin(deltaTime) * 2f;
            sprite.setRotation(originalRotation + sway);
        }
    }
}
