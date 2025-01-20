package org.lpc.terrain.buildings;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javatuples.Pair;
import org.lpc.map.BaseMap;
import org.lpc.map.maps.SurfaceMap;
import org.lpc.terrain.resources.ResourceType;

@Getter
@Setter
public abstract class BaseBuilding {
    protected static final Logger LOGGER = LogManager.getLogger(BaseBuilding.class);

    protected final Sprite sprite;
    protected final BuildingFunctionality type;
    protected final SurfaceMap.SurfaceTile tile;
    protected final int maxHealth;

    protected int health;

    public BaseBuilding(BuildingFunctionality functionality, int health, Sprite sprite, SurfaceMap.SurfaceTile tile) {
        this.type = functionality;
        this.health = health;
        this.maxHealth = health;
        this.sprite = sprite;
        this.tile = tile;
    }

    public abstract boolean canHarvestResources();
    public abstract Pair<ResourceType, Integer> harvestResources();
    public abstract void update();

    /**
     * Override this method to return the progress percentage of the building
     * @return progress percentage or -1 if not applicable
     */
    public int getProgressPercentage() {
        return -1;
    }

    public boolean isDestroyed() {
        return health <= 0;
    }

    public void damage(int damage) {
        health -= damage;
    }

    public void repair(int repair) {
        health += repair;
        if (health > maxHealth) {
            health = maxHealth;
        }
    }
}
