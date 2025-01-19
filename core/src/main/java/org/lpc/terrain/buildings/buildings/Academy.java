package org.lpc.terrain.buildings.buildings;

import org.lpc.terrain.buildings.BaseBuilding;
import org.lpc.terrain.buildings.BuildingFunctionality;

public class Academy extends BaseBuilding {
    public Academy() {
        super(BuildingFunctionality.RESEARCH, 900);
    }

    @Override
    public void update() {
        // Implement specific functionality for the Academy
    }
}
