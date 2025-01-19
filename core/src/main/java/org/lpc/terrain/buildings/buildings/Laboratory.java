package org.lpc.terrain.buildings.buildings;

import org.lpc.terrain.buildings.BaseBuilding;
import org.lpc.terrain.buildings.BuildingFunctionality;

public class Laboratory extends BaseBuilding {
    public Laboratory() {
        super(BuildingFunctionality.RESEARCH, 800);
    }

    @Override
    public void update() {
        // Implement specific functionality for the Laboratory
    }
}
