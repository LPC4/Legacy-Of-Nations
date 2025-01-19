package org.lpc.terrain.buildings.buildings;

import org.lpc.terrain.buildings.BaseBuilding;
import org.lpc.terrain.buildings.BuildingFunctionality;

public class Wall extends BaseBuilding {
    public Wall() {
        super(BuildingFunctionality.DEFENSIVE, 1500);
    }

    @Override
    public void update() {
        // Implement specific functionality for the Wall
    }
}
