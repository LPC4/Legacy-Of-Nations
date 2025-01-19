package org.lpc.terrain.buildings.buildings;

import org.lpc.terrain.buildings.BaseBuilding;
import org.lpc.terrain.buildings.BuildingFunctionality;

public class Sawmill extends BaseBuilding {
    public Sawmill() {
        super(BuildingFunctionality.RESOURCE_GATHERING, 600);
    }

    @Override
    public void update() {
        // Implement specific functionality for the Sawmill
    }
}
