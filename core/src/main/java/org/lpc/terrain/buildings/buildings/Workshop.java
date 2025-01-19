package org.lpc.terrain.buildings.buildings;

import org.lpc.terrain.buildings.BaseBuilding;
import org.lpc.terrain.buildings.BuildingFunctionality;

public class Workshop extends BaseBuilding {
    public Workshop() {
        super(BuildingFunctionality.RESOURCE_REFINING, 900);
    }

    @Override
    public void update() {
        // Implement specific functionality for the Workshop
    }
}
