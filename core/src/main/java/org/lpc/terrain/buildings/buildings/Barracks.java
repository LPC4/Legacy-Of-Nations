package org.lpc.terrain.buildings.buildings;

import org.lpc.terrain.buildings.BaseBuilding;
import org.lpc.terrain.buildings.BuildingFunctionality;

public class Barracks extends BaseBuilding {
    public Barracks() {
        super(BuildingFunctionality.DEFENSIVE, 1200);
    }

    @Override
    public void update() {
        // Implement specific functionality for the Barracks
    }
}

