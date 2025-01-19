package org.lpc.terrain.buildings.buildings;

import lombok.Getter;
import org.lpc.terrain.buildings.BaseBuilding;
import org.lpc.terrain.buildings.BuildingFunctionality;
import org.lpc.terrain.resources.ResourceType;

@Getter
public class Refinery extends BaseBuilding {
    private ResourceType resourceType;

    public Refinery() {
        super(BuildingFunctionality.RESOURCE_REFINING, 300);
    }

    @Override
    public void update() {

    }
}
