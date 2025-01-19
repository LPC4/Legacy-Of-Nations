package org.lpc.terrain.buildings.buildings;

import org.lpc.terrain.buildings.BaseBuilding;
import org.lpc.terrain.buildings.BuildingFunctionality;

public class Quarry extends BaseBuilding {
    public Quarry() {
        super(BuildingFunctionality.RESOURCE_GATHERING, 800);
    }

    @Override
    public void update() {
        // Implement specific functionality for the Quarry
    }
}
