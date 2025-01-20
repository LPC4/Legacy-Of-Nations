package org.lpc.terrain.buildings.buildings;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import lombok.Getter;
import org.javatuples.Pair;
import org.lpc.map.maps.SurfaceMap;
import org.lpc.terrain.buildings.BaseBuilding;
import org.lpc.terrain.buildings.BuildingFunctionality;
import org.lpc.terrain.resources.ResourceNode;
import org.lpc.terrain.resources.ResourceType;
import org.lpc.utility.TickedTimer;

import static org.lpc.utility.Constants.FARM_HARVEST_DELAY_TICKS;
import static org.lpc.utility.Constants.FARM_HARVEST_RATE;

@Getter
public class Farm extends BaseBuilding {
    private static final Texture texture = new Texture("textures/pixel-art.png");

    private final TickedTimer harvestTimer;

    public Farm(SurfaceMap.SurfaceTile tile) {
        super(BuildingFunctionality.RESOURCE_GATHERING, 700, new Sprite(texture), tile);
        this.harvestTimer = new TickedTimer(FARM_HARVEST_DELAY_TICKS);
    }

    @Override
    public void update() {
        harvestTimer.tick();
    }

    @Override
    public boolean canHarvestResources() {
        return harvestTimer.isTime();
    }

    @Override
    public Pair<ResourceType, Integer> harvestResources() {
        ResourceType resourceType = ResourceType.FOOD;
        ResourceNode resources = getTile().getResources();

        int amount = resources.getResourceQuantity(resourceType);
        int harvestedAmount = (int) (amount * FARM_HARVEST_RATE);

        int harvest = resources.harvestReplicableResource(resourceType, harvestedAmount);

        LOGGER.debug("Harvesting resources from farm: {} {}", harvest, resourceType);

        return new Pair<>(ResourceType.FOOD, harvest);
    }

    @Override
    public int getProgressPercentage() {
        return harvestTimer.getProgressPercentage();
    }
}
