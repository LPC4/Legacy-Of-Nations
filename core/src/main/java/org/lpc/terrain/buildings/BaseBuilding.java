package org.lpc.terrain.buildings;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javatuples.Pair;
import org.lpc.map.BaseMap;
import org.lpc.map.maps.SurfaceMap;
import org.lpc.terrain.resources.ResourceType;

import java.util.Optional;

@Getter
@Setter
public abstract class BaseBuilding {
    protected static final Logger LOGGER = LogManager.getLogger(BaseBuilding.class);

    @NonNull protected final BuildingFunctionality type;
    @NonNull protected final Sprite sprite;
    @NonNull protected final SurfaceMap.SurfaceTile tile;
    protected final int maxHealth;
    protected int health;

    public BaseBuilding(
        @NonNull BuildingFunctionality functionality,
        int health,
        @NonNull Sprite sprite,
        @NonNull SurfaceMap.SurfaceTile tile
    ) {
        validateHealth(health);
        this.type = functionality;
        this.health = health;
        this.maxHealth = health;
        this.sprite = sprite;
        this.tile = tile;
    }

    public abstract boolean canHarvestResources();

    public void update() {
        // Default no-op implementation
    }

    public abstract Pair<ResourceType, Integer> harvestResources();

    public Optional<Integer> getProgressPercentage() {
        return Optional.empty();
    }

    public boolean isDestroyed() {
        return health <= 0;
    }

    public void damage(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException("Damage cannot be negative");
        }
        health = Math.max(health - damage, 0);
    }

    public void repair(int repairAmount) {
        if (repairAmount < 0) {
            throw new IllegalArgumentException("Repair amount cannot be negative");
        }
        health = Math.min(health + repairAmount, maxHealth);
    }

    private static void validateHealth(int health) {
        if (health <= 0) {
            throw new IllegalArgumentException("Initial health must be positive");
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
