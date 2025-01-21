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

import java.util.Optional;

import static org.lpc.utility.Constants.SAWMILL_HARVEST_DELAY_TICKS;
import static org.lpc.utility.Constants.SAWMILL_HARVEST_RATE;


@Getter
public class Sawmill extends BaseBuilding {
    private static final Texture texture = new Texture("textures/pixel-art.png");

    private final TickedTimer harvestTimer;

    public Sawmill(SurfaceMap.SurfaceTile tile) {
        super(BuildingFunctionality.RESOURCE_GATHERING, 600, new Sprite(texture), tile);
        this.harvestTimer = new TickedTimer(SAWMILL_HARVEST_DELAY_TICKS);
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
        ResourceType resourceType = ResourceType.WOOD;
        ResourceNode resources = getTile().getResources();

        int amount = resources.getResourceQuantity(resourceType);

        if (amount == 0) {
            return new Pair<>(ResourceType.WOOD, 0);
        }

        int harvestedAmount = (int) (amount * SAWMILL_HARVEST_RATE);
        int harvest = resources.harvestResource(resourceType, harvestedAmount);

        LOGGER.debug("Harvesting resources from sawmill: {} {}", harvest, resourceType);

        return new Pair<>(ResourceType.WOOD, harvest);
    }

    @Override
    public Optional<Integer> getProgressPercentage() {
        return Optional.of(harvestTimer.getProgressPercentage());
    }
}
