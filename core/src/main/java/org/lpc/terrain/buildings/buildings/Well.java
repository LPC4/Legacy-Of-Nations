package org.lpc.terrain.buildings.buildings;

import org.lpc.terrain.buildings.BaseBuilding;
import org.lpc.terrain.buildings.BuildingFunctionality;

public class Well extends BaseBuilding {
    public Well() {
        super(BuildingFunctionality.RESOURCE_GATHERING, 500);
    }

    @Override
    public void update() {
        // Implement specific functionality for the Well
    }
}
